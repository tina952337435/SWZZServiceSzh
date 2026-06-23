package swzzmodeserver.xssFilter;

import java.util.Map;
import javax.servlet.DispatcherType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import com.google.common.collect.Maps;

/**
 * Filter配置
 *
 * @author xxx
 */
@Configuration
public class FilterConfig
{
    @Value("${xss.enabled}")
    private String enabled;

    @Value("${xss.excludes}")
    private String excludes;

    @Value("${xss.urlPatterns}")
    private String urlPatterns;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Bean
    public FilterRegistrationBean xssFilterRegistration()
    {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        //
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        //过滤器类（继承Filter）
        registration.setFilter(new XssFilter());
        //
        registration.addUrlPatterns(StringUtils.split(urlPatterns, ","));
        //
        registration.setName("xssFilter");
        //
        registration.setOrder(Integer.MAX_VALUE);
        Map<String, String> initParameters = Maps.newHashMap();
        initParameters.put("excludes", excludes);
        initParameters.put("enabled", enabled);
        //Filter 初始化参数
        registration.setInitParameters(initParameters);
        return registration;
    }
}

