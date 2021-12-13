package cn.vesns.netdisk.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.vesns.netdisk.common.vo.file.FileListVo;
import cn.vesns.netdisk.mapper.FileMapper;
import cn.vesns.netdisk.mapper.RecoveryFileMapper;
import cn.vesns.netdisk.mapper.UserFileMapper;
import cn.vesns.netdisk.pojo.RecoveryFile;
import cn.vesns.netdisk.pojo.User;
import cn.vesns.netdisk.pojo.UserFile;
import cn.vesns.netdisk.service.UserFileService;
import cn.vesns.netdisk.util.DateUtil;
import cn.vesns.netdisk.util.SnowUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.qiwenshare.common.constant.FileConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;

/**
 * @author: vesns vip865047755@126.com
 * @Title: UserFileServiceImpl
 * @ProjectName: netdisk
 * @Description:
 * @date: 2021-11-03 21:34
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class UserFileServiceImpl extends ServiceImpl<UserFileMapper, UserFile> implements UserFileService {

    @Resource
    UserFileMapper userFileMapper;

    @Resource
    FileMapper fileMapper;

    @Resource
    RecoveryFileMapper recoveryFileMapper;

    private static ListeningExecutorService executor = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(20));

    SnowUtil snowUtil = new SnowUtil(0,0);

    @Override
    public List<UserFile> selectUserFileByNameAndPath(String fileName, String filePath, Long userId) {
        LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserFile::getFileName, fileName)
                .eq(UserFile::getFilePath, filePath)
                .eq(UserFile::getUserId, userId)
                .eq(UserFile::getDeleteFlag, 0);
        return userFileMapper.selectList(lambdaQueryWrapper);
    }

    @Override
    public boolean isDirExist(String fileName, String filePath, long userId) {
        LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserFile::getFileName, fileName)
                .eq(UserFile::getFileName, fileName)
                .eq(UserFile::getUserId, userId)
                .eq(UserFile::getDeleteFlag, 0)
                .eq(UserFile::getIsDir, 1);
        List<UserFile> userFiles = userFileMapper.selectList(lambdaQueryWrapper);
        if (userFiles != null && !userFiles.isEmpty()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isFileExist(String fileName, String filePath, long userId) {
        LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserFile::getFileName, fileName)
                .eq(UserFile::getFileName, fileName)
                .eq(UserFile::getUserId, userId)
                .eq(UserFile::getDeleteFlag, 0)
                .eq(UserFile::getIsDir, 0);
        List<UserFile> userFiles = userFileMapper.selectList(lambdaQueryWrapper);
        if (userFiles != null && !userFiles.isEmpty()) {
            return true;
        }
        return false;
    }

    @Override
    public List<UserFile> selectSameUserFile(String fileName, String filePath, String extendName, Long userId) {
        LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserFile::getFileName, fileName)
                .eq(UserFile::getFilePath, filePath)
                .eq(UserFile::getUserId, userId)
                .eq(UserFile::getExtendName, extendName)
                .eq(UserFile::getDeleteFlag, 0);
        return userFileMapper.selectList(lambdaQueryWrapper);
    }

    @Override
    public void replaceUserFilePath(String filePath, String oldFilePath, Long userId) {
        userFileMapper.replaceFilePath(filePath, oldFilePath, userId);

    }

    @Override
    public List<FileListVo> userFileList(UserFile userFile, Long beginCount, Long pageCount) {

        return userFileMapper.getUserFileList(userFile, beginCount, pageCount);
    }

    @Override
    public void updateFilepathByFilepath(String oldfilePath, String newfilePath, String fileName, String extendName, long userId) {
        if ("null".equals(extendName)) {
            extendName = null;
        }
        // 移动根目录
        userFileMapper.updateFilepathByPathAndName(oldfilePath, newfilePath, fileName, extendName, userId);
        oldfilePath = oldfilePath + fileName + "/";
        newfilePath = newfilePath + fileName + "/";
        oldfilePath = oldfilePath.replace("\\", "\\\\\\\\");
        oldfilePath = oldfilePath.replace("'", "\\'");
        oldfilePath = oldfilePath.replace("%", "\\%");
        oldfilePath = oldfilePath.replace("_", "\\_");

        if (extendName == null) {
            // 移动子目录
            userFileMapper.updateFilepathByFilepath(oldfilePath, newfilePath, userId);
        }

    }

    @Override
    public void userFileCopy(String oldfilePath, String newfilePath, String fileName, String extendName, long userId) {

        if ("null".equals(extendName)) {
            extendName = null;
        }

        userFileMapper.batchInsertByPathAndName(oldfilePath, newfilePath, fileName, extendName, userId);
        //移动根目录
//        userFileMapper.updateFilepathByPathAndName(oldfilePath, newfilePath, fileName, extendName, userId);

        //移动子目录
        oldfilePath = oldfilePath + fileName + "/";
        newfilePath = newfilePath + fileName + "/";

        oldfilePath = oldfilePath.replace("\\", "\\\\\\\\");
        oldfilePath = oldfilePath.replace("'", "\\'");
        oldfilePath = oldfilePath.replace("%", "\\%");
        oldfilePath = oldfilePath.replace("_", "\\_");

        if (extendName == null) { //为null说明是目录，则需要移动子目录
            userFileMapper.batchInsertByFilepath(oldfilePath, newfilePath, userId);
        }
    }

    @Override
    public List<FileListVo> selectFileByExtendName(List<String> fileNameList, Long beginCount, Long pageCount, long userId) {
        return userFileMapper.selectFileByExtendName(fileNameList,beginCount,pageCount,userId);
    }

    @Override
    public Long selectCountByExtendName(List<String> fileNameList, Long beginCount, Long pageCount, long userId) {
        return userFileMapper.selectCountByExtendName(fileNameList, beginCount, pageCount, userId);
    }

    @Override
    public List<FileListVo> selectFileNotInExtendNames(List<String> fileNameList, Long beginCount, Long pageCount, long userId) {
        return userFileMapper.selectFileNotInExtendNames(fileNameList, beginCount, pageCount, userId);
    }

    @Override
    public Long selectCountNotInExtendNames(List<String> fileNameList, Long beginCount, Long pageCount, long userId) {
        return userFileMapper.selectCountNotInExtendNames(fileNameList, beginCount, pageCount, userId);
    }

    @Override
    public List<UserFile> selectFileListLikeRightFilePath(String filePath, long userId) {
        filePath = filePath.replace("\\", "\\\\\\\\");
        filePath = filePath.replace("'", "\\'");
        filePath = filePath.replace("%", "\\%");
        filePath = filePath.replace("_", "\\_");

        LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        log.info("查询文件路径：" + filePath);

        lambdaQueryWrapper.eq(UserFile::getUserId, userId)
                .likeRight(UserFile::getFilePath, filePath)
                .eq(UserFile::getDeleteFlag, 0);
        return userFileMapper.selectList(lambdaQueryWrapper);
    }

    @Override
    public List<UserFile> selectFilePathTreeByUserId(Long userId) {
        LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserFile::getUserId, userId)
                .eq(UserFile::getIsDir, 1)
                .eq(UserFile::getDeleteFlag, 0);
        return userFileMapper.selectList(lambdaQueryWrapper);
    }

    @Override
    public void deleteUserFile(Long userFileId, Long sessionUserId) {
        UserFile userFile = userFileMapper.selectById(userFileId);
        System.out.println("userFile--->" + userFile.toString());
        String uuid = UUID.randomUUID().toString();
        if (userFile.getIsDir() == 1) {
            LambdaUpdateWrapper<UserFile> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.set(UserFile::getDeleteFlag, RandomUtil.randomInt(FileConstant.deleteFileRandomSize))
                    .set(UserFile::getDeleteBatchNum, uuid)
                    .set(UserFile::getDeleteTime, DateUtil.getCurrentTime())
                    .eq(UserFile::getUserFileId, userFileId);
            userFileMapper.update(null, lambdaUpdateWrapper);

            String filePath = userFile.getFilePath() + userFile.getFileName() + "/";
            updateFileDeleteStateByFilePath(filePath, uuid, sessionUserId);
        } else {
            UserFile tempUserFile = userFileMapper.selectById(userFileId);
            LambdaUpdateWrapper<UserFile> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.set(UserFile::getDeleteFlag, RandomUtil.randomInt(FileConstant.deleteFileRandomSize))
                    .set(UserFile::getDeleteTime, DateUtil.getCurrentTime())
                    .set(UserFile::getDeleteBatchNum, uuid)
                    .eq(UserFile::getUserFileId, tempUserFile.getUserFileId());
            userFileMapper.update(null,lambdaUpdateWrapper);
        }
        RecoveryFile recoveryFile = new RecoveryFile();
        recoveryFile.setUserFileId(userFileId);
        recoveryFile.setRecoveryFileId(snowUtil.nextId());
        recoveryFile.setDeleteBatchNum(uuid);
        recoveryFile.setDeleteTime(DateUtil.getCurrentTime());
        recoveryFileMapper.insert(recoveryFile);


    }

    public void updateFileDeleteStateByFilePath(String filePath, String deleteBatchNum, Long userId) {
        executor.execute(() -> {
            List<UserFile> userFiles = selectFileListLikeRightFilePath(filePath, userId);
            for (int i = 0; i < userFiles.size(); i++) {
                UserFile userFile = userFiles.get(i);
                LambdaUpdateWrapper<UserFile> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                lambdaUpdateWrapper.set(UserFile::getDeleteFlag, RandomUtil.randomInt(FileConstant.deleteFileRandomSize))
                        .set(UserFile::getDeleteTime, DateUtil.getCurrentTime())
                        .set(UserFile::getDeleteBatchNum, deleteBatchNum)
                        .eq(UserFile::getUserFileId, userFile.getUserFileId())
                        .eq(UserFile::getDeleteFlag, 0);
                userFileMapper.update(null, lambdaUpdateWrapper);
            }
        });
    }
}
