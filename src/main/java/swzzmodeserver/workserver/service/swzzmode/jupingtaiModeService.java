package swzzmodeserver.workserver.service.swzzmode;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import swzzmodeserver.tools.javalog;
import swzzmodeserver.workserver.pojo.swzzmode.*;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class jupingtaiModeService {

    @Value("${http.urlPath.HuishuiApi}")
    private String HuishuiApi;

    @Autowired
    private ES_ZHANDIANDATAService service;

    @Autowired
    private HuishuiApiService huishuiApiService;

    @Value("${file.path.templatefilepath}")
    private String filePathName;

    //第一步：设置依据时间和计算时长后，自动获取边界条件，返回边界条件的dd_id和任务编号
    public Map<String, Object> SetAreaPredictInfo(String stime, int num, String jydatatype, String gcdatatype, String scwdatatype,String DD_DISTRIBY) {
        Map<String, Object> obj = new HashMap<>();
        // 定义时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // 解析时间字符串为LocalDateTime对象
        LocalDateTime startTime = LocalDateTime.parse(stime, formatter);

        // 加上指定的小时数
        LocalDateTime endTime = startTime.plusHours(num);

        // 将结果格式化为字符串
        String etime = endTime.format(formatter);
        try {
            String _dd_id = huishuiApiService.upDataZhandianData(stime, etime, jydatatype, gcdatatype, scwdatatype);
            String result = "";
            if (!_dd_id.equals("")) {// 边界入库成功了，可以计算
                obj.put("dd_id", _dd_id);
                new javalog().writelog("模型开始计算，参数（stime：" + stime + ",etime：" + etime + ",jydatatype：" + jydatatype+ ",gcdatatype：" + gcdatatype + ",scwdatatype：" + scwdatatype + "）", filePathName);
                
                result = huishuiApiService.modelSetTaskNew(stime, etime, num, _dd_id, DD_DISTRIBY);
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> mapList = new HashMap<>();
                mapList = objectMapper.readValue(result, new TypeReference<Map<String, Object>>() {});
                Map<String, Object> info = (Map<String, Object>) mapList.get("info");

                Boolean success = (Boolean) info.get("success");
                 if (success) {// 调用接口成功
                    String taskID = mapList.get("taskID").toString();
                    obj.put("taskID", taskID);
                    obj.put("message", "设置任务成功");
                    obj.put("success", success);
                    new javalog().writelog("任务编号：" + taskID, filePathName);
                } else {
                    System.out.println("设置任务报错：" + result);
                    obj.put("message", "模型不在线");                    
                    obj.put("success", success);
                }   
            }
        } catch (IOException e) {
            obj.put("message", "设置边界数据报错");
            obj.put("success", false);
        }
        return obj;
    }
    //第二步：根据任务编号获取模型计算状态
    public Map<String, Object> modelGetTaskStatus(String taskID,String dd_id) {        
        try {
            int Status = 0;
            Map<String, Object> mapList = huishuiApiService.GetTaskStatus(taskID);
            Map<String, Object> info = (Map<String, Object>) mapList.get("info");
            Boolean success = (Boolean) info.get("success");
            if (!success) {// 调用接口成功
                int code = (int) info.get("code");
                if (code == -1) {
                    // 计算不下去，模型报错了
                    Status = code;
                } else {
                    if (info.get("modelTime") != null) {
                        System.out.println("模型计算进度：" + info.get("modelTime"));
                        Status = 0;
                    }
                }
            } else {
                // ****************************************************************算完了，保存结果                
                Status = 1;
                new javalog().writelog("任务" + taskID + "计算完成，结果：" + mapList, filePathName);                
            }
            mapList.put("status", Status);
            mapList.put("dd_id", dd_id);
            return mapList;
        } catch (Exception e) {
            return null;
        }
    }
    //第三步：根据任务编号获取模型计算结果，并保存到数据库
    public int onResultOk(String _dd_id, String stime, String etime, String taskID, String DD_DISTRIBY) {
        int rows = 0;
        // 保存数据
        List<BDMS_PREDICTPojo> bdms_predictSql = huishuiApiService.getModelResult(_dd_id, stime, etime, taskID);
        // var bdms_predictSqlNew=[];
        // bdms_predictSqlNew.push(bdms_predictSql[0]);
        String bdms_predictSqlStr = "";
        // 创建ObjectMapper实例
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // 将List对象转换为JSON字符串
            bdms_predictSqlStr = objectMapper.writeValueAsString(bdms_predictSql);
            System.out.println("JSON String: " + bdms_predictSqlStr);
        } catch (IOException e) {
            // e.printStackTrace();
        }
        // 定义时间格式
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyyMMddHH");
        long ms = 0;
        Date startDate, endDate;
        String formattedEndDate = null;
        try {
            // 解析时间字符串为Date对象
            startDate = dateFormat.parse(stime);
            endDate = dateFormat.parse(etime);
            formattedEndDate = outputFormat.format(startDate);
            // 计算时间差（毫秒）
            ms = endDate.getTime() - startDate.getTime();
        } catch (ParseException e) {
        }
        String title = formattedEndDate + "水情预报(应急分流预案)";
        if (ms > 0) {
            int hour = (int) Math.floor(ms / 1000 / 60 / 60);
            DD_SOLUTIONPojo ddobj = new DD_SOLUTIONPojo();
            ddobj.setID(_dd_id);
            ddobj.setDD_ID(_dd_id);
            ddobj.setDD_NAME(title);
            ddobj.setDD_BY("自动预报员");
            ddobj.setDD_TM(stime);
            ddobj.setDD_CARRYTM(dateFormat.format(new Date()));
            ddobj.setDD_NOTE("应急分流预案");
            ddobj.setDD_EVALUE("1");
            ddobj.setDD_CHECKBY(etime);
            ddobj.setDD_STANA(String.valueOf(hour));
            ddobj.setDD_FOR(taskID);
            if (!DD_DISTRIBY.equals("")) {
                ddobj.setDD_DISTRIBY(DD_DISTRIBY);
            }
            service.FH_inset_ModifyApi(bdms_predictSqlStr, ddobj, false, _dd_id);
            new javalog().writelog("方案入库成功，方案编号：" + _dd_id, filePathName);
            rows++;
        }
        return rows;
    }    
}