package cn.vesns.netdisk.service;

import cn.vesns.netdisk.common.vo.file.RecoveryFileListVo;
import cn.vesns.netdisk.pojo.RecoveryFile;
import cn.vesns.netdisk.pojo.UserFile;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author : vip865047755@126.com
 * @version :JDK1.8
 * @date : 2021-11-04 20:28
 * @File : RecoveryService.java
 * @software: IntelliJ IDEA
 */
public interface RecoveryService extends IService<RecoveryFile> {


    int deleteRecoveryFile(UserFile userFile);

    int restoreFile(String deleteBatchNum,String filePath,Long sessionUserId);

    List<RecoveryFileListVo> selectRecoveryFiles(Long userId);

}
