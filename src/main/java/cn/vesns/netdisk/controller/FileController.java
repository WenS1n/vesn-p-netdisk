package cn.vesns.netdisk.controller;/**
 * @version :JDK1.8
 * @date : 2021-11-08 21:06
 * @author : vip865047755@126.com
 * @File : FileController.java
 * @software: IntelliJ IDEA
 */

import cn.vesns.netdisk.aop.MyLog;
import cn.vesns.netdisk.common.dto.file.*;
import cn.vesns.netdisk.common.lang.ResponseResult;
import cn.vesns.netdisk.common.vo.file.FileListVo;
import cn.vesns.netdisk.config.es.FileSearch;
import cn.vesns.netdisk.config.exception.VesnsException;
import cn.vesns.netdisk.helper.FileHelper;
import cn.vesns.netdisk.pojo.TreeNode;
import cn.vesns.netdisk.pojo.User;
import cn.vesns.netdisk.pojo.UserFile;
import cn.vesns.netdisk.service.ElasticSearchService;
import cn.vesns.netdisk.service.FileService;
import cn.vesns.netdisk.service.UserFileService;
import cn.vesns.netdisk.util.DateUtil;
import cn.vesns.netdisk.util.SessionUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.qiwenshare.common.exception.NotLoginException;
import com.qiwenshare.common.result.RestResult;
import com.qiwenshare.ufop.util.UFOPUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.codec.digest.DigestUtils;
import org.eclipse.jetty.util.StringUtil;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.*;

/**
 * @author: vesns vip865047755@126.com
 * @Title: FileController
 * @ProjectName: netdisk
 * @Description:
 * @date: 2021-11-08 21:06
 */

@RequestMapping("/file")
@RestController
@Slf4j
@Tag(name = "file", description = "????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????")
public class FileController {

    @Autowired
    FileService fileService;

    @Autowired
    UserFileService userFileService;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Resource
    FileHelper fileHelper;

    public static final String CURRENT_MODULE = "????????????";


    @PostMapping("/createfile")
    @MyLog(operation = "????????????", module = CURRENT_MODULE)
    @Operation(summary = "????????????", description = "??????(?????????)?????????", tags = {"file"})
    public ResponseResult createFile(@Valid @RequestBody CreateFileDto createFileDto) {
        User userSession = (User) SessionUtil.getSession();

        boolean dirExist = userFileService.isDirExist(createFileDto.getFileName(), createFileDto.getFilePath(), userSession.getUserId());
        if (dirExist) {
            return ResponseResult.fail().message("???????????????,?????????????????????");
        }

        UserFile userFile = new UserFile();
        userFile.setUserId(userSession.getUserId());
        userFile.setFileName(createFileDto.getFileName());
        userFile.setFilePath(createFileDto.getFilePath());
        userFile.setDeleteFlag(0);
        userFile.setIsDir(1);
        userFile.setUploadTime(DateUtil.getCurrentTime());
        userFileService.save(userFile);
        fileHelper.uploadElasticSearchByUserFileId(userFile.getFileId());
        return ResponseResult.success();

    }

    @GetMapping("/search")
    @MyLog(operation = "????????????", module = CURRENT_MODULE)
    @Operation(summary = "????????????", description = "????????????", tags = {"file"})
    public ResponseResult<SearchHits<FileSearch>> searchFile(SearchFileDto searchFileDto) {
        User session = (User) SessionUtil.getSession();
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        HighlightBuilder.Field allHigh = new HighlightBuilder.Field("*").preTags("<span class='keyword'>")
                .postTags("</span>");
        queryBuilder.withHighlightFields(allHigh);
        // ??????
        int currentPage = (int) (searchFileDto.getCurrentPage() - 1);
        int pageCount = searchFileDto.getPageCount() == 0 ? 10 : (int) searchFileDto.getPageCount();
        Sort.Direction direction = null;
        String order = searchFileDto.getOrder();

        if (searchFileDto.getDirection() == null) {
            direction = Sort.Direction.DESC;
        } else if ("asc".equals(searchFileDto.getDirection())) {
            direction = Sort.Direction.ASC;
        } else if ("desc".equals(searchFileDto.getDirection())) {
            direction = Sort.Direction.DESC;
        } else {
            direction = Sort.Direction.DESC;
        }
        if (order == null) {
            queryBuilder.withPageable(PageRequest.of(currentPage, pageCount));
        } else {
            queryBuilder.withPageable(PageRequest.of(currentPage, pageCount, Sort.by(direction, order)));
        }
        queryBuilder.withQuery(QueryBuilders.boolQuery()
                .must(QueryBuilders.multiMatchQuery(searchFileDto.getFileName(), "fileName", "content"))
                .must(QueryBuilders.termQuery("userId", session.getUserId()))
        );

        SearchHits<FileSearch> search = elasticsearchRestTemplate.search(queryBuilder.build(), FileSearch.class);
        return ResponseResult.success().data(search);
    }

