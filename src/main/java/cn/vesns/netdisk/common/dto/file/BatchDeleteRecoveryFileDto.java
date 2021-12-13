package cn.vesns.netdisk.common.dto.file;/**
 * @version :JDK1.8
 * @date : 2021-11-15 22:22
 * @author : vip865047755@126.com
 * @File : BatchDeleteRecoveryFileDto.java
 * @software: IntelliJ IDEA
 */

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: vesns vip865047755@126.com
 * @Title: BatchDeleteRecoveryFileDto
 * @ProjectName: netdisk
 * @Description:
 * @date: 2021-11-15 22:22
 */@Data
@Schema(name = "批量删除回收文件DTO",required = true)
public class BatchDeleteRecoveryFileDto {
    @Schema(description="恢复文件集合")
    private String recoveryFileIds;
}
