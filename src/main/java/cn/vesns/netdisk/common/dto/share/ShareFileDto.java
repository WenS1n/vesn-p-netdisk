package cn.vesns.netdisk.common.dto.share;/**
 * @version :JDK1.8
 * @date : 2021-11-15 22:36
 * @author : vip865047755@126.com
 * @File : ShareFileDto.java
 * @software: IntelliJ IDEA
 */

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: vesns vip865047755@126.com
 * @Title: ShareFileDto
 * @ProjectName: netdisk
 * @Description:
 * @date: 2021-11-15 22:36
 */
@Schema(name = "分享文件dto",required = true)
@Data
public class ShareFileDto {
    @Schema(description="文件集合")
    private String files;
    @Schema(description = "过期日期", example="2020-05-23 22:10:33")
    private String endTime;
    @Schema(description = "分享类型", example="0公共分享，1私密分享，2好友分享")
    private Integer shareType;
    @Schema(description = "备注", example="")
    private String remarks;

}
