package cn.vesns.netdisk.service;

import cn.vesns.netdisk.pojo.Operationlog;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author : vip865047755@126.com
 * @version :JDK1.8
 * @date : 2021-11-04 20:19
 * @File : OperationLogService.java
 * @software: IntelliJ IDEA
 */
public interface OperationLogService extends IService<Operationlog> {

    IPage<Operationlog> selectOperationLogPage(Integer current, Integer size);

    List<Operationlog> selectOperationLog();

    void insertOperationLog(Operationlog operationlog);

}
