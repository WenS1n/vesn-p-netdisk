package cn.vesns.netdisk.common.dto.share;/**
 * @version :JDK1.8
 * @date : 2021-11-15 23:35
 * @author : vip865047755@126.com
 * @File : ShareTypeDto.java
 * @software: IntelliJ IDEA
 */

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: vesns vip865047755@126.com
 * @Title: ShareTypeDto
 * @ProjectName: netdisk
 * @Description:
 * @date: 2021-11-15 23:35
 */
@Data
@Schema(name = "分享类型DTO",required = true)
public class ShareTypeDto {
    @Schema(description="批次号")
    private String shareBatchNum;

}
