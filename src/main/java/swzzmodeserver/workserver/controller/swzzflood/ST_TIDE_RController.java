package swzzmodeserver.workserver.controller.swzzflood;

import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swzzmodeserver.tools.*;
import swzzmodeserver.workserver.data.swzzflood.ST_TIDE_RData;
import swzzmodeserver.workserver.pojo.swzzflood.ST_TIDEH_RPojo;
import swzzmodeserver.workserver.pojo.swzzflood.ST_TIDE_RPojo;
import swzzmodeserver.workserver.pojo.swzzmode.BDMS_PREDICTPojo;
import swzzmodeserver.workserver.pojo.swzzwater.BP_DATAPojo;
import swzzmodeserver.workserver.pojo.swzzzjk.ST_TIDEHIGHParam;
import swzzmodeserver.workserver.service.swzzflood.ST_TIDEH_RService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/SWZZ_FLOOD_ST_TIDE_R")
public class ST_TIDE_RController {
    @Autowired
    private ST_TIDE_RData data;
    @Autowired
    private ST_TIDEH_RService service;

    @RequestMapping("/findResult")
    public ResultUtils findResult(@RequestBody ParamField bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String starttime = "",endtime = "";
        List<String> stcdList = new ArrayList<>();
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
            stcdList = Arrays.asList(bpPojo.getStcd().split(","));
        }
        List<ST_TIDEH_RPojo> pojoList = data.selectTideHList(stcdList, starttime, endtime);
        watch.stop();
        if(pojoList.size() > 0){
            return new ResultUtils<>(pojoList, "操作成功",true ,pojoList.size(),watch.getTime());
        }else {
            return new ResultUtils<>(pojoList, "操作成功",false,pojoList.size(),watch.getTime());
        }
    }

    @RequestMapping("/WATER_ST_TIDE_RHTIDE")
    public ResultUtils WATER_ST_TIDE_RHTIDE(@RequestBody ParamField bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String starttime = "",
                endtime = "",
                type = null;
        List<String> stcdList = new ArrayList<>();
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
            stcdList = Arrays.asList(bpPojo.getStcd().split(","));
        }
        if (null != bpPojo.getPattem()){
            type = bpPojo.getPattem();
        }
        List<ST_TIDEHIGHParam> pojoList = service.WATER_ST_TIDE_RHTIDE(stcdList, starttime, endtime, type);
        watch.stop();
        if(pojoList.size() > 0){
            return new ResultUtils<>(pojoList, "操作成功",true ,pojoList.size(),watch.getTime());
        }else {
            return new ResultUtils<>(pojoList, "操作成功",false,pojoList.size(),watch.getTime());
        }
    }
    @RequestMapping("/WATER_ST_TIDE_RHTIDEHS")
    public ResultUtils WATER_ST_TIDE_RHTIDEHS(@RequestBody ParamField bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String starttime = "",
                endtime = "",
                type = null;
        List<String> stcdList = new ArrayList<>();
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
            stcdList = Arrays.asList(bpPojo.getStcd().split(","));
        }
        if (null != bpPojo.getPattem()){
            type = bpPojo.getPattem();
        }
        List<ST_TIDEHIGHParam> pojoList = service.WATER_ST_TIDE_RHTIDEHS(stcdList, starttime, endtime, type);
        watch.stop();
        if(pojoList.size() > 0){
            return new ResultUtils<>(pojoList, "操作成功",true ,pojoList.size(),watch.getTime());
        }else {
            return new ResultUtils<>(pojoList, "操作成功",false,pojoList.size(),watch.getTime());
        }
    }
    @RequestMapping("/WaterRainfallFlowBINYB")
    public ResultUtils WaterRainfallFlowBINYB(@RequestBody ParamField bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String starttime = "",
                endtime = "",
                type = "";
        String stcd = "";
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
            stcd = bpPojo.getStcd();
        }
        if (null != bpPojo.getPattem()){
            type = bpPojo.getPattem();
        }
        List<Map<String,Object>> pojoList = service.WaterRainfallFlowBINYB(starttime, endtime, stcd, type);
        watch.stop();
        if(pojoList.size() > 0){
            return new ResultUtils<>(pojoList, "操作成功",true ,pojoList.size(),watch.getTime());
        }else {
            return new ResultUtils<>(pojoList, "操作成功",false,pojoList.size(),watch.getTime());
        }
    }
    @RequestMapping("/WaterRainfallFlowBIN")
    public ResultUtils WaterRainfallFlowBIN(@RequestBody ParamField bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String starttime = "",
                endtime = "",
                type = "";
        String stcd = "";
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
            stcd = bpPojo.getStcd();
        }
        if (null != bpPojo.getPattem()){
            type = bpPojo.getPattem();
        }
        List<Map<String,Object>> pojoList = service.WaterRainfallFlowBIN(starttime, endtime, stcd, type);
        watch.stop();
        if(pojoList.size() > 0){
            return new ResultUtils<>(pojoList, "操作成功",true ,pojoList.size(),watch.getTime());
        }else {
            return new ResultUtils<>(pojoList, "操作成功",false,pojoList.size(),watch.getTime());
        }
    }

    @RequestMapping("/WaterRainfallFlowBINJX")
    public ResultUtils WaterRainfallFlowBINJX(@RequestBody ParamField bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String starttime = "",
                endtime = "",
                type = "",
                strExp="";
        String stcd = "";
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
            stcd = bpPojo.getStcd();
        }
        if (null != bpPojo.getPattem()){
            type = bpPojo.getPattem();
        }
        if (null != bpPojo.getStrExp()){
            strExp = bpPojo.getStrExp();
        }
        List<BP_DATAPojo> pojoList = service.WaterRainfallFlowBINJX(starttime, endtime, stcd, type,strExp);
        watch.stop();
        if(pojoList.size() > 0){
            return new ResultUtils<>(pojoList, "操作成功",true ,pojoList.size(),watch.getTime());
        }else {
            return new ResultUtils<>(pojoList, "操作成功",false,pojoList.size(),watch.getTime());
        }
    }

    @RequestMapping("/add")
    public ResultUtils add(@RequestBody ST_TIDE_RPojo bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo, ST_TIDE_RPojo.class))){
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
    public ResultUtils modify(@RequestBody ST_TIDE_RPojo bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo, ST_TIDE_RPojo.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        Integer num = data.upDateOne(bpPojo);
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
    public ResultUtils batchResult(@PathVariable("pattem") String pattem, @RequestBody List<ST_TIDE_RPojo> bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String type = "";
        if(CommonUtills.isEmpty(FieldIsValid.getListColumnName(bpPojo, ST_TIDE_RPojo.class)) || !FieldIsValid.isValid(pattem)){
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
        List<ST_TIDE_RPojo> list = new ArrayList<>();
        for(int i=0;i<number;i++){
            if(i == number - 1){
                list = bpPojo.subList(count * i,bpPojo.size());
            }else {
                list = bpPojo.subList(count * i,count * (i + 1));
            }
            if(type.equals("true") ){
                num += data.upDateAll(list);
            }else {
                num += data.insertAll(list);
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
    public ResultUtils batchRemove(@RequestBody ParamField bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo, ParamField.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        List<String> stringList = new ArrayList<>();
        if (bpPojo.getStcd() != null){
            stringList = Arrays.asList(bpPojo.getStcd().split(","));
        }
        Integer num = 0;
        int count = 80;
        int number = stringList.size() / count;
        if (stringList.size() % count != 0){
            number += 1;
        }
        List<String> list = new ArrayList<>();
        for(int i=0;i<number;i++){
            if(i == number - 1){
                list = stringList.subList(count * i,stringList.size());
            }else {
                list = stringList.subList(count * i,count * (i + 1));
            }
            num += data.deleteAll(list);
        }
        watch.stop();
        if(num > 0){
            return new ResultUtils<>(num, "操作成功",true, num,watch.getTime());
        }else {
            return new ResultUtils<>(num, "操作成功",false, num,watch.getTime());
        }
    }

    @RequestMapping("/shftpconvertGrb2ToNc")
    public ResultUtils shftpconvertGrb2ToNc(@RequestBody ParamField bpPojo) throws IOException, InterruptedException {
        StopWatch watch = new StopWatch();
        watch.start();
        List<BP_DATAPojo> pojoList = new ArrayList<>();
        String inputGrb2File="Z_NWGD_C_BCSH_20240723161951_P_RFFC_SPCC-ER01_202407232000_04801.GRB2", outputNcFile="Z_NWGD_C_BCSH_20240723161951_P_RFFC_SPCC-ER01_202407232000_04801.nc";
        service.shftpconvertGrb2ToNc(inputGrb2File,outputNcFile);
        watch.stop();
        if(pojoList.size() > 0){
            return new ResultUtils<>(pojoList, "操作成功",true ,pojoList.size(),watch.getTime());
        }else {
            return new ResultUtils<>(pojoList, "操作成功",false,pojoList.size(),watch.getTime());
        }
    }
}
