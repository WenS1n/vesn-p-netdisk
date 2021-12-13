package cn.vesns.netdisk.common.dto.file;/**
 * @version :JDK1.8
 * @date : 2021-11-15 19:26
 * @author : vip865047755@126.com
 * @File : CreateOfficeFileDto.java
 * @software: IntelliJ IDEA
 */

import lombok.Data;

/**
 * @author: vesns vip865047755@126.com
 * @Title: CreateOfficeFileDto
 * @ProjectName: netdisk
 * @Description:
 * @date: 2021-11-15 19:26
 */
@Data
public class CreateOfficeFileDto {
    private String filePath;
    private String fileName;
    private String extendName;
}
