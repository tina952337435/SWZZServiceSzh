package swzzmodeserver.workserver.controller.swzzqxsj;

import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swzzmodeserver.tools.CommonUtills;
import swzzmodeserver.tools.FieldIsValid;
import swzzmodeserver.tools.ParamField;
import swzzmodeserver.tools.ResultUtils;
import swzzmodeserver.workserver.data.swzzqxsj.Tz_gridData;
import swzzmodeserver.workserver.data.swzzqxsj.Tz_ncfilelistData;
import swzzmodeserver.workserver.data.swzzqxsj.Tz_watersheddataData;
import swzzmodeserver.workserver.pojo.swzzqxsj.Tz_gridPojo;
import swzzmodeserver.workserver.pojo.swzzqxsj.Tz_ncfilelistPojo;
import swzzmodeserver.workserver.pojo.swzzqxsj.Tz_watersheddataPojo;
import swzzmodeserver.workserver.service.swzzqxsj.Tz_ncfilelistService;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/SWZZ_QXSJ_tz_ncfilelist")
public class Tz_ncfilelistController {
    @Autowired
    private Tz_ncfilelistData data;
    @Autowired
    private Tz_ncfilelistService service;
    @Autowired
    private Tz_watersheddataData watersheddataData;

    @RequestMapping("/findResult")
    public ResultUtils findResult(@RequestBody ParamField bpPojo) {
        StopWatch watch = new StopWatch();
        watch.start();
        String ID = "", key = "", pageindex = "", pagesize = "10";
        List<String> type = new ArrayList<>();
        String stime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date().getTime() - 24 * 60 * 60 * 1000),
                etime = "";
        if (CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo, ParamField.class))) {
            watch.stop();
            return new ResultUtils<>(null, "存在非法字符", false, -1, watch.getTime());
        }
        if (null != bpPojo.getStcd()) {
            ID = bpPojo.getStcd();
        }
        if (null != bpPojo.getKwtxt()) {
            key = bpPojo.getKwtxt();
        }
        if (null != bpPojo.getStartdate()) {
            stime = bpPojo.getStartdate();
        }
        if (null != bpPojo.getEnddate()) {
            etime = bpPojo.getEnddate();
        }
        if (null != bpPojo.getPattem()) {
            type = Arrays.asList(bpPojo.getPattem().split(","));
        }
        if (null != bpPojo.getPageindex()) {
            pageindex = bpPojo.getPageindex();
        }
        if (null != bpPojo.getPagesize()) {
            pagesize = bpPojo.getPagesize();
        }
        Integer startindex = null;
        if (!"".equals(pageindex) && !"".equals(pagesize)) {
            startindex = (Integer.parseInt(pageindex) - 1) * Integer.parseInt(pagesize);
        }
        List<Tz_ncfilelistPojo> fxList = data.selectList(ID, key, stime, etime, type, startindex,
                Integer.valueOf(pagesize));
        Integer integer = data.selectCount(ID, key, stime, etime, type);
        Integer count = 1;
        if (null != pagesize && !"".equals(pagesize)) {
            count = integer / Integer.parseInt(pagesize);
            if (integer % Integer.parseInt(pagesize) != 0) {
                count += 1;
            }
        }
        watch.stop();
        if (!"".equals(pagesize) && !"".equals(pageindex)) {
            if (fxList.size() > 0) {
                return new ResultUtils<>(fxList, "操作成功", true, Integer.parseInt(pagesize), Integer.parseInt(pageindex),
                        count, integer, fxList.size(), watch.getTime());
            } else {
                return new ResultUtils<>(fxList, "操作成功", false, Integer.parseInt(pagesize), Integer.parseInt(pageindex),
                        count, integer, fxList.size(), watch.getTime());
            }
        } else {
            if (fxList.size() > 0) {
                return new ResultUtils<>(fxList, "操作成功", true, fxList.size(), watch.getTime());
            } else {
                return new ResultUtils<>(fxList, "操作成功", false, fxList.size(), watch.getTime());
            }
        }
    }

    @RequestMapping("/add")
    public ResultUtils add(@RequestBody Tz_ncfilelistPojo bpPojo) {
        StopWatch watch = new StopWatch();
        watch.start();
        if (CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo, Tz_ncfilelistPojo.class))) {
            watch.stop();
            return new ResultUtils<>(null, "存在非法字符", false, -1, watch.getTime());
        }
        Integer num = data.insertOne(bpPojo);
        watch.stop();
        if (num > 0) {
            return new ResultUtils<>(num, "操作成功", true, num, watch.getTime());
        } else {
            return new ResultUtils<>(num, "操作成功", false, num, watch.getTime());
        }
    }

    @RequestMapping("/modify")
    public ResultUtils modify(@RequestBody Tz_ncfilelistPojo bpPojo) {
        StopWatch watch = new StopWatch();
        watch.start();
        if (CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo, Tz_ncfilelistPojo.class))) {
            watch.stop();
            return new ResultUtils<>(null, "存在非法字符", false, -1, watch.getTime());
        }
        Integer num = data.updateOne(bpPojo);
        watch.stop();
        if (num > 0) {
            return new ResultUtils<>(num, "操作成功", true, num, watch.getTime());
        } else {
            return new ResultUtils<>(num, "操作成功", false, num, watch.getTime());
        }
    }

    @RequestMapping("/remove")
    public ResultUtils remove(@RequestBody ParamField bpPojo) {
        StopWatch watch = new StopWatch();
        watch.start();
        String ID = "";
        if (CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo, ParamField.class))) {
            watch.stop();
            return new ResultUtils<>(null, "存在非法字符", false, -1, watch.getTime());
        }
        if (null != bpPojo.getStcd()) {
            ID = bpPojo.getStcd();
        }
        Integer num = data.deleteOne(ID);
        watch.stop();
        if (num > 0) {
            return new ResultUtils<>(num, "操作成功", true, num, watch.getTime());
        } else {
            return new ResultUtils<>(num, "操作成功", false, num, watch.getTime());
        }
    }

    @RequestMapping("/QXSJ_TZ_NCFILELISTSelNew")
    public ResultUtils QXSJ_TZ_NCFILELISTSelNew(@RequestBody ParamField bpPojo) {
        StopWatch watch = new StopWatch();
        watch.start();
        String ID = "", startdate = "", enddate = "", pattem = "48";
        List<String> idList = new ArrayList<>();
        if (CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo, ParamField.class))) {
            watch.stop();
            return new ResultUtils<>(null, "存在非法字符", false, -1, watch.getTime());
        }
        if (null != bpPojo.getStcd()) {
            ID = bpPojo.getStcd();
            idList = Arrays.asList(bpPojo.getStcd().split(","));
        } else {
            // 默认15片，不然太慢了
            ID = "1744830539,1744830531,1744830552,1744830532,1744830533,1744830545,1744830546,1744830547,1744830535,1744830534,1744830464,1744830536,1744830553,1744830549,1744830548";
        }
        if (null != bpPojo.getStartdate()) {
            startdate = bpPojo.getStartdate();
        }
        if (null != bpPojo.getEnddate()) {
            enddate = bpPojo.getEnddate();
        }
        if (null != bpPojo.getPattem()) {
            pattem = bpPojo.getPattem();
        }
        List<Map<String, Object>> mapList = service.QXSJ_TZ_NCFILELISTSelNew(startdate, enddate, pattem, ID);
        watch.stop();
        if (mapList.size() > 0) {
            return new ResultUtils<>(mapList, "操作成功", true, mapList.size(), watch.getTime());
        } else {
            return new ResultUtils<>(mapList, "操作成功", false, mapList.size(), watch.getTime());
        }
    }

    @RequestMapping("/getshqixiang_yl")
    public ResultUtils getshqixiang_yl(@RequestBody ParamField bpPojo) {
        StopWatch watch = new StopWatch();
        watch.start();
        if (bpPojo.getPid() == null) {
            watch.stop();
            return new ResultUtils<>(null, "必传参数不能为空", false, -1, watch.getTime());
        }
        String rlStime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date().getTime() - 24 * 60 * 60 * 1000);// 24小时前
        String rlEtime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date().getTime());// 当前时间
        List<String> FPDR = Arrays.asList(bpPojo.getPid().split(","));
        List<Tz_watersheddataPojo> mapList = watersheddataData.selectByTimeAndFPDR(null, null, rlStime, rlEtime, FPDR);
        if (bpPojo.getPid().equals(bpPojo.getPid())) {
            mapList = mapList.stream().filter(item -> "淀北片小,蕴南片小,嘉宝北片小,青松片小".contains(item.getKEYID()))
                    .collect(Collectors.toList());
        }
        List<Map<String, Object>> resultList = mapList.stream()
                .collect(Collectors.groupingBy(
                        // 1. 定义复合键进行分组（这里使用了 List<Object> 作为键）
                        item -> Arrays.asList(item.getKEYID(), item.getRLSTM()),
                        // 2. 指定下游收集器，对 DRP 字段进行求和
                        Collectors.summingDouble(Tz_watersheddataPojo::getDRP)))
                .entrySet().stream()
                // 3. 将分组后的 Map 结果转换为新的 List<Map<String, Object>>
                .map(entry -> {
                    Map<String, Object> newMap = new HashMap<>();
                    List<String> keys = entry.getKey();
                    newMap.put("NAME", keys.get(0)); // 取出复合键中的 KEYID
                    newMap.put("RLSTM", keys.get(1)); // 取出复合键中的 RLSTM
                    newMap.put("DRP", entry.getValue()); // 放入求和后的 DRP
                    return newMap;
                })
                .collect(Collectors.toList());

        watch.stop();
        if (resultList.size() > 0) {
            return new ResultUtils<>(resultList, "操作成功", true, resultList.size(), watch.getTime());
        } else {
            return new ResultUtils<>(resultList, "操作成功", false, resultList.size(), watch.getTime());
        }
    }

    @RequestMapping("/getshqixiang_ylgc")
    public ResultUtils getshqixiang_ylgc(@RequestBody ParamField bpPojo) {
        StopWatch watch = new StopWatch();
        watch.start();
        if (bpPojo.getPid() == null) {
            watch.stop();
            return new ResultUtils<>(null, "必传参数不能为空", false, -1, watch.getTime());
        }
        String rlStime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date().getTime() - 24 * 60 * 60 * 1000);// 24小时前
        String rlEtime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date().getTime());// 当前时间
        List<String> FPDR = Arrays.asList(bpPojo.getPid().split(","));
        List<Tz_watersheddataPojo> mapList = watersheddataData.selectByTimeAndFPDR(null, null, rlStime, rlEtime, FPDR);
        if (bpPojo.getPid().equals(bpPojo.getPid())) {
            mapList = mapList.stream().filter(item -> "淀北片小,蕴南片小,嘉宝北片小,青松片小".contains(item.getKEYID()))
                    .collect(Collectors.toList());
        }
        List<Map<String, Object>> resultList = new ArrayList<>();
        mapList.forEach(item -> {
            Map<String, Object> newMap = new HashMap<>();
            newMap.put("NAME", item.getKEYID()); // 取出复合键中的 KEYID
            newMap.put("RLSTM", item.getRLSTM()); // 取出复合键中的 RLSTM
            newMap.put("DRP", item.getDRP()); // 放入求和后的 DRP
            newMap.put("TM", item.getFTM()); // 时间
            resultList.add(newMap);
        });
        watch.stop();
        if (resultList.size() > 0) {
            return new ResultUtils<>(resultList, "操作成功", true, resultList.size(), watch.getTime());
        } else {
            return new ResultUtils<>(resultList, "操作成功", false, resultList.size(), watch.getTime());
        }
    }

}
