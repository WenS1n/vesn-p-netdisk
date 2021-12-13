package cn.vesns.netdisk.helper;/**
 * @version :JDK1.8
 * @date : 2021-11-02 18:14
 * @author : vip865047755@126.com
 * @File : FileHelper.java
 * @software: IntelliJ IDEA
 */

import cn.hutool.core.bean.BeanUtil;
import cn.vesns.netdisk.common.vo.file.FileListVo;
import cn.vesns.netdisk.config.es.FileSearch;
import cn.vesns.netdisk.mapper.ShareFileMapper;
import cn.vesns.netdisk.mapper.UserFileMapper;
import cn.vesns.netdisk.pojo.*;
import cn.vesns.netdisk.service.*;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.qiwenshare.common.constant.FileConstant;
import com.qiwenshare.common.util.DateUtil;
import com.qiwenshare.ufop.util.UFOPUtils;
import lombok.extern.slf4j.Slf4j;


import org.apache.catalina.mbeans.UserMBean;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author: vesns vip865047755@126.com
 * @Title: FileHelper
 * @ProjectName: netdisk
 * @Description:
 * @date: 2021-11-02 18:14
 * 文件逻辑处理组件
 */
@Slf4j
@Component
public class FileHelper {

    @Resource
    UserFileMapper userFileMapper;

    @Autowired
    UserService userService;

    @Autowired
    ShareService shareService;

    @Autowired
    UserFileService userFileService;

    @Autowired
    ShareFileService shareFileService;

    @Resource
    ElasticSearchService elasticSearchService;


