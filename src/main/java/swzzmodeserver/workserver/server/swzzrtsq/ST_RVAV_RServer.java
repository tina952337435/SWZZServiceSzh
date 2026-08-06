package swzzmodeserver.workserver.server.swzzrtsq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swzzmodeserver.workserver.data.swzzrtsq.ST_FLOW_RData;
import swzzmodeserver.workserver.data.swzzrtsq.ST_RVAV_RData;
import swzzmodeserver.workserver.pojo.swzzrtsq.GetWaterViewNewPojo;
import swzzmodeserver.workserver.pojo.swzzrtsq.ST_FLOW_RPojo;
import swzzmodeserver.workserver.pojo.swzzrtsq.ST_RVAV_RPojo;
import swzzmodeserver.tools.DateUtil;

import java.util.*;
import java.util.stream.Collectors;

/**
 * SL323-2011 表79 河道水情多日均值表 (ST_RVAV_R) 业务服务层
 */
@Service
public class ST_RVAV_RServer {

    @Autowired
    private ST_RVAV_RData data;

    @Autowired
    private GetWaterViewNewServer getWaterViewNewServer;

    @Autowired
    private ST_FLOW_RData stFlowRData;

    // ======================== 均值整理方法 ========================

    /**
     * 从原始数据直接计算时段均值（日/三日/一侯/一旬）
     */
    public Map<String, Object> syncDailyAverage(String stime, String etime, String idtm, String sttdrcd, String mtype,
                                                 List<String> stcdList) {
        Map<String, Object> result = new HashMap<>();
        List<String> errors = new ArrayList<>();

        List<GetWaterViewNewPojo> waterList = getWaterViewNewServer.selectListByHisIsTime(stcdList, stime, etime, mtype);
        List<ST_FLOW_RPojo> flowList = stFlowRData.selectHis(stcdList, stime, etime);

        Map<String, Double> waterAvgMap = computeWaterAvg(waterList);
        Map<String, Double> flowAvgMap = computeFlowAvg(flowList);

        Set<String> allStcds = new LinkedHashSet<>();
        allStcds.addAll(waterAvgMap.keySet());
        allStcds.addAll(flowAvgMap.keySet());

        List<ST_RVAV_RPojo> insertList = new ArrayList<>();
        for (String stcd : allStcds) {
            ST_RVAV_RPojo pojo = new ST_RVAV_RPojo();
            pojo.setSTCD(stcd);
            pojo.setIDTM(idtm);
            pojo.setSTTDRCD(sttdrcd);
            pojo.setAVZ(waterAvgMap.get(stcd));
            pojo.setAVQ(flowAvgMap.get(stcd));
            insertList.add(pojo);
        }

        int upsertCount = 0;
        if (!insertList.isEmpty()) {
            try {
                upsertCount = data.upsertAll(insertList);
            } catch (Exception e) {
                errors.add("批量 MERGE 失败: " + e.getMessage());
            }
        }

        result.put("stationCount", allStcds.size());
        result.put("waterStationCount", waterAvgMap.size());
        result.put("flowStationCount", flowAvgMap.size());
        result.put("insertedCount", upsertCount);
        result.put("errors", errors);
        return result;
    }

    /**
     * 批量整理日均值：一次查询，按天分组，产出范围内每一天的日均值
     */
    public int syncDailyBatchAvg(String stime, String etime, String mtype, List<String> stcdList) {
        List<GetWaterViewNewPojo> waterList = getWaterViewNewServer.selectListByHisIsTime(stcdList, stime, etime, mtype);
        List<ST_FLOW_RPojo> flowList = stFlowRData.selectHis(stcdList, stime, etime);

        // 水位按 (STCD, 日期) 分组
        Map<String, Map<String, List<GetWaterViewNewPojo>>> waterByDate = waterList.stream()
                .filter(w -> w.getSTCD() != null && w.getTM() != null && w.getUPZ() != null)
                .filter(w -> {
                    try {
                        double v = Double.parseDouble(w.getUPZ());
                        return v > -15 && v < 15;
                    } catch (NumberFormatException e) { return false; }
                })
                .collect(Collectors.groupingBy(
                        w -> w.getTM().substring(0, 10),
                        Collectors.groupingBy(GetWaterViewNewPojo::getSTCD)));

        // 流量按 (STCD, 日期) 分组
        Map<String, Map<String, List<ST_FLOW_RPojo>>> flowByDate = flowList.stream()
                .filter(f -> f.getSTCD() != null && f.getTM() != null && f.getQ() != null)
                .collect(Collectors.groupingBy(
                        f -> f.getTM().substring(0, 10),
                        Collectors.groupingBy(ST_FLOW_RPojo::getSTCD)));

        Set<String> allDates = new LinkedHashSet<>();
        allDates.addAll(waterByDate.keySet());
        allDates.addAll(flowByDate.keySet());
        Set<String> allStcds = new LinkedHashSet<>();
        waterByDate.values().forEach(m -> allStcds.addAll(m.keySet()));
        flowByDate.values().forEach(m -> allStcds.addAll(m.keySet()));

        List<ST_RVAV_RPojo> insertList = new ArrayList<>();
        for (String date : allDates) {
            Map<String, List<GetWaterViewNewPojo>> dayWater = waterByDate.getOrDefault(date, Collections.emptyMap());
            Map<String, List<ST_FLOW_RPojo>> dayFlow = flowByDate.getOrDefault(date, Collections.emptyMap());

            for (String stcd : allStcds) {
                ST_RVAV_RPojo pojo = new ST_RVAV_RPojo();
                pojo.setSTCD(stcd);
                // 标志时间：截止后的次日零点
                Date nextDay = DateUtil.addTimeToDate(
                        DateUtil.strToDate(date + " 00:00:00", DateUtil.YMDHMS), "d", 1);
                pojo.setIDTM(DateUtil.dateFormat(nextDay, "yyyy-MM-dd") + " 00:00:00");
                pojo.setSTTDRCD("1");

                List<GetWaterViewNewPojo> wList = dayWater.get(stcd);
                if (wList != null && !wList.isEmpty()) {
                    double sum = wList.stream().mapToDouble(w -> Double.parseDouble(w.getUPZ())).sum();
                    pojo.setAVZ(Double.parseDouble(String.format("%.3f", sum / wList.size())));
                }

                List<ST_FLOW_RPojo> fList = dayFlow.get(stcd);
                if (fList != null && !fList.isEmpty()) {
                    double sum = fList.stream().mapToDouble(ST_FLOW_RPojo::getQ).sum();
                    pojo.setAVQ(Double.parseDouble(String.format("%.3f", sum / fList.size())));
                }

                if (pojo.getAVZ() != null || pojo.getAVQ() != null) {
                    insertList.add(pojo);
                }
            }
        }

        if (!insertList.isEmpty()) {
            return data.upsertAll(insertList);
        }
        return 0;
    }

