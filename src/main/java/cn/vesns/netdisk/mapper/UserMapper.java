package cn.vesns.netdisk.mapper;

import cn.vesns.netdisk.pojo.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @author : vip865047755@126.com
 * @version :JDK1.8
 * @date : 2021-09-23 22:39
 * @File : UserMapper.java
 * @software: IntelliJ IDEA
 */
public interface UserMapper extends BaseMapper<User> {

    int insertUser (User user);

    int insertRole (long userId,long roleId);

}