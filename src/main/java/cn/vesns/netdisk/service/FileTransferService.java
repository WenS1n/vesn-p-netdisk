package cn.vesns.netdisk.service;

import cn.vesns.netdisk.common.dto.file.DownloadFileDTO;
import cn.vesns.netdisk.common.dto.file.PreviewDTO;
import cn.vesns.netdisk.common.dto.file.UploadFileDTO;
import cn.vesns.netdisk.pojo.FileBean;
import cn.vesns.netdisk.pojo.Storage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author : vip865047755@126.com
 * @version :JDK1.8
 * @date : 2021-11-02 22:22
 * @File : FileTransferService.java
 * @software: IntelliJ IDEA
 */
public interface FileTransferService {

    /**
     * 上传文件
     *
     * @param request       请求
     * @param uploadFileDto 文件信息
     */
    void uploadFile(HttpServletRequest request, UploadFileDTO uploadFileDto, Long userId);

    /**
     * 下载文件
     * @param httpServletResponse
     * @param downloadFileDTO
     */
    void downloadFile(HttpServletResponse httpServletResponse, DownloadFileDTO downloadFileDTO);

    /**
     * 预览文件
     * @param httpServletResponse
     * @param previewDTO
     */
    void previewFile(HttpServletResponse httpServletResponse, PreviewDTO previewDTO);

    /**
     * 删除文件
     * @param fileBean
     */
    void deleteFile(FileBean fileBean);

    Storage selectStorageBean(Storage storageBean);

    void insertStorageBean(Storage storageBean);

    void updateStorageBean(Storage storageBean);

    Storage selectStorageByUser(Storage storageBean);

    Long selectStorageSizeByUserId(Long userId);
}
