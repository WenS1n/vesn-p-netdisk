package cn.vesns.netdisk.common.dto.file;/**
 * @version :JDK1.8
 * @date : 2021-11-11 22:24
 * @author : vip865047755@126.com
 * @File : BatchDeleteFileDto.java
 * @software: IntelliJ IDEA
 */

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.regex.Pattern;

/**
 * @author: vesns vip865047755@126.com
 * @Title: BatchDeleteFileDto
 * @ProjectName: netdisk
 * @Description:
 * @date: 2021-11-11 22:24
 */
@Data
@Schema(name = "批量删除文件",required = true)
public class BatchDeleteFileDto {

    @Schema(description="文件集合", required = true)
    private String files;

    public static void main(String[] args) {
        boolean ddd = Pattern.matches("(?!((^(con)$)|^(con)/..*|(^(prn)$)|^(prn)/..*|(^(aux)$)|^(aux)/..*|(^(nul)$)|^(nul)/..*|(^(com)[1-9]$)|^(com)[1-9]/..*|(^(lpt)[1-9]$)|^(lpt)[1-9]/..*)|^/s+|.*/s$)(^[^/////:/*/?/\"/</>/|]{1,255}$)", "con1");
        System.out.println(ddd);
    }

}
