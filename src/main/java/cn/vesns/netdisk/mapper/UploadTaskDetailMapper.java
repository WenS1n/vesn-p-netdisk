package cn.vesns.netdisk.mapper;


import cn.vesns.netdisk.pojo.UploadTaskDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @author : vip865047755@126.com
 * @version :JDK1.8
 * @date : 2021-11-02 20:37
 * @File : UploadTaskDetailMapper.java
 * @software: IntelliJ IDEA
 */
public interface UploadTaskDetailMapper extends BaseMapper<UploadTaskDetail> {

    /**
     * 查询上传总数
     * @param identifier
     * @return
     */
    List<Integer> selectUploadedChunkNumList(String identifier);

}
