package swzzmodeserver.workserver.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import swzzmodeserver.tools.ColumnName;
import swzzmodeserver.tools.FilePathUtils;
import swzzmodeserver.tools.ResultUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author lk
 * @Date 2020-06-20 14:01
 * @Description
 */
@RequestMapping(path = "/excel")
@RestController
@Api(tags = "Excel操作")
public class ExcelController {

    //Excel模板路径
    @Value("${file.path.templatefilepath}")
    private String templatefilepath;

    @RequestMapping(value = "/excelTemplateExport", method = {RequestMethod.POST})
    @ApiOperation("Excel导出通用模板 （{\"templatename\":\"模板名称.xls\",\"maplist\":\"list数据对象；maplist名称可根据模板定义名称进行设置；其他非list参数，可自定义名称后在模板进行设置\"}）")
    public ResultUtils<List> excelTemplateExport(HttpServletResponse response, @RequestBody String body) throws IOException {
        response.setContentType("application/binary;charset=UTF-8");
        StopWatch watch = new StopWatch();
        // 计时器开始
        watch.start();
        Map<String, Object> maps = (Map) JSON.parse(body);

        //获取模板名称（固定参数名称）
        String strTemplateName = maps.get("templatename").toString();

        String PathName = "";
        if(maps.get("pathname")!=null){
            PathName=maps.get("pathname").toString();
        }

        //实例化模板
        TemplateExportParams params = new TemplateExportParams(FilePathUtils.getRealFilePath(templatefilepath + "MB/" + strTemplateName));

        Workbook workbook = ExcelExportUtil.exportExcel(params, maps);
        File savefile = new File(FilePathUtils.getRealFilePath(templatefilepath + PathName+"/"));
        if (!savefile.exists()) {
            savefile.mkdirs();
        }
        //本地生成excel临时文件
        String filePathName = PathName+"/" + UUID.randomUUID() + ".xls";
        FileOutputStream fos = new FileOutputStream(FilePathUtils.getRealFilePath(templatefilepath + filePathName));
        workbook.write(fos);
        fos.close();

        List<ColumnName> list = new ArrayList<>();
        ColumnName param = new ColumnName();
        param.setValue(filePathName);
        list.add(param);
        //计时器结束
        watch.stop();
        return new ResultUtils<>(list, "操作成功", true, 1, watch.getTime());

    }

