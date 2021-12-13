package cn.vesns.netdisk.pojo;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.Table;

/**
 * @author 86504
 */
@TableName("operationlog")
@Table(name = "operationlog")
public class Operationlog {

  @TableId
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private long operationLogId;

  private String detail;

  private String logLevel;

  private String operation;

  private String operationObj;

  private String result;

  private String source;

  private String terminal;

  private String time;

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private long userId;


  public long getOperationLogId() {
    return operationLogId;
  }

  public void setOperationLogId(long operationLogId) {
    this.operationLogId = operationLogId;
  }


  public String getDetail() {
    return detail;
  }

  public void setDetail(String detail) {
    this.detail = detail;
  }


  public String getLogLevel() {
    return logLevel;
  }

  public void setLogLevel(String logLevel) {
    this.logLevel = logLevel;
  }


  public String getOperation() {
    return operation;
  }

  public void setOperation(String operation) {
    this.operation = operation;
  }


  public String getOperationObj() {
    return operationObj;
  }

  public void setOperationObj(String operationObj) {
    this.operationObj = operationObj;
  }


  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
  }


  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }


  public String getTerminal() {
    return terminal;
  }

  public void setTerminal(String terminal) {
    this.terminal = terminal;
  }


  public String getTime() {
    return time;
  }

  public void setTime(String time) {
    this.time = time;
  }


  public long getUserId() {
    return userId;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }

}
