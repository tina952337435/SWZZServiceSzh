package swzzmodeserver.workserver.controller.swzzrtsq;

import swzzmodeserver.workserver.data.swzzrtsq.RTSQST_PPTN_RData;
import swzzmodeserver.workserver.data.swzzrtsq.ST_PSTAT_RData;
import swzzmodeserver.workserver.data.swzzrtsq.ST_STBPRP_B_QUData;
import swzzmodeserver.workserver.data.swzzrtsq.ST_STBPRP_B_TREEData;
import swzzmodeserver.workserver.data.swzzrtsq.RTSQST_STBPRP_BData;
import swzzmodeserver.workserver.data.swzzrtsq.RTSQST_STBPRP_B_STCDData;
import swzzmodeserver.workserver.pojo.swzzrtsq.*;
import swzzmodeserver.tools.ColumnName;
import swzzmodeserver.tools.CommonUtills;
import swzzmodeserver.tools.RainfallAnalyzer;
import swzzmodeserver.tools.ResultUtils;
import swzzmodeserver.tools.TimeRangeChecker;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import swzzmodeserver.tools.DateUtil;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/SWZZ_RTSQ_ST_PSTAT_R")
public class ST_PSTAT_RController {
    @Autowired
    private ST_PSTAT_RData data;

    @Autowired
    private RTSQST_PPTN_RData st_pptn_rData;
    @Autowired
    private RTSQST_STBPRP_B_STCDData st_stbprp_b_stcdData;

    @Autowired
    private RTSQST_STBPRP_BData stbprpBData;

    @Autowired
    private ST_STBPRP_B_TREEData treeData;

    @Autowired
    private ST_STBPRP_B_QUData quData;

    //文件存储路径
    @Value("${file.path.templatefilepath}")
    private String templatefilepath;

    @Autowired
    private CommonUtills commonUtills;

    @RequestMapping("/findResult")
    public Map<String, Object> selectList(@RequestBody ColumnName param) {
        Date date = new Date(new Date().getTime() - 24 * 60 * 60 * 1000);
        String stime = "",   etime = "";
        List<String> stcdList = new ArrayList<>();
        List<String> typeList = new ArrayList<>();
        if (null != param.getStcd()&&!param.getStcd().equals("")) {
            stcdList = Arrays.asList(param.getStcd().split(","));
        }
        if (null != param.getPathname()) {
            typeList = Arrays.asList(param.getPathname().split(","));
        }
        if (null != param.getStime()) {
            stime = param.getStime();
        }
        if (null != param.getEtime()) {
            etime = param.getEtime();
        }
        List<ST_PSTAT_RPojo> pptnList = data.selectList(stcdList, stime, etime, typeList);
        if (null != pptnList) {
            return commonUtills.returnJson("select", pptnList.size(), pptnList);
        } else {
            return commonUtills.returnJson("select", -1, pptnList);
        }
    }

    @RequestMapping("/modify")
    public Map<String, Object> upDateOne(@RequestBody ST_PSTAT_RPojo quPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        Integer integer = data.upDateOne(quPojo);
        watch.stop();
        return commonUtills.returnJson("insert",integer,null);
    }

