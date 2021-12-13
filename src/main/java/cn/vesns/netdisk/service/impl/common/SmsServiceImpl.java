package cn.vesns.netdisk.service.impl.common;/**
 * @version :JDK1.8
 * @date : 2021-10-25 21:33
 * @author : vip865047755@126.com
 * @File : SmsServiceImpl.java
 * @software: IntelliJ IDEA
 */

import cn.vesns.netdisk.common.lang.ResponseResult;
import cn.vesns.netdisk.config.SmsConfig;
import cn.vesns.netdisk.service.common.SmsService;
import cn.vesns.netdisk.util.RandomUtil;
import cn.vesns.netdisk.util.RedisUtils;
import cn.vesns.netdisk.util.SmsUtil;
import com.qiwenshare.common.result.RestResult;
import com.tencentcloudapi.sms.v20190711.models.SendStatus;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author: vesns vip865047755@126.com
 * @Title: SmsServiceImpl
 * @ProjectName: netdisk
 * @Description:
 * @date: 2021-10-25 21:33
 */
@Service
public class SmsServiceImpl implements SmsService {

    @Resource
    private SmsConfig smsConfig;
    @Resource
    private RedisUtils redisUtils;

    @Override
    public ResponseResult<String> sendSmsCode(String phoneNumber) {

        // 下发手机号码，采用e.164标准，+[国家或地区码][手机号]
        String[] phoneNumbers = {"+86" + phoneNumber};
        // 生成6位随机数字字符串
        String smsCode = RandomUtil.randomNumbers(6);
        // 模板参数：若无模板参数，则设置为空（参数1为随机验证码，参数2为有效时间）
        String[] templateParams = {smsCode, smsConfig.getExpireTime()};
        // 发送短信验证码
        SendStatus[] sendStatuses = SmsUtil.sendSms(smsConfig, templateParams, phoneNumbers);
        if ("Ok".equals(sendStatuses[0].getCode())) {
            // 创建缓存的key
            String key = RedisUtils.createCacheKey(smsConfig.getPhonePrefix(), phoneNumber);
            // 将验证码缓存到redis并设置过期时间
            redisUtils.setCacheObject(key, smsCode, Long.parseLong(smsConfig.getExpireTime()));
            return  ResponseResult.success();
        } else {
            return ResponseResult.fail().data(sendStatuses[0].getMessage()) ;
        }
    }

    @Override
    public ResponseResult<String> verifySmsCode(String phoneNumber, String smsCode) {
        // 创建key
        String key = RedisUtils.createCacheKey(smsConfig.getPhonePrefix(), phoneNumber);
        // 判断指定key是否存在并且未过期
        if (redisUtils.hasKey(key)) {
            // 验证输入的验证码是否正确
            if (smsCode.equals(redisUtils.getCacheObject(key))) {
                // 验证成功后删除验证码缓存
                redisUtils.delete(key);
                return ResponseResult.success().data("验证成功");
            } else {
                return ResponseResult.fail().code(20002).data("验证码错误");
            }
        } else {
            return ResponseResult.fail().code(20003).data("验证码已失效");
        }
    }
}
