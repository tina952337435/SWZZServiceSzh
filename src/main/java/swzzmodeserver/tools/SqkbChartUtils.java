package swzzmodeserver.tools;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.DateTickUnitType;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.annotations.CategoryLineAnnotation;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import javax.imageio.ImageIO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;

public class SqkbChartUtils {

    public static String generateRainfallChart(List<Map<String, Object>> hourlyData, String stationName, String savePath) {
        try {
            // 设置中文字体
            StandardChartTheme theme = new StandardChartTheme("CN");
            theme.setLargeFont(new Font("宋体", Font.BOLD, 14));
            theme.setRegularFont(new Font("宋体", Font.PLAIN, 12));
            theme.setExtraLargeFont(new Font("宋体", Font.BOLD, 16));
            ChartFactory.setChartTheme(theme);

            // 准备数据
            String[] timeLabels = new String[hourlyData.size()];
            double[] rainValues = new double[hourlyData.size()];
            double[] cumulativeValues = new double[hourlyData.size()];
            double cumulative = 0;

            for (int i = 0; i < hourlyData.size(); i++) {
                Map<String, Object> record = hourlyData.get(i);
                double drp = record.get("drp") != null ? ((Number) record.get("drp")).doubleValue() : 0;
                rainValues[i] = drp;
                cumulative += drp;
                cumulativeValues[i] = cumulative;

                String tm = record.get("tm") != null ? record.get("tm").toString() : "";
                if (tm.length() >= 16) {
                    timeLabels[i] = tm.substring(11, 16);
                } else {
                    timeLabels[i] = tm;
                }
            }

            // 创建数据集
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            for (int i = 0; i < hourlyData.size(); i++) {
                dataset.addValue(rainValues[i], "降雨量(mm)", timeLabels[i]);
                dataset.addValue(cumulativeValues[i], "累计(mm)", timeLabels[i]);
            }

            // 创建图表
            JFreeChart chart = ChartFactory.createLineChart(
                    stationName + " 降雨过程图",
                    "时间",
                    "降雨量(mm)",
                    dataset,
                    PlotOrientation.VERTICAL,
                    true,
                    true,
                    false
            );

            CategoryPlot plot = chart.getCategoryPlot();

            // 第一个渲染器：柱状图
            BarRenderer barRenderer = new BarRenderer() {
                @Override
                public Paint getItemPaint(int row, int column) {
                    if (row == 0) {
                        return new Color(65, 105, 225);
                    }
                    return super.getItemPaint(row, column);
                }
            };
            barRenderer.setDefaultItemLabelsVisible(true);
            barRenderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
            barRenderer.setDefaultPositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE3, TextAnchor.BOTTOM_CENTER));
            plot.setRenderer(0, barRenderer);

