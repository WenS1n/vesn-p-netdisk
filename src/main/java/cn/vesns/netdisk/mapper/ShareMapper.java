package cn.vesns.netdisk.mapper;

import cn.vesns.netdisk.common.vo.share.ShareListVo;
import cn.vesns.netdisk.pojo.Share;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @author : vip865047755@126.com
 * @version :JDK1.8
 * @date : 2021-11-02 19:54
 * @File : ShareMapper.java
 * @software: IntelliJ IDEA
 */
public interface ShareMapper extends BaseMapper<Share> {

    List<ShareListVo> selectShareList(String shareFilePath, String shareBatchNum, Long beginCount, Long pageCount, Long userId);

    int selectShareListTotalCount(String shareFilePath,String shareBatchNum, Long userId);
}
