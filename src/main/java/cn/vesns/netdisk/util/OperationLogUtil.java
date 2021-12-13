package cn.vesns.netdisk.util;/**
 * @version :JDK1.8
 * @date : 2021-11-05 0:10
 * @author : vip865047755@126.com
 * @File : OperationLogUtil.java
 * @software: IntelliJ IDEA
 */

import cn.vesns.netdisk.pojo.Operationlog;
import cn.vesns.netdisk.pojo.User;
import com.qiwenshare.common.util.CollectUtil;
import com.qiwenshare.common.util.DateUtil;

import javax.servlet.http.HttpServletRequest;

/**
 * @author: vesns vip865047755@126.com
 * @Title: OperationLogUtil
 * @ProjectName: netdisk
 * @Description:
 * @date: 2021-11-05 0:10
 */
public class OperationLogUtil {



    /**
     * 构造操作日志参数
     *
     * @param request   请求
     * @param isSuccess 操作是否成功（成功/失败）
     * @param source    操作源模块
     * @param operation 执行操作
     * @param detail    详细信息
     * @return 操作日志参数
     *
     */
    public static Operationlog getOperationLogObj(HttpServletRequest request, User sessionUserBean, String isSuccess, String source, String operation, String detail) {

//        UserBean sessionUserBean = (UserBean) SecurityUtils.getSubject().getPrincipal();
        //用户需要登录才能进行的操作，需要记录操作日志
        long userId = 1;
        if (sessionUserBean != null) {
            userId = sessionUserBean.getUserId();
        }
        SnowUtil snowUtil = new SnowUtil(1,3);
        Operationlog operationLog = new Operationlog();
        operationLog.setOperationLogId(snowUtil.nextId());
        operationLog.setUserId(userId);
        operationLog.setTime(DateUtil.getCurrentTime());
        operationLog.setTerminal(new CollectUtil().getClientIpAddress(request));
        operationLog.setSource(source);
        operationLog.setResult(isSuccess);
        operationLog.setOperation(operation);
        operationLog.setDetail(detail);

        return operationLog;
    }
}
