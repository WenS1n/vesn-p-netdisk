package cn.vesns.netdisk.service.impl;/**
 * @version :JDK1.8
 * @date : 2021-11-02 20:08
 * @author : vip865047755@126.com
 * @File : ShareServiceImpl.java
 * @software: IntelliJ IDEA
 */

import cn.vesns.netdisk.common.dto.share.ShareListDTO;
import cn.vesns.netdisk.common.vo.share.ShareListVo;
import cn.vesns.netdisk.mapper.ShareMapper;
import cn.vesns.netdisk.pojo.Share;
import cn.vesns.netdisk.service.ShareService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: vesns vip865047755@126.com
 * @Title: ShareServiceImpl
 * @ProjectName: netdisk
 * @Description:
 * @date: 2021-11-02 20:08
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
class ShareServiceImpl extends ServiceImpl<ShareMapper, Share> implements ShareService {


    @Resource
    ShareMapper shareMapper;

    @Override
    public List<ShareListVo> selectShareList(ShareListDTO shareListDTO, Long userId) {
        Long beginCount = (shareListDTO.getCurrentPage() -1) *shareListDTO.getCurrentPage();
        return shareMapper.selectShareList(
                shareListDTO.getShareFilePath(),
                shareListDTO.getShareBatchNum(),
                beginCount,shareListDTO.getPageCount(),
                userId);

    }

    @Override
    public int selectShareListTotalCount(ShareListDTO shareListDTO, Long userId) {
        return shareMapper.selectShareListTotalCount(shareListDTO.getShareFilePath(),shareListDTO.getShareBatchNum(), userId);

    }


}

