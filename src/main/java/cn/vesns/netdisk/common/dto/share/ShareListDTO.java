package cn.vesns.netdisk.common.dto.share;/**
 * @version :JDK1.8
 * @date : 2021-11-02 19:52
 * @author : vip865047755@126.com
 * @File : ShareListDTO.java
 * @software: IntelliJ IDEA
 */

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: vesns vip865047755@126.com
 * @Title: ShareListDTO
 * @ProjectName: netdisk
 * @Description:
 * @date: 2021-11-02 19:52
 */

@Data
@Schema(name = "分享列表DTO",required = true)
public class ShareListDTO {

    @Schema(description="分享文件路径")
    private String shareFilePath;
    @Schema(description="批次号")
    private String shareBatchNum;
    @Schema(description = "当前页码")
    private Long currentPage;
    @Schema(description = "一页显示数量")
    private Long pageCount;

}