    @PostMapping("/renamefile")
    @MyLog(operation = "???????????????", module = CURRENT_MODULE)
    @Operation(summary = "???????????????", description = "???????????????", tags = {"file"})
    public ResponseResult<SearchHits<FileSearch>> reNameFile(@RequestBody RenameFileDTO renameFileDto) {

        User userSession = (User) SessionUtil.getSession();
        if (userSession == null) {
            throw new NotLoginException();
        }

        UserFile userFile = userFileService.getById(renameFileDto.getUserFileId());
        List<UserFile> userFiles = userFileService.selectUserFileByNameAndPath(renameFileDto.getFileName(), userFile.getFilePath(), userSession.getUserId());

        if (userFiles != null && !userFiles.isEmpty()) {
            return ResponseResult.fail().data("?????????????????????");
        }
        LambdaUpdateWrapper<UserFile> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.set(UserFile::getFileName, renameFileDto.getFileName())
                .set(UserFile::getUploadTime, DateUtil.getCurrentTime())
                .eq(UserFile::getUserFileId, renameFileDto.getUserFileId());
        userFileService.update(lambdaUpdateWrapper);
        if (1 == userFile.getIsDir()) {
            userFileService.replaceUserFilePath(userFile.getFilePath() + renameFileDto.getFileName() + "/",
                    userFile.getFilePath() + userFile.getFileName() + "/", userSession.getUserId());
        }
        fileHelper.uploadElasticSearchByUserFileId(renameFileDto.getUserFileId());
        return ResponseResult.success();

    }


    @Operation(summary = "??????????????????", description = "??????????????????", tags = {"file"})
    @GetMapping("/getfilelist")
    public ResponseResult getFileList(
            @Parameter(description = "????????????", required = true) String filePath,
            @Parameter(description = "?????????", required = true) long currentPage,
            @Parameter(description = "????????????", required = true) long pageCount) {

        System.out.println("--->" + filePath + "--->" + currentPage + "--->" + pageCount);
        UserFile userFile = new UserFile();
        User session = (User) SessionUtil.getSession();
        if (session == null) {
            throw new NotLoginException();
        }
        if (userFile == null) {
            return ResponseResult.fail();
        }
        userFile.setUserId(session.getUserId());

        List<FileListVo> fileList = null;
        userFile.setFilePath(UFOPUtils.urlDecode(filePath));
        if (currentPage == 0 || pageCount == 0) {
            fileList = userFileService.userFileList(userFile, 0L, 10L);
        } else {
            long beginCount = (currentPage - 1) * pageCount;
            System.out.println("beginCount------>>" + beginCount);
            fileList = userFileService.userFileList(userFile, beginCount, pageCount);

        }
        LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserFile::getUserId, userFile.getUserId())
                .eq(UserFile::getFilePath, userFile.getFilePath())
                .eq(UserFile::getDeleteFlag, 0);
        int total = userFileService.count(lambdaQueryWrapper);

        Map<String, Object> map = new HashMap();
        map.put("total", total);
        map.put("list", fileList);

