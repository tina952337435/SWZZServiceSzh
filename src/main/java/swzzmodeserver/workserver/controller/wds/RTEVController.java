package swzzmodeserver.workserver.controller.wds;

import swzzmodeserver.tools.ColumnName;
import swzzmodeserver.tools.CommonUtills;
import swzzmodeserver.tools.DateUtil;
import swzzmodeserver.tools.javalog;
import swzzmodeserver.workserver.data.wds.RTEVData;
import swzzmodeserver.workserver.pojo.wds.RTEVPojo;
import swzzmodeserver.workserver.pojo.swzzflood.ST_WAS_RPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/WDS_RTEV")
public class RTEVController {
    @Autowired
    private RTEVData data;

    @Value("${file.path.templatefilepath}")
    private String templatefilepath;

    @Autowired
    private CommonUtills commonUtills;

    @RequestMapping("/findResult")
    public Map<String, Object> findResult(@RequestBody ColumnName param){

        Date date = new Date(new Date().getTime() - 24 * 60 * 60 * 1000);
        String stime= DateUtil.dateFormat(date,"yyyy-MM-dd 08:00:00"),etime="";
        List<String> stcdList = new ArrayList<>();
        if(null != param.getStcd()){
            stcdList = Arrays.asList(param.getStcd().split(","));
        }
        if(null != param.getStime()){
            stime = param.getStime();
        }
        if(null != param.getEtime()){
            etime = param.getEtime();
        }
        List<RTEVPojo> rtevList = data.ga(stime, etime, stcdList);
        if(null != rtevList){
            return commonUtills.returnJson("select",rtevList.size(),rtevList);
        }else {
            return commonUtills.returnJson("select", -1, rtevList);
        }
    }

    @RequestMapping("/GetWaterData")
    public Map<String, Object> GetWaterData(@RequestBody ColumnName param){
        new   javalog().writelog("进入主服务GetWaterData接口",templatefilepath,"GetWaterData");
        List<String> idList = new ArrayList<>();
        String stime = "";
        if(null != param.getStcd()){
            idList = Arrays.asList(param.getStcd().split(","));
        }
        if(null != param.getStime()){
            stime = param.getStime();
        }
        
        new   javalog().writelog("GetWaterData接口参数stime："+stime+",stcd："+param.getStcd(),templatefilepath,"GetWaterData");
        List<ST_WAS_RPojo> waterList =new ArrayList<>();
        try {            
            waterList=data.GetWaterData(idList, stime);
        } catch (Exception e) {
            new   javalog().writelog("GetWaterData接口报错："+e.getMessage(),templatefilepath,"GetWaterData");
        }
        if(null != waterList){
            return commonUtills.returnJson("select",waterList.size(),waterList);
        }else {
            return commonUtills.returnJson("select", -1, waterList);
        }
    }
}