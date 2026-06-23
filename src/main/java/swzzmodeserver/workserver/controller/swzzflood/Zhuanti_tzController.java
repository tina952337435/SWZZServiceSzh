package swzzmodeserver.workserver.controller.swzzflood;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.util.concurrent.AtomicDouble;

import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swzzmodeserver.tools.CommonUtills;
import swzzmodeserver.tools.FieldIsValid;
import swzzmodeserver.tools.ParamField;
import swzzmodeserver.tools.ResultUtils;
import swzzmodeserver.tools.javalog;
import swzzmodeserver.workserver.data.swzzflood.ST_AREACWSTATION_BData;
import swzzmodeserver.workserver.data.swzzflood.ST_AREASTATION_BData;
import swzzmodeserver.workserver.data.swzzflood.ST_AREA_BData;
import swzzmodeserver.workserver.data.swzzflood.ST_PPTN_RData;
import swzzmodeserver.workserver.data.swzzflood.ST_TIDEH_RData;
import swzzmodeserver.workserver.data.swzzflood.Zhuanti_tzData;
import swzzmodeserver.workserver.pojo.swzzflood.ST_AREACWSTATION_BPojo;
import swzzmodeserver.workserver.pojo.swzzflood.ST_AREASTATION_BPojo;
import swzzmodeserver.workserver.pojo.swzzflood.ST_AREA_BPojo;
import swzzmodeserver.workserver.pojo.swzzflood.ST_PPTN_RPojo;
import swzzmodeserver.workserver.pojo.swzzflood.ST_TIDEH_RPojo;
import swzzmodeserver.workserver.pojo.swzzflood.ZHUANTI_TZAreaParamPojo;
import swzzmodeserver.workserver.pojo.swzzflood.Zhuanti_tzPojo;
import swzzmodeserver.workserver.pojo.swzzwater.Water_ST_STBPRP_B_STCDPojo;
import swzzmodeserver.workserver.data.swzzwater.Water_ST_STBPRP_B_STCDData;

import swzzmodeserver.workserver.pojo.swzzflood.Zhuanti_tznamePojo;

import  swzzmodeserver.workserver.pojo.swzzmode.ST_RVFCCH_BPojo;
import swzzmodeserver.workserver.pojo.swzzmode.ST_STBPRP_BPojo;
import  swzzmodeserver.workserver.data.swzzmode.ST_RVFCCH_BData;
import swzzmodeserver.workserver.data.swzzmode.ST_STBPRP_BData;

import  swzzmodeserver.workserver.pojo.swzzflood.Zhuanti_tzWrzPojo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/SWZZ_FLOOD_Zhuanti_tz")
public class Zhuanti_tzController {
    @Autowired
    private Zhuanti_tzData data;
    @Autowired
    private Water_ST_STBPRP_B_STCDData waterStStbprpBStcdData;

    @Autowired
    private ST_RVFCCH_BData stRvfcchBData;

    @Autowired
    private ST_STBPRP_BData stbprpBData;

    @Autowired
    private ST_AREA_BData stAreaBData;

     @Autowired
    private ST_PPTN_RData stPptnRData;

    @Autowired
    private ST_AREACWSTATION_BData stAreacwstationBData;

    @Autowired
    private ST_AREASTATION_BData stAreastationBData;

    @Value("${file.path.templatefilepath}")
    private String templatefilepath;

