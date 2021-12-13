package cn.vesns.netdisk;

import cn.vesns.netdisk.pojo.FileBean;
import cn.vesns.netdisk.service.FileService;
import cn.vesns.netdisk.util.PasswordUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class NetdiskApplicationTests {

    @Autowired
    FileService fileService;

        @Test
        public void test() {
//            System.out.println(PasswordUtil.getSaltValue());
//            7056985593087575
            LambdaQueryWrapper<FileBean> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(FileBean::getPointCount, 0);
            List<FileBean> list = fileService.list(lambdaQueryWrapper);
            for (FileBean fileBean : list) {
                System.out.println(fileBean);
            }
        }

}
