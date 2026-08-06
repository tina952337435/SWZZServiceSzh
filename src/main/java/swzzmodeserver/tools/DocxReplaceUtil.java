package swzzmodeserver.tools;

import org.apache.poi.xwpf.usermodel.*;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DOCX 占位符替换工具类
 * 支持文本占位符替换、图片占位符替换、表格数据填充
 *
 * 占位符格式: $key$
 */
public class DocxReplaceUtil {

    /** 占位符正则：匹配 $xxx$ 格式 */
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$([^$]+)\\$");

    /**
     * 替换文档中的所有文本占位符
     * 自动处理段落和表格中的占位符
     *
     * @param doc          XWPFDocument 文档对象
     * @param placeholders 占位符映射 (key → value)，key 不带 $ 符号
     */
    public static void replaceText(XWPFDocument doc, Map<String, String> placeholders) {
        if (placeholders == null || placeholders.isEmpty()) {
            return;
        }

        // 1. 替换普通段落中的占位符
        for (XWPFParagraph paragraph : doc.getParagraphs()) {
            replaceInParagraph(paragraph, placeholders);
        }

        // 2. 替换表格中的占位符
        for (XWPFTable table : doc.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph paragraph : cell.getParagraphs()) {
                        replaceInParagraph(paragraph, placeholders);
                    }
                }
            }
        }
    }

    /**
     * 替换段落中的占位符
     * 处理 Word 自动拆分 run 的情况：先合并相邻 run 中包含 $ 的文本，再替换
     */
    private static void replaceInParagraph(XWPFParagraph paragraph, Map<String, String> placeholders) {
        List<XWPFRun> runs = paragraph.getRuns();
        if (runs == null || runs.isEmpty()) {
            return;
        }

        // 将 runs 按 $ 符号分组，合并属于同一个占位符的相邻 runs
        // 策略：遍历所有 runs，收集文本，识别 $...$ 模式，替换后回写
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            String placeholder = "$" + entry.getKey() + "$";
            String replacement = entry.getValue() != null ? entry.getValue() : "";

            // 先尝试在单个 run 中替换（大多数情况）
            boolean found = false;
            for (XWPFRun run : runs) {
                String text = run.getText(0);
                if (text != null && text.contains(placeholder)) {
                    run.setText(text.replace(placeholder, replacement), 0);
                    found = true;
                }
            }

            // 如果单个 run 中没找到完整占位符，尝试跨 run 合并替换
            if (!found) {
                replaceCrossRun(paragraph, runs, placeholder, replacement);
            }
        }
    }

    /**
     * 处理占位符被 Word 拆分到多个 run 中的情况
     * 例如: run1="$typhoon" run2="Title$" → 合并后替换
     */
    private static void replaceCrossRun(XWPFParagraph paragraph, List<XWPFRun> runs,
            String placeholder, String replacement) {
        // 收集所有 run 的文本
        StringBuilder fullText = new StringBuilder();
        for (XWPFRun run : runs) {
            String text = run.getText(0);
            fullText.append(text != null ? text : "");
        }

        String combined = fullText.toString();
        if (!combined.contains(placeholder)) {
            return; // 该占位符不在这个段落中
        }

        // 找到了跨 run 的占位符，进行替换
        String replaced = combined.replace(placeholder, replacement);

        // 清空所有 run，将替换后的文本写入第一个 run
        for (int i = 0; i < runs.size(); i++) {
            if (i == 0) {
                runs.get(i).setText(replaced, 0);
            } else {
                runs.get(i).setText("", 0);
            }
        }
    }

    /**
     * 替换图片占位符
     * 找到包含指定占位符的段落，清除原内容，插入图片和题注
     *
     * @param doc         XWPFDocument 文档对象
     * @param placeholder 占位符 key（不带 $ 符号），如 "typhoonImage"
     * @param imagePath   图片文件完整路径
     * @param caption     图片题注，如 "图1 台风路径预报图"
     */
    public static void replaceImage(XWPFDocument doc, String placeholder, String imagePath, String caption) {
        if (imagePath == null || imagePath.isEmpty()) {
            return;
        }
        File imageFile = new File(imagePath);
        if (!imageFile.exists()) {
            return;
        }

        String fullPlaceholder = "$" + placeholder + "$";

        // 先在普通段落中查找
        for (XWPFParagraph paragraph : doc.getParagraphs()) {
            if (replaceImageInParagraph(paragraph, fullPlaceholder, imagePath, caption)) {
                return; // 替换成功后退出
            }
        }

        // 再在表格中查找
        for (XWPFTable table : doc.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph paragraph : cell.getParagraphs()) {
                        if (replaceImageInParagraph(paragraph, fullPlaceholder, imagePath, caption)) {
                            return;
                        }
                    }
                }
            }
        }
    }

    /**
     * 在段落中替换图片占位符
     */
    private static boolean replaceImageInParagraph(XWPFParagraph paragraph, String placeholder,
            String imagePath, String caption) {
        // 检查段落是否包含该占位符
        StringBuilder sb = new StringBuilder();
        for (XWPFRun run : paragraph.getRuns()) {
            String text = run.getText(0);
            if (text != null) {
                sb.append(text);
            }
        }
        if (!sb.toString().contains(placeholder)) {
            return false;
        }

        try {
            // 清除段落原有内容
            for (int i = paragraph.getRuns().size() - 1; i >= 0; i--) {
                paragraph.removeRun(i);
            }

            // 设置段落居中对齐
            paragraph.setAlignment(ParagraphAlignment.CENTER);

            // 读取图片尺寸
            int imgWidth = 400;
            int imgHeight = 250;
            try (ImageInputStream iis = ImageIO.createImageInputStream(new File(imagePath))) {
                Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
                if (readers.hasNext()) {
                    ImageReader reader = readers.next();
                    reader.setInput(iis);
                    imgWidth = reader.getWidth(0);
                    imgHeight = reader.getHeight(0);
                    reader.dispose();
                }
            } catch (Exception e) {
                // 降级使用默认尺寸
            }

            // 按最大展示宽度等比缩放
            int maxWidthEMU = 480 * 9525;
            int maxHeightEMU = 330 * 9525;
            int heightEMU = (int) ((double) imgHeight / imgWidth * maxWidthEMU);
            if (heightEMU > maxHeightEMU) {
                heightEMU = maxHeightEMU;
                maxWidthEMU = (int) ((double) imgWidth / imgHeight * maxHeightEMU);
            }

            // 插入图片
            XWPFRun picRun = paragraph.createRun();
            FileInputStream fis = new FileInputStream(imagePath);
            byte[] imageBytes = new byte[(int) new File(imagePath).length()];
            fis.read(imageBytes);
            fis.close();
            picRun.addPicture(new java.io.ByteArrayInputStream(imageBytes),
                    getPictureType(imagePath), imagePath, maxWidthEMU, heightEMU);

            // 添加换行和图片题注
            XWPFRun breakRun = paragraph.createRun();
            breakRun.addBreak();

            XWPFRun captionRun = paragraph.createRun();
            captionRun.setText(caption != null ? caption : "");
            captionRun.setFontSize(12);
            captionRun.setFontFamily("仿宋_GB2312");

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            // 失败时插入占位文本
            XWPFRun errorRun = paragraph.createRun();
            errorRun.setText("[" + caption + "]");
            errorRun.setFontSize(12);
            return true;
        }
    }

    /**
     * 根据文件扩展名获取 POI 图片类型
     */
    private static int getPictureType(String imagePath) {
        String lower = imagePath.toLowerCase();
        if (lower.endsWith(".png")) return XWPFDocument.PICTURE_TYPE_PNG;
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return XWPFDocument.PICTURE_TYPE_JPEG;
        if (lower.endsWith(".gif")) return XWPFDocument.PICTURE_TYPE_GIF;
        if (lower.endsWith(".bmp")) return XWPFDocument.PICTURE_TYPE_BMP;
        return XWPFDocument.PICTURE_TYPE_PNG; // 默认
    }

    /**
     * 填充表格数据
     * 从指定行开始，用 data 中的行数据填充表格
     *
     * @param doc        XWPFDocument 文档对象
     * @param tableIndex 表格索引（0-based，第几个表格）
     * @param data       表格数据，每个 String[] 是一行
     * @param startRow   从表格的第几行开始填充（0-based，默认表头占第0行，数据从第1行开始）
     */
    public static void fillTable(XWPFDocument doc, int tableIndex, List<String[]> data, int startRow) {
        if (data == null || data.isEmpty()) {
            return;
        }

        List<XWPFTable> tables = doc.getTables();
        if (tables == null || tableIndex >= tables.size()) {
            return;
        }

        XWPFTable table = tables.get(tableIndex);
        List<XWPFTableRow> rows = table.getRows();

        for (int i = 0; i < data.size(); i++) {
            int rowIdx = startRow + i;
            if (rowIdx >= rows.size()) {
                // 行不够，创建新行
                XWPFTableRow newRow = table.createRow();
                fillRow(newRow, data.get(i));
            } else {
                fillRow(rows.get(rowIdx), data.get(i));
            }
        }
    }

    /**
     * 填充表格中的一行数据
     */
    private static void fillRow(XWPFTableRow row, String[] rowData) {
        List<XWPFTableCell> cells = row.getTableCells();
        for (int j = 0; j < rowData.length && j < cells.size(); j++) {
            XWPFTableCell cell = cells.get(j);
            // 清除单元格原有文本
            List<XWPFParagraph> paragraphs = cell.getParagraphs();
            if (!paragraphs.isEmpty()) {
                XWPFParagraph p = paragraphs.get(0);
                List<XWPFRun> runs = p.getRuns();
                if (!runs.isEmpty()) {
                    runs.get(0).setText(rowData[j] != null ? rowData[j] : "", 0);
                    // 清除多余的 runs
                    for (int k = 1; k < runs.size(); k++) {
                        runs.get(k).setText("", 0);
                    }
                } else {
                    XWPFRun run = p.createRun();
                    run.setText(rowData[j] != null ? rowData[j] : "");
                }
            }
        }
    }

    /**
     * 获取文档中所有段落文本（调试用）
     */
    public static String getAllText(XWPFDocument doc) {
        StringBuilder sb = new StringBuilder();
        for (XWPFParagraph p : doc.getParagraphs()) {
            sb.append(p.getText()).append("\n");
        }
        return sb.toString();
    }
}
