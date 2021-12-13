package cn.vesns.netdisk.common.dto.share;/**
 * @version :JDK1.8
 * @date : 2021-11-17 21:20
 * @author : vip865047755@126.com
 * @File : ShareFileListDto.java
 * @software: IntelliJ IDEA
 */

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: vesns vip865047755@126.com
 * @Title: ShareFileListDto
 * @ProjectName: netdisk
 * @Description:
 * @date: 2021-11-17 21:20
 */
@Data
@Schema(name = "分享文件列表DTO",required = true)
public class ShareFileListDto {
    @Schema(description="批次号")
    private String shareBatchNum;
    @Schema(description="分享文件路径")
    private String shareFilePath;


}
