package cn.vesns.netdisk.pojo;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Table;


@Table(name = "recoveryfile")
@TableName("recoveryfile")
@ToString
public class RecoveryFile {
    @TableId
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long recoveryFileId;

    private String deleteBatchNum;

    private String deleteTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userFileId;


    public Long getRecoveryFileId() {
        return recoveryFileId;
    }

    public void setRecoveryFileId(Long recoveryFileId) {
        this.recoveryFileId = recoveryFileId;
    }

    public String getDeleteBatchNum() {
        return deleteBatchNum;
    }

    public void setDeleteBatchNum(String deleteBatchNum) {
        this.deleteBatchNum = deleteBatchNum;
    }


    public String getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(String deleteTime) {
        this.deleteTime = deleteTime;
    }


    public Long getUserFileId() {
        return userFileId;
    }

    public void setUserFileId(Long userFileId) {
        this.userFileId = userFileId;
    }
}
