package cn.vesns.netdisk.common.dto.share;/**
 * @version :JDK1.8
 * @date : 2021-11-15 23:01
 * @author : vip865047755@126.com
 * @File : SaveShareFileDto.java
 * @software: IntelliJ IDEA
 */

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: vesns vip865047755@126.com
 * @Title: SaveShareFileDto
 * @ProjectName: netdisk
 * @Description:
 * @date: 2021-11-15 23:01
 */
@Data
@Schema(name = "保存分享文件DTO",required = true)
public class SaveShareFileDto {
    @Schema(description="文件集合", example = "[{\"userFileId\":12},{\"userFileId\":13}]")
    private String files;
    @Schema(description = "文件路径")
    private String filePath;

}
