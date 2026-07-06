package swzzmodeserver.workserver.controller.swzzmode;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.org.apache.bcel.internal.generic.NEW;

import org.apache.commons.lang.ObjectUtils.Null;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swzzmodeserver.tools.*;
import swzzmodeserver.workserver.data.swzzmode.*;
import swzzmodeserver.workserver.pojo.swzzflood.TB_TIDE_HIGHLOWPojo;
import swzzmodeserver.workserver.pojo.swzzmode.BDMS_PREDICTPojo;
import swzzmodeserver.workserver.pojo.swzzmode.DD_SOLUTIONPojo;
import swzzmodeserver.workserver.pojo.swzzmode.ES_PUMP_BPojo;
import swzzmodeserver.workserver.pojo.swzzmode.ES_PUMP_RNEWPojo;
import swzzmodeserver.workserver.pojo.swzzmode.ES_PUMP_RPojo;
import swzzmodeserver.workserver.pojo.swzzmode.ES_TIDALFORECASTGCPojo;
import swzzmodeserver.workserver.pojo.swzzmode.ES_TIDALFORECASTPojo;
import swzzmodeserver.workserver.pojo.swzzmode.ST_ASTRONOMICALTIDE_RPojo;
import swzzmodeserver.workserver.pojo.swzzrtsq.PUMPSTATIONResponse;
import swzzmodeserver.workserver.pojo.swzzrtsq.PUMPSTATIONResponse.PumpData;
import swzzmodeserver.workserver.server.swzzrtsq.fangjiangServer;
import swzzmodeserver.workserver.server.swzzrtsq.shuizhaServer;
import swzzmodeserver.workserver.service.swzzmode.HuishuiApiService;
import swzzmodeserver.workserver.data.swzzwater.WATERTB_TIDE_HIGHLOWData;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/SWZZ_MODE_ES_TIDALFORECAST")
public class ES_TIDALFORECASTController {
    @Autowired
    private ES_TIDALFORECASTData data;

    @Autowired
    private ES_TIDALFORECASTGCData es_tidalforecastgcData;
    @Autowired
    private ST_ASTRONOMICALTIDE_RData st_astronomicaltide_rData;

    @Autowired
    private WATERTB_TIDE_HIGHLOWData tb_tide_highlowData;

    @Autowired
    private DD_SOLUTIONData dd_solutionData;

    @Autowired
    private HuishuiApiService huishuiApiService;

    @Autowired
    private ES_PUMP_BData es_pump_bData;

    @Autowired
    private fangjiangServer fangjiangServer;

    @Autowired
    private ES_PUMP_RNEWData es_pump_rnewData;

    @Autowired
    private ES_PUMP_RData es_pump_rData;

    @Value("${file.path.templatefilepath}")
    private String filePathName;

    @RequestMapping("/findResult")
    public ResultUtils findResult(@RequestBody ParamField bpPojo) {
        StopWatch watch = new StopWatch();
        watch.start();
        String ID = "", key = "", pageindex = "", pagesize = "10";
        List<String> type = new ArrayList<>();
        String stime = ""// new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date().getTime() - 24
                         // * 60 * 60 * 1000)
                , etime = "", ybstm = "", ybetm = "";
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
        if (null != bpPojo.getKID1()) {
            ybstm = bpPojo.getKID1();
        }
        if (null != bpPojo.getKID2()) {
            ybetm = bpPojo.getKID2();
        }
        Integer startindex = null;
        if (!"".equals(pageindex) && !"".equals(pagesize)) {
            startindex = (Integer.parseInt(pageindex) - 1) * Integer.parseInt(pagesize);
        }
        List<ES_TIDALFORECASTPojo> fxList = data.selectList(ID, key, stime, etime, null, null, type, startindex,
                Integer.valueOf(pagesize));
        if (fxList.size() > 0) {
            return new ResultUtils<>(fxList, "操作成功", true, fxList.size(), watch.getTime());
        } else {
            return new ResultUtils<>(fxList, "操作成功", false, fxList.size(), watch.getTime());
        }
    }

