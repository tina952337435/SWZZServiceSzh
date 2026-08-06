package swzzmodeserver.workserver.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 报表生成结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportResult {

    /** XQKB_LIST 记录ID（确认保存后才有值，草稿阶段为 null） */
    private String xqkbId;

    /** 草稿标识（generateDraft 返回，用于 confirmDraft 和文件访问） */
    private String draftKey;

    /** 生成的 DOCX 文件名 */
    private String fileName;

    /** DOCX 文件完整路径 */
    private String docxPath;

    /** PDF 文件完整路径（确认保存后才有值） */
    private String pdfPath;

    /** 期号（确认保存后才有值） */
    private Integer qihao;

    /** 报表类型 */
    private String reportType;

    /** 提取到的报表数据（占位符+表格+图片等，供前端展示） */
    private Object content;
}
