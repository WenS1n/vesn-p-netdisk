package cn.vesns.netdisk.pojo;



import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Data
@Table(name = "userfile", uniqueConstraints = {
        @UniqueConstraint(name = "fileindex", columnNames = {"fileName", "filePath", "extendName", "deleteFlag", "userId"})})
@TableName("userfile")
public class UserFile {

  @Id
  @TableId
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private Long userFileId;

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private Long userId;

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private Long fileId;


  private String fileName;


  private String filePath;


  private String extendName;


  private Integer isDir;


  private String uploadTime;


  private Integer deleteFlag;


  private String deleteTime;


  private String deleteBatchNum;

}
