package cn.vesns.netdisk.controller;/**
 * @version :JDK1.8
 * @date : 2021-11-15 22:01
 * @author : vip865047755@126.com
 * @File : RecoveryFileController.java
 * @software: IntelliJ IDEA
 */

import cn.vesns.netdisk.aop.MyLog;
import cn.vesns.netdisk.common.dto.file.BatchDeleteRecoveryFileDto;
import cn.vesns.netdisk.common.dto.file.DeleteRecoveryFileDto;
import cn.vesns.netdisk.common.dto.file.RestoreFileDto;
import cn.vesns.netdisk.common.lang.ResponseResult;
import cn.vesns.netdisk.common.vo.file.RecoveryFileListVo;
import cn.vesns.netdisk.pojo.RecoveryFile;
import cn.vesns.netdisk.pojo.User;
import cn.vesns.netdisk.pojo.UserFile;
import cn.vesns.netdisk.service.RecoveryService;
import cn.vesns.netdisk.service.UserFileService;
import cn.vesns.netdisk.service.UserService;
import com.alibaba.fastjson.JSON;
import com.qiwenshare.common.exception.NotLoginException;
import com.qiwenshare.common.result.RestResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: vesns vip865047755@126.com
 * @Title: RecoveryFileController
 * @ProjectName: netdisk
 * @Description:
 * @date: 2021-11-15 22:01
 */
@Slf4j
@RestController
@RequestMapping("/recoveryfile")
@Tag(name = "recoveryfile", description = "回收站管理接口")
public class RecoveryFileController {

    @Autowired
    RecoveryService recoveryService;

    @Autowired
    UserFileService userFileService;

    @Autowired
    UserService userService;

    public static final String CURRENT_MODULE = "回收站文件接口";

    @PostMapping("/deleterecoveryfile")
    @MyLog(operation = "删除回收站文件", module = CURRENT_MODULE)
    @Operation(summary = "删除回收站文件", description = "删除文件接口", tags = {"recoveryfile"})
    public ResponseResult deleteRecoveryFile(@RequestBody DeleteRecoveryFileDto deleteRecoveryFileDto, @RequestHeader("token") String token) {
        User user = userService.getUserByToken(token);
        if (user == null) {
            throw new NotLoginException();
        }
        RecoveryFile recoveryFile = recoveryService.getById(deleteRecoveryFileDto.getRecoveryFileId());
        System.out.println("recoveryFile.toString-->" + recoveryFile.toString());
        System.out.println("deleteRecoveryFileDto.toString-->" + deleteRecoveryFileDto.toString());
        UserFile userFile = userFileService.getById(recoveryFile.getUserFileId());
        recoveryService.deleteRecoveryFile(userFile);
        recoveryService.removeById(deleteRecoveryFileDto.getRecoveryFileId());
        return ResponseResult.success().data("删除成功");
    }

    @Operation(summary = "批量删除回收文件", description = "批量删除回收文件", tags = {"recoveryfile"})
    @PostMapping("/batchdelete")
    @MyLog(operation = "批量删除回收站文件", module = CURRENT_MODULE)
    public ResponseResult batchDeleteRecoveryFile(@RequestBody BatchDeleteRecoveryFileDto batchDeleteRecoveryFileDto, @RequestHeader("token") String token) {
        User user = userService.getUserByToken(token);
        if (user == null) {
            throw new NotLoginException();
        }
        List<RecoveryFile> recoveryFiles = JSON.parseArray(batchDeleteRecoveryFileDto.getRecoveryFileIds(), RecoveryFile.class);
        for (RecoveryFile recoveryFile : recoveryFiles) {
            RecoveryFile recoveryFile1 = recoveryService.getById(recoveryFile.getRecoveryFileId());
            UserFile userFile = userFileService.getById(recoveryFile1.getUserFileId());
            recoveryService.deleteRecoveryFile(userFile);
            recoveryService.removeById(recoveryFile.getRecoveryFileId());
        }
        return ResponseResult.success().data("批量删除成功");
    }

    @PostMapping("/list")
    @Operation(summary = "回收文件列表", description = "回收文件列表", tags = {"recoveryfile"})
    public ResponseResult<List<RecoveryFileListVo>> getRecoveryFilesList(@RequestHeader("token") String token) {
        User user = userService.getUserByToken(token);
        if (user == null) {
            throw new NotLoginException();
        }
        ResponseResult<List<RecoveryFileListVo>> result = new ResponseResult<>();
        List<RecoveryFileListVo> recoveryFileListVos = recoveryService.selectRecoveryFiles(user.getUserId());
        result.setData(recoveryFileListVos);
        result.setSuccess(true);
        return result;
    }

    @PostMapping("/restorefile")
    @Operation(summary = "还原文件", description = "还原文件", tags = {"recoveryfile"})
    @MyLog(operation = "还原回收站文件", module = CURRENT_MODULE)
    public ResponseResult<List<RecoveryFileListVo>> restoreFile(@RequestBody RestoreFileDto restoreFileDto, @RequestHeader("token") String token) {
        User user = userService.getUserByToken(token);
        if (user == null) {
            throw new NotLoginException();
        }
        int i = recoveryService.restoreFile(restoreFileDto.getDeleteBatchNum(), restoreFileDto.getFilePath(), user.getUserId());
        if (i == 1) {
            return ResponseResult.success().message("还原成功！");
        }
        return ResponseResult.fail().message("还原失败！");
    }
}
