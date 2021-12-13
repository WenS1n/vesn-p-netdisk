package cn.vesns.netdisk.util;/**
 * @version :JDK1.8
 * @date : 2021-10-25 21:30
 * @author : vip865047755@126.com
 * @File : RedisUtil.java
 * @software: IntelliJ IDEA
 */

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author: vesns vip865047755@126.com
 * @Title: RedisUtil
 * @ProjectName: netdisk
 * @Description:
 * @date: 2021-10-25 21:30
 */
@Component
public class RedisUtils {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 缓存基本对象
     *
     * @param key    键
     * @param value  值
     * @param expire 键的过期时间
     */
    public void setCacheObject(String key, Object value, long expire) {
        redisTemplate.opsForValue().set(key, value);
        if (expire > 0) {
            redisTemplate.expire(key, expire, TimeUnit.MINUTES);
        }
    }

    /**
     * 获取指定键的缓存对象
     *
     * @param key 键
     * @return 缓存对象
     */
    public Object getCacheObject(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 判断键是否存在并且未过期
     *
     * @param key 键
     * @return true，键存在并且未过期；false，键不存在或存在但过期
     */
    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key) && getExpire(key) > 0 ? true : false;
    }

    /**
     * 获取键的过期时间
     *
     * @param key 键
     * @return 过期时间
     */
    public long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.MINUTES);
    }

    /**
     * 删除指定键的缓存
     *
     * @param key 键
     */
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 创建缓存的键
     *
     * @param prefix      前缀
     * @param phoneNumber 手机号
     * @return 键
     */
    public static String createCacheKey(String prefix, String phoneNumber) {
        return prefix + phoneNumber;
    }
}
