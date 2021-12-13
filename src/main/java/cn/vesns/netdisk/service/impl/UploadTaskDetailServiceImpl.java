package cn.vesns.netdisk.service.impl;/**
 * @version :JDK1.8
 * @date : 2021-11-04 22:32
 * @author : vip865047755@126.com
 * @File : UploadTaskDetailServiceImpl.java
 * @software: IntelliJ IDEA
 */

import cn.vesns.netdisk.mapper.UploadTaskDetailMapper;
import cn.vesns.netdisk.mapper.UploadTaskMapper;
import cn.vesns.netdisk.pojo.UploadTaskDetail;
import cn.vesns.netdisk.service.UploadTaskDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: vesns vip865047755@126.com
 * @Title: UploadTaskDetailServiceImpl
 * @ProjectName: netdisk
 * @Description:
 * @date: 2021-11-04 22:32
 */
@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class UploadTaskDetailServiceImpl extends ServiceImpl<UploadTaskDetailMapper, UploadTaskDetail> implements UploadTaskDetailService {

    @Resource
    UploadTaskDetailMapper uploadTaskDetailMapper;

    @Override
    public List<Integer> getUploadCheckNumList(String identifier) {
        return uploadTaskDetailMapper.selectUploadedChunkNumList(identifier);
    }
}
