package swzzmodeserver.workserver.report.generator;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import swzzmodeserver.tools.DocxReplaceUtil;
import swzzmodeserver.workserver.data.swzzmode.ES_TIDALFORECASTData;
import swzzmodeserver.workserver.pojo.swzzmode.ES_TIDALFORECASTPojo;
import swzzmodeserver.workserver.report.AbstractReportGenerator;
import swzzmodeserver.workserver.report.ReportRequest;
import swzzmodeserver.workserver.report.data.NmcTyphoonService;
import swzzmodeserver.workserver.report.data.NmcTyphoonService.*;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.io.File;
import java.util.*;

/**
 * 风暴潮预报专报生成器
 *
 * 数据来源：
 * - 台风动态/预报分析 → NMC 国家气象中心网页爬取 (http://typhoon.nmc.cn)
 * - 潮位预报表 → 前端传入，或 ES_TIDALFORECAST 兜底
 */
@Component
public class StormSurgeForecastGenerator extends AbstractReportGenerator {

    @Autowired
    private ES_TIDALFORECASTData esTidalforecastData;

    @Autowired
    private NmcTyphoonService nmcTyphoonService;

    private static final DecimalFormat df2 = new DecimalFormat("0.00");

    @Override
    public String getReportType() {
        return "风暴潮预报专报";
    }

    @Override
    protected String getTemplateName() {
        return "storm_forecast.docx";
    }

    @Override
    protected String getOutputDirName() {
        return "storm_forecast";
    }

    @Override
    protected ReportData extractData(ReportRequest params) throws Exception {
        ReportData data = new ReportData();
        Date now = new Date();
        String currentYear = new SimpleDateFormat("yyyy").format(now);

        // 1. 从 NMC 爬取当前活跃的台风（无需前端传参）
        TyphoonDetail typhoonDetail = null;
        TyphoonSummary typhoonSummary = null;
        TrackPoint latestObs = null;
        List<ForecastPoint> cmaForecast = null;

        try {
            int year = Integer.parseInt(currentYear);

            List<TyphoonSummary> allTyphoons = nmcTyphoonService.getTyphoonList(year);
            // 优先0：前端传入台风ID
            if (typhoonSummary == null && params.getTyphoonId() != null) {
                typhoonSummary = nmcTyphoonService.findTyphoonById(allTyphoons, params.getTyphoonId());
            }
            // 优先1：前端传入台风编号/名称
            if (typhoonSummary == null && params.getTyphoonCode() != null) {
                typhoonSummary = nmcTyphoonService.findTyphoon(year, params.getTyphoonCode(), params.getTyphoonName());
            }
            // 优先2：找 probability-img 页面对应的台风（与网页一致）
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
            // 兜底：取有正式名称的活跃台风中 ID 最大的
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
            }
        } catch (Exception e) {
            System.err.println("NMC 台风数据爬取失败: " + e.getMessage());
            e.printStackTrace();
        }

        // 用爬取到的台风编号和名称覆盖（忽略前端传入的）
        String typhoonCode = typhoonSummary != null ? typhoonSummary.getCode1() : params.getTyphoonCode();
        String typhoonName = typhoonSummary != null ? typhoonSummary.getCnName() : params.getTyphoonName();
        // 回写 params，供 buildFileName 使用
        params.setTyphoonCode(typhoonCode);
        params.setTyphoonName(typhoonName);

        // 2. 确定台风名称
        String resolvedName = typhoonName;
        if ((resolvedName == null || resolvedName.isEmpty()) && typhoonSummary != null) {
            resolvedName = typhoonSummary.getCnName();
        }
        String resolvedCode = typhoonCode;
        if ((resolvedCode == null || resolvedCode.isEmpty()) && typhoonSummary != null) {
            resolvedCode = typhoonSummary.getCode1();
        }

        // 3. 构建报告标题
        String typhoonTitle = (resolvedCode != null ? resolvedCode : "") + "号台风"
                + (resolvedName != null ? "“" + resolvedName + "”" : "") + "风暴潮预报";
        data.setTitle(typhoonTitle);

        // 4. 报告期号和日期
        int qihao = calculateNextQihao(params);
        data.addPlaceholder("reportNo", currentYear + "年第" + qihao + "期");
        data.addPlaceholder("reportDate", new SimpleDateFormat("yyyy年M月d日").format(now));
        data.addPlaceholder("typhoonTitle", typhoonTitle);

        // 5. 台风动态描述（观测数据）
        String typhoonStatus = "";
        String typhoonMovement = "";
        if (typhoonDetail != null && latestObs != null) {
            typhoonStatus = nmcTyphoonService.buildTyphoonStatusText(typhoonDetail, latestObs, cmaForecast);
            typhoonMovement = nmcTyphoonService.buildTyphoonMovementText(typhoonDetail, latestObs, cmaForecast);
        } else {
            typhoonStatus = "（台风动态数据暂未获取，请手动填写）";
        }
        data.addPlaceholder("typhoonStatus", typhoonStatus);
        data.addPlaceholder("typhoonMovement", typhoonMovement);

