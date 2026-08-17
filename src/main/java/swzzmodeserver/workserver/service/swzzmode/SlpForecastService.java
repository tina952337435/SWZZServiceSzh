package swzzmodeserver.workserver.service.swzzmode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swzzmodeserver.workserver.data.swzzmode.BDMS_PREDICTData;
import swzzmodeserver.workserver.data.swzzmode.ESSlpBaseData;
import swzzmodeserver.workserver.pojo.swzzmode.BDMS_PREDICTPojo;
import swzzmodeserver.workserver.pojo.swzzmode.ESSlpBasePojo;
import swzzmodeserver.workserver.pojo.swzzrtsq.GetWaterViewNewPojo;
import swzzmodeserver.workserver.server.swzzrtsq.GetWaterViewNewServer;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 水利片水位预测业务
 */
@Service
public class SlpForecastService {

    @Autowired
    private ESSlpBaseData esslpBaseData;

    @Autowired
    private BDMS_PREDICTData bdmsPredictData;

    @Autowired
    private GetWaterViewNewServer getWaterViewNewServer;

    private static final DecimalFormat df2 = new DecimalFormat("0.00");

    /**
     * 查询水利片水位预测数据
     */
    public List<Map<String, Object>> query(String ddId, String stime) {
        List<Map<String, Object>> result = new ArrayList<>();
        List<ESSlpBasePojo> baseList = esslpBaseData.selectList();
        if (baseList == null || baseList.isEmpty()) return result;

        Set<String> allStcds = new LinkedHashSet<>();
        for (ESSlpBasePojo base : baseList) {
            if (base.getSTCD_LIST() != null && !base.getSTCD_LIST().isEmpty()) {
                allStcds.addAll(Arrays.asList(base.getSTCD_LIST().split(",")));
            }
        }

        Map<String, Double> predictMap = queryPredict(ddId, stime, allStcds);
        Map<String, Double> actualMap = queryActual(stime, allStcds);

        for (ESSlpBasePojo base : baseList) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("xh", base.getXH());                                    // 序号
            row.put("slpName", base.getSLP_NAME());                          // 水利片名称
            row.put("area", base.getAREA());                                 // 面积(km²)
            row.put("hhArea", base.getHH_AREA());                            // 河湖面积(km²)
            row.put("stnmList", base.getSTNM_LIST());                        // 站点名称列表
            row.put("yjSw", base.getYJ_SW());                                // 预降水位(输入水位)
            row.put("cSw", base.getC_SW());                                  // 常水位
            row.put("xjjSw", base.getXJJ_SW());                              // 新警戒水位
            row.put("bzSw", base.getBZ_SW());                                // 保证水位
            row.put("formulaA", base.getFORMULA_A());                        // 库容公式系数a
            row.put("formulaB", base.getFORMULA_B());                        // 库容公式系数b
            row.put("formulaC", base.getFORMULA_C());                        // 库容公式系数c (y=ax²+bx+c)

            if (base.getSTCD_LIST() != null && !base.getSTCD_LIST().isEmpty()) {
                List<String> stcds = Arrays.asList(base.getSTCD_LIST().split(","));
                Double predictSw = avg(predictMap, stcds);                   // 方案+时间预测水位(片内多站平均)
                Double actualSw = avg(actualMap, stcds);                     // 实测水位(配置站点最新值平均)

                row.put("predictSw", predictSw != null ? df2.format(predictSw) : null);   // 预测水位
                row.put("actualSw", actualSw != null ? df2.format(actualSw) : null);       // 实测水位
                row.put("predictDiffBz", diff(predictSw, base.getBZ_SW()));   // 预测水位距保证水位
                row.put("predictDiffXjj", diff(predictSw, base.getXJJ_SW())); // 预测水位距新警戒水位
                row.put("actualDiffBz", diff(actualSw, base.getBZ_SW()));     // 实测水位距保证水位
                row.put("actualDiffXjj", diff(actualSw, base.getXJJ_SW()));   // 实测水位距新警戒水位
            } else {
                // 无代表站的片：水位字段为空，基础资料照常返回
                row.put("predictSw", null);
                row.put("actualSw", null);
                row.put("predictDiffBz", null);
                row.put("predictDiffXjj", null);
                row.put("actualDiffBz", null);
                row.put("actualDiffXjj", null);
            }

            result.add(row);
        }
        return result;
    }

    /** 预测水位：BDMS_PREDICT 按方案+时间查，返回 STCD → 水位 */
    private Map<String, Double> queryPredict(String ddId, String stime, Set<String> allStcds) {
        Map<String, Double> map = new LinkedHashMap<>();
        if (ddId == null || ddId.isEmpty()) return map;
        try {
            List<String> planList = Collections.singletonList(ddId);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date d = stime != null ? sdf.parse(stime) : new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            cal.add(Calendar.HOUR, -1);
            String qstime = new SimpleDateFormat("yyyy-MM-dd HH:00:00").format(cal.getTime());
            cal.add(Calendar.HOUR, 2);
            String qetime = new SimpleDateFormat("yyyy-MM-dd HH:59:59").format(cal.getTime());

            List<String> stcdList = new ArrayList<>(allStcds);
            List<BDMS_PREDICTPojo> list = bdmsPredictData.selectList(
                    "", qstime, qetime, planList, null, "1", null, null, "", stcdList);
            if (list != null) {
                for (BDMS_PREDICTPojo p : list) {
                    if (p.getSTCD() != null && p.getDATA() != null) {
                        map.put(p.getSTCD(), p.getDATA().doubleValue());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    /** 实测水位：配置站点最新值，返回 STCD → 水位 */
    private Map<String, Double> queryActual(String stime, Set<String> allStcds) {
        Map<String, Double> map = new LinkedHashMap<>();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date now = stime != null ? sdf.parse(stime) : new Date();
            String qetime = sdf.format(now);
            String qstime = sdf.format(new Date(now.getTime() - 3L * 3600 * 1000));

            List<String> stcdList = new ArrayList<>(allStcds);
            List<GetWaterViewNewPojo> list = getWaterViewNewServer.selectListByHisIsTime(
                    stcdList, qstime, qetime, null);
            if (list != null) {
                Map<String, GetWaterViewNewPojo> latest = new LinkedHashMap<>();
                for (GetWaterViewNewPojo w : list) {
                    if (w.getSTCD() == null || w.getUPZ() == null) continue;
                    if (!latest.containsKey(w.getSTCD())
                            || (w.getTM() != null && w.getTM().compareTo(latest.get(w.getSTCD()).getTM()) > 0)) {
                        latest.put(w.getSTCD(), w);
                    }
                }
                for (Map.Entry<String, GetWaterViewNewPojo> e : latest.entrySet()) {
                    try {
                        map.put(e.getKey(), Double.parseDouble(e.getValue().getUPZ()));
                    } catch (Exception ignore) {}
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    /** 多站取平均 */
    private Double avg(Map<String, Double> map, List<String> stcds) {
        double sum = 0;
        int cnt = 0;
        for (String stcd : stcds) {
            Double v = map.get(stcd.trim());
            if (v != null) { sum += v; cnt++; }
        }
        return cnt == 0 ? null : sum / cnt;
    }

    /** 差值 = 水位 - 参考水位 */
    private String diff(Double sw, Double ref) {
        if (sw == null || ref == null) return null;
        return df2.format(sw - ref);
    }
}
