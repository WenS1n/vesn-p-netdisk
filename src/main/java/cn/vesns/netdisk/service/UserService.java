package cn.vesns.netdisk.service;/**
 * @version :JDK1.8
 * @date : 2021-09-23 22:29
 * @author : vip865047755@126.com
 * @File : IUserService.java
 * @software: IntelliJ IDEA
 */

import cn.vesns.netdisk.pojo.User;
import cn.vesns.netdisk.common.lang.ResponseResult;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author: vesns vip865047755@126.com
 * @Title: IUserService
 * @ProjectName: netdisk
 * @Description:
 * @date: 2021-09-23 22:29
 */
public interface UserService  extends IService<User> {

    /**
     * 用户注册
     * @param user 用户信息
     * @return 结果
     */
    ResponseResult<String> registerUser(User user);

    /**
     * 根据用户手机号查找用户
     * @param telephone
     * @return
     */
    User findUserInfoByTelephone(String telephone);

    /**
     * 得到用户token
     * @param token
     * @return
     */
    User getUserByToken(String token);

    /**
     * 根据openid查询用户
     * @param openId
     * @return
     */
    User selectUserByOpenId(String openId);





}
