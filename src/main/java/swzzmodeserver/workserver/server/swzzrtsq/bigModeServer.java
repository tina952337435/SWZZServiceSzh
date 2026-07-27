package swzzmodeserver.workserver.server.swzzrtsq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import swzzmodeserver.tools.CacheHelper;
import swzzmodeserver.tools.CacheHelperUp;
import swzzmodeserver.tools.DataTransformUtil;
import swzzmodeserver.tools.apihelper;
import swzzmodeserver.tools.javalog;
import swzzmodeserver.workserver.pojo.swzzrtsq.BigModeGCResponse;
import swzzmodeserver.workserver.pojo.swzzrtsq.BigModeGCResponse.DataItem;
import swzzmodeserver.workserver.pojo.swzzrtsq.BigModeGCResponse.DataPoint;
import swzzmodeserver.workserver.pojo.swzzrtsq.ChuLaoBengZhaResponse;
import swzzmodeserver.workserver.pojo.swzzrtsq.ChuLaoBengZhaResponse.GateWasData;
import swzzmodeserver.workserver.pojo.swzzrtsq.SZBZGKResponse;
import swzzmodeserver.workserver.pojo.swzzrtsq.ST_GATE_RPojo;
import swzzmodeserver.workserver.pojo.swzzrtsq.ST_WAS_RPojo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class bigModeServer {
    @Value("${http.urlPath.bigModelApi}")
    private String bigModelApi;

    @Value("${http.urlPath.bigModelApiToken}")
    private String bigModelApiToken;

    //服务ID
    @Value("${http.urlPath.bigModelApiServiceId}")
    private String bigModelApiServiceId;

    // 项目ID
    @Value("${http.urlPath.bigModelApiProjectId}")
    private String bigModelApiProjectId;

    @Autowired
    private DataTransformUtil transformUtil;


    @Value("${file.path.templatefilepath}")
    private String filePathName;

    CacheHelperUp<String, String> cache = new CacheHelperUp<>();


    //Sfq 是单个泵的流量
    public List<DataItem> getBigModeFactorQuery(String startTime, String endTime, List<String> factorItemIds) {
        List<DataItem> resultList=new ArrayList<>();
        try {
            HashMap<String, Object> header=new HashMap<>();
            header.put("Content-Type","application/json;charset=UTF-8");
            header.put("token",bigModelApiToken);
            String url = bigModelApi + "/ds/" + bigModelApiServiceId + "/data-service/factor/data-query";

            JSONObject body = new JSONObject();
            body.put("startTime", startTime);
            body.put("endTime", endTime);
            body.put("projectId", bigModelApiProjectId);
            body.put("factorItemIds", factorItemIds);

            new   javalog().writelog("【大模型预报】请求接口地址：" + url, filePathName, "SWZZServiceBigMode");
            try {
                String jsonResult = apihelper.apipost(url, body.toJSONString(), header);                
                new   javalog().writelog("【大模型预报】请求接口结果jsonResult："+jsonResult,filePathName,"SWZZServiceBigMode");
                if (jsonResult != null && !jsonResult.isEmpty()) {
                    BigModeGCResponse response = JSON.parseObject(jsonResult, BigModeGCResponse.class);
                    new   javalog().writelog("【大模型预报】请求接口结果code："+response.getCode(),filePathName,"SWZZServiceBigMode");
                    if (response != null && response.getCode() == 200 && response.getData() != null) {
                        resultList = response.getData();//.get(0).getDataPoints();
                    }
                }
            } catch (Exception e) {
                new   javalog().writelog("【大模型预报】请求接口地址："+url+"报错："+e.getMessage(),filePathName,"SWZZServiceBigMode");
            }
        } catch (Exception e) {
            new   javalog().writelog("getChuLaoBengZha调用报错："+e.getMessage(),filePathName,"SWZZServiceBigMode");
        }
        return resultList;
    }
}