    /**
     * excel动态生成列，进行导出
     *
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/excelDownloadExport", method = {RequestMethod.POST})
    @ApiOperation("Excel导出通用模板 （{\"maplist\":\"list数据对象；maplist名称可根据模板定义名称进行设置；其他非list参数，可自定义名称后在模板进行设置\"}）")
    private ResultUtils<List> excelDownloadExport(HttpServletResponse response, @RequestBody String body) throws IOException {
        List<ColumnName> ColumnNamelist = new ArrayList<>();
        StopWatch watch = new StopWatch();
        // 计时器开始
        watch.start();
        try {
            Map<String, Object> maps = (Map) JSON.parse(body);
            String title = maps.get("title").toString();
            String PathName = "";
            if(maps.get("pathname")!=null){
                PathName=maps.get("pathname").toString();
            }
            String SheetName = "Sheet1";
            if(maps.get("sheetname")!=null){
                SheetName=maps.get("sheetname").toString();
            }
            JSONArray maplist = (JSONArray) maps.get("maplist");
            JSONArray columnName = (JSONArray) maps.get("columnname");
            List<ExcelExportEntity> entity = new ArrayList<ExcelExportEntity>();

            if (columnName.size() > 0) {
                for (int num = 0; num < columnName.size(); num++) {
                    JSONObject obj = (JSONObject) columnName.get(num);
                    String objName = obj.get("name").toString().replaceAll("/", "").replaceAll("\n", "").replaceAll("\t", "").replaceAll("\r", "");
                    String objValue = obj.get("value").toString();
                    entity.add(new ExcelExportEntity(objName, objValue));
                }
            }
            List<Map<String, String>> list = new ArrayList<Map<String, String>>();
            Map<String, String> map;
            for (int j = 0; j < maplist.size(); j++) {
                map = new HashMap<String, String>();
                JSONObject objList = (JSONObject) maplist.get(j);
                for (int i = 0; i < columnName.size(); i++) {
                    String objListValue = "";
                    JSONObject obj = (JSONObject) columnName.get(i);
                    String objValue = obj.get("value").toString();
                    try {
                        objListValue = objList.get(objValue).toString();
                    } catch (Exception ex) {
                        objListValue = "";
                    }
                    map.put(objValue, objListValue);
                }
                list.add(map);
            }
            ExportParams params = new ExportParams(title, SheetName, ExcelType.XSSF);
            Workbook workbook = ExcelExportUtil.exportExcel(params, entity, list);

            //本地生成excel临时文件
            File savefile = new File(FilePathUtils.getRealFilePath(templatefilepath + PathName+"/"));
            if (!savefile.exists()) {
                savefile.mkdirs();
            }
            String filePathName = PathName+"/" + UUID.randomUUID() + ".xlsx";
            FileOutputStream fos = new FileOutputStream(FilePathUtils.getRealFilePath(templatefilepath + filePathName));
            workbook.write(fos);
            fos.close();

            ColumnName param = new ColumnName();
            param.setValue(filePathName);
            ColumnNamelist.add(param);

        } catch (Exception e) {
            e.printStackTrace();
        }
        //计时器结束
        watch.stop();
        return new ResultUtils<>(ColumnNamelist, "操作成功", true, 1, watch.getTime());
    }

    @RequestMapping(value = "/excelSheetExport", method = {RequestMethod.POST})
    @ApiOperation("Excel导出通用模板 （{\"templatename\":\"模板名称.xls\",\"maplist\":\"list数据对象；maplist名称可根据模板定义名称进行设置；其他非list参数，可自定义名称后在模板进行设置\"}）")
    public ResultUtils<List> excelSheetExport(HttpServletResponse response, @RequestBody String body) throws IOException {
        response.setContentType("application/binary;charset=UTF-8");
        StopWatch watch = new StopWatch();
        // 计时器开始
        watch.start();
        Map<String, Object> maps = (Map) JSON.parse(body);

        //获取模板名称（固定参数名称）
        String strTemplateName = maps.get("templatename").toString();

        String PathName = "";
        if(maps.get("pathname")!=null){
            PathName=maps.get("pathname").toString();
        }

        //实例化模板
        TemplateExportParams params = new TemplateExportParams(FilePathUtils.getRealFilePath(templatefilepath + "MB/" + strTemplateName));

        Workbook workbook = ExcelExportUtil.exportExcel(params, maps);
        File savefile = new File(FilePathUtils.getRealFilePath(templatefilepath + PathName+"/"));
        if (!savefile.exists()) {
            savefile.mkdirs();
        }
        //本地生成excel临时文件
        String filePathName = PathName+"/" + UUID.randomUUID() + ".xls";
        FileOutputStream fos = new FileOutputStream(FilePathUtils.getRealFilePath(templatefilepath + filePathName));
        workbook.write(fos);


        /*
         * 功能描述: <br>
         * 〈〉
         * @Param: 导出Sheet
         * @Return: com.js.myswproject.common.utils.ResultUtils<java.util.List>
         * @Author: 刘彬
         * @Date: 2020/9/15 17:03
         */
        String title = maps.get("title").toString();

        String SheetName = "Sheet2";
        if(maps.get("sheetname")!=null){
            SheetName=maps.get("sheetname").toString();
        }
        JSONArray sheetdata = (JSONArray) maps.get("sheetdata");
        JSONArray columnName = (JSONArray) maps.get("columnname");
        List<ExcelExportEntity> entity = new ArrayList<ExcelExportEntity>();

        if (columnName.size() > 0) {
            for (int num = 0; num < columnName.size(); num++) {
                JSONObject obj = (JSONObject) columnName.get(num);
                String objName = obj.get("name").toString().replaceAll("/", "").replaceAll("\n", "").replaceAll("\t", "").replaceAll("\r", "");
                String objValue = obj.get("value").toString();
                entity.add(new ExcelExportEntity(objName, objValue));
            }
        }
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        Map<String, String> map;
        for (int j = 0; j < sheetdata.size(); j++) {
            map = new HashMap<String, String>();
            JSONObject objList = (JSONObject) sheetdata.get(j);
            for (int i = 0; i < columnName.size(); i++) {
                String objListValue = "";
                JSONObject obj = (JSONObject) columnName.get(i);
                String objValue = obj.get("value").toString();
                try {
                    objListValue = objList.get(objValue).toString();
                } catch (Exception ex) {
                    objListValue = "";
                }
                map.put(objValue, objListValue);
            }
            list.add(map);
        }
        ExportParams paramslist = new ExportParams(title, SheetName, ExcelType.XSSF);
        Workbook workbook1 = ExcelExportUtil.exportExcel(paramslist, entity, list);


