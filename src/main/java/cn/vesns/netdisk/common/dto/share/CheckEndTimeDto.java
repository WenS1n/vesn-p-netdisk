package cn.vesns.netdisk.common.dto.share;/**
 * @version :JDK1.8
 * @date : 2021-11-15 23:43
 * @author : vip865047755@126.com
 * @File : CheckEndTimeDto.java
 * @software: IntelliJ IDEA
 */

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: vesns vip865047755@126.com
 * @Title: CheckEndTimeDto
 * @ProjectName: netdisk
 * @Description:
 * @date: 2021-11-15 23:43
 */
@Data
@Schema(name = "校验过期时间dto",required = true)
public class CheckEndTimeDto {
    @Schema(description="批次号")
    private String shareBatchNum;
}
