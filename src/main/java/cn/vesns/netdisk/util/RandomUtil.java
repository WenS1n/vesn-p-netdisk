package cn.vesns.netdisk.util;/**
 * @version :JDK1.8
 * @date : 2021-10-25 21:30
 * @author : vip865047755@126.com
 * @File : RandomUtil.java
 * @software: IntelliJ IDEA
 */

import java.util.Random;

/**
 * @author: vesns vip865047755@126.com
 * @Title: RandomUtil
 * @ProjectName: netdisk
 * @Description:
 * @date: 2021-10-25 21:30
 */
public class RandomUtil {

    private static final Random RANDOM = new Random();

    /**
     * 生成指定位数的随机数字字符串
     *
     * @param length 字符串长度
     * @return 随机数字字符串
     */
    public static String randomNumbers(int length) {
        StringBuilder randomNumbers = new StringBuilder();
        for (int i = 0; i < length; i++) {
            randomNumbers.append(RANDOM.nextInt(10));
        }
        return randomNumbers.toString();
    }

}
