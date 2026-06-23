package swzzmodeserver.workserver.service.swzzmode;

import com.alibaba.fastjson.JSON;

import org.apache.commons.lang.ObjectUtils.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swzzmodeserver.tools.ComputeHL;
import swzzmodeserver.workserver.data.swzzmode.*;
import swzzmodeserver.workserver.data.swzzrtsq.RTSQST_RVFCCH_BData;
import swzzmodeserver.workserver.data.swzzrtsq.RTSQST_STBPRP_BData;
import swzzmodeserver.workserver.data.wds.RTEVData;
import swzzmodeserver.workserver.data.swzzflood.RTSQData;
import swzzmodeserver.workserver.pojo.swzzmode.*;
import swzzmodeserver.workserver.server.swzzrtsq.GetWaterViewNewServer;
import swzzmodeserver.workserver.pojo.swzzflood.RTSQPojo;
import swzzmodeserver.workserver.pojo.swzzflood.ST_WAS_RPojo;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
@Service
public class BDMS_PREDICTServiceImpl implements BDMS_PREDICTService {
    private final DD_SOLUTIONData ddSolutionData;
    private final ES_ZHANDIANDATAData esZhandiandataData;
    private final BDMS_PREDICTData bdmsPredictData;
    private final RTSQData rtsqData;
    private final ES_MODELGUANLIANData esModelguanlianData;
    private final ES_JISUANZHANData esJisuanzhanData;
    private final ES_JISUANZHANPXData esJisuanzhanpxData;
    private final ST_STBPRP_BData stbprpBData;
    private final ST_RVFCCH_BData stRvfcchBData;
    private final ES_SLTONGJIData esSltongjiData;
    private final ES_ZHANGUANLIANData esZhanguanlianData;
    private final ES_ZHANDIANDATAServiceImpl dataService;
    private final RTEVData rtevData;
    @Autowired
    private RTSQST_STBPRP_BData rStbprp_BData;
    @Autowired
    private RTSQST_RVFCCH_BData rRvfcch_BData;
    @Autowired
    private GetWaterViewNewServer server;
    @Autowired
    public BDMS_PREDICTServiceImpl(DD_SOLUTIONData ddSolutionData,ES_ZHANDIANDATAData esZhandiandataData,
                                   BDMS_PREDICTData bdmsPredictData,RTSQData rtsqData,
                                   ES_MODELGUANLIANData esModelguanlianData,ES_JISUANZHANData esJisuanzhanData,
                                   ES_JISUANZHANPXData esJisuanzhanpxData,ST_STBPRP_BData stbprpBData,
                                   ST_RVFCCH_BData stRvfcchBData,ES_SLTONGJIData esSltongjiData,
                                   ES_ZHANGUANLIANData esZhanguanlianData,
                                   ES_ZHANDIANDATAServiceImpl dataService,
                                   RTEVData rtevData,
                                   RTSQST_STBPRP_BData rStbprp_BData,
                                   GetWaterViewNewServer server) {
        this.ddSolutionData = ddSolutionData;
        this.esZhandiandataData = esZhandiandataData;
        this.bdmsPredictData = bdmsPredictData;
        this.rtsqData = rtsqData;
        this.esModelguanlianData = esModelguanlianData;
        this.esJisuanzhanData = esJisuanzhanData;
        this.esJisuanzhanpxData = esJisuanzhanpxData;
        this.stbprpBData = stbprpBData;
        this.stRvfcchBData = stRvfcchBData;
        this.esSltongjiData = esSltongjiData;
        this.esZhanguanlianData = esZhanguanlianData;
        this.dataService = dataService;
        this.rtevData= rtevData;
        this.rStbprp_BData=rStbprp_BData;
        this.server = server;
    }

    @Override
    public String GetFangAnData(String DD_MIND, String STCD, String DATA_TYPE,String DD_EVALUE,String MKEYID) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<DD_SOLUTIONPojo> listDD = ddSolutionData.selectList("","", Collections.singletonList(DD_MIND),null, "", "", null, null);
        if(null != DD_EVALUE && !DD_EVALUE.equals("")){
            listDD = listDD.stream().filter(m -> DD_EVALUE.equals(m.getDD_EVALUE())).collect(Collectors.toList());
        }
//       listDD.stream().sorted(Comparator.comparingLong(a -> {
//                    try {
//                        return format.parse(a.getDD_CARRYTM()).getTime();
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//                    return 0;
//                })).collect(Collectors.toList());
        listDD.sort((t1,t2)->{
            return t2.getDD_CARRYTM().compareTo(t1.getDD_CARRYTM());
        });
        List<Map<String,String>> _jsonText = new ArrayList<>();
        List<Map<String,String>> strWheres = new ArrayList<>();
        List<String> PLAN_N = new ArrayList<>();
        if(listDD.size() > 0){
            if("1".equals(DATA_TYPE)){
                Map<String,String> json = new HashMap<>();
                json.put("STCD","VALUE");
                json.put("STNM","实测");
                _jsonText.add(json);
            }
            listDD.forEach(m->{
                PLAN_N.add(m.getDD_ID());
                Map<String,String> json = new HashMap<>();
                json.put("STCD",m.getDD_ID());
                json.put("STNM",m.getDD_NAME());
                _jsonText.add(json);
                if("1".equals(DATA_TYPE)){
                    json = new HashMap<>();
                    json.put("STCD","DRP" + m.getDD_ID());
                    json.put("STNM",m.getDD_NAME() + "预报降雨");
                    _jsonText.add(json);
                }
            });
        }

