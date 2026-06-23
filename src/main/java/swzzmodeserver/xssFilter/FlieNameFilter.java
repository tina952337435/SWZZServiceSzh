package swzzmodeserver.xssFilter;

import org.junit.platform.commons.logging.LoggerFactory;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.logging.Filter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public abstract class FlieNameFilter implements Filter {

//    private static final Logger log = (Logger) LoggerFactory.getLogger(FlieNameFilter.class);
    private ResourceBundle bundle;

    public void destroy() {
        bundle = null;
    }

    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // 获得所有请求参数名
        Enumeration<String> params = req.getParameterNames();
        StringBuffer content = new StringBuffer("");
        while (params.hasMoreElements()) {
            // 得到参数名
            String name = params.nextElement().toString();

            String value = req.getParameter(name);

            content.append(value);
        }

        String sqlInjectListStr = bundle.getString("fileNameListStr");

        String message = fileNameValidate(content.toString(), sqlInjectListStr);

        if (message != null) {
            req.getSession().setAttribute("overpowerMessage", message);
//            log.error("sqlFilter============================>>error!! find fileName inject.");
//            log.error("sqlFilter============================>>传入的参数字符串："+ content.toString());
            res.sendRedirect(req.getContextPath() + "/overpower.jsp");
        } else {
            chain.doFilter(request, response);
        }
    }

    private String fileNameValidate(String str, String sqlInjectListStr) {


        if(null!=sqlInjectListStr && !"".equals(sqlInjectListStr))
        {
            str = str.toLowerCase();// 统一转为小写

//            log.info("sqlFilter===========================>>路径遍历过滤规则:"+ sqlInjectListStr);

            String[] badStrs = sqlInjectListStr.split("\\|");
            for (int i = 0; i < badStrs.length; i++) {
                if (str.indexOf(badStrs[i]) >= 0) {
                    return badStrs[i];
                }
            }
        }
        return null;
    }

    public void init(FilterConfig filterConfig) throws ServletException {
        try {
            bundle = new PropertyResourceBundle(this.getClass().getClassLoader().getResourceAsStream("parameters.properties"));
        } catch (IOException e) {
//            log.error("parameters.properties配置文件夹在失败");
        }
    }

}
