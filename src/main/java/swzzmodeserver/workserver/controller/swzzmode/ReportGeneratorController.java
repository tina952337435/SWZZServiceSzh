package swzzmodeserver.workserver.controller.swzzmode;

import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import swzzmodeserver.tools.ResultUtils;
import swzzmodeserver.workserver.data.swzzflood.XQKB_LISTData;
import swzzmodeserver.workserver.pojo.swzzflood.XQKB_LISTPojo;
import swzzmodeserver.workserver.report.AbstractReportGenerator;
import swzzmodeserver.workserver.report.ReportRequest;
import swzzmodeserver.workserver.report.ReportResult;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.List;

@RestController
@RequestMapping("/SWZZ_REPORT_GENERATOR")
public class ReportGeneratorController {

    @Autowired
    private XQKB_LISTData xqkbListData;

    @Autowired
    private List<AbstractReportGenerator> generators;

    private AbstractReportGenerator getGenerator(String reportType) {
        for (AbstractReportGenerator g : generators) {
            if (reportType != null && reportType.contains(g.getReportType()))
                return g;
        }
        return generators.get(0);
    }

    @Value("${file.path.templatefilepath}")
    private String templateFilePath;

    /**
     * 生成初稿 — 后端提取数据生成 DOCX，不存库
     */
    @Autowired
    private swzzmodeserver.workserver.report.data.NmcTyphoonService nmcTyphoonService;

    /**
     * 获取当前活跃台风列表（供前端选择）
     */
    @GetMapping("/typhoonList")
    public ResultUtils<java.util.List<java.util.Map<String, String>>> typhoonList() {
        StopWatch watch = new StopWatch();
        watch.start();
        try {
            java.util.List<java.util.Map<String, String>> list = new java.util.ArrayList<>();
            int year = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
            for (swzzmodeserver.workserver.report.data.NmcTyphoonService.TyphoonSummary ts :
                    nmcTyphoonService.getTyphoonList(year)) {
                if ("start".equals(ts.getStatus()) && !"nameless".equals(ts.getCnName())) {
                    java.util.Map<String, String> m = new java.util.HashMap<>();
                    m.put("code", ts.getCode1());
                    m.put("name", ts.getCnName());
                    m.put("id", String.valueOf(ts.getId()));
                    list.add(m);
                }
            }
            watch.stop();
            return new ResultUtils<>(list, "操作成功", true, list.size(), watch.getTime());
        } catch (Exception e) {
            e.printStackTrace();
            watch.stop();
            return new ResultUtils<>(null, e.getMessage(), false);
        }
    }

    @PostMapping("/generateDraft")
    public ResultUtils<ReportResult> generateDraft(@RequestBody ReportRequest params) {
        StopWatch watch = new StopWatch();
        watch.start();
        try {
            ReportResult result = getGenerator(params.getReportType()).generateDraft(params);
            watch.stop();
            return new ResultUtils<>(result, "初稿生成成功", true, 1, watch.getTime());
        } catch (Exception e) {
            e.printStackTrace();
            watch.stop();
            return new ResultUtils<>(null, "生成失败: " + e.getMessage(), false);
        }
    }

    /**
     * 确认保存 — 草稿转正，写 XQKB_LIST + 生成 PDF
     */
    @PostMapping("/confirmDraft")
    public ResultUtils<ReportResult> confirmDraft(@RequestBody ReportRequest params) {
        StopWatch watch = new StopWatch();
        watch.start();
        try {
            if (params.getDraftKey() == null || params.getDraftKey().isEmpty()) {
                return new ResultUtils<>(null, "draftKey 不能为空", false);
            }
            ReportResult result = getGenerator(params.getReportType()).confirmDraft(params.getDraftKey(), params);
            watch.stop();
            return new ResultUtils<>(result, "保存成功", true, 1, watch.getTime());
        } catch (Exception e) {
            e.printStackTrace();
            watch.stop();
            return new ResultUtils<>(null, "保存失败: " + e.getMessage(), false);
        }
    }

