package cn.vesns.netdisk.controller;/**
 * @version :JDK1.8
 * @date : 2021-11-15 22:32
 * @author : vip865047755@126.com
 * @File : ShareController.java
 * @software: IntelliJ IDEA
 */

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import cn.vesns.netdisk.aop.MyLog;
import cn.vesns.netdisk.common.dto.share.*;
import cn.vesns.netdisk.common.lang.ResponseResult;
import cn.vesns.netdisk.common.vo.share.ShareFileListVO;
import cn.vesns.netdisk.common.vo.share.ShareFileVo;
import cn.vesns.netdisk.common.vo.share.ShareListVo;
import cn.vesns.netdisk.common.vo.share.ShareTypeVO;
import cn.vesns.netdisk.helper.FileHelper;
import cn.vesns.netdisk.pojo.Share;
import cn.vesns.netdisk.pojo.ShareFile;
import cn.vesns.netdisk.pojo.User;
import cn.vesns.netdisk.pojo.UserFile;
import cn.vesns.netdisk.service.*;
import cn.vesns.netdisk.util.DateUtil;
import cn.vesns.netdisk.util.SnowUtil;
import com.alibaba.fastjson.JSON;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qiwenshare.common.exception.NotLoginException;

import com.qiwenshare.common.result.RestResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.*;

/**
 * @author: vesns vip865047755@126.com
 * @Title: ShareController
 * @ProjectName: netdisk
 * @Description:
 * @date: 2021-11-15 22:32
 */
@RestController
@RequestMapping("/share")
@Slf4j
@Tag(name = "share", description = "文件分享接口")
public class ShareController {

    @Autowired
    UserService userService;

    @Autowired
    UserFileService userFileService;

    @Autowired
    ShareService shareService;

    @Autowired
    FileService fileService;

    @Autowired
    ShareFileService shareFileService;

    @Autowired
    FileHelper fileHelper;

    public static final String CURRENT_MODULE = "文件分享";

    @PostMapping(value = "/sharefile")
    @Operation(summary = "分享文件", description = "分享文件统一接口", tags = {"share"})
    @MyLog(operation = "分享文件", module = CURRENT_MODULE)
    public ResponseResult<ShareFileVo> shareFile(@RequestBody ShareFileDto shareFileDto, @RequestHeader("token") String token) {
        ShareFileVo shareFileVo = new ShareFileVo();
        User user = userService.getUserByToken(token);
        if (user == null) {
            throw new NotLoginException();
        }
        SnowUtil snowUtil = new SnowUtil(0,0);
        String uuid = UUID.randomUUID().toString().replace("-", "");
        Share share = new Share();
        BeanUtil.copyProperties(shareFileDto, share);
        share.setShareId(snowUtil.nextId());
        share.setShareTime(DateUtil.getCurrentTime());
        share.setUserId(user.getUserId());
        share.setShareStatus(0);
        if (share.getShareType() == 1) {
            String randomNumbers = RandomUtil.randomNumbers(6);
            share.setExtractionCode(randomNumbers);
            shareFileVo.setExtractionCode(share.getExtractionCode());
        }
        share.setShareBatchNum(uuid);
        shareService.save(share);
        List<ShareFile> shareFiles = JSON.parseArray(shareFileDto.getFiles(), ShareFile.class);
        List<ShareFile> saveFileList = new ArrayList<>();
        for (ShareFile shareFile : shareFiles) {
            UserFile userFile = userFileService.getById(shareFile.getUserFileId());
            if (userFile.getUserId().compareTo(user.getUserId()) != 0) {
                return ResponseResult.fail().message("只能分享自己的文件");
            }

            if (userFile.getIsDir() == 1) {
                List<UserFile> userFiles = userFileService.selectFileListLikeRightFilePath(userFile.getFilePath() + userFile.getFileName() + "/", user.getUserId());
                for (UserFile file : userFiles) {
                    shareFile.setShareFileId(snowUtil.nextId());
                    ShareFile shareFile1 = new ShareFile();
                    shareFile1.setUserFileId(file.getUserFileId());
                    shareFile1.setShareBatchNum(uuid);
                    shareFile1.setShareFilePath(file.getFilePath().replaceFirst(userFile.getFilePath(), "/"));
                    saveFileList.add(shareFile1);
                }
            }
            shareFile.setShareFileId(snowUtil.nextId());
            shareFile.setShareFilePath("/");
            shareFile.setShareBatchNum(uuid);
            saveFileList.add(shareFile);
        }
        shareFileService.batchInsertShareFile(saveFileList);
        shareFileVo.setShareBatchNum(uuid);
        return ResponseResult.success().data(shareFileVo);
    }

