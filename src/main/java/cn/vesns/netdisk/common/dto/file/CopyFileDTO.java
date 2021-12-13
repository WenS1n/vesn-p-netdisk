package cn.vesns.netdisk.common.dto.file;/**
 * @version :JDK1.8
 * @date : 2021-11-11 23:04
 * @author : vip865047755@126.com
 * @File : CopyFileDTO.java
 * @software: IntelliJ IDEA
 */

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: vesns vip865047755@126.com
 * @Title: CopyFileDTO
 * @ProjectName: netdisk
 * @Description:
 * @date: 2021-11-11 23:04
 */
@Data
@Schema(name = "复制文件DTO",required = true)
public class CopyFileDTO {
    @Schema(description = "用户文件id", required = true)
    private long userFileId;

    @Schema(description = "文件路径", required = true)
    private String filePath;
}
