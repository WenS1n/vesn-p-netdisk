package cn.vesns.netdisk.service.impl;/**
 * @version :JDK1.8
 * @date : 2021-11-04 20:22
 * @author : vip865047755@126.com
 * @File : OperationLogServiceImpl.java
 * @software: IntelliJ IDEA
 */

import cn.vesns.netdisk.mapper.OperationLogMapper;
import cn.vesns.netdisk.pojo.Operationlog;
import cn.vesns.netdisk.service.OperationLogService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: vesns vip865047755@126.com
 * @Title: OperationLogServiceImpl
 * @ProjectName: netdisk
 * @Description:
 * @date: 2021-11-04 20:22
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, Operationlog> implements OperationLogService {

    @Resource
    OperationLogMapper operationLogMapper;

    @Override
    public IPage<Operationlog> selectOperationLogPage(Integer current, Integer size) {
        IPage<Operationlog> page = new Page<>(current, size);
        IPage<Operationlog> list = operationLogMapper.selectPage(page, null);
        return list;
    }

    @Override
    public List<Operationlog> selectOperationLog() {
        return operationLogMapper.selectList(null);
    }

    @Override
    public void insertOperationLog(Operationlog operationlog) {
        operationLogMapper.insert(operationlog);
    }
}
