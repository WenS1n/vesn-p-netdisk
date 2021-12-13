package cn.vesns.netdisk.service.impl;/**
 * @version :JDK1.8
 * @date : 2021-11-04 22:09
 * @author : vip865047755@126.com
 * @File : StorageServiceImpl.java
 * @software: IntelliJ IDEA
 */

import cn.vesns.netdisk.mapper.StorageMapper;
import cn.vesns.netdisk.mapper.SysParamMapper;
import cn.vesns.netdisk.mapper.UserFileMapper;
import cn.vesns.netdisk.pojo.Storage;
import cn.vesns.netdisk.pojo.Sysparam;
import cn.vesns.netdisk.pojo.UserFile;
import cn.vesns.netdisk.service.StorageService;
import cn.vesns.netdisk.util.SnowUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author: vesns vip865047755@126.com
 * @Title: StorageServiceImpl
 * @ProjectName: netdisk
 * @Description:
 * @date: 2021-11-04 22:09
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class StorageServiceImpl extends ServiceImpl<StorageMapper, Storage> implements StorageService {


    @Resource
    StorageMapper storageMapper;

    @Resource
    SysParamMapper sysParamMapper;

    @Resource
    UserFileMapper userFileMapper;

    SnowUtil snowUtil = new SnowUtil(0, 0);


    @Override
    public Long getTotalStorageSize(Long userId) {
        return getStorageSizeAndCheckStorage(userId);
    }

    @Override
    public boolean checkStorage(Long userId, Long fileSize) {
        long storageSizeAndCheckStorage = getStorageSizeAndCheckStorage(userId);
        Long aLong = userFileMapper.selectStorageSizeByUserId(userId);
        if (aLong == null) {
            aLong = 0L;
        }
        if (aLong + fileSize > storageSizeAndCheckStorage) {
            return false;
        }
        return true;

    }

    public long getStorageSizeAndCheckStorage(Long userId) {
        LambdaQueryWrapper<Storage> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Storage::getUserId, userId);
        Storage storage = storageMapper.selectOne(lambdaQueryWrapper);
        Long totalStorageSize = null;
        if (storage == null || storage.getTotalStorageSize() == null) {
            LambdaQueryWrapper<Sysparam> lambdaQueryWrapper1 = new LambdaQueryWrapper<>();
            lambdaQueryWrapper1.eq(Sysparam::getSysParamKey, "totalStorageSize");
            Sysparam sysparam = sysParamMapper.selectOne(lambdaQueryWrapper1);
            totalStorageSize = Long.parseLong(sysparam.getSysParamValue());
            storage = new Storage();
            storage.setStorageId(snowUtil.nextId());
            storage.setUserId(userId);
            storage.setTotalStorageSize(totalStorageSize);
            storageMapper.insert(storage);
        } else {
            totalStorageSize = storage.getTotalStorageSize();
        }
        if (totalStorageSize != null) {
            totalStorageSize = totalStorageSize * 1024 * 1024;
        }
        return totalStorageSize;
    }
}