    @RequestMapping("/batchResult/{pattem}")
    public ResultUtils batchResult(@PathVariable("pattem") String pattem,
            @RequestBody List<ES_TIDALFORECASTPojo> bpPojo) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writeValueAsString(bpPojo);
            new javalog().writelog("潮位推送接口被调用，参数是：" + json, filePathName);
        } catch (IOException e) {
            new javalog().writelog("潮位推送接口转json报错：" + e.getMessage(), filePathName);
        }

        StopWatch watch = new StopWatch();
        watch.start();
        String type = "";
        List<ES_TIDALFORECASTPojo> listFast = new ArrayList<>();
        if (CommonUtills.isEmpty(FieldIsValid.getListColumnName(bpPojo, ES_TIDALFORECASTPojo.class))
                || !FieldIsValid.isValid(pattem)) {
            watch.stop();
            return new ResultUtils<>(null, "存在非法字符", false, -1, watch.getTime());
        }
        if (null != pattem) {
            type = pattem;
        }
        Integer num = 0;
        addResult(bpPojo);
        watch.stop();
        if (num > 0) {
            return new ResultUtils<>(num, "操作成功", true, num, watch.getTime());
        } else {
            return new ResultUtils<>(num, "操作成功", false, num, watch.getTime());
        }
    }

    // 潮位预报之后调用模型
    @RequestMapping("/restock")
    public ResultUtils restock(@RequestBody ParamField bpPojo) {
        StopWatch watch = new StopWatch();
        watch.start();
        List<ES_TIDALFORECASTGCPojo> listGC = new ArrayList<>();

        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        // 截取到整点（小时不变，分钟和秒都设置为0）
        LocalDateTime truncatedToHour = now.truncatedTo(java.time.temporal.ChronoUnit.HOURS);
        // 格式化输出
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedTime = truncatedToHour.format(formatter);// 计算依据时间

        // 当前时间减去24小时
        // LocalDateTime timeMinus24Hours = truncatedToHour.minusHours(72);//减去24小时
        // String formattedTime72=timeMinus24Hours.format(formatter);
        // String formattedTime24=now.plusHours(24).format(formatter);;//加24小时
        // Date maxYBTM=data.selectMaxYBTM(formattedTime72,formattedTime24);
        // 将 Date 对象转换为 LocalDateTime
        // LocalDateTime localDateTime = LocalDateTime.ofInstant(maxYBTM.toInstant(),
        // ZoneId.systemDefault());
        // 将 LocalDateTime 转换为字符串
        String maxYBTMString = ""; // formatter.format(localDateTime);
        // 临时处理
        // formattedTime=maxYBTMString;
        String type = "1";
        List<String> typeList = new ArrayList<>();// Collections.singletonList(type);
        int minute = LocalTime.now().getMinute();
        int hour = truncatedToHour.getHour();

        if ((hour == 10 && minute >= 35 && minute < 59) || (hour == 15 && minute >= 35 && minute < 59)
                || bpPojo.getSttp() != null) {
            maxYBTMString = truncatedToHour.format(formatter);
            if (bpPojo.getStartdate() != null) {
                maxYBTMString = bpPojo.getStartdate();
            }
            List<DD_SOLUTIONPojo> listDD = new ArrayList<>();
            if (bpPojo.getSttp() == null) {// 当bpPojo.getSttp()不等于null时，是手动触发的，必须要算的
                listDD = dd_solutionData.selectListByDD_IDandDD_status(null, null, "15", maxYBTMString);
            }
            // List<ES_TIDALFORECASTPojo> bpPojo
            // =data.selectList(null,null,null,null,maxYBTMString,maxYBTMString,typeList,null,null);
            if (listDD.size() == 0) {
                // List<ES_TIDALFORECASTPojo>
                // bpPojoT=bpPojo.stream().filter(m->m.getSTATUS()==null).collect(Collectors.toList());
                // if(bpPojoT.size()>0)//判断这套数据有没有被计算过
                // {
                // 按照 ZHANID 分组
                // Map<String, List<ES_TIDALFORECASTPojo>> groupedByStcd = bpPojo.stream()
                // .collect(Collectors.groupingBy(ES_TIDALFORECASTPojo::getSTCD));
                // if(groupedByStcd.size()>=4){//四个站数据到齐了
                // 启动自动计算了
                int num = 24;
                List<ES_TIDALFORECASTPojo> listWSK = data.selectMaxFast("SW63401750", maxYBTMString, null);
                List<ES_TIDALFORECASTPojo> listMSD = data.selectMaxFast("SW63401100", maxYBTMString, null);
                List<ES_TIDALFORECASTPojo> listHPGY = data.selectMaxFast("SW63401500", maxYBTMString, null);
                List<ES_TIDALFORECASTPojo> listLCG = data.selectMaxFast("SW63405800", maxYBTMString, null);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date maxYBTMWSK = null;
                Date maxYBTMSD = null;
                Date maxYBTMHPGY = null;
                Date maxYBTMLCG = null;

                try {
                    maxYBTMWSK = listWSK.size() > 0 ? sdf.parse(listWSK.get(0).getTM()) : null;
                } catch (ParseException e) {
                }
                try {
                    maxYBTMSD = listMSD.size() > 0 ? sdf.parse(listMSD.get(0).getTM()) : null;
                } catch (ParseException e) {
                }
                try {
                    maxYBTMHPGY = listHPGY.size() > 0 ? sdf.parse(listHPGY.get(0).getTM()) : null;
                } catch (ParseException e) {
                }
                try {
                    maxYBTMLCG = listLCG.size() > 0 ? sdf.parse(listLCG.get(0).getTM()) : null;
                } catch (ParseException e) {
                }

                Date targetDate = new Date();
                try {
                    targetDate = sdf.parse(maxYBTMString);
                } catch (ParseException e) {
                }
                Date minYBTM = Stream.of(maxYBTMWSK, maxYBTMSD, maxYBTMHPGY, maxYBTMLCG)
                        .filter(Objects::nonNull) // 过滤掉 null
                        .min(Date::compareTo) // 找最小（最早）的 Date
                        .orElse(null);
                long diffInMillis = minYBTM.getTime() - targetDate.getTime();
                long diffInHoursTruncated = TimeUnit.MILLISECONDS.toHours(diffInMillis);
                if (diffInHoursTruncated < num) {// 预报数据不够24小时，只取到最新的整点
                    num = (int) diffInHoursTruncated;
                }
                String jydatatype = "temperatezone@shanghaiyb@" + maxYBTMString;
                String gcdatatype = "DDFN";
                String scwdatatype = "余弦曲线插值";
                int resultRows = huishuiApiService.startHuishuiJisuan(maxYBTMString, num, jydatatype, gcdatatype,
                        scwdatatype, "15");
                if (resultRows > 0) {
                    new javalog().writelog(maxYBTMString + "模型计算成功", filePathName);
                    try {
                        // List<ES_TIDALFORECASTPojo> bpPojoNew =new ArrayList<>();
                        // //更新预报数据的状态
                        // for (ES_TIDALFORECASTPojo es_tidalforecastPojo : bpPojo) {
                        // ES_TIDALFORECASTPojo es_tidalforecastPojoNew=new ES_TIDALFORECASTPojo();
                        // es_tidalforecastPojoNew.setSTCD(es_tidalforecastPojo.getSTCD());
                        // es_tidalforecastPojoNew.setSTNM(es_tidalforecastPojo.getSTNM());
                        // es_tidalforecastPojoNew.setTM(es_tidalforecastPojo.getTM());
                        // es_tidalforecastPojoNew.setYBTM(es_tidalforecastPojo.getYBTM());
                        // es_tidalforecastPojoNew.setFTM(es_tidalforecastPojo.getFTM());
                        // es_tidalforecastPojoNew.setTDZ(es_tidalforecastPojo.getTDZ());
                        // es_tidalforecastPojoNew.setZS(es_tidalforecastPojo.getZS());
                        // es_tidalforecastPojoNew.setSTATUS("1");
                        // bpPojoNew.add(es_tidalforecastPojoNew);
                        // }
                        // data.updateALLSTATUS(bpPojoNew);
                    } catch (Exception ex) {
                        System.out.println("批量修改报错");
                    }
                } else {
                    System.out.println("模型报错");
                }
                // }
                // }
            } else {
                return new ResultUtils<>(listGC, "当前时刻已经预报过了", true, listGC.size(), watch.getTime());
            }
        }

        if (listGC.size() > 0) {
            return new ResultUtils<>(listGC, "操作成功", true, listGC.size(), watch.getTime());
        } else {
            return new ResultUtils<>(listGC, "操作成功", false, listGC.size(), watch.getTime());
        }
    }

    @RequestMapping("/restockhighlow")
    public ResultUtils restockhighlow(@RequestBody ParamField bpPojo) {
        new javalog().writelog("开始执行restockhighlow接口", filePathName);
        StopWatch watch = new StopWatch();
        watch.start();

        // 1. 获取当前的完整日期时间 (包含年月日时分秒)
        LocalDateTime now = LocalDateTime.now();

        // 2. 获取今天的日期，用于构建具体的时间点
        LocalDate today = LocalDate.now();

        // 3. 构建起始和结束时间 (今天的具体时刻)
        // 原代码: LocalTime.of(10, 20) -> 现代码: today.atTime(10, 20)
        LocalDateTime start1 = today.atTime(10, 20);
        LocalDateTime end1 = today.atTime(12, 30);

        LocalDateTime start2 = today.atTime(15, 20);
        LocalDateTime end2 = today.atTime(16, 30);

        // 4. 判断逻辑保持不变 (LocalDateTime 也支持 isBefore 和 isAfter)
        boolean inFirstRange = !now.isBefore(start1) && !now.isAfter(end1);
        boolean inSecondRange = !now.isBefore(start2) && !now.isAfter(end2);
        // if (
        // inFirstRange || inSecondRange||bpPojo.getSttp()!=null
        // ) {
        // System.out.println("当前时间在 10:20 到 12:30 之间（含边界）");
        new javalog().writelog("读取网波公司预报高低潮时间在允许的时间段内", filePathName);
        // 格式化输出
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        List<String> stcdList = Arrays.asList("SW63401500,SW63401750,SW63401100,SW63405800".split(","));
        List<String> stnmList = Arrays.asList("黄浦公园,吴淞口,米市渡,芦潮港".split(","));

        List<ES_TIDALFORECASTPojo> listGCALL = new ArrayList<>();
        for (int num = 0; num < stcdList.size(); num++) {
            String stcd = stcdList.get(num);
            String stnm = stnmList.get(num);
            List<String> stcdListDan = Arrays.asList(stcd.split(","));
            List<ES_TIDALFORECASTPojo> listFast = data.selectMaxFast(stcd, null, null);// 最新的时间
            if (listFast.size() > 0) {
                String fastTM = listFast.get(0).getTM();
                if (bpPojo.getStartdate() != null) {
                    fastTM = bpPojo.getStartdate();
                }
                LocalDateTime time = LocalDateTime.parse(fastTM, formatter);
                LocalDateTime newTime = time.plusSeconds(1);
                String stime = newTime.format(formatter);
                String etime = time.plusDays(5).format(formatter);// 加上5天
                List<TB_TIDE_HIGHLOWPojo> listData = tb_tide_highlowData.selectList(stcdListDan, stime, etime);
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    String jsonString = objectMapper.writeValueAsString(listData);
                    new javalog().writelog(stnm + "站（" + stcd + "）TB_TIDE_HIGHLOW表开始时间:" + stime + " 结束时间:" + etime
                            + " 查询结果：" + jsonString, filePathName);
                } catch (JsonProcessingException ex) {
                    new javalog().writelog(stnm + "站（" + stcd + "）listData转化报错了************", filePathName);
                }

                List<TB_TIDE_HIGHLOWPojo> listFour = new ArrayList<>();
                String ybtm = stime;
                listFour = listData;
                List<ES_TIDALFORECASTPojo> listGC = new ArrayList<>();
                String finalYbtm = ybtm;
                listFour.forEach(u -> {
                    ES_TIDALFORECASTPojo pojo = new ES_TIDALFORECASTPojo();
                    pojo.setSTCD(stcd);
                    pojo.setSTNM(stnm);
                    pojo.setTM(u.getDt_timeforecast());
                    pojo.setYBTM(finalYbtm);
                    pojo.setFTM(now.format(formatter));
                    pojo.setTDZ(u.getNm_valueforecast());
                    pojo.setZS(u.getNm_valueforecast() - u.getNm_valueastronomical());
                    listGC.add(pojo);
                    listGCALL.add(pojo);
                });

                int _index = addResult(listGC);
                new javalog().writelog(stnm + "站插入ES_TIDALFORECAST表数据行数" + _index, filePathName);
            }
        }

        if (listGCALL.size() > 0) {
            return new ResultUtils<>(listGCALL, "操作成功", true, listGCALL.size(), watch.getTime());
        } else {
            return new ResultUtils<>(listGCALL, "操作成功", false, listGCALL.size(), watch.getTime());
        }
        // }else {
        // new javalog().writelog("读取网波公司预报高低潮时间还未到",filePathName);
        // return new ResultUtils<>(new ArrayList<>(), "操作成功", false,0,
        // watch.getTime());
        // }
    }

    public Integer addResult(List<ES_TIDALFORECASTPojo> bpPojo) {
        Integer num = 0;
        List<ES_TIDALFORECASTPojo> listFast = new ArrayList<>();
        List<ES_TIDALFORECASTPojo> list = new ArrayList<>();
        try {
            listFast = data.selectMaxFast(bpPojo.get(0).getSTCD(), null, null);
            if (listFast.size() > 0) {
                String fastTM = listFast.get(0).getTM();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = sdf.parse(fastTM);
                list = bpPojo.stream().filter(n -> {
                    try {
                        return sdf.parse(n.getTM()).getTime() > date.getTime();// 大于最新的时间才去进行插值
                    } catch (ParseException e) {
                        return false;
                    }
                }).collect(Collectors.toList());
                if (list.size() > 0) {
                    num = data.insertALL(list);// 去掉重复的之后再放入库

                    ES_TIDALFORECASTPojo pojoNew = listFast.get(0);
                    pojoNew.setYBTM(bpPojo.get(0).getYBTM());
                    list.add(0, pojoNew);

                }
            }
        } catch (Exception ex) {
        }
        try {
            if (list.size() > 0) {
                // 余弦曲线插值：生成5分钟间隔的插值数据
                List<ES_TIDALFORECASTGCPojo> interpolatedData = tideinterpolation.interpolateTideData(list, 5);
                if (listFast.size() > 0) {
                    if (!interpolatedData.isEmpty()) {
                        interpolatedData.remove(0);
                    }
                }
                es_tidalforecastgcData.insertALL(interpolatedData);
            }
        } catch (Exception ex) {
            System.out.println("余弦曲线插值报错：" + ex.getMessage());
        }
        try {
            if (list.size() > 0) {
                // 样条函数插值：成5分钟间隔的插值数据
                List<ES_TIDALFORECASTGCPojo> interpolatedData2 = TideSplineInterpolator.interpolateTideData(list, 5);
                if (listFast.size() > 0) {
                    if (!interpolatedData2.isEmpty()) {
                        interpolatedData2.remove(0);
                    }
                }
                es_tidalforecastgcData.insertALL(interpolatedData2);
            }
        } catch (Exception ex) {
            System.out.println("样条函数插值报错：" + ex.getMessage());
        }
        return num;
    }

    // 防汛泵站
    @RequestMapping("/getFangjiangOverflow")
    public ResultUtils getFangjiangOverflow(@RequestBody ParamField bpPojo) {
        new javalog().writelog("开始执行getFangjiangOverflow接口", filePathName, "SWZZServiceFangjiang");
        StopWatch watch = new StopWatch();
        watch.start();
        List<ES_PUMP_RPojo> listR = new ArrayList<>();
        // 1. 获取当前的完整日期时间 (包含年月日时分秒)
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedTime = now.format(formatter);// 计算依据时间
        if (bpPojo.getStartdate() != null) {
            formattedTime = bpPojo.getStartdate();
        }

        // 查询防汛泵站基础信息表
        List<ES_PUMP_BPojo> list = es_pump_bData.selectList(null, null, null);
        AtomicInteger _length = new AtomicInteger(0);
        if (list.size() > 0) {
            PUMPSTATIONResponse response = fangjiangServer.getFangjiangOverflow(list.get(0).getSTCD(), formattedTime);
            if (response != null) {
                if (response.getCode() == 200) {
                    PumpData pumpData = response.getData();
                    if (pumpData != null) {
                        String calc_time = pumpData.getCalc_time();
                        // 判断当前模型计算时间是否存在
                        List<ES_PUMP_RNEWPojo> listRnew = es_pump_rnewData.selectList(null, calc_time, calc_time, null,
                                null);
                        if (listRnew.size() > 0) {
                            new javalog().writelog("getFangjiangOverflow当前模型计算时间" + calc_time + "存在", filePathName,
                                    "SWZZServiceFangjiang");
                            return new ResultUtils<>(null, "操作成功", true, 1, watch.getTime());
                        }
                    }
                }
            }
            for (int ii = 0; ii < list.size(); ii++) {
                ES_PUMP_BPojo n = list.get(ii);
                PUMPSTATIONResponse responseNew = fangjiangServer.getFangjiangOverflow(n.getSTCD(), formattedTime);
                if (responseNew != null) {
                    if (responseNew.getCode() == 200) {
                        PumpData pumpData = responseNew.getData();
                        if (pumpData != null) {
                            try {
                                String calc_time = pumpData.getCalc_time();
                                String data_time_str = pumpData.getData_time_str();
                                List<Double> flow_array = pumpData.getFlow_array();// 放江量（单位方）
                                List<Integer> pump_array = pumpData.getPump_array();

                                for (int i = 0; i < flow_array.size(); i++) {
                                    ES_PUMP_RPojo pojo = new ES_PUMP_RPojo();
                                    LocalDateTime dateTime = LocalDateTime.parse(data_time_str, formatter);
                                    LocalDateTime newDateTime = dateTime.plusMinutes(i * 5);
                                    String TM = newDateTime.format(formatter);
                                    // 将 double 转为 BigDecimal，设置保留2位小数，并使用 HALF_UP（常规的四舍五入）模式
                                    Double flow = BigDecimal.valueOf(flow_array.get(i) / (5 * 60))
                                            .setScale(2, RoundingMode.HALF_UP)
                                            .doubleValue();
                                    pojo.setSTCD(n.getZHANID());
                                    pojo.setTM(TM);
                                    pojo.setOMCN(pump_array.get(i));
                                    pojo.setPMPQ(flow);
                                    pojo.setRLSTM(calc_time);
                                    listR.add(pojo);
                                }

                            } catch (Exception e) {
                                new javalog().writelog(
                                        "getFangjiangOverflow当前模型计算时间" + n.getSTCD() + "报错:" + e.getMessage(),
                                        filePathName,
                                        "SWZZServiceFangjiang");
                            }
                        }
                    }
                }
            }

            if (listR.size() > 0) {
                Integer num = 0;
                int count = 800;
                int number = listR.size() / count;
                if (listR.size() % count != 0) {
                    number += 1;
                }
                List<ES_PUMP_RPojo> listNew = new ArrayList<>();
                for (int i = 0; i < number; i++) {
                    if (i == number - 1) {
                        listNew = listR.subList(count * i, listR.size());
                    } else {
                        listNew = listR.subList(count * i, count * (i + 1));
                    }

                    _length.addAndGet(es_pump_rData.insertALL(listNew));
                }

                ES_PUMP_RNEWPojo pojo = new ES_PUMP_RNEWPojo();
                pojo.setID(UUID.randomUUID().toString());
                pojo.setCALC_TIME(listR.get(0).getRLSTM());
                pojo.setDATA_TIME(listR.get(0).getTM());
                es_pump_rnewData.insertOne(pojo);

                try {
                    List<ES_PUMP_RPojo> listRQ = listR.stream().filter(u -> u.getPMPQ() > 0)
                            .collect(Collectors.toList());
                    if (listRQ.size() > 0) {// 有放江量，需要驱动模型计算
                        now = LocalDateTime.now();
                        // 截取到整点（小时不变，分钟和秒都设置为0）
                        LocalDateTime truncatedToHour = now.truncatedTo(java.time.temporal.ChronoUnit.HOURS);
                        // 格式化输出
                        String maxYBTMString = truncatedToHour.format(formatter);// 计算依据时间
                        String jydatatype = "temperatezone@shanghaiyb";
                        String gcdatatype = "fangjiangliang";
                        String scwdatatype = "temperatezone";
                        int resultRows = huishuiApiService.startHuishuiJisuan(maxYBTMString, 24, jydatatype, gcdatatype,
                                scwdatatype, "");
                        if (resultRows > 0) {
                            new javalog().writelog(maxYBTMString + "模型计算成功", filePathName);
                        }
                    }
                } catch (Exception e) {
                    new javalog().writelog("放江量入库之后，模型计算失败：" + e.getMessage(), filePathName, "SWZZServiceFangjiang");
                }

            }
        }
        watch.getTime();
        return new ResultUtils<>(listR, "操作成功", true, _length.get(), watch.getTime());
    }
}
