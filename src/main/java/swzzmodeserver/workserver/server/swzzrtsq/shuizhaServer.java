package swzzmodeserver.workserver.server.swzzrtsq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import swzzmodeserver.tools.CacheHelper;
import swzzmodeserver.tools.CacheHelperUp;
import swzzmodeserver.tools.DataTransformUtil;
import swzzmodeserver.tools.apihelper;
import swzzmodeserver.tools.javalog;
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
public class shuizhaServer {
    @Value("${http.urlPath.shuizhaApi}")
    private String shuizhaApi;
    @Value("${http.urlPath.shuizhaclientId}")
    private String clientId;

    @Value("${http.urlPath.shuizhaclientSecret}")
    private String clientSecret;

    @Autowired
    private DataTransformUtil transformUtil;

    
    @Value("${file.path.templatefilepath}")
    private String filePathName;

    CacheHelperUp<String, String> cache = new CacheHelperUp<>();

    public String getShuiZhaToken() throws Exception {
        // 1. 尝试根据 key 获取 Token
        String access_token = cache.get("ShuiZhaToken");
        // 2. 判断是否为 null
        if (access_token  != null) {//不为空，取缓存里面的
            return access_token;
        }
        new   javalog().writelog("进入水闸getShuiZhaToken接口***********************",filePathName,"SWZZServiceGate");
        HashMap<String, Object> header=new HashMap<>();
        header.put("Content-Type","application/json;charset=UTF-8");
        String parmasMap = "{\"clientId\":\""+clientId+"\",\"clientSecret\":\""+clientSecret+"\"}";
        String url=shuizhaApi+"token?clientId="+clientId+"&clientSecret="+clientSecret;
        String result= apihelper.apipost(url,parmasMap,header);
        
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            // 1. 一次性解析整个 JSON 结构
            Map<String, Object> resultMap = objectMapper.readValue(result, new TypeReference<Map<String, Object>>(){});

            // 2. 从 map 中获取 data 对象，并强制转换为 Map
            Map<String, Object> dataMap = (Map<String, Object>) resultMap.get("data");

            // 3. 直接从 dataMap 中获取 accessToken
            access_token = (String) dataMap.get("accessToken");
            new   javalog().writelog("水闸getShuiZhaToken接口access_token的值："+access_token,filePathName,"SWZZServiceGate");
        } catch (IOException e) {
            System.out.println("调用"+url+"接口报错,接口返回结果是："+result);
        }
        return access_token;
    }


    //Sfq 是单个泵的流量
    public GateWasData getChuLaoBengZha(int pageSize, int pageNumber,String stationid,String startTime,String endTime,double Sfq){
        GateWasData item=new GateWasData();
        List<ST_GATE_RPojo> dbRecords=new ArrayList<>();
        List<ST_WAS_RPojo> dbRecordsWas=new ArrayList<>();
        List<ChuLaoBengZhaResponse.ResultItem> resultList=new ArrayList<>();
        try {
            String accessToken=getShuiZhaToken();
            HashMap<String, Object> header=new HashMap<>();
            header.put("Content-Type","application/json;charset=UTF-8");
            header.put("clientId",clientId);
            header.put("accessToken",accessToken);

            String url = UriComponentsBuilder.fromHttpUrl(shuizhaApi + "/getChuLaoBengZha")
//                    .queryParam("clientId", clientId)
//                    .queryParam("accessToken", accessToken)
                    .queryParam("pageSize", pageSize)
                    .queryParam("pageNumber", pageNumber)
                    .queryParam("STARTTIME", startTime) // 直接传原始字符串，它会自动编码
                    .queryParam("ENDTIME", endTime)
                    .queryParam("STATIONID", stationid)
                    .toUriString();
            // System.out.println("【水闸】请求接口地址："+url);
            new   javalog().writelog("【水闸】请求接口地址："+url,filePathName,"SWZZServiceGate");
            try {
                String jsonResult= apihelper.apigethttps(url,header);                
                // new   javalog().writelog("【水闸】请求接口结果jsonResult："+jsonResult,filePathName,"SWZZServiceGate");
                if (jsonResult != null && !jsonResult.isEmpty()) {
                    ChuLaoBengZhaResponse response = JSON.parseObject(jsonResult, ChuLaoBengZhaResponse.class);
                    new   javalog().writelog("【水闸】请求接口结果code："+response.getCode(),filePathName,"SWZZServiceGate");
                    if (response != null && response.getCode() == 200 && response.getData() != null) {
                        resultList = response.getData().getResult();
                        if (resultList != null && !resultList.isEmpty()) {
                            // 2. 数据转换
                            // dbRecords = transformUtil.transformToGateR(resultList,Sfq);
                            item=transformUtil.transformToGateR(resultList,Sfq);

                            new   javalog().writelog("【水闸】请求接口结果："+item.getGateDate().size()+"::::::::::::::::"+item.getWasDate().size(),filePathName,"SWZZServiceGate");
                        }
                    }
                }
            } catch (Exception e) {
//                e.printStackTrace();
                System.err.println(">>> 任务执行异常: " + e.getMessage());
                new   javalog().writelog("【水闸】请求接口地址："+url+"报错："+e.getMessage(),filePathName,"SWZZServiceGate");
            }
        } catch (Exception e) {
            // System.out.println("getChuLaoBengZha调用报错");
            new   javalog().writelog("getChuLaoBengZha调用报错："+e.getMessage(),filePathName,"SWZZServiceGate");
        }
        // return dbRecords;
        return item;
    }

    public SZBZGKResponse getSZBZGK(int pageSize, int pageNumber, String stationid, String startTime, String endTime) {
        SZBZGKResponse response = null;
        try {
            new   javalog().writelog("调用防汛泵站接口getSZBZGK_x",filePathName,"SWZZServiceBeng");
            String accessToken = getShuiZhaToken();
            HashMap<String, Object> header = new HashMap<>();
            header.put("Content-Type", "application/json;charset=UTF-8");
            header.put("clientId", clientId);
            header.put("accessToken", accessToken);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(shuizhaApi + "/getSZBZGK")
                    .queryParam("pageSize", pageSize)
                    .queryParam("pageNumber", pageNumber);
            if (stationid != null && !stationid.isEmpty()) {
                builder.queryParam("STATIONID", stationid);
            }
            if (startTime != null && !startTime.isEmpty()) {
                builder.queryParam("STARTTIME", startTime);
            }
            if (endTime != null && !endTime.isEmpty()) {
                builder.queryParam("ENDTIME", endTime);
            }
            String url = builder.toUriString();
            // System.out.println("【市政泵站】请求接口地址：" + url);            
            new   javalog().writelog("【市政泵站】请求接口地址：" + url,filePathName,"SWZZServiceBeng");
            String jsonResult = apihelper.apigethttps(url, header);            
            try {
                if (jsonResult != null && !jsonResult.isEmpty()) {
                  response = JSON.parseObject(jsonResult, SZBZGKResponse.class);
                }
            } catch (Exception e) {
                new   javalog().writelog("【市政泵站】"+stationid+"请求接口地址："+url+"，返回结果：" + jsonResult,filePathName,"SWZZServiceBengError");
            }
            
        } catch (Exception e) {
            // System.err.println("getSZBZGK_xy调用报错: " + e.getMessage());
            new   javalog().writelog("getSZBZGK_xy调用报错: " + e.getMessage(),filePathName,"SWZZServiceBeng");
        }
        return response;
    }
}
