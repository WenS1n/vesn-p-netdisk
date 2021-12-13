package cn.vesns.netdisk.service.impl;/**
 * @version :JDK1.8
 * @date : 2021-11-04 20:31
 * @author : vip865047755@126.com
 * @File : RecoveryServiceImpl.java
 * @software: IntelliJ IDEA
 */

import cn.vesns.netdisk.common.vo.file.RecoveryFileListVo;
import cn.vesns.netdisk.helper.FileHelper;
import cn.vesns.netdisk.mapper.FileMapper;
import cn.vesns.netdisk.mapper.RecoveryFileMapper;
import cn.vesns.netdisk.mapper.UserFileMapper;
import cn.vesns.netdisk.pojo.FileBean;
import cn.vesns.netdisk.pojo.RecoveryFile;
import cn.vesns.netdisk.pojo.UserFile;
import cn.vesns.netdisk.service.RecoveryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * @author: vesns vip865047755@126.com
 * @Title: RecoveryServiceImpl
 * @ProjectName: netdisk
 * @Description:
 * @date: 2021-11-04 20:31
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class RecoveryServiceImpl extends ServiceImpl<RecoveryFileMapper, RecoveryFile> implements RecoveryService {


    @Resource
    UserFileMapper userFileMapper;

    @Resource
    FileMapper fileMapper;

    @Resource
    RecoveryFileMapper recoveryFileMapper;

    @Resource
    FileHelper fileHelper;

    private static ListeningExecutorService executorService = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(20));

    @Override
    public int deleteRecoveryFile(UserFile userFile) {
        if (userFile == null) {
            return 0;
        }
        if (userFile.getIsDir() == 1) {
            updateFilePointCountByBatchNum(userFile.getDeleteBatchNum());
        } else {
            UserFile tempUserFile = userFileMapper.selectById(userFile.getUserFileId());
            System.out.println("tempUserFile-->" + tempUserFile.toString());
            FileBean fileBean = fileMapper.selectById(tempUserFile.getFileId());
            System.out.println("fileBean -->"+fileBean.toString());
            LambdaUpdateWrapper<FileBean> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.set(FileBean::getPointCount, fileBean.getPointCount())
                    .eq(FileBean::getFileId, fileBean.getFileId());
            fileMapper.update(null, lambdaUpdateWrapper);
        }
        LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserFile::getDeleteBatchNum, userFile.getDeleteBatchNum());
        return userFileMapper.delete(lambdaQueryWrapper);
    }

    @Override
    public int restoreFile(String deleteBatchNum, String filePath, Long sessionUserId) {
        LambdaUpdateWrapper<UserFile> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.set(UserFile::getDeleteFlag, 0)
                .set(UserFile::getDeleteBatchNum, "")
                .eq(UserFile::getDeleteBatchNum, deleteBatchNum);
        userFileMapper.update(null, lambdaUpdateWrapper);

        fileHelper.restoreParentFilePath(filePath, sessionUserId);

        fileHelper.deleteRepeatSubDirFile(filePath, sessionUserId);

        LambdaQueryWrapper<RecoveryFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(RecoveryFile::getDeleteBatchNum, deleteBatchNum);
        return recoveryFileMapper.delete(lambdaQueryWrapper);
    }

    @Override
    public List<RecoveryFileListVo> selectRecoveryFiles(Long userId) {
        return recoveryFileMapper.selectRecoveryFileList(userId);
    }

    /**
     * 分批次更新文件点数
     *
     * @param deleteBatchNum
     */
    public void updateFilePointCountByBatchNum(String deleteBatchNum) {
        LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserFile::getDeleteBatchNum, deleteBatchNum);
        List<UserFile> userFiles = userFileMapper.selectList(lambdaQueryWrapper);

        new Thread(() -> {
            for (int i = 0; i < userFiles.size(); i++) {
                UserFile tempUserFile = userFiles.get(i);
                executorService.execute(() -> {
                    if (tempUserFile.getIsDir() != 1) {
                        FileBean fileBean = fileMapper.selectById(tempUserFile.getFileId());
                        if (fileBean.getPointCount() != null) {
                            LambdaUpdateWrapper<FileBean> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                            lambdaUpdateWrapper.set(FileBean::getPointCount, fileBean.getPointCount() - 1)
                                    .eq(FileBean::getFileId, fileBean.getFileId());
                            fileMapper.update(null, lambdaUpdateWrapper);
                        }
                    }
                });
            }
        }).start();
    }
}
