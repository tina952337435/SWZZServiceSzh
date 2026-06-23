package swzzmodeserver.workserver.controller.zjtyphoon;

import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swzzmodeserver.tools.CommonUtills;
import swzzmodeserver.tools.FieldIsValid;
import swzzmodeserver.tools.ParamField;
import swzzmodeserver.tools.ResultUtils;
import swzzmodeserver.workserver.data.zjtyphoon.ZJ_LANDData;
import swzzmodeserver.workserver.data.zjtyphoon.ZJ_TFLSLJData;
import swzzmodeserver.workserver.pojo.zjtyphoon.ZJ_LANDPojo;
import swzzmodeserver.workserver.pojo.zjtyphoon.ZJ_TFLSLJPojo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/ZJ_TYPHOON_ZJ_TFLSLJ")
public class ZJ_TFLSLJController {
    @Autowired
    private ZJ_TFLSLJData data;

    @RequestMapping("/findResult")
    public ResultUtils findResult(@RequestBody ParamField bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String ID = "",key = "",pageindex = "",pagesize = "10";
        List<String> type = new ArrayList<>();
        String stime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date().getTime() - 24 * 60 * 60 * 1000),etime = "";
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo, ParamField.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if(null != bpPojo.getStcd()){
            ID = bpPojo.getStcd();
        }
//        if(null != bpPojo.getKwtxt()){
//            key = bpPojo.getKwtxt();
//        }
        if(null != bpPojo.getStartdate()){
            stime = bpPojo.getStartdate();
        }
        if(null != bpPojo.getEnddate()){
            etime = bpPojo.getEnddate();
        }
//        if(null != bpPojo.getPattem()){
//            type = Arrays.asList(bpPojo.getPattem().split(","));
//        }
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
        List<ZJ_TFLSLJPojo> fxList = data.selectList(ID,key,stime,etime,type,startindex, Integer.valueOf(pagesize));
        Integer integer = data.selectCount(ID,key,stime,etime,type);
        Integer count = 1;
        if(null != pagesize && !"".equals(pagesize)){
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
    public ResultUtils add(@RequestBody ZJ_TFLSLJPojo bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo, ZJ_TFLSLJPojo.class))){
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
    public ResultUtils modify(@RequestBody ZJ_TFLSLJPojo bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo, ZJ_TFLSLJPojo.class))){
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
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo, ParamField.class))){
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

    @RequestMapping("/batchResult/{pattem}")
    public ResultUtils batchResult(@PathVariable("pattem") String pattem, @RequestBody List<ZJ_TFLSLJPojo> bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String type = "";
        if(CommonUtills.isEmpty(FieldIsValid.getListColumnName(bpPojo, ZJ_TFLSLJPojo.class)) || !FieldIsValid.isValid(pattem)){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if(null != pattem){
            type = pattem;
        }
        Integer num = 0;
        int count = 80;
        int number = bpPojo.size() / count;
        if (bpPojo.size() % count != 0){
            number += 1;
        }
        List<ZJ_TFLSLJPojo> list = new ArrayList<>();
        for(int i=0;i<number;i++){
            if(i == number - 1){
                list = bpPojo.subList(count * i,bpPojo.size());
            }else {
                list = bpPojo.subList(count * i,count * (i + 1));
            }
            if(type.equals("true") ){
                num += data.updateALL(list);
            }else {
                num += data.insertALL(list);
            }
        }
        watch.stop();
        if(num > 0){
            return new ResultUtils<>(num, "操作成功",true, num,watch.getTime());
        }else {
            return new ResultUtils<>(num, "操作成功",false, num,watch.getTime());
        }
    }

    @RequestMapping("/batchRemove")
    public ResultUtils batchRemove(@RequestBody List<ParamField> bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        if(CommonUtills.isEmpty(FieldIsValid.getListColumnName(bpPojo, ParamField.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        Integer num = 0;
        int count = 80;
        int number = bpPojo.size() / count;
        if (bpPojo.size() % count != 0){
            number += 1;
        }
        List<ParamField> list = new ArrayList<>();
        for(int i=0;i<number;i++){
            if(i == number - 1){
                list = bpPojo.subList(count * i,bpPojo.size());
            }else {
                list = bpPojo.subList(count * i,count * (i + 1));
            }
            num += data.deleteALL(list);
        }
        watch.stop();
        if(num > 0){
            return new ResultUtils<>(num, "操作成功",true, num,watch.getTime());
        }else {
            return new ResultUtils<>(num, "操作成功",false, num,watch.getTime());
        }
    }
}
