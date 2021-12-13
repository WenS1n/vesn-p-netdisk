package cn.vesns.netdisk.service.impl;/**
 * @version :JDK1.8
 * @date : 2021-11-02 22:28
 * @author : vip865047755@126.com
 * @File : FileTransferServiceImpl.java
 * @software: IntelliJ IDEA
 */

import cn.hutool.core.bean.BeanUtil;
import cn.vesns.netdisk.common.dto.file.DownloadFileDTO;
import cn.vesns.netdisk.common.dto.file.PreviewDTO;
import cn.vesns.netdisk.common.dto.file.UploadFileDTO;
import cn.vesns.netdisk.common.vo.file.FileListVo;
import cn.vesns.netdisk.helper.FileHelper;
import cn.vesns.netdisk.mapper.*;
import cn.vesns.netdisk.pojo.*;
import cn.vesns.netdisk.service.FileTransferService;
import cn.vesns.netdisk.util.DateUtil;
import cn.vesns.netdisk.util.SnowUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.qiwenshare.ufop.constant.StorageTypeEnum;
import com.qiwenshare.ufop.constant.UploadFileStatusEnum;
import com.qiwenshare.ufop.exception.DownloadException;
import com.qiwenshare.ufop.exception.UploadException;
import com.qiwenshare.ufop.factory.UFOPFactory;
import com.qiwenshare.ufop.operation.delete.Deleter;
import com.qiwenshare.ufop.operation.delete.domain.DeleteFile;
import com.qiwenshare.ufop.operation.download.Downloader;
import com.qiwenshare.ufop.operation.download.domain.DownloadFile;
import com.qiwenshare.ufop.operation.preview.Previewer;
import com.qiwenshare.ufop.operation.preview.domain.PreviewFile;
import com.qiwenshare.ufop.operation.upload.Uploader;
import com.qiwenshare.ufop.operation.upload.domain.UploadFile;
import com.qiwenshare.ufop.operation.upload.domain.UploadFileResult;
import com.qiwenshare.ufop.util.UFOPUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author: vesns vip865047755@126.com
 * @Title: FileTransferServiceImpl
 * @ProjectName: netdisk
 * @Description:
 * @date: 2021-11-02 22:28
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class FileTransferServiceImpl implements FileTransferService {


    @Resource
    StorageMapper storageMapper;

    @Resource
    FileMapper fileMapper;

    @Resource
    UserFileMapper userFileMapper;

    @Resource
    UFOPFactory ufopFactory;

    @Resource
    FileHelper fileHelper;

    @Resource
    UploadTaskDetailMapper uploadTaskDetailMapper;

    @Resource
    UploadTaskMapper uploadTaskMapper;

    SnowUtil snowUtil = new SnowUtil(0, 0);


    @Override
    public void uploadFile(HttpServletRequest request, UploadFileDTO uploadFileDto, Long userId) {
        // 切片上传
        UploadFile uploadFile = new UploadFile();

        uploadFile.setChunkNumber(uploadFileDto.getChunkNumber());
        uploadFile.setChunkSize(uploadFileDto.getChunkSize());
        uploadFile.setTotalChunks(uploadFileDto.getTotalChunks());
        uploadFile.setIdentifier(uploadFileDto.getIdentifier());
        uploadFile.setTotalSize(uploadFileDto.getTotalSize());
        uploadFile.setCurrentChunkSize(uploadFileDto.getCurrentChunkSize());

        // 使用上传工厂上传
        Uploader uploader = ufopFactory.getUploader();
        if (uploader == null) {
            throw new UploadException("上传失败");
        }
        List<UploadFileResult> upload = uploader.upload(request, uploadFile);
        for (int i = 0; i < upload.size(); i++) {
            UploadFileResult uploadFileResult = upload.get(i);
            FileBean fileBean = new FileBean();
            BeanUtil.copyProperties(uploadFileDto, fileBean);
            // 得到相对路径
            String relativePath = uploadFileDto.getRelativePath();
            // 如果上传成功，则插入文件
            if (UploadFileStatusEnum.SUCCESS.equals(uploadFileResult.getStatus())) {

                fileBean.setFileId(snowUtil.nextId());
                fileBean.setFileUrl(uploadFileResult.getFileUrl());
                fileBean.setFileSize(uploadFileResult.getFileSize());
                fileBean.setStorageType(uploadFileResult.getStorageType().getCode());
                fileBean.setPointCount(1);
                fileMapper.insert(fileBean);
                UserFile userFile = new UserFile();
                // 如果相对路径含有下级目录
                if (relativePath.contains("/")) {
                    // 重新设置路径
                    userFile.setFilePath(uploadFileDto.getFilePath() + UFOPUtils.getParentPath(relativePath) + "/");
                    // 重置父路径
                    fileHelper.restoreParentFilePath(uploadFileDto.getFilePath() + UFOPUtils.getParentPath(relativePath) + "/", userId);
                    // 删除重复目录
                    fileHelper.deleteRepeatSubDirFile(uploadFileDto.getFilePath(), userId);
                } else {
                    userFile.setFilePath(uploadFileDto.getFilePath());
                }

                userFile.setUserId(userId);
                SnowUtil snowUtil = new SnowUtil(0, 0);
                userFile.setUserFileId(snowUtil.nextId());
                userFile.setFileName(uploadFileResult.getFileName());
                userFile.setExtendName(uploadFileResult.getExtendName());
                userFile.setDeleteFlag(0);
                userFile.setIsDir(0);
                List<FileListVo> userFileList = userFileMapper.getUserFileList(userFile, null, null);
                if (userFileList.size() > 0) {
                    // 获取重复文件名
                    String repeatFileName = fileHelper.getRepeatFileName(userFile, uploadFileDto.getFilePath());
                    userFile.setFileName(repeatFileName);
                }
                userFile.setFileId(fileBean.getFileId());
                userFile.setUploadTime(DateUtil.getCurrentTime());

                // 紧接插入用户文件列表,并更新es
                userFileMapper.insert(userFile);
                fileHelper.uploadElasticSearchByUserFileId(userFile.getUserFileId());

                LambdaQueryWrapper<UploadTaskDetail> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(UploadTaskDetail::getIdentifier, uploadFileDto.getIdentifier());
                uploadTaskDetailMapper.delete(lambdaQueryWrapper);

                // 更新上传状态，至此完成上传
                LambdaUpdateWrapper<UploadTask> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                lambdaUpdateWrapper.set(UploadTask::getUploadStatus, UploadFileStatusEnum.SUCCESS.getCode())
                        .eq(UploadTask::getIdentifier, uploadFileDto.getIdentifier());
                uploadTaskMapper.update(null, lambdaUpdateWrapper);
            } else if (UploadFileStatusEnum.UNCOMPLATE.equals(uploadFileResult.getStatus())) {
                UploadTaskDetail uploadTaskDetail = new UploadTaskDetail();
//                SnowUtil snowUtil = new SnowUtil(0,1);
                uploadTaskDetail.setUploadTaskDetailId(snowUtil.nextId());
                uploadTaskDetail.setFilePath(uploadFileDto.getFilePath());
                if (relativePath.contains("/")) {
                    uploadTaskDetail.setFilePath(uploadFileDto.getFilePath() + UFOPUtils.getParentPath(relativePath) + "/");
                } else {
                    uploadTaskDetail.setFilePath(uploadFileDto.getFilePath());
                }
                uploadTaskDetail.setFilename(uploadFileDto.getFilename());
                uploadTaskDetail.setChunkNumber(uploadFileDto.getChunkNumber());
                uploadTaskDetail.setChunkSize((int)uploadFileDto.getChunkSize());
                uploadTaskDetail.setIdentifier(uploadFileDto.getIdentifier());
                uploadTaskDetail.setRelativePath(uploadFileDto.getRelativePath());
                uploadTaskDetail.setTotalChunks(uploadFileDto.getTotalChunks());
                uploadTaskDetail.setTotalSize((int)uploadFileDto.getTotalSize());
                uploadTaskDetailMapper.insert(uploadTaskDetail);

            } else if (UploadFileStatusEnum.FAIL.equals(uploadFileResult.getStatus())) {
                // 如果上传失败就删除任务
                LambdaQueryWrapper<UploadTaskDetail> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(UploadTaskDetail::getIdentifier, uploadFileDto.getIdentifier());
                uploadTaskDetailMapper.delete(lambdaQueryWrapper);

                // 更新上传文件列表
                LambdaUpdateWrapper<UploadTask> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                lambdaUpdateWrapper.set(UploadTask::getUploadStatus, UploadFileStatusEnum.FAIL.getCode())
                        .eq(UploadTask::getUploadTime, uploadFileDto.getIdentifier());
                uploadTaskMapper.update(null, lambdaUpdateWrapper);
            }
        }

    }

    @Override
    public void downloadFile(HttpServletResponse httpServletResponse, DownloadFileDTO downloadFileDTO) {

        UserFile userFile = userFileMapper.selectById(downloadFileDTO.getUserFileId());
        // 如果不是文件夹
        if (userFile.getIsDir() == 0) {
            FileBean fileBean = fileMapper.selectById(userFile.getFileId());
            Downloader downloader = ufopFactory.getDownloader(fileBean.getStorageType());
            if (downloader == null) {
                log.error("下载失败，文件存储类型不支持下载，storageType:{}", fileBean.getStorageType());
                throw new DownloadException("下载失败");
            }
            DownloadFile downloadFile = new DownloadFile();
            downloadFile.setFileUrl(fileBean.getFileUrl());
            downloadFile.setFileSize(fileBean.getFileSize());
            httpServletResponse.setContentLengthLong(fileBean.getFileSize());
            downloader.download(httpServletResponse, downloadFile);
        } else {
            // 将文件夹或者问价打包成为zip形式压缩包
            LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.likeRight(UserFile::getFilePath, userFile.getFilePath() + userFile.getFileName() + "/")
                    .eq(UserFile::getUserId, userFile.getUserId())
                    .eq(UserFile::getIsDir, 0)
                    .eq(UserFile::getDeleteFlag, 0);
            List<UserFile> userFiles = userFileMapper.selectList(lambdaQueryWrapper);
            String staticPath = UFOPUtils.getStaticPath();
            String tempPath = staticPath + "temp" + File.separator;
            File tempDirFile = new File(tempPath);
            // 如果不存在则创建新的
            if (!tempDirFile.exists()) {
                tempDirFile.mkdirs();
            }
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(tempPath + userFile.getFileName() + ".zip");

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            // 压缩zip操作
            CheckedOutputStream checkedOutputStream = new CheckedOutputStream(fos, new Adler32());
            ZipOutputStream zos = new ZipOutputStream(checkedOutputStream);
            BufferedOutputStream out = new BufferedOutputStream(zos);

            try {
                for (UserFile userFile1 : userFiles) {
                    FileBean fileBean = fileMapper.selectById(userFile1.getFileId());
                    Downloader downloader = ufopFactory.getDownloader(fileBean.getStorageType());
                    if (downloader == null) {
                        log.error("下载失败，文件存储类型不支持下载，storageType:{}", fileBean.getStorageType());
                        throw new UploadException("下载失败");
                    }
                    DownloadFile downloadFile = new DownloadFile();
                    downloadFile.setFileUrl(fileBean.getFileUrl());
                    downloadFile.setFileSize(fileBean.getFileSize());
                    InputStream inputStream = downloader.getInputStream(downloadFile);
                    BufferedInputStream bis = new BufferedInputStream(inputStream);
                    try {
                        zos.putNextEntry(new ZipEntry(userFile1.getFilePath().replace(userFile.getFilePath(), "/") + userFile1.getFileName() + "." + userFile1.getExtendName()));

                        byte[] buffer = new byte[1024];
                        int i = bis.read(buffer);
                        while (i != -1) {
                            out.write(buffer, 0, i);
                            i = bis.read(buffer);
                        }
                    } catch (IOException e) {
                        log.error("" + e);
                        e.printStackTrace();
                    } finally {
                        try {
                            bis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            out.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error("压缩过程中出现异常:"+ e);
            } finally {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            Downloader downloader = ufopFactory.getDownloader(StorageTypeEnum.LOCAL.getCode());
            DownloadFile downloadFile = new DownloadFile();
            downloadFile.setFileUrl("temp" + File.separator + userFile.getFileName() + ".zip");
            File tempFile = new File(UFOPUtils.getStaticPath() + downloadFile.getFileUrl());
            httpServletResponse.setContentLengthLong(tempFile.length());
            downloader.download(httpServletResponse, downloadFile);
            String zipPath = UFOPUtils.getStaticPath() + "temp" + File.separator + userFile.getFileName() + ".zip";
            File file = new File(zipPath);
            if (file.exists()) {
                file.delete();
            }
        }


    }

    /**
     * 预览文件
     *
     * @param httpServletResponse
     * @param previewDTO
     */
    @Override
    public void previewFile(HttpServletResponse httpServletResponse, PreviewDTO previewDTO) {

        UserFile userFile = userFileMapper.selectById(previewDTO.getUserFileId());
        FileBean fileBean = fileMapper.selectById(userFile.getFileId());
        Previewer previewer = ufopFactory.getPreviewer(fileBean.getStorageType());
        if (previewer == null) {
            throw new UploadException("预览失败");
        }
        PreviewFile previewFile = new PreviewFile();
        previewFile.setFileSize(fileBean.getFileSize());
        previewFile.setFileUrl(fileBean.getFileUrl());
        if ("true".equals(previewDTO.getIsMin())) {
            previewer.imageThumbnailPreview(httpServletResponse, previewFile);
        } else {
            previewer.imageOriginalPreview(httpServletResponse, previewFile);
        }
    }

    @Override
    public void deleteFile(FileBean fileBean) {

        Deleter delete = ufopFactory.getDeleter(fileBean.getStorageType());
        DeleteFile deleteFile = new DeleteFile();
        deleteFile.setFileUrl(fileBean.getFileUrl());
        delete.delete(deleteFile);


    }

    @Override
    public Storage selectStorageBean(Storage storageBean) {
        LambdaQueryWrapper<Storage> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Storage::getUserId, storageBean.getUserId());
        return storageMapper.selectOne(lambdaQueryWrapper);
    }

    @Override
    public void insertStorageBean(Storage storage) {

        storage.setStorageId(snowUtil.nextId());
        storageMapper.insert(storage);
    }

    @Override
    public void updateStorageBean(Storage storage) {
        LambdaUpdateWrapper<Storage> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.set(Storage::getStorageSize, storage.getStorageSize())
                .eq(Storage::getStorageId, storage.getStorageId())
                .eq(Storage::getUserId, storage.getUserId());
        storageMapper.update(null, lambdaUpdateWrapper);
    }

    @Override
    public Storage selectStorageByUser(Storage storage) {
        LambdaQueryWrapper<Storage> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Storage::getUserId, storage.getUserId());
        return storageMapper.selectOne(lambdaQueryWrapper);
    }

    @Override
    public Long selectStorageSizeByUserId(Long userId) {
        return userFileMapper.selectStorageSizeByUserId(userId);
    }
}