    /**
     * 单独存记录 — 前端直接传 XQKB_LISTPojo 入库
     */
    @PostMapping("/saveRecord")
    public ResultUtils<Integer> saveRecord(@RequestBody swzzmodeserver.workserver.pojo.swzzflood.XQKB_LISTPojo pojo) {
        StopWatch watch = new StopWatch();
        watch.start();
        try {
            if (pojo.getXQKB_ID() == null || pojo.getXQKB_ID().isEmpty()) {
                pojo.setXQKB_ID(java.util.UUID.randomUUID().toString());
            }
            if (pojo.getXQKB_TM() == null || pojo.getXQKB_TM().isEmpty()) {
                pojo.setXQKB_TM(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
            }
            Integer result = xqkbListData.insertOne(pojo);
            watch.stop();
            return new ResultUtils<>(result, "保存成功", true, result, watch.getTime());
        } catch (Exception e) {
            e.printStackTrace();
            watch.stop();
            return new ResultUtils<>(null, "保存失败: " + e.getMessage(), false);
        }
    }

    /** 获取草稿文件，供 OnlyOffice 加载 */
    @GetMapping("/fileByDraft/{draftKey}")
    public void getFileByDraft(@PathVariable String draftKey, HttpServletResponse response) {
        String filePath = findDraftFile(draftKey);
        if (filePath == null) {
            response.setStatus(404);
            return;
        }
        streamFile(new File(filePath), response, false);
    }

    /** 获取正式文件 */
    @GetMapping("/file/{xqkbId}")
    public void getFile(@PathVariable String xqkbId, HttpServletResponse response) {
        XQKB_LISTPojo record = xqkbListData.selectOne(xqkbId);
        if (record == null || record.getXQKB_FILE() == null) {
            response.setStatus(404);
            return;
        }
        File file = new File(buildFilePath(record));
        if (!file.exists()) {
            response.setStatus(404);
            return;
        }
        streamFile(file, response, false);
    }

    /** 下载 DOCX */
    @GetMapping("/download/{xqkbId}")
    public void download(@PathVariable String xqkbId, HttpServletResponse response) {
        XQKB_LISTPojo record = xqkbListData.selectOne(xqkbId);
        if (record == null || record.getXQKB_FILE() == null) {
            response.setStatus(404);
            return;
        }
        File file = new File(buildFilePath(record));
        if (!file.exists()) {
            response.setStatus(404);
            return;
        }
        streamFile(file, response, true);
    }

    /** 下载 PDF */
    @GetMapping("/downloadPdf/{xqkbId}")
    public void downloadPdf(@PathVariable String xqkbId, HttpServletResponse response) {
        XQKB_LISTPojo record = xqkbListData.selectOne(xqkbId);
        if (record == null || record.getXQKB_FILE() == null) {
            response.setStatus(404);
            return;
        }
        File file = new File(buildFilePath(record).replace(".docx", ".pdf"));
        if (!file.exists()) {
            response.setStatus(404);
            return;
        }
        streamFile(file, response, true);
    }

    // ===================== 私有方法 =====================

    private String findDraftFile(String draftKey) {
        String[] dirs = { "storm_forecast", "monthly_water", "meiyu", "flood_season", "annual", "reports" };
        for (String dir : dirs) {
            File f = new File(templateFilePath + File.separator + dir + File.separator + "draft_" + draftKey + ".docx");
            if (f.exists())
                return f.getAbsolutePath();
        }
        return null;
    }

    private String buildFilePath(XQKB_LISTPojo record) {
        String subDir = "reports";
        String type = record.getXQKB_TYPE();
        if ("风暴潮预报专报".equals(type))
            subDir = "storm_forecast";
        else if ("水情月报".equals(type))
            subDir = "monthly_water";
        else if ("梅雨期报".equals(type))
            subDir = "meiyu";
        else if ("汛期报".equals(type))
            subDir = "flood_season";
        else if ("年报".equals(type))
            subDir = "annual";
        return templateFilePath + File.separator + subDir + File.separator + record.getXQKB_FILE();
    }

    private void streamFile(File file, HttpServletResponse response, boolean attachment) {
        try {
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition",
                    (attachment ? "attachment" : "inline") + ";filename=" + URLEncoder.encode(file.getName(), "UTF-8"));
            response.setContentType(attachment
                    ? "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                    : "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            try (FileInputStream fis = new FileInputStream(file);
                    OutputStream os = response.getOutputStream()) {
                byte[] buf = new byte[8192];
                int n;
                while ((n = fis.read(buf)) != -1)
                    os.write(buf, 0, n);
                os.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
