package cn.vesns.netdisk.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.vesns.netdisk.common.dto.user.RegisterDto;
import cn.vesns.netdisk.pojo.User;

import cn.vesns.netdisk.service.UserService;
import cn.vesns.netdisk.service.common.SmsService;
import cn.vesns.netdisk.util.JwtUtil;
import cn.vesns.netdisk.common.lang.ResponseResult;
import cn.vesns.netdisk.common.vo.user.UserLoginVo;
import com.alibaba.fastjson.JSON;
import com.qiwenshare.common.anno.MyLog;
import com.qiwenshare.common.result.RestResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;


import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : vip865047755@126.com
 * @version :JDK1.8
 * @date : 2021-09-23 22:21
 * @File : UserController.java
 * @software: IntelliJ IDEA
 */
@Tag(name = "user", description = "用户接口，用户登录，注册和校验token")
@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    @Resource
    UserService userService;

    @Resource
    SmsService smsService;



    public static Map<String, String> verificationCodeMap = new HashMap<>();

    public static final String CURRENT_MODULE = "用户管理";

    @Operation(summary = "用户注册", description = "注册账号", tags = {"user"})
    @PostMapping(value = "/register")
    @MyLog(operation = "用户注册", module = CURRENT_MODULE)
    public ResponseResult<String> register(@Valid RegisterDto registerDto) {
        ResponseResult<String> restResult = null;
        User user = new User();
        System.out.println("registerDto==================>" + registerDto.toString());
        ResponseResult<String> restResult1 = smsService.verifySmsCode(registerDto.getTelephone(),registerDto.getAuthcode());
        System.out.println("restResult1==================>" + restResult1.toString());
        if (restResult1.getCode() == 20000) {
            BeanUtil.copyProperties(registerDto, user);
            restResult = userService.registerUser(user);
            return restResult;
        }else {
            return ResponseResult.fail().data("用户注册失败，请检查输入内容");
        }
    }

    @Operation(summary = "用户登录", description = "用户登录认证后才能进入系统", tags = {"user"})
    @GetMapping("/login")
    @MyLog(operation = "用户登录", module = CURRENT_MODULE)
    public ResponseResult userLogin(
            @Parameter( description = "登录手机号")String telephone,
            @Parameter(description = "登录密码") String password) {
        User saveUserBean = userService.findUserInfoByTelephone(telephone);
        if (saveUserBean == null) {
            return ResponseResult.fail().message("手机号或密码错误。");
        }
        System.out.println(saveUserBean.toString() + "userinfo");
        String jwt = "";
        try {
            User userSession = new User();
            userSession.setPassword(saveUserBean.getPassword());
            userSession.setQqPassword(saveUserBean.getQqPassword());
            userSession.setTelephone(saveUserBean.getTelephone());
            userSession.setOpenId(saveUserBean.getOpenId());
            jwt = JwtUtil.createJWT("vesns", "vesn", JSON.toJSONString(userSession));
        } catch (Exception e) {
            log.info("登录异常：{} ->", e);
            return ResponseResult.fail().message("创建token失败！");
        }

        String passwordHash = new SimpleHash("MD5", password, saveUserBean.getSalt(), 1024).toHex();
        if (passwordHash.equals(saveUserBean.getPassword())) {
            UserLoginVo userLoginVo = new UserLoginVo();
            BeanUtil.copyProperties(saveUserBean, userLoginVo);
            userLoginVo.setToken(jwt);
            return ResponseResult.success().data(userLoginVo);
        } else {
            return ResponseResult.fail().message("手机号或密码错误！");
        }
    }

    @Operation(summary = "检查用户登录信息", description = "验证token的有效性", tags = {"user"})
    @GetMapping("/checkuserlogininfo")
    @ResponseBody
    public RestResult<User> checkUserLoginInfo(@RequestHeader("token") String token) {

        if ("undefined".equals(token) || StringUtils.isEmpty(token)) {
            System.out.println("==================>1" + token);
            return RestResult.fail().message("用户暂未登录");
        }
        User sessionUserBean = userService.getUserByToken(token);
        System.out.println("==================>2" + token);

        if (sessionUserBean != null) {
            System.out.println("==================>3" + token);
            return RestResult.success().data(sessionUserBean);

        } else {
            System.out.println("==================>4" + token);
            return RestResult.fail().message("用户暂未登录");
        }

    }
}