    @RequestMapping("/modifyxiuzheng")
    public Map<String, Object> modifyxiuzheng(@RequestBody ST_PSTAT_RPojo quPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        Integer integer=0;
        if(quPojo.getDATASTATUS()==null) {
            integer = data.upDateOne(quPojo);
        }
        else{
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            DateTimeFormatter formatterH = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:00:00");
            String tm=quPojo.getIDTM();
            List<String> stcdListR= Arrays.asList(quPojo.getSTCD().split(","));

            //********************************************改小时数据
            LocalDateTime tmDateTime = LocalDateTime.parse(tm, formatter);
            String tmStime= tmDateTime.minusHours(0).format(formatterH);
            String tmEtime= tmDateTime.plusHours(1).format(formatterH);//加上一个小时
            List<ST_PPTN_RPojo> ListRH=st_pptn_rData.selectListByTime(stcdListR,tmStime,tmEtime,"BX");
            double sumDRP = ListRH.stream()
                    .filter(item -> item.getDRP() != null) // 过滤掉 DRP 为 null 的项
                    .mapToDouble(ST_PPTN_RPojo::getDRP)   // 将 DRP 提取为 double 类型
                    .sum();
            ST_PSTAT_RPojo pojop=new ST_PSTAT_RPojo();
            pojop.setSTCD(quPojo.getSTCD());
            pojop.setIDTM(tmEtime);
            pojop.setACCP(sumDRP);
            pojop.setSTTDRCD("h");
            pojop.setWTH("1");
            integer = data.upDateOne(pojop);
            //********************************************改小时数据

            //********************************************改日数据
            String boundaryTime = TimeRangeChecker.getBoundaryTime(tm);
            LocalDateTime boundaryDateTime = LocalDateTime.parse(boundaryTime, formatter);
            String stime= boundaryDateTime.minusHours(24).format(formatter);
            List<ST_PPTN_RPojo> ListR=st_pptn_rData.selectListByTime(stcdListR,stime,boundaryTime,"BX");
            double sumDRPDay = ListR.stream()
                    .filter(item -> item.getDRP() != null) // 过滤掉 DRP 为 null 的项
                    .mapToDouble(ST_PPTN_RPojo::getDRP)   // 将 DRP 提取为 double 类型
                    .sum();
            ST_PSTAT_RPojo quPojoDay=new ST_PSTAT_RPojo();
            quPojoDay.setSTCD(quPojo.getSTCD());
            quPojoDay.setIDTM(boundaryTime); //存在截止日期里面
            quPojoDay.setSTTDRCD("1");
            quPojoDay.setACCP(sumDRPDay);
            quPojoDay.setWTH("1");
            integer = data.upDateOne(quPojoDay);
            //********************************************改日数据
        }

        watch.stop();
        return commonUtills.returnJson("insert",integer,null);
    }

    @RequestMapping("/addAll")
    public Map<String,Object> addAll(@RequestBody List<ST_PSTAT_RPojo> quPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        Integer integer = data.insertAll(quPojo);
        watch.stop();
        return commonUtills.returnJson("insert",integer,quPojo);
    }


