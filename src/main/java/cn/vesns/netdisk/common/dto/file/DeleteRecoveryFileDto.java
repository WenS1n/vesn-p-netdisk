package cn.vesns.netdisk.common.dto.file;/**
 * @version :JDK1.8
 * @date : 2021-11-15 22:05
 * @author : vip865047755@126.com
 * @File : DeleteRecoveryFileDto.java
 * @software: IntelliJ IDEA
 */

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: vesns vip865047755@126.com
 * @Title: DeleteRecoveryFileDto
 * @ProjectName: netdisk
 * @Description:
 * @date: 2021-11-15 22:05
 */
@Data
@Schema(name = "删除回收文件DTO",required = true)
public class DeleteRecoveryFileDto {
    @Schema(description = "回收文件id")
    private Long recoveryFileId;

}
