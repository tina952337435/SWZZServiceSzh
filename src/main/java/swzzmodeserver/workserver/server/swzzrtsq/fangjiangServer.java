package swzzmodeserver.workserver.server.swzzrtsq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import swzzmodeserver.tools.DataTransformUtil;
import swzzmodeserver.tools.apihelper;
import swzzmodeserver.tools.javalog;
import swzzmodeserver.workserver.pojo.swzzrtsq.ChuLaoBengZhaResponse;
import swzzmodeserver.workserver.pojo.swzzrtsq.ChuLaoBengZhaResponse.GateWasData;
import swzzmodeserver.workserver.pojo.swzzrtsq.PUMPSTATIONResponse;
import swzzmodeserver.workserver.pojo.swzzrtsq.SZBZGKResponse;
import swzzmodeserver.workserver.pojo.swzzrtsq.ST_GATE_RPojo;
import swzzmodeserver.workserver.pojo.swzzrtsq.ST_WAS_RPojo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class fangjiangServer {
    @Value("${http.urlPath.fangjiangApi}")
    private String fangjiangApi;
    @Value("${http.urlPath.fangjiangToken}")
    private String fangjiangToken;

    @Value("${http.urlPath.fangjiangclient_id}")
    private String client_id;

    @Value("${file.path.templatefilepath}")
    private String filePathName;

    //2.泵站未来 24 小时放江量预测接口
    public PUMPSTATIONResponse getFangjiangOverflow(String ps_id, String time) {
        PUMPSTATIONResponse response = null;
        try {
            new   javalog().writelog("开始调用站点"+ps_id+"预测放江量接口",filePathName,"SWZZServiceFangjiang");
            String accessToken = fangjiangToken;
            HashMap<String, Object> header = new HashMap<>();
            header.put("Content-Type", "application/json;charset=UTF-8");
            header.put("Authorization", accessToken);
            
            String parmasMap = "{\"client_id\":\""+client_id+"\",\"ps_id\":\""+ps_id+"\",\"time\":\""+time+"\"}";
            
            String url = fangjiangApi + "overflow";   
              
            new   javalog().writelog("【放江模型】请求接口地址：" + url,filePathName,"SWZZServiceFangjiang");
            String jsonResult = apihelper.apipost(url,parmasMap, header);
            new   javalog().writelog("调用放江模型接口返回内容jsonResult: " + jsonResult,filePathName,"SWZZServiceFangjiang");
            if (jsonResult != null && !jsonResult.isEmpty()) {
                response = JSON.parseObject(jsonResult, PUMPSTATIONResponse.class);
                new   javalog().writelog("调用放江模型接口返回内容response: " + response,filePathName,"SWZZServiceFangjiang");
            }
        } catch (Exception e) {
            new   javalog().writelog("调用放江模型接口报错: " + e.getMessage(),filePathName,"SWZZServiceFangjiang");
        }
        return response;
    }
}
