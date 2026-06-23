package swzzmodeserver.workserver.service.swzzmode;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import swzzmodeserver.tools.ObjUtils;
import swzzmodeserver.tools.apihelper;
import swzzmodeserver.tools.javalog;
import swzzmodeserver.workserver.data.swzzmode.ES_ZHANDIANDATAData;
import swzzmodeserver.workserver.pojo.Huishui.GetSubjectListPojo;
import swzzmodeserver.workserver.pojo.swzzmode.BDMS_PREDICTPojo;
import swzzmodeserver.workserver.pojo.swzzmode.DD_SOLUTIONPojo;
import swzzmodeserver.workserver.pojo.swzzmode.ES_ZHANDIANDATAPojo;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class swicApiService {

    @Value("${http.urlPath.swicclient_url}")
    private String swicApi;

    @Value("${http.urlPath.swicclient_id}")
    private String client_id;

    @Value("${http.urlPath.swicclient_secret}")
    private String client_secret;
    //获取token
    public String getSwicToken() throws Exception {
        HashMap<String, Object> header=new HashMap<>();
        header.put("Content-Type","application/json;charset=UTF-8");
        String url=swicApi+"/oauth/token?client_id="+client_id+"&client_secret="+client_secret;
        String result= apihelper.apigethttps(url,header);
        String access_token="";
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
    //获取最新一次预报
    public List<Map<String, Object>> getSwicSQYBCG_XY(int pageSize,int pageNumber){
        List<Map<String, Object>> resultList=new ArrayList<>();
        try {
            String access_token=getSwicToken();
            HashMap<String, Object> header=new HashMap<>();
            header.put("Content-Type","application/json;charset=UTF-8");
            String url=swicApi+"service/api/swic/getSQYBCG_XY?client_id="+client_id+"&access_token="+access_token+"&pageSize="+pageSize+"&pageNumber="+pageNumber;
            String result= apihelper.apigethttps(url,header);
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String,Object> mapList=new HashMap<>();
            mapList = objectMapper.readValue(result, new TypeReference<Map<String, Object>>() {});
            int totalRows=(int)mapList.get("totalRows");
            if(totalRows>0){
                resultList = (List<Map<String, Object>>) mapList.get("result");
            }
        } catch (Exception e) {
            System.out.println("getSwicSQYBCG_XY调用报错");
        }
        return resultList;
    }
    //单站预报过程
    public List<Map<String, Object>> getSwicSQYBCG(String STATIONID,String STATIONNAME,String STARTTIME,String ENDTIME,String YIJUTIME){
        List<Map<String, Object>> resultList=new ArrayList<>();
        try {
            HashMap<String, Object> header=new HashMap<>();
            header.put("Content-Type","application/json;charset=UTF-8");
            String access_token=getSwicToken();
            String url=swicApi+"service/api/swic/getSQYBCG?client_id="+client_id+"&access_token="+access_token;
            url+="&STATIONID="+STATIONID;
            url+="&STATIONNAME="+STATIONNAME;
            url+="&STARTTIME="+URLEncoder.encode(STARTTIME, "UTF-8");
            url+="&ENDTIME="+URLEncoder.encode(ENDTIME, "UTF-8");
            url+="&YIJUTIME="+ URLEncoder.encode(YIJUTIME, "UTF-8");
            System.out.println("上游边界过程接口路径："+url);
            String result= apihelper.apigethttps(url,header);

            ObjectMapper objectMapper = new ObjectMapper();
            resultList=objectMapper.readValue(result,
                    new TypeReference<List<Map<String, Object>>>(){});
        } catch (Exception e) {
            System.out.println("getSwicSQYBCG调用报错："+e.getMessage());
        }
        return resultList;
    }
}
