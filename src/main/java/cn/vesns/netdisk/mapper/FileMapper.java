package cn.vesns.netdisk.mapper;/**
 * @version :JDK1.8
 * @date : 2021-10-15 17:55
 * @author : vip865047755@126.com
 * @File : FileMapper.java
 * @software: IntelliJ IDEA
 */

import cn.vesns.netdisk.pojo.FileBean;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @author: vesns vip865047755@126.com
 * @Title: FileMapper
 * @ProjectName: netdisk
 * @Description:
 * @date: 2021-10-15 17:55
 */
public interface FileMapper extends BaseMapper<FileBean> {

    void batchInsertFile(List<FileBean> fileBeanList);
}
