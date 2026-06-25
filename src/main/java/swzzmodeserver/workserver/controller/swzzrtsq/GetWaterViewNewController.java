package swzzmodeserver.workserver.controller.swzzrtsq;

//import Equivalent.MyWaterDZXUtils;
//import Equivalent.ObjUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import swzzmodeserver.workserver.data.swzzmode.BDMS_PREDICTData;
import swzzmodeserver.workserver.data.swzzmode.DD_SOLUTIONData;
import swzzmodeserver.workserver.data.swzzrtsq.*;
//import swzzmodeserver.workserver.pojo.DENGZHIXIANYQPojo;
import swzzmodeserver.workserver.pojo.swzzmode.BDMS_PREDICTPojo;
import swzzmodeserver.workserver.pojo.swzzmode.DD_SOLUTIONPojo;
import swzzmodeserver.workserver.pojo.swzzrtsq.*;
import swzzmodeserver.workserver.server.swzzrtsq.GetWaterByGateServer;
import swzzmodeserver.workserver.server.swzzrtsq.GetWaterViewNewServer;
import swzzmodeserver.tools.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import swzzmodeserver.workserver.server.swzzrtsq.TongbuServer;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/GetWaterViewNew")
public class GetWaterViewNewController {
    @Autowired
    private GetWaterViewNewData data;
    @Autowired
    private GetWaterViewNewBXData databx;
    @Autowired
    private ST_STBPRP_B_QUData quData;
    @Autowired
    private GetWaterViewNewServer server;
    @Autowired
    private RTSQST_STBPRP_BData stbprpBData;
    @Autowired
    private RTSQST_RVFCCH_BData rvfcchBData;
    @Autowired
    private RTSQST_PPTN_RData dataRain;
    @Autowired
    private ST_WDPSTAT_RData dataLL;
    @Autowired
    private GetWaterByGateServer dataWaterGateServer;

    @Autowired
    private TongbuServer tongbuServer;

    @Autowired
    private DD_SOLUTIONData ddSolutionData;

    @Autowired
    private BDMS_PREDICTData bdmsPredictData;

    @Autowired
    private RTSQST_STBPRP_B_STCDData stbprpBStcdData;

    // 文件存储路径
    @Value("${file.path.templatefilepath}")
    private String templatefilepath;

