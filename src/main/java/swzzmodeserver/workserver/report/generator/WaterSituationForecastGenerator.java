package swzzmodeserver.workserver.report.generator;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import swzzmodeserver.tools.DocxReplaceUtil;
import swzzmodeserver.tools.SqkbChartUtils;
import swzzmodeserver.workserver.pojo.swzzmode.WJ_MODELSINGRESULTParam;
import swzzmodeserver.workserver.pojo.swzzrtsq.GetWaterViewNewPojo;
import swzzmodeserver.workserver.report.AbstractReportGenerator;
import swzzmodeserver.workserver.report.ReportRequest;
import swzzmodeserver.workserver.report.data.NmcTyphoonService;
import swzzmodeserver.workserver.report.data.NmcTyphoonService.*;
import swzzmodeserver.workserver.server.swzzrtsq.GetWaterViewNewServer;
import swzzmodeserver.workserver.service.swzzmode.BDMS_PREDICTService;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.io.File;
import java.util.*;

@Component
public class WaterSituationForecastGenerator extends AbstractReportGenerator {

    @Autowired
    private NmcTyphoonService nmcTyphoonService;

    @Autowired
    private GetWaterViewNewServer getWaterViewNewServer;

    @Autowired
    private BDMS_PREDICTService bdmsPredictService;

    @Autowired
    private swzzmodeserver.workserver.data.swzzmode.DD_SOLUTIONData ddSolutionData;

    private static final DecimalFormat df2 = new DecimalFormat("0.00");
    private ReportRequest currentParams; // fillTemplate 中需要用到

    // 7片区代表站 (站名, STCD)
    private static final String[][] AREA_STATIONS = {
            // { "虹桥", "63404700" },
            // { "嘉定南门", "63405480" },
            // { "祝桥", "63425700" },
            // { "张堰（二）", "63401900" },
            // { "陈坊桥", "63404100" },
            // { "青浦南门", "63404000" },
            // { "南桥", "63404500" },

            { "青浦南门", "63404000" },
            { "陈坊桥", "63404100" },
            { "南桥", "63404500" },
            { "祝桥", "63425700" },
            { "嘉定南门", "63405480" },
            { "虹桥", "63404700" },
            { "张堰", "63401900" },
    };

    // 7片区名称（对应模板表1顺序）
    private static final String[] AREA_NAMES = {
            "青松片", "青松片", "浦东片", "浦东片", "嘉宝北片", "淀北片", "浦南东片"
    };

    // 表2额外站点 (站名, STCD) — 排在7片区之后
    private static final String[][] TABLE2_EXTRA_STATIONS = {
            { "北桥", "63425120" },
            { "黄浦公园", "63401500" },
            { "米市渡", "63401100" },
            { "洙泾", "63402300" },
            { "泖甸", "63403100" },
    };

    // 表2全部12站的片区名
    private static final String[] TABLE2_AREA_NAMES = {
            "青松片", "青松片", "浦东片", "浦东片", "嘉宝北片", "淀北片", "淀南片",
            "浦南东片", "黄浦江干流", "黄浦江干流", "掘石港", "拦路港"
    };

    @Override
    public String getReportType() {
        return "分片水情预报专报";
    }

    @Override
    protected String getTemplateName() {
        return "water_situation_forecast.docx";
    }

    @Override
    protected String getOutputDirName() {
        return "water_situation";
    }

    @Override
    protected boolean isSaveRecord() {
        return false;
    }

