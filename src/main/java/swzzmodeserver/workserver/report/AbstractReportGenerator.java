package swzzmodeserver.workserver.report;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import swzzmodeserver.tools.PdfUtil;
import swzzmodeserver.workserver.data.swzzflood.XQKB_LISTData;
import swzzmodeserver.workserver.pojo.swzzflood.XQKB_LISTPojo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * 报表生成器抽象基类
 *
 * 两阶段流程：
 *   1. generateDraft()  → 仅生成 DOCX 初稿（不存库，不生成 PDF）
 *   2. confirmDraft()   → 客户确认后：定稿文件名 → 存 XQKB_LIST → 生成 PDF
 */
public abstract class AbstractReportGenerator implements IReportGenerator {

    @Autowired
    protected XQKB_LISTData xqkbListData;

    @Value("${file.path.templatefilepath}")
    protected String templateFilePath;

    /** 模板文件名（相对于 MB 目录） */
    protected abstract String getTemplateName();

    /** 输出子目录名（相对于 templateFilePath） */
    protected abstract String getOutputDirName();

    /** 提取报表数据 */
    protected abstract ReportData extractData(ReportRequest params) throws Exception;

    /** 填充模板 */
    protected abstract void fillTemplate(XWPFDocument doc, ReportData data) throws Exception;

    /** 是否入库 XQKB_LIST，默认true。分片水情预报等不存档的覆盖返回false */
    protected boolean isSaveRecord() { return true; }

    // ===================== 第一阶段：生成初稿 =====================

