package cn.vesns.netdisk.util;/**
 * @version :JDK1.8
 * @date : 2021-11-02 21:17
 * @author : vip865047755@126.com
 * @File : VesnsFileUtil.java
 * @software: IntelliJ IDEA
 */

import cn.vesns.netdisk.pojo.User;
import cn.vesns.netdisk.pojo.UserFile;
import com.qiwenshare.common.util.DateUtil;

/**
 * @author: vesns vip865047755@126.com
 * @Title: VesnsFileUtil
 * @ProjectName: qiwen-file
 * @Description:
 * @date: 2021-11-02 21:17
 */
public class VesnsFileUtil {


    public static UserFile getVesnsDir(long userId, String filePath, String fileName) {
        SnowUtil snow = new SnowUtil(0,0);
        UserFile userFile = new UserFile();
        userFile.setUserFileId(snow.nextId());
        userFile.setUserId(userId);
        userFile.setFileId(null);
        userFile.setFileName(fileName);
        userFile.setFilePath(filePath);
        userFile.setExtendName(null);
        userFile.setIsDir(1);
        userFile.setUploadTime(DateUtil.getCurrentTime());
        userFile.setDeleteFlag(0);
        userFile.setDeleteBatchNum(null);
        return userFile;
    }
}
