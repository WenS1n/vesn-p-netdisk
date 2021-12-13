package cn.vesns.netdisk.common.dto.file;/**
 * @version :JDK1.8
 * @date : 2021-11-10 21:22
 * @author : vip865047755@126.com
 * @File : RenameFileDTO.java
 * @software: IntelliJ IDEA
 */

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: vesns vip865047755@126.com
 * @Title: RenameFileDTO
 * @ProjectName: netdisk
 * @Description:
 * @date: 2021-11-10 21:22
 */
@Data
public class RenameFileDTO {

    @Schema(description = "用户文件id", required = true)
    private Long userFileId;

    @Schema(description = "文件名", required = true)
    private String fileName;
}
