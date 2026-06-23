package swzzmodeserver.workserver.controller.swzzflood;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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
import swzzmodeserver.workserver.data.swzzflood.ST_TIDEH_RData;
import swzzmodeserver.workserver.data.swzzflood.ST_TIDE_RData;
import swzzmodeserver.workserver.data.swzzflood.ZhuantiData;
import swzzmodeserver.workserver.data.swzzmode.ST_STBPRP_BData;
import swzzmodeserver.workserver.pojo.swzzmode.ST_STBPRP_BPojo;
import swzzmodeserver.workserver.data.swzzmode.ST_RVFCCH_BData;
import swzzmodeserver.workserver.pojo.swzzmode.ST_RVFCCH_BPojo;
import swzzmodeserver.workserver.pojo.swzzflood.ST_TIDEH_RParam;
import swzzmodeserver.workserver.pojo.swzzflood.ST_TIDEH_RPojo;
import swzzmodeserver.workserver.pojo.swzzflood.SWZZ_FLOODTIDEDATA_ST_TIDEHIGH_RTJParam;
import swzzmodeserver.workserver.pojo.swzzflood.ZhuantiPojo;
import swzzmodeserver.workserver.service.swzzflood.ST_TIDEH_RService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Comparator;

@RestController
@RequestMapping("/SWZZ_FLOOD_ST_TIDEH_R")
public class ST_TIDEH_RController {
    @Autowired
    private ST_TIDEH_RData data;
    @Autowired
    private ST_TIDEH_RService service;
    @Autowired
    private ST_STBPRP_BData stBprpData;

    @Autowired
    private ST_RVFCCH_BData stRvfcchData;

    @Autowired
    private ST_TIDE_RData stTideData;

    @RequestMapping("/findResult")
    public ResultUtils findResult(@RequestBody ParamField paramField){
        StopWatch watch = new StopWatch();
        watch.start();
        String stm = "",etm = "";
        Integer pageIndex = null,pageSize = null;
        List<String> idList = new ArrayList<>();
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
        if (null != paramField.getPageindex() && null != paramField.getPagesize()){
            pageIndex = Integer.parseInt(paramField.getPageindex());
            pageSize = Integer.parseInt(paramField.getPagesize());
            PageHelper.startPage(pageIndex,pageSize);
            List<ST_TIDEH_RPojo> zhuantiList = data.selectTideHList(idList, stm,etm);
            watch.stop();
            PageInfo<ST_TIDEH_RPojo> pageInfo = new PageInfo<>(zhuantiList);
            if (pageInfo.getList().size() > 0){
                return new ResultUtils<List>(pageInfo.getList(),"操作成功",true,pageInfo.getSize(),pageInfo.getPageNum(),pageInfo.getPages(),pageInfo.getTotal(),zhuantiList.size(),watch.getTime());
            }else {
                return new ResultUtils<List>(null,"操作成功",false,pageInfo.getSize(),pageInfo.getPageNum(),pageInfo.getPages(),pageInfo.getTotal(),zhuantiList.size(),watch.getTime());
            }
        }
        List<ST_TIDEH_RPojo> zhuantiList = data.selectTideHList(idList, stm,etm);
        watch.stop();
        if (zhuantiList.size() > 0){
            return new ResultUtils<List>(zhuantiList,"操作成功",true,zhuantiList.size(),watch.getTime());
        }else {
            return new ResultUtils<List>(null,"操作成功",false,zhuantiList.size(),watch.getTime());
        }
    }

