package cn.vesns.netdisk.config.exception;/**
 * @version :JDK1.8
 * @date : 2021-11-02 21:07
 * @author : vip865047755@126.com
 * @File : VesnsException.java
 * @software: IntelliJ IDEA
 */


import cn.vesns.netdisk.common.lang.ResultCodeEnum;
import lombok.Data;

/**
 * @author: vesns vip865047755@126.com
 * @Title: VesnsException
 * @ProjectName: netdisk
 * @Description:
 * @date: 2021-11-02 21:07
 */
@Data
public class VesnsException extends RuntimeException {
    private Integer code;

    public VesnsException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public VesnsException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
    }

    @Override
    public String toString() {
        return "VesnsException{" +
                "code=" + code + ", message = " + this.getMessage() +
                '}';
    }
}
