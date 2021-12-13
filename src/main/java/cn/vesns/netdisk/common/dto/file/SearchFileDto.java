package cn.vesns.netdisk.common.dto.file;/**
 * @version :JDK1.8
 * @date : 2021-11-08 21:35
 * @author : vip865047755@126.com
 * @File : SearchFileDto.java
 * @software: IntelliJ IDEA
 */

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: vesns vip865047755@126.com
 * @Title: SearchFileDto
 * @ProjectName: netdisk
 * @Description:
 * @date: 2021-11-08 21:35
 */
@Data
public class SearchFileDto {
    @Schema(description="文件名", required=true)
    private String fileName;
    @Schema(description="当前页", required=true)
    private long currentPage;
    @Schema(description="每页数量", required=true)
    private long pageCount;
    @Schema(description="排序字段(可排序字段：fileName, fileSize, extendName, uploadTime)", required=false)
    private String order;
    @Schema(description="方向(升序：asc, 降序：desc)", required=false)
    private String direction;
}
