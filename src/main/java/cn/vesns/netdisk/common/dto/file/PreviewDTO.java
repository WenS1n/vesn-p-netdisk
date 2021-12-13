package cn.vesns.netdisk.common.dto.file;/**
 * @version :JDK1.8
 * @date : 2021-11-02 22:24
 * @author : vip865047755@126.com
 * @File : PreviewDTO.java
 * @software: IntelliJ IDEA
 */

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

/**
 * @author: vesns vip865047755@126.com
 * @Title: PreviewDTO
 * @ProjectName: netdisk
 * @Description:
 * @date: 2021-11-02 22:24
 */
@Data
@Schema(name = "预览文件DTO",required = true)
@ToString
public class PreviewDTO {

    private Long userFileId;
    private String token;
    @Schema(description="批次号")
    private String shareBatchNum;
    @Schema(description="提取码")
    private String extractionCode;
    private String isMin;
}