        return ResponseResult.success().data(map);
    }

    @PostMapping("/batchdeletefile")
    @Operation(summary = "??????????????????", description = "??????????????????", tags = "{file}")
    @MyLog(operation = "??????????????????", module = CURRENT_MODULE)
    public ResponseResult<String> deleteImageByIds(@RequestBody BatchDeleteFileDto batchDeleteFileDto) {
        User sessionUser = (User) SessionUtil.getSession();
        if (sessionUser == null) {
            throw new NotLoginException();
        }
        List<UserFile> userFiles = JSON.parseArray(batchDeleteFileDto.getFiles(), UserFile.class);
        DigestUtils.md5Hex("data");
        for (UserFile userFile : userFiles) {
            userFileService.deleteUserFile(userFile.getUserFileId(), sessionUser.getUserId());
            fileHelper.deleteElasticSearchByUserFileId(userFile.getUserFileId());


        }
        return ResponseResult.success().message("????????????????????????");
    }

    @PostMapping("/deletefile")
    @Operation(summary = "????????????", description = "??????????????????????????????", tags = {"file"})
    @MyLog(operation = "????????????", module = CURRENT_MODULE)
    public ResponseResult deleteFile(@RequestBody DeleteFileDTO deleteFileDto) {
        System.out.println("deleteFileDto.toString()-------->"+deleteFileDto.toString());
        User sessionUser = (User) SessionUtil.getSession();
        if (sessionUser == null) {
            throw new NotLoginException();
        }
        userFileService.deleteUserFile(deleteFileDto.getUserFileId(), sessionUser.getUserId());
        fileHelper.deleteElasticSearchByUserFileId(deleteFileDto.getUserFileId());

        return ResponseResult.success();
    }


    @PostMapping("/unzipfile")
    @Operation(summary = "????????????", description = "?????????????????????????????????????????????zip???rar?????????????????????????????????????????????????????????????????????????????????rar??????????????????", tags = {"file"})
    @MyLog(operation = "????????????", module = CURRENT_MODULE)
    public ResponseResult unzipFile(@RequestBody UnZipFileDto unZipFileDto) {
        System.out.println("getUserFileId ------>" + unZipFileDto.getUserFileId());
        System.out.println("unZipFileDto toString ------>" + unZipFileDto.toString());
        User sessionUser = (User) SessionUtil.getSession();
        if (sessionUser == null) {
            throw new NotLoginException();
        }
        try {
            fileService.unzipFile(unZipFileDto.getUserFileId(), unZipFileDto.getUnzipMode(), unZipFileDto.getFilePath());
        } catch (VesnsException e) {
            return ResponseResult.fail().message(e.getMessage());
        }
        return ResponseResult.success();

    }

    @Operation(summary = "????????????", description = "??????????????????????????????", tags = {"file"})
    @PutMapping("/copyfile")
    @MyLog(operation = "????????????", module = CURRENT_MODULE)
    public ResponseResult copyFile(@RequestBody CopyFileDTO copyFileDTO) {

        User sessionUser = (User) SessionUtil.getSession();
        if (sessionUser == null) {
            throw new NotLoginException();
        }
        long userFileId = copyFileDTO.getUserFileId();
        UserFile userFile = userFileService.getById(userFileId);
        String oldFilePath = userFile.getFilePath();
        String newFilePath = copyFileDTO.getFilePath();
        String fileName = userFile.getFileName();
        String extendName = userFile.getExtendName();
        if (userFile.getIsDir() == 1) {
            String testFilePath = oldFilePath + newFilePath + "/";
            if (newFilePath.startsWith(testFilePath)) {
                return ResponseResult.fail().message("??????????????????????????????,????????????");
            }
        }
        userFileService.updateFilepathByFilepath(oldFilePath, newFilePath, fileName, extendName, sessionUser.getUserId());

        return ResponseResult.success();

    }

    @Operation(summary = "????????????", description = "??????????????????????????????", tags = {"file"})
    @PostMapping("/movefile")
    @MyLog(operation = "????????????", module = CURRENT_MODULE)
    public ResponseResult moveFile(@RequestBody MoveFileDto moveFileDto) {

        User sessionUser = (User) SessionUtil.getSession();
        if (sessionUser == null) {
            throw new NotLoginException();
        }

        String oldFilePath = moveFileDto.getOldFilePath();
        String newFilePath = moveFileDto.getFilePath();
        String fileName = moveFileDto.getFileName();
        String extendName = moveFileDto.getExtendName();
        if (StringUtil.isEmpty(extendName)) {
            String testFilePath = oldFilePath + fileName + "/";
            if (newFilePath.startsWith(testFilePath)) {
                return ResponseResult.fail().message("??????????????????????????????,????????????");
            }
        }
        userFileService.updateFilepathByFilepath(oldFilePath, newFilePath, fileName, extendName, sessionUser.getUserId());
        return ResponseResult.success();

    }

    @Operation(summary = "??????????????????", description = "????????????????????????????????????", tags = {"file"})
    @PostMapping("/batchmovefile")
    @MyLog(operation = "??????????????????", module = CURRENT_MODULE)
    public ResponseResult batchMoveFile(@RequestBody BatchMoveFileDTO batchMoveFileDTO) {

        User sessionUser = (User) SessionUtil.getSession();
        if (sessionUser == null) {
            throw new NotLoginException();
        }

        String files = batchMoveFileDTO.getFiles();
        String newFilePath = batchMoveFileDTO.getFilePath();
        List<UserFile> fileList = JSON.parseArray(files, UserFile.class);
        for (UserFile userFile : fileList) {
            if (StringUtil.isEmpty(userFile.getExtendName())) {
                String testFilePath = userFile.getFilePath() + userFile.getFileName() + "/";
                if (newFilePath.startsWith(testFilePath)) {
                    return ResponseResult.fail().message("??????????????????????????????,????????????");
                }
            }
            userFileService.updateFilepathByFilepath(userFile.getFilePath(), newFilePath, userFile.getFileName(), userFile.getExtendName(), sessionUser.getUserId());
        }
        return ResponseResult.success().data("????????????????????????");

    }


    @Operation(summary = "??????????????????????????????", description = "????????????????????????", tags = "{file}")
    @GetMapping("/selectfilebyfiletype")
    public ResponseResult<List<Map<String, Object>>> selectFileByFileType(
            @Parameter(description = "????????????", required = true) int fileType,
            @Parameter(description = "?????????", required = true) long currentPage,
            @Parameter(description = "????????????", required = true) long pageCount) {


        User sessionUser = (User) SessionUtil.getSession();
        if (sessionUser == null) {
            throw new NotLoginException();
        }

        Long userId = sessionUser.getUserId();
        List<FileListVo> fileList = new ArrayList<>();
        Long beginCount = 0L;
        if (pageCount == 0 || currentPage == 0) {
            beginCount = 0L;
            pageCount = 10L;
        } else {
            beginCount = (currentPage - 1) * pageCount;
        }
        Long total = 0L;
        if (fileType == UFOPUtils.OTHER_TYPE) {
            List<String> list = new ArrayList<>();
            list.addAll(Arrays.asList(UFOPUtils.DOC_FILE));
            list.addAll(Arrays.asList(UFOPUtils.IMG_FILE));
            list.addAll(Arrays.asList(UFOPUtils.VIDEO_FILE));
            list.addAll(Arrays.asList(UFOPUtils.MUSIC_FILE));

            fileList = userFileService.selectFileNotInExtendNames(list, beginCount, pageCount, userId);
            total = userFileService.selectCountNotInExtendNames(list, beginCount, pageCount, userId);
        } else {
//            System.out.println(UFOPUtils.getFileExtendsByType(fileType));
            fileList = userFileService.selectFileByExtendName(UFOPUtils.getFileExtendsByType(fileType), beginCount, pageCount, userId);
            total = userFileService.selectCountByExtendName(UFOPUtils.getFileExtendsByType(fileType), beginCount, pageCount, userId);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("list", fileList);
        map.put("total", total);
        return ResponseResult.success().data(map);
    }


    @Operation(summary = "???????????????", description = "???????????????????????????????????????", tags = "{file}")
    @GetMapping("/getfiletree")
    public ResponseResult<TreeNode> getFileType() {
        ResponseResult<TreeNode> treeNodeResponseResult = new ResponseResult<>();
        User sessionUser = (User) SessionUtil.getSession();
        if (sessionUser == null) {
            throw new NotLoginException();
        }
        List<UserFile> userFiles = userFileService.selectFilePathTreeByUserId(sessionUser.getUserId());
        TreeNode treeNode = new TreeNode();
        treeNode.setLabel("/");
        treeNode.setId(0L);
        long id = 1;
        for (int i = 0; i < userFiles.size(); i++) {
            UserFile userFile = userFiles.get(i);
            String filePath = userFile.getFilePath() + userFile.getFileName() + "/";
            Queue<String> queue = new LinkedList<>();
            String[] split = filePath.split("/");
            for (int j = 0; j < split.length; j++) {
                if (!"".equals(split[j]) && split[j] != null) {
                    queue.add(split[j]);
                }
            }
            if (queue.size() == 0) {
                continue;
            }
            treeNode = fileHelper.insertTreeNode(treeNode, id++, "/", queue);
        }
        List<TreeNode> children = treeNode.getChildren();
        children.sort((o1, o2) -> {
            long i = o1.getId() - o2.getId();
            return (int) i;
        });
        treeNodeResponseResult.setSuccess(true);
        treeNodeResponseResult.setData(treeNode);
        return treeNodeResponseResult;
    }
}
