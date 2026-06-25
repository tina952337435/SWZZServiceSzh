package swzzmodeserver.workserver.controller.swzzrtsq;

import swzzmodeserver.workserver.data.swzzqxsj.Tz_watersheddataData;
import swzzmodeserver.workserver.data.swzzrtsq.*;
import swzzmodeserver.workserver.pojo.swzzqxsj.Tz_watersheddataPojo;
import swzzmodeserver.workserver.pojo.swzzrtsq.*;
import swzzmodeserver.workserver.server.swzzrtsq.GetWaterViewNewServer;
import swzzmodeserver.workserver.server.swzzrtsq.ST_PPTN_RServer;
import swzzmodeserver.tools.*;
import swzzmodeserver.tools.RainfallAnalyzer.EventRainResult;
import swzzmodeserver.workserver.data.swzzflood.ST_RAINSTORMFREQUENCY_RData;
import swzzmodeserver.workserver.pojo.swzzflood.ST_RAINSTORMFREQUENCY_RPojo;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

// 【修改点】在 @RestController 中指定唯一名称
@RestController("RtsqST_PPTN_RController")
@RequestMapping("/SWZZ_RTSQ_ST_PPTN_R")
public class ST_PPTN_RController {
    @Autowired
    private RTSQST_PPTN_RData data;
    @Autowired
    private ST_STBPRP_B_QUData quData;
    @Autowired
    private ST_STBPRP_B_TREEData treeData;
    @Autowired
    private RTSQST_STBPRP_BData stbprpBData;
    @Autowired
    private ST_PPTN_RServer pptnServer;

    @Autowired
    private ST_STBPRP_B_STTPData sttpData;

    @Autowired
    private GetWaterViewNewServer viewNewServer;

    @Autowired
    private Tz_watersheddataData tzWatersheddataData;

    @Autowired
    private GetWaterViewNewServer server;

    @Autowired
    private ST_RAINSTORMFREQUENCY_RData stormData;

    // 文件存储路径
    @Value("${file.path.templatefilepath}")
    private String templatefilepath;

    @RequestMapping("/selectListIsDay")
    public ResultUtils selectListIsDay(@RequestBody ColumnName param) {
        StopWatch watch = new StopWatch();
        watch.start();
        Date date = new Date(new Date().getTime() - 24 * 60 * 60 * 1000);
        String stcd = "", stime = DateUtil.dateFormat(date, "yyyy-MM-dd HH:mm:ss"), etime = "", PID = "", ADMAUTH = "",
                STNM = "";
        if (null != param.getPid()) {
            PID = param.getPid();
        }
        if (null != param.getStcd()) {
            stcd = param.getStcd();
        }
        if (null != param.getStime()) {
            stime = param.getStime();
        }
        if (null != param.getEtime()) {
            etime = param.getEtime();
        }
        if (null != param.getKey()) {
            STNM = param.getKey();
        }
        if (null != param.getDatasource()) {
            ADMAUTH = param.getDatasource();
        }
        // 查询配置的站码
        List<ST_STBPRP_B_QUPojo> quList = quData.selectList("", STNM, null, PID, ADMAUTH);
        List<String> stcdList = new ArrayList<>();
        if (null != quList && quList.size() > 0) {
            for (ST_STBPRP_B_QUPojo quPojo : quList) {
                if (null != quPojo.getSTCD()) {
                    stcdList.add(quPojo.getSTCD());
                }
            }
        }
        // 查询累计降雨
        List<ST_PPTN_RPojo> sprlist = data.selectListIsDay(stcdList, stime, etime);
        // 查询基础站码信息
        List<ST_STBPRP_BPojo> stStbprpBPList = stbprpBData.selectList(stcdList, null);
        List<ST_PPTN_RPojo> mList = new ArrayList<>();
        if (stcdList.size() > 0) {
            for (String item : stcdList) {
                List<ST_STBPRP_BPojo> collect = stStbprpBPList.stream().filter(i -> i.getSTCD().equals(item))
                        .collect(toList());
                if (collect.size() > 0) {
                    ST_PPTN_RPojo model = new ST_PPTN_RPojo();

                    ST_STBPRP_BPojo stbprpBPojo = collect.get(0);
                    model.setSTCD(item);
                    model.setSTNM(stbprpBPojo.getSTNM());
                    if (null != stbprpBPojo.getADMAUTH()) {
                        model.setADMAUTH(stbprpBPojo.getADMAUTH());
                    }
                    if (null != stbprpBPojo.getADDVNM()) {
                        model.setADDVNM(stbprpBPojo.getADDVNM());
                    }
                    if (null != stbprpBPojo.getRVNM()) {
                        model.setRVNM(stbprpBPojo.getRVNM());
                    }
                    if (null != stbprpBPojo.getBSNM()) {
                        model.setBSNM(stbprpBPojo.getBSNM());
                    }
                    if (null != stbprpBPojo.getLGTD()) {
                        model.setLGTD(stbprpBPojo.getLGTD());
                    }
                    if (null != stbprpBPojo.getLTTD()) {
                        model.setLTTD(stbprpBPojo.getLTTD());
                    }
                    model.setMTYPE(stbprpBPojo.getMTYPE());
                    List<ST_PPTN_RPojo> drpList = sprlist.stream().filter(i -> i.getSTCD().equals(item))
                            .collect(toList());
                    if (drpList.size() > 0) {
                        model.setDRP(Double.parseDouble(String.format("%.1f", drpList.get(0).getDRP())));
                    }
                    mList.add(model);
                }
            }
        }
        if (null != mList && mList.size() > 0) {
            mList.sort((a, b) -> {
                if (a.getDRP() != null && b.getDRP() != null) {
                    return (int) (b.getDRP() * 1000 - a.getDRP() * 1000);
                }
                return -1;
            });
        }
        watch.stop();
        if (mList.size() > 0) {
            return new ResultUtils<>(mList, "操作成功", true, mList.size(), watch.getTime());
        } else {
            return new ResultUtils<>(mList, "操作成功", false, mList.size(), watch.getTime());
        }
    }

    @RequestMapping("/selectListByDay")
    public ResultUtils selectList(@RequestBody ColumnName param) {
        StopWatch watch = new StopWatch();
        watch.start();

        Date date = new Date(new Date().getTime() - 24 * 60 * 60 * 1000);
        String stime = DateUtil.dateFormat(date, "yyyy-MM-dd 08:00:00"), etime = "", PID = "";

        if (null != param.getPid()) {
            PID = param.getPid();
        }
        if (null != param.getStime()) {
            stime = param.getStime();
        }
        if (null != param.getEtime()) {
            etime = param.getEtime();
        }
        String mtype = "";
        if (!CommonUtills.isEmpty(param.getDatasource())) {
            mtype = param.getDatasource();
        }
        List<ST_STBPRP_B_QUPojo> quList = quData.selectList("", "", null, PID, null);

        // 查询配置的站码
        List<String> stcdList = new ArrayList<>();
        if (null != quList && quList.size() > 0) {
            for (ST_STBPRP_B_QUPojo quPojo : quList) {
                if (null != quPojo.getSTCD()) {
                    stcdList.add(quPojo.getSTCD());
                }
            }
        }

        List<ST_PPTN_RPojo> pptnList = pptnServer.selectListByDay(stcdList, stime, etime, mtype);

        List<ST_STBPRP_B_QUDto> quDtoList = new ArrayList<>();
        if (null != quList) {
            for (ST_STBPRP_B_QUPojo quPojo : quList) {
                ST_STBPRP_B_QUDto quDto = new ST_STBPRP_B_QUDto();
                BeanUtils.copyProperties(quPojo, quDto);
                List<ST_PPTN_RPojo> collect = pptnList.stream().filter(i -> i.getSTCD().equals(quDto.getSTCD()))
                        .collect(toList());
                quDto.setPptnList(collect);
                quDtoList.add(quDto);
            }
        }

        watch.stop();
        if (quDtoList.size() > 0) {
            return new ResultUtils<>(quDtoList, "操作成功", true, quDtoList.size(), watch.getTime());
        } else {
            return new ResultUtils<>(quDtoList, "操作成功", false, quDtoList.size(), watch.getTime());
        }
    }

    @RequestMapping("/selectListByHouseMax")
    public ResultUtils selectListByHouseMax(@RequestBody ColumnName param) {
        StopWatch watch = new StopWatch();
        watch.start();
        Date date = new Date(new Date().getTime() - 24 * 60 * 60 * 1000);
        String stcd = "", stime = DateUtil.dateFormat(date, "yyyy-MM-dd HH:mm:ss"), etime = "", PID = "";
        if (null != param.getPid()) {
            PID = param.getPid();
        }
        if (null != param.getStcd()) {
            stcd = param.getStcd();
        }
        if (null != param.getStime()) {
            stime = param.getStime();
        }
        if (null != param.getEtime()) {
            etime = param.getEtime();
        }
        String mtype = "";
        if (!CommonUtills.isEmpty(param.getDatasource())) {
            mtype = param.getDatasource();
        }
        List<ST_STBPRP_B_QUPojo> quList = quData.selectList("", "", null, PID, null);
        List<String> stcdList = new ArrayList<>();
        List<ST_STBPRP_B_QUDto> quDtoList = new ArrayList<>();
        if (null != quList && quList.size() > 0) {
            for (ST_STBPRP_B_QUPojo quPojo : quList) {
                if (null != quPojo.getSTCD()) {
                    stcdList.add(quPojo.getSTCD());
                }
            }
            List<ST_PPTN_RPojo> sprlist = data.selectListByHouse(stcdList, stime, etime, mtype);
            for (ST_STBPRP_B_QUPojo quPojo : quList) {
                ST_STBPRP_B_QUDto quDto = new ST_STBPRP_B_QUDto();
                BeanUtils.copyProperties(quPojo, quDto);
                List<ST_PPTN_RPojo> collect = sprlist.stream().filter(i -> i.getSTCD().equals(quDto.getSTCD()))
                        .collect(toList());
                if (collect.size() > 0) {
                    ST_PPTN_RPojo pptnRPojo = collect.get(0);
                    if (null != pptnRPojo.getDRP()) {
                        quDto.setDRP(pptnRPojo.getDRP());
                    }
                }
                quDtoList.add(quDto);
            }
            quDtoList.sort((a, b) -> {
                if (a.getDRP() != null && b.getDRP() != null) {
                    return (int) (b.getDRP() * 1000 - a.getDRP() * 1000);
                }
                return -1;
            });
        }

        watch.stop();
        if (quDtoList.size() > 0) {
            return new ResultUtils<>(quDtoList, "操作成功", true, quDtoList.size(), watch.getTime());
        } else {
            return new ResultUtils<>(quDtoList, "操作成功", false, quDtoList.size(), watch.getTime());
        }
    }

