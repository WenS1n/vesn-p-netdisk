package cn.vesns.netdisk.controller;/**
 * @version :JDK1.8
 * @date : 2021-11-15 19:21
 * @author : vip865047755@126.com
 * @File : OfficeController.java
 * @software: IntelliJ IDEA
 */

import cn.hutool.core.io.file.FileMode;
import cn.vesns.netdisk.common.dto.file.CreateOfficeFileDto;
import cn.vesns.netdisk.common.dto.file.EditOfficeFileDto;
import cn.vesns.netdisk.common.dto.file.PreviewOfficeFileDto;
import cn.vesns.netdisk.common.lang.ResponseResult;
import cn.vesns.netdisk.helper.ConfigManager;
import cn.vesns.netdisk.pojo.FileBean;
import cn.vesns.netdisk.pojo.FileModel;
import cn.vesns.netdisk.pojo.User;
import cn.vesns.netdisk.pojo.UserFile;
import cn.vesns.netdisk.service.FileService;
import cn.vesns.netdisk.service.UserFileService;
import cn.vesns.netdisk.service.UserService;
import cn.vesns.netdisk.util.DateUtil;
import cn.vesns.netdisk.util.SnowUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.qiwenshare.common.exception.NotLoginException;
import com.qiwenshare.ufop.factory.UFOPFactory;
import com.qiwenshare.ufop.operation.copy.Copier;
import com.qiwenshare.ufop.operation.copy.domain.CopyFile;
import com.qiwenshare.ufop.operation.download.domain.DownloadFile;
import com.qiwenshare.ufop.operation.write.Writer;
import com.qiwenshare.ufop.operation.write.domain.WriteFile;
import com.qiwenshare.ufop.util.UFOPUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

/**
 * @author: vesns vip865047755@126.com
 * @Title: OfficeController
 * @ProjectName: netdisk
 * @Description:
 * @date: 2021-11-15 19:21
 */
@RestController
@Tag(name = "office", description = "该接口为Onlyoffice文件操作接口，主要用来做一些文档的编辑，浏览等。")
@Slf4j
@RequestMapping("/office")
public class OfficeController {

    public static final String CURRENT_MODULE = "Onlyoffice文件操作接口";
    @Autowired
    UserService userService;

    @Autowired
    UFOPFactory ufopFactory;

    @Value("${deployment.host}")
    private String deploymentHost;
    @Value("${ufop.storage-type}")
    private Integer storageType;

    @Resource
    FileService fileService;

    @Resource
    UserFileService userFileService;

    @Operation(summary = "创建office文件", description = "创建office文件", tags = {"office"})
    @PostMapping("/createofficefile")
    public ResponseResult createOnlyOfficeFile(@RequestBody CreateOfficeFileDto createOfficeFileDto, @RequestHeader("token") String token) {

        try {
            User userByToken = userService.getUserByToken(token);
            if (userByToken == null) {
                throw new NotLoginException();
            }
            String filePath = createOfficeFileDto.getFilePath();
            String fileName = createOfficeFileDto.getFileName();
            String extendName = createOfficeFileDto.getExtendName();
            List<UserFile> userFiles = userFileService.selectSameUserFile(fileName, filePath, extendName, userByToken.getUserId());
            if (userFiles != null && !userFiles.isEmpty()) {
                return ResponseResult.fail().message("同名文件已存在");
            }
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            String templateFilePath = "";
            if ("docx".equals(extendName)) {
                templateFilePath = "template/Word.docx";
            } else if ("xlsx".equals(extendName)) {
                templateFilePath = "template/Excel.xlsx";
            } else if ("pptx".equals(extendName)) {
                templateFilePath = "template/PowerPoint.pptx";
            }
            String templateFileUrl = UFOPUtils.getStaticPath() + templateFilePath;
            FileInputStream fileInputStream = new FileInputStream(templateFileUrl);
            Copier copier = ufopFactory.getCopier();
            CopyFile copyFile = new CopyFile();
            copyFile.setExtendName(extendName);
            String fileUrl = copier.copy(fileInputStream, copyFile);
            FileBean fileBean = new FileBean();
            fileBean.setFileId(new SnowUtil(0, 0).nextId());
            fileBean.setFileSize(0L);
            fileBean.setFileUrl(fileUrl);
            fileBean.setStorageType(storageType);
            fileBean.setPointCount(1);
            fileBean.setIdentifier(uuid);
            boolean save = fileService.save(fileBean);
            if (save) {
                UserFile userFile = new UserFile();
                userFile.setUserId(userByToken.getUserId());
                userFile.setUploadTime(DateUtil.getCurrentTime());
                userFile.setFilePath(filePath);
                userFile.setFileName(fileName);
                userFile.setDeleteFlag(0);
                userFile.setIsDir(0);
                userFile.setExtendName(extendName);
                userFile.setFileId(fileBean.getFileId());
                userFileService.save(userFile);
            }
            return ResponseResult.success().message("文件创建成功");

        } catch (Exception e) {
            log.error("文件创建失败--" + e.getMessage());
            return ResponseResult.fail().code(500).message("服务端错误，文件创建失败");
        }
    }

