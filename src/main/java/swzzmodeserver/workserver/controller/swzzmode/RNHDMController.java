package swzzmodeserver.workserver.controller.swzzmode;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swzzmodeserver.tools.*;
import swzzmodeserver.workserver.data.swzzmode.*;
import swzzmodeserver.workserver.pojo.Huishui.GetAreaXSLPojo;
import swzzmodeserver.workserver.pojo.swzzflood.ST_WAS_RPojo;
import swzzmodeserver.workserver.service.swzzmode.ES_ZHANDIANDATAServiceImpl;
import swzzmodeserver.workserver.pojo.Huishui.GetPlansRiverHPJPojo;
import swzzmodeserver.workserver.pojo.swzzdata.EmployeeGetTokenPojo;
import swzzmodeserver.workserver.pojo.swzzmode.*;
import swzzmodeserver.workserver.pojo.swzzrtsq.ST_STBPRP_B_QUPojo;
import swzzmodeserver.workserver.service.swzzmode.HuishuiApiService;
import swzzmodeserver.workserver.service.swzzmode.jupingtaiModeService;
import swzzmodeserver.workserver.data.swzzrtsq.RTSQST_STBPRP_BData;
import swzzmodeserver.workserver.data.swzzrtsq.ST_STBPRP_B_QUData;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/RNHDM")
public class RNHDM‌Controller {
    @Autowired
    private jupingtaiModeService jupingtaiModeService;

    @Value("${file.path.templatefilepath}")
    private String filePathName;

    @RequestMapping("/SetAreaPredictInfo")
    public Map<String, Object> SetAreaPredictInfo(@RequestBody ParamField bpPojo) {
        StopWatch watch = new StopWatch();
        watch.start();
        String stime="";
        String jydatatype = "temperatezone@shanghaiyb";//上海气象台降雨
        String gcdatatype="sangezhiliugc";
        String scwdatatype="";//采用默认的天文潮
        String DD_DISTRIBY="15";
        int num=0;
        Boolean isCanBoolean=true;
        if(bpPojo.getStartdate()!=null){
            stime=bpPojo.getStartdate();
        }
        else{
            isCanBoolean=false;
        }
        if(bpPojo.getHour()!=null){
            num=bpPojo.getHour();
        }else{
            isCanBoolean=false;
        }
        if(bpPojo.getDdList()!=null){
            List<ES_JISUANZHANPojo> ddList=bpPojo.getDdList();
            gcdatatype=gcdatatype+"@";
            for(int i=0;i<ddList.size();i++){
                ES_JISUANZHANPojo dd=ddList.get(i);
                gcdatatype+=dd.getNAME()+":"+dd.getTYPE()+"#";
            }
            gcdatatype=gcdatatype.substring(0,gcdatatype.length()-1);
        }else{
            isCanBoolean=false;
        }
        if(!isCanBoolean){
            watch.stop();
            Map<String, Object> map = new HashMap<>();
            map.put("message", "必传参数需传");
            map.put("success", false);
        }
        if (CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo, ParamField.class))) {
            watch.stop();
            Map<String, Object> map = new HashMap<>();
            map.put("message", "参数存在非法字符");
            map.put("success", false);            
        }
        return jupingtaiModeService.SetAreaPredictInfo(stime, num, jydatatype, gcdatatype, scwdatatype, DD_DISTRIBY);
    }

    @RequestMapping("/modelGetTaskStatus")
    public Map<String, Object> modelGetTaskStatus(@RequestBody ParamField bpPojo) {
        StopWatch watch = new StopWatch();
        watch.start();
        String taskID="",dd_id="";
        Boolean isCanBoolean=true;
        if(bpPojo.getDd_id()!=null){
            dd_id=bpPojo.getDd_id();
        }else{
            isCanBoolean=false;
        }
        if(bpPojo.getTaskID()!=null){
            taskID=bpPojo.getTaskID();
        }else{
            isCanBoolean=false;
        }
        if(!isCanBoolean){
            watch.stop();
            Map<String, Object> map = new HashMap<>();
            map.put("message", "必传参数需传");
            map.put("success", false);
        }
        return jupingtaiModeService.modelGetTaskStatus(taskID,dd_id);
    }    

    @RequestMapping("/SeAreatPredictScheduleData")
    public Map<String, Object> SeAreatPredictScheduleData(@RequestBody ParamField bpPojo) {
        StopWatch watch = new StopWatch();
        watch.start();
        String stime="",dd_id="",taskID="";
        String DD_DISTRIBY="15";
        int num=0;
        Boolean isCanBoolean=true;
        if(bpPojo.getStartdate()!=null){
            stime=bpPojo.getStartdate();
        }
        else{
            isCanBoolean=false;
        }
        if(bpPojo.getHour()!=null){
            num=bpPojo.getHour();
        }else{
            isCanBoolean=false;
        }
        if(bpPojo.getDd_id()!=null){
            dd_id=bpPojo.getDd_id();
        }else{
            isCanBoolean=false;
        }
        if(bpPojo.getTaskID()!=null){
            taskID=bpPojo.getTaskID();
        }else{
            isCanBoolean=false;
        }
        if(!isCanBoolean){
            watch.stop();
            Map<String, Object> map = new HashMap<>();
            map.put("message", "必传参数需传");
            map.put("success", false);
        }
        if (CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo, ParamField.class))) {
            watch.stop();
            Map<String, Object> map = new HashMap<>();
            map.put("message", "参数存在非法字符");
            map.put("success", false);            
        }
        // 定义时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // 解析时间字符串为LocalDateTime对象
        LocalDateTime startTime = LocalDateTime.parse(stime, formatter);

        // 加上指定的小时数
        LocalDateTime endTime = startTime.plusHours(num);

        // 将结果格式化为字符串
        String etime = endTime.format(formatter);
        int rows= jupingtaiModeService.onResultOk(dd_id,stime, etime, taskID, DD_DISTRIBY);

        Map<String, Object> map=new HashMap<>();
        if(rows>0){
            map.put("message", "保存成功");
            map.put("success", true);
            map.put("dd_id", dd_id);
        }
        else{
            map.put("message", "保存失败");
            map.put("success", false);
        }
        watch.stop();
        return map;
    }
}
