package cn.vesns.netdisk.common.vo.share;/**
 * @version :JDK1.8
 * @date : 2021-11-02 19:50
 * @author : vip865047755@126.com
 * @File : ShareListVo.java
 * @software: IntelliJ IDEA
 */

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: vesns vip865047755@126.com
 * @Title: ShareListVo
 * @ProjectName: netdisk
 * @Description:
 * @date: 2021-11-02 19:50
 */
@Schema(description="分享列表VO")
@Data
public class ShareListVo {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long shareId;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;
    private String shareTime;
    private String endTime;
    private String extractionCode;
    private String shareBatchNum;
    private Integer shareType;//0公共，1私密，2好友
    private Integer shareStatus;//0正常，1已失效，2已撤销
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long shareFileId;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userFileId;
    private String shareFilePath;
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
    private String timeStampName;
    private String fileUrl;
    private Long fileSize;
    private Integer storageType;

}