    @RequestMapping("/selectListByXZ")
    public ResultUtils selectListByXZ(@RequestBody ColumnName param) {
        StopWatch watch = new StopWatch();
        watch.start();
        Date date = new Date(new Date().getTime() - 24 * 60 * 60 * 1000);
        String stime = DateUtil.dateFormat(date, "yyyy-MM-dd HH:mm:ss"), etime = "", PID = "";

        List<ST_PPTN_RPojo> xzList = new ArrayList<>();

        if (null != param.getPid()) {
            PID = param.getPid();
            if (null != param.getStime()) {
                stime = param.getStime();
            }
            if (null != param.getEtime()) {
                etime = param.getEtime();
            }

            ST_STBPRP_B_TREEPojo model = new ST_STBPRP_B_TREEPojo();
            model.setPID(param.getPid());

            List<ST_STBPRP_B_TREEPojo> quList = treeData.selectList(model);
            List<ST_STBPRP_B_QUPojo> quPojosAll = quData.queryList(null, null, null, null);
            //// List<ST_PPTN_RPojo> pptnPojos = data.selectListIsDay(stcds, stime, etime);
            List<ST_PPTN_RPojo> pptnPojosAll = data.selectListIsDay(new ArrayList<>(), stime, etime);
            // Map<String, Double> groupSum = pptnPojosAll.stream()
            // .collect(Collectors.groupingBy(
            // ST_PPTN_RPojo::getSTCD,
            // Collectors.summingDouble(ST_PPTN_RPojo::getDRP)
            // ));
            // List<ST_PPTN_RPojo> pptnPojosAllList = new ArrayList<>();
            // groupSum.forEach((category, sum) ->
            // {
            // ST_PPTN_RPojo onePojo=new ST_PPTN_RPojo();
            // onePojo.setSTCD(category);
            // onePojo.setDRP(sum);
            // pptnPojosAllList.add(onePojo);
            // }
            // );

            for (ST_STBPRP_B_TREEPojo xzpojo : quList) {
                List<ST_STBPRP_B_QUPojo> quPojos = quPojosAll.stream().filter(u -> u.getPID().equals(xzpojo.getID()))
                        .collect(toList());
                List<String> stcds = new ArrayList<>();
                if (quPojos.size() > 0) {
                    for (ST_STBPRP_B_QUPojo quPojo : quPojos) {
                        stcds.add(quPojo.getSTCD());
                    }
                }
                // List<ST_PPTN_RPojo> pptnPojos = data.selectListIsDay(stcds, stime, etime);
                List<ST_PPTN_RPojo> pptnPojos = pptnPojosAll.stream().filter(u -> stcds.contains(u.getSTCD()))
                        .collect(toList());// data.selectListIsDay(stcds, stime, etime);
                Double DRPSUM = 0.00, DRPAVG = 0.00;
                if (quPojos.size() > 0 && null != pptnPojos && pptnPojos.size() > 0) {
                    if (quPojos.size() > 1) {
                        for (ST_PPTN_RPojo pptnRPojo : pptnPojos) {
                            if (null != pptnRPojo.getDRP()) {
                                DRPSUM += pptnRPojo.getDRP();
                            }
                        }
                        DRPAVG = DRPSUM / quPojos.size();
                    } else {
                        DRPAVG = pptnPojos.get(0).getDRP();
                    }
                }
                ST_PPTN_RPojo xzobj = new ST_PPTN_RPojo();
                xzobj.setSTNM(xzpojo.getTITLE());
                xzobj.setDRP(DRPAVG);
                xzList.add(xzobj);
            }
        }
        watch.stop();
        if (xzList.size() > 0) {
            return new ResultUtils<>(xzList, "操作成功", true, xzList.size(), watch.getTime());
        } else {
            return new ResultUtils<>(xzList, "操作成功", false, xzList.size(), watch.getTime());
        }
    }

    @RequestMapping("/selectListByTime")
    public ResultUtils selectListByTime(@RequestBody ColumnName param) {
        StopWatch watch = new StopWatch();
        watch.start();
        Date date = new Date(new Date().getTime() - 24 * 60 * 60 * 1000);
        String stime = DateUtil.dateFormat(date, "yyyy-MM-dd HH:mm:ss"), etime = "", PID = "";
        if (null != param.getPid()) {
            PID = param.getPid();
        }
        if (null != param.getStime()) {
            stime = param.getStime();
        }
        if (null != param.getEtime()) {
            etime = param.getEtime();
        }
        String mtype = "";
        if (!CommonUtills.isEmpty(param.getDatasource())) {
            mtype = param.getDatasource();
        }
        List<ST_STBPRP_B_QUDto> quList = quData.selectQUAndRPBList(PID);
        if (null != quList && quList.size() > 0) {
            List<String> stcdList = new ArrayList<>();
            for (ST_STBPRP_B_QUDto quPojo : quList) {
                if (null != quPojo.getSTCD()) {
                    stcdList.add(quPojo.getSTCD());
                }
            }
            List<ST_PPTN_RPojo> sprlist = data.selectListByTime(stcdList, stime, etime, mtype);
            List<ST_STBPRP_BPojo> bPojos = stbprpBData.selectList(stcdList, null);
            for (ST_STBPRP_B_QUDto quPojo : quList) {
                List<ST_PPTN_RPojo> collect = sprlist.stream().filter(i -> i.getSTCD().equals(quPojo.getSTCD()))
                        .collect(toList());
                List<ST_STBPRP_BPojo> collectList = bPojos.stream().filter(i -> {
                    return i.getSTCD().equals(quPojo.getSTCD());
                }).collect(toList());
                if (collectList.size() > 0) {
                    ST_STBPRP_BPojo stb = collectList.get(0);
                    if (stb.getSTNM() != null) {
                        quPojo.setSTNM(stb.getSTNM());
                    }
                    if (stb.getADMAUTH() != null) {
                        quPojo.setADMAUTH(stb.getADMAUTH());
                    }
                }
                quPojo.setPptnList(collect);
            }
        }
        watch.stop();
        if (quList.size() > 0) {
            return new ResultUtils<>(quList, "操作成功", true, quList.size(), watch.getTime());
        } else {
            return new ResultUtils<>(quList, "操作成功", false, quList.size(), watch.getTime());
        }
    }

    @RequestMapping("/batchResultI")
    public ResultUtils insertAll(@RequestBody List<ST_PPTN_RPojo> quPojo) {
        StopWatch watch = new StopWatch();
        watch.start();
        Integer num = data.insertAll(quPojo);
        watch.stop();
        if (num > 0) {
            return new ResultUtils<>(num, "操作成功", true, num, watch.getTime());
        } else {
            return new ResultUtils<>(num, "操作成功", false, num, watch.getTime());
        }
    }

