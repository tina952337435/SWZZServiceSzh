package swzzmodeserver.workserver.server.swzzrtsq;

import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import swzzmodeserver.tools.CommonUtills;
import swzzmodeserver.tools.DateUtil;
import swzzmodeserver.tools.WindUtil;
import swzzmodeserver.tools.javalog;
import swzzmodeserver.workserver.data.swzzdata.EmergencyResponseInfoData;
import swzzmodeserver.workserver.data.swzzflood.RTSQData;
import swzzmodeserver.workserver.data.swzzqxsj.St_areatide_rybData;
import swzzmodeserver.workserver.data.swzzrtsq.*;
import swzzmodeserver.workserver.data.swzzwater.ST_GATE_RData;
import swzzmodeserver.workserver.data.wds.RTEVData;
import swzzmodeserver.workserver.data.wds.TSDBData;
import swzzmodeserver.workserver.pojo.swzzmode.ES_ZHANDIANDATAPojo;
import swzzmodeserver.workserver.pojo.swzzrtsq.*;
import swzzmodeserver.workserver.pojo.swzzrtsq.ChuLaoBengZhaResponse.GateWasData;
import swzzmodeserver.workserver.pojo.swzzrtsq.ChuLaoBengZhaResponse.ResultItem;
import swzzmodeserver.workserver.pojo.swzzdata.EmergencyResponseInfoPojo;
import swzzmodeserver.workserver.pojo.swzzflood.ST_WAS_RPojo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.Data;

@Service
public class TongbuServer {
    @Autowired
    private RTSQST_PPTN_RData data;
    @Autowired
    private RTSQST_STBPRP_BData rtsqstStbprpBData;

    @Autowired
    private RTSQData rtsqData;

    @Autowired
    private ST_RIVER_RData stRiverRData;

    @Autowired
    private RTSQST_TIDE_RData stTideRData;

    @Autowired
    private RTSQST_STBPRP_B_STCDData rtsqstStbprpBStcdData;

    @Autowired
    private RTSQST_PPTN_RData rtsqstPptnRData;

    @Autowired
    private ST_FLOW_RData stFlowRData;

    @Autowired
    private ST_VEL_RData stVelRData;

    @Autowired
    private ST_WDWV_RData stWdwvRData;

    @Autowired
    private RTEVData rtevData;

    @Autowired
    private TSDBData tsdbData;

    @Autowired
    private St_areatide_rybData stAreatideRybData;

    @Autowired
    private CommonUtills commonUtills;

    @Value("${spring.server.tongbutm}")
    private String tongbutm;

    @Autowired
    private shuiwupingServer shuiwupingServer;

    @Autowired
    private shuizhaServer shuizhaServer;

    @Autowired
    private RTSQST_GATE_RNEWData rtsqstGateRnewData;
    @Autowired
    private RTSQST_GATE_RData rtsqstGateRData;

    @Autowired
    private RTSQST_WAS_RData rtsqstWasRData;

    @Autowired
    private Executor syncTaskExecutor;

    @Autowired
    private EmergencyResponseInfoData emergencyResponseInfoData;

    @Value("${file.path.templatefilepath}")
    private String filePathName;

    // 同步总接口
    public Integer SyncData(String mtype, String type) {
        int rows = 0;
        List<V_ST_STBPRP_BTZDto> listItemData = new ArrayList<>();
        List<String> typeList = Arrays.asList(type.split(","));
        switch (mtype) {
            case "上海水文总站":
            case "市气象局":
                listItemData = rtsqstStbprpBData.GetSyncSTCD(null, typeList, "1", Arrays.asList(mtype));
                rows = SyncRealData(listItemData);
                break;
            case "水利部太湖局":
            case "水利部报汛":
            case "水利部":
            case "自然资源部东海局":
                List<String> sourceList = Arrays.asList("水利部太湖局,水利部报汛,上海海事局,自然资源部东海局".split(","));
                listItemData = rtsqstStbprpBData.GetSyncSTCD(null, typeList, "1", sourceList);
                rows = SyncRealDataSWPT(listItemData);// 水务平台接数据
                break;
            case "水利中心":
                new javalog().writelog("进入主服务SynchronizeData接口：" + mtype, filePathName, "SWZZServiceGate");
                listItemData = rtsqstStbprpBData.GetSyncSTCD(null, typeList, "1", Arrays.asList(mtype));
                new javalog().writelog("【水闸】listItemData的长度是：" + listItemData.size(), filePathName, "SWZZServiceGate");
                System.out.println("【水闸】listItemData的长度是：" + listItemData.size());
                rows = SyncRealDataGate(listItemData);
                new javalog().writelog("SyncRealDataGate的返回值是：" + rows, filePathName, "SWZZServiceGate");
                updateSt_gate_rNewAll();// 更新最新表
                break;
            case "市排水中心":
                new javalog().writelog("进入主服务SyncRealDataGateBeng接口：" + mtype, filePathName, "SWZZServiceGate");
                listItemData = rtsqstStbprpBData.GetSyncSTCD(null, typeList, "1", Arrays.asList(mtype));
                new javalog().writelog("【市政泵站】listItemData的长度是：" + listItemData.size(), filePathName,
                        "SWZZServiceGate");
                rows = SyncRealDataGateBeng(listItemData);
                new javalog().writelog("SyncRealDataGateBeng的返回值是：" + rows, filePathName, "SWZZServiceGate");
                updateSt_gate_rNewAll();// 更新最新表
            default:
                break;
        }
        return rows;
    }

