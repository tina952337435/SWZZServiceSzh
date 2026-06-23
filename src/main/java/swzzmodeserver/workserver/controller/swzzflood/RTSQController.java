package swzzmodeserver.workserver.controller.swzzflood;

import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swzzmodeserver.tools.CommonUtills;
import swzzmodeserver.tools.FieldIsValid;
import swzzmodeserver.tools.ParamField;
import swzzmodeserver.tools.ResultUtils;
import swzzmodeserver.workserver.data.swzzflood.ST_TIDE_RData;
import swzzmodeserver.workserver.pojo.swzzflood.ST_TIDEH_RPojo;
import swzzmodeserver.workserver.pojo.swzzzjk.ST_TIDEHIGHParam;
import swzzmodeserver.workserver.service.swzzflood.RTSQService;
import swzzmodeserver.workserver.service.swzzflood.ST_TIDEH_RService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/SWZZ_FLOOD_RTSQ")
public class RTSQController {
    @Autowired
    private RTSQService service;

    @RequestMapping("/WATER_ST_WAS_RNEW")
    public ResultUtils WATER_ST_WAS_RNEW(@RequestBody ParamField bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String starttime = "",endtime = "";
        List<String> stcd = new ArrayList<>();
        ParamField params = FieldIsValid.getColumnName(bpPojo,ParamField.class);
        if(CommonUtills.isEmpty(params)){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if(null != bpPojo.getStartdate()){
            starttime = bpPojo.getStartdate();
        }
        if(null != bpPojo.getEnddate()){
            endtime = bpPojo.getEnddate();
        }
        if (null != bpPojo.getStcd()){
            stcd = Arrays.asList(bpPojo.getStcd().split(","));
        }
        List<Map<String,Object>> pojoList = service.WATER_ST_WAS_RNEW(stcd,starttime, endtime);
        watch.stop();
        if(pojoList.size() > 0){
            return new ResultUtils<>(pojoList, "操作成功",true ,pojoList.size(),watch.getTime());
        }else {
            return new ResultUtils<>(pojoList, "操作成功",false,pojoList.size(),watch.getTime());
        }
    }

    @RequestMapping("/WATER_ST_WAS_RDZNEW")
    public ResultUtils WATER_ST_WAS_RDZNEW(@RequestBody ParamField bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String starttime = "",endtime = "";
        List<String> stcd = new ArrayList<>();
        String dayHour="";
        ParamField params = FieldIsValid.getColumnName(bpPojo,ParamField.class);
        if(CommonUtills.isEmpty(params)){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if(null != bpPojo.getStartdate()){
            starttime = bpPojo.getStartdate();
        }
        if(null != bpPojo.getEnddate()){
            endtime = bpPojo.getEnddate();
        }
        if (null != bpPojo.getStcd()){
            stcd = Arrays.asList(bpPojo.getStcd().split(","));
        }
        if(null!=bpPojo.getDayHour()){
            dayHour=bpPojo.getDayHour();
        }
        List<Map<String,Object>> pojoList = service.WATER_ST_WAS_RDZNEW(stcd,starttime, endtime,dayHour);
        watch.stop();
        if(pojoList.size() > 0){
            return new ResultUtils<>(pojoList, "操作成功",true ,pojoList.size(),watch.getTime());
        }else {
            return new ResultUtils<>(pojoList, "操作成功",false,pojoList.size(),watch.getTime());
        }
    }
    @RequestMapping("/WATER_ST_PPTN_RNEW")
    public ResultUtils WATER_ST_PPTN_RNEW(@RequestBody ParamField bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String starttime = "",endtime = "";
        List<String> stcd = new ArrayList<>();
        ParamField params = FieldIsValid.getColumnName(bpPojo,ParamField.class);
        if(CommonUtills.isEmpty(params)){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if(null != bpPojo.getStartdate()){
            starttime = bpPojo.getStartdate();
        }
        if(null != bpPojo.getEnddate()){
            endtime = bpPojo.getEnddate();
        }
        if (null != bpPojo.getStcd()){
            stcd = Arrays.asList(bpPojo.getStcd().split(","));
        }
        List<Map<String,Object>> pojoList = service.WATER_ST_PPTN_RNEW(stcd,starttime, endtime);
        watch.stop();
        if(pojoList.size() > 0){
            return new ResultUtils<>(pojoList, "操作成功",true ,pojoList.size(),watch.getTime());
        }else {
            return new ResultUtils<>(pojoList, "操作成功",false,pojoList.size(),watch.getTime());
        }
    }
    @RequestMapping("/WATER_ST_PPTN_RDZNEW")
    public ResultUtils WATER_ST_PPTN_RDZNEW(@RequestBody ParamField bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String starttime = "",endtime = "",dayHour = "";
        List<String> stcd = new ArrayList<>();
        ParamField params = FieldIsValid.getColumnName(bpPojo,ParamField.class);
        if(CommonUtills.isEmpty(params)){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if(null != bpPojo.getStartdate()){
            starttime = bpPojo.getStartdate();
        }
        if(null != bpPojo.getEnddate()){
            endtime = bpPojo.getEnddate();
        }
        if (null != bpPojo.getStcd()){
            stcd = Arrays.asList(bpPojo.getStcd().split(","));
        }
        if(null != bpPojo.getDayHour()){
            dayHour = bpPojo.getDayHour();
        }
        List<Map<String,Object>> pojoList = service.WATER_ST_PPTN_RDZNEW(stcd,starttime, endtime,dayHour);
        watch.stop();
        if(pojoList.size() > 0){
            return new ResultUtils<>(pojoList, "操作成功",true ,pojoList.size(),watch.getTime());
        }else {
            return new ResultUtils<>(pojoList, "操作成功",false,pojoList.size(),watch.getTime());
        }
    }

    //水雨情
    @RequestMapping("/WATER_ST_PPTNWAS_RDZNEW")
    public ResultUtils WATER_ST_PPTNWAS_RDZNEW(@RequestBody ParamField bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String starttime = "",endtime = "",dayHour = "";
        List<String> stcd = new ArrayList<>();
        ParamField params = FieldIsValid.getColumnName(bpPojo,ParamField.class);
        if(CommonUtills.isEmpty(params)){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if(null != bpPojo.getStartdate()){
            starttime = bpPojo.getStartdate();
        }
        if(null != bpPojo.getEnddate()){
            endtime = bpPojo.getEnddate();
        }
        if (null != bpPojo.getStcd()){
            stcd = Arrays.asList(bpPojo.getStcd().split(","));
        }
        if(null != bpPojo.getDayHour()){
            dayHour = bpPojo.getDayHour();
        }
        List<Map<String,Object>> pojoList = service.WATER_ST_PPTNWAS_RDZNEW(stcd,starttime, endtime,dayHour);
        watch.stop();
        if(pojoList.size() > 0){
            return new ResultUtils<>(pojoList, "操作成功",true ,pojoList.size(),watch.getTime());
        }else {
            return new ResultUtils<>(pojoList, "操作成功",false,pojoList.size(),watch.getTime());
        }
    }
}
