package exam.gcc.business.service;

import exam.gcc.business.repository.FileRepository;
import exam.gcc.domain.SaveFile;
import exam.gcc.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public class FileService {
    @Autowired
    private FileRepository fileRepository;
    @Value("${FILE_SAVE_PATH}")
    private String FILE_SAVE_PATH;

    public SaveFile getFileByHash(String hash) {
        return fileRepository.getFileByHash(hash);
    }

    public List<SaveFile> getFiles(String username){
        return fileRepository.getFiles(username);
    }

    public void checkFileExist() {
        List<SaveFile> allFiles = fileRepository.getAllFiles();
        for (SaveFile file : allFiles) {
            File f = new File(FILE_SAVE_PATH +file.getLocation());
            if(!f.exists()){
                deleteFile4All(file.getId());
            }
        }
    }

    public int deleteFile4All(Long id){
        return fileRepository.deleteFile(id) + fileRepository.deleteFileConnection(id);
    }

    public int deleteFile4User(Long id, String username){
        return fileRepository.deleteFileConnection4User(id, username);
    }

    public int addFile(SaveFile saveFile, User user){
        return fileRepository.addFile(saveFile) * fileRepository.addFileConnection(saveFile.getId(),user.getUsername());
    }
}