    // 上海水文总站（南瑞遥测库）：水位、雨量
    private Integer SyncRealData(List<V_ST_STBPRP_BTZDto> listItem) {
        Integer rows = 0;
        String stcd = "", tm = "", stnm = "", tab = "";
        for (V_ST_STBPRP_BTZDto model : listItem) {
            stcd = model.getAdmauth();
            tm = model.getTm();
            stnm = model.getStnm();
            tab = model.getTab();
            if (tm == null || tm.isEmpty()) {
                tm = tongbutm;
            }
            List<String> stcdList = Arrays.asList(stcd.split(","));
            if (model.getType().equals("1"))// 水位
            {
                new javalog().writelog("进入主服务SyncRealData接口，开始同步水位数据：" + stcd, filePathName);
                // 查询
                // List<ST_WAS_RPojo> st_was_r = rtsqData.GetWaterData(tm, null, stcdList,
                // null);//达梦库
                List<ST_WAS_RPojo> st_was_r = rtevData.GetWaterData(stcdList, tm);// wds 老水情库
                st_was_r = st_was_r.stream().filter(item -> {
                    try {
                        double upzValue = Double.parseDouble(item.getUPZ());
                        return upzValue < 15 && upzValue > -15;
                    } catch (Exception e) {
                        // 如果 getUPZ() 为空或非数字，则过滤掉该条数据
                        return false;
                    }
                }).collect(Collectors.toList());

                new javalog().writelog("st_was_r数据长度：" + st_was_r.size(), filePathName);
                if (tab.equals("st_river_r")) {// 河道水位
                    List<ST_RIVER_RPojo> listR = SyncWaterDataToListRiver(st_was_r, model);
                    if (listR.size() > 0) {
                        // 插入操作
                        try {
                            rows += stRiverRData.insertAll(listR);
                        } catch (Exception e) {
                            // TODO: handle exception
                            new javalog().writelog("同步水位报错：" + e.getMessage() + ",listR内容：" + listR, filePathName);
                        }

                        // if (rows > 0) {
                        // 更新最新时间
                        UpdateWaterData(tab, model.getType(), model.getStcd());
                        new javalog().writelog("更新" + model.getStnm() + "站最新时间", filePathName);
                        // }
                    }
                } else if (tab.equals("st_tide_r")) {// 潮位站
                    List<ST_TIDE_RPojo> listR = SyncWaterDataToListTide(st_was_r, model);
                    if (listR.size() > 0) {
                        // 插入操作
                        rows += stTideRData.insertAll(listR);
                        // if (rows > 0) {
                        // 更新最新时间
                        UpdateWaterData(tab, model.getType(), model.getStcd());
                        new javalog().writelog("更新" + model.getStnm() + "站【水位】最新时间", filePathName);
                        // }
                    }
                } else if (tab.equals("st_was_r")) {// 堰闸水文站
                } else if (tab.equals("st_pump_r")) {// 泵站
                } else if (tab.equals("st_rsvr_r")) {// 水库
                }

                new javalog().writelog(model.getStnm() + "站入库水位数据长度：" + rows, filePathName);
            } 
            else if (model.getType().equals("2"))// 雨量
            {
                new javalog().writelog("进入主服务SyncRealData接口，开始同步雨量数据：" + stcd, filePathName, "SWZZServiceYL");
                List<ST_PPTN_RPojo> listPptn = new ArrayList<>();
                if (model.getSource().equals("上海水文总站")) {
                    listPptn = getRTSQ_5MINXZYL(stcdList, tm, null, model.getStcd());
                } else if (model.getSource().equals("市气象局")) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    String etime = LocalDateTime.parse(tm, formatter).plusDays(3).format(formatter);// 加三天

                    new javalog().writelog(model.getStnm() + "站【市气象局雨量】开始查询：" + tm + "至" + etime, filePathName,
                            "SWZZServiceYL");

                    listPptn = rtsqstPptnRData.selectListSL323(stcdList, tm, etime);
                }
                new javalog().writelog(model.getStnm() + "站【" + model.getStcd() + "】雨量数据长度：" + listPptn.size(),
                        filePathName, "SWZZServiceYL");
                if (listPptn.size() > 0) {
                    // 插入操作
                    try {
                        rows += rtsqstPptnRData.insertAll(listPptn);
                        new javalog().writelog(model.getStnm() + "站入库雨量数据长度：" + rows, filePathName, "SWZZServiceYL");
                    } catch (Exception e) {
                        new javalog().writelog(model.getStnm() + "入库报错：" + e.getMessage(), filePathName,
                                "SWZZServiceYL");
                        UpdateWaterData(tab, model.getType(), model.getStcd());
                        // TODO: handle exception
                    }
                    // if (rows > 0) {
                    // 更新最新时间
                    UpdateWaterData(tab, model.getType(), model.getStcd());
                    new javalog().writelog("更新" + model.getStnm() + "站【雨量】最新时间", filePathName, "SWZZServiceYL");
                    // }

                }
            } 
            else if (model.getType().equals("3"))// 工情
            {

            } else if (model.getType().equals("5"))// 流量
            {
                new javalog().writelog("进入主服务SyncRealData接口，开始同步流量数据：" + stcd, filePathName, "SWZZServiceLL");
                // 查询
                // List<ST_WAS_RPojo> st_was_rLL = rtsqData.GetWaterDataLL(tm, null, stcdList,
                // null);//达梦数据库
                List<ST_WAS_RPojo> st_was_rLL = rtevData.GetWaterDataLL(stcdList, tm);// wds 老水情库
                new javalog().writelog("流量数据长度：" + st_was_rLL.size(), filePathName, "SWZZServiceLL");
                List<ST_FLOW_RPojo> listLL = SyncWaterDataToListFlow(st_was_rLL, model);
                if (listLL.size() > 0) {
                    // 插入操作
                    new javalog().writelog("流量数据长度：" + listLL.size(), filePathName, "SWZZServiceLL");
                    try {
                        rows += stFlowRData.insertAll(listLL);
                    } catch (Exception e) {
                        new javalog().writelog(model.getStnm() + "站入库流量报错：" + e.getMessage(), filePathName,
                                "SWZZServiceLLError");
                    }
                    new javalog().writelog(model.getStnm() + "站入库流量数据长度：" + rows, filePathName, "SWZZServiceLL");
                    // if (rows > 0) {
                    // 更新最新时间
                    UpdateWaterData(tab, model.getType(), model.getStcd());
                    new javalog().writelog("更新" + model.getStnm() + "站【流量】最新时间", filePathName, "SWZZServiceLL");
                    // }
                }
            } 
            else if (model.getType().equals("8")) {// 风速风向
                new javalog().writelog("进入主服务SyncRealData接口，开始同步风速风向数据：" + stcd, filePathName, "SWZZServiceFX");
                // List<tb_wind_informationPojo> listFeng =
                // stWdwvRData.selecttb_wind_informationListtTop(stcdList, tm, null);

                // List<tb_wind_informationPojo> listFeng
                // =stAreatideRybData.selecttb_wind_informationListtTop(stcdList,
                // tm,null);//国产化改造的数据
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String etime = LocalDateTime.parse(tm, formatter).plusDays(3).format(formatter);// 加三天
                try {
                    List<ST_WAS_RPojo> st_was_rFX = rtevData.GetWaterDataFX(stcdList, tm, etime);// wds 老水情库

                    new javalog().writelog("风速风向数据长度：" + st_was_rFX.size(), filePathName, "SWZZServiceFX");
                    List<ST_WDWV_RPojo> listF = SyncWaterDataToListFengRtev(st_was_rFX, model);
                    if (listF.size() > 0) {
                        // 插入操作
                        try {
                            rows += stWdwvRData.insertAll(listF);
                            new javalog().writelog(model.getStnm() + "站入库风速风向数据长度：" + rows, filePathName,
                                    "SWZZServiceFX");
                        } catch (Exception e) {
                            new javalog().writelog(model.getStnm() + "站报错：" + e.getMessage(), filePathName,
                                    "SWZZServiceFXError");
                            new javalog().writelog(model.getStnm() + "站入库风速风向数据失败listF的值********" + listF, filePathName,
                                    "SWZZServiceFXError");
                        }
                        // if (rows > 0) {
                        // 更新最新时间
                        UpdateWaterData(tab, model.getType(), model.getStcd());
                        new javalog().writelog("更新" + model.getStnm() + "站【风速风向】最新时间", filePathName, "SWZZServiceFX");
                        // }
                    }
                } catch (Exception e) {
                    new javalog().writelog(model.getStnm() + "站报错：" + e.getMessage(), filePathName,
                            "SWZZServiceFXError");
                }
            } 
            else if (model.getType().equals("9")) {// 断面流速
                new javalog().writelog("进入主服务SyncRealData接口，开始同步【流速】数据：" + stcd, filePathName, "SWZZServiceLS");
                // 查询
                // List<ST_WAS_RPojo> st_was_rLL = rtsqData.GetWaterDataLL(tm, null, stcdList,
                // null);//达梦数据库
                List<ST_WAS_RPojo> st_was_rLL = rtevData.GetWaterDataLL(stcdList, tm);// wds 老水情库
                new javalog().writelog("【流速】数据长度：" + st_was_rLL.size(), filePathName, "SWZZServiceLS");
                List<ST_VEL_RPojo> listLL = SyncWaterDataToListVel(st_was_rLL, model);
                if (listLL.size() > 0) {
                    // 插入操作
                    new javalog().writelog("【流速】数据长度：" + listLL.size(), filePathName, "SWZZServiceLS");
                    try {
                        rows += stVelRData.insertAll(listLL);
                    } catch (Exception e) {
                        new javalog().writelog(model.getStnm() + "站入库【流速】数据报错：" + e.getMessage(), filePathName,
                                "SWZZServiceLLError");
                    }
                    new javalog().writelog(model.getStnm() + "站入库【流速】数据长度：" + rows, filePathName, "SWZZServiceLS");
                    // if (rows > 0) {
                    // 更新最新时间
                    UpdateWaterData(tab, model.getType(), model.getStcd());
                    new javalog().writelog("更新" + model.getStnm() + "站【流速】最新时间", filePathName, "SWZZServiceLS");
                    // }
                }
            }
        }
        return rows;
    }

    private List<ST_RIVER_RPojo> SyncWaterDataToListRiver(List<ST_WAS_RPojo> st_was_r, V_ST_STBPRP_BTZDto model) {
        List<ST_RIVER_RPojo> list = new ArrayList<>();
        try {
            if (st_was_r.size() > 0) {
                String strLastTM = (model.getTm() != null && !model.getTm().trim().isEmpty()) ? model.getTm().trim()
                        : "";
                String strLastUPZ = model.getUpz() != null
                        ? String.format("%.2f", new java.math.BigDecimal(model.getUpz()))
                        : "";
                boolean flag = true;
                for (ST_WAS_RPojo pojo : st_was_r) {
                    flag = true;
                    String strTM = pojo.getTM();
                    if (!"".equals(strLastTM)) {
                        if (strLastTM.equals(strTM)) {
                            flag = false;
                        }
                    }
                    if (flag) {
                        ST_RIVER_RPojo wasInfo = new ST_RIVER_RPojo();
                        wasInfo.setSTCD(model.getStcd());
                        wasInfo.setTM(pojo.getTM());

                        String upzStr = pojo.getUPZ();
                        Double upzValue = 0.0;
                        if (upzStr != null && !upzStr.trim().isEmpty()) {
                            try {
                                double temp = Double.parseDouble(upzStr);
                                upzValue = Double.parseDouble(String.format("%.2f", temp));
                            } catch (NumberFormatException e) {
                                upzValue = 0.0;
                            }
                        }
                        if (!upzValue.equals(0)) {
                            wasInfo.setZ(upzValue);
                        }
                        list.add(wasInfo);

                        strLastUPZ = upzValue.toString();
                        strLastTM = strTM;
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println("发生错误：" + ex.getMessage());
        }
        return list;
    }

    private List<ST_TIDE_RPojo> SyncWaterDataToListTide(List<ST_WAS_RPojo> st_was_r, V_ST_STBPRP_BTZDto model) {
        List<ST_TIDE_RPojo> list = new ArrayList<>();
        try {
            if (st_was_r.size() > 0) {
                // 如果不需要严格的类型转换校验，只是想把字符串传下去
                String strLastTM = (model.getTm() != null && !model.getTm().trim().isEmpty()) ? model.getTm().trim()
                        : "";
                String strLastUPZ = model.getUpz() != null
                        ? String.format("%.2f", new java.math.BigDecimal(model.getUpz()))
                        : "";
                boolean flag = true;
                for (ST_WAS_RPojo pojo : st_was_r) {
                    flag = true;
                    String strTM = pojo.getTM();
                    if (!"".equals(strLastTM)) {
                        if (strLastTM.equals(strTM)) {
                            flag = false;
                        }
                    }
                    if (flag) {
                        ST_TIDE_RPojo wasInfo = new ST_TIDE_RPojo();
                        wasInfo.setSTCD(model.getStcd());
                        wasInfo.setTM(pojo.getTM());

                        String upzStr = pojo.getUPZ();
                        Double upzValue = 0.0;
                        if (upzStr != null && !upzStr.trim().isEmpty()) {
                            try {
                                double temp = Double.parseDouble(upzStr);
                                upzValue = Double.parseDouble(String.format("%.2f", temp));
                            } catch (NumberFormatException e) {
                                upzValue = 0.0;
                            }
                        }
                        if (!upzValue.equals(0)) {
                            wasInfo.setTDZ(upzValue);
                        }
                        list.add(wasInfo);

                        strLastUPZ = upzValue.toString();
                        strLastTM = strTM;
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println("发生错误：" + ex.getMessage());
        }
        return list;
    }

    // 流量数据
    private List<ST_FLOW_RPojo> SyncWaterDataToListFlow(List<ST_WAS_RPojo> st_was_r, V_ST_STBPRP_BTZDto model) {
        List<ST_FLOW_RPojo> list = new ArrayList<>();
        try {
            if (st_was_r.size() > 0) {
                // 如果不需要严格的类型转换校验，只是想把字符串传下去
                String strLastTM = (model.getTm() != null && !model.getTm().trim().isEmpty()) ? model.getTm().trim()
                        : "";
                String strLastUPZ = model.getUpz() != null
                        ? String.format("%.2f", new java.math.BigDecimal(model.getUpz()))
                        : "";
                boolean flag = true;
                for (ST_WAS_RPojo pojo : st_was_r) {
                    flag = true;
                    String strTM = pojo.getTM();
                    if (!"".equals(strLastTM)) {
                        if (strLastTM.equals(strTM)) {
                            flag = false;
                        }
                    }
                    if (flag) {
                        ST_FLOW_RPojo wasInfo = new ST_FLOW_RPojo();
                        wasInfo.setSTCD(model.getStcd());
                        wasInfo.setTM(pojo.getTM());

                        String upzStr = pojo.getUPZ();
                        Double upzValue = 0.0;
                        if (upzStr != null && !upzStr.trim().isEmpty()) {
                            try {
                                double temp = Double.parseDouble(upzStr);
                                upzValue = Double.parseDouble(String.format("%.2f", temp));
                            } catch (NumberFormatException e) {
                                upzValue = 0.0;
                            }
                        }
                        if (!upzValue.equals(0)) {
                            wasInfo.setQ(upzValue);
                        }
                        list.add(wasInfo);

                        strLastUPZ = upzValue.toString();
                        strLastTM = strTM;
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println("发生错误：" + ex.getMessage());
        }
        return list;
    }

    // 流速数据
    private List<ST_VEL_RPojo> SyncWaterDataToListVel(List<ST_WAS_RPojo> st_was_r, V_ST_STBPRP_BTZDto model) {
        List<ST_VEL_RPojo> list = new ArrayList<>();
        try {
            if (st_was_r.size() > 0) {
                // 如果不需要严格的类型转换校验，只是想把字符串传下去
                String strLastTM = (model.getTm() != null && !model.getTm().trim().isEmpty()) ? model.getTm().trim()
                        : "";
                String strLastUPZ = model.getUpz() != null
                        ? String.format("%.2f", new java.math.BigDecimal(model.getUpz()))
                        : "";
                boolean flag = true;
                for (ST_WAS_RPojo pojo : st_was_r) {
                    flag = true;
                    String strTM = pojo.getTM();
                    if (!"".equals(strLastTM)) {
                        if (strLastTM.equals(strTM)) {
                            flag = false;
                        }
                    }
                    if (flag) {
                        ST_VEL_RPojo wasInfo = new ST_VEL_RPojo();
                        wasInfo.setSTCD(model.getStcd());
                        wasInfo.setTM(pojo.getTM());

                        String upzStr = pojo.getUPZ();
                        Double upzValue = 0.0;
                        if (upzStr != null && !upzStr.trim().isEmpty()) {
                            try {
                                double temp = Double.parseDouble(upzStr);
                                upzValue = Double.parseDouble(String.format("%.2f", temp));
                            } catch (NumberFormatException e) {
                                upzValue = 0.0;
                            }
                        }
                        if (!upzValue.equals(0)) {
                            wasInfo.setVEL(upzValue);
                        }
                        list.add(wasInfo);

                        strLastUPZ = upzValue.toString();
                        strLastTM = strTM;
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println("发生错误：" + ex.getMessage());
        }
        return list;
    }

    // 风向风速
    private List<ST_WDWV_RPojo> SyncWaterDataToListFeng(List<tb_wind_informationPojo> st_was_r,
            V_ST_STBPRP_BTZDto model) {
        List<ST_WDWV_RPojo> list = new ArrayList<>();
        try {
            if (st_was_r.size() > 0) {
                // 如果不需要严格的类型转换校验，只是想把字符串传下去
                String strLastTM = (model.getTm() != null && !model.getTm().trim().isEmpty()) ? model.getTm().trim()
                        : "";
                boolean flag = true;
                for (tb_wind_informationPojo pojo : st_was_r) {
                    flag = true;
                    String strTM = pojo.getDt_time();
                    if (!"".equals(strLastTM)) {
                        if (strLastTM.equals(strTM)) {
                            flag = false;
                        }
                    }
                    if (flag) {
                        ST_WDWV_RPojo wasInfo = new ST_WDWV_RPojo();
                        wasInfo.setSTCD(model.getStcd());
                        wasInfo.setTM(pojo.getDt_time());
                        /*** 风速 */
                        wasInfo.setWNDV(pojo.getNm_speed());
                        int level = WindUtil.getWindLevel(pojo.getNm_speed());
                        /** * 风力 */
                        wasInfo.setWNDPWR(level);
                        /*** 风向 */
                        wasInfo.setWNDDIR(pojo.getSt_direction());
                        wasInfo.setWNANGLE(pojo.getNm_angle());
                        list.add(wasInfo);
                        strLastTM = strTM;
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println("发生错误：" + ex.getMessage());
        }
        return list;
    }

    private List<ST_WDWV_RPojo> SyncWaterDataToListFengRtev(List<ST_WAS_RPojo> st_was_rAll,
            V_ST_STBPRP_BTZDto model) {
        List<ST_WDWV_RPojo> list = new ArrayList<>();
        try {
            List<String> stcdList = Arrays.asList(model.getAdmauth().split(","));// 风速（85结尾）、风向（86结尾）、气压（73结尾）设备码
            if (st_was_rAll.size() > 0) {
                // 如果不需要严格的类型转换校验，只是想把字符串传下去
                String strLastTM = (model.getTm() != null && !model.getTm().trim().isEmpty()) ? model.getTm().trim()
                        : "";
                boolean flag = true;

                // 假设 st_was_r 已经初始化并填充了数据
                Map<String, List<ST_WAS_RPojo>> groupedByTM = st_was_rAll.stream()
                        .collect(Collectors.groupingBy(ST_WAS_RPojo::getTM));
                // 使用传统增强 for 循环遍历
                for (Map.Entry<String, List<ST_WAS_RPojo>> entry : groupedByTM.entrySet()) {
                    String tm = entry.getKey(); // 获取分组的键（即 TM 时间）
                    List<ST_WAS_RPojo> st_was_r = entry.getValue(); // 获取该时间下的对象列表

                    System.out.println("当前时间: " + tm + "，该时间点的数据条数: " + list.size());
                    flag = true;
                    // 继续对 st_was_r进行业务处理...
                    Double windSpeed = 0.0, pressure = 0.0, direction = 0.0;
                    int level = 0;
                    Boolean isWindSpeed = false, isPressure = false, isDirection = false;
                    for (String stcd : stcdList) {
                        boolean result = stcd.endsWith("85");// 风速设备码
                        if (result) {
                            List<ST_WAS_RPojo> st_was_rT = st_was_r.stream().filter(u -> u.getSTCD().equals(stcd))
                                    .collect(Collectors.toList());
                            if (st_was_rT.size() > 0) {
                                isWindSpeed = true;
                                windSpeed = Double.parseDouble(st_was_rT.get(0).getUPZ());
                                level = WindUtil.getWindLevel(windSpeed);
                            }
                        }
                        result = stcd.endsWith("86");// 风向设备码
                        if (result) {
                            List<ST_WAS_RPojo> st_was_rT = st_was_r.stream().filter(u -> u.getSTCD().equals(stcd))
                                    .collect(Collectors.toList());
                            if (st_was_rT.size() > 0) {
                                isDirection = true;
                                direction = Double.parseDouble(st_was_rT.get(0).getUPZ());
                            }
                        }
                        result = stcd.endsWith("73");// 气压设备码
                        if (result) {
                            List<ST_WAS_RPojo> st_was_rT = st_was_r.stream().filter(u -> u.getSTCD().equals(stcd))
                                    .collect(Collectors.toList());
                            if (st_was_rT.size() > 0) {
                                isPressure = true;
                                pressure = Double.parseDouble(st_was_rT.get(0).getUPZ());
                            }
                        }
                    }
                    ;

                    String strTM = tm;
                    if (!"".equals(strLastTM)) {
                        if (strLastTM.equals(strTM)) {
                            flag = false;
                        }
                    }
                    if (flag) {
                        ST_WDWV_RPojo wasInfo = new ST_WDWV_RPojo();
                        wasInfo.setSTCD(model.getStcd());
                        wasInfo.setTM(tm);

                        if (isWindSpeed) {
                            /*** 风速 */
                            wasInfo.setWNDV(windSpeed);
                            /** * 风力 */
                            wasInfo.setWNDPWR(level);
                        }
                        if (isPressure) {
                            /*** 气压 */
                            wasInfo.setPRESSURE(pressure);
                        }
                        if (isDirection) {
                            /*** 风向 */
                            String directionNM = WindUtil.getWindDirection(direction);
                            wasInfo.setWNDDIR(directionNM);
                            wasInfo.setWNANGLE(direction);
                        }
                        if (windSpeed > -1000) {// 存在-10000.0这种值，不存入数据库，是缺测
                            list.add(wasInfo);
                        }
                        strLastTM = strTM;
                    }

                }
            }
        } catch (Exception ex) {
            // System.out.println("发生错误：" + ex.getMessage());
            new javalog().writelog("SyncWaterDataToListFengRtev报错：" + ex.getMessage(), filePathName, "SWZZServiceFX");
        }
        return list;
    }

    private Integer UpdateWaterData(String tab, String type, String stcd) {
        Integer rows = 0;
        try {
            if (tab.equals("st_river_r")) {
                rows = rtsqstStbprpBStcdData.UpdateWaterDataRiver(type);
            } else if (tab.equals("st_tide_r")) {// 潮位站
                rows = rtsqstStbprpBStcdData.UpdateWaterDataTide(type);
            } else if (tab.equals("st_pptn_r")) {// 雨量站
                rows = rtsqstStbprpBStcdData.UpdateWaterDataPptn(type, stcd);
            }
            // else if(tab.equals("st_was_r")){//堰闸水文站
            // rows= rtsqstStbprpBStcdData. UpdateWaterDataWas(type);
            // }

            // else if(tab.equals("st_pump_r")){//泵站
            //
            // }
            // else if(tab.equals("st_rsvr_r")){//水库
            //
            // }
            else if (tab.equals("st_flow_r")) {// 流量站
                rows = rtsqstStbprpBStcdData.UpdateWaterDataFlow(type);
            }
            else if (tab.equals("st_vel_r")) {// 流速站
                rows = rtsqstStbprpBStcdData.UpdateWaterDataVel(type);
            }
             else if (tab.equals("st_wdwv_r")) {// 风速风向站
                rows = rtsqstStbprpBStcdData.UpdateWaterDataFeng(type);
            }
        } catch (Exception e) {
            new javalog().writelog(stcd + "【" + type + "】UpdateWaterData更新最新时间报错：" + e.getMessage(), filePathName);
            // TODO: handle exception
        }

        return rows;
    }

    private Integer UpdateWaterData(String tab, String type, String stcd, String tm) {
        Integer rows = 0;
        if (tab.equals("st_gate_r")) {// 水闸
            if (tm != null && !tm.trim().isEmpty()) {
                rows = rtsqstStbprpBStcdData.UpdateWaterDataGate(type, stcd, tm);
            } else {
                rows = rtsqstStbprpBStcdData.UpdateWaterDataGateAll(type, stcd);
            }
        }
        return rows;
    }

    // 遥测库5分钟雨量数据：修正后的
    private List<ST_PPTN_RPojo> getRTSQ_5MINXZYL(List<String> stcdList, String stime, String etime, String stcd) {
        List<ST_PPTN_RPojo> list = new ArrayList<>();
        // 取时间的整点
        // 1. 定义输入输出的格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String stimeH = "";
        try {
            // 2. 解析字符串为 LocalDateTime 对象
            LocalDateTime dateTime = LocalDateTime.parse(stime, formatter);
            // 3. 核心操作：截断到小时 (分钟和秒自动变为0)
            LocalDateTime truncatedDateTime = dateTime.truncatedTo(java.time.temporal.ChronoUnit.HOURS);
            // 4. 格式化回字符串
            stimeH = truncatedDateTime.format(formatter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // List<TSDBPojo> listTsdb = rtsqstPptnRData.selectTSDBListtTop(stcdList,
        // stimeH, etime);//达梦数据库
        List<TSDBPojo> listTsdb = tsdbData.selectTSDBListtTop(stcdList, stimeH, etime);// 老水情库
        try {
            for (TSDBPojo tsdb : listTsdb) {
                Date time = DateUtil.strToDate(tsdb.getTime(), DateUtil.YMDHMS);
                String senid = tsdb.getSenid().toString();
                for (int i = 0; i < 12; i++) {
                    Date tm = new Date(time.getTime() + i * 5 * 60 * 1000);
                    String tmStr = DateUtil.dateFormat(tm, "yyyy-MM-dd HH:mm:ss"); // 增加分钟数
                    ST_PPTN_RPojo dto = new ST_PPTN_RPojo();
                    dto.setSTCD(stcd);
                    dto.setTM(tmStr);
                    // 获取对应的 v0 到 v11 值
                    String fieldName = "v" + i;
                    Double factv = commonUtills.getFieldValue(tsdb, fieldName);
                    if (factv >= 0) {// 缺测的不记录，因为是一个小时一个记录，还没到的时间没有数据
                        dto.setDRP(factv);
                        list.add(dto);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 执行过滤
        List<ST_PPTN_RPojo> filteredList = list.stream()
                .filter(item -> {
                    // 空值保护：如果 TM 为 null，则排除该数据
                    if (item.getTM() == null) {
                        return false;
                    }
                    // 字符串比较：compareTo > 0 表示 item.TM 晚于 stime
                    return item.getTM().compareTo(stime) > 0;
                })
                .collect(Collectors.toList());
        return filteredList;
    }

    public List<ST_PPTN_RPojo> getRTSQ_5MINXZYLNew(List<String> stcdList, String stime, String etime) {
        List<ST_PPTN_RPojo> list = new ArrayList<>();
        // 取时间的整点
        // 1. 定义输入输出的格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String stimeH = "";
        try {
            // 2. 解析字符串为 LocalDateTime 对象
            LocalDateTime dateTime = LocalDateTime.parse(stime, formatter);
            // 3. 核心操作：截断到小时 (分钟和秒自动变为0)
            LocalDateTime truncatedDateTime = dateTime.truncatedTo(java.time.temporal.ChronoUnit.HOURS);
            // 4. 格式化回字符串
            stimeH = truncatedDateTime.format(formatter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<TSDBPojo> listTsdb = rtsqstPptnRData.selectTSDBList(stcdList, stimeH, etime);
        try {
            stcdList.forEach(stcd -> {
                List<TSDBPojo> listTsdbT = listTsdb.stream().filter(p -> p.getSenid().equals(stcd))
                        .collect(Collectors.toList());
                for (TSDBPojo tsdb : listTsdbT) {
                    Date time = DateUtil.strToDate(tsdb.getTime(), DateUtil.YMDHMS);
                    String senid = tsdb.getSenid().toString();
                    for (int i = 0; i < 12; i++) {
                        Date tm = new Date(time.getTime() + i * 5 * 60 * 1000);
                        String tmStr = DateUtil.dateFormat(tm, "yyyy-MM-dd HH:mm:ss"); // 增加分钟数
                        ST_PPTN_RPojo dto = new ST_PPTN_RPojo();
                        dto.setSTCD(stcd);
                        dto.setTM(tmStr);
                        // 获取对应的 v0 到 v11 值
                        String fieldName = "v" + i;
                        Double factv = commonUtills.getFieldValue(tsdb, fieldName);
                        if (factv >= 0) {// 缺测的不记录，因为是一个小时一个记录，还没到的时间没有数据
                            dto.setDRP(factv);
                            list.add(dto);
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 执行过滤
        List<ST_PPTN_RPojo> filteredList = list.stream()
                .filter(item -> {
                    // 空值保护：如果 TM 为 null，则排除该数据
                    if (item.getTM() == null) {
                        return false;
                    }
                    // 字符串比较：compareTo > 0 表示 item.TM 晚于 stime
                    return item.getTM().compareTo(stime) > 0;
                })
                .collect(Collectors.toList());
        return filteredList;
    }

    public List<ST_PPTN_RPojo> getRTSQ_5MINXZYLHourList(List<TSDBPojo> listTsdb) {
        List<ST_PPTN_RPojo> list = new ArrayList<>();
        try {
            for (TSDBPojo tsdb : listTsdb) {
                double sum = 0.0;
                sum += (tsdb.getV0() != null) ? tsdb.getV0() : 0.0;
                sum += (tsdb.getV1() != null) ? tsdb.getV1() : 0.0;
                sum += (tsdb.getV2() != null) ? tsdb.getV2() : 0.0;
                sum += (tsdb.getV3() != null) ? tsdb.getV3() : 0.0;
                sum += (tsdb.getV4() != null) ? tsdb.getV4() : 0.0;
                sum += (tsdb.getV5() != null) ? tsdb.getV5() : 0.0;
                sum += (tsdb.getV6() != null) ? tsdb.getV6() : 0.0;
                sum += (tsdb.getV7() != null) ? tsdb.getV7() : 0.0;
                sum += (tsdb.getV8() != null) ? tsdb.getV8() : 0.0;
                sum += (tsdb.getV9() != null) ? tsdb.getV9() : 0.0;
                sum += (tsdb.getV10() != null) ? tsdb.getV10() : 0.0;
                sum += (tsdb.getV11() != null) ? tsdb.getV11() : 0.0;
                ST_PPTN_RPojo pojo = new ST_PPTN_RPojo();
                pojo.setSTCD(tsdb.getSenid());
                pojo.setTM(tsdb.getTime());
                pojo.setDRP(sum);
                list.add(pojo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 水务平台*****************************************************************************************************************
    private Integer SyncRealDataSWPT(List<V_ST_STBPRP_BTZDto> listItem) {
        Integer rows = 0;
        String stcd = "", tm = "", stnm = "", tab = "";
        for (V_ST_STBPRP_BTZDto model : listItem) {
            stcd = model.getAdmauth();
            tm = model.getTm();
            stnm = model.getStnm();
            tab = model.getTab();
            if (tm == null || tm.isEmpty()) {
                tm = tongbutm;
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime dateTime = LocalDateTime.parse(tm, formatter);
            LocalDateTime newDateTime = dateTime.plusSeconds(30);// 加上30秒钟
            String stime = newDateTime.format(outputFormatter);
            // String etime = LocalDateTime.parse(stime,
            // formatter).plusDays(6).format(formatter);// 加上6天
            String etime = newDateTime.plusDays(6).format(outputFormatter);

            if (model.getType().equals("1"))// 水位
            {
                // 查询
                List<Map<String, Object>> st_was_r = shuiwupingServer.getShiShiShuiWei(2000, 1, stcd, stime, etime);
                if (tab.equals("st_river_r")) {// 河道水位
                    List<ST_RIVER_RPojo> listR = SyncWaterDataToListRiverSWPT(st_was_r, model);
                    if (listR.size() > 0) {
                        // 插入操作
                        try {
                            rows += stRiverRData.insertAll(listR);
                        } catch (Exception e) {
                            // TODO: handle exception
                            new javalog().writelog("同步外省市【水位】报错：" + e, filePathName, "SyncRealDataSWPT");
                            // 更新最新时间
                            UpdateWaterData(tab, model.getType(), model.getStcd());
                        }
                        if (rows > 0) {
                            // 更新最新时间
                            UpdateWaterData(tab, model.getType(), model.getStcd());
                        }
                    }
                }
            } else if (model.getType().equals("5"))// 流量
            {
                // 查询
                List<Map<String, Object>> st_was_rLL = shuiwupingServer.getLiuLiang(2000, 1, stcd, stime, etime);
                List<ST_FLOW_RPojo> listLL = SyncWaterDataToListFlowSWPT(st_was_rLL, model);
                if (listLL.size() > 0) {
                    // 插入操作
                    try {
                        rows += stFlowRData.insertAll(listLL);
                    } catch (Exception e) {
                        // TODO: handle exception
                        new javalog().writelog("同步外省市【流量】报错：" + e, filePathName, "SyncRealDataLLPT");
                        // 更新最新时间
                        UpdateWaterData(tab, model.getType(), model.getStcd());
                    }
                    if (rows > 0) {
                        // 更新最新时间
                        UpdateWaterData(tab, model.getType(), model.getStcd());
                    }
                }
            }
        }
        return rows;
    }

    private List<ST_RIVER_RPojo> SyncWaterDataToListRiverSWPT(List<Map<String, Object>> st_was_r,
            V_ST_STBPRP_BTZDto model) {
        List<ST_RIVER_RPojo> list = new ArrayList<>();
        try {
            if (st_was_r.size() > 0) {
                String strLastTM = (model.getTm() != null && !model.getTm().trim().isEmpty()) ? model.getTm().trim()
                        : "";
                boolean flag = true;
                for (Map<String, Object> pojo : st_was_r) {
                    flag = true;
                    String strTM = pojo.get("DATETIME").toString();
                    if (!"".equals(strLastTM)) {
                        if (strLastTM.equals(strTM)) {
                            flag = false;
                        }
                    }
                    if (flag) {
                        ST_RIVER_RPojo wasInfo = new ST_RIVER_RPojo();
                        wasInfo.setSTCD(model.getStcd());
                        wasInfo.setTM(strTM);

                        String upzStr = pojo.get("OUTWATER") != null ? pojo.get("OUTWATER").toString() : "";
                        Double upzValue = 0.0;
                        if (upzStr != null && !upzStr.trim().isEmpty()) {
                            try {
                                double temp = Double.parseDouble(upzStr) - 0.26;// 基面不一样
                                upzValue = Double.parseDouble(String.format("%.2f", temp));
                            } catch (NumberFormatException e) {
                                upzValue = 0.0;
                            }
                        } else {
                            upzStr = pojo.get("INWATER") != null ? pojo.get("INWATER").toString() : "";
                            try {
                                double temp = Double.parseDouble(upzStr) - 0.26;// 基面不一样
                                upzValue = Double.parseDouble(String.format("%.2f", temp));
                            } catch (Exception e) {
                                // TODO: handle exception
                            }

                        }
                        if (!upzValue.equals(0)) {
                            wasInfo.setZ(upzValue);
                        }
                        list.add(wasInfo);
                        strLastTM = strTM;
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println("发生错误：" + ex.getMessage());
        }
        return list;
    }

    private List<ST_FLOW_RPojo> SyncWaterDataToListFlowSWPT(List<Map<String, Object>> st_was_r,
            V_ST_STBPRP_BTZDto model) {
        List<ST_FLOW_RPojo> list = new ArrayList<>();
        try {
            if (st_was_r.size() > 0) {
                // 如果不需要严格的类型转换校验，只是想把字符串传下去
                String strLastTM = (model.getTm() != null && !model.getTm().trim().isEmpty()) ? model.getTm().trim()
                        : "";
                boolean flag = true;
                for (Map<String, Object> pojo : st_was_r) {
                    flag = true;
                    String strTM = pojo.get("DATETIME").toString();
                    if (!"".equals(strLastTM)) {
                        if (strLastTM.equals(strTM)) {
                            flag = false;
                        }
                    }
                    if (flag) {
                        ST_FLOW_RPojo wasInfo = new ST_FLOW_RPojo();
                        wasInfo.setSTCD(model.getStcd());
                        wasInfo.setTM(strTM);

                        String qStr = pojo.get("LIULIANG") != null ? pojo.get("LIULIANG").toString() : "";
                        String upzStr = pojo.get("OUTWATER") != null ? pojo.get("OUTWATER").toString() : "";

                        Double qValue = 0.0, upzValue = 0.00;
                        if (qStr != null && !qStr.trim().isEmpty()) {
                            try {
                                double temp = Double.parseDouble(qStr);
                                qValue = Double.parseDouble(String.format("%.2f", temp));
                            } catch (NumberFormatException e) {
                                qValue = 0.0;
                            }
                        }
                        if (!qValue.equals(0)) {
                            wasInfo.setQ(qValue);
                        }

                        if (upzStr != null && !upzStr.trim().isEmpty()) {
                            try {
                                double temp = Double.parseDouble(upzStr);
                                upzValue = Double.parseDouble(String.format("%.2f", temp));
                            } catch (NumberFormatException e) {
                                upzValue = 0.0;
                            }
                        }
                        if (!upzValue.equals(0)) {
                            wasInfo.setZ(upzValue);
                        }
                        list.add(wasInfo);
                        strLastTM = strTM;
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println("发生错误：" + ex.getMessage());
        }
        return list;
    }

    // 应急响应
    public Integer SyncDataYJXY() {
        int count = 0;
        try {
            List<EmergencyResponseInfoPojo> list = new ArrayList<>();
            List<EmergencyResponseInfoPojo> listNew = emergencyResponseInfoData.selectByTMNew(null, null);
            String tm = tongbutm;
            if (listNew != null && listNew.size() > 0) {
                tm = listNew.get(0).getSTART_TIME();
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime dateTime = LocalDateTime.parse(tm, formatter);
            LocalDateTime newDateTime = dateTime.plusSeconds(30);// 加上30秒钟
            String stime = newDateTime.format(outputFormatter);
            String etime = newDateTime.plusDays(180).format(outputFormatter);// 加180天
            List<Map<String, Object>> listMap = shuiwupingServer.getSJYJXY(1, 100000, stime, etime);
            if (listMap.size() > 0) {
                for (Map<String, Object> pojo : listMap) {
                    EmergencyResponseInfoPojo info = new EmergencyResponseInfoPojo();
                    info.setID(pojo.get("ID") == null ? null : pojo.get("ID").toString());
                    info.setSTART_TIME(pojo.get("START_TIME") == null ? null : pojo.get("START_TIME").toString());
                    info.setYJD_NUMBER(pojo.get("YJD_NUMBER") == null ? null : pojo.get("YJD_NUMBER").toString());
                    info.setSIGNAL_STAGE(pojo.get("SIGNAL_STAGE") == null ? null : pojo.get("SIGNAL_STAGE").toString());
                    info.setSIGNAL_LEVEL(pojo.get("SIGNAL_LEVEL") == null ? null : pojo.get("SIGNAL_LEVEL").toString());
                    info.setSIGNAL_CATEGORY(
                            pojo.get("SIGNAL_CATEGORY") == null ? null : pojo.get("SIGNAL_CATEGORY").toString());
                    info.setEARLY_WARNING_NUM(pojo.get("EARLY_WARNING_NUM") == null ? null
                            : Integer.valueOf(pojo.get("EARLY_WARNING_NUM").toString()));
                    info.setTITLE(pojo.get("TITLE") == null ? null : pojo.get("TITLE").toString());
                    info.setCONTENT(pojo.get("CONTENT") == null ? null : pojo.get("CONTENT").toString());
                    info.setQXTYJ_DATE(pojo.get("QXTYJ_DATE") == null ? null : pojo.get("QXTYJ_DATE").toString());
                    list.add(info);
                }
                count = emergencyResponseInfoData.insertAll(list);
            }

        } catch (Exception e) {
            // TODO: handle exception
        }
        return count;
    }

    // 水务平台*****************************************************************************************************************

    // 水闸平台 - 并行化改造
    private Integer SyncRealDataGate(List<V_ST_STBPRP_BTZDto> listItem) {
        if (listItem == null || listItem.isEmpty()) {
            return 0;
        }

        int stationCount = listItem.size();

        // Semaphore 控制同时并发数
        Semaphore semaphore = new Semaphore(MAX_CONCURRENT);

        new javalog().writelog(
                "【水闸】开始并行同步，共" + stationCount + "个站点，最大并发数：" + MAX_CONCURRENT,
                filePathName, "SWZZServiceGate");

        try {
            // 分批提交
            List<CompletableFuture<SyncStationGateResult>> allFutures = new ArrayList<>();

            for (int i = 0; i < stationCount; i += BATCH_SIZE) {
                int end = Math.min(i + BATCH_SIZE, stationCount);
                List<V_ST_STBPRP_BTZDto> batch = listItem.subList(i, end);

                List<CompletableFuture<SyncStationGateResult>> batchFutures = batch.stream()
                        .map(model -> CompletableFuture.supplyAsync(
                                () -> {
                                    try {
                                        semaphore.acquire();
                                        try {
                                            return syncSingleStationGate(model);
                                        } finally {
                                            semaphore.release();
                                        }
                                    } catch (InterruptedException e) {
                                        Thread.currentThread().interrupt();
                                        return SyncStationGateResult.failed(model.getAdmauth(), model.getStnm(),
                                                "线程被中断");
                                    }
                                },
                                syncTaskExecutor))
                        .collect(Collectors.toList());

                allFutures.addAll(batchFutures);
            }

            // 等待全部完成
            CompletableFuture.allOf(allFutures.toArray(new CompletableFuture[0])).join();

            // 汇总结果
            int totalRows = 0;
            int successCount = 0;
            int failCount = 0;
            List<String> errors = new ArrayList<>();

            for (CompletableFuture<SyncStationGateResult> future : allFutures) {
                SyncStationGateResult result = future.join();
                if (result.isSuccess()) {
                    totalRows += result.getRows();
                    successCount++;
                } else {
                    failCount++;
                    errors.add(result.getStnm() + "(" + result.getStcd() + "): " + result.getErrorMsg());
                }
            }

            // 记录汇总日志
            new javalog().writelog(
                    "【水闸】并行同步完成：总计" + stationCount + "个站点，" +
                            "成功" + successCount + "个，失败" + failCount + "个，总入库" + totalRows + "行",
                    filePathName, "SWZZServiceGate");

            if (!errors.isEmpty() && errors.size() <= 20) {
                new javalog().writelog("【水闸】失败站点：" + String.join("; ", errors), filePathName, "SWZZServiceGate");
            }

            return totalRows;
        } catch (Exception e) {
            new javalog().writelog("【水闸】并行同步异常：" + e.getMessage(), filePathName, "SWZZServiceGate");
            return 0;
        }
    }

    // 水闸单站点同步（并行执行单元）
    private SyncStationGateResult syncSingleStationGate(V_ST_STBPRP_BTZDto model) {
        String stcd = model.getAdmauth();
        String stnm = model.getStnm();
        String tab = model.getTab();
        String tm = model.getTm();

        if (tm == null || tm.isEmpty()) {
            tm = tongbutm;
        }

        new javalog().writelog("开始同步水闸数据，水闸编码：" + stcd + ",最新时间:" + tm, filePathName, "SWZZServiceGate");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(tm, formatter);
        LocalDateTime newDateTime = dateTime.plusSeconds(30);
        String stime = newDateTime.format(formatter);
        String etime = LocalDateTime.parse(stime, formatter).plusDays(2).format(formatter);

        int rows = 0;
        try {
            if (model.getType().equals("3")) {
                new javalog().writelog("开始同步水闸数据，水闸编码：" + stcd + ",开始时间:" + stime + "结束时间:" + etime, filePathName,
                        "SWZZServiceGate");
                double sfq = model.getSfq() != null ? model.getSfq() : 0;

                GateWasData item;
                try {
                    item = shuizhaServer.getChuLaoBengZha(1000, 1, stcd, stime, etime, sfq);
                } catch (Exception e) {
                    new javalog().writelog("【水闸】" + stnm + "(" + stcd + ")API超时或异常：" + e.getMessage(), filePathName,
                            "SWZZServiceGate");
                    return SyncStationGateResult.failed(stcd, stnm, "API超时: " + e.getMessage());
                }
                List<ST_GATE_RPojo> st_gate_r = item.getGateDate();
                if (st_gate_r != null) {
                    new javalog().writelog("水闸编码：" + stcd + "的数据长度：" + st_gate_r.size(), filePathName);
                }
                List<swzzmodeserver.workserver.pojo.swzzrtsq.ST_WAS_RPojo> st_was_r = item.getWasDate();

                if (tab.equals("st_gate_r")) {
                    if (st_gate_r != null && st_gate_r.size() > 0) {
                        int count = 2000;
                        int number = st_gate_r.size() / count;
                        if (st_gate_r.size() % count != 0) {
                            number = number + 1;
                        }

                        boolean alreadyLoggedError = false;
                        for (int i = 0; i < number; i++) {
                            List<ST_GATE_RPojo> list;
                            if (i == number - 1) {
                                list = st_gate_r.subList(count * i, st_gate_r.size());
                            } else {
                                list = st_gate_r.subList(count * i, count * (i + 1));
                            }
                            try {
                                rows += rtsqstGateRData.insertAll(list);
                            } catch (Exception ex) {
                                if (!alreadyLoggedError) {
                                    new javalog().writelog(
                                            "【水闸】" + stnm + "(" + stcd + ")发生错误（部分数据已存在），继续处理：" + ex.getMessage(),
                                            filePathName, "SWZZServiceGate");
                                    alreadyLoggedError = true;
                                }
                            }
                        }

                        if (rows > 0) {
                            UpdateWaterData(tab, model.getType(), stcd, null);
                            new javalog().writelog("【水闸】" + stnm + "(" + stcd + ")更新成功：", filePathName,
                                    "SWZZServiceGate");
                        }
                    }

                    try {
                        if (st_was_r != null && st_was_r.size() > 0) {
                            int count = 2000;
                            int number = st_was_r.size() / count;
                            if (st_was_r.size() % count != 0) {
                                number = number + 1;
                            }
                            for (int i = 0; i < number; i++) {
                                List<swzzmodeserver.workserver.pojo.swzzrtsq.ST_WAS_RPojo> list;
                                if (i == number - 1) {
                                    list = st_was_r.subList(count * i, st_was_r.size());
                                } else {
                                    list = st_was_r.subList(count * i, count * (i + 1));
                                }
                                rows += rtsqstWasRData.insertAll(list);
                            }
                        }
                    } catch (Exception e) {
                        // 水位入库异常不影响主流程
                    }
                }
            }
            return SyncStationGateResult.success(stcd, stnm, rows);

        } catch (Exception e) {
            new javalog().writelog("【水闸】" + stnm + "(" + stcd + ")同步异常：" + e.getMessage(), filePathName,
                    "SWZZServiceGate");
            return SyncStationGateResult.failed(stcd, stnm, e.getMessage());
        }
    }

    // 水闸同步结果封装类
    @Data
    public static class SyncStationGateResult {
        private String stcd;
        private String stnm;
        private boolean success;
        private int rows;
        private String errorMsg;

        public static SyncStationGateResult failed(String stcd, String stnm, String errorMsg) {
            SyncStationGateResult result = new SyncStationGateResult();
            result.setStcd(stcd);
            result.setStnm(stnm);
            result.setSuccess(false);
            result.setErrorMsg(errorMsg);
            return result;
        }

        public static SyncStationGateResult success(String stcd, String stnm, int rows) {
            SyncStationGateResult result = new SyncStationGateResult();
            result.setStcd(stcd);
            result.setStnm(stnm);
            result.setSuccess(true);
            result.setRows(rows);
            return result;
        }
    }

    private Integer updateSt_gate_rNew(String stcd) {
        if (stcd == null || stcd.isEmpty()) {
            return 0;
        }
        new javalog().writelog("开始更新站点[" + stcd + "]最新表", filePathName, "SWZZServiceGate");
        int count = rtsqstGateRnewData.upsertByStcd(stcd);
        new javalog().writelog("站点[" + stcd + "]最新表更新完成：" + count + "条", filePathName, "SWZZServiceGate");
        return count;
    }

    private Integer updateSt_gate_rNewAll() {
        new javalog().writelog("开始更新所有站点最新表", filePathName, "SWZZServiceGate");
        int count = rtsqstGateRnewData.upsertAll();
        new javalog().writelog("所有站点最新表更新完成：" + count + "条", filePathName, "SWZZServiceGate");
        return count;
    }

    // ==================== 并行同步配置 ====================
    private static final int MAX_CONCURRENT = 10; // 最多10个站点同时跑
    private static final int BATCH_SIZE = 50; // 每批提交50个任务

    // 雨水泵 - 并行化改造
    private Integer SyncRealDataGateBeng(List<V_ST_STBPRP_BTZDto> listItem) {
        if (listItem == null || listItem.isEmpty()) {
            return 0;
        }

        int stationCount = listItem.size();

        // 2. Semaphore 控制同时并发数
        Semaphore semaphore = new Semaphore(MAX_CONCURRENT);

        new javalog().writelog(
                "【市政泵站】开始并行同步，共" + stationCount + "个站点，最大并发数：" + MAX_CONCURRENT,
                filePathName, "SWZZServiceBeng");

        try {
            // 3. 分批提交（避免一次性创建过多 Future）
            List<CompletableFuture<SyncStationResult>> allFutures = new ArrayList<>();

            for (int i = 0; i < stationCount; i += BATCH_SIZE) {
                int end = Math.min(i + BATCH_SIZE, stationCount);
                List<V_ST_STBPRP_BTZDto> batch = listItem.subList(i, end);

                List<CompletableFuture<SyncStationResult>> batchFutures = batch.stream()
                        .map(model -> CompletableFuture.supplyAsync(
                                () -> {
                                    try {
                                        semaphore.acquire(); // 获取许可证
                                        try {
                                            return syncSingleStationBeng(model);
                                        } finally {
                                            semaphore.release(); // 释放许可证
                                        }
                                    } catch (InterruptedException e) {
                                        Thread.currentThread().interrupt();
                                        return SyncStationResult.failed(model.getAdmauth(), model.getStnm(), "线程被中断");
                                    }
                                },
                                syncTaskExecutor))
                        .collect(Collectors.toList());

                allFutures.addAll(batchFutures);
            }

            // 4. 等待全部完成
            CompletableFuture.allOf(allFutures.toArray(new CompletableFuture[0])).join();

            // 5. 汇总结果
            int totalRows = 0;
            int successCount = 0;
            int failCount = 0;
            List<String> errors = new ArrayList<>();

            for (CompletableFuture<SyncStationResult> future : allFutures) {
                SyncStationResult result = future.join();
                if (result.isSuccess()) {
                    totalRows += result.getRows();
                    successCount++;
                } else {
                    failCount++;
                    errors.add(result.getStnm() + "(" + result.getStcd() + "): " + result.getErrorMsg());
                }
            }

            // 6. 记录汇总日志
            new javalog().writelog(
                    "【市政泵站】并行同步完成：总计" + stationCount + "个站点，" +
                            "成功" + successCount + "个，失败" + failCount + "个，总入库" + totalRows + "行",
                    filePathName, "SWZZServiceBeng");

            if (!errors.isEmpty() && errors.size() <= 20) { // 错误太多只打印前20个
                new javalog().writelog("【市政泵站】失败站点：" + String.join("; ", errors), filePathName, "SWZZServiceBeng");
            }

            return totalRows;
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            new javalog().writelog("【市政泵站】并行同步异常：" + e.getMessage(), filePathName, "SWZZServiceBeng");
            return 0;
        }
    }

    // 市政泵站单站点同步（并行执行单元）
    private SyncStationResult syncSingleStationBeng(V_ST_STBPRP_BTZDto model) {
        String stcd = model.getAdmauth();
        String stnm = model.getStnm();
        String tab = model.getTab();
        String tm = model.getTm();

        if (tm == null || tm.isEmpty()) {
            tm = tongbutm;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(tm, formatter);
        LocalDateTime newDateTime = dateTime.plusSeconds(30);
        String stime = newDateTime.format(formatter);
        String etime = LocalDateTime.parse(stime, formatter).plusDays(5).format(formatter);

        int rows = 0;
        try {
            // 1. 调用API获取数据（30秒超时）
            SZBZGKResponse item = null;
            try {
                CompletableFuture<SZBZGKResponse> future = CompletableFuture
                        .supplyAsync(() -> shuizhaServer.getSZBZGK(1000, 1, stcd, stime, etime));
                item = future.get(30, TimeUnit.SECONDS);
            } catch (Exception e) {
                new javalog().writelog("【市政泵站】" + stnm + "(" + stcd + ")API超时或异常：" + e.getMessage(), filePathName,
                        "SWZZServiceBeng");
                return SyncStationResult.failed(stcd, stnm, "API超时: " + e.getMessage());
            }
            new javalog().writelog("【市政泵站】" + stnm + "站接口结果：" + item, filePathName, "SWZZServiceBeng");

            if (item.getData() != null) {
                List<SZBZGKResponse.ResultItem> result = item.getData().getResult();

                List<ST_GATE_RPojo> st_gate_r = new ArrayList<>();
                List<swzzmodeserver.workserver.pojo.swzzrtsq.ST_WAS_RPojo> st_was_r = new ArrayList<>();

                result.forEach(obj -> {
                    ST_GATE_RPojo pojoGate = new ST_GATE_RPojo();
                    pojoGate.setSTCD(obj.getSTATIONID());
                    pojoGate.setTM(obj.getDATETIME());
                    pojoGate.setEXKEY("1");
                    pojoGate.setEQPTP("泵站状态");
                    pojoGate.setEQPNO("2");
                    pojoGate.setGTQ(obj.getFLOW());
                    st_gate_r.add(pojoGate);

                    swzzmodeserver.workserver.pojo.swzzrtsq.ST_WAS_RPojo pojoWas = new swzzmodeserver.workserver.pojo.swzzrtsq.ST_WAS_RPojo();
                    pojoWas.setStcd(obj.getSTATIONID());
                    pojoWas.setTm(obj.getDATETIME());
                    pojoWas.setUpz(String.valueOf(obj.getPUMPLEVEL()));
                    st_was_r.add(pojoWas);
                });

                // 2. 入库 st_gate_r
                if (tab.equals("st_gate_r")) {
                    if (st_gate_r != null && st_gate_r.size() > 0) {
                        new javalog().writelog("【市政泵站】" + stnm + "站接口结果：" + st_gate_r.size(), filePathName,
                                "SWZZServiceBeng");

                        int count = 2000;
                        int number = st_gate_r.size() / count;
                        if (st_gate_r.size() % count != 0) {
                            number = number + 1;
                        }

                        for (int i = 0; i < number; i++) {
                            List<ST_GATE_RPojo> list;
                            if (i == number - 1) {
                                list = st_gate_r.subList(count * i, st_gate_r.size());
                            } else {
                                list = st_gate_r.subList(count * i, count * (i + 1));
                            }
                            try {
                                rows += rtsqstGateRData.insertAll(list);
                            } catch (Exception ex) {
                                new javalog().writelog(
                                        "【市政泵站】" + stnm + "(" + stcd + ")最新时间（" + tm + "）发生错误：" + ex.getMessage(),
                                        filePathName, "SWZZServiceBeng");
                                UpdateWaterData(tab, model.getType(), model.getStcd(), null);
                            }
                        }

                        // 3. 入库 st_was_r
                        try {
                            if (st_was_r != null && st_was_r.size() > 0) {
                                int countW = 500;
                                int numberW = st_was_r.size() / countW;
                                if (st_was_r.size() % countW != 0) {
                                    numberW = numberW + 1;
                                }
                                for (int i = 0; i < numberW; i++) {
                                    List<swzzmodeserver.workserver.pojo.swzzrtsq.ST_WAS_RPojo> list;
                                    if (i == numberW - 1) {
                                        list = st_was_r.subList(countW * i, st_was_r.size());
                                    } else {
                                        list = st_was_r.subList(countW * i, countW * (i + 1));
                                    }
                                    rows += rtsqstWasRData.insertAll(list);
                                }
                            }
                        } catch (Exception e) {
                            new javalog().writelog("【市政泵站】" + stnm + "(" + stcd + ")水位的入库发生错误：" + e.getMessage(),
                                    filePathName, "SWZZServiceBeng");
                        }

                        // 4. 更新最新表
                        if (rows > 0) {
                            UpdateWaterData(tab, model.getType(), model.getStcd(), null);
                            new javalog().writelog("【市政泵站】" + stnm + "(" + stcd + ")更新成功：", filePathName,
                                    "SWZZServiceBeng");
                        }
                    }
                }
            }
            return SyncStationResult.success(stcd, stnm, rows);

        } catch (Exception e) {
            new javalog().writelog("【市政泵站】" + stnm + "(" + stcd + ")同步异常：" + e.getMessage(), filePathName,
                    "SWZZServiceBeng");
            return SyncStationResult.failed(stcd, stnm, e.getMessage());
        }
    }

    // 市政泵站同步结果封装类
    @Data
    public static class SyncStationResult {
        private String stcd;
        private String stnm;
        private boolean success;
        private int rows;
        private String errorMsg;

        public static SyncStationResult failed(String stcd, String stnm, String errorMsg) {
            SyncStationResult result = new SyncStationResult();
            result.setStcd(stcd);
            result.setStnm(stnm);
            result.setSuccess(false);
            result.setErrorMsg(errorMsg);
            return result;
        }

        public static SyncStationResult success(String stcd, String stnm, int rows) {
            SyncStationResult result = new SyncStationResult();
            result.setStcd(stcd);
            result.setStnm(stnm);
            result.setSuccess(true);
            result.setRows(rows);
            return result;
        }
    }
    // 水闸平台*****************************************************************************************************************
}
