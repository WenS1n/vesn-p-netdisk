package cn.vesns.netdisk.config;/**
 * @version :JDK1.8
 * @date : 2021-11-16 23:37
 * @author : vip865047755@126.com
 * @File : JacksonConfig.java
 * @software: IntelliJ IDEA
 */

import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.bind.annotation.ControllerAdvice;

/**
 * @author: vesns vip865047755@126.com
 * @Title: JacksonConfig
 * @ProjectName: netdisk
 * @Description:
 * @date: 2021-11-16 23:37
 */
@Configuration
public class JacksonConfig {

    /**
     * Jackson全局转化long类型为String，解决jackson序列化时传入前端Long类型缺失精度问题
     */
//    @Bean
//    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
//        Jackson2ObjectMapperBuilderCustomizer cunstomizer = new Jackson2ObjectMapperBuilderCustomizer() {
//            @Override
//            public void customize(Jackson2ObjectMapperBuilder jacksonObjectMapperBuilder) {
////				jacksonObjectMapperBuilder.serializerByType(BigInteger.class, ToStringSerializer.instance);
//                jacksonObjectMapperBuilder.serializerByType(Long.class, ToStringSerializer.instance);
////                jacksonObjectMapperBuilder.serializerByType(Long.TYPE, ToStringSerializer.instance);
//            }
//        };
//        return cunstomizer;
//    }

}
