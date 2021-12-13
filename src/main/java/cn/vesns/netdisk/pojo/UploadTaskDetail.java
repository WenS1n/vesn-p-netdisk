package cn.vesns.netdisk.pojo;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.Table;

@Data
@TableName("uploadtaskdetail")
@Table(name = "uploadtaskdetail")
public class UploadTaskDetail {

  private long uploadTaskDetailId;
  private long chunkNumber;
  private long chunkSize;
  private String filePath;
  private String filename;
  private String identifier;
  private String relativePath;
  private long totalChunks;
  private long totalSize;



  public long getUploadTaskDetailId() {
    return uploadTaskDetailId;
  }

  public void setUploadTaskDetailId(long uploadTaskDetailId) {
    this.uploadTaskDetailId = uploadTaskDetailId;
  }


  public long getChunkNumber() {
    return chunkNumber;
  }

  public void setChunkNumber(long chunkNumber) {
    this.chunkNumber = chunkNumber;
  }


  public long getChunkSize() {
    return chunkSize;
  }

  public void setChunkSize(long chunkSize) {
    this.chunkSize = chunkSize;
  }


  public String getFilePath() {
    return filePath;
  }

  public void setFilePath(String filePath) {
    this.filePath = filePath;
  }


  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }


  public String getIdentifier() {
    return identifier;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }


  public String getRelativePath() {
    return relativePath;
  }

  public void setRelativePath(String relativePath) {
    this.relativePath = relativePath;
  }


  public long getTotalChunks() {
    return totalChunks;
  }

  public void setTotalChunks(long totalChunks) {
    this.totalChunks = totalChunks;
  }


  public long getTotalSize() {
    return totalSize;
  }

  public void setTotalSize(long totalSize) {
    this.totalSize = totalSize;
  }

}
