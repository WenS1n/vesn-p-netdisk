package cn.vesns.netdisk.service;

import cn.vesns.netdisk.pojo.UploadTaskDetail;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author : vip865047755@126.com
 * @version :JDK1.8
 * @date : 2021-11-04 22:30
 * @File : UploadTaskDetailService.java
 * @software: IntelliJ IDEA
 */
public interface UploadTaskDetailService extends IService<UploadTaskDetail> {

    List<Integer> getUploadCheckNumList(String identifier);

}
