package cn.vesns.netdisk.controller;/**
 * @version :JDK1.8
 * @date : 2021-11-12 20:47
 * @author : vip865047755@126.com
 * @File : FiletransferController.java
 * @software: IntelliJ IDEA
 */

import cn.vesns.netdisk.aop.MyLog;
import cn.vesns.netdisk.common.dto.file.DownloadFileDTO;
import cn.vesns.netdisk.common.dto.file.PreviewDTO;
import cn.vesns.netdisk.common.dto.file.UploadFileDTO;
import cn.vesns.netdisk.common.lang.ResponseResult;
import cn.vesns.netdisk.common.vo.file.FileListVo;
import cn.vesns.netdisk.common.vo.file.UploadFileVo;
import cn.vesns.netdisk.helper.FileHelper;
import cn.vesns.netdisk.pojo.*;
import cn.vesns.netdisk.service.*;
import cn.vesns.netdisk.util.DateUtil;
import cn.vesns.netdisk.util.SnowUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qiwenshare.common.exception.NotLoginException;
import com.qiwenshare.common.util.MimeUtils;
import com.qiwenshare.ufop.constant.UploadFileStatusEnum;
import com.qiwenshare.ufop.util.UFOPUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: vesns vip865047755@126.com
 * @Title: FiletransferController
 * @ProjectName: netdisk
 * @Description:
 * @date: 2021-11-12 20:47
 */
@RestController
@RequestMapping("/filetransfer")
@Slf4j
@Tag(name = "filetransfer", description = "该接口为文件传输接口，主要用来做文件的上传和下载")
public class FileTransferController {

    @Autowired
    FileTransferService fileTransferService;

    @Autowired
    FileService fileService;

    @Autowired
    UserService userService;

    @Autowired
    UserFileService userFileService;

    @Autowired
    FileHelper fileHelper;

    @Autowired
    StorageService storageService;

    @Autowired
    UploadTaskService uploadTaskService;

    @Autowired
    UploadTaskDetailService uploadTaskDetailService;

    public static final String CURRENT_MODULE = "文件传输接口";

    @GetMapping("/uploadfile")
    @Operation(summary = "急速上传", description = "校验文件MD5判断文件是否存在，如果存在直接上传成功并返回skipUpload=true，如果不存在返回skipUpload=false需要再次调用该接口的POST方法", tags = {"filetransfer"})
    @MyLog(operation = "极速上传", module = CURRENT_MODULE)
    public ResponseResult<UploadFileVo> uploadFileSpeed(UploadFileDTO uploadFileDTO, @RequestHeader("token") String token) {

        User userSession = userService.getUserByToken(token);
        boolean b = storageService.checkStorage(userSession.getUserId(), uploadFileDTO.getTotalSize());
        if (!b) {
            return ResponseResult.fail().message("存储空间不足");
        }
        boolean fileExist = userFileService.isFileExist(uploadFileDTO.getFilename(), uploadFileDTO.getFilePath(), userSession.getUserId());
        if (fileExist) {
            return ResponseResult.fail().message("文件已存在，不能重复上传");
        }
        UploadFileVo uploadFileVo = new UploadFileVo();
        Map<String, Object> map = new HashMap<>();
        map.put("identifier", uploadFileDTO.getIdentifier());
        List<FileBean> list = fileService.listByMap(map);
        if (list != null && !list.isEmpty()) {
            FileBean fileBean = list.get(0);
            UserFile userFile = new UserFile();
            userFile.setUserId(userSession.getUserId());
            userFile.setFileId(new SnowUtil(0,0).nextId());
            String relativePath = uploadFileDTO.getRelativePath();
            if (relativePath.contains("/")) {
                userFile.setFilePath(uploadFileDTO.getFilePath() + UFOPUtils.getParentPath(relativePath) + "/");
                fileHelper.restoreParentFilePath(uploadFileDTO.getFilePath() + UFOPUtils.getParentPath(relativePath) + "/", userSession.getUserId());
                fileHelper.deleteRepeatSubDirFile(uploadFileDTO.getFilePath(), userSession.getUserId());
            } else {
                userFile.setFilePath(uploadFileDTO.getFilePath());
            }
            String filename = uploadFileDTO.getFilename();
            userFile.setFileName(UFOPUtils.getFileNameNotExtend(filename));
            userFile.setExtendName(UFOPUtils.getFileExtendName(filename));
            userFile.setDeleteFlag(0);
            List<FileListVo> fileListVoList = userFileService.userFileList(userFile, null, null);
            if (fileListVoList.size() <= 0) {
                userFile.setIsDir(0);
                userFile.setUploadTime(DateUtil.getCurrentTime());
                userFile.setFileId(fileBean.getFileId());
                userFileService.save(userFile);
                fileService.increaseFilePointCount(fileBean.getFileId());
                fileHelper.uploadElasticSearchByUserFileId(userFile.getUserFileId());
            }
            uploadFileVo.setSkipUpload(true);
        } else {
            uploadFileVo.setSkipUpload(false);
            List<Integer> uploadCheckNumList = uploadTaskDetailService.getUploadCheckNumList(uploadFileDTO.getIdentifier());
            if (uploadCheckNumList != null && !uploadCheckNumList.isEmpty()) {
                uploadFileVo.setUploaded(uploadCheckNumList);
            } else {
                LambdaQueryWrapper<UploadTask> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(UploadTask::getIdentifier, uploadFileDTO.getIdentifier());
                List<UploadTask> rslist = uploadTaskService.list(lambdaQueryWrapper);
                if (rslist == null || rslist.isEmpty()) {

                    UploadTask uploadTask = new UploadTask();
                    SnowUtil s = new SnowUtil(0, 0);
                    uploadTask.setUploadTaskId(s.nextId());
                    uploadTask.setIdentifier(uploadFileDTO.getIdentifier());
                    uploadTask.setUploadTime(DateUtil.getCurrentTime());
                    uploadTask.setUploadStatus(UploadFileStatusEnum.UNCOMPLATE.getCode());
                    uploadTask.setFileName(uploadFileDTO.getFilename());
                    String relativePath = uploadFileDTO.getRelativePath();
                    if (relativePath.contains("/")) {
                        uploadTask.setFilePath(uploadFileDTO.getFilePath() + UFOPUtils.getParentPath(relativePath) + "/");
                    } else {
                        uploadTask.setFilePath(uploadFileDTO.getFilePath());
                    }
                    uploadTask.setExtendName(uploadTask.getExtendName());
                    uploadTask.setUserId(userSession.getUserId());
                    uploadTaskService.save(uploadTask);
                }
            }
        }
        System.out.println("uploadFileVo" + uploadFileVo);
        return ResponseResult.success().data(uploadFileVo);
    }