    @PostMapping("/previewofficefile")
    @Operation(summary = "预览office文件", description = "预览office文件", tags = {"office"})
    public ResponseResult previewOfficeFile(@RequestHeader("token") String token, HttpServletRequest request, @RequestBody PreviewOfficeFileDto previewOfficeFileDto) {
        ResponseResult result = new ResponseResult();
        try {
            User userByToken = userService.getUserByToken(token);
            if (userByToken == null) {
                throw new NotLoginException();
            }
            UserFile userFile = userFileService.getById(previewOfficeFileDto.getUserFileId());
            String baseUrl = request.getScheme() + "://" + deploymentHost + request.getContextPath();
            System.out.println("baseUrl--->"+baseUrl);
            FileModel file = new FileModel(userFile.getFileName() + "." + userFile.getExtendName(),
                    previewOfficeFileDto.getPreviewUrl(),
                    userFile.getUploadTime(),
                    String.valueOf(userByToken.getUserId()),
                    userByToken.getUsername(),
                    "view");
            String query = "?type=show&token=" + token;
            file.editorConfig.callbackUrl = baseUrl + "/office/IndexServlet" + query;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("file", file);
            System.out.println(file.toString());
            jsonObject.put("docserviceApiUrl", ConfigManager.GetProperty("files.docservice.url.site") + ConfigManager.GetProperty("files.docservice.url.api"));
            jsonObject.put("reportName", userFile.getFileName());
            result.setData(jsonObject);
            result.setCode(200);
            result.setMessage("获取报告成功！");
        } catch (Exception e) {
            log.error(e.getMessage());
            result.setCode(500);
            result.setMessage("服务端错误！");
        }
        return result;
    }

    @Operation(summary = "编辑office文件", description = "编辑office文件", tags = {"office"})
    @PostMapping("/editofficefile")
    public ResponseResult editOfficeFile(HttpServletRequest request, @RequestBody EditOfficeFileDto editOfficeFileDto, @RequestHeader("token") String token) {
        ResponseResult result = new ResponseResult();
        log.info("editOfficeFile");
        try {
            User user = userService.getUserByToken(token);
            if (user == null) {
                throw new NotLoginException();
            }
            UserFile userFile = userFileService.getById(editOfficeFileDto.getUserFileId());
            String baseUrl = request.getScheme() + "://" + deploymentHost + request.getContextPath();
            log.info("回调地址baseUrl：" + baseUrl);
            FileModel file = new FileModel(userFile.getFileName() + "." + userFile.getExtendName(),
                    editOfficeFileDto.getPreviewUrl(),
                    userFile.getUploadTime(),
                    String.valueOf(user.getUserId()),
                    user.getUsername(),
                    "edit");
            file.changeType(request.getParameter("mode"), "edit");
            String query = "?type=edit&userFileId=" + userFile.getUserFileId() + "&token=" + token;
            file.editorConfig.callbackUrl = baseUrl + "/office/IndexServlet" + query;

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("file", file);
            jsonObject.put("docserviceApiUrl", ConfigManager.GetProperty("files.docservice.url.site") + ConfigManager.GetProperty("files.docservice.url.api"));
            jsonObject.put("reportName", userFile.getFileName());
            result.setData(jsonObject);
            result.setCode(200);
            result.setMessage("编辑报告成功！");
            System.out.println("result---------->"+result.toString());
        } catch (Exception e) {
            log.error(e.getMessage());
            result.setCode(500);
            result.setMessage("服务器错误！");
        }
        return result;
    }

