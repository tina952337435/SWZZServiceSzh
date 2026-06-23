package swzzmodeserver.workserver.service.swzzwater;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swzzmodeserver.workserver.data.swzzflood.Flood_ST_STBPRP_BData;
import swzzmodeserver.workserver.data.swzzflood.HY_DP_CData;
import swzzmodeserver.workserver.data.swzzflood.RTSQData;
import swzzmodeserver.workserver.data.swzzflood.ST_PPTN_RData;
import swzzmodeserver.workserver.pojo.swzzflood.Flood_ST_STBPRP_BDto;
import swzzmodeserver.workserver.pojo.swzzflood.HY_DP_CPojo;
import swzzmodeserver.workserver.pojo.swzzflood.ST_PPTN_RPojo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ST_PPTN_RServiceImpl implements ST_PPTN_RService {
    @Autowired
    private RTSQData rtsqData;
    @Autowired
    private Flood_ST_STBPRP_BData floodStStbprpBData;
    @Autowired
    private HY_DP_CData hyDpCData;
    @Autowired
    private ST_PPTN_RData pptnRData;

    @Override
    public List<Map<String, Object>> WATER_ST_PPTN_RCUSTOMLIST(List<String> stcdList, String stime, String etime, String dayHour) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dfH = new SimpleDateFormat("yyyy-MM-dd HH:00:00");
        List<Flood_ST_STBPRP_BDto> tempST_STBPRP_B = floodStStbprpBData.getBASE_BTZ(stcdList)
                .stream().filter(m -> "2".equals(m.getTYPE())).collect(Collectors.toList());
        List<String> tempList = tempST_STBPRP_B.stream().map(Flood_ST_STBPRP_BDto::getZSTCD).collect(Collectors.toList());
        List<String> tempListSTCD = tempST_STBPRP_B.stream().map(Flood_ST_STBPRP_BDto::getSTCD).collect(Collectors.toList());
        List<Map<String,Object>> mapList = new ArrayList<>();
        if (stcdList.size() > 0){
            Map<String,Object> map = new HashMap<>();
            map.put("TM","");
            for (String stcd : stcdList){
                List<Flood_ST_STBPRP_BDto> tempstbrpb = tempST_STBPRP_B.stream().filter(m -> m.getSTCD().equals(stcd)).collect(Collectors.toList());
                if (tempstbrpb.size() > 0){
                    map.put("YQ" + tempstbrpb.get(0).getSTCD(),tempstbrpb.get(0).getSTNM());
                }
            }
            map.put("FLAG","0");
            mapList.add(map);
        }
        int count = 0;
        Date stm = null,etm = null;
        long timeSpan = 0;
        try {
            stm = dateFormat.parse(stime);
            etm = dateFormat.parse(etime);
            timeSpan = dateFormat.parse(etime).getTime() - dateFormat.parse(stime).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if ("DAY".equals(dayHour)){
            count = (int)timeSpan / (24 * 60 * 60 * 1000);
            List<HY_DP_CPojo> listPPTN = hyDpCData.selectList(stime, etime, stcdList);
            List<Map<String, Object>> st_pptn_r = new ArrayList<>();
            if (etm.getYear() + 1900 > 2020){
                String stimes = "2021-01-01 00:00:00";
                if (stm.getTime() + 1900 > 2020) {
                    stimes = stime;
                }
                st_pptn_r = rtsqData.getSCJYDAY(stimes, etime, tempList);
            }
            if (count == 0){
                Map<String,Object> map = new HashMap<>();
                map.put("TM",df.format(stm));
                for (String stcd : stcdList){
                    List<HY_DP_CPojo> listPPTNTemp = listPPTN.stream().filter(m -> m.getSTCD().equals(stcd)).collect(Collectors.toList());
                    double drp = 0;
                    if (listPPTNTemp.size() > 0){
                        drp = listPPTNTemp.stream().mapToDouble(HY_DP_CPojo::getP).sum();
                    }else {
                        List<Flood_ST_STBPRP_BDto> tempST_STBPRP_BTemp = tempST_STBPRP_B.stream().filter(m -> m.getSTCD().equals(stcd)).collect(Collectors.toList());
                        if (tempST_STBPRP_BTemp.size() > 0){
                            List<Map<String, Object>> tempWas_r = st_pptn_r.stream().filter(m -> {
                                if (m.containsKey("STCD") && m.get("STCD") != null) {
                                    return m.get("STCD").toString().equals(tempST_STBPRP_BTemp.get(0).getZSTCD());
                                }
                                return false;
                            }).collect(Collectors.toList());
                            drp = tempWas_r.stream().mapToDouble(m->{
                                if (m.containsKey("DRP") && m.get("DRP") != null){
                                    return Double.parseDouble(m.get("DRP").toString());
                                }
                                return 0;
                            }).sum();
                        }
                    }
                    map.put("YQ"+stcd,String.format("%.1f",drp));
                }
                map.put("FLAG","1");
                mapList.add(map);
            }else {
                count+=1;
                for (int i=0;i<count;i++){
                    String time1 = df.format(new Date(stm.getTime() + (i * 24 * 60 * 60 * 1000)));
                    String p1 = df.format(new Date(stm.getTime())) + " 08:00:00";
                    String p2 = df.format(new Date(stm.getTime() + ((i + 1) * 24 * 60 * 60 * 1000))) + " 08:00:00";
                    Map<String,Object> map = new HashMap<>();
                    map.put("TM",time1);
                    for (String stcd : stcdList){
                        Date finalStm = stm;
                        List<HY_DP_CPojo> listPPTNTemp = listPPTN.stream().filter(m -> {
                            try {
                                return m.getSTCD().equals(stcd)
                                        && df.format(dateFormat.parse(m.getDT())).equals(df.format(new Date(finalStm.getTime())));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            return false;
                        }).collect(Collectors.toList());
                        double drp = 0;
                        if (listPPTNTemp.size() > 0){
                            drp = listPPTNTemp.stream().mapToDouble(HY_DP_CPojo::getP).sum();
                        }else {
                            List<Flood_ST_STBPRP_BDto> tempST_STBPRP_BTemp = tempST_STBPRP_B.stream().filter(m -> m.getSTCD().equals(stcd)).collect(Collectors.toList());
                            if (tempST_STBPRP_BTemp.size() > 0){
                                List<Map<String, Object>> tempWas_r = st_pptn_r.stream().filter(m -> {
                                    if (m.containsKey("STCD") && m.get("STCD") != null && m.containsKey("TM") && m.get("TM") != null) {
                                        return m.get("STCD").toString().equals(tempST_STBPRP_BTemp.get(0).getZSTCD())
                                                && m.get("TM").toString().compareTo(p1) >= 0
                                                && m.get("TM").toString().compareTo(p2) < 0;
                                    }
                                    return false;
                                }).collect(Collectors.toList());
                                drp = tempWas_r.stream().mapToDouble(m->{
                                    if (m.containsKey("DRP") && m.get("DRP") != null){
                                        return Double.parseDouble(m.get("DRP").toString());
                                    }
                                    return 0;
                                }).sum();
                            }
                        }
                        map.put("YQ"+stcd,drp);
                    }
                    map.put("FLAG","1");
                    mapList.add(map);
                }
            }
        }else if ("HOUR".equals(dayHour)){
            List<ST_PPTN_RPojo> listPPTN = new ArrayList<>();
            List<Map<String,Object>> st_pptn_r = new ArrayList<>();
            if (stm.getYear() + 1900 <= 2020){
                String etimes = etime;
                if (etm.getYear() + 1900 > 2020) {
                    etimes = "2020-12-31 23:59:59";
                }
                listPPTN = pptnRData.selectList(stime, etimes, tempListSTCD).stream()
                        .filter(m -> m.getDRP() != null && m.getDRP() > 0).collect(Collectors.toList());
            }
            if (etm.getYear() + 1900 > 2020){
                String stimes = "2021-01-01 00:00:00";
                if (stm.getYear() + 1900 > 2020) {
                    stimes = stime;
                }
                st_pptn_r = rtsqData.getSCJY(stimes, etime, tempList);
            }
            count = (int)timeSpan / (60 * 60 * 1000);
            if (count == 0){
                Map<String,Object> map = new HashMap<>();
                map.put("TM",df.format(stm));
                for (String stcd : stcdList){
                    List<Flood_ST_STBPRP_BDto> tempST_STBPRP_BTemp = tempST_STBPRP_B.stream().filter(m -> m.getSTCD().equals(stcd)).collect(Collectors.toList());
                    if (tempST_STBPRP_BTemp.size() > 0){
                        List<Map<String, Object>> tempWas_r = st_pptn_r.stream().filter(m -> {
                            if (m.containsKey("STCD") && m.get("STCD") != null) {
                                return m.get("STCD").toString().equals(tempST_STBPRP_BTemp.get(0).getSTCD());
                            }
                            return false;
                        }).collect(Collectors.toList());
                        List<ST_PPTN_RPojo> listPPTNTemp = listPPTN.stream().filter(m -> m.getSTCD().equals(tempST_STBPRP_BTemp.get(0).getSTCD())).collect(Collectors.toList());
                        double drp1 = tempWas_r.stream().mapToDouble(m->{
                            if (m.containsKey("DRP") && m.get("DRP") != null){
                                return Double.parseDouble(m.get("DRP").toString());
                            }
                            return 0;
                        }).sum();
                        double drp2 = listPPTNTemp.stream().mapToDouble(ST_PPTN_RPojo::getDRP).sum();
                        map.put("YQ"+stcd, drp1 + drp2);
                    }
                }
                map.put("FLAG","1");
                mapList.add(map);
            }else {
                //count+=1;
                for (int i=0;i<count;i++){
                    String time1 = dfH.format(new Date(stm.getTime() + (i * 60 * 60 * 1000)));
                    String p1 = dfH.format(new Date(stm.getTime() + (i * 60 * 60 * 1000)));
                    String p2 = dfH.format(new Date(stm.getTime() + ((i + 1) * 60 * 60 * 1000)));
                    Map<String,Object> map = new HashMap<>();
                    map.put("TM",p2);
                    for (String stcd : stcdList){
                        List<Flood_ST_STBPRP_BDto> tempST_STBPRP_BTemp = tempST_STBPRP_B.stream().filter(m -> {
                            return m.getSTCD().equals(stcd);
                        }).collect(Collectors.toList());
                        if (tempST_STBPRP_BTemp.size() > 0){
                            List<Map<String, Object>> tempWas_r = st_pptn_r.stream().filter(m -> {
                                if (m.containsKey("STCD") && m.get("STCD") != null && m.containsKey("TM") && m.get("TM") != null) {
                                    return m.get("STCD").toString().equals(tempST_STBPRP_BTemp.get(0).getZSTCD())
                                            && m.get("TM").toString().compareTo(p1) > 0
                                            && m.get("TM").toString().compareTo(p2) <= 0;
                                }
                                return false;
                            }).collect(Collectors.toList());
                            List<ST_PPTN_RPojo> listPPTNTemp = listPPTN.stream().filter(m -> {
                                return m.getSTCD().equals(tempST_STBPRP_BTemp.get(0).getSTCD())
                                        && m.getTM().compareTo(p1) > 0
                                        && m.getTM().compareTo(p2) <= 0;
                            }).collect(Collectors.toList());
                            double drp1 = tempWas_r.stream().mapToDouble(m->{
                                if (m.containsKey("DRP") && m.get("DRP") != null){
                                    return Double.parseDouble(m.get("DRP").toString());
                                }
                                return 0;
                            }).sum();
                            double drp2 = listPPTNTemp.stream().mapToDouble(ST_PPTN_RPojo::getDRP).sum();
                            map.put("YQ"+stcd,drp1 + drp2);
                        }
                    }
                    map.put("FLAG","1");
                    mapList.add(map);
                }
            }
        }
        return mapList;
    }
}
