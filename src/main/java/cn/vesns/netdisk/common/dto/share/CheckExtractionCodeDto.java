package cn.vesns.netdisk.common.dto.share;/**
 * @version :JDK1.8
 * @date : 2021-11-15 23:40
 * @author : vip865047755@126.com
 * @File : CheckExtractionCodeDto.java
 * @software: IntelliJ IDEA
 */

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: vesns vip865047755@126.com
 * @Title: CheckExtractionCodeDto
 * @ProjectName: netdisk
 * @Description:
 * @date: 2021-11-15 23:40
 */
@Data
@Schema(name = "校验提取码DTO",required = true)
public class CheckExtractionCodeDto {
    @Schema(description="批次号")
    private String shareBatchNum;
    @Schema(description="提取码")
    private String extractionCode;
}
