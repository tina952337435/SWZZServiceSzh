package swzzmodeserver.workserver.server.swzzrtsq;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import swzzmodeserver.tools.DateUtil;
import swzzmodeserver.tools.ResultUtils;
import swzzmodeserver.tools.SqkbChartUtils;
import swzzmodeserver.workserver.data.swzzrtsq.RTSQST_PPTN_RData;
import swzzmodeserver.workserver.data.swzzrtsq.RTSQST_STBPRP_BData;
import swzzmodeserver.workserver.data.swzzrtsq.ST_STBPRP_B_QUData;
import swzzmodeserver.workserver.pojo.swzzrtsq.ST_PPTN_RPojo;
import swzzmodeserver.workserver.pojo.swzzrtsq.ST_STBPRP_BPojo;
import swzzmodeserver.workserver.pojo.swzzrtsq.ST_STBPRP_B_QUPojo;
import swzzmodeserver.workserver.pojo.swzzrtsq.SqkbReportPojo;
import swzzmodeserver.workserver.server.swzzrtsq.GetWaterViewNewServer;
import swzzmodeserver.workserver.pojo.swzzrtsq.GetWaterViewNewPojo;
import swzzmodeserver.workserver.data.swzzflood.XQKB_LISTData;
import swzzmodeserver.workserver.pojo.swzzflood.XQKB_LISTPojo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SqkbServer {

    @Value("${file.path.templatefilepath}")
    private String templateFilePath;

    @Autowired
    private RTSQST_PPTN_RData data;

    @Autowired
    private RTSQST_STBPRP_BData stationData;

    @Autowired
    private ST_STBPRP_B_QUData quData;

    @Autowired
    private GetWaterViewNewServer getWaterViewNewServer;

    @Autowired
    private XQKB_LISTData xqkbListData;

    @Value("${http.urlPath.dzxmTraeApi}")
    private String dzxmTraeApi;

    private static final DecimalFormat df = new DecimalFormat("0.0");

    /**
     * 获取水情快报数据（供前端微调）
     */
    public ResultUtils<SqkbReportPojo> getReportData(String stime, String etime) {
        try {
            // 1. 查询所有降雨数据
            List<ST_PPTN_RPojo> rainData = data.selectListByTime(null, stime, etime, null);

            // 2. 查询站点基础信息
            List<ST_STBPRP_BPojo> stationList = stationData.selectList(null, null);

            // 3. 计算统计数据
            SqkbReportPojo reportData = calculateReportData(rainData, stationList, stime, etime);

            // 4. 生成图表返回给前端
            generateChartsForFrontend(reportData);

            return new ResultUtils<>(reportData, "操作成功！", true);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultUtils<>(null, "获取水情快报数据失败：" + e.getMessage(), false);
        }
    }

    /**
     * 生成图表返回给前端
     */
    private void generateChartsForFrontend(SqkbReportPojo reportData) {
        try {
            // 图表保存路径：使用配置的templateFilePath下的sqkb目录
            String chartSavePath = templateFilePath + File.separator + "sqkb";
            File chartDir = new File(chartSavePath);
            if (!chartDir.exists()) {
                chartDir.mkdirs();
            }

            // 1. 生成雨量等级饼图
            Map<String, Integer> rainLevelMap = new LinkedHashMap<>();
            rainLevelMap.put("小雨(0.1-10mm)",
                    reportData.getCountLightRain() != null ? reportData.getCountLightRain() : 0);
            rainLevelMap.put("中雨(10-25mm)",
                    reportData.getCountMediumRain() != null ? reportData.getCountMediumRain() : 0);
            rainLevelMap.put("大雨(25-50mm)",
                    reportData.getCountHeavyRain() != null ? reportData.getCountHeavyRain() : 0);
            rainLevelMap.put("暴雨(50-100mm)",
                    reportData.getCountRainstorm() != null ? reportData.getCountRainstorm() : 0);
            rainLevelMap.put("大暴雨(100-200mm)",
                    reportData.getCountSevereRain() != null ? reportData.getCountSevereRain() : 0);
            rainLevelMap.put("特大暴雨(>=200mm)",
                    reportData.getCountExtremeRain() != null ? reportData.getCountExtremeRain() : 0);

            String pieChartPath = SqkbChartUtils.generateRainLevelPieChart(rainLevelMap, chartSavePath);
            if (pieChartPath != null) {
                reportData.setImage2Path(new File(pieChartPath).getName());
                String timeRangeStr = formatTimeRangeStr(reportData.getStime(), reportData.getEtime());
                reportData.setImage2PathTitle(timeRangeStr + "降水量级分布图");
            }

            // 2. 生成最大站降水量柱状图
            if (reportData.getHourlyRainList() != null && !reportData.getHourlyRainList().isEmpty()) {
                List<Map<String, Object>> hourlyDataList = new ArrayList<>();
                for (SqkbReportPojo.HourlyRainPojo hourly : reportData.getHourlyRainList()) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("tm", hourly.getTm());
                    item.put("drp", hourly.getDrp());
                    hourlyDataList.add(item);
                }

                String barChartPath = SqkbChartUtils.generateStationRainChart(hourlyDataList,
                        reportData.getMaxDrpStation(), chartSavePath);
                if (barChartPath != null) {
                    reportData.setImage4Path(new File(barChartPath).getName());
                    String dateStr = formatTimeRangeStr(reportData.getStime());
                    reportData.setImage4PathTitle(reportData.getMaxDrpStation() + dateStr + "降水量棒图"); // 单站降水量棒图名称
                }
            }

            // 3. 生成各区降水量柱状图
            if (reportData.getAreaRainMap() != null && !reportData.getAreaRainMap().isEmpty()) {
                String areaBarChartPath = SqkbChartUtils.generateAreaRainBarChart(reportData.getAreaRainMap(),
                        chartSavePath,reportData.getAvgDrp());
                if (areaBarChartPath != null) {
                    reportData.setImage3Path(new File(areaBarChartPath).getName());
                    reportData.setImage3PathTitle("各区降水量对照图");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 调用外部API获取上海市降水分布图，保存到本地并返回文件名
     * 
     * @param stationSumMap 站点累计雨量 Map<stcd, drp>
     * @param stationMap    站点基础信息 Map<stcd, stationInfo>
     * @param saveDir       图片保存目录
     * @return 文件名，失败返回null
     */
    private String fetchRainfallDistributionImage(Map<String, Double> stationSumMap,
            Map<String, ST_STBPRP_BPojo> stationMap,
            String saveDir) {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        FileOutputStream fos = null;
        try {
            // 1. 构建 stations 数组
            List<Map<String, Object>> stationList = new ArrayList<>();
            for (Map.Entry<String, Double> entry : stationSumMap.entrySet()) {
                String stcd = entry.getKey();
                Double drp = entry.getValue();                

                // 过滤：雨量小于0.1的不传
                // if (drp == null || drp < 0.1) {
                //     continue;
                // }

                ST_STBPRP_BPojo station = stationMap.get(stcd);
                if (station == null) {
                    continue;
                }

                // 过滤：经纬度为空的站点
                if (station.getLTTD() == null || station.getLGTD() == null) {
                    continue;
                }

                if(stcd.equals("63422650")){//边角需要插值的点
                    //RainfallStr.push({ lon:-58570.8187561 , lat:-14245.7293732, value: f }); 
                    
                    Map<String, Object> itemNew = new HashMap<>();
                    itemNew.put("lat", -14245.7293732);
                    itemNew.put("lon", -58570.8187561);
                    itemNew.put("value", String.valueOf(drp));
                    stationList.add(itemNew); 
                }
                if(stcd.equals("63422480")){
                    // RainfallStr.push({ lon:-45857.005352, lat:-45373.137903, value: f });   
                    
                    Map<String, Object> itemNew = new HashMap<>();
                    itemNew.put("lat", -45373.137903);
                    itemNew.put("lon", -45857.005352);
                    itemNew.put("value", String.valueOf(drp));
                    stationList.add(itemNew); 
                }
                if(stcd.equals("X1160101")){
                    // RainfallStr.push({ lon:-18971.869913,lat:-60205.822566, value: f }); 
                    
                    Map<String, Object> itemNew = new HashMap<>();
                    itemNew.put("lat", -60205.822566);
                    itemNew.put("lon", -18971.869913);
                    itemNew.put("value", String.valueOf(drp));
                    stationList.add(itemNew);
                }

                Map<String, Object> item = new HashMap<>();
                item.put("lat", station.getLTTD());
                item.put("lon", station.getLGTD());
                item.put("value", String.valueOf(drp));
                stationList.add(item);
            }

            if (stationList.isEmpty()) {
                return null;
            }

            // 2. 构建完整请求JSON
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("stations", stationList);
            requestBody.put("levels", Arrays.asList(0.01, 10, 25, 50, 100, 200));
            requestBody.put("colors", Arrays.asList("#A6F28E", "#007B00", "#3DBCF9", "#0000F9", "#FB3DFA", "#7B0000"));
            requestBody.put("interpolation_method", "trigonometric");
            requestBody.put("resolution", 1000);

            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            String jsonStr = mapper.writeValueAsString(requestBody);

            // 3. 发送POST请求
            httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(dzxmTraeApi + "/multi-regions/image");
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setEntity(new StringEntity(jsonStr, "UTF-8"));

            response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();

            if (entity == null || entity.getContent() == null) {
                return null;
            }

            // 4. 确保保存目录存在
            File dir = new File(saveDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 5. 保存图片到本地
            String fileName = "rain_distribution_" + System.currentTimeMillis() + ".png";
            String filePath = saveDir + File.separator + fileName;
            fos = new FileOutputStream(filePath);

            // 根据Content-Type判断格式，默认png
            byte[] buffer = new byte[8192];
            int bytesRead;
            java.io.InputStream inputStream = entity.getContent();
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
            fos.flush();

            return fileName;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (fos != null)
                    fos.close();
                if (response != null)
                    response.close();
                if (httpClient != null)
                    httpClient.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据前端编辑的数据生成Word文档并保存到服务器，返回文件路径
     */
    public ResultUtils<Map<String, String>> generateWordFromData(SqkbReportPojo reportData) {
        try {
            // 模板路径
            String templatePath = templateFilePath + File.separator + "MB" + File.separator + "sqkb.docx";
            File templateFile = new File(templatePath);

            if (!templateFile.exists()) {
                throw new RuntimeException("模板文件不存在: " + templatePath);
            }

            // 加载模板文档
            XWPFDocument document = new XWPFDocument(new FileInputStream(templateFile));

            // 获取图表保存路径
            String chartSavePath = templateFilePath + File.separator + "sqkb";
            File chartDir = new File(chartSavePath);
            if (!chartDir.exists()) {
                chartDir.mkdirs();
            }

            // 生成图表并替换文档内容
            replaceTemplateContent(document, reportData, chartSavePath);

            // 保存文件
            String dateStr = formatTimeRangeStr(reportData.getStime());
            String fileName = dateStr + "暴雨分析.docx";
            String savePath = templateFilePath + File.separator + "sqkb" + File.separator;
            File saveDir = new File(savePath);
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }
            String filePath = savePath + fileName;

            FileOutputStream out = new FileOutputStream(filePath);
            document.write(out);
            out.close();
            document.close();

            // 保存成功后，向XQKB_LIST表新增一条记录
            try {
                XQKB_LISTPojo xqkbPojo = new XQKB_LISTPojo();
                xqkbPojo.setXQKB_ID(UUID.randomUUID().toString()); // 主键
                xqkbPojo.setXQKB_TITLE(reportData.getTitle()); // 标题
                xqkbPojo.setXQKB_TM(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())); // 填报时间
                xqkbPojo.setXQKB_STM(reportData.getStime()); // 开始时间
                xqkbPojo.setXQKB_ETM(reportData.getEtime()); // 结束时间
                xqkbPojo.setXQKB_QIHAO(reportData.getQihao()); // 期号
                xqkbPojo.setXQKB_FILE(fileName); // 文件路径/文件名
                xqkbPojo.setXQKB_OWEN(reportData.getAuthor() != null ? reportData.getAuthor() : ""); // 编写人
                xqkbPojo.setXQKB_TYPE("水情快报"); // 类型
                xqkbListData.insertOne(xqkbPojo);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Map<String, String> result = new HashMap<>();
            result.put("filePath", fileName);
            result.put("fileName", fileName);

            return new ResultUtils<>(result, "生成Word文档成功！", true);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResultUtils<>(null, "生成Word文档失败：" + e.getMessage(), false);
        }
    }

    /**
     * 根据前端编辑的数据生成Word文档并下载（保留原有方法供下载使用）
     */
    public void generateWordDownload(SqkbReportPojo reportData, HttpServletResponse response) {
        try {
            // 模板路径
            String templatePath = templateFilePath + File.separator + "MB" + File.separator + "sqkb.docx";
            File templateFile = new File(templatePath);

            if (!templateFile.exists()) {
                throw new RuntimeException("模板文件不存在: " + templatePath);
            }

            // 加载模板文档
            XWPFDocument document = new XWPFDocument(new FileInputStream(templateFile));

            // 获取图表保存路径
            String chartSavePath = templateFilePath + File.separator + "sqkb";
            File chartDir = new File(chartSavePath);
            if (!chartDir.exists()) {
                chartDir.mkdirs();
            }

            // 生成图表并替换文档内容
            replaceTemplateContent(document, reportData, chartSavePath);

            String dateStr = formatTimeRangeStr(reportData.getStime());
            String fileName = dateStr + "暴雨分析.docx";
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");

            document.write(response.getOutputStream());
            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据前端编辑的数据生成Word文档并在线预览（Content-Disposition: inline）
     */
    public void generateWordInline(SqkbReportPojo reportData, HttpServletResponse response) {
        try {
            // 模板路径
            String templatePath = templateFilePath + File.separator + "MB" + File.separator + "sqkb.docx";
            File templateFile = new File(templatePath);

            if (!templateFile.exists()) {
                throw new RuntimeException("模板文件不存在: " + templatePath);
            }

            // 加载模板文档
            XWPFDocument document = new XWPFDocument(new FileInputStream(templateFile));

            // 获取图表保存路径
            String chartSavePath = templateFilePath + File.separator + "sqkb";
            File chartDir = new File(chartSavePath);
            if (!chartDir.exists()) {
                chartDir.mkdirs();
            }

            // 生成图表并替换文档内容
            replaceTemplateContent(document, reportData, chartSavePath);

            String dateStr = formatTimeRangeStr(reportData.getStime());
            String fileName = dateStr + "暴雨分析.docx";
            response.setCharacterEncoding("UTF-8");
            // inline 会尝试在浏览器中直接打开，attachment 会下载
            response.setHeader("Content-Disposition", "inline;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");

            document.write(response.getOutputStream());
            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 替换模板中的占位符内容
     */
    private void replaceTemplateContent(XWPFDocument document, SqkbReportPojo reportData, String chartSavePath) {
        String dateStr = formatTimeRangeStr(reportData.getStime());

        // 替换文本占位符
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            List<XWPFRun> runs = paragraph.getRuns();
            for (XWPFRun run : runs) {
                String text = run.getText(0);
                if (text == null)
                    continue;
                // 年份
                if (text.contains("$year$")) {
                    text = text.replace("$year$", reportData.getYear() != null ? reportData.getYear() : "");
                    run.setText(text, 0);
                }
                // 期号
                if (text.contains("$qihao$")) {
                    text = text.replace("$qihao$",
                            reportData.getQihao() != null ? reportData.getQihao().toString() : "");
                    run.setText(text, 0);
                }
                // 编写日期
                if (text.contains("$docDate$")) {
                    text = text.replace("$docDate$", reportData.getDocDate() != null ? reportData.getDocDate() : "");
                    run.setText(text, 0);
                }

                // 替换 $title$ - 日期标题
                if (text.contains("$title$")) {
                    text = text.replace("$title$", reportData.getTitle() != null ? reportData.getTitle() : "");
                    run.setText(text, 0);
                }

                // 替换 $overview$ - 概况段落
                if (text.contains("$overview$")) {
                    text = text.replace("$overview$", reportData.getOverview() != null ? reportData.getOverview() : "");
                    run.setText(text, 0);
                }

                // 替换 $analysisText$ - 分析第一点
                if (text.contains("$analysisText$")) {
                    text = text.replace("$analysisText$",
                            reportData.getAnalysisText() != null ? reportData.getAnalysisText() : "");
                    run.setText(text, 0);
                }

                // 替换 $analysisText2$ - 分析第二点
                if (text.contains("$analysisText2$")) {
                    text = text.replace("$analysisText2$",
                            reportData.getAnalysisText2() != null ? reportData.getAnalysisText2() : "");
                    run.setText(text, 0);
                }

                // 替换 $waterLevelDesc$ - 水位情况描述
                if (text.contains("$waterLevelDesc$")) {
                    text = text.replace("$waterLevelDesc$",
                            reportData.getWaterLevelDesc() != null ? reportData.getWaterLevelDesc() : "");
                    run.setText(text, 0);
                }

                // 替换 $author$ - 文档编写人
                if (text.contains("$author$")) {
                    text = text.replace("$author$", reportData.getAuthor() != null ? reportData.getAuthor() : "");
                    run.setText(text, 0);
                }
                // 替换 $reviewer$ - 文档校核人
                if (text.contains("$reviewer$")) {
                    text = text.replace("$reviewer$", reportData.getReviewer() != null ? reportData.getReviewer() : "");
                    run.setText(text, 0);
                }
                // 替换 $approver$ - 文档审核人
                if (text.contains("$approver$")) {
                    text = text.replace("$approver$", reportData.getApprover() != null ? reportData.getApprover() : "");
                    run.setText(text, 0);
                }
            }
        }

        // 处理图片占位符
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            String paraText = paragraph.getText();
            if (paraText == null)
                continue;

            String timeRangeStr = formatTimeRangeStr(reportData.getStime(), reportData.getEtime());
            // 处理 image1Path - 上海市降水分布图
            if (paraText.contains("$image1Path$")) {
                String chartPath = generateAndGetChartPath(reportData.getImage1Path(), "image1", reportData,
                        chartSavePath);
                replaceImagePlaceholder(paragraph, "$image1Path$", chartPath, "图1  " + timeRangeStr + "上海市降水分布图");
            }

            // 处理 image2Path - 降水量级分布图
            if (paraText.contains("$image2Path$")) {
                String chartPath = generateAndGetChartPath(reportData.getImage2Path(), "image2", reportData,
                        chartSavePath);
                replaceImagePlaceholder(paragraph, "$image2Path$", chartPath, "图2  " + timeRangeStr + "降水量级分布图");
            }

            // 处理 image3Path - 各区降水量对照图
            if (paraText.contains("$image3Path$")) {
                String chartPath = generateAndGetChartPath(reportData.getImage3Path(), "image3", reportData,
                        chartSavePath);
                replaceImagePlaceholder(paragraph, "$image3Path$", chartPath, "图3  各区降水量对照图");
            }

            // 处理 image4Path - 单站降水量棒图
            if (paraText.contains("$image4Path$")) {
                String chartPath = generateAndGetChartPath(reportData.getImage4Path(), "image4", reportData,
                        chartSavePath);
                replaceImagePlaceholder(paragraph, "$image4Path$", chartPath,
                        "图4  " + reportData.getMaxDrpStation() + dateStr + "降水量棒图");
            }

            // 处理 image5Path - 水位过程线图
            if (paraText.contains("$image5Path$")) {
                String chartPath = generateAndGetChartPath(reportData.getImage5Path(), "image5", reportData,
                        chartSavePath);
                replaceImagePlaceholder(paragraph, "$image5Path$", chartPath,
                        "图5  " + reportData.getMaxChangeStationNameSlp() + "水位过程线");
            }
        }
    }

    /**
     * 生成图表并返回路径
     */
    private String generateAndGetChartPath(String existingPath, String chartType, SqkbReportPojo reportData,
            String chartSavePath) {
        try {
            switch (chartType) {
                case "image1":
                    // 上海市降水分布图
                    if (reportData.getImage1Path() != null && !reportData.getImage1Path().isEmpty()) {
                        return chartSavePath + File.separator + reportData.getImage1Path();
                    } else {
                        return "";
                    }
                case "image2":
                    // 雨量等级饼图
                    if (reportData.getImage2Path() != null && !reportData.getImage2Path().isEmpty()) {
                        return chartSavePath + File.separator + reportData.getImage2Path();
                    } else {
                        Map<String, Integer> rainLevelMap = new LinkedHashMap<>();
                        rainLevelMap.put("小雨(0.1-10mm)",
                                reportData.getCountLightRain() != null ? reportData.getCountLightRain() : 0);
                        rainLevelMap.put("中雨(10-25mm)",
                                reportData.getCountMediumRain() != null ? reportData.getCountMediumRain() : 0);
                        rainLevelMap.put("大雨(25-50mm)",
                                reportData.getCountHeavyRain() != null ? reportData.getCountHeavyRain() : 0);
                        rainLevelMap.put("暴雨(50-100mm)",
                                reportData.getCountRainstorm() != null ? reportData.getCountRainstorm() : 0);
                        rainLevelMap.put("大暴雨(100-200mm)",
                                reportData.getCountSevereRain() != null ? reportData.getCountSevereRain() : 0);
                        rainLevelMap.put("特大暴雨(>=200mm)",
                                reportData.getCountExtremeRain() != null ? reportData.getCountExtremeRain() : 0);
                        String pieChartPath = SqkbChartUtils.generateRainLevelPieChart(rainLevelMap, chartSavePath);
                        return pieChartPath;
                    }
                case "image3":
                    // 各区降水量柱状图
                    if (reportData.getImage3Path() != null && !reportData.getImage3Path().isEmpty()) {
                        return chartSavePath + File.separator + reportData.getImage3Path();
                    } else {
                        if (reportData.getAreaRainMap() != null && !reportData.getAreaRainMap().isEmpty()) {
                            return SqkbChartUtils.generateAreaRainBarChart(reportData.getAreaRainMap(), chartSavePath,reportData.getAvgDrp());
                        }
                    }
                    break;

                case "image4":
                    // 单站降水量棒图
                    if (reportData.getImage4Path() != null && !reportData.getImage4Path().isEmpty()) {
                        return chartSavePath + File.separator + reportData.getImage4Path();
                    } else {
                        if (reportData.getHourlyRainList() != null && !reportData.getHourlyRainList().isEmpty()) {
                            List<Map<String, Object>> hourlyDataList = new ArrayList<>();
                            for (SqkbReportPojo.HourlyRainPojo hourly : reportData.getHourlyRainList()) {
                                Map<String, Object> item = new HashMap<>();
                                item.put("tm", hourly.getTm());
                                item.put("drp", hourly.getDrp());
                                hourlyDataList.add(item);
                            }
                            return SqkbChartUtils.generateStationRainChart(hourlyDataList,
                                    reportData.getMaxDrpStation(), chartSavePath);
                        }
                    }

                    break;

                case "image5":
                    // 水位过程线图（已在getReportData时生成）
                    if (reportData.getImage5Path() != null && !reportData.getImage5Path().isEmpty()) {
                        return chartSavePath + File.separator + reportData.getImage5Path();
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取最大水位变化站点名称
     */
    private String getMaxChangeStationName(SqkbReportPojo reportData) {
        // 从waterLevelDetail中提取站点名称，或使用默认值
        if (reportData.getWaterLevelDetail() != null && reportData.getWaterLevelDetail().contains("。")) {
            String detail = reportData.getWaterLevelDetail();
            // 提取 "xxx站" 格式的站点名
            int stationIdx = detail.indexOf("站");
            if (stationIdx > 0) {
                int startIdx = Math.max(0, stationIdx - 10);
                return detail.substring(startIdx, stationIdx + 1);
            }
        }
        return "曹家渡"; // 默认站点
    }

    /**
     * 替换图片占位符为实际图片
     */
    private void replaceImagePlaceholder(XWPFParagraph paragraph, String placeholder, String imagePath,
            String caption) {
        try {
            // 清除原有内容
            for (XWPFRun run : paragraph.getRuns()) {
                run.setText("", 0);
            }

            // 设置段落居中对齐
            paragraph.setAlignment(ParagraphAlignment.CENTER);

            if (imagePath != null && new File(imagePath).exists()) {
                // 插入图片
                XWPFRun picRun = paragraph.createRun();
                FileInputStream fis = new FileInputStream(new File(imagePath));
                byte[] imageBytes = new byte[(int) new File(imagePath).length()];
                fis.read(imageBytes);
                fis.close();
                picRun.addPicture(new java.io.ByteArrayInputStream(imageBytes), 6,
                        new File(imagePath).getName(), 400 * 9525, 250 * 9525);

                // 添加换行和图片说明（使用\r\n换行）
                XWPFRun breakRun = paragraph.createRun();
                breakRun.setText("\r\n");

                XWPFRun captionRun = paragraph.createRun();
                captionRun.setText(caption);
                captionRun.setFontSize(12);
                captionRun.setFontFamily("仿宋_GB2312");
            } else {
                // 图片不存在，使用占位符文本
                XWPFRun placeholderRun = paragraph.createRun();
                placeholderRun.setText("[" + caption + "]");
                placeholderRun.setFontSize(12);
                placeholderRun.setFontFamily("仿宋_GB2312");
            }
        } catch (Exception e) {
            e.printStackTrace();
            XWPFRun errorRun = paragraph.createRun();
            errorRun.setText("[图片插入失败: " + caption + "]");
            errorRun.setFontSize(12);
            errorRun.setFontFamily("仿宋_GB2312");
        }
    }

    /**
     * 保存编辑后的数据到服务器
     */
    public ResultUtils<Map<String, String>> saveReportData(SqkbReportPojo reportData, HttpServletRequest request) {
        try {
            String dateStr = formatTimeRangeStr(reportData.getStime());
            String fileName = "水情快报_" + DateUtil.dateFormat(new Date(), "yyyyMMddHHmmss") + ".docx";
            String savePath = request.getServletContext().getRealPath("/") + "upload" + File.separator + "sqkb"
                    + File.separator;
            File saveDir = new File(savePath);
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }
            String filePath = savePath + fileName;

            XWPFDocument document = new XWPFDocument();
            List<String> chartPaths = createWordFromData(document, reportData, reportData.getAreaRainMap());

            FileOutputStream out = new FileOutputStream(filePath);
            document.write(out);
            out.close();
            document.close();

            if (chartPaths != null) {
                for (String chartPath : chartPaths) {
                    if (chartPath != null && !chartPath.isEmpty()) {
                        new File(chartPath).delete();
                    }
                }
            }

            Map<String, String> result = new HashMap<>();
            result.put("filePath", "/upload/sqkb/" + fileName);
            result.put("fileName", fileName);

            return new ResultUtils<>(result, "保存成功！", true);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultUtils<>(null, "保存水情快报失败：" + e.getMessage(), false);
        }
    }

    /**
     * 计算报告数据
     */
    private SqkbReportPojo calculateReportData(List<ST_PPTN_RPojo> rainData, List<ST_STBPRP_BPojo> stationList,
            String stime, String etime) {
        SqkbReportPojo report = new SqkbReportPojo();
        report.setStime(stime);
        report.setEtime(etime);

        // 设置年份为当前年
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
        String currentYear = yearFormat.format(new Date());
        report.setYear(currentYear);

        // 查询XQKB_LIST表获取期号（XQKB_TYPE='水情快报' and XQKB_STM >= 当年1月1日）
        try {
            List<XQKB_LISTPojo> existList = xqkbListData.selectList(Collections.singletonList("水情快报"),
                    currentYear + "-01-01 00:00:00", null);
            // 期号 = 已有条数 + 1
            int nextQihao = (existList != null ? existList.size() : 0) + 1;
            report.setQihao(nextQihao);
        } catch (Exception e) {
            e.printStackTrace();
            report.setQihao(1);
        }

        String dateStr = formatTimeRangeStr(report.getStime());
        report.setTitle(dateStr + "暴雨分析");

        // 构建站点信息Map (stcd -> stationInfo)
        Map<String, ST_STBPRP_BPojo> stationMap = new HashMap<>();
        Map<String, String> stcdToAddvnm = new HashMap<>();
        String xingzhengArea="闵行区,宝山区,嘉定区,浦东新区,金山区,松江区,青浦区,奉贤区,崇明区,中心城区,徐汇区,黄浦区,杨浦区,长宁区,普陀区,虹口区,静安区" ;
        String zhognxinArea="中心城区,徐汇区,黄浦区,杨浦区,长宁区,普陀区,虹口区,静安区" ;
        if (stationList != null) {
            for (ST_STBPRP_BPojo s : stationList) {
                stationMap.put(s.getSTCD(), s);                
                if (s.getADDVNM() != null) {
                    String addvnm = s.getADDVNM();
                    if(xingzhengArea.contains(addvnm)){
                        if(zhognxinArea.contains(addvnm)){
                            addvnm="中心城区";
                        }
                        stcdToAddvnm.put(s.getSTCD(),addvnm);
                    }
                }
            }
        }

        // 按站点分组统计（包含0值，用于计算小时雨量过程）
        Map<String, Double> stationSumMap = new LinkedHashMap<>();
        Map<String, List<ST_PPTN_RPojo>> stationDataMap = new HashMap<>();

        if (rainData != null && !rainData.isEmpty()) {
            for (ST_PPTN_RPojo pojo : rainData) {
                String stcd = pojo.getSTCD();
                double drp = pojo.getDRP() != null ? pojo.getDRP() : 0;
                // 累加雨量（不管是否大于0）
                stationSumMap.merge(stcd, drp, Double::sum);
                // 保存所有原始数据用于计算小时雨量
                stationDataMap.computeIfAbsent(stcd, k -> new ArrayList<>()).add(pojo);
            }
        }

        // 计算全市平均降水量（算术平均）
        double avgDrp = 0;
        if (!stationSumMap.isEmpty()) {
            double total = stationSumMap.values().stream().mapToDouble(Double::doubleValue).sum();
            avgDrp = total / stationSumMap.values().size();
        }
        report.setAvgDrp(df.format(avgDrp));

        // 找出最大站点
        Map.Entry<String, Double> maxStationEntry = stationSumMap.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);

        String maxDrpStationStcd = "";
        String maxDrpStation = "";
        String maxDrpStationArea = "";
        double maxDrp = 0;
        if (maxStationEntry != null) {
            maxDrpStationStcd = maxStationEntry.getKey();
            ST_STBPRP_BPojo station = stationMap.get(maxDrpStationStcd);
            if (station != null) {
                String area = station.getADDVNM() != null ? station.getADDVNM() : "";
                String stnm = station.getSTNM() != null ? station.getSTNM() : maxDrpStationStcd;
                // 格式为：xx区xx站
                if (!area.isEmpty()) {
                    maxDrpStation = area + stnm;
                } else {
                    maxDrpStation = stnm;
                }
                maxDrpStationArea = area;
            } else {
                maxDrpStation = maxDrpStationStcd;
            }
            maxDrp = maxStationEntry.getValue();
        }

        report.setMaxDrpStationStcd(maxDrpStationStcd);
        report.setMaxDrpStation(maxDrpStation);
        report.setMaxDrp(df.format(maxDrp));

        // 计算最大60分钟降雨量及其时间（滑动窗口）
        Map<String, Object> max60Result = calculateMax60Rainfall(stationDataMap.get(maxDrpStationStcd));
        report.setMax60Drp(df.format(max60Result.get("maxDrp")));
        report.setMax60DrpStation(maxDrpStation);
        report.setMax60DrpTime(max60Result.get("timeRange").toString());

        // 最大站点的小时降雨过程数据
        List<SqkbReportPojo.HourlyRainPojo> hourlyList = calculateHourlyData(stationDataMap.get(maxDrpStationStcd),
                stime, etime);
        report.setHourlyRainList(hourlyList);

        // 按分区(ADDVNM)计算面雨量
        Map<String, List<Double>> hnnmRainMap = new HashMap<>();
        for (Map.Entry<String, Double> entry : stationSumMap.entrySet()) {
            // 1. 先获取值，如果 Map 里没有这个键，就拿到 "未知"
            String addvnm = stcdToAddvnm.getOrDefault(entry.getKey(), "未知");

            // 2. 加个判断：只有当值不等于 "未知" 时，才执行添加操作
            if (!"未知".equals(addvnm)) {
                // String addvnm = entry.getKey();//stcdToAddvnm.getOrDefault(entry.getKey(), "未知");
                hnnmRainMap.computeIfAbsent(addvnm, k -> new ArrayList<>()).add(entry.getValue());
            }
            
        }

        // 计算每个分区的面雨量（算术平均）
        Map<String, Double> hnnmAvgMap = new HashMap<>();
        for (Map.Entry<String, List<Double>> entry : hnnmRainMap.entrySet()) {
            double sum = entry.getValue().stream().mapToDouble(Double::doubleValue).sum();
            hnnmAvgMap.put(entry.getKey(), sum / entry.getValue().size());
        }

        // 找出面雨量最大和最小（需要包含区域名称和雨量值）
        Map.Entry<String, Double> maxHnnm = hnnmAvgMap.entrySet().stream()
                .max(Map.Entry.comparingByValue()).orElse(null);
        Map.Entry<String, Double> minHnnm = hnnmAvgMap.entrySet().stream()
                .min(Map.Entry.comparingByValue()).orElse(null);

        report.setAreaRainMax(maxHnnm != null ? maxHnnm.getKey() + " " + df.format(maxHnnm.getValue()) + "mm" : "0");
        report.setAreaRainMin(minHnnm != null ? minHnnm.getKey() + " " + df.format(minHnnm.getValue()) + "mm" : "0");

        // 保存分区雨量Map用于Word生成
        report.setAreaRainMap(hnnmAvgMap);

        // 排序后的站点列表
        List<Map.Entry<String, Double>> stationSorted = stationSumMap.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .collect(Collectors.toList());

        List<SqkbReportPojo.StationRainPojo> stationRainList = new ArrayList<>();
        int rank = 1;
        for (Map.Entry<String, Double> entry : stationSorted) {
            SqkbReportPojo.StationRainPojo s = new SqkbReportPojo.StationRainPojo();
            s.setStcd(entry.getKey());
            s.setStnm(
                    stationMap.get(entry.getKey()) != null ? stationMap.get(entry.getKey()).getSTNM() : entry.getKey());
            s.setDrp(entry.getValue());
            s.setRank(rank++);
            stationRainList.add(s);
        }
        report.setStationList(stationRainList);

        // 按雨量级分布统计（小雨0.1-10mm, 中雨10-25mm, 大雨25-50mm, 暴雨50-100mm, 大暴雨100-200mm,
        // 特大暴雨>=200mm）
        int countLightRain = 0, countMediumRain = 0, countHeavyRain = 0, countRainstorm = 0, countSevereRain = 0,
                countExtremeRain = 0;
        for (Double drp : stationSumMap.values()) {
            if (drp >= 200)
                countExtremeRain++;
            else if (drp >= 100)
                countSevereRain++;
            else if (drp >= 50)
                countRainstorm++;
            else if (drp >= 25)
                countHeavyRain++;
            else if (drp >= 10)
                countMediumRain++;
            else if (drp >= 0.1)
                countLightRain++;
        }
        report.setTotalStations(stationSumMap.size());
        report.setCountLightRain(countLightRain);
        report.setCountMediumRain(countMediumRain);
        report.setCountHeavyRain(countHeavyRain);
        report.setCountRainstorm(countRainstorm);
        report.setCountSevereRain(countSevereRain);
        report.setCountExtremeRain(countExtremeRain);

        // 调用外部API生成降水分布图(image1)
        try {
            String chartSavePath = templateFilePath + File.separator + "sqkb";
            File chartDir = new File(chartSavePath);
            if (!chartDir.exists()) {
                chartDir.mkdirs();
            }
            String image1FileName = fetchRainfallDistributionImage(stationSumMap, stationMap, chartSavePath);
            if (image1FileName != null) {
                report.setImage1Path(image1FileName);
                String timeRangeStrForImg1 = formatTimeRangeStr(stime, etime);
                report.setImage1PathTitle(timeRangeStrForImg1 + "上海市降水分布图");
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 不影响主流程，image1Path 保持为空
        }

        // 生成概况段落
        String rainLevelDesc = getRainLevelDescription(countLightRain, countMediumRain, countHeavyRain,
                countRainstorm, countSevereRain, countExtremeRain);
        String timeRangeStr = formatTimeRangeStr(stime, etime);
        String overview =  timeRangeStr
                + "全市平均降水量" + df.format(avgDrp) + "mm，单站最大降水量为" + maxDrpStation
                + df.format(maxDrp) + "mm，最大60min降水量为" + maxDrpStation
                + df.format(max60Result.get("maxDrp")) + "mm（" + max60Result.get("timeRange") + "），降水分布图如图1。";
        report.setOverview(overview);

        // 生成分析第一点段落
        String analysisText = "一是                ，" + generateAnalysisText(avgDrp, maxHnnm, minHnnm,
                stationSumMap.size(), countLightRain, countMediumRain, countHeavyRain,
                countRainstorm, countSevereRain, countExtremeRain);
        report.setAnalysisText(analysisText);

        String analysisText2 = "二是                ，单站最大降水量为" + maxDrpStation
                + df.format(maxDrp) + "mm，最大60min降水量为" + maxDrpStation
                + df.format(max60Result.get("maxDrp")) + "mm（" + max60Result.get("timeRange") + "），" + maxDrpStation
                + "降水过程如图4。";
        report.setAnalysisText2(analysisText2);

        // 水位情况
        String waterLevelDesc = calculateWaterLevelDesc(stime, etime, report);
        report.setWaterLevelDesc(waterLevelDesc);

        // 落款
        report.setAuthor("周殊凡");
        report.setReviewer("聂源");
        report.setApprover("潘崇伦");

        return report;
    }

    /**
     * 根据雨量等级统计生成描述
     */
    private String getRainLevelDescription(int countLightRain, int countMediumRain, int countHeavyRain,
            int countRainstorm, int countSevereRain, int countExtremeRain) {
        StringBuilder desc = new StringBuilder("本市普降");
        boolean hasExtreme = countExtremeRain > 0;
        boolean hasSevere = countSevereRain > 0;
        boolean hasRainstorm = countRainstorm > 0;
        boolean hasHeavy = countHeavyRain > 0;
        boolean hasMedium = countMediumRain > 0;
        boolean hasLight = countLightRain > 0;

        if (hasExtreme) {
            desc.append("特大暴雨");
        } else if (hasSevere) {
            desc.append("大暴雨");
        } else if (hasRainstorm) {
            desc.append("暴雨");
        } else if (hasHeavy) {
            desc.append("大雨");
        } else if (hasMedium) {
            desc.append("中雨");
        } else if (hasLight) {
            desc.append("小雨");
        } else {
            desc.append("雨");
        }

        if (hasExtreme || hasSevere) {
            desc.append("，局部");
            if (hasExtreme) {
                desc.append("特大暴雨");
            } else {
                desc.append("大暴雨");
            }
        } else if (hasRainstorm || hasHeavy) {
            desc.append("，局部暴雨");
        } else if (hasMedium) {
            desc.append("，局部中雨");
        }

        return desc.toString();
    }

    /**
     * 生成分析第一点段落
     */
    private String generateAnalysisText(double avgDrp, Map.Entry<String, Double> maxHnnm,
            Map.Entry<String, Double> minHnnm, int totalStations,
            int countLightRain, int countMediumRain, int countHeavyRain,
            int countRainstorm, int countSevereRain, int countExtremeRain) {
        StringBuilder sb = new StringBuilder();
        sb.append("全市累积降水量").append(df.format(avgDrp)).append("mm。");
        sb.append("面雨量最大为").append(maxHnnm != null ? maxHnnm.getKey() : "")
                .append(df.format(maxHnnm != null ? maxHnnm.getValue().doubleValue() : 0.0)).append("mm，");
        sb.append("最小为").append(minHnnm != null ? minHnnm.getKey() : "")
                .append(df.format(minHnnm != null ? minHnnm.getValue().doubleValue() : 0.0)).append("mm。");
        sb.append("全市").append(totalStations).append("个统一站中，");

        // 统计主要雨量等级（25mm以上）
        if (countExtremeRain > 0) {
            sb.append("达200mm以上的有").append(countExtremeRain).append("个测站，占比")
                    .append(String.format("%.2f", countExtremeRain * 100.0 / totalStations)).append("%；");
        }
        if (countSevereRain > 0) {
            sb.append("达100-200mm的有").append(countSevereRain).append("个测站，占比")
                    .append(String.format("%.2f", countSevereRain * 100.0 / totalStations)).append("%；");
        }
        if (countRainstorm > 0) {
            sb.append("达50-100mm的有").append(countRainstorm).append("个测站，占比")
                    .append(String.format("%.2f", countRainstorm * 100.0 / totalStations)).append("%；");
        }
        if (countHeavyRain > 0) {
            sb.append("达25-50mm的有").append(countHeavyRain).append("个测站，占比")
                    .append(String.format("%.2f", countHeavyRain * 100.0 / totalStations)).append("%；");
        }
        if (countMediumRain > 0) {
            sb.append("达10-25mm的有").append(countMediumRain).append("个测站，占比")
                    .append(String.format("%.2f", countMediumRain * 100.0 / totalStations)).append("%；");
        }
        if (countLightRain > 0) {
            sb.append("达0.1-10mm的有").append(countLightRain).append("个测站，占比")
                    .append(String.format("%.2f", countLightRain * 100.0 / totalStations)).append("%。");
        }
        return sb.toString();
    }

    /**
     * 计算水位情况描述
     * 
     * @param stime  开始时间
     * @param etime  结束时间
     * @param report 报告数据对象（用于设置水位图表路径）
     * @return 水位描述
     */
    private String calculateWaterLevelDesc(String stime, String etime, SqkbReportPojo report) {
        try {
            // 查询水情代表站配置 PID=2026031114184492913-3
            List<ST_STBPRP_B_QUPojo> stationList = quData.selectList("", "", null, "2026031114184492913-3", null);
            List<ST_STBPRP_B_QUPojo> stationListSW = quData.selectList("", "", null, "2026031114184492913-33", null);//不算潮位站

            if (stationList == null || stationList.isEmpty()) {
                return "全市防汛代表站潮水位均未超警。";
            }

            // 获取站点STCD列表
            List<String> stcdList = stationList.stream()
                    .map(ST_STBPRP_B_QUPojo::getSTCD)
                    .filter(s -> s != null && !s.isEmpty())
                    .collect(Collectors.toList());


            List<String> stcdListSWW = stationListSW.stream()
                    .map(ST_STBPRP_B_QUPojo::getSTCD)
                    .filter(s -> s != null && !s.isEmpty())
                    .collect(Collectors.toList());

            if (stcdList.isEmpty()) {
                return "全市防汛代表站潮水位均未超警。";
            }

            // 查询时间段内的水位数据
            List<GetWaterViewNewPojo> waterDataList = getWaterViewNewServer.selectListByHisIsTime(stcdList, stime,
                    etime, null);

            // 构建站点水位map (STCD -> 水位数据列表)
            Map<String, List<GetWaterViewNewPojo>> stationWaterMap = new LinkedHashMap<>();
            for (GetWaterViewNewPojo water : waterDataList) {
                stationWaterMap.computeIfAbsent(water.getSTCD(), k -> new ArrayList<>()).add(water);
            }

            // 计算超警戒站数和水利片水位变化
            int overWarnCount = 0;
            Map<String, List<Double>> slpChangeMap = new LinkedHashMap<>(); // SLP -> 水位上涨幅度列表

            // 记录水位上涨最大的站点
            String maxChangeStationStcd = "";
            String maxChangeStationName = "";
            String maxChangeStationNameSlp = "";
            double maxChangeValue = 0;
            List<GetWaterViewNewPojo> maxChangeStationWaterList = null;
            Double maxChangeWrz = null;
            Double maxChangeIvhz = null;

            for (Map.Entry<String, List<GetWaterViewNewPojo>> entry : stationWaterMap.entrySet()) {
                List<GetWaterViewNewPojo> waterList = entry.getValue();
                if (waterList == null || waterList.isEmpty())
                    continue;

                // 按时间排序
                waterList.sort(Comparator.comparing(GetWaterViewNewPojo::getTM));

                // 获取警戒水位WRZ
                Double wrz = null;
                try {
                    wrz = waterList.get(0).getWRZ() != null ? Double.parseDouble(waterList.get(0).getWRZ()) : null;
                } catch (Exception e) {
                    // ignore
                }

                // 获取开始时间的水位和最高水位
                Double startWaterLevel = null;
                Double maxWaterLevel = null;
                boolean hasOverWarn = false;

                maxWaterLevel = waterList.stream()
                        .map(GetWaterViewNewPojo::getUPZ)
                        .filter(Objects::nonNull) // 过滤掉 null
                        .mapToDouble(Double::parseDouble) // 将 String 转为 double
                        .max() // 获取最大值
                        .orElse(0.0);
                startWaterLevel = Double.parseDouble(waterList.get(0).getUPZ());

                // 检查是否超警戒
                if (wrz != null && maxWaterLevel >= wrz) {
                    hasOverWarn = true;
                }

                if (hasOverWarn) {
                    overWarnCount++;
                }

                if (startWaterLevel != null && maxWaterLevel != null) {
                    double change = maxWaterLevel - startWaterLevel;
                    if (change > 0) {
                        

                        // 记录水位上涨最大的站点
                        if(stcdListSWW.contains(entry.getKey()))
                        {
                            String slp = waterList.get(0).getSLP(); // 使用STLC作为水利片字段
                            if (slp == null || slp.isEmpty()) {
                                slp = "其他水利片";
                            }
                            slpChangeMap.computeIfAbsent(slp, k -> new ArrayList<>()).add(change);
                            
                            if (change > maxChangeValue) {
                                maxChangeValue = change;
                                maxChangeStationStcd = entry.getKey();
                                maxChangeStationName = waterList.get(0).getSTNM();
                                maxChangeStationNameSlp = slp + maxChangeStationName;
                                maxChangeStationWaterList = new ArrayList<>(waterList);
                                maxChangeWrz = wrz;
                                // 获取历史最高水位IVHZ
                                Double ivhz = null;
                                try {
                                    ivhz = waterList.get(0).getIVHZ() != null
                                            ? Double.parseDouble(waterList.get(0).getIVHZ().toString())
                                            : null;
                                } catch (Exception e) {
                                    // ignore
                                }
                                maxChangeIvhz = ivhz;
                            }
                        }                     
                    }
                }
            }

            // 生成描述
            StringBuilder result = new StringBuilder();
            result.append("三是");
            if (overWarnCount > 0) {
                result.append("全市").append(overWarnCount).append("个防汛代表站潮水位超警。");
            } else {
                result.append("全市防汛代表站潮水位均未超警。");
            }

            // 按水利片统计水位上涨幅度
            if (!slpChangeMap.isEmpty()) {
                result.append("水利片内部分河道水位出现上涨，");
                List<String> slpDescriptions = new ArrayList<>();
                for (Map.Entry<String, List<Double>> slpEntry : slpChangeMap.entrySet()) {
                    List<Double> changes = slpEntry.getValue();
                    double minChange = changes.stream().mapToDouble(Double::doubleValue).min().orElse(0);
                    double maxChange = changes.stream().mapToDouble(Double::doubleValue).max().orElse(0);
                    String desc = slpEntry.getKey() + "水位上涨幅度为" + df.format(minChange) + "m~" + df.format(maxChange)
                            + "m";
                    slpDescriptions.add(desc);
                }
                result.append(String.join("，", slpDescriptions));
                result.append("。");
            }
            if (!maxChangeStationNameSlp.isEmpty()) {
                report.setMaxChangeStationNameSlp(maxChangeStationNameSlp);
            }
            // 生成水位过程线图
            if (maxChangeStationWaterList != null && !maxChangeStationWaterList.isEmpty()) {
                try {
                    List<Map<String, Object>> chartDataList = new ArrayList<>();
                    for (GetWaterViewNewPojo water : maxChangeStationWaterList) {
                        Map<String, Object> item = new HashMap<>();
                        item.put("tm", water.getTM());
                        item.put("upz", water.getUPZ() != null ? Double.parseDouble(water.getUPZ()) : 0);
                        chartDataList.add(item);
                    }
                    String waterChartPath = SqkbChartUtils.generateWaterLevelChart(
                            chartDataList, maxChangeStationName, stime, etime, maxChangeWrz, maxChangeIvhz,
                            templateFilePath + File.separator + "sqkb");
                    if (waterChartPath != null) {
                        report.setImage5PathTitle(report.getMaxChangeStationNameSlp() + "水位过程线");
                        report.setImage5Path(new File(waterChartPath).getName());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return result.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "全市防汛代表站潮水位均未超警。";
        }
    }

    /**
     * 计算最大60分钟降雨量及其时间范围
     * 使用滑动窗口算法
     */
    private Map<String, Object> calculateMax60Rainfall(List<ST_PPTN_RPojo> stationData) {
        Map<String, Object> result = new HashMap<>();
        result.put("maxDrp", 0.0);
        result.put("timeRange", "");

        if (stationData == null || stationData.isEmpty()) {
            return result;
        }

        // 按时间排序
        List<ST_PPTN_RPojo> sortedData = new ArrayList<>(stationData);
        sortedData.sort(Comparator.comparing(ST_PPTN_RPojo::getTM));

        // 转换为时间戳降雨量列表
        List<Map.Entry<Long, Double>> timeRainList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (ST_PPTN_RPojo pojo : sortedData) {
            try {
                long tm = sdf.parse(pojo.getTM()).getTime();
                double drp = pojo.getDRP() != null ? pojo.getDRP() : 0;
                timeRainList.add(new AbstractMap.SimpleEntry<>(tm, drp));
            } catch (Exception e) {
                // ignore
            }
        }

        if (timeRainList.isEmpty()) {
            return result;
        }

        // 滑动窗口计算60分钟内的最大降雨量
        double maxSum = 0;
        long windowStart = 0;
        long windowEnd = 0;
        long oneHour = 60 * 60 * 1000; // 1小时毫秒数

        for (int i = 0; i < timeRainList.size(); i++) {
            long startTime = timeRainList.get(i).getKey();
            long endTime = startTime + oneHour;

            double sum = 0;
            for (int j = i; j < timeRainList.size(); j++) {
                if (timeRainList.get(j).getKey() <= endTime) {
                    sum += timeRainList.get(j).getValue();
                } else {
                    break;
                }
            }

            if (sum > maxSum) {
                maxSum = sum;
                windowStart = startTime;
                windowEnd = endTime;
            }
        }

        result.put("maxDrp", maxSum);
        result.put("timeRange", formatMax60TimeRange(windowStart, windowEnd));

        return result;
    }

    /**
     * 格式化最大60min时间范围
     * 同一天格式："09日04:35-05:35"
     * 不同天格式："9日04:35-10日05:35"
     */
    private String formatMax60TimeRange(long windowStart, long windowEnd) {
        SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
        String startDay = dayFormat.format(new Date(windowStart));
        String endDay = dayFormat.format(new Date(windowEnd));

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String startTime = timeFormat.format(new Date(windowStart));
        String endTime = timeFormat.format(new Date(windowEnd));

        if (startDay.equals(endDay)) {
            // 同一天：09日04:35-05:35
            SimpleDateFormat dayOnly = new SimpleDateFormat("dd");
            return dayOnly.format(new Date(windowStart)) + "日" + startTime + "-" + endTime;
        } else {
            // 不同天：9日04:35-10日05:35
            SimpleDateFormat dayOnlyStart = new SimpleDateFormat("d");
            SimpleDateFormat dayOnlyEnd = new SimpleDateFormat("d");
            String startDayStr = dayOnlyStart.format(new Date(windowStart));
            String endDayStr = dayOnlyEnd.format(new Date(windowEnd));
            return startDayStr + "日" + startTime + "-" + endDayStr + "日" + endTime;
        }
    }

    /**
     * 计算小时降雨数据
     * 规则：>前一个小时 <= 后一个小时
     * 记录的时间是前一个小时的整点（如2026-04-26 08:00:00的雨量是08:05:00到09:00:00）
     * 
     * @param stationData 站点降雨数据
     * @param stime       开始时间
     * @param etime       结束时间
     */
    private List<SqkbReportPojo.HourlyRainPojo> calculateHourlyData(List<ST_PPTN_RPojo> stationData, String stime,
            String etime) {
        List<SqkbReportPojo.HourlyRainPojo> hourlyList = new ArrayList<>();

        if (stationData == null || stationData.isEmpty()) {
            return hourlyList;
        }

        // 按时间排序
        List<ST_PPTN_RPojo> sortedData = new ArrayList<>(stationData);
        sortedData.sort(Comparator.comparing(ST_PPTN_RPojo::getTM));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat hourFormat = new SimpleDateFormat("yyyy-MM-dd HH:00:00");

        // 按小时汇总，规则：>前一个小时 <= 后一个小时
        Map<String, Double> hourlyMap = new LinkedHashMap<>();
        for (int i = 0; i < sortedData.size(); i++) {
            ST_PPTN_RPojo pojo = sortedData.get(i);
            String tm = pojo.getTM();
            if (tm == null || pojo.getDRP() == null)
                continue;

            try {
                Date currentTime = sdf.parse(tm);
                Calendar cal = Calendar.getInstance();
                cal.setTime(currentTime);

                // 取整到小时
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                String hourKey = hourFormat.format(cal.getTime());

                hourlyMap.merge(hourKey, pojo.getDRP(), Double::sum);
            } catch (Exception e) {
                // ignore
            }
        }

        // 补充缺失的小时（值为0）
        try {
            Calendar startCal = Calendar.getInstance();
            startCal.setTime(sdf.parse(stime));
            startCal.set(Calendar.MINUTE, 0);
            startCal.set(Calendar.SECOND, 0);
            startCal.set(Calendar.SECOND, 0);

            Calendar endCal = Calendar.getInstance();
            endCal.setTime(sdf.parse(etime));
            endCal.set(Calendar.MINUTE, 0);
            endCal.set(Calendar.SECOND, 0);

            // 从开始小时到结束小时，补全缺失的小时
            while (!startCal.after(endCal)) {
                String hourKey = hourFormat.format(startCal.getTime());
                hourlyMap.putIfAbsent(hourKey, 0.0);
                startCal.add(Calendar.HOUR_OF_DAY, 1);
            }
        } catch (Exception e) {
            // ignore
        }

        // 转换为列表
        for (Map.Entry<String, Double> entry : hourlyMap.entrySet()) {
            SqkbReportPojo.HourlyRainPojo item = new SqkbReportPojo.HourlyRainPojo();
            item.setTm(entry.getKey());
            item.setDrp(entry.getValue());
            hourlyList.add(item);
        }

        // 排序
        hourlyList.sort(Comparator.comparing(SqkbReportPojo.HourlyRainPojo::getTm));
        return hourlyList;
    }

    /**
     * 创建Word文档
     */
    private List<String> createWordFromData(XWPFDocument document, SqkbReportPojo report,
            Map<String, Double> hnnmAvgMap) {
        List<String> chartPaths = new ArrayList<>();
        String dateStr = formatTimeRangeStr(report.getStime());
        String timeRange = formatTimeRange(report.getStime(), report.getEtime());

        // 标题
        XWPFParagraph title = document.createParagraph();
        title.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun titleRun = title.createRun();
        titleRun.setText(dateStr + "暴雨分析");
        titleRun.setFontSize(22);
        titleRun.setBold(true);

        // 一、概况
        addSectionTitle(document, "一、概况");
        addParagraph(document,
                String.format("%s本市普降大雨，局部暴雨。%s全市平均降水量%smm，单站最大降水量为%s%smm，%s最大60min降水量为%s%smm（%s），降水分布图如图1。",
                        dateStr, timeRange, report.getAvgDrp(), report.getMaxDrpStation(), report.getMaxDrp(),
                        timeRange, report.getMax60DrpStation(), report.getMax60Drp(), report.getMax60DrpTime()));
        addImagePlaceholder(document, "图1  " + dateStr + "上海市降水分布图");

        // 二、简要分析
        addSectionTitle(document, "二、简要分析");

        // 分析第一点 - 生成雨量等级统计和分区柱状图
        String tempDir = System.getProperty("java.io.tmpdir");

        // 构建雨量等级Map用于饼图
        Map<String, Integer> rainLevelMap = new LinkedHashMap<>();
        rainLevelMap.put("小雨(0.1-10mm)", report.getCountLightRain() != null ? report.getCountLightRain() : 0);
        rainLevelMap.put("中雨(10-25mm)", report.getCountMediumRain() != null ? report.getCountMediumRain() : 0);
        rainLevelMap.put("大雨(25-50mm)", report.getCountHeavyRain() != null ? report.getCountHeavyRain() : 0);
        rainLevelMap.put("暴雨(50-100mm)", report.getCountRainstorm() != null ? report.getCountRainstorm() : 0);
        rainLevelMap.put("大暴雨(100-200mm)", report.getCountSevereRain() != null ? report.getCountSevereRain() : 0);
        rainLevelMap.put("特大暴雨(>=200mm)", report.getCountExtremeRain() != null ? report.getCountExtremeRain() : 0);

        // 生成雨量等级饼图
        String pieChartPath = SqkbChartUtils.generateRainLevelPieChart(rainLevelMap, tempDir);
        if (pieChartPath != null && new File(pieChartPath).exists()) {
            chartPaths.add(pieChartPath);
            addChartImageToWord(document, pieChartPath, "雨量等级分布", dateStr);
        }

        // 构建分区雨量Map用于柱状图
        Map<String, Double> areaRainMap = new LinkedHashMap<>();
        if (hnnmAvgMap != null) {
            for (Map.Entry<String, Double> entry : hnnmAvgMap.entrySet()) {
                areaRainMap.put(entry.getKey(), entry.getValue());
            }
        }

        // 生成各区降水量柱状图
        String barChartPath = SqkbChartUtils.generateAreaRainBarChart(areaRainMap, tempDir,report.getAvgDrp());
        if (barChartPath != null && new File(barChartPath).exists()) {
            chartPaths.add(barChartPath);
            addChartImageToWord(document, barChartPath, "各区降水量对照", dateStr);
        }

        addParagraph(document, String.format("一是，降水覆盖面广，西南部偏多。%s全市累积降水量%smm。面雨量最大为%s，最小为%s。",
                timeRange, report.getAvgDrp(), report.getAreaRainMax(), report.getAreaRainMin()));

        // 分析第二点
        addParagraph(document, String.format("二是降水持续时间长，雨强大。降雨主要集中在8日12时~23时及9日4时~8时。" +
                "单站最大降水量为%s%smm，最大60min降水量为%s%smm（%s），%s降水过程如图4。",
                report.getMaxDrpStation(), report.getMaxDrp(),
                report.getMax60DrpStation(), report.getMax60Drp(), report.getMax60DrpTime(),
                report.getMaxDrpStation()));

        // 图4 - 实际图表
        try {
            List<Map<String, Object>> hourlyDataList = new ArrayList<>();
            if (report.getHourlyRainList() != null) {
                for (SqkbReportPojo.HourlyRainPojo hourly : report.getHourlyRainList()) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("tm", hourly.getTm());
                    item.put("drp", hourly.getDrp());
                    hourlyDataList.add(item);
                }
            }

            if (!hourlyDataList.isEmpty()) {
                String chartPath = SqkbChartUtils.generateStationRainChart(hourlyDataList, report.getMaxDrpStation(),
                        tempDir);
                if (chartPath != null && new File(chartPath).exists()) {
                    chartPaths.add(chartPath);
                    addChartImageToWord(document, chartPath, report.getMaxDrpStation(), dateStr);
                } else {
                    addImagePlaceholder(document, "图4  " + report.getMaxDrpStation() + dateStr + "降水量棒图");
                }
            } else {
                addImagePlaceholder(document, "图4  " + report.getMaxDrpStation() + dateStr + "降水量棒图");
            }
        } catch (Exception e) {
            e.printStackTrace();
            addImagePlaceholder(document, "图4  " + report.getMaxDrpStation() + dateStr + "降水量棒图");
        }

        // 分析第三点
        addParagraph(document, "三是" + report.getWaterLevelDesc());

        // 空行和落款
        for (int i = 0; i < 5; i++) {
            document.createParagraph();
        }
        XWPFParagraph footer = document.createParagraph();
        footer.setAlignment(ParagraphAlignment.RIGHT);
        XWPFRun fRun = footer.createRun();
        fRun.setText(String.format("编写：%s    校核：%s    审核：%s",
                report.getAuthor() != null ? report.getAuthor() : "",
                report.getReviewer() != null ? report.getReviewer() : "",
                report.getApprover() != null ? report.getApprover() : ""));
        fRun.setFontSize(12);

        return chartPaths;
    }

    private void addSectionTitle(XWPFDocument document, String text) {
        XWPFParagraph p = document.createParagraph();
        p.setSpacingBefore(300);
        XWPFRun run = p.createRun();
        run.setText(text);
        run.setBold(true);
        run.setFontSize(14);
    }

    private void addParagraph(XWPFDocument document, String text) {
        XWPFParagraph p = document.createParagraph();
        XWPFRun run = p.createRun();
        run.setText(text);
        run.setFontSize(12);
    }

    private void addImagePlaceholder(XWPFDocument document, String text) {
        XWPFParagraph fig = document.createParagraph();
        fig.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun run = fig.createRun();
        run.setText(text);
        run.setItalic(true);
        run.setFontSize(10);
    }

    private void addChartImageToWord(XWPFDocument document, String imagePath, String stationName, String dateStr) {
        try {
            XWPFParagraph fig = document.createParagraph();
            fig.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun figRun = fig.createRun();
            figRun.setText("图4  " + stationName + dateStr + "降水量棒图");
            figRun.setItalic(true);
            figRun.setFontSize(10);

            XWPFParagraph picPara = document.createParagraph();
            picPara.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun picRun = picPara.createRun();

            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                FileInputStream fis = new FileInputStream(imageFile);
                byte[] imageBytes = new byte[(int) imageFile.length()];
                fis.read(imageBytes);
                fis.close();
                picRun.addPicture(new java.io.ByteArrayInputStream(imageBytes), 6,
                        imageFile.getName(), 400 * 9525, 250 * 9525);
            }
        } catch (Exception e) {
            e.printStackTrace();
            addImagePlaceholder(document, "[图表4: " + stationName + dateStr + "降水量棒图]");
        }
    }

    private String formatDateForTitle(String stime) {
        if (stime != null && stime.length() >= 10) {
            return stime.substring(5, 10).replace("-", "月") + "日";
        }
        return "";
    }

    private String formatTimeRange(String stime, String etime) {
        if (stime != null && etime != null) {
            String s = stime.substring(5, 16).replace("-", "月").replace(" ", "日").replace(":", "时") + "分";
            String e = etime.substring(5, 16).replace("-", "月").replace(" ", "日").replace(":", "时") + "分";
            return s + "至" + e;
        }
        return "";
    }

    private String formatTimeRangeStr(String stime, String etime) {
        // 1. 判空处理
        if (stime == null || stime.isEmpty() || etime == null || etime.isEmpty()) {
            return "";
        }

        try {
            // 2. 兼容常见的带秒或不带秒的时间格式
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm[:ss]");
            LocalDateTime start = LocalDateTime.parse(stime, inputFormatter);
            LocalDateTime end = LocalDateTime.parse(etime, inputFormatter);

            // 3. 定义基础格式：如“5月8日08时”
            DateTimeFormatter monthDayFormatter = DateTimeFormatter.ofPattern("M月d日HH时");
            // 定义只有日期和小时的格式：如“9日08时”
            DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("d日HH时");

            String startStr = start.format(monthDayFormatter);
            String endStr;

            // 4. 核心逻辑：判断开始时间和结束时间是否在同一年且同一月
            if (start.getYear() == end.getYear() && start.getMonth() == end.getMonth()) {
                // 同年同月：结束时间省略月份
                endStr = end.format(dayFormatter);
            } else {
                // 跨月或跨年：结束时间保留完整月份
                endStr = end.format(monthDayFormatter);
            }

            return startStr + "至" + endStr;

        } catch (Exception e) {
            // 5. 异常兜底，防止程序崩溃
            e.printStackTrace();
            return "";
        }
    }

    private String formatTimeRangeStr(String tm) {
        // 1. 判空处理
        if (tm == null || tm.isEmpty()) {
            return "";
        }

        try {
            // 2. 兼容常见的带秒或不带秒的时间格式
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm[:ss]");
            LocalDateTime start = LocalDateTime.parse(tm, inputFormatter);

            // 3. 定义基础格式：如“5月8日”
            DateTimeFormatter monthDayFormatter = DateTimeFormatter.ofPattern("M月d日");

            String tmStr = start.format(monthDayFormatter);

            return tmStr;

        } catch (Exception e) {
            // 5. 异常兜底，防止程序崩溃
            e.printStackTrace();
            return "";
        }
    }
}