    /**
     * 基于下级均值整理上级均值：月基于日，年基于月
     * <p>上级均值 = 所有下级均值的算术平均
     */
    public Map<String, Object> syncUpperAverage(String stime, String etime, String idtm,
                                                 String fromSttdrcd, String toSttdrcd, List<String> stcdList) {
        Map<String, Object> result = new HashMap<>();
        List<String> errors = new ArrayList<>();

        List<ST_RVAV_RPojo> lowerList = data.selectListByStcdAndPeriodAndTime(stcdList, fromSttdrcd, stime, etime);

        if (lowerList == null || lowerList.isEmpty()) {
            result.put("stationCount", 0);
            result.put("insertedCount", 0);
            result.put("errors", errors);
            return result;
        }

        Map<String, List<ST_RVAV_RPojo>> grouped = lowerList.stream()
                .filter(r -> r.getSTCD() != null)
                .collect(Collectors.groupingBy(ST_RVAV_RPojo::getSTCD));

        List<ST_RVAV_RPojo> insertList = new ArrayList<>();
        for (Map.Entry<String, List<ST_RVAV_RPojo>> entry : grouped.entrySet()) {
            String stcd = entry.getKey();
            List<ST_RVAV_RPojo> records = entry.getValue();

            ST_RVAV_RPojo pojo = new ST_RVAV_RPojo();
            pojo.setSTCD(stcd);
            pojo.setIDTM(idtm);
            pojo.setSTTDRCD(toSttdrcd);

            List<ST_RVAV_RPojo> withWater = records.stream()
                    .filter(r -> r.getAVZ() != null).collect(Collectors.toList());
            if (!withWater.isEmpty()) {
                double avg = withWater.stream().mapToDouble(ST_RVAV_RPojo::getAVZ).average().orElse(0);
                pojo.setAVZ(Double.parseDouble(String.format("%.3f", avg)));
            }

            List<ST_RVAV_RPojo> withFlow = records.stream()
                    .filter(r -> r.getAVQ() != null).collect(Collectors.toList());
            if (!withFlow.isEmpty()) {
                double avg = withFlow.stream().mapToDouble(ST_RVAV_RPojo::getAVQ).average().orElse(0);
                pojo.setAVQ(Double.parseDouble(String.format("%.3f", avg)));
            }

            insertList.add(pojo);
        }

        int upsertCount = 0;
        if (!insertList.isEmpty()) {
            try {
                upsertCount = data.upsertAll(insertList);
            } catch (Exception e) {
                errors.add("批量 MERGE 失败: " + e.getMessage());
            }
        }

        result.put("stationCount", grouped.size());
        result.put("insertedCount", upsertCount);
        result.put("errors", errors);
        return result;
    }

    // ======================== 私有方法 ========================

    /**
     * 按站点分组计算平均水位
     */
    private Map<String, Double> computeWaterAvg(List<GetWaterViewNewPojo> waterList) {
        if (waterList == null || waterList.isEmpty()) return Collections.emptyMap();

        return waterList.stream()
                .filter(w -> w.getSTCD() != null && w.getUPZ() != null)
                .filter(w -> {
                    try {
                        double v = Double.parseDouble(w.getUPZ());
                        return v > -15 && v < 15;
                    } catch (NumberFormatException e) { return false; }
                })
                .collect(Collectors.groupingBy(
                        GetWaterViewNewPojo::getSTCD,
                        Collectors.averagingDouble(w -> Double.parseDouble(w.getUPZ()))))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> Double.parseDouble(String.format("%.3f", e.getValue()))));
    }

    /**
     * 按站点分组计算平均流量
     */
    private Map<String, Double> computeFlowAvg(List<ST_FLOW_RPojo> flowList) {
        if (flowList == null || flowList.isEmpty()) return Collections.emptyMap();

        return flowList.stream()
                .filter(f -> f.getSTCD() != null && f.getQ() != null)
                .collect(Collectors.groupingBy(
                        ST_FLOW_RPojo::getSTCD,
                        Collectors.averagingDouble(ST_FLOW_RPojo::getQ)))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> Double.parseDouble(String.format("%.3f", e.getValue()))));
    }
}
