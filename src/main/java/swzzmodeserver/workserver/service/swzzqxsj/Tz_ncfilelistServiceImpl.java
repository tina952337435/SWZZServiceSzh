package swzzmodeserver.workserver.service.swzzqxsj;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swzzmodeserver.workserver.data.swzzqxsj.Tz_watershedData;
import swzzmodeserver.workserver.data.swzzqxsj.Tz_watersheddataData;
import swzzmodeserver.workserver.pojo.swzzqxsj.Tz_watershedPojo;
import swzzmodeserver.workserver.pojo.swzzqxsj.Tz_watersheddataPojo;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class Tz_ncfilelistServiceImpl implements Tz_ncfilelistService {
    @Autowired
    private Tz_watersheddataData tzWatersheddataData;
    @Autowired
    private Tz_watershedData tzWatershedData;

    @Override
    public List<Map<String, Object>> QXSJ_TZ_NCFILELISTSelNew(String startdate, String enddate, String pattem,
            String stcdStr) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat format = new SimpleDateFormat("MM月dd日HH时");
        List<String> stcdList = new ArrayList<>();
        if (stcdStr != null && !stcdStr.isEmpty()) {
            stcdList = Arrays.asList(stcdStr.split(","));
        }
        List<Tz_watershedPojo> listW = tzWatershedData.selectListByID(stcdList, null);
        List<Tz_watersheddataPojo> userList = tzWatersheddataData.selectListByID(stcdList, pattem, startdate, enddate,
                null);
        userList.sort(Comparator.comparing(Tz_watersheddataPojo::getFTM));
        List<Map<String, Object>> mapList = new ArrayList<>();
        if (userList.size() > 0) {
            stcdList = listW.stream().map(Tz_watershedPojo::getKEYID).collect(Collectors.toList());
            String TMtime = startdate;
            int dataCount = Integer.parseInt(pattem);
            if (Integer.parseInt(pattem) > 24) {
                dataCount = Integer.parseInt(pattem) / 24;
                try {
                    for (int num = 0; num < dataCount; num++) {
                        Date p1 = new Date(dateFormat.parse(TMtime).getTime() + num * 24 * 60 * 60 * 1000);
                        Date p2 = new Date(p1.getTime() + 24 * 60 * 60 * 1000);
                        for (String stcd : stcdList) {
                            List<Tz_watersheddataPojo> tempst_pptn_r = userList.stream().filter(m -> {
                                if (null != m.getKEYID() && null != m.getFTM()) {
                                    try {
                                        return stcd.equals(m.getKEYID()) &&
                                                dateFormat.parse(m.getFTM()).getTime() > p1.getTime() &&
                                                dateFormat.parse(m.getFTM()).getTime() <= p2.getTime();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                return false;
                            }).collect(Collectors.toList());
                            Map<String, Object> dto = new HashMap<>();
                            dto.put("KEYID", stcd);
                            dto.put("FTM", dateFormat.format(p1));
                            dto.put("RLSTM", startdate);
                            dto.put("DRP", tempst_pptn_r.stream().mapToDouble(Tz_watersheddataPojo::getDRP).sum());
                            String strTM = format.format(p1.getTime() + 60 * 60 * 1000) + "至" + format.format(p2);
                            dto.put("DATESPAN", strTM);
                            List<Tz_watershedPojo> listBTemp = listW.stream().filter(m -> stcd.equals(m.getKEYID()))
                                    .collect(Collectors.toList());
                            if (listBTemp.size() > 0) {
                                dto.put("NAME", listBTemp.get(0).getNAME());
                            }
                            mapList.add(dto);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                userList.forEach(m -> {
                    Map<String, Object> dto = new HashMap<>();
                    dto.put("KEYID", m.getKEYID());
                    dto.put("FTM", m.getFTM());
                    dto.put("RLSTM", m.getRLSTM());
                    dto.put("DRP", m.getDRP());
                    try {
                        if (m.getFTM() != null) {
                            Date ftm = dateFormat.parse(m.getFTM());
                            String strTM = ftm.getMinutes() == 0 ? format.format(ftm)
                                    : new SimpleDateFormat("MM月dd日HH时mm分").format(ftm);
                            dto.put("DATESPAN", strTM);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    List<Tz_watershedPojo> listBTemp = listW.stream().filter(n -> n.getKEYID().equals(m.getKEYID()))
                            .collect(Collectors.toList());
                    if (listBTemp.size() > 0) {
                        dto.put("NAME", listBTemp.get(0).getNAME());
                    }
                    mapList.add(dto);
                });
            }
        }
        return mapList;
    }
}
