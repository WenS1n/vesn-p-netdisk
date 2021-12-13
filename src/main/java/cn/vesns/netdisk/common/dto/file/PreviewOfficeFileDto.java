package cn.vesns.netdisk.common.dto.file;/**
 * @version :JDK1.8
 * @date : 2021-11-15 20:33
 * @author : vip865047755@126.com
 * @File : PreviewOfficeFileDTO.java
 * @software: IntelliJ IDEA
 */

import lombok.Data;

/**
 * @author: vesns vip865047755@126.com
 * @Title: PreviewOfficeFileDTO
 * @ProjectName: netdisk
 * @Description:
 * @date: 2021-11-15 20:33
 */
@Data
public class PreviewOfficeFileDto {
    private long userFileId;
    private String previewUrl;
}
