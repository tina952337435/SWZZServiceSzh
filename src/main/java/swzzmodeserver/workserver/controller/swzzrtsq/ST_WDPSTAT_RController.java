package swzzmodeserver.workserver.controller.swzzrtsq;

import org.apache.commons.lang3.time.StopWatch;
import swzzmodeserver.tools.ResultUtils;
import swzzmodeserver.workserver.data.swzzrtsq.RTSQST_STBPRP_BData;
import swzzmodeserver.workserver.data.swzzrtsq.ST_STBPRP_B_QUData;
import swzzmodeserver.workserver.data.swzzrtsq.ST_STBPRP_B_TREEData;
import swzzmodeserver.workserver.data.swzzrtsq.ST_WDPSTAT_RData;
import swzzmodeserver.workserver.pojo.swzzrtsq.*;
import swzzmodeserver.tools.ColumnName;
import swzzmodeserver.tools.CommonUtills;
import swzzmodeserver.tools.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/SWZZ_RTSQ_ST_WDPSTAT_R")
public class ST_WDPSTAT_RController {
    @Autowired
    private ST_WDPSTAT_RData data;
    @Autowired
    private ST_STBPRP_B_QUData quData;
    @Autowired
    private RTSQST_STBPRP_BData stbprpBData;
    @Autowired
    private ST_STBPRP_B_TREEData treeData;

    @RequestMapping("/selectAll")
    public ResultUtils selectAll(@RequestBody ColumnName param){
        StopWatch watch = new StopWatch();
        watch.start();
        Date date = new Date(new Date().getTime() - 24 * 60 * 60 * 1000);
        String stime= DateUtil.dateFormat(date,"yyyy-MM-dd HH:mm:ss"),etime="";
        List<String> IDList = new ArrayList<>();
        if(null != param.getStcd()){
            IDList = Arrays.asList(param.getStcd().split(","));
        }
        List<ST_WDPSTAT_RPojo> treeList=data.selectList(IDList,stime,etime);
        watch.stop();
        if(treeList.size() > 0){
            return new ResultUtils<>(treeList, "操作成功",true ,treeList.size(),watch.getTime());
        }else {
            return new ResultUtils<>(treeList, "操作成功",false,treeList.size(),watch.getTime());
        }
    }

    @RequestMapping("/add")
    public ResultUtils insertOne(@RequestBody ST_WDPSTAT_RPojo param){
        StopWatch watch = new StopWatch();
        watch.start();
        int integer= data.insertOne(param);
        watch.stop();
        if(integer > 0){
            return new ResultUtils<>(integer, "操作成功",true, integer,watch.getTime());
        }else {
            return new ResultUtils<>(integer, "操作成功",false, integer,watch.getTime());
        }
    }

    @RequestMapping("/modify")
    public ResultUtils updateOne(@RequestBody ST_WDPSTAT_RPojo param){
        StopWatch watch = new StopWatch();
        watch.start();
        int integer= data.upDateOne(param);
        watch.stop();
        if(integer > 0){
            return new ResultUtils<>(integer, "操作成功",true, integer,watch.getTime());
        }else {
            return new ResultUtils<>(integer, "操作成功",false, integer,watch.getTime());
        }
    }

    @RequestMapping("/remove")
    public ResultUtils deleteOne(@RequestBody ColumnName param){
        StopWatch watch = new StopWatch();
        watch.start();
        String id = "";
        if(null != param.getStcd()){
            id = param.getStcd();
        }
        int integer= data.deleteOne(id);
        watch.stop();
        if(integer > 0){
            return new ResultUtils<>(integer, "操作成功",true, integer,watch.getTime());
        }else {
            return new ResultUtils<>(integer, "操作成功",false, integer,watch.getTime());
        }
    }

    @RequestMapping("/selectList")//逐日水量接口
    public ResultUtils selectList(@RequestBody ColumnName param){
        StopWatch watch = new StopWatch();
        watch.start();
        Date date = new Date(new Date().getTime() - 24 * 60 * 60 * 1000);
        String stime= DateUtil.dateFormat(date,"yyyy-MM-dd HH:mm:ss"),etime="";
        String PID = "",stcd = "";
        if(null != param.getPid()){
            PID = param.getPid();
        }
        if(null != param.getStime()){
            stime = param.getStime();
        }
        if(null != param.getEtime()){
            etime = param.getEtime();
        }
        if(null != param.getStcd()){
            stcd = param.getStcd();
        }
        List<String> stcdList = new ArrayList<>();
        if(null != stcd && !("".equals(stcd))){
            stcdList.add(stcd);
        }
        if(null != PID && !("".equals(PID))){
            List<ST_STBPRP_B_QUPojo> quPojos = quData.selectList(null, null, null, PID,null);
            for(ST_STBPRP_B_QUPojo quPojo : quPojos){
                if(null != quPojo.getSTCD()){
                    stcdList.add(quPojo.getSTCD());
                }
            }
        }
        List<ST_STBPRP_BPojo> stStbprpBList = stbprpBData.selectList(stcdList, null);
        List<ST_WDPSTAT_RPojo> wdpstatRList = data.selectList(null,stime,etime);
        for(ST_STBPRP_BPojo stbprpBPojo : stStbprpBList){
            List<ST_WDPSTAT_RPojo> collect = wdpstatRList.stream().filter(i -> {
                return i.getSTCD().equals(stbprpBPojo.getSTCD());
            }).collect(Collectors.toList());
            stbprpBPojo.setWdpstatRList(collect);
        }
        watch.stop();
        if(stStbprpBList.size() > 0){
            return new ResultUtils<>(stStbprpBList, "操作成功",true ,stStbprpBList.size(),watch.getTime());
        }else {
            return new ResultUtils<>(stStbprpBList, "操作成功",false,stStbprpBList.size(),watch.getTime());
        }
    }