    @Override
    public ReportResult generateDraft(ReportRequest params) throws Exception {
        // 1. 提取数据
        ReportData data = extractData(params);

        // 2. 加载模板
        String templatePath = templateFilePath + File.separator + "MB" + File.separator + getTemplateName();
        File templateFile = new File(templatePath);
        if (!templateFile.exists()) {
            throw new RuntimeException("模板文件不存在: " + templatePath);
        }
        // 模板内嵌字体压缩比极高，调低 POI 防 zip bomb 检测阈值
        org.apache.poi.openxml4j.util.ZipSecureFile.setMinInflateRatio(0.001);
        XWPFDocument doc = new XWPFDocument(new FileInputStream(templateFile));

        // 3. 填充模板
        fillTemplate(doc, data);

        // 4. 保存 DOCX
        int qihao = calculateNextQihao(params);
        String outputDir = templateFilePath + File.separator + getOutputDirName() + File.separator;
        File dir = new File(outputDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String draftKey = UUID.randomUUID().toString();
        String fileName = buildFileName(params, qihao, data);
        String docxPath = outputDir + fileName;
        try (FileOutputStream out = new FileOutputStream(docxPath)) {
            doc.write(out);
        }
        doc.close();

        // 5. 生成 PDF
        String pdfPath = docxPath.replace(".docx", ".pdf");
        PdfUtil.doc2pdf(docxPath, pdfPath);

        // 6. 返回结果（不存库，记录由 /saveRecord 接口单独保存）
        ReportResult result = new ReportResult();
        result.setXqkbId(null);
        result.setFileName(fileName);
        String relativeDocx = getOutputDirName() + "/" + fileName;
        result.setDocxPath(relativeDocx);
        result.setPdfPath(relativeDocx.replace(".docx", ".pdf"));
        result.setQihao(qihao);
        result.setReportType(params.getReportType() != null ? params.getReportType() : getReportType());
        result.setDraftKey(draftKey);
        // 返回提取的数据供前端展示
        java.util.Map<String, Object> content = new java.util.LinkedHashMap<>();
        content.put("title", data.getTitle());
        content.put("reportNo", data.getReportNo());
        content.put("reportDate", data.getReportDate());
        content.put("placeholders", data.getPlaceholders());
        content.put("tableData", data.getTableData());
        content.put("images", data.getImages());
        result.setContent(content);
        return result;
    }

    // ===================== 第二阶段：确认保存 =====================

    /**
     * 客户编辑完成后确认保存
     * 将草稿文件重命名为正式文件名 → 写入 XQKB_LIST → 生成 PDF
     *
     * @param draftKey 草稿标识（generateDraft 返回的）
     * @param params   报表参数（用于生成文件名和记录）
     * @return 最终结果（含 xqkbId、正式文件路径）
     */
    public ReportResult confirmDraft(String draftKey, ReportRequest params) throws Exception {
        String outputDir = templateFilePath + File.separator + getOutputDirName() + File.separator;
        String draftFileName = "draft_" + draftKey + ".docx";
        String draftPath = outputDir + draftFileName;

        File draftFile = new File(draftPath);
        if (!draftFile.exists()) {
            throw new RuntimeException("草稿文件不存在: " + draftPath);
        }

        // 1. 计算期号
        int qihao = calculateNextQihao(params);

        // 2. 定稿文件名
        String finalFileName = buildFileName(params, qihao, null);
        String finalDocxPath = outputDir + finalFileName;

        // 3. 重命名草稿 → 正式文件
        File finalFile = new File(finalDocxPath);
        if (finalFile.exists()) {
            finalFile.delete(); // 覆盖同名文件
        }
        if (!draftFile.renameTo(finalFile)) {
            // renameTo 跨分区可能失败，改用复制+删除
            java.nio.file.Files.copy(draftFile.toPath(), finalFile.toPath());
            draftFile.delete();
        }

        // 4. 生成 PDF
        String pdfPath = finalDocxPath.replace(".docx", ".pdf");
        PdfUtil.doc2pdf(finalDocxPath, pdfPath);

        // 5. 写入 XQKB_LIST 记录（此时才是真正的"确认"）
        String xqkbId = UUID.randomUUID().toString();
        XQKB_LISTPojo record = new XQKB_LISTPojo();
        record.setXQKB_ID(xqkbId);
        record.setXQKB_TITLE(params.getTyphoonName() != null
                ? params.getTyphoonCode() + "号台风\"" + params.getTyphoonName() + "\"风暴潮预报" : "");
        record.setXQKB_TM(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        record.setXQKB_STM(params.getStime());
        record.setXQKB_ETM(params.getEtime());
        record.setXQKB_QIHAO(qihao);
        record.setXQKB_FILE(finalFileName);
        record.setXQKB_OWEN(params.getAuthor() != null ? params.getAuthor() : "");
        record.setXQKB_TYPE(params.getReportType() != null ? params.getReportType() : getReportType());
        record.setXQKB_DZMPIC(params.getTyphoonImagePath() != null ? params.getTyphoonImagePath() : "");
        xqkbListData.insertOne(record);

        // 6. 返回最终结果
        ReportResult result = new ReportResult();
        result.setXqkbId(null);
        result.setFileName(finalFileName);
        result.setDocxPath(finalDocxPath);
        result.setPdfPath(pdfPath);
        result.setQihao(qihao);
        result.setReportType(params.getReportType() != null ? params.getReportType() : getReportType());
        result.setDraftKey(null); // 已定稿，不再需要 draftKey
        return result;
    }

    /**
     * 计算下一期号：当年同类型报告最大期号 + 1
     */
    public int calculateNextQihao(ReportRequest params) {
        try {
            String currentYear = new SimpleDateFormat("yyyy").format(new Date());
            // 用前端传入的类型查，与入库一致
            String type = params.getReportType() != null ? params.getReportType() : getReportType();
            Integer maxQihao = xqkbListData.selectMaxQihao(type,
                    currentYear + "-01-01 00:00:00");
            return (maxQihao != null ? maxQihao : 0) + 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    /**
     * 构建定稿文件名，子类可覆盖
     */
    public String buildFileName(ReportRequest params, int qihao, ReportData data) {
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        return getReportType() + "_" + timestamp + ".docx";
    }

    // ===================== 报表数据封装类 =====================

    public static class ReportData {
        private String title;
        private String reportNo;
        private String reportDate;
        private java.util.Map<String, String> placeholders;
        private java.util.List<String[]> tableData;
        private java.util.Map<String, ImageInfo> images;

        public ReportData() {
            this.placeholders = new java.util.LinkedHashMap<>();
            this.tableData = new java.util.ArrayList<>();
            this.images = new java.util.LinkedHashMap<>();
        }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getReportNo() { return reportNo; }
        public void setReportNo(String reportNo) { this.reportNo = reportNo; }
        public String getReportDate() { return reportDate; }
        public void setReportDate(String reportDate) { this.reportDate = reportDate; }
        public java.util.Map<String, String> getPlaceholders() { return placeholders; }
        public void setPlaceholders(java.util.Map<String, String> placeholders) { this.placeholders = placeholders; }
        public java.util.List<String[]> getTableData() { return tableData; }
        public void setTableData(java.util.List<String[]> tableData) { this.tableData = tableData; }
        public java.util.Map<String, ImageInfo> getImages() { return images; }
        public void setImages(java.util.Map<String, ImageInfo> images) { this.images = images; }

        public void addPlaceholder(String key, String value) { this.placeholders.put(key, value); }
        public void addTableRow(String[] row) { this.tableData.add(row); }
        public void addImage(String placeholder, String path, String caption) {
            this.images.put(placeholder, new ImageInfo(path, caption));
        }

        public static class ImageInfo {
            private String path;
            private String caption;
            public ImageInfo() {}
            public ImageInfo(String path, String caption) { this.path = path; this.caption = caption; }
            public String getPath() { return path; }
            public void setPath(String path) { this.path = path; }
            public String getCaption() { return caption; }
            public void setCaption(String caption) { this.caption = caption; }
        }
    }
}
