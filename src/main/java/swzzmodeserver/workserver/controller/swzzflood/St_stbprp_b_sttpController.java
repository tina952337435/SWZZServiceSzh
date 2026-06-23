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
import swzzmodeserver.workserver.data.swzzflood.St_stbprp_b_qu_treeData;
import swzzmodeserver.workserver.data.swzzflood.St_stbprp_b_sttpData;
import swzzmodeserver.workserver.pojo.swzzflood.St_stbprp_b_qu_treePojo;
import swzzmodeserver.workserver.pojo.swzzflood.St_stbprp_b_sttpPojo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/SWZZ_WATER_st_stbprp_b_sttp")
public class St_stbprp_b_sttpController {
    @Autowired
    private St_stbprp_b_sttpData data;

    @RequestMapping("/findResult")
    public ResultUtils findResult(@RequestBody ParamField bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String ID = "",key = "",pageindex = "",pagesize = "10";
        List<String> type = new ArrayList<>();
        String stime = "",etime = "";
        ParamField params = FieldIsValid.getColumnName(bpPojo,ParamField.class);
        if(CommonUtills.isEmpty(params)){
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
        Integer startindex = null;
        if(!"".equals(pageindex) && !"".equals(pagesize)){
            startindex = (Integer.parseInt(pageindex) - 1) * Integer.parseInt(pagesize);
        }
        List<St_stbprp_b_sttpPojo> fxList = data.selectList(ID,key,stime,etime,type,startindex, Integer.valueOf(pagesize));
        Integer integer = data.selectCount(ID,key,stime,etime,type);
        Integer count = 1;
        if(!"".equals(pagesize)){
            count = integer / Integer.parseInt(pagesize);
            if(integer % Integer.parseInt(pagesize) != 0){
                count += 1;
            }
        }
        watch.stop();
        if(!"".equals(pagesize) && !"".equals(pageindex)){
            if(fxList.size() > 0){
                return new ResultUtils<>(fxList, "操作成功",true,Integer.parseInt(pagesize) ,Integer.parseInt(pageindex),count,integer,fxList.size(),watch.getTime());
            }else {
                return new ResultUtils<>(fxList, "操作成功",false,Integer.parseInt(pagesize) ,Integer.parseInt(pageindex),count,integer,fxList.size(),watch.getTime());
            }
        }else {
            if(fxList.size() > 0){
                return new ResultUtils<>(fxList, "操作成功",true ,fxList.size(),watch.getTime());
            }else {
                return new ResultUtils<>(fxList, "操作成功",false,fxList.size(),watch.getTime());
            }
        }
    }
    @RequestMapping("/add")
    public ResultUtils add(@RequestBody St_stbprp_b_sttpPojo bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        St_stbprp_b_sttpPojo params = FieldIsValid.getColumnName(bpPojo,St_stbprp_b_sttpPojo.class);
        if(CommonUtills.isEmpty(params)){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        Integer num = data.insertOne(bpPojo);
        watch.stop();
        if(num > 0){
            return new ResultUtils<>(num, "操作成功",true, num,watch.getTime());
        }else {
            return new ResultUtils<>(num, "操作成功",false, num,watch.getTime());
        }
    }
    @RequestMapping("/modify")
    public ResultUtils modify(@RequestBody St_stbprp_b_sttpPojo bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        St_stbprp_b_sttpPojo params = FieldIsValid.getColumnName(bpPojo,St_stbprp_b_sttpPojo.class);
        if(CommonUtills.isEmpty(params)){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        Integer num = data.updateOne(bpPojo);
        watch.stop();
        if(num > 0){
            return new ResultUtils<>(num, "操作成功",true, num,watch.getTime());
        }else {
            return new ResultUtils<>(num, "操作成功",false, num,watch.getTime());
        }
    }
    @RequestMapping("/remove")
    public ResultUtils remove(@RequestBody ParamField bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String ID = "";
        ParamField params = FieldIsValid.getColumnName(bpPojo,ParamField.class);
        if(CommonUtills.isEmpty(params)){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if(null != bpPojo.getStcd()){
            ID = bpPojo.getStcd();
        }
        Integer num = data.deleteOne(ID);
        watch.stop();
        if(num > 0){
            return new ResultUtils<>(num, "操作成功",true, num,watch.getTime());
        }else {
            return new ResultUtils<>(num, "操作成功",false, num,watch.getTime());
        }
    }

}