    @Operation(summary = "保存分享文件", description = "用来将别人分享的文件保存到自己的网盘中", tags = {"share"})
    @PostMapping(value = "/savesharefile")
    @MyLog(operation = "保存分享文件", module = CURRENT_MODULE)
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult saveShareFile(@RequestBody SaveShareFileDto saveShareFileDto, @RequestHeader("token") String token) {
        User user = userService.getUserByToken(token);
        List<ShareFile> shareFiles = JSON.parseArray(saveShareFileDto.getFiles(), ShareFile.class);
        String filePath = saveShareFileDto.getFilePath();
        Long userId = user.getUserId();
        List<UserFile> saveUserFileList = new ArrayList<>();
        for (ShareFile shareFile : shareFiles) {
            UserFile userFile = userFileService.getById(shareFile.getUserFileId());
            String fileName = userFile.getFileName();
            String savefileName = fileHelper.getRepeatFileName(userFile, filePath);
            if (userFile.getIsDir() == 1) {
                List<UserFile> userFiles = userFileService.selectFileListLikeRightFilePath(userFile.getFilePath() + userFile.getFileName(), userFile.getUserId());
                log.info("查询文件列表：" + JSON.toJSONString(userFiles));
                String filePath1 = userFile.getFilePath();
                userFiles.forEach(p -> {
                    p.setUserFileId(null);
                    p.setUserId(userId);
                    p.setFilePath(p.getFilePath().replaceFirst(filePath1 + fileName, filePath + savefileName));
                    saveUserFileList.add(p);
                    log.info("当前文件：" + JSON.toJSONString(p));
                    if (p.getIsDir() == 0) {
                        fileService.increaseFilePointCount(p.getFileId());
                    }
                });
            } else {
                fileService.increaseFilePointCount(userFile.getFileId());
            }
            userFile.setUserFileId(null);
            userFile.setUserId(userId);
            userFile.setFilePath(filePath);
            userFile.setFileName(savefileName);
            saveUserFileList.add(userFile);
        }
        log.info("----------" + JSON.toJSONString(saveUserFileList));
        userFileService.saveBatch(saveUserFileList);
        return ResponseResult.success();
    }

    @Operation(summary = "查看已分享列表", description = "查看已分享列表", tags = {"share"})
    @GetMapping(value = "/shareList")
    public ResponseResult shareList(ShareListDTO shareListDTO, @RequestHeader("token") String token) {
        User user = userService.getUserByToken(token);
        if (user == null) {
            throw new NotLoginException();
        }
        List<ShareListVo> shareListVos = shareService.selectShareList(shareListDTO, user.getUserId());
        int total = shareService.selectShareListTotalCount(shareListDTO, user.getUserId());
        Map<String, Object> map = new HashMap<>();
        map.put("total", total);
        map.put("list", shareListVos);
        return ResponseResult.success().data(map);
    }
    @Operation(summary = "分享文件列表", description = "分享列表", tags = {"share"})
    @GetMapping(value = "/sharefileList")
    @ResponseBody
    public RestResult<List<ShareFileListVO>> shareFileList(ShareFileListDto shareFileListDto) {
        String shareBatchNum = shareFileListDto.getShareBatchNum();
        String shareFilePath = shareFileListDto.getShareFilePath();
        List<ShareFileListVO> list = shareFileService.selectShareFileList(shareBatchNum, shareFilePath);
        for (ShareFileListVO shareFileListVO : list) {
            shareFileListVO.setShareFilePath(shareFilePath);
        }
        return RestResult.success().data(list);
    }

    @Operation(summary = "分享类型", description = "可用此接口判断是否需要提取码", tags = {"share"})
    @GetMapping(value = "/sharetype")
    public ResponseResult<ShareTypeVO> shareType(ShareTypeDto shareTypeDto) {
        LambdaQueryWrapper<Share> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Share::getShareBatchNum, shareTypeDto.getShareBatchNum());
        Share one = shareService.getOne(lambdaQueryWrapper);
        ShareTypeVO shareTypeVO = new ShareTypeVO();
        shareTypeVO.setShareType(one.getShareType());
        return ResponseResult.success().data(shareTypeVO);
    }

    @Operation(summary = "校验提取码", description = "校验提取码", tags = {"share"})
    @GetMapping(value = "/checkextractioncode")
    public ResponseResult checkExtractionCode(CheckExtractionCodeDto checkExtractionCodeDto) {
        LambdaQueryWrapper<Share> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Share::getShareBatchNum, checkExtractionCodeDto.getShareBatchNum())
                .eq(Share::getExtractionCode, checkExtractionCodeDto.getExtractionCode());
        List<Share> list = shareService.list(lambdaQueryWrapper);
        if (list.isEmpty()) {
            return ResponseResult.fail().message("校验失败");
        } else {
            return ResponseResult.success();
        }
    }

    @Operation(summary = "校验过期时间", description = "校验过期时间", tags = {"share"})
    @GetMapping(value = "/checkendtime")
    public ResponseResult checkEntTime(CheckEndTimeDto checkEndTimeDto) {
        LambdaQueryWrapper<Share> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Share::getShareBatchNum, checkEndTimeDto.getShareBatchNum());
        Share one = shareService.getOne(lambdaQueryWrapper);
        if (one == null) {
            return ResponseResult.fail().message("分享文件不存在");
        }
        String endTime = one.getEndTime();
        Date entTime1 = null;
        try {
            entTime1 = DateUtil.getDateByFormatString(endTime, "yyyy-MM-dd HH:mm:ss");
        } catch (ParseException e) {
            log.error("日期解析失败：{}", e);
        }
        if (new Date().after(entTime1)) {
            return ResponseResult.fail().message("文件分享已过期");
        } else {
            return ResponseResult.success();
        }
    }

}
