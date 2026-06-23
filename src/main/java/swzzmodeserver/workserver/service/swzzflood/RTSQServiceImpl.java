package swzzmodeserver.workserver.service.swzzflood;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swzzmodeserver.workserver.data.swzzflood.Flood_ST_STBPRP_BData;
import swzzmodeserver.workserver.data.swzzflood.RTSQData;
import swzzmodeserver.workserver.data.swzzflood.ST_PPTN_RData;
import swzzmodeserver.workserver.data.swzzmode.ST_RVFCCH_BData;
import swzzmodeserver.workserver.data.swzzmode.ST_STBPRP_BData;
import swzzmodeserver.workserver.data.swzzrtsq.GetWaterViewNewData;
import swzzmodeserver.workserver.data.swzzwater.ST_GATE_RData;
import swzzmodeserver.workserver.pojo.swzzflood.ST_PPTN_RPojo;
import swzzmodeserver.workserver.pojo.swzzflood.ST_WAS_RPojo;
import swzzmodeserver.workserver.pojo.swzzmode.ST_RVFCCH_BPojo;
import swzzmodeserver.workserver.pojo.swzzmode.ST_STBPRP_BDto;
import swzzmodeserver.workserver.pojo.swzzrtsq.GetWaterViewNewPojo;
import swzzmodeserver.workserver.pojo.swzzwater.ST_GATE_RPojo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RTSQServiceImpl implements RTSQService {
    @Autowired
    private ST_STBPRP_BData stbprpBData;
    @Autowired
    private ST_RVFCCH_BData rvfcchBData;
    @Autowired
    private RTSQData rtsqData;
    @Autowired
    private ST_PPTN_RData pptnRData;
    @Autowired
    private Flood_ST_STBPRP_BData floodStStbprpBData;

    @Autowired
    private GetWaterViewNewData getWaterViewNewData;

    @Autowired
    private ST_GATE_RData gateRData;
    @Override
    public List<Map<String, Object>> WATER_ST_WAS_RNEW(List<String> stcdList,String stime,String etime) {
        List<Map<String,Object>> userList = new ArrayList<>();
        List<ST_STBPRP_BDto> stbprplist = stbprpBData.selectListBandStcdByStcdList(stcdList);
        stbprplist = stbprplist.stream().filter(m-> "1".equals(m.getTYPE())).collect(Collectors.toList());
        List<ST_RVFCCH_BPojo> stRvfcchBList = rvfcchBData.selectList(null, null, null, null, null);

        List<String> stcds = stbprplist.stream().map(ST_STBPRP_BDto::getZSTCD).collect(Collectors.toList());
        List<GetWaterViewNewPojo> st_was_r = new ArrayList<>();
        // st_was_r = rtsqData.getSCSW(stime,etime,stcds,null);

        List<GetWaterViewNewPojo> waterViewList = new ArrayList<>();
        st_was_r = getWaterViewNewData.selectListWaterAll(stcdList, stime, etime, null, null, null);
        

        for (ST_STBPRP_BDto obj : stbprplist){
            List<Object> stcd = userList.stream().map(m -> {
                if (m.containsKey("STCD")){
                    return m.get("STCD");
                }
                return null;
            }).collect(Collectors.toList());
            if (!stcd.contains(obj.getSTCD())) {
                String stnm = "";
                Double lgtd = null, lttd = null;
                Double wrz = null, grz = null;
                List<ST_RVFCCH_BPojo> tempST_rvfcch_b = stRvfcchBList.stream().filter(m -> m.getSTCD().equals(obj.getSTCD())).collect(Collectors.toList());
                if (tempST_rvfcch_b.size() > 0){
                    wrz = tempST_rvfcch_b.get(0).getWRZ();
                    grz = tempST_rvfcch_b.get(0).getGRZ();
                }
                Map<String,Object> map = new HashMap<>();
                List<GetWaterViewNewPojo> tempst_was_r = st_was_r.stream().filter(m -> m.getSTCD().equals(obj.getZSTCD())).sorted((a, b) -> {
                    return b.getTM().compareTo(a.getTM());
                }).collect(Collectors.toList());
                if (tempst_was_r.size() > 0){
                    map.put("UPZ",tempst_was_r.get(0).getUPZ());
                    map.put("TM",tempst_was_r.get(0).getTM());
                }
                map.put("STCD",obj.getSTCD());
                map.put("STNM",obj.getSTNM());
                map.put("LGTD",obj.getLGTD());
                map.put("LTTD",obj.getLTTD());
                map.put("LGTD84",obj.getLGTD84());
                map.put("LTTD84",obj.getLTTD84());
                map.put("WRZ",wrz);
                map.put("GRZ",grz);
                map.put("ZSTCD",obj.getZSTCD());
                userList.add(map);
            }
        }
        return userList;
    }

    @Override
    public List<Map<String, Object>> WATER_ST_WAS_RDZNEW(List<String> stcdList,String stime,String etime,String hour) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        List<ST_STBPRP_BDto> stbprplist = stbprpBData.selectListBandStcdByStcdList(stcdList);
        stbprplist = stbprplist.stream().filter(m-> "1".equals(m.getTYPE())).collect(Collectors.toList());
        List<String> tempList = stbprplist.stream().map(ST_STBPRP_BDto::getZSTCD).collect(Collectors.toList());
        // List<ST_WAS_RPojo> st_was_r = rtsqData.getSCSW(stime, etime, tempList, hour);
        List<GetWaterViewNewPojo> st_was_r = getWaterViewNewData.selectListWaterAll(stcdList, stime, etime, null, null, null);
        List<String> stcds = stbprplist.stream().map(ST_STBPRP_BDto::getSTCD).collect(Collectors.toList());
        List<ST_RVFCCH_BPojo> listST_RVFCCH_B = rvfcchBData.selectList(null, null, null, null, null)
                .stream().filter(m -> stcds.contains(m.getSTCD())).collect(Collectors.toList());
        List<ST_STBPRP_BDto> finalStbprplist = stbprplist;
        st_was_r.forEach(m->{
            Map<String,Object> map = new HashMap<>();
            List<ST_STBPRP_BDto> stbprplistTemp = finalStbprplist.stream().filter(n -> n.getZSTCD().equals(m.getSTCD())).collect(Collectors.toList());
            if (stbprplistTemp.size() > 0){
                List<ST_RVFCCH_BPojo> temp = listST_RVFCCH_B.stream().filter(n -> n.getSTCD().equals(stbprplistTemp.get(0).getSTCD())).collect(Collectors.toList());
                Double wrz = null,grz = null;
                if (temp.size() > 0){
                    wrz = temp.get(0).getWRZ();
                    grz = temp.get(0).getGRZ();
                }
                map.put("WRZ",wrz);
                map.put("GRZ",grz);
            }
            map.put("STCD",m.getSTCD());
            map.put("TM",m.getTM());
            map.put("UPZ",m.getUPZ());
            mapList.add(map);
        });
        return mapList;
    }

    @Override
    public List<Map<String, Object>> WATER_ST_PPTN_RNEW(List<String> stcdList,String stime,String etime) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        List<ST_STBPRP_BDto> stbprplist = stbprpBData.selectListBandStcdByStcdList(stcdList);
        stbprplist = stbprplist.stream().filter(m-> "2".equals(m.getTYPE())).collect(Collectors.toList());
        List<String> tempList = stbprplist.stream().map(ST_STBPRP_BDto::getZSTCD).collect(Collectors.toList());
        List<Map<String, Object>> st_pptn_r = rtsqData.getSCJY(stime, etime, tempList);
        List<Map<String, Object>> st_pptn_rTemp = st_pptn_r.stream().filter(m -> {
            if (null != m.get("TM")) {
                return m.get("TM").toString().compareTo(stime) >= 0;
            }
            return false;
        }).collect(Collectors.toList());
        for (ST_STBPRP_BDto dto : stbprplist){
            Map<String,Object> map = new HashMap<>();
            map.put("STCD",dto.getSTCD());
            map.put("STNM",dto.getSTNM());
            map.put("LGTD",dto.getLGTD());
            map.put("LTTD",dto.getLTTD());
            map.put("LGTD84",dto.getLGTD84());
            map.put("LTTD84",dto.getLTTD84());
            List<Map<String, Object>> st_pptn_rs = st_pptn_rTemp.stream().filter(n -> {
                if (null != n.get("STCD")) {
                    return n.get("STCD").toString().equals(dto.getZSTCD());
                }
                return false;
            }).sorted((a, b) -> {
                if (null != a.get("TM") && null != b.get("TM")) {
                    return b.get("TM").toString().compareTo(a.get("TM").toString());
                }
                return 0;
            }).collect(Collectors.toList());
            double sum = st_pptn_rs.stream().mapToDouble(m -> m.get("DRP") != null ? Double.parseDouble(m.get("DRP").toString()) : 0.0)
                    .sum();
            map.put("DRP",String.format("%.1f",sum));
            mapList.add(map);
        }
        return mapList;
    }

    @Override
    public List<Map<String, Object>> WATER_ST_PPTN_RDZNEW(List<String> stcdList,String stime,String etime,String dayHour) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date stm = null;
        Date etm = null;
        try {
            stm = dateFormat.parse(stime);
            etm = dateFormat.parse(etime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        List<Map<String, Object>> mapList = new ArrayList<>();
        List<ST_STBPRP_BDto> stbprplist = stbprpBData.selectListBandStcdByStcdList(stcdList);
        stbprplist = stbprplist.stream().filter(m-> "2".equals(m.getTYPE())).collect(Collectors.toList());
        if (!(stbprplist.size() > 0)){
            stbprplist = floodStStbprpBData.getBASE_BTZ(stcdList).stream().filter(m-> "2".equals(m.getTYPE())).map(m->{
                ST_STBPRP_BDto dto = new ST_STBPRP_BDto();
                BeanUtils.copyProperties(m,dto);
                return dto;
            }).collect(Collectors.toList());
        }
        if (stbprplist.size() > 0){
            List<String> tempList = stbprplist.stream().map(ST_STBPRP_BDto::getZSTCD).collect(Collectors.toList());
            List<String> tempListSTCD = stbprplist.stream().map(ST_STBPRP_BDto::getSTCD).collect(Collectors.toList());

            List<ST_PPTN_RPojo> listPPTN = new ArrayList<>();
            if (null != stm && stm.getYear() + 1900 <= 2020){
                Date etimes = etm;
                if (etm.getYear() + 1900 > 2020){
                    try {
                        etimes = dateFormat.parse("2020-12-31 23:59:59");
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                listPPTN = pptnRData.selectList(dateFormat.format(stm), dateFormat.format(etimes), tempListSTCD)
                        .stream().filter(m -> {
                            if (m.getDRP() != null) {
                                return m.getDRP() > 0;
                            }
                            return false;
                        }).collect(Collectors.toList());
            }
            List<Map<String, Object>> st_pptn_r = new ArrayList<>();
            if (null != etm && etm.getYear() + 1900 > 2020){
                Date stimes = null;
                try {
                    stimes = dateFormat.parse("2021-01-01 00:00:00");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (null != stm && stm.getYear() + 1900 > 2020){
                    stimes = stm;
                }
                if ("DAY".equals(dayHour)){
                    st_pptn_r = rtsqData.getSCJYDAY(dateFormat.format(stimes), dateFormat.format(etm), tempList);
                }else if ("HOUR".equals(dayHour)){
                    st_pptn_r = rtsqData.getSCJY(dateFormat.format(stm), dateFormat.format(etm), tempList);
                }else {
                    st_pptn_r = rtsqData.getSCJY_5MIN(dateFormat.format(stm), dateFormat.format(etm), tempList);
                }
            }
            int dataCount = 0;
            long timeSpan = etm != null && stm != null ? etm.getTime() - stm.getTime() : 0;
            if ("DAY".equals(dayHour)){
                dataCount = (int)(timeSpan / (24 * 60 * 60 * 1000));
                if (dataCount == 0){
                    List<ST_STBPRP_BDto> finalStbprplist = stbprplist;
                    List<Map<String, Object>> tempst_pptn_r = st_pptn_r.stream().filter(m -> {
                        if (m.containsKey("STCD") && m.get("STCD") != null) {
                            return m.get("STCD").toString().equals(finalStbprplist.get(0).getZSTCD());
                        }
                        return false;
                    }).collect(Collectors.toList());
                    List<ST_PPTN_RPojo> listPPTNTemp = listPPTN.stream().filter(m -> {
                        if (m.getSTCD() != null) {
                            return m.getSTCD().equals(finalStbprplist.get(0).getSTCD());
                        }
                        return false;
                    }).collect(Collectors.toList());
                    Map<String,Object> map = new HashMap<>();
                    map.put("STCD",stcdList);
                    map.put("TM",dateFormat.format(stm));
                    double drpSum1 = tempst_pptn_r.stream().mapToDouble(m->{
                        if (m.containsKey("DRP") && m.get("DRP") != null){
                            return Double.parseDouble(m.get("DRP").toString());
                        }
                        return 0;
                    }).sum();
                    double drpSum2 = listPPTNTemp.stream().mapToDouble(m->{
                        if (m.getDRP() != null){
                            return m.getDRP();
                        }
                        return 0;
                    }).sum();
                    map.put("DRP",drpSum1 + drpSum2);
                    mapList.add(map);
                }else {
                    dataCount++;
                    for (int i=0;i<dataCount;i++){
                        String time1 = df.format(new Date(stm.getTime() + (i * 24 * 60 * 60 * 1000)));
                        String p1 = df.format(new Date(stm.getTime() + (i * 24 * 60 * 60 * 1000))) + " 08:00:00";
                        String p2 = df.format(new Date(stm.getTime() + ((i + 1) * 24 * 60 * 60 * 1000))) + " 08:00:00";
                        List<ST_STBPRP_BDto> finalStbprplist1 = stbprplist;
                        List<Map<String, Object>> tempst_pptn_r = st_pptn_r.stream().filter(m -> {
                            if (m.containsKey("STCD") && m.get("STCD") != null && m.containsKey("TM") && m.get("TM") != null) {
                                return m.get("STCD").toString().equals(finalStbprplist1.get(0).getZSTCD())
                                        && m.get("TM").toString().compareTo(p1) > 0
                                        && m.get("TM").toString().compareTo(p2) <= 0;
                            }
                            return false;
                        }).collect(Collectors.toList());
                        List<ST_PPTN_RPojo> listPPTNTemp = listPPTN.stream().filter(m -> {
                            if (null != m.getSTCD() && null != m.getTM()) {
                                return m.getSTCD().equals(finalStbprplist1.get(0).getSTCD())
                                        && m.getTM().compareTo(p1) > 0
                                        && m.getTM().compareTo(p2) <= 0;
                            }
                            return false;
                        }).collect(Collectors.toList());
                        Map<String,Object> map = new HashMap<>();
                        map.put("STCD",stcdList);
                        map.put("TM",time1);
                        double drpSum1 = tempst_pptn_r.stream().mapToDouble(m->{
                            if (m.containsKey("DRP") && m.get("DRP") != null){
                                return Double.parseDouble(m.get("DRP").toString());
                            }
                            return 0;
                        }).sum();
                        double drpSum2 = listPPTNTemp.stream().mapToDouble(m->{
                            if (m.getDRP() != null){
                                return m.getDRP();
                            }
                            return 0;
                        }).sum();
                        map.put("DRP",drpSum1 + drpSum2);
                        mapList.add(map);
                    }
                }
            }else if ("MINUTE".equals(dayHour)){
                List<ST_STBPRP_BDto> finalStbprplist2 = stbprplist;
                List<Map<String, Object>> tempst_pptn_r = st_pptn_r.stream().filter(m -> {
                    if (m.containsKey("STCD") && m.get("STCD") != null) {
                        return m.get("STCD").toString().equals(finalStbprplist2.get(0).getZSTCD());
                    }
                    return false;
                }).collect(Collectors.toList());
                mapList.addAll(tempst_pptn_r);
            }else {
                dataCount = (int)(timeSpan / (60 * 60 * 1000)) + 1;
                for (long i=0;i<dataCount;i++){
                    String time1 = dateFormat.format(new Date(stm.getTime() + (i * 60 * 60 * 1000)));
                    String p1 = dateFormat.format(new Date(stm.getTime() + (i * 60 * 60 * 1000)));
                    String p2 = dateFormat.format(new Date(stm.getTime() + ((i + 1) * 60 * 60 * 1000)));
                    List<ST_STBPRP_BDto> finalStbprplist1 = stbprplist;
                    List<Map<String, Object>> tempst_pptn_r = st_pptn_r.stream().filter(m -> {
                        if (m.containsKey("STCD") && m.get("STCD") != null && m.containsKey("TM") && m.get("TM") != null) {
                            return m.get("STCD").toString().equals(finalStbprplist1.get(0).getZSTCD())
                                    && m.get("TM").toString().compareTo(p1) >= 0
                                    && m.get("TM").toString().compareTo(p2) < 0;
                        }
                        return false;
                    }).collect(Collectors.toList());
                    List<ST_PPTN_RPojo> listPPTNTemp = listPPTN.stream().filter(m -> {
                        if (null != m.getSTCD() && null != m.getTM()) {
                            return m.getSTCD().equals(finalStbprplist1.get(0).getSTCD())
                                    && m.getTM().compareTo(p1) > 0
                                    && m.getTM().compareTo(p2) <= 0;
                        }
                        return false;
                    }).collect(Collectors.toList());
                    Map<String,Object> map = new HashMap<>();
                    map.put("STCD",stcdList);
                    map.put("TM",time1);
                    double drpSum1 = tempst_pptn_r.stream().mapToDouble(m->{
                        if (m.containsKey("DRP") && m.get("DRP") != null){
                            return Double.parseDouble(m.get("DRP").toString());
                        }
                        return 0;
                    }).sum();
                    double drpSum2 = listPPTNTemp.stream().mapToDouble(m->{
                        if (m.getDRP() != null){
                            return m.getDRP();
                        }
                        return 0;
                    }).sum();
                    map.put("DRP",drpSum1 + drpSum2);
                    mapList.add(map);
                }
            }
        }
        return mapList;
    }

    @Override
    public List<Map<String, Object>> WATER_ST_PPTNWAS_RDZNEW(List<String> stcdList,String stime,String etime,String dayHour){
        List<Map<String, Object>> mapList = new ArrayList<>();
        List<ST_STBPRP_BDto> stbprplist = stbprpBData.selectListBandStcdByStcdList(stcdList);
        List<String> stcds = stbprplist.stream().map(ST_STBPRP_BDto::getZSTCD).collect(Collectors.toList());
        List<ST_RVFCCH_BPojo> listST_RVFCCH_B = rvfcchBData.selectList(null, null, null, null, null)
                .stream().filter(m -> stcds.contains(m.getSTCD())).collect(Collectors.toList());
        List<ST_STBPRP_BDto> stbprplistSW = stbprplist.stream().filter(m-> "1".equals(m.getTYPE())).collect(Collectors.toList());
        List<ST_STBPRP_BDto> stbprplistJY = stbprplist.stream().filter(m-> "2".equals(m.getTYPE())).collect(Collectors.toList());
        List<ST_STBPRP_BDto> stbprplistGQ = stbprplist.stream().filter(m-> "3".equals(m.getTYPE())).collect(Collectors.toList());
        List<Map<String,Object>> st_was_r = new ArrayList<>();

        if(stbprplistSW.size()>0){
            List<String> stcdSW=new ArrayList<>();
            stcdSW.add(stbprplistSW.get(0).getZSTCD());
            if(dayHour.equals("hour")){//小时
                st_was_r=rtsqData.getSCHOUR(stime,etime,stcdSW);
            }
            else if(dayHour.equals("day")){//日
                st_was_r=rtsqData.getSCJYDAY(stime,etime,stcdSW);
            }
            List<Map<String, Object>> st_was_rTemp=st_was_r;
            List<Map<String, Object>> st_pptn_rTemp=new ArrayList<>();
            List<ST_GATE_RPojo> tempST_GATE_R = new ArrayList<>();
            if(st_was_rTemp.size()>0){
                if(stbprplistJY.size()>0){
                    List<String> stcdJY=new ArrayList<>();
                    stcdJY.add(stbprplistJY.get(0).getZSTCD());
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date stm=null,etm=null;
                    try {
                        stm = dateFormat.parse(st_was_rTemp.get(0).get("TM").toString());
                        etm = dateFormat.parse(st_was_rTemp.get(st_was_rTemp.size()-1).get("TM").toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    String stimeJY = df.format(new Date(stm.getTime() - (24 * 60 * 60 * 1000))) + " 08:00:00";
                    String etimeJY = df.format(new Date(etm.getTime() + (24 * 60 * 60 * 1000))) + " 08:00:00";

                    if(dayHour.equals("hour")){//小时
                        st_pptn_rTemp=rtsqData.getSCJY(stimeJY,etimeJY,stcdJY);
                    }
                    else if(dayHour.equals("day")){//日
                        st_pptn_rTemp=rtsqData.getSCJYDAY(stimeJY,etimeJY,stcdJY);
                    }
                }

                //工情
                if(stbprplistGQ.size()>0){
                    List<String> stcdGQ=new ArrayList<>();
                    stcdGQ.add(stbprplistGQ.get(0).getZSTCD());
                    tempST_GATE_R = gateRData.selectList(stime, etime, stcdGQ);
                }
            }
            List<Map<String, Object>> finalSt_pptn_rTemp = st_pptn_rTemp;
            List<ST_GATE_RPojo> finalTempST_GATE_R = tempST_GATE_R;
            st_was_rTemp.forEach(item->{
                String swtm=item.get("TM").toString();
                List<Map<String, Object>> st_pptn_rTempT = finalSt_pptn_rTemp.stream().filter(m -> {
                    if (null != m.get("TM")) {
                        return m.get("TM").toString().compareTo(swtm) >= 0;
                    }
                    return false;
                }).collect(Collectors.toList());

                List<ST_GATE_RPojo> st_gate_rTempT = finalTempST_GATE_R.stream().filter(m -> {
                    System.out.println("swtm："+swtm+"；gqtm："+m.getTM().toString().replaceAll(".000000",""));
                    return m.getTM().toString().replaceAll(".000000","").equals(swtm);
                }).collect(Collectors.toList());

                Map<String,Object> map = new HashMap<>();
                map.put("STCD",stbprplistSW.get(0).getSTCD());
                map.put("STNM",stbprplistSW.get(0).getSTNM());
                map.put("ZSTCD",stbprplistSW.get(0).getZSTCD());
                map.put("UPZ",item.get("DRP"));
                map.put("TM",swtm);
                if(st_pptn_rTempT.size()>0){
                    map.put("DRP",st_pptn_rTempT.get(0).get("DRP"));
                    map.put("RSTCD",stbprplistJY.get(0).getZSTCD());
                }
                if(st_gate_rTempT.size()>0){
                    List<ST_GATE_RPojo> st_gate_rTempKai = st_gate_rTempT.stream().filter(m-> m.getGTQ()>=0.002).collect(Collectors.toList());
                    int KONGSHU=st_gate_rTempKai.size();
                    map.put("KONGSHU",KONGSHU);
                    map.put("GQSTCD",stbprplistGQ.get(0).getZSTCD());
                }
                List<ST_RVFCCH_BPojo> temp = listST_RVFCCH_B.stream().filter(n -> n.getSTCD().equals(stbprplistSW.get(0).getSTCD())).collect(Collectors.toList());
                if(temp.size()>0){
                    map.put("WRZ",temp.get(0).getWRZ());
                }
                mapList.add(map);
            });
        }
        return  mapList;
    }
}
