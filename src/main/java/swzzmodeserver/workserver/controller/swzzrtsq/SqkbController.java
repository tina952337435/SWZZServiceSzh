package swzzmodeserver.workserver.controller.swzzrtsq;

import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swzzmodeserver.tools.ColumnName;
import swzzmodeserver.tools.ResultUtils;
import swzzmodeserver.workserver.pojo.swzzrtsq.SqkbReportPojo;
import swzzmodeserver.workserver.server.swzzrtsq.SqkbServer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequestMapping("/SWZZ_RTSQ_sqkb")
public class SqkbController {

    @Autowired
    private SqkbServer sqkbServer;

    /**
     * 获取水情快报数据（供前端微调）
     * @param param 包含 stime, etime 等参数
     * @return 前端编辑所需的数据
     */
    @RequestMapping("/getReportData")
    public ResultUtils<SqkbReportPojo> getReportData(@RequestBody ColumnName param) {
        StopWatch watch = new StopWatch();
        watch.start();

        String stime = param.getStime();
        String etime = param.getEtime();

        if (stime == null || etime == null) {
            return new ResultUtils<>(null, "时间参数必传", false, 0, 0);
        }

        ResultUtils<SqkbReportPojo> result = sqkbServer.getReportData(stime, etime);
        watch.stop();
        result.setElapseTime(watch.getTime());
        return result;
    }

    /**
     * 根据前端编辑的数据生成Word文档并下载
     * @param reportData 前端编辑后的数据
     */
    @RequestMapping("/generateWord")
    public void generateWord(@RequestBody SqkbReportPojo reportData, HttpServletResponse response) {
        sqkbServer.generateWordDownload(reportData, response);
    }

    /**
     * 根据前端编辑的数据生成Word文档，返回文件路径
     * @param reportData 前端编辑后的数据
     * @return 文件路径
     */
    @RequestMapping("/generateWordPath")
    public ResultUtils<Map<String, String>> generateWordPath(@RequestBody SqkbReportPojo reportData) {
        StopWatch watch = new StopWatch();
        watch.start();

        ResultUtils<Map<String, String>> result = sqkbServer.generateWordFromData(reportData);

        watch.stop();
        result.setElapseTime(watch.getTime());
        return result;
    }

    /**
     * 根据前端编辑的数据生成Word文档并预览（在线查看）
     * @param reportData 前端编辑后的数据
     * @param response HttpServletResponse
     */
    @RequestMapping("/previewWord")
    public void previewWord(@RequestBody SqkbReportPojo reportData, HttpServletResponse response) {
        sqkbServer.generateWordInline(reportData, response);
    }

    /**
     * 保存水情快报到服务器
     * @param reportData 前端编辑后的数据
     * @return 文件路径
     */
    @RequestMapping("/saveReport")
    public ResultUtils<Map<String, String>> saveReport(@RequestBody SqkbReportPojo reportData, HttpServletRequest request) {
        StopWatch watch = new StopWatch();
        watch.start();

        ResultUtils<Map<String, String>> result = sqkbServer.saveReportData(reportData, request);

        watch.stop();
        result.setElapseTime(watch.getTime());
        return result;
    }
}
