package swzzmodeserver.workserver.controller.swzzmode;

import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swzzmodeserver.tools.*;
import swzzmodeserver.workserver.data.swzzmode.ES_SLTONGJIData;
import swzzmodeserver.workserver.data.swzzmode.ES_ZHANDIANDATAData;
import swzzmodeserver.workserver.data.swzzmode.ES_ZHANDIANData;
import swzzmodeserver.workserver.data.swzzqxsj.Tz_watersheddataData;
import swzzmodeserver.workserver.pojo.swzzmode.*;
import swzzmodeserver.workserver.pojo.swzzqxsj.Tz_watersheddataPojo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/SWZZ_MODE_ES_ZHANDIAN")
public class ES_ZHANDIANController {
    @Autowired
    private ES_ZHANDIANData data;
    @Autowired
    private Tz_watersheddataData tzWatersheddataData;
    @Autowired
    private ES_ZHANDIANDATAData esZhandiandataData;

    @Autowired
    private ES_SLTONGJIData esSltongjIData;

    @RequestMapping("/findResult")
    public ResultUtils findResult(@RequestBody ParamField bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String ID = "",key = "",pageindex = "",pagesize = "10";
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
        Integer startindex = null;
        if(!"".equals(pageindex) && !"".equals(pagesize)){
            startindex = (Integer.parseInt(pageindex) - 1) * Integer.parseInt(pagesize);
        }
        List<ES_ZHANDIANPojo> fxList = data.selectList(ID,startindex, Integer.valueOf(pagesize),type,key);
        Integer integer = data.selectCount(ID,type,key);
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
    public ResultUtils add(@RequestBody ES_ZHANDIANPojo bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo,ES_ZHANDIANPojo.class))){
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
    public ResultUtils modify(@RequestBody ES_ZHANDIANPojo bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo,ES_ZHANDIANPojo.class))){
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
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo,ParamField.class))){
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
    public ResultUtils batchResult(@PathVariable("pattem") String pattem, @RequestBody List<ES_ZHANDIANPojo> bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String type = "";
        if(CommonUtills.isEmpty(FieldIsValid.getListColumnName(bpPojo,ES_ZHANDIANPojo.class)) || !FieldIsValid.isValid(pattem)){
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
        List<ES_ZHANDIANPojo> list = new ArrayList<>();
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
    public ResultUtils batchRemove(@RequestBody List<ParamField> bpPojo){
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

    @RequestMapping("/FH_GetBind")
    public ResultUtils FH_GetBind(@RequestBody ParamFields bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String startdate = "",enddate = "",solutionid = "0",dayhour = "";
        List<String> datatype = new ArrayList<>();
        List<ES_ZHANDIANDto> DtoList = new ArrayList<>();
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo,ParamFields.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if(null != bpPojo.getSolutionid()){
            solutionid = bpPojo.getSolutionid();
        }
        if(null != bpPojo.getDatatype()){
            datatype = Arrays.asList(bpPojo.getDatatype().split(","));
        }
        if(null != bpPojo.getStartdate()){
            startdate = bpPojo.getStartdate();
        }
        if(null != bpPojo.getEnddate()){
            enddate = bpPojo.getEnddate();
        }
        if(null != bpPojo.getDayhour()){
            dayhour = bpPojo.getDayhour();
        }

        if(dayhour.equals("day")){
            DtoList = data.selectES_ZHANDIANAndDATAByDay(solutionid,datatype,startdate,enddate);
        }else {
            DtoList = data.selectES_ZHANDIANAndDATAByHour(solutionid,datatype,startdate,enddate);
        }
        Set<String> set = new TreeSet<>();
        List<Map<String,String>> mapList = new ArrayList<>();
        for(ES_ZHANDIANDto obj : DtoList){
            if(obj.getZHANTIME() != null){
                if(":00:00".equals(obj.getZHANTIME())){
                    continue;
                }
                set.add(obj.getZHANTIME());
            }
        }
        for(String time : set){
            Map<String,String> map = new HashMap<>();
            map.put("ID","");
            map.put("TM",time);
            DtoList.stream().filter(m->{
                if(m.getZHANTIME() != null){
                    return m.getZHANTIME().equals(time);
                }
                return false;
            }).forEach(n->{
                if(n.getZHANID() != null){
                    map.put(n.getZHANID(),n.getZHANDATA());
                }
            });
            mapList.add(map);
        }
        watch.stop();
        if(mapList.size() > 0){
            return new ResultUtils<>(mapList, "操作成功",true, mapList.size(),watch.getTime());
        }else {
            return new ResultUtils<>(mapList, "操作成功",false, mapList.size(),watch.getTime());
        }
    }


    @RequestMapping("/FH_GetBindJY")
    public ResultUtils FH_GetBindJY(@RequestBody ParamFields bpPojo) {
        StopWatch watch = new StopWatch();
        watch.start();

        // 1. 参数校验与提取（保持原有逻辑）
        String startdate = "", enddate = "", solutionid = "0", dayhour = "";
        List<String> datatype = new ArrayList<>();

        if (CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo, ParamFields.class))) {
            watch.stop();
            return new ResultUtils<>(null, "存在非法字符", false, -1, watch.getTime());
        }
        if (null != bpPojo.getSolutionid()) solutionid = bpPojo.getSolutionid();
        if (null != bpPojo.getDatatype()) datatype = Arrays.asList(bpPojo.getDatatype().split(","));
        if (null != bpPojo.getStartdate()) startdate = bpPojo.getStartdate();
        if (null != bpPojo.getEnddate()) enddate = bpPojo.getEnddate();
        if (null != bpPojo.getDayhour()) dayhour = bpPojo.getDayhour();

        // 2. 查询数据（保持原有逻辑）
        List<ES_ZHANDIANDto> DtoList;
        if ("day".equals(dayhour)) {
            DtoList = data.selectES_ZHANDIANAndDATAByDay(solutionid, datatype, startdate, enddate);
        } else {
            DtoList = data.selectES_ZHANDIANAndDATAByHour(solutionid, datatype, startdate, enddate);
        }

        List<ES_SLTONGJIPojo> esSltongjiList = esSltongjIData.selectList(null, "134", null, null, null);

        // ========== 核心优化部分开始 ==========

        // 3. 【关键优化】按时间分组，避免在内层循环中反复遍历全量列表
        Map<String, List<ES_ZHANDIANDto>> timeGroupMap = DtoList.stream()
        // 1. 过滤掉无效的时间数据
        .filter(obj -> obj.getZHANTIME() != null && !":00:00".equals(obj.getZHANTIME()))
        // 2. 【关键】在分组前，先按 ZHANTIME 进行升序排序
        .sorted(Comparator.comparing(ES_ZHANDIANDto::getZHANTIME))
        // 3. 分组，并指定使用 LinkedHashMap 来保持插入顺序（即排序后的顺序）
        .collect(Collectors.groupingBy(
                ES_ZHANDIANDto::getZHANTIME,
                LinkedHashMap::new,      // 保证 Map 是有序的
                Collectors.toList()      // 收集器
        ));

        List<Map<String, String>> mapList = new ArrayList<>();

        // 4. 遍历时间点组装结果
        for (Map.Entry<String, List<ES_ZHANDIANDto>> entry : timeGroupMap.entrySet()) {
            Map<String, String> map = new HashMap<>();
            map.put("ID", "");
            map.put("TM", entry.getKey());

            // 获取当前时间下的所有站点数据（内存中直接取，无需再次过滤）
            List<ES_ZHANDIANDto> currentHourDataList = entry.getValue();

            for (ES_SLTONGJIPojo u : esSltongjiList) {
                // 5. 计算平均值，增加空值和数字格式防御
                double avgData = currentHourDataList.stream()
                        .filter(m -> u.getSTCD().contains(m.getZHANID())) // 匹配片区包含的站点
                        .map(ES_ZHANDIANDto::getZHANDATA)
                        .filter(dataStr -> dataStr != null && !dataStr.trim().isEmpty())
                        .mapToDouble(dataStr -> {
                            try {
                                return Double.parseDouble(dataStr.trim());
                            } catch (NumberFormatException e) {
                                return 0.0; // 遇到非数字脏数据默认当作0处理
                            }
                        })
                        .average()
                        .orElse(0.0);

                // 保留一位小数
                double roundedAvg = Math.round(avgData * 10.0) / 10.0;
                map.put(u.getID(), roundedAvg + "");
            }
            mapList.add(map);
        }

        // ========== 核心优化部分结束 ==========

        watch.stop();
        boolean hasData = mapList.size() > 0;
        return new ResultUtils<>(mapList, "操作成功", hasData, mapList.size(), watch.getTime());
    }


    @RequestMapping("/MODIFYJYYBBY")
    public ResultUtils MODIFYJYYBBY(@RequestBody ParamFields bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo,ParamFields.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String startdate = format.format(new Date(new Date().getTime() - 3 * 24 * 60 * 60 * 1000)),
                enddate = format.format(new Date()),
                solutionid = "";
        List<String> fpdrList = new ArrayList<>();
        fpdrList.add("6");
        fpdrList.add("48");
        if(null != bpPojo.getStartdate()){
            startdate = bpPojo.getStartdate();
        }
        if(null != bpPojo.getEnddate()){
            enddate = bpPojo.getEnddate();
        }
        if(null != bpPojo.getSolutionid()){
            solutionid = bpPojo.getSolutionid();
        }
        if(null != bpPojo.getFpdr()){
            fpdrList = Arrays.asList(bpPojo.getFpdr().split(","));
        }
        Date rldate = null;
        try {
            rldate = format.parse(startdate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        List<Tz_watersheddataPojo> tzWatersheddataPojoList = tzWatersheddataData.selectByTimeAndFPDR(startdate, enddate,
//                format.format(new Date(rldate.getTime() - 3 * 24 * 60 * 60 * 1000)), format.format(rldate),
//                fpdrList);
        List<Tz_watersheddataPojo> tzWatersheddataPojoList = tzWatersheddataData.selectListLastByID(startdate, enddate,fpdrList,"上海气象台",format.format(new Date(rldate.getTime() - 3 * 24 * 60 * 60 * 1000)), format.format(rldate));
        List<ES_ZHANDIANPojo> esZhandianPojoList = data.selectList("", null, null, Collections.singletonList("0"),"");
        List<String> zhanidList = esZhandianPojoList.stream().map(ES_ZHANDIANPojo::getZHANID).collect(Collectors.toList());
        List<ES_ZHANDIANDATAPojo> esZhandiandataPojoList = esZhandiandataData.selectList("", null, null, solutionid,null,null,null);
        esZhandiandataPojoList = esZhandiandataPojoList.stream().filter(n->{
            if(n.getZHANID() != null){
               return  zhanidList.contains(n.getZHANID());
            }
            return false;
        }).collect(Collectors.toList());
        List<ES_ZHANDIANDATAPojo> esZhandianDataList = new ArrayList<>();
        for(String id : zhanidList){
            List<ES_ZHANDIANDATAPojo> ZHANDIANDATAFilter = esZhandiandataPojoList.stream().filter(m->m.getZHANID().equals(id)).collect(Collectors.toList());
            ZHANDIANDATAFilter.forEach(n->{
                List<Tz_watersheddataPojo> tzFilter = tzWatersheddataPojoList.stream().filter(t->{
                    if(t.getKEYID() != null && n.getZHANID() != null && t.getFTM() != null && n.getZHANTIME() != null){
                        return t.getKEYID().equals(n.getZHANID()) && t.getFTM().equals(n.getZHANTIME());
                    }
                    return false;
                }).collect(Collectors.toList());
                Double DRP = 0.0;
                if(tzFilter.size() > 0){
                    DRP = tzFilter.get(0).getDRP();
                }
                ES_ZHANDIANDATAPojo esZhandiandataPojo = new ES_ZHANDIANDATAPojo();
                esZhandiandataPojo.setID(n.getID());
                esZhandiandataPojo.setZHANID(n.getZHANID());
                esZhandiandataPojo.setZHANTIME(n.getZHANTIME());
                esZhandiandataPojo.setZHANDATA(n.getZHANID());
                esZhandiandataPojo.setZHANDATA(String.valueOf(DRP));
                esZhandiandataPojo.setSOLUTIONID(n.getSOLUTIONID());
                esZhandianDataList.add(esZhandiandataPojo);
            });
        }
        Integer num = 0;
        int count = 300;
        int number = esZhandianDataList.size() / count;
        if(esZhandianDataList.size() % count != 0){
            number = number + 1;
        }
        List<ES_ZHANDIANDATAPojo> list = new ArrayList<>();
        for(int i = 0;i < number;i++){
            if(i == number - 1){
                list = esZhandianDataList.subList(count * i,esZhandianDataList.size());
            }else {
                list = esZhandianDataList.subList(count * i,count * ( i + 1));
            }
            num += esZhandiandataData.updateALL(list);
        }
        watch.stop();
        if(num > 0){
            return new ResultUtils<>(num, "操作成功",true, num,watch.getTime());
        }else {
            return new ResultUtils<>(num, "操作成功",false, -num,watch.getTime());
        }
    }

   // 潮位辅助系统预报的数据

}
