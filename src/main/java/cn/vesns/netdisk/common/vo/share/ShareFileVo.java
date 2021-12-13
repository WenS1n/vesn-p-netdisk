package cn.vesns.netdisk.common.vo.share;/**
 * @version :JDK1.8
 * @date : 2021-11-15 22:35
 * @author : vip865047755@126.com
 * @File : ShareFileVo.java
 * @software: IntelliJ IDEA
 */

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: vesns vip865047755@126.com
 * @Title: ShareFileVo
 * @ProjectName: netdisk
 * @Description:
 * @date: 2021-11-15 22:35
 */
@Data
@Schema(description="分享文件VO")
public class ShareFileVo {
    @Schema(description="批次号")
    private String shareBatchNum;
    @Schema(description = "提取编码")
    private String extractionCode;

}
