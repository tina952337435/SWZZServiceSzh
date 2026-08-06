package swzzmodeserver.workserver.server.swzzrtsq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swzzmodeserver.workserver.data.swzzrtsq.ST_FLOW_RData;
import swzzmodeserver.workserver.data.swzzrtsq.ST_RVEVS_RData;
import swzzmodeserver.workserver.pojo.swzzrtsq.GetWaterViewNewPojo;
import swzzmodeserver.workserver.pojo.swzzrtsq.ST_FLOW_RPojo;
import swzzmodeserver.workserver.pojo.swzzrtsq.ST_RVEVS_RPojo;
import swzzmodeserver.tools.DateUtil;

import java.util.*;
import java.util.stream.Collectors;

/**
 * SL323-2011 表90 河道水情极值表 (ST_RVEVS_R) 业务服务层
 */
@Service
public class ST_RVEVS_RServer {

    @Autowired
    private ST_RVEVS_RData data;

    @Autowired
    private GetWaterViewNewServer getWaterViewNewServer;

    @Autowired
    private ST_FLOW_RData stFlowRData;

    // ======================== 查询方法 ========================

    /**
     * 按站点+统计时段标志查询极值列表
     */
    public List<ST_RVEVS_RPojo> selectListByStcdAndPeriod(List<String> stcdList, String sttdrcd) {
        return data.selectListByStcdAndPeriod(stcdList, sttdrcd);
    }

    /**
     * 按站点+时段+时间范围查询极值列表
     */
    public List<ST_RVEVS_RPojo> selectListByStcdAndPeriodAndTime(
            List<String> stcdList, String sttdrcd, String stime, String etime) {
        return data.selectListByStcdAndPeriodAndTime(stcdList, sttdrcd, stime, etime);
    }

    /**
     * 按时间范围查询极值列表
     */
    public List<ST_RVEVS_RPojo> selectListByTimeRange(String stime, String etime) {
        return data.selectListByTimeRange(stime, etime);
    }

    /**
     * 批量插入极值数据
     */
    public Integer insertAll(List<ST_RVEVS_RPojo> pojoList) {
        return data.insertAll(pojoList);
    }

    /**
     * 按联合主键查询单条记录
     */
    public ST_RVEVS_RPojo selectOne(String stcd, String idtm, String sttdrcd) {
        return data.selectOne(stcd, idtm, sttdrcd);
    }

    // ======================== 极值整理方法 ========================

