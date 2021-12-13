package cn.vesns.netdisk.service.impl;
/**
 * @version :JDK1.8
 * @date : 2021-09-23 22:38
 * @author : vip865047755@126.com
 * @File : IUserServiceImpl.java
 * @software: IntelliJ IDEA
 */

import cn.vesns.netdisk.common.lang.ResponseResult;
import cn.vesns.netdisk.controller.UserController;
import cn.vesns.netdisk.helper.UserInfoHelper;
import cn.vesns.netdisk.mapper.UserMapper;
import cn.vesns.netdisk.pojo.User;
import cn.vesns.netdisk.service.UserService;
import cn.vesns.netdisk.util.*;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import javax.annotation.Resource;

/**
 * @author: vesns vip865047755@126.com
 * @Title: IUserServiceImpl
 * @ProjectName: netdisk
 * @Description:
 * @date: 2021-09-23 22:38
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    UserMapper userMapper;

    @Resource
    UserInfoHelper userInfoHelper;


    @Override
    public User getUserByToken(String token) {
        Claims c = null;
        if (StringUtils.isEmpty(token)) {
            System.out.println("==========>token is null");
            return null;
        }

        token = token.replace("Bearer ", "");
        try {
            c = JwtUtil.parseJWT(token);
        } catch (Exception e) {
            log.error("解码异常:" + e);
            e.printStackTrace();
            System.out.println("==========>解码异常");
            return null;
        }
        if (c == null) {
            log.error("解码为空");
            System.out.println("==========>解码为空");
            return null;
        }
        String subject = c.getSubject();
        log.debug("解析结果：----" + subject);
        System.out.println("--------------->1 subject ==>" + subject);

        User tokenUserBean = JSON.parseObject(subject, User.class);
        System.out.println("--------------->1 tokenUserBean.toString" + tokenUserBean.toString());

        User saveUserBean = new User();
        String tokenPassword = "";
        String savePassword = "";
        if (StringUtils.isNotEmpty(tokenUserBean.getPassword())) {
            saveUserBean = findUserInfoByTelephone(tokenUserBean.getTelephone());
            if (saveUserBean == null) {
                System.out.println("--------------->1" + saveUserBean);
                return null;
            }
            tokenPassword = tokenUserBean.getPassword();
            savePassword = saveUserBean.getPassword();
        } else if (StringUtils.isNotEmpty(tokenUserBean.getQqPassword())) {
            saveUserBean = selectUserByOpenId(tokenUserBean.getOpenId());
            if (saveUserBean == null) {
                System.out.println("--------------->1" + saveUserBean);
                return null;
            }
            tokenPassword = tokenUserBean.getQqPassword();
            savePassword = saveUserBean.getQqPassword();
            System.out.println("tokenPassword" + tokenPassword);
            System.out.println("savePassword" + savePassword);
        }
        if (StringUtils.isEmpty(tokenPassword) || StringUtils.isEmpty(savePassword)) {
            return null;
        }
        if (tokenPassword.equals(savePassword)) {
            return saveUserBean;
        } else {
            return null;
        }

    }

    /**
     * 用户注册
     *
     * @param user 用户信息
     * @return
     */
    @Override
    public ResponseResult registerUser(User user) {

        String telephone = user.getTelephone();
        UserController.verificationCodeMap.remove(telephone);
        if (!userInfoHelper.isPhoneFormatRight(user.getTelephone())) {
            return ResponseResult.fail().message("手机号格式不正确。");
        }
        if (userInfoHelper.isTelPhoneExist(user)) {
            return ResponseResult.fail().message("手机号已存在。");
        }
        if (userInfoHelper.isUserNameExist(user)) {
            return ResponseResult.fail().message("用户名已存在。");
        }
        //加盐

        String salt = PasswordUtil.getSaltValue();
        String newPassword = new SimpleHash("MD5", user.getPassword(), salt, 1024).toHex();
        user.setSalt(salt);
        user.setPassword(newPassword);
        user.setRegisterTime(DateUtil.getCurrentTime());
        SnowUtil snowUtil = new SnowUtil(0, 0);
        user.setUserId(snowUtil.nextId());
        System.out.println(user.toString() + "-------------------------------");
        int result = userMapper.insert(user);
        userMapper.insertRole(user.getUserId(), 2);
        if (result == 1) {
            return ResponseResult.success();
        } else {
            return ResponseResult.fail().message("用户注册失败，请检查输入信息");
        }
    }

    /**
     * 通过手机号获取用户信息
     * @param telephone
     * @return
     */
    @Override
    public User findUserInfoByTelephone(String telephone) {
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getTelephone, telephone);
        return userMapper.selectOne(userLambdaQueryWrapper);
    }


    @Override
    public User selectUserByOpenId(String openId) {
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getOpenId, openId);
        return userMapper.selectOne(lambdaQueryWrapper);
    }

}
