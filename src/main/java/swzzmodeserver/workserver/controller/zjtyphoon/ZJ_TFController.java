package swzzmodeserver.workserver.controller.zjtyphoon;

import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swzzmodeserver.tools.*;
import swzzmodeserver.workserver.data.zjtyphoon.ZJ_LANDData;
import swzzmodeserver.workserver.data.zjtyphoon.ZJ_TFData;
import swzzmodeserver.workserver.pojo.zjtyphoon.ZJ_LANDPojo;
import swzzmodeserver.workserver.pojo.zjtyphoon.ZJ_TFPojo;
import swzzmodeserver.workserver.pojo.zjtyphoon.ZJ_TFZTPojo;
import swzzmodeserver.workserver.service.zjtyphoon.ZJ_TFZTService;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/ZJ_TYPHOON_ZJ_TF")
public class ZJ_TFController {
    @Autowired
    private ZJ_TFData data;
    @Autowired
    private ZJ_TFZTService service;

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
        List<ZJ_TFPojo> fxList = data.selectList(ID,key,stime,etime,type,null,startindex, Integer.valueOf(pagesize));
        Integer integer = data.selectCount(ID,key,stime,etime,type,null);
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

    @RequestMapping("/findResultzt")
    public ResultUtils findResultzt(@RequestBody ParamField bpPojo){
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
        List<ZJ_TFZTPojo> fxList = data.selectListandzhuanti(ID,key,stime,etime,type,null,startindex, Integer.valueOf(pagesize));
        Integer integer = data.selectCount(ID,key,stime,etime,type,null);
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
    public ResultUtils add(@RequestBody ZJ_TFPojo bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo, ZJ_TFPojo.class))){
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
    public ResultUtils modify(@RequestBody ZJ_TFPojo bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo, ZJ_TFPojo.class))){
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
    public ResultUtils batchResult(@PathVariable("pattem") String pattem, @RequestBody List<ZJ_TFPojo> bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String type = "";
        if(CommonUtills.isEmpty(FieldIsValid.getListColumnName(bpPojo, ZJ_TFPojo.class)) || !FieldIsValid.isValid(pattem)){
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
        List<ZJ_TFPojo> list = new ArrayList<>();
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

    @RequestMapping("/TYPHOON_ZJ_TFZTSel")
    public ResultUtils<List> TYPHOON_ZJ_TFZTSel(@RequestBody ParamField bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String ID = "",key = "",pageindex = "",pagesize = "10",year = "",ddwj = "提供2023";
        String type = "";
        String stime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date().getTime() - 24 * 60 * 60 * 1000),etime = "";
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo, ParamField.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
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
            type = bpPojo.getPattem();
        }
        if(null != bpPojo.getPageindex()){
            pageindex = bpPojo.getPageindex();
        }
        if(null != bpPojo.getPagesize()){
            pagesize = bpPojo.getPagesize();
        }
        if(null != bpPojo.getYear()){
            year = bpPojo.getYear();
        }
        if(null != bpPojo.getDdwj()){
            ddwj = bpPojo.getDdwj();
        }
        Integer startindex = null;
        if(!"".equals(pageindex) && !"".equals(pagesize)){
            startindex = (Integer.parseInt(pageindex) - 1) * Integer.parseInt(pagesize);
        }
        List<Map<String,Object>> fxList = service.TYPHOON_ZJ_TFZTSel(key,stime,etime,ddwj,year,type,startindex, Integer.valueOf(pagesize));
        Integer integer = data.selectCount(ID,key,stime,etime, Collections.singletonList(type),null);
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

    @RequestMapping("/GetTFBH_XSSel")
    public ResultUtils<List> GetTFBH_XSSel(@RequestBody ParamFields bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String tfbh = "",type = "",isWX = "";
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo, ParamFields.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if (null != bpPojo.getTfbh()){
            tfbh = bpPojo.getTfbh();
        }
        if (null != bpPojo.getType()){
            type = bpPojo.getType();
        }
        if (null != bpPojo.getIsWX()){
            isWX = bpPojo.getIsWX();
        }
        List<Map<String, Object>> mapList = service.GetTFBH_XSSel(tfbh, type, isWX);
        watch.stop();
        if(mapList.size() > 0){
            return new ResultUtils<>(mapList, "操作成功",true,mapList.size(),watch.getTime());
        }else {
            return new ResultUtils<>(mapList, "操作成功",false,mapList.size(),watch.getTime());
        }
    }

    @RequestMapping("/GetTFBH_XSYBSel")
    public ResultUtils<List> GetTFBH_XSYBSel(@RequestBody ParamFields bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String tfbh = "",ddwj = "",isWX = "",ZJ_YBSJ = "",ZJ_TM = "";
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo, ParamFields.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if (null != bpPojo.getTfbh()){
            tfbh = bpPojo.getTfbh();
        }
//        if (null != bpPojo.getDdwj()){
//            ddwj = bpPojo.getDdwj();
//        }
        if (null != bpPojo.getIsWX()){
            isWX = bpPojo.getIsWX();
        }
        if (null != bpPojo.getZJ_YBSJ()){
            ZJ_YBSJ = bpPojo.getZJ_YBSJ();
        }
        if (null != bpPojo.getZJ_TM()){
            ZJ_TM = bpPojo.getZJ_TM();
        }
        List<Map<String, Object>> mapList = service.GetTFBH_XSYBSel(tfbh,ddwj,isWX,ZJ_YBSJ,ZJ_TM);
        watch.stop();
        if(mapList.size() > 0){
            return new ResultUtils<>(mapList, "操作成功",true,mapList.size(),watch.getTime());
        }else {
            return new ResultUtils<>(mapList, "操作成功",false,mapList.size(),watch.getTime());
        }
    }

    @RequestMapping("/GetTyphoonList")
    public ResultUtils<List> GetTyphoonList(@RequestBody ParamFields bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        List<String> tfbh = new ArrayList<>();
        String isYB = "";
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo, ParamFields.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if (null != bpPojo.getTfbh()){
            tfbh = Arrays.asList(bpPojo.getTfbh().split(","));
        }
        if (null != bpPojo.getIsYB()){
            isYB = bpPojo.getIsYB();
        }
        List<Map<String, Object>> mapList = service.GetTyphoonList(tfbh,isYB);
        watch.stop();
        if(mapList.size() > 0){
            return new ResultUtils<>(mapList, "操作成功",true,mapList.size(),watch.getTime());
        }else {
            return new ResultUtils<>(mapList, "操作成功",false,mapList.size(),watch.getTime());
        }
    }

    @RequestMapping("/GetTyphoonListYB")
    public ResultUtils<List> GetTyphoonListYB(@RequestBody ParamFields bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String tfbh = "",rqsj2 = "",tfybdw = "";
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo, ParamFields.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if (null != bpPojo.getTfbh()){
            tfbh = bpPojo.getTfbh();
        }
        if (null != bpPojo.getRqsj2()){
            rqsj2 = bpPojo.getRqsj2();
        }
        if (null != bpPojo.getTfybdw()){
            tfybdw = bpPojo.getTfybdw();
        }
        List<Map<String, Object>> mapList = service.GetTyphoonListYB(tfbh,rqsj2,tfybdw);
        watch.stop();
        if(mapList.size() > 0){
            return new ResultUtils<>(mapList, "操作成功",true,mapList.size(),watch.getTime());
        }else {
            return new ResultUtils<>(mapList, "操作成功",false,mapList.size(),watch.getTime());
        }
    }
}