    @PostMapping("/uploadfile")
    @Operation(summary = "上传文件", description = "文件上传接口", tags = "{filetransfer}")
//    @MyLog(operation = "文件上传", module = CURRENT_MODULE)
    public ResponseResult uploadFile(HttpServletRequest request, UploadFileDTO uploadFileDTO, @RequestHeader("token") String token) {
        User userSession = userService.getUserByToken(token);
        if (userSession == null) {
            throw new NotLoginException();
        }
        fileTransferService.uploadFile(request, uploadFileDTO, userSession.getUserId());
        UploadFileVo uploadFileVo = new UploadFileVo();
        System.out.println("uploadfile---->" + uploadFileVo);
        return ResponseResult.success().message("相同文件不能重复上传噢~").data(uploadFileVo);
    }

    @GetMapping("/downloadfile")
    @Operation(summary = "下载文件", description = "下载文件接口", tags = "{filetransfer}")
    @MyLog(operation = "文件下载", module = CURRENT_MODULE)
    public void downloadFile(HttpServletResponse response, DownloadFileDTO downloadFileDTO) {
        System.out.println("DownloadFileDTO.toString----->" + downloadFileDTO.toString());
        boolean authResult = fileHelper.checkAuthDownloadAndPreview(downloadFileDTO.getShareBatchNum(),
                downloadFileDTO.getExtractionCode(),
                downloadFileDTO.getToken(),
                downloadFileDTO.getUserFileId());
        if (!authResult) {
            log.error("没有下载权限");
            return;
        }
        // 强制下载不打开
        response.setContentType("application/force-download");
        UserFile userFile = userFileService.getById(downloadFileDTO.getUserFileId());
        String fileName = null;
        if (userFile.getIsDir() == 1) {
            fileName = userFile.getFileName() + ".zip";
        } else {
            fileName = userFile.getFileName() + "." + userFile.getExtendName();
        }
        fileName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        // 设置文件名称
        response.addHeader("Content-Disposition", "attachment;fileName=" + fileName);

        fileTransferService.downloadFile(response, downloadFileDTO);
    }

    @Operation(summary = "预览文件", description = "用于文件预览", tags = {"filetransfer"})
    @GetMapping("/preview")
    public void preview(HttpServletResponse response, HttpServletRequest request, PreviewDTO previewDTO) {
        System.out.println("previewDTO.getUserFileId-->" + previewDTO.getUserFileId());
        UserFile userFile = userFileService.getById(previewDTO.getUserFileId());
        boolean authResult = fileHelper.checkAuthDownloadAndPreview(previewDTO.getShareBatchNum(),
                previewDTO.getExtractionCode(),
                previewDTO.getToken(),
                previewDTO.getUserFileId());
        if (!authResult) {
            log.error("没有预览权限");
            return;
        }
        System.out.println("userFile.getFileId()=============>"+userFile.getFileId());
        FileBean fileBean = fileService.getById(userFile.getFileId());
        String mime = MimeUtils.getMime(userFile.getExtendName());
        response.setHeader("Content-Type",mime);
        String range = request.getHeader("Range");
        if (StringUtils.isNotEmpty(range)) {
            long aLong = Long.parseLong(range.substring(range.indexOf("=") + 1, range.indexOf("-")));
            response.setContentLength(Math.toIntExact(fileBean.getFileSize()));
            response.setHeader("Content-Type",String.valueOf(aLong + (Math.toIntExact(fileBean.getFileSize()) - 1)));

        }
        response.setHeader("Accept-Ranges","bytes");
        String fileName = userFile.getFileName() + "." + userFile.getExtendName();
        fileName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        // 设置文件名
        response.addHeader("Content-Disposition", "fileName=" + fileName);
        try {
            fileTransferService.previewFile(response,previewDTO);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("此异常不做处理" + e);
        }
    }

    @GetMapping("/getstorage")
    @Operation(summary = "获取存储信息",description = "该接口查看存储信息",tags = "{filetransfer}")
    public ResponseResult getStorage(@RequestHeader("token") String token) {
        User user = userService.getUserByToken(token);
        if(user == null) {
            throw new NotLoginException();
        }

//        Storage storage = new Storage();
//        storage.setUserId(user.getUserId());

        Long size = fileTransferService.selectStorageSizeByUserId(user.getUserId());
        Storage storage2 = new Storage();
        storage2.setUserId(user.getUserId());
        storage2.setStorageSize(size);
        storage2.setTotalStorageSize(storageService.getTotalStorageSize(user.getUserId()));
        return ResponseResult.success().data(storage2);
    }

}
