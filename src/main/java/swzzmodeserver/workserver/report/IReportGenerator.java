package swzzmodeserver.workserver.report;

/**
 * 报表生成器接口
 * 每种报表类型对应一个实现类
 */
public interface IReportGenerator {

    /**
     * 报表类型，对应 XQKB_LIST.XQKB_TYPE
     */
    String getReportType();

    /**
     * 生成报表初稿
     * @param params 请求参数
     * @return 生成结果（包含文件路径、记录ID等）
     */
    ReportResult generateDraft(ReportRequest params) throws Exception;
}
