package cn.vesns.netdisk.helper;/**
 * @version :JDK1.8
 * @date : 2021-09-25 0:07
 * @author : vip865047755@126.com
 * @File : UserInfoHelper.java
 * @software: IntelliJ IDEA
 */

import cn.vesns.netdisk.mapper.UserMapper;
import cn.vesns.netdisk.pojo.User;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author: vesns vip865047755@126.com
 * @Title: UserInfoHelper
 * @ProjectName: netdisk
 * @Description:
 * @date: 2021-09-25 0:07
 */
@Component
public class UserInfoHelper {

    @Resource
    UserMapper userMapper;


    public boolean isUserNameExist(User user) {
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User :: getUsername,user.getUsername());
        List<User> users = userMapper.selectList(lambdaQueryWrapper);
        if (users != null && !users.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isTelPhoneExist(User user) {
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User :: getTelephone,user.getTelephone());
        List<User> users = userMapper.selectList(lambdaQueryWrapper);
        if (users != null && !users.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean isPhoneFormatRight(String phone){
        boolean isRight = Pattern.matches(RegexConstant.PASSWORD_REGEX, phone);
        return isRight;
    }

}
