package swzzmodeserver.workserver.controller.swzzmode;

import com.alibaba.fastjson.JSON;
import io.swagger.models.auth.In;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swzzmodeserver.tools.CommonUtills;
import swzzmodeserver.tools.FieldIsValid;
import swzzmodeserver.tools.ParamField;
import swzzmodeserver.tools.ResultUtils;
import swzzmodeserver.tools.javalog;
import swzzmodeserver.workserver.data.swzzmode.ES_JISUANZHANData;
import swzzmodeserver.workserver.data.swzzmode.ES_JISUANZHANPXData;
import swzzmodeserver.workserver.data.swzzmode.ST_STBPRP_BData;
import swzzmodeserver.workserver.pojo.swzzmode.ES_JISUANZHANPXPojo;
import swzzmodeserver.workserver.pojo.swzzmode.ES_JISUANZHANPojo;
import swzzmodeserver.workserver.pojo.swzzmode.ST_STBPRP_BDto;
import swzzmodeserver.workserver.pojo.swzzrtsq.ST_STBPRP_B_QUPojo;
import swzzmodeserver.workserver.data.swzzrtsq.ST_STBPRP_B_QUData;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/SWZZ_MODE_ES_JISUANZHAN")
public class ES_JISUANZHANController {
    @Autowired
    private ES_JISUANZHANData data;
    @Autowired
    private ST_STBPRP_BData stbprpBData;
    @Autowired
    private ES_JISUANZHANPXData jisuanzhanpxData;

    @Autowired
    private ST_STBPRP_B_QUData stbprpB_QUData;

    @Value("${file.path.templatefilepath}")
    private String filePathName;

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
        List<ES_JISUANZHANPojo> fxList = data.selectList(ID, type, key, startindex, Integer.valueOf(pagesize));
        Integer integer = data.selectCount(ID, type, key);
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

    @RequestMapping("/findResultSzh")
    public ResultUtils findResultSzh(@RequestBody ParamField bpPojo) {
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
        List<ES_JISUANZHANPojo> fxList = data.selectList(ID, type, key, startindex, Integer.valueOf(pagesize));

        String pid = "2026060312362939391";
        if (bpPojo.getPid() != null) {
            pid = bpPojo.getPid();
        }
        List<ST_STBPRP_B_QUPojo> listQU = stbprpB_QUData.selectList(null, null, null, pid, null);
        List<String> listID = listQU.stream().map(item -> item.getSTCD()).collect(Collectors.toList());
        fxList = fxList.stream().filter(item -> listID.contains(item.getID())).collect(Collectors.toList());

        Integer integer = data.selectCount(ID, type, key);
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
    public ResultUtils add(@RequestBody ES_JISUANZHANPojo bpPojo) {
        StopWatch watch = new StopWatch();
        watch.start();
        if (CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo, ES_JISUANZHANPojo.class))) {
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
    public ResultUtils modify(@RequestBody ES_JISUANZHANPojo bpPojo) {
        StopWatch watch = new StopWatch();
        watch.start();
        if (CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo, ES_JISUANZHANPojo.class))) {
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

    // @RequestMapping("/insertALL")
    // public ResultUtils insertALL(@RequestBody List<ES_JISUANZHANPojo> bpPojo){
    // StopWatch watch = new StopWatch();
    // watch.start();
    // Integer num = data.insertALL(bpPojo);
    // watch.stop();
    // if(num > 0){
    // return new ResultUtils<>(num, "操作成功",true, num,watch.getTime());
    // }else {
    // return new ResultUtils<>(num, "操作成功",false, num,watch.getTime());
    // }
    // }
    @RequestMapping("/JISUANZHAN")
    public ResultUtils JISUANZHAN(@RequestBody ParamField bpPojo) {
        StopWatch watch = new StopWatch();
        watch.start();
        List<ES_JISUANZHANPojo> es_jisuanzhanPojos = data.selectList("", null, "", null, null);

        List<ES_JISUANZHANPXPojo> esJisuanzhanpxPojos = jisuanzhanpxData.selectList("", null, null);
        String jsonText = "[{ id: \"1\", text: \"水位\" }, { id: \"2,7,8,24,25\", text: \"流量\" },{ id: \"15\", text: \"蓄量\" },{ id: \"14\", text: \"水面积\" },{ id: \"19\", text: \"平均水位\" }]";
        List<Map> mapList = JSON.parseArray(jsonText, Map.class);
        List<String> stcds = es_jisuanzhanPojos.stream().map(ES_JISUANZHANPojo::getSTCD).collect(Collectors.toList());
        List<ST_STBPRP_BDto> stStbprpBDtos = stbprpBData.selectListBandStcdByStcdList(stcds);
        List<Map<String, String>> maps = new ArrayList<>();
        Integer _INDEX = 0;
        for (Map m : mapList) {
            Map<String, String> map = new HashMap<>();
            List<String> mtypeArr = Arrays.asList(m.get("id").toString().split(","));
            map.put("id", m.get("id").toString());
            map.put("name", m.get("text").toString());
            map.put("type", m.get("id").toString());
            map.put("stcd", m.get("id").toString());
            map.put("pid", "-1");
            map.put("px", _INDEX.toString());
            maps.add(map);
            _INDEX++;
            List<ES_JISUANZHANPojo> collect = es_jisuanzhanPojos.stream().filter(s -> mtypeArr.contains(s.getTYPE()))
                    .collect(Collectors.toList());
            collect.forEach(n -> {
                List<ES_JISUANZHANPXPojo> eS_JISUANZHANsPXTepm = esJisuanzhanpxPojos.stream()
                        .filter(s -> s.getSTCD().equals(n.getSTCD())).collect(Collectors.toList());
                Map<String, String> stringMap = new HashMap<>();
                stringMap.put("id", n.getID());
                stringMap.put("name", n.getNAME());
                stringMap.put("type", n.getTYPE());
                stringMap.put("stcd", n.getSTCD());
                stringMap.put("pid", m.get("id").toString());
                if (eS_JISUANZHANsPXTepm.size() > 0) {
                    stringMap.put("px", eS_JISUANZHANsPXTepm.get(0).getPX().toString());
                }
                if (stStbprpBDtos.size() > 0) {
                    List<ST_STBPRP_BDto> listBaseTemp = stStbprpBDtos.stream()
                            .filter(s -> s.getSTCD().equals(n.getSTCD())).collect(Collectors.toList());
                    if (listBaseTemp.size() > 0) {
                        stringMap.remove("name");
                        stringMap.put("name", listBaseTemp.get(0).getSTNM());
                    }

                }
                maps.add(stringMap);
            });
        }
        maps.sort((a, b) -> {
            if (a.containsKey("px") && a.get("px") != null && b.containsKey("px") && b.get("px") != null) {
                return Integer.parseInt(a.get("px")) - Integer.parseInt(b.get("px"));
            }
            return 0;
        });
        int num = maps.size();
        watch.stop();
        if (num > 0) {
            return new ResultUtils<>(maps, "操作成功", true, num, watch.getTime());
        } else {
            return new ResultUtils<>(maps, "操作成功", false, num, watch.getTime());
        }
    }

}
