package swzzmodeserver.workserver.service.swzzmode;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import swzzmodeserver.tools.LinearInterpolationUtil;
import swzzmodeserver.tools.ParamField;
import swzzmodeserver.tools.javalog;
import swzzmodeserver.workserver.data.swzzflood.ST_TIDE_RData;
import swzzmodeserver.workserver.data.swzzmode.*;
import swzzmodeserver.workserver.data.swzzqxsj.St_rnfl_fData;
import swzzmodeserver.workserver.data.swzzqxsj.St_tide_rybData;
import swzzmodeserver.workserver.data.swzzqxsj.Tz_watersheddataData;
import swzzmodeserver.workserver.data.swzzrtsq.GetWaterViewNewData;
import swzzmodeserver.workserver.data.swzzrtsq.RTSQST_STBPRP_BData;
import swzzmodeserver.workserver.data.wds.RTEVData;
import swzzmodeserver.workserver.data.swzzflood.RTSQData;
import swzzmodeserver.workserver.pojo.swzzflood.ST_TIDEH_RPojo;
import swzzmodeserver.workserver.pojo.swzzflood.ST_TIDE_RPojo;
import swzzmodeserver.workserver.pojo.swzzmode.*;
import swzzmodeserver.workserver.pojo.swzzqxsj.St_rnfl_fPojo;
import swzzmodeserver.workserver.pojo.swzzqxsj.St_tide_rybPojo;
import swzzmodeserver.workserver.pojo.swzzqxsj.Tz_watersheddataPojo;
import swzzmodeserver.workserver.pojo.swzzrtsq.BigModeGCResponse.DataItem;
import swzzmodeserver.workserver.pojo.swzzrtsq.BigModeGCResponse.DataPoint;
import swzzmodeserver.workserver.pojo.swzzrtsq.GetWaterViewNewPojo;
import swzzmodeserver.workserver.pojo.swzzzjk.ST_TIDEHIGHParam;
import swzzmodeserver.workserver.server.swzzrtsq.bigModeServer;
import swzzmodeserver.workserver.pojo.swzzflood.ST_WAS_RPojo;

import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Function;
// 使用时：Function.identity()

@Service
public class ES_ZHANDIANDATAServiceImpl implements ES_ZHANDIANDATAService {
    private final ES_ZHANDIANDATAData data;
    private final ES_ZHANDIANXSData xsData;
    private final ES_ZHANDIANData esZhandianDataData;
    private final ST_ASTRONOMICALTIDE_RData rData;
    private final St_tide_rybData rybData;
    private final ES_MODELGUANLIANData esModGuData;
    private final ES_MODELFANGANZHANData esModelfanData;
    private final Tz_watersheddataData watersheddataData;
    private final St_rnfl_fData stRnflFData;
    private final ES_ZHANGUANLIANData esZhanguanlianData;
    private final ES_MODELFANGANData esModelfanganData;
    private final ST_ASTRONOMICALTIDE_RData stAstronomicaltideRData;
    private final DD_SOLUTIONData ddSolutionData;
    private final SCHEME_TYPEData schemeTypeData;
    private final BDMS_PREDICTData bdmsPredictData;
    private final RTSQData rtsqData;
    private final ST_TIDE_RData stTideRData;
    private final ST_STBPRP_BData stbprpBData;
    private final St_AstronomicalTide_BData stAstronomicalTideBData;
    private final ES_JISUANZHANData esJisuanzhanData;
    private final ST_RVFCCH_BData stRvfcchBData;
    private final ES_SLTONGJIData esSltongjiData;

    @Autowired
    private ES_TIDALFORECASTGCData es_tidalforecastgcData;
    @Autowired
    private ST_FORECAST_FData st_forecast_fData;

    @Autowired
    private GetWaterViewNewData getWaterViewNewData;

    @Autowired
    private final RTEVData rtevData;

    @Autowired
    private final RTSQST_STBPRP_BData rtsqStbprpBData;
    @Autowired
    private ES_PUMP_RData es_pump_rData;

    @Autowired
    private ES_PUMP_BData es_pump_bData;

    @Autowired
    private ES_ZHANDIANDATA_YUANData es_ZHANDIANDATA_YUANData;

    @Autowired
    private ST_BIGMODEBASE_BData st_bigmodebase_bData;

    @Autowired
    private bigModeServer bigModeServer;

    @Value("${file.path.templatefilepath}")
    private String filePathName;

    @Autowired
    public ES_ZHANDIANDATAServiceImpl(ST_ASTRONOMICALTIDE_RData rData,
            ES_ZHANDIANDATAData data,
            ES_ZHANDIANXSData xsData,
            ES_ZHANDIANData esZhandianDataData,
            St_tide_rybData rybData,
            ES_MODELGUANLIANData esModGuData,
            ES_MODELFANGANZHANData esModelfanData,
            Tz_watersheddataData watersheddataData,
            ST_ASTRONOMICALTIDE_RData stAstronomicaltideRData,
            BDMS_PREDICTData bdmsPredictData,
            DD_SOLUTIONData ddSolutionData,
            SCHEME_TYPEData schemeTypeData,
            St_rnfl_fData stRnflFData,
            ES_ZHANGUANLIANData esZhanguanlianData,
            ES_MODELFANGANData esModelfanganData,
            RTSQData rtsqData,
            ST_TIDE_RData stTideRData,
            ST_STBPRP_BData stbprpBData,
            St_AstronomicalTide_BData stAstronomicalTideBData,
            ES_JISUANZHANData esJisuanzhanData, ST_RVFCCH_BData stRvfcchBData,
            ES_TIDALFORECASTGCData es_tidalforecastgcData,
            ST_FORECAST_FData st_forecast_fData,
            GetWaterViewNewData getWaterViewNewData,
            RTEVData rtevData,
            RTSQST_STBPRP_BData rtsqStbprpBData,
            ES_PUMP_RData es_pump_rData,
            ES_PUMP_BData es_pump_bData,
            ES_SLTONGJIData esSltongjiData,
            ES_ZHANDIANDATA_YUANData es_ZHANDIANDATA_YUANData,
            ST_BIGMODEBASE_BData st_bigmodebase_bData) {
        this.rData = rData;
        this.data = data;
        this.xsData = xsData;
        this.esZhandianDataData = esZhandianDataData;
        this.rybData = rybData;
        this.esModGuData = esModGuData;
        this.esModelfanData = esModelfanData;
        this.watersheddataData = watersheddataData;
        this.stAstronomicaltideRData = stAstronomicaltideRData;
        this.bdmsPredictData = bdmsPredictData;
        this.ddSolutionData = ddSolutionData;
        this.schemeTypeData = schemeTypeData;
        this.stRnflFData = stRnflFData;
        this.esZhanguanlianData = esZhanguanlianData;
        this.esModelfanganData = esModelfanganData;
        this.rtsqData = rtsqData;
        this.stTideRData = stTideRData;
        this.stbprpBData = stbprpBData;
        this.stAstronomicalTideBData = stAstronomicalTideBData;
        this.esJisuanzhanData = esJisuanzhanData;
        this.stRvfcchBData = stRvfcchBData;
        this.es_tidalforecastgcData = es_tidalforecastgcData;
        this.st_forecast_fData = st_forecast_fData;
        this.getWaterViewNewData = getWaterViewNewData;
        this.rtevData = rtevData;
        this.rtsqStbprpBData = rtsqStbprpBData;
        this.es_pump_rData = es_pump_rData;
        this.es_pump_bData = es_pump_bData;
        this.esSltongjiData = esSltongjiData;
        this.es_ZHANDIANDATA_YUANData = es_ZHANDIANDATA_YUANData;
        this.st_bigmodebase_bData = st_bigmodebase_bData;
    }

    @Override
    public Integer FH_modify_batchJY(String zhanid, String solutionid, String dayhour, String zhandata) {
        Integer num = 0;
        // System.out.println("zhanid::::::::::"+zhanid);
        // System.out.println("solutionid::::::::::"+solutionid);
        // /// 1. 134片合并成15大片
        // List<ES_SLTONGJIPojo> esSltongjiList = esSltongjiData.selectList(zhanid,
        // "134", null, null,null);
        List<String> zhanidList = new ArrayList<>();
        // if (esSltongjiList.size() > 0) {
        // zhanidList= Arrays.asList(esSltongjiList.get(0).getSTCD().split(","));

        zhanidList = Arrays.asList(zhanid.split(","));
        List<ES_ZHANDIANDATAPojo> zhandiandataList = data.selectList("", null, null, solutionid, zhanidList, null,
                null);
        List<ES_ZHANDIANDATAPojo> newzhandiandataList = new ArrayList<>();
        if ("day".equals(dayhour)) {
            double value_s = Double.parseDouble(zhandata);
            double dec_val = (double) Math.round(((value_s / 24) * 10) / 10);
            if (value_s > 0 && dec_val < 0.1) {
                dec_val = 0.1;
            }
            int day = (int) Math.ceil((double) zhandiandataList.size() / 24);
            for (int i = 0; i < day; i++) {
                for (int z = 0, j = 0; z < 24; z++, j++) {
                    double value_d = value_s - (dec_val * i);
                    double value_z = i < 23 ? dec_val : value_d;
                    double mValue = value_d <= 0 ? 0 : Math.min(value_d, value_z);
                    String zhantime = zhandiandataList.get(j).getZHANTIME();
                    List<ES_ZHANDIANDATAPojo> filter = zhandiandataList.stream().filter(m -> {
                        if (m.getZHANTIME() != null) {
                            return m.getZHANTIME().equals(zhantime);
                        }
                        return false;
                    }).collect(Collectors.toList());
                    if (filter.size() > 0) {
                        ES_ZHANDIANDATAPojo obj = new ES_ZHANDIANDATAPojo();
                        obj.setID(filter.get(0).getID());
                        obj.setZHANID(filter.get(0).getZHANID());
                        obj.setZHANDATA(String.valueOf(mValue));
                        obj.setSOLUTIONID(solutionid);
                        obj.setZHANTIME(zhantime);
                        newzhandiandataList.add(obj);
                    }
                }
            }
        } else {
            for (ES_ZHANDIANDATAPojo esobj : zhandiandataList) {
                esobj.setZHANDATA(zhandata);
                newzhandiandataList.add(esobj);
            }
        }
        if (newzhandiandataList.size() > 0) {
            int count = 200;
            int number = newzhandiandataList.size() / count;
            if (newzhandiandataList.size() % count != 0) {
                number = number + 1;
            }
            List<ES_ZHANDIANDATAPojo> list = new ArrayList<>();
            for (int i = 0; i < number; i++) {
                if (i == number - 1) {
                    list = newzhandiandataList.subList(count * i, newzhandiandataList.size());
                } else {
                    list = newzhandiandataList.subList(count * i, count * (i + 1));
                }
                num += data.updateALL(list);
            }

        }
        // }
        return num;
    }

    @Override
    public Integer FH_modify_batchJY134(String zhanid, String solutionid, String dayhour, String zhandata) {
        Integer num = 0;
        System.out.println("zhanid::::::::::" + zhanid);
        System.out.println("solutionid::::::::::" + solutionid);
        /// 1. 134片合并成15大片
        List<ES_SLTONGJIPojo> esSltongjiList = esSltongjiData.selectList(zhanid, "134", null, null, null);
        List<String> zhanidList = new ArrayList<>();
        if (esSltongjiList.size() > 0) {
            zhanidList = Arrays.asList(esSltongjiList.get(0).getSTCD().split(","));

            List<ES_ZHANDIANDATAPojo> zhandiandataList = data.selectList("", null, null, solutionid, zhanidList, null,
                    null);
            List<ES_ZHANDIANDATAPojo> newzhandiandataList = new ArrayList<>();
            // if ("day".equals(dayhour)) {
            // 均值模式：总雨量 / 总时段数，每个时段（所有站）分配相同均值
            double value_s = Double.parseDouble(zhandata);
            // 提取所有唯一时刻（保持顺序）
            java.util.LinkedHashSet<String> timeSet = new java.util.LinkedHashSet<>();
            for (ES_ZHANDIANDATAPojo pojo : zhandiandataList) {
                if (pojo.getZHANTIME() != null) {
                    timeSet.add(pojo.getZHANTIME());
                }
            }
            List<String> uniqueTimes = new ArrayList<>(timeSet);
            int totalHours = uniqueTimes.size();
            if (totalHours == 0) {
                totalHours = 1;
            }
            // 均值保留一位小数
            double avgValue = Math.round((value_s / totalHours) * 10) / 10.0;
            if (value_s > 0 && avgValue < 0.1) {
                avgValue = 0.1;
            }
            // 最后一个时段补足舍入误差，保证各时段之和等于输入总值
            double lastValue = value_s - avgValue * (totalHours - 1);
            if (lastValue < 0) {
                lastValue = 0;
            }
            for (ES_ZHANDIANDATAPojo obj : zhandiandataList) {
                String zhantime = obj.getZHANTIME();
                boolean isLast = (zhantime != null && zhantime.equals(uniqueTimes.get(totalHours - 1)));
                double mValue = isLast ? lastValue : avgValue;
                obj.setZHANDATA(String.valueOf(mValue));
                newzhandiandataList.add(obj);
            }
            // } else {
            // for (ES_ZHANDIANDATAPojo esobj : zhandiandataList) {
            // esobj.setZHANDATA(zhandata);
            // newzhandiandataList.add(esobj);
            // }
            // }
            if (newzhandiandataList.size() > 0) {
                int count = 200;
                int number = newzhandiandataList.size() / count;
                if (newzhandiandataList.size() % count != 0) {
                    number = number + 1;
                }
                List<ES_ZHANDIANDATAPojo> list = new ArrayList<>();
                for (int i = 0; i < number; i++) {
                    if (i == number - 1) {
                        list = newzhandiandataList.subList(count * i, newzhandiandataList.size());
                    } else {
                        list = newzhandiandataList.subList(count * i, count * (i + 1));
                    }
                    num += data.updateALL(list);
                }

            }
        }
        return num;
    }

