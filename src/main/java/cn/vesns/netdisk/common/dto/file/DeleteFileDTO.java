package cn.vesns.netdisk.common.dto.file;/**
 * @version :JDK1.8
 * @date : 2021-11-11 22:40
 * @author : vip865047755@126.com
 * @File : DeleteFileDTO.java
 * @software: IntelliJ IDEA
 */

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: vesns vip865047755@126.com
 * @Title: DeleteFileDTO
 * @ProjectName: netdisk
 * @Description:
 * @date: 2021-11-11 22:40
 */
@Data
@Schema(name = "删除文件dto", required = true)
public class DeleteFileDTO {
    @Schema(description = "用户文件id", required = true)
    private Long userFileId;

}
