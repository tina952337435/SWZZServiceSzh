package swzzmodeserver.swagger;


import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;

@Configuration
@EnableSwagger2 // 开启Swagger2的自动配置
// 修改逻辑：只要 swagger.enabled 不等于 true，就彻底不加载这个配置类
// 移除了 matchIfMissing = true，改为默认 false，更安全
@ConditionalOnProperty(name = "swagger.enabled", havingValue = "true")
public class SwaggerConfig {

    //配置了Swagger的Docket的bean实例
    //enable 是否启动Swagger 如果为false 则Swagger 不能再浏览器中访问
//    @Bean
//    public Docket docket(Environment environment){
//
//        //设置要显示Swagger的环境
//        Profiles profiles=Profiles.of("dev","test");
//        //获取项目的环境  通过environment.acceptsProfiles判断是否处在自己的设定的环境当中
//        boolean flag = environment.acceptsProfiles(profiles);
//
//
//        return new Docket(DocumentationType.SWAGGER_2)
//                .apiInfo(apiInfo())
//                .groupName("workserve")
//                .enable(flag)
//                .select()
//                .apis(RequestHandlerSelectors.basePackage("swzzmodeserver.workserver.controller"))
//                .build();
//    }
//    private static final Contact DEFAULT_CONTACT =new Contact("bin","HTTP","40384195@qq.com");
//    //配置Swagger信息  apiInfo
//    private ApiInfo apiInfo(){
//        return new ApiInfo("SwaggerAPI文档", "Api Documentation",
//                "1.0", "urn:tos", DEFAULT_CONTACT,
//                "Apache 2.0",
//                "http://www.apache.org/licenses/LICENSE-2.0", new ArrayList());
//    }


    @Bean
    public Docket docket() {
        // 这里的代码只有在 swagger.enabled=true 时才会执行
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .enable(true) // 这里保持 true，控制权交给上面的注解
                .select()
                // 修改为你自己的 Controller 包路径
                .apis(RequestHandlerSelectors.basePackage("swzzmodeserver.workserver.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("API文档").build();
    }
}