    @Override
    public Integer FH_ModifyMethod(String zhandata, String zhantime, String zhanid, String dayhour, String solutionid) {
        Integer num = 0;
        double value_s = Double.parseDouble(zhandata);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date ztime = null;
        try {
            ztime = format.parse(zhantime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String dd_id = !"".equals(solutionid) ? solutionid : "0";
        if ("day".equals(dayhour)) {
            double value_h = (double) Math.round((value_s / 24) * 10) / 10;
            if (value_s > 0 && value_h < 0.1) {
                value_h = 0.1;
            }
            for (int i = 0; i < 24; i++) {
                double value_d = value_s - (value_h * i);
                double value_z = i < 23 ? value_h : value_d;
                double mValue = value_d <= 0 ? 0 : Math.min(value_d, value_z);
                Date time = null;
                if (null != ztime) {
                    time = new Date(ztime.getTime() + i * 60 * 60 * 1000);
                }
                ES_ZHANDIANDATAPojo pojo = new ES_ZHANDIANDATAPojo();
                pojo.setZHANID(zhanid);
                pojo.setZHANTIME(format.format(time));
                pojo.setZHANDATA(String.valueOf(mValue));
                pojo.setSOLUTIONID(dd_id);
                num += data.updateOne(pojo);
            }
        } else {
            ES_ZHANDIANDATAPojo pojo = new ES_ZHANDIANDATAPojo();
            pojo.setZHANID(zhanid);
            pojo.setZHANTIME(zhantime);
            pojo.setZHANDATA(String.valueOf(value_s));
            pojo.setSOLUTIONID(dd_id);
            num += data.updateOne(pojo);
        }
        return num;
    }

    @Override
    public Integer FH_ModifyMethodJY(String zhandata, String zhantime, String zhanid, String dayhour,
            String solutionid) {
        // 改为134个分区
        List<ES_SLTONGJIPojo> esSltongjiList = esSltongjiData.selectList(zhanid, "134", null, null, null);
        List<String> stcdList = new ArrayList<>();
        if (esSltongjiList.size() > 0) {
            stcdList = Arrays.asList(esSltongjiList.get(0).getSTCD().split(","));
        }
        Integer num = 0;
        double value_s = Double.parseDouble(zhandata);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date ztime = null;
        try {
            ztime = format.parse(zhantime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        List<ES_ZHANDIANDATAPojo> zhandiandataList = new ArrayList<>();
        String dd_id = !"".equals(solutionid) ? solutionid : "0";
        if ("day".equals(dayhour)) {
            double value_h = (double) Math.round((value_s / 24) * 10) / 10;
            if (value_s > 0 && value_h < 0.1) {
                value_h = 0.1;
            }
            for (int i = 0; i < 24; i++) {
                double value_d = value_s - (value_h * i);
                double value_z = i < 23 ? value_h : value_d;
                double mValue = value_d <= 0 ? 0 : Math.min(value_d, value_z);
                Date time = null;
                if (null != ztime) {
                    time = new Date(ztime.getTime() + i * 60 * 60 * 1000);
                }
                for (String stcd : stcdList) {
                    ES_ZHANDIANDATAPojo pojo = new ES_ZHANDIANDATAPojo();
                    pojo.setZHANID(stcd);
                    pojo.setZHANTIME(format.format(time));
                    pojo.setZHANDATA(String.valueOf(mValue));
                    pojo.setSOLUTIONID(dd_id);
                    zhandiandataList.add(pojo);
                }
            }
        } else {
            for (String stcd : stcdList) {
                ES_ZHANDIANDATAPojo pojo = new ES_ZHANDIANDATAPojo();
                pojo.setZHANID(stcd);
                pojo.setZHANTIME(zhantime);
                pojo.setZHANDATA(String.valueOf(value_s));
                pojo.setSOLUTIONID(dd_id);
                zhandiandataList.add(pojo);
            }
        }
        if (zhandiandataList.size() > 0) {
            int count = 500;
            int number = zhandiandataList.size() / count;
            if (zhandiandataList.size() % count != 0) {
                number = number + 1;
            }
            List<ES_ZHANDIANDATAPojo> list = new ArrayList<>();
            for (int i = 0; i < number; i++) {
                if (i == number - 1) {
                    list = zhandiandataList.subList(count * i, zhandiandataList.size());
                } else {
                    list = zhandiandataList.subList(count * i, count * (i + 1));
                }
                num += data.updateALLME(list);
            }
        }
        return num;
    }

    @Override
    public Integer FH_modify_batch(String zhandata, String zhanid, String dayhour, String solutionid) {
        Integer num = 0;
        String dd_id = "0";
        if (!"".equals(solutionid)) {
            dd_id = solutionid;
        }
        List<ES_ZHANDIANDATAPojo> zhandiandataList = data.selectList("", null, null, dd_id,
                Collections.singletonList(zhanid), null, null);
        List<ES_ZHANDIANDATAPojo> newzhandiandataList = new ArrayList<>();
        String mValue = zhandata;
        if ("SW".equals(dayhour)) {
            for (ES_ZHANDIANDATAPojo obj : zhandiandataList) {
                ES_ZHANDIANDATAPojo esobj = new ES_ZHANDIANDATAPojo();
                esobj.setZHANDATA(
                        String.format("%.2f", Double.parseDouble(obj.getZHANDATA()) + Double.parseDouble(mValue)));
                esobj.setZHANID(obj.getZHANID());
                esobj.setID(obj.getID());
                esobj.setSOLUTIONID(dd_id);
                esobj.setZHANTIME(obj.getZHANTIME());
                newzhandiandataList.add(esobj);
            }
        } else {
            for (ES_ZHANDIANDATAPojo obj : zhandiandataList) {
                ES_ZHANDIANDATAPojo esobj = new ES_ZHANDIANDATAPojo();
                esobj.setZHANDATA(String.format("%.2f", Double.parseDouble(mValue)));
                esobj.setZHANID(obj.getZHANID());
                esobj.setID(obj.getID());
                esobj.setSOLUTIONID(dd_id);
                esobj.setZHANTIME(obj.getZHANTIME());
                newzhandiandataList.add(esobj);
            }
        }
        if (newzhandiandataList.size() > 0) {
            int count = 500;
            int number = newzhandiandataList.size() / count;
            if (newzhandiandataList.size() % count != 0) {
                number = number + 1;
            }
            List<ES_ZHANDIANDATAPojo> list = new ArrayList<>();
            for (int i = 0; i < number; i++) {
                if (i == number - 1) {
                    list = newzhandiandataList.subList(count * i, newzhandiandataList.size());
                } else {
                    list = newzhandiandataList.subList(count * i, count * (i + 1));
                }
                num += data.updateALL(list);
            }
        }
        return num;
    }

    @Override
    public Integer chooseTideMethod(String sDate, String eDate, String solutionid, String type) {
        Integer num = 0;
        String DefuldSolutionid = "0";
        if (null != solutionid) {
            DefuldSolutionid = solutionid;
        }
        List<ES_ZHANDIANXSPojo> listXS = xsData.selectList("", null, null);
        List<String> zhandians = Arrays.asList("1728053248", "1728053250", "1728053251", "1728053252");
        List<ES_ZHANDIANPojo> zhandianList = esZhandianDataData.selectList("", null, null,
                Collections.singletonList("1"), "");
        List<ES_ZHANDIANPojo> zhandianListcollect = zhandianList.stream()
                .filter(m -> !zhandians.contains(m.getZHANID())).collect(Collectors.toList());
        List<String> zhanidList = zhandianListcollect.stream().map(ES_ZHANDIANPojo::getZHANID)
                .collect(Collectors.toList());
        List<ES_ZHANDIANDATAPojo> newzhandiandataList = new ArrayList<>();
        List<ES_ZHANDIANDATAPojo> zhandiandataList = data.selectList("", null, null, DefuldSolutionid, null, null,
                null);
        zhandiandataList = zhandiandataList.stream().filter(m -> zhanidList.contains(m.getZHANID()))
                .collect(Collectors.toList());
        if ("Astronomicaltide".equals(type)) {
            List<ST_ASTRONOMICALTIDE_RPojo> listAS = rData.selectList(zhanidList, "", sDate, eDate, null, null, null);
            for (String id : zhanidList) {
                List<ST_ASTRONOMICALTIDE_RPojo> listASTemp = listAS.stream().filter(m -> id.equals(m.getZHANID()))
                        .collect(Collectors.toList());
                List<ES_ZHANDIANDATAPojo> listDataTemp = zhandiandataList.stream().filter(m -> id.equals(m.getZHANID()))
                        .collect(Collectors.toList());
                // List<ES_ZHANDIANXSPojo> listXSTemp = esZhandianxsList.stream().filter(m ->
                // id.equals(m.getMKEYID())).collect(Collectors.toList());
                if (listASTemp.size() > 0) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    for (ES_ZHANDIANDATAPojo dobj : listDataTemp) {
                        List<ST_ASTRONOMICALTIDE_RPojo> listASTempTemp = listASTemp.stream().filter(m ->
                        // dobj.getZHANTIME().equals(m.getTM())
                        {
                            try {
                                return dateFormat.parse(m.getTM()).getTime() == dateFormat.parse(dobj.getZHANTIME())
                                        .getTime();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            return false;
                        }).collect(Collectors.toList());
                        String data = "0";
                        if (listASTempTemp.size() > 0) {
                            data = String.format("%.2f", listASTempTemp.get(0).getZ());
                        }
                        dobj.setZHANDATA(data);
                        newzhandiandataList.add(dobj);
                    }
                }
            }
        }
        // else if("modeTide".equals(type)){
        // List<ES_ZHANDIANDATAPojo> finalZhandiandataList = zhandiandataList;
        // zhandianListcollect.forEach(m->{
        // Map<String,Object> WebPredictDikeDataItem = new HashMap<>();
        // List<String> strlist = new ArrayList<>();
        // WebPredictDikeDataItem.put("mQZT",strlist);
        // List<ES_ZHANDIANDATAPojo> listDataTemp =
        // finalZhandiandataList.stream().filter(i ->
        // i.getZHANID().equals(m.getZHANID())).collect(Collectors.toList());
        // for(int i=0;i<listDataTemp.size();i++){
        // Double DATA = 0.0;
        // if (((List<String>)WebPredictDikeDataItem.get("mQZT")).size() == 1){
        // DATA = Double.parseDouble(((List<String>)
        // WebPredictDikeDataItem.get("mQZT")).get(0));
        // }else {
        // DATA = Double.parseDouble(((List<String>)
        // WebPredictDikeDataItem.get("mQZT")).get(i));
        // }
        // ES_ZHANDIANDATAPojo eszhDataObj = listDataTemp.get(i);
        // eszhDataObj.setZHANDATA(String.valueOf(DATA));
        // newzhandiandataList.add(eszhDataObj);
        // }
        // });
        // }
        else {
            List<St_tide_rybPojo> listTide = new ArrayList<>();
            String ybstcd = "";
            if ("typhoon".equals(type)) {
                ybstcd = "63405800";
            } else if ("temperatezone".equals(type) || "OceanForecastTideNorth".equals(type)
                    || "OceanForecastTideSouth".equals(type)) {
                ybstcd = "10001010";
                if ("OceanForecastTideNorth".equals(type)) {
                    ybstcd = "E17";
                }
                if ("OceanForecastTideSouth".equals(type)) {
                    ybstcd = "E18";
                }
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            // 将字符串解析为LocalDate
            LocalDateTime date = LocalDateTime.parse(sDate, formatter);
            // 从日期中减去7天
            LocalDateTime newSDate = date.minusDays(7);
            // 将新的日期格式化回字符串
            String newSDateStr = newSDate.format(formatter);
            listTide = rybData.selectListByNew(ybstcd, newSDateStr, sDate);
            if (listTide.size() > 0) {
                for (String id : zhanidList) {
                    System.out.println("编号id：" + id);
                    List<ES_ZHANDIANDATAPojo> listDataTemp = zhandiandataList.stream()
                            .filter(m -> m.getZHANID().equals(id)).collect(Collectors.toList());
                    List<ES_ZHANDIANXSPojo> listXSTemp = listXS.stream().filter(m -> m.getMKEYID().equals(id))
                            .collect(Collectors.toList());
                    for (ES_ZHANDIANDATAPojo obj : listDataTemp) {
                        String newDate = obj.getZHANTIME();// .substring(0,obj.getZHANTIME().indexOf(":")) + ":00:00";
                        List<St_tide_rybPojo> listTideTemp = listTide.stream().filter(m -> m.getTM().equals(newDate))
                                .collect(Collectors.toList());
                        if (listTideTemp.size() > 0) {
                            Double tdz = listTideTemp.get(0).getTDZ();
                            Double xs = listXSTemp.get(0).getXS();
                            String DATA = String.format("%.2f", Double.parseDouble(obj.getZHANDATA()) + (tdz * xs));
                            obj.setZHANDATA(DATA);
                        }
                        newzhandiandataList.add(obj);
                    }
                }
            }
        }
        // List<ParamField> paramFieldList = new ArrayList<>();
        // newzhandiandataList.forEach(m->{
        // ParamField obj = new ParamField();
        // obj.setStcd(m.getID());
        // paramFieldList.add(obj);
        // });

        int count = 1000;
        int number = newzhandiandataList.size() / count;
        if (newzhandiandataList.size() % count != 0) {
            number = number + 1;
        }
        List<ES_ZHANDIANDATAPojo> nlist = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            if (i == number - 1) {
                nlist = newzhandiandataList.subList(count * i, newzhandiandataList.size());
            } else {
                nlist = newzhandiandataList.subList(count * i, count * (i + 1));
            }
            num += data.updateALL(nlist);
        }
        // num = batchUpdate(newzhandiandataList);
        return num;
    }

    @Value("${spring.datasource.swzzmode.jdbc-url}")
    private String jdbcUrl;
    @Value("${spring.datasource.swzzmode.username}")
    private String username;
    @Value("${spring.datasource.swzzmode.password}")
    private String password;

    public int batchUpdate(List<ES_ZHANDIANDATAPojo> zlist) {
        int num = 0;
        // SQL更新语句
        String sql = "UPDATE ES_ZHANDIANDATA  SET  ZHANDATA = ? WHERE ID = ?";
        // 加载达梦数据库驱动
        try {
            Class.forName("dm.jdbc.driver.DmDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("达梦数据库驱动未找到！");
            return num;
        }

        // 建立数据库连接
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
            // 设置批量操作模式
            connection.setAutoCommit(false);

            // 创建PreparedStatement
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                // 遍历zlist，为每个对象添加SQL语句
                for (ES_ZHANDIANDATAPojo pojo : zlist) {
                    // preparedStatement.setString(1, pojo.getZHANTIME());
                    preparedStatement.setString(1, pojo.getZHANDATA());
                    preparedStatement.setString(2, pojo.getID());
                    preparedStatement.addBatch(); // 添加到批量操作
                }

                // 执行批量更新
                int[] updateCounts = preparedStatement.executeBatch();

                // 提交事务
                connection.commit();

                // 输出更新结果
                System.out.println("批量更新完成，更新的记录数：");
                for (int count : updateCounts) {
                    System.out.println(count);
                    num += count;
                }
            } catch (SQLException e) {
                // 如果发生异常，回滚事务
                connection.rollback();
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return num;
    }

    @Override
    public Integer TideLineardifference(String solutionid, List<ES_ZHANDIANDATAPojo> list, String stcd, String type,
            String startDate, String endDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<String> zhandians = Arrays.asList("1728053248,1728053250,1728053251,1728053252".split(","));
        List<ES_ZHANDIANDATAPojo> listDataNew = new ArrayList<>();
        List<ES_ZHANDIANPojo> listZhan = esZhandianDataData.selectList("", null, null, Collections.singletonList("1"),
                "");
        if (null == type || "".equals(type)) {
            listZhan = listZhan.stream().filter(m -> !zhandians.contains(m.getZHANID())).collect(Collectors.toList());
        } else {
            listZhan = listZhan.stream().filter(m -> zhandians.contains(m.getZHANID())).collect(Collectors.toList());
        }
        List<String> aggstcd = listZhan.stream().map(ES_ZHANDIANPojo::getZHANID).collect(Collectors.toList());
        List<ES_ZHANDIANDATAPojo> queryable = data.selectList("", null, null, solutionid, aggstcd, startDate, endDate);
        if (!"".equals(stcd)) {
            queryable = queryable.stream().filter(m -> stcd.equals(m.getZHANID())).collect(Collectors.toList());
            // listZhan =
            // listZhan.stream().filter(m->stcd.equals(m.getZHANID())).collect(Collectors.toList());
        }
        List<ES_ZHANDIANDATAPojo> listData = queryable;
        String curTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        System.out.println("************循环之前：" + curTime);
        listData.forEach(m -> {
            String stime = m.getZHANTIME().substring(0, m.getZHANTIME().indexOf(":")) + ":00:00";
            List<ES_ZHANDIANDATAPojo> listTemp = list.stream().filter(n -> {
                try {
                    return dateFormat.parse(n.getZHANTIME()).getTime() == dateFormat.parse(stime).getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return false;
            }).collect(Collectors.toList());
            if (listTemp.size() > 0) {
                String upz = String.format("%.2f", Double.parseDouble(listTemp.get(0).getZHANDATA()));// 边界水位
                if (null == type || "".equals(type)) {
                    upz = String.format("%.2f",
                            Double.parseDouble(listTemp.get(0).getZHANDATA()) + Double.parseDouble(m.getZHANDATA()));// 叠加增水
                }
                m.setZHANDATA(upz);
                listDataNew.add(m);
            }
        });
        curTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        System.out.println("************循环之后：" + curTime);
        int num = 0;
        int count = 1000;
        int number = listDataNew.size() / count;
        if (listDataNew.size() % count != 0) {
            number = number + 1;
        }
        List<ES_ZHANDIANDATAPojo> zlist = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            if (i == number - 1) {
                zlist = listDataNew.subList(count * i, listDataNew.size());
            } else {
                zlist = listDataNew.subList(count * i, count * (i + 1));
            }
            num += data.updateALL(zlist);
        }
        curTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        System.out.println("************入库之后：" + curTime);
        return num;
    }

    @Override
    public Integer TideLineardifferenceXG(String solutionid, List<ES_ZHANDIANDATAPojo> list, String stcd, String type,
            String startDate, String endDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<ES_ZHANDIANDATAPojo> listDataNew = new ArrayList<>();
        List<ES_MODELGUANLIANPojo> listGuan = esModGuData.selectList(null, type, null, null);
        List<String> aggstcd = listGuan.stream().map(ES_MODELGUANLIANPojo::getSTCD).collect(Collectors.toList());
        List<ES_ZHANDIANDATAPojo> queryable = data.selectList("", null, null, solutionid, aggstcd, startDate, endDate);
        if (!"".equals(stcd)) {
            queryable = queryable.stream().filter(m -> stcd.equals(m.getZHANID())).collect(Collectors.toList());
        }
        List<ES_ZHANDIANDATAPojo> listData = queryable;
        String curTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        System.out.println("************循环之前：" + curTime);
        listData.forEach(m -> {
            String stime = m.getZHANTIME().substring(0, m.getZHANTIME().indexOf(":")) + ":00:00";
            List<ES_ZHANDIANDATAPojo> listTemp = list.stream().filter(n -> {
                try {
                    return dateFormat.parse(n.getZHANTIME()).getTime() == dateFormat.parse(stime).getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return false;
            }).collect(Collectors.toList());
            if (listTemp.size() > 0) {
                String upz = String.format("%.2f",
                        Double.parseDouble(listTemp.get(0).getZHANDATA()) + Double.parseDouble(m.getZHANDATA()));// 叠加增水
                m.setZHANDATA(upz);
                listDataNew.add(m);
            }
        });
        curTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        System.out.println("************循环之后：" + curTime);
        int num = 0;
        int count = 1000;
        int number = listDataNew.size() / count;
        if (listDataNew.size() % count != 0) {
            number = number + 1;
        }
        List<ES_ZHANDIANDATAPojo> zlist = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            if (i == number - 1) {
                zlist = listDataNew.subList(count * i, listDataNew.size());
            } else {
                zlist = listDataNew.subList(count * i, count * (i + 1));
            }
            num += data.updateALL(zlist);
        }
        curTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        System.out.println("************入库之后：" + curTime);
        return num;
    }

    @Override
    public Integer ModifyGCSSLLAREAGCGZ(String solutionid, List<String> areaids, List<String> faids) {
        List<ES_MODELGUANLIANPojo> listG = esModGuData.selectList("", "6", null, null).stream()
                .filter(m -> areaids.contains(m.getMKEYID())).collect(Collectors.toList());
        List<String> stcdList = listG.stream().map(ES_MODELGUANLIANPojo::getSTCD).collect(Collectors.toList());

        String stcdListjoined = String.join(", ", stcdList);
        stcdList = Arrays.asList(stcdListjoined.split(","));

        List<ES_ZHANDIANDATAPojo> listZhanData = data.selectListGC(solutionid, "30");
        List<ES_MODELFANGANZHANPojo> listM = esModelfanData.selectList("", null, null, null).stream()
                .filter(m -> faids.contains(m.getFA_ID())).collect(Collectors.toList());

        System.out.println("stcdListjoined：" + stcdListjoined + ",listZhanData.size()：" + listZhanData.size());

        List<ES_ZHANDIANDATAPojo> listZhanDatanew = new ArrayList<>();
        AtomicInteger faidsindex = new AtomicInteger();
        List<String> strIDS = new ArrayList<>();
        listG.forEach(m -> {
            String[] stcds = m.getSTCD().split(",");
            for (int i = 0; i < stcds.length; i++) {
                String stcd = stcds[i];
                // System.out.println("工程调度站码："+stcd);

                List<ES_ZHANDIANDATAPojo> listZhanDataTemp = listZhanData.stream()
                        .filter(n -> stcd.equals(n.getZHANID().trim())).collect(Collectors.toList());
                List<ES_MODELFANGANZHANPojo> listMTemp = listM.stream()
                        .filter(n -> n.getFA_ID().equals(faids.get(faidsindex.get())) && n.getZHANID().equals(stcd))
                        .collect(Collectors.toList());

                // if(stcd.equals("1795166918")){
                System.out.println(stcd + ",listMTemp.size()：" + listMTemp.size() + ",listZhanDataTemp.size()："
                        + listZhanDataTemp.size());
                // }

                if (listMTemp.size() > 0) {
                    listZhanDataTemp.forEach(n -> {
                        n.setZHANDATA(listMTemp.get(0).getNORMAL());
                        listZhanDatanew.add(n);
                    });
                }
            }
            faidsindex.getAndIncrement();
        });

        int num = 0;
        int count = 4500;
        int number = listZhanDatanew.size() / count;
        if (listZhanDatanew.size() % count != 0) {
            number = number + 1;
        }
        List<ES_ZHANDIANDATAPojo> zlist = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            if (i == number - 1) {
                zlist = listZhanDatanew.subList(count * i, listZhanDatanew.size());
            } else {
                zlist = listZhanDatanew.subList(count * i, count * (i + 1));
            }
            num += data.updateALL(zlist);
        }
        return num;
    }

    @Override
    public Integer FH_inset_ModifyApi(String bdms_predictSqlStr, DD_SOLUTIONPojo ddobj, Boolean isGetCookieDD_ID,
            String solutionid) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Integer number = 0;
        String yj_time = ddobj.getDD_TM();
        String strWhere = "";
        String SOLUTIONID = !"".equals(solutionid) ? solutionid : "0";
        isGetCookieDD_ID = null != isGetCookieDD_ID ? isGetCookieDD_ID : true;
        int result = 0;
        boolean existsuccess = false;
        List<DD_SOLUTIONPojo> ddSolution = ddSolutionData.selectList(ddobj.getID(), "", null, null, null, null, null,
                null);
        if (ddSolution.size() > 0) {
            DD_SOLUTIONPojo obj = new DD_SOLUTIONPojo();
            obj.setDD_ID(ddobj.getDD_ID());
            obj.setID(ddobj.getID());
            obj.setDD_NAME(ddobj.getDD_NAME());
            obj.setDD_TM(yj_time);
            obj.setDD_BY(ddobj.getDD_BY());
            obj.setDD_NOTE(ddobj.getDD_NOTE());
            obj.setDD_MIND(ddobj.getDD_MIND());
            obj.setDD_CARRYTM(ddobj.getDD_CARRYTM());
            obj.setDD_EVALUE(ddobj.getDD_EVALUE());
            obj.setDD_CARRYBY(ddobj.getDD_CARRYBY());
            number = ddSolutionData.updateOne(obj);
            if (number > 0) {
                strWhere = "修改成功!";
            } else {
                strWhere = "修改失败!";
            }
        } else {
            DD_SOLUTIONPojo obj = new DD_SOLUTIONPojo();
            obj.setDD_STATUS(ddobj.getDD_STATUS() != null ? ddobj.getDD_STATUS() : "1");
            String DD_MIND = "";
            if (ddobj.getDD_MIND() == null) {
                List<SCHEME_TYPEPojo> listType = schemeTypeData.selectList("", "", yj_time, yj_time, "", "", null,
                        null);
                if (listType.size() > 0) {
                    DD_MIND = listType.get(0).getID();
                } else {
                    List<SCHEME_TYPEPojo> listct = schemeTypeData.selectList("", "", "", "", yj_time, yj_time, null,
                            null);
                    if (listct.size() > 0) {
                        DD_MIND = listct.get(0).getID();
                    } else {
                        DD_MIND = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16);
                        SCHEME_TYPEPojo dto = new SCHEME_TYPEPojo();
                        Date tm = null;
                        try {
                            tm = format.parse(yj_time);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        dto.setID(DD_MIND);
                        dto.setNAME(dateFormat.format(tm) + "预报方案集");
                        dto.setDD_TIME(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                        dto.setYJ_TIME(yj_time);
                        dto.setNOTE(dateFormat.format(tm) + "预报方案集");
                        result = schemeTypeData.insertOne(dto);
                    }
                }
            } else {
                DD_MIND = ddobj.getDD_MIND();
            }
            String userId = ddobj.getDD_FOR();
            obj.setDD_ID(ddobj.getDD_ID());
            obj.setID(ddobj.getID());
            obj.setDD_NAME(ddobj.getDD_NAME());
            obj.setDD_TM(yj_time);
            obj.setDD_BY(ddobj.getDD_BY());
            obj.setDD_NOTE(ddobj.getDD_NOTE());
            obj.setDD_CARRYTM(ddobj.getDD_CARRYTM());
            obj.setDD_EVALUE(ddobj.getDD_EVALUE());
            obj.setDD_CARRYBY(ddobj.getDD_CARRYBY());
            obj.setDD_FOR(userId);
            obj.setDD_MIND(DD_MIND);
            obj.setDD_STANA(ddobj.getDD_STANA());
            obj.setDD_CHECKBY(ddobj.getDD_CHECKBY());
            obj.setDD_DISTRIBY(ddobj.getDD_DISTRIBY());
            result = ddSolutionData.insertOne(obj);
            if (result > 0) {
                List<BDMS_PREDICTPojo> bdmsPredictPojos = JSONArray.parseArray(bdms_predictSqlStr,
                        BDMS_PREDICTPojo.class);
                if (bdmsPredictPojos.size() > 0) {
                    int count = 80;
                    int numbers = bdmsPredictPojos.size() / count;
                    if (bdmsPredictPojos.size() % count != 0) {
                        numbers = numbers + 1;
                    }
                    List<BDMS_PREDICTPojo> zlist = new ArrayList<>();
                    for (int i = 0; i < numbers; i++) {
                        if (i == numbers - 1) {
                            zlist = bdmsPredictPojos.subList(count * i, bdmsPredictPojos.size());
                        } else {
                            zlist = bdmsPredictPojos.subList(count * i, count * (i + 1));
                        }
                        result += bdmsPredictData.insertALL(zlist);
                    }
                    if (result > 0) {
                        existsuccess = true;
                    }
                }
                if (isGetCookieDD_ID) {
                    if (existsuccess) {
                        List<ES_ZHANDIANDATAPojo> List = data.selectList("", null, null, SOLUTIONID, null, null, null);
                        List<ES_ZHANDIANDATAPojo> newList = new ArrayList<>();
                        List.forEach(m -> {
                            m.setID(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16));
                            m.setSOLUTIONID(ddobj.getDD_ID());
                            newList.add(m);
                        });
                        int count = 80;
                        int numbers = newList.size() / count;
                        if (newList.size() % count != 0) {
                            numbers = numbers + 1;
                        }
                        List<ES_ZHANDIANDATAPojo> zlist = new ArrayList<>();
                        for (int i = 0; i < numbers; i++) {
                            if (i == numbers - 1) {
                                zlist = newList.subList(count * i, newList.size());
                            } else {
                                zlist = newList.subList(count * i, count * (i + 1));
                            }
                            number += data.insertALL(zlist);
                        }
                    }
                }
            }
        }
        return number;
    }

    @Override
    @Transactional
    public Integer MODIFY_MODEZHANDData(String startdate, String enddate, String solutionid, String jydatatype,
            String gcdatatype, String scwdatatype, String username) {
        new javalog().writelog("MODIFY_MODEZHANDData：jydatatype******" + jydatatype + "******gcdatatype******"
                + gcdatatype + "******scwdatatype******" + scwdatatype, filePathName);
        String stnm = "";
        // double DRP = 0.0;
        // int FA_INDEX = 0;
        Integer number = 0;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // if(null != FA_INDEX){
        //
        // }
        try {
            long _t0 = System.currentTimeMillis();
            new javalog().writelog("==== MODIFY_MODEZHANDData 开始计时 ====", filePathName, "time");
            new javalog().writelog("参数: startdate=" + startdate + " enddate=" + enddate
                    + " solutionid=" + solutionid + " jydatatype=" + jydatatype
                    + " gcdatatype=" + gcdatatype + " scwdatatype=" + scwdatatype
                    + " username=" + username, filePathName, "time");
            long timeSpan = 0;
            long stimeLong = 0;
            long etimeLong = 0;
            try {
                stimeLong = dateFormat.parse(startdate).getTime();
                etimeLong = dateFormat.parse(enddate).getTime();
                timeSpan = etimeLong - stimeLong;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            int timeCount = (int) timeSpan / (60 * 60 * 1000);
            int dayCount = (int) timeSpan / (24 * 60 * 60 * 1000);
            long hydroDtNo = timeCount * 12;
            List<ES_ZHANDIANDATADto> listData = new ArrayList<>();
            long _t1 = System.currentTimeMillis();
            List<ES_ZHANDIANPojo> list = esZhandianDataData.selectList("", null, null, null, "");
            long _t2 = System.currentTimeMillis();
            List<ES_MODELGUANLIANPojo> listModel = esModGuData.selectList("", "3", null, null);
            long _t3 = System.currentTimeMillis();
            new javalog().writelog("⏱ DB-esZhandianData: " + (_t2 - _t1) + "ms", filePathName, "time");
            new javalog().writelog("⏱ DB-esModGuData: " + (_t3 - _t2) + "ms", filePathName, "time");
            if (jydatatype.contains("SK") || jydatatype.contains("shanghaiyb")) {
                stnm = jydatatype;
                // new javalog().writelog("开始拼雨量边界", filePathName);

                Date curDay = new Date();
                List<Tz_watersheddataPojo> dt = new ArrayList<>();
                String danwei = jydatatype.split("@")[1];
                List<String> FPDR = Arrays.asList("6,48,336".split(","));
                // dt = watersheddataData.selectByTimeAndFPDR(startdate, enddate,
                // dateFormat.format(new Date(stimeLong - 3 * 24 * 60 * 60 * 1000)),
                // dateFormat.format(new Date(stimeLong)), FPDR);

                long _t4 = System.currentTimeMillis();
                dt = watersheddataData.selectListLastByID(startdate, enddate, FPDR, "上海气象台",
                        dateFormat.format(new Date(stimeLong - 3 * 24 * 60 * 60 * 1000)),
                        dateFormat.format(new Date(stimeLong)));
                long _t5 = System.currentTimeMillis();
                new javalog().writelog("⏱ DB-watersheddata: " + (_t5 - _t4) + "ms", filePathName, "time");
                List<ES_ZHANDIANPojo> lists = list.stream().filter(m -> "0".equals(m.getPTYPE()))
                        .collect(Collectors.toList());
                new javalog().writelog("雨量边界站共：" + lists.size() + "个", filePathName);
                // 将dt按 KEYID:FTM 建Map索引，避免循环内全量扫描
                Map<String, Tz_watersheddataPojo> dtMap = new HashMap<>();
                for (Tz_watersheddataPojo item : dt) {
                    if (item.getKEYID() != null && item.getFTM() != null) {
                        dtMap.putIfAbsent(item.getKEYID() + ":" + item.getFTM(), item);
                    }
                }
                long _t6 = System.currentTimeMillis();
                new javalog().writelog("⏱ SK建Map索引: " + (_t6 - _t5) + "ms", filePathName, "time");
                for (ES_ZHANDIANPojo obj : lists) {
                    for (int i = 0; i < timeCount; i++) {
                        ES_ZHANDIANDATADto dto = new ES_ZHANDIANDATADto();
                        dto.setID(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16));
                        dto.setZHANID(obj.getZHANID());
                        String tm = dateFormat.format(new Date(stimeLong + i * 60 * 60 * 1000));
                        dto.setZHANTIME(tm);
                        dto.setZHANDATA("0.0");
                        dto.setSOLUTIONID(solutionid);
                        dto.setDD_FOR(username);
                        Tz_watersheddataPojo dr = dtMap.get(obj.getZHANID() + ":" + tm);
                        if (dr != null) {
                            double hourDrp = dr.getDRP();
                            // List<Tz_watersheddataPojo> drTemp6 = dr.stream().filter(m ->
                            // m.getFPDR().intValue() == 6).collect(Collectors.toList());
                            // List<Tz_watersheddataPojo> drTemp48 = dr.stream().filter(m ->
                            // m.getFPDR().intValue() == 48).collect(Collectors.toList());
                            // if(drTemp6.size() == 0){
                            // hourDrp = drTemp48.size() > 0 ? drTemp48.get(0).getDRP() : 0;
                            // }else {
                            // hourDrp = drTemp6.get(0).getDRP();
                            // }
                            if (hourDrp < 0 || hourDrp > 500) {// 过滤异常值
                                hourDrp = 0;
                            }
                            dto.setZHANDATA(String.valueOf(hourDrp));
                        }
                        listData.add(dto);
                    }
                }
                // new javalog().writelog("雨量边界拼完了，listData的长度："+listData.size(), filePathName);
                long _t7 = System.currentTimeMillis();
                new javalog().writelog("⏱ SK双层循环(" + (lists.size() * timeCount) + "次): " + (_t7 - _t6) + "ms",
                        filePathName, "time");
            } else if (jydatatype.contains("zhongyangyb")) {
                List<St_rnfl_fPojo> listDataRNFL = stRnflFData.selectByHourHX(startdate, enddate,
                        dateFormat.format(new Date(stimeLong - 3 * 24 * 60 * 60 * 1000)),
                        dateFormat.format(new Date(stimeLong)), null);
                List<St_rnfl_fPojo> listDataRNFLNew = new ArrayList<>();
                listDataRNFL.forEach(m -> {
                    int intValue = m.getINTV().intValue();
                    for (int i = intValue; i >= 0; i--) {
                        String ZFP = String.valueOf(Math.round((m.getDRP() / intValue) * 1000) / 1000);
                        St_rnfl_fPojo stRnflFPojo = new St_rnfl_fPojo();
                        BeanUtils.copyProperties(m, stRnflFPojo);
                        Long TMLong = null;
                        try {
                            TMLong = dateFormat.parse(m.getTM()).getTime() - i * 60 * 60 * 1000;
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (null != TMLong) {
                            stRnflFPojo.setTM(dateFormat.format(new Date(TMLong)));
                        }
                        stRnflFPojo.setDRP(Double.valueOf(ZFP));
                        listDataRNFLNew.add(stRnflFPojo);
                    }
                });
                List<ES_ZHANGUANLIANPojo> listDataGuan = esZhanguanlianData.selectList("", null, null,
                        Collections.singletonList("0"));
                List<ES_ZHANDIANPojo> lists = list.stream().filter(m -> "0".equals(m.getPTYPE()))
                        .collect(Collectors.toList());
                for (ES_ZHANDIANPojo obj : lists) {
                    for (int i = 0; i < timeCount; i++) {
                        ES_ZHANDIANDATADto dto = new ES_ZHANDIANDATADto();
                        dto.setID(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16));
                        dto.setZHANID(obj.getZHANID());
                        String tm = dateFormat.format(new Date(stimeLong + i * 60 * 60 * 1000));
                        dto.setZHANTIME(tm);
                        dto.setZHANDATA("0.0");
                        dto.setSOLUTIONID(solutionid);
                        dto.setDD_FOR(username);
                        List<ES_ZHANGUANLIANPojo> listDataGuanTemp = listDataGuan.stream()
                                .filter(m -> obj.getZHANID().equals(m.getZHANID())).collect(Collectors.toList());
                        if (listDataGuanTemp.size() > 0) {
                            List<St_rnfl_fPojo> listDataTemp = listDataRNFLNew.stream().filter(
                                    m -> m.getTM().equals(tm) && m.getSTCD().equals(listDataGuanTemp.get(0).getSTCD()))
                                    .collect(Collectors.toList());
                            if (listDataTemp.size() > 0) {
                                dto.setZHANDATA(String.format("%.1f", listDataTemp.get(0).getDRP()));
                            }
                        }
                        listData.add(dto);
                    }
                }
            }
            // else if(jydatatype.contains("ZDY")){
            // List<ES_ZHANDIANPojo> listJY = list.stream().filter(m ->
            // m.getPTYPE().equals("0")).collect(Collectors.toList());
            // List<String> zhanids =
            // listJY.stream().map(ES_ZHANDIANPojo::getZHANID).collect(Collectors.toList());
            // List<ES_ZHANDIANDATAPojo> tempZDATA = data.selectList("", "", "", solutionid,
            // zhanids);
            // for (ES_ZHANDIANDATAPojo obj : tempZDATA){
            // ES_ZHANDIANDATADto dto = new ES_ZHANDIANDATADto();
            // BeanUtils.copyProperties(obj,dto);
            // dto.setID(UUID.randomUUID().toString().replaceAll("-","").substring(0,16));
            // dto.setSOLUTIONID(solutionid);
            // dto.setDD_FOR(username);
            // listData.add(dto);
            // }
            // }else if (jydatatype.contains("SSJY")){
            //
            // }
            if (gcdatatype.equals("DDFN")) {
                stnm = gcdatatype;
                try {
                    // 分区调度方案
                    long _t8 = System.currentTimeMillis();
                    List<ES_MODELFANGANPojo> listFang = esModelfanganData.selectList(null, null, null);
                    long _t9 = System.currentTimeMillis();
                    new javalog().writelog("⏱ DB-esModelfangan: " + (_t9 - _t8) + "ms", filePathName, "time");
                    // 获取区域调度方案
                    List<ES_ZHANDIANDATA_YUANPojo> listFQ = getSLPDDFN(listData, solutionid, listFang);
                    long _t10 = System.currentTimeMillis();
                    new javalog().writelog("⏱ getSLPDDFN总计: " + (_t10 - _t9) + "ms", filePathName, "time");

                    List<ES_ZHANDIANPojo> listZHAN = list.stream().filter(m -> Integer.parseInt(m.getPTYPE()) >= 3)
                            .collect(Collectors.toList());
                    long finalStimeLong = stimeLong;
                    listZHAN.forEach(m -> {
                        String zhanData = "日常调度";
                        List<ES_ZHANDIANDATA_YUANPojo> listFQT = listFQ.stream()
                                .filter(p -> p.getNEWFA_NAME().contains(m.getZHANID())).collect(Collectors.toList());
                        if (listFQT.size() > 0) {
                            zhanData = listFQT.get(0).getFA_NAME();
                        }
                        ES_ZHANDIANDATADto dto = new ES_ZHANDIANDATADto();
                        dto.setID(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16));
                        dto.setZHANID(m.getZHANID());
                        String tm = dateFormat.format(new Date(finalStimeLong + 1 * 5 * 60 * 1000));
                        dto.setZHANTIME(tm);
                        dto.setZHANDATA(zhanData);
                        dto.setSOLUTIONID(solutionid);
                        dto.setDD_FOR(username);
                        listData.add(dto);
                        // for (int i = 1; i < hydroDtNo + 1; i++) {
                        // ES_ZHANDIANDATADto dto = new ES_ZHANDIANDATADto();
                        // dto.setID(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16));
                        // dto.setZHANID(m.getZHANID());
                        // String tm = dateFormat.format(new Date(finalStimeLong + i * 5 * 60 * 1000));
                        // dto.setZHANTIME(tm);
                        // dto.setZHANDATA(zhanData);
                        // dto.setSOLUTIONID(solutionid);
                        // dto.setDD_FOR(username);
                        // listData.add(dto);
                        // }
                    });
                    long _t11 = System.currentTimeMillis();
                    new javalog().writelog("⏱ DDFN循环(" + (listZHAN.size() * timeCount) + "次): " + (_t11 - _t10) + "ms",
                            filePathName, "time");
                } catch (Exception e) {
                    new javalog().writelog(
                            "报错了：" + e.getMessage(),
                            filePathName, "mode");
                }
            } else if (gcdatatype.equals("fangjiangliang")) {
                stnm = gcdatatype;
                new javalog().writelog("苏州河泵站采用放江量", filePathName, "SWZZServiceFangjiang");
                // 放江量
                List<ES_PUMP_RPojo> listPumpData = es_pump_rData.selectListNew(null, startdate, null);
                System.err.println("放江量数据长度：" + listPumpData.size());
                new javalog().writelog("放江量数据长度：" + listPumpData.size(), filePathName, "SWZZServiceFangjiang");
                // 放江量

                // 分区调度方案
                List<ES_MODELFANGANPojo> listFang = esModelfanganData.selectList(null, null, null);
                // 获取区域调度方案
                List<ES_ZHANDIANDATA_YUANPojo> listFQ = getSLPDDFN(listData, solutionid, listFang);

                List<ES_ZHANDIANPojo> listZHAN = list.stream().filter(m -> Integer.parseInt(m.getPTYPE()) >= 3)
                        .collect(Collectors.toList());
                long finalStimeLong = stimeLong;
                listZHAN.forEach(m -> {
                    String zhanData = "日常调度";
                    List<ES_PUMP_RPojo> listPumpDataTemp = listPumpData.stream()
                            .filter(p -> p.getSTCD().equals(
                                    m.getZHANID().trim()))
                            .collect(Collectors.toList());
                    if (listPumpDataTemp.size() == 0) {
                        List<ES_ZHANDIANDATA_YUANPojo> listFQT = listFQ.stream()
                                .filter(p -> p.getNEWFA_NAME().contains(m.getZHANID())).collect(Collectors.toList());
                        if (listFQT.size() > 0) {
                            zhanData = listFQT.get(0).getFA_NAME();
                        }
                        String tm = dateFormat.format(new Date(finalStimeLong + 1 * 5 * 60 * 1000));
                        ES_ZHANDIANDATADto dto = new ES_ZHANDIANDATADto();
                        dto.setID(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16));
                        dto.setZHANID(m.getZHANID());
                        dto.setZHANTIME(tm);
                        dto.setZHANDATA(zhanData);
                        dto.setSOLUTIONID(solutionid);
                        dto.setDD_FOR(username);
                        listData.add(dto);
                    } else {// 有放江量
                        // System.err.println(m.getZHANID()+"站放江量数据长度："+listPumpDataTemp.size());
                        new javalog().writelog(m.getZHANID() + "站放江量数据长度：" + listPumpDataTemp.size(), filePathName);
                        for (int i = 1; i < hydroDtNo + 1; i++) {
                            String tm = dateFormat.format(new Date(finalStimeLong + i * 5 * 60 * 1000));
                            ES_ZHANDIANDATADto dto = new ES_ZHANDIANDATADto();
                            List<ES_PUMP_RPojo> listPumpDataTempT = listPumpDataTemp.stream()
                                    .filter(p -> p.getTM().equals(tm))
                                    .collect(Collectors.toList());
                            zhanData = listPumpDataTempT.size() > 0 ? listPumpDataTempT.get(0).getPMPQ().toString()
                                    : zhanData;// 有放江量就用放江量

                            dto.setID(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16));
                            dto.setZHANID(m.getZHANID());
                            dto.setZHANTIME(tm);
                            dto.setZHANDATA(zhanData);
                            dto.setSOLUTIONID(solutionid);
                            dto.setDD_FOR(username);
                            listData.add(dto);
                        }
                    }
                });
                // new javalog().writelog("调度工程边界拼完了，listData的长度："+listData.size(),
                // filePathName);
            } else if (gcdatatype.contains("sangezhiliugc")) {// 三个直流，局里调用
                stnm = gcdatatype;
                new javalog().writelog("三个直流，局里调用", filePathName);

                // 分区调度方案
                List<ES_MODELFANGANPojo> listFang = esModelfanganData.selectList(null, null, null);
                // 获取区域调度方案
                List<ES_ZHANDIANDATA_YUANPojo> listFQ = getSLPDDFN(listData, solutionid, listFang);

                // 三个片区的工程需要按照传过来的方案调度：嘉宝北片、蕰南片、淀北片
                // 格式：调度方式(sangezhiliugc)@片区名称1:调度方式1#片区名称2:调度方式2#片区名称3:调度方式3
                // （1）嘉宝北片
                // 防汛防台橙色预警及除涝泵闸限排及应急分流—“应急分流预案”（按照应急调度预案）；
                // 防汛防台橙色预警及强制应急分流—“应急分流实时”（不考虑苏州河及分片代表站水位）
                // （2）蕰南片
                // 应急分流—“应急分流预案”（按照应急调度预案）；
                // 强制应急分流—“应急分流实时”（不考虑苏州河及分片代表站水位）
                // （3）淀北片
                // 应急分流—“应急分流预案”（按照应急调度预案）；
                // 强制应急分流—“应急分流实时”（不考虑苏州河及分片代表站水位）

                String[] ddArray = gcdatatype.split("@");
                String ddStr = ddArray[1];// 应急分流、强制应急分流
                String[] ddStrArray = ddStr.split("#");
                // 将listFang转成Map，key: "片区名:方案名", value: TYPE，方便后续查表
                Map<String, String> fangTypeMap = new HashMap<>();
                if (listFang != null) {
                    for (ES_MODELFANGANPojo fang : listFang) {
                        if (fang.getID() != null && fang.getFA_NAME() != null) {
                            fangTypeMap.put(fang.getID() + ":" + fang.getFA_NAME(), fang.getTYPE());
                        }
                    }
                }

                for (String ddStrItem : ddStrArray) {
                    String[] ddStrItemArray = ddStrItem.split(":");
                    String slpName = ddStrItemArray[0];
                    String ddStrItemValue = ddStrItemArray[1];// 应急分流、强制应急分流
                    String ddfs = "";
                    if (slpName.equals("嘉宝北片") && ddStrItemValue.equals("应急调度预案")) {
                        ddfs = "防汛防台橙色预警及除涝泵闸限排及应急分流";
                    }
                    if (slpName.equals("嘉宝北片") && ddStrItemValue.equals("应急分流实时")) {
                        ddfs = "防汛防台橙色预警及强制应急分流";
                    }
                    if ((slpName.equals("蕰南片") && ddStrItemValue.equals("应急调度预案"))
                            || slpName.equals("淀北片") && ddStrItemValue.equals("应急调度预案")) {
                        ddfs = "应急分流";
                    }
                    if ((slpName.equals("蕰南片") && ddStrItemValue.equals("应急分流实时"))
                            || slpName.equals("淀北片") && ddStrItemValue.equals("应急分流实时")) {
                        ddfs = "强制应急分流";
                    }
                    // 从listFang查找TYPE，更新listFQ对应片区的ZHANDATA
                    if (listFQ != null && !ddfs.isEmpty()) {
                        String type = fangTypeMap.get(slpName + ":" + ddfs);
                        if (type != null) {
                            for (ES_ZHANDIANDATA_YUANPojo pojo : listFQ) {
                                if (slpName.equals(pojo.getZHANID())) {
                                    pojo.setZHANDATA(type);
                                    pojo.setFA_NAME(ddfs);
                                }
                            }
                        }
                        es_ZHANDIANDATA_YUANData.updateALL(listFQ);// 更新一下数据库里面的调度方案
                    }
                }
                List<ES_ZHANDIANPojo> listZHAN = list.stream().filter(m -> Integer.parseInt(m.getPTYPE()) >= 3)
                        .collect(Collectors.toList());
                long finalStimeLong = stimeLong;
                listZHAN.forEach(m -> {
                    String zhanData = "日常调度";
                    List<ES_ZHANDIANDATA_YUANPojo> listFQT = listFQ.stream()
                            .filter(p -> p.getNEWFA_NAME().contains(m.getZHANID())).collect(Collectors.toList());
                    if (listFQT.size() > 0) {
                        zhanData = listFQT.get(0).getFA_NAME();
                        new javalog().writelog("⏱ zhanid：" + m.getZHANID() + "，zhanData: " + zhanData, filePathName,
                                "time");
                    }
                    ES_ZHANDIANDATADto dto = new ES_ZHANDIANDATADto();
                    dto.setID(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16));
                    dto.setZHANID(m.getZHANID());
                    String tm = dateFormat.format(new Date(finalStimeLong + 1 * 5 * 60 * 1000));
                    dto.setZHANTIME(tm);
                    dto.setZHANDATA(zhanData);
                    dto.setSOLUTIONID(solutionid);
                    dto.setDD_FOR(username);
                    listData.add(dto);
                    // for (int i = 1; i < hydroDtNo + 1; i++) {
                    // ES_ZHANDIANDATADto dto = new ES_ZHANDIANDATADto();
                    // dto.setID(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16));
                    // dto.setZHANID(m.getZHANID());
                    // String tm = dateFormat.format(new Date(finalStimeLong + i * 5 * 60 * 1000));
                    // dto.setZHANTIME(tm);
                    // dto.setZHANDATA(zhanData);
                    // dto.setSOLUTIONID(solutionid);
                    // dto.setDD_FOR(username);
                    // listData.add(dto);
                    // }
                });
            }

            long _ta1 = System.currentTimeMillis();
            // 收集PTYPE=1的站ID，走联合主键(ZHANID,TM)索引，避免全表扫
            List<String> tideZhanIdList = list.stream()
                    .filter(m -> "1".equals(m.getPTYPE()))
                    .map(ES_ZHANDIANPojo::getZHANID)
                    .distinct()
                    .collect(Collectors.toList());
            List<ST_ASTRONOMICALTIDE_RPojo> listAS = stAstronomicaltideRData
                    .selectList(tideZhanIdList, null, startdate, enddate, null, null, null)
                    .stream().sorted(Comparator.comparing(ST_ASTRONOMICALTIDE_RPojo::getTM))
                    .collect(Collectors.toList());
            long _ta2 = System.currentTimeMillis();
            new javalog().writelog("⏱ DB-listAS天文潮: " + (_ta2 - _ta1) + "ms 共" + listAS.size() + "条", filePathName,
                    "time");
            if (scwdatatype.contains("modeTide")) {
            } else if (scwdatatype.contains("AppModelXIANGSITide")) {
                String stcdList = "63401750,62701710,63405800,63401100,63401500,63405900";
                List<ST_TIDEHIGHParam> listZS = SWZZ_FLOODTIDEDATA_ST_TIDEHIGH_RSel(stcdList, startdate, enddate, "2");
                listZS.sort((a, b) -> {
                    try {
                        return Math.toIntExact(dateFormat.parse(a.getTM()).getTime())
                                - Math.toIntExact(dateFormat.parse(b.getTM()).getTime());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return 0;
                });
                List<ES_MODELGUANLIANPojo> listGuanlian = esModGuData.selectList("", "3001", null, null);
                // List<String> stcdGuanAgg =
                // listGuanlian.stream().map(ES_MODELGUANLIANPojo::getSTCD).collect(Collectors.toList());
                List<String> zhanGuanAgg = listGuanlian.stream().map(ES_MODELGUANLIANPojo::getMKEYID)
                        .collect(Collectors.toList());
                List<ST_ASTRONOMICALTIDE_RPojo> listASGuan = listAS.stream()
                        .filter(m -> zhanGuanAgg.contains(m.getZHANID())).collect(Collectors.toList());
                listAS = listAS.stream().filter(m -> !zhanGuanAgg.contains(m.getZHANID())).collect(Collectors.toList());
                List<ST_ASTRONOMICALTIDE_RPojo> listASNew = new ArrayList<>();
                listGuanlian.forEach(m -> {
                    List<ST_ASTRONOMICALTIDE_RPojo> listASGuanTemp = listASGuan.stream()
                            .filter(n -> n.getZHANID().equals(m.getMKEYID())).collect(Collectors.toList());
                    List<ST_TIDEHIGHParam> listZSTemp = listZS.stream().filter(n -> n.getSTCD().equals(m.getSTCD()))
                            .collect(Collectors.toList());
                    for (int i = 0; i < listASGuanTemp.size(); i++) {
                        ST_ASTRONOMICALTIDE_RPojo dto = listASGuanTemp.get(i);
                        dto.setZ(Double.parseDouble(listZSTemp.get(i).getZTDZ()) + dto.getZ());
                        listASNew.add(dto);
                    }
                });
                listAS.addAll(listASNew);
                List<St_AstronomicalTide_BPojo> listASB = stAstronomicalTideBData.selectList("", "", "", "", null, null,
                        null);
                List<ST_ASTRONOMICALTIDE_RPojo> listASCHABU = new ArrayList<>();
                List<ST_ASTRONOMICALTIDE_RPojo> finalListAS = listAS;
                listASB.forEach(m -> {
                    List<ST_ASTRONOMICALTIDE_RPojo> listASTemp = finalListAS.stream()
                            .filter(n -> n.getZHANID().equals(m.getZHANID())).collect(Collectors.toList());
                    List<ST_ASTRONOMICALTIDE_RPojo> listASTempA = finalListAS.stream()
                            .filter(n -> n.getZHANID().equals(m.getZHANIDA())).collect(Collectors.toList());
                    List<ST_ASTRONOMICALTIDE_RPojo> listASTempB = finalListAS.stream()
                            .filter(n -> n.getZHANID().equals(m.getZHANIDB())).collect(Collectors.toList());
                    List<ST_ASTRONOMICALTIDE_RPojo> listASTempC = finalListAS.stream()
                            .filter(n -> n.getZHANID().equals(m.getZHANIDC())).collect(Collectors.toList());
                    listASTemp.forEach(n -> {
                        String tm = n.getTM();
                        List<ST_ASTRONOMICALTIDE_RPojo> listASTempAT = listASTempA.stream()
                                .filter(s -> s.getTM().equals(tm)).collect(Collectors.toList());
                        List<ST_ASTRONOMICALTIDE_RPojo> listASTempBT = listASTempB.stream()
                                .filter(s -> s.getTM().equals(tm)).collect(Collectors.toList());
                        List<ST_ASTRONOMICALTIDE_RPojo> listASTempCT = listASTempC.stream()
                                .filter(s -> s.getTM().equals(tm)).collect(Collectors.toList());
                        double ZA = listASTempAT.size() > 0 ? listASTempAT.get(0).getZ() * m.getXSA() : 0;
                        double ZB = listASTempBT.size() > 0 ? listASTempBT.get(0).getZ() * m.getXSB() : 0;
                        double ZC = listASTempCT.size() > 0 ? listASTempCT.get(0).getZ() * m.getXSC() : 0;
                        double Z = ZA + ZB + ZC;
                        Z = Z == 0 ? n.getZ() : Z;
                        n.setZ(Z);
                        listASCHABU.add(n);
                    });
                });
                List<String> zhanidsChabu = listASB.stream().map(St_AstronomicalTide_BPojo::getZHANID)
                        .collect(Collectors.toList());
                listAS = listAS.stream().filter(m -> !zhanidsChabu.contains(m.getZHANID()))
                        .collect(Collectors.toList());
                listAS.addAll(listASCHABU);
                String stcds = "63403500,63301150,63205150,63205350";// "12200083,63301183,12190083,11150083";//陈墓,嘉兴,昆山,平望
                String zhandians = "1728053248,1728053250,1728053251,1728053252";
                List<GetWaterViewNewPojo> listWas = new ArrayList<>();
                String sTime = "";
                try {
                    sTime = dateFormat.format(new Date(dateFormat.parse(startdate).getTime() - 8 * 60 * 60 * 1000));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                try {
                    // listWas = getSCSW(sTime, startdate, Arrays.asList(stcds.split(",")), "2");
                    listWas = getWaterViewNewData.selectListWaterAll(Arrays.asList(stcds.split(",")), sTime, startdate,
                            null, null, null);
                } catch (Exception e) {
                    // e.printStackTrace();
                }

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String SFYMDH = LocalDateTime.parse(startdate, formatter)
                        .minusDays(3)
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                List<String> unitList = Arrays.asList("太湖局,水利部太湖局".split(","));
                List<ST_FORECAST_FPojo> listFor = st_forecast_fData.selectListFast(unitList, SFYMDH, enddate, startdate,
                        enddate);// 太湖局预报的潮位边界，1小时一个数据

                for (ES_ZHANDIANPojo obj : list.stream()
                        .filter(m -> m.getPTYPE().equals("1") || m.getPTYPE().equals("2"))
                        .collect(Collectors.toList())) {
                    List<ST_ASTRONOMICALTIDE_RPojo> listASTemp = listAS.stream()
                            .filter(m -> m.getZHANID().equals(obj.getZHANID())).collect(Collectors.toList());
                    for (int num = 1; num < hydroDtNo + 1; num++) {
                        try {
                            sTime = dateFormat
                                    .format(new Date(dateFormat.parse(startdate).getTime() + num * 5 * 60 * 1000));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        ES_ZHANDIANDATADto dto = new ES_ZHANDIANDATADto();
                        dto.setID(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16));
                        dto.setZHANID(obj.getZHANID());
                        dto.setZHANTIME(sTime);
                        dto.setSOLUTIONID(solutionid);
                        dto.setZHANDATA("0.0");
                        dto.setDD_FOR(username);
                        if (zhandians.contains(obj.getZHANID())) {
                            String swSTCD = "";
                            if ("1728053248".equals(obj.getZHANID())) {// 陈墓
                                swSTCD = "63403500";
                            } else if ("1728053250".equals(obj.getZHANID()))// 嘉兴
                            {
                                swSTCD = "63301150";
                            } else if ("1728053251".equals(obj.getZHANID()))// 昆山
                            {
                                swSTCD = "63205150";
                            } else if ("1728053252".equals(obj.getZHANID()))// 平望
                            {
                                swSTCD = "63205350";
                            }
                            String finalSwSTCD = swSTCD;
                            List<GetWaterViewNewPojo> listWasTemp = listWas.stream()
                                    .filter(m -> m.getSTCD().equals(finalSwSTCD)).collect(Collectors.toList());
                            if (listWasTemp.size() > 0) {
                                double upz = Double.parseDouble(listWasTemp.get(listWasTemp.size() - 1).getUPZ());
                                dto.setZHANDATA(String.format("%.2f", upz));
                            }

                            String finalStime1 = sTime;
                            List<ST_FORECAST_FPojo> listForTemp = listFor.stream()
                                    .filter(m -> m.getSTCD().equals(obj.getZHANID()) && m.getYMDH().equals(finalStime1))
                                    .collect(Collectors.toList());
                            if (listForTemp.size() > 0) {// 就太湖局预报数据就用太湖局的
                                double upz = listForTemp.get(0).getZ();
                                dto.setZHANDATA(String.format("%.2f", upz));
                            }
                        } else {
                            String finalSTime = sTime;
                            List<ST_ASTRONOMICALTIDE_RPojo> listASTempT = listASTemp.stream()
                                    .filter(m -> m.getTM().equals(finalSTime)).collect(Collectors.toList());
                            if (listASTempT.size() > 0) {
                                dto.setZHANDATA(String.format("%.2f", listASTempT.get(0).getZ()));
                            }
                        }
                        listData.add(dto);
                    }
                }
            } else {
                // new javalog().writelog("开始拼水位、流量边界", filePathName);
                long _te1 = System.currentTimeMillis();
                new javalog().writelog("⏱ 进入潮位else分支", filePathName, "time");
                stnm = "预报风暴潮";
                List<St_tide_rybPojo> listTide = new ArrayList<>();
                if (jydatatype.contains("typhoon")) {
                    String ybstcd = "63405800";
                    String rtype = "台风风暴潮";
                    String stime = "";
                    try {
                        Date s = dateFormat.parse(startdate);
                        stime = dateFormat.format(new Date(s.getTime() - 7 * 24 * 60 * 60 * 1000));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    listTide = rybData.selectListByNew(ybstcd, stime, startdate).stream()
                            .filter(m -> m.getRTYPE().equals(rtype)).collect(Collectors.toList());
                } else if (jydatatype.contains("temperatezone") ||
                        jydatatype.contains("OceanForecastTideNorth") ||
                        jydatatype.contains("OceanForecastTideSouth")) {

                    String ybstcd = "10001010";
                    if (jydatatype.contains("OceanForecastTideNorth")) {
                        ybstcd = "E17";
                    } else if (jydatatype.contains("OceanForecastTideSouth")) {
                        ybstcd = "E18";
                    }
                    String stime = "";
                    try {
                        Date s = dateFormat.parse(startdate);
                        stime = dateFormat.format(new Date(s.getTime() - 7 * 24 * 60 * 60 * 1000));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    listTide = rybData.selectListByNew(ybstcd, stime, startdate);
                }
                long _te2 = System.currentTimeMillis();
                new javalog().writelog("⏱ 潮位-tideDB: " + (_te2 - _te1) + "ms", filePathName, "time");
                List<ES_ZHANDIANXSPojo> listXS = xsData.selectList("", null, null);
                long _te3 = System.currentTimeMillis();
                new javalog().writelog("⏱ 潮位-xsData: " + (_te3 - _te2) + "ms", filePathName, "time");
                String stcds = "63403500,63301150,63205150,63205350";// "12200083,63301183,12190083,11150083";//陈墓,嘉兴,昆山,平望
                String zhandians = "1728053248,1728053250,1728053251,1728053252";
                List<GetWaterViewNewPojo> listWas = new ArrayList<>();
                String txtName = "shModeLog" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".txt";
                String stime = "";
                try {
                    Date s = dateFormat.parse(startdate);
                    stime = dateFormat.format(new Date(s.getTime() - 8 * 60 * 60 * 1000));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                // ***************************************如果是插值的预报需要查插值
                List<ES_TIDALFORECASTGCPojo> tideGCPojo = new ArrayList<>();
                List<ES_ZHANGUANLIANPojo> listDataGuanSW = new ArrayList<>();
                if (scwdatatype.contains("样条函数插值") || scwdatatype.contains("余弦曲线插值")) {
                    String maxYBTMString = jydatatype.split("@")[2];
                    String typeCzhi = scwdatatype;
                    List<String> typeList = Collections.singletonList(typeCzhi);
                    tideGCPojo = es_tidalforecastgcData.selectList(null, null, startdate, enddate, null, null, typeList,
                            null, null);// es_tidalforecastgcData.selectList(null,null,null,null,maxYBTMString,maxYBTMString,typeList,null,null);
                    listDataGuanSW = esZhanguanlianData.selectList("", null, null,
                            Collections.singletonList("TIDALFORECASTGC"));
                }
                // ***************************************如果是插值的预报需要查插值
                try {
                    // listWas = getSCSW(stime,startdate,Arrays.asList(stcds.split(",")),"");
                    long _te4 = System.currentTimeMillis();
                    listWas = getWaterViewNewData.selectListWaterAll(Arrays.asList(stcds.split(",")), stime, startdate,
                            null, null, null);
                    long _te5 = System.currentTimeMillis();
                    new javalog().writelog("⏱ 潮位-getWaterViewNew: " + (_te5 - _te4) + "ms 共" + listWas.size() + "条",
                            filePathName, "time");
                } catch (Exception e) {
                    // TODO: handle exception
                }

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String SFYMDH = LocalDateTime.parse(startdate, formatter)
                        .minusDays(3)
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                long _te6 = System.currentTimeMillis();
                List<String> unitList = Arrays.asList("太湖局,水利部太湖局".split(","));
                List<ST_FORECAST_FPojo> listFor = st_forecast_fData.selectListFast(unitList, SFYMDH, enddate,
                        startdate,
                        enddate);// 太湖局预报的潮位边界，1小时一个数据
                long _te7 = System.currentTimeMillis();
                new javalog().writelog("⏱ 潮位-st_forecast_f: " + (_te7 - _te6) + "ms 共" + listFor.size() + "条",
                        filePathName, "time");

                int Minutes = 5;// 潮位5分钟一个数据
                List<ES_ZHANDIANPojo> listTideStation = list.stream()
                        .filter(m -> "1".equals(m.getPTYPE()) || "2".equals(m.getPTYPE()))
                        .collect(Collectors.toList());
                long totalLoopCount = (long) listTideStation.size() * (hydroDtNo + 1);
                long _te8 = System.currentTimeMillis();
                new javalog().writelog(
                        "⏱ 潮位双层循环开始: " + listTideStation.size() + "站×" + (hydroDtNo + 1) + "次=" + totalLoopCount + "次",
                        filePathName, "time");
                // 将listAS按 ZHANID:TM 建Map索引，避免循环内全量扫描
                Map<String, ST_ASTRONOMICALTIDE_RPojo> listASMap = new HashMap<>();
                for (ST_ASTRONOMICALTIDE_RPojo item : listAS) {
                    if (item.getZHANID() != null && item.getTM() != null) {
                        listASMap.putIfAbsent(item.getZHANID() + ":" + item.getTM(), item);
                    }
                }
                for (ES_ZHANDIANPojo obj : listTideStation) {
                    List<ES_ZHANDIANXSPojo> listXSTemp = listXS.stream()
                            .filter(m -> m.getMKEYID().equals(obj.getZHANID())).collect(Collectors.toList());
                    List<ES_ZHANGUANLIANPojo> listDataGuanSWT = listDataGuanSW.stream()
                            .filter(m -> m.getZHANID().equals(obj.getZHANID())).collect(Collectors.toList());
                    for (int num = 1; num < hydroDtNo + 1; num++) {
                        try {
                            Date s = dateFormat.parse(startdate);
                            stime = dateFormat.format(new Date(s.getTime() + num * Minutes * 60 * 1000));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        ES_ZHANDIANDATADto Dto = new ES_ZHANDIANDATADto();
                        Dto.setID(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16));
                        Dto.setZHANID(obj.getZHANID());
                        Dto.setZHANTIME(stime);
                        Dto.setSOLUTIONID(solutionid);
                        Dto.setZHANDATA("0.0");
                        Dto.setDD_FOR(username);
                        // 四个边界站默认采用依据时间的实测潮位：平望,嘉兴,陈墓,昆山
                        if (zhandians.contains(obj.getZHANID())) {
                            String swSTCD = "";
                            if ("1728053248".equals(obj.getZHANID())) {// 陈墓
                                swSTCD = "63403500";
                            } else if ("1728053250".equals(obj.getZHANID()))// 嘉兴
                            {
                                swSTCD = "63301150";
                            } else if ("1728053251".equals(obj.getZHANID()))// 昆山
                            {
                                swSTCD = "63205150";
                            } else if ("1728053252".equals(obj.getZHANID()))// 平望
                            {
                                swSTCD = "63205350";
                            }
                            String finalSwSTCD = swSTCD;
                            List<GetWaterViewNewPojo> listWasTemp = listWas.stream()
                                    .filter(m -> m.getSTCD().equals(finalSwSTCD)).collect(Collectors.toList());
                            if (listWasTemp.size() > 0) {
                                double upz = Double.parseDouble(listWasTemp.get(listWasTemp.size() - 1).getUPZ());
                                Dto.setZHANDATA(String.format("%.2f", upz));
                            }

                            // SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            // SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:00:00");
                            // String formattedTime="";
                            // try {
                            // Date date = inputFormat.parse(stime);
                            // formattedTime = outputFormat.format(date);
                            // } catch (ParseException e) {
                            // e.printStackTrace();
                            // }
                            // String finalFormattedTime = formattedTime;
                            String finalStime1 = stime;
                            List<ST_FORECAST_FPojo> listForTemp = listFor.stream()
                                    .filter(m -> m.getSTCD().equals(obj.getZHANID()) && m.getYMDH().equals(finalStime1))
                                    .collect(Collectors.toList());
                            if (listForTemp.size() > 0) {// 就太湖局预报数据就用太湖局的
                                double upz = listForTemp.get(0).getZ();
                                Dto.setZHANDATA(String.format("%.2f", upz));
                            }
                        } else {
                            Double DATA = 0.0;
                            String finalStime = stime;
                            // if(obj.getZHANID().equals( "1778384920")) {
                            // System.out.println(obj.getZHANID());
                            // System.out.println(finalStime);
                            // List<ST_ASTRONOMICALTIDE_RPojo> listASTempT=listAS.stream().filter(m ->
                            // m.getZHANID().equals(obj.getZHANID())).collect(Collectors.toList());
                            // System.out.println(JSON.toJSON(listASTempT));
                            // }
                            ST_ASTRONOMICALTIDE_RPojo asItem = listASMap.get(obj.getZHANID() + ":" + finalStime);
                            if (asItem != null) {
                                DATA = asItem.getZ();
                            }
                            if (jydatatype.contains("typhoon") || jydatatype.contains("temperatezone")) {
                                String tmNew = stime;// stime.substring(0,stime.indexOf(":")) + ":00:00";
                                List<St_tide_rybPojo> listTideTemp = listTide.stream()
                                        .filter(m -> m.getTM().equals(tmNew)).collect(Collectors.toList());
                                if (listTideTemp.size() > 0) {
                                    double TDZ = listTideTemp.get(0).getTDZ();
                                    double XS = 0;
                                    if (listXSTemp.size() > 0) {
                                        XS = listXSTemp.get(0).getXS();
                                    }
                                    DATA = DATA + TDZ * XS;
                                }
                            }
                            // ***************************************如果是插值计算，需要用到插值
                            if (scwdatatype.contains("样条函数插值") || scwdatatype.contains("余弦曲线插值")) {
                                if (listDataGuanSWT.size() > 0) {
                                    String stcdSW = listDataGuanSWT.get(0).getSTCD();
                                    List<ES_TIDALFORECASTGCPojo> tideGCPojoT = tideGCPojo.stream()
                                            .filter(m -> m.getSTCD().equals(stcdSW) && m.getTM().equals(finalStime))
                                            .collect(Collectors.toList());
                                    if (tideGCPojoT.size() > 0) {
                                        DATA = tideGCPojoT.get(0).getTDZ();
                                    }
                                }
                            }
                            // ***************************************如果是插值计算，需要用到插值
                            Dto.setZHANDATA(String.format("%.2f", DATA));
                        }
                        listData.add(Dto);
                    }
                }
                // new javalog().writelog("水位、流量边界拼完了，listData的长度："+listData.size(),
                // filePathName);
                long _te9 = System.currentTimeMillis();
                new javalog().writelog("⏱ 潮位双层循环结束: " + (_te9 - _te8) + "ms", filePathName, "time");
            }
            if (listData.size() > 0) {
                // try {
                // data.deleteOneBySOLUTIONID(solutionid);
                // } catch (Exception e) {

                // }
                // int count = 3000;
                // int nums = listData.size() / count;
                // if(listData.size() % nums != 0){
                // nums += 1;
                // }
                // List<ES_ZHANDIANDATADto> dtoList = new ArrayList<>();
                // for (int j = 0;j < nums;j++){
                // if(j == nums - 1){
                // dtoList = listData.subList(j * count,listData.size());
                // }else {
                // dtoList = listData.subList(j * count,( j + 1 ) * count);
                // }
                // number += data.insertALLDto(dtoList);
                // }

                long _ti1 = System.currentTimeMillis();
                new javalog().writelog("MODIFY_MODEZHANDData接口listData长度" + listData.size(), filePathName, "modenew");
                int count = 800;
                int nums = listData.size() / count;
                if (nums > 0) {
                    if (listData.size() % nums != 0) {
                        nums += 1;
                    }
                    final int finalNums = nums;
                    final int finalCount = count;
                    int threadCount = Math.min(finalNums, 5);
                    new javalog().writelog("⏱ 入库开始: 每批" + finalCount + "条,共" + finalNums + "批 " + threadCount + "线程并行",
                            filePathName, "time");
                    ExecutorService pool = Executors.newFixedThreadPool(threadCount);
                    List<Future<Integer>> futures = new ArrayList<>();
                    for (int j = 0; j < finalNums; j++) {
                        final int batchIdx = j;
                        futures.add(pool.submit(() -> {
                            List<ES_ZHANDIANDATADto> dtoList;
                            if (batchIdx == finalNums - 1) {
                                dtoList = listData.subList(batchIdx * finalCount, listData.size());
                            } else {
                                dtoList = listData.subList(batchIdx * finalCount, (batchIdx + 1) * finalCount);
                            }
                            long _tij1 = System.currentTimeMillis();
                            int n = data.insertALLDto(dtoList);
                            long _tij2 = System.currentTimeMillis();
                            new javalog().writelog("⏱ 入库第" + (batchIdx + 1) + "/" + finalNums + "批 " + dtoList.size()
                                    + "条: " + (_tij2 - _tij1) + "ms", filePathName, "time");
                            return n;
                        }));
                    }
                    pool.shutdown();
                    pool.awaitTermination(30, TimeUnit.MINUTES);
                    for (Future<Integer> f : futures) {
                        number += f.get();
                    }
                }
                long _ti2 = System.currentTimeMillis();
                new javalog().writelog("⏱ 入库总计: " + (_ti2 - _ti1) + "ms", filePathName, "time");
                new javalog().writelog("MODIFY_MODEZHANDData入库结束：" + number, filePathName, "modenew");
                new javalog().writelog("⏱ 总耗时: " + (System.currentTimeMillis() - _t0) + "ms", filePathName, "time");

            }
        } catch (Exception e) {
            number = 0;
            e.printStackTrace();
            new javalog().writelog("upDataZhandianData接口报错：" + e.getMessage(), filePathName);
        }
        return number;
    }

    private List<ES_ZHANDIANDATA_YUANPojo> getSLPDDFN(List<ES_ZHANDIANDATADto> listData, String solutionid,
            List<ES_MODELFANGANPojo> listFang) {
        List<ES_ZHANDIANDATA_YUANPojo> listFQ = new ArrayList<>();
        // 根据降雨量匹配调度方案：134个分片转成15个大片
        List<ES_ZHANDIANDATADto> listData134 = new ArrayList<>();
        long _gs1 = System.currentTimeMillis();
        List<ES_SLTONGJIPojo> esSltongjiList = esSltongjiData.selectList(null, "134", null, null, null);
        long _gs2 = System.currentTimeMillis();
        List<String> wuPianList = Arrays.asList("嘉宝北片,蕴南片,青松片青浦区,淀北片,中心片".split(","));
        double wupianDrp = 0;
        // 将listData按ZHANID建Map索引，避免循环内全量扫描
        Map<String, List<ES_ZHANDIANDATADto>> listDataMap = new HashMap<>();
        for (ES_ZHANDIANDATADto item : listData) {
            listDataMap.computeIfAbsent(item.getZHANID(), k -> new ArrayList<>()).add(item);
        }
        for (int numII = 0; numII < esSltongjiList.size(); numII++) {
            ES_SLTONGJIPojo esSltongji = esSltongjiList.get(numII);
            List<String> yqIDList = Arrays.asList(esSltongji.getSTCD().split(","));
            double drptotal = 0;
            for (String yqID : yqIDList) {
                List<ES_ZHANDIANDATADto> items = listDataMap.get(yqID);
                if (items != null) {
                    drptotal += items.stream().mapToDouble(n -> Double.parseDouble(n.getZHANDATA())).sum();
                }
            }

            double drp = yqIDList.size() > 0 ? drptotal / yqIDList.size() : 0;// 平均降雨量
            ES_ZHANDIANDATADto dto = new ES_ZHANDIANDATADto();
            dto.setZHANID(esSltongji.getID());
            dto.setZHANDATA(String.format("%.1f", drp));
            listData134.add(dto);

            if (wuPianList.contains(esSltongji.getTITLE())) {
                wupianDrp += drp;
            }

            ES_ZHANDIANDATA_YUANPojo yPojo = getIndexPlan(listFang, esSltongji.getTITLE(), drp, solutionid);
            if (yPojo != null) {
                listFQ.add(yPojo);
            }
        }

        // 苏州河河口闸根据五片平均雨量匹配规则
        double wupianDrpAvg = wupianDrp / wuPianList.size();
        ES_ZHANDIANDATA_YUANPojo yPojoSzh = getIndexPlan(listFang, "苏州河河口闸", wupianDrpAvg, solutionid);
        listFQ.add(yPojoSzh);
        // 苏州河河口闸根据五片平均雨量匹配规则
        long _gs3 = System.currentTimeMillis();

        try {
            if (listFQ.size() > 0) {
                es_ZHANDIANDATA_YUANData.insertALL(listFQ);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        long _gs4 = System.currentTimeMillis();
        new javalog().writelog("  getSLPDDFN耗时: DB-esSltongji=" + (_gs2 - _gs1)
                + "ms 134次计算=" + (_gs3 - _gs2)
                + "ms DB-insertALL=" + (_gs4 - _gs3) + "ms", filePathName, "time");
        // 根据降雨量匹配调度方案
        return listFQ;
    }

    private ES_ZHANDIANDATA_YUANPojo getIndexPlan(List<ES_MODELFANGANPojo> listFang, String areaName, double drp,
            String solutionid) {
        String planIndex = "", ddfs = "";
        List<ES_MODELFANGANPojo> listFangTemp = listFang.stream()
                .filter(n -> n.getID().equals(areaName))
                .collect(Collectors.toList());
        new javalog().writelog(areaName + "的listFangTemp长度：" + listFangTemp.size(),
                filePathName, "mode");
        if (listFangTemp.size() > 0) {
            List<ES_MODELFANGANPojo> listFangTempT = listFangTemp.stream()
                    .filter(n -> n.getMAXDRP() != null && n.getMAXDRP().doubleValue() <= drp)
                    .collect(Collectors.toList());
            planIndex = "4";
            ddfs = "日常调度";
            if (listFangTempT.size() > 0) {
                planIndex = listFangTempT.get(0).getTYPE();
                ddfs = listFangTempT.get(0).getFA_NAME();
            }
            ES_ZHANDIANDATA_YUANPojo yPojo = new ES_ZHANDIANDATA_YUANPojo();
            yPojo.setZHANID(areaName);
            yPojo.setSOLUTIONID(solutionid);
            yPojo.setZHANDATA(planIndex);
            yPojo.setYJZ(listFangTemp.get(0).getYJZ());
            yPojo.setNEWFA_NAME(listFangTemp.get(0).getNEWFA_NAME());
            yPojo.setFA_NAME(ddfs);
            return yPojo;
        } else {
            return null;
        }

    }

    @Override
    public List<Map<String, Object>> YBSHUIWEI(String stime, String etime, String stcd, String plan_n, String type,
            String mkeyid, String solutionid) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<Map<String, Object>> mapList = new ArrayList<>();
        // List<ES_JISUANZHANPojo> es_jisuanzhan = esJisuanzhanData.selectList("", null,
        // "", null, null)
        // .stream().filter(m ->
        // stcd.contains(m.getSTCD())).collect(Collectors.toList());
        if ("1".equals(type)) {
            Date stm = null;
            try {
                stm = new Date(dateFormat.parse(stime).getTime() - 24 * 60 * 60 * 1000);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            stime = dateFormat.format(stm);
        }
        List<ST_WAS_RPojo> _WAS_Rs = getSCSW(stime, etime, Arrays.asList(stcd.split(",")), "");
        // List<ST_STBPRP_BDto> st_stbprp_bAll =
        // stbprpBData.selectListBandStcdByStcdList(null);
        List<swzzmodeserver.workserver.pojo.swzzrtsq.ST_STBPRP_BPojo> st_stbprp_bAll = rtsqStbprpBData.selectList(null,
                null);
        List<ST_RVFCCH_BPojo> st_rvfcch_b = stRvfcchBData.selectList("", "", "", null, null);
        // 降雨
        List<ES_ZHANDIANDATAPojo> _pptn_r = new LinkedList<>();
        if (null != mkeyid) {
            _pptn_r = data.selectList("", null, null, solutionid, Collections.singletonList(mkeyid), null, null);
        }
        List<BDMS_PREDICTPojo> BDMS_Table = bdmsPredictData
                .selectList("", stime, etime, Arrays.asList(plan_n.split(",")), null, type, null, null, "asc", null)
                .stream().filter(m -> stcd.contains(m.getSTCD())).collect(Collectors.toList());
        BDMS_Table.forEach(m -> {
            Map<String, Object> ybParam = new HashMap<>();
            ybParam.put("TM", m.getYMDHM());
            ybParam.put("YBZ", m.getDATA());
            List<swzzmodeserver.workserver.pojo.swzzrtsq.ST_STBPRP_BPojo> st_stbprp_b = st_stbprp_bAll.stream()
                    .filter(n -> {
                        if (n.getSTCD() != null) {
                            return n.getSTCD().equals(m.getSTCD());
                        }
                        return false;
                    }).collect(Collectors.toList());
            if (st_stbprp_b.size() > 0) {
                ybParam.put("STCD", st_stbprp_b.get(0).getSTCD());
                ybParam.put("STNM", st_stbprp_b.get(0).getSTNM());
                ybParam.put("LGTD", st_stbprp_b.get(0).getLGTD());
                ybParam.put("LTTD", st_stbprp_b.get(0).getLTTD());
                // ybParam.put("ZSTCD",st_stbprp_b.get(0).getZSTCD());
                List<ST_RVFCCH_BPojo> st_rvfcch_bTemp = st_rvfcch_b.stream()
                        .filter(n -> n.getSTCD().equals(ybParam.get("STCD"))).collect(Collectors.toList());
                if (st_rvfcch_bTemp.size() > 0) {
                    ybParam.put("WRZ", st_rvfcch_bTemp.get(0).getWRZ());
                    ybParam.put("GRZ", st_rvfcch_bTemp.get(0).getGRZ());
                }
                if (_WAS_Rs.size() > 0) {
                    String curTM = ybParam.get("TM").toString().replaceAll(".000000", "");

                    List<ST_WAS_RPojo> tempZ = _WAS_Rs.stream()
                            .filter(n -> n.getTM().equals(curTM) && n.getSTCD().equals(st_stbprp_b.get(0).getSTCD()))
                            .collect(Collectors.toList());
                    if (tempZ.size() > 0) {
                        ybParam.put("UPZ", tempZ.get(0).getUPZ());
                    }
                }
            }
            mapList.add(ybParam);
        });
        mapList.sort(Comparator.comparing(a -> a.get("TM").toString()));
        return mapList;
    }

    @Override
    public Integer FH_modify_batchJYQuan(String dd_id, List<String> ZhanID, String DayHour, String ZhanData) {
        new javalog().writelog("FH_modify_batchJYQuan开始查询", filePathName, "mode");
        // String strMsg = "";
        List<ES_ZHANDIANDATAPojo> dt = data.selectList(null, null, null, dd_id, ZhanID, null, null);
        new javalog().writelog("FH_modify_batchJYQuan结束查询，查询集合长度：" + dt.size(), filePathName, "mode");
        List<ES_ZHANDIANDATAPojo> list_DataUPDATE = new ArrayList<>();
        List<String> ids = new ArrayList<>();
        if ("SW".equals(DayHour)) {
            for (ES_ZHANDIANDATAPojo obj : dt) {
                double zz = Double.parseDouble(ZhanData);
                ES_ZHANDIANDATAPojo dto = new ES_ZHANDIANDATAPojo();
                BeanUtils.copyProperties(obj, dto);
                dto.setZHANDATA(String.format("%.2f", Double.parseDouble(obj.getZHANDATA()) + zz));
                dto.setSOLUTIONID(dd_id);
                list_DataUPDATE.add(dto);
                ids.add(dto.getID());
            }
        } else if ("DAY".equals(DayHour)) {
            double value_s = Double.parseDouble(ZhanData);
            double value_h = (double) Math.round((value_s / 24) * 10) / 10;
            if (value_s > 0 && value_h < 0.1) {
                value_h = 0.1;
            }
            for (String stcd : ZhanID) {
                List<ES_ZHANDIANDATAPojo> dtTemp = dt.stream().filter(m -> m.getZHANID().equals(stcd))
                        .collect(Collectors.toList());
                int day = (int) Math.ceil((double) dtTemp.size() / 24);
                int index = 0;
                for (int i = 0; i < day; i++) {
                    for (int j = 0; j < 24; j++) {
                        double value_d = value_s - (value_h * j);
                        double value_z = j < 23 ? value_h : value_d;
                        String mValue = value_d <= 0 ? "0"
                                : (value_d < value_z ? String.valueOf(value_d) : String.valueOf(value_z));
                        // String cur = dtTemp.get(index).getZHANTIME();
                        ES_ZHANDIANDATAPojo dto = new ES_ZHANDIANDATAPojo();
                        BeanUtils.copyProperties(dtTemp.get(index), dto);
                        dto.setZHANDATA(mValue);
                        dto.setSOLUTIONID(dd_id);
                        list_DataUPDATE.add(dto);
                        ids.add(dto.getID());
                        index++;
                    }
                }
            }
        } else {
            for (ES_ZHANDIANDATAPojo obj : dt) {
                ES_ZHANDIANDATAPojo dto = new ES_ZHANDIANDATAPojo();
                BeanUtils.copyProperties(obj, dto);
                dto.setZHANDATA(ZhanData);
                dto.setSOLUTIONID(dd_id);
                list_DataUPDATE.add(dto);
                ids.add(dto.getID());
            }
        }
        new javalog().writelog("FH_modify_batchJYQuan开始入库：" + list_DataUPDATE.size(), filePathName, "mode");
        Integer integer = 0;
        if (list_DataUPDATE.size() > 0) {
            // if (ids.size() > 0) {
            // List<ParamField> paramFields = ids.stream().map(m -> {
            // ParamField field = new ParamField();
            // field.setStcd(m);
            // return field;
            // }).collect(Collectors.toList());
            // int rowsDel = data.deleteALL(paramFields);

            // new javalog().writelog("FH_modify_batchJYQuan删除成功，删除行数：" + rowsDel,
            // filePathName, "mode");
            // }

            int count = 1000;
            int number = list_DataUPDATE.size() / count;
            if (list_DataUPDATE.size() % count != 0) {
                number += 1;
            }
            List<ES_ZHANDIANDATAPojo> list = new ArrayList<>();
            for (int i = 0; i < number; i++) {
                if (i == number - 1) {
                    list = list_DataUPDATE.subList(count * i, list_DataUPDATE.size());
                } else {
                    list = list_DataUPDATE.subList(count * i, count * (i + 1));
                }
                integer += data.updateALL(list);
            }
        }
        new javalog().writelog("FH_modify_batchJYQuan结束入库：" + list_DataUPDATE.size(), filePathName, "mode");
        return integer;
    }

    @Override
    public Integer FH_modify_batchJYQuanJY(String dd_id, List<String> ZhanID, String DayHour, String ZhanData) {
        /// 1. 134片合并成15大片
        List<ES_SLTONGJIPojo> esSltongjiList = esSltongjiData.selectListByID(ZhanID, "134", null, null, null);
        List<String> stcdList = new ArrayList<>();
        if (esSltongjiList != null && esSltongjiList.size() > 0) {
            for (ES_SLTONGJIPojo esSltongji : esSltongjiList) {
                // 增加空指针保护，防止 getSTCD() 返回 null 导致报错
                if (esSltongji.getSTCD() != null) {
                    // 将拆分后的数组转换为 List，然后追加到原有的 stcdList 中
                    stcdList.addAll(Arrays.asList(esSltongji.getSTCD().split(",")));
                }
            }
        }
        // System.out.println("stcdList的长度："+stcdList.size());
        new javalog().writelog("FH_modify_batchJYQuanJY分片长度：" + stcdList.size(), filePathName, "mode");
        List<String> zhanidList = new ArrayList<>();
        // String strMsg = "";
        List<ES_ZHANDIANDATAPojo> dt = data.selectList(null, null, null, dd_id, stcdList, null, null);
        new javalog().writelog("FH_modify_batchJYQuanJY查询边界长度：" + dt.size(), filePathName, "mode");

        List<ES_ZHANDIANDATAPojo> list_DataUPDATE = new ArrayList<>();
        List<String> ids = new ArrayList<>();
        if ("DAY".equals(DayHour)) {
            double value_s = Double.parseDouble(ZhanData);
            double value_h = (double) Math.round((value_s / 24) * 10) / 10;
            if (value_s > 0 && value_h < 0.1) {
                value_h = 0.1;
            }
            for (String stcd : stcdList) {
                List<ES_ZHANDIANDATAPojo> dtTemp = dt.stream().filter(m -> m.getZHANID().equals(stcd))
                        .collect(Collectors.toList());
                int day = (int) Math.ceil((double) dtTemp.size() / 24);
                int index = 0;
                for (int i = 0; i < day; i++) {
                    for (int j = 0; j < 24; j++) {
                        double value_d = value_s - (value_h * j);
                        double value_z = j < 23 ? value_h : value_d;
                        String mValue = value_d <= 0 ? "0"
                                : (value_d < value_z ? String.valueOf(value_d) : String.valueOf(value_z));
                        // String cur = dtTemp.get(index).getZHANTIME();
                        ES_ZHANDIANDATAPojo dto = new ES_ZHANDIANDATAPojo();
                        BeanUtils.copyProperties(dtTemp.get(index), dto);
                        dto.setZHANDATA(mValue);
                        dto.setSOLUTIONID(dd_id);
                        list_DataUPDATE.add(dto);
                        ids.add(dto.getID());
                        index++;
                    }
                }
            }
        } else {
            for (ES_ZHANDIANDATAPojo obj : dt) {
                ES_ZHANDIANDATAPojo dto = new ES_ZHANDIANDATAPojo();
                BeanUtils.copyProperties(obj, dto);
                dto.setZHANDATA(ZhanData);
                dto.setSOLUTIONID(dd_id);
                list_DataUPDATE.add(dto);
                ids.add(dto.getID());
            }
        }
        new javalog().writelog("FH_modify_batchJYQuanJY需要修改的长度：" + list_DataUPDATE.size(), filePathName, "mode");
        Integer integer = 0;
        if (list_DataUPDATE.size() > 0) {
            // if (ids.size() > 0) {
            // List<ParamField> paramFields = ids.stream().map(m -> {
            // ParamField field = new ParamField();
            // field.setStcd(m);
            // return field;
            // }).collect(Collectors.toList());
            // data.deleteALL(paramFields);
            // }
            int count = 1000;
            int number = list_DataUPDATE.size() / count;
            if (list_DataUPDATE.size() % count != 0) {
                number += 1;
            }
            List<ES_ZHANDIANDATAPojo> list = new ArrayList<>();
            for (int i = 0; i < number; i++) {
                if (i == number - 1) {
                    list = list_DataUPDATE.subList(count * i, list_DataUPDATE.size());
                } else {
                    list = list_DataUPDATE.subList(count * i, count * (i + 1));
                }
                integer += data.updateALL(list);
            }
        }
        new javalog().writelog("FH_modify_batchJYQuanJY最终修改的长度：" + integer, filePathName, "mode");
        return integer;
    }

    @Override
    public Integer modify_byTM(String TM, String dd_id, List<String> ZhanID, String DayHour, String ZhanData) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<ES_ZHANDIANDATAPojo> dt = data.selectList(null, null, null, dd_id, ZhanID, null, null);
        List<String> ids = new ArrayList<>();
        List<ES_ZHANDIANDATAPojo> list_DataUPDATE = new ArrayList<>();
        if ("SW".equals(DayHour)) {
            List<ES_ZHANDIANDATAPojo> dtTemp = dt.stream().filter(m -> m.getZHANTIME().equals(TM))
                    .collect(Collectors.toList());
            for (ES_ZHANDIANDATAPojo obj : dtTemp) {
                double zz = Double.parseDouble(ZhanData);
                ES_ZHANDIANDATAPojo dto = new ES_ZHANDIANDATAPojo();
                BeanUtils.copyProperties(obj, dto);
                dto.setZHANDATA(String.format("%.2f", Double.parseDouble(obj.getZHANDATA()) + zz));
                dto.setSOLUTIONID(dd_id);
                list_DataUPDATE.add(dto);
                ids.add(dto.getID());
            }
        } else if ("DAY".equals(DayHour)) {
            double value_s = Double.parseDouble(ZhanData);
            double value_h = (double) Math.round((value_s / 24) * 10) / 10;
            if (value_s > 0 && value_h < 0.1) {
                value_h = 0.1;
            }
            try {
                String ETM = dateFormat.format(new Date(dateFormat.parse(TM).getTime() + 24 * 60 * 60 * 1000));
                for (String stcd : ZhanID) {
                    List<ES_ZHANDIANDATAPojo> dtTemp = dt.stream().filter(m -> {
                        return m.getZHANID().equals(stcd) && m.getZHANTIME().compareTo(TM) >= 0
                                && m.getZHANTIME().compareTo(ETM) <= 0;
                    }).collect(Collectors.toList());
                    // int day = (int)Math.ceil( (double) dtTemp.size() / 24);
                    // int index = 0;
                    for (int j = 0; j < 24; j++) {
                        double value_d = value_s - (value_h * j);
                        double value_z = j < 23 ? value_h : value_d;
                        String mValue = value_d <= 0 ? "0"
                                : (value_d < value_z ? String.valueOf(value_d) : String.valueOf(value_z));
                        // String cur = dtTemp.get(index).getZHANTIME();
                        ES_ZHANDIANDATAPojo dto = new ES_ZHANDIANDATAPojo();
                        BeanUtils.copyProperties(dtTemp.get(j), dto);
                        dto.setZHANDATA(mValue);
                        dto.setSOLUTIONID(dd_id);
                        list_DataUPDATE.add(dto);
                        ids.add(dto.getID());
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            List<ES_ZHANDIANDATAPojo> dtTemp = dt.stream().filter(m -> m.getZHANTIME().equals(TM))
                    .collect(Collectors.toList());
            for (ES_ZHANDIANDATAPojo obj : dtTemp) {
                ES_ZHANDIANDATAPojo dto = new ES_ZHANDIANDATAPojo();
                BeanUtils.copyProperties(obj, dto);
                dto.setZHANDATA(ZhanData);
                dto.setSOLUTIONID(dd_id);
                list_DataUPDATE.add(dto);
                ids.add(dto.getID());
            }
        }
        Integer integer = 0;
        if (list_DataUPDATE.size() > 0) {
            if (ids.size() > 0) {
                List<ParamField> paramFields = ids.stream().map(m -> {
                    ParamField field = new ParamField();
                    field.setStcd(m);
                    return field;
                }).collect(Collectors.toList());
                data.deleteALL(paramFields);
            }
            int count = 4500;
            int number = list_DataUPDATE.size() / count;
            if (list_DataUPDATE.size() % count != 0) {
                number += 1;
            }
            List<ES_ZHANDIANDATAPojo> list = new ArrayList<>();
            for (int i = 0; i < number; i++) {
                if (i == number - 1) {
                    list = list_DataUPDATE.subList(count * i, list_DataUPDATE.size());
                } else {
                    list = list_DataUPDATE.subList(count * i, count * (i + 1));
                }
            }
            integer += data.insertALL(list);
        }

        return integer;
    }

    @Override
    public Integer modify_byTMJY(String TM, String dd_id, List<String> ZhanID, String DayHour, String ZhanData) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        List<ES_ZHANDIANPojo> listZH = esZhandianDataData.selectList(null, null, null, Arrays.asList("0".split(",")),
                null);
        ZhanID = listZH.stream().map(ES_ZHANDIANPojo::getZHANID).collect(Collectors.toList());
        List<ES_ZHANDIANDATAPojo> dt = data.selectList(null, null, null, dd_id, ZhanID, null, null);
        List<String> ids = new ArrayList<>();
        List<ES_ZHANDIANDATAPojo> list_DataUPDATE = new ArrayList<>();
        if ("SW".equals(DayHour) || "sw".equals(DayHour)) {
            List<ES_ZHANDIANDATAPojo> dtTemp = dt.stream().filter(m -> m.getZHANTIME().equals(TM))
                    .collect(Collectors.toList());
            for (ES_ZHANDIANDATAPojo obj : dtTemp) {
                double zz = Double.parseDouble(ZhanData);
                ES_ZHANDIANDATAPojo dto = new ES_ZHANDIANDATAPojo();
                BeanUtils.copyProperties(obj, dto);
                dto.setZHANDATA(String.format("%.2f", Double.parseDouble(obj.getZHANDATA()) + zz));
                dto.setSOLUTIONID(dd_id);
                list_DataUPDATE.add(dto);
                ids.add(dto.getID());
            }
        } else if ("DAY".equals(DayHour) || "day".equals(DayHour)) {
            double value_s = Double.parseDouble(ZhanData);
            double value_h = (double) Math.round((value_s / 24) * 10) / 10;
            if (value_s > 0 && value_h < 0.1) {
                value_h = 0.1;
            }
            try {
                String ETM = dateFormat.format(new Date(dateFormat.parse(TM).getTime() + 24 * 60 * 60 * 1000));
                for (String stcd : ZhanID) {
                    List<ES_ZHANDIANDATAPojo> dtTemp = dt.stream().filter(m -> {
                        return m.getZHANID().equals(stcd) && m.getZHANTIME().compareTo(TM) >= 0
                                && m.getZHANTIME().compareTo(ETM) <= 0;
                    }).collect(Collectors.toList());
                    // int day = (int)Math.ceil( (double) dtTemp.size() / 24);
                    // int index = 0;
                    for (int j = 0; j < 24; j++) {
                        double value_d = value_s - (value_h * j);
                        double value_z = j < 23 ? value_h : value_d;
                        String mValue = value_d <= 0 ? "0"
                                : (value_d < value_z ? String.valueOf(value_d) : String.valueOf(value_z));
                        // String cur = dtTemp.get(index).getZHANTIME();
                        ES_ZHANDIANDATAPojo dto = new ES_ZHANDIANDATAPojo();
                        BeanUtils.copyProperties(dtTemp.get(j), dto);
                        dto.setZHANDATA(mValue);
                        dto.setSOLUTIONID(dd_id);
                        list_DataUPDATE.add(dto);
                        ids.add(dto.getID());
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            List<ES_ZHANDIANDATAPojo> dtTemp = dt.stream().filter(m -> m.getZHANTIME().equals(TM))
                    .collect(Collectors.toList());
            for (ES_ZHANDIANDATAPojo obj : dtTemp) {
                ES_ZHANDIANDATAPojo dto = new ES_ZHANDIANDATAPojo();
                BeanUtils.copyProperties(obj, dto);
                dto.setZHANDATA(ZhanData);
                dto.setSOLUTIONID(dd_id);
                list_DataUPDATE.add(dto);
                ids.add(dto.getID());
            }
        }
        Integer integer = 0;
        if (list_DataUPDATE.size() > 0) {
            if (ids.size() > 0) {
                List<ParamField> paramFields = ids.stream().map(m -> {
                    ParamField field = new ParamField();
                    field.setStcd(m);
                    return field;
                }).collect(Collectors.toList());
                data.deleteALL(paramFields);
            }
            int count = 4500;
            int number = list_DataUPDATE.size() / count;
            if (list_DataUPDATE.size() % count != 0) {
                number += 1;
            }
            List<ES_ZHANDIANDATAPojo> list = new ArrayList<>();
            for (int i = 0; i < number; i++) {
                if (i == number - 1) {
                    list = list_DataUPDATE.subList(count * i, list_DataUPDATE.size());
                } else {
                    list = list_DataUPDATE.subList(count * i, count * (i + 1));
                }
            }
            integer += data.insertALL(list);
        }

        return integer;
    }

    public List<ST_WAS_RPojo> getSCSW(String stime, String etime, List<String> idList, String hour) {
        List<ST_WAS_RPojo> list = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int year = 0;
        // try {
        // Date date = dateFormat.parse(stime);
        // year = date.getYear();
        // } catch (ParseException e) {
        // e.printStackTrace();
        // }
        // if(year + 1900 >= 2022){
        // list = rtsqData.getSCSW(stime, etime, idList, hour);
        // }else {
        // list = rtsqData.getSCSWLS(stime, etime, idList, hour);
        // }
        // list = rtevData.selectHis(idList,stime, etime);//wds 老水情库
        list = getWaterViewNewData.selectHis(idList, stime, etime);
        return list;
    }

    public List<ST_TIDEHIGHParam> SWZZ_FLOODTIDEDATA_ST_TIDEHIGH_RSel(String stcd, String stime, String etime,
            String tdptn) {
        List<ST_TIDEHIGHParam> listData = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String sT = dateFormat.format(new Date(date.getTime() - 3 * 24 * 60 * 60 * 1000));
        String eT = dateFormat.format(date);
        List<String> stcdList = new ArrayList<>();
        if (null != stcd && !"".equals(stcd)) {
            stcdList = Arrays.asList(stcd.split(","));
        }
        if (null != stime) {
            sT = stime;
        }
        if (null != etime) {
            eT = etime;
        }
        if (null != tdptn) {
            // tdptn = tdptn;
        } else {
            tdptn = "1";
        }
        Integer year = 0;
        try {
            Date parse = dateFormat.parse(sT);
            year = parse.getYear();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        List<ST_TIDEH_RPojo> userList = stTideRData.selectTideHList(stcdList, sT, eT);
        // if(year + 1900 > 2020){
        // List<String> finalStcdList = stcdList;
        // List<ST_STBPRP_BDto> listB = new ArrayList<>();
        // if (finalStcdList.size() > 0){
        // listB = stbprpBData.selectListBandStcd("").stream().filter(m->
        // finalStcdList.contains(m.getSTCD()) &&
        // "1".equals(m.getTYPE())).collect(Collectors.toList());
        // }else {
        // listB = stbprpBData.selectListBandStcd("").stream().filter(m->
        // "1".equals(m.getTYPE())).collect(Collectors.toList());
        // }
        // if(listB.size() > 0){
        // List<String> zstcdList =
        // listB.stream().map(ST_STBPRP_BDto::getZSTCD).collect(Collectors.toList());
        // // StopWatch stopWatch = new StopWatch();
        // // stopWatch.start();
        // List<ST_WAS_RPojo> listSC = getSCSW(sT, eT, zstcdList, "");
        // // stopWatch.stop();
        // // System.out.println(stopWatch.getTotalTimeSeconds());
        // for(String zstcd : zstcdList){
        // List<ST_WAS_RPojo> listSCTemp = listSC.stream().filter(m ->
        // m.getSTCD().equals(zstcd)).collect(Collectors.toList());
        // List<ST_STBPRP_BDto> listBTemp = listB.stream().filter(m ->
        // m.getZSTCD().equals(zstcd)).collect(Collectors.toList());
        // listSCTemp.forEach(m->{
        // ST_TIDEHIGHParam dto = new ST_TIDEHIGHParam();
        // dto.setSTCD(listBTemp.get(0).getSTCD());
        // dto.setSTNM(listBTemp.get(0).getSTNM());
        // dto.setTDZ(m.getUPZ());
        // dto.setTM(m.getTM());
        // List<ST_TIDEH_RPojo> userListTemp = userList.stream().filter(n ->
        // n.getSTCD().equals(dto.getSTCD())&&n.getTM().equals(m.getTM()+".000000")).collect(Collectors.toList());
        // if(userListTemp.size() > 0){
        // dto.setHTDZ(String.valueOf(userListTemp.get(0).getTDZ()));
        // dto.setZTDZ(String.valueOf(Double.parseDouble(m.getUPZ()) -
        // Double.parseDouble(dto.getHTDZ())));
        // }
        // listData.add(dto);
        // });
        // }
        // }
        // }else {
        String finalTdptn = tdptn;
        List<ST_TIDE_RPojo> userListR = stTideRData.selectTideList(stcdList, sT, eT).stream()
                .filter(m -> m.getTDPTN().equals(finalTdptn)).collect(Collectors.toList());
        List<ST_STBPRP_BPojo> listB = stbprpBData.selectList("", "", "", "", null, null);
        if (userListR.size() == 0) {
            userListR = stTideRData.selectHList(stcdList, sT, eT);
        }
        userListR.forEach(m -> {
            ST_TIDEHIGHParam dto = new ST_TIDEHIGHParam();
            dto.setSTCD(m.getSTCD());
            dto.setTDZ(String.valueOf(m.getTDZ()));
            dto.setTM(m.getTM());
            List<ST_TIDEH_RPojo> userListTemp = userList.stream()
                    .filter(n -> n.getSTCD().equals(m.getSTCD()) && n.getTM().equals(m.getTM()))
                    .collect(Collectors.toList());
            if (userListTemp.size() > 0) {
                dto.setHTDZ(String.valueOf(userListTemp.get(0).getTDZ()));
                dto.setZTDZ(String.valueOf(m.getTDZ() - userListTemp.get(0).getTDZ()));
            }
            List<ST_STBPRP_BPojo> listBTemp = listB.stream().filter(n -> n.getSTCD().equals(m.getSTCD()))
                    .collect(Collectors.toList());
            if (listBTemp.size() > 0) {
                dto.setSTNM(listBTemp.get(0).getSTNM());
            }
            listData.add(dto);
        });
        // }
        return listData;
    }

    @Override
    public Integer StatisticalCorrelationModelSW(String solutionid, List<String> list) {
        List<Map> mapList = list.stream().map(m -> JSON.parseObject(m, Map.class)).collect(Collectors.toList());
        List<String> stcdList = mapList.stream().map(m -> {
            if (m.containsKey("stcd") && null != m.get("stcd")) {
                return m.get("stcd").toString();
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
        List<ES_ZHANDIANDATAPojo> pojoList = data.selectList(null, null, null, solutionid, stcdList, null, null);
        final Integer[] num = { 0 };
        mapList.forEach(m -> {
            String mdata = m.containsKey("mData") ? m.get("mData").toString() : null;
            List<Double> mData = JSON.parseArray(mdata, Double.class);
            String zhanid = m.containsKey("stcd") ? m.get("stcd").toString() : "";
            List<ES_ZHANDIANDATAPojo> collectList = pojoList.stream().filter(n -> n.getZHANID().equals(zhanid))
                    .sorted(Comparator.comparing(ES_ZHANDIANDATAPojo::getZHANTIME)).collect(Collectors.toList());
            int i = 0;
            for (ES_ZHANDIANDATAPojo es : collectList) {
                if (null != mData) {
                    es.setZHANDATA(String.valueOf(mData.get(i)));
                }
                i++;
            }
            int count = 4000;
            int numsize = collectList.size() / count;
            List<ES_ZHANDIANDATAPojo> subList = new ArrayList<>();
            if (collectList.size() % count != 0) {
                numsize += 1;
            }
            for (i = 0; i < numsize; i++) {
                if (i == numsize - 1) {
                    subList = collectList.subList(i * count, collectList.size());
                } else {
                    subList = collectList.subList(i * count, (i + 1) * count);
                }
                num[0] += data.updateALL(subList);
            }
        });

        return num[0];
    }

    @Override
    public Integer mx_recalculate(String solutionid, String dd_id) {
        final Integer[] num = { 0 };
        List<String> stcdList = new ArrayList<>();
        List<ES_ZHANDIANDATAPojo> pojoList = data.selectList(null, null, null, solutionid, stcdList, null, null);
        List<ES_ZHANDIANDATAPojo> collectList = new ArrayList<>();
        pojoList.forEach(m -> {
            ES_ZHANDIANDATAPojo dto = new ES_ZHANDIANDATAPojo();
            String id = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16);
            dto.setID(id);
            dto.setZHANID(m.getZHANID());
            dto.setZHANTIME(m.getZHANTIME());
            dto.setZHANDATA(m.getZHANDATA());
            dto.setSOLUTIONID(dd_id);
            dto.setDD_FOR(m.getDD_FOR());
            collectList.add(dto);
        });
        int count = 4000;
        int numsize = collectList.size() / count;
        List<ES_ZHANDIANDATAPojo> subList = new ArrayList<>();
        if (collectList.size() % count != 0) {
            numsize += 1;
        }
        int i = 0;
        for (i = 0; i < numsize; i++) {
            if (i == numsize - 1) {
                subList = collectList.subList(i * count, collectList.size());
            } else {
                subList = collectList.subList(i * count, (i + 1) * count);
            }
            num[0] += data.insertALL(subList);
        }
        return num[0];
    }

    @Override
    public Integer ModifyGCSSLLAREAGCGZ_SZH(String solutionid, List<String> areaids, List<String> faids) {
        List<ES_MODELGUANLIANPojo> listG = esModGuData.selectListByID(areaids, "6", null, null).stream()
                .filter(m -> areaids.contains(m.getMKEYID())).collect(Collectors.toList());

        List<String> stcdList = new ArrayList<>();
        if (listG != null && listG.size() > 0) {
            for (ES_MODELGUANLIANPojo esSltongji : listG) {
                // 增加空指针保护，防止 getSTCD() 返回 null 导致报错
                if (esSltongji.getSTCD() != null) {
                    // 将拆分后的数组转换为 List，然后追加到原有的 stcdList 中
                    stcdList.addAll(Arrays.asList(esSltongji.getSTCD().split(",")));
                }
            }
        }

        List<ES_ZHANDIANDATAPojo> listZhanData = data.selectListGCID(solutionid, "3", stcdList);
        // List<ES_MODELFANGANZHANPojo> listM = esModelfanData.selectList("", null,
        // null, null).stream()
        // .filter(m -> faids.contains(m.getFA_ID())).collect(Collectors.toList());

        List<ES_ZHANDIANDATAPojo> listZhanDatanew = new ArrayList<>();

        listG.forEach(m -> {
            int _index = areaids.indexOf(m.getMKEYID());
            String faid = faids.get(_index);// 使用的方案方案编号
            String[] stcds = m.getSTCD().split(",");
            System.out.print("stcds的长度：" + stcds.length);
            for (int i = 0; i < stcds.length; i++) {
                String stcd = stcds[i];
                List<ES_ZHANDIANDATAPojo> listZhanDataTemp = listZhanData.stream()
                        .filter(n -> stcd.equals(n.getZHANID().trim())).collect(Collectors.toList());
                // List<ES_MODELFANGANZHANPojo> listMTemp = listM.stream()
                // .filter(n -> n.getFA_ID().equals(faid) && n.getZHANID().equals(stcd))
                // .collect(Collectors.toList());
                // if (listMTemp.size() > 0) {
                listZhanDataTemp.forEach(n -> {
                    String normal = faid;// listMTemp.get(0).getCZ().equals(-1) ? "调度预案" : listMTemp.get(0).getNORMAL();
                    n.setZHANDATA(normal);
                    listZhanDatanew.add(n);
                });
                // }
            }
            // faidsindex.getAndIncrement();
        });
        System.out.print("listZhanDatanew的长度：" + listZhanDatanew.size());
        List<ES_ZHANDIANDATAPojo> uniqueList = listZhanDatanew.stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(
                                pojo -> Arrays.asList(pojo.getZHANID(), pojo.getZHANID(), pojo.getZHANTIME()),
                                Function.identity(),
                                (v1, v2) -> v1),
                        map -> new ArrayList<>(map.values())));
        System.out.print("uniqueList的长度：" + uniqueList.size());
        int num = 0;
        int count = 500;
        int number = uniqueList.size() / count;
        if (uniqueList.size() % count != 0) {
            number = number + 1;
        }
        List<ES_ZHANDIANDATAPojo> zlist = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            if (i == number - 1) {
                zlist = uniqueList.subList(count * i, uniqueList.size());
            } else {
                zlist = uniqueList.subList(count * i, count * (i + 1));
            }
            num += data.updateALLME(zlist);
        }
        return num;
    }

    @Override
    public Integer ModifyGCSSLLAREAGCGZ_SZHFJL(String solutionid, String stime, String etime) {
        // 查询最新的放江量
        List<ES_PUMP_RPojo> listPumpData = es_pump_rData.selectListNew(null, stime, null);
        if (listPumpData.size() == 0) {
            return 0;
        }

        List<ES_PUMP_BPojo> listG = es_pump_bData.selectList(null, null, null);
        Set<String> stcdSet = listG.stream().map(ES_PUMP_BPojo::getZHANID).collect(Collectors.toSet());

        // // 预索引：listZhanData 按 trim 后的 ZHANID 分组，O(N) 建索引 + O(1) 查找
        // Map<String, List<ES_ZHANDIANDATAPojo>> zhanDataMap =
        // data.selectListGC(solutionid, "3").stream()
        // .filter(n -> stcdSet.contains(n.getZHANID()))
        // .collect(Collectors.groupingBy(n -> n.getZHANID().trim()));

        // 预索引：listPumpData 按 STCD -> TM -> PMPQ 建二级Map，O(N) 建索引 + O(1) 查找
        Map<String, Map<String, String>> pumpDataMap = new HashMap<>();
        for (ES_PUMP_RPojo p : listPumpData) {
            pumpDataMap
                    .computeIfAbsent(p.getSTCD().trim(), k -> new HashMap<>())
                    .putIfAbsent(p.getTM(), p.getPMPQ().toString());
        }

        List<ES_ZHANDIANDATAPojo> listZhanDatanew = new ArrayList<>();

        // 遍历每个站点，按5分钟间隔生成时间序列，有值取值，无值取0
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Calendar startCal = Calendar.getInstance();
            startCal.setTime(sdf.parse(stime));
            Calendar endCal = Calendar.getInstance();
            endCal.setTime(sdf.parse(etime));

            for (String stcd : stcdSet) {
                Map<String, String> timeValueMap = pumpDataMap.get(stcd);
                // pumpDataMap 中完全不存在该站点记录的，跳过不添加
                if (timeValueMap == null || timeValueMap.isEmpty()) {
                    continue;
                }
                Calendar cal = (Calendar) startCal.clone();
                cal.add(Calendar.MINUTE, 5); // 开始时间不要，从下一个5分钟开始
                while (!cal.after(endCal)) {
                    String timeStr = sdf.format(cal.getTime());
                    String value = "0";
                    if (timeValueMap != null && timeValueMap.containsKey(timeStr)) {
                        value = timeValueMap.get(timeStr);
                    }
                    ES_ZHANDIANDATAPojo pojo = new ES_ZHANDIANDATAPojo();
                    pojo.setID(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16));
                    pojo.setZHANID(stcd);
                    pojo.setZHANTIME(timeStr);
                    pojo.setZHANDATA(value);
                    pojo.setSOLUTIONID(solutionid);
                    listZhanDatanew.add(pojo);
                    cal.add(Calendar.MINUTE, 5);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }

        // 分批入库，避免一次性 MERGE 数据量过大
        int num = 0;
        int batchSize = 800;
        int number = listZhanDatanew.size() / batchSize;
        if (listZhanDatanew.size() % batchSize != 0) {
            number = number + 1;
        }
        for (int i = 0; i < number; i++) {
            List<ES_ZHANDIANDATAPojo> batch;
            if (i == number - 1) {
                batch = listZhanDatanew.subList(batchSize * i, listZhanDatanew.size());
            } else {
                batch = listZhanDatanew.subList(batchSize * i, batchSize * (i + 1));
            }
            num += data.updateALLME(batch);
        }
        return num;
    }

    @Override
    public Integer BigModeLineardifference(String solutionid, String stcd, String startDate, String endDate) {
        List<ES_ZHANDIANDATAPojo> list = new ArrayList<>();
        List<ST_BIGMODEBASE_BPojo> listZhan = st_bigmodebase_bData.selectList(null, null, null, null);
        List<String> itemList = listZhan.stream().map(ST_BIGMODEBASE_BPojo::getITEMID).collect(Collectors.toList());

        // ITEMID -> MKEYID 映射，避免重复过滤
        Map<String, String> itemToMkey = new HashMap<>();
        listZhan.forEach(z -> itemToMkey.put(z.getITEMID(), z.getMKEYID()));

        // 查询大模型预报潮位结果
        List<DataItem> resultList = bigModeServer.getBigModeFactorQuery(startDate, endDate, itemList);
        resultList.forEach(m -> {
            String factorItemId = m.getFactorItemId();
            String mkeyId = itemToMkey.get(factorItemId);
            List<DataPoint> dataPoints = m.getDataPoints();
            List<Map<String, Object>> dataPointsMapList = dataPoints.stream().map(dp -> {
                Map<String, Object> map = new HashMap<>();
                map.put("tt", dp.getTt());
                map.put("val", dp.getVal());
                map.put("formatVal", dp.getFormatVal());
                return map;
            }).collect(Collectors.toList());

            // 1小时一个数据，需要插值为5分钟的
            List<Map<String, Object>> dataListGC5 = LinearInterpolationUtil.interpolateTideData(dataPointsMapList, "tt",
                    "val");
            for (Map<String, Object> dataItem : dataListGC5) {
                ES_ZHANDIANDATAPojo pojo = new ES_ZHANDIANDATAPojo();
                pojo.setZHANID(mkeyId);
                pojo.setZHANTIME(dataItem.get("tt").toString());
                pojo.setSOLUTIONID(solutionid);
                pojo.setZHANDATA(String.format("%.2f", (double) dataItem.get("val")));
                list.add(pojo);
            }
        });

        // 构建 Map<(ZHANTIME + "_" + ZHANID), pojo>，O(1)查找
        Map<String, ES_ZHANDIANDATAPojo> listMap = list.stream()
                .collect(Collectors.toMap(p -> p.getZHANTIME() + "_" + p.getZHANID(), p -> p, (a, b) -> a));

        List<ES_ZHANDIANDATAPojo> listDataNew = new ArrayList<>();
        List<String> aggstcd = listZhan.stream().map(ST_BIGMODEBASE_BPojo::getMKEYID).collect(Collectors.toList());
        List<ES_ZHANDIANDATAPojo> listData = data.selectListBySolutionIds(Arrays.asList(solutionid.split(",")),
                aggstcd);
        listData.forEach(m -> {
            String key = m.getZHANTIME() + "_" + m.getZHANID();
            ES_ZHANDIANDATAPojo matched = listMap.get(key);
            if (matched != null) {
                String upz = String.format("%.2f", Double.parseDouble(matched.getZHANDATA()));
                m.setZHANDATA(upz);
                listDataNew.add(m);
            }
        });
        int num = 0;
        int count = 1000;
        int number = listDataNew.size() / count;
        if (listDataNew.size() % count != 0) {
            number = number + 1;
        }
        List<ES_ZHANDIANDATAPojo> zlist = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            if (i == number - 1) {
                zlist = listDataNew.subList(count * i, listDataNew.size());
            } else {
                zlist = listDataNew.subList(count * i, count * (i + 1));
            }
            num += data.updateALL(zlist);
        }
        String curTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        System.out.println("************入库之后：" + curTime);
        return num;
    }
}
