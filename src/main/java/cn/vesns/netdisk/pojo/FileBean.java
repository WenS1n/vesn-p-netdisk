package cn.vesns.netdisk.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;


import javax.persistence.Id;
import javax.persistence.Table;


/**
 * @author: vesns vip865047755@126.com
 * @Title: File
 * @ProjectName: netdisk
 * @Description:
 * @date: 2021-10-17 16:19
 */
@Data
@Table(name = "file")
@TableName("file")
public class FileBean {
    @Id
    @TableId
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long fileId;

    private String fileUrl;

    private Long fileSize;

    private Integer storageType;

    private Integer pointCount;

    private String identifier;
}
