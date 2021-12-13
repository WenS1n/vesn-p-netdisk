package cn.vesns.netdisk.mapper;

import cn.vesns.netdisk.common.vo.file.RecoveryFileListVo;
import cn.vesns.netdisk.pojo.RecoveryFile;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author : vip865047755@126.com
 * @version :JDK1.8
 * @date : 2021-11-02 20:14
 * @File : RecoveryFileMapper.java
 * @software: IntelliJ IDEA
 */
public interface RecoveryFileMapper extends BaseMapper<RecoveryFile> {

    /**
     * 查询回收站
     * @param userId
     * @return
     */
    List<RecoveryFileListVo> selectRecoveryFileList (@Param("userId") Long userId);

}
