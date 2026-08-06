package swzzmodeserver.workserver.report.generator;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import swzzmodeserver.tools.DocxReplaceUtil;
import swzzmodeserver.workserver.report.AbstractReportGenerator;
import swzzmodeserver.workserver.report.ReportRequest;
import swzzmodeserver.workserver.report.AbstractReportGenerator.ReportData;
import swzzmodeserver.workserver.report.data.NmcTyphoonService;
import swzzmodeserver.workserver.report.data.NmcTyphoonService.*;

import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class RollingForecastGenerator extends AbstractReportGenerator {

    @Autowired
    private NmcTyphoonService nmcTyphoonService;

    @Override
    public String getReportType() {
        return "6h风暴潮滚动预报单";
    }

    @Override
    protected String getTemplateName() {
        return "rolling_forecast.docx";
    }

    @Override
    protected String getOutputDirName() {
        return "rolling_forecast";
    }

    @Override
    protected boolean isSaveRecord() {
        return false;
    }

    @Override
    protected ReportData extractData(ReportRequest params) throws Exception {
        ReportData data = new ReportData();
        Date now = new Date();
        String currentYear = new SimpleDateFormat("yyyy").format(now);

        // 台风数据
        TyphoonDetail typhoonDetail = null;
        TyphoonSummary typhoonSummary = null;
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

        // 期号
        int qihao = calculateNextQihao(params);

        // 占位符
        data.setTitle(typhoonCode + "号台风\"" + typhoonName + "\"风暴潮滚动预报单");
        data.addPlaceholder("typhoonTitle", typhoonCode + "号台风\"" + typhoonName + "\"风暴潮滚动预报单");
        data.addPlaceholder("reportNo", currentYear + "-" + String.format("%02d", qihao));
        data.addPlaceholder("reportDate", new SimpleDateFormat("yyyy年M月d日H时").format(now));
        data.addPlaceholder("forecaster", params.getAuthor() != null ? params.getAuthor() : "");

        // 表格不填——客户在OnlyOffice里手动填
        return data;
    }

    @Override
    protected void fillTemplate(XWPFDocument doc, ReportData data) throws Exception {
        DocxReplaceUtil.replaceText(doc, data.getPlaceholders());
    }

    @Override
    public String buildFileName(ReportRequest params, int qihao, ReportData data) {
        String typhoonName = params.getTyphoonName() != null ? params.getTyphoonName() : "";
        String year = new SimpleDateFormat("yyyy").format(new Date());
        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String qihaoCn = qihaoToChinese(qihao);
        return "滚动预报单-第" + qihaoCn + "期-" + year + "年风暴潮滚动预报单-" + typhoonName + "台风-" + date + ".docx";
    }

    private static String qihaoToChinese(int n) {
        String[] d = {"", "一", "二", "三", "四", "五", "六", "七", "八", "九"};
        if (n < 1) return String.valueOf(n);
        if (n <= 9) return d[n];
        if (n == 10) return "十";
        if (n < 20) return "十" + d[n % 10];
        return String.valueOf(n);
    }
}
