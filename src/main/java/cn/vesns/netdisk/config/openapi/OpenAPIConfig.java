package cn.vesns.netdisk.config.openapi;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;


@Configuration
public class OpenAPIConfig {


    /**
     * 前台API分组
     *
     * @return
     */
    @Bean(value = "indexApi")
    public Docket indexApi() {
        return new Docket(DocumentationType.OAS_30)
                .groupName("网站前端接口分组")
                .apiInfo(apiInfo())
                .select()

                .apis(RequestHandlerSelectors.basePackage("cn.vesns.netdisk.controller"))
                .paths(PathSelectors.any())
                .build();
    }
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("烧杯网盘API")
                .description("基于springboot + vue 框架开发的Web文件系统，旨在为用户提供一个简单、方便的文件存储方案，能够以完善的目录结构体系，对文件进行管理 。")
//                .termsOfServiceUrl("http://www.qiwenshare.com:8762/")
//				.contact("developer@mail.com")
                .version("1.1.0")
                .build();
    }
}
