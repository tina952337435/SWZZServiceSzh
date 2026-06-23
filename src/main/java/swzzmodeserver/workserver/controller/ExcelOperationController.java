package swzzmodeserver.workserver.controller;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import swzzmodeserver.tools.ParamFields;
import swzzmodeserver.tools.ResultUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

@RequestMapping(path = "/excelOperation")
@RestController
@Api(tags = "Excel操作")
public class ExcelOperationController {

    @Value("${file.path.templatefilepath}")
    private String templatefilepath;

    @RequestMapping("/readExcelServlet")
    public ResultUtils<List> readExcelServlet(MultipartFile upLoadFile, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/binary;charset=UTF-8");
        StopWatch watch = new StopWatch();
        // 计时器开始
        watch.start();
        List<Map<String,String>> list =  new ArrayList<Map<String,String>>();
        String fileName = upLoadFile.getOriginalFilename();
        String columns = request.getParameter("columns");
        if (fileName != null) {
            String suff = fileName.substring(fileName.indexOf("."));
            //URL url = getClass().getClassLoader().getResource("uploadfile");//获得类加载路径
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String dateString = dateFormat.format(new Date());
            int randNum = (int)(Math.random() * 90000) + 10000;
            String filePathName = templatefilepath + dateString + randNum + suff;
            File file = new File(filePathName);
            upLoadFile.transferTo(file);

            String columnsList[] = {"tm","name","zz","zd","zpo","zpt"};
            if(columns!=null){
                columnsList = columns.split(",");
            }
            try {
                //String encoding = "GBK";
                File excel = new File(filePathName);
                Workbook wb =null;
                Sheet sheet = null;
                Row row = null;

                String cellData = null;
                if (excel.isFile() && excel.exists()) {   //判断文件是否存在
                    wb = readExcel(filePathName);
                    if(wb != null){
                        //用来存放表中数据
                        //list = new ArrayList<Map<String,String>>();
                        //获取第一个sheet
                        sheet = wb.getSheetAt(0);
                        //获取最大行数
                        int rownum = sheet.getPhysicalNumberOfRows();
                        //获取第一行
                        row = sheet.getRow(0);
                        //获取最大列数
                        int colnum = row.getPhysicalNumberOfCells();
                        for (int i = 1; i<rownum; i++) {
                            Map<String,String> map = new LinkedHashMap<String,String>();
                            row = sheet.getRow(i);
                            if(row !=null){
                                for (int j=0;j<colnum;j++){
                                    cellData = (String) getCellFormatValue(row.getCell(j));
                                    map.put(columnsList[j], cellData);
                                }
                            }else{
                                break;
                            }
                            list.add(map);
                        }
                    }
                    wb.close();
                } else {
                    return new ResultUtils<>(list, "操作失败(找不到指定的文件)", true, 0, watch.getTime());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        watch.stop();
        return new ResultUtils<>(list, "操作成功", true, list.size(), watch.getTime());
    }

    @RequestMapping("/writeExcelServlet/{className}")
    public ResultUtils<String> writeExcelServle(@PathVariable("className") String className,@RequestBody ParamFields param) throws IOException {
        StopWatch watch = new StopWatch();
        // 计时器开始
        watch.start();
        String ojbList = "",filename = "";
        if (null != param.getOjbList()){
            ojbList = param.getOjbList();
        }
        if (null != param.getFilename()){
            filename = param.getFilename();
        }
        //写excel
        Workbook workbook = null;
        //int random = (int)(Math.random() * 10000 + 10000);
        String fileName = "工作簿1.xlsx";
        String parentPath = templatefilepath + "Excel/";
        String filepath = parentPath + fileName;
        File file = new File(filepath);
        File parentFile = file.getParentFile();
        if (!parentFile.exists()){
            parentFile.mkdirs();
        }
        if (!file.exists()){
            file.createNewFile();
        }
        workbook = readExcel(filepath);
        if(null != workbook){
            try {
                Class<?> aClass = Class.forName("swzzmodeserver.workserver.pojo." + className);
                List<?> parseArray = JSON.parseArray(ojbList,aClass);
                Field[] fields = aClass.getDeclaredFields();
                Sheet sheet = workbook.getSheet("Sheet1");
                int rowID = 0;
                Row row = sheet.createRow(rowID++);//第一行
                for(int j = 0;j < fields.length;j++){
                    Cell cell = row.createCell(j);
                    cell.setCellValue(fields[j].getName());
                }
                for (Object obj : parseArray){
                    Row r = sheet.createRow(rowID++);
                    for(int j = 0;j < fields.length;j++){
                        Cell cell = r.createCell(j);
                        try {
                            fields[j].setAccessible(true);
                            setCellFormatValue(cell,getType(fields[j]),fields[j].get(obj));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
                filepath = parentPath + filename + ".xlsx";
                FileOutputStream out = new FileOutputStream(new File(filepath));
                workbook.write(out);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            watch.stop();
            return new ResultUtils<>(filename + ".xlsx", "操作成功", true, -1, watch.getTime());
        }
        watch.stop();
        return new ResultUtils<>("", "操作成功", false, -1, watch.getTime());
    }

    //读取excel
    public static Workbook readExcel(String filePath){
        Workbook wb = null;
        if(filePath==null){
            return null;
        }
        String extString = filePath.substring(filePath.lastIndexOf("."));
        InputStream is = null;
        try {
            is = new FileInputStream(filePath);
            if(".XLS".equals(extString.toUpperCase())){
                return wb = new HSSFWorkbook(is);
            }else if(".XLSX".equals(extString.toUpperCase())){
                return wb = new XSSFWorkbook(is);
            }else{
                return wb = null;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wb;
    }

    public static Object getCellFormatValue(Cell cell){
        Object cellValue = null;
        if(cell!=null){
            //判断cell类型
            switch(cell.getCellType()){
                case NUMERIC: // 数字
                    if (DateUtil.isCellDateFormatted(cell)) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        cellValue = sdf.format(DateUtil.getJavaDate(cell.getNumericCellValue())).toString();
                    } else {
                        DataFormatter dataFormatter = new DataFormatter();
                        cellValue = dataFormatter.formatCellValue(cell);
                    }
                    break;
                case STRING: // 字符串
                    cellValue = cell.getStringCellValue();
                    break;
                case BOOLEAN: // Boolean
                    cellValue = cell.getBooleanCellValue() + "";
                    break;
                case FORMULA: // 公式
                    cellValue = cell.getCellFormula() + "";
                    break;
                case BLANK: // 空值
                    cellValue = "";
                    break;
                case ERROR: // 故障
                    cellValue = "非法字符";
                    break;
                default:
                    cellValue = "";
                    break;
            }
        }else{
            cellValue = "";
        }
        return cellValue;
    }
    public static void setCellFormatValue(Cell cell,String type,Object object){
        Object cellValue = null;
        if(cell!=null){
            //判断cell类型
            switch(type){
                case "NUMERIC": // 数字
                    cell.setCellValue((Double)object);
                    break;
                case "STRING": // 字符串
                    cell.setCellValue((String)object);
                    break;
                case "BOOLEAN": // Boolean
                    cell.setCellValue((Boolean)object);
                    break;
                default:
                    cell.setCellValue("");
                    break;
            }
        }
    }
    public static String getType(Field field){
        Class<?> fieldType = field.getType();
        if(fieldType == String.class){
            return "STRING";
        }else if(fieldType == Integer.class || fieldType == Double.class || fieldType == Float.class){
            return "NUMERIC";
        }else if(fieldType == Boolean.class){
            return "BOOLEAN";
        }else {
            return "BLANK";
        }
    }
}
