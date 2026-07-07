package swzzmodeserver.workserver.service.swzzmode;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
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
import swzzmodeserver.workserver.pojo.swzzrtsq.GetWaterViewNewPojo;
import swzzmodeserver.workserver.pojo.swzzzjk.ST_TIDEHIGHParam;
import swzzmodeserver.workserver.server.swzzrtsq.GetWaterViewNewServer;
import swzzmodeserver.workserver.pojo.swzzflood.ST_WAS_RPojo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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
            ES_SLTONGJIData esSltongjiData) {
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
        int count = 4500;
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
            List<ES_ZHANDIANDATADto> listData = new ArrayList<>();
            List<ES_ZHANDIANPojo> list = esZhandianDataData.selectList("", null, null, null, "");
            List<ES_MODELGUANLIANPojo> listModel = esModGuData.selectList("", "3", null, null);
            if (jydatatype.contains("SK") || jydatatype.contains("shanghaiyb")) {
                stnm = jydatatype;
                // new javalog().writelog("开始拼雨量边界", filePathName);

                Date curDay = new Date();
                List<Tz_watersheddataPojo> dt = new ArrayList<>();
                String danwei = jydatatype.split("@")[1];
                List<String> FPDR = Arrays.asList("6,48".split(","));
                // dt = watersheddataData.selectByTimeAndFPDR(startdate, enddate,
                // dateFormat.format(new Date(stimeLong - 3 * 24 * 60 * 60 * 1000)),
                // dateFormat.format(new Date(stimeLong)), FPDR);

                dt = watersheddataData.selectListLastByID(startdate, enddate, FPDR, "上海气象台",
                        dateFormat.format(new Date(stimeLong - 3 * 24 * 60 * 60 * 1000)),
                        dateFormat.format(new Date(stimeLong)));
                List<ES_ZHANDIANPojo> lists = list.stream().filter(m -> "0".equals(m.getPTYPE()))
                        .collect(Collectors.toList());
                new javalog().writelog("雨量边界站共：" + lists.size() + "个", filePathName);
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
                        List<Tz_watersheddataPojo> dr = dt.stream()
                                .filter(m -> obj.getZHANID().equals(m.getKEYID()) && m.getFTM().equals(tm))
                                .collect(Collectors.toList());
                        if (dr.size() > 0) {
                            double hourDrp = dr.get(0).getDRP();
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
                    List<ES_MODELGUANLIANPojo> listGuanlian = esModGuData.selectList("", "6", null, null);
                    // 根据降雨量匹配调度方案：134个分片转成15个大片
                    List<ES_ZHANDIANDATADto> listData134 = new ArrayList<>();
                    List<ES_SLTONGJIPojo> esSltongjiList = esSltongjiData.selectList(null, "134", null, null, null);
                    esSltongjiList.forEach(esSltongji -> {
                        List<String> yqIDList = Arrays.asList(esSltongji.getSTCD().split(","));
                        double drptotal = listData.stream().filter(n -> yqIDList.contains(n.getZHANID()))
                                .mapToDouble(n -> Double.parseDouble(n.getZHANDATA())).sum();
                        double drp = yqIDList.size() > 0 ? drptotal / yqIDList.size() : 0;// 平均降雨量
                        ES_ZHANDIANDATADto dto = new ES_ZHANDIANDATADto();
                        dto.setZHANID(esSltongji.getID());
                        dto.setZHANDATA(String.format("%.1f", drp));
                        listData134.add(dto);
                    });
                    new javalog().writelog("listData134的长度：" + listData134.size(), filePathName, "mode");
                    // 根据降雨量匹配调度方案

                    List<ES_MODELFANGANZHANPojo> listFaZhan = esModelfanData.selectList("", null, null, null);
                    List<ES_ZHANDIANPojo> listZHAN = list.stream().filter(m -> Integer.parseInt(m.getPTYPE()) >= 3)
                            .collect(Collectors.toList());
                    long finalStimeLong = stimeLong;
                    listZHAN.forEach(m -> {
                        String zhanData = "日常调度";
                        List<ES_MODELGUANLIANPojo> listGuanlianTemp = listGuanlian.stream()
                                .filter(n -> n.getSTCD().contains(m.getZHANID())).collect(Collectors.toList());
                        if (listGuanlianTemp.size() > 0) {
                            new javalog().writelog("listGuanlianTemp的长度：" + listGuanlianTemp.size(),
                                    filePathName, "mode");
                            String yqID = listGuanlianTemp.get(0).getMKEYID();
                            double num = listData134.stream().filter(n -> n.getZHANID().equals(yqID))
                                    .mapToDouble(n -> Double.parseDouble(n.getZHANDATA())).sum();// 根据降雨量默认调度方案
                            new javalog().writelog("num的值：" + num, filePathName, "mode");
                            List<ES_MODELFANGANZHANPojo> listFaZhanTemp = listFaZhan.stream()
                                    .filter(n -> n.getMAXDRP() != null && n.getMAXDRP().doubleValue() <= num)
                                    .collect(Collectors.toList());
                            new javalog().writelog("listFaZhanTemp的长度：" + listFaZhanTemp.size(), filePathName, "mode");
                            if (listFaZhanTemp.size() > 0) {
                                zhanData = listFaZhanTemp.get(0).getNORMAL();
                            }
                        }
                        for (int i = 0; i < timeCount; i++) {
                            ES_ZHANDIANDATADto dto = new ES_ZHANDIANDATADto();
                            dto.setID(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16));
                            dto.setZHANID(m.getZHANID());
                            String tm = dateFormat.format(new Date(finalStimeLong + i * 60 * 60 * 1000));
                            dto.setZHANTIME(tm);
                            dto.setZHANDATA(zhanData);
                            dto.setSOLUTIONID(solutionid);
                            dto.setDD_FOR(username);
                            listData.add(dto);
                        }
                    });
                } catch (Exception e) {
                    new javalog().writelog(
                            "报错了：" + e.getMessage(),
                            filePathName, "mode");
                }
            } else if (gcdatatype.equals("fangjiangliang")) {
                stnm = gcdatatype;
                new javalog().writelog("苏州河泵站采用放江量", filePathName);
                // 放江量
                List<ES_PUMP_RPojo> listPumpData = es_pump_rData.selectListNew(null, startdate, null);
                System.err.println("放江量数据长度：" + listPumpData.size());
                new javalog().writelog("放江量数据长度：" + listPumpData.size(), filePathName);
                // 放江量

                List<ES_MODELFANGANPojo> listFang = esModelfanganData.selectList("", null, null).stream()
                        .filter(m -> m.getMAXDRP() != null).collect(Collectors.toList());
                List<String> faids = listFang.stream().map(ES_MODELFANGANPojo::getID).collect(Collectors.toList());
                List<ES_MODELGUANLIANPojo> listGuanlian = esModGuData.selectList("", "6", null, null);
                List<ES_MODELFANGANZHANPojo> listFaZhan = esModelfanData.selectList("", faids, null, null);
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
                        List<ES_MODELGUANLIANPojo> listGuanlianTemp = listGuanlian.stream()
                                .filter(n -> n.getSTCD().contains(m.getZHANID())).collect(Collectors.toList());
                        if (listGuanlianTemp.size() > 0) {
                            String yqID = listGuanlianTemp.get(0).getMKEYID();
                            double num = listData.stream().filter(n -> n.getZHANID().equals(yqID))
                                    .mapToDouble(n -> Double.parseDouble(n.getZHANDATA())).sum();
                            List<ES_MODELFANGANPojo> listFangTemp = listFang.stream().filter(n -> n.getMAXDRP() >= num)
                                    .sorted(Comparator.comparingDouble(ES_MODELFANGANPojo::getMAXDRP))
                                    .collect(Collectors.toList());
                            if (listFangTemp.size() > 0) {
                                List<ES_MODELFANGANZHANPojo> listFaZhanTemp = listFaZhan.stream()
                                        .filter(n -> n.getFA_ID().equals(listFangTemp.get(0).getID()))
                                        .collect(Collectors.toList());
                                if (listFaZhanTemp.size() > 0) {
                                    zhanData = listFaZhanTemp.get(0).getNORMAL();
                                }
                            }
                        }
                    }
                    // System.err.println(m.getZHANID()+"站放江量数据长度："+listPumpDataTemp.size());
                    new javalog().writelog(m.getZHANID() + "站放江量数据长度：" + listPumpDataTemp.size(), filePathName);
                    for (int i = 0; i < timeCount; i++) {
                        ES_ZHANDIANDATADto dto = new ES_ZHANDIANDATADto();
                        dto.setID(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16));
                        dto.setZHANID(m.getZHANID());
                        String tm = dateFormat.format(new Date(finalStimeLong + i * 60 * 60 * 1000));

                        List<ES_PUMP_RPojo> listPumpDataTempT = listPumpData.stream()
                                .filter(p -> p.getTM().equals(tm))
                                .collect(Collectors.toList());
                        zhanData = listPumpDataTempT.size() > 0 ? listPumpDataTempT.get(0).getPMPQ().toString()
                                : "0";// 有放江量就用放江量
                        dto.setZHANTIME(tm);
                        dto.setZHANDATA(zhanData);
                        dto.setSOLUTIONID(solutionid);
                        dto.setDD_FOR(username);
                        listData.add(dto);
                    }
                });
                // new javalog().writelog("调度工程边界拼完了，listData的长度："+listData.size(),
                // filePathName);
            } else if (gcdatatype.contains("sangezhiliugc")) {// 三个直流，局里调用
                stnm = gcdatatype;
                new javalog().writelog("三个直流，局里调用", filePathName);
                List<ES_MODELFANGANPojo> listFang = esModelfanganData.selectList("", null, null).stream()
                        .filter(m -> m.getMAXDRP() != null).collect(Collectors.toList());
                List<String> faids = listFang.stream().map(ES_MODELFANGANPojo::getID).collect(Collectors.toList());
                List<ES_MODELGUANLIANPojo> listGuanlian = esModGuData.selectList("", "6", null, null);
                List<ES_MODELFANGANZHANPojo> listFaZhan = esModelfanData.selectList("", faids, null, null);
                List<ES_ZHANDIANPojo> listZHAN = list.stream().filter(m -> Integer.parseInt(m.getPTYPE()) >= 3)
                        .collect(Collectors.toList());
                long finalStimeLong = stimeLong;

                // 三个片区的工程需要按照传过来的方案调度：嘉宝北片、蕰南片、淀北片
                String[] ddArray = gcdatatype.split("@");
                String ddStr = ddArray[1];// 应急分流、强制应急分流
                if (ddStr.equals("应急分流")) {
                    ddStr = "防汛防台橙色预警及除涝泵闸限排及应急分流";
                } else if (ddStr.equals("强制应急分流")) {
                    ddStr = "防汛防台橙色预警及除涝泵闸限排及强制应急分流";
                }
                List<String> IDList = Arrays.asList("1ce41ea68b9355e7,dc122ef184a4fa26,84e55db0b8acfc51".split(","));
                List<ES_SLTONGJIPojo> esSltongjiList = esSltongjiData.selectListByID(IDList, "134", null, null, null);
                List<String> zhanidList = new ArrayList<>();
                if (esSltongjiList != null && esSltongjiList.size() > 0) {
                    for (ES_SLTONGJIPojo esSltongji : esSltongjiList) {
                        // 增加空指针保护，防止 getSTCD() 返回 null 导致报错
                        if (esSltongji.getSTCD() != null) {
                            // 将拆分后的数组转换为 List，然后追加到原有的 stcdList 中
                            zhanidList.addAll(Arrays.asList(esSltongji.getSTCD().split(",")));
                        }
                    }
                }

                listZHAN.forEach(m -> {
                    String zhanData = "日常调度";
                    List<ES_MODELGUANLIANPojo> listGuanlianTemp = listGuanlian.stream()
                            .filter(n -> n.getSTCD().contains(m.getZHANID())).collect(Collectors.toList());
                    if (listGuanlianTemp.size() > 0) {
                        String yqID = listGuanlianTemp.get(0).getMKEYID();
                        double num = listData.stream().filter(n -> n.getZHANID().equals(yqID))
                                .mapToDouble(n -> Double.parseDouble(n.getZHANDATA())).sum();
                        List<ES_MODELFANGANPojo> listFangTemp = listFang.stream().filter(n -> n.getMAXDRP() >= num)
                                .sorted(Comparator.comparingDouble(ES_MODELFANGANPojo::getMAXDRP))
                                .collect(Collectors.toList());
                        if (listFangTemp.size() > 0) {
                            List<ES_MODELFANGANZHANPojo> listFaZhanTemp = listFaZhan.stream()
                                    .filter(n -> n.getFA_ID().equals(listFangTemp.get(0).getID()))
                                    .collect(Collectors.toList());
                            if (listFaZhanTemp.size() > 0) {
                                zhanData = listFaZhanTemp.get(0).getNORMAL();
                            }
                        }
                    }

                    if (zhanidList.contains(m.getZHANID())) {

                    }
                    for (int i = 0; i < timeCount; i++) {
                        ES_ZHANDIANDATADto dto = new ES_ZHANDIANDATADto();
                        dto.setID(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16));
                        dto.setZHANID(m.getZHANID());
                        String tm = dateFormat.format(new Date(finalStimeLong + i * 60 * 60 * 1000));
                        dto.setZHANTIME(tm);
                        dto.setZHANDATA(zhanData);
                        dto.setSOLUTIONID(solutionid);
                        dto.setDD_FOR(username);
                        listData.add(dto);
                    }
                });
            }
            long hydroDtNo = timeCount * 12;
            List<ST_ASTRONOMICALTIDE_RPojo> listAS = stAstronomicaltideRData
                    .selectList(null, null, startdate, enddate, null, null, null)
                    .stream().sorted(Comparator.comparingLong(n -> {
                        try {
                            return dateFormat.parse(n.getTM()).getTime();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        return 0;
                    })).collect(Collectors.toList());
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
                List<ST_FORECAST_FPojo> listFor = st_forecast_fData.selectListFast("太湖局", SFYMDH, enddate, startdate,
                        enddate);// 太湖局预报的潮位边界，1小时一个数据

                for (ES_ZHANDIANPojo obj : list.stream()
                        .filter(m -> m.getPTYPE().equals("1") || m.getPTYPE().equals("2"))
                        .collect(Collectors.toList())) {
                    List<ST_ASTRONOMICALTIDE_RPojo> listASTemp = listAS.stream()
                            .filter(m -> m.getZHANID().equals(obj.getZHANID())).collect(Collectors.toList());
                    for (int num = 0; num < hydroDtNo + 1; num++) {
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
                                double upz = Double.parseDouble(listWasTemp.get(listWasTemp.size() - 1).getUPZ())
                                        - 0.26;
                                dto.setZHANDATA(String.format("%.2f", upz));
                            }

                            String finalStime1 = sTime;
                            List<ST_FORECAST_FPojo> listForTemp = listFor.stream()
                                    .filter(m -> m.getSTCD().equals(obj.getZHANID()) && m.getYMDH().equals(finalStime1))
                                    .collect(Collectors.toList());
                            if (listForTemp.size() > 0) {// 就太湖局预报数据就用太湖局的
                                double upz = listForTemp.get(0).getZ() - 0.26;
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
                List<ES_ZHANDIANXSPojo> listXS = xsData.selectList("", null, null);
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
                    listWas = getWaterViewNewData.selectListWaterAll(Arrays.asList(stcds.split(",")), stime, startdate,
                            null, null, null);
                } catch (Exception e) {
                    // TODO: handle exception
                }

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String SFYMDH = LocalDateTime.parse(startdate, formatter)
                        .minusDays(3)
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                List<ST_FORECAST_FPojo> listFor = st_forecast_fData.selectListFast("太湖局", SFYMDH, enddate, startdate,
                        enddate);// 太湖局预报的潮位边界，1小时一个数据

                int Minutes = 5;// 潮位5分钟一个数据
                for (ES_ZHANDIANPojo obj : list.stream()
                        .filter(m -> "1".equals(m.getPTYPE()) || "2".equals(m.getPTYPE()))
                        .collect(Collectors.toList())) {
                    List<ES_ZHANDIANXSPojo> listXSTemp = listXS.stream()
                            .filter(m -> m.getMKEYID().equals(obj.getZHANID())).collect(Collectors.toList());
                    List<ES_ZHANGUANLIANPojo> listDataGuanSWT = listDataGuanSW.stream()
                            .filter(m -> m.getZHANID().equals(obj.getZHANID())).collect(Collectors.toList());
                    for (int num = 0; num < hydroDtNo + 1; num++) {
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
                                double upz = Double.parseDouble(listWasTemp.get(listWasTemp.size() - 1).getUPZ())
                                        - 0.26;
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
                                double upz = listForTemp.get(0).getZ() - 0.26;
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
                            List<ST_ASTRONOMICALTIDE_RPojo> listASTemp = listAS.stream()
                                    .filter(m -> m.getZHANID().equals(obj.getZHANID()) && m.getTM().equals(finalStime))
                                    .collect(Collectors.toList());
                            if (listASTemp.size() > 0) {
                                DATA = listASTemp.get(0).getZ();
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
            }
            if (listData.size() > 0) {
                try {
                    data.deleteOneBySOLUTIONID(solutionid);
                } catch (Exception e) {

                }
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

                new javalog().writelog("MODIFY_MODEZHANDData接口listData长度" + listData.size(), filePathName);
                int count = 800;
                int nums = listData.size() / count;
                if (nums > 0) {
                    if (listData.size() % nums != 0) {
                        nums += 1;
                    }
                    new javalog().writelog("MODIFY_MODEZHANDData接口每次入库" + count + "条数据,共" + nums + "次", filePathName);
                    List<ES_ZHANDIANDATADto> dtoList = new ArrayList<>();
                    for (int j = 0; j < nums; j++) {
                        if (j == nums - 1) {
                            dtoList = listData.subList(j * count, listData.size());
                        } else {
                            dtoList = listData.subList(j * count, (j + 1) * count);
                        }
                        number += data.insertALLDto(dtoList);
                    }
                }
            }
        } catch (Exception e) {
            number = 0;
            e.printStackTrace();
            new javalog().writelog("upDataZhandianData接口报错：" + e.getMessage(), filePathName);
        }
        return number;
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
                .selectList("", stime, etime, Arrays.asList(plan_n.split(",")), "", type, null, null, "asc", null)
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
        List<ES_MODELFANGANZHANPojo> listM = esModelfanData.selectList("", null, null, null).stream()
                .filter(m -> faids.contains(m.getFA_ID())).collect(Collectors.toList());

        List<ES_ZHANDIANDATAPojo> listZhanDatanew = new ArrayList<>();

        listG.forEach(m -> {
            int _index = areaids.indexOf(m.getMKEYID());
            String faid = faids.get(_index);// 使用的方案方案编号
            String[] stcds = m.getSTCD().split(",");
            for (int i = 0; i < stcds.length; i++) {
                String stcd = stcds[i];
                List<ES_ZHANDIANDATAPojo> listZhanDataTemp = listZhanData.stream()
                        .filter(n -> stcd.equals(n.getZHANID().trim())).collect(Collectors.toList());
                List<ES_MODELFANGANZHANPojo> listMTemp = listM.stream()
                        .filter(n -> n.getFA_ID().equals(faid) && n.getZHANID().equals(stcd))
                        .collect(Collectors.toList());
                if (listMTemp.size() > 0) {
                    listZhanDataTemp.forEach(n -> {
                        String normal = listMTemp.get(0).getCZ().equals(-1) ? "调度预案" : listMTemp.get(0).getNORMAL();
                        n.setZHANDATA(normal);
                        listZhanDatanew.add(n);
                    });
                }
            }
            // faidsindex.getAndIncrement();
        });

        List<ES_ZHANDIANDATAPojo> uniqueList = listZhanDatanew.stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(
                                pojo -> Arrays.asList(pojo.getZHANID(), pojo.getZHANID(), pojo.getZHANTIME()),
                                Function.identity(),
                                (v1, v2) -> v1),
                        map -> new ArrayList<>(map.values())));

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
    public Integer ModifyGCSSLLAREAGCGZ_SZHFJL(String solutionid, String stime) {
        System.err.println("========== 进入 ModifyGCSSLLAREAGCGZ_SZHFJL 方法 ==========");
        System.err.println("solutionid=" + solutionid + ", stime=" + stime);
        // 查询最新的放江量
        List<ES_PUMP_RPojo> listPumpData = es_pump_rData.selectListNew(null, stime, null);
        if (listPumpData.size() == 0) {
            return 0;
        } else {
            List<ES_PUMP_BPojo> listG = es_pump_bData.selectList(null, null, null);
            List<String> stcdList = listG.stream().map(ES_PUMP_BPojo::getZHANID).collect(Collectors.toList());
            List<ES_ZHANDIANDATAPojo> listZhanData = data.selectListGC(solutionid, "3").stream()
                    .filter(n -> stcdList.contains(n.getZHANID())).collect(Collectors.toList());

            List<ES_ZHANDIANDATAPojo> listZhanDatanew = new ArrayList<>();
            System.err.println("调试: listG.size=" + listG.size() + ", stcdList.size=" + stcdList.size()
                    + ", listZhanData.size=" + listZhanData.size());
            listG.forEach(m -> {
                List<ES_ZHANDIANDATAPojo> listZhanDataTemp = listZhanData.stream()
                        .filter(n -> m.getZHANID().equals(n.getZHANID().trim())).collect(Collectors.toList());

                System.err.println("listZhanDataTemp的长度" + m.getZHANID() + "：" +
                        listZhanDataTemp.size());

                List<ES_PUMP_RPojo> listPumpDataTemp = listPumpData.stream()
                        .filter(p -> p.getSTCD().equals(
                                m.getZHANID().trim()))
                        .collect(Collectors.toList());
                if (listPumpDataTemp.size() > 0) {
                    listZhanDataTemp.forEach(n -> {
                        List<ES_PUMP_RPojo> listPumpDataTempT = listPumpData.stream()
                                .filter(p -> p.getTM().equals(n.getZHANTIME()))
                                .collect(Collectors.toList());
                        String DATA = listPumpDataTempT.size() > 0 ? listPumpDataTempT.get(0).getPMPQ().toString()
                                : "0";
                        n.setZHANDATA(DATA);
                        listZhanDatanew.add(n);
                    });
                }
            });

            int num = 0;
            num = data.updateALLBatch(listZhanDatanew);
            // if (listZhanDatanew.size() > 0) {
            // int count = 4500;
            // int number = listZhanDatanew.size() / count;
            // if (listZhanDatanew.size() % count != 0) {
            // number = number + 1;
            // }
            // List<ES_ZHANDIANDATAPojo> zlist = new ArrayList<>();
            // for (int i = 0; i < number; i++) {
            // if (i == number - 1) {
            // zlist = listZhanDatanew.subList(count * i, listZhanDatanew.size());
            // } else {
            // zlist = listZhanDatanew.subList(count * i, count * (i + 1));
            // }
            // num += data.updateALL(zlist);
            // }
            // }
            return num;
        }
    }

    /**
     * 优化版 MODIFY_MODEZHANDData - 保留所有原始逻辑，仅优化性能
     * 核心优化：预建 HashMap 索引，将内层循环中的 O(n) stream().filter() 改为 O(1) map.get()
     */
    @Override
    public Integer MODIFY_MODEZHANDDataNew(String startdate, String enddate, String solutionid, String jydatatype,
            String gcdatatype, String scwdatatype, String username) {
        new javalog().writelog("MODIFY_MODEZHANDDataNew：jydatatype******" + jydatatype + "******gcdatatype******"
                + gcdatatype + "******scwdatatype******" + scwdatatype, filePathName);
        Integer number = 0;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
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
            List<ES_ZHANDIANDATADto> listData = new ArrayList<>();
            List<ES_ZHANDIANPojo> list = esZhandianDataData.selectList("", null, null, null, "");
            List<ES_MODELGUANLIANPojo> listModel = esModGuData.selectList("", "3", null, null);

            // ========== 预处理：雨量边界 dt -> Map ==========
            Map<String, Map<String, Tz_watersheddataPojo>> dtByKeyidAndFtm = new HashMap<>();
            if (jydatatype.contains("SK") || jydatatype.contains("shanghaiyb")) {
                List<Tz_watersheddataPojo> dt = watersheddataData.selectListLastByID(startdate, enddate,
                        Arrays.asList("6,48".split(",")), "上海气象台",
                        dateFormat.format(new Date(stimeLong - 3 * 24 * 60 * 60 * 1000)),
                        dateFormat.format(new Date(stimeLong)));
                for (Tz_watersheddataPojo item : dt) {
                    dtByKeyidAndFtm.computeIfAbsent(item.getKEYID(), k -> new HashMap<>()).put(item.getFTM(), item);
                }
            }

            // ========== 预处理：中央预报 listDataRNFLNew -> Map ==========
            Map<String, Map<String, St_rnfl_fPojo>> rnflNewByStcdAndTm = new HashMap<>();
            List<ES_ZHANGUANLIANPojo> listDataGuan = new ArrayList<>();
            if (jydatatype.contains("zhongyangyb")) {
                List<St_rnfl_fPojo> listDataRNFL = stRnflFData.selectByHourHX(startdate, enddate,
                        dateFormat.format(new Date(stimeLong - 3 * 24 * 60 * 60 * 1000)),
                        dateFormat.format(new Date(stimeLong)), null);
                for (St_rnfl_fPojo m : listDataRNFL) {
                    int intValue = m.getINTV().intValue();
                    for (int i = intValue; i >= 0; i--) {
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
                        stRnflFPojo.setDRP(Double.valueOf(Math.round((m.getDRP() / intValue) * 1000) / 1000));
                        rnflNewByStcdAndTm.computeIfAbsent(stRnflFPojo.getSTCD(), k -> new HashMap<>())
                                .put(stRnflFPojo.getTM(), stRnflFPojo);
                    }
                }
                listDataGuan = esZhanguanlianData.selectList("", null, null, Collections.singletonList("0"));
            }

            // ========== 预处理：放江量 listPumpData -> Map ==========
            Map<String, List<ES_PUMP_RPojo>> pumpDataByStcd = new HashMap<>();
            Map<String, Map<String, ES_PUMP_RPojo>> pumpDataByStcdAndTm = new HashMap<>();
            if (gcdatatype.equals("fangjiangliang")) {
                List<ES_PUMP_RPojo> listPumpData = es_pump_rData.selectListNew(null, startdate, null);
                new javalog().writelog("放江量数据长度：" + listPumpData.size(), filePathName);
                for (ES_PUMP_RPojo item : listPumpData) {
                    pumpDataByStcd.computeIfAbsent(item.getSTCD().trim(), k -> new ArrayList<>()).add(item);
                    pumpDataByStcdAndTm.computeIfAbsent(item.getSTCD().trim(), k -> new HashMap<>()).put(item.getTM(),
                            item);
                }
            }

            // ========== 预处理：天文潮 listAS -> Map ==========
            List<ST_ASTRONOMICALTIDE_RPojo> listAS = stAstronomicaltideRData.selectList(null, null, startdate, enddate,
                    null, null, null);
            Map<String, ST_ASTRONOMICALTIDE_RPojo> listASByZhanidAndTm = new HashMap<>();
            for (ST_ASTRONOMICALTIDE_RPojo item : listAS) {
                listASByZhanidAndTm.put(item.getZHANID() + "_" + item.getTM(), item);
            }

            // ========== 预处理：太湖局预报 listFor -> Map ==========
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String SFYMDH = LocalDateTime.parse(startdate, formatter).minusDays(3)
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            List<ST_FORECAST_FPojo> listFor = st_forecast_fData.selectListFast("太湖局", SFYMDH, enddate, startdate,
                    enddate);
            Map<String, ST_FORECAST_FPojo> listForByStcdAndYmdh = new HashMap<>();
            for (ST_FORECAST_FPojo item : listFor) {
                listForByStcdAndYmdh.put(item.getSTCD() + "_" + item.getYMDH(), item);
            }

            // ========== 预处理：潮汐预报 listTide -> Map ==========
            List<St_tide_rybPojo> listTide = new ArrayList<>();
            if (jydatatype.contains("typhoon")) {
                String stime = dateFormat.format(new Date(stimeLong - 7 * 24 * 60 * 60 * 1000));
                listTide = rybData.selectListByNew("63405800", stime, startdate).stream()
                        .filter(m -> "台风风暴潮".equals(m.getRTYPE())).collect(Collectors.toList());
            } else if (jydatatype.contains("temperatezone") || jydatatype.contains("OceanForecastTideNorth")
                    || jydatatype.contains("OceanForecastTideSouth")) {
                String ybstcd = "10001010";
                if (jydatatype.contains("OceanForecastTideNorth"))
                    ybstcd = "E17";
                else if (jydatatype.contains("OceanForecastTideSouth"))
                    ybstcd = "E18";
                String stime = dateFormat.format(new Date(stimeLong - 7 * 24 * 60 * 60 * 1000));
                listTide = rybData.selectListByNew(ybstcd, stime, startdate);
            }
            Map<String, St_tide_rybPojo> listTideByTm = new HashMap<>();
            for (St_tide_rybPojo item : listTide) {
                listTideByTm.put(item.getTM(), item);
            }

            // ========== 预处理：限差系数 listXS -> Map ==========
            List<ES_ZHANDIANXSPojo> listXS = xsData.selectList("", null, null);
            Map<String, ES_ZHANDIANXSPojo> listXSByMkeyid = new HashMap<>();
            for (ES_ZHANDIANXSPojo item : listXS) {
                listXSByMkeyid.put(item.getMKEYID(), item);
            }

            // ========== 预处理：实时水位 listWas -> Map ==========
            String stcds = "63403500,63301150,63205150,63205350";
            String zhandians = "1728053248,1728053250,1728053251,1728053252";
            List<GetWaterViewNewPojo> listWas = new ArrayList<>();
            try {
                String stime = dateFormat.format(new Date(stimeLong - 8 * 60 * 60 * 1000));
                listWas = getWaterViewNewData.selectListWaterAll(Arrays.asList(stcds.split(",")), stime, startdate,
                        null, null, null);
            } catch (Exception e) {
                /* ignore */ }
            Map<String, GetWaterViewNewPojo> listWasByStcdLast = new HashMap<>();
            for (GetWaterViewNewPojo item : listWas) {
                listWasByStcdLast.put(item.getSTCD(), item);
            }

            // ========== 预处理：插值计算 tideGCPojo, listDataGuanSW -> Map ==========
            List<ES_TIDALFORECASTGCPojo> tideGCPojo = new ArrayList<>();
            List<ES_ZHANGUANLIANPojo> listDataGuanSW = new ArrayList<>();
            if (scwdatatype.contains("样条函数插值") || scwdatatype.contains("余弦曲线插值")) {
                String maxYBTMString = jydatatype.split("@")[2];
                List<String> typeList = Collections.singletonList(scwdatatype);
                tideGCPojo = es_tidalforecastgcData.selectList(null, null, startdate, enddate, null, null, typeList,
                        null, null);
                listDataGuanSW = esZhanguanlianData.selectList("", null, null,
                        Collections.singletonList("TIDALFORECASTGC"));
            }
            Map<String, ES_TIDALFORECASTGCPojo> tideGCPojoByStcdAndTm = new HashMap<>();
            for (ES_TIDALFORECASTGCPojo item : tideGCPojo) {
                tideGCPojoByStcdAndTm.put(item.getSTCD() + "_" + item.getTM(), item);
            }
            Map<String, String> listDataGuanSWStcdByZhanid = new HashMap<>();
            for (ES_ZHANGUANLIANPojo item : listDataGuanSW) {
                listDataGuanSWStcdByZhanid.put(item.getZHANID(), item.getSTCD());
            }

            // ========== 预处理：工程调度 DDFN/fangjiangliang 相关数据 ==========
            List<ES_MODELFANGANPojo> listFang = esModelfanganData.selectList("", null, null).stream()
                    .filter(m -> m.getMAXDRP() != null).collect(Collectors.toList());
            List<String> faids = listFang.stream().map(ES_MODELFANGANPojo::getID).collect(Collectors.toList());
            List<ES_MODELGUANLIANPojo> listGuanlian = esModGuData.selectList("", "6", null, null);
            List<ES_MODELFANGANZHANPojo> listFaZhan = esModelfanData.selectList("", faids, null, null);
            Map<String, ES_MODELFANGANPojo> listFangById = new HashMap<>();
            for (ES_MODELFANGANPojo item : listFang) {
                listFangById.put(item.getID(), item);
            }
            Map<String, List<ES_MODELFANGANZHANPojo>> listFaZhanByFaId = new HashMap<>();
            for (ES_MODELFANGANZHANPojo item : listFaZhan) {
                listFaZhanByFaId.computeIfAbsent(item.getFA_ID(), k -> new ArrayList<>()).add(item);
            }

            // ========== 分支1：雨量边界 SK/shanghaiyb ==========
            if (jydatatype.contains("SK") || jydatatype.contains("shanghaiyb")) {
                List<ES_ZHANDIANPojo> lists = list.stream().filter(m -> "0".equals(m.getPTYPE()))
                        .collect(Collectors.toList());
                new javalog().writelog("雨量边界站共：" + lists.size() + "个", filePathName);
                for (ES_ZHANDIANPojo obj : lists) {
                    Map<String, Tz_watersheddataPojo> dtMap = dtByKeyidAndFtm.get(obj.getZHANID());
                    for (int i = 0; i < timeCount; i++) {
                        ES_ZHANDIANDATADto dto = new ES_ZHANDIANDATADto();
                        dto.setID(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16));
                        dto.setZHANID(obj.getZHANID());
                        String tm = dateFormat.format(new Date(stimeLong + i * 60 * 60 * 1000));
                        dto.setZHANTIME(tm);
                        dto.setZHANDATA("0.0");
                        dto.setSOLUTIONID(solutionid);
                        dto.setDD_FOR(username);
                        if (dtMap != null) {
                            Tz_watersheddataPojo dr = dtMap.get(tm);
                            if (dr != null) {
                                double hourDrp = dr.getDRP();
                                if (hourDrp < 0 || hourDrp > 500)
                                    hourDrp = 0;
                                dto.setZHANDATA(String.valueOf(hourDrp));
                            }
                        }
                        listData.add(dto);
                    }
                }
            }
            // ========== 分支2：中央预报 zhongyangyb ==========
            else if (jydatatype.contains("zhongyangyb")) {
                List<ES_ZHANDIANPojo> lists = list.stream().filter(m -> "0".equals(m.getPTYPE()))
                        .collect(Collectors.toList());
                Map<String, String> listDataGuanStcdByZhanid = new HashMap<>();
                for (ES_ZHANGUANLIANPojo item : listDataGuan) {
                    listDataGuanStcdByZhanid.put(item.getZHANID(), item.getSTCD());
                }
                for (ES_ZHANDIANPojo obj : lists) {
                    String stcd = listDataGuanStcdByZhanid.get(obj.getZHANID());
                    Map<String, St_rnfl_fPojo> rnflMap = (stcd != null) ? rnflNewByStcdAndTm.get(stcd) : null;
                    for (int i = 0; i < timeCount; i++) {
                        ES_ZHANDIANDATADto dto = new ES_ZHANDIANDATADto();
                        dto.setID(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16));
                        dto.setZHANID(obj.getZHANID());
                        String tm = dateFormat.format(new Date(stimeLong + i * 60 * 60 * 1000));
                        dto.setZHANTIME(tm);
                        dto.setZHANDATA("0.0");
                        dto.setSOLUTIONID(solutionid);
                        dto.setDD_FOR(username);
                        if (rnflMap != null) {
                            St_rnfl_fPojo rnfl = rnflMap.get(tm);
                            if (rnfl != null) {
                                dto.setZHANDATA(String.format("%.1f", rnfl.getDRP()));
                            }
                        }
                        listData.add(dto);
                    }
                }
            }

            // ========== 分支3：工程调度 DDFN ==========
            if (gcdatatype.equals("DDFN")) {
                List<ES_ZHANDIANPojo> listZHAN = list.stream().filter(m -> Integer.parseInt(m.getPTYPE()) >= 3)
                        .collect(Collectors.toList());
                for (ES_ZHANDIANPojo m : listZHAN) {
                    String zhanData = "日常调度";
                    List<ES_MODELGUANLIANPojo> listGuanlianTemp = listGuanlian.stream()
                            .filter(n -> n.getSTCD().contains(m.getZHANID())).collect(Collectors.toList());
                    if (listGuanlianTemp.size() > 0) {
                        String yqID = listGuanlianTemp.get(0).getMKEYID();
                        double num = listData.stream().filter(n -> n.getZHANID().equals(yqID))
                                .mapToDouble(n -> Double.parseDouble(n.getZHANDATA())).sum();
                        List<ES_MODELFANGANPojo> listFangTemp = listFang.stream().filter(n -> n.getMAXDRP() >= num)
                                .sorted(Comparator.comparingDouble(ES_MODELFANGANPojo::getMAXDRP))
                                .collect(Collectors.toList());
                        if (listFangTemp.size() > 0) {
                            List<ES_MODELFANGANZHANPojo> listFaZhanTemp = listFaZhanByFaId
                                    .get(listFangTemp.get(0).getID());
                            if (listFaZhanTemp != null && listFaZhanTemp.size() > 0) {
                                zhanData = listFaZhanTemp.get(0).getNORMAL();
                            }
                        }
                    }
                    for (int i = 0; i < timeCount; i++) {
                        ES_ZHANDIANDATADto dto = new ES_ZHANDIANDATADto();
                        dto.setID(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16));
                        dto.setZHANID(m.getZHANID());
                        String tm = dateFormat.format(new Date(stimeLong + i * 60 * 60 * 1000));
                        dto.setZHANTIME(tm);
                        dto.setZHANDATA(zhanData);
                        dto.setSOLUTIONID(solutionid);
                        dto.setDD_FOR(username);
                        listData.add(dto);
                    }
                }
            }
            // ========== 分支4：放江量 fangjiangliang ==========
            else if (gcdatatype.equals("fangjiangliang")) {
                new javalog().writelog("苏州河泵站采用放江量", filePathName);
                List<ES_ZHANDIANPojo> listZHAN = list.stream().filter(m -> Integer.parseInt(m.getPTYPE()) >= 3)
                        .collect(Collectors.toList());
                for (ES_ZHANDIANPojo m : listZHAN) {
                    String zhanData = "日常调度";
                    List<ES_PUMP_RPojo> listPumpDataTemp = pumpDataByStcd.get(m.getZHANID().trim());
                    if (listPumpDataTemp == null || listPumpDataTemp.isEmpty()) {
                        List<ES_MODELGUANLIANPojo> listGuanlianTemp = listGuanlian.stream()
                                .filter(n -> n.getSTCD().contains(m.getZHANID())).collect(Collectors.toList());
                        if (listGuanlianTemp.size() > 0) {
                            String yqID = listGuanlianTemp.get(0).getMKEYID();
                            double num = listData.stream().filter(n -> n.getZHANID().equals(yqID))
                                    .mapToDouble(n -> Double.parseDouble(n.getZHANDATA())).sum();
                            List<ES_MODELFANGANPojo> listFangTemp = listFang.stream().filter(n -> n.getMAXDRP() >= num)
                                    .sorted(Comparator.comparingDouble(ES_MODELFANGANPojo::getMAXDRP))
                                    .collect(Collectors.toList());
                            if (listFangTemp.size() > 0) {
                                List<ES_MODELFANGANZHANPojo> listFaZhanTemp = listFaZhanByFaId
                                        .get(listFangTemp.get(0).getID());
                                if (listFaZhanTemp != null && listFaZhanTemp.size() > 0) {
                                    zhanData = listFaZhanTemp.get(0).getNORMAL();
                                }
                            }
                        }
                    }
                    Map<String, ES_PUMP_RPojo> pumpMap = pumpDataByStcdAndTm.get(m.getZHANID().trim());
                    // 预计算所有时间字符串
                    String[] timeStringsHourly = new String[timeCount];
                    for (int i = 0; i < timeCount; i++) {
                        timeStringsHourly[i] = dateFormat.format(new Date(stimeLong + i * 60 * 60 * 1000));
                    }
                    for (int i = 0; i < timeCount; i++) {
                        ES_ZHANDIANDATADto dto = new ES_ZHANDIANDATADto();
                        dto.setID(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16));
                        dto.setZHANID(m.getZHANID());
                        String tm = timeStringsHourly[i];
                        if (pumpMap != null && pumpMap.containsKey(tm)) {
                            zhanData = pumpMap.get(tm).getPMPQ().toString();
                        } else {
                            if (pumpMap == null || pumpMap.isEmpty()) {
                                // 保持 zhanData 不变
                            } else {
                                zhanData = "0";
                            }
                        }
                        dto.setZHANTIME(tm);
                        dto.setZHANDATA(zhanData);
                        dto.setSOLUTIONID(solutionid);
                        dto.setDD_FOR(username);
                        listData.add(dto);
                    }
                }
            }

            // ========== 预处理：天文潮列表按站点分组 ==========
            Map<String, List<ST_ASTRONOMICALTIDE_RPojo>> listASByZhanid = new HashMap<>();
            for (ST_ASTRONOMICALTIDE_RPojo item : listAS) {
                listASByZhanid.computeIfAbsent(item.getZHANID(), k -> new ArrayList<>()).add(item);
            }

            long hydroDtNo = timeCount * 12;

            // ========== 分支5：AppModelXIANGSITide ==========
            if (scwdatatype.contains("AppModelXIANGSITide")) {
                String stcdList = "63401750,62701710,63405800,63401100,63401500,63405900";
                List<ST_TIDEHIGHParam> listZS = SWZZ_FLOODTIDEDATA_ST_TIDEHIGH_RSel(stcdList, startdate, enddate, "2");
                listZS.sort(Comparator.comparingLong(n -> {
                    try {
                        return dateFormat.parse(n.getTM()).getTime();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return 0;
                }));
                List<ES_MODELGUANLIANPojo> listGuanlian3001 = esModGuData.selectList("", "3001", null, null);
                List<String> zhanGuanAgg = listGuanlian3001.stream().map(ES_MODELGUANLIANPojo::getMKEYID)
                        .collect(Collectors.toList());

                List<ES_ZHANDIANPojo> filteredStationList = list.stream()
                        .filter(m -> m.getPTYPE().equals("1") || m.getPTYPE().equals("2")).collect(Collectors.toList());

                for (ES_ZHANDIANPojo obj : filteredStationList) {
                    List<ST_ASTRONOMICALTIDE_RPojo> listASTemp = listASByZhanid.get(obj.getZHANID());
                    if (listASTemp == null)
                        listASTemp = new ArrayList<>();
                    for (int num = 0; num < hydroDtNo + 1; num++) {
                        String sTime = dateFormat.format(new Date(stimeLong + num * 5 * 60 * 1000));
                        ES_ZHANDIANDATADto dto = new ES_ZHANDIANDATADto();
                        dto.setID(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16));
                        dto.setZHANID(obj.getZHANID());
                        dto.setZHANTIME(sTime);
                        dto.setSOLUTIONID(solutionid);
                        dto.setZHANDATA("0.0");
                        dto.setDD_FOR(username);
                        if (zhandians.contains(obj.getZHANID())) {
                            String swSTCD = "";
                            if ("1728053248".equals(obj.getZHANID()))
                                swSTCD = "63403500";
                            else if ("1728053250".equals(obj.getZHANID()))
                                swSTCD = "63301150";
                            else if ("1728053251".equals(obj.getZHANID()))
                                swSTCD = "63205150";
                            else if ("1728053252".equals(obj.getZHANID()))
                                swSTCD = "63205350";
                            GetWaterViewNewPojo listWasTemp = listWasByStcdLast.get(swSTCD);
                            if (listWasTemp != null) {
                                double upz = Double.parseDouble(listWasTemp.getUPZ()) - 0.26;
                                dto.setZHANDATA(String.format("%.2f", upz));
                            }
                            ST_FORECAST_FPojo listForTemp = listForByStcdAndYmdh.get(obj.getZHANID() + "_" + sTime);
                            if (listForTemp != null) {
                                double upz = listForTemp.getZ() - 0.26;
                                dto.setZHANDATA(String.format("%.2f", upz));
                            }
                        } else {
                            ST_ASTRONOMICALTIDE_RPojo ast = listASByZhanidAndTm.get(obj.getZHANID() + "_" + sTime);
                            if (ast != null) {
                                dto.setZHANDATA(String.format("%.2f", ast.getZ()));
                            }
                        }
                        listData.add(dto);
                    }
                }
            }
            // ========== 分支6：普通预报风暴潮（else分支） ==========
            else {
                List<ES_ZHANDIANPojo> filteredStationList = list.stream()
                        .filter(m -> "1".equals(m.getPTYPE()) || "2".equals(m.getPTYPE())).collect(Collectors.toList());
                new javalog().writelog(
                        "MODIFY_MODEZHANDDataNew：站点数=" + filteredStationList.size() + ",时间点数=" + (hydroDtNo + 1),
                        filePathName);

                int Minutes = 5;
                // ========== 优化：预计算所有时间字符串，避免100,800次Date对象创建 ==========
                int totalTimes = (int) (hydroDtNo + 1);
                String[] timeStrings = new String[totalTimes];
                for (int num = 0; num < hydroDtNo + 1; num++) {
                    timeStrings[num] = dateFormat.format(new Date(stimeLong + num * Minutes * 60 * 1000));
                }

                for (ES_ZHANDIANPojo obj : filteredStationList) {
                    String zhanid = obj.getZHANID();
                    ES_ZHANDIANXSPojo listXSTemp = listXSByMkeyid.get(zhanid);
                    String stcdSW = listDataGuanSWStcdByZhanid.get(zhanid);

                    for (int num = 0; num < hydroDtNo + 1; num++) {
                        String stime = timeStrings[num];
                        ES_ZHANDIANDATADto Dto = new ES_ZHANDIANDATADto();
                        Dto.setID(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16));
                        Dto.setZHANID(zhanid);
                        Dto.setZHANTIME(stime);
                        Dto.setSOLUTIONID(solutionid);
                        Dto.setZHANDATA("0.0");
                        Dto.setDD_FOR(username);

                        if (zhandians.contains(zhanid)) {
                            String swSTCD = "";
                            if ("1728053248".equals(zhanid))
                                swSTCD = "63403500";
                            else if ("1728053250".equals(zhanid))
                                swSTCD = "63301150";
                            else if ("1728053251".equals(zhanid))
                                swSTCD = "63205150";
                            else if ("1728053252".equals(zhanid))
                                swSTCD = "63205350";
                            GetWaterViewNewPojo listWasTemp = listWasByStcdLast.get(swSTCD);
                            if (listWasTemp != null) {
                                double upz = Double.parseDouble(listWasTemp.getUPZ()) - 0.26;
                                Dto.setZHANDATA(String.format("%.2f", upz));
                            }
                            ST_FORECAST_FPojo listForTemp = listForByStcdAndYmdh.get(zhanid + "_" + stime);
                            if (listForTemp != null) {
                                double upz = listForTemp.getZ() - 0.26;
                                Dto.setZHANDATA(String.format("%.2f", upz));
                            }
                        } else {
                            Double DATA = 0.0;
                            ST_ASTRONOMICALTIDE_RPojo listASTempT = listASByZhanidAndTm.get(zhanid + "_" + stime);
                            if (listASTempT != null) {
                                DATA = listASTempT.getZ();
                            }
                            if (jydatatype.contains("typhoon") || jydatatype.contains("temperatezone")) {
                                St_tide_rybPojo listTideTemp = listTideByTm.get(stime);
                                if (listTideTemp != null) {
                                    double TDZ = listTideTemp.getTDZ();
                                    double XS = (listXSTemp != null) ? listXSTemp.getXS() : 0;
                                    DATA = DATA + TDZ * XS;
                                }
                            }
                            if (scwdatatype.contains("样条函数插值") || scwdatatype.contains("余弦曲线插值")) {
                                if (stcdSW != null) {
                                    ES_TIDALFORECASTGCPojo tideGCPojoT = tideGCPojoByStcdAndTm
                                            .get(stcdSW + "_" + stime);
                                    if (tideGCPojoT != null) {
                                        DATA = tideGCPojoT.getTDZ();
                                    }
                                }
                            }
                            Dto.setZHANDATA(String.format("%.2f", DATA));
                        }
                        listData.add(Dto);
                    }
                }
            }

            // ========== 入库操作 ==========
            if (listData.size() > 0) {
                long deleteStart = System.currentTimeMillis();
                try {
                    data.deleteOneBySOLUTIONID(solutionid);
                } catch (Exception e) {
                    /* ignore */ }
                long deleteCost = System.currentTimeMillis() - deleteStart;

                // 优化：增大批量大小，减少数据库往返次数
                long insertStart = System.currentTimeMillis();
                int count = 4000; // 从800增大到4000
                int nums = listData.size() / count;
                if (listData.size() % count != 0) {
                    nums += 1;
                }
                for (int j = 0; j < nums; j++) {
                    int start = j * count;
                    int end = Math.min(start + count, listData.size());
                    List<ES_ZHANDIANDATADto> dtoList = listData.subList(start, end);
                    number += data.insertALLDto(dtoList);
                }
                long insertCost = System.currentTimeMillis() - insertStart;
                new javalog().writelog("MODIFY_MODEZHANDDataNew：删除耗时" + deleteCost + "ms,插入" + listData.size() + "条耗时"
                        + insertCost + "ms", filePathName);
            }
        } catch (Exception e) {
            number = 0;
            e.printStackTrace();
            new javalog().writelog("MODIFY_MODEZHANDDataNew接口报错：" + e.getMessage(), filePathName);
        }
        return number;
    }
}
