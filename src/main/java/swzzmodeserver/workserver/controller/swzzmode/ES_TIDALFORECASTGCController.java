package swzzmodeserver.workserver.controller.swzzmode;

import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swzzmodeserver.tools.*;
import swzzmodeserver.workserver.data.swzzmode.ES_TIDALFORECASTGCData;
import swzzmodeserver.workserver.data.swzzmode.ST_ASTRONOMICALTIDE_RData;
import swzzmodeserver.workserver.pojo.swzzmode.ES_TIDALFORECASTGCPojo;
import swzzmodeserver.workserver.pojo.swzzmode.ST_ASTRONOMICALTIDE_RPojo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/SWZZ_MODE_ES_TIDALFORECASTGC")
public class ES_TIDALFORECASTGCController {
    @Autowired
    private ES_TIDALFORECASTGCData data;

    @Autowired
    private ST_ASTRONOMICALTIDE_RData st_astronomicaltide_rData;

    @RequestMapping("/findResult")
    public ResultUtils findResult(@RequestBody ParamField bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String ID = "",key = "",pageindex = "",pagesize = "10";
        List<String> type = new ArrayList<>();
        String stime = ""//new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date().getTime() - 24 * 60 * 60 * 1000)
                ,etime = "",ybstm = "",ybetm = "";
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo, ParamField.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if(null != bpPojo.getStcd()){
            ID = bpPojo.getStcd();
        }
        if(null != bpPojo.getKwtxt()){
            key = bpPojo.getKwtxt();
        }
        if(null != bpPojo.getStartdate()){
            stime = bpPojo.getStartdate();
        }
        if(null != bpPojo.getEnddate()){
            etime = bpPojo.getEnddate();
        }
        if(null != bpPojo.getPattem()){
            type = Arrays.asList(bpPojo.getPattem().split(","));
        }
        if(null != bpPojo.getPageindex()){
            pageindex = bpPojo.getPageindex();
        }
        if(null != bpPojo.getPagesize()){
            pagesize = bpPojo.getPagesize();
        }
        if(null != bpPojo.getKID1()){
            ybstm = bpPojo.getKID1();
        }
        if(null != bpPojo.getKID2()){
            ybetm = bpPojo.getKID2();
        }
        Integer startindex = null;
        if(!"".equals(pageindex) && !"".equals(pagesize)){
            startindex = (Integer.parseInt(pageindex) - 1) * Integer.parseInt(pagesize);
        }
        List<ES_TIDALFORECASTGCPojo> fxList = data.selectList(ID,key,stime,etime,null,null,type,startindex, Integer.valueOf(pagesize));
        if(fxList.size() > 0){
            return new ResultUtils<>(fxList, "操作成功",true ,fxList.size(),watch.getTime());
        }else {
            return new ResultUtils<>(fxList, "操作成功",false,fxList.size(),watch.getTime());
        }
    }
    @RequestMapping("/batchResult/{pattem}")
    public ResultUtils batchResult(@PathVariable("pattem") String pattem, @RequestBody List<ES_TIDALFORECASTGCPojo> bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String type = "";
        if(CommonUtills.isEmpty(FieldIsValid.getListColumnName(bpPojo, ES_TIDALFORECASTGCPojo.class)) || !FieldIsValid.isValid(pattem)){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if(null != pattem){
            type = pattem;
        }
        Integer num = 0;
        int count = 80;
        int number = bpPojo.size() / count;
        if(bpPojo.size() % count != 0){
            number = number + 1;
        }
        List<ES_TIDALFORECASTGCPojo> list = new ArrayList<>();
        for(int i = 0;i < number;i++){
            if(i == number - 1){
                list = bpPojo.subList(count * i,bpPojo.size());
            }else {
                list = bpPojo.subList(count * i,count * ( i + 1));
            }
            num += data.insertALL(list);
        }
        watch.stop();
        if(num > 0){
            return new ResultUtils<>(num, "操作成功",true, num,watch.getTime());
        }else {
            return new ResultUtils<>(num, "操作成功",false, num,watch.getTime());
        }
    }

    //插值过程

}