        List<BDMS_PREDICTPojo> listBDMS = bdmsPredictData.selectList("", "", "", PLAN_N, STCD, DATA_TYPE, null, null,"",null);
        List<ES_ZHANDIANDATAPojo> _pptn_r = new ArrayList<>();
        if(!"".equals(MKEYID)){
            _pptn_r = esZhandiandataData.selectList("", null, null, "", Collections.singletonList(MKEYID),null,null)
                    .stream().filter(m -> PLAN_N.contains(m.getSOLUTIONID())).collect(Collectors.toList());
        }
        if(listBDMS.size() > 0){
            Set<String> tmSet = new TreeSet<>();
            listBDMS.forEach(m->{
                tmSet.add(m.getYMDHM());
            });

            List<String> dtTM = tmSet.stream().sorted(Comparator.comparingLong(t -> {
                try {
                    return format.parse(t).getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            })).collect(Collectors.toList());
            if(dtTM.size() > 0){
                String stime = dtTM.get(0);
                String etime = dtTM.get(dtTM.size() - 1);
                List<ST_WAS_RPojo> rtsqList = new ArrayList<>();
                if("1".equals(DATA_TYPE)){
                    try {
                        //int year = format.parse(stime).getYear();
                        // if(format.parse(stime).getYear() + 1900 >= 2022){
                        //     rtsqList = rtsqData.getSCSW(stime,etime,Collections.singletonList(STCD),"");
                        //     if(rtsqList.size() == 0){
                        //         rtsqList = rtsqData.getSCSWLS(stime,etime,Collections.singletonList(STCD),"");
                        //     }
                        // }else {
                        //     rtsqList = rtsqData.getSCSWLS(stime,etime,Collections.singletonList(STCD),"");
                        // }
                        rtsqList=dataService.getSCSW(stime, etime, Collections.singletonList(STCD), null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                for (String tm : dtTM){
                    Map<String,String> strWhere = new HashMap<>();
                    String curTM=tm.replaceAll(".000000","");
                    strWhere.put("TM",curTM);
                    if("1".equals(DATA_TYPE)){
                        List<ST_WAS_RPojo> rtsqPojoList = rtsqList.stream().filter(m -> STCD.trim().equals(m.getSTCD().trim()) && curTM.equals(m.getTM())).collect(Collectors.toList());
                        if(rtsqPojoList.size() > 0){
                            strWhere.put("VALUE",rtsqPojoList.get(0).getUPZ().toString());
                        }
                    }
                    List<ES_ZHANDIANDATAPojo> final_pptn_r = _pptn_r;
                    listDD.forEach(m->{
                        String dd_id = m.getDD_ID();
                        List<ES_ZHANDIANDATAPojo> tempPP = final_pptn_r.stream().filter(s -> s.getSOLUTIONID().trim().equals(dd_id.trim()) && s.getZHANTIME().equals(tm)).collect(Collectors.toList());
                        if(tempPP.size() > 0){
                            strWhere.put("DRP"+dd_id,tempPP.get(0).getZHANDATA());
                        }
                        List<BDMS_PREDICTPojo> temp = listBDMS.stream().filter(s -> s.getYMDHM().equals(tm) && s.getPLAN_N().trim().equals(dd_id.trim())).collect(Collectors.toList());
                        if(temp.size() > 0){
                            Float data = temp.get(0).getDATA();
                            String str = "";
                            if(null != data){
                                str = String.format("%.2f",data);
                            }
                            strWhere.put(dd_id,str);
                        }
                    });
                    strWheres.add(strWhere);
                }
            }
        }
        Map<String,Object> map = new HashMap<>();
        map.put("data",strWheres);
        map.put("total",strWheres.size());
        return JSON.toJSONString(_jsonText) + "@" + JSON.toJSONString(map);
    }
    @Override
    public String GetFangAnDataDuo(String DD_ID, String STCD, String DATA_TYPE,String DD_EVALUE,String MKEYID) {
        List<String> idList = Arrays.stream(DD_ID.split(","))
                .map(String::trim) // 去除每个 ID 前后可能的空格
                .collect(Collectors.toList());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<DD_SOLUTIONPojo> listDD = ddSolutionData.selectListDuo("","", idList,null, "", "", null, null);
        if(null != DD_EVALUE && !DD_EVALUE.equals("")){
            listDD = listDD.stream().filter(m -> DD_EVALUE.equals(m.getDD_EVALUE())).collect(Collectors.toList());
        }
//       listDD.stream().sorted(Comparator.comparingLong(a -> {
//                    try {
//                        return format.parse(a.getDD_CARRYTM()).getTime();
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//                    return 0;
//                })).collect(Collectors.toList());
        listDD.sort((t1,t2)->{
            return t2.getDD_CARRYTM().compareTo(t1.getDD_CARRYTM());
        });
        List<Map<String,String>> _jsonText = new ArrayList<>();
        List<Map<String,String>> strWheres = new ArrayList<>();
        List<String> PLAN_N = new ArrayList<>();
        if(listDD.size() > 0){
            if("1".equals(DATA_TYPE)){
                Map<String,String> json = new HashMap<>();
                json.put("STCD","VALUE");
                json.put("STNM","实测");
                _jsonText.add(json);
            }
            listDD.forEach(m->{
                PLAN_N.add(m.getDD_ID());
                Map<String,String> json = new HashMap<>();
                json.put("STCD",m.getDD_ID());
                json.put("STNM",m.getDD_NAME());
                _jsonText.add(json);
                if("1".equals(DATA_TYPE)){
                    json = new HashMap<>();
                    json.put("STCD","DRP" + m.getDD_ID());
                    json.put("STNM",m.getDD_NAME() + "预报降雨");
                    _jsonText.add(json);
                }
            });
        }

        List<BDMS_PREDICTPojo> listBDMS = bdmsPredictData.selectList("", "", "", PLAN_N, STCD, DATA_TYPE, null, null,"",null);
        List<ES_ZHANDIANDATAPojo> _pptn_r = new ArrayList<>();
        if(!"".equals(MKEYID)){
            _pptn_r = esZhandiandataData.selectList("", null, null, "", Collections.singletonList(MKEYID),null,null)
                    .stream().filter(m -> PLAN_N.contains(m.getSOLUTIONID())).collect(Collectors.toList());
        }
        if(listBDMS.size() > 0){
            Set<String> tmSet = new TreeSet<>();
            listBDMS.forEach(m->{
                tmSet.add(m.getYMDHM());
            });

            List<String> dtTM = tmSet.stream().sorted(Comparator.comparingLong(t -> {
                try {
                    return format.parse(t).getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            })).collect(Collectors.toList());
            if(dtTM.size() > 0){
                String stime = dtTM.get(0);
                String etime = dtTM.get(dtTM.size() - 1);
                List<ST_WAS_RPojo> rtsqList = new ArrayList<>();
                if("1".equals(DATA_TYPE)){
                    try {
                        //int year = format.parse(stime).getYear();
                        // if(format.parse(stime).getYear() + 1900 >= 2022){
                        //     rtsqList = rtsqData.getSCSW(stime,etime,Collections.singletonList(STCD),"");
                        //     if(rtsqList.size() == 0){
                        //         rtsqList = rtsqData.getSCSWLS(stime,etime,Collections.singletonList(STCD),"");
                        //     }
                        // }else {
                        //     rtsqList = rtsqData.getSCSWLS(stime,etime,Collections.singletonList(STCD),"");
                        // }

                        rtsqList=dataService.getSCSW(stime, etime, idList, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                for (String tm : dtTM){
                    Map<String,String> strWhere = new HashMap<>();
                    String curTM=tm.replaceAll(".000000","");
                    strWhere.put("TM",curTM);
                    if("1".equals(DATA_TYPE)){
                        List<ST_WAS_RPojo> rtsqPojoList = rtsqList.stream().filter(m -> STCD.trim().equals(m.getSTCD().trim()) && curTM.equals(m.getTM())).collect(Collectors.toList());
                        if(rtsqPojoList.size() > 0){
                            strWhere.put("VALUE",rtsqPojoList.get(0).getUPZ().toString());
                        }
                    }
                    List<ES_ZHANDIANDATAPojo> final_pptn_r = _pptn_r;
                    listDD.forEach(m->{
                        String dd_id = m.getDD_ID();
                        List<ES_ZHANDIANDATAPojo> tempPP = final_pptn_r.stream().filter(s -> s.getSOLUTIONID().trim().equals(dd_id.trim()) && s.getZHANTIME().equals(tm)).collect(Collectors.toList());
                        if(tempPP.size() > 0){
                            strWhere.put("DRP"+dd_id,tempPP.get(0).getZHANDATA());
                        }
                        List<BDMS_PREDICTPojo> temp = listBDMS.stream().filter(s -> s.getYMDHM().equals(tm) && s.getPLAN_N().trim().equals(dd_id.trim())).collect(Collectors.toList());
                        if(temp.size() > 0){
                            Float data = temp.get(0).getDATA();
                            String str = "";
                            if(null != data){
                                str = String.format("%.2f",data);
                            }
                            strWhere.put(dd_id,str);
                        }
                    });
                    strWheres.add(strWhere);
                }
            }
        }
        Map<String,Object> map = new HashMap<>();
        map.put("data",strWheres);
        map.put("total",strWheres.size());
        return JSON.toJSONString(_jsonText) + "@" + JSON.toJSONString(map);
    }

    @Override
    public List<AREASLParam> AREASL(String DD_ID, String STCD) {
        List<AREASLParam> paramList = new ArrayList<>();
        List<BDMS_PREDICTPojo> bdmsList = bdmsPredictData.selectList("", "", "", Collections.singletonList(DD_ID), "", "", null, null, "asc",null);
        List<ES_MODELGUANLIANPojo> listG = esModelguanlianData.selectList("", "4", null, null);
        List<ES_MODELGUANLIANPojo> listGJY = esModelguanlianData.selectList("", "5", null, null);
        if(!"".equals(STCD)){
            listG = listG.stream().filter(m->STCD.trim().equals(m.getSTCD().trim())).collect(Collectors.toList());
            listGJY = listGJY.stream().filter(m->STCD.trim().equals(m.getSTCD().trim())).collect(Collectors.toList());
        }
        List<String> mkeyids = listG.stream().map(m->m.getMKEYID().trim()).collect(Collectors.toList());
        //List<String> stcds = listG.stream().map(ES_MODELGUANLIANPojo::getSTCD).collect(Collectors.toList());
        List<String> mkeyidsCL = listGJY.stream().map(m->m.getMKEYID().trim()).collect(Collectors.toList());
        List<BDMS_PREDICTPojo> bdmsListRQ = bdmsList.stream().filter(m->"24".equals(m.getDATA_TYPE())).collect(Collectors.toList());
        List<BDMS_PREDICTPojo> bdmsListCQ = bdmsList.stream()
                .filter(m->"25".equals(m.getDATA_TYPE()) && mkeyids.contains(m.getSTCD().trim())).collect(Collectors.toList());
        List<BDMS_PREDICTPojo> bdmsListCL = bdmsList.stream()
                .filter(m->"2".equals(m.getDATA_TYPE()) && mkeyidsCL.contains(m.getSTCD().trim())).collect(Collectors.toList());
        List<ES_MODELGUANLIANPojo> finalListGJY = listGJY;
        listG.forEach(m->{
            AREASLParam dto = new AREASLParam();
            dto.setSTCD(m.getSTCD());
            dto.setSTNM(m.getFIELD());
            List<BDMS_PREDICTPojo> rq = bdmsListRQ.stream().filter(n -> m.getSTCD().trim().equals(n.getSTCD().trim())).collect(Collectors.toList());
            List<BDMS_PREDICTPojo> cq = bdmsListCQ.stream().filter(n -> m.getMKEYID().trim().equals(n.getSTCD().trim())).collect(Collectors.toList());
            double rsll = rq.stream().mapToDouble(BDMS_PREDICTPojo::getDATA).sum();
            double csll = cq.stream().mapToDouble(BDMS_PREDICTPojo::getDATA).sum();
            dto.setRSL(String.valueOf(new BigDecimal(Math.round(rsll * 0.36 * 10)).setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue() / 10));
            dto.setCSL(String.valueOf(new BigDecimal(Math.round(csll * 0.36 * 10)).setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue() / 10));
            List<ES_MODELGUANLIANPojo> collect = finalListGJY.stream().filter(n -> n.getSTCD().trim().equals(m.getSTCD().trim())).collect(Collectors.toList());
            if(collect.size() > 0){
                double jycsll = bdmsListCL.stream().filter(n -> n.getSTCD().trim().equals(collect.get(0).getMKEYID().trim())).mapToDouble(BDMS_PREDICTPojo::getDATA).sum();
                dto.setCLSL(String.valueOf(new BigDecimal(Math.round(jycsll * 0.36 * 10)).setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue() / 10));
                dto.setJYMKEYID(collect.get(0).getMKEYID());
            }
            paramList.add(dto);
        });
        return paramList;
    }

    @Override
    public List<YBList> YBList(String solutionid,String typeID,String TM) {
        List<YBList> ybListList = new ArrayList<>();
        List<BDMS_PREDICTPojo> listBDMS = bdmsPredictData.selectList("", TM, TM, Collections.singletonList(solutionid), "", typeID, null, null, "asc",null);
        List<ES_JISUANZHANPojo> eS_JISUANZHANs = esJisuanzhanData.selectList("", Collections.singletonList(typeID), "",null, null);
        List<String> aggList = eS_JISUANZHANs.stream().map(ES_JISUANZHANPojo::getSTCD).collect(Collectors.toList());
        List<ES_JISUANZHANPXPojo> eS_JISUANZHANsPX = esJisuanzhanpxData.selectList("", null, null);
        // List<ST_STBPRP_BDto> dtST_STBPRP_B = stbprpBData.selectListBandStcd("");

        List<swzzmodeserver.workserver.pojo.swzzrtsq.ST_STBPRP_BPojo> dtST_STBPRP_B = rStbprp_BData.selectList(null,null);

        List<ST_RVFCCH_BPojo> dtST_RVFCCH_B = stRvfcchBData.selectList("", "", "", null, null);
        List<ES_MODELGUANLIANPojo> listG = esModelguanlianData.selectList("", "3", null, null);
        List<ES_SLTONGJIPojo> listSL = esSltongjiData.selectList("", "2",null, null, null);
        List<ES_ZHANGUANLIANPojo> listGuan = esZhanguanlianData.selectList("", null, null, Collections.singletonList("3"));
        if(listBDMS.size() > 0){
            if(eS_JISUANZHANs.size() > 0){
                for(ES_JISUANZHANPojo obj : eS_JISUANZHANs){
                    List<ES_JISUANZHANPXPojo> eS_JISUANZHANsPXTemp = eS_JISUANZHANsPX.stream().filter(m -> obj.getSTCD().trim().equals(m.getSTCD().trim())).collect(Collectors.toList());
                    String stcd = obj.getSTCD() != null ? obj.getSTCD() : obj.getID();
                    List<BDMS_PREDICTPojo> listBDMSTemp = listBDMS.stream().filter(m -> stcd.trim().equals(m.getSTCD().trim()) && solutionid.trim().equals(m.getPLAN_N().trim())).collect(Collectors.toList());
                    List<swzzmodeserver.workserver.pojo.swzzrtsq.ST_STBPRP_BPojo> st_stbrprp_b = dtST_STBPRP_B.stream().filter(m -> obj.getSTCD().trim().equals(m.getSTCD().trim())).collect(Collectors.toList());
                    String Type = "";
                    List<ES_MODELGUANLIANPojo> listGtemp = listG.stream().filter(m -> stcd.trim().equals(m.getSTCD().trim())).collect(Collectors.toList());
                    if(listGtemp.size() > 0){
                        List<ES_SLTONGJIPojo> listSLTemp = listSL.stream().filter(m -> listGtemp.get(0).getMKEYID().trim().equals(m.getSTCD().trim())).collect(Collectors.toList());
                        if(listSLTemp.size() > 0){
                            Type = listSLTemp.get(0).getTITLE();
                        }
                    }
                    String angle = "";
                    List<ES_ZHANGUANLIANPojo> listGuanTemp = listGuan.stream().filter(m -> stcd.trim().equals(m.getSTCD().trim())).collect(Collectors.toList());
                    if(listGuanTemp.size() > 0){
                        angle = listGuanTemp.get(0).getANGLE();
                    }
                    for(BDMS_PREDICTPojo BDMSobj : listBDMSTemp){
                        YBList ybList = new YBList();
                        if(eS_JISUANZHANsPXTemp.size() > 0){
                            ybList.setPX(String.valueOf(eS_JISUANZHANsPXTemp.get(0).getPX()));
                        }
                        ybList.setTYPE(Type);
                        ybList.setANGLE(angle);
                        ybList.setSTCD(obj.getSTCD());
                        ybList.setSTNM(obj.getNAME());
                        ybList.setDATATYPE(obj.getTYPE());
                        ybList.setDATA(String.valueOf(BDMSobj.getDATA()));
                        ybList.setTM(BDMSobj.getYMDHM());
                        Double max = listBDMSTemp.stream().mapToDouble(BDMS_PREDICTPojo::getDATA).max().getAsDouble();
                        List<BDMS_PREDICTPojo> maxOne = listBDMSTemp.stream().filter(m -> max.equals(m.getDATA().doubleValue())).collect(Collectors.toList());
                        ybList.setMAXDATA(String.valueOf(max));
                        if(maxOne.size() > 0){
                            ybList.setMAXDATATM(maxOne.get(0).getYMDHM()); 
                        }
                        if(st_stbrprp_b.size() > 0){
                            ybList.setLGTD(String.valueOf(st_stbrprp_b.get(0).getLGTD()));
                            ybList.setLTTD(String.valueOf(st_stbrprp_b.get(0).getLTTD()));
                            ybList.setSTNM(st_stbrprp_b.get(0).getSTNM());
                            List<ST_RVFCCH_BPojo> temp_ST_RVFCCH_B = dtST_RVFCCH_B.stream().filter(m -> st_stbrprp_b.get(0).getSTCD().trim().equals(m.getSTCD().trim())).collect(Collectors.toList());
                            if(temp_ST_RVFCCH_B.size() > 0){
                                ybList.setGRZ(String.valueOf(temp_ST_RVFCCH_B.get(0).getGRZ()));
                                ybList.setWRZ(String.valueOf(temp_ST_RVFCCH_B.get(0).getWRZ()));
                            }
                        }
                        ybListList.add(ybList);
                    }
                }
            }
        }
        return ybListList;
    }

    @Override
    public List<WJ_MODELSINGRESULTParam> WJ_MODELSINGRESULT(List<String> SOLUTIONID,List<String> STCD,String DATA_TYPE) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<BDMS_PREDICTPojo> ListBDMS = bdmsPredictData.selectList("", "", "", SOLUTIONID, "", DATA_TYPE, null, null, "",null);
        if(null != STCD && STCD.size() > 0){
            ListBDMS = ListBDMS.stream().filter(m -> STCD.contains(m.getSTCD().trim())).collect(Collectors.toList());
        }
        List<DD_SOLUTIONPojo> ListDD = ddSolutionData.selectList(null,"", null, null, null, null, null,null)
                .stream().filter(m->{
                    //System.out.println(m.getDD_ID());
                    return SOLUTIONID.contains(m.getDD_ID());
                }).collect(Collectors.toList());
        // List<ST_STBPRP_BDto> ListBasic = stbprpBData.selectListBandStcd("").stream().filter(m -> "1".equals(m.getTYPE().trim())).collect(Collectors.toList());
        
        List<swzzmodeserver.workserver.pojo.swzzrtsq.ST_STBPRP_BPojo> ListBasic = rStbprp_BData.selectList(null,null);

        List<swzzmodeserver.workserver.pojo.swzzrtsq.ST_RVFCCH_BPojo> List_B = rRvfcch_BData.selectList(null);
        List<ES_JISUANZHANPojo> List_G = esJisuanzhanData.selectList("", Collections.singletonList(DATA_TYPE), "",null, null);
        List<ES_JISUANZHANPXPojo> es_jisuanpx  = esJisuanzhanpxData.selectList("", null, null).stream().sorted(Comparator.comparingDouble(ES_JISUANZHANPXPojo::getPX)).collect(Collectors.toList());
        List<WJ_MODELSINGRESULTParam> wjModelsingresultParamList = new ArrayList<>();
        List<BDMS_PREDICTPojo> finalListBDMS = ListBDMS;
        ListDD.forEach(m->{
            List_G.forEach(n->{
                WJ_MODELSINGRESULTParam dto = new WJ_MODELSINGRESULTParam();
                dto.setSOLUTIONID(m.getDD_ID());
                dto.setDD_NAME(m.getDD_NAME());
                dto.setSTCD(n.getSTCD());

                dto.setDD_TM(m.getDD_TM());
                dto.setDD_CHECKBY(m.getDD_CHECKBY());

                List<BDMS_PREDICTPojo> ListBDMSTemp = finalListBDMS.stream().filter(s -> s.getPLAN_N().trim().equals(m.getDD_ID().trim()) && s.getSTCD().trim().equals(n.getSTCD().trim())).collect(Collectors.toList());
                if(ListBDMSTemp.size() > 0){
                    float max = ListBDMSTemp.stream().map(BDMS_PREDICTPojo::getDATA).max(Float::compareTo).get();
                    dto.setMAXZ(String.valueOf(max));
                    String maxtm =  ListBDMSTemp.stream().filter(s->max == s.getDATA()).collect(Collectors.toList()).get(0).getYMDHM();
                    dto.setMAXTM(maxtm);
                    float min = ListBDMSTemp.stream().map(BDMS_PREDICTPojo::getDATA).min(Float::compareTo).get();
                    dto.setMINZ(String.valueOf(min));
                    String mintm =  ListBDMSTemp.stream().filter(s->min == s.getDATA()).collect(Collectors.toList()).get(0).getYMDHM();
                    dto.setMINTM(mintm);
                }
                List<swzzmodeserver.workserver.pojo.swzzrtsq.ST_STBPRP_BPojo> List_BT = ListBasic.stream().filter(s -> s.getSTCD().trim().equals(n.getSTCD().trim())).collect(Collectors.toList());
                if(List_BT.size() > 0){
                    dto.setSTNM(List_BT.get(0).getSTNM());
                    dto.setLGTD(List_BT.get(0).getLGTD());
                    dto.setLTTD(List_BT.get(0).getLTTD());
                    dto.setLGTD84(List_BT.get(0).getLGTD84());
                    dto.setLTTD84(List_BT.get(0).getLTTD84());
                    dto.setPX(100.0);
                    List<ES_JISUANZHANPXPojo> es_jisuanpxTemp = es_jisuanpx.stream().filter(s -> s.getSTCD().trim().equals(List_BT.get(0).getSTCD().trim())).collect(Collectors.toList());
                    if(es_jisuanpxTemp.size() > 0){
                        dto.setPX(Double.valueOf(es_jisuanpxTemp.get(0).getPX()));
                    }
                    if(ListBDMSTemp.size() > 0){
                        List<swzzmodeserver.workserver.pojo.swzzrtsq.ST_RVFCCH_BPojo> List_Btemp = List_B.stream().filter(s -> s.getSTCD().trim().equals(List_BT.get(0).getSTCD().trim())).collect(Collectors.toList());
                        if(List_Btemp.size() > 0){
                            if(List_Btemp.get(0).getWRZ() != null && List_Btemp.get(0).getWRZ() != 0){
                                dto.setWRZ(List_Btemp.get(0).getWRZ());
                                List<BDMS_PREDICTPojo> list = ListBDMSTemp.stream().filter(s -> {
                                    int minutes = -1;
                                    try {
                                        minutes = dateFormat.parse(s.getYMDHM()).getMinutes();
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    return s.getDATA() >= dto.getWRZ() && !s.getYMDHM().trim().equals(m.getDD_TM().trim()) && minutes == 0;
                                }).collect(Collectors.toList());
                                dto.setDURW(list.size());
                            }else {
                                dto.setWRZ(0.00);
                                dto.setDURW(0);
                            }
                            if(List_Btemp.get(0).getGRZ() != null && List_Btemp.get(0).getGRZ() != 0){
                                dto.setGRZ(List_Btemp.get(0).getGRZ());
                                List<BDMS_PREDICTPojo> list = ListBDMSTemp.stream().filter(s -> s.getDATA() >= dto.getGRZ()).collect(Collectors.toList());
                                dto.setDURG(list.size());
                            }else {
                                dto.setGRZ(0.00);
                                dto.setDURG(0);
                            }
                            dto.setXZDZ(String.valueOf(List_Btemp.get(0).getIVHZ()));
                        }
                    }
                }else {
                    dto.setSTNM(n.getNAME());
                }
                wjModelsingresultParamList.add(dto);
            });
        });
        
        if(wjModelsingresultParamList.size()>0){
            return wjModelsingresultParamList.stream()
            .sorted(Comparator.comparing(
                WJ_MODELSINGRESULTParam::getPX,          // 这里返回的是 Double 对象
                Comparator.nullsLast(Double::compareTo)  // 处理 null 值排在最后
            ))
            .collect(Collectors.toList());
        }
        else{
            return wjModelsingresultParamList;
        }
    }

    @Override
    public List<Map<String,String>> MODE_BDMS_PREDICTSelMORE(String stime, String etime, List<String> singletonList, String stcd, String data_type) {
        List<Map<String,String>> maps = new ArrayList<>();
        List<BDMS_PREDICTPojo> userList = bdmsPredictData.selectList("", stime, etime, singletonList, "", data_type, null, null, "asc",null).stream().filter(m->Arrays.asList(stcd.split(",")).contains(m.getSTCD().trim())).collect(Collectors.toList());
        List<ST_WAS_RPojo> _WAS_Rs = new ArrayList<>();
        List<ST_RVFCCH_BPojo> st_rvfcch_b = new ArrayList<>();
        if ("1".equals(data_type)){
            List<String> stringList = Arrays.asList(stcd.split(","));
            // _WAS_Rs = dataService.getSCSW(stime, etime, stringList, null);
            _WAS_Rs=dataService.getSCSW(stime, etime, stringList, null);
            st_rvfcch_b = stRvfcchBData.selectList("", "", "", null, null)
                    .stream().filter(m -> stringList.contains(m.getSTCD().trim())).collect(Collectors.toList());
        }
        List<ST_RVFCCH_BPojo> finalSt_rvfcch_b = st_rvfcch_b;
        List<ST_WAS_RPojo> final_WAS_Rs = _WAS_Rs;
        userList.forEach(m->{
            Map<String,String> dto = new HashMap<>();
            dto.put("STCD",m.getSTCD().trim());
            dto.put("YMDHM",m.getYMDHM());
            dto.put("PLAN_N",m.getPLAN_N());
            dto.put("DATA_TYPE",m.getDATA_TYPE());
            dto.put("DATA",m.getDATA().toString());
            dto.put("WRZ",null);
            dto.put("GRZ",null);
            dto.put("UPZ",null);
            if(finalSt_rvfcch_b.size() > 0){
                List<ST_RVFCCH_BPojo> st_rvfcch_bTemp = finalSt_rvfcch_b.stream().filter(n -> n.getSTCD().trim().equals(m.getSTCD().trim())).collect(Collectors.toList());
                if (st_rvfcch_bTemp.size() > 0){
                    if(st_rvfcch_bTemp.get(0).getWRZ()!=null){                        
                       dto.put("WRZ",st_rvfcch_bTemp.get(0).getWRZ().toString());
                    }
                    if(st_rvfcch_bTemp.get(0).getGRZ()!=null){
                      dto.put("GRZ",st_rvfcch_bTemp.get(0).getGRZ().toString());
                    }
                }
            }
            if (final_WAS_Rs.size() > 0){
                String ymdhm=m.getYMDHM().replaceAll(".000000","");
                List<ST_WAS_RPojo> tempZ = final_WAS_Rs.stream().filter(n -> n.getTM().equals(ymdhm) && n.getSTCD().trim().equals(m.getSTCD().trim())).collect(Collectors.toList());
                if (tempZ.size() > 0){
                    dto.put("UPZ",tempZ.get(0).getUPZ());
                }
            }
            maps.add(dto);
        });
        return maps;
    }


    public List<WJ_MODELSINGRESULTHLParam> WJ_MODELSINGRESULTHL(List<String> SOLUTIONID,List<String> STCD,String DATA_TYPE){
        List<WJ_MODELSINGRESULTHLParam> wjModelsingresultParamList = new ArrayList<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<BDMS_PREDICTPojo> ListBDMS = bdmsPredictData.selectList("", "", "", SOLUTIONID, "", DATA_TYPE, null, null, "",null);
        if(null != STCD && STCD.size() > 0){
            ListBDMS = ListBDMS.stream().filter(m -> STCD.contains(m.getSTCD().trim())).collect(Collectors.toList());
        }
        // List<ST_STBPRP_BDto> ListBasic = stbprpBData.selectListBandStcd("").stream().filter(m -> "1".equals(m.getTYPE().trim())).collect(Collectors.toList());
        List<swzzmodeserver.workserver.pojo.swzzrtsq.ST_STBPRP_BPojo> ListBasic = rStbprp_BData.selectList(null,null);
        List<ES_JISUANZHANPojo> List_G = esJisuanzhanData.selectList("", Collections.singletonList(DATA_TYPE), "",null, null);

        List<BDMS_PREDICTPojo> finalListBDMS = ListBDMS;
        List_G.forEach(n->{
            List<BDMS_PREDICTPojo> ListBDMSTemp = finalListBDMS.stream().filter(s -> s.getSTCD().trim().equals(n.getSTCD().trim())).collect(Collectors.toList());
            List<swzzmodeserver.workserver.pojo.swzzrtsq.ST_STBPRP_BPojo> List_BT = ListBasic.stream().filter(s -> s.getSTCD().trim().equals(n.getSTCD().trim())).collect(Collectors.toList());

            if(ListBDMSTemp.size()>0){
                String[][] tideHArr = new String[ListBDMSTemp.size()][];
                for(int i = 0;i < ListBDMSTemp.size();i++){
                    String[] item = {ListBDMSTemp.get(i).getYMDHM(), String.valueOf(ListBDMSTemp.get(i).getDATA())};
                    tideHArr[i] = item;
                }
                List<Map<String,Object>> gdcList=ComputeHL.computeHL(tideHArr);

                if(List_BT.size() > 0){
                    WJ_MODELSINGRESULTHLParam dto=new WJ_MODELSINGRESULTHLParam();
                    dto.setSTCD(List_BT.get(0).getSTCD());
                    dto.setSTNM(List_BT.get(0).getSTNM());
                    dto.setSOLUTIONID(ListBDMSTemp.get(0).getPLAN_N());
                    dto.setMapList(gdcList);
                    wjModelsingresultParamList.add(dto);
                }
            }
        });
        return  wjModelsingresultParamList;
    }
}
