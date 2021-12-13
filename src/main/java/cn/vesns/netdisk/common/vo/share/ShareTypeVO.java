package cn.vesns.netdisk.common.vo.share;/**
 * @version :JDK1.8
 * @date : 2021-11-15 23:36
 * @author : vip865047755@126.com
 * @File : ShareTypeVO.java
 * @software: IntelliJ IDEA
 */

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: vesns vip865047755@126.com
 * @Title: ShareTypeVO
 * @ProjectName: netdisk
 * @Description:
 * @date: 2021-11-15 23:36
 */

@Data
@Schema(description="分享类型VO")
public class ShareTypeVO {
    @Schema(description="0公共，1私密，2好友")
    private Integer shareType;
}