    @PostMapping("/IndexServlet")
    @ResponseBody
    public void IndexServlet(HttpServletResponse response, HttpServletRequest request) throws IOException {
        String token = request.getParameter("token");
        User loginUser = userService.getUserByToken(token);
        if (loginUser == null) {
            throw new NotLoginException();
        }

        PrintWriter writer = response.getWriter();
        Scanner scanner = new Scanner(request.getInputStream()).useDelimiter("\\A");
        String body = scanner.hasNext() ? scanner.next() : "";

        JSONObject jsonObj = JSON.parseObject(body);
        log.info("===saveeditedfile:" + jsonObj.get("status"));
        ;
        String status = jsonObj != null ? jsonObj.get("status").toString() : "";
        if ("2".equals(status) || "6".equals(status)) {
            String type = request.getParameter("type");
            String downloadUri = (String) jsonObj.get("url");

            if ("edit".equals(type)) {//修改报告
                String userFileId = request.getParameter("userFileId");
                UserFile userFile = userFileService.getById(userFileId);
                FileBean fileBean = fileService.getById(userFile.getFileId());
                if (fileBean.getPointCount() > 1) {
                    //该场景，暂不支持编辑修改
                    writer.write("{\"error\":1}");
                    return;
                }

                URL url = new URL(downloadUri);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                int fileLength = 0;
                try {
                    InputStream stream = connection.getInputStream();

                    Writer writer1 = ufopFactory.getWriter(fileBean.getStorageType());
                    WriteFile writeFile = new WriteFile();
                    writeFile.setFileUrl(fileBean.getFileUrl());

                    writeFile.setFileSize(connection.getContentLength());
                    writer1.write(stream, writeFile);
                } catch (Exception e) {
                    log.error(e.getMessage());
                } finally {
                    if ("2".equals(status)) {
                        LambdaUpdateWrapper<UserFile> userFileUpdateWrapper = new LambdaUpdateWrapper<>();
                        userFileUpdateWrapper
                                .set(UserFile::getUploadTime, com.qiwenshare.common.util.DateUtil.getCurrentTime())
                                .eq(UserFile::getUserFileId, userFileId);
                        userFileService.update(userFileUpdateWrapper);
                    }
                    LambdaUpdateWrapper<FileBean> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                    fileLength = connection.getContentLength();
                    log.info("当前修改文件大小为：" + Long.valueOf(fileLength));

                    DownloadFile downloadFile = new DownloadFile();
                    downloadFile.setFileUrl(fileBean.getFileUrl());
                    InputStream inputStream = ufopFactory.getDownloader(fileBean.getStorageType()).getInputStream(downloadFile);
                    String md5Str = DigestUtils.md5Hex(inputStream);
                    lambdaUpdateWrapper
                            .set(FileBean::getIdentifier, md5Str)
                            .set(FileBean::getFileSize, Long.valueOf(fileLength))
                            .eq(FileBean::getFileId, fileBean.getFileId());
                    fileService.update(lambdaUpdateWrapper);

                    connection.disconnect();
                }
            }
        }

        if ("3".equals(status) || "7".equals(status)) {//不强制手动保存时为6,"6".equals(status)
            log.debug("====保存失败:");
            writer.write("{\"error\":1}");
        } else {
            log.debug("状态为：0");
            writer.write("{\"error\":" + "0" + "}");
        }
    }

}
