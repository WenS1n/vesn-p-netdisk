package cn.vesns.netdisk.service;

import cn.vesns.netdisk.common.vo.share.ShareFileListVO;
import cn.vesns.netdisk.pojo.ShareFile;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author : vip865047755@126.com
 * @version :JDK1.8
 * @date : 2021-11-03 22:38
 * @File : ShareFileService.java
 * @software: IntelliJ IDEA
 */
public interface ShareFileService extends IService<ShareFile> {

    void batchInsertShareFile(List<ShareFile> shareFiles);
    List<ShareFileListVO> selectShareFileList(String shareBatchNum, String filePath);
}
