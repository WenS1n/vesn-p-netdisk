package cn.vesns.netdisk.common.dto.file;/**
 * @version :JDK1.8
 * @date : 2021-11-11 22:54
 * @author : vip865047755@126.com
 * @File : UnZipFileDto.java
 * @software: IntelliJ IDEA
 */

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: vesns vip865047755@126.com
 * @Title: UnZipFileDto
 * @ProjectName: netdisk
 * @Description:
 * @date: 2021-11-11 22:54
 */
@Data
@Schema(name = "解压缩文件DTO", required = true)
public class UnZipFileDto {
    @Schema(description = "文件url", required = true)
    private long userFileId;

    @Schema(description = "解压模式 0-解压到当前文件夹， 1-自动创建该文件名目录，并解压到目录里， 2-手动选择解压目录", required = true)
    private int unzipMode;

    @Schema(description = "解压目的文件目录，仅当 unzipMode 为 2 时必传")
    private String filePath;

}
