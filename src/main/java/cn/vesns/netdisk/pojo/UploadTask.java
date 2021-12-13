package cn.vesns.netdisk.pojo;

import com.baomidou.mybatisplus.annotation.TableName;

import javax.persistence.Table;

@TableName("uploadtask")
@Table(name = "uploadtask")
public class UploadTask {

  private long uploadTaskId;
  private String extendName;
  private String fileName;
  private String filePath;
  private String identifier;
  private long uploadStatus;
  private String uploadTime;
  private long userId;


  public long getUploadTaskId() {
    return uploadTaskId;
  }

  public void setUploadTaskId(long uploadTaskId) {
    this.uploadTaskId = uploadTaskId;
  }


  public String getExtendName() {
    return extendName;
  }

  public void setExtendName(String extendName) {
    this.extendName = extendName;
  }


  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }


  public String getFilePath() {
    return filePath;
  }

  public void setFilePath(String filePath) {
    this.filePath = filePath;
  }


  public String getIdentifier() {
    return identifier;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }


  public long getUploadStatus() {
    return uploadStatus;
  }

  public void setUploadStatus(long uploadStatus) {
    this.uploadStatus = uploadStatus;
  }


  public String getUploadTime() {
    return uploadTime;
  }

  public void setUploadTime(String uploadTime) {
    this.uploadTime = uploadTime;
  }


  public long getUserId() {
    return userId;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }

}
