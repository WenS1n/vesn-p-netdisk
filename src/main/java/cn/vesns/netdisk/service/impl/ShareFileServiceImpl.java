package cn.vesns.netdisk.service.impl;/**
 * @version :JDK1.8
 * @date : 2021-11-03 22:39
 * @author : vip865047755@126.com
 * @File : ShareFileServiceImpl.java
 * @software: IntelliJ IDEA
 */

import cn.vesns.netdisk.common.vo.share.ShareFileListVO;
import cn.vesns.netdisk.mapper.ShareFileMapper;
import cn.vesns.netdisk.pojo.ShareFile;
import cn.vesns.netdisk.service.ShareFileService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: vesns vip865047755@126.com
 * @Title: ShareFileServiceImpl
 * @ProjectName: netdisk
 * @Description:
 * @date: 2021-11-03 22:39
 */
@Slf4j
@Service
@Transactional(rollbackFor=Exception.class)
public class ShareFileServiceImpl extends ServiceImpl<ShareFileMapper, ShareFile> implements ShareFileService {

    @Resource
    ShareFileMapper shareFileMapper;

    @Override
    public void batchInsertShareFile(List<ShareFile> shareFiles) {
        shareFileMapper.batchInsertShareFile(shareFiles);
    }

    @Override
    public List<ShareFileListVO> selectShareFileList(String shareBatchNum, String filePath) {
        return shareFileMapper.selectShareFileList(shareBatchNum, filePath);
    }
}
