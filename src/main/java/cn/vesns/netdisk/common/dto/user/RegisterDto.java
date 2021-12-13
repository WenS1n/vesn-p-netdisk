package cn.vesns.netdisk.common.dto.user;/**
 * @version :JDK1.8
 * @date : 2021-09-24 23:28
 * @author : vip865047755@126.com
 * @File : User.java
 * @software: IntelliJ IDEA
 */


import cn.vesns.netdisk.helper.RegexConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @author: vesns vip865047755@126.com
 * @Title: User
 * @ProjectName: netdisk
 * @Description:
 * @date: 2021-09-24 23:28
 */
@Data
@ToString
@Schema(name = "用户注册DTO",required = true)
public class RegisterDto {

    @Schema(description = "用户名", required = true, example = "南站枪哥")
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3,max = 20,message = "用户名不能少于3位，最多20位")
    private String username;

    @Schema(description = "手机号", required = true,example = "13866668888")
    @Pattern(regexp = RegexConstant.PHONE_REGEX, message = "手机号输入有误")
    @NotBlank(message = "手机号不能为空")
    private String telephone;

    @Schema(description = "密码", required = true,example = "password111//$")
    @Pattern(regexp = RegexConstant.PASSWORD_REGEX,message = "密码长度6-20位，不允许中文")
    @NotBlank(message = "密码不能为空")
    private String password;

    @Schema(description = "验证码", required = true, example = "123456")
    @NotBlank(message = "验证码不能为空")
    @Size(min = 6, max = 6, message = "验证码只能有6位")
    private String authcode;


}