        FileOutputStream foslist = new FileOutputStream(FilePathUtils.getRealFilePath(templatefilepath + filePathName));
        workbook1.write(foslist);

        fos.close();

        List<ColumnName> listName = new ArrayList<>();
        ColumnName param = new ColumnName();
        param.setValue(filePathName);
        listName.add(param);
        //计时器结束
        watch.stop();
        return new ResultUtils<>(listName, "操作成功", true, 1, watch.getTime());

    }

    @RequestMapping(value = "/readExcelServletbin", method = {RequestMethod.POST})
    public void readExcelServletbin(HttpServletResponse response, @RequestBody String body) throws IOException {
        response.setContentType("application/binary;charset=UTF-8");

        //excel文件路径
        String excelPath = "F:\\项目\\UploadDoc\\MB\\数据上报模版.xlsx";

        try {
            //String encoding = "GBK";
            File excel = new File(excelPath);
            if (excel.isFile() && excel.exists()) {   //判断文件是否存在

                String[] split = excel.getName().split("\\.");  //.是特殊字符，需要转义！！！！！
//                Workbook wb;
//                //根据文件后缀（xls/xlsx）进行判断
//                if ( "xls".equals(split[1])){
//                    FileInputStream fis = new FileInputStream(excel);   //文件流对象
//                    wb = new HSSFWorkbook(fis);
//                }else if ("xlsx".equals(split[1])){
//                    wb = new XSSFWorkbook(excel);
//                }else {
//                    System.out.println("文件类型错误!");
//                    return;
//                }
                Workbook wb=readExcel(excelPath);

                //开始解析
                Sheet sheet = wb.getSheetAt(0);     //读取sheet 0

                int firstRowIndex = sheet.getFirstRowNum()+1;   //第一行是列名，所以不读
                int lastRowIndex = sheet.getLastRowNum();
                System.out.println("firstRowIndex: "+firstRowIndex);
                System.out.println("lastRowIndex: "+lastRowIndex);

                for(int rIndex = firstRowIndex; rIndex <= lastRowIndex; rIndex++) {   //遍历行
                    System.out.println("rIndex: " + rIndex);
                    Row row = sheet.getRow(rIndex);
                    if (row != null) {
                        int firstCellIndex = row.getFirstCellNum();
                        int lastCellIndex = row.getLastCellNum();
                        for (int cIndex = firstCellIndex; cIndex < lastCellIndex; cIndex++) {   //遍历列
                            Cell cell = row.getCell(cIndex);
                            if (cell != null) {

                                System.out.println(getCellFormatValue(cell).toString());
                            }
                        }
                    }
                }
                wb.close();

            } else {
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/readExcelServlet", method = {RequestMethod.POST})
    public ResultUtils<List>  readExcelServlet(HttpServletResponse response, @RequestBody String body) throws IOException {
        response.setContentType("application/binary;charset=UTF-8");
        StopWatch watch = new StopWatch();
        // 计时器开始
        watch.start();
        //excel文件路径
        String excelPath = "F:\\项目\\UploadDoc\\MB\\数据上报模版.xlsx";
        Map<String, Object> maps = (Map) JSON.parse(body);

        if(maps.get("templatename")!=null){
            //获取模板名称（固定参数名称）
            excelPath= templatefilepath+maps.get("templatename").toString();
        }
        String columns[] = {"stnm","stcd","tm","q","dr"};
        if(maps.get("columns")!=null){
            columns=maps.get("columns").toString().split(",");
        }
        List<Map<String,String>> list =  new ArrayList<Map<String,String>>();
        try {
            //String encoding = "GBK";
            File excel = new File(excelPath);
            Workbook wb =null;
            Sheet sheet = null;
            Row row = null;

            String cellData = null;
            if (excel.isFile() && excel.exists()) {   //判断文件是否存在
                wb = readExcel(excelPath);
                if(wb != null){
                    //用来存放表中数据
                    list = new ArrayList<Map<String,String>>();
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
                                map.put(columns[j], cellData);
                            }
                        }else{
                            break;
                        }
                        list.add(map);
                    }
                }
//                System.out.println(JSON.toJSONString(list));
//                //遍历解析出来的list
//                for (Map<String,String> map : list) {
//                    for (Map.Entry<String,String> entry : map.entrySet()) {
//                        System.out.print(entry.getKey()+":"+entry.getValue()+",");
//                    }
////                    System.out.println();
//                }

                wb.close();

            } else {
                return new ResultUtils<>(list, "操作失败(找不到指定的文件)", true, 0, watch.getTime());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        watch.stop();
        return new ResultUtils<>(list, "操作成功", true, list.size(), watch.getTime());
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


    @RequestMapping(value = "/excelSheetExportTab", method = {RequestMethod.POST})
    @ApiOperation("Excel导出通用模板 （{\"templatename\":\"模板名称.xls\",\"maplist\":\"list数据对象；maplist名称可根据模板定义名称进行设置；其他非list参数，可自定义名称后在模板进行设置\"}）")
    public ResultUtils<List> excelSheetExportTab(HttpServletResponse response, @RequestBody String body) throws IOException {
        response.setContentType("application/binary;charset=UTF-8");
        StopWatch watch = new StopWatch();
        // 计时器开始
        watch.start();
        Map<String, Object> maps = (Map) JSON.parse(body);

        //获取模板名称（固定参数名称）
        String strTemplateName = maps.get("templatename").toString();

        String PathName = "";
        if(maps.get("pathname")!=null){
            PathName=maps.get("pathname").toString();
        }

        //实例化模板
        TemplateExportParams params = new TemplateExportParams(FilePathUtils.getRealFilePath(templatefilepath + "MB/" + strTemplateName));

        Workbook workbook = ExcelExportUtil.exportExcel(params, maps);
        File savefile = new File(FilePathUtils.getRealFilePath(templatefilepath + PathName+"/"));
        if (!savefile.exists()) {
            savefile.mkdirs();
        }
        //本地生成excel临时文件
        String filePathName = PathName+"/" + UUID.randomUUID() + ".xls";
        FileOutputStream fos = new FileOutputStream(FilePathUtils.getRealFilePath(templatefilepath + filePathName));
        workbook.write(fos);

//
//        /*
//         * 功能描述: <br>
//         * 〈〉
//         * @Param: 导出Sheet
//         * @Return: com.js.myswproject.common.utils.ResultUtils<java.util.List>
//         * @Author: 刘彬
//         * @Date: 2020/9/15 17:03
//         */
//        String title = maps.get("title").toString();
//
//        String SheetName = "Sheet2";
//        if(maps.get("sheetname")!=null){
//            SheetName=maps.get("sheetname").toString();
//        }
//        JSONArray sheetdata = (JSONArray) maps.get("sheetdata");
//        JSONArray columnName = (JSONArray) maps.get("columnname");
//        List<ExcelExportEntity> entity = new ArrayList<ExcelExportEntity>();
//
//        if (columnName.size() > 0) {
//            for (int num = 0; num < columnName.size(); num++) {
//                JSONObject obj = (JSONObject) columnName.get(num);
//                String objName = obj.get("name").toString().replaceAll("/", "").replaceAll("\n", "").replaceAll("\t", "").replaceAll("\r", "");
//                String objValue = obj.get("value").toString();
//                entity.add(new ExcelExportEntity(objName, objValue));
//            }
//        }
//        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
//        Map<String, String> map;
//        for (int j = 0; j < sheetdata.size(); j++) {
//            map = new HashMap<String, String>();
//            JSONObject objList = (JSONObject) sheetdata.get(j);
//            for (int i = 0; i < columnName.size(); i++) {
//                String objListValue = "";
//                JSONObject obj = (JSONObject) columnName.get(i);
//                String objValue = obj.get("value").toString();
//                try {
//                    objListValue = objList.get(objValue).toString();
//                } catch (Exception ex) {
//                    objListValue = "";
//                }
//                map.put(objValue, objListValue);
//            }
//            list.add(map);
//        }
//        ExportParams paramslist = new ExportParams(title, SheetName, ExcelType.XSSF);
//        Workbook workbook1 = ExcelExportUtil.exportExcel(paramslist, entity, list);
//
//
//        FileOutputStream foslist = new FileOutputStream(FilePathUtils.getRealFilePath(templatefilepath + filePathName));
//        workbook1.write(foslist);

        fos.close();

        List<ColumnName> listName = new ArrayList<>();
        ColumnName param = new ColumnName();
        param.setValue(filePathName);
        listName.add(param);
        //计时器结束
        watch.stop();
        return new ResultUtils<>(listName, "操作成功", true, 1, watch.getTime());

    }

}
