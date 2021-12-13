package cn.vesns.netdisk.common.vo.user;/**
 * @version :JDK1.8
 * @date : 2021-09-24 23:41
 * @author : vip865047755@126.com
 * @File : RegisterVo.java
 * @software: IntelliJ IDEA
 */

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: vesns vip865047755@126.com
 * @Title: RegisterVo
 * @ProjectName: netdisk
 * @Description:
 * @date: 2021-09-24 23:41
 */
@Data
@Schema(name = "用户登录" ,required = true)
public class UserLoginVo {

    @Schema(description = "用户id", example = "891109714690048000")
    private long userId;

    @Schema(description = "用户名", example = "456484")
    private String username;

    @Schema(description = "真实名", example = "张三")
    private String realname;

    @Schema(description = "用户头像", example = "https://thirdqq.qlogo.cn/g?b=oidb&k=qxLE4dibR9sic8kS7mHLxlLw&s=100&t=1557468980")
    private String qqImageUrl;

    @Schema(description = "手机号", example = "187****1817")
    private String telephone;

    @Schema(description = "邮箱", example = "116****483@qq.com")
    private String email;

    @Schema(description = "地址", example = "四川省成都市金牛区万达广场4F666号")
    private String address;

    @Schema(description = "个人介绍", example = "错把陈醋当成墨，写尽半生都是酸")
    private String intro;

    @Schema(description = "用户头像地址", example = "\\upload\\20200405\\93811586079860974.png")
    private String imageUrl;

    @Schema(description = "注册时间", example = "2021-11-17 14:21:52")
    private String registerTime;

    @Schema(description = "最后登录时间", example = "021-11-17 14:21:52")
    private String lastLoginTime;

    @Schema(description = "Token 接口访问凭证")
    private String token;


}