        // 6. 预报分析描述
        String forecastAnalysis = buildForecastAnalysis(resolvedName, latestObs, cmaForecast);
        data.addPlaceholder("forecastAnalysis", forecastAnalysis);

        // 7. 结论摘要
        String summaryText = buildSummaryText(resolvedName, latestObs, cmaForecast);
        data.addPlaceholder("summaryText", summaryText);

        // 8. 不确定性说明
        String disclaimerText = "";
        data.addPlaceholder("disclaimerText", disclaimerText);

        // 8.5 预报人员（前端传入的当前登录用户）
        data.addPlaceholder("forecaster", params.getAuthor() != null ? params.getAuthor() : "");

        // 9. 台风路径图 — 从 NMC 爬取
        String imagePath = params.getTyphoonImagePath();
        System.out.println("图片: 前端传入=" + imagePath + ", resolvedCode=" + resolvedCode);
        if (imagePath == null || imagePath.isEmpty()) {
            String typhoonDir = templateFilePath + File.separator + "typhoon" + File.separator;
            // 先尝试本地匹配
            imagePath = findTyphoonImage(typhoonDir, resolvedCode, resolvedName);
            System.out.println("图片: 本地匹配=" + imagePath);
            // 本地没有则从 NMC 爬取
            if (imagePath == null && resolvedCode != null) {
                try {
                    String imgUrl = nmcTyphoonService.fetchTrackImageUrl(resolvedCode);
                    System.out.println("图片: NMC爬取URL=" + imgUrl);
                    if (imgUrl != null) {
                        imagePath = nmcTyphoonService.downloadTrackImage(imgUrl, typhoonDir, resolvedCode);
                        System.out.println("图片: 下载到本地=" + imagePath);
                    }
                } catch (Exception e) {
                    System.err.println("台风图片爬取失败: " + e.getMessage());
                }
            }
        }
        System.out.println("图片: 最终使用=" + imagePath);
        if (imagePath != null && !imagePath.isEmpty()) {
            // 图题格式: "图1 7月8日9时第9号台风"巴威"中央气象台路径预报图"
            String timeStr = "";
            if (latestObs != null && nmcTyphoonService.getObsTimeString(latestObs) != null) {
                timeStr = formatObsTimeForCaption(nmcTyphoonService.getObsTimeString(latestObs));
            }
            String numStr = "";
            if (resolvedCode != null && resolvedCode.length() >= 4) {
                numStr = String.valueOf(Integer.parseInt(resolvedCode.substring(2))); // 2609→9, 2613→13
            }
            String caption = "图1  " + timeStr + "第" + numStr + "号台风“"
                    + (resolvedName != null ? resolvedName : "") + "”中央气象台路径预报图";
            data.addImage("typhoonImage", imagePath, caption);
        }

        // 10. 潮位预报表数据
        // String[] stations = { "吴淞口", "芦潮港", "黄浦公园", "米市渡", "松浦大桥", "淀峰" };
        // String[] stcds = { "SW63401750", "SW63405800", "SW63401500", "SW63401100",
        // "SW63401900", "SW63402000" };
        // data.setTableData(buildTableData(stations, stcds, params));

