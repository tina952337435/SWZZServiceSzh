package swzzmodeserver.workserver.controller.swzzmode;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import swzzmodeserver.tools.PdfUtil;
import swzzmodeserver.workserver.data.swzzflood.XQKB_LISTData;
import swzzmodeserver.workserver.pojo.swzzflood.XQKB_LISTPojo;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * OnlyOffice 在线编辑回调接口
 *
 * 支持两类文档的编辑保存：
 * - 草稿：key 格式 "draft_{draftKey}" → 覆盖草稿文件，不生成 PDF
 * - 正式报告：key 格式 "report_{xqkbId}" → 覆盖正式文件 + 重新生成 PDF
 */
@RestController
@RequestMapping("/SWZZ_ONLYOFFICE_CALLBACK")
public class OnlyOfficeCallbackController {

    @Autowired
    private XQKB_LISTData xqkbListData;

    @Value("${file.path.templatefilepath}")
    private String templateFilePath;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * OnlyOffice 保存回调
     */
    @PostMapping("/save")
    public Map<String, Object> onSave(@RequestBody String callbackJson) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", 0);

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> callbackData = objectMapper.readValue(callbackJson, Map.class);

            Integer status = (Integer) callbackData.get("status");
            String key = (String) callbackData.get("key");

            // status 2=用户关闭编辑器, 6=强制自动保存
            if ((status != null && (status == 2 || status == 6)) && key != null) {
                String downloadUrl = (String) callbackData.get("url");
                if (downloadUrl != null && !downloadUrl.isEmpty()) {
                    saveFile(key, downloadUrl);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", 1);
            response.put("message", e.getMessage());
        }

        return response;
    }

    /**
     * 根据 key 类型保存文件
     * "draft_{draftKey}" → 草稿文件
     * "report_{xqkbId}" → 正式报告文件
     */
    private void saveFile(String key, String downloadUrl) {
        try {
            if (key.startsWith("draft_")) {
                // 草稿文件：找到并覆盖
                String draftKey = key.substring(6); // 去掉 "draft_" 前缀
                String filePath = findDraftFile(draftKey);
                if (filePath != null) {
                    downloadToFile(downloadUrl, filePath);
                    System.out.println("OnlyOffice 草稿保存成功: " + filePath);
                }
            } else if (key.startsWith("report_")) {
                // 正式报告：查 XQKB_LIST → 覆盖 → 重新生成 PDF
                String xqkbId = key.substring(7);
                XQKB_LISTPojo record = xqkbListData.selectOne(xqkbId);
                if (record != null && record.getXQKB_FILE() != null) {
                    String subDir = getSubDirByType(record.getXQKB_TYPE());
                    String filePath = templateFilePath + File.separator + subDir
                            + File.separator + record.getXQKB_FILE();
                    downloadToFile(downloadUrl, filePath);
                    // 重新生成 PDF
                    String pdfPath = filePath.replace(".docx", ".pdf");
                    PdfUtil.doc2pdf(filePath, pdfPath);
                    System.out.println("OnlyOffice 正式报告保存成功: " + filePath);
                }
            } else {
                // 兼容：直接作为 xqkbId 处理
                XQKB_LISTPojo record = xqkbListData.selectOne(key);
                if (record != null && record.getXQKB_FILE() != null) {
                    String subDir = getSubDirByType(record.getXQKB_TYPE());
                    String filePath = templateFilePath + File.separator + subDir
                            + File.separator + record.getXQKB_FILE();
                    downloadToFile(downloadUrl, filePath);
                    String pdfPath = filePath.replace(".docx", ".pdf");
                    PdfUtil.doc2pdf(filePath, pdfPath);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** 在所有报表目录中查找草稿文件 */
    private String findDraftFile(String draftKey) {
        String[] dirs = { "storm_forecast", "monthly_water", "meiyu", "flood_season", "annual", "reports" };
        String fileName = "draft_" + draftKey + ".docx";
        for (String dir : dirs) {
            String path = templateFilePath + File.separator + dir + File.separator + fileName;
            if (new File(path).exists()) {
                return path;
            }
        }
        return null;
    }

    /** 下载 URL 内容并覆盖本地文件 */
    private void downloadToFile(String downloadUrl, String filePath) throws IOException {
        URL url = new URL(downloadUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(30000);
        conn.setReadTimeout(60000);

        File localFile = new File(filePath);
        File parentDir = localFile.getParentFile();
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (InputStream is = conn.getInputStream();
                FileOutputStream fos = new FileOutputStream(localFile)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
            fos.flush();
        }
        conn.disconnect();
    }

    private String getSubDirByType(String reportType) {
        if ("风暴潮预报专报".equals(reportType) || "3天风暴潮预报专报".equals(reportType) || "1天风暴潮预报专报".equals(reportType))
            return "storm_forecast";
        if ("水情月报".equals(reportType))
            return "monthly_water";
        if ("梅雨期报".equals(reportType))
            return "meiyu";
        if ("汛期报".equals(reportType))
            return "flood_season";
        if ("年报".equals(reportType))
            return "annual";
        return "reports";
    }
}
