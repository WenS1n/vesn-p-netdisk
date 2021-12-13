package cn.vesns.netdisk.common.vo.file;/**
 * @version :JDK1.8
 * @date : 2021-11-12 20:54
 * @author : vip865047755@126.com
 * @File : UploadFileVo.java
 * @software: IntelliJ IDEA
 */

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author: vesns vip865047755@126.com
 * @Title: UploadFileVo
 * @ProjectName: netdisk
 * @Description:
 * @date: 2021-11-12 20:54
 */
@Data
@Schema(name = "文件上传Vo",required = true)
public class UploadFileVo {
    @Schema(description = "时间戳", example = "123123123123")
    private String timeStampName;
    @Schema(description = "跳过上传", example = "true")
    private boolean skipUpload;
    @Schema(description = "是否需要合并分片", example = "true")
    private boolean needMerge;
    @Schema(description = "已经上传的分片", example = "[1,2,3]")
    private List<Integer> uploaded;

}