        return data;
    }

    @Override
    protected void fillTemplate(XWPFDocument doc, ReportData data) throws Exception {
        DocxReplaceUtil.replaceText(doc, data.getPlaceholders());

        if (data.getImages() != null) {
            for (Map.Entry<String, ReportData.ImageInfo> entry : data.getImages().entrySet()) {
                ReportData.ImageInfo img = entry.getValue();
                DocxReplaceUtil.replaceImage(doc, entry.getKey(), img.getPath(), img.getCaption());
            }
        }

        if (data.getTableData() != null && !data.getTableData().isEmpty()) {
            DocxReplaceUtil.fillTable(doc, 0, data.getTableData(), 1);
        }
    }

    // ===================== 文本生成 =====================

    private String buildSummaryText(String typhoonName, TrackPoint latestObs,
            List<ForecastPoint> forecast) {
        StringBuilder sb = new StringBuilder();
        // sb.append("根据气象台台风预报路径、上海历史台风增水分析、数值预报模型预报和预报员经验，");

        // if (forecast != null && !forecast.isEmpty()) {
        // sb.append("预计黄浦江、苏州河、蕴藻浜干流增水主要集中在")
        // .append("11日夜间，增水幅度0.6米~1.0米，")
        // .append("黄浦江干流吴淞口可能出现达到蓝色警戒值的高潮位，")
        // .append("黄浦江上游可能出现达到蓝色警戒值的高潮位。");
        // } else {
        // sb.append("请关注后续滚动预报。");
        // }

        return sb.toString();
    }

    private String buildForecastAnalysis(String typhoonName, TrackPoint latestObs,
            List<ForecastPoint> forecast) {
        StringBuilder sb = new StringBuilder();
        sb.append("潮位预报结果见表1。");
        return sb.toString();
    }

    // ===================== 表格数据 =====================

    private List<String[]> buildTableData(String[] stations, String[] stcds, ReportRequest params) {
        // 优先使用前端传入的潮位预报数据
        java.util.List<java.util.List<String>> frontendData = params.getTideTableData();
        if (frontendData != null && !frontendData.isEmpty()) {
            List<String[]> tableData = new ArrayList<>();
            for (java.util.List<String> frontRow : frontendData) {
                String[] row = new String[6];
                for (int j = 0; j < Math.min(frontRow.size(), 6); j++) {
                    row[j] = frontRow.get(j) != null ? frontRow.get(j) : "";
                }
                for (int j = 0; j < 6; j++) {
                    if (row[j] == null)
                        row[j] = "";
                }
                tableData.add(row);
            }
            return tableData;
        }

        // 前端没传，尝试从数据库提取
        List<String[]> tableData = new ArrayList<>();
        for (int i = 0; i < stations.length; i++) {
            String[] row = new String[6];
            row[0] = stations[i];
            try {
                List<ES_TIDALFORECASTPojo> forecasts = esTidalforecastData.selectList(
                        stcds[i], null, params.getStime(), params.getEtime(),
                        null, null, null, null, null);
                if (forecasts != null && !forecasts.isEmpty()) {
                    ES_TIDALFORECASTPojo f = forecasts.get(0);
                    row[1] = formatForecastTime(f.getTM());
                    row[2] = f.getTDZ() != null ? df2.format(f.getTDZ()) : "";
                    if (f.getTDZ() != null && f.getZS() != null) {
                        row[3] = df2.format(f.getTDZ() + f.getZS() + 0.2);
                        row[4] = df2.format(f.getTDZ() + f.getZS() - 0.1);
                        row[5] = df2.format(f.getTDZ() - 0.1) + "~" + df2.format(f.getTDZ() + f.getZS() + 0.2);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            for (int j = 1; j < 6; j++) {
                if (row[j] == null || row[j].isEmpty())
                    row[j] = "";
            }
            tableData.add(row);
        }
        return tableData;
    }

    // ===================== 辅助方法 =====================

    private String formatForecastTime(String tm) {
        if (tm == null || tm.isEmpty())
            return "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = sdf.parse(tm);
            return new SimpleDateFormat("dd日HH:mm").format(date);
        } catch (Exception e) {
            return tm;
        }
    }

    private String findTyphoonImage(String typhoonDir, String typhoonCode, String typhoonName) {
        File dir = new File(typhoonDir);
        if (!dir.exists() || !dir.isDirectory())
            return null;

        String[] candidates;
        if (typhoonName != null && !typhoonName.isEmpty()) {
            candidates = new String[] {
                    typhoonCode + typhoonName + ".jpg",
                    typhoonCode + typhoonName + ".png",
                    typhoonCode + typhoonName + ".jpeg",
                    typhoonCode + ".jpg",
                    typhoonCode + ".png",
            };
        } else {
            candidates = new String[] {
                    typhoonCode + ".jpg",
                    typhoonCode + ".png",
            };
        }

        for (String name : candidates) {
            File file = new File(dir, name);
            if (file.exists())
                return file.getAbsolutePath();
        }
        return null;
    }

    @Override
    public String buildFileName(ReportRequest params, int qihao, ReportData data) {
        String typhoonName = params.getTyphoonName() != null ? params.getTyphoonName() : "";
        String year = new SimpleDateFormat("yyyy").format(new Date());
        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String qihaoCn = qihaoToChinese(qihao);
        return "第" + qihaoCn + "期" + year + "年风暴潮预报专报-" + typhoonName + "台风（" + qihaoCn + "）" + date + ".docx";
    }

    /** 1→一, 2→二, ... 11→十一 */
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
        return String.valueOf(n); // 超出范围直接返回数字
    }

    /** "2026年08月03日17时00分" → "8月3日17时" */
    private String formatObsTimeForCaption(String chineseTime) {
        if (chineseTime == null)
            return "";
        try {
            SimpleDateFormat in = new SimpleDateFormat("yyyy年MM月dd日HH时mm分", Locale.CHINA);
            Date d = in.parse(chineseTime);
            SimpleDateFormat out = new SimpleDateFormat("M月d日H时", Locale.CHINA);
            return out.format(d);
        } catch (Exception e) {
            return "";
        }
    }
}
