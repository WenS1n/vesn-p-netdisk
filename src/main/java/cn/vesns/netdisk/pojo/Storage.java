package cn.vesns.netdisk.pojo;


import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "storage")
@TableName("storage")
public class Storage {
  @Id
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private Long storageId;
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private Long storageSize;
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private Long totalStorageSize;
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private Long userId;

  public Long getStorageId() {
    return storageId;
  }

  public void setStorageId(Long storageId) {
    this.storageId = storageId;
  }

  public Long getStorageSize() {
    return storageSize;
  }

  public void setStorageSize(Long storageSize) {
    this.storageSize = storageSize;
  }

  public Long getTotalStorageSize() {
    return totalStorageSize;
  }

  public void setTotalStorageSize(Long totalStorageSize) {
    this.totalStorageSize = totalStorageSize;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }
}
