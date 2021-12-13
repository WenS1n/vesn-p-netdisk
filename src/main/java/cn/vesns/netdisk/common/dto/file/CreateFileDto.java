package cn.vesns.netdisk.common.dto.file;/**
 * @version :JDK1.8
 * @date : 2021-11-08 21:20
 * @author : vip865047755@126.com
 * @File : CreateFileDto.java
 * @software: IntelliJ IDEA
 */

import com.qiwenshare.common.constant.RegexConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * @author: vesns vip865047755@126.com
 * @Title: CreateFileDto
 * @ProjectName: netdisk
 * @Description:
 * @date: 2021-11-08 21:20
 */
@Data
@Schema(name = "创建文件DTO",required = true)
public class CreateFileDto {

    @Schema(description="文件名", required=true)
    @NotBlank(message = "文件名不能为空")
    @Pattern(regexp = RegexConstant.FILE_NAME_REGEX, message = "文件名不合法！")
    private String fileName;
    @Schema(description="文件路径", required=true)
    private String filePath;
}
