package cn.vesns.netdisk.common.dto.file;/**
 * @version :JDK1.8
 * @date : 2021-11-11 23:11
 * @author : vip865047755@126.com
 * @File : MoveFileDto.java
 * @software: IntelliJ IDEA
 */

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: vesns vip865047755@126.com
 * @Title: MoveFileDto
 * @ProjectName: netdisk
 * @Description:
 * @date: 2021-11-11 23:11
 */
@Data
@Schema(name = "移动文件DTO",required = true)
public class MoveFileDto {

    @Schema(description = "文件路径", required = true)
    private String filePath;

    @Schema(description = "文件名", required = true)
    private String fileName;

    @Schema(description = "旧文件名", required = true)
    private String oldFilePath;
    @Schema(description = "扩展名", required = true)
    private String extendName;


}