    /**
     * 从实时数据表整理极值数据（由定时器调用，如每日凌晨整理昨日日极值）
     *
     * <p>数据来源：
     * <ul>
     *   <li>水位时序 → GetWaterViewNewServer.selectListByHisIsTime（汇总 ST_RIVER_R / ST_WAS_R / ST_TIDE_R / ST_PUMP_R 四表）</li>
     *   <li>流量时序 → ST_FLOW_R（取 Q 字段）</li>
     * </ul>
     *
     * <p>整理逻辑：
     * <ol>
     *   <li>从水位综合视图拉取时间范围内所有站点的水位时序数据</li>
     *   <li>从流量表拉取时间范围内所有站点的流量时序数据</li>
     *   <li>按站点分组，分别计算水位极值(HTZ/HTZTM/LTZ/LTZTM)和流量极值(MXQ/MXQTM/MNQ/MNQTM)</li>
     *   <li>合并水位和流量极值到同一行（有数据的填数据，没数据的留空）</li>
     *   <li>批量 MERGE 原子写入，零空档期</li>
     * </ol>
     *
     * @param stime   数据查询开始时间，格式 yyyy-MM-dd HH:mm:ss
     * @param etime   数据查询结束时间，格式 yyyy-MM-dd HH:mm:ss
     * @param idtm    写入 IDTM 字段的标志时间，格式 yyyy-MM-dd HH:mm:ss
     * @param sttdrcd 统计时段标志（1=一日, 2=三日, 3=一侯, 4=一旬, 5=一月, 6=一年）
     * @param mtype   水位数据来源类型筛选，传 null 则不做筛选
     * @param stcdList 站点编码列表，传 null 则处理全部站点
     * @return 统计结果 Map，包含 stationCount / waterStationCount / flowStationCount / insertedCount / errors
     */
    public Map<String, Object> syncDailyExtreme(String stime, String etime, String idtm, String sttdrcd, String mtype,
                                                 List<String> stcdList) {
        Map<String, Object> result = new HashMap<>();
        List<String> errors = new ArrayList<>();

        // 1. 拉取站点的水位原始数据（汇总四表视图）
        List<GetWaterViewNewPojo> waterList = getWaterViewNewServer.selectListByHisIsTime(
                stcdList, stime, etime, mtype);

        // 2. 拉取站点的流量原始数据
        List<ST_FLOW_RPojo> flowList = stFlowRData.selectHis(stcdList, stime, etime);

        // 3. 按站点分组，计算水位极值（UPZ 为水位数）
        Map<String, WaterExtreme> waterExtremeMap = computeWaterExtremes(waterList);

        // 4. 按站点分组，计算流量极值
        Map<String, FlowExtreme> flowExtremeMap = computeFlowExtremes(flowList);

        // 5. 合并：所有出现过水位数或流量数据的站点都生成一行
        Set<String> allStcds = new LinkedHashSet<>();
        allStcds.addAll(waterExtremeMap.keySet());
        allStcds.addAll(flowExtremeMap.keySet());

        List<ST_RVEVS_RPojo> insertList = new ArrayList<>();
        for (String stcd : allStcds) {
            WaterExtreme we = waterExtremeMap.get(stcd);
            FlowExtreme fe = flowExtremeMap.get(stcd);

            ST_RVEVS_RPojo pojo = new ST_RVEVS_RPojo();
            pojo.setSTCD(stcd);
            pojo.setIDTM(idtm);
            pojo.setSTTDRCD(sttdrcd);

            // 水位极值（有数据才填）
            if (we != null) {
                pojo.setHTZ(we.getHtz());
                pojo.setHTZTM(we.getHtztm());
                pojo.setLTZ(we.getLtz());
                pojo.setLTZTM(we.getLtztm());
            }

            // 流量极值（有数据才填）
            if (fe != null) {
                pojo.setMXQ(fe.getMxq());
                pojo.setMXQTM(fe.getMxqtm());
                pojo.setMNQ(fe.getMnq());
                pojo.setMNQTM(fe.getMnqtm());
            }

            insertList.add(pojo);
        }

        // 6. 批量 MERGE（原子 upsert：一条 SQL，存在则更新，不存在则插入，零空档期）
        int upsertCount = 0;
        if (!insertList.isEmpty()) {
            try {
                upsertCount = data.upsertAll(insertList);
            } catch (Exception e) {
                errors.add("批量 MERGE 失败: " + e.getMessage());
            }
        }

        result.put("stationCount", allStcds.size());
        result.put("waterStationCount", waterExtremeMap.size());
        result.put("flowStationCount", flowExtremeMap.size());
        result.put("insertedCount", upsertCount);
        result.put("errors", errors);
        return result;
    }

