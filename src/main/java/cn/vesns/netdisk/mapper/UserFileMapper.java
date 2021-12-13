package cn.vesns.netdisk.mapper;/**
 * @version :JDK1.8
 * @date : 2021-11-01 23:38
 * @author : vip865047755@126.com
 * @File : UserFileMapper.java
 * @software: IntelliJ IDEA
 */

import cn.vesns.netdisk.common.vo.file.FileListVo;
import cn.vesns.netdisk.pojo.UserFile;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * @author: vesns vip865047755@126.com
 * @Title: UserFileMapper
 * @ProjectName: netdisk
 * @Description:
 * @date: 2021-11-01 23:38
 */
public interface UserFileMapper extends BaseMapper<UserFile> {

    /**
     * 修改文件路径
     * @param filePath
     * @param oldFilePath
     * @param userId
     */
    void replaceFilePath(@Param("filePath") String filePath,@Param("oldFilePath") String oldFilePath,@Param("userId") Long userId);


    /**
     * 查询user文件列表 并分页展示
     * @param userFile
     * @param beginCount
     * @param pageCount
     * @return
     */
    List<FileListVo> getUserFileList(@Param("userFile") UserFile userFile, @Param("beginCount") Long beginCount,@Param("pageCount") Long pageCount);

    /**
     * 修改文件路径 并修改名称
     * @param oldfilePath 原来路径
     * @param newfilePath 新路径
     * @param fileName 文件名
     * @param extendName 后缀名
     * @param userId 用户id
     */
    void updateFilepathByPathAndName(@Param("oldfilePath")String oldfilePath, @Param("newfilePath") String newfilePath,@Param("fileName")  String fileName, @Param("extendName")String extendName, long userId);

    /**
     * 文件位置替换
     * @param oldfilePath
     * @param newfilePath
     * @param userId
     */
    void updateFilepathByFilepath(String oldfilePath, String newfilePath, long userId);

    /**
     * 插入路径
     * @param oldFilePath
     * @param newfilePath
     * @param fileName
     * @param extendName
     * @param userId
     */
    void batchInsertByPathAndName(@Param("oldFilePath") String oldFilePath,
                                  @Param("newFilePath") String newfilePath,
                                  @Param("fileName") String fileName,
                                  @Param("extendName") String extendName,
                                  @Param("userId") long userId);

    /**
     * 插入文件
     * @param oldFilePath
     * @param newfilePath
     * @param userId
     */
    void batchInsertByFilepath(@Param("oldFilePath") String oldFilePath,
                               @Param("newFilePath") String newfilePath,
                               @Param("userId") long userId);

    /**
     * 查询文件后缀名
     * @param fileNameList
     * @param beginCount
     * @param pageCount
     * @param userId
     * @return
     */
    List<FileListVo> selectFileByExtendName(List<String> fileNameList, @Param("beginCount") Long beginCount, @Param("pageCount") Long pageCount, long userId);


    /**
     * 查询总数
     * @param fileNameList
     * @param beginCount
     * @param pageCount
     * @param userId
     * @return
     */
    Long selectCountByExtendName(List<String> fileNameList,@Param("beginCount") Long beginCount,@Param("pageCount") Long pageCount, long userId );



    List<FileListVo> selectFileNotInExtendNames(List<String> fileNameList, @Param("beginCount") Long beginCount,@Param("pageCount") Long pageCount, long userId);

    Long selectCountNotInExtendNames(List<String> fileNameList, Long beginCount, Long pageCount, long userId);

    Long selectStorageSizeByUserId(@Param("userId") Long userId);
}
