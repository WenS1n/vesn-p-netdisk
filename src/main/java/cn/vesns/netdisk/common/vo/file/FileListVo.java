package cn.vesns.netdisk.common.vo.file;/**
 * @version :JDK1.8
 * @date : 2021-11-01 23:57
 * @author : vip865047755@126.com
 * @File : FileListVo.java
 * @software: IntelliJ IDEA
 */


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * @author: vesns vip865047755@126.com
 * @Title: FileListVo
 * @ProjectName: netdisk
 * @Description:
 * @date: 2021-11-01 23:57
 */
@Data
public class FileListVo {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long fileId;

    private String timeStampName;

    private String fileUrl;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long fileSize;

    @Deprecated
    private Integer isOSS;

    private Integer storageType;

    private Integer pointCount;

    private String identifier;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userFileId;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;


    private String fileName;

    private String filePath;

    private String extendName;

    private Integer isDir;

    private String uploadTime;

    private Integer deleteFlag;

    private String deleteTime;

    private String deleteBatchNum;
}
