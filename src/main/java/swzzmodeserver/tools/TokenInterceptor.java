package swzzmodeserver.tools;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;

@Component
public class TokenInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if ("OPTIONS".equals(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }
        response.setCharacterEncoding("utf-8");
        String token = request.getHeader("Authorization");

        String uri = request.getRequestURI();
        System.out.println("请求URI: " + uri);        
        if (uri.contains("GetToken")) {
            System.out.println("绕过GetToken");
            return true;
        }

//        System.out.println(token);
        if(token == null){
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            PrintWriter out = null;
            try {
                JSONObject json = new JSONObject();
                json.put("success", "false");
                json.put("msg", "您还未登录或身份验证失败，请重新登录！");
                json.put("code", "-401");
                response.getWriter().append(json.toJSONString());
                System.out.println("token认证失败");
            } catch (Exception e) {
                e.printStackTrace();
                response.sendError(500);
                return false;
            }
            return false;
        }
        else if(JwtUtil.verify(token)){

        }
        else if(token == null || !JwtUtil.checkToken(token)){
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            PrintWriter out = null;
            try {
                JSONObject json = new JSONObject();
                json.put("success", "false");
                json.put("msg", "您还未登录或身份验证失败，请重新登录！");
                json.put("code", "-401");
                response.getWriter().append(json.toJSONString());
                System.out.println("token认证失败");
            } catch (Exception e) {
                e.printStackTrace();
                response.sendError(500);
                return false;
            }
            return false;
        }

        return HandlerInterceptor.super.preHandle(request, response, handler);

//        if(token != null ){
//            boolean result = JwtUtil.verify(token);
//            if (result) {
////                System.out.println("通过拦截器");
//                return true;
//            }
//        }
//        response.setCharacterEncoding("UTF-8");
//        response.setContentType("application/json; charset=utf-8");
//        PrintWriter out = null;
//        try {
//            JSONObject json = new JSONObject();
//            json.put("success", "false");
//            json.put("msg", "认证失败，未通过拦截器");
//            json.put("code", "500");
//            response.getWriter().append(json.toJSONString());
//            System.out.println("认证失败，未通过拦截器");
//        } catch (Exception e) {
//            e.printStackTrace();
//            response.sendError(500);
//            return false;
//        }
//        return false;

    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
//        System.out.println("拦截器后置处理 postHandle");

        // 从HttpServletRequest获取相关信息
        String ip = request.getRemoteAddr();
        if (ip.equals("0:0:0:0:0:0:0:1"))   ip = "127.0.0.1";
        String browser = request.getHeader("Sec-Ch-Ua-Platform");
        String httpMethod = request.getMethod();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String time = sdf.format(System.currentTimeMillis());

        // 日志存档
        //  Record record = new Record(null, ip, httpMethod, browser, time);
        // 访问日志记录逻辑

//        // 从token中获取相关信息
//        String token = request.getHeader("Authorization");
////        System.out.println(" token = " + token);
//        String userName = JwtUtil.getTokenInfo(token, "userName");
//        String userId = JwtUtil.getTokenInfo(token, "userId");
//
//        System.out.println(String.format("访问用户：{%s}-{%s}，访问IP：{%s}，访问时间：{%s}，请求方式：{%s}，访问设备：{%s}",userName, userId, ip, time, httpMethod, browser));

        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
//        System.out.println("拦截器完成后 afterCompletion");
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