    @RequestMapping("/findResult")
    public ResultUtils findResult(@RequestBody ParamField paramField){
        StopWatch watch = new StopWatch();
        watch.start();
        String stm = "",etm = "";
        Integer pageIndex = null,pageSize = null;
        List<String> idList = new ArrayList<>();
        List<String> ztidList = new ArrayList<>();
        List<String> typeList = new ArrayList<>();
        ParamField params = FieldIsValid.getColumnName(paramField,ParamField.class);
        if(CommonUtills.isEmpty(params)){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if (null != paramField.getStcd()){
            idList = Arrays.asList(paramField.getStcd().split(","));
        }
        if (null != paramField.getStartdate()){
            stm = paramField.getStartdate();
        }
        if (null != paramField.getEnddate()){
            etm = paramField.getEnddate();
        }
        if(null!=paramField.getZTID()){
            ztidList=Arrays.asList(paramField.getZTID().split(","));
        }
        if (null != paramField.getPattem()){
            typeList = Arrays.asList(paramField.getPattem());
        }
        if (null != paramField.getPageindex() && null != paramField.getPagesize()){
            pageIndex = Integer.parseInt(paramField.getPageindex());
            pageSize = Integer.parseInt(paramField.getPagesize());
            PageHelper.startPage(pageIndex,pageSize);
            List<Zhuanti_tzPojo> zhuantiList = data.selectList(idList,ztidList,typeList);
            watch.stop();
            PageInfo<Zhuanti_tzPojo> pageInfo = new PageInfo<>(zhuantiList);
            if (pageInfo.getList().size() > 0){
                return new ResultUtils<List>(pageInfo.getList(),"操作成功",true,pageInfo.getSize(),pageInfo.getPageNum(),pageInfo.getPages(),pageInfo.getTotal(),zhuantiList.size(),watch.getTime());
            }else {
                return new ResultUtils<List>(null,"操作成功",false,pageInfo.getSize(),pageInfo.getPageNum(),pageInfo.getPages(),pageInfo.getTotal(),zhuantiList.size(),watch.getTime());
            }
        }
        List<Zhuanti_tzPojo> zhuantiList = data.selectList(idList,ztidList,typeList);
        watch.stop();
        if (zhuantiList.size() > 0){
            return new ResultUtils<List>(zhuantiList,"操作成功",true,zhuantiList.size(),watch.getTime());
        }else {
            return new ResultUtils<List>(null,"操作成功",false,zhuantiList.size(),watch.getTime());
        }
    }

    @RequestMapping("/findResultName")
    public ResultUtils findResultName(@RequestBody ParamField paramField){
        StopWatch watch = new StopWatch();
        watch.start();
        String stm = "",etm = "";
        Integer pageIndex = null,pageSize = null;
        List<String> idList = new ArrayList<>();
        List<String> ztidList = new ArrayList<>();
        List<String> typeList =new ArrayList<>();
        ParamField params = FieldIsValid.getColumnName(paramField,ParamField.class);
        if(CommonUtills.isEmpty(params)){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if (null != paramField.getStcd()){
            idList = Arrays.asList(paramField.getStcd().split(","));
        }
        if (null != paramField.getStartdate()){
            stm = paramField.getStartdate();
        }
        if (null != paramField.getEnddate()){
            etm = paramField.getEnddate();
        }
        if(null!=paramField.getZTID()){
            ztidList=Arrays.asList(paramField.getZTID().split(","));
        }
        if (null != paramField.getPattem()){
            typeList = Arrays.asList(paramField.getPattem());
        }
        List<Zhuanti_tzPojo> zhuantiList = data.selectList(idList,ztidList,typeList);
        List<String> baseTYPE = new ArrayList<>();
        List<Water_ST_STBPRP_B_STCDPojo> baseList= waterStStbprpBStcdData.selectList(baseTYPE);

        List<Zhuanti_tznamePojo> userData=new ArrayList<>();
        zhuantiList.forEach(m->{
            Zhuanti_tznamePojo dto=new Zhuanti_tznamePojo();
            dto.setSTCD(m.getSTCD());
            dto.setZT_ID(m.getZT_ID());
            dto.setMAXVALUE(m.getMAXVALUE());
            dto.setTYPE(m.getTYPE());
            try {
                List<Water_ST_STBPRP_B_STCDPojo> baseListTemp=new ArrayList<>();
                if(m.getTYPE().equals("2")) {
                    baseListTemp=baseList.stream().filter(n -> {
                        if (n.getSTCD() != null) {
                            return n.getSTCD().equals(m.getSTCD());
                        }
                        return false;
                    }).collect(Collectors.toList());
                }
                else if(m.getTYPE().equals("1")){
                    baseListTemp=baseList.stream().filter(n -> {
                        if (n.getSTCD() != null) {
                            return n.getZSTCD().equals(m.getSTCD());
                        }
                        return false;
                    }).collect(Collectors.toList());
                }
                if(baseListTemp.size()>0){
                    dto.setSTNM(baseListTemp.get(0).getSTNM());
                }
            } catch (Exception e) {
                // TODO: handle exception
                new   javalog().writelog("findResultName报错："+e.getMessage(),templatefilepath);           
            }
            
            userData.add(dto);
        });

        watch.stop();
        if (userData.size() > 0){
            return new ResultUtils<List>(userData,"操作成功",true,userData.size(),watch.getTime());
        }else {
            return new ResultUtils<List>(null,"操作成功",false,userData.size(),watch.getTime());
        }
    }

    @RequestMapping("/findResultWrz")
    public ResultUtils findResultWrz(@RequestBody ParamField paramField){
        StopWatch watch = new StopWatch();
        watch.start();
        List<String> typeList =new ArrayList<>();
        Integer pageIndex = null,pageSize = null;
        List<String> idList = new ArrayList<>();
        List<String> ztidList = new ArrayList<>();
        ParamField params = FieldIsValid.getColumnName(paramField,ParamField.class);
        if(CommonUtills.isEmpty(params)){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if (null != paramField.getStcd()){
            idList = Arrays.asList(paramField.getStcd().split(","));
        }
        if (null != paramField.getPattem()){
            typeList = Arrays.asList(paramField.getPattem());
        }
        if(null!=paramField.getZTID()){
            ztidList=Arrays.asList(paramField.getZTID().split(","));
        }
        List<Zhuanti_tzPojo> zhuantiList = data.selectList(idList,ztidList,typeList);
        List<String> baseTYPE = new ArrayList<>();
        List<Water_ST_STBPRP_B_STCDPojo> baseList= waterStStbprpBStcdData.selectList(baseTYPE);

        //特征值表
        List<ST_RVFCCH_BPojo> listCH = stRvfcchBData.selectList(null,null,null,null,null);
        //基础站点信息表
        List<ST_STBPRP_BPojo> listBase = stbprpBData.selectList(null,null,null,null,null,null);


        List<Zhuanti_tzWrzPojo> userData=new ArrayList<>();
        zhuantiList.forEach(m->{
            Zhuanti_tzWrzPojo dto=new Zhuanti_tzWrzPojo();
            dto.setSTCD(m.getSTCD());
            dto.setMAXZ(m.getMAXVALUE());

            try {
                 List<Water_ST_STBPRP_B_STCDPojo> baseListTemp=baseList.stream().filter(n -> {
                if (n.getSTCD() != null) {
                    return n.getZSTCD().equals(m.getSTCD());
                }
                return false;
                }).collect(Collectors.toList());

                if(baseListTemp.size()>0){
                    dto.setSTNM(baseListTemp.get(0).getSTNM());                
                    List<ST_RVFCCH_BPojo> listCHTemp = listCH.stream().filter(n -> {
                        if (n.getSTCD() != null) {
                            return n.getSTCD().equals(baseListTemp.get(0).getSTCD());
                        }
                        return false;
                    }).collect(Collectors.toList());

                    if (listCHTemp.size() > 0)
                    {
                        dto.setWRZ(listCHTemp.get(0).getWRZ());
                    }
                    List<ST_STBPRP_BPojo> listBaseTemp= listBase.stream().filter(n -> {
                        if (n.getSTCD() != null) {
                            return n.getSTCD().equals(baseListTemp.get(0).getSTCD());
                        }
                        return false;
                    }).collect(Collectors.toList());
                    if (listBaseTemp.size() > 0)
                    {
                        dto.setLGTD(listBaseTemp.get(0).getLGTD());
                        dto.setLTTD(listBaseTemp.get(0).getLTTD());
                    }
                }
            
            } catch (Exception e) {
                new   javalog().writelog("findResultWrz报错："+e.getMessage(),templatefilepath);                
            }
            userData.add(dto);
        });

        watch.stop();
        if (userData.size() > 0){
            return new ResultUtils<List>(userData,"操作成功",true,userData.size(),watch.getTime());
        }else {
            return new ResultUtils<List>(null,"操作成功",false,userData.size(),watch.getTime());
        }
    }

    @RequestMapping("/add")
    public ResultUtils add(@RequestBody Zhuanti_tzPojo bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo, Zhuanti_tzPojo.class))){
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
    public ResultUtils modify(@RequestBody Zhuanti_tzPojo bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo, Zhuanti_tzPojo.class))){
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
    public ResultUtils batchResult(@PathVariable("pattem") String pattem, @RequestBody List<Zhuanti_tzPojo> bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String type = "";
        if(CommonUtills.isEmpty(FieldIsValid.getListColumnName(bpPojo, Zhuanti_tzPojo.class)) || !FieldIsValid.isValid(pattem)){
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
        List<Zhuanti_tzPojo> list = new ArrayList<>();
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

    @RequestMapping("/ZHUANTIAreaJY")
    public ResultUtils ZHUANTIAreaJY(@RequestBody ParamField paramField){
        StopWatch watch = new StopWatch();
        watch.start();
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(paramField, ParamField.class))){
             watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        List<ST_AREACWSTATION_BPojo> listB = stAreacwstationBData.selectList(null, new ArrayList<>(), null);
        List<ST_AREA_BPojo> listArea=stAreaBData.selectList(paramField.getStcd(),paramField.getKwtxt(),paramField.getPid());
        List<ST_PPTN_RPojo> listPPTN=stPptnRData.selectList(paramField.getStartdate(),paramField.getEnddate(),new ArrayList<>());

        List<ZHUANTI_TZAreaParamPojo> zhuantiList = new ArrayList<>();
        listArea.forEach(u->{
            List<ST_AREACWSTATION_BPojo> listBTemp=listB.stream().filter(v->v.getPAID().equals(u.getAID())).collect(Collectors.toList());
            List<String> stcdList = listBTemp.stream().map(p -> p.getSTCD()).collect(Collectors.toList());
            List<ST_PPTN_RPojo> listPPTNTemp = listPPTN.stream()
            .filter(p -> stcdList.contains(p.getSTCD()))
            .collect(Collectors.toList());
            ZHUANTI_TZAreaParamPojo dto = new ZHUANTI_TZAreaParamPojo();
            dto.setAID(u.getAID());
            dto.setAREANAME(u.getAREANAME());
            double total = listPPTNTemp.stream()
            .mapToDouble(p -> p.getDRP() != null ? p.getDRP() : 0.0)
            .sum() / stcdList.size();
            double drpAvg = Math.round(total * 10.0) / 10.0;
            dto.setDRP(drpAvg);
            zhuantiList.add(dto);
        });
        watch.stop();
        if (zhuantiList.size() > 0){
            return new ResultUtils<List>(zhuantiList,"操作成功",true,zhuantiList.size(),watch.getTime());
        }else {
            return new ResultUtils<List>(null,"操作成功",false,zhuantiList.size(),watch.getTime());
        }
    }

    @RequestMapping("/ZHUANTIAreaJYDay")
    public ResultUtils ZHUANTIAreaJYDay(@RequestBody ParamField paramField){
        StopWatch watch = new StopWatch();
        watch.start();
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(paramField, ParamField.class))){
             watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        List<ST_AREASTATION_BPojo> listB = stAreastationBData.selectList(null, new ArrayList<>(), null,null);
        List<ST_AREA_BPojo> listArea=stAreaBData.selectList(paramField.getStcd(),paramField.getKwtxt(),paramField.getPid());
        List<ST_PPTN_RPojo> listPPTN=stPptnRData.selectList(paramField.getStartdate(),paramField.getEnddate(),new ArrayList<>());

        List<ZHUANTI_TZAreaParamPojo> zhuantiList = new ArrayList<>();
        listArea.forEach(u->{
            List<ST_AREASTATION_BPojo> listBTemp=listB.stream().filter(v->v.getPAID().equals(u.getAID())).collect(Collectors.toList());
            AtomicDouble drpAvg = new AtomicDouble(0);
            listBTemp.forEach(item -> {//泰森多边形算平均降雨量
                 List<ST_PPTN_RPojo>  listPPTNTemp = listPPTN.stream()
                 .filter(p -> item.getSTCD().equals(p.getSTCD()))
                 .collect(Collectors.toList());
                 double arearatio = item.getAREARATIO();
                 double totaldrp=listPPTNTemp.stream()
            .mapToDouble(p -> p.getDRP() != null ? p.getDRP() : 0.0)
            .sum();
                 double drp =totaldrp * arearatio;
                 drpAvg.addAndGet(drp);  // 原子操作累加
            });
            ZHUANTI_TZAreaParamPojo dto = new ZHUANTI_TZAreaParamPojo();
            dto.setAID(u.getAID());
            dto.setAREANAME(u.getAREANAME());            
            drpAvg.set(Math.round(drpAvg.get() * 10.0) / 10.0);
            dto.setDRP(drpAvg.get());
            zhuantiList.add(dto);
        });
        watch.stop();
        if (zhuantiList.size() > 0){
            return new ResultUtils<List>(zhuantiList,"操作成功",true,zhuantiList.size(),watch.getTime());
        }else {
            return new ResultUtils<List>(null,"操作成功",false,zhuantiList.size(),watch.getTime());
        }
    }

}
