package swzzmodeserver.workserver.controller.swzzmode;

import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swzzmodeserver.tools.*;
import swzzmodeserver.workserver.data.swzzmode.BDMS_PREDICTData;
import swzzmodeserver.workserver.data.swzzmode.DD_SOLUTIONData;
import swzzmodeserver.workserver.data.swzzmode.ST_RVFCCH_BData;
import swzzmodeserver.workserver.pojo.swzzmode.*;
import swzzmodeserver.workserver.pojo.swzzqxsj.TzgriddataPojo;
import swzzmodeserver.workserver.pojo.swzzflood.ST_WAS_RPojo;
import swzzmodeserver.workserver.service.swzzmode.BDMS_PREDICTService;
import swzzmodeserver.workserver.service.swzzmode.ES_ZHANDIANDATAService;
import swzzmodeserver.workserver.service.swzzmode.ES_ZHANDIANDATAServiceImpl;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/SWZZ_MODE_BDMS_PREDICT")
public class BDMS_PREDICTController {
    @Autowired
    private BDMS_PREDICTData data;
    @Autowired
    private BDMS_PREDICTService service;

    @Autowired
    private DD_SOLUTIONData ddSolutionData;

    @RequestMapping("/findResult")
    public ResultUtils findResult(@RequestBody ParamField bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String ID = "",key = "",pageindex = "",pagesize = "10";
        List<String> stcdlist = new ArrayList<>();
        String type ="";
        List<String> PLAN_N = new ArrayList<>();
        String stime = "",etime = "";
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo, ParamField.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if(null != bpPojo.getStcd()){
            stcdlist=Arrays.asList(bpPojo.getStcd().split(","));
        }
        if(null != bpPojo.getPid()){
            PLAN_N=Arrays.asList(bpPojo.getPid().split(","));
        }
        else 
        {
            String stimeF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date().getTime() - 72 * 60 * 60 * 1000);
            //查询最新的
            List<DD_SOLUTIONPojo> ListDDTop=ddSolutionData.selectListNew(null,stimeF,null);
            if(ListDDTop.size()>0){
                String solutionid=ListDDTop.get(0).getDD_ID();
                PLAN_N=Arrays.asList(solutionid.split(","));
            }
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
       if(null != bpPojo.getPattem()){
           type = bpPojo.getPattem();
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
        List<BDMS_PREDICTPojo> fxList = data.selectList(ID,stime,etime,PLAN_N,null,type,startindex, Integer.valueOf(pagesize),"asc",stcdlist);
        Integer integer = data.selectCount(ID,stime,etime,null,"","");
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
    public ResultUtils add(@RequestBody BDMS_PREDICTPojo bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo, BDMS_PREDICTPojo.class))){
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
    public ResultUtils modify(@RequestBody BDMS_PREDICTPojo bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo, BDMS_PREDICTPojo.class))){
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
    public ResultUtils batchResult(@PathVariable("pattem") String pattem, @RequestBody List<BDMS_PREDICTPojo> bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String type = "";
        if(CommonUtills.isEmpty(FieldIsValid.getListColumnName(bpPojo, BDMS_PREDICTPojo.class)) || !FieldIsValid.isValid(pattem)){
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
        List<BDMS_PREDICTPojo> list = new ArrayList<>();
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

    @RequestMapping("/GetFangAnData")
    public ResultUtils GetFangAnData(@RequestBody ParamFields bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo, ParamFields.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        String DD_MIND = "",STCD = "",DATA_TYPE = "",DD_EVALUE = "",MKEYID = "";
        if(null != bpPojo.getDD_MIND()){
            DD_MIND = bpPojo.getDD_MIND();
        }
        if(null != bpPojo.getStcd()){
            STCD = bpPojo.getStcd();
        }
        if(null != bpPojo.getDATA_TYPE()){
            DATA_TYPE = bpPojo.getDATA_TYPE();
        }
        if(null != bpPojo.getDD_EVALUE()){
            DD_EVALUE = bpPojo.getDD_EVALUE();
        }
        if(null != bpPojo.getMKEYID()){
            MKEYID = bpPojo.getMKEYID();
        }
        String json = service.GetFangAnData(DD_MIND,STCD,DATA_TYPE,DD_EVALUE,MKEYID);
        watch.stop();
        if(null != json){
            return new ResultUtils<>(json, "操作成功",true, 1,watch.getTime());
        }else {
            return new ResultUtils<>(json, "操作成功",false, 0,watch.getTime());
        }
    }

    @RequestMapping("/GetFangAnDataDuo")
    public ResultUtils GetFangAnDataDuo(@RequestBody ParamFields bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo, ParamFields.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        String dd_id = "",STCD = "",DATA_TYPE = "",DD_EVALUE = "",MKEYID = "";
        if(null != bpPojo.getDd_id()){
            dd_id = bpPojo.getDd_id();
        }
        if(null != bpPojo.getStcd()){
            STCD = bpPojo.getStcd();
        }
        if(null != bpPojo.getDATA_TYPE()){
            DATA_TYPE = bpPojo.getDATA_TYPE();
        }
        if(null != bpPojo.getDD_EVALUE()){
            DD_EVALUE = bpPojo.getDD_EVALUE();
        }
        if(null != bpPojo.getMKEYID()){
            MKEYID = bpPojo.getMKEYID();
        }
        String json = service.GetFangAnDataDuo(dd_id,STCD,DATA_TYPE,DD_EVALUE,MKEYID);
        watch.stop();
        if(null != json){
            return new ResultUtils<>(json, "操作成功",true, 1,watch.getTime());
        }else {
            return new ResultUtils<>(json, "操作成功",false, 0,watch.getTime());
        }
    }

    @RequestMapping("/AREASL")
    public ResultUtils AREASL(@RequestBody ParamFields bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String dd_id = "",STCD = "";
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo, ParamFields.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if(null != bpPojo.getDd_id()){
            dd_id = bpPojo.getDd_id();
        }
        if(null != bpPojo.getStcd()){
            STCD = bpPojo.getStcd();
        }

        List<AREASLParam> areasl = service.AREASL(dd_id, STCD);
        watch.stop();
        if(areasl.size() > 0){
            return new ResultUtils<>(areasl, "操作成功",true, areasl.size(),watch.getTime());
        }else {
            return new ResultUtils<>(areasl, "操作成功",false, areasl.size(),watch.getTime());
        }
    }

    @RequestMapping("/YBList")
    public ResultUtils YBList(@RequestBody ParamFields bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String solutionid = "",typeID = "",TM = "";
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo, ParamFields.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if(null != bpPojo.getSolutionid()){
            solutionid = bpPojo.getSolutionid();
        }
        if(null != bpPojo.getTypeID()){
            typeID = bpPojo.getTypeID();
        }
        if(null != bpPojo.getTM()){
            TM = bpPojo.getTM();
        }
        List<YBList> ybListList = service.YBList(solutionid, typeID, TM);
        watch.stop();
        if(ybListList.size() > 0){
            return new ResultUtils<>(ybListList, "操作成功",true, ybListList.size(),watch.getTime());
        }else {
            return new ResultUtils<>(ybListList, "操作成功",false, ybListList.size(),watch.getTime());
        }
    }

    @RequestMapping("/WJ_MODELSINGRESULT")
    public ResultUtils WJ_MODELSINGRESULT(@RequestBody ParamFields bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String solutionid = "",data_type = "";
        List<String> stcd = new ArrayList<>();
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo, ParamFields.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if(null != bpPojo.getSolutionid()){
            solutionid = bpPojo.getSolutionid();
        }
        else{//查询最新的方案
            List<DD_SOLUTIONPojo> ListDDTop=ddSolutionData.selectListNew(null,bpPojo.getStartdate(),bpPojo.getEnddate());
            if(ListDDTop.size()>0){
                solutionid=ListDDTop.get(0).getDD_ID();
            }
        }
        if(null != bpPojo.getDATA_TYPE()){
            data_type = bpPojo.getDATA_TYPE();
        }
        if(null != bpPojo.getStcd()){
            stcd = Arrays.asList(bpPojo.getStcd().split(","));
        }
        List<WJ_MODELSINGRESULTParam> ybListList = service.WJ_MODELSINGRESULT(Arrays.asList(solutionid.split(",")),
                stcd,data_type);
        watch.stop();
        if(ybListList.size() > 0){
            return new ResultUtils<>(ybListList, "操作成功",true, ybListList.size(),watch.getTime());
        }else {
            return new ResultUtils<>(ybListList, "操作成功",false, ybListList.size(),watch.getTime());
        }
    }

    @RequestMapping("/MODE_BDMS_PREDICTSelMORE")
    public ResultUtils MODE_BDMS_PREDICTSelMORE(@RequestBody ParamFields bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String plan_n = "",stcd = "",data_type = "",stime = "",etime = "";
        if (null != bpPojo.getPlan_n()){
            plan_n = bpPojo.getPlan_n();
        }
        if (null != bpPojo.getStcd()){
            stcd = bpPojo.getStcd();
        }
        if (null != bpPojo.getDATA_TYPE()){
            data_type = bpPojo.getDATA_TYPE();
        }
        if (null != bpPojo.getStartdate()){
            stime = bpPojo.getStartdate();
        }
        if (null != bpPojo.getEnddate()){
            etime = bpPojo.getEnddate();
        }
        //String finalStcd = stcd;
        List<Map<String, String>> mapList = service.MODE_BDMS_PREDICTSelMORE(stime, etime,Arrays.asList(plan_n.split(",")), stcd, data_type);
        watch.stop();
        if(mapList.size() > 0){
            return new ResultUtils<>(mapList, "操作成功",true, mapList.size(),watch.getTime());
        }else {
            return new ResultUtils<>(mapList, "操作成功",false, mapList.size(),watch.getTime());
        }
    }

    @RequestMapping("/WJ_MODELSINGRESULTHL")
    public ResultUtils WJ_MODELSINGRESULTHL(@RequestBody ParamFields bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String solutionid = "",data_type = "";
        List<String> stcd = new ArrayList<>();
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo, ParamFields.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if(null != bpPojo.getSolutionid()){
            solutionid = bpPojo.getSolutionid();
        }
        if(null != bpPojo.getDATA_TYPE()){
            data_type = bpPojo.getDATA_TYPE();
        }
        if(null != bpPojo.getStcd()){
            stcd = Arrays.asList(bpPojo.getStcd().split(","));
        }
        List<WJ_MODELSINGRESULTHLParam> ybListList = service.WJ_MODELSINGRESULTHL(Arrays.asList(solutionid.split(",")),
                stcd,data_type);
        watch.stop();
        if(ybListList.size() > 0){
            return new ResultUtils<>(ybListList, "操作成功",true, ybListList.size(),watch.getTime());
        }else {
            return new ResultUtils<>(ybListList, "操作成功",false, ybListList.size(),watch.getTime());
        }
    }
}
