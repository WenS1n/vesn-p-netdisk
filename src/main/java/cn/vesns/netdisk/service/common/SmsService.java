package cn.vesns.netdisk.service.common;/**
 * @version :JDK1.8
 * @date : 2021-10-25 21:32
 * @author : vip865047755@126.com
 * @File : SmsService.java
 * @software: IntelliJ IDEA
 */

import cn.vesns.netdisk.common.lang.ResponseResult;
import com.qiwenshare.common.result.RestResult;

/**
 * @author: vesns vip865047755@126.com
 * @Title: SmsService
 * @ProjectName: netdisk
 * @Description:
 * @date: 2021-10-25 21:32
 */
public interface SmsService {
    /**
     * 发送短信验证码
     *
     * @param phoneNumber 手机号
     * @return
     */
    ResponseResult<String> sendSmsCode(String phoneNumber);

    /**
     * 验证短信验证码
     *
     * @param phoneNumber 手机号
     * @param smsCode     短信验证码
     * @return
     */
    ResponseResult<String> verifySmsCode(String phoneNumber, String smsCode);
}