    @RequestMapping("/selectListBySen")
    public ResultUtils selectListBySen(@RequestBody ColumnName param) {
        StopWatch watch = new StopWatch();
        watch.start();
        Date date = new Date(new Date().getTime() - 24 * 60 * 60 * 1000);
        String etime = DateUtil.dateFormat(date, "yyyy-MM-dd 08:00:00"), PID = "", stcd = "";
        if (null != param.getPid()) {
            PID = param.getPid();
        }
        if (null != param.getEtime()) {
            etime = param.getEtime();
        }
        if (null != param.getStcd()) {
            stcd = param.getStcd();
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // Date parse = dateFormat.parse(etime);
        // if(parse.getTime() > new Date().getTime()){
        // etime = DateUtil.dateFormat(date,"yyyy-MM-dd 08:00:00");
        // }else{
        etime = DateUtil.dateFormat(DateUtil.strToDate(etime, DateUtil.YMDHMS), "yyyy-MM-dd 08:00:00");
        // }
        List<ST_STBPRP_B_QUPojo> quList = quData.selectList("", "", null, PID, null);
        List<String> stcdList = new ArrayList<>();
        List<ST_STBPRP_BDto> rpbList = new ArrayList<>();
        if (null != quList && quList.size() > 0) {
            for (ST_STBPRP_B_QUPojo quPojo : quList) {
                if (null != quPojo.getSTCD()) {
                    stcdList.add(quPojo.getSTCD());
                }
            }
            List<ST_PPTN_RDto> sprlist = data.selectListBySen(stcdList, etime, stcd);
            List<ST_STBPRP_BPojo> stbbList = stbprpBData.selectList(stcdList, "");
            for (ST_STBPRP_BPojo rpBPojo : stbbList) {
                ST_STBPRP_BDto rpBDto = new ST_STBPRP_BDto();
                BeanUtils.copyProperties(rpBPojo, rpBDto);
                List<ST_PPTN_RDto> collect = sprlist.stream().filter(i -> i.getSTCD().equals(rpBDto.getSTCD()))
                        .collect(toList());
                if (collect.size() > 0) {
                    ST_PPTN_RDto pptnRDto = collect.get(0);
                    if (null != pptnRDto.getYear()) {
                        rpBDto.setYear(pptnRDto.getYear());
                    }
                    if (null != pptnRDto.getTm()) {
                        rpBDto.setTm(pptnRDto.getTm());
                    }
                    if (null != pptnRDto.getZ()) {
                        rpBDto.setZ(pptnRDto.getZ());
                    }
                    if (null != pptnRDto.getOrderIndex()) {
                        rpBDto.setOrderIndex(pptnRDto.getOrderIndex());
                    }
                }
                rpbList.add(rpBDto);
            }
        }
        watch.stop();
        if (rpbList.size() > 0) {
            return new ResultUtils<>(rpbList, "操作成功", true, rpbList.size(), watch.getTime());
        } else {
            return new ResultUtils<>(rpbList, "操作成功", false, rpbList.size(), watch.getTime());
        }
    }

    @RequestMapping("/selectListDanZhan")
    public ResultUtils selectListDanZhan(@RequestBody ColumnName param) {
        StopWatch watch = new StopWatch();
        watch.start();
        Date date = new Date(new Date().getTime() - 24 * 60 * 60 * 1000);
        String stime = DateUtil.dateFormat(date, "yyyy-MM-dd 08:00:00"), etime = "", type = "Minute";
        List<String> stcdList = new ArrayList<>();
        if (null != param.getStcd()) {
            stcdList = Arrays.asList(param.getStcd().split(","));
        }
        if (null != param.getPathname()) {
            type = param.getPathname();
        }
        if (null != param.getStime()) {
            stime = param.getStime();
        }
        if (null != param.getEtime()) {
            etime = param.getEtime();
        }
        String mtype = "";
        if (!CommonUtills.isEmpty(param.getDatasource())) {
            mtype = param.getDatasource();
        }
        List<ST_PPTN_RPojo> pptnList = new ArrayList<>();
        if ("Minute".equals(type)) {
            pptnList = data.selectListByTime(stcdList, stime, etime, mtype);
        } else if ("HOUR".equals(type)) {
            // pptnList = data.selectListDanZhanByHouse(stcdList,stime,etime);
            pptnList = pptnServer.selectListByHour(stcdList, stime, etime, mtype);
        } else if ("DAY".equals(type)) {
            pptnList = pptnServer.selectListByDay(stcdList, stime, etime, mtype);
        }
        List<ST_STBPRP_BPojo> bPojos = stbprpBData.selectList(stcdList, null);
        for (ST_PPTN_RPojo pptnRPojo : pptnList) {
            List<ST_STBPRP_BPojo> collect = bPojos.stream().filter(i -> {
                if (null != i.getSTCD() && null != pptnRPojo.getSTCD()) {
                    return i.getSTCD().equals(pptnRPojo.getSTCD());
                }
                return false;
            }).collect(toList());
            if (collect.size() > 0) {
                ST_STBPRP_BPojo stb = collect.get(0);
                if (stb.getSTNM() != null) {
                    pptnRPojo.setSTNM(stb.getSTNM());
                }
                if (stb.getADMAUTH() != null) {
                    pptnRPojo.setADMAUTH(stb.getADMAUTH());
                }
            }
            if ("HOUR".equals(type)) {
                if ("" != pptnRPojo.getTM() && pptnRPojo.getTM().length() < 16) {
                    pptnRPojo.setTM(pptnRPojo.getTM() + ":00:00");
                }
            }
        }
        watch.stop();
        if (pptnList.size() > 0) {
            return new ResultUtils<>(pptnList, "操作成功", true, pptnList.size(), watch.getTime());
        } else {
            return new ResultUtils<>(pptnList, "操作成功", false, pptnList.size(), watch.getTime());
        }
    }

    // @RequestMapping(value = "/dengzhixian",method = RequestMethod.POST)
    // @ResponseBody
    // public List<Map<String,Object>> dengzhixian(@RequestBody DENGZHIXIANYQPojo
    // param){
    // StopWatch watch = new StopWatch();
    // // 计时器开始
    // watch.start();
    // List<String> listSTCD= new ArrayList<>();
    // String filePath=templatefilepath;
    // filePath = FilePathUtils.getRealFilePath(templatefilepath+"Rainfall\\");
    // File savefile = new File(filePath);
    // if (!savefile.exists()) {
    // savefile.mkdirs();
    // }
    //
    // String filename = "dzx"+UUID.randomUUID().toString()+".png";
    // MyDZXUtils dzxUtils=new MyDZXUtils();
    // dzxUtils.bijiao_x=0.1;
    // String filenameNew =
    // dzxUtils.init(param.getLEVELDATA(),"",FilePathUtils.getRealFilePath(filePath+"/"+filename),param.getLEVELRANGE());
    //
    // List<Map<String,Object>> list = new ArrayList<>();
    // Map<String,Object> map = new HashMap<>();
    // if(!ObjUtils.isEmpty(filenameNew)){
    // map.put("filename",filename);
    // }
    // list.add(map);
    // //计时器结束
    // watch.stop();
    //
    // return list;
    // }

    //
    // @RequestMapping(value = "/dengzhixianNew",method = RequestMethod.POST)
    // @ResponseBody
    // public ResultUtils<List> dengzhixianNew(@RequestBody DENGZHIXIANYQPojo
    // param){
    // StopWatch watch = new StopWatch();
    // // 计时器开始
    // watch.start();
    // List<String> listSTCD= new ArrayList<>();
    // String filePath=templatefilepath;
    // filePath = FilePathUtils.getRealFilePath(templatefilepath+"Rainfall\\");
    // File savefile = new File(filePath);
    // if (!savefile.exists()) {
    // savefile.mkdirs();
    // }
    //
    // String filename = "dzx"+UUID.randomUUID().toString()+".png";
    // MyDZXUtils dzxUtils=new MyDZXUtils();
    // dzxUtils.bijiao_x=0.1;
    // String filenameNew =
    // dzxUtils.init(param.getLEVELDATA(),"",FilePathUtils.getRealFilePath(filePath+"/"+filename),param.getLEVELRANGE());
    //
    // List<Map<String,Object>> list = new ArrayList<>();
    // Map<String,Object> map = new HashMap<>();
    // if(!ObjUtils.isEmpty(filenameNew)){
    // map.put("filename",filename);
    // }
    // list.add(map);
    // //计时器结束
    // watch.stop();
    //
    // return new ResultUtils<>(list, "操作成功", true, 1, 1, 0, 0, watch.getTime(),
    // null);
    // }

    /**
     * 查询日最大降雨站点，过程数据
     */
    @RequestMapping("/selectListDayMaxRain")
    public ResultUtils selectListDayMaxRain(@RequestBody ColumnName param) {
        StopWatch watch = new StopWatch();
        watch.start();
        List<Map<String, Object>> mListRain = new ArrayList<>();
        Date date = new Date(new Date().getTime() - 24 * 60 * 60 * 1000);
        String stime = DateUtil.dateFormat(date, "yyyy-MM-dd 08:00:00"), etime = "", type = "DAY";
        String PID = "";
        List<String> stcdList = new ArrayList<>();
        if (null != param.getPid()) {
            PID = param.getPid();
            // 查询配置的站码
            List<ST_STBPRP_B_QUPojo> quList = quData.selectList("", "", null, PID, "");
            if (null != quList && quList.size() > 0) {
                for (ST_STBPRP_B_QUPojo quPojo : quList) {
                    if (null != quPojo.getSTCD()) {
                        stcdList.add(quPojo.getSTCD());
                    }
                }
            }
        }
        if (null != param.getStcd()) {
            stcdList = Arrays.asList(param.getStcd().split(","));
        }
        if (null != param.getPathname()) {
            type = param.getPathname();
        }
        if (null != param.getStime()) {
            stime = param.getStime();
        }
        if (null != param.getEtime()) {
            etime = param.getEtime();
        }
        String mtype = "";
        if (!CommonUtills.isEmpty(param.getDatasource())) {
            mtype = param.getDatasource();
        }
        List<ST_PPTN_RPojo> pptnList = new ArrayList<>();
        if ("Minute".equals(type)) {
            pptnList = data.selectListByTime(stcdList, stime, etime, mtype);
        } else if ("HOUR".equals(type)) {
            // pptnList = data.selectListDanZhanByHouse(stcdList,stime,etime);
            pptnList = pptnServer.selectListByHour(stcdList, stime, etime, mtype);
        } else if ("DAY".equals(type)) {
            pptnList = pptnServer.selectListByDay(stcdList, stime, etime, mtype);
        }
        List<ST_STBPRP_BPojo> bPojos = stbprpBData.selectList(stcdList, null);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long timedif = 2, sTime = 0, eTime = 0;
        if (!(etime.equals("") && stime.equals(""))) {
            try {
                sTime = dateFormat.parse(stime).getTime();
                eTime = dateFormat.parse(etime).getTime();
                timedif = (eTime - sTime) / (24 * 60 * 60 * 1000);
                if (Integer.valueOf(new SimpleDateFormat("HH").format(eTime)) > 8) {
                    timedif = timedif + 1;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        System.out.println("timedif = " + timedif);
        for (int i = 0; i < timedif; i++) {
            Double DRPSum = 0.00;
            int finalI = i;
            long finalSTime = sTime;
            List<ST_PPTN_RPojo> collect = pptnList.stream().filter(n -> null != n.getDRP() && n.getDRP() > 0)
                    .collect(toList());
            List<ST_PPTN_RPojo> collectList = collect.stream().filter(j -> {
                if (null != j.getTM()) {
                    Date stm = DateUtil.addTimeToDate(new Date(finalSTime), "d", finalI);
                    Date etm = DateUtil.addTimeToDate(DateUtil.addTimeToDate(new Date(finalSTime), "d", finalI + 1),
                            "n", 5);
                    try {
                        return dateFormat.parse(j.getTM()).after(stm) && dateFormat.parse(j.getTM()).before(etm);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }).collect(toList());

            // 日累计降雨
            DRPSum = collectList.stream().mapToDouble(ST_PPTN_RPojo::getDRP).sum();

            // 计算日最大降雨站点
            List<ST_PPTN_RPojo> mRainListMax = collectList.stream().filter(u -> null != u.getDRP() && u.getDRP() > 0.1)
                    .sorted(Comparator.comparing(ST_PPTN_RPojo::getDRP).reversed()).collect(toList());
            Date newDate = DateUtil.addTimeToDate(new Date(sTime), "d", finalI);
            if (null != mRainListMax && mRainListMax.size() > 0 && mRainListMax.get(0).getDRP() > 0.2) {
                Map<String, Object> map = new HashMap<>();
                map.put("stcd", mRainListMax.get(0).getSTCD()); // 最大降雨站站码

                List<ST_STBPRP_BPojo> info = bPojos.stream()
                        .filter(k -> k.getSTCD().equals(mRainListMax.get(0).getSTCD())).collect(toList());
                if (null != info && info.size() > 0) {
                    map.put("stnm", info.get(0).getSTNM()); // 最大降雨站名称
                }
                map.put("tm", dateFormat.format(newDate)); // 降雨日期
                map.put("drp", Double.parseDouble(String.format("%.1f", mRainListMax.get(0).getDRP())));// 日累计降雨
                map.put("drpday", Double.parseDouble(String.format("%.1f", (DRPSum / stcdList.size()))));// 日累计降雨
                mListRain.add(map);
            }
            System.out.println(" = " + dateFormat.format(newDate));
        }

        watch.stop();
        if (mListRain.size() > 0) {
            return new ResultUtils<>(mListRain, "操作成功", true, mListRain.size(), watch.getTime());
        } else {
            return new ResultUtils<>(mListRain, "操作成功", false, mListRain.size(), watch.getTime());
        }
    }

    /**
     * 查询时段行政分区小时最大降雨站点及日最大降雨站点
     */
    @RequestMapping("/selectListCityMaxRainHourToDay")
    public ResultUtils selectListCityMaxRainHourToDay(@RequestBody ColumnName param) {
        StopWatch watch = new StopWatch();
        watch.start();
        List<Map<String, Object>> mListRain = new ArrayList<>();
        Date date = new Date(new Date().getTime() - 24 * 60 * 60 * 1000);
        String stime = DateUtil.dateFormat(date, "yyyy-MM-dd HH:mm:ss"), etime = "", PID = "";

        List<ST_PPTN_RPojo> xzList = new ArrayList<>();

        if (null != param.getPid()) {
            PID = param.getPid();
            if (null != param.getStime()) {
                stime = param.getStime();
            }
            if (null != param.getEtime()) {
                etime = param.getEtime();
            }

            ST_STBPRP_B_TREEPojo model = new ST_STBPRP_B_TREEPojo();
            model.setPID(param.getPid());

            List<ST_STBPRP_B_TREEPojo> quList = treeData.selectList(model);

            // flag 1 过滤降雨为0、 0 不进行降雨过滤
            List<ST_PPTN_RPojo> sprlist = data.selectListSumByHour(null, stime, etime, "1");
            List<ST_PPTN_RPojo> sprlistDay = data.selectListSumByDay(null, stime, etime, "1");
            for (ST_STBPRP_B_TREEPojo xzpojo : quList) {
                Map<String, Object> map = new HashMap<>();
                map.put("cityname", xzpojo.getTITLE().trim());

                List<ST_STBPRP_B_QUPojo> quPojos = quData.selectList(null, null, null, xzpojo.getID(), null);
                List<String> stcds = new ArrayList<>();
                if (quPojos.size() > 0) {
                    for (ST_STBPRP_B_QUPojo quPojo : quPojos) {
                        stcds.add(quPojo.getSTCD());
                    }
                }
                List<ST_PPTN_RPojo> pptnList = new ArrayList<>();
                String type = "HOUR";
                pptnList = sprlist;
                // 查询多站小时 降雨过程
                // if("HOUR".equals(type)){
                // pptnList = pptnServer.selectListByHour(sprlist,stcds,stime,etime,true);
                // }

                if (null != pptnList && pptnList.size() > 0) {
                    String stnm = "";
                    Double drp = Double.valueOf("0");
                    String tm = "";
                    for (ST_STBPRP_B_QUPojo quPojo : quPojos) {
                        // 对象根据降雨属性降序排序
                        List<ST_PPTN_RPojo> newList = pptnList.stream()
                                .filter(k -> k.getSTCD().equals(quPojo.getSTCD()))
                                .sorted(Comparator.comparing(ST_PPTN_RPojo::getDRP).reversed()).collect(toList());
                        if (null != newList && newList.size() > 0) {
                            if (newList.get(0).getDRP() > drp) {
                                drp = newList.get(0).getDRP();
                                stnm = quPojo.getSTNM();
                                tm = newList.get(0).getTM();
                            }
                        }
                    }

                    map.put("hourstnm", stnm);
                    map.put("hourtm", tm + ":00:00");
                    map.put("hourdrp", Double.parseDouble(String.format("%.1f", drp)));
                }

                type = "DAY";
                // 查询多站日 降雨过程
                // if("DAY".equals(type)){
                // sprlistDay = pptnServer.selectListByDay(stcds,stime,etime);
                // }
                pptnList = sprlistDay;
                if (null != pptnList && pptnList.size() > 0) {
                    String stnm = "";
                    Double drp = Double.valueOf("0");
                    String tm = "";
                    for (ST_STBPRP_B_QUPojo quPojo : quPojos) {
                        // 对象根据降雨属性降序排序
                        List<ST_PPTN_RPojo> newList = pptnList.stream()
                                .filter(k -> k.getSTCD().equals(quPojo.getSTCD()))
                                .sorted(Comparator.comparing(ST_PPTN_RPojo::getDRP).reversed()).collect(toList());
                        if (null != newList && newList.size() > 0) {
                            if (newList.get(0).getDRP() > drp) {
                                drp = newList.get(0).getDRP();
                                stnm = quPojo.getSTNM();
                                tm = newList.get(0).getTM();
                            }
                        }
                    }

                    map.put("daystnm", stnm);
                    map.put("daytm", tm + " 08:00:00");
                    map.put("daydrp", Double.parseDouble(String.format("%.1f", drp)));
                }

                mListRain.add(map);
            }
        }
        watch.stop();
        if (mListRain.size() > 0) {
            return new ResultUtils<>(mListRain, "操作成功", true, mListRain.size(), watch.getTime());
        } else {
            return new ResultUtils<>(mListRain, "操作成功", false, mListRain.size(), watch.getTime());
        }
    }

    @RequestMapping("/queryDRPList")
    public ResultUtils queryDRPList(@RequestBody ColumnName param) {
        StopWatch watch = new StopWatch();
        watch.start();
        Date date = new Date(new Date().getTime() - 24 * 60 * 60 * 1000);
        String stcd = "", stime = DateUtil.dateFormat(date, "yyyy-MM-dd HH:mm:ss"), etime = "", PID = "", ADMAUTH = "",
                STNM = "", pathname = "";
        if (null != param.getPid()) {
            PID = param.getPid();
        }
        if (null != param.getStcd()) {
            stcd = param.getStcd();
        }
        if (null != param.getStime()) {
            stime = param.getStime();
        }
        if (null != param.getEtime()) {
            etime = param.getEtime();
        }
        if (null != param.getKey()) {
            STNM = param.getKey();
        }
        if (null != param.getDatasource()) {
            ADMAUTH = param.getDatasource();
        }
        if (!CommonUtills.isEmpty(param.getPathname())) {
            pathname = param.getPathname();
        }
        List<String> stcdList = new ArrayList<>();

        if (!CommonUtills.isEmpty(PID)) {
            // 查询配置的站码
            List<ST_STBPRP_B_QUPojo> quList = quData.selectList("", STNM, null, PID, ADMAUTH);
            if (null != quList && quList.size() > 0) {
                for (ST_STBPRP_B_QUPojo quPojo : quList) {
                    if (null != quPojo.getSTCD()) {
                        stcdList.add(quPojo.getSTCD());
                    }
                }
            }
        } else {
            stcdList = Arrays.asList(stcd.split(","));
        }

        // 查询累计降雨
        List<ST_STBPRP_BPojo> stStbprpBPojosList = stbprpBData.selectStbprpBList(stcdList, "", ADMAUTH);
        // List<ST_PPTN_RPojo> sprlist = data.queryDRPList(stime,
        // etime,ADMAUTH,stcdList);

        List<ST_PPTN_RPojo> mList = new ArrayList<>();
        if (pathname.equals("DAY")) {
            /***
             * Long dayCount=DateUtil.dateDiff(stime,etime,DateUtil.YMD,"d");
             * if(dayCount>0){
             * List<ST_PPTN_RPojo> finalMList = new ArrayList<>();
             * for(int num=0;num<=dayCount;num++){
             * Date
             * time=DateUtil.addDays(DateUtil.strToDate(DateUtil.pastTM(stime,DateUtil.YMD)+"
             * 08:00:00",DateUtil.YMDHMS),num);
             * Date finatime=DateUtil.addDays(time,1);
             * // List<ST_PPTN_RPojo>
             * tempDrpList=sprlist.stream().filter(u->DateUtil.strToDate(u.getTM(),DateUtil.YMDHMS).getTime()>time.getTime()&&DateUtil.strToDate(u.getTM(),DateUtil.YMDHMS).getTime()<=finatime.getTime()).collect(toList());
             * String _stime=DateUtil.dateToStr(time,DateUtil.YMDHMS);
             * String _etime=DateUtil.dateToStr(finatime,DateUtil.YMDHMS);
             * List<ST_PPTN_RPojo>
             * tempDrpList=sprlist.stream().filter(u->u.getTM().compareTo(_stime)>0&&u.getTM().compareTo(_etime)<=0).collect(toList());
             * 
             * if(tempDrpList.size()>0){
             * Map<String, List<ST_PPTN_RPojo>>
             * mapDRP=tempDrpList.stream().collect(Collectors.groupingBy(ST_PPTN_RPojo::getSTCD));
             * mapDRP.forEach((key, value)->{
             * ST_PPTN_RPojo pojo=new ST_PPTN_RPojo();
             * pojo.setSTCD(value.get(0).getSTCD());
             * pojo.setSTNM(value.get(0).getSTNM());
             * pojo.setBSNM(value.get(0).getBSNM());
             * pojo.setRVNM(value.get(0).getRVNM());
             * pojo.setHNNM(value.get(0).getHNNM());
             * pojo.setLGTD(value.get(0).getLGTD());
             * pojo.setLTTD(value.get(0).getLTTD());
             * pojo.setMTYPE(value.get(0).getMTYPE());
             * pojo.setSTLC(value.get(0).getSTLC());
             * pojo.setDRP(value.stream().mapToDouble(ST_PPTN_RPojo::getDRP).sum());
             * pojo.setTM(DateUtil.dateToStr(finatime,DateUtil.YMDHMS));
             * finalMList.add(pojo);
             * });
             * }
             * }
             * mList=finalMList;
             * System.out.println("结果时间："+DateUtil.dateFormat(DateUtil.getCustomerDate(),DateUtil.YMDHMS));
             * 
             * }
             **/
            mList = data.queryDAYDRPList(stime, etime, ADMAUTH, stcdList);
        } else if (pathname.equals("HOUR")) {
            /**
             * Long HourCount=DateUtil.dateDiff(stime,etime,DateUtil.YMD,"h");
             * List<ST_PPTN_RPojo> finalMList = new ArrayList<>();
             * if(HourCount>0){
             * for(int num=0;num<=HourCount;num++){
             * Date time=DateUtil.addHours(DateUtil.strToDate(stime,DateUtil.YMDHMS),num);
             * Date finatime=DateUtil.addHours(time,1);
             * // List<ST_PPTN_RPojo>
             * tempDrpList=sprlist.stream().filter(u->DateUtil.strToDate(u.getTM(),DateUtil.YMDHMS).getTime()>time.getTime()&&DateUtil.strToDate(u.getTM(),DateUtil.YMDHMS).getTime()<=finatime.getTime()).collect(toList());
             * String _stime=DateUtil.dateToStr(time,DateUtil.YMDHMS);
             * String _etime=DateUtil.dateToStr(finatime,DateUtil.YMDHMS);
             * List<ST_PPTN_RPojo>
             * tempDrpList=sprlist.stream().filter(u->u.getTM().compareTo(_stime)>0&&u.getTM().compareTo(_etime)<=0).collect(toList());
             * if(tempDrpList.size()>0){
             * Map<String, List<ST_PPTN_RPojo>>
             * mapDRP=tempDrpList.stream().collect(Collectors.groupingBy(ST_PPTN_RPojo::getSTCD));
             * mapDRP.forEach((key, value)->{
             * ST_PPTN_RPojo pojo=new ST_PPTN_RPojo();
             * pojo.setSTCD(value.get(0).getSTCD());
             * pojo.setSTNM(value.get(0).getSTNM());
             * pojo.setBSNM(value.get(0).getBSNM());
             * pojo.setRVNM(value.get(0).getRVNM());
             * pojo.setHNNM(value.get(0).getHNNM());
             * pojo.setLGTD(value.get(0).getLGTD());
             * pojo.setLTTD(value.get(0).getLTTD());
             * pojo.setMTYPE(value.get(0).getMTYPE());
             * pojo.setSTLC(value.get(0).getSTLC());
             * pojo.setDRP(value.stream().mapToDouble(ST_PPTN_RPojo::getDRP).sum());
             * pojo.setTM(DateUtil.dateToStr(finatime,DateUtil.YMDHMS));
             * finalMList.add(pojo);
             * });
             * }
             * 
             * }
             * mList=finalMList;
             * System.out.println("结果时间："+DateUtil.dateFormat(DateUtil.getCustomerDate(),DateUtil.YMDHMS));
             * 
             * }
             **/
            mList = data.queryHOURDRPList(stime, etime, ADMAUTH, stcdList);
        } else if (pathname.equals("SUM")) {
            /**
             * Map<String, List<ST_PPTN_RPojo>> mapDRP =
             * sprlist.stream().collect(Collectors.groupingBy(ST_PPTN_RPojo::getSTCD));
             * 
             * List<ST_PPTN_RPojo> finalMList = new ArrayList<>();
             * mapDRP.forEach((key, value)->{
             * ST_PPTN_RPojo pojo=new ST_PPTN_RPojo();
             * pojo.setSTCD(value.get(0).getSTCD());
             * pojo.setSTNM(value.get(0).getSTNM());
             * pojo.setBSNM(value.get(0).getBSNM());
             * pojo.setRVNM(value.get(0).getRVNM());
             * pojo.setHNNM(value.get(0).getHNNM());
             * pojo.setLGTD(value.get(0).getLGTD());
             * pojo.setLTTD(value.get(0).getLTTD());
             * pojo.setMTYPE(value.get(0).getMTYPE());
             * pojo.setSTLC(value.get(0).getSTLC());
             * pojo.setDRP(value.stream().mapToDouble(ST_PPTN_RPojo::getDRP).sum());
             * finalMList.add(pojo);
             * });
             * mList=finalMList;
             **/
            mList = data.querySUMDRPList(stime, etime, ADMAUTH, stcdList);

        } else {
            mList = data.queryDRPList(stime, etime, ADMAUTH, stcdList);
        }

        if (mList.size() > 0) {
            mList.forEach(u -> {
                List<ST_STBPRP_BPojo> stStbprpBPojos = stStbprpBPojosList.stream().filter(item -> {
                    return item.getSTCD().equals(u.getSTCD()) && item.getMTYPE().equals(u.getMTYPE());
                }).collect(toList());
                if (stStbprpBPojos.size() > 0) {
                    u.setRVNM(stStbprpBPojos.get(0).getRVNM());
                    u.setHNNM(stStbprpBPojos.get(0).getHNNM());
                    u.setBSNM(stStbprpBPojos.get(0).getBSNM());
                    u.setATCUNIT(stStbprpBPojos.get(0).getATCUNIT());
                }
            });
        }
        watch.stop();
        if (mList.size() > 0) {
            return new ResultUtils<>(mList, "操作成功", true, mList.size(), watch.getTime());
        } else {
            return new ResultUtils<>(mList, "操作成功", false, mList.size(), watch.getTime());
        }
    }

    @RequestMapping("/queryDRPDANZHANList")
    public ResultUtils queryDRPDANZHANList(@RequestBody ColumnName param) {
        StopWatch watch = new StopWatch();
        watch.start();
        Date date = new Date(new Date().getTime() - 24 * 60 * 60 * 1000);
        String stcd = "", stime = DateUtil.dateFormat(date, "yyyy-MM-dd HH:mm:ss"), etime = "", PID = "", ADMAUTH = "",
                STNM = "", pathname = "";

        if (null != param.getStime()) {
            stime = param.getStime();
        }
        if (null != param.getEtime()) {
            etime = param.getEtime();
        }
        if (null != param.getDatasource()) {
            ADMAUTH = param.getDatasource();
        }
        if (!CommonUtills.isEmpty(param.getPathname())) {
            pathname = param.getPathname();
        }
        List<String> stcdList = new ArrayList<>();
        if (null != param.getStcd()) {
            stcdList = Arrays.asList(param.getStcd().split(","));
        }
        List<ST_PPTN_RPojo> mList = new ArrayList<>();
        if (pathname.equals("DAY")) {
            mList = data.queryDAYDRPList(stime, etime, ADMAUTH, stcdList);
        } else if (pathname.equals("HOUR")) {
            mList = data.queryHOURDRPList(stime, etime, ADMAUTH, stcdList);
        } else if (pathname.equals("SUM")) {
            mList = data.querySUMDRPList(stime, etime, ADMAUTH, stcdList);
        } else {
            mList = data.queryDRPList(stime, etime, ADMAUTH, stcdList);
        }
        watch.stop();
        if (mList.size() > 0) {
            return new ResultUtils<>(mList, "操作成功", true, mList.size(), watch.getTime());
        } else {
            return new ResultUtils<>(mList, "操作成功", false, mList.size(), watch.getTime());
        }
    }

    @RequestMapping("/queryTREEDRPList")
    public ResultUtils queryTREEDRPList(@RequestBody ColumnName param) {
        StopWatch watch = new StopWatch();
        watch.start();
        Date date = new Date(new Date().getTime() - 24 * 60 * 60 * 1000);
        String stcd = "", stime = DateUtil.dateFormat(date, "yyyy-MM-dd HH:mm:ss"), etime = "", PID = "", ADMAUTH = "",
                STNM = "", pathname = "";
        if (null != param.getPid()) {
            PID = param.getPid();
        }
        if (null != param.getStcd()) {
            stcd = param.getStcd();
        }
        if (null != param.getStime()) {
            stime = param.getStime();
        }
        if (null != param.getEtime()) {
            etime = param.getEtime();
        }
        if (null != param.getKey()) {
            STNM = param.getKey();
        }
        if (null != param.getDatasource()) {
            ADMAUTH = param.getDatasource();
        }
        if (!CommonUtills.isEmpty(param.getPathname())) {
            pathname = param.getPathname();
        }

        List<ST_PPTN_RPojo> userList = new ArrayList<>();
        if (!CommonUtills.isEmpty(PID)) {
            ST_STBPRP_B_TREEPojo treePojo = new ST_STBPRP_B_TREEPojo();
            treePojo.setPID(PID);
            List<ST_STBPRP_B_TREEPojo> treePojoList = treeData.selectList(treePojo);

            if (treePojoList.size() > 0) {
                List<String> idList = treePojoList.stream().map(x -> x.getID()).collect(Collectors.toList());
                List<ST_STBPRP_B_QUPojo> stBprpBquList = quData.queryList("", null, null, idList);

                String finalStime = stime;
                String finalEtime = etime;
                String finalADMAUTH = ADMAUTH;
                List<ST_PPTN_RPojo> stPptnRPojoList = data.querySUMDRPList(finalStime, finalEtime, finalADMAUTH, null);
                new javalog().writelog("queryTREEDRPList雨量累计接口数据量：" + stPptnRPojoList.size(), templatefilepath);
                treePojoList.forEach(u -> {
                    try {
                        // 查询配置的站码
                        List<ST_STBPRP_B_QUPojo> quList = stBprpBquList.stream()
                                .filter(o -> o.getPID().equals(u.getID())).collect(toList());
                        if (null != quList && quList.size() > 0) {
                            List<String> stcdList = quList.stream().map(item -> item.getSTCD())
                                    .collect(Collectors.toList());

                            List<ST_PPTN_RPojo> mList = stPptnRPojoList.stream().filter(o -> {
                                boolean _flag = quList.stream().anyMatch(item -> {
                                    if (!CommonUtills.isEmpty(item.getSTTP())) {
                                        if (item.getSTCD().equals(o.getSTCD()) && item.getSTTP().equals(o.getMTYPE())) {
                                            return true;
                                        }
                                    } else {
                                        if (item.getSTCD().equals(o.getSTCD())) {
                                            return true;
                                        }
                                    }

                                    return false;
                                });
                                return _flag;
                            }).collect(toList());
                            ST_PPTN_RPojo pojo = new ST_PPTN_RPojo();
                            pojo.setSTNM(u.getTITLE());
                            pojo.setDYP(mList.stream().mapToDouble(ST_PPTN_RPojo::getDRP).sum());
                            pojo.setDRP(mList.stream().mapToDouble(ST_PPTN_RPojo::getDRP).average().getAsDouble());
                            userList.add(pojo);
                        }
                    } catch (Exception e) {
                        new javalog().writelog("queryTREEDRPList雨量累计接口报错：" + e.getMessage(), templatefilepath);
                    }
                });
            }

        }

        watch.stop();
        if (userList.size() > 0) {
            return new ResultUtils<>(userList, "操作成功", true, userList.size(), watch.getTime());
        } else {
            return new ResultUtils<>(userList, "操作成功", false, userList.size(), watch.getTime());
        }
    }

    @RequestMapping("/queryTREEDRPMaxList")
    public ResultUtils queryTREEDRPMaxList(@RequestBody ColumnName param) {
        StopWatch watch = new StopWatch();
        watch.start();
        Date date = new Date(new Date().getTime() - 24 * 60 * 60 * 1000);
        String stcd = "", stime = DateUtil.dateFormat(date, "yyyy-MM-dd HH:mm:ss"), etime = "", PID = "", ADMAUTH = "",
                STNM = "", pathname = "";
        if (null != param.getPid()) {
            PID = param.getPid();
        }
        if (null != param.getStcd()) {
            stcd = param.getStcd();
        }
        if (null != param.getStime()) {
            stime = param.getStime();
        }
        if (null != param.getEtime()) {
            etime = param.getEtime();
        }
        if (null != param.getKey()) {
            STNM = param.getKey();
        }
        if (null != param.getDatasource()) {
            ADMAUTH = param.getDatasource();
        }
        if (!CommonUtills.isEmpty(param.getPathname())) {
            pathname = param.getPathname();
        }

        List<ST_PPTN_RPojo> userList = new ArrayList<>();
        if (!CommonUtills.isEmpty(PID)) {
            ST_STBPRP_B_TREEPojo treePojo = new ST_STBPRP_B_TREEPojo();
            treePojo.setPID(PID);
            List<ST_STBPRP_B_TREEPojo> treePojoList = treeData.selectList(treePojo);

            if (treePojoList.size() > 0) {
                List<String> idList = treePojoList.stream().map(x -> x.getID()).collect(Collectors.toList());
                List<ST_STBPRP_B_QUPojo> stBprpBquList = quData.queryList("", null, null, idList);

                String finalStime = stime;
                String finalEtime = etime;
                String finalADMAUTH = ADMAUTH;
                List<ST_PPTN_RPojo> stPptnRPojoList = data.querySUMDRPList(finalStime, finalEtime, finalADMAUTH, null);

                treePojoList.forEach(u -> {
                    // 查询配置的站码
                    List<ST_STBPRP_B_QUPojo> quList = stBprpBquList.stream().filter(o -> o.getPID().equals(u.getID()))
                            .collect(Collectors.toList());
                    if (null != quList && quList.size() > 0) {
                        List<String> stcdList = quList.stream().map(item -> item.getSTCD())
                                .collect(Collectors.toList());

                        List<ST_PPTN_RPojo> mList = stPptnRPojoList.stream().filter(o -> {
                            boolean _flag = quList.stream().anyMatch(item -> {
                                if (!CommonUtills.isEmpty(item.getSTTP())) {
                                    if (item.getSTCD().equals(o.getSTCD()) && item.getSTTP().equals(o.getMTYPE())) {
                                        return true;
                                    }
                                } else {
                                    if (item.getSTCD().equals(o.getSTCD())) {
                                        return true;
                                    }
                                }

                                return false;
                            });
                            return _flag;
                        }).collect(Collectors.toList());
                        System.out.println("mList的长度::::::::::::" + mList.size());
                        if (mList != null && mList.size() > 0) {
                            // 找出雨量最大的站
                            ST_PPTN_RPojo maxDrpPojo = mList.stream()
                                    .filter(p -> p.getDRP() != null)
                                    .max(Comparator.comparing(ST_PPTN_RPojo::getDRP))
                                    .orElse(null);
                            if (maxDrpPojo != null) {
                                ST_PPTN_RPojo pojo = new ST_PPTN_RPojo();
                                pojo.setADDVNM(u.getTITLE());
                                // 找出最大雨量站的站点名称
                                String maxStnm = quList.stream()
                                        .filter(q -> q.getSTCD().equals(maxDrpPojo.getSTCD()))
                                        .findFirst()
                                        .map(ST_STBPRP_B_QUPojo::getSTNM)
                                        .orElse("");
                                pojo.setSTNM(maxStnm);
                                pojo.setDRP(maxDrpPojo.getDRP());
                                userList.add(pojo);
                            }
                        }
                    }

                });
            }

        }

        watch.stop();
        if (userList.size() > 0) {
            return new ResultUtils<>(userList, "操作成功", true, userList.size(), watch.getTime());
        } else {
            return new ResultUtils<>(userList, "操作成功", false, userList.size(), watch.getTime());
        }
    }

    @RequestMapping("/queryMAXHOURDRPList")
    public ResultUtils queryMAXHOURDRPList(@RequestBody ColumnName param) {
        StopWatch watch = new StopWatch();
        watch.start();
        Date date = new Date(new Date().getTime() - 24 * 60 * 60 * 1000);
        String stcd = "", stime = DateUtil.dateFormat(date, "yyyy-MM-dd HH:mm:ss"), etime = "", PID = "", ADMAUTH = "",
                STNM = "", pathname = "";
        if (null != param.getPid()) {
            PID = param.getPid();
        }
        if (null != param.getStcd()) {
            stcd = param.getStcd();
        }
        if (null != param.getStime()) {
            stime = param.getStime();
        }
        if (null != param.getEtime()) {
            etime = param.getEtime();
        }
        if (null != param.getKey()) {
            STNM = param.getKey();
        }
        if (null != param.getDatasource()) {
            ADMAUTH = param.getDatasource();
        }
        if (!CommonUtills.isEmpty(param.getPathname())) {
            pathname = param.getPathname();
        }

        List<ST_PPTN_RPojo> userList = new ArrayList<>();
        if (!CommonUtills.isEmpty(PID)) {
            List<String> stcdList = new ArrayList<>();
            if (!CommonUtills.isEmpty(PID)) {
                // 查询配置的站码
                List<ST_STBPRP_B_QUPojo> quList = quData.selectList("", STNM, null, PID, ADMAUTH);
                if (null != quList && quList.size() > 0) {
                    for (ST_STBPRP_B_QUPojo quPojo : quList) {
                        if (null != quPojo.getSTCD()) {
                            stcdList.add(quPojo.getSTCD());
                        }
                    }
                }
            } else {
                stcdList = Arrays.asList(stcd.split(","));
            }
            List<ST_PPTN_RPojo> oneuserList = data.queryMAXHOURDRPList(stime, etime, ADMAUTH, stcdList);
            Map<String, List<ST_PPTN_RPojo>> mapDRP = oneuserList.stream()
                    .collect(Collectors.groupingBy(ST_PPTN_RPojo::getSTCD));
            mapDRP.forEach((key, value) -> {
                List<ST_PPTN_RPojo> itemList = value.stream()
                        .sorted(Comparator.comparing(ST_PPTN_RPojo::getDRP).reversed()).collect(Collectors.toList());
                if (itemList.size() > 0) {
                    ST_PPTN_RPojo pojo = new ST_PPTN_RPojo();
                    pojo.setSTCD(itemList.get(0).getSTCD());
                    pojo.setSTNM(itemList.get(0).getSTNM());
                    pojo.setMTYPE(itemList.get(0).getMTYPE());
                    pojo.setTM(itemList.get(0).getTM());
                    pojo.setDRP(itemList.get(0).getDRP());
                    userList.add(pojo);
                }

            });
        }
        watch.stop();
        if (userList.size() > 0) {
            return new ResultUtils<>(userList, "操作成功", true, userList.size(), watch.getTime());
        } else {
            return new ResultUtils<>(userList, "操作成功", false, userList.size(), watch.getTime());
        }
    }

    @RequestMapping("/querySingleStationRainAnalysis")
    public ResultUtils querySingleStationRainAnalysis(@RequestBody ColumnName param) {
        StopWatch watch = new StopWatch();
        watch.start();
        Date date = new Date(new Date().getTime() - 24 * 60 * 60 * 1000);
        String stcd = "", stime = DateUtil.dateFormat(date, "yyyy-MM-dd HH:mm:ss"), etime = "", PID = "", ADMAUTH = "",
                STNM = "", pathname = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (null != param.getStime()) {
            stime = param.getStime();
        }
        if (null != param.getEtime()) {
            etime = param.getEtime();
            long etimeTM = 0;
            try {
                etimeTM = dateFormat.parse(etime).getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long finaletimeTM = etimeTM;
            Date etimeNew = DateUtil.addTimeToDate(new Date(finaletimeTM), "d", 1);
            etime = DateUtil.dateFormat(etimeNew, "yyyy-MM-dd HH:mm:ss");// 加一天
        }
        if (null != param.getDatasource()) {
            ADMAUTH = param.getDatasource();
        }
        if (!CommonUtills.isEmpty(param.getPathname())) {
            pathname = param.getPathname();
        }
        List<String> stcdList = new ArrayList<>();
        if (null != param.getStcd()) {
            stcdList = Arrays.asList(param.getStcd().split(","));
        }
        List<ST_PPTN_RPojo> mList = new ArrayList<>();
        mList = data.queryHOURDRPList(stime, etime, ADMAUTH, stcdList);
        long timedif = DateUtil.dateDiff(stime, etime, "yyyy-MM-dd HH:mm:ss", "d");// +1;
        long sTime = 0, eTime = 0;
        try {
            sTime = dateFormat.parse(stime).getTime();
            eTime = dateFormat.parse(etime).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        List<SingleStationRainAnalysisPojo> dataList = new ArrayList<>();
        for (int i = 0; i < timedif; i++) {
            int finalI = i;
            long finalSTime = sTime;
            Date _stm = DateUtil.addTimeToDate(new Date(finalSTime), "d", finalI);
            Date _etm = DateUtil.addTimeToDate(new Date(finalSTime), "d", finalI + 1);
            try {
                _stm = dateFormat.parse(DateUtil.dateFormat(_stm, "yyyy-MM-dd 08:00:00"));
                _etm = dateFormat.parse(DateUtil.dateFormat(_etm, "yyyy-MM-dd 08:00:00"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Date final_stm = _stm;
            Date final_etm = _etm;
            String ymdhm = DateUtil.dateFormat(_stm, "yyyy-MM-dd");
            List<ST_PPTN_RPojo> mListTemp = mList.stream().filter(j -> {
                if (null != j.getTM()) {
                    try {
                        return dateFormat.parse(j.getTM()).after(final_stm)
                                && dateFormat.parse(j.getTM()).before(final_etm);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }).collect(Collectors.toList());
            mListTemp.sort((a, b) -> {
                if (a.getDRP() != null && b.getDRP() != null) {
                    return (int) (Double.valueOf(b.getDRP()) * 1000 - Double.valueOf(a.getDRP()) * 1000);
                }
                return -1;
            });
            // 累计雨量
            // 日累计降雨
            Double DRPSum = mListTemp.stream().mapToDouble(ST_PPTN_RPojo::getDRP).sum();
            // 最高雨量
            Double maxZ = (new BigDecimal(mListTemp.get(0).getDRP()).setScale(1, BigDecimal.ROUND_HALF_UP))
                    .doubleValue();
            String maxZ_TM = mListTemp.get(0).getTM();
            SingleStationRainAnalysisPojo pojo = new SingleStationRainAnalysisPojo();
            pojo.setSTCD(mListTemp.get(0).getSTCD());
            pojo.setSTNM(mListTemp.get(0).getSTNM());
            pojo.setYMDHM(ymdhm);
            pojo.setMAX_DRP(maxZ);
            pojo.setTM(maxZ_TM);
            pojo.setDYRN(DRPSum);
            dataList.add(pojo);
        }
        watch.stop();
        if (dataList.size() > 0) {
            return new ResultUtils<>(dataList, "操作成功", true, dataList.size(), watch.getTime());
        } else {
            return new ResultUtils<>(dataList, "操作成功", false, dataList.size(), watch.getTime());
        }
    }

    // 多站雨情
    @RequestMapping("/queryDRPDUOZHANList")
    public ResultUtils queryDRPDUOZHANList(@RequestBody ColumnName param) {
        StopWatch watch = new StopWatch();
        watch.start();
        Date date = new Date(new Date().getTime() - 24 * 60 * 60 * 1000);
        String stcd = "", stime = DateUtil.dateFormat(date, "yyyy-MM-dd HH:mm:ss"), etime = "", PID = "", ADMAUTH = "",
                STNM = "", pathname = "";

        if (null != param.getStime()) {
            stime = param.getStime();
        }
        if (null != param.getEtime()) {
            etime = param.getEtime();
        }
        if (null != param.getDatasource()) {
            ADMAUTH = param.getDatasource();
        }
        if (!CommonUtills.isEmpty(param.getPathname())) {
            pathname = param.getPathname();
        }
        List<String> stcdList = new ArrayList<>();
        if (null != param.getStcd()) {
            stcdList = Arrays.asList(param.getStcd().split(","));
        }
        List<ST_PPTN_RPojo> mList = new ArrayList<>();
        mList = data.querySUMDRPList(stime, etime, ADMAUTH, stcdList);
        Map<String, Object> map = new HashMap<>();
        List<String> stcdListnm = new ArrayList<>();
        List<Map<String, Object>> mapList = new ArrayList<>();
        map.put("listSTCD", stcdListnm);
        map.put("list", mapList);
        JSONObject json = new JSONObject(map);
        watch.stop();
        if (mapList.size() > 0) {
            return new ResultUtils<>(json, "操作成功", true, mapList.size(), watch.getTime());
        } else {
            return new ResultUtils<>(json, "操作成功", false, mapList.size(), watch.getTime());
        }
    }

    // 水雨情关系
    @RequestMapping("/queryStationWaterRainAnalysis")
    public ResultUtils queryStationWaterRainAnalysis(@RequestBody ColumnName param) {
        StopWatch watch = new StopWatch();
        watch.start();
        Date date = new Date(new Date().getTime() - 24 * 60 * 60 * 1000);
        String stime = DateUtil.dateFormat(date, "yyyy-MM-dd HH:mm:ss"), etime = "", ADMAUTH = "", PID = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (null != param.getPid()) {
            PID = param.getPid();
        }
        if (null != param.getStime()) {
            stime = param.getStime();
            long etimeTM = 0;
            try {
                etimeTM = dateFormat.parse(stime).getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long finaletimeTM = etimeTM;
            Date etimeNew = DateUtil.addTimeToDate(new Date(finaletimeTM), "h", 24);
            etime = DateUtil.dateFormat(etimeNew, "yyyy-MM-dd HH:mm:ss");// 加一天
        }
        if (null != param.getDatasource()) {
            ADMAUTH = param.getDatasource();
        }
        String STZZ = "1", STPP = "1", STDP = "", STDD = "", STMM = "";
        List<ST_STBPRP_B_STTPPojo> sttpList = sttpData.selectList(STZZ, STPP, STDP, STDD, STMM);
        // 查询配置的站码
        List<String> stcdList = new ArrayList<>();
        if (!CommonUtills.isEmpty(PID)) {
            // 查询配置的站码
            List<ST_STBPRP_B_QUPojo> quList = quData.selectList("", "", null, PID, ADMAUTH);
            if (null != quList && quList.size() > 0) {
                for (ST_STBPRP_B_QUPojo quPojo : quList) {
                    if (null != quPojo.getSTCD()) {
                        stcdList.add(quPojo.getSTCD());
                    }
                }
            }
        } else {
            if (null != sttpList && sttpList.size() > 0) {
                for (ST_STBPRP_B_STTPPojo sttpPojo : sttpList) {
                    if (null != sttpPojo.getSTCD()) {
                        stcdList.add(sttpPojo.getSTCD());
                    }
                }
            }
        }
        // 查询基础站码信息
        List<ST_STBPRP_BPojo> stStbprpBPList = stbprpBData.selectList(stcdList, null);
        List<ST_PPTN_RPojo> mList = data.queryHOURDRPList(stime, etime, ADMAUTH, stcdList);
        List<GETRAINANDWATERPojo> dataList = new ArrayList<>();
        List<GetWaterViewNewPojo> list = viewNewServer.selectListByHisIsHouse(stcdList, stime, etime, ADMAUTH);

        long sTime = 0;
        try {
            sTime = dateFormat.parse(stime).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long finalSTime = sTime;
        Date ED3 = DateUtil.addTimeToDate(new Date(finalSTime), "h", 3);
        Date ED6 = DateUtil.addTimeToDate(new Date(finalSTime), "h", 6);
        Date ED9 = DateUtil.addTimeToDate(new Date(finalSTime), "h", 9);
        Date ED12 = DateUtil.addTimeToDate(new Date(finalSTime), "h", 12);
        Date ED24 = DateUtil.addTimeToDate(new Date(finalSTime), "h", 24);
        String ED3Str = DateUtil.dateFormat(ED3, "yyyy-MM-dd HH:mm:ss");
        String ED6Str = DateUtil.dateFormat(ED6, "yyyy-MM-dd HH:mm:ss");
        String ED9Str = DateUtil.dateFormat(ED9, "yyyy-MM-dd HH:mm:ss");
        String ED12Str = DateUtil.dateFormat(ED12, "yyyy-MM-dd HH:mm:ss");
        String ED24Str = DateUtil.dateFormat(ED24, "yyyy-MM-dd HH:mm:ss");

        if (stStbprpBPList.size() > 0) {
            for (ST_STBPRP_BPojo item : stStbprpBPList) {
                GETRAINANDWATERPojo pojo = new GETRAINANDWATERPojo();
                pojo.setSTCD(item.getSTCD());
                pojo.setSTNM(item.getSTNM());
                pojo.setLGTD(item.getLGTD());
                pojo.setLTTD(item.getLTTD());
                pojo.setTM1(stime);
                Double Z1 = 0.0;
                Double Z3 = 0.0;
                Double Z6 = 0.0;
                Double Z9 = 0.0;
                Double Z12 = 0.0;
                Double Z24 = 0.0;
                String _stcd = item.getSTCD();
                String finalStime = stime;
                // 水位
                List<GetWaterViewNewPojo> listTemp = list.stream()
                        .filter(p -> DateUtil.strToDate(p.getTM(), DateUtil.YMDHMS).getTime() == DateUtil
                                .strToDate(finalStime, DateUtil.YMDHMS).getTime() && p.getSTCD().equals(_stcd))
                        .collect(Collectors.toList());
                if (listTemp.size() > 0) {
                    Double strVal = Double.valueOf(listTemp.get(0).getUPZ());
                    pojo.setZ1(strVal);
                }
                listTemp = list.stream()
                        .filter(p -> DateUtil.strToDate(p.getTM(), DateUtil.YMDHMS).getTime() == DateUtil
                                .strToDate(ED3Str, DateUtil.YMDHMS).getTime() && p.getSTCD().equals(_stcd))
                        .collect(Collectors.toList());
                if (listTemp.size() > 0) {
                    Double strVal = Double.valueOf(listTemp.get(0).getUPZ());
                    pojo.setZ3(strVal);
                }
                listTemp = list.stream()
                        .filter(p -> DateUtil.strToDate(p.getTM(), DateUtil.YMDHMS).getTime() == DateUtil
                                .strToDate(ED6Str, DateUtil.YMDHMS).getTime() && p.getSTCD().equals(_stcd))
                        .collect(Collectors.toList());
                if (listTemp.size() > 0) {
                    Double strVal = Double.valueOf(listTemp.get(0).getUPZ());
                    pojo.setZ6(strVal);
                }
                listTemp = list.stream()
                        .filter(p -> DateUtil.strToDate(p.getTM(), DateUtil.YMDHMS).getTime() == DateUtil
                                .strToDate(ED9Str, DateUtil.YMDHMS).getTime() && p.getSTCD().equals(_stcd))
                        .collect(Collectors.toList());
                if (listTemp.size() > 0) {
                    Double strVal = Double.valueOf(listTemp.get(0).getUPZ());
                    pojo.setZ9(strVal);
                }
                listTemp = list.stream()
                        .filter(p -> DateUtil.strToDate(p.getTM(), DateUtil.YMDHMS).getTime() == DateUtil
                                .strToDate(ED12Str, DateUtil.YMDHMS).getTime() && p.getSTCD().equals(_stcd))
                        .collect(Collectors.toList());
                if (listTemp.size() > 0) {
                    Double strVal = Double.valueOf(listTemp.get(0).getUPZ());
                    pojo.setZ12(strVal);
                }
                listTemp = list.stream()
                        .filter(p -> DateUtil.strToDate(p.getTM(), DateUtil.YMDHMS).getTime() == DateUtil
                                .strToDate(ED24Str, DateUtil.YMDHMS).getTime() && p.getSTCD().equals(_stcd))
                        .collect(Collectors.toList());
                if (listTemp.size() > 0) {
                    Double strVal = Double.valueOf(listTemp.get(0).getUPZ());
                    pojo.setZ24(strVal);
                }

                // 雨量
                long finalSTime1 = sTime;
                List<ST_PPTN_RPojo> mListTemp = mList.stream().filter(j -> {
                    if (null != j.getTM()) {
                        try {
                            return dateFormat.parse(j.getTM()).after(new Date(finalSTime1))
                                    && dateFormat.parse(j.getTM()).before(ED3) && j.getSTCD().equals(_stcd);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    return false;
                }).collect(Collectors.toList());
                Double DRPSum = mListTemp.stream().mapToDouble(ST_PPTN_RPojo::getDRP).sum();
                pojo.setDRP3(DRPSum);

                mListTemp = mList.stream().filter(j -> {
                    if (null != j.getTM()) {
                        try {
                            return dateFormat.parse(j.getTM()).after(new Date(finalSTime1))
                                    && dateFormat.parse(j.getTM()).before(ED6) && j.getSTCD().equals(_stcd);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    return false;
                }).collect(Collectors.toList());
                DRPSum = mListTemp.stream().mapToDouble(ST_PPTN_RPojo::getDRP).sum();
                pojo.setDRP6(DRPSum);

                mListTemp = mList.stream().filter(j -> {
                    if (null != j.getTM()) {
                        try {
                            return dateFormat.parse(j.getTM()).after(new Date(finalSTime1))
                                    && dateFormat.parse(j.getTM()).before(ED9) && j.getSTCD().equals(_stcd);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    return false;
                }).collect(Collectors.toList());
                DRPSum = mListTemp.stream().mapToDouble(ST_PPTN_RPojo::getDRP).sum();
                pojo.setDRP9(DRPSum);

                mListTemp = mList.stream().filter(j -> {
                    if (null != j.getTM()) {
                        try {
                            return dateFormat.parse(j.getTM()).after(new Date(finalSTime1))
                                    && dateFormat.parse(j.getTM()).before(ED12) && j.getSTCD().equals(_stcd);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    return false;
                }).collect(Collectors.toList());
                DRPSum = mListTemp.stream().mapToDouble(ST_PPTN_RPojo::getDRP).sum();
                pojo.setDRP12(DRPSum);

                mListTemp = mList.stream().filter(j -> {
                    if (null != j.getTM()) {
                        try {
                            return dateFormat.parse(j.getTM()).after(new Date(finalSTime1))
                                    && dateFormat.parse(j.getTM()).before(ED24) && j.getSTCD().equals(_stcd);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    return false;
                }).collect(Collectors.toList());
                DRPSum = mListTemp.stream().mapToDouble(ST_PPTN_RPojo::getDRP).sum();
                pojo.setDRP24(DRPSum);

                dataList.add(pojo);
            }
        }
        watch.stop();
        if (dataList.size() > 0) {
            return new ResultUtils<>(dataList, "操作成功", true, dataList.size(), watch.getTime());
        } else {
            return new ResultUtils<>(dataList, "操作成功", false, dataList.size(), watch.getTime());
        }
    }

    // 汛情报文
    @RequestMapping("/queryFloodSituationMessage")
    public ResultUtils queryFloodSituationMessage(@RequestBody ColumnName param) {
        StopWatch watch = new StopWatch();
        watch.start();
        Date date = new Date(new Date().getTime() - 24 * 60 * 60 * 1000);
        String stime = DateUtil.dateFormat(date, "yyyy-MM-dd HH:mm:ss"), etime = "", ADMAUTH = "", PID = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (null != param.getPid()) {
            PID = param.getPid();
        }
        if (null != param.getStime()) {
            stime = param.getStime();
        }
        if (null != param.getEtime()) {
            etime = param.getEtime();
        }
        if (null != param.getDatasource()) {
            ADMAUTH = param.getDatasource();
        }
        String STZZ = "1", STPP = "1", STDP = "", STDD = "", STMM = "";
        List<ST_STBPRP_B_STTPPojo> sttpList = sttpData.selectList(STZZ, STPP, STDP, STDD, STMM);
        // 查询配置的站码
        List<String> stcdList = new ArrayList<>();
        if (!CommonUtills.isEmpty(PID)) {
            // 查询配置的站码
            List<ST_STBPRP_B_QUPojo> quList = quData.selectList("", "", null, PID, ADMAUTH);
            if (null != quList && quList.size() > 0) {
                for (ST_STBPRP_B_QUPojo quPojo : quList) {
                    if (null != quPojo.getSTCD()) {
                        stcdList.add(quPojo.getSTCD());
                    }
                }
            }
        } else {
            if (null != sttpList && sttpList.size() > 0) {
                for (ST_STBPRP_B_STTPPojo sttpPojo : sttpList) {
                    if (null != sttpPojo.getSTCD()) {
                        stcdList.add(sttpPojo.getSTCD());
                    }
                }
            }
        }
        // 查询基础站码信息
        List<ST_STBPRP_BPojo> stStbprpBPList = stbprpBData.selectList(stcdList, null);
        List<ST_PPTN_RPojo> mList = data.queryHOURDRPList(stime, etime, ADMAUTH, stcdList);
        List<GETMAXWATERRAINPojo> dataList = new ArrayList<>();
        List<GetWaterViewNewPojo> list = viewNewServer.selectListByHisIsTime(stcdList, stime, etime, ADMAUTH);
        long sTime = 0, eTime = 0;
        try {
            sTime = dateFormat.parse(stime).getTime();
            eTime = dateFormat.parse(etime).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long timedif = DateUtil.dateDiff(stime, etime, "yyyy-MM-dd HH:mm:ss", "d") + 1;
        if (stStbprpBPList.size() > 0) {
            for (ST_STBPRP_BPojo item : stStbprpBPList) {
                String _stcd = item.getSTCD();
                List<GetWaterViewNewPojo> listTemp = list.stream().filter(p -> p.getSTCD().equals(_stcd))
                        .collect(Collectors.toList());
                List<ST_PPTN_RPojo> mListTemp = mList.stream().filter(p -> p.getSTCD().equals(_stcd))
                        .collect(Collectors.toList());

                GETMAXWATERRAINPojo pojo = new GETMAXWATERRAINPojo();
                pojo.setSTCD(_stcd);
                pojo.setSTNM(item.getSTNM());
                if (listTemp.size() > 0) {
                    listTemp.sort((a, b) -> {
                        if (a.getUPZ() != null && b.getUPZ() != null) {
                            return (int) (Double.valueOf(b.getUPZ()) * 1000 - Double.valueOf(a.getUPZ()) * 1000);
                        }
                        return -1;
                    });
                    Double maxZ = (new BigDecimal(listTemp.get(0).getUPZ()).setScale(2, BigDecimal.ROUND_HALF_UP))
                            .doubleValue();
                    String maxZ_TM = listTemp.get(0).getTM();
                    pojo.setZ(maxZ.toString());
                    pojo.setTM(maxZ_TM);
                }

                if (mListTemp.size() > 0) {
                    mListTemp.sort((a, b) -> {
                        if (a.getDRP() != null && b.getDRP() != null) {
                            return (int) (Double.valueOf(b.getDRP()) * 1000 - Double.valueOf(a.getDRP()) * 1000);
                        }
                        return -1;
                    });
                    Double maxDrp = (new BigDecimal(mListTemp.get(0).getDRP()).setScale(1, BigDecimal.ROUND_HALF_UP))
                            .doubleValue();
                    String maxDrp_TM = mListTemp.get(0).getTM();
                    // 累计降雨
                    Double DRPSum = mListTemp.stream().mapToDouble(ST_PPTN_RPojo::getDRP).sum();
                    pojo.setDRP(DRPSum);// 累计降雨
                    pojo.setMAX_HDRP(maxDrp);// 小时最大降雨量
                    pojo.setMAX_HTM(maxDrp_TM);// 小时最大降雨量时段

                    double maxDdrp = 0;
                    String maxDDrp_TM = "";
                    for (int i = 0; i < timedif; i++) {
                        int finalI = i;
                        long finalSTime = sTime;
                        Date _stm = DateUtil.addTimeToDate(new Date(finalSTime), "d", finalI);
                        Date _etm = DateUtil.addTimeToDate(new Date(finalSTime), "d", finalI + 1);
                        try {
                            _stm = dateFormat.parse(DateUtil.dateFormat(_stm, "yyyy-MM-dd 08:00:00"));
                            _etm = dateFormat.parse(DateUtil.dateFormat(_etm, "yyyy-MM-dd 08:00:00"));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Date final_stm = _stm;
                        Date final_etm = _etm;
                        List<ST_PPTN_RPojo> mListTempDan = mList.stream().filter(j -> {
                            if (null != j.getTM()) {
                                try {
                                    return dateFormat.parse(j.getTM()).after(final_stm)
                                            && dateFormat.parse(j.getTM()).before(final_etm);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                            return false;
                        }).collect(Collectors.toList());
                        Double DDRPSum = mListTempDan.stream().mapToDouble(ST_PPTN_RPojo::getDRP).sum();
                        if (maxDdrp < DDRPSum) {
                            maxDdrp = DDRPSum;
                            maxDDrp_TM = DateUtil.dateFormat(final_stm, "yyyy-MM-dd");
                        }
                    }
                    pojo.setMAX_DDRP(maxDdrp);// 日最大降雨量
                    pojo.setMAX_DTM(maxDDrp_TM);// 日最大降雨量时间
                }
                dataList.add(pojo);
            }
        }
        watch.stop();
        if (dataList.size() > 0) {
            return new ResultUtils<>(dataList, "操作成功", true, dataList.size(), watch.getTime());
        } else {
            return new ResultUtils<>(dataList, "操作成功", false, dataList.size(), watch.getTime());
        }
    }

    @RequestMapping("/selectListHourly")
    public ResultUtils selectListHourly(@RequestBody ColumnName param) {
        StopWatch watch = new StopWatch();
        watch.start();
        List<ST_PSTAT_RPojo> listData = new ArrayList<>();
        String stimes = "", etimes = "", pathname = "1";
        List<String> stcdList = new ArrayList<>();
        if (null != param.getStcd() && !param.getStcd().equals("")) {
            stcdList = Arrays.asList(param.getStcd().split(","));
        }
        if (null != param.getStime()) {
            stimes = param.getStime();
        }
        if (null != param.getEtime()) {
            etimes = param.getEtime();
        }
        if (null != param.getPathname()) {
            pathname = param.getPathname();
        }
        List<ST_PPTN_RPojo> userData = data.selectListHourly(stcdList, stimes, etimes);
        watch.stop();
        if (userData.size() > 0) {
            return new ResultUtils<>(userData, "操作成功", true, userData.size(), watch.getTime());
        } else {
            return new ResultUtils<>(userData, "操作成功", false, userData.size(), watch.getTime());
        }
    }

    @RequestMapping("/selectListHourlyArea")
    public ResultUtils selectListHourlyArea(@RequestBody ColumnName param) {
        StopWatch watch = new StopWatch();
        watch.start();
        List<ST_PSTAT_RPojo> listData = new ArrayList<>();
        String stimes = "", etimes = "", pathname = "1";
        List<String> stcdList = new ArrayList<>();
        if (!CommonUtills.isEmpty(param.getPid())) {
            // 查询配置的站码
            List<ST_STBPRP_B_QUPojo> quList = quData.selectList("", null, null, param.getPid(), null);
            quList.stream()
                    .map(ST_STBPRP_B_QUPojo::getSTCD) // 提取 STCD
                    .filter(Objects::nonNull) // 过滤掉 null 值
                    .forEach(stcdList::add); // 添加到列表
        } else {
            stcdList = Arrays.asList(param.getStcd().split(","));
        }
        if (null != param.getStime()) {
            stimes = param.getStime();
        }
        if (null != param.getEtime()) {
            etimes = param.getEtime();
        }
        List<ST_PPTN_RPojo> userData = data.selectListHourlyArea(stcdList, stimes, etimes);
        watch.stop();
        if (userData.size() > 0) {
            return new ResultUtils<>(userData, "操作成功", true, userData.size(), watch.getTime());
        } else {
            return new ResultUtils<>(userData, "操作成功", false, userData.size(), watch.getTime());
        }
    }

    @RequestMapping("/selectListHourlyAreaYB")
    public ResultUtils selectListHourlyAreaYB(@RequestBody ColumnName param) {
        StopWatch watch = new StopWatch();
        watch.start();
        List<ST_PSTAT_RPojo> listData = new ArrayList<>();
        String stimes = "", etimes = "", pathname = "1";
        List<String> stcdList = new ArrayList<>();
        if (!CommonUtills.isEmpty(param.getPid())) {
            // 查询配置的站码
            List<ST_STBPRP_B_QUPojo> quList = quData.selectList("", null, null, param.getPid(), null);
            quList.stream()
                    .map(ST_STBPRP_B_QUPojo::getSTCD) // 提取 STCD
                    .filter(Objects::nonNull) // 过滤掉 null 值
                    .forEach(stcdList::add); // 添加到列表
        } else {
            stcdList = Arrays.asList(param.getStcd().split(","));
        }
        if (null != param.getStime()) {
            stimes = param.getStime();
        }
        if (null != param.getEtime()) {
            etimes = param.getEtime();
        }
        int hours = 6;
        if (param.getPathname() != null) {
            hours = Integer.parseInt(param.getPathname());
        }
        List<ST_PPTN_RPojo> userData = data.selectListHourlyArea(stcdList, stimes, etimes);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime ldt = LocalDateTime.parse(etimes, formatter);
        ZonedDateTime zdt = ldt.atZone(ZoneId.systemDefault());
        ZonedDateTime resultZdt = zdt.plusHours(hours);
        String resultStrE = resultZdt.format(formatter);
        String rlstime = zdt.minusHours(48).format(formatter);

        List<Tz_watersheddataPojo> listYB = tzWatersheddataData.selectListLast(etimes, resultStrE, Arrays.asList("48"),
                "上海气象台");
        Map<String, Double> averageMap = listYB.stream()
                .collect(Collectors.groupingBy(
                        Tz_watersheddataPojo::getFTM, // 1. 分组键
                        LinkedHashMap::new, // 2. 【关键修改】指定使用 LinkedHashMap
                        Collectors.averagingDouble(Tz_watersheddataPojo::getDRP) // 3. 下游收集器
                ));
        for (Map.Entry<String, Double> entry : averageMap.entrySet()) {
            String ftm = entry.getKey(); // 获取时间
            Double avgDrp = entry.getValue(); // 获取平均雨量
            avgDrp = Math.round(avgDrp * 10) / 10.0;
            ST_PPTN_RPojo pojo = new ST_PPTN_RPojo();
            pojo.setTM(ftm);
            pojo.setDYP(avgDrp);
            userData.add(pojo);
        }
        watch.stop();
        if (userData.size() > 0) {
            return new ResultUtils<>(userData, "操作成功", true, userData.size(), watch.getTime());
        } else {
            return new ResultUtils<>(userData, "操作成功", false, userData.size(), watch.getTime());
        }
    }

    // 5分钟，小时，日水雨情
    @RequestMapping("/querySWDRPDANZHANList")
    public ResultUtils querySWDRPDANZHANList(@RequestBody ColumnName param) {
        StopWatch watch = new StopWatch();
        watch.start();
        Date date = new Date(new Date().getTime() - 24 * 60 * 60 * 1000);
        String stcd = "", stime = DateUtil.dateFormat(date, "yyyy-MM-dd HH:mm:ss"), etime = "", PID = "", ADMAUTH = "",
                STNM = "", pathname = "";

        if (null != param.getStime()) {
            stime = param.getStime();
        }
        if (null != param.getEtime()) {
            etime = param.getEtime();
        }
        if (null != param.getDatasource()) {
            ADMAUTH = param.getDatasource();
        }
        if (!CommonUtills.isEmpty(param.getPathname())) {
            pathname = param.getPathname();
        }
        List<String> stcdList = new ArrayList<>();
        if (null != param.getStcd()) {
            stcdList = Arrays.asList(param.getStcd().split(","));
        }
        List<ST_PPTN_RPojo> mList = new ArrayList<>();
        List<GetWaterViewNewPojo> list = new ArrayList<>();
        if (pathname.equals("DAY")) {
            list = server.selectListByHisIsDay(stcdList, stime, etime, ADMAUTH);
            mList = data.queryDAYDRPList(stime, etime, ADMAUTH, stcdList);
        } else if (pathname.equals("HOUR")) {
            list = server.selectListByHisIsHouse(stcdList, stime, etime, ADMAUTH);
            mList = data.queryHOURDRPList(stime, etime, ADMAUTH, stcdList);
        } else if (pathname.equals("SUM")) {
            mList = data.querySUMDRPList(stime, etime, ADMAUTH, stcdList);
        } else {
            mList = data.queryDRPList(stime, etime, ADMAUTH, stcdList);
            list = server.selectListByHisIsTime(stcdList, stime, etime, ADMAUTH);
        }
        List<ST_PPTN_RPojo> finalMList = mList;
        List<GetWaterViewNewPojo> listNew = new ArrayList<>();
        list.forEach(u -> {
            GetWaterViewNewPojo pojo = u;
            List<ST_PPTN_RPojo> mListT = finalMList.stream()
                    .filter(p -> p.getSTCD().equals(u.getSTCD()) && p.getTM().equals(u.getTM())).collect(toList());
            if (mListT.size() > 0) {
                pojo.setDRP(mListT.get(0).getDRP());
            }
            listNew.add(pojo);
        });
        watch.stop();
        if (listNew.size() > 0) {
            return new ResultUtils<>(listNew, "操作成功", true, listNew.size(), watch.getTime());
        } else {
            return new ResultUtils<>(listNew, "操作成功", false, listNew.size(), watch.getTime());
        }
    }

    // 一个水位站，一个雨量站
    @RequestMapping("/querySWDRPDANZHANListSYL")
    public ResultUtils querySWDRPDANZHANListSYL(@RequestBody ColumnName param) {
        StopWatch watch = new StopWatch();
        watch.start();
        Date date = new Date(new Date().getTime() - 24 * 60 * 60 * 1000);
        String stcd = "", stime = DateUtil.dateFormat(date, "yyyy-MM-dd HH:mm:ss"), etime = "", PID = "", ADMAUTH = "",
                STNM = "", pathname = "";

        if (null != param.getStime()) {
            stime = param.getStime();
        }
        if (null != param.getEtime()) {
            etime = param.getEtime();
        }
        if (null != param.getDatasource()) {
            ADMAUTH = param.getDatasource();
        }
        if (!CommonUtills.isEmpty(param.getPathname())) {
            pathname = param.getPathname();
        }
        List<String> stcdListSW = new ArrayList<>();
        List<String> stcdList = new ArrayList<>();
        if (null != param.getStcd()) {
            stcdListSW = Arrays.asList(param.getStcd().split(",")[0]);
            stcdList = param.getStcd().split(",").length >= 1 ? Arrays.asList(param.getStcd().split(",")[1])
                    : Arrays.asList("aaaaaaaaaaaaaa");
        }
        List<ST_PPTN_RPojo> mList = new ArrayList<>();
        List<GetWaterViewNewPojo> list = new ArrayList<>();
        if (pathname.equals("DAY")) {
            list = server.selectListByHisIsDay(stcdListSW, stime, etime, ADMAUTH);// 水位
            mList = data.queryDAYDRPList(stime, etime, ADMAUTH, stcdList);
        } else if (pathname.equals("HOUR")) {
            list = server.selectListByHisIsHouse(stcdListSW, stime, etime, ADMAUTH);// 水位
            mList = data.queryHOURDRPList(stime, etime, ADMAUTH, stcdList);
        } else if (pathname.equals("SUM")) {
            mList = data.querySUMDRPList(stime, etime, ADMAUTH, stcdList);
        } else {
            list = server.selectListByHisIsTime(stcdListSW, stime, etime, ADMAUTH);// 水位
            mList = data.queryDRPList(stime, etime, ADMAUTH, stcdList);
        }
        List<ST_PPTN_RPojo> finalMList = mList;
        List<GetWaterViewNewPojo> listNew = new ArrayList<>();
        list.forEach(u -> {
            GetWaterViewNewPojo pojo = u;
            List<ST_PPTN_RPojo> mListT = finalMList.stream()
                    .filter(p -> p.getTM().equals(u.getTM())).collect(toList());
            if (mListT.size() > 0) {
                pojo.setDRP(mListT.get(0).getDRP());
            }
            listNew.add(pojo);
        });
        watch.stop();
        if (listNew.size() > 0) {
            return new ResultUtils<>(listNew, "操作成功", true, listNew.size(), watch.getTime());
        } else {
            return new ResultUtils<>(listNew, "操作成功", false, listNew.size(), watch.getTime());
        }
    }

    @RequestMapping("/getRainDashboard")
    public ResultUtils getRainDashboard(@RequestBody ColumnName param) {
        StopWatch watch = new StopWatch();
        watch.start();
        Map<String, Object> result = new HashMap<>();

        String stime = "", etime = "";
        if (param.getStime() != null) {
            stime = param.getStime();
        }
        if (param.getEtime() != null) {
            etime = param.getEtime();
        }
        List<ST_PPTN_RPojo> mList = data.selectListMinuArea(null, stime, etime);

        // 1. 计算滑动时段雨量 (1h, 3h, 6h, 12h, 24h)
        result.put("rain_1h", calculateEventRainMinute(60, mList, param.getEtime()));
        result.put("rain_3h", calculateEventRainMinute(180, mList, param.getEtime()));
        result.put("rain_6h", calculateEventRainMinute(360, mList, param.getEtime()));
        result.put("rain_12h", calculateEventRainMinute(720, mList, param.getEtime()));
        result.put("rain_24h", calculateEventRainMinute(1440, mList, param.getEtime()));

        // 2. 计算场次雨量 (通过 Java 逻辑分析最近的数据)
        // BigDecimal eventRain = calculateEventRain(stcd,param.getEtime());
        // result.put("rain_event", format(eventRain));

        // return result;
        watch.stop();
        if (result.size() > 0) {
            return new ResultUtils<>(result, "操作成功", true, result.size(), watch.getTime());
        } else {
            return new ResultUtils<>(result, "操作成功", false, result.size(), watch.getTime());
        }
    }

    private double calculateEventRainMinute(int minute, List<ST_PPTN_RPojo> mList, String etime) {
        // 1. 定义日期时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // 2. 将字符串解析为 LocalDateTime 对象，处理日期和日期时间两种格式
        LocalDateTime dateTime;
        if (etime.length() <= 10) {
            dateTime = LocalDateTime.parse(etime + " 00:00:00", formatter);
        } else {
            dateTime = LocalDateTime.parse(etime, formatter);
        }

        // 3. 减去 60 分钟
        LocalDateTime newDateTime = dateTime.minusMinutes(minute);

        // 4. 格式化回字符串
        String newEtime = newDateTime.format(formatter);

        // 将边界时间字符串解析为 LocalDateTime 对象
        LocalDateTime startTime = LocalDateTime.parse(newEtime, formatter);
        LocalDateTime endTime;
        if (etime.length() <= 10) {
            endTime = LocalDateTime.parse(etime + " 00:00:00", formatter);
        } else {
            endTime = LocalDateTime.parse(etime, formatter);
        }

        // 使用 Stream API 进行过滤
        List<ST_PPTN_RPojo> filteredList = mList.stream()
                // 1. 过滤掉 TM 为空的数据，防止空指针异常
                .filter(pojo -> pojo.getTM() != null && !pojo.getTM().isEmpty())
                // 2. 解析 TM 并判断是否在 [newEtime, etime] 范围内
                .filter(pojo -> {
                    LocalDateTime tmTime = LocalDateTime.parse(pojo.getTM(), formatter);
                    // 大于等于 startTime 且 小于等于 endTime
                    return !tmTime.isBefore(startTime) && !tmTime.isAfter(endTime);
                })
                // 3. 收集为新的 List
                .collect(Collectors.toList());

        // 计算 filteredList 中 DRP 的总和
        double totalDrp = filteredList.stream()
                // 过滤掉 DRP 为 null 的数据，避免空指针异常
                .filter(pojo -> pojo.getDRP() != null)
                // 将对象流映射为 Double 类型的流
                .mapToDouble(ST_PPTN_RPojo::getDRP)
                // 求和
                .sum();
        return totalDrp;
    }

    // 32个代表站当前场次雨量
    @RequestMapping("/getEventRain32")
    public ResultUtils getEventRain32(@RequestBody ColumnName param) {
        StopWatch watch = new StopWatch();
        watch.start();
        List<ST_PPTN_RPojo> result = new ArrayList<>();

        String stime = "", etime = "", PID = "";
        if (param.getStime() != null) {
            stime = param.getStime();
        }
        if (param.getEtime() != null) {
            etime = param.getEtime();
        }
        if (param.getPid() != null) {
            PID = param.getPid();
        } else {
            return new ResultUtils<>(result, "操作失败:必传参数不能为空", false, 0, watch.getTime());
        }
        List<ST_STBPRP_B_QUPojo> quList = quData.selectList("", "", null, PID, null);
        List<String> stcdList = quList.stream().map(ST_STBPRP_B_QUPojo::getSTCD).collect(Collectors.toList());
        List<ST_PPTN_RPojo> mList = data.queryHOURDRPList(stime, etime, null, stcdList);
        List<ST_RAINSTORMFREQUENCY_RPojo> historyList = stormData.selectList(null, null, null);
        quList.forEach(item -> {
            List<ST_PPTN_RPojo> mListT = mList.stream().filter(o -> o.getSTCD().equals(item.getSTCD()))
                    .collect(Collectors.toList());
            EventRainResult reRainResult = RainfallAnalyzer.analyzeEventRain(mListT);
            ST_PPTN_RPojo pojo = new ST_PPTN_RPojo();
            pojo.setSTCD(item.getSTCD());
            pojo.setSTNM(item.getSTNM());
            pojo.setDYP(reRainResult == null ? 0 : reRainResult.eventRain.doubleValue());
            pojo.setDRP(reRainResult == null ? 0 : reRainResult.max24hRain.doubleValue());

            // 查询历史场次数据，计算每个站的场次排名
            List<ST_RAINSTORMFREQUENCY_RPojo> historyListT = historyList.stream()
                    .filter(o -> o.getSTCD().equals(item.getSTCD())).collect(Collectors.toList());
            if (historyListT != null && !historyListT.isEmpty()) {
                int rank = 1;
                for (ST_RAINSTORMFREQUENCY_RPojo history : historyList) {
                    if (history.getDRP() != null && pojo.getDRP() != null && history.getDRP() > pojo.getDRP()) {
                        rank++;
                    }
                }
                pojo.setRANK(rank);
                pojo.setSTORMTOTAL(historyListT.size());
            } else {
                pojo.setRANK(1); // 无历史记录，默认为第1
                pojo.setSTORMTOTAL(0);
            }
            result.add(pojo);
        });
        watch.stop();
        if (result.size() > 0) {
            // 按雨量倒序排序
            result.sort(
                    Comparator.comparing(ST_PPTN_RPojo::getDRP, Comparator.nullsLast(Double::compareTo)).reversed());

            return new ResultUtils<>(result, "操作成功", true, result.size(), watch.getTime());
        } else {
            return new ResultUtils<>(result, "操作成功", false, result.size(), watch.getTime());
        }
    }

}
