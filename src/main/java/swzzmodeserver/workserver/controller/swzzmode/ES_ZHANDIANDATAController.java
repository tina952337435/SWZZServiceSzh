package swzzmodeserver.workserver.controller.swzzmode;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swzzmodeserver.tools.*;
import swzzmodeserver.workserver.data.swzzmode.ES_ZHANDIANDATAData;
import swzzmodeserver.workserver.pojo.Huishui.GetSubjectListPojo;
import swzzmodeserver.workserver.pojo.swzzmode.DD_SOLUTIONPojo;
import swzzmodeserver.workserver.pojo.swzzmode.ES_ZHANDIANDATAPojo;
import swzzmodeserver.workserver.pojo.swzzzjk.ST_TIDEHIGHParam;
import swzzmodeserver.workserver.service.swzzmode.ES_ZHANDIANDATAService;
import swzzmodeserver.workserver.service.swzzmode.HuishuiApiService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/SWZZ_MODE_ES_ZHANDIANDATA")
public class ES_ZHANDIANDATAController {
    @Autowired
    private ES_ZHANDIANDATAData data;
    @Autowired
    private ES_ZHANDIANDATAService service;

    @Autowired
    private HuishuiApiService huishuiApiService;

    @RequestMapping("/findResult")
    public ResultUtils findResult(@RequestBody ParamField bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String ID = "",key = "",pageindex = "",pagesize = "10",soid = "",startdate="",enddate="";
        List<String> type = new ArrayList<>();
        String stime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date().getTime() - 24 * 60 * 60 * 1000),etime = "";
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo,ParamField.class))){
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
        if(null != bpPojo.getStrExp()){
            soid = bpPojo.getStrExp();
        }
        if(null != bpPojo.getStartdate()){
            startdate = bpPojo.getStartdate();
        }
        if(null != bpPojo.getEnddate()){
            enddate = bpPojo.getEnddate();
        }
        Integer startindex = null;
        if(!"".equals(pageindex) && !"".equals(pagesize)){
            startindex = (Integer.parseInt(pageindex) - 1) * Integer.parseInt(pagesize);
        }
        List<ES_ZHANDIANDATAPojo> fxList = data.selectList(ID,startindex, Integer.valueOf(pagesize),soid,type,startdate,enddate);
        Integer integer = data.selectCount(ID,soid,type);
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
    public ResultUtils add(@RequestBody ES_ZHANDIANDATAPojo bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo,ES_ZHANDIANDATAPojo.class))){
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
    public ResultUtils modify(@RequestBody ES_ZHANDIANDATAPojo bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo,ES_ZHANDIANDATAPojo.class))){
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
        String ID = "",strExp = "",SOLUTIONID = "";
        Integer num = 0;
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo,ParamField.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if(null != bpPojo.getStcd()){
            ID = bpPojo.getStcd();
        }
        if(null != bpPojo.getStrExp()){
            strExp = bpPojo.getStrExp();
        }
        if(null != bpPojo.getStcd()){
            SOLUTIONID = bpPojo.getStcd();
        }
        if(strExp.equals("SOLUTIONID")){
            num = data.deleteOneBySOLUTIONID(SOLUTIONID);
        }else {
            num = data.deleteOne(ID);
        }

        watch.stop();
        if(num > 0){
            return new ResultUtils<>(num, "操作成功",true, num,watch.getTime());
        }else {
            return new ResultUtils<>(num, "操作成功",false, num,watch.getTime());
        }
    }
    @RequestMapping("/batchResult/{pattem}")
    public ResultUtils batchResult(@PathVariable("pattem") String pattem, @RequestBody List<ES_ZHANDIANDATAPojo> bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String type = "";
        if(CommonUtills.isEmpty(FieldIsValid.getListColumnName(bpPojo,ES_ZHANDIANDATAPojo.class)) || !FieldIsValid.isValid(pattem)){
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
        List<ES_ZHANDIANDATAPojo> list = new ArrayList<>();
        for(int i = 0;i < number;i++){
            if(i == number - 1){
                list = bpPojo.subList(count * i,bpPojo.size());
            }else {
                list = bpPojo.subList(count * i,count * ( i + 1));
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
    public ResultUtils batchRemove(String pattem,@RequestBody List<ParamField> bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        if(CommonUtills.isEmpty(FieldIsValid.getListColumnName(bpPojo,ParamField.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        Integer num = 0;
        int count = 80;
        int number = bpPojo.size() / count;
        if(bpPojo.size() % count != 0){
            number = number + 1;
        }
        List<ParamField> list = new ArrayList<>();
        for(int i = 0;i < number;i++){
            if(i == number - 1){
                list = bpPojo.subList(count * i,bpPojo.size());
            }else {
                list = bpPojo.subList(count * i,count * ( i + 1));
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

    @RequestMapping("/FH_modify_batchJY")
    public ResultUtils FH_modify_batchJY(@RequestBody ParamFields bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String zhanid = "",solutionid = "",dayhour = "",zhandata = "";
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo,ParamFields.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if(null != bpPojo.getZhanID()){
            zhanid = bpPojo.getZhanID();
        }
        if(null != bpPojo.getSolutionid()){
            solutionid = bpPojo.getSolutionid();
        }
        if(null != bpPojo.getDayhour()){
            dayhour = bpPojo.getDayhour();
        }
        if(null != bpPojo.getZhanData()){
            zhandata = bpPojo.getZhanData();
        }
        Integer num = service.FH_modify_batchJY(zhanid, solutionid, dayhour, zhandata);
        watch.stop();
        if(num > 0){
            return new ResultUtils<>(num, "操作成功",true, num,watch.getTime());
        }else {
            return new ResultUtils<>(num, "操作成功",false, num,watch.getTime());
        }
    }

    @RequestMapping("/FH_modify_batchJY134")
    public ResultUtils FH_modify_batchJY134(@RequestBody ParamFields bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String zhanid = "",solutionid = "",dayhour = "",zhandata = "";
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo,ParamFields.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if(null != bpPojo.getZhanID()){
            zhanid = bpPojo.getZhanID();
        }
        if(null != bpPojo.getSolutionid()){
            solutionid = bpPojo.getSolutionid();
        }
        if(null != bpPojo.getDayhour()){
            dayhour = bpPojo.getDayhour();
        }
        if(null != bpPojo.getZhanData()){
            zhandata = bpPojo.getZhanData();
        }
        Integer num = service.FH_modify_batchJY134(zhanid, solutionid, dayhour, zhandata);
        watch.stop();
        if(num > 0){
            return new ResultUtils<>(num, "操作成功",true, num,watch.getTime());
        }else {
            return new ResultUtils<>(num, "操作成功",false, num,watch.getTime());
        }
    }

    @RequestMapping("/FH_ModifyMethod")
    public ResultUtils FH_ModifyMethod(@RequestBody ParamFields bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String zhanid = "",solutionid = "",dayhour = "",zhandata = "",zhantime = "";
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo,ParamFields.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if(null != bpPojo.getZhanID()){
            zhanid = bpPojo.getZhanID();
        }
        if(null != bpPojo.getSolutionid()){
            solutionid = bpPojo.getSolutionid();
        }
        if(null != bpPojo.getDayhour()){
            dayhour = bpPojo.getDayhour();
        }
        if(null != bpPojo.getZhanData()){
            zhandata = bpPojo.getZhanData();
        }
        if(null != bpPojo.getZhanTime()){
            zhantime = bpPojo.getZhanTime();
        }
        Integer num = service.FH_ModifyMethod(zhandata,zhantime,zhanid,dayhour,solutionid);
        watch.stop();
        if(num > 0){
            return new ResultUtils<>(num, "操作成功",true, num,watch.getTime());
        }else {
            return new ResultUtils<>(num, "操作成功",false, num,watch.getTime());
        }
    }


    @RequestMapping("/FH_ModifyMethodJY")
    public ResultUtils FH_ModifyMethodJY(@RequestBody ParamFields bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String zhanid = "",solutionid = "",dayhour = "",zhandata = "",zhantime = "";
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo,ParamFields.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if(null != bpPojo.getZhanID()){
            zhanid = bpPojo.getZhanID();
        }
        if(null != bpPojo.getSolutionid()){
            solutionid = bpPojo.getSolutionid();
        }
        if(null != bpPojo.getDayhour()){
            dayhour = bpPojo.getDayhour();
        }
        if(null != bpPojo.getZhanData()){
            zhandata = bpPojo.getZhanData();
        }
        if(null != bpPojo.getZhanTime()){
            zhantime = bpPojo.getZhanTime();
        }
        Integer num = service.FH_ModifyMethodJY(zhandata,zhantime,zhanid,dayhour,solutionid);
        watch.stop();
        if(num > 0){
            return new ResultUtils<>(num, "操作成功",true, num,watch.getTime());
        }else {
            return new ResultUtils<>(num, "操作成功",false, num,watch.getTime());
        }
    }


    @RequestMapping("/FH_modify_batch")
    public ResultUtils FH_modify_batch(@RequestBody ParamFields bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String zhanid = "",solutionid = "",dayhour = "",zhandata = "",zhantime = "";
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo,ParamFields.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if(null != bpPojo.getZhanID()){
            zhanid = bpPojo.getZhanID();
        }
        if(null != bpPojo.getSolutionid()){
            solutionid = bpPojo.getSolutionid();
        }
        if(null != bpPojo.getDayhour()){
            dayhour = bpPojo.getDayhour();
        }
        if(null != bpPojo.getZhanData()){
            zhandata = bpPojo.getZhanData();
        }
        if(null != bpPojo.getZhanTime()){
            zhantime = bpPojo.getZhanTime();
        }
        Integer num = service.FH_modify_batch(zhandata,zhanid,dayhour,solutionid);
        watch.stop();
        if(num > 0){
            return new ResultUtils<>(num, "操作成功",true, num,watch.getTime());
        }else {
            return new ResultUtils<>(num, "操作成功",false, num,watch.getTime());
        }
    }

    @RequestMapping("/chooseTideMethod")
    public ResultUtils chooseTideMethod(@RequestBody ParamFields bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String type = "",solutionid = "",sDate = "",eDate="";
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo,ParamFields.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if(null != bpPojo.getType()){
            type = bpPojo.getType();
        }
        if(null != bpPojo.getSolutionid()){
            solutionid = bpPojo.getSolutionid();
        }
        if(null != bpPojo.getStartdate()){
            sDate = bpPojo.getStartdate();
        }
        if(null != bpPojo.getEnddate()){
            eDate = bpPojo.getEnddate();
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date eTime = null;
        try {
            eTime = dateFormat.parse(eDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date stime = new Date(eTime.getTime() - 7 * 24 * 60 * 60 * 1000);
        try {
            stime = dateFormat.parse(sDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Integer num = service.chooseTideMethod(dateFormat.format(stime),dateFormat.format(eTime),solutionid,type);
        watch.stop();
        if(num > 0){
            return new ResultUtils<>(num, "操作成功",true, num,watch.getTime());
        }else {
            return new ResultUtils<>(num, "操作成功",false, num,watch.getTime());
        }
    }

    @RequestMapping("/TideLineardifference")
    public ResultUtils TideLineardifference(@RequestBody ParamFields bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String type = "",solutionid = "0",stcd = "", startDate="",endDate="";

        List<ES_ZHANDIANDATAPojo> dataList = new ArrayList<>();
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo,ParamFields.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if(null != bpPojo.getType()){
            type = bpPojo.getType();
        }
        if(null != bpPojo.getSolutionid()){
            solutionid = bpPojo.getSolutionid();
        }
        if(null != bpPojo.getStcd()){
            stcd = bpPojo.getStcd();
        }
        if(null != bpPojo.getListMode()){
            dataList = bpPojo.getListMode();
        }
        if(null != bpPojo.getStartdate()){
            startDate = bpPojo.getStartdate();
        }
        if(null != bpPojo.getEnddate()){
            endDate = bpPojo.getEnddate();
        }
        Integer num = service.TideLineardifference(solutionid,dataList,stcd,type,startDate,endDate);
        watch.stop();
        if(num > 0){
            return new ResultUtils<>(num, "操作成功",true, num,watch.getTime());
        }else {
            return new ResultUtils<>(num, "操作成功",false, num,watch.getTime());
        }
    }

    @RequestMapping("/ModifyGCSSLLAREAGCGZ")
    public ResultUtils ModifyGCSSLLAREAGCGZ(@RequestBody ParamFields bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String solutionid = "",gcddfa = "";
        List<String> a = new ArrayList<>();
        List<String> b = new ArrayList<>();
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo,ParamFields.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if(null != bpPojo.getGcddfa()){
            gcddfa = bpPojo.getGcddfa();
            String[] asList = gcddfa.split(",");
            for(String str : asList){
                String[] strings = str.split("@");
                a.add(strings[0]);
                b.add(strings[1]);
            }
        }
        if(null != bpPojo.getSolutionid()){
            solutionid = bpPojo.getSolutionid();
        }

        Integer num = service.ModifyGCSSLLAREAGCGZ(solutionid,a,b);
        watch.stop();
        if(num > 0){
            return new ResultUtils<>(num, "操作成功",true, num,watch.getTime());
        }else {
            return new ResultUtils<>(num, "操作成功",false, num,watch.getTime());
        }
    }

    @RequestMapping("/FH_inset_ModifyApi")
    public ResultUtils FH_inset_ModifyApi(@RequestBody ParamFields bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String bdmsList = "",solutionid = "";
        Boolean isGetCookieDD_ID = null;
        DD_SOLUTIONPojo ddobj = new DD_SOLUTIONPojo();
//        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo,ParamFields.class))){
//            watch.stop();
//            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
//        }
        if(null != bpPojo.getBdms_predictSqlStr()){
            bdmsList = bpPojo.getBdms_predictSqlStr();
        }
        if(null != bpPojo.getSolutionid()){
            solutionid = bpPojo.getSolutionid();
        }
        if(null != bpPojo.getDdobj()){
            ddobj = bpPojo.getDdobj();
        }
        if(null != bpPojo.getIsGetCookieDD_ID()){
            isGetCookieDD_ID = bpPojo.getIsGetCookieDD_ID();
        }
        Integer num = service.FH_inset_ModifyApi(bdmsList,ddobj,isGetCookieDD_ID,solutionid);
        watch.stop();
        if(num > 0){
            return new ResultUtils<>(num, "操作成功",true, num,watch.getTime());
        }else {
            return new ResultUtils<>(num, "操作成功",false, num,watch.getTime());
        }
    }

    @RequestMapping("/MODIFY_MODEZHANDData")
    public ResultUtils MODIFY_MODEZHANDData(@RequestBody ParamFields bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String startdate = "",enddate = "",solutionid = "0",
                jydatatype = "SK",gcdatatype = "",scwdatatype = "",username = "";
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo,ParamFields.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if(null != bpPojo.getStartdate()){
            startdate = bpPojo.getStartdate();
        }
        if(null != bpPojo.getEnddate()){
            enddate = bpPojo.getEnddate();
        }
        if(null != bpPojo.getSolutionid()){
            solutionid = bpPojo.getSolutionid();
        }
        if(null != bpPojo.getJydatatype()){
            jydatatype = bpPojo.getJydatatype();
        }
        if(null != bpPojo.getGcdatatype()){
            gcdatatype = bpPojo.getGcdatatype();
        }
        if(null != bpPojo.getScwdatatype()){
            scwdatatype = bpPojo.getScwdatatype();
        }
        if(null != bpPojo.getUsername()){
            username = bpPojo.getUsername();
        }
        Integer num = service.MODIFY_MODEZHANDData(startdate,enddate,solutionid,jydatatype,gcdatatype,scwdatatype,username);
        watch.stop();
        if(num > 0){
            return new ResultUtils<>(num, "操作成功",true, num,watch.getTime());
        }else {
            return new ResultUtils<>(num, "操作成功",false, num,watch.getTime());
        }
    }

    /**
     * 优化版 MODIFY_MODEZHANDData - 使用 HashMap 预索引解决 O(n³) 性能问题
     */
    @RequestMapping("/MODIFY_MODEZHANDDataNew")
    public ResultUtils MODIFY_MODEZHANDDataNew(@RequestBody ParamFields bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String startdate = "",enddate = "",solutionid = "0",
                jydatatype = "SK",gcdatatype = "",scwdatatype = "",username = "";
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo,ParamFields.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if(null != bpPojo.getStartdate()){
            startdate = bpPojo.getStartdate();
        }
        if(null != bpPojo.getEnddate()){
            enddate = bpPojo.getEnddate();
        }
        if(null != bpPojo.getSolutionid()){
            solutionid = bpPojo.getSolutionid();
        }
        if(null != bpPojo.getJydatatype()){
            jydatatype = bpPojo.getJydatatype();
        }
        if(null != bpPojo.getGcdatatype()){
            gcdatatype = bpPojo.getGcdatatype();
        }
        if(null != bpPojo.getScwdatatype()){
            scwdatatype = bpPojo.getScwdatatype();
        }
        if(null != bpPojo.getUsername()){
            username = bpPojo.getUsername();
        }
        Integer num = service.MODIFY_MODEZHANDDataNew(startdate,enddate,solutionid,jydatatype,gcdatatype,scwdatatype,username);
        watch.stop();
        if(num > 0){
            return new ResultUtils<>(num, "操作成功",true, num,watch.getTime());
        }else {
            return new ResultUtils<>(num, "操作成功",false, num,watch.getTime());
        }
    }

    @RequestMapping("/YBSHUIWEI")
    public ResultUtils YBSHUIWEI(@RequestBody ParamFields bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String type = "",solutionid = "",plan = "",stcd = "",stime = "",etime="",mkid = "";
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo,ParamFields.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if (null != bpPojo.getType()){
            type = bpPojo.getType();
        }
        if (null != bpPojo.getSolutionid()){
            solutionid = bpPojo.getSolutionid();
        }
        if (null != bpPojo.getPlan_n()){
            plan = bpPojo.getPlan_n();
        }
        if (null != bpPojo.getStcd()){
            stcd = bpPojo.getStcd();
        }
        if (null != bpPojo.getStartdate()){
            stime = bpPojo.getStartdate();
        }
        if (null != bpPojo.getEnddate()){
            etime = bpPojo.getEnddate();
        }
        if (null != bpPojo.getMKEYID()){
            mkid = bpPojo.getMKEYID();
        }
        List<Map<String, Object>> mapList = service.YBSHUIWEI(stime, etime, stcd, plan, type, mkid, solutionid);
        watch.stop();
        if(mapList.size() > 0){
            return new ResultUtils<>(mapList, "操作成功",true, mapList.size(),watch.getTime());
        }else {
            return new ResultUtils<>(mapList, "操作成功",false, mapList.size(),watch.getTime());
        }
    }

    @RequestMapping("/FH_modify_batchJYQuan")
    public ResultUtils FH_modify_batchJYQuan(@RequestBody ParamFields bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String dd_id = "",DayHour = "",ZhanData = "";
        List<String> ZhanID = new ArrayList<>();
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo,ParamFields.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if (null != bpPojo.getDd_id()){
            dd_id = bpPojo.getDd_id();
        }
        if (null != bpPojo.getDayhour()){
            DayHour = bpPojo.getDayhour();
        }
        if (null != bpPojo.getZhanData()){
            ZhanData = bpPojo.getZhanData();
        }
        if (null != bpPojo.getZhanID()){
            ZhanID = Arrays.asList(bpPojo.getZhanID().split(","));
        }
        Integer integer = service.FH_modify_batchJYQuan(dd_id, ZhanID, DayHour, ZhanData);
        watch.stop();
        if(integer > 0){
            return new ResultUtils<>(null, "操作成功",true, integer,watch.getTime());
        }else {
            return new ResultUtils<>(null, "操作成功",false, integer,watch.getTime());
        }
    }


    @RequestMapping("/FH_modify_batchJYQuanJY")
    public ResultUtils FH_modify_batchJYQuanJY(@RequestBody ParamFields bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String dd_id = "",DayHour = "",ZhanData = "";
        List<String> ZhanID = new ArrayList<>();
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo,ParamFields.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if (null != bpPojo.getDd_id()){
            dd_id = bpPojo.getDd_id();
        }
        if (null != bpPojo.getDayhour()){
            DayHour = bpPojo.getDayhour();
        }
        if (null != bpPojo.getZhanData()){
            ZhanData = bpPojo.getZhanData();
        }
        if (null != bpPojo.getZhanID()){
            ZhanID = Arrays.asList(bpPojo.getZhanID().split(","));
        }
        Integer integer = service.FH_modify_batchJYQuanJY(dd_id, ZhanID, DayHour, ZhanData);
        watch.stop();
        if(integer > 0){
            return new ResultUtils<>(null, "操作成功",true, integer,watch.getTime());
        }else {
            return new ResultUtils<>(null, "操作成功",false, integer,watch.getTime());
        }
    }


    @RequestMapping("/modify_byTM")
    public ResultUtils modify_byTM(@RequestBody ParamFields bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String dd_id = "",DayHour = "",ZhanData = "",TM = "";
        List<String> ZhanID = new ArrayList<>();
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo,ParamFields.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if (null != bpPojo.getDd_id()){
            dd_id = bpPojo.getDd_id();
        }
        if (null != bpPojo.getDayhour()){
            DayHour = bpPojo.getDayhour();
        }
        if (null != bpPojo.getZhanData()){
            ZhanData = bpPojo.getZhanData();
        }
        if (null != bpPojo.getZhanID()){
            ZhanID = Arrays.asList(bpPojo.getZhanID().split(","));
        }
        if (null != bpPojo.getTM()){
            TM = bpPojo.getTM();
        }
        Integer integer = service.modify_byTM(TM,dd_id, ZhanID, DayHour, ZhanData);
        watch.stop();
        if(integer > 0){
            return new ResultUtils<>(null, "操作成功",true, integer,watch.getTime());
        }else {
            return new ResultUtils<>(null, "操作成功",false, integer,watch.getTime());
        }
    }

    @RequestMapping("/SWZZ_FLOODTIDEDATA_ST_TIDEHIGH_RSel")
    public ResultUtils SWZZ_FLOODTIDEDATA_ST_TIDEHIGH_RSel(@RequestBody ParamFields bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String stcd = "";
        String stime = "";
        String etime = "";
        String tdptn = "";
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo,ParamFields.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if (null != bpPojo.getStcd()){
            stcd = bpPojo.getStcd();
        }
        if (null != bpPojo.getStartdate()){
            stime = bpPojo.getStartdate();
        }
        if (null != bpPojo.getEnddate()){
            etime = bpPojo.getEnddate();
        }
        if (null != bpPojo.getTdptn()){
            tdptn = bpPojo.getTdptn();
        }
        List<ST_TIDEHIGHParam> tidehighParamList = service.SWZZ_FLOODTIDEDATA_ST_TIDEHIGH_RSel(stcd, stime, etime, tdptn);
        watch.stop();
        if(tidehighParamList.size() > 0){
            return new ResultUtils<>(tidehighParamList, "操作成功",true, tidehighParamList.size(),watch.getTime());
        }else {
            return new ResultUtils<>(tidehighParamList, "操作成功",false, tidehighParamList.size(),watch.getTime());
        }
    }

    @RequestMapping("/StatisticalCorrelationModelSW")
    public ResultUtils StatisticalCorrelationModelSW(@RequestBody ParamFields bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String solutionid = "";
        List<String> ddobj = new ArrayList<>();
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo,ParamFields.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if (null != bpPojo.getSolutionid()){
            solutionid = bpPojo.getSolutionid();
        }
        if (null != bpPojo.getDdobjson()){
            ddobj = JSON.parseArray(bpPojo.getDdobjson(),String.class);
        }
        Integer integer = service.StatisticalCorrelationModelSW(solutionid, ddobj);
        watch.stop();
        if(integer > 0){
            return new ResultUtils<>(null, "操作成功",true, integer,watch.getTime());
        }else {
            return new ResultUtils<>(null, "操作成功",false, integer,watch.getTime());
        }
    }

    @RequestMapping("/mx_recalculate")
    public ResultUtils mx_recalculate(@RequestBody ParamFields bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String solutionid = "",dd_id="";
        List<String> ddobj = new ArrayList<>();
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo,ParamFields.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if (null != bpPojo.getSolutionid()){
            solutionid = bpPojo.getSolutionid();
        }
        if (null != bpPojo.getDd_id()){
            dd_id = bpPojo.getDd_id();
        }
        Integer integer = service.mx_recalculate(solutionid,dd_id);
        watch.stop();
        if(integer > 0){
            return new ResultUtils<>(null, "操作成功",true, integer,watch.getTime());
        }else {
            return new ResultUtils<>(null, "操作成功",false, integer,watch.getTime());
        }
    }

    @RequestMapping("/startHuishuiJisuan")
    public ResultUtils startHuishuiJisuan(@RequestBody ParamFields bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String TM = "",jydatatype="",gcdatatype="",scwdatatype="";
        int num=0;
        List<String> ddobj = new ArrayList<>();
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo,ParamFields.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if (null != bpPojo.getTM()){
            TM = bpPojo.getTM();
        }
        if (null != bpPojo.getJydatatype()){
            jydatatype = bpPojo.getJydatatype();
        }
        if (null != bpPojo.getGcdatatype()){
            gcdatatype = bpPojo.getGcdatatype();
        }
        if (null != bpPojo.getScwdatatype()){
            scwdatatype = bpPojo.getScwdatatype();
        }
        if(bpPojo.getFpdr()!=null){
            num =Integer.parseInt( bpPojo.getFpdr());
        }
        int resultRows= huishuiApiService.startHuishuiJisuan(TM,num,jydatatype,gcdatatype,scwdatatype,"");
        watch.stop();
        if(resultRows>0){
            return new ResultUtils<>(null, "操作成功",true, resultRows,watch.getTime());
        }
        else{
            return new ResultUtils<>(null, "模型报错",false, resultRows,watch.getTime());
        }
    }

    @RequestMapping("/modifyModePlan")
    public ResultUtils modifyModePlan(@RequestBody ParamFields bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo,ParamFields.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        int resultRows= huishuiApiService.modifyModePlan();
        watch.stop();
        if(resultRows>0){
            return new ResultUtils<>(null, "操作成功",true, resultRows,watch.getTime());
        }
        else{
            return new ResultUtils<>(null, "模型报错",false, resultRows,watch.getTime());
        }
    }

    @RequestMapping("/ModifyGCSSLLAREAGCGZ_SZH")
    public ResultUtils ModifyGCSSLLAREAGCGZ_SZH(@RequestBody ParamFields bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String solutionid = "",gcddfa = "";
        List<String> a = new ArrayList<>();
        List<String> b = new ArrayList<>();
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo,ParamFields.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if(null != bpPojo.getGcddfa()){
            gcddfa = bpPojo.getGcddfa();
            String[] asList = gcddfa.split(",");
            for(String str : asList){
                String[] strings = str.split("@");
                a.add(strings[0]);
                b.add(strings[1]);
            }
        }
        if(null != bpPojo.getSolutionid()){
            solutionid = bpPojo.getSolutionid();
        }

        Integer num = service.ModifyGCSSLLAREAGCGZ_SZH(solutionid,a,b);
        watch.stop();
        if(num > 0){
            return new ResultUtils<>(num, "操作成功",true, num,watch.getTime());
        }else {
            return new ResultUtils<>(num, "操作成功",false, num,watch.getTime());
        }
    }
    @RequestMapping("/ModifyGCSSLLAREAGCGZ_SZHFJL")
    public ResultUtils ModifyGCSSLLAREAGCGZ_SZHFJL(@RequestBody ParamFields bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String solutionid = "",stime = "";
        if(null != bpPojo.getSolutionid()){
            solutionid = bpPojo.getSolutionid();
        }
        if(null != bpPojo.getTM()){
            stime = bpPojo.getTM();
        }
        Integer num = service.ModifyGCSSLLAREAGCGZ_SZHFJL(solutionid,stime);
        watch.stop();
        if(num > 0){
            return new ResultUtils<>(num, "操作成功",true, num,watch.getTime());
        }else {
            return new ResultUtils<>(num, "操作成功",false, num,watch.getTime());
        }
    }
}
