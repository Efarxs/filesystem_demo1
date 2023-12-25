package exam.gcc.business.repository;

import exam.gcc.domain.SaveFile;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FileRepository {
    List<SaveFile> getAllFiles();
    List<SaveFile> getFiles(String username);
    int addFile(SaveFile saveFile);
    int deleteFile(Long id);
    int deleteFileConnection(Long id);
    int deleteFileConnection4User(Long id, String username);
    int addFileConnection(Long file_id, String username);

    SaveFile getFileByHash(String md5_hash);
}
