package swzzmodeserver.tools;

import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class javalog {
    @Value("${file.path.templatefilepath}")
    private String filePathName;

    public void writelog(String logStr,String PathName){
        FileOutputStream outputStream = null;
        //获取当前日期
        // LocalDate currentDate = LocalDate.now();

        // // 定义日期格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
        // // 格式化当前日期
        // String formattedDate = currentDate.format(formatter);

        // DateTimeFormatter formatterYMDHM = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
        LocalDateTime currentDate = LocalDateTime.now();
        String formattedDate = currentDate.format(formatter);
        try {
            if (filePathName==null){
                filePathName=PathName;
            }
            String fullPath = filePathName + "/logs/";
            // 确保日志目录存在（递归创建所有父目录）
            File logDir = new File(fullPath);
            if (!logDir.exists()) {
                logDir.mkdirs();
            }
            outputStream = new FileOutputStream(new File(fullPath + "/SWZZService" + formattedDate + ".txt"), true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            DateTimeFormatter formatterYMDHM = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime currentDateLog = LocalDateTime.now();
            String formattedDateLog = currentDateLog.format(formatterYMDHM);
            logStr = formattedDateLog + "：" + logStr + "\n";
            //logStr=logStr+"\n";
            if (outputStream != null) {
                outputStream.write(logStr.getBytes());
                outputStream.flush();
            }
        } catch (Exception e) {
//            e.printStackTrace();
            System.out.println("javalog写入日志转换文件报错：" + e.getMessage());
        }
    }

    public void writelog(String logStr,String PathName,String fileName){
        FileOutputStream outputStream = null;
        //获取当前日期
        // LocalDate currentDate = LocalDate.now();

        // // 定义日期格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
        FileOutputStream outputStream1 = null;
        //获取当前日期
        // LocalDate currentDate = LocalDate.now();

        // // 定义日期格式
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
        // // 格式化当前日期
        // String formattedDate = currentDate.format(formatter);

        // DateTimeFormatter formatterYMDHM = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
        LocalDateTime currentDate = LocalDateTime.now();
        String formattedDate = currentDate.format(formatter1);
        try {
            if (filePathName==null){
                filePathName=PathName;
            }
            String fullPath = filePathName + "/logs/";
            // 确保日志目录存在（递归创建所有父目录）
            File logDir = new File(fullPath);
            if (!logDir.exists()) {
                logDir.mkdirs();
            }
            String logsFile = "/SWZZService" + formattedDate + ".txt";
            if (fileName != null && !fileName.isEmpty()) {
                logsFile = fileName + formattedDate + ".txt";
                fullPath += fileName + "/";
                // 确保子目录也存在
                File subDir = new File(fullPath);
                if (!subDir.exists()) {
                    subDir.mkdirs();
                }
            }
            outputStream1 = new FileOutputStream(new File(fullPath + logsFile), true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            DateTimeFormatter formatterYMDHM = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime currentDateLog = LocalDateTime.now();
            String formattedDateLog = currentDateLog.format(formatterYMDHM);
            logStr = formattedDateLog + "：" + logStr + "\n";
            //logStr=logStr+"\n";
            if (outputStream1 != null) {
                outputStream1.write(logStr.getBytes());
                outputStream1.flush();
            }
        } catch (Exception e) {
//            e.printStackTrace();
            System.out.println("javalog写入日志转换文件报错：" + e.getMessage());
        }
    }
}
