package cn.vesns.netdisk.pojo;


import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.Id;
import javax.persistence.Table;

@TableName("share")
@Table(name = "share")
public class Share {

  @Id
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private long shareId;
  private String endTime;
  private String extractionCode;
  private String shareBatchNum;
  private Integer shareStatus;
  private String shareTime;
  private Integer shareType;
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private long userId;

  public Integer getShareStatus() {
    return shareStatus;
  }

  public void setShareStatus(Integer shareStatus) {
    this.shareStatus = shareStatus;
  }

  public Integer getShareType() {
    return shareType;
  }

  public void setShareType(Integer shareType) {
    this.shareType = shareType;
  }

  public long getShareId() {
    return shareId;
  }

  public void setShareId(long shareId) {
    this.shareId = shareId;
  }


  public String getEndTime() {
    return endTime;
  }

  public void setEndTime(String endTime) {
    this.endTime = endTime;
  }


  public String getExtractionCode() {
    return extractionCode;
  }

  public void setExtractionCode(String extractionCode) {
    this.extractionCode = extractionCode;
  }


  public String getShareBatchNum() {
    return shareBatchNum;
  }

  public void setShareBatchNum(String shareBatchNum) {
    this.shareBatchNum = shareBatchNum;
  }




  public String getShareTime() {
    return shareTime;
  }

  public void setShareTime(String shareTime) {
    this.shareTime = shareTime;
  }




  public long getUserId() {
    return userId;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }

}
