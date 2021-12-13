package cn.vesns.netdisk.controller.common;/**
 * @version :JDK1.8
 * @date : 2021-10-25 21:31
 * @author : vip865047755@126.com
 * @File : controller.java
 * @software: IntelliJ IDEA
 */

import cn.vesns.netdisk.common.lang.ResponseResult;
import cn.vesns.netdisk.service.common.SmsService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: vesns vip865047755@126.com
 * @Title: controller
 * @ProjectName: netdisk
 * @Description:
 * @date: 2021-10-25 21:31
 */
@RequestMapping("/sms")
@RestController
public class SmsController {


    @Resource
    private SmsService smsService;

    @GetMapping("/send")
    public ResponseResult<String> sendSmsCode(@RequestParam(value = "telephone") String telephone) {
        return smsService.sendSmsCode(telephone);
    }

    @PostMapping("/verify")
    public ResponseResult<String> verifySmsCode(@RequestParam(value = "phoneNumber") String phoneNumber,
                                @RequestParam(value = "smsCode") String smsCode) {
        return smsService.verifySmsCode(phoneNumber, smsCode);
    }


}
