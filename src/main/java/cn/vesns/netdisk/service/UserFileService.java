package cn.vesns.netdisk.service;

import cn.vesns.netdisk.common.vo.file.FileListVo;
import cn.vesns.netdisk.pojo.UserFile;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author : vip865047755@126.com
 * @version :JDK1.8
 * @date : 2021-11-03 21:33
 * @File : UserFileService.java
 * @software: IntelliJ IDEA
 */
public interface UserFileService extends IService<UserFile> {


    /**
     * 查询用户文件和路径
     * @param fileName
     * @param filePath
     * @param userId
     * @return
     */
    List<UserFile> selectUserFileByNameAndPath(String fileName, String filePath, Long userId);

    /**
     * 是否存在想用文件夹
     * @param fileName
     * @param filePath
     * @param userId
     * @return
     */
    boolean isDirExist(String fileName, String filePath, long userId);

    boolean isFileExist(String fileName, String filePath, long userId);

    List<UserFile> selectSameUserFile(String fileName, String filePath, String extendName, Long userId);

    void replaceUserFilePath(String filePath, String oldFilePath, Long userId);

    List<FileListVo> userFileList(UserFile userFile, Long beginCount, Long pageCount);

    void updateFilepathByFilepath(String oldfilePath, String newfilePath, String fileName, String extendName, long userId);

    void userFileCopy(String oldfilePath, String newfilePath, String fileName, String extendName, long userId);

    List<FileListVo> selectFileByExtendName(List<String> fileNameList, Long beginCount, Long pageCount, long userId);

    Long selectCountByExtendName(List<String> fileNameList, Long beginCount, Long pageCount, long userId);

    List<FileListVo> selectFileNotInExtendNames(List<String> fileNameList, Long beginCount, Long pageCount, long userId);

    Long selectCountNotInExtendNames(List<String> fileNameList, Long beginCount, Long pageCount, long userId);

    List<UserFile> selectFileListLikeRightFilePath(String filePath, long userId);

    List<UserFile> selectFilePathTreeByUserId(Long userId);

    void deleteUserFile(Long userFileId, Long sessionUserId);

}