    @RequestMapping("/selectListByWQ")//逐日水量接口
    public ResultUtils selectListByWQ(@RequestBody ColumnName param){
        StopWatch watch = new StopWatch();
        watch.start();
        Date date = new Date(new Date().getTime() - 168 * 60 * 60 * 1000);
        String stime= DateUtil.dateFormat(date,"yyyy-MM-dd HH:mm:ss"),etime="";
        String PID = "",stcd = "";
        if(null != param.getPid()){
            PID = param.getPid();
        }
        if(null != param.getStime()){
            stime = param.getStime();
        }
        if(null != param.getEtime()){
            etime = param.getEtime();
        }
        if(null != param.getStcd()){
            stcd = param.getStcd();
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long timedif = 2,sTime = 0,eTime = 0;
        try {
            sTime = dateFormat.parse(stime).getTime();
            eTime = dateFormat.parse(etime).getTime();
            timedif =  (eTime - sTime) / (24 * 60 * 60 * 1000);
//            if(Integer.valueOf(new SimpleDateFormat("HH").format(eTime)) > 8){
                timedif = timedif + 1;
//            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        List<String> stcdList = new ArrayList<>();

        List<Map<String,Object>> mapList = new ArrayList<>();
        if(null != PID && !("".equals(PID))){
            List<ST_STBPRP_B_QUPojo> quList = quData.selectList(null, null, null, PID,null);
            for(ST_STBPRP_B_QUPojo quPojo : quList){
                if(null != quPojo.getSTCD()){
                    stcdList.add(quPojo.getSTCD());
                }
            }
            List<ST_WDPSTAT_RPojo> wdpstatRList = new ArrayList<>();
            if(null != stcdList && stcdList.size() >0){
                wdpstatRList = data.selectList(stcdList,stime,etime);
            }
            for(int i = 0;i < timedif;i++){
                Double ACCDWSum = 0.00,ACCPWSum = 0.00;
                int finalI = i;
                long finalSTime = sTime;
                List<ST_WDPSTAT_RPojo> collectList = wdpstatRList.stream().filter(j -> {
                    if(null != j.getIDTM()){
                        try {
                            return dateFormat.parse(j.getIDTM()).getTime() >= finalSTime + (24 * 60 * 60 * 1000) * finalI
                                    && dateFormat.parse(j.getIDTM()).getTime() < finalSTime + (24 * 60 * 60 * 1000) * (finalI + 1);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    return false;
                }).collect(Collectors.toList());
                for(ST_WDPSTAT_RPojo pptnRPojo : collectList){
                    if(null != pptnRPojo.getACCDW())
                        ACCDWSum += pptnRPojo.getACCDW();
                    if(null != pptnRPojo.getACCPW())
                        ACCPWSum += pptnRPojo.getACCPW();
                }

                Map<String,Object> map = new HashMap<>();
                map.put("tm",dateFormat.format(new Date(sTime + (24 * 60 * 60 * 1000) * finalI)));
                map.put("accdw",ACCDWSum);
                map.put("accpw",ACCPWSum);
                mapList.add(map);
            }
        }
        watch.stop();
        if(mapList.size() > 0){
            return new ResultUtils<>(mapList, "操作成功",true ,mapList.size(),watch.getTime());
        }else {
            return new ResultUtils<>(mapList, "操作成功",false,mapList.size(),watch.getTime());
        }
    }

    @RequestMapping("/selectListByJH")//边界水量交换
    public ResultUtils selectListByJH(@RequestBody ColumnName param){
        StopWatch watch = new StopWatch();
        watch.start();
        Date date = new Date(new Date().getTime() - 24 * 60 * 60 * 1000);
        String stime= DateUtil.dateFormat(date,"yyyy-MM-dd HH:mm:ss"),etime="";
        List<String> stcdList = new ArrayList<>();//圩区ID
        if(null != param.getStcd()){
            stcdList = Arrays.asList(param.getStcd().split(","));
        }
        if(null != param.getStime()){
            stime = param.getStime();
        }
        if(null != param.getEtime()){
            etime = param.getEtime();
        }
        List<ST_STBPRP_BPojo> stStbprpBList = stbprpBData.selectList(stcdList, null);
        List<ST_WDPSTAT_RPojo> wdpstatRList = data.selectList(null,stime,etime);
        for(ST_STBPRP_BPojo stbprpBPojo : stStbprpBList){
            List<ST_WDPSTAT_RPojo> collect = wdpstatRList.stream().filter(i -> i.getSTCD().equals(stbprpBPojo.getSTCD())).collect(Collectors.toList());
            stbprpBPojo.setWdpstatRList(collect);
        }
        watch.stop();
        if(stStbprpBList.size() > 0){
            return new ResultUtils<>(stStbprpBList, "操作成功",true ,stStbprpBList.size(),watch.getTime());
        }else {
            return new ResultUtils<>(stStbprpBList, "操作成功",false,stStbprpBList.size(),watch.getTime());
        }
    }

    @RequestMapping("/selectTreeListBy")
    public ResultUtils selectTreeListBy(@RequestBody ColumnName param){
        StopWatch watch = new StopWatch();
        watch.start();
        Date date = new Date(new Date().getTime() - 168 * 60 * 60 * 1000);
        String stime= DateUtil.dateFormat(date,"yyyy-MM-dd HH:mm:ss"),etime="";
        String PID = "",stcd = "",pathname="";
        if(null != param.getPid()){
            PID = param.getPid();
        }
        if(null != param.getStime()){
            stime = param.getStime();
        }
        if(null != param.getEtime()){
            etime = param.getEtime();
        }
        if(null != param.getStcd()){
            stcd = param.getStcd();
        }
        if(null != param.getPathname()){
            pathname = param.getPathname();
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long timedif = 2,sTime = 0,eTime = 0;
        try {
            sTime = dateFormat.parse(stime).getTime();
            eTime = dateFormat.parse(etime).getTime();
            timedif =  (eTime - sTime) / (24 * 60 * 60 * 1000);
//            if(Integer.valueOf(new SimpleDateFormat("HH").format(eTime)) > 8){
            timedif = timedif + 1;
//            }
        } catch (ParseException e) {
            e.printStackTrace();
        }



        List<Map<String,Object>> mapList = new ArrayList<>();
        if(null != PID && !("".equals(PID))){
            ST_STBPRP_B_TREEPojo treePojo=new ST_STBPRP_B_TREEPojo();
            treePojo.setPID(PID);
            List<ST_STBPRP_B_TREEPojo> treeList=treeData.selectList(treePojo);
            if(treeList.size()>0){
                String finalPathname = pathname;
                String finalStime = stime;
                String finalEtime = etime;
                treeList.forEach(u->{
                    List<String> stcdList = new ArrayList<>();
                    List<ST_STBPRP_B_QUPojo> quList = quData.selectList(null, null, null, u.getID(),null);
                    for(ST_STBPRP_B_QUPojo quPojo : quList){
                        if(null != quPojo.getSTCD()){
                            stcdList.add(quPojo.getSTCD());
                        }
                    }
                    List<ST_WDPSTAT_RPojo> wdpstatRList = new ArrayList<>();
                    if(null != stcdList && stcdList.size() >0){
                        wdpstatRList = data.selectWdpstatRBXList(stcdList, finalStime, finalEtime);
                    }
                    if(finalPathname.equals("DAY")){
                        Date localDate1=DateUtil.strToDateTM(finalStime,DateUtil.YMDHMS);
                        Date localDate2=DateUtil.strToDateTM(finalEtime,DateUtil.YMDHMS);
                        long msNum=localDate2.getTime()-localDate1.getTime();
                        long daysDiff=msNum/(24*60*60*1000);
                        if(daysDiff>0){
                            for(int num=0;num<daysDiff;num++){
                                Date stime1=DateUtil.addDays(DateUtil.strToDate(finalStime,DateUtil.YMDHMS),num);
                                Date etime2=DateUtil.addDays(stime1,1);
                                List<ST_WDPSTAT_RPojo> finalwdpstatRList =wdpstatRList.stream().filter(e->DateUtil.strToDate(e.getIDTM(),DateUtil.YMDHMS).getTime()>=stime1.getTime()&&DateUtil.strToDate(e.getIDTM(),DateUtil.YMDHMS).getTime()<etime2.getTime()).collect(Collectors.toList());
                                Double ACCDWSum=finalwdpstatRList.stream().map(ST_WDPSTAT_RPojo::getACCDW) // 获取每个对象的 BigDecimal 属性值
                                        .filter(Objects::nonNull) // 过滤掉为 null 的值
                                        .reduce(0.0, Double::sum); // 将所有值累加起来
                                Double ACCPWSum=finalwdpstatRList.stream().map(ST_WDPSTAT_RPojo::getACCPW) // 获取每个对象的 BigDecimal 属性值
                                        .filter(Objects::nonNull) // 过滤掉为 null 的值
                                        .reduce(0.0, Double::sum); // 将所有值累加起来

                                Map<String,Object> map = new HashMap<>();
                                map.put("STNM",u.getTITLE());
                                map.put("ORDERBYID",u.getORDERBYID());
                                map.put("TM",DateUtil.dateFormat(stime1,DateUtil.YMDHMS));
                                map.put("accdw",ACCDWSum);
                                map.put("accpw",ACCPWSum);
                                mapList.add(map);
                            }
                        }
                    }
                    else{
                        Double ACCDWSum=wdpstatRList.stream().map(ST_WDPSTAT_RPojo::getACCDW) // 获取每个对象的 BigDecimal 属性值
                                .filter(Objects::nonNull) // 过滤掉为 null 的值
                                .reduce(0.0, Double::sum); // 将所有值累加起来
                        Double ACCPWSum=wdpstatRList.stream().map(ST_WDPSTAT_RPojo::getACCPW) // 获取每个对象的 BigDecimal 属性值
                                .filter(Objects::nonNull) // 过滤掉为 null 的值
                                .reduce(0.0, Double::sum); // 将所有值累加起来
                        Map<String,Object> map = new HashMap<>();
                        map.put("STNM",u.getTITLE());
                        map.put("ID",u.getID());
                        map.put("ORDERBYID",u.getORDERBYID());
                        map.put("accdw",ACCDWSum);
                        map.put("accpw",ACCPWSum);
                        mapList.add(map);

                    }

                });
            }

        }

        watch.stop();
        if(mapList.size() > 0){
            return new ResultUtils<>(mapList, "操作成功",true ,mapList.size(),watch.getTime());
        }else {
            return new ResultUtils<>(mapList, "操作成功",false,mapList.size(),watch.getTime());
        }
    }

    @RequestMapping("/selectWdpstatRBXList")//报汛逐日水量接口
    public ResultUtils selectWdpstatRBXList(@RequestBody ColumnName param){
        StopWatch watch = new StopWatch();
        watch.start();
        Date date = new Date(new Date().getTime() - 168 * 60 * 60 * 1000);
        String stime= DateUtil.dateFormat(date,"yyyy-MM-dd HH:mm:ss"),etime="";
        String PID = "",stcd = "";
        if(null != param.getPid()){
            PID = param.getPid();
        }
        if(null != param.getStime()){
            stime = param.getStime();
        }
        if(null != param.getEtime()){
            etime = param.getEtime();
        }
        if(null != param.getStcd()){
            stcd = param.getStcd();
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long timedif = 2,sTime = 0,eTime = 0;
        try {
            sTime = dateFormat.parse(stime).getTime();
            eTime = dateFormat.parse(etime).getTime();
            timedif =  (eTime - sTime) / (24 * 60 * 60 * 1000);
//            if(Integer.valueOf(new SimpleDateFormat("HH").format(eTime)) > 8){
            timedif = timedif + 1;
//            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        List<String> stcdList = new ArrayList<>();

        List<Map<String,Object>> mapList = new ArrayList<>();
        List<ST_WDPSTAT_RPojo> wdpstatRList = new ArrayList<>();
        if(null != PID && !("".equals(PID))){
            List<ST_STBPRP_B_QUPojo> quList = quData.selectList(null, null, null, PID,null);
            for(ST_STBPRP_B_QUPojo quPojo : quList){
                if(null != quPojo.getSTCD()){
                    stcdList.add(quPojo.getSTCD());
                }
            }
            if(null != stcdList && stcdList.size() >0){
                wdpstatRList = data.selectWdpstatRBXList(stcdList,stime,etime);
                List<ST_STBPRP_BPojo> oneStbprpBxB= stbprpBData.selectStbprpBXBList(stcdList,null);
                if(wdpstatRList.size()>0){
                    wdpstatRList.forEach(t->{
                        List<ST_STBPRP_BPojo> onePojo=oneStbprpBxB.stream().filter(u->u.getSTCD().equals(t.getSTCD())).collect(Collectors.toList());
                        if(onePojo.size()>0){
                            t.setSTNM(onePojo.get(0).getSTNM());
                        }
                    });
                }
            }
        }
        else{
            if(!CommonUtills.isEmpty(param.getStcd())){
                stcdList= Arrays.asList(param.getStcd().split(","));
                wdpstatRList = data.selectWdpstatRBXList(stcdList,stime,etime);
            }
        }
        watch.stop();
        if(wdpstatRList.size() > 0){
            return new ResultUtils<>(wdpstatRList, "操作成功",true ,wdpstatRList.size(),watch.getTime());
        }else {
            return new ResultUtils<>(wdpstatRList, "操作成功",false,wdpstatRList.size(),watch.getTime());
        }
    }

    @RequestMapping("/selectWdpstatRList")//遥测逐日水量接口
    public ResultUtils selectWdpstatRList(@RequestBody ColumnName param){
        StopWatch watch = new StopWatch();
        watch.start();
        Date date = new Date(new Date().getTime() - 168 * 60 * 60 * 1000);
        String stime= DateUtil.dateFormat(date,"yyyy-MM-dd HH:mm:ss"),etime="";
        String PID = "",stcd = "";
        if(null != param.getPid()){
            PID = param.getPid();
        }
        if(null != param.getStime()){
            stime = param.getStime();
        }
        if(null != param.getEtime()){
            etime = param.getEtime();
        }
        if(null != param.getStcd()){
            stcd = param.getStcd();
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long timedif = 2,sTime = 0,eTime = 0;
        try {
            sTime = dateFormat.parse(stime).getTime();
            eTime = dateFormat.parse(etime).getTime();
            timedif =  (eTime - sTime) / (24 * 60 * 60 * 1000);
//            if(Integer.valueOf(new SimpleDateFormat("HH").format(eTime)) > 8){
            timedif = timedif + 1;
//            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        List<String> stcdList = new ArrayList<>();

        List<Map<String,Object>> mapList = new ArrayList<>();
        List<ST_WDPSTAT_RPojo> wdpstatRList = new ArrayList<>();
        if(null != PID && !("".equals(PID))){
            List<ST_STBPRP_B_QUPojo> quList = quData.selectList(null, null, null, PID,null);
            for(ST_STBPRP_B_QUPojo quPojo : quList){
                if(null != quPojo.getSTCD()){
                    stcdList.add(quPojo.getSTCD());
                }
            }
            if(null != stcdList && stcdList.size() >0){
                wdpstatRList = data.selectWdpstatRList(stcdList,stime,etime);
                List<ST_STBPRP_BPojo> oneStbprpBxB= stbprpBData.selectStbprpBList(stcdList,null,"遥测");
                if(wdpstatRList.size()>0){
                    wdpstatRList.forEach(t->{
                        List<ST_STBPRP_BPojo> onePojo=oneStbprpBxB.stream().filter(u->u.getSTCD().equals(t.getSTCD())).collect(Collectors.toList());
                        if(onePojo.size()>0){
                            t.setSTNM(onePojo.get(0).getSTNM());
                        }
                    });
                }
            }
        }
        else{
            if(!CommonUtills.isEmpty(param.getStcd())){
                stcdList= Arrays.asList(param.getStcd().split(","));
                wdpstatRList = data.selectWdpstatRList(stcdList,stime,etime);
            }
        }
        watch.stop();
        if(wdpstatRList.size() > 0){
            return new ResultUtils<>(wdpstatRList, "操作成功",true ,wdpstatRList.size(),watch.getTime());
        }else {
            return new ResultUtils<>(wdpstatRList, "操作成功",false,wdpstatRList.size(),watch.getTime());
        }
    }


    @RequestMapping(value = "/selectGQDAYList")
    public List<ST_WDPSTAT_RPojo> selectGQDAYList(@RequestBody ColumnName param){
        List<ST_WDPSTAT_RPojo> userList=new ArrayList<>();
        Date date = new Date(new Date().getTime() - 168 * 60 * 60 * 1000);
        String stime= DateUtil.dateFormat(date,"yyyy-MM-dd HH:mm:ss"),etime="";
        String PID = "",stcd = "";
        if(null != param.getPid()){
            PID = param.getPid();
        }
        if(null != param.getStime()){
            stime = param.getStime();
        }
        if(null != param.getEtime()){
            etime = param.getEtime();
        }
        if(null != param.getStcd()){
            stcd = param.getStcd();
        }
        String pathname="DAY";
        if(!CommonUtills.isEmpty(param.getPathname())){
            pathname = param.getPathname();
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long timedif = 2,sTime = 0,eTime = 0;
        try {
            sTime = dateFormat.parse(stime).getTime();
            eTime = dateFormat.parse(etime).getTime();
            timedif =  (eTime - sTime) / (24 * 60 * 60 * 1000);
//            if(Integer.valueOf(new SimpleDateFormat("HH").format(eTime)) > 8){
            timedif = timedif + 1;
//            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        List<String> stcdList = new ArrayList<>();
        List<ST_STBPRP_B_QUPojo> quList =new ArrayList<>();
        if(null != PID && !("".equals(PID))){
            quList = quData.selectList(null, null, null, PID,null);
            for(ST_STBPRP_B_QUPojo quPojo : quList){
                if(null != quPojo.getSTCD()){
                    stcdList.add(quPojo.getSTCD());
                }
            }
        }
        if(null != stcdList && stcdList.size() >0){
            List<ST_WDPSTAT_RPojo> wdpstatRList = data.selectGQDAYList(stime,etime,stcdList);
//            List<ST_STBPRP_BPojo> oneStbprpBxB= stbprpBData.selectStbprpBList(stcdList,null,"");
            for(int num=0;num<stcdList.size();num++){
                String oneStcd=stcdList.get(num);

                List<ST_WDPSTAT_RPojo> oneWdpstatr=wdpstatRList.stream().filter(u->u.getSTCD().equals(oneStcd)).collect(Collectors.toList());
                if(pathname.equals("SUM")){
                    Double _accdwVal = oneWdpstatr.stream()
                            .mapToDouble(ST_WDPSTAT_RPojo::getACCDW)  // 假设 getValue() 返回 BigDecimal
                            .sum();
                    ST_WDPSTAT_RPojo pojo=new ST_WDPSTAT_RPojo();
                    pojo.setSTCD(oneStcd);
                    pojo.setACCDW(_accdwVal);
                    List<ST_STBPRP_B_QUPojo> onePojo=quList.stream().filter(u->u.getSTCD().equals(oneStcd)).collect(Collectors.toList());
                    if(onePojo.size()>0){
                        pojo.setSTNM(onePojo.get(0).getSTNM());
                    }
                    userList.add(pojo);
                }
                else{
                    List<ST_STBPRP_B_QUPojo> onePojo=quList.stream().filter(u->u.getSTCD().equals(oneStcd)).collect(Collectors.toList());
                    Date localDate1=DateUtil.strToDate(stime,DateUtil.YMDHMS);
                    Date localDate2=DateUtil.strToDate(etime,DateUtil.YMDHMS);
                    // 计算两个日期之间的毫秒差，转换为天数（向上取整）
                    BigDecimal msNum = BigDecimal.valueOf(localDate2.getTime() - localDate1.getTime());
                    BigDecimal miss = BigDecimal.valueOf(24 * 60 * 60 * 1000);
                    BigDecimal days = msNum.divide(miss, 3, RoundingMode.HALF_UP); // 保留10位小数确保精度
                    int daysDiff = (int) Math.ceil(days.doubleValue());
                    if(daysDiff>0){
                        for (int numII=0;numII<daysDiff;numII++){
                            Date times=DateUtil.addDays(DateUtil.strToDate(DateUtil.dateToStr(localDate1,DateUtil.YMD)+" 00:00:00",DateUtil.YMDHMS),numII);
                            long finalstime=times.getTime();
                            Date curtime=DateUtil.strToDate(DateUtil.dateToStr(times,DateUtil.YMD)+" 23:59:59",DateUtil.YMDHMS);
                            long finaletime=curtime.getTime();
                            List<ST_WDPSTAT_RPojo> tempWdpstatr=new ArrayList<>();
                            if(oneWdpstatr.size()>0){
                                tempWdpstatr=oneWdpstatr.stream().filter(u->{
//                                    DateUtil.strToDate(u.getIDTM(),DateUtil.YMDHMS).getTime()>=times.getTime()&&DateUtil.strToDate(u.getIDTM(),DateUtil.YMDHMS).getTime()<=curtime.getTime()
                                    long timestamp = DateUtil.strToDate(u.getIDTM(), DateUtil.YMD).getTime();
                                    return timestamp>=finalstime&&timestamp<=finaletime;

                                }).collect(Collectors.toList());
                            }
                            ST_WDPSTAT_RPojo pojo=new ST_WDPSTAT_RPojo();
                            pojo.setSTCD(oneStcd);
                            pojo.setIDTM(DateUtil.dateToStr(curtime,"yyyy-MM-dd 08:00:00"));
//                            if(tempWdpstatr.size()>0){
//                                pojo.setACCDW(tempWdpstatr.get(0).getACCDW());
//                            }
                            pojo.setACCPW(0.00);
                            // 计算总和并保留两位小数（四舍五入）
                            Double _accdwVal = BigDecimal.valueOf(
                                            tempWdpstatr.stream()
                                                    .mapToDouble(ST_WDPSTAT_RPojo::getACCDW)
                                                    .sum()
                                    ).setScale(2, RoundingMode.HALF_UP)
                                    .doubleValue();

                            pojo.setACCDW(_accdwVal);
                            if(onePojo.size()>0){
                                pojo.setSTNM(onePojo.get(0).getSTNM());
                            }
                            userList.add(pojo);

                        }
                    }
                }
            }
        }

        return userList;
    }

    @RequestMapping("/selectSUMSLListBy")
    public List<Map<String,Object>> selectSUMSLListBy(@RequestBody ColumnName param){
        Date date = new Date(new Date().getTime() - 168 * 60 * 60 * 1000);
        String stime= DateUtil.dateFormat(date,"yyyy-MM-dd HH:mm:ss"),etime="";
        String PID = "",stcd = "",pathname="";
        if(null != param.getPid()){
            PID = param.getPid();
        }
        if(null != param.getStime()){
            stime = param.getStime();
        }
        if(null != param.getEtime()){
            etime = param.getEtime();
        }
        if(null != param.getStcd()){
            stcd = param.getStcd();
        }
        if(null != param.getPathname()){
            pathname = param.getPathname();
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long timedif = 2,sTime = 0,eTime = 0;
        try {
            sTime = dateFormat.parse(stime).getTime();
            eTime = dateFormat.parse(etime).getTime();
            timedif =  (eTime - sTime) / (24 * 60 * 60 * 1000);
//            if(Integer.valueOf(new SimpleDateFormat("HH").format(eTime)) > 8){
            timedif = timedif + 1;
//            }
        } catch (ParseException e) {
            e.printStackTrace();
        }



        List<Map<String,Object>> mapList = new ArrayList<>();
        if(null != PID && !("".equals(PID))){
            ST_STBPRP_B_TREEPojo treePojo=new ST_STBPRP_B_TREEPojo();
            treePojo.setPID(PID);
            List<ST_STBPRP_B_TREEPojo> treeList=treeData.selectList(treePojo);
            if(treeList.size()>0){
                String finalPathname = pathname;
                String finalStime = stime;
                String finalEtime = etime;
                treeList.forEach(u->{
                    List<String> stcdList = new ArrayList<>();
                    List<ST_STBPRP_B_QUPojo> quList = quData.selectList(null, null, null, u.getID(),null);
                    for(ST_STBPRP_B_QUPojo quPojo : quList){
                        if(null != quPojo.getSTCD()){
                            stcdList.add(quPojo.getSTCD());
                        }
                    }
                    List<ST_WDPSTAT_RPojo> wdpstatRList = new ArrayList<>();
                    if(null != stcdList && stcdList.size() >0){
                        wdpstatRList = data.selectWdpstatRBXList(stcdList, finalStime, finalEtime);
                    }
                    if(finalPathname.equals("DAY")){
                        Date localDate1=DateUtil.strToDateTM(finalStime,DateUtil.YMDHMS);
                        Date localDate2=DateUtil.strToDateTM(finalEtime,DateUtil.YMDHMS);
                        long msNum=localDate2.getTime()-localDate1.getTime();
                        long daysDiff=msNum/(24*60*60*1000);
                        if(daysDiff>0){
                            for(int num=0;num<daysDiff;num++){
                                Date stime1=DateUtil.addDays(DateUtil.strToDate(finalStime,DateUtil.YMDHMS),num);
                                Date etime2=DateUtil.addDays(stime1,1);
                                List<ST_WDPSTAT_RPojo> finalwdpstatRList =wdpstatRList.stream().filter(e->DateUtil.strToDate(e.getIDTM(),DateUtil.YMDHMS).getTime()>=stime1.getTime()&&DateUtil.strToDate(e.getIDTM(),DateUtil.YMDHMS).getTime()<etime2.getTime()).collect(Collectors.toList());
                                Double ACCDWSum=finalwdpstatRList.stream().map(ST_WDPSTAT_RPojo::getACCDW) // 获取每个对象的 BigDecimal 属性值
                                        .filter(Objects::nonNull) // 过滤掉为 null 的值
                                        .reduce(0.0, Double::sum); // 将所有值累加起来
                                Double ACCPWSum=finalwdpstatRList.stream().map(ST_WDPSTAT_RPojo::getACCPW) // 获取每个对象的 BigDecimal 属性值
                                        .filter(Objects::nonNull) // 过滤掉为 null 的值
                                        .reduce(0.0, Double::sum); // 将所有值累加起来

                                Map<String,Object> map = new HashMap<>();
                                map.put("STNM",u.getTITLE());
                                map.put("ORDERBYID",u.getORDERBYID());
                                map.put("TM",DateUtil.dateFormat(stime1,DateUtil.YMDHMS));
                                map.put("accdw",ACCDWSum);
                                map.put("accpw",ACCPWSum);
                                mapList.add(map);
                            }
                        }
                    }
                    else{
                        Double ACCDWSum=wdpstatRList.stream().map(ST_WDPSTAT_RPojo::getACCDW) // 获取每个对象的 BigDecimal 属性值
                                .filter(Objects::nonNull) // 过滤掉为 null 的值
                                .reduce(0.0, Double::sum); // 将所有值累加起来
                        Double ACCPWSum=wdpstatRList.stream().map(ST_WDPSTAT_RPojo::getACCPW) // 获取每个对象的 BigDecimal 属性值
                                .filter(Objects::nonNull) // 过滤掉为 null 的值
                                .reduce(0.0, Double::sum); // 将所有值累加起来
                        Map<String,Object> map = new HashMap<>();
                        map.put("STNM",u.getTITLE());
                        map.put("ID",u.getID());
                        map.put("ORDERBYID",u.getORDERBYID());
                        map.put("accdw",ACCDWSum);
                        map.put("accpw",ACCPWSum);
                        mapList.add(map);

                    }

                });
            }

        }
        return mapList;
    }


    @RequestMapping("/INOUTWDPSTATDAYSel")
    public ResultUtils INOUTWDPSTATDAYSel(@RequestBody ColumnName param){
        StopWatch watch = new StopWatch();
        watch.start();
        Date date = new Date(new Date().getTime() - 168 * 60 * 60 * 1000);
        String stime= DateUtil.dateFormat(date,"yyyy-MM-dd HH:mm:ss"),etime="";
        String PID = "", pathname="",rvnm="";
        List<String> listSTCD=new ArrayList<>();
        List<String> listTYPE=new ArrayList<>();
        if(null != param.getPid()){
            PID = param.getPid();
        }
        if(null != param.getStime()){
            stime = param.getStime();
        }
        if(null != param.getEtime()){
            etime = param.getEtime();
        }
        if(null != param.getStcd()){
            listSTCD = Arrays.asList(param.getStcd().split(","));
        }
        if(null != param.getPathname()){
            pathname = param.getPathname();
        }
        if(null != param.getDatasource()){
            rvnm = param.getDatasource();
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long timedif = 2,sTime = 0,eTime = 0;
        try {
            sTime = dateFormat.parse(stime).getTime();
            eTime = dateFormat.parse(etime).getTime();
            timedif =  (eTime - sTime) / (24 * 60 * 60 * 1000);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(!CommonUtills.isEmpty(pathname)){
            listTYPE = Arrays.asList(pathname.split(","));
        }
        List<ST_STBPRP_B_TREEPojo> treeList=treeData.selectListByID(listTYPE,null);
        List<String> listPID= treeList.stream()
                .map(ST_STBPRP_B_TREEPojo::getID) // 或者 item -> item.getPid()
                .collect(Collectors.toList());

        List<ST_STBPRP_B_QUPojo> quList = quData.queryList(null, null, null, listPID);
        listSTCD=quList.stream()
                .map(ST_STBPRP_B_QUPojo::getSTCD) // 或者 item -> item.getPid()
                .collect(Collectors.toList());

        List<INOUTWDPSTATPojo> userList = new ArrayList<>();
        List<INOUTWDPSTATPojo> allList=data.INOUTWDPSTATDAYSel(stime,etime,listSTCD,pathname,rvnm);
        if(timedif>0){
            for(int num=0;num<=timedif;num++){
                Date  stm=DateUtil.addDays(DateUtil.strToDateTM(stime,DateUtil.YMD),num);
                Date etm=DateUtil.addDays(stm,1);
                if(quList.size()>0){
                    for(int i=0;i<quList.size();i++){
                        String stcd=quList.get(i).getSTCD();
                        if(!CommonUtills.isEmpty(stcd)){
                            List<INOUTWDPSTATPojo> oneList=allList.stream().filter(u-> {
                                try {
                                    return dateFormat.parse(u.getTM()).getTime()>=stm.getTime()&&dateFormat.parse(u.getTM()).getTime()<=etm.getTime()&&u.getSTCD().equals(stcd);
                                } catch (ParseException e) {
                                    throw new RuntimeException(e);
                                }
                            }).collect(Collectors.toList());
                            String pid=  quList.get(i).getPID();
                            List<ST_STBPRP_B_TREEPojo>  treeListTemp=  treeList.stream().filter(p->p.getID().equals(pid)).collect(Collectors.toList());
                            INOUTWDPSTATPojo pojo=new INOUTWDPSTATPojo();
                            pojo.setSTCD(stcd);
                            pojo.setSTNM(quList.get(i).getSTNM());
                            pojo.setRVNM(treeListTemp.size()>0?treeListTemp.get(0).getID():null);
                            pojo.setPATHNAME(treeListTemp.size()>0?treeListTemp.get(0).getTITLE():null);
                            pojo.setTM(DateUtil.dateFormat(stm,DateUtil.YMD)+" 08:00:00");
                            pojo.setINQ(0.0);
                            pojo.setOUTQ(0.0);
                            if(oneList.size()>0){
                                pojo.setINQ(oneList.get(0).getINQ());
                                pojo.setOUTQ(oneList.get(0).getOUTQ());
                                pojo.setTM(oneList.get(0).getTM());
                            }
                            userList.add(pojo);

                        }
                    }
                }
            }
        }
        watch.stop();
        if(userList.size() > 0){
            return new ResultUtils<>(userList, "操作成功",true ,treeList.size(),watch.getTime());
        }else {
            return new ResultUtils<>(userList, "操作成功",false,treeList.size(),watch.getTime());
        }
    }

}
