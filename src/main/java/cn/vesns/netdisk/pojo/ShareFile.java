package cn.vesns.netdisk.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;

@Data
@TableName("sharefile")
@Table(name = "sharefile")
public class ShareFile {
    @Id
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long shareFileId;
    private String shareBatchNum;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userFileId;
    private String shareFilePath;

}