            // 第二个渲染器：折线图
            org.jfree.chart.renderer.category.LineAndShapeRenderer lineRenderer =
                    new org.jfree.chart.renderer.category.LineAndShapeRenderer(true, true);
            lineRenderer.setSeriesPaint(0, new Color(220, 20, 60));
            lineRenderer.setSeriesStroke(0, new BasicStroke(2.0f));
            lineRenderer.setDefaultItemLabelsVisible(true);
            lineRenderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}", new DecimalFormat("0.0")));
            plot.setRenderer(1, lineRenderer);

            // Y轴
            NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
            yAxis.setTickUnit(new NumberTickUnit(5));
            double maxY = Math.max(Arrays.stream(cumulativeValues).max().orElse(0), Arrays.stream(rainValues).max().orElse(0)) * 1.2;
            yAxis.setRange(0, maxY);

            // X轴
            CategoryAxis xAxis = plot.getDomainAxis();
            xAxis.setCategoryLabelPositions(org.jfree.chart.axis.CategoryLabelPositions.UP_45);

            plot.setBackgroundPaint(new Color(245, 245, 245));

            // 保存图片
            BufferedImage image = chart.createBufferedImage(800, 500);
            File file = new File(savePath);
            File parentDir = file.getParentFile();
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }
            ImageIO.write(image, "png", file);

            return savePath;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String generateStationRainChart(List<Map<String, Object>> rainData, String stationName, String saveDir) {
        Map<String, Double> hourlyMap = new LinkedHashMap<>();
        for (Map<String, Object> record : rainData) {
            String tm = record.get("tm") != null ? record.get("tm").toString() : "";
            double drp = record.get("drp") != null ? ((Number) record.get("drp")).doubleValue() : 0;

            if (tm.length() >= 13) {
                String hour = tm.substring(0, 13);
                hourlyMap.merge(hour, drp, Double::sum);
            }
        }

        List<Map<String, Object>> hourlyData = new ArrayList<>();
        for (Map.Entry<String, Double> entry : hourlyMap.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("tm", entry.getKey() + ":00");
            item.put("drp", entry.getValue());
            hourlyData.add(item);
        }

        hourlyData.sort(Comparator.comparing(m -> m.get("tm").toString()));

        // 获取时间范围
        String stime = hourlyData.isEmpty() ? "" : hourlyData.get(0).get("tm").toString();
        String etime = hourlyData.isEmpty() ? "" : hourlyData.get(hourlyData.size() - 1).get("tm").toString();

        String fileName = stationName.replaceAll("[^\\u4e00-\\u9fa5a-zA-Z0-9]", "_") + "_" + System.currentTimeMillis() + ".png";
        String savePath = saveDir + File.separator + fileName;

        return generateStationRainBarChart(hourlyData, stationName, stime, etime, savePath);
    }

    /**
     * 生成单站降雨柱状图（只有柱子，无累计线）
     */
     public static String generateStationRainBarChart(List<Map<String, Object>> hourlyData, String stationName,
                                                 String stime, String etime, String savePath) {
        try {
            // 1. 全局主题设置：统一使用宋体，防止乱码
            StandardChartTheme theme = new StandardChartTheme("CN");
            theme.setExtraLargeFont(new Font("宋体", Font.BOLD, 16)); // 标题字体
            theme.setLargeFont(new Font("宋体", Font.BOLD, 14));     // 图例字体
            theme.setRegularFont(new Font("宋体", Font.PLAIN, 12));   // 其他字体
            ChartFactory.setChartTheme(theme);

            // 2. 准备数据
            String[] timeLabels = new String[hourlyData.size()];
            double[] rainValues = new double[hourlyData.size()];

            for (int i = 0; i < hourlyData.size(); i++) {
                Map<String, Object> record = hourlyData.get(i);
                double drp = record.get("drp") != null ? ((Number) record.get("drp")).doubleValue() : 0;
                rainValues[i] = drp;

                String tm = record.get("tm") != null ? record.get("tm").toString() : "";
                // 格式化 X 轴标签：如 "05日14时"
                String day = tm.substring(8, 10);
                String hour = tm.substring(11, 13);
                if(i == 0){
                    timeLabels[i] = day + "日" + hour + "时";
                }
                else{                        
                    timeLabels[i] = hour;
                }
            }

            // 3. 创建数据集（图例已关闭，"降雨量(mm)"移至横坐标结尾）
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            for (int i = 0; i < hourlyData.size(); i++) {
                dataset.addValue(rainValues[i], "", timeLabels[i]);
            }

            // 4. 构建标题
            String titleStime = formatTitleTime(stime);
            String titleEtime = formatTitleTime(etime);
            String title = stationName + "\n" + "开始时间：" + titleStime + "-结束时间：" + titleEtime;

            // 5. 创建图表（关闭图例）
            JFreeChart chart = ChartFactory.createBarChart(
                    title,
                    "",
                    "",
                    dataset,
                    PlotOrientation.VERTICAL,
                    false,
                    true,
                    false
            );

            // 6. 设置标题样式
            chart.getTitle().setFont(new Font("宋体", Font.BOLD, 16));
            chart.getTitle().setPaint(Color.BLACK);

            CategoryPlot plot = chart.getCategoryPlot();

            // 7. 背景设置
            plot.setBackgroundPaint(Color.WHITE); // 绘图区纯白
            plot.setOutlineVisible(false);        // 去掉绘图区边框

            // 8. 柱子渲染器设置（关键：消除白边和阴影）
            BarRenderer renderer = (BarRenderer) plot.getRenderer();
            renderer.setSeriesPaint(0, new Color(34, 145, 34)); // 设置柱子颜色 #229122
            renderer.setDrawBarOutline(false);      // 不绘制柱子边框
            renderer.setShadowVisible(false);       // 不绘制阴影
            renderer.setBarPainter(new StandardBarPainter()); // 关键：使用标准画家消除渐变白边

            // 9. 数据标签设置（柱子顶部的数字）
            renderer.setDefaultItemLabelsVisible(true);
            renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}", new DecimalFormat("0.0")));
            renderer.setDefaultItemLabelPaint(Color.BLACK);
            renderer.setDefaultItemLabelFont(new Font("宋体", Font.PLAIN, 10));
            // 标签位置：柱子正上方居中
            renderer.setDefaultPositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BOTTOM_CENTER));

            // 10. Y 轴设置（数值轴）
            NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
            yAxis.setLabel("降雨量(mm)"); // 纵坐标顶部显示单位
            yAxis.setLabelFont(new Font("宋体", Font.PLAIN, 12));
            yAxis.setTickLabelFont(new Font("宋体", Font.PLAIN, 12));

            // Y 轴刻度设置
            yAxis.setTickMarkPaint(Color.BLACK);
            yAxis.setAxisLinePaint(Color.BLACK);
            yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits()); // 自动整数刻度，或者手动设置

            // 动态设置 Y 轴最大值，留出 20% 空间给标签
            double maxVal = Arrays.stream(rainValues).max().orElse(10.0);
            double upperMargin = maxVal * 0.2;
            if (upperMargin < 2) upperMargin = 2; // 至少留点空隙
            yAxis.setRange(0, maxVal + upperMargin);

            // 11. 网格线设置（水平虚线）
            plot.setRangeGridlinesVisible(true);
            plot.setRangeGridlinePaint(new Color(200, 200, 200)); // 浅灰色
            plot.setRangeGridlineStroke(new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[]{2.0f, 4.0f}, 0.0f));

            // 12. X 轴设置（分类轴）
            CategoryAxis xAxis = plot.getDomainAxis();
            xAxis.setLabelFont(new Font("宋体", Font.PLAIN, 12));
            xAxis.setTickLabelFont(new Font("宋体", Font.PLAIN, 11));
            xAxis.setAxisLinePaint(Color.BLACK);
            xAxis.setTickMarkPaint(Color.BLACK);

            // 横坐标标签始终水平显示（24个小时全部显示）
            xAxis.setCategoryLabelPositions(CategoryLabelPositions.STANDARD);
            // 增大标签宽度比率，防止长标签被截断为省略号
            xAxis.setMaximumCategoryLabelWidthRatio(3.0f);
            // 增加左右边距，防止首尾标签被裁剪（首个标签"X日X时"较长）
            xAxis.setLowerMargin(0.04);
            xAxis.setUpperMargin(0.02);

            // 13. 整体背景（图表边框区域）
            chart.setBackgroundPaint(Color.WHITE); // 纯白背景

            // 14. 保存文件
            BufferedImage image = chart.createBufferedImage(900, 500);
            File file = new File(savePath);
            File parentDir = file.getParentFile();
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }
            ImageIO.write(image, "png", file);

            return savePath;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 辅助方法：格式化时间标题
    private static String formatTitleTime(String timeStr) {
        // if (timeStr == null || timeStr.length() < 16) return "";
        // String month = timeStr.substring(5, 7);
        // String day = timeStr.substring(8, 10);
        // String hour = timeStr.substring(11, 13);
        // return month + "月" + day + "日" + hour + "时";

        String startFmt = timeStr.length() >= 16 ? timeStr.substring(0, 4) + "/" + timeStr.substring(5, 7) + "/" + timeStr.substring(8, 10) + " " + timeStr.substring(11, 16) : timeStr;
        return startFmt;
    }
    /**
     * 生成雨量等级饼图
     * @param rainLevels 雨量等级统计 Map<等级名称, 数量>
     * @param saveDir 保存目录
     * @return 图片路径
     */
    public static String generateRainLevelPieChart(Map<String, Integer> rainLevels, String saveDir) {
        try {
            StandardChartTheme theme = new StandardChartTheme("CN");
            theme.setLargeFont(new Font("宋体", Font.BOLD, 14));
            theme.setRegularFont(new Font("宋体", Font.PLAIN, 12));
            theme.setExtraLargeFont(new Font("宋体", Font.BOLD, 16));
            ChartFactory.setChartTheme(theme);

            DefaultPieDataset dataset = new DefaultPieDataset();
            int total = 0;
            for (Map.Entry<String, Integer> entry : rainLevels.entrySet()) {
                if (entry.getValue() > 0) {
                    dataset.setValue(entry.getKey(), entry.getValue());
                    total += entry.getValue();
                }
            }

            JFreeChart chart = ChartFactory.createPieChart(
                    "雨量等级分布",
                    dataset,
                    true,
                    true,
                    false
            );

            PiePlot plot = (PiePlot) chart.getPlot();
            // 1. 设置 Plot 区域背景为白色（修复灰色背景的关键）
            plot.setBackgroundPaint(Color.WHITE);
            plot.setOutlineVisible(false);
            plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}({1}) {2}", new DecimalFormat("0"), new DecimalFormat("0.00%")));

            // 设置饼图颜色
            Color[] colors = {
                    new Color(166,242,142),   // 小雨 - 黄绿色
                    new Color(0,123,0),   // 中雨 - 深天蓝
                    new Color(61,188,249),  // 大雨 - 皇家蓝
                    new Color(0,0,249),   // 暴雨 - 深红色
                    new Color(251,61,250),  // 大暴雨 - 蓝紫色
                    new Color(123,0,0)    // 特大暴雨 - 橙色
            };

            int i = 0;
            for (Map.Entry<String, Integer> entry : rainLevels.entrySet()) {
                if (entry.getValue() > 0 && i < colors.length) {
                    plot.setSectionPaint(entry.getKey(), colors[i++]);
                }
            }

            // 设置背景白色
            chart.setBackgroundPaint(Color.WHITE);

            BufferedImage image = chart.createBufferedImage(800, 500);
            String fileName = "rain_level_pie_" + System.currentTimeMillis() + ".png";
            File file = new File(saveDir + File.separator + fileName);
            ImageIO.write(image, "png", file);

            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 生成各区面雨量对照柱状图
     * @param areaRainMap 分区雨量 Map<分区名称, 面雨量>
     * @param saveDir 保存目录
     * @return 图片路径
     */
    public static String generateAreaRainBarChart(Map<String, Double> areaRainMap, String saveDir,String AvgDrp) {
        try {
            areaRainMap.put("全市", Double.parseDouble(AvgDrp));
            // 1. 全局主题设置：统一使用宋体
            StandardChartTheme theme = new StandardChartTheme("CN");
            theme.setExtraLargeFont(new Font("宋体", Font.BOLD, 16)); // 主标题
            theme.setLargeFont(new Font("宋体", Font.BOLD, 14));     // 图例
            theme.setRegularFont(new Font("宋体", Font.PLAIN, 12));   // 坐标轴标签
            ChartFactory.setChartTheme(theme);

            // 2. 创建数据集：全市在第一个，其余按雨量从高到低排列
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            // 全市始终排第一个
            if (areaRainMap.containsKey("全市")) {
                dataset.addValue(areaRainMap.get("全市"), "雨量(mm)", "全市");
            }

            // 其他区域按雨量值降序排列
            areaRainMap.entrySet().stream()
                    .filter(entry -> !"全市".equals(entry.getKey()))
                    .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                    .forEach(entry -> dataset.addValue(entry.getValue(), "雨量(mm)", entry.getKey()));

            // 3. 创建图表
            JFreeChart chart = ChartFactory.createBarChart(
                    "各区面雨量对照图", // 标题
                    "",             // X轴标签（空）
                    "雨量(mm)",      // Y轴标签
                    dataset,
                    PlotOrientation.VERTICAL,
                    false, // 是否显示图例（截图无图例）
                    true,  // 是否生成工具提示
                    false
            );

            CategoryPlot plot = chart.getCategoryPlot();

            // 4. 设置背景与边框
            chart.setBackgroundPaint(Color.WHITE); // 整体背景：纯白
            plot.setBackgroundPaint(Color.WHITE);                // 绘图区背景：纯白
            plot.setOutlinePaint(Color.BLACK);                   // 绘图区边框：黑色
            plot.setOutlineVisible(true);                        // 显示边框

            // 5. 设置网格线（关键：水平虚线）
            plot.setRangeGridlinesVisible(true);
            plot.setRangeGridlinePaint(new Color(192, 192, 192)); // 浅灰色
            plot.setRangeGridlineStroke(new BasicStroke(
                    1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                    10.0f, new float[]{4.0f, 4.0f}, 0.0f
            )); // 虚线样式
            plot.setDomainGridlinesVisible(false); // 隐藏垂直网格线

            // 6. 设置柱子渲染器
            BarRenderer renderer = new BarRenderer() {
                @Override
                public Paint getItemPaint(int row, int column) {
                    return new Color(79, 129, 189); // #4F81BD 纯蓝色，无渐变
                }
            };
            renderer.setDrawBarOutline(false); // 不绘制柱子边框
            renderer.setShadowVisible(false);  // 关闭阴影
            renderer.setBarPainter(new StandardBarPainter()); // 消除渐变效果

            // 7. 设置数值标签（在柱子正上方）
            renderer.setDefaultItemLabelsVisible(true);
            renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}", new DecimalFormat("0.0")));
            renderer.setDefaultItemLabelPaint(Color.BLACK);
            // OUTSIDE12 + CENTER 确保标签在柱子正上方居中
            renderer.setDefaultPositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BOTTOM_CENTER));
            plot.setRenderer(renderer);

            // 8. Y轴设置
            NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
            rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits()); // 自动整数刻度
            rangeAxis.setTickMarkPaint(Color.BLACK); // 刻度线黑色
            rangeAxis.setAxisLinePaint(Color.BLACK); // 轴线黑色
            rangeAxis.setLowerBound(0); // 从0开始

            // 动态调整Y轴最大值
            if (!areaRainMap.isEmpty()) {
                double maxVal = areaRainMap.values().stream().mapToDouble(Double::doubleValue).max().orElse(0);
                double upperBound = Math.ceil(maxVal / 10.0) * 10; // 向上取整到10的倍数
                if (upperBound < 10) upperBound = 10;
                rangeAxis.setUpperBound(upperBound * 1.15); // 留出顶部空间给标签
            }

            // 9. X轴设置（关键：水平显示）
            CategoryAxis domainAxis = plot.getDomainAxis();
            domainAxis.setAxisLinePaint(Color.BLACK); // 轴线黑色
            domainAxis.setTickMarkPaint(Color.BLACK); // 刻度线黑色
            domainAxis.setCategoryLabelPositions(CategoryLabelPositions.STANDARD); // 标签水平

            // 10. 保存图片
            BufferedImage image = chart.createBufferedImage(900, 500);
            String fileName = "area_rain_bar_" + System.currentTimeMillis() + ".png";
            File file = new File(saveDir, fileName);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            ImageIO.write(image, "png", file);

            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 生成水位过程折线图
     * @param waterDataList 水位数据列表
     * @param stationName 站名
     * @param stime 开始时间
     * @param etime 结束时间
     * @param wrz 警戒水位
     * @param saveDir 保存目录
     * @return 图片路径
     */
    public static String generateWaterLevelChart(List<Map<String, Object>> waterDataList, String stationName,
                                             String stime, String etime, Double wrz, String saveDir) {
        try {
            if (waterDataList == null || waterDataList.isEmpty()) {
                return null;
            }

            // 1. 设置中文字体主题
            StandardChartTheme theme = new StandardChartTheme("CN");
            theme.setLargeFont(new Font("宋体", Font.BOLD, 14));
            theme.setRegularFont(new Font("宋体", Font.PLAIN, 12));
            theme.setExtraLargeFont(new Font("宋体", Font.BOLD, 16));
            ChartFactory.setChartTheme(theme);

            // 2. 准备时间序列数据集
            TimeSeries waterSeries = new TimeSeries("水位(m)");

            // 用于解析时间的格式，根据你的数据库字段调整，这里假设是 yyyy-MM-dd HH:mm:ss
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            Date lastDate = null; // 记录最后的时间点，用于标注

            for (Map<String, Object> record : waterDataList) {
                String tmStr = record.get("tm") != null ? record.get("tm").toString() : "";
                Double upz = record.get("upz") != null ? ((Number) record.get("upz")).doubleValue() : 0;

                Date date = sdf.parse(tmStr);
                lastDate = date; // 更新最后时间

                // 使用 Millisecond 包装日期（也可以根据精度用 Minute.class）
                waterSeries.add(new Millisecond(date), upz);
            }

            TimeSeriesCollection dataset = new TimeSeriesCollection();
            dataset.addSeries(waterSeries);

            // 警戒水位序列（仅在有值时添加）
            TimeSeries warnSeries = null;
            if (wrz != null) {
                warnSeries = new TimeSeries("警戒水位");
                for (Map<String, Object> record : waterDataList) {
                    String tmStr = record.get("tm") != null ? record.get("tm").toString() : "";
                    if (!tmStr.isEmpty()) {
                        Date date = sdf.parse(tmStr);
                        warnSeries.add(new Millisecond(date), wrz);
                    }
                }
                dataset.addSeries(warnSeries);
            }

            // 3. 创建时间序列图表
            JFreeChart chart = ChartFactory.createTimeSeriesChart(
                    stationName + "水位过程线",
                    "", // X轴标签
                    "水位(m)",
                    dataset,
                    true,
                    true,
                    false
            );

            // 设置副标题
            String subtitle = "开始时间：" + stime + " - 结束时间：" + etime;
            chart.addSubtitle(new TextTitle(subtitle, new Font("宋体", Font.PLAIN, 12)));

            // 4. 获取 XYPlot 进行高级设置
            XYPlot plot = chart.getXYPlot();
            plot.setBackgroundPaint(Color.WHITE);

            // 获取已有渲染器并设置线条样式
            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();

            // 水位线：蓝色，实线，宽度2
            renderer.setSeriesPaint(0, new Color(0, 0, 255));
            renderer.setSeriesStroke(0, new BasicStroke(2.0f));
            renderer.setSeriesShapesVisible(0, false); // 不显示数据点圆点

            // 警戒水位：橙色，虚线
            if (wrz != null) {
                renderer.setSeriesPaint(1, new Color(251, 90, 16));
                renderer.setSeriesStroke(1, new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{10.0f, 6.0f}, 0.0f));
                renderer.setSeriesShapesVisible(1, false);
            }

            // 5. Y轴设置
            NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
            yAxis.setTickUnit(new NumberTickUnit(0.5)); // 刻度间隔0.5
            yAxis.setRange(0, 7); // 你原来的逻辑是自动算最大值，这里为了演示写死或保留你的逻辑均可
            // 如果需要保留自动计算最大值逻辑：
            /*
            double maxVal = waterDataList.stream().mapToDouble(r -> ((Number)r.get("upz")).doubleValue()).max().orElse(0);
            maxVal = Math.max(maxVal, wrz != null ? wrz : 0);
            yAxis.setRange(0, maxVal * 1.2);
            */

            // 6. X轴设置（解决密集问题的核心代码）
            DateAxis xAxis = (DateAxis) plot.getDomainAxis();

            // --- 核心设置开始 ---
            // 设置每隔 4 小时显示一个标签
            xAxis.setTickUnit(new DateTickUnit(DateTickUnitType.HOUR, 4, new SimpleDateFormat("HH:mm")));

            // 强制显示这个间隔，不自动缩放
            xAxis.setAutoTickUnitSelection(false);

            // 设置日期格式（如果标签显示日期的话）
            xAxis.setDateFormatOverride(new SimpleDateFormat("dd HH:mm"));
            // --- 核心设置结束 ---

            // 7. 添加文字标注（改为 XYTextAnnotation）
            if (lastDate != null) {
                long timeMillis = lastDate.getTime();
                // X坐标稍微往右偏移一点，防止贴边
                long xOffset = 3600 * 1000 * 2; // 偏移2小时

                if (wrz != null) {
                    XYTextAnnotation ann = new XYTextAnnotation("警戒水位", timeMillis + xOffset, wrz);
                    ann.setFont(new Font("宋体", Font.PLAIN, 12));
                    ann.setPaint(new Color(251, 90, 16));
                    ann.setTextAnchor(TextAnchor.CENTER_LEFT);
                    plot.addAnnotation(ann);
                }
            }

            // 8. 保存图片
            BufferedImage image = chart.createBufferedImage(900, 500);
            String fileName = "water_level_" + System.currentTimeMillis() + ".png";
            File file = new File(saveDir, fileName);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            ImageIO.write(image, "png", file);

            return file.getAbsolutePath();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
