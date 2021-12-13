package cn.vesns.netdisk.service;

import cn.vesns.netdisk.common.dto.share.ShareListDTO;
import cn.vesns.netdisk.common.vo.share.ShareListVo;
import cn.vesns.netdisk.pojo.Share;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author : vip865047755@126.com
 * @version :JDK1.8
 * @date : 2021-11-02 19:48
 * @File :
 * @software: IntelliJ IDEA
 */
public interface ShareService extends IService<Share> {

    /**
     * 查询分享
     * @param shareListDTO
     * @param userId
     * @return
     */
    List<ShareListVo> selectShareList(ShareListDTO shareListDTO, Long userId);

    /**
     * 查询分享总数
     * @param shareListDTO
     * @param userId
     * @return
     */
    int selectShareListTotalCount(ShareListDTO shareListDTO, Long userId);



}
