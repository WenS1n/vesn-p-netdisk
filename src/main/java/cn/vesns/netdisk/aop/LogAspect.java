package cn.vesns.netdisk.aop;/**
 * @version :JDK1.8
 * @date : 2021-11-04 23:51
 * @author : vip865047755@126.com
 * @File : LogAcpect.java
 * @software: IntelliJ IDEA
 */

/**
 * @author: vesns vip865047755@126.com
 * @Title: LogAcpect
 * @ProjectName: netdisk
 * @Description:
 * @date: 2021-11-04 23:51
 */

import cn.vesns.netdisk.common.lang.ResponseResult;
import cn.vesns.netdisk.pojo.User;
import cn.vesns.netdisk.service.OperationLogService;
import cn.vesns.netdisk.service.UserService;
import cn.vesns.netdisk.util.OperationLogUtil;
import com.qiwenshare.common.result.RestResult;
import org.aspectj.lang.JoinPoint;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class LogAspect {

    @Autowired
    OperationLogService operationLogService;
    @Autowired
    UserService userService;


    private String operation = "";
    private String module = "";
    private String token = "";
    private HttpServletRequest request;


    /**
     * 定义切入点
     */
    @Pointcut("@annotation(cn.vesns.netdisk.aop.MyLog)")
    public void myLog() { }

    /**
     * 前置通知
     * @param joinPoint
     */
    @Before("myLog()")
    public void doBefore(JoinPoint joinPoint) {
        // 从切面织入点处通过反射机制获取织入点处的方法
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        //         //获取切入点所在的方法
        Method method = signature.getMethod();
        Map<String, Object> nameAndValue = getNameAndValue(joinPoint);

        // 获取操作
        MyLog myLog = method.getAnnotation(MyLog.class);
        if (myLog != null) {
            operation = myLog.operation();
            module = myLog.module();
            token = (String) nameAndValue.get("token");
        }

        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        request = attributes.getRequest();
    }


    @AfterReturning(returning = "ret", pointcut = "myLog()")
    public void doAfterReturning(Object ret) throws Throwable {

        if (ret instanceof ResponseResult) {
            boolean isSuccess = ((ResponseResult) ret).getSuccess();
            String errorMessage = ((ResponseResult) ret).getMessage();
            User sessionUserBean = userService.getUserByToken(token);
            if (isSuccess) {

                operationLogService.insertOperationLog(
                        OperationLogUtil.getOperationLogObj(request,sessionUserBean, "成功", module, operation, "操作成功"));
            } else {
                operationLogService.insertOperationLog(
                        OperationLogUtil.getOperationLogObj(request,sessionUserBean, "失败", module, operation, errorMessage));
            }
        }

    }


    Map<String,Object> getNameAndValue(JoinPoint joinPoint) {
        Map<String, Object> map = new HashMap<>();
        Object[] args = joinPoint.getArgs();
        String[] parameterNames = ((CodeSignature) joinPoint.getSignature()).getParameterNames();
        for (int i = 0; i < parameterNames.length; i++) {
            map.put(parameterNames[i],args[i]);
        }
        return map;
    }



}
