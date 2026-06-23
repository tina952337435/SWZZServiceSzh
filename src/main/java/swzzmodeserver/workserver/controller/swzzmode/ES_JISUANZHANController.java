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
        List<ES_JISUANZHANPojo> fxList = data.selectList(ID,type,key,startindex, Integer.valueOf(pagesize));
        Integer integer = data.selectCount(ID,type,key);
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
    @RequestMapping("/findResultSzh")
    public ResultUtils findResultSzh(@RequestBody ParamField bpPojo){
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
        List<ES_JISUANZHANPojo> fxList = data.selectList(ID,type,key,startindex, Integer.valueOf(pagesize));

        // 81185	志丹泵站计算	1	2030283
        // 81615	赵屯校正计算水位	1	1330383
        // 80988	黄渡计算	1	1230283
        // 81644	温州路计算水位	1	2060281
        // 8723	黄浦公园计算	1	2060881
        // 81077	北新泾计算	1	2060181
        // 81432	嘉定南门计算	1	1230483
        // 72978	青浦南门计算水位	1	1330183
        // 81174	虹桥计算	1	1530383
        // 81821	苏州河可调蓄库容-淀北片槽蓄容量	15	81821
        // 81822	苏州河可调蓄库容-嘉宝北片槽蓄容量	15	81822
        // 81823	苏州河可调蓄库容-青松片槽蓄容量	15	81823
        // 81824	苏州河可调蓄库容-蕰南片非杨树浦港槽蓄容量	15	81824
        // 81827	苏州河可调蓄库容-蕰南杨树浦港水系计算	15	81827
        // 81828	华新泵闸水闸计算流量	2	81828
        // 81829	华新泵闸泵站计算流量	2	81829
        // 81830	东大盈泵闸水闸计算流量	2	81830
        // 81831	东大盈泵闸泵站计算流量	2	81831
        // 81832	西大盈泵闸水闸计算流量	2	81832
        // 81833	西大盈泵闸泵站计算流量	2	81833
        // 81834	南顾浦泵闸水闸计算流量	2	81834
        // 81835	南顾浦泵闸泵站计算流量	2	81835
        // 81836	新槎浦泵闸水闸计算流量	2	81836
        // 81837	新槎浦泵闸泵站计算流量	2	81837
        // 81838	彭越浦泵闸水闸计算流量	2	81838
        // 81839	彭越浦泵闸泵站计算流量	2	81839
        // 81840	木渎港泵闸水闸计算流量	2	81840
        // 81841	木渎港泵闸泵站计算流量	2	81841
        // 81842	许浦港泵闸水闸计算流量	2	81842
        // 81843	许浦港泵闸泵站计算流量	2	81843
        // 81844	龙尖嘴泵闸水闸计算流量	2	81844
        // 81845	龙尖嘴泵闸泵站计算流量	2	81845
        // 81846	盐仓浦泵闸水闸计算流量	2	81846
        // 81847	盐仓浦泵闸泵站计算流量	2	81847
        // 81848	外环西河泵闸水闸计算流量	2	81848
        // 81849	外环西河泵闸泵站计算流量	2	81849
        // 81850	北新泾泵闸水闸计算流量	2	81850
        // 81851	北新泾泵闸泵站计算流量	2	81851
        // 81852	华漕港泵闸水闸计算流量	2	81852
        // 81853	华漕港泵闸泵站计算流量	2	81853
        // 81854	北横泾北泵闸水闸计算流量	2	81854
        // 81855	北横泾北泵闸泵站计算流量	2	81855
        // 81314	苏州河河口水闸计算	2	81314

        // 81801	苏州河淀北片入流计算	24	81801	NULL	NULL
        // 81802	苏州河嘉宝北片入流计算	24	81802	NULL	NULL
        // 81803	苏州河青松片入流计算	24	81803	NULL	NULL
        // 81804	苏州河蕴南片入流计算	24	81804	NULL	NULL

        //81650	苏州河槽蓄容量	15	81650	NULL	NULL
        //81826	曹家渡	1	63405200	NULL	NULL
        

        String idStr="81185,81615,80988,81644,8723,81077,81432,72978,81174,81821,81822,81823,81824,81827,81828,81829,81830,81831,81832,81833,81834,81835,81836,81837,81838,81839,81840,81841,81842,81843,81844,81845,81846,81847,81848,81849,81850,81851,81852,81853,81854,81855,81314,81801,81802,81803,81804,81650,81826";
        
        List<ST_STBPRP_B_QUPojo> listQU=stbprpB_QUData.selectList(null, null, null, "2026060312362939391", null);
        
        // new   javalog().writelog("listQU的长度"+listQU.size(),filePathName);

        List<String> listID=listQU.stream().map(item->item.getSTCD()).collect(Collectors.toList());
        
        // new   javalog().writelog("listID的长度"+listID.size(),filePathName);
        
        fxList=fxList.stream().filter(item->listID.contains(item.getID())).collect(Collectors.toList());

        
        Integer integer = data.selectCount(ID,type,key);
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
    public ResultUtils add(@RequestBody ES_JISUANZHANPojo bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo,ES_JISUANZHANPojo.class))){
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
    public ResultUtils modify(@RequestBody ES_JISUANZHANPojo bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo,ES_JISUANZHANPojo.class))){
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
//    @RequestMapping("/insertALL")
//    public ResultUtils insertALL(@RequestBody List<ES_JISUANZHANPojo> bpPojo){
//        StopWatch watch = new StopWatch();
//        watch.start();
//        Integer num = data.insertALL(bpPojo);
//        watch.stop();
//        if(num > 0){
//            return new ResultUtils<>(num, "操作成功",true, num,watch.getTime());
//        }else {
//            return new ResultUtils<>(num, "操作成功",false, num,watch.getTime());
//        }
//    }
    @RequestMapping("/JISUANZHAN")
    public ResultUtils JISUANZHAN(@RequestBody ParamField bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        List<ES_JISUANZHANPojo> es_jisuanzhanPojos = data.selectList("", null, "", null, null);

        List<ES_JISUANZHANPXPojo> esJisuanzhanpxPojos = jisuanzhanpxData.selectList("", null, null);
        String jsonText = "[{ id: \"1\", text: \"水位\" }, { id: \"2,7,8,24,25\", text: \"流量\" },{ id: \"15\", text: \"蓄量\" },{ id: \"14\", text: \"水面积\" },{ id: \"19\", text: \"平均水位\" }]";
        List<Map> mapList = JSON.parseArray(jsonText, Map.class);
        List<String> stcds = es_jisuanzhanPojos.stream().map(ES_JISUANZHANPojo::getSTCD).collect(Collectors.toList());
        List<ST_STBPRP_BDto> stStbprpBDtos = stbprpBData.selectListBandStcdByStcdList(stcds);
        List<Map<String,String>> maps = new ArrayList<>();
        Integer _INDEX = 0;
        for(Map m : mapList){
            Map<String,String> map = new HashMap<>();
            List<String> mtypeArr = Arrays.asList(m.get("id").toString().split(","));
            map.put("id",m.get("id").toString());
            map.put("name",m.get("text").toString());
            map.put("type",m.get("id").toString());
            map.put("stcd",m.get("id").toString());
            map.put("pid","-1");
            map.put("px",_INDEX.toString());
            maps.add(map);
            _INDEX++;
            List<ES_JISUANZHANPojo> collect = es_jisuanzhanPojos.stream().filter(s -> mtypeArr.contains(s.getTYPE())).collect(Collectors.toList());
            collect.forEach(n->{
                List<ES_JISUANZHANPXPojo> eS_JISUANZHANsPXTepm = esJisuanzhanpxPojos.stream().filter(s->s.getSTCD().equals(n.getSTCD())).collect(Collectors.toList());
                Map<String,String> stringMap = new HashMap<>();
                stringMap.put("id",n.getID());
                stringMap.put("name",n.getNAME());
                stringMap.put("type",n.getTYPE());
                stringMap.put("stcd",n.getSTCD());
                stringMap.put("pid",m.get("id").toString());
                if(eS_JISUANZHANsPXTepm.size() > 0){
                    stringMap.put("px",eS_JISUANZHANsPXTepm.get(0).getPX().toString());
                }
                if (stStbprpBDtos.size() > 0){
                    List<ST_STBPRP_BDto> listBaseTemp = stStbprpBDtos.stream().filter(s -> s.getSTCD().equals(n.getSTCD())).collect(Collectors.toList());
                    if (listBaseTemp.size() > 0){
                        stringMap.remove("name");
                        stringMap.put("name",listBaseTemp.get(0).getSTNM());
                    }

                }
                maps.add(stringMap);
            });
        }
        maps.sort((a,b)->{
            if(a.containsKey("px") && a.get("px") != null && b.containsKey("px") && b.get("px") != null){
                return Integer.parseInt(a.get("px")) - Integer.parseInt(b.get("px"));
            }
            return 0;
        });
        int num = maps.size();
        watch.stop();
        if(num > 0){
            return new ResultUtils<>(maps, "操作成功",true, num,watch.getTime());
        }else {
            return new ResultUtils<>(maps, "操作成功",false, num,watch.getTime());
        }
    }

}