    private static ListeningExecutorService executor = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(20));

    /**
     * 获取重复文件名
     * <p>
     * 场景1: 文件还原时，在 savefilePath 路径下，保存 测试.txt 文件重名，则会生成 测试(1).txt
     * 场景2： 上传文件时，在 savefilePath 路径下，保存 测试.txt 文件重名，则会生成 测试(1).txt 以此类推
     *
     * @param userFile
     * @param savefilePath
     * @return
     */
    public String getRepeatFileName(UserFile userFile, String savefilePath) {
        String fileName = userFile.getFileName();
        String extendName = userFile.getExtendName();
        Integer deleteFlag = userFile.getDeleteFlag();
        Long userId = userFile.getUserId();
        Integer isDir = userFile.getIsDir();
        LambdaQueryWrapper<UserFile> userFileLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userFileLambdaQueryWrapper.eq(UserFile::getFilePath, savefilePath)
                .eq(UserFile::getDeleteFlag, deleteFlag)
                .eq(UserFile::getUserId, userId)
                .eq(UserFile::getFileName, fileName)
                .eq(UserFile::getIsDir, isDir);
        if (userFile.getIsDir() == 0) {
            userFileLambdaQueryWrapper.eq(UserFile::getExtendName, extendName);
        }
        List<UserFile> userFiles = userFileMapper.selectList(userFileLambdaQueryWrapper);
        if (userFiles == null) {
            return fileName;
        }
        if (userFiles.isEmpty()) {
            return fileName;
        }

        int i = 0;
        while (userFiles != null && !userFiles.isEmpty()) {
            i++;
            LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(UserFile::getFilePath, savefilePath)
                    .eq(UserFile::getDeleteFlag, deleteFlag)
                    .eq(UserFile::getUserId, userId)
                    .eq(UserFile::getFileName, fileName + "(" + i + ")")
                    .eq(UserFile::getIsDir, isDir);
            if (userFile.getIsDir() == 0) {
                lambdaQueryWrapper.eq(UserFile::getFilePath, savefilePath);
            }
            userFiles = userFileMapper.selectList(lambdaQueryWrapper);
        }
        return fileName + "(" + i + ")";

    }


    /**
     * 还原父文件路径
     * <p>
     * 1、回收站文件还原操作会将文件恢复到原来的路径下,当还原文件的时候，如果父目录已经不存在了，则需要先把父目录给还原
     * 2、上传目录
     *
     * @param filePath
     * @param sessionUserId
     */
    public void restoreParentFilePath(String filePath, Long sessionUserId) {
        // 加锁，防止并发情况下有重复创建目录情况
        Lock lock = new ReentrantLock();
        lock.lock();
        try {
            String parentPath = UFOPUtils.getParentPath(filePath);
            if (parentPath.contains("/")) {
                String fileName = parentPath.substring(parentPath.lastIndexOf("/") + 1);
                parentPath = UFOPUtils.getParentPath(parentPath);

                LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(UserFile::getFilePath, parentPath + FileConstant.pathSeparator)
                        .eq(UserFile::getFileName, fileName)
                        .eq(UserFile::getDeleteFlag, 0)
                        .eq(UserFile::getIsDir, 1)
                        .eq(UserFile::getUserId, sessionUserId);
                List<UserFile> userFileList = userFileMapper.selectList(lambdaQueryWrapper);
                if (userFileList.size() == 0) {
                    UserFile userFile = new UserFile();
                    userFile.setUserId(sessionUserId);
                    userFile.setFileName(fileName);
                    userFile.setFilePath(parentPath + FileConstant.pathSeparator);
                    userFile.setDeleteFlag(0);
                    userFile.setIsDir(1);
                    userFile.setUploadTime(DateUtil.getCurrentTime());

                    userFileMapper.insert(userFile);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

    }

    /**
     * 删除重复的子目录文件
     * <p>
     * 当还原目录的时候，如果其子目录在文件系统中已存在，则还原之后进行去重操作
     *
     * @param filePath
     * @param sessionUserId
     */
    public void deleteRepeatSubDirFile(String filePath, Long sessionUserId) {
        LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.select(UserFile::getFileName, UserFile::getFilePath)
                .eq(UserFile::getIsDir, 1)
                .eq(UserFile::getDeleteFlag, 0)
                .eq(UserFile::getUserId, sessionUserId)
                .groupBy(UserFile::getFilePath, UserFile::getFileName)
                .having("count(fileName) >= 2");
        List<UserFile> repeatList = userFileMapper.selectList(lambdaQueryWrapper);
        for (UserFile userFile : repeatList) {
            LambdaQueryWrapper<UserFile> lambdaQueryWrapper2 = new LambdaQueryWrapper<>();
            lambdaQueryWrapper2.eq(UserFile::getFilePath, userFile.getFileName())
                    .eq(UserFile::getFileName, userFile.getFilePath())
                    .eq(UserFile::getDeleteFlag, 0);
            List<UserFile> userFiles = userFileMapper.selectList(lambdaQueryWrapper2);
            for (int i = 0; i < userFiles.size() - 1; i++) {
                userFileMapper.deleteById(userFiles.get(i).getUserFileId());
            }

        }

    }


    /**
     * 判断该路径在树节点中是否已经存在
     *
     * @param childrenTreeNodes
     * @param path
     * @return
     */
    public boolean isExistPath(List<TreeNode> childrenTreeNodes, String path) {
        boolean isExistPath = false;

        try {
            for (int i = 0; i < childrenTreeNodes.size(); i++) {
                if (path.equals(childrenTreeNodes.get(i).getLabel())) {
                    isExistPath = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return isExistPath;
    }


    /**
     * 组织一个树目录节点，文件移动的时候使用
     *
     * @param treeNode
     * @param id
     * @param filePath
     * @param nodeNameQueue
     * @return
     */
    public TreeNode insertTreeNode(TreeNode treeNode, long id, String filePath, Queue<String> nodeNameQueue) {
        List<TreeNode> children = treeNode.getChildren();
        String peek = nodeNameQueue.peek();
        if (peek == null) {
            return treeNode;
        }

        filePath = filePath + peek + "/";

        if (!isExistPath(children, peek)) {
            // 判断有无该子节点，如没有则插入
            TreeNode resultTreeNode = new TreeNode();
            resultTreeNode.setLabel(nodeNameQueue.poll());
            resultTreeNode.setFilePath(filePath);
            resultTreeNode.setId(++id);

            children.add(resultTreeNode);
        } else {
            // 如果存在，则跳过
            nodeNameQueue.poll();
        }

        if (nodeNameQueue.size() != 0) {
            for (int i = 0; i < nodeNameQueue.size(); i++) {
                TreeNode childrenTreeNode = children.get(i);
                if (peek.equals(childrenTreeNode.getLabel())) {
                    insertTreeNode(childrenTreeNode, id * 10, filePath, nodeNameQueue);
                    children.remove(i);
                    children.add(childrenTreeNode);
                    treeNode.setChildren(children);
                }
            }

        } else {
            treeNode.setChildren(children);
        }
        return treeNode;
    }

    /**
     * 更新es
     *
     * @param userFileId
     */
    public void uploadElasticSearchByUserFileId(Long userFileId) {
        executor.submit(() -> {
            try {
                UserFile userFile = new UserFile();
                userFile.setUserFileId(userFileId);
                List<FileListVo> userFileList = userFileMapper.getUserFileList(userFile, null, null);
                if (userFileList.size() > 0 && userFileList != null) {
                    FileSearch fileSearch = new FileSearch();
                    BeanUtil.copyProperties(userFileList.get(0), fileSearch);
                    //                if (fileSearch.getIsDir() == 0) {
//
//                    Reader reader = ufopFactory.getReader(fileSearch.getStorageType());
//                    ReadFile readFile = new ReadFile();
//                    readFile.setFileUrl(fileSearch.getFileUrl());
//                    String content = reader.read(readFile);
//                    //全文搜索
//    //                fileSearch.setContent(content);
//
//                }
                    elasticSearchService.save(fileSearch);
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error("es更新失败，请检查配置");
            }
        });
    }


    /**
     * 删除es
     *
     * @param userFileId
     */
    public void deleteElasticSearchByUserFileId(Long userFileId) {
        executor.execute(() -> {
            try {
                elasticSearchService.deleteById(userFileId);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("es删除失败，请检查配置");
            }
        });
    }

    /**
     * 根据用户传入的参数，判断是否有下载或者预览权限
     *
     * @return
     */
    public boolean checkAuthDownloadAndPreview(String shareBatchNum,
                                               String extractionCode,
                                               String token,
                                               long userFileId) {
        System.out.println("userFileId---->2" + userFileId);
        UserFile userFile = userFileService.getById(userFileId);
        System.out.println("userFile-------------->" + userFile.toString());
        if ("undefined".equals(shareBatchNum) || StringUtils.isEmpty(shareBatchNum)) {
            User userByToken = userService.getUserByToken(token);
            System.out.println("当前登录用户====>" + userByToken.getUserId());
            System.out.println(JSON.toJSONString("当前登录session用户：" + userByToken));
            if (userByToken == null) {
                return false;
            }
            System.out.println("文件所属用户id：" + userFile.getUserId());
            System.out.println("登录用户id:" + userByToken.getUserId());
            System.out.println("userFile.getUserId----->" + userFile.getUserId());
            if (userFile.getUserId().longValue() != userByToken.getUserId().longValue()) {
                // 用户id不一致，校验失败
                return false;
            }

        } else {
            Map<String, Object> map = new HashMap<>();
            map.put("shareBatchNum", shareBatchNum);
            List<Share> shares = shareService.listByMap(map);
            if (shares.size() <= 0) {
                // 分享批次号不存在，校验失败
                return false;
            }
            Integer shareType = Math.toIntExact(shares.get(0).getShareType());
            if (1 == shareType) {
                // 提取码错误，校验失败
                if (!shares.get(0).getExtractionCode().equals(extractionCode)) {
                    return false;
                }
            }
            map.put("userFileId", userFileId);
            List<ShareFile> shareFiles = shareFileService.listByMap(map);
            if (shareFiles.size() <= 0) {
                // 用户id和分享批次号不匹配，权限校验失败
                return false;
            }
        }
        return true;

    }


}
