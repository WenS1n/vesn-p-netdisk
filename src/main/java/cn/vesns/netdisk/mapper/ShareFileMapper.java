package cn.vesns.netdisk.mapper;/**
 * @version :JDK1.8
 * @date : 2021-11-02 20:20
 * @author : vip865047755@126.com
 * @File : ShareFileMapper.java
 * @software: IntelliJ IDEA
 */

import cn.vesns.netdisk.common.vo.share.ShareFileListVO;
import cn.vesns.netdisk.pojo.ShareFile;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author: vesns vip865047755@126.com
 * @Title: ShareFileMapper
 * @ProjectName: netdisk
 * @Description:
 * @date: 2021-11-02 20:20
 */
public interface ShareFileMapper extends BaseMapper<ShareFile> {

    /**
     * 新增分享
     * @param shareFiles
     */
    void batchInsertShareFile(List<ShareFile> shareFiles);


    List<ShareFileListVO> selectShareFileList(@Param("shareBatchNum") String shareBatchNum, @Param("shareFilePath") String filePath);


}
