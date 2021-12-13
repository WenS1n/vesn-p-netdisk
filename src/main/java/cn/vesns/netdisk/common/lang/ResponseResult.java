package cn.vesns.netdisk.common.lang;/**
 * @version :JDK1.8
 * @date : 2021-09-24 22:43
 * @author : vip865047755@126.com
 * @File : ResponseResult.java
 * @software: IntelliJ IDEA
 */


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @author: vesns vip865047755@126.com
 * @Title: ResponseResult
 * @ProjectName: netdisk
 * @Description:
 * @date: 2021-09-24 22:43
 */

/**
 * 统一结果返回
 *
 * @param <T> 参数泛型
 */
@Data
@Schema(name = "统一结果返回", required = true)
public class ResponseResult<T> implements Serializable {
    @Schema(description = "请求是否成功", example = "true")
    private Boolean success = true;
    @Schema(description = "返回码", example = "200")
    private Integer code;
    @Schema(description = "返回信息", example = "成功")
    private String message;
    @Schema(description = "返回数据")
    private T data;

    // 通用返回成功
    public static ResponseResult success() {
        ResponseResult r = new ResponseResult();
        r.setSuccess(ResultCodeEnum.SUCCESS.getSuccess());
        r.setCode(ResultCodeEnum.SUCCESS.getCode());
        r.setMessage(ResultCodeEnum.SUCCESS.getMessage());
        return r;
    }


    // 通用返回失败，未知错误
    public static ResponseResult fail() {
        ResponseResult r = new ResponseResult();
        r.setSuccess(ResultCodeEnum.UNKNOWN_ERROR.getSuccess());
        r.setCode(ResultCodeEnum.UNKNOWN_ERROR.getCode());
        r.setMessage(ResultCodeEnum.UNKNOWN_ERROR.getMessage());
        return r;
    }

    // 设置结果，形参为结果枚举
    public static ResponseResult setResult(ResultCodeEnum result) {
        ResponseResult r = new ResponseResult();
        r.setSuccess(result.getSuccess());
        r.setCode(result.getCode());
        r.setMessage(result.getMessage());
        return r;
    }


    // 自定义返回数据
    public ResponseResult data(T param) {
        this.setData(param);
        return this;
    }

    // 自定义状态信息
    public ResponseResult message(String message) {
        this.setMessage(message);
        return this;
    }

    // 自定义状态码
    public ResponseResult code(Integer code) {
        this.setCode(code);
        return this;
    }

    // 自定义返回结果
    public ResponseResult success(Boolean success) {
        this.setSuccess(success);
        return this;
    }
}
