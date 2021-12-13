package cn.vesns.netdisk.service;

import cn.vesns.netdisk.pojo.Storage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author : vip865047755@126.com
 * @version :JDK1.8
 * @date : 2021-11-04 22:07
 * @File : StorageService.java
 * @software: IntelliJ IDEA
 */
public interface StorageService extends IService<Storage> {

    Long getTotalStorageSize(Long userId);

    boolean checkStorage (Long userId,Long fileSize);
}