    @RequestMapping("/add")
    public ResultUtils add(@RequestBody ST_TIDEH_RPojo bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo, ST_TIDEH_RPojo.class))){
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
    public ResultUtils modify(@RequestBody ST_TIDEH_RPojo bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo, ST_TIDEH_RPojo.class))){
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
    public ResultUtils batchResult(@PathVariable("pattem") String pattem, @RequestBody List<ST_TIDEH_RPojo> bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String type = "";
        if(CommonUtills.isEmpty(FieldIsValid.getListColumnName(bpPojo, ST_TIDEH_RPojo.class)) || !FieldIsValid.isValid(pattem)){
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
        List<ST_TIDEH_RPojo> list = new ArrayList<>();
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

    @RequestMapping("/computeHL")
    public ResultUtils computeHL(@RequestBody ParamField bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String stcd = "",stime = "",etime = "";
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo, ParamField.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if(null != bpPojo.getStcd()){
            stcd = bpPojo.getStcd();
        }
        if (null != bpPojo.getStartdate()){
            stime = bpPojo.getStartdate();
        }
        if (null != bpPojo.getEnddate()){
            etime = bpPojo.getEnddate();
        }
        List<Map<String, Object>> mapList = service.computeHL(stime, etime, stcd);
        watch.stop();
        if(mapList.size() > 0){
            return new ResultUtils<>(mapList, "操作成功",true, mapList.size(),watch.getTime());
        }else {
            return new ResultUtils<>(mapList, "操作成功",false, mapList.size(),watch.getTime());
        }
    }

    @RequestMapping("/findResultTJ")
    public ResultUtils findResultTJ(@RequestBody ParamField paramField){
        StopWatch watch = new StopWatch();
        watch.start();
        StringBuilder sql = new StringBuilder();
        sql.append("select r.*, h.TDZ as HTDZ, r.TDZ - h.TDZ as TDZZS ");
        sql.append("from ST_TIDEH_R h inner join ST_TIDE_R r on h.STCD = r.STCD and h.TM = r.TM ");

        String stcds = "63401750,62701710,63405800,63401100,63401500,63405900";
        if (paramField.getStcd() != null) {
            stcds = paramField.getStcd();
        }
        String stime="",etime="";
        List<String> aggStcd = Arrays.asList(stcds.split(","));
        if (paramField.getStartdate() != null) {
            stime= paramField.getStartdate();
        }
        if (paramField.getEnddate() != null) {
            etime= paramField.getEnddate();
        }

        String TDPTN = "1";
        if (paramField.getKwtxt() != null) {
            TDPTN = paramField.getKwtxt();
        }
        sql.append(" and r.TDPTN = '").append(TDPTN).append("'");
        sql.append(" order by r.tm asc");

        List<ST_TIDEH_RParam> userList =stTideData.selectTideTJ(aggStcd,stime,etime,TDPTN);


        List<ST_STBPRP_BPojo> listB = stBprpData.selectList(null, null, null, null, null, null);
        List<ST_RVFCCH_BPojo> listRB = stRvfcchData.selectList(null, null, null, null, null);

        Map<String, List<ST_TIDEH_RParam>> groupedByStcd = userList.stream()
            .collect(Collectors.groupingBy(ST_TIDEH_RParam::getSTCD));

        List<SWZZ_FLOODTIDEDATA_ST_TIDEHIGH_RTJParam> listData = new ArrayList<>();

        System.out.println("groupedByStcd的长度::::::::::::"+groupedByStcd.size());

        groupedByStcd.forEach((stcd, userListTemp) -> {
            SWZZ_FLOODTIDEDATA_ST_TIDEHIGH_RTJParam dto = new SWZZ_FLOODTIDEDATA_ST_TIDEHIGH_RTJParam();
            dto.setSTCD(stcd);
            
            double maxZ = userListTemp.stream()
                .mapToDouble(p -> p.getTDZ().doubleValue())
                .max()
                .orElse(0.0);
            dto.setMAXZ(maxZ);
            
            List<ST_TIDEH_RParam> maxZList = userListTemp.stream()
                .filter(p -> p.getTDZ().doubleValue() == maxZ)
                .collect(Collectors.toList());
            
            if (!maxZList.isEmpty()) {
                ST_TIDEH_RParam firstMax = maxZList.get(0);
                dto.setMAXZTM(firstMax.getTM());
                dto.setMAXZZS(firstMax.getTDZZS().doubleValue());
                dto.setMAXZZSTM(firstMax.getTM());
            }
            
            double maxZS = userListTemp.stream()
                .mapToDouble(p -> p.getTDZZS().doubleValue())
                .max()
                .orElse(0.0);
            dto.setMAXZS(maxZS);
            
            List<ST_TIDEH_RParam> maxZSList = userListTemp.stream()
                .filter(p -> p.getTDZZS().doubleValue() == maxZS)
                .collect(Collectors.toList());
            
            if (!maxZSList.isEmpty()) {
                dto.setMAXZSTM(maxZSList.get(0).getTM());
            }
            
            List<ST_STBPRP_BPojo> listBTemp = listB.stream()
                .filter(u -> stcd.equals(u.getSTCD()))
                .collect(Collectors.toList());
            if (!listBTemp.isEmpty()) {
                dto.setSTNM(listBTemp.get(0).getSTNM());
            }
            
            List<ST_RVFCCH_BPojo> listRBTemp = listRB.stream()
                .filter(p -> stcd.equals(p.getSTCD()))
                .collect(Collectors.toList());
            if (!listRBTemp.isEmpty()) {
                dto.setWRZ(listRBTemp.get(0).getWRZ().doubleValue());
                dto.setWRZFD(dto.getMAXZ() - dto.getWRZ());
            }
            
            // 超警戒天数
            Map<String, Double> dailyMax = userListTemp.stream()
            .collect(Collectors.groupingBy(
                // 直接截取字符串前10位作为分组的Key，简单且高效
                d -> d.getTM().substring(0, 10),
                Collectors.collectingAndThen(
                    Collectors.maxBy(Comparator.comparingDouble(p -> p.getTDZ().doubleValue())),
                    opt -> opt.map(p -> p.getTDZ().doubleValue()).orElse(0.0)
                )
            ));
            
            long wrzCount = dailyMax.values().stream()
                .filter(maxZVal -> maxZVal >= dto.getWRZ())
                .count();
            dto.setWRZCOUT((double) wrzCount);            
            listData.add(dto);
        });
    
        watch.stop();
        if (listData.size() > 0){
            return new ResultUtils<List>(listData,"操作成功",true,listData.size(),watch.getTime());
        }else {
            return new ResultUtils<List>(null,"操作成功",false,listData.size(),watch.getTime());
        }
    }
}
