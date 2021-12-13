package cn.vesns.netdisk.common.dto.file;/**
 * @version :JDK1.8
 * @date : 2021-11-15 22:29
 * @author : vip865047755@126.com
 * @File : RestoreFileDto.java
 * @software: IntelliJ IDEA
 */

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: vesns vip865047755@126.com
 * @Title: RestoreFileDto
 * @ProjectName: netdisk
 * @Description:
 * @date: 2021-11-15 22:29
 */
@Data
@Schema(name = "回收文件DTO",required = true)
public class RestoreFileDto {
    @Schema(description="删除批次号")
    private String deleteBatchNum;
    @Schema(description="文件路径")
    private String filePath;
}
