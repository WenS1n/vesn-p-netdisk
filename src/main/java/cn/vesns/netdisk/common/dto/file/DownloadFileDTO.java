package cn.vesns.netdisk.common.dto.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

/**
 * @author: vesns vip865047755@126.com
 * @Title: DownloadFileDTO
 * @ProjectName: netdisk
 * @Description:
 * @date: 2021-11-02 22:23
 */
@Data
@Schema(name = "下载文件DTO",required = true)
@ToString
public class DownloadFileDTO {
    private Long userFileId;
    private String token;
    @Schema(description="批次号")
    private String shareBatchNum;
    @Schema(description="提取码")
    private String extractionCode;
}
