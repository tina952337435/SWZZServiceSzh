package swzzmodeserver.workserver.server.swzzrtsq;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import swzzmodeserver.tools.CacheHelperUp;
import swzzmodeserver.tools.apihelper;
import swzzmodeserver.tools.javalog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class shuiwupingServer {
    @Value("${http.urlPath.shuiwupingtaiApi}")
    private String shuiwupingtaiApi;
    @Value("${http.urlPath.shuiwupingtaiclient_id}")
    private String client_id;

    @Value("${http.urlPath.shuiwupingtaiclient_secret}")
    private String client_secret;

    @Value("${file.path.templatefilepath}")
    private String filePathName;

    CacheHelperUp<String, String> cache = new CacheHelperUp<>();
    public String getSwptToken() throws Exception {        
        // 1. 尝试根据 key 获取 Token
        String access_token=cache.get("shuiwupingtaiToken");
        // 2. 判断是否为 null
        if (access_token  != null) {//不为空，取缓存里面的
            return access_token;
        }
        HashMap<String, Object> header=new HashMap<>();
        header.put("Content-Type","application/json;charset=UTF-8");
        String url=shuiwupingtaiApi+"/oauth/token?client_id="+client_id+"&client_secret="+client_secret;
        String result= apihelper.apigethttps(url,header);
        
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String,Object> mapList=new HashMap<>();
            mapList = objectMapper.readValue(result, new TypeReference<Map<String, Object>>() {});
            access_token=(String) mapList.get("access_token");
        } catch (IOException e) {
            System.out.println("调用"+url+"接口报错,接口返回结果是："+result);
        }
        return access_token;
    }


    public List<Map<String, Object>> getShiShiShuiWei(int pageSize, int pageNumber,String stationid,String startTime,String endTime){
        List<Map<String, Object>> resultList=new ArrayList<>();
        try {
            String access_token=getSwptToken();
            HashMap<String, Object> header=new HashMap<>();
            header.put("Content-Type","application/json;charset=UTF-8");
            String url = UriComponentsBuilder.fromHttpUrl(shuiwupingtaiApi + "/service/api/swic/getShiShiShuiWei")
                    .queryParam("client_id", client_id)
                    .queryParam("access_token", access_token)
                    .queryParam("pageSize", pageSize)
                    .queryParam("pageNumber", pageNumber)
                    .queryParam("STARTTIME", startTime) // 直接传原始字符串，它会自动编码
                    .queryParam("ENDTIME", endTime)
                    .queryParam("STATIONID", stationid)
                    .toUriString();
            new   javalog().writelog("【外省市水位】请求接口地址："+url,filePathName,"SyncRealDataSWPT"); 

            String result= apihelper.apigethttps(url,header);
            new   javalog().writelog("【外省市水位】请求接口结果："+result,filePathName,"SyncRealDataSWPT"); 
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String,Object> mapList=new HashMap<>();
            mapList = objectMapper.readValue(result, new TypeReference<Map<String, Object>>() {});
            int totalRows=(int)mapList.get("totalRows");
            if(totalRows>0){
                resultList = (List<Map<String, Object>>) mapList.get("result");
            }
        } catch (Exception e) {
            new javalog().writelog("getShiShiShuiWei调用报错："+e.getMessage(),filePathName,"SyncRealDataSWPT");
        }
        return resultList;
    }

    public List<Map<String, Object>> getLiuLiang(int pageSize, int pageNumber,String stationid,String startTime,String endTime){
        List<Map<String, Object>> resultList=new ArrayList<>();
        try {
            String access_token=getSwptToken();
            HashMap<String, Object> header=new HashMap<>();
            header.put("Content-Type","application/json;charset=UTF-8");
            
            String url = UriComponentsBuilder.fromHttpUrl(shuiwupingtaiApi + "/service/api/swic/getLiuLiang")
                    .queryParam("client_id", client_id)
                    .queryParam("access_token", access_token)
                    .queryParam("pageSize", pageSize)
                    .queryParam("pageNumber", pageNumber)
                    .queryParam("STARTTIME", startTime) // 直接传原始字符串，它会自动编码
                    .queryParam("ENDTIME", endTime)
                    .queryParam("STATIONID", stationid)
                    .toUriString();
            new   javalog().writelog("【外省市流量】请求接口地址："+url,filePathName,"SyncRealDataLLPT"); 
            String result= apihelper.apigethttps(url,header);
            
            new   javalog().writelog("【外省市流量】请求接口结果："+result,filePathName,"SyncRealDataLLPT"); 
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                // 使用 objectMapper.readValue 进行解析
               resultList = objectMapper.readValue(result, new TypeReference<List<Map<String, Object>>>() {});
                // 你的后续逻辑...
            } catch (Exception e) {
                // System.out.println("getLiuLiang调用报错");
                new javalog().writelog("1、getLiuLiang调用报错："+e.getMessage(),filePathName,"SyncRealDataLLPT");
            }

        } catch (Exception e) {
            // System.out.println("getLiuLiang调用报错");            
            new javalog().writelog("2、getLiuLiang调用报错："+e.getMessage(),filePathName,"SyncRealDataLLPT");
        }
        return resultList;
    }

    //气象预警
    public List<Map<String, Object>> getQXYJ(String startTime,String endTime){
        List<Map<String, Object>> resultList=new ArrayList<>();
        try {
            String access_token=getSwptToken();
            HashMap<String, Object> header=new HashMap<>();
            header.put("Content-Type","application/json;charset=UTF-8");
            
            String url = UriComponentsBuilder.fromHttpUrl(shuiwupingtaiApi + "/service/api/swic/getQXYJ")
                    .queryParam("client_id", client_id)
                    .queryParam("access_token", access_token)
                    .queryParam("STARTTIME", startTime) // 直接传原始字符串，它会自动编码
                    .queryParam("ENDTIME", endTime)
                    .toUriString();
            new   javalog().writelog("【气象预警】请求接口地址："+url,filePathName,"SyncRealDataQXYJ"); 
            String result= apihelper.apigethttps(url,header);
            
            new   javalog().writelog("【气象预警】请求接口结果："+result,filePathName,"SyncRealDataQXYJ"); 
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                // 使用 objectMapper.readValue 进行解析
               resultList = objectMapper.readValue(result, new TypeReference<List<Map<String, Object>>>() {});
                // 你的后续逻辑...
            } catch (Exception e) {
                // System.out.println("getLiuLiang调用报错");
                new javalog().writelog("1、getQXYJ调用报错："+e.getMessage(),filePathName,"SyncRealDataQXYJ");
            }

        } catch (Exception e) {
            // System.out.println("getLiuLiang调用报错");            
            new javalog().writelog("2、getQXYJ调用报错："+e.getMessage(),filePathName,"SyncRealDataQXYJ");
        }
        return resultList;
    }

    // 潮位预警
    public List<Map<String, Object>> getChaoWeiYuJing(){
        List<Map<String, Object>> resultList=new ArrayList<>();
        try {
            String access_token=getSwptToken();
            HashMap<String, Object> header=new HashMap<>();
            header.put("Content-Type","application/json;charset=UTF-8");
            
            String url = UriComponentsBuilder.fromHttpUrl(shuiwupingtaiApi + "/service/api/swic/getChaoWeiYuJing")
                    .queryParam("client_id", client_id)
                    .queryParam("access_token", access_token)
                    .toUriString();
            new   javalog().writelog("【气象预警】请求接口地址："+url,filePathName,"SyncRealDataCWYJ"); 
            String result= apihelper.apigethttps(url,header);
            
            new   javalog().writelog("【气象预警】请求接口结果："+result,filePathName,"SyncRealDataCWYJ"); 
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                // 使用 objectMapper.readValue 进行解析
               resultList = objectMapper.readValue(result, new TypeReference<List<Map<String, Object>>>() {});
                // 你的后续逻辑...
            } catch (Exception e) {
                // System.out.println("getLiuLiang调用报错");
                new javalog().writelog("1、getQXYJ调用报错："+e.getMessage(),filePathName,"SyncRealDataCWYJ");
            }

        } catch (Exception e) {
            // System.out.println("getLiuLiang调用报错");            
            new javalog().writelog("2、getQXYJ调用报错："+e.getMessage(),filePathName,"SyncRealDataCWYJ");
        }
        return resultList;
    }

    // 当前市级响应
    public List<Map<String, Object>> getSJYJXY_CURRENT(){
        List<Map<String, Object>> resultList=new ArrayList<>();
        try {
            String access_token=getSwptToken();
            HashMap<String, Object> header=new HashMap<>();
            header.put("Content-Type","application/json;charset=UTF-8");            
            String url = UriComponentsBuilder.fromHttpUrl(shuiwupingtaiApi + "/service/api/swic/getSJYJXY_CURRENT")
                    .queryParam("client_id", client_id)
                    .queryParam("access_token", access_token)
                    .toUriString();
            String result= apihelper.apigethttps(url,header);
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                // 使用 objectMapper.readValue 进行解析
               resultList = objectMapper.readValue(result, new TypeReference<List<Map<String, Object>>>() {});
                // 你的后续逻辑...
            } catch (Exception e) {
            }

        } catch (Exception e) {
        }
        return resultList;
    }

    //历史应急响应
    public List<Map<String, Object>> getSJYJXY(int pageSize, int pageNumber,String startTime,String endTime){
        List<Map<String, Object>> resultList=new ArrayList<>();
        try {
            String access_token=getSwptToken();
            HashMap<String, Object> header=new HashMap<>();
            header.put("Content-Type","application/json;charset=UTF-8");            
            String url = UriComponentsBuilder.fromHttpUrl(shuiwupingtaiApi + "/service/api/swic/getSJYJXY")
                    .queryParam("client_id", client_id)
                    .queryParam("access_token", access_token)
                    .queryParam("pageSize", pageSize)
                    .queryParam("pageNumber", pageNumber)
                    .queryParam("STARTTIME", startTime) // 直接传原始字符串，它会自动编码
                    .queryParam("ENDTIME", endTime)
                    .toUriString();
            String result= apihelper.apigethttps(url,header);
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String,Object> mapList=new HashMap<>();
                mapList = objectMapper.readValue(result, new TypeReference<Map<String, Object>>() {});
                int totalRows=(int)mapList.get("totalRows");
                if(totalRows>0){
                    resultList = (List<Map<String, Object>>) mapList.get("result");
                }
            } catch (Exception e) {
            }
        } catch (Exception e) {
        }
        return resultList;
    }


    //天文潮位
    public List<Map<String, Object>> getTianWenChaoWei(String stationid,String startTime,String endTime){
        List<Map<String, Object>> resultList=new ArrayList<>();
        try {
            String access_token=getSwptToken();
            HashMap<String, Object> header=new HashMap<>();
            header.put("Content-Type","application/json;charset=UTF-8");
            String url = UriComponentsBuilder.fromHttpUrl(shuiwupingtaiApi + "/service/api/swic/getTianWenChaoWei")
                    .queryParam("client_id", client_id)
                    .queryParam("access_token", access_token)
                    .queryParam("STARTTIME", startTime) // 直接传原始字符串，它会自动编码
                    .queryParam("ENDTIME", endTime)
                    .queryParam("STATIONID", stationid)
                    .toUriString();
            new   javalog().writelog("【天文潮位】请求接口地址："+url,filePathName,"SyncRealDataSWPT"); 
            String result= apihelper.apigethttps(url,header);        
            ObjectMapper objectMapper = new ObjectMapper();
            resultList = objectMapper.readValue(result, new TypeReference<List<Map<String, Object>>>() {});
            new   javalog().writelog("【天文潮位】请求接口结果："+resultList.size(),filePathName,"SyncRealDataSWPT"); 
        } catch (Exception e) {
            new javalog().writelog("getTianWenChaoWei调用报错："+e.getMessage(),filePathName,"SyncRealDataSWPT");
        }
        return resultList;
    }

}