package cn.vesns.netdisk.aop;

import java.lang.annotation.*;

/**
 * @author : vip865047755@126.com
 * @version :JDK1.8
 * @date : 2021-11-04 23:55
 * @File : MyLog.java
 * @software: IntelliJ IDEA
 */
@Target(ElementType.METHOD) //注解放置的目标位置,METHOD是可注解在方法级别上
@Retention(RetentionPolicy.RUNTIME) //注解在哪个阶段执行
@Documented
public @interface MyLog {

    String module() default "";

    String operation() default "";

    String type() default "operation";

    //0-低，1-中，2-高
    String level() default "0";

}
