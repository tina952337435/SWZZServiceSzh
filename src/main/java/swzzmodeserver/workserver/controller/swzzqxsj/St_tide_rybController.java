package swzzmodeserver.workserver.controller.swzzqxsj;

import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swzzmodeserver.tools.*;
import swzzmodeserver.workserver.data.swzzqxsj.St_rnfl_fData;
import swzzmodeserver.workserver.data.swzzqxsj.St_tide_rybData;
import swzzmodeserver.workserver.pojo.swzzmode.ES_ZHANDIANDto;
import swzzmodeserver.workserver.pojo.swzzqxsj.St_rnfl_fPojo;
import swzzmodeserver.workserver.pojo.swzzqxsj.St_tide_rybPojo;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/SWZZ_QXSJ_st_tide_ryb")
public class St_tide_rybController {
    @Autowired
    private St_tide_rybData data;

    @RequestMapping("/findResult")
    public ResultUtils findResult(@RequestBody ParamField bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String ID = "",key = "",pageindex = "",pagesize = "10";
        List<String> type = new ArrayList<>();
        String stime = ""//new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date().getTime() - 24 * 60 * 60 * 1000)
                ,etime = "",ybstm = "",ybetm = "";
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
        if(null != bpPojo.getKID1()){
            ybstm = bpPojo.getKID1();
        }
        if(null != bpPojo.getKID2()){
            ybetm = bpPojo.getKID2();
        }
        Integer startindex = null;
        if(!"".equals(pageindex) && !"".equals(pagesize)){
            startindex = (Integer.parseInt(pageindex) - 1) * Integer.parseInt(pagesize);
        }
        List<St_tide_rybPojo> fxList = data.selectList(ID,key,stime,etime,type,ybstm,ybetm,startindex, Integer.valueOf(pagesize));
        Integer integer = data.selectCount(ID,key,stime,etime,type,ybstm,ybetm);
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
    public ResultUtils add(@RequestBody St_tide_rybPojo bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo, St_tide_rybPojo.class))){
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
    public ResultUtils modify(@RequestBody St_tide_rybPojo bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo, St_tide_rybPojo.class))){
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
        String ID = "",ID1 = "",ID2 = "";
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo, ParamField.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if(null != bpPojo.getStcd()){
            ID = bpPojo.getStcd();
        }
        if(null != bpPojo.getKID1()){
            ID1 = bpPojo.getKID1();//YBTM
        }
        if(null != bpPojo.getKID2()){
            ID2 = bpPojo.getKID2();//TM
        }
        Integer num = data.deleteOne(ID,ID1,ID2);
        watch.stop();
        if(num > 0){
            return new ResultUtils<>(num, "操作成功",true, num,watch.getTime());
        }else {
            return new ResultUtils<>(num, "操作成功",false, num,watch.getTime());
        }
    }

    @RequestMapping("/batchResult/{pattem}")
    public ResultUtils batchResult(@PathVariable("pattem") String pattem, @RequestBody List<St_tide_rybPojo> bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String type = "";
        if(CommonUtills.isEmpty(FieldIsValid.getListColumnName(bpPojo, St_tide_rybPojo.class)) || !FieldIsValid.isValid(pattem)){
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
        List<St_tide_rybPojo> list = new ArrayList<>();
        for(int i = 0;i < number;i++){
            if(i == number - 1){
                list = bpPojo.subList(count * i,bpPojo.size());
            }else {
                list = bpPojo.subList(count * i,count * ( i + 1));
            }
            if(type.equals("true") ){
                num += -data.updateALL(list);
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
        if(CommonUtills.isEmpty(FieldIsValid.getListColumnName(bpPojo, ParamField.class))){
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

    @RequestMapping("/findResultFisrt")
    public ResultUtils findResultFisrt(@RequestBody ParamField bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        List<String> ID = new ArrayList<>();
        String stime = "",etime = "";
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo, ParamField.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if(null != bpPojo.getStartdate()){
            stime = bpPojo.getStartdate();
        }
        if(null != bpPojo.getEnddate()){
            etime = bpPojo.getEnddate();
        }
        String wdStcds="10001010";//温带风暴潮
        String wdStnms="海洋温带风暴潮(高桥)";
        String tfStcds="63405800,63405900,63401750,62701710";//台风风暴潮
        String tfStnms="海洋台风风暴潮(芦潮港),海洋台风风暴潮(金山嘴),海洋台风风暴潮(吴淞口),海洋台风风暴潮(高桥)";
        String hcStcds="E17,E18";//海潮增水
        String hcStnms= "浦东北部近岸海域,浦东南部近岸海域";
        String allStcds=wdStcds+","+tfStcds+","+hcStcds;
        String allStnms=wdStnms+","+tfStnms+","+hcStnms;
        ID = Arrays.asList(allStcds.split(","));
        List<St_tide_rybPojo> fxList = data.selectListByNewFirst(ID,stime,etime);

        Set<String> set = new TreeSet<>();
        List<Map<String,String>> mapList = new ArrayList<>();
        for(St_tide_rybPojo obj : fxList){
            if(obj.getTM() != null){
                if(":00:00".equals(obj.getTM())){
                    continue;
                }
                set.add(obj.getTM());
            }
        }
        for(String time : set){
            Map<String,String> map = new HashMap<>();
            //map.put("ID","");
            map.put("TM",time);
            fxList.stream().filter(m->{
                if(m.getTM() != null){
                    return m.getTM().equals(time);
                }
                return false;
            }).forEach(n->{
                if(n.getSTCD() != null){
                    String stnm="";
                    String stcd=n.getSTCD().replaceAll(" ","");
                    switch (stcd){
                        case "10001010":
                            stnm="海洋温带风暴潮(高桥)";
                            break;
                        case "63405800":
                            stnm="海洋台风风暴潮(芦潮港)";
                            break;
                        case "63405900":
                            stnm="海洋台风风暴潮(金山嘴)";
                            break;
                        case "63401750":
                            stnm="海洋台风风暴潮(吴淞口)";
                            break;
                        case "62701710":
                            stnm="海洋台风风暴潮(高桥)";
                            break;
                        case "E17":
                            stnm="浦东北部近岸海域";
                            break;
                        case "E18":
                            stnm="浦东南部近岸海域";
                            break;
                        default:
                    }
                    map.put(stnm.replaceAll(" ",""),n.getTDZ().toString());
                }
            });
            mapList.add(map);
        }
        watch.stop();
        if(fxList.size() > 0){
            return new ResultUtils<>(mapList, "操作成功",true ,mapList.size(),watch.getTime());
        }else {
            return new ResultUtils<>(mapList, "操作成功",false,mapList.size(),watch.getTime());
        }
    }


    @RequestMapping("/findResultMaxTM")
    public ResultUtils findResultMaxTM(@RequestBody ParamField bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        List<String> ID = new ArrayList<>();
        String stime = "",etime = "";
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo, ParamField.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if(null != bpPojo.getStartdate()){
            stime = bpPojo.getStartdate();
        }
        if(null != bpPojo.getEnddate()){
            etime = bpPojo.getEnddate();
        }
        if(bpPojo.getStcd()!=null){
            ID = Arrays.asList(bpPojo.getStcd().split(","));
        }
        List<St_tide_rybPojo> fxList = data.selectListMaxTM(ID,stime,etime);
        watch.stop();
        if(fxList.size() > 0){
            return new ResultUtils<>(fxList, "操作成功",true ,fxList.size(),watch.getTime());
        }else {
            return new ResultUtils<>(fxList, "操作成功",false,fxList.size(),watch.getTime());
        }
    }
}