    /**
     * 基于下级极值整理上级极值：月基于日，年基于月
     *
     * <p>不再扫描原始数据表，直接对 ST_RVEVS_R 中已有的下级极值取 max/min。
     * <p>数据量：月极值 ≤ 31条/站，年极值 ≤ 12条/站，秒级完成。
     *
     * <p>典型调用：
     * <ul>
     *   <li>月基于日：fromSttdrcd="1", toSttdrcd="5"</li>
     *   <li>年基于月：fromSttdrcd="5", toSttdrcd="6"</li>
     * </ul>

     * @param stime        查询开始时间
     * @param etime        查询结束时间
     * @param idtm         写入的标志时间
     * @param fromSttdrcd  源时段标志（1=日 → 5=月，5=月 → 6=年）
     * @param toSttdrcd    目标时段标志
     */
    public Map<String, Object> syncUpperExtreme(String stime, String etime, String idtm,
                                                 String fromSttdrcd, String toSttdrcd, List<String> stcdList) {
        Map<String, Object> result = new HashMap<>();
        List<String> errors = new ArrayList<>();

        // 1. 查询已有的下级极值
        List<ST_RVEVS_RPojo> lowerList = data.selectListByStcdAndPeriodAndTime(
                stcdList, fromSttdrcd, stime, etime);

        if (lowerList == null || lowerList.isEmpty()) {
            result.put("stationCount", 0);
            result.put("insertedCount", 0);
            result.put("errors", errors);
            return result;
        }

        // 2. 按 STCD 分组
        Map<String, List<ST_RVEVS_RPojo>> grouped = lowerList.stream()
                .filter(r -> r.getSTCD() != null)
                .collect(Collectors.groupingBy(ST_RVEVS_RPojo::getSTCD));

        List<ST_RVEVS_RPojo> insertList = new ArrayList<>();
        for (Map.Entry<String, List<ST_RVEVS_RPojo>> entry : grouped.entrySet()) {
            String stcd = entry.getKey();
            List<ST_RVEVS_RPojo> records = entry.getValue();

            ST_RVEVS_RPojo pojo = new ST_RVEVS_RPojo();
            pojo.setSTCD(stcd);
            pojo.setIDTM(idtm);
            pojo.setSTTDRCD(toSttdrcd);

            // 最高水位：值降序，同值取最早出现时间
            records.stream()
                    .filter(r -> r.getHTZ() != null)
                    .sorted(Comparator.comparingDouble(ST_RVEVS_RPojo::getHTZ).reversed()
                            .thenComparing(r -> r.getHTZTM() != null ? r.getHTZTM() : ""))
                    .findFirst()
                    .ifPresent(r -> {
                        pojo.setHTZ(r.getHTZ());
                        pojo.setHTZTM(r.getHTZTM());
                    });

            // 最低水位：值升序，同值取最早出现时间
            records.stream()
                    .filter(r -> r.getLTZ() != null)
                    .sorted(Comparator.comparingDouble(ST_RVEVS_RPojo::getLTZ)
                            .thenComparing(r -> r.getLTZTM() != null ? r.getLTZTM() : ""))
                    .findFirst()
                    .ifPresent(r -> {
                        pojo.setLTZ(r.getLTZ());
                        pojo.setLTZTM(r.getLTZTM());
                    });

            // 最大流量：值降序，同值取最早出现时间
            records.stream()
                    .filter(r -> r.getMXQ() != null)
                    .sorted(Comparator.comparingDouble(ST_RVEVS_RPojo::getMXQ).reversed()
                            .thenComparing(r -> r.getMXQTM() != null ? r.getMXQTM() : ""))
                    .findFirst()
                    .ifPresent(r -> {
                        pojo.setMXQ(r.getMXQ());
                        pojo.setMXQTM(r.getMXQTM());
                    });

            // 最小流量：值升序，同值取最早出现时间
            records.stream()
                    .filter(r -> r.getMNQ() != null)
                    .sorted(Comparator.comparingDouble(ST_RVEVS_RPojo::getMNQ)
                            .thenComparing(r -> r.getMNQTM() != null ? r.getMNQTM() : ""))
                    .findFirst()
                    .ifPresent(r -> {
                        pojo.setMNQ(r.getMNQ());
                        pojo.setMNQTM(r.getMNQTM());
                    });

            insertList.add(pojo);
        }

        // 3. 批量 MERGE 写入
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

    /**
     * 批量整理日极值：一次查询原始数据，按天分组，批量产出范围内每一天的日极值
     * <p>用于月极值的前置步骤，避免 30 次独立查询。
     *
     * @param stime 查询开始时间
     * @param etime 查询结束时间
     * @param mtype 水位数据来源类型，传 null 不做筛选
     * @return 入库条数
     */
    public int syncDailyBatch(String stime, String etime, String mtype, List<String> stcdList) {
        // 1. 拉取水位原始数据
        List<GetWaterViewNewPojo> waterList = getWaterViewNewServer.selectListByHisIsTime(
                stcdList, stime, etime, mtype);

        // 2. 拉取流量原始数据
        List<ST_FLOW_RPojo> flowList = stFlowRData.selectHis(stcdList, stime, etime);

        // 3. 水位按 (STCD, 日期) 分组
        Map<String, Map<String, List<GetWaterViewNewPojo>>> waterByDate = waterList.stream()
                .filter(w -> w.getSTCD() != null && w.getTM() != null && w.getUPZ() != null)
                .filter(w -> {
                    try {
                        double v = Double.parseDouble(w.getUPZ());
                        return v > -15 && v < 15;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                })
                .collect(Collectors.groupingBy(
                        w -> w.getTM().substring(0, 10),     // 日期 yyyy-MM-dd
                        Collectors.groupingBy(GetWaterViewNewPojo::getSTCD)));

        // 4. 流量按 (STCD, 日期) 分组
        Map<String, Map<String, List<ST_FLOW_RPojo>>> flowByDate = flowList.stream()
                .filter(f -> f.getSTCD() != null && f.getTM() != null && f.getQ() != null)
                .collect(Collectors.groupingBy(
                        f -> f.getTM().substring(0, 10),
                        Collectors.groupingBy(ST_FLOW_RPojo::getSTCD)));

        // 5. 收集所有日期和所有站点
        Set<String> allDates = new LinkedHashSet<>();
        allDates.addAll(waterByDate.keySet());
        allDates.addAll(flowByDate.keySet());

        Set<String> allStcds = new LinkedHashSet<>();
        waterByDate.values().forEach(m -> allStcds.addAll(m.keySet()));
        flowByDate.values().forEach(m -> allStcds.addAll(m.keySet()));

        // 6. 逐日逐站计算极值
        List<ST_RVEVS_RPojo> insertList = new ArrayList<>();
        for (String date : allDates) {
            Map<String, List<GetWaterViewNewPojo>> dayWater = waterByDate.getOrDefault(date, Collections.emptyMap());
            Map<String, List<ST_FLOW_RPojo>> dayFlow = flowByDate.getOrDefault(date, Collections.emptyMap());

            for (String stcd : allStcds) {
                ST_RVEVS_RPojo pojo = new ST_RVEVS_RPojo();
                pojo.setSTCD(stcd);
                Date nextDay = DateUtil.addTimeToDate(
                        DateUtil.strToDate(date + " 00:00:00", DateUtil.YMDHMS), "d", 1);
                pojo.setIDTM(DateUtil.dateFormat(nextDay, "yyyy-MM-dd") + " 00:00:00");
                pojo.setSTTDRCD("1");  // 日极值

                // 水位极值：值优先，同值取第一次出现
                List<GetWaterViewNewPojo> wList = dayWater.get(stcd);
                if (wList != null && !wList.isEmpty()) {
                    wList.stream()
                            .sorted(Comparator.comparingDouble(
                                    (GetWaterViewNewPojo w) -> Double.parseDouble(w.getUPZ())).reversed()
                                    .thenComparing(GetWaterViewNewPojo::getTM))
                            .findFirst()
                            .ifPresent(w -> { pojo.setHTZ(Double.parseDouble(w.getUPZ())); pojo.setHTZTM(w.getTM()); });
                    wList.stream()
                            .sorted(Comparator.comparingDouble(
                                    (GetWaterViewNewPojo w) -> Double.parseDouble(w.getUPZ()))
                                    .thenComparing(GetWaterViewNewPojo::getTM))
                            .findFirst()
                            .ifPresent(w -> { pojo.setLTZ(Double.parseDouble(w.getUPZ())); pojo.setLTZTM(w.getTM()); });
                }

                // 流量极值：值优先，同值取第一次出现
                List<ST_FLOW_RPojo> fList = dayFlow.get(stcd);
                if (fList != null && !fList.isEmpty()) {
                    fList.stream()
                            .sorted(Comparator.comparingDouble((ST_FLOW_RPojo f) -> f.getQ()).reversed()
                                    .thenComparing(ST_FLOW_RPojo::getTM))
                            .findFirst()
                            .ifPresent(f -> { pojo.setMXQ(f.getQ()); pojo.setMXQTM(f.getTM()); });
                    fList.stream()
                            .sorted(Comparator.comparingDouble((ST_FLOW_RPojo f) -> f.getQ())
                                    .thenComparing(ST_FLOW_RPojo::getTM))
                            .findFirst()
                            .ifPresent(f -> { pojo.setMNQ(f.getQ()); pojo.setMNQTM(f.getTM()); });
                }

                // 只有当水位或流量至少有一个有数据时才入库
                if (pojo.getHTZ() != null || pojo.getLTZ() != null
                        || pojo.getMXQ() != null || pojo.getMNQ() != null) {
                    insertList.add(pojo);
                }
            }
        }

        // 7. 批量 MERGE
        if (!insertList.isEmpty()) {
            return data.upsertAll(insertList);
        }
        return 0;
    }

    /**
     * 按站点分组，从水位综合视图数据中计算最高/最低水位及出现时间
     * <p>水位值取 UPZ 字段（String → Double），过滤空值和异常值
     */
    private Map<String, WaterExtreme> computeWaterExtremes(List<GetWaterViewNewPojo> waterList) {
        if (waterList == null || waterList.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, WaterExtreme> result = new HashMap<>();

        // 过滤：STCD 非空、TM 非空、UPZ 可解析为有效数字且在合理范围
        Map<String, List<GetWaterViewNewPojo>> grouped = waterList.stream()
                .filter(w -> w.getSTCD() != null && w.getTM() != null && w.getUPZ() != null)
                .filter(w -> {
                    try {
                        double v = Double.parseDouble(w.getUPZ());
                        return v > -15 && v < 15;  // 过滤异常值（与同步逻辑一致）
                    } catch (NumberFormatException e) {
                        return false;
                    }
                })
                .collect(Collectors.groupingBy(GetWaterViewNewPojo::getSTCD));

        for (Map.Entry<String, List<GetWaterViewNewPojo>> entry : grouped.entrySet()) {
            String stcd = entry.getKey();
            List<GetWaterViewNewPojo> records = entry.getValue();

            // 最高水位：值降序，同值按时间升序（取第一次出现）
            GetWaterViewNewPojo maxZ = records.stream()
                    .sorted(Comparator.comparingDouble(
                            (GetWaterViewNewPojo w) -> Double.parseDouble(w.getUPZ())).reversed()
                            .thenComparing(GetWaterViewNewPojo::getTM))
                    .findFirst().orElse(null);

            // 最低水位：值升序，同值按时间升序（取第一次出现）
            GetWaterViewNewPojo minZ = records.stream()
                    .sorted(Comparator.comparingDouble(
                            (GetWaterViewNewPojo w) -> Double.parseDouble(w.getUPZ()))
                            .thenComparing(GetWaterViewNewPojo::getTM))
                    .findFirst().orElse(null);

            WaterExtreme extreme = new WaterExtreme();
            if (maxZ != null) {
                extreme.setHtz(Double.parseDouble(maxZ.getUPZ()));
                extreme.setHtztm(maxZ.getTM());
            }
            if (minZ != null) {
                extreme.setLtz(Double.parseDouble(minZ.getUPZ()));
                extreme.setLtztm(minZ.getTM());
            }
            result.put(stcd, extreme);
        }

        return result;
    }

    /**
     * 按站点分组，从流量时序数据中计算最大/最小流量及出现时间
     */
    private Map<String, FlowExtreme> computeFlowExtremes(List<ST_FLOW_RPojo> flowList) {
        if (flowList == null || flowList.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, FlowExtreme> result = new HashMap<>();

        Map<String, List<ST_FLOW_RPojo>> grouped = flowList.stream()
                .filter(f -> f.getSTCD() != null && f.getQ() != null && f.getTM() != null)
                .collect(Collectors.groupingBy(ST_FLOW_RPojo::getSTCD));

        for (Map.Entry<String, List<ST_FLOW_RPojo>> entry : grouped.entrySet()) {
            String stcd = entry.getKey();
            List<ST_FLOW_RPojo> records = entry.getValue();

            // 最大流量：值降序，同值按时间升序（取第一次出现）
            ST_FLOW_RPojo maxQ = records.stream()
                    .sorted(Comparator.comparingDouble((ST_FLOW_RPojo f) -> f.getQ()).reversed()
                            .thenComparing(ST_FLOW_RPojo::getTM))
                    .findFirst().orElse(null);

            // 最小流量：值升序，同值按时间升序（取第一次出现）
            ST_FLOW_RPojo minQ = records.stream()
                    .sorted(Comparator.comparingDouble((ST_FLOW_RPojo f) -> f.getQ())
                            .thenComparing(ST_FLOW_RPojo::getTM))
                    .findFirst().orElse(null);

            FlowExtreme extreme = new FlowExtreme();
            if (maxQ != null) {
                extreme.setMxq(maxQ.getQ());
                extreme.setMxqtm(maxQ.getTM());
            }
            if (minQ != null) {
                extreme.setMnq(minQ.getQ());
                extreme.setMnqtm(minQ.getTM());
            }
            result.put(stcd, extreme);
        }

        return result;
    }

    // ======================== 内部类 ========================

    private static class WaterExtreme {
        private Double htz;
        private String htztm;
        private Double ltz;
        private String ltztm;

        public Double getHtz() { return htz; }
        public void setHtz(Double htz) { this.htz = htz; }
        public String getHtztm() { return htztm; }
        public void setHtztm(String htztm) { this.htztm = htztm; }
        public Double getLtz() { return ltz; }
        public void setLtz(Double ltz) { this.ltz = ltz; }
        public String getLtztm() { return ltztm; }
        public void setLtztm(String ltztm) { this.ltztm = ltztm; }
    }

    private static class FlowExtreme {
        private Double mxq;
        private String mxqtm;
        private Double mnq;
        private String mnqtm;

        public Double getMxq() { return mxq; }
        public void setMxq(Double mxq) { this.mxq = mxq; }
        public String getMxqtm() { return mxqtm; }
        public void setMxqtm(String mxqtm) { this.mxqtm = mxqtm; }
        public Double getMnq() { return mnq; }
        public void setMnq(Double mnq) { this.mnq = mnq; }
        public String getMnqtm() { return mnqtm; }
        public void setMnqtm(String mnqtm) { this.mnqtm = mnqtm; }
    }
}