    //统计表日数据入库
    @RequestMapping("/addAllDay")
    public Map<String, Object> addAllDay(@RequestBody ColumnName param)
    {
        StopWatch watch = new StopWatch();
        watch.start();
        List<ST_PSTAT_RPojo> listData=new ArrayList<>();
        Date date = new Date(new Date().getTime() - 24 * 60 * 60 * 1000);
        String stimes = "",   etimes= "";
        List<String> stcdList = new ArrayList<>();
        if (null != param.getStcd()&&!param.getStcd().equals("")) {
            stcdList = Arrays.asList(param.getStcd().split(","));
        }
        if (null != param.getStime()) {
            stimes = param.getStime();
        }
        if (null != param.getEtime()) {
            etimes = param.getEtime();
        }
        List<ST_PPTN_RPojo> userData = st_pptn_rData.selectListByTime(stcdList, stimes, etimes,"BX");

        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
        for (String item : stcdList) {
            // 按测站筛选数据
            List<ST_PPTN_RPojo> userDataT = userData.stream()
                    .filter(p -> p.getSTCD().equals(item))
                    .collect(Collectors.toList());

            // 按日期分组
            Map<String, List<ST_PPTN_RPojo>> groupedByDay = userDataT.stream()
                    .collect(Collectors.groupingBy(
                            p -> {
                                try {
                                    return DAY_FORMAT.format(DATE_FORMAT.parse(p.getTM()));
                                } catch (ParseException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                    ));

            // 按日期排序处理
            groupedByDay.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> {
                        try {
                            String dayStr = entry.getKey();
                            Date stime = DATE_FORMAT.parse(dayStr + " 08:00:00");
                            Date etime = DATE_FORMAT.parse(dayStr + " 08:00:00");
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(etime);
                            cal.add(Calendar.DAY_OF_MONTH, 1);
                            etime = cal.getTime();

                            // 筛选水文日数据(前一天8时至当天8时)
                            Date finalEtime = etime;
                            List<ST_PPTN_RPojo> userDataTT = userDataT.stream()
                                    .filter(p -> {
                                        try {
                                            Date tm = DATE_FORMAT.parse(p.getTM());
                                            return tm.after(stime) && tm.before(finalEtime);
                                        } catch (ParseException e) {
                                            return false;
                                        }
                                    })
                                    .collect(Collectors.toList());

                            // 处理8点整的数据
                            List<ST_PPTN_RPojo> userDataTTT = userDataT.stream()
                                    .filter(p -> {
                                        try {
                                            return p.getTM().equals(DATE_FORMAT.format(finalEtime));
                                        } catch (Exception e) {
                                            return false;
                                        }
                                    })
                                    .collect(Collectors.toList());

                            if (!userDataTT.isEmpty()) {
                                // 计算日雨量
                                double drp =userDataTT.stream().filter(p->p.getDRP()>=0)
                                        .mapToDouble(ST_PPTN_RPojo::getDRP)
                                        .sum();
                                // 将etime转换为String
                                String etimeString = DATE_FORMAT.format(etime);
                                ST_PSTAT_RPojo dto = new ST_PSTAT_RPojo();
                                dto.setSTCD(item);
                                dto.setIDTM(etimeString);//截止日期
                                dto.setSTTDRCD("1");
                                dto.setACCP(drp);
                                listData.add(dto);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    });
        }
        Integer integer = data.insertAll(listData);
        watch.stop();
        return commonUtills.returnJson("insert",integer,listData);
    }


    @RequestMapping("/selectListIsLast")
    public Map<String,Object> selectListIsLast(@RequestBody ColumnName param){
        Date date = new Date(new Date().getTime() - 24 * 60 * 60 * 1000);
        String stime="",//DateUtil.dateFormat(date,"yyyy-MM-dd 08:00:00"),
                etime="",type="Minute";
        List<String> stcdList = new ArrayList<>();
        List<String> typeList = new ArrayList<>();
        if(null != param.getStcd()){
            stcdList = Arrays.asList(param.getStcd().split(","));
        }
        if(null != param.getPathname()){
            typeList = Arrays.asList(param.getPathname().split(","));
        }
        if(null != param.getStime()){
            stime = param.getStime();
        }
        if(null != param.getEtime()){
            etime = param.getEtime();
        }
        List<ST_PSTAT_RPojo> pptnList = new ArrayList<>();
        pptnList = data.selectListIsLast(stcdList, stime, etime,typeList);
        if(null != pptnList){
            return commonUtills.returnJson("select",pptnList.size(),pptnList);
        }else {
            return commonUtills.returnJson("select", -1, pptnList);
        }
    }




    //统计数据入库：包括日、月、旬数据
    @RequestMapping("/addRainStatistics")
    public Map<String, Object> addRainStatistics(@RequestBody ColumnName param)
    {
        StopWatch watch = new StopWatch();
        watch.start();
        List<ST_PSTAT_RPojo> listData=new ArrayList<>();
        String stimes = "",   etimes= "",pathname="1";
        List<String> stcdList = new ArrayList<>();
        if (null != param.getStcd()&&!param.getStcd().equals("")) {
            stcdList = Arrays.asList(param.getStcd().split(","));
        }
        if (null != param.getStime()) {
            stimes = param.getStime();
        }
        if (null != param.getEtime()) {
            etimes = param.getEtime();
        }
        if(null !=param.getPathname()){
            pathname=param.getPathname();
        }
        List<ST_PPTN_RPojo> userData = st_pptn_rData.selectListTotal(stcdList, stimes, etimes);

        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
        for (String item : stcdList) {
            // 按测站筛选数据
            List<ST_PPTN_RPojo> userDataT = userData.stream()
                    .filter(p -> p.getSTCD().equals(item))
                    .collect(Collectors.toList());
            if(userDataT.size()>0){
                // 计算日雨量
                double drp =userDataT.stream().filter(p->p.getDRP()>=0)
                        .mapToDouble(ST_PPTN_RPojo::getDRP)
                        .sum();

                ST_PSTAT_RPojo dto = new ST_PSTAT_RPojo();
                dto.setSTCD(item);
                dto.setIDTM(etimes);//截止日期
                dto.setSTTDRCD(pathname);
                dto.setACCP(drp);
                listData.add(dto);
            }
        }
        Integer integer = data.insertAll(listData);
        watch.stop();
        return commonUtills.returnJson("insert",integer,listData);
    }

    //统计数据入库：小时
    @RequestMapping("/addRainStatisticsHourly")
    public Map<String, Object> addRainStatisticsHourly(@RequestBody ColumnName param)
    {
        StopWatch watch = new StopWatch();
        watch.start();
        List<ST_PSTAT_RPojo> listData=new ArrayList<>();
        String stimes = "",   etimes= "",pathname="1";
        List<String> stcdList = new ArrayList<>();
        if (null != param.getStcd()&&!param.getStcd().equals("")) {
            stcdList = Arrays.asList(param.getStcd().split(","));
        }
        if (null != param.getStime()) {
            stimes = param.getStime();
        }
        if (null != param.getEtime()) {
            etimes = param.getEtime();
        }
        if(null !=param.getPathname()){
            pathname=param.getPathname();
        }
        List<ST_PPTN_RPojo> userData = st_pptn_rData.selectListHourly(stcdList, stimes, etimes);
        String finalPathname = pathname;
        userData.forEach(item->{
            ST_PSTAT_RPojo dto = new ST_PSTAT_RPojo();
            dto.setSTCD(item.getSTCD());
            dto.setIDTM(item.getTM());//截止日期
            dto.setSTTDRCD(finalPathname);
            dto.setACCP(item.getDRP());
            listData.add(dto);
        });
        Integer integer = 0;
        if(listData.size()>0){
            int count = 80;
            int number = listData.size() / count;
            if(listData.size() % count != 0){
                number = number + 1;
            }
            List<ST_PSTAT_RPojo> list = new ArrayList<>();
            for(int i = 0;i < number;i++){
                if(i == number - 1){
                    list = listData.subList(count * i,listData.size());
                }else {
                    list = listData.subList(count * i,count * ( i + 1));
                }
                integer += data.insertAll(list);
            }
        }
        watch.stop();
        return commonUtills.returnJson("insert",integer,listData);
    }

    //基于ST_PSTAT_R表统计数据入库：包括日、月、旬数据
    @RequestMapping("/addRainStatisticsTJ")
    public Map<String, Object> addRainStatisticsTJ(@RequestBody ColumnName param)
    {
        StopWatch watch = new StopWatch();
        watch.start();
        String stimes = "",   etimes= "",pathname="1";
        List<String> stcdList = new ArrayList<>();
        List<String> typeList = new ArrayList<>();
        if (null != param.getStcd()&&!param.getStcd().equals("")) {
            stcdList = Arrays.asList(param.getStcd().split(","));
        }
        if (null != param.getStime()) {
            stimes = param.getStime();
        }
        if (null != param.getEtime()) {
            etimes = param.getEtime();
        }
        if(null !=param.getPathname()){
            pathname=param.getPathname();
        }
        if(null !=param.getDatasource()){
            typeList=Arrays.asList(param.getDatasource().split(","));
        }
        List<ST_PSTAT_RPojo> listData = new ArrayList<>();
        if(pathname.equals("1")){
            listData=data.selectListDayly(stcdList, stimes, etimes,typeList);//整理日数据入库
        }
        Integer integer = 0;
        if(listData.size()>0){
            int count = 80;
            int number = listData.size() / count;
            if(listData.size() % count != 0){
                number = number + 1;
            }
            List<ST_PSTAT_RPojo> list = new ArrayList<>();
            for(int i = 0;i < number;i++){
                if(i == number - 1){
                    list = listData.subList(count * i,listData.size());
                }else {
                    list = listData.subList(count * i,count * ( i + 1));
                }
                integer += data.insertAll(list);
            }
        }
        watch.stop();
        return commonUtills.returnJson("insert",integer,listData);

//        if(null != listData){
//            return commonUtills.returnJson("select",listData.size(),listData);
//        }else {
//            return commonUtills.returnJson("select", -1, listData);
//        }
    }

    //小时表查询
    @RequestMapping("/HourlyReport")
    public Map<String, Object> HourlyReport(@RequestBody ColumnName param)
    {
        StopWatch watch = new StopWatch();
        watch.start();
        List<ST_PSTAT_RPojo> listData=new ArrayList<>();
        Date date = new Date(new Date().getTime() - 24 * 60 * 60 * 1000);
        String  stime="", etime= "";
        List<String> stcdList = new ArrayList<>();
        List<String> typeList =Arrays.asList("h".split(","));
        if (null != param.getStcd()&&!param.getStcd().equals("")) {
            stcdList = Arrays.asList(param.getStcd().split(","));
        }
        if(null!=param.getPathname()){
            typeList = Arrays.asList(param.getPathname().split(","));
        }
        if (null != param.getStime()) {
            stime = param.getStime();
        }
        if (null != param.getEtime()) {
            etime = param.getEtime();
        }
        List<ST_PSTAT_RPojo> ListData=new ArrayList<>();
        List<ST_PSTAT_RPojo> userData = data.selectListHour(stcdList, stime, etime,typeList);
        List<ST_STBPRP_BPojo> listB = stbprpBData.selectList(stcdList, null);
        userData.forEach(u->{
            ST_PSTAT_RPojo pojo =new ST_PSTAT_RPojo();
            pojo.setSTCD(u.getSTCD());
            pojo.setIDTM(u.getIDTM());
            pojo.setSTTDRCD(u.getSTTDRCD());
            pojo.setACCP(u.getACCP());
            List<ST_STBPRP_BPojo> listBT=listB.stream().filter(p->p.getSTCD().equals(u.getSTCD())).collect(Collectors.toList());;
            if(listBT.size()>0){
                pojo.setSTNM(listBT.get(0).getSTNM());
                pojo.setHNNM(listBT.get(0).getSTLC());
            }
            ListData.add(pojo);
        });

        watch.stop();
        if (null != userData) {
            return commonUtills.returnJson("select", ListData.size(), ListData);
        } else {
            return commonUtills.returnJson("select", -1, ListData);
        }
    }

    //日报表查询
    @RequestMapping("/DailyReport")
    public Map<String, Object> DailyReport(@RequestBody ColumnName param)
    {
        StopWatch watch = new StopWatch();
        watch.start();
        List<ST_PSTAT_RPojo> listData=new ArrayList<>();
        Date date = new Date(new Date().getTime() - 24 * 60 * 60 * 1000);
        String  stime="", etime= "";
        List<String> stcdList = new ArrayList<>();
        List<String> typeList = new ArrayList<>();
        if (null != param.getStcd()&&!param.getStcd().equals("")) {
            stcdList = Arrays.asList(param.getStcd().split(","));
        }
        if(null!=param.getPathname()){
            typeList = Arrays.asList(param.getPathname().split(","));
        }
        if (null != param.getStime()) {
            stime = param.getStime();
        }
        if (null != param.getEtime()) {
            etime = param.getEtime();
        }
//        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
//        try {
//            Date etime = DATE_FORMAT.parse(etimes);
//
//            Calendar cal = Calendar.getInstance();
//            cal.setTime(etime);
//            cal.add(Calendar.DAY_OF_MONTH, 1);//加一天
//            etime = cal.getTime();
//            etimes = DATE_FORMAT.format(etime);
//        }catch (ParseException e) {
//            //e.printStackTrace();
//        }

        List<ST_PSTAT_RPojo> ListData=new ArrayList<>();
        List<ST_PSTAT_RPojo> userData = data.selectListDay(stcdList, stime, etime,typeList);
        List<ST_STBPRP_BPojo> listB = stbprpBData.selectList(stcdList, null);
        userData.forEach(u->{
            ST_PSTAT_RPojo pojo =new ST_PSTAT_RPojo();
            pojo.setSTCD(u.getSTCD());
            pojo.setIDTM(u.getIDTM());
            pojo.setSTTDRCD(u.getSTTDRCD());
            pojo.setACCP(u.getACCP());
            List<ST_STBPRP_BPojo> listBT=listB.stream().filter(p->p.getSTCD().equals(u.getSTCD())).collect(Collectors.toList());;
            if(listBT.size()>0){
                pojo.setSTNM(listBT.get(0).getSTNM());
                pojo.setHNNM(listBT.get(0).getSTLC());
                pojo.setLGTD(listBT.get(0).getLGTD());
                pojo.setLTTD(listBT.get(0).getLTTD());
                pojo.setLGTD84(listBT.get(0).getLGTD84());
                pojo.setLTTD84(listBT.get(0).getLTTD84());
            }
            ListData.add(pojo);
        });

        watch.stop();
        if (null != userData) {
            return commonUtills.returnJson("select", ListData.size(), ListData);
        } else {
            return commonUtills.returnJson("select", -1, ListData);
        }
    }

    //月报表查询
    @RequestMapping("/MonthlyReport")
    public Map<String, Object> MonthlyReport(@RequestBody ColumnName param)
    {
        StopWatch watch = new StopWatch();
        watch.start();
        List<ST_PSTAT_RPojo> listData=new ArrayList<>();
        Date date = new Date(new Date().getTime() - 24 * 60 * 60 * 1000);
        String  stime="",etime= "";
        List<String> stcdList = new ArrayList<>();
        List<String> typeList = new ArrayList<>();
        if (null != param.getStcd()&&!param.getStcd().equals("")) {
            stcdList = Arrays.asList(param.getStcd().split(","));
        }
        if(null!=param.getPathname()){
            typeList = Arrays.asList(param.getPathname().split(","));
        }
        if (null != param.getStime()) {
            stime = param.getStime();
        }
        if (null != param.getEtime()) {
            etime = param.getEtime();
        }
        List<ST_PSTAT_RPojo> ListData=new ArrayList<>();
        List<ST_PSTAT_RPojo> userData = data.selectListMonth(stcdList, stime, etime,typeList);
        List<ST_STBPRP_BPojo> listB = stbprpBData.selectList(stcdList, null);
        userData.forEach(u->{
            ST_PSTAT_RPojo pojo =new ST_PSTAT_RPojo();
            pojo.setSTCD(u.getSTCD());
            pojo.setIDTM(u.getIDTM());
            pojo.setSTTDRCD(u.getSTTDRCD());
            pojo.setACCP(u.getACCP());
            List<ST_STBPRP_BPojo> listBT=listB.stream().filter(p->p.getSTCD().equals(u.getSTCD())).collect(Collectors.toList());;
            if(listBT.size()>0){
                pojo.setSTNM(listBT.get(0).getSTNM());
                pojo.setHNNM(listBT.get(0).getSTLC());
            }
            ListData.add(pojo);
        });

        watch.stop();
        if (null != userData) {
            return commonUtills.returnJson("select", ListData.size(), ListData);
        } else {
            return commonUtills.returnJson("select", -1, ListData);
        }
    }

    //月报表查询
    @RequestMapping("/YearlyReport")
    public Map<String, Object> YearlyReport(@RequestBody ColumnName param)
    {
        StopWatch watch = new StopWatch();
        watch.start();
        List<ST_PSTAT_RPojo> listData=new ArrayList<>();
        Date date = new Date(new Date().getTime() - 24 * 60 * 60 * 1000);
        String  stime="",etime= "";
        List<String> stcdList = new ArrayList<>();
        List<String> typeList = new ArrayList<>();
        if (null != param.getStcd()&&!param.getStcd().equals("")) {
            stcdList = Arrays.asList(param.getStcd().split(","));
        }
        if(null!=param.getPathname()){
            typeList = Arrays.asList(param.getPathname().split(","));
        }
        if (null != param.getStime()) {
            stime = param.getStime();
        }
        if (null != param.getEtime()) {
            etime = param.getEtime();
        }
        List<ST_PSTAT_RPojo> ListData=new ArrayList<>();
        List<ST_PSTAT_RPojo> userData = data.selectListYear(stcdList, stime, etime,typeList);
        List<ST_STBPRP_BPojo> listB = stbprpBData.selectList(stcdList, null);
        userData.forEach(u->{
            ST_PSTAT_RPojo pojo =new ST_PSTAT_RPojo();
            pojo.setSTCD(u.getSTCD());
            pojo.setIDTM(u.getIDTM());
            pojo.setSTTDRCD(u.getSTTDRCD());
            pojo.setACCP(u.getACCP());
            List<ST_STBPRP_BPojo> listBT=listB.stream().filter(p->p.getSTCD().equals(u.getSTCD())).collect(Collectors.toList());;
            if(listBT.size()>0){
                pojo.setSTNM(listBT.get(0).getSTNM());
                pojo.setHNNM(listBT.get(0).getSTLC());
            }
            ListData.add(pojo);
        });

        watch.stop();
        if (null != userData) {
            return commonUtills.returnJson("select", ListData.size(), ListData);
        } else {
            return commonUtills.returnJson("select", -1, ListData);
        }
    }

    //各时段最大降水量
    @RequestMapping("/MaximumPeriod")
    public Map<String, Object> MaximumPeriod(@RequestBody ColumnName param){
        StopWatch watch = new StopWatch();
        watch.start();
        List<ST_PSTAT_RPojo> listData=new ArrayList<>();
        Date date = new Date(new Date().getTime() - 24 * 60 * 60 * 1000);
        String  stime="",etime= "";
        List<String> stcdList = new ArrayList<>();
        List<String> typeList = new ArrayList<>();
        if (null != param.getStcd()&&!param.getStcd().equals("")) {
            stcdList = Arrays.asList(param.getStcd().split(","));
        }
        if(null!=param.getPathname()){
            typeList = Arrays.asList(param.getPathname().split(","));
        }
        if (null != param.getStime()) {
            stime = param.getStime();
        }
        if (null != param.getEtime()) {
            etime = param.getEtime();
        }
        List<ST_PSTAT_RPojo> ListData=new ArrayList<>();
        List<ST_PSTAT_RPojo> userData = data.selectList(stcdList, stime, etime,typeList);
        List<ST_STBPRP_BPojo> listB = stbprpBData.selectList(stcdList, null);

        List<RainfallResultPojo> List=new ArrayList<>();
        userData.forEach(u->{
            int min=0;
            if(u.getSTTDRCD().equals("7")){//最大24小时
                min=1440;
            }else if(u.getSTTDRCD().equals("8")){//最大12小时
                min=720;
            } else if(u.getSTTDRCD().equals("9")){//最大6小时
                min=360;
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime tmDateTime = LocalDateTime.parse(u.getIDTM(), formatter);
            String StartTime= tmDateTime.minusMinutes(min).format(formatter);

            RainfallResultPojo  pojo=new RainfallResultPojo();
            pojo.setSTCD(u.getSTCD());
            pojo.setStartTime(StartTime);
            pojo.setEtime(u.getIDTM());
            pojo.setMaxRainfall(u.getACCP());
            List<ST_STBPRP_BPojo> listBT=listB.stream().filter(p->p.getSTCD().equals(u.getSTCD())).collect(Collectors.toList());
            if(listBT.size()>0){
                pojo.setSTNM(listBT.get(0).getSTNM());
                pojo.setHNNM(listBT.get(0).getSTLC());
            }

//            ST_PSTAT_RPojo pojo =new ST_PSTAT_RPojo();
//            pojo.setSTCD(u.getSTCD());
//            pojo.setIDTM(u.getIDTM());
//            pojo.setSTTDRCD(u.getSTTDRCD());
//            pojo.setACCP(u.getACCP());
//            List<ST_STBPRP_BPojo> listBT=listB.stream().filter(p->p.getSTCD().equals(u.getSTCD())).collect(Collectors.toList());;
//            if(listBT.size()>0){
//                pojo.setSTNM(listBT.get(0).getSTNM());
//                pojo.setHNNM(listBT.get(0).getSTLC());
//            }
            List.add(pojo);
        });

        watch.stop();
        if (null != List) {
            return commonUtills.returnJson("select", List.size(), List);
        } else {
            return commonUtills.returnJson("select", -1, List);
        }
    }


    //整理1440min、720min、360min最大降雨（年为单位）
    @RequestMapping("/RainfallAnalyzerData")
    public Map<String, Object> RainfallAnalyzerData(@RequestBody ColumnName param){
        StopWatch watch = new StopWatch();
        watch.start();
        String  stime="",etime= "";
        List<String> stcdList = new ArrayList<>();
        int min= 1440;
        AtomicReference<String> STTDRCD= new AtomicReference<>("7");
        if (null != param.getStcd()&&!param.getStcd().equals("")) {
            stcdList = Arrays.asList(param.getStcd().split(","));
        }
        if (null != param.getStime()) {
            stime = param.getStime();
        }
        if (null != param.getEtime()) {
            etime = param.getEtime();
        }
        if (null != param.getPathname()) {
            min =Integer.parseInt( param.getPathname());
        }
        List<ST_PPTN_RPojo> ListRH=st_pptn_rData.selectListByTime(stcdList,stime,etime,"BX");
        List<RainfallResultPojo> List= RainfallAnalyzer.calculateYearlyMaxRainfall(ListRH,min);

        List<ST_PSTAT_RPojo> listData=new ArrayList<>();
        int finalMin = min;
        List.forEach(rainfallResultPojo -> {
            if(finalMin==1440){
                STTDRCD.set("7");
            }else if(finalMin==720){
                STTDRCD.set("8");
            } else if(finalMin==360){
                STTDRCD.set("9");
            }

            ST_PSTAT_RPojo pojop=new ST_PSTAT_RPojo();
            pojop.setSTCD(rainfallResultPojo.getSTCD());
            pojop.setIDTM(rainfallResultPojo.getEndTime());
            pojop.setACCP(rainfallResultPojo.getMaxRainfall());
            pojop.setSTTDRCD(STTDRCD.get());
            pojop.setWTH(String.valueOf(rainfallResultPojo.getYear()));
            listData.add(pojop);
        });
        Integer integer = 0;
        if(listData.size()>0){
            int count = 80;
            int number = listData.size() / count;
            if(listData.size() % count != 0){
                number = number + 1;
            }
            List<ST_PSTAT_RPojo> list = new ArrayList<>();
            for(int i = 0;i < number;i++){
                if(i == number - 1){
                    list = listData.subList(count * i,listData.size());
                }else {
                    list = listData.subList(count * i,count * ( i + 1));
                }
                integer += data.insertAll(list);
            }
        }
        watch.stop();
        return commonUtills.returnJson("insert",integer,listData);

//        if (null != listData) {
//            return commonUtills.returnJson("select", listData.size(), listData);
//        } else {
//            return commonUtills.returnJson("select", -1, listData);
//        }
    }


    // 逐日水量,算片区平均
    @RequestMapping("/findResultDayArea")
    public ResultUtils findResultDayArea(@RequestBody ColumnName param){
        StopWatch watch = new StopWatch();
        watch.start();
        Date date = new Date(new Date().getTime() - 24 * 60 * 60 * 1000);
        String PID="",stcd="",stime= DateUtil.dateFormat(date,"yyyy-MM-dd HH:mm:ss"),etime="";
        List<String> typeList = Arrays.asList("1".split(","));
        if(null != param.getPid()){
            PID = param.getPid();
        }
        if(null != param.getStcd()){
            stcd = param.getStcd();
        }
        if(null != param.getStime()){
            stime = param.getStime();
        }
        if(null != param.getEtime()){
            etime = param.getEtime();
        }
        if(null != param.getPathname()){
            typeList = Arrays.asList(param.getPathname().split(","));
        }
        List<ST_PPTN_RPojo> userList = new ArrayList<>();
        if(!CommonUtills.isEmpty(PID)){
            ST_STBPRP_B_TREEPojo treePojo=new ST_STBPRP_B_TREEPojo();
            treePojo.setPID(PID);
            List<ST_STBPRP_B_TREEPojo> treePojoList=treeData.selectList(treePojo);
            if(treePojoList.size()>0){
                List<String> idList=treePojoList.stream().map(x -> x.getID()).collect(Collectors.toList());
                List<ST_STBPRP_B_QUPojo> stBprpBquList = quData.queryList("", null, null, idList);
                List<String> stcdList = stBprpBquList.stream().map(item -> item.getSTCD()).collect(Collectors.toList());
                String finalStime = stime;
                String finalEtime = etime;
                List<ST_PSTAT_RPojo> stPptnRPojoList=data.selectListTotalDay(stcdList,finalStime, finalEtime,typeList);
                treePojoList.forEach(u->{
                    //查询配置的站码
                    List<ST_STBPRP_B_QUPojo> quList = stBprpBquList.stream().filter(o->o.getPID().equals(u.getID())).collect(Collectors.toList());
                    if(null != quList && quList.size() > 0){
                        List<String> stcdListZi = quList.stream().map(item -> item.getSTCD()).collect(Collectors.toList());

                        List<ST_PSTAT_RPojo> mList=stPptnRPojoList.stream().filter(o->stcdListZi.contains(o.getSTCD())).collect(Collectors.toList());
                        ST_PPTN_RPojo pojo=new ST_PPTN_RPojo();
                        pojo.setSTNM(u.getTITLE());
                        pojo.setDYP(mList.stream().mapToDouble(ST_PSTAT_RPojo::getACCP).sum());
                        pojo.setDRP(mList.stream().mapToDouble(ST_PSTAT_RPojo::getACCP).average().orElse(0.0));
                        userList.add(pojo);
                    }
                });
            }
        }
        watch.stop();
        if(userList.size() > 0){
            return new ResultUtils<>(userList, "操作成功",true ,userList.size(),watch.getTime());
        }else {
            return new ResultUtils<>(userList, "操作成功",false,userList.size(),watch.getTime());
        }
    }
}