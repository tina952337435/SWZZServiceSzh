package swzzmodeserver.workserver.report.generator;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import swzzmodeserver.tools.DocxReplaceUtil;
import swzzmodeserver.workserver.pojo.swzzrtsq.GetWaterViewNewPojo;
import swzzmodeserver.workserver.report.AbstractReportGenerator;
import swzzmodeserver.workserver.report.ReportRequest;
import swzzmodeserver.workserver.server.swzzrtsq.GetWaterViewNewServer;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class SzhWaterLevelGenerator extends AbstractReportGenerator {

    @Autowired
    private GetWaterViewNewServer getWaterViewNewServer;

    @Autowired
    private swzzmodeserver.workserver.data.swzzmode.BDMS_PREDICTData bdmsPredictData;

    // 温州路, 北新泾
    private static final String[] STATION_NAMES = { "温州路", "北新泾" };
    private static final String[] STCDS = { "63405250", "63405150" };
    private static final DecimalFormat df2 = new DecimalFormat("0.00");

    @Override
    public String getReportType() {
        return "苏州河水位预测";
    }

    @Override
    protected String getTemplateName() {
        return "szh_water_level.docx";
    }

    @Override
    protected String getOutputDirName() {
        return "szh_water";
    }

    @Override
    protected boolean isSaveRecord() {
        return false;
    }

    private ReportRequest currentParams;

    @Override
    protected ReportData extractData(ReportRequest params) throws Exception {
        this.currentParams = params;
        ReportData data = new ReportData();
        Date now = new Date();
        String stime = params.getStime() != null ? params.getStime()
                : new SimpleDateFormat("yyyy-MM-dd HH:00:00").format(now);

        int[] offsets = { 0, 3, 6, 9, 12 };
        Calendar cal = Calendar.getInstance();
        cal.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(stime));

        // 从 BDMS_PREDICT 查询预报水位
        Map<String, Map<String, Float>> stationTimeData = new LinkedHashMap<>(); // STCD → (TM → DATA)
        if (params.getDdId() != null && !params.getDdId().isEmpty()) {
            try {
                List<String> planList = Collections.singletonList(params.getDdId());
                Calendar c = Calendar.getInstance();
                c.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(stime));
                String queryStime = new SimpleDateFormat("yyyy-MM-dd HH:00:00").format(c.getTime());
                c.add(Calendar.HOUR, 12);
                String queryEtime = new SimpleDateFormat("yyyy-MM-dd HH:59:59").format(c.getTime());
                List<swzzmodeserver.workserver.pojo.swzzmode.BDMS_PREDICTPojo> list = bdmsPredictData
                        .selectList("", queryStime, queryEtime, planList, null, "1", null, null, "", null);
                if (list != null) {
                    for (swzzmodeserver.workserver.pojo.swzzmode.BDMS_PREDICTPojo p : list) {
                        if (p.getSTCD() != null && p.getDATA() != null) {
                            stationTimeData.computeIfAbsent(p.getSTCD(), k -> new LinkedHashMap<>())
                                    .put(p.getYMDHM(), p.getDATA());
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 构建表格：5个时间点，水位用预测值
        List<String[]> tableData = new ArrayList<>();
        for (int offset : offsets) {
            cal.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(stime));
            cal.add(Calendar.HOUR, offset);
            String targetTime = new SimpleDateFormat("M/d H:mm").format(cal.getTime());

            String targetTimeFull = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.getTime());
            String[] row = new String[3];
            row[0] = targetTime;
            for (int s = 0; s < STATION_NAMES.length; s++) {
                Map<String, Float> tData = stationTimeData.get(STCDS[s]);
                String val = "";
                if (tData != null) {
                    // 匹配时间（可能格式不完全一致，模糊匹配）
                    for (Map.Entry<String, Float> e : tData.entrySet()) {
                        if (e.getKey() != null && e.getKey().startsWith(targetTimeFull.substring(0, 13))) {
                            val = e.getValue() != null ? df2.format(e.getValue()) : "";
                            break;
                        }
                    }
                    if (val.isEmpty())
                        val = tData.get(targetTimeFull) != null
                                ? df2.format(tData.get(targetTimeFull))
                                : "";
                }
                row[s + 1] = val;
            }
            tableData.add(row);
        }
        data.setTableData(tableData);

        String text = buildSummaryText(stime, tableData);
        data.addPlaceholder("summaryText", text);
        data.addPlaceholder("reportDate", new SimpleDateFormat("yyyy年M月d日H时").format(now));

        return data;
    }

    private String buildSummaryText(String stime, List<String[]> tableData) {
        StringBuilder sb = new StringBuilder();
        sb.append("根据上海市气象台降水预报及当前工情，市政泵站按放江模型预测计算，");
        for (String[] row : tableData) {
            if (row[1] != null && !row[1].isEmpty() && row[2] != null && !row[2].isEmpty()) {
                sb.append(row[0]).append("温州路").append(row[1]).append("米，")
                        .append("北新泾").append(row[2]).append("米，");
            }
        }
        return sb.toString();
    }

    @Override
    protected void fillTemplate(XWPFDocument doc, ReportData data) throws Exception {
        // 先找 $summaryText$ 段落（替换前）
        XWPFParagraph targetP = null;
        for (XWPFParagraph pp : doc.getParagraphs()) {
            if (pp.getText().contains("$summaryText$")) { targetP = pp; break; }
        }
        // 替换其他占位符
        data.getPlaceholders().remove("summaryText");
        DocxReplaceUtil.replaceText(doc, data.getPlaceholders());

        // 清空 $summaryText$ 段落，蓝黑混排重建
        if (targetP != null) {
            XWPFParagraph p = targetP;
            // 保存原始run属性（字体、大小等）
            // 提取模板原始run属性（removeRun后原run会断开，必须先读值）
            String _font = null;
            int _size = 12;
            boolean _bold = false;
            List<XWPFRun> oldRuns = p.getRuns();
            if (oldRuns != null && oldRuns.size() > 0) {
                for (XWPFRun r : oldRuns) {
                    if (r.getText(0) != null && !r.getText(0).isEmpty()) {
                        _font = r.getFontFamily();
                        _size = r.getFontSize();
                        _bold = r.isBold();
                        break;
                    }
                }
            }
            final String fnt = _font;
            final int fsz = _size;
            final boolean bld = _bold;
            while (p.getRuns().size() > 0)
                p.removeRun(0);
            List<String[]> td = data.getTableData();

            java.util.function.Consumer<String> black = t -> {
                XWPFRun r = p.createRun();
                r.setText(t);
                r.setColor("000000");
                if (fnt != null)
                    r.setFontFamily(fnt);
                r.setFontSize(fsz);
                r.setBold(bld);
            };
            java.util.function.Consumer<String> blue = t -> {
                XWPFRun r = p.createRun();
                r.setText(t);
                r.setColor("0000F7");
                if (fnt != null)
                    r.setFontFamily(fnt);
                r.setFontSize(fsz);
                r.setBold(bld);
            };

            black.accept("根据上海市气象台降水预报及当前工情，市政泵站按放江模型预测计算，");
            if (td != null) {
                for (String[] row : td) {
                    if (row[1] != null && !row[1].isEmpty() && row[2] != null && !row[2].isEmpty()) {
                        // row[0]格式 "8/9 8:00" → 拆为 day, time
                        String t = row[0];
                        String[] parts = t.split(" ");
                        String day = parts[0].contains("/") ? parts[0].split("/")[1] : parts[0];
                        String time = parts.length > 1 ? parts[1] : "";
                        // 9日8:00时温州路（3.00米）
                        blue.accept(day);
                        black.accept("日");
                        blue.accept(time);
                        black.accept("时");
                        black.accept("温州路（");
                        blue.accept(row[1]);
                        black.accept("米），");
                        // 北新泾（3.00米）
                        black.accept("北新泾（");
                        blue.accept(row[2]);
                        black.accept("米）。");
                    }
                }
            }
        }

        if (data.getTableData() != null && !data.getTableData().isEmpty()) {
            DocxReplaceUtil.fillTable(doc, 0, data.getTableData(), 1);
            // 强制表内字体设为宋体
            for (XWPFTableRow row : doc.getTables().get(0).getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph cp : cell.getParagraphs()) {
                        for (XWPFRun cr : cp.getRuns()) {
                            cr.setFontFamily("宋体");
                        }
                    }
                }
            }
        }
    }

    private void copyRunStyle(XWPFRun from, XWPFRun to) {
        if (from.getFontFamily() != null)
            to.setFontFamily(from.getFontFamily());
        if (from.getFontSize() > 0)
            to.setFontSize(from.getFontSize());
        if (from.isBold())
            to.setBold(true);
        if (from.isItalic())
            to.setItalic(true);
    }

    @Override
    public String buildFileName(ReportRequest params, int qihao, ReportData data) {
        String date = new SimpleDateFormat("yyyyMMddHHmm").format(new Date());
        return "苏州河水位预测_" + date + ".docx";
    }
}
