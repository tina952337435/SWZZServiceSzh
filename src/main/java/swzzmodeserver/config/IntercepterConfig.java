package swzzmodeserver.config;
import swzzmodeserver.tools.TokenInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author YuanChangLiang
 * @Date now
 */
@Component
public class IntercepterConfig implements WebMvcConfigurer {

    private TokenInterceptor tokenInterceptor;

    //构造方法
    public IntercepterConfig(TokenInterceptor tokenInterceptor){
        this.tokenInterceptor = tokenInterceptor;
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry){
        List<String> excludePath = new ArrayList<>();
        //登录
        excludePath.add("/SWZZ_DATA_employee/GetToken");
        excludePath.add("/SWZZ_DATA_employee/LOGINSel");
        excludePath.add("/sysEmployee/genernateCaptcha");
        excludePath.add("/sysEmployee/getPhoneNumber"); //获取手机号
        excludePath.add("/sysEmployee/updateUserPhoneNumber");  //根据手机号更新openId
//        excludePath.add("/sysEmployee/getPhoneLogin"); //获取手机号，考虑到安全性，增加了token验证（社保巡检使用、物资仓库暂时没有使用）
//        excludePath.add("/**");
//        excludePath.add("/swagger-resources/**");
//        excludePath.add("/doc.html");
//        excludePath.add("/webjars/**");
//        excludePath.add("/v2/**");
//        excludePath.add("/swagger-ui.html/**");
        List<String> allExcludePath = new ArrayList<>(excludePath);
        allExcludePath.add("/swagger-resources/**");
        allExcludePath.add("/doc.html");
        allExcludePath.add("/webjars/**");
        allExcludePath.add("/v2/**");
        allExcludePath.add("/swagger-ui.html/**");

        registry.addInterceptor((HandlerInterceptor) tokenInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(allExcludePath);
        //除了登陆接口其他所有接口都需要token验证
        WebMvcConfigurer.super.addInterceptors(registry);

    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

}