    @Override
    protected ReportData extractData(ReportRequest params) throws Exception {
        this.currentParams = params;
        ReportData data = new ReportData();
        Date now = new Date();
        String currentYear = new SimpleDateFormat("yyyy").format(now);

        // === 台风数据（复用NMC爬取）===
        TyphoonDetail typhoonDetail = null;
        TyphoonSummary typhoonSummary = null;
        TrackPoint latestObs = null;
        List<ForecastPoint> cmaForecast = null;

        try {
            int year = Integer.parseInt(currentYear);
            List<TyphoonSummary> allTyphoons = nmcTyphoonService.getTyphoonList(year);
            // 优先0：前端传入ID
            if (typhoonSummary == null && params.getTyphoonId() != null) {
                typhoonSummary = nmcTyphoonService.findTyphoonById(allTyphoons, params.getTyphoonId());
            }
            // 优先1：前端传入
            if (typhoonSummary == null && params.getTyphoonCode() != null) {
                typhoonSummary = nmcTyphoonService.findTyphoon(year, params.getTyphoonCode(), params.getTyphoonName());
            }
            // 优先2：网页当前台风
            if (typhoonSummary == null) {
                String webCode = nmcTyphoonService.fetchCurrentTyphoonCode();
                if (webCode != null) {
                    for (TyphoonSummary ts : allTyphoons) {
                        if (webCode.equals(ts.getCode1()) || webCode.equals(ts.getCode2())) {
                            typhoonSummary = ts;
                            break;
                        }
                    }
                }
            }
            // 兜底
            if (typhoonSummary == null) {
                for (TyphoonSummary ts : allTyphoons) {
                    if ("start".equals(ts.getStatus()) && !"nameless".equals(ts.getCnName())) {
                        if (typhoonSummary == null || ts.getId() > typhoonSummary.getId()) {
                            typhoonSummary = ts;
                        }
                    }
                }
            }
            if (typhoonSummary != null) {
                typhoonDetail = nmcTyphoonService.getTyphoonDetail(typhoonSummary.getId());
                latestObs = nmcTyphoonService.getLatestObservation(typhoonDetail);
                cmaForecast = nmcTyphoonService.getCmaForecast(typhoonDetail);
                System.out.println("localTimeChinese=" + (latestObs != null ? nmcTyphoonService.getObsTimeString(latestObs) : "null"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String typhoonCode = typhoonSummary != null ? typhoonSummary.getCode1() : params.getTyphoonCode();
        String typhoonName = typhoonSummary != null ? typhoonSummary.getCnName() : params.getTyphoonName();
        if (typhoonName == null || typhoonName.isEmpty())
            typhoonName = "台风";
        params.setTyphoonCode(typhoonCode);
        params.setTyphoonName(typhoonName);

        // === 通用占位符 ===
        int qihao = calculateNextQihao(params);
        data.setTitle(typhoonCode + "号台风“" + typhoonName + "”分片水情预报");
        data.addPlaceholder("reportNo", currentYear + "年第" + qihao + "期");
        data.addPlaceholder("reportDate", new SimpleDateFormat("yyyy年M月d日H时").format(now));
        data.addPlaceholder("typhoonTitle", typhoonCode + "号台风“" + typhoonName + "”分片水情预报");
        data.addPlaceholder("forecaster", params.getAuthor() != null ? params.getAuthor() : "");

        // === 台风动态 ===
        if (typhoonDetail != null && latestObs != null) {
            data.addPlaceholder("typhoonStatus",
                    nmcTyphoonService.buildTyphoonStatusText(typhoonDetail, latestObs, cmaForecast));
            data.addPlaceholder("typhoonMovement",
                    nmcTyphoonService.buildTyphoonMovementText(typhoonDetail, latestObs, cmaForecast));
        } else {
            data.addPlaceholder("typhoonStatus", "（台风动态数据暂未获取，请手动填写）");
            data.addPlaceholder("typhoonMovement", "");
        }

        // === 摘要 ===
        data.addPlaceholder("summaryText", buildSummaryText(typhoonName, latestObs, cmaForecast));
        data.addPlaceholder("disclaimerText", buildDisclaimer(typhoonName));

        // === 台风路径图 ===
        String imagePath = findOrFetchTyphoonImage(params, typhoonCode, typhoonName);
        if (imagePath != null) {
            String timeStr = formatObsTimeForCaption(latestObs != null ? nmcTyphoonService.getObsTimeString(latestObs) : null);
            String numStr = typhoonCode != null && typhoonCode.length() >= 4
                    ? String.valueOf(Integer.parseInt(typhoonCode.substring(2)))
                    : "";
            data.addImage("typhoonImage", imagePath,
                    "图1  " + timeStr + "第" + numStr + "号台风“" + typhoonName + "”中央气象台路径预报图");
        }

        // === 表1：7片区当前水位 ===
        String table1Time = buildTable1Caption(params);
        data.addPlaceholder("table1Title", "表1  主要水利片代表站" + table1Time + "水位");
        data.setTableData(buildTable1(params));

        // === 水情现状文字 ===
        data.addPlaceholder("waterStatus", buildWaterStatusText(params));

        // === 水位过程图（模板已有图题，不传caption） ===
        String chartPath = generateWaterLevelChart(params);
        if (chartPath != null) {
            data.addImage("chartImage", chartPath, null);
        }

        // === 水情预测文字 ===
        data.addPlaceholder("waterPrediction", buildWaterPredictionText(params));
        data.addPlaceholder("forecastConclusion", buildForecastConclusion(latestObs));

        // === 表2：12站点预测水位 ===
        // 在fillTemplate中处理，需要单独填充第二个表格

        return data;
    }

    @Override
    protected void fillTemplate(XWPFDocument doc, ReportData data) throws Exception {
        DocxReplaceUtil.replaceText(doc, data.getPlaceholders());

        // 图片
        if (data.getImages() != null) {
            for (Map.Entry<String, ReportData.ImageInfo> e : data.getImages().entrySet()) {
                DocxReplaceUtil.replaceImage(doc, e.getKey(), e.getValue().getPath(), e.getValue().getCaption());
            }
        }

        // 表1：7片区当前水位
        if (data.getTableData() != null && !data.getTableData().isEmpty()) {
            DocxReplaceUtil.fillTable(doc, 0, data.getTableData(), 1);
        }

        // 表2：预测水位
        List<String[]> table2 = buildTable2(currentParams);
        if (table2 != null && !table2.isEmpty()) {
            DocxReplaceUtil.fillTable(doc, 1, table2, 1);
        }
    }

    // ===================== 表1：7片区当前水位 =====================

    private List<String[]> buildTable1(ReportRequest params) {
        List<String[]> rows = new ArrayList<>();
        Date now = new Date();
        // etime 取当前整点（如17:30→17:00）
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        String etime = params.getEtime() != null ? params.getEtime()
                : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.getTime());
        String stime = params.getStime() != null ? params.getStime()
                : new SimpleDateFormat("yyyy-MM-dd HH:00:00").format(new Date(cal.getTimeInMillis() - 86400000L));
        System.out.println("表1查询: stime=" + stime + ", etime=" + etime);

        try {
            List<String> stcdList = new ArrayList<>();
            for (String[] st : AREA_STATIONS)
                stcdList.add(st[1]);
            System.out.println("表1查询: stcdList=" + stcdList);

            List<GetWaterViewNewPojo> waterList = getWaterViewNewServer.selectListByHisIsTime(stcdList, stime, etime,
                    null);
            System.out.println("表1查询: 结果数=" + (waterList != null ? waterList.size() : 0));

            // 打印返回数据中的唯一STCD
            Set<String> uniqueStcds = new LinkedHashSet<>();
            if (waterList != null)
                for (GetWaterViewNewPojo w : waterList)
                    uniqueStcds.add(w.getSTCD());
            System.out.println("表1查询: 实际返回STCD=" + uniqueStcds);

            // 按站点取最新一条
            Map<String, GetWaterViewNewPojo> latestMap = new LinkedHashMap<>();
            for (GetWaterViewNewPojo w : waterList) {
                if (!latestMap.containsKey(w.getSTCD())
                        || w.getTM().compareTo(latestMap.get(w.getSTCD()).getTM()) > 0) {
                    latestMap.put(w.getSTCD(), w);
                }
            }

            for (int i = 0; i < AREA_STATIONS.length; i++) {
                GetWaterViewNewPojo w = latestMap.get(AREA_STATIONS[i][1]);
                String[] row = new String[6];
                row[0] = String.valueOf(i + 1);
                row[1] = i < AREA_NAMES.length ? AREA_NAMES[i] : "";
                row[2] = AREA_STATIONS[i][0];
                row[3] = w != null && w.getUPZ() != null ? df2.format(Double.parseDouble(w.getUPZ())) : "";
                row[4] = w != null && w.getWRZ() != null ? df2.format(Double.parseDouble(w.getWRZ())) : "";
                row[5] = w != null && w.getIVHZ() != null ? df2.format(w.getIVHZ()) : "";
                rows.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rows;
    }

    // ===================== 表2：12站点预测水位 =====================

    private List<String[]> buildTable2(ReportRequest params) {
        List<String[]> rows = new ArrayList<>();
        System.out.println("表2查询: ddId=" + (params != null ? params.getDdId() : "null"));
        if (params == null || params.getDdId() == null || params.getDdId().isEmpty()) {
            System.out.println("表2查询: ddId为空，跳过");
            return rows;
        }

        try {
            List<String> solutionIds = Collections.singletonList(params.getDdId());
            List<String> allStcds = new ArrayList<>();

            // 12个站点：7水利片 + 5黄浦江
            String[][] allStations = new String[AREA_STATIONS.length + TABLE2_EXTRA_STATIONS.length][2];
            for (int i = 0; i < AREA_STATIONS.length; i++) {
                allStations[i] = AREA_STATIONS[i];
                allStcds.add(AREA_STATIONS[i][1]);
            }
            for (int i = 0; i < TABLE2_EXTRA_STATIONS.length; i++) {
                allStations[AREA_STATIONS.length + i] = TABLE2_EXTRA_STATIONS[i];
                allStcds.add(TABLE2_EXTRA_STATIONS[i][1]);
            }

            List<WJ_MODELSINGRESULTParam> predictions = bdmsPredictService.WJ_MODELSINGRESULT(solutionIds, allStcds,
                    "1");
            System.out.println("表2查询: 预测结果数(不过滤STCD)=" + (predictions != null ? predictions.size() : 0));
            if (predictions != null && !predictions.isEmpty()) {
                // 打印返回的实际STCD
                java.util.Set<String> stcds = new java.util.LinkedHashSet<>();
                for (WJ_MODELSINGRESULTParam p : predictions)
                    stcds.add(p.getSTCD());
                System.out.println("表2查询: 实际返回STCD=" + stcds);
            }
            if (predictions != null && !predictions.isEmpty()) {
                WJ_MODELSINGRESULTParam first = predictions.get(0);
                System.out.println("表2查询: 第一条 STCD=" + first.getSTCD() + " MAXZ=" + first.getMAXZ() + " MAXTM="
                        + first.getMAXTM());
            }

            // 按STCD建立索引
            Map<String, WJ_MODELSINGRESULTParam> predMap = new LinkedHashMap<>();
            if (predictions != null) {
                for (WJ_MODELSINGRESULTParam p : predictions) {
                    if (p.getSTCD() != null)
                        predMap.put(p.getSTCD(), p);
                }
            }

            for (int i = 0; i < allStations.length; i++) {
                WJ_MODELSINGRESULTParam p = predMap.get(allStations[i][1]);
                String[] row = new String[7];
                row[0] = String.valueOf(i + 1); // 序号
                row[1] = i < TABLE2_AREA_NAMES.length ? TABLE2_AREA_NAMES[i] : ""; // 片名
                row[2] = allStations[i][0]; // 站名
                row[3] = p != null && p.getMAXZ() != null ? p.getMAXZ() : ""; // 预测最高水位
                row[4] = p != null && p.getMAXTM() != null ? stripYear(p.getMAXTM()) : ""; // 出现时间(去年份)
                row[5] = p != null && p.getWRZ() != null ? df2.format(p.getWRZ()) : ""; // 警戒水位
                row[6] = p != null && p.getXZDZ() != null ? p.getXZDZ() : ""; // 历史最高(String)
                rows.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rows;
    }

    // ===================== 文字生成 =====================

    private String buildSummaryText(String typhoonName, TrackPoint latestObs, List<ForecastPoint> forecast) {
        if (latestObs == null)
            return "";
        return "";
    }

    private String buildDisclaimer(String typhoonName) {
        return "后续，水文总站将根据气象部门提供的最新风雨影响预报，及时加强"
                + "会商研判，滚动报告水位预报的最新情况。";
    }

    private String buildTable1Caption(ReportRequest params) {
        String stime = params.getStime();
        if (stime == null || stime.isEmpty())
            stime = new SimpleDateFormat("yyyy-MM-dd HH:00:00").format(new Date());
        try {
            SimpleDateFormat in = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date d = in.parse(stime);
            SimpleDateFormat out = new SimpleDateFormat("M月d日HH:mm");
            return out.format(d);
        } catch (Exception e) {
            return stime;
        }
    }

    private String buildWaterStatusText(ReportRequest params) {
        String stime = params.getStime() != null ? params.getStime()
                : new SimpleDateFormat("yyyy-MM-dd HH:00:00").format(new Date());
        return "";
    }

    private String buildWaterPredictionText(ReportRequest params) {
        StringBuilder sb = new StringBuilder();
        if (params.getDdId() != null && !params.getDdId().isEmpty()) {
            try {
                List<swzzmodeserver.workserver.pojo.swzzmode.DD_SOLUTIONPojo> solutions = ddSolutionData
                        .selectListByDDID(Collections.singletonList(params.getDdId()), null, null);
                if (solutions != null && !solutions.isEmpty()) {
                    String ddTm = solutions.get(0).getDD_TM();
                    String ddTmE = solutions.get(0).getDD_CHECKBY();
                    if (ddTm != null && ddTm.length() >= 16) {
                        try {
                            Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(ddTm);
                            Date dE = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(ddTmE);
                            sb.append("预报时间为：").append(new SimpleDateFormat("M月d日H时").format(d) + "~"
                                    + new SimpleDateFormat("M月d日H时").format(dE)).append("，");
                        } catch (Exception e) {
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        sb.append("预报降水按照上海市气象台网格降水预报，过程雨量为         ，")
                .append("期间水利闸泵工程按照         进行调度。");
        return sb.toString();
    }

    /** 预报结论: "据7月10日15时中央气象台台风预报路径..." */
    private String buildForecastConclusion(TrackPoint latestObs) {
        String timeStr = "";
        if (latestObs != null && nmcTyphoonService.getObsTimeString(latestObs) != null) {
            try {
                Date d = new SimpleDateFormat("yyyy年MM月dd日HH时mm分").parse(nmcTyphoonService.getObsTimeString(latestObs));
                timeStr = new SimpleDateFormat("M月d日H时").format(d);
            } catch (Exception e) {
            }
        }
        return "据" + timeStr + "中央气象台台风预报路径，综合历史台风增水经验和数值模型预报，"
                + "黄浦江中上游及个别水利片可能         警戒水位。各代表站点预报时段水位特征值见表2。";
    }

    // ===================== 水位过程图 =====================

    private String generateWaterLevelChart(ReportRequest params) {
        try {
            Date now = new Date();
            String stime = params.getStime() != null ? params.getStime()
                    : new SimpleDateFormat("yyyy-MM-dd HH:00:00").format(new Date(now.getTime() - 3L * 86400000L));
            String etime = params.getEtime() != null ? params.getEtime()
                    : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now);
            String chartDir = templateFilePath + File.separator + "water_situation" + File.separator + "temp";
            new File(chartDir).mkdirs();

            List<String> stcdList = new ArrayList<>();
            for (String[] st : AREA_STATIONS)
                stcdList.add(st[1]);

            List<GetWaterViewNewPojo> waterList = getWaterViewNewServer.selectListByHisIsTime(stcdList, stime, etime,
                    null);
            Map<String, List<Map<String, Object>>> stationData = new LinkedHashMap<>();

            for (GetWaterViewNewPojo w : waterList) {
                stationData.computeIfAbsent(w.getSTCD(), k -> new ArrayList<>());
                Map<String, Object> item = new HashMap<>();
                item.put("tm", w.getTM());
                item.put("upz", w.getUPZ() != null ? Double.parseDouble(w.getUPZ()) : 0);
                stationData.get(w.getSTCD()).add(item);
            }

            // 7站合一图表
            Map<String, List<Map<String, Object>>> multiData = new LinkedHashMap<>();
            for (String[] st : AREA_STATIONS) {
                List<Map<String, Object>> d = stationData.get(st[1]);
                if (d != null && !d.isEmpty())
                    multiData.put(st[0], d);
            }
            if (!multiData.isEmpty()) {
                return SqkbChartUtils.generateMultiWaterLevelChart(multiData, stime, etime, chartDir);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // ===================== 台风图片（复用） =====================

    private String findOrFetchTyphoonImage(ReportRequest params, String resolvedCode, String resolvedName) {
        String imagePath = params.getTyphoonImagePath();
        if (imagePath == null || imagePath.isEmpty()) {
            String typhoonDir = templateFilePath + File.separator + "typhoon" + File.separator;
            imagePath = findTyphoonImage(typhoonDir, resolvedCode, resolvedName);
            if (imagePath == null && resolvedCode != null) {
                try {
                    String imgUrl = nmcTyphoonService.fetchTrackImageUrl(resolvedCode);
                    if (imgUrl != null) {
                        imagePath = nmcTyphoonService.downloadTrackImage(imgUrl, typhoonDir, resolvedCode);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return imagePath;
    }

    private String formatObsTimeForCaption(String chineseTime) {
        if (chineseTime == null)
            return "";
        try {
            SimpleDateFormat in = new SimpleDateFormat("yyyy年MM月dd日HH时mm分", Locale.CHINA);
            Date d = in.parse(chineseTime);
            return new SimpleDateFormat("M月d日H时", Locale.CHINA).format(d);
        } catch (Exception e) {
            return "";
        }
    }

    private String findTyphoonImage(String typhoonDir, String typhoonCode, String typhoonName) {
        File dir = new File(typhoonDir);
        if (!dir.exists() || !dir.isDirectory())
            return null;
        String[] exts = { ".jpg", ".png", ".jpeg" };
        for (String prefix : new String[] { typhoonCode + typhoonName, typhoonCode }) {
            if (prefix == null)
                continue;
            for (String ext : exts) {
                File f = new File(dir, prefix + ext);
                if (f.exists())
                    return f.getAbsolutePath();
            }
        }
        return null;
    }

    // ===================== 文件名 =====================

    @Override
    public String buildFileName(ReportRequest params, int qihao, ReportData data) {
        String typhoonName = params.getTyphoonName() != null ? params.getTyphoonName() : "";
        String year = new SimpleDateFormat("yyyy").format(new Date());
        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String qihaoCn = qihaoToChinese(qihao);
        return "第" + qihaoCn + "期" + year + "年分片水情预报专报-" + typhoonName + "台风（第" + qihao + "期）" + date + ".docx";
    }

    /** "2026/7/12 6:10" → "7/12 6:10" */
    private String stripYear(String timeStr) {
        if (timeStr == null)
            return "";
        try {
            String[] fmts = { "yyyy/M/d H:mm", "yyyy-MM-dd HH:mm", "yyyy/M/d HH:mm", "yyyy-MM-dd H:mm" };
            for (String fmt : fmts) {
                try {
                    Date d = new java.text.SimpleDateFormat(fmt).parse(timeStr);
                    return new java.text.SimpleDateFormat("M/d H:mm").format(d);
                } catch (Exception ignored) {
                }
            }
        } catch (Exception e) {
        }
        return timeStr.replaceFirst("^\\d{4}[-/]", "").replace("-", "/");
    }

    private static String qihaoToChinese(int n) {
        String[] digits = { "", "一", "二", "三", "四", "五", "六", "七", "八", "九" };
        if (n < 1)
            return String.valueOf(n);
        if (n <= 9)
            return digits[n];
        if (n == 10)
            return "十";
        if (n < 20)
            return "十" + (n % 10 == 0 ? "" : digits[n % 10]);
        return String.valueOf(n);
    }
}