    @RequestMapping("/selectListByNew")
    public List<ST_STBPRP_B_QUDto> selectListByNew(@RequestBody ColumnName param) {
        Date date = new Date(new Date().getTime() - 24 * 60 * 60 * 1000);
        String PID = "", stime = DateUtil.dateFormat(date, "yyyy-MM-dd HH:mm:ss"), etime = "", ADMAUTH = "", STNM = "";
        if (null != param.getPid()) {
            PID = param.getPid();
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
        List<ST_STBPRP_B_QUPojo> quList = quData.selectList("", STNM, null, PID, ADMAUTH);
        List<String> stcdList = new ArrayList<>();
        if (null != quList && quList.size() > 0) {
            for (ST_STBPRP_B_QUPojo quPojo : quList) {
                if (null != quPojo.getSTCD()) {
                    stcdList.add(quPojo.getSTCD());
                }
            }
        }
        if (!(stcdList.size() > 0)) {
            return new ArrayList<>();
        }

        // 警戒、保证水位
        List<ST_RVFCCH_BPojo> rvfcchBList = rvfcchBData.selectList(null);

        // List<GetWaterViewNewPojo> waterList = data.selectListByNew(stcdList, stime,
        // etime);
        List<GetWaterViewNewPojo> waterList = server.selectListByNew(stcdList, stime, etime, ADMAUTH);

        waterList = waterList.stream().filter(i -> i.getUPZ() != null).collect(Collectors.toList());

        List<ST_STBPRP_BPojo> stStbprpBList = stbprpBData.selectList(stcdList, null);
        List<ST_STBPRP_B_QUDto> quDtoList = new ArrayList<>();
        for (ST_STBPRP_B_QUPojo quPojo : quList) {
            ST_STBPRP_B_QUDto quDto = new ST_STBPRP_B_QUDto();
            BeanUtils.copyProperties(quPojo, quDto);
            List<GetWaterViewNewPojo> collect = waterList.stream().filter(i -> {
                return i.getSTCD().equals(quDto.getSTCD());
            }).collect(Collectors.toList());
            if (collect.size() > 0) {
                GetWaterViewNewPojo waterViewNewPojo = collect.get(0);
                if (null != waterViewNewPojo.getUPZ()) {
                    quDto.setUPZ(waterViewNewPojo.getUPZ());
                }
                if (null != waterViewNewPojo.getDWZ()) {
                    quDto.setDWZ(waterViewNewPojo.getDWZ());
                }
                if (null != waterViewNewPojo.getWRZ()) {
                    quDto.setWRZ(waterViewNewPojo.getWRZ());
                }
                if (null != waterViewNewPojo.getGRZ()) {
                    quDto.setGRZ(waterViewNewPojo.getGRZ());
                }
                if (null != waterViewNewPojo.getQ()) {
                    quDto.setQ(Double.valueOf(waterViewNewPojo.getQ()));
                }
                if (null != waterViewNewPojo.getTM()) {
                    quDto.setTM(waterViewNewPojo.getTM());
                }
            }
            List<ST_STBPRP_BPojo> collects = stStbprpBList.stream().filter(i -> i.getSTCD().equals(quDto.getSTCD()))
                    .collect(Collectors.toList());
            if (collects.size() > 0) {
                ST_STBPRP_BPojo stStbprpBPojo = collects.get(0);
                if (null != stStbprpBPojo.getADMAUTH()) {
                    quDto.setADMAUTH(stStbprpBPojo.getADMAUTH());
                }
                if (null != stStbprpBPojo.getLGTD()) {
                    quDto.setLGTD(stStbprpBPojo.getLGTD());
                }
                if (null != stStbprpBPojo.getLTTD()) {
                    quDto.setLTTD(stStbprpBPojo.getLTTD());
                }
                if (null != stStbprpBPojo.getRVNM()) {
                    quDto.setRVNM(stStbprpBPojo.getRVNM());
                }
                if (null != stStbprpBPojo.getBSNM()) {
                    quDto.setBSNM(stStbprpBPojo.getBSNM());
                }
                if (null != stStbprpBPojo.getHNNM()) {
                    quDto.setHNNM(stStbprpBPojo.getHNNM());
                }

                // 警戒、保证水位
                List<ST_RVFCCH_BPojo> collect1 = rvfcchBList.stream().filter(p -> p.getSTCD().equals(quDto.getSTCD()))
                        .collect(Collectors.toList());
                if (collect1.size() > 0) {
                    ST_RVFCCH_BPojo modelRvfcch = collect1.get(0);
                    // if (null != modelRvfcch.getSFQ()) {
                    if (null != modelRvfcch.getWRZ()) {
                        quDto.setWRZ(modelRvfcch.getWRZ().toString());
                    }
                    if (null != modelRvfcch.getGRZ()) {
                        quDto.setGRZ(modelRvfcch.getGRZ().toString());
                    }
                    // }
                }
            }
            quDtoList.add(quDto);
        }
        return quDtoList;
    }

    @RequestMapping("/selectListByHis")
    public ResultUtils selectListByHisIsTime(@RequestBody ColumnName param) {
        StopWatch watch = new StopWatch();
        watch.start();
        Date date = new Date(new Date().getTime() - 24 * 60 * 60 * 1000);
        String stime = DateUtil.dateFormat(date, "yyyy-MM-dd HH:mm:ss"),
                etime = DateUtil.dateFormat(new Date(), "yyyy-MM-dd HH:mm:ss"), id = "Minute";
        List<String> stcdList = new ArrayList<>();
        String mtype = "";
        if (null != param.getPathname()) {
            id = param.getPathname();
        }
        if (null != param.getStcd()) {
            stcdList = Arrays.asList(param.getStcd().split(","));
        }
        if (null != param.getStime()) {
            stime = param.getStime();
        }
        if (null != param.getEtime()) {
            etime = param.getEtime();
        }
        if (null != param.getDatasource()) {
            mtype = param.getDatasource();
        }
        List<GetWaterViewNewPojo> list = new ArrayList<>();
        if ("HOUR".equals(id)) {
            list = server.selectListByHisIsHouse(stcdList, stime, etime, mtype);
        } else if ("DAY".equals(id)) {
            list = server.selectListByHisIsDay(stcdList, stime, etime, mtype);
        } else {
            list = server.selectListByHisIsTime(stcdList, stime, etime, mtype);
        }
        watch.stop();
        if (list.size() > 0) {
            return new ResultUtils<>(list, "操作成功", true, list.size(), watch.getTime());
        } else {
            return new ResultUtils<>(list, "操作成功", false, list.size(), watch.getTime());
        }
    }

    @RequestMapping("/getWaterListById")
    public List<GetWaterViewNewPojo> getWaterListById(@RequestBody ColumnName param) {
        Date date = new Date(new Date().getTime() - 24 * 60 * 60 * 1000);
        String stime = DateUtil.dateFormat(date, "yyyy-MM-dd HH:mm:ss"),
                etime = DateUtil.dateFormat(new Date(), "yyyy-MM-dd HH:mm:ss"), id = "Minute";
        List<String> stcdList = new ArrayList<>();
        if (null != param.getPathname()) {
            id = param.getPathname();
        }
        if (null != param.getStcd()) {
            stcdList = Arrays.asList(param.getStcd().split(","));
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
        List<GetWaterViewNewPojo> list = new ArrayList<>();
        if ("Minute".equals(id)) {
            list = server.selectListByHisIsTime(stcdList, stime, etime, mtype);
        } else if ("HOUR".equals(id)) {
            list = server.selectListByHisIsHouse(stcdList, stime, etime, mtype);
        } else if ("DAY".equals(id)) {
            list = server.selectListByHisIsDay(stcdList, stime, etime, mtype);
        }

        List<GetWaterViewNewPojo> listN = new ArrayList<>();
        if (null != list && list.size() > 0) {
            for (GetWaterViewNewPojo mode : list) {
                GetWaterViewNewPojo item = new GetWaterViewNewPojo();
                item.setSTCD(mode.getSTCD());
                item.setTM(mode.getTM());
                item.setUPZ(mode.getUPZ());
                item.setWRZ(mode.getWRZ());
                item.setGRZ(mode.getGRZ());
                listN.add(item);
            }
        }

        return listN;
    }

    @RequestMapping("/selectOne")
    public List<GetWaterViewNewPojo> selectOne(@RequestBody ColumnName param) {
        Date date = new Date(new Date().getTime() - 24 * 60 * 60 * 1000);
        String stime = DateUtil.dateFormat(date, "yyyy-MM-dd HH:mm:ss"), etime = "";
        List<String> stcdList = new ArrayList<>();
        if (null != param.getStcd()) {
            stcdList = Arrays.asList(param.getStcd().split(","));
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
        return server.selectListByHisIsTime(stcdList, stime, etime, mtype);
    }

    // @RequestMapping("/selectByKS")
    // public KS_WaterPojo selectByKS(@RequestBody ColumnName param){
    // Date date = new Date(new Date().getTime() - 24 * 60 * 60 * 1000);
    // String stime=DateUtil.dateFormat(date,"yyyy-MM-dd HH:mm:ss"),etime="";
    // List<String> stcdList = new ArrayList<>();
    // if(null != param.getStcd()){
    // stcdList = Arrays.asList(param.getStcd().split(","));
    // }
    // if(null != param.getEtime()){
    // etime = param.getEtime();
    // try {
    // Long time = new SimpleDateFormat("yyyy-MM-dd
    // HH:mm:ss").parse(etime).getTime() - 24 * 60 * 60 * 1000;
    // stime = DateUtil.dateFormat(new Date(time),"yyyy-MM-dd HH:mm:ss");
    // } catch (ParseException e) {
    // e.printStackTrace();
    // }
    // }
    // String mtype="";
    // if(!CommonUtills.isEmpty(param.getDatasource())){
    // mtype = param.getDatasource();
    // }
    // KS_WaterPojo ksWaterPojo = new KS_WaterPojo();
    // List<GetWaterViewNewPojo> viewNewList = server.selectListByHisIsDay(stcdList,
    // stime, etime,mtype);
    // if(null != viewNewList && viewNewList.size() > 0){
    // ksWaterPojo.setYEUPZ(viewNewList.get(0).getUPZ());
    // ksWaterPojo.setTOUPZ(viewNewList.get(viewNewList.size()-1).getUPZ());
    // ksWaterPojo.setWRZ(viewNewList.get(0).getWRZ());
    // ksWaterPojo.setGRZ(viewNewList.get(0).getGRZ());
    // }
    // return ksWaterPojo;
    // }

    @RequestMapping("/selectListNewAndLength")
    public ResultUtils selectListNewAndLength(@RequestBody ColumnName param) {
        StopWatch watch = new StopWatch();
        watch.start();
        Date date = new Date(new Date().getTime() - 24 * 60 * 60 * 1000);
        String PID = "", stime = DateUtil.dateFormat(date, "yyyy-MM-dd HH:mm:ss"), etime = "";
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
        List<GetWaterViewNewPojo> treeList = server.selectListNewAndLength(PID, stime, etime, mtype);
        watch.stop();
        if (treeList.size() > 0) {
            return new ResultUtils<>(treeList, "操作成功", true, treeList.size(), watch.getTime());
        } else {
            return new ResultUtils<>(treeList, "操作成功", false, treeList.size(), watch.getTime());
        }
    }

    // @RequestMapping(value = "/dengzhixianWater",method = RequestMethod.POST)
    // @ResponseBody
    // public List<Map<String,Object>> dengzhixianWater(@RequestBody
    // DENGZHIXIANYQPojo param){
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
    // String filename = "water_dzx"+UUID.randomUUID().toString()+".png";
    // MyWaterDZXUtils dzxUtils=new MyWaterDZXUtils();
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

    /**
     * 获取生成昆山每日08时短信数据
     **/
    @RequestMapping("/selectListByMsg")
    public List<Map<String, Object>> selectListByMsg(@RequestBody ColumnName param) {
        Date date = new Date(new Date().getTime() - 24 * 60 * 60 * 1000);
        String PID = "";
        String stime = DateUtil.dateFormat(date, "yyyy-MM-dd HH:00:00");
        String etime = DateUtil.dateFormat(new Date(), "yyyy-MM-dd HH:00:00");
        List<String> stcdList = new ArrayList<>();
        List<ST_STBPRP_B_QUPojo> quListWater = new ArrayList<>();
        if (null != param.getPid()) {
            PID = param.getPid();
            // 查询配置的站码
            quListWater = quData.selectList("", "", null, PID, "");
            stcdList = new ArrayList<>();
            if (null != quListWater && quListWater.size() > 0) {
                for (ST_STBPRP_B_QUPojo quPojo : quListWater) {
                    if (null != quPojo.getSTCD()) {
                        stcdList.add(quPojo.getSTCD());
                    }
                }
            }
        }
        if (null != param.getStime()) {
            stime = param.getStime();
        }
        if (null != param.getEtime()) {
            etime = param.getEtime();
        }
        if (null != param.getStcd()) {
            stcdList = Arrays.asList(param.getStcd().split(","));
        }
        String mtype = "";
        if (!CommonUtills.isEmpty(param.getDatasource())) {
            mtype = param.getDatasource();
        }
        List<GetWaterViewNewPojo> list = new ArrayList<>();
        list = server.selectListByHisIsTime(stcdList, stime, etime, mtype);// 日8点数据，会出现缺失，使用前一条记录代替 selectListByHisIsDay

        Integer n = 0;
        List<Map<String, Object>> mlist = new ArrayList<>();
        StringBuffer strMsg = new StringBuffer();
        strMsg.append("【今日8时】");
        for (ST_STBPRP_B_QUPojo quPojo : quListWater) {
            String _time = etime;
            // List<GetWaterViewNewPojo> mWaterList = list.stream().filter(p ->
            // p.getSTCD().equals(quPojo.getSTCD()) &&
            // _time.equals(p.getTM())).collect(Collectors.toList());
            List<GetWaterViewNewPojo> mWaterList = list.stream().filter(p -> p.getSTCD().equals(quPojo.getSTCD()))
                    .sorted(Comparator.comparing(GetWaterViewNewPojo::getTM).reversed()).collect(Collectors.toList());
            strMsg.append(quPojo.getSTNM());
            if (null != mWaterList && mWaterList.size() > 0) {
                double z = (new BigDecimal(mWaterList.get(0).getUPZ()).setScale(2, BigDecimal.ROUND_HALF_UP))
                        .doubleValue();
                DecimalFormat decimalFormat = new DecimalFormat("0.00#");
                strMsg.append((decimalFormat.format(z)) + "m");
                // System.out.println(quPojo.getSTNM() +" ： "+ mWaterList.get(0).getTM()+ " = "
                // + (decimalFormat.format(z))+"m");
                // double z =
                // Double.parseDouble(String.format("%.2f",(mWaterList.get(0).getUPZ())));
                // DecimalFormat decimalFormat = new DecimalFormat("0.00#");
                // strMsg.append((decimalFormat.format(z))+"m");
            } else {
                strMsg.append("—m");
            }
            n++;
            if (n == quListWater.size()) {
                strMsg.append("。");
            } else {
                strMsg.append("；");
            }
        }
        // for(GetWaterViewNewPojo item : list) {
        // if(etime.equals(item.getTM())){
        //
        // List<ST_STBPRP_B_QUPojo> mWaterListQu = quListWater.stream().filter(p ->
        // p.getSTCD().equals(item.getSTCD())).collect(Collectors.toList());
        // strMsg.append(mWaterListQu.get(0).getSTNM()+""+Double.parseDouble(String.format("%.2f",Double.valueOf(item.getUPZ())))+"m");
        // strMsg.append("；");
        //// String _day = stime.toString();
        //// List<GetWaterViewNewPojo> mWaterListNew = list.stream().filter(p ->
        // p.getSTCD().equals(item.getSTCD()) &&
        // p.getTM().equals(_day)).collect(Collectors.toList());
        //// if(null != mWaterListNew && mWaterListNew.size() > 0) {
        //// GetWaterViewNewPojo model = mWaterListNew.get(0);
        //// Double cha = 0.00;
        //// if(Double.valueOf(item.getUPZ()) > Double.valueOf(model.getUPZ())){
        //// cha = Double.valueOf(item.getUPZ()) - Double.valueOf(model.getUPZ());
        //// strMsg.append("，涨" + (Double.parseDouble(
        // String.format("%.2f",cha)))+"m；");
        //// }else if(Double.valueOf(item.getUPZ()) < Double.valueOf(model.getUPZ())){
        //// cha = Double.valueOf(model.getUPZ()) - Double.valueOf(item.getUPZ());
        //// strMsg.append("，落" + (Double.parseDouble(
        // String.format("%.2f",cha)))+"m；");
        //// }else{
        //// strMsg.append("，平0.00m；");
        //// }
        //// }else{
        //// strMsg.append("；");
        //// }
        // }
        // }
        Map<String, Object> map = new HashMap<>();

        String _stimeRain = "", _etimeRain = "";
        try {
            Long time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(etime).getTime();
            _stimeRain = DateUtil.dateFormat(new Date(new Date(time).getTime() - 24 * 60 * 60 * 1000),
                    "yyyy-MM-dd 08:05:00");
            _etimeRain = DateUtil.dateFormat(new Date(time), "yyyy-MM-dd 08:00:00");
        } catch (Exception ex) {
        }

        StringBuffer strRainMsg = new StringBuffer();
        // 查询配置的站码
        List<ST_STBPRP_B_QUPojo> quList = quData.selectList("", "", null, "201901101419326076", "");
        stcdList = new ArrayList<>();
        if (null != quList && quList.size() > 0) {
            for (ST_STBPRP_B_QUPojo quPojo : quList) {
                if (null != quPojo.getSTCD()) {
                    stcdList.add(quPojo.getSTCD());
                }
            }
        }
        // 查询累计降雨
        List<ST_PPTN_RPojo> sprlist = dataRain.selectListIsDay(stcdList, _stimeRain, _etimeRain);
        // 查询基础站码信息
        List<ST_STBPRP_BPojo> stStbprpBPList = stbprpBData.selectList(stcdList, null);
        List<ST_PPTN_RPojo> mList = new ArrayList<>();
        Double SumDRP = 0.0;
        if (stcdList.size() > 0) {
            for (String item : stcdList) {
                List<ST_STBPRP_BPojo> collect = stStbprpBPList.stream().filter(i -> i.getSTCD().equals(item))
                        .collect(Collectors.toList());
                if (collect.size() > 0) {
                    ST_PPTN_RPojo model = new ST_PPTN_RPojo();

                    ST_STBPRP_BPojo stbprpBPojo = collect.get(0);
                    model.setSTCD(item);
                    model.setSTNM(stbprpBPojo.getSTNM());
                    List<ST_PPTN_RPojo> drpList = sprlist.stream().filter(i -> i.getSTCD().equals(item))
                            .collect(Collectors.toList());
                    if (drpList.size() > 0) {
                        model.setDRP(Double.parseDouble(String.format("%.1f", drpList.get(0).getDRP())));
                        mList.add(model);
                        SumDRP += drpList.get(0).getDRP();
                    }
                }
            }
        }
        strRainMsg.append("【昨日雨情】");
        if (null != mList && mList.size() > 0) {
            mList.sort((a, b) -> {
                if (a.getDRP() != null && b.getDRP() != null) {
                    return (int) (b.getDRP() * 1000 - a.getDRP() * 1000);
                }
                return -1;
            });

            if (SumDRP > 0) {
                List<ST_PPTN_RPojo> mListJBXY = mList.stream().filter(i -> i.getDRP() < 10)
                        .collect(Collectors.toList());
                List<ST_PPTN_RPojo> mListJBZY = mList.stream().filter(i -> i.getDRP() < 25 && i.getDRP() >= 10)
                        .collect(Collectors.toList());
                List<ST_PPTN_RPojo> mListJBDY = mList.stream().filter(i -> i.getDRP() < 50 && i.getDRP() >= 25)
                        .collect(Collectors.toList());
                List<ST_PPTN_RPojo> mListJBBY = mList.stream().filter(i -> i.getDRP() < 100 && i.getDRP() >= 50)
                        .collect(Collectors.toList());
                List<ST_PPTN_RPojo> mListJBDBY = mList.stream().filter(i -> i.getDRP() < 250 && i.getDRP() >= 100)
                        .collect(Collectors.toList());
                List<ST_PPTN_RPojo> mListJBTDBY = mList.stream().filter(i -> i.getDRP() >= 250)
                        .collect(Collectors.toList());
                // System.out.println(" mListJBXY.size() = " + mListJBXY.size() + " SumDRP = " +
                // SumDRP);
                // System.out.println(" mListJBZY.size() = " + mListJBZY.size() + " SumDRP = " +
                // SumDRP);
                // System.out.println(" mListJBDY.size() = " + mListJBDY.size() + " SumDRP = " +
                // SumDRP);
                // System.out.println(" mListJBBY.size() = " + mListJBBY.size() + " SumDRP = " +
                // SumDRP);
                // System.out.println(" mListJBDBY.size() = " + mListJBDBY.size() + " SumDRP = "
                // + SumDRP);
                // System.out.println(" mListJBTDBY.size() = " + mListJBTDBY.size() + " SumDRP =
                // " + SumDRP);

                if (null != mListJBTDBY
                        && ((mListJBTDBY.size() < 10 && mListJBTDBY.size() > 0) || mListJBTDBY.size() >= 10)) {
                    Double sumDrp = SumDRP / mList.size();
                    Double drp = mList.get(0).getDRP();
                    strRainMsg.append("全市局部大暴雨到特大暴雨，");
                    strRainMsg.append("面平均雨量" + String.format("%.1f", sumDrp) + "mm，");
                } else if (null != mListJBDBY
                        && ((mListJBDBY.size() < 10 && mListJBDBY.size() > 0) || mListJBDBY.size() > 0)) {
                    Double sumDrp = SumDRP / mList.size();
                    Double drp = mList.get(0).getDRP();
                    strRainMsg.append("全市局部暴雨到大暴雨，");
                    strRainMsg.append("面平均雨量" + String.format("%.1f", sumDrp) + "mm，");
                } else if (null != mListJBBY
                        && ((mListJBBY.size() < 10 && mListJBBY.size() > 0) || mListJBBY.size() >= 10)) {
                    Double sumDrp = SumDRP / mList.size();
                    Double drp = mList.get(0).getDRP();
                    strRainMsg.append("全市局部大雨到暴雨，");
                    strRainMsg.append("面平均雨量" + String.format("%.1f", sumDrp) + "mm，");
                } else if (null != mListJBDY
                        && ((mListJBDY.size() < 10 && mListJBDY.size() > 0) || mListJBDY.size() >= 10)) {
                    Double sumDrp = SumDRP / mList.size();
                    Double drp = mList.get(0).getDRP();
                    strRainMsg.append("全市局部中雨到大雨，");
                    strRainMsg.append("面平均雨量" + String.format("%.1f", sumDrp) + "mm，");
                } else if (null != mListJBZY
                        && ((mListJBZY.size() < 10 && mListJBZY.size() > 0) || mListJBZY.size() >= 10)) {
                    Double sumDrp = SumDRP / mList.size();
                    Double drp = mList.get(0).getDRP();
                    strRainMsg.append("全市局部小雨到中雨，");
                    strRainMsg.append("面平均雨量" + String.format("%.1f", sumDrp) + "mm，");
                } else if (null != mListJBXY && ((mListJBXY.size() < 10) || mListJBXY.size() > 10)) {
                    strRainMsg.append("全市局部小雨，");
                    // Double sumDrp = SumDRP / mList.size();
                    // Double drp = mList.get(0).getDRP();
                    // strRainMsg.append("面平均雨量"+String.format("%.1f",sumDrp)+"mm，");
                } else {
                    Double sumDrp = SumDRP / mList.size();
                    Double drp = mList.get(0).getDRP();
                    if (drp > 0 && drp < 10) {
                        strRainMsg.append("全市出现小雨，");
                    } else if (drp >= 10 && drp < 25) {
                        strRainMsg.append("全市出现中雨，");
                    } else if (drp >= 25 && drp < 50) {
                        strRainMsg.append("全市出现大雨，");
                    } else if (drp >= 50 && drp < 100) {
                        strRainMsg.append("全市出现暴雨，");
                    } else if (drp >= 100 && drp < 250) {
                        strRainMsg.append("全市出现大暴雨，");
                    } else if (drp >= 250) {
                        strRainMsg.append("全市出现特大暴雨，");
                    }
                    strRainMsg.append("面平均雨量" + String.format("%.1f", sumDrp) + "mm，");
                }
                strRainMsg.append("最大点雨量" + mList.get(0).getSTNM() + mList.get(0).getDRP() + "mm。");
            } else {
                strRainMsg.append("全市无降雨。");
            }
        } else {
            strRainMsg.append("缺少降雨数据。");
        }
        if (!ObjUtils.isEmpty(strRainMsg.toString())) {
            map.put("rainMsg", strRainMsg.toString());
            map.put("rainList", mList);
        }

        StringBuffer strWdpstatMsg = new StringBuffer();
        // 查询配置的站码
        List<ST_STBPRP_B_QUPojo> quListLL = quData.selectList("", "", null, "2023063023122647790", "");
        stcdList = new ArrayList<>();
        if (null != quListLL && quListLL.size() > 0) {
            for (ST_STBPRP_B_QUPojo quPojo : quListLL) {
                if (null != quPojo.getSTCD()) {
                    stcdList.add(quPojo.getSTCD());
                }
            }
        }
        List<ST_WDPSTAT_RPojo> mListLL = dataLL.selectList(stcdList, stime, etime);

        strWdpstatMsg.append("【昨日累计水量】");
        n = 0;
        for (ST_STBPRP_B_QUPojo quPojo : quListLL) {
            String _time = etime;
            List<ST_WDPSTAT_RPojo> mWdpstatList = mListLL.stream()
                    .filter(p -> p.getSTCD().equals(quPojo.getSTCD()) && _time.equals(p.getIDTM()))
                    .collect(Collectors.toList());
            strWdpstatMsg.append(quPojo.getSTNM());
            if (null != mWdpstatList && mWdpstatList.size() > 0) {
                String str = "";

                if ((null == mWdpstatList.get(0).getACCPW() && null == mWdpstatList.get(0).getACCDW())) {
                    strWdpstatMsg.append("关闸");
                } else {
                    if ((null != mWdpstatList.get(0).getACCPW() && mWdpstatList.get(0).getACCPW() <= 0)
                            && (null != mWdpstatList.get(0).getACCDW() && mWdpstatList.get(0).getACCDW() <= 0)) {
                        strWdpstatMsg.append("关闸");
                    } else {
                        if (null != mWdpstatList.get(0).getACCPW() && mWdpstatList.get(0).getACCPW() > 0) {
                            Double accpw = mWdpstatList.get(0).getACCPW();
                            if (accpw.intValue() - accpw == 0) {
                                str = "引水" + String.valueOf(accpw.intValue()) + "万m3";
                            } else {
                                str = "引水" + Double.parseDouble(String.format("%.1f", (accpw))) + "万m3";
                            }
                        }
                        strWdpstatMsg.append(str);

                        if (null != mWdpstatList.get(0).getACCDW() && mWdpstatList.get(0).getACCDW() > 0) {
                            if (!str.equals(""))
                                strWdpstatMsg.append("，");

                            Double accdw = mWdpstatList.get(0).getACCDW();
                            if (accdw.intValue() - accdw == 0) {
                                str = "排水" + String.valueOf(accdw.intValue()) + "万m3";
                            } else {
                                str = "排水" + Double.parseDouble(String.format("%.1f", (accdw))) + "万m3";
                            }
                            strWdpstatMsg.append(str);
                        }

                        if (str == "") {
                            strWdpstatMsg.append("—m3");
                        }
                    }
                }
            } else {
                strWdpstatMsg.append("—m3");
            }
            n++;
            if (n == quListLL.size()) {
                strWdpstatMsg.append("。");
            } else {
                strWdpstatMsg.append("；");
            }
        }
        if (!ObjUtils.isEmpty(strWdpstatMsg.toString())) {
            map.put("wdpstatMsg", strWdpstatMsg.toString());
            map.put("wdpstatList", mListLL);
        }

        Double czUpz = 0.03;
        // 昨日开关闸情况
        List<Map<String, Object>> mGateList = dataWaterGateServer.selectListByGate("HOUR", "2024030816580613583", stime,
                etime, czUpz);
        StringBuffer strGateMsg = new StringBuffer();
        StringBuffer strGateMsgNew = new StringBuffer();
        if (null != mGateList && mGateList.size() > 0) {
            String etimeGate = etime;
            mGateList.forEach((res) -> {
                Long sumHour = Long.valueOf(res.get("sumHour").toString());
                if (strGateMsg.toString().length() > 0) {
                    strGateMsg.append("，");
                }
                strGateMsg.append(res.get("title"));
                strGateMsg.append(sumHour > 0.0 ? "开闸约" + sumHour + "小时" : "关闸");

                if (strGateMsgNew.toString().length() > 0) {
                    strGateMsgNew.append("，");
                }
                strGateMsgNew.append(res.get("title"));
                String waterListRes = JSON.toJSONString(res.get("waterList"));
                // List<GetWaterViewNewPojo> waterList = new
                // JSONMapper().readValue(waterListRes,new
                // TypeReference<List<GetWaterViewNewPojo>>(){});
                List<GetWaterViewNewPojo> waterList = JSONObject.parseArray(waterListRes, GetWaterViewNewPojo.class);

                if (null != waterList && waterList.size() > 0) {
                    // System.out.println(" waterList === " + waterList);
                    System.out.println(" etimeGate === " + etimeGate);
                    List<GetWaterViewNewPojo> waterListNew = waterList.stream().filter(u -> u.getTM().equals(etimeGate))
                            .collect(Collectors.toList());
                    System.out.println(" waterListNew === " + waterListNew);
                    if (waterListNew.size() > 0) {
                        GetWaterViewNewPojo waterViewNew = waterListNew.get(0);
                        String upz = waterViewNew.getUPZ();
                        String dwz = waterViewNew.getDWZ();
                        if ("" != upz && "" != dwz) {
                            Double zMax = czUpz, zMin = czUpz * -1;
                            Double cz = Double
                                    .parseDouble(String.format("%.2f", (Double.valueOf(upz) - Double.valueOf(dwz))));
                            if ((cz <= zMax && cz >= zMin)) {
                                strGateMsgNew.append("开闸");
                            } else if (cz >= zMax || cz <= zMin) {
                                strGateMsgNew.append("关闸");
                            }
                        } else {
                            strGateMsgNew.append("缺测");
                        }
                    } else {
                        strGateMsgNew.append("缺测");
                    }
                } else {
                    strGateMsgNew.append("缺测");
                }
            });
            strGateMsg.append("。");
            if (strGateMsgNew.toString() != "") {
                strGateMsgNew.append("。");
            }
        }
        map.put("gateMsg", "【昨日开关闸情况】" + strGateMsg.toString());

        if (!ObjUtils.isEmpty(strMsg.toString())) {
            map.put("waterMsg", strMsg.toString() + "" + strGateMsgNew.toString());
            map.put("waterList", list);
        }

        mlist.add(map);

        return mlist;
    }

    @RequestMapping("/selectListByGate")
    public List<Map<String, Object>> selectListByGate(@RequestBody ColumnName param) {
        Date date = new Date(new Date().getTime() - 24 * 60 * 60 * 1000);
        String PID = "2024030816580613583";
        String stime = DateUtil.dateFormat(date, "yyyy-MM-dd HH:00:00");
        String etime = DateUtil.dateFormat(new Date(), "yyyy-MM-dd HH:00:00");
        List<String> stcdList = new ArrayList<>();
        List<ST_STBPRP_B_QUPojo> quListWater = new ArrayList<>();
        if (null != param.getPid()) {
            PID = param.getPid();
        }
        if (null != param.getStime()) {
            stime = param.getStime();
        }
        if (null != param.getEtime()) {
            etime = param.getEtime();
        }

        Double czUpz = 0.03;
        try {
            // 自定义参数
            Map<String, Object> maps = JsonToMapUtils.convertJsonToMap(param.getStrExp());
            if (null != maps) {
                String _czUpz = JsonToMapUtils.GetValueByKey(maps, "upz");
                if ("" != _czUpz) {
                    czUpz = Double.valueOf(_czUpz);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<Map<String, Object>> mList = new ArrayList<>();
        mList = dataWaterGateServer.selectListByGate("HOUR", PID, stime, etime, czUpz);
        if (null != mList && mList.size() > 0) {
            StringBuffer strGate = new StringBuffer();
            mList.forEach((res) -> {
                Long sumHour = Long.valueOf(res.get("sumHour").toString());
                if (strGate.toString().length() > 0) {
                    strGate.append("，");
                }
                strGate.append(res.get("title"));
                strGate.append(sumHour > 0.0 ? "开闸" + sumHour + "小时" : "关闸");
            });
            strGate.append("。");
        }

        return mList;
    }

    @RequestMapping("/queryBySWNew")
    public ResultUtils queryBySWNew(@RequestBody ColumnName param) {
        StopWatch watch = new StopWatch();
        watch.start();
        Date date = new Date(new Date().getTime() - 24 * 60 * 60 * 1000);
        String dayhour = "Minute", stime = DateUtil.dateFormat(date, "yyyy-MM-dd HH:mm:ss"), etime = "";
        List<String> listSTCD = new ArrayList<>();
        if (null != param.getPathname()) {
            dayhour = param.getPathname();
        }
        if (null != param.getStime()) {
            stime = param.getStime();
        }
        if (null != param.getEtime()) {
            etime = param.getEtime();
        }
        String mtype = "";
        if (null != param.getDatasource()) {
            mtype = param.getDatasource();
        }
        String pid = "";
        List<ST_STBPRP_B_QUPojo> quList = new ArrayList<>();
        if (null != param.getPid()) {
            // pid = param.getPid();
            List<String> idList = new ArrayList<>();
            idList = Arrays.asList(param.getPid().split(","));
            // idList.add(pid);
            quList = quData.queryList("", "", null, idList);
            listSTCD = quList.stream().map(s -> s.getSTCD()).distinct().collect(Collectors.toList());

        }
        // List<GetWaterViewNewPojo> userListRiver=data.selectListByHisIsTime(listSTCD,
        // stime, etime,dayhour,mtype);
        // List<GetWaterViewNewPojo> userListWas=databx.selectListByHisIsTime(listSTCD,
        // stime, etime,dayhour,mtype);

        List<GetWaterViewNewPojo> userListRiver = data.queryByRiver(listSTCD, stime, etime, dayhour, mtype);
        List<GetWaterViewNewPojo> userListWas = data.queryByWas(listSTCD, stime, etime, dayhour, mtype);
        List<GetWaterViewNewPojo> userListTide = data.queryByTide(listSTCD, stime, etime, dayhour, mtype);
        List<GetWaterViewNewPojo> userListPump = data.queryByPump(listSTCD, stime, etime, dayhour, mtype);
        List<GetWaterViewNewPojo> dataList = new ArrayList<>();
        List<ST_STBPRP_BPojo> stStbprpBList = stbprpBData.selectStbprpBList(listSTCD, null, null);

        if (quList.size() > 0) {
            for (int num = 0; num < quList.size(); num++) {
                ST_STBPRP_B_QUPojo onestbprpBQuList = quList.get(num);
                String stcd = onestbprpBQuList.getSTCD().toString();
                List<GetWaterViewNewPojo> oneRiverList = userListRiver.stream()
                        .filter(u -> u.getSTCD().equals(stcd) && onestbprpBQuList.getSTTP().equals(u.getMTYPE()))
                        .collect(Collectors.toList());
                List<GetWaterViewNewPojo> oneWasList = userListWas.stream()
                        .filter(u -> u.getSTCD().equals(stcd) && onestbprpBQuList.getSTTP().equals(u.getMTYPE()))
                        .collect(Collectors.toList());
                List<GetWaterViewNewPojo> oneTideList = userListTide.stream()
                        .filter(u -> u.getSTCD().equals(stcd) && onestbprpBQuList.getSTTP().equals(u.getMTYPE()))
                        .collect(Collectors.toList());
                List<GetWaterViewNewPojo> onePumpList = userListPump.stream()
                        .filter(u -> u.getSTCD().equals(stcd) && onestbprpBQuList.getSTTP().equals(u.getMTYPE()))
                        .collect(Collectors.toList());
                List<ST_STBPRP_BPojo> onestStbprpB = stStbprpBList.stream()
                        .filter(u -> u.getSTCD().equals(stcd) && onestbprpBQuList.getSTTP().equals(u.getMTYPE()))
                        .collect(Collectors.toList());
                List<ST_STBPRP_B_QUPojo> tempquList = quList.stream().filter(u -> u.getSTCD().equals(stcd))
                        .collect(Collectors.toList());
                List<GetWaterViewNewPojo> temponeRiverList = new ArrayList<>();
                List<GetWaterViewNewPojo> temponewasList = new ArrayList<>();
                temponeRiverList = oneRiverList.stream().filter(u -> u.getUPZ() != null)
                        .sorted(Comparator.comparing(GetWaterViewNewPojo::getTM).reversed())
                        .collect(Collectors.toList());
                // if(temponeRiverList.size()==0){
                // temponeRiverList=oneRiverList.stream().filter(u->u.getUPZ()!=null||u.getDWZ()!=null).sorted(Comparator.comparing(GetWaterViewNewPojo::getTM).reversed()).collect(Collectors.toList());
                // }
                temponewasList = oneWasList.stream().filter(u -> u.getUPZ() != null && u.getDWZ() != null)
                        .sorted(Comparator.comparing(GetWaterViewNewPojo::getTM).reversed())
                        .collect(Collectors.toList());
                if (temponewasList.size() == 0) {
                    temponewasList = oneWasList.stream().filter(u -> u.getUPZ() != null || u.getDWZ() != null)
                            .sorted(Comparator.comparing(GetWaterViewNewPojo::getTM).reversed())
                            .collect(Collectors.toList());
                    ;
                }
                // if(!CommonUtills.isEmpty(onestbprpBQuList.getSTTP())){
                // if(onestbprpBQuList.getSTTP().equals("YC")){
                // temponeRiverList=oneRiverList.stream().filter(u->Double.valueOf(u.getUPZ())>0&&Double.valueOf(u.getDWZ())>0).sorted(Comparator.comparing(GetWaterViewNewPojo::getTM).reversed()).collect(Collectors.toList());
                // if(temponeRiverList.size()==0){
                // temponeRiverList=oneRiverList.stream().filter(u->Double.valueOf(u.getUPZ())>0).sorted(Comparator.comparing(GetWaterViewNewPojo::getTM).reversed()).collect(Collectors.toList());
                // }
                // }
                // else{
                // temponewasList=oneWasList.stream().filter(u->Double.valueOf(u.getUPZ())>0&&Double.valueOf(u.getDWZ())>0).sorted(Comparator.comparing(GetWaterViewNewPojo::getTM).reversed()).collect(Collectors.toList());
                // if(temponewasList.size()==0){
                // temponewasList=oneWasList.stream().filter(u->Double.valueOf(u.getUPZ())>0).sorted(Comparator.comparing(GetWaterViewNewPojo::getTM).reversed()).collect(Collectors.toList());
                // }
                // }
                // }

                if (temponeRiverList.size() > 0) {
                    GetWaterViewNewPojo pojo = temponeRiverList.get(0);
                    if (tempquList.size() > 0) {
                        pojo.setOrderbyid(tempquList.get(0).getORDERBYID());
                        pojo.setSTNM(tempquList.get(0).getSTNM());
                    }
                    if (onestStbprpB.size() > 0) {
                        pojo.setADMAUTH(onestStbprpB.get(0).getADMAUTH());
                        pojo.setDTPR(onestStbprpB.get(0).getDTPR());
                        // if(!CommonUtills.isEmpty(pojo.getDTPR())){
                        // String _upz="";
                        // if(!CommonUtills.isEmpty(pojo.getUPZ())){
                        // _upz= String.valueOf(Double.parseDouble(pojo.getUPZ())+pojo.getDTPR());
                        // }
                        // pojo.setUPZ(_upz);
                        // }
                    }
                    List<ST_STBPRP_B_QUPojo> onequstcd = quList.stream()
                            .filter(o -> o.getSTCD().equals(pojo.getSTCD()) && o.getSTTP().equals(pojo.getMTYPE()))
                            .collect(Collectors.toList());
                    if (onequstcd.size() > 0) {
                        pojo.setDIR(onequstcd.get(0).getDIR());
                        pojo.setMAPSIZE(String.valueOf(onequstcd.get(0).getMAPSIZE()));
                        pojo.setROTATE(onequstcd.get(0).getROTATE());
                    }
                    dataList.add(pojo);
                } else if (temponewasList.size() > 0) {

                    GetWaterViewNewPojo pojo = temponewasList.get(0);
                    if (tempquList.size() > 0) {
                        pojo.setOrderbyid(tempquList.get(0).getORDERBYID());
                        pojo.setSTNM(tempquList.get(0).getSTNM());
                    }
                    if (onestStbprpB.size() > 0) {
                        pojo.setADMAUTH(onestStbprpB.get(0).getADMAUTH());
                        pojo.setDTPR(onestStbprpB.get(0).getDTPR());
                        // if(!CommonUtills.isEmpty(pojo.getDTPR())){
                        // String _upz="";
                        // if(!CommonUtills.isEmpty(pojo.getUPZ())){
                        // _upz= String.valueOf(Double.parseDouble(pojo.getUPZ())+pojo.getDTPR());
                        // }
                        // pojo.setUPZ(_upz);
                        // }
                    }
                    List<ST_STBPRP_B_QUPojo> onequstcd = quList.stream()
                            .filter(o -> o.getSTCD().equals(pojo.getSTCD()) && o.getSTTP().equals(pojo.getMTYPE()))
                            .collect(Collectors.toList());
                    if (onequstcd.size() > 0) {
                        pojo.setDIR(onequstcd.get(0).getDIR());
                        pojo.setMAPSIZE(String.valueOf(onequstcd.get(0).getMAPSIZE()));
                        pojo.setROTATE(onequstcd.get(0).getROTATE());
                    }
                    dataList.add(pojo);
                } else if (oneTideList.size() > 0) {
                    GetWaterViewNewPojo pojo = oneTideList.get(0);
                    if (tempquList.size() > 0) {
                        pojo.setOrderbyid(tempquList.get(0).getORDERBYID());
                        pojo.setSTNM(tempquList.get(0).getSTNM());
                    }
                    if (onestStbprpB.size() > 0) {
                        pojo.setADMAUTH(onestStbprpB.get(0).getADMAUTH());
                        pojo.setDTPR(onestStbprpB.get(0).getDTPR());
                        // if(!CommonUtills.isEmpty(pojo.getDTPR())){
                        // String _upz="";
                        // if(!CommonUtills.isEmpty(pojo.getUPZ())){
                        // _upz= String.valueOf(Double.parseDouble(pojo.getUPZ())+pojo.getDTPR());
                        // }
                        // pojo.setUPZ(_upz);
                        // }
                    }
                    List<ST_STBPRP_B_QUPojo> onequstcd = quList.stream()
                            .filter(o -> o.getSTCD().equals(pojo.getSTCD()) && o.getSTTP().equals(pojo.getMTYPE()))
                            .collect(Collectors.toList());
                    if (onequstcd.size() > 0) {
                        pojo.setDIR(onequstcd.get(0).getDIR());
                        pojo.setMAPSIZE(String.valueOf(onequstcd.get(0).getMAPSIZE()));
                        pojo.setROTATE(onequstcd.get(0).getROTATE());
                    }
                    dataList.add(pojo);
                } else if (onePumpList.size() > 0) {
                    GetWaterViewNewPojo pojo = onePumpList.get(0);
                    if (tempquList.size() > 0) {
                        pojo.setOrderbyid(tempquList.get(0).getORDERBYID());
                        pojo.setSTNM(tempquList.get(0).getSTNM());
                    }
                    if (onestStbprpB.size() > 0) {
                        pojo.setADMAUTH(onestStbprpB.get(0).getADMAUTH());
                        pojo.setDTPR(onestStbprpB.get(0).getDTPR());
                        // if(!CommonUtills.isEmpty(pojo.getDTPR())){
                        // String _upz="";
                        // if(!CommonUtills.isEmpty(pojo.getUPZ())){
                        // _upz= String.valueOf(Double.parseDouble(pojo.getUPZ())+pojo.getDTPR());
                        // }
                        // pojo.setUPZ(_upz);
                        // }
                    }

                    List<ST_STBPRP_B_QUPojo> onequstcd = quList.stream()
                            .filter(o -> o.getSTCD().equals(pojo.getSTCD()) && o.getSTTP().equals(pojo.getMTYPE()))
                            .collect(Collectors.toList());
                    if (onequstcd.size() > 0) {
                        pojo.setDIR(onequstcd.get(0).getDIR());
                        pojo.setMAPSIZE(String.valueOf(onequstcd.get(0).getMAPSIZE()));
                        pojo.setROTATE(onequstcd.get(0).getROTATE());
                    }
                    dataList.add(pojo);
                } else {
                    if (onestStbprpB.size() > 0) {
                        GetWaterViewNewPojo pojo = new GetWaterViewNewPojo();
                        pojo.setSTCD(onestStbprpB.get(0).getSTCD());
                        pojo.setSTNM(onestStbprpB.get(0).getSTNM());
                        pojo.setHNNM(onestStbprpB.get(0).getHNNM());
                        pojo.setRVNM(onestStbprpB.get(0).getRVNM());
                        pojo.setLGTD(String.valueOf(onestStbprpB.get(0).getLGTD()));
                        pojo.setLTTD(String.valueOf(onestStbprpB.get(0).getLTTD()));
                        pojo.setOrderbyid(quList.get(num).getORDERBYID());
                        pojo.setADMAUTH(onestStbprpB.get(0).getADMAUTH());
                        pojo.setMTYPE(onestStbprpB.get(0).getMTYPE());
                        pojo.setATCUNIT(onestStbprpB.get(0).getATCUNIT());

                        if (onestStbprpB.get(0).getWRZ() != null) {
                            pojo.setWRZ(onestStbprpB.get(0).getWRZ().toString());
                        }
                        if (onestStbprpB.get(0).getGRZ() != null) {
                            pojo.setGRZ(onestStbprpB.get(0).getGRZ().toString());
                        }

                        List<ST_STBPRP_B_QUPojo> onequstcd = quList.stream()
                                .filter(o -> o.getSTCD().equals(pojo.getSTCD()) && o.getSTTP().equals(pojo.getMTYPE()))
                                .collect(Collectors.toList());
                        if (onequstcd.size() > 0) {
                            pojo.setDIR(onequstcd.get(0).getDIR());
                            pojo.setMAPSIZE(String.valueOf(onequstcd.get(0).getMAPSIZE()));
                            pojo.setROTATE(String.valueOf(onequstcd.get(0).getROTATE()));
                        }
                        if (tempquList.size() > 0) {
                            pojo.setOrderbyid(tempquList.get(0).getORDERBYID());
                            pojo.setSTNM(tempquList.get(0).getSTNM());
                        }
                        dataList.add(pojo);
                    }
                }
            }
        }
        watch.stop();
        if (dataList.size() > 0) {
            return new ResultUtils<>(dataList, "操作成功", true, dataList.size(), watch.getTime());
        } else {
            return new ResultUtils<>(dataList, "操作成功", false, dataList.size(), watch.getTime());
        }
    }

    @RequestMapping("/queryBySWDanZhan")
    public List<GetWaterViewNewPojo> queryBySWDanZhan(@RequestBody ColumnName param) {
        Date date = new Date(new Date().getTime() - 24 * 60 * 60 * 1000);
        String dayhour = "Minute", stime = DateUtil.dateFormat(date, "yyyy-MM-dd HH:mm:ss"), etime = "";
        List<String> listSTCD = new ArrayList<>();
        if (null != param.getPathname()) {
            dayhour = param.getPathname();
        }
        if (null != param.getStime()) {
            stime = param.getStime();
        }
        if (null != param.getEtime()) {
            etime = param.getEtime();
        }
        if (null != param.getStcd()) {
            listSTCD = Arrays.asList(param.getStcd().split(","));
        }
        String mtype = "";
        if (null != param.getDatasource()) {
            mtype = param.getDatasource();
        }
        List<GetWaterViewNewPojo> userListRiver = data.queryByRiver(listSTCD, stime, etime, dayhour, mtype);
        List<GetWaterViewNewPojo> userListWas = data.queryByWas(listSTCD, stime, etime, dayhour, mtype);
        List<GetWaterViewNewPojo> userListTide = data.queryByTide(listSTCD, stime, etime, dayhour, mtype);
        List<GetWaterViewNewPojo> userListPump = data.queryByPump(listSTCD, stime, etime, dayhour, mtype);
        List<ST_STBPRP_BPojo> stStbprpBList = stbprpBData.selectStbprpBList(listSTCD, null, null);

        List<GetWaterViewNewPojo> dataList = new ArrayList<>();

        if (listSTCD.size() > 0) {
            for (int num = 0; num < listSTCD.size(); num++) {
                String stcd = listSTCD.get(num);
                List<GetWaterViewNewPojo> oneRiverList = userListRiver.stream().filter(u -> u.getSTCD().equals(stcd))
                        .collect(Collectors.toList());
                List<GetWaterViewNewPojo> oneWasList = userListWas.stream().filter(u -> u.getSTCD().equals(stcd))
                        .collect(Collectors.toList());
                List<GetWaterViewNewPojo> oneTideList = userListTide.stream().filter(u -> u.getSTCD().equals(stcd))
                        .collect(Collectors.toList());
                List<GetWaterViewNewPojo> onePumpList = userListPump.stream().filter(u -> u.getSTCD().equals(stcd))
                        .collect(Collectors.toList());
                String finalDataSource = mtype;
                if (userListRiver.size() > 0) {

                    oneRiverList.forEach(u -> {
                        GetWaterViewNewPojo pojo = u;
                        List<ST_STBPRP_BPojo> onestStbprpB = stStbprpBList.stream()
                                .filter(i -> i.getSTCD().equals(u.getSTCD()) && i.getMTYPE().equals(finalDataSource))
                                .collect(Collectors.toList());
                        if (onestStbprpB.size() > 0) {
                            pojo.setDTPR(onestStbprpB.get(0).getDTPR());
                            if (!CommonUtills.isEmpty(pojo.getDTPR())) {
                                String _upz = "";
                                if (!CommonUtills.isEmpty(pojo.getUPZ())) {
                                    _upz = String.valueOf(Double.parseDouble(pojo.getUPZ()) + pojo.getDTPR());
                                }
                                pojo.setUPZ(_upz);
                            }
                        }
                        dataList.add(pojo);
                    });

                }
                if (oneWasList.size() > 0) {
                    oneWasList.forEach(u -> {
                        GetWaterViewNewPojo pojo = u;
                        List<ST_STBPRP_BPojo> onestStbprpB = stStbprpBList.stream()
                                .filter(i -> i.getSTCD().equals(u.getSTCD()) && i.getMTYPE().equals(finalDataSource))
                                .collect(Collectors.toList());
                        if (onestStbprpB.size() > 0) {
                            pojo.setDTPR(onestStbprpB.get(0).getDTPR());
                            if (!CommonUtills.isEmpty(pojo.getDTPR())) {
                                String _upz = "";
                                if (!CommonUtills.isEmpty(pojo.getUPZ())) {
                                    _upz = String.valueOf(Double.parseDouble(pojo.getUPZ()) + pojo.getDTPR());
                                }
                                pojo.setUPZ(_upz);
                            }
                        }
                        dataList.add(pojo);
                    });
                }
                if (oneTideList.size() > 0) {
                    oneTideList.forEach(u -> {
                        GetWaterViewNewPojo pojo = u;
                        List<ST_STBPRP_BPojo> onestStbprpB = stStbprpBList.stream()
                                .filter(i -> i.getSTCD().equals(u.getSTCD()) && i.getMTYPE().equals(finalDataSource))
                                .collect(Collectors.toList());
                        if (onestStbprpB.size() > 0) {
                            pojo.setDTPR(onestStbprpB.get(0).getDTPR());
                            if (!CommonUtills.isEmpty(pojo.getDTPR())) {
                                String _upz = "";
                                if (!CommonUtills.isEmpty(pojo.getUPZ())) {
                                    _upz = String.valueOf(Double.parseDouble(pojo.getUPZ()) + pojo.getDTPR());
                                }
                                pojo.setUPZ(_upz);
                            }
                        }
                        dataList.add(pojo);
                    });
                }
                if (onePumpList.size() > 0) {
                    onePumpList.forEach(u -> {
                        GetWaterViewNewPojo pojo = u;
                        List<ST_STBPRP_BPojo> onestStbprpB = stStbprpBList.stream()
                                .filter(i -> i.getSTCD().equals(u.getSTCD()) && i.getMTYPE().equals(finalDataSource))
                                .collect(Collectors.toList());
                        if (onestStbprpB.size() > 0) {
                            pojo.setDTPR(onestStbprpB.get(0).getDTPR());
                            if (!CommonUtills.isEmpty(pojo.getDTPR())) {
                                String _upz = "";
                                if (!CommonUtills.isEmpty(pojo.getUPZ())) {
                                    _upz = String.valueOf(Double.parseDouble(pojo.getUPZ()) + pojo.getDTPR());
                                }
                                pojo.setUPZ(_upz);
                            }
                        }
                        dataList.add(pojo);
                    });
                }
            }
        }
        return dataList;
    }

    @RequestMapping("/queryBySWQNew")
    public List<GetWaterViewNewPojo> queryBySWQNew(@RequestBody ColumnName param) {
        Date date = new Date(new Date().getTime() - 24 * 60 * 60 * 1000);
        String dayhour = "Minute", stime = DateUtil.dateFormat(date, "yyyy-MM-dd HH:mm:ss"), etime = "";
        List<String> listSTCD = new ArrayList<>();
        if (null != param.getPathname()) {
            dayhour = param.getPathname();
        }
        if (null != param.getStime()) {
            stime = param.getStime();
        }
        if (null != param.getEtime()) {
            etime = param.getEtime();
        }
        String mtype = "";
        if (null != param.getDatasource()) {
            mtype = param.getDatasource();
        }
        String pid = "";
        List<ST_STBPRP_B_QUPojo> quList = new ArrayList<>();
        if (null != param.getPid()) {
            pid = param.getPid();
            quList = quData.selectList("", "", null, pid, "");
            listSTCD = quList.stream().map(s -> s.getSTCD()).distinct().collect(Collectors.toList());

        }
        List<GetWaterViewNewPojo> userListRiver = data.queryByRiver(listSTCD, stime, etime, dayhour, mtype);
        List<GetWaterViewNewPojo> userListWas = data.queryByWas(listSTCD, stime, etime, dayhour, mtype);
        List<GetWaterViewNewPojo> dataList = new ArrayList<>();
        List<ST_STBPRP_BPojo> stStbprpBList = stbprpBData.selectStbprpBList(listSTCD, null, null);

        if (quList.size() > 0) {
            for (int num = 0; num < quList.size(); num++) {
                String stcd = quList.get(num).getSTCD().toString();
                List<GetWaterViewNewPojo> oneRiverList = userListRiver.stream()
                        .filter(u -> u.getSTCD().equals(stcd) && !CommonUtills.isEmpty(u.getQ()))
                        .collect(Collectors.toList());
                List<GetWaterViewNewPojo> oneWasList = userListWas.stream()
                        .filter(u -> u.getSTCD().equals(stcd) && !CommonUtills.isEmpty(u.getQ()))
                        .collect(Collectors.toList());
                List<ST_STBPRP_BPojo> onestStbprpB = stStbprpBList.stream().filter(u -> u.getSTCD().equals(stcd))
                        .collect(Collectors.toList());
                List<ST_STBPRP_B_QUPojo> onequList = quList.stream().filter(u -> u.getSTCD().equals(stcd))
                        .collect(Collectors.toList());
                if (oneRiverList.size() > 0) {
                    GetWaterViewNewPojo pojo = oneRiverList.get(0);
                    if (onequList.size() > 0) {
                        pojo.setOrderbyid(onequList.get(0).getORDERBYID());
                        pojo.setSTNM(onequList.get(0).getSTNM());
                    }
                    if (onestStbprpB.size() > 0) {
                        pojo.setADMAUTH(onestStbprpB.get(0).getADMAUTH());
                    }
                    pojo.setDIR(quList.get(num).getDIR());
                    pojo.setMAPSIZE(String.valueOf(quList.get(num).getMAPSIZE()));
                    pojo.setROTATE(quList.get(num).getROTATE());

                    dataList.add(pojo);
                } else if (oneWasList.size() > 0) {
                    GetWaterViewNewPojo pojo = oneWasList.get(0);
                    if (onequList.size() > 0) {
                        pojo.setOrderbyid(onequList.get(0).getORDERBYID());
                        pojo.setSTNM(onequList.get(0).getSTNM());
                    }
                    if (onestStbprpB.size() > 0) {
                        pojo.setADMAUTH(onestStbprpB.get(0).getADMAUTH());
                    }
                    pojo.setDIR(quList.get(num).getDIR());
                    pojo.setMAPSIZE(String.valueOf(quList.get(num).getMAPSIZE()));
                    pojo.setROTATE(quList.get(num).getROTATE());

                    dataList.add(pojo);
                } else {
                    if (onestStbprpB.size() > 0) {
                        GetWaterViewNewPojo pojo = new GetWaterViewNewPojo();
                        pojo.setSTCD(onestStbprpB.get(0).getSTCD());
                        pojo.setSTNM(onestStbprpB.get(0).getSTNM());
                        pojo.setHNNM(onestStbprpB.get(0).getHNNM());
                        pojo.setRVNM(onestStbprpB.get(0).getRVNM());
                        pojo.setLGTD(String.valueOf(onestStbprpB.get(0).getLGTD()));
                        pojo.setLTTD(String.valueOf(onestStbprpB.get(0).getLTTD()));
                        pojo.setADMAUTH(onestStbprpB.get(0).getADMAUTH());
                        pojo.setMTYPE(onestStbprpB.get(0).getMTYPE());
                        pojo.setDIR(quList.get(num).getDIR());
                        pojo.setMAPSIZE(String.valueOf(quList.get(num).getMAPSIZE()));
                        pojo.setROTATE(quList.get(num).getROTATE());
                        if (onequList.size() > 0) {
                            pojo.setOrderbyid(onequList.get(0).getORDERBYID());
                            pojo.setSTNM(onequList.get(0).getSTNM());
                        }
                        dataList.add(pojo);
                    }
                }
            }
        }
        return dataList;
    }

    @RequestMapping("/queryBySWQDanZhan")
    public List<GetWaterViewNewPojo> queryBySWQDanZhan(@RequestBody ColumnName param) {
        Date date = new Date(new Date().getTime() - 24 * 60 * 60 * 1000);
        String dayhour = "Minute", stime = DateUtil.dateFormat(date, "yyyy-MM-dd HH:mm:ss"), etime = "";
        List<String> listSTCD = new ArrayList<>();
        if (null != param.getPathname()) {
            dayhour = param.getPathname();
        }
        if (null != param.getStime()) {
            stime = param.getStime();
        }
        if (null != param.getEtime()) {
            etime = param.getEtime();
        }
        String mtype = "";
        if (null != param.getDatasource()) {
            mtype = param.getDatasource();
        }

        if (null != param.getStcd()) {
            listSTCD = Arrays.asList(param.getStcd().split(","));
        }
        List<GetWaterViewNewPojo> userListRiver = data.queryByRiver(listSTCD, stime, etime, dayhour, mtype);
        List<GetWaterViewNewPojo> userListWas = data.queryByWas(listSTCD, stime, etime, dayhour, mtype);
        List<GetWaterViewNewPojo> dataList = new ArrayList<>();

        if (listSTCD.size() > 0) {
            for (int num = 0; num < listSTCD.size(); num++) {
                String stcd = listSTCD.get(num);
                List<GetWaterViewNewPojo> oneRiverList = userListRiver.stream().filter(u -> u.getSTCD().equals(stcd))
                        .collect(Collectors.toList());
                List<GetWaterViewNewPojo> oneWasList = userListWas.stream().filter(u -> u.getSTCD().equals(stcd))
                        .collect(Collectors.toList());
                if (userListRiver.size() > 0) {
                    oneRiverList.forEach(u -> {
                        GetWaterViewNewPojo pojo = u;
                        dataList.add(pojo);
                    });

                }
                if (userListWas.size() > 0) {
                    userListWas.forEach(u -> {
                        GetWaterViewNewPojo pojo = u;
                        dataList.add(pojo);
                    });
                }
            }
        }
        return dataList;
    }

    // 水情分析
    // @RequestMapping("/querySingleStationWaterAnalysis")
    // public List<SingleStationWaterAnalysisPojo>
    // querySingleStationWaterAnalysis(@RequestBody ColumnName param){
    // Date date = new Date(new Date().getTime() - 24 * 60 * 60 * 1000);
    // String dayhour="Minute",stime= DateUtil.dateFormat(date,"yyyy-MM-dd
    // HH:mm:ss"),etime="";
    // List<String> listSTCD=new ArrayList<>();
    // if(null != param.getPathname()){
    // dayhour = param.getPathname();
    // }
    // if(null != param.getStime()){
    // stime = param.getStime();
    // }
    // if(null != param.getEtime()){
    // etime = param.getEtime();
    // }
    // if(null != param.getStcd()){
    // listSTCD = Arrays.asList(param.getStcd().split(","));
    // }
    //
    // SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    // long sTime = 0,eTime = 0;
    // try {
    // sTime = dateFormat.parse(stime).getTime();
    // eTime = dateFormat.parse(etime).getTime();
    // } catch (ParseException e) {
    // e.printStackTrace();
    // }
    // String mtype="";
    // List<GetWaterViewNewPojo> userListRiver=data.queryByRiver(listSTCD, stime,
    // etime,dayhour,mtype);
    // List<GetWaterViewNewPojo> userListWas=data.queryByWas(listSTCD, stime,
    // etime,dayhour,mtype);
    // List<GetWaterViewNewPojo> userListTide=data.queryByTide(listSTCD, stime,
    // etime,dayhour,mtype);
    // List<GetWaterViewNewPojo> userListPump=data.queryByPump(listSTCD, stime,
    // etime,dayhour,mtype);
    //
    // List<SingleStationWaterAnalysisPojo> dataList=new ArrayList<>();
    //
    // long timedif = DateUtil.dateDiff(stime,etime,"yyyy-MM-dd HH:mm:ss","d")+1;
    // for(int i = 0;i < timedif;i++){
    // int finalI = i;
    // long finalSTime = sTime;
    // Date _stm = DateUtil.addTimeToDate(new Date(finalSTime), "d" ,finalI);
    // Date _etm = DateUtil.addTimeToDate(new Date(finalSTime), "d" ,finalI + 1);
    // String ymdhm = DateUtil.dateFormat(_stm,"yyyy-MM-dd");
    //
    // List<GetWaterViewNewPojo> userListRiverTemp = userListRiver.stream().filter(j
    // -> {
    // if(null != j.getTM()){
    // try {
    // return dateFormat.parse(j.getTM()).after(_stm) &&
    // dateFormat.parse(j.getTM()).before(_etm);
    // } catch (ParseException e) {
    // e.printStackTrace();
    // }
    // }
    // return false;
    // }).collect(Collectors.toList());
    // List<GetWaterViewNewPojo> userListWasTemp = userListWas.stream().filter(j ->
    // {
    // if(null != j.getTM()){
    // try {
    // return dateFormat.parse(j.getTM()).after(_stm) &&
    // dateFormat.parse(j.getTM()).before(_etm);
    // } catch (ParseException e) {
    // e.printStackTrace();
    // }
    // }
    // return false;
    // }).collect(Collectors.toList());
    // List<GetWaterViewNewPojo> userListTideTemp = userListTide.stream().filter(j
    // -> {
    // if(null != j.getTM()){
    // try {
    // return dateFormat.parse(j.getTM()).after(_stm) &&
    // dateFormat.parse(j.getTM()).before(_etm);
    // } catch (ParseException e) {
    // e.printStackTrace();
    // }
    // }
    // return false;
    // }).collect(Collectors.toList());
    // List<GetWaterViewNewPojo> userListPumpTemp = userListPump.stream().filter(j
    // -> {
    // if(null != j.getTM()){
    // try {
    // return dateFormat.parse(j.getTM()).after(_stm) &&
    // dateFormat.parse(j.getTM()).before(_etm);
    // } catch (ParseException e) {
    // e.printStackTrace();
    // }
    // }
    // return false;
    // }).collect(Collectors.toList());
    //
    //
    // SingleStationWaterAnalysisPojo pojo=new SingleStationWaterAnalysisPojo();
    // if(userListRiverTemp.size()>0){
    // userListRiverTemp.sort((a, b) -> {
    // if (a.getUPZ() != null && b.getUPZ() != null) {
    // return (int) (Double.valueOf(b.getUPZ()) * 1000 - Double.valueOf(a.getUPZ())
    // * 1000);
    // }
    // return -1;
    // });
    // //平均水位
    // Double avgZ = userListRiverTemp.stream().mapToDouble(item ->
    // Double.valueOf(item.getUPZ())).average().getAsDouble();
    // avgZ= (new BigDecimal(avgZ).setScale(2,
    // BigDecimal.ROUND_HALF_UP)).doubleValue();
    // //最高水位
    // Double maxZ = (new BigDecimal(userListRiverTemp.get(0).getUPZ()).setScale(2,
    // BigDecimal.ROUND_HALF_UP)).doubleValue();
    // String maxZ_TM=userListRiverTemp.get(0).getTM();
    // //最低水位
    // Double minZ = (new BigDecimal(userListRiverTemp.get(userListRiverTemp.size()
    // - 1).getUPZ()).setScale(2, BigDecimal.ROUND_HALF_UP)).doubleValue();
    // String minZ_TM=userListRiverTemp.get(userListRiverTemp.size() - 1).getTM();
    //
    // pojo.setSTCD(userListRiverTemp.get(0).getSTCD());
    // pojo.setSTNM(userListRiverTemp.get(0).getSTNM());
    // pojo.setYMDHM(ymdhm);
    // pojo.setZ(avgZ.toString());
    // pojo.setMAX_Z(maxZ.toString());
    // pojo.setMAX_TM(maxZ_TM);
    // pojo.setMIN_Z(minZ.toString());
    // pojo.setMIN_TM(minZ_TM);
    // pojo.setWRZ(userListRiverTemp.get(0).getWRZ());
    // pojo.setGRZ(userListRiverTemp.get(0).getGRZ());
    // dataList.add(pojo);
    // }
    // if(userListWasTemp.size()>0){
    // userListWasTemp.sort((a, b) -> {
    // if (a.getUPZ() != null && b.getUPZ() != null) {
    // return (int) (Double.valueOf(b.getUPZ()) * 1000 - Double.valueOf(a.getUPZ())
    // * 1000);
    // }
    // return -1;
    // });
    // //平均水位
    // Double avgZ = userListWasTemp.stream().mapToDouble(item ->
    // Double.valueOf(item.getUPZ())).average().getAsDouble();
    // avgZ= (new BigDecimal(avgZ).setScale(2,
    // BigDecimal.ROUND_HALF_UP)).doubleValue();
    // //最高水位
    // Double maxZ = (new BigDecimal(userListWasTemp.get(0).getUPZ()).setScale(2,
    // BigDecimal.ROUND_HALF_UP)).doubleValue();
    // String maxZ_TM=userListWasTemp.get(0).getTM();
    // //最低水位
    // Double minZ = (new BigDecimal(userListWasTemp.get(userListWasTemp.size() -
    // 1).getUPZ()).setScale(2, BigDecimal.ROUND_HALF_UP)).doubleValue();
    // String minZ_TM=userListWasTemp.get(userListWasTemp.size() - 1).getTM();
    //
    // pojo.setSTCD(userListWasTemp.get(0).getSTCD());
    // pojo.setSTNM(userListWasTemp.get(0).getSTNM());
    // pojo.setYMDHM(ymdhm);
    // pojo.setZ(avgZ.toString());
    // pojo.setMAX_Z(maxZ.toString());
    // pojo.setMAX_TM(maxZ_TM);
    // pojo.setMIN_Z(minZ.toString());
    // pojo.setMIN_TM(minZ_TM);
    // pojo.setWRZ(userListWasTemp.get(0).getWRZ());
    // pojo.setGRZ(userListWasTemp.get(0).getGRZ());
    // dataList.add(pojo);
    // }
    // if(userListTideTemp.size()>0){
    // userListTideTemp.sort((a, b) -> {
    // if (a.getUPZ() != null && b.getUPZ() != null) {
    // return (int) (Double.valueOf(b.getUPZ()) * 1000 - Double.valueOf(a.getUPZ())
    // * 1000);
    // }
    // return -1;
    // });
    // //平均水位
    // Double avgZ = userListTideTemp.stream().mapToDouble(item ->
    // Double.valueOf(item.getUPZ())).average().getAsDouble();
    // avgZ= (new BigDecimal(avgZ).setScale(2,
    // BigDecimal.ROUND_HALF_UP)).doubleValue();
    // //最高水位
    // Double maxZ = (new BigDecimal(userListTideTemp.get(0).getUPZ()).setScale(2,
    // BigDecimal.ROUND_HALF_UP)).doubleValue();
    // String maxZ_TM=userListTideTemp.get(0).getTM();
    // //最低水位
    // Double minZ = (new BigDecimal(userListTideTemp.get(userListTideTemp.size() -
    // 1).getUPZ()).setScale(2, BigDecimal.ROUND_HALF_UP)).doubleValue();
    // String minZ_TM=userListTideTemp.get(userListTideTemp.size() - 1).getTM();
    //
    // pojo.setSTCD(userListTideTemp.get(0).getSTCD());
    // pojo.setSTNM(userListTideTemp.get(0).getSTNM());
    // pojo.setYMDHM(ymdhm);
    // pojo.setZ(avgZ.toString());
    // pojo.setMAX_Z(maxZ.toString());
    // pojo.setMAX_TM(maxZ_TM);
    // pojo.setMIN_Z(minZ.toString());
    // pojo.setMIN_TM(minZ_TM);
    // pojo.setWRZ(userListTideTemp.get(0).getWRZ());
    // pojo.setGRZ(userListTideTemp.get(0).getGRZ());
    // dataList.add(pojo);
    // }
    // if(userListPumpTemp.size()>0){
    // userListPumpTemp.sort((a, b) -> {
    // if (a.getUPZ() != null && b.getUPZ() != null) {
    // return (int) (Double.valueOf(b.getUPZ()) * 1000 - Double.valueOf(a.getUPZ())
    // * 1000);
    // }
    // return -1;
    // });
    // //平均水位
    // Double avgZ = userListPumpTemp.stream().mapToDouble(item ->
    // Double.valueOf(item.getUPZ())).average().getAsDouble();
    // avgZ= (new BigDecimal(avgZ).setScale(2,
    // BigDecimal.ROUND_HALF_UP)).doubleValue();
    // //最高水位
    // Double maxZ = (new BigDecimal(userListPumpTemp.get(0).getUPZ()).setScale(2,
    // BigDecimal.ROUND_HALF_UP)).doubleValue();
    // String maxZ_TM=userListPumpTemp.get(0).getTM();
    // //最低水位
    // Double minZ = (new BigDecimal(userListPumpTemp.get(userListPumpTemp.size() -
    // 1).getUPZ()).setScale(2, BigDecimal.ROUND_HALF_UP)).doubleValue();
    // String minZ_TM=userListPumpTemp.get(userListPumpTemp.size() - 1).getTM();
    //
    // pojo.setSTCD(userListPumpTemp.get(0).getSTCD());
    // pojo.setSTNM(userListPumpTemp.get(0).getSTNM());
    // pojo.setYMDHM(ymdhm);
    // pojo.setZ(avgZ.toString());
    // pojo.setMAX_Z(maxZ.toString());
    // pojo.setMAX_TM(maxZ_TM);
    // pojo.setMIN_Z(minZ.toString());
    // pojo.setMIN_TM(minZ_TM);
    // pojo.setWRZ(userListPumpTemp.get(0).getWRZ());
    // pojo.setGRZ(userListPumpTemp.get(0).getGRZ());
    // dataList.add(pojo);
    // }
    // }
    // dataList=dataList.stream().sorted(Comparator.comparing(SingleStationWaterAnalysisPojo::getYMDHM).reversed()).collect(Collectors.toList());
    // return dataList;
    // }

    // 多站水情
    @RequestMapping("/queryBySWDuoZhan")
    public ResultUtils queryBySWDuoZhan(@RequestBody ColumnName param) {
        StopWatch watch = new StopWatch();
        watch.start();
        Date date = new Date(new Date().getTime() - 24 * 60 * 60 * 1000);
        String stime = DateUtil.dateFormat(date, "yyyy-MM-dd HH:mm:ss"),
                etime = DateUtil.dateFormat(new Date(), "yyyy-MM-dd HH:mm:ss"), id = "Minute";
        List<String> stcdList = new ArrayList<>();
        String mtype = "";
        if (null != param.getPathname()) {
            id = param.getPathname();
        }
        if (null != param.getStcd()) {
            stcdList = Arrays.asList(param.getStcd().split(","));
        }
        if (null != param.getStime()) {
            stime = param.getStime();
        }
        if (null != param.getEtime()) {
            etime = param.getEtime();
        }
        if (null != param.getDatasource()) {
            mtype = param.getDatasource();
        }
        List<ST_STBPRP_BPojo> stStbprpBList = stbprpBData.selectList(stcdList, null);
        List<GetWaterViewNewPojo> list = new ArrayList<>();
        if ("HOUR".equals(id)) {
            list = server.selectListByHisIsHouse(stcdList, stime, etime, mtype);
        } else if ("DAY".equals(id)) {
            list = server.selectListByHisIsDay(stcdList, stime, etime, mtype);
        } else {
            list = server.selectListByHisIsTime(stcdList, stime, etime, mtype);
        }

        Map<String, Object> map = new HashMap<>();
        List<String> stcdListnm = new ArrayList<>();
        List<Map<String, Object>> mapList = new ArrayList<>();
        if (stStbprpBList.size() > 0) {
            for (int numII = 0; numII < stStbprpBList.size(); numII++) {
                String stcdStnm = stStbprpBList.get(numII).getSTCD() + ":" + stStbprpBList.get(numII).getSTNM();
                stcdListnm.add(stcdStnm);
            }
        }

        if (null != list && list.size() > 0) {
            List<List<GetWaterViewNewPojo>> waterGroupList = new ArrayList<>();
            list.sort(Comparator.comparing(GetWaterViewNewPojo::getTM));
            list.stream()
                    .collect(Collectors.groupingBy(GetWaterViewNewPojo::getTM, LinkedHashMap::new, Collectors.toList()))
                    .forEach((tm, fooListByTm) -> {
                        waterGroupList.add(fooListByTm);
                    });
            if (null != waterGroupList && waterGroupList.size() > 0) {
                List<String> finalStcdList = stcdList;
                List<GetWaterViewNewPojo> finalList = list;
                waterGroupList.forEach((mWaterList) -> {
                    if (mWaterList.size() > 0) {
                        String finaTime = mWaterList.get(0).getTM();
                        Map<String, Object> strWhere = new HashMap<>();
                        strWhere.put("tm", finaTime);
                        for (int numII = 0; numII < finalStcdList.size(); numII++) {
                            String _stcd = finalStcdList.get(numII);
                            List<GetWaterViewNewPojo> finalListTemp = finalList.stream()
                                    .filter(p -> DateUtil.strToDate(p.getTM(), DateUtil.YMDHMS).getTime() == DateUtil
                                            .strToDate(finaTime, DateUtil.YMDHMS).getTime()
                                            && p.getSTCD().equals(_stcd))
                                    .collect(Collectors.toList());
                            if (finalListTemp.size() > 0) {
                                GetWaterViewNewPojo itemList = finalListTemp.get(0);
                                if (!CommonUtills.isEmpty(itemList.getUPZ())) {
                                    Double strVal = Double.valueOf(itemList.getUPZ());
                                    strWhere.put(_stcd + "upz", strVal);
                                } else {
                                    strWhere.put(_stcd + "upz", 0.00);
                                }
                            }
                        }
                        mapList.add(strWhere);
                    }
                });
            }
        }
        map.put("listSTCD", stcdListnm);
        map.put("list", mapList);
        JSONObject json = new JSONObject(map);
        // return json;
        watch.stop();
        if (json.size() > 0) {
            return new ResultUtils<>(json, "操作成功", true, json.size(), watch.getTime());
        } else {
            return new ResultUtils<>(json, "操作成功", false, json.size(), watch.getTime());
        }

    }

    /*
     * 数据同步接口
     */
    @RequestMapping("/SynchronizeData")
    public ResultUtils SynchronizeData(@RequestBody ColumnName param) {
        StopWatch watch = new StopWatch();
        watch.start();
        String mtype = "上海水文总站", type = "1,2,3,4,5,6,7,8,9,10,11,12";
        if (param.getDatasource() != null) {
            mtype = param.getDatasource();
        }
        if (param.getPathname() != null) {
            type = param.getPathname();
        }
        new javalog().writelog("进入主服务SynchronizeData接口：", templatefilepath);
        int num = tongbuServer.SyncData(mtype, type);
        watch.stop();
        if (num > 0) {
            return new ResultUtils<>(num, "操作成功", true, num, watch.getTime());
        } else {
            return new ResultUtils<>(num, "操作成功", false, num, watch.getTime());
        }
    }

    @RequestMapping("/selectListByHisYB")
    public ResultUtils selectListByHisYB(@RequestBody ColumnName param) {
        StopWatch watch = new StopWatch();
        watch.start();
        Date date = new Date(new Date().getTime() - 24 * 60 * 60 * 1000);
        String stime = DateUtil.dateFormat(date, "yyyy-MM-dd HH:mm:ss"),
                etime = DateUtil.dateFormat(new Date(), "yyyy-MM-dd HH:mm:ss"), id = "Minute";
        List<String> stcdList = new ArrayList<>();
        String mtype = "";
        if (null != param.getPathname()) {
            id = param.getPathname();
        }
        if (null != param.getStcd()) {
            stcdList = Arrays.asList(param.getStcd().split(","));
        }
        if (null != param.getStime()) {
            stime = param.getStime();
        }
        if (null != param.getEtime()) {
            etime = param.getEtime();
        }
        if (null != param.getDatasource()) {
            mtype = param.getDatasource();
        }
        List<GetWaterViewNewPojo> list = new ArrayList<>();
        list = server.selectListByHisIsTime(stcdList, stime, etime, mtype);
        // 查询最新的一次预报
        List<DD_SOLUTIONPojo> ListDDTop = ddSolutionData.selectListNew(null, param.getStime(), param.getEtime());
        String solutionid = "";
        if (ListDDTop.size() > 0) {
            solutionid = ListDDTop.get(0).getDD_ID();
        }
        List<BDMS_PREDICTPojo> listBDMS = new ArrayList<>();
        if (!solutionid.equals("")) {
            // List<ST_STBPRP_B_STCDPojo>
            // listB=stbprpBStcdData.selectList(Arrays.asList(param.getStcd().split(",")),Arrays.asList("1"),Arrays.asList("上海水文总站"));
            // String zstcd=listB.size()>0?listB.get(0).getZSTCD():"";
            listBDMS = bdmsPredictData.selectList(null, null, null, Arrays.asList(solutionid.split(",")), null, id,
                    null, null, null, stcdList);
        }
        List<GetWaterViewNewPojo> listNew = new ArrayList<>();
        String lastTM = "";// 记录最后被用过的时间
        if (listBDMS.size() > 0) {
            for (GetWaterViewNewPojo item : list) {
                GetWaterViewNewPojo pojo = item;
                List<BDMS_PREDICTPojo> listBDMSTemp = listBDMS.stream().filter(u -> u.getYMDHM().equals(item.getTM()))
                        .collect(Collectors.toList());
                if (listBDMSTemp.size() > 0) {
                    pojo.setYBZ(listBDMSTemp.get(0).getDATA().toString());
                    lastTM = listBDMSTemp.get(0).getYMDHM();
                }
                listNew.add(pojo);
            }
            String finalLastTM = lastTM;
            List<BDMS_PREDICTPojo> listBDMSTempS = listBDMS.stream()
                    .filter(u -> {
                        String currentTM = u.getYMDHM();
                        // 防止空指针异常，并执行字符串比较（假设时间格式为 "yyyy-MM-dd HH:mm:ss" 或类似可字典序比较的格式）
                        return currentTM != null && !currentTM.isEmpty()
                                && (finalLastTM == null || finalLastTM.isEmpty()
                                        || currentTM.compareTo(finalLastTM) > 0);
                    })
                    .collect(Collectors.toList());
            for (BDMS_PREDICTPojo item : listBDMSTempS) {
                GetWaterViewNewPojo pojo = new GetWaterViewNewPojo();
                pojo.setTM(item.getYMDHM().toString());
                pojo.setYBZ(item.getDATA().toString());
                pojo.setSTCD(param.getStcd());
                pojo.setWRZ(list.get(0).getWRZ());
                pojo.setGRZ(list.get(0).getGRZ());
                pojo.setIVHZ(list.get(0).getIVHZ());
                pojo.setIVHZTM(list.get(0).getIVHZTM());
                listNew.add(pojo);
            }
        } else {
            listNew = list;
        }
        watch.stop();
        if (listNew.size() > 0) {
            return new ResultUtils<>(listNew, "操作成功", true, listNew.size(), watch.getTime());
        } else {
            return new ResultUtils<>(listNew, "操作成功", false, listNew.size(), watch.getTime());
        }
    }

    /*
     * 数据同步接口
     */
    @RequestMapping("/SynchronizeData5min")
    public ResultUtils SynchronizeData5min(@RequestBody ColumnName param) {
        StopWatch watch = new StopWatch();
        watch.start();
        List<String> stcdList = new ArrayList<>();
        Date date = new Date(new Date().getTime() - 24 * 60 * 60 * 1000);
        String stime = DateUtil.dateFormat(date, "yyyy-MM-dd HH:mm:ss"), etime = "";
        // 1. 定义日期格式化器，用于解析 stime
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime stimeLdt = LocalDateTime.parse(stime, formatter);
        if (param.getStcd() != null) {
            stcdList = Arrays.asList(param.getStcd().split(","));
        }
        if (param.getStime() != null) {
            stime = param.getStime();
        }
        if (param.getEtime() != null) {
            etime = param.getEtime();
        } else {
            // 2. 从 stime 字符串解析出 LocalDateTime 对象
            stimeLdt = LocalDateTime.parse(stime, formatter);
            LocalDate dateStime = stimeLdt.toLocalDate();
            LocalDate lastDay = dateStime.withDayOfMonth(dateStime.lengthOfMonth());
            // 4. 组合时间 23:00:00
            LocalDateTime etimeLdt = lastDay.atTime(LocalTime.of(23, 0, 0));
            // 5. 格式化输出
            etime = etimeLdt.format(formatter);
        }
        List<ST_PPTN_RPojo> listNew = tongbuServer.getRTSQ_5MINXZYLNew(stcdList, stime, etime);
        // 创建 ObjectMapper 实例
        ObjectMapper objectMapper = new ObjectMapper();
        // 开启缩进，使生成的 JSON 文件更易读
        objectMapper.enable(com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT);
        try {
            // 将 listNew 对象序列化为 JSON 并写入文件
            // 文件将保存在项目根目录下，文件名为 "data.json"
            DateTimeFormatter formatterYM = DateTimeFormatter.ofPattern("yyyy年M月");
            String jsonFileName = "市政雨水排水片雨量站(" + stimeLdt.format(formatterYM) + "5分钟数据).json";
            objectMapper.writeValue(new File(templatefilepath + "/提供给模型的5分钟数据/" + jsonFileName), listNew);

            System.out.println("JSON 文件已成功导出！");
        } catch (IOException e) {
            // e.printStackTrace();
            System.err.println("导出 JSON 文件时发生错误。");
        }

        watch.stop();
        if (listNew.size() > 0) {
            return new ResultUtils<>(listNew, "操作成功", true, listNew.size(), watch.getTime());
        } else {
            return new ResultUtils<>(listNew, "操作成功", false, listNew.size(), watch.getTime());
        }
    }

    /*
     * 数据同步接口
     */
    @RequestMapping("/SynchronizeDataHour")
    public ResultUtils SynchronizeDataHour(@RequestBody List<TSDBPojo> param) {
        StopWatch watch = new StopWatch();
        watch.start();
        List<ST_PPTN_RPojo> listNew = tongbuServer.getRTSQ_5MINXZYLHourList(param);
        watch.stop();
        if (listNew.size() > 0) {
            return new ResultUtils<>(listNew, "操作成功", true, listNew.size(), watch.getTime());
        } else {
            return new ResultUtils<>(listNew, "操作成功", false, listNew.size(), watch.getTime());
        }
    }
}
