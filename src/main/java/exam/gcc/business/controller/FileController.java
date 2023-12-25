package exam.gcc.business.controller;

import exam.gcc.business.service.FileService;
import exam.gcc.domain.SaveFile;
import exam.gcc.framework.base.BaseController;
import exam.gcc.util.MD5;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/file")
public class FileController extends BaseController {

    @Value("${FILE_SAVE_PATH}")
    private String FILE_SAVE_PATH;
    @Autowired
    private FileService fileService;


    @GetMapping("/check")
    public String checkFileHash(String hash) {
        SaveFile fileByHash = fileService.getFileByHash(hash);

        if (fileByHash != null) {
            // 存在的
            // 直接保存
            fileService.addFile(fileByHash, getUser());

            return "success";
        }
        return "fail";
    }

    @RequestMapping("/getFiles")
    public List<SaveFile> getFiles(){
        if(getUser()==null){
            return new ArrayList<>();
        }
        List<SaveFile> s = fileService.getFiles(getUser().getUsername());
        return s;
    }

    @PostMapping("/uploadFile")
    public String uploadFile(@RequestParam("file") MultipartFile file){
        if (file.isEmpty()) {
            return "请选择文件!";
        }
        try {
            // 获取文件名字
            String originalFileName = file.getOriginalFilename();
            // 文件存放路径
            String path = System.getProperty("user.dir") + File.separator + FILE_SAVE_PATH + getUser().getUsername() + "/";
            File dest = new File(path + originalFileName);
            if(!dest.getParentFile().exists()){
                dest.getParentFile().mkdirs();
            }

            // 计算md5值
            byte[] fileData = MD5.readFile(file);
            String fileHash = MD5.calculateMD5(fileData);
            //String fileHash = MD5.md5Hex(Arrays.toString(DigestUtils.md5Digest(file.getInputStream())));
            // 压缩文件名
            String zipFileName = originalFileName + ".zip";

            // 创建压缩文件
            FileOutputStream fos = new FileOutputStream(path + zipFileName);
            ZipOutputStream zipOut = new ZipOutputStream(fos);

            // 添加文件到压缩文件
            ZipEntry zipEntry = new ZipEntry(originalFileName);
            zipOut.putNextEntry(zipEntry);
            zipOut.write(file.getBytes());
            zipOut.closeEntry();

            // 关闭流
            zipOut.close();
            fos.close();

            //file.transferTo(dest);
            SaveFile saveFile = new SaveFile();
            saveFile.setFilename(file.getOriginalFilename());
            saveFile.setLocation(getUser().getUsername()+"/"+file.getOriginalFilename());
            saveFile.setFilesize(file.getSize());
            saveFile.setUpdatetime(new Date());
            saveFile.setHash(fileHash);
            fileService.addFile(saveFile, getUser());
            return "success";
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "文件上传失败";
        }
    }

    @PostMapping("/deleteFile")
    public int deleteFile(@RequestParam Long id, @RequestParam String location){
        File file = new File(System.getProperty("user.dir") + File.separator + FILE_SAVE_PATH + location);
        if(!file.exists()){
            return fileService.deleteFile4All(id);
        }
        return fileService.deleteFile4User(id, getUser().getUsername());
    }

    @PostMapping("/deleteFileList")
    public int deleteFileList(@RequestBody Map<String, Object> requestPayload){
        List<Integer> ids = (List<Integer>) requestPayload.get("ids");
        List<String> locations = (List<String>) requestPayload.get("locations");
        int rtn = 0;
        for(int i=0;i< locations.size();i++){
            File file = new File(FILE_SAVE_PATH + locations.get(i));
            if(!file.exists()){
                rtn += fileService.deleteFile4All(ids.get(i).longValue());
            }else{
                rtn += fileService.deleteFile4User(ids.get(i).longValue(), getUser().getUsername());
            }
        }
        return rtn;
    }

    @PostMapping("/checkFile")
    public String checkFile(@RequestParam String location) {
        File file = new File(FILE_SAVE_PATH + location + ".zip");
        if (!file.exists()) {
            return "fail";
        }
        return "success";
    }

    /**
     * 下载文件 需要解压
     * @param location
     * @param response
     */
    @GetMapping("/downloadFile")
    public void downloadFile(@RequestParam String location, HttpServletResponse response) {
        try {
            // 文件原始路径
            String filePath = FILE_SAVE_PATH + location;
            File file = new File(filePath + ".zip");
            if (!file.exists()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            String encodedFileName = URLEncoder.encode(file.getName(), StandardCharsets.UTF_8.toString());
            encodedFileName = encodedFileName.replaceAll("\\+", "%20");

            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFileName);

            BufferedInputStream inputStream = new BufferedInputStream(Files.newInputStream(file.toPath()));
            // 先解压
            ZipInputStream zipIn = new ZipInputStream(inputStream);
            // 解压文件
            zipIn.getNextEntry();


            // 创建一个临时字节输出流 用来保存解压文件流数据
            ByteArrayOutputStream bao = new ByteArrayOutputStream();

            // 设定每次最大读取流大小
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = zipIn.read(buffer)) != -1) {
                bao.write(buffer,0, bytesRead);
            }

            // 关闭流 关闭流的顺序不建议调换，先用哪个就最后关哪个
            zipIn.close();
            inputStream.close();

            // 输出
            byte[] byteArray = bao.toByteArray();
            // 判断是否VIP 如果是VIP那就全速下载，如果不是，那么就读取50*1024b大小
            int size = getUser().getVip() == 1 ? byteArray.length : 50 * 1024;
            // 因为我们是字节数组流，索引是从0开始的，就算不是数组，我们一个文件的字节都是从0 开始的
            int start = 0;
            // 这里获取size和字节数组长度之间的最小值，取最小的那个
            // 为什么这么做呢？因为如果说我们读取的长度大小比字节数组的长度还小，比如说我的文件是10kb，但是我要求的速度是50kb，那还限速个屁啊。
            int end = Math.min(size,byteArray.length);
            // 循环逐步输出数据流
            // 获取输出流
            ServletOutputStream outputStream = response.getOutputStream();
            while (start < byteArray.length) {
                // 将当前块的数据写入response的输出流
                outputStream.write(byteArray, start, end - start);
                // 刷新缓冲区
                outputStream.flush();
                // 如果不是vip，那么就延迟1秒读取一次
                if (getUser().getVip() == 0) {
                    Thread.sleep(1000);
                }
                // 更新起始位置和结束位置 因为我们读取到了某一个位置的时候，下一次读取时，就要从这个位置开始
                start = end;
                // 这里的含义跟上面的end一样
                end = Math.min(start + size, byteArray.length);
            }

            outputStream.close();
            bao.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
