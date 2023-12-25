package exam.gcc.domain;

import javax.persistence.*;
import java.util.Date;

@Entity
public class SaveFile {
    private String filename;
    private String location;
    private Date updatetime;
    private Long filesize;
    private String hash;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFilesize() {
        return filesize;
    }

    public void setFilesize(Long filesize) {
        this.filesize = filesize;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    @Override
    public String toString() {
        return "SaveFile{" +
                "filename='" + filename + '\'' +
                ", location='" + location + '\'' +
                ", updatetime=" + updatetime +
                ", filesize=" + filesize +
                ", hash='" + hash + '\'' +
                ", id=" + id +
                '}';
    }
}