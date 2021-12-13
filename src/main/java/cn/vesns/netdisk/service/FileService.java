package cn.vesns.netdisk.service;


import cn.vesns.netdisk.pojo.FileBean;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author : vip865047755@126.com
 * @version :JDK1.8
 * @date : 2021-10-17 16:21
 * @File : FileService.java
 * @software: IntelliJ IDEA
 */
public interface FileService extends IService<FileBean> {


    void increaseFilePointCount(Long fileId);

    void decreaseFilePointCount(Long fileId);

    void unzipFile(long userFileId, int unzipMode, String filePath);



}
