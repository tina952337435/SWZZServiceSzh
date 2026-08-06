package swzzmodeserver.tools;

import com.aspose.words.Document;
import com.aspose.words.FontSettings;
import com.aspose.words.License;
import com.aspose.words.SaveFormat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

/**
 * Word 转 Pdf 帮助类
 *
 * 备注:需要引入 aspose-words-15.8.0-jdk16.jar
 */
public class PdfUtil {

    public static boolean getLicense() {
        boolean result = false;
        try {
            InputStream is = PdfUtil.class.getClassLoader().getResourceAsStream("license.xml");
            License aposeLic = new License();
            aposeLic.setLicense(is);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    /**
     * @param wordPath 需要被转换的word全路径带文件名
     * @param pdfPath  转换之后pdf的全路径带文件名
     */
    public static void doc2pdf(String wordPath, String pdfPath) {
        // 验证License 若不验证则转化出的pdf文档会有水印产生
        if (!getLicense()) {
            return;
        }
        try {
            // 从 classpath 加载项目自带中文字体，解决 Linux 服务器乱码问题
            URL fontsUrl = PdfUtil.class.getClassLoader().getResource("fonts");
            if (fontsUrl != null) {
                FontSettings.setFontsFolder(fontsUrl.getPath(), false);
            }

            long old = System.currentTimeMillis();
            //新建一个pdf文档
            File file = new File(pdfPath);
            FileOutputStream os = new FileOutputStream(file);
            //Address是将要被转化的word文档
            Document doc = new Document(wordPath);
            //全面支持DOC, DOCX, OOXML, RTF HTML, OpenDocument, PDF, EPUB, XPS, SWF 相互转换
            doc.save(os, SaveFormat.PDF);
            long now = System.currentTimeMillis();
            os.close();
            //转化用时
            System.out.println("共耗时：" + ((now - old) / 1000.0) + "秒");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
