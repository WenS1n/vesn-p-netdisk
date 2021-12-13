package cn.vesns.netdisk.controller;/**
 * @version :JDK1.8
 * @date : 2021-11-15 23:50
 * @author : vip865047755@126.com
 * @File : TaskController.java
 * @software: IntelliJ IDEA
 */

import cn.vesns.netdisk.helper.FileHelper;
import cn.vesns.netdisk.pojo.FileBean;
import cn.vesns.netdisk.pojo.UserFile;
import cn.vesns.netdisk.service.ElasticSearchService;
import cn.vesns.netdisk.service.FileService;
import cn.vesns.netdisk.service.FileTransferService;
import cn.vesns.netdisk.service.UserFileService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author: vesns vip865047755@126.com
 * @Title: TaskController
 * @ProjectName: netdisk
 * @Description:
 * @date: 2021-11-15 23:50
 */
@RestController
@Slf4j
public class TaskController {


    @Autowired
    FileService fileService;

    @Autowired
    UserFileService userFileService;

    @Autowired
    private ElasticSearchService elasticSearchService;

    @Autowired
    FileTransferService fileTransferService;

    @Autowired
    FileHelper fileHelper;

    @Scheduled(cron = "0 0/1 * * * ?")
    public void deleteFile() {
        LambdaQueryWrapper<FileBean> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(FileBean::getPointCount, 0);
        List<FileBean> list = fileService.list(lambdaQueryWrapper);
        for (int i = 0; i < list.size(); i++) {
            FileBean fileBean = list.get(i);
            log.info("删除本地文件：" + JSON.toJSONString(fileBean));
            try {
                fileTransferService.deleteFile(fileBean);
                fileService.removeById(fileBean.getFileId());
            }catch (Exception e) {
                log.error("删除本地文件失败：" + JSON.toJSONString(fileBean));
            }
        }
    }

    @Scheduled(fixedRate = 1000 * 60 * 60 * 24)
    public void updateElasticSearch() {

        try {
            elasticSearchService.deleteAll();
        } catch (Exception e) {
            log.debug("删除ES失败:" + e);
        }

        List<UserFile> userFileList = userFileService.list();
        for (UserFile userFile : userFileList) {
            fileHelper.uploadElasticSearchByUserFileId(userFile.getUserFileId());
        }

    }

}
