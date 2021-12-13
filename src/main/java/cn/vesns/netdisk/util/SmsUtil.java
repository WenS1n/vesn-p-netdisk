package cn.vesns.netdisk.util;/**
 * @version :JDK1.8
 * @date : 2021-10-25 20:40
 * @author : vip865047755@126.com
 * @File : SmsUtil.java
 * @software: IntelliJ IDEA
 */


import cn.vesns.netdisk.config.SmsConfig;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20190711.SmsClient;
import com.tencentcloudapi.sms.v20190711.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20190711.models.SendSmsResponse;
import com.tencentcloudapi.sms.v20190711.models.SendStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: vesns vip865047755@126.com
 * @Title: SmsUtil
 * @ProjectName: netdisk
 * @Description: 腾讯云短信工具类
 * @date: 2021-10-25 20:40
 */

public class SmsUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmsUtil.class);

    /**
     * 发送短信
     *
     * @param smsConfig      腾讯云短信配置对象
     * @param templateParams 模板参数
     * @param phoneNumbers   手机号数组
     * @return SendStatus[]，短信发送状态
     */
    public static SendStatus[] sendSms(SmsConfig smsConfig, String[] templateParams, String[] phoneNumbers) {
        try {
            // 实例化一个认证对象，入参需要传入腾讯云账户密钥对secretId，secretKey。
            Credential cred = new Credential(smsConfig.getSecretId(), smsConfig.getSecretKey());
            // 实例化一个http选项，可选，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();
            // SDK默认使用POST方法
            httpProfile.setReqMethod("POST");
            // SDK有默认的超时时间，非必要请不要进行调整
            httpProfile.setConnTimeout(120);
            // 非必要步骤：实例化一个客户端配置对象，可以指定超时时间等配置
            ClientProfile clientProfile = new ClientProfile();
            // SDK默认用TC3-HMAC-SHA256进行签名，非必要请不要修改这个字段
            clientProfile.setSignMethod("HmacSHA256");
            clientProfile.setHttpProfile(httpProfile);
            // 实例化要请求产品(以sms为例)的client对象，第二个参数是地域信息，可以直接填写字符串ap-guangzhou，或者引用预设的常量
            SmsClient smsClient = new SmsClient(cred, "ap-guangzhou", clientProfile);
            // 实例化一个请求对象
            SendSmsRequest req = new SendSmsRequest();
            // 设置短信应用ID：短信SdkAppId在[短信控制台]添加应用后生成的实际SdkAppId
            req.setSmsSdkAppid(smsConfig.getAppId());
            // 设置短信签名内容：使用UTF-8编码，必须填写已审核通过的签名，签名信息可登录[短信控制台]查看

            req.setSign(smsConfig.getSign());
            // 设置国际/港澳台短信SenderId：国内短信填空，默认未开通
            req.setSenderId("");
            // 设置模板ID：必须填写已审核通过的模板ID。模板ID可登录[短信控制台]查看
            req.setTemplateID(smsConfig.getTemplateId());
            // 设置下发手机号码，采用E.164标准，+[国家或地区码][手机号]
            req.setPhoneNumberSet(phoneNumbers);
            // 设置模板参数：若无模板参数，则设置为空
            req.setTemplateParamSet(templateParams);
            // 通过client对象调用SendSms方法发起请求。注意请求方法名与请求对象是对应的，返回的res是一个SendSmsResponse类的实例，与请求对象对应
            SendSmsResponse res = smsClient.SendSms(req);
            // 控制台打印日志输出json格式的字符串回包
            LOGGER.info(SendSmsResponse.toJsonString(res));
            return res.getSendStatusSet();
        } catch (TencentCloudSDKException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

}
