package cn.vesns.netdisk.common.dto.file;/**
 * @version :JDK1.8
 * @date : 2021-11-11 23:17
 * @author : vip865047755@126.com
 * @File : BatchMoveFileDTO.java
 * @software: IntelliJ IDEA
 */

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: vesns vip865047755@126.com
 * @Title: BatchMoveFileDTO
 * @ProjectName: netdisk
 * @Description:
 * @date: 2021-11-11 23:17
 */
@Data
@Schema(name = "批量移动文件dto", required = true)
public class BatchMoveFileDTO {

    @Schema(description = "文件集合", required = true)
    private String files;
    @Schema(description = "目的文件路径", required = true)
    private String filePath;

}
