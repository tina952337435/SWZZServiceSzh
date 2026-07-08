package swzzmodeserver.workserver.service.swzzmode;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import swzzmodeserver.tools.ComputeHL;
import swzzmodeserver.tools.ObjUtils;
import swzzmodeserver.tools.apihelper;
import swzzmodeserver.tools.javalog;
import swzzmodeserver.workserver.data.swzzflood.RTSQData;
import swzzmodeserver.workserver.data.swzzmode.*;
import swzzmodeserver.workserver.pojo.Huishui.GetAreaXSLPojo;
import swzzmodeserver.workserver.pojo.Huishui.GetSubjectListPojo;
import swzzmodeserver.workserver.pojo.swzzflood.ST_WAS_RPojo;
import swzzmodeserver.workserver.pojo.swzzmode.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class HuishuiApiService {
    @Autowired
    private ES_ZHANDIANDATAData data;

    @Value("${http.urlPath.HuishuiApi}")
    private String HuishuiApi;

    @Autowired
    private ES_ZHANDIANDATAService service;

    @Autowired
    private ES_MODELFANGANZHANData esModelfanganzhanData;

    @Autowired
    private ES_MODELFANGANData esModelfanganData;

    @Autowired
    private ES_JISUANZHANData esJisuanzhanData;

    @Autowired
    private ST_WATERSTORAGE_BData esWaterstoragebData;

    @Autowired
    private ES_MODELGUANLIANData esModelGuanlianData;

    @Value("${file.path.templatefilepath}")
    private String filePathName;

    public int startHuishuiJisuan(String stime, int num, String jydatatype, String gcdatatype, String scwdatatype,
            String DD_DISTRIBY) {
        // 定义时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // 解析时间字符串为LocalDateTime对象
        LocalDateTime startTime = LocalDateTime.parse(stime, formatter);

        // 加上指定的小时数
        LocalDateTime endTime = startTime.plusHours(num);

        // 将结果格式化为字符串
        String etime = endTime.format(formatter);
        String _dd_id = upDataZhandianData(stime, etime, jydatatype, gcdatatype, scwdatatype);
        int _num = 0;
        if (!_dd_id.equals("")) {// 边界入库成功了，可以计算
            new javalog().writelog("模型开始计算，参数（stime：" + stime + ",etime：" + etime + ",jydatatype：" + jydatatype
                    + ",gcdatatype：" + gcdatatype + ",scwdatatype：" + scwdatatype + "）", filePathName);
            _num = modelSetTask(stime, etime, num, _dd_id, DD_DISTRIBY);
        }
        return _num;
    }

    // ***************************************************************初始化数据入库
    public String upDataZhandianData(String stime, String etime, String jydatatype, String gcdatatype,
            String scwdatatype) {
        new javalog().writelog("开始调用upDataZhandianData方法", filePathName);
        String _dd_id = ObjUtils.getTableID();
        String username = "管理员";
        int rows = service.MODIFY_MODEZHANDData(stime, etime, _dd_id, jydatatype, gcdatatype, scwdatatype, username);
        if (rows > 0) {// 边界入库成功了，可以计算
            new javalog().writelog("调用upDataZhandianData方法成功，方案编号_dd_id：" + _dd_id, filePathName);
        } else {
            _dd_id = "";
            new javalog().writelog("调用upDataZhandianData方法不成功成功，影响行数：" + rows, filePathName);
        }
        return _dd_id;
    }

    // 获取计算专题列表GetSubjectList：获取模型的基础信息，包括模型步长、专题名称等
    // 获取doc跟subID，后面的接口调用需要这个doc跟subID
    public GetSubjectListPojo modelGetSubjectList() {
        String parmasMap = "{\"version\":\"1\",\"api\":\"GetSubjectList\"}";
        HashMap<String, Object> header = new HashMap<>();
        header.put("Content-Type", "application/json;charset=UTF-8");
        String result = apihelper.apipost(HuishuiApi, parmasMap, header);
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> mapList = new HashMap<>();

        GetSubjectListPojo pojo = new GetSubjectListPojo();
        try {
            mapList = objectMapper.readValue(result, new TypeReference<Map<String, Object>>() {
            });
            Map<String, Object> _id = (Map<String, Object>) mapList.get("_id");
            String docid = _id.get("doc").toString();
            int sbjID = 0, timeStepHydro = 0, timeStep = 0;
            // 获取 data 部分的值
            List<Map<String, Object>> dataList = (List<Map<String, Object>>) mapList.get("data");
            // 遍历 data 列表
            for (Map<String, Object> dataItem : dataList) {
                // 检查 bCur 字段是否等于 true
                Boolean bCur = (Boolean) dataItem.get("bCur");
                if (bCur != null && bCur) {
                    sbjID = (int) dataItem.get("id");
                    timeStepHydro = (int) dataItem.get("timeStepHydro");
                    timeStep = (int) dataItem.get("timeStep");
                }
            }
            pojo.setDocid(docid);
            pojo.setSbjID(sbjID);
            pojo.setTimeStepHydro(timeStepHydro);
            pojo.setTimeStep(timeStep);

            System.out.println("modelGetSubjectList：" + pojo);
        } catch (IOException e) {
            System.out.println("调用" + parmasMap + "接口报错,接口返回结果是：" + result);
        }
        return pojo;
    }

    // 获取水文序列列表GetHydroSeriesList
    public int modeGetHydroSeriesList() {
        String parmasMap = "{\"version\":\"1\",\"api\":\"GetHydroSeriesList\"}";
        HashMap<String, Object> header = new HashMap<>();
        header.put("Content-Type", "application/json;charset=UTF-8");
        String result = apihelper.apipost(HuishuiApi, parmasMap, header);
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> mapList = new HashMap<>();
        int huishuimodehdyroID = 0;
        try {
            mapList = objectMapper.readValue(result, new TypeReference<Map<String, Object>>() {
            });
            // 获取 data 部分的值
            List<Map<String, Object>> dataList = (List<Map<String, Object>>) mapList.get("data");
            // 遍历 data 列表
            for (Map<String, Object> dataItem : dataList) {
                // 检查 bCur 字段是否等于 true
                Boolean bCur = (Boolean) dataItem.get("bCur");
                if (bCur != null && bCur) {
                    huishuimodehdyroID = (int) dataItem.get("id");
                }
            }
            System.out.println("获取的水文序列：" + huishuimodehdyroID);
        } catch (IOException e) {
            System.out.println("调用" + parmasMap + "接口报错,接口返回结果是：" + result);
        }
        return huishuimodehdyroID;
    }

    // 获取控制调度对象列表 GetScheduleObjList
    public List<Map<String, Object>> modelGetScheduleObjList(String docid, int sbjID) {
        String parmasMap = "{\"version\":\"1\",\"doc\":\"" + docid + "\",\"sbjID\":" + sbjID
                + ",\"api\":\"GetScheduleObjList\"}";
        HashMap<String, Object> header = new HashMap<>();
        header.put("Content-Type", "application/json;charset=UTF-8");
        String result = apihelper.apipost(HuishuiApi, parmasMap, header);
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> mapList = new HashMap<>();
        List<Map<String, Object>> dataList = new ArrayList<>();
        try {
            mapList = objectMapper.readValue(result, new TypeReference<Map<String, Object>>() {
            });
            dataList = (List<Map<String, Object>>) mapList.get("data");
        } catch (IOException e) {
            System.out.println("调用" + parmasMap + "接口报错,接口返回结果是：" + result);
        }
        return dataList;
    }

    public boolean isNumeric(String str) {
        // 正则表达式，匹配整数、浮点数（包括负数和正数）
        String regex = "^-?\\d+(\\.\\d+)?$";
        return str.matches(regex);
    }

    // 主图形：获取预制输出项列表GetPresetResultInfoList
    public List<Map<String, Object>> modelGetPresetResultInfoList(String docid, int sbjID) {
        String parmasMap = "{\"version\":\"1\",\"doc\":\"" + docid + "\",\"sbjID\":" + sbjID
                + ",\"api\":\"GetPresetResultInfoList\"}";
        HashMap<String, Object> header = new HashMap<>();
        header.put("Content-Type", "application/json;charset=UTF-8");
        String result = apihelper.apipost(HuishuiApi, parmasMap, header);
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> mapList = new HashMap<>();
        List<Map<String, Object>> dataList = new ArrayList<>();
        try {
            mapList = objectMapper.readValue(result, new TypeReference<Map<String, Object>>() {
            });
            dataList = (List<Map<String, Object>>) mapList.get("data");
        } catch (IOException e) {
            System.out.println("调用" + parmasMap + "接口报错,接口返回结果是：" + result);
        }
        return dataList;
    }

    // 设置预报调度任务SetTask
    public int modelSetTask(String stime, String etime, int hour, String dd_id, String DD_DISTRIBY) {
        int resultRows = 0;
        GetSubjectListPojo subjectListPojo = modelGetSubjectList();
        List<ES_ZHANDIANDATAPojo> bjData = data.selectList(null, null, null, dd_id, null, null, null);
        List<Map<String, Object>> bjZhan = modelGetScheduleObjList(subjectListPojo.getDocid(),
                subjectListPojo.getSbjID());
        new javalog().writelog("获取获取计算专题列表成功", filePathName);

        int hydroID = modeGetHydroSeriesList();
        int timeStep = subjectListPojo.getTimeStep();// 步数
        int timeStepHydro = subjectListPojo.getTimeStepHydro();// 步数
        int predictSteps = (3600 * hour) / timeStepHydro;// 预见期
        List<Map<String, Object>> scheduleObjs = new ArrayList<>();
        for (int num = 0; num < bjZhan.size(); num++) {
            Map<String, Object> res = bjZhan.get(num);
            int id = (int) res.get("id");
            if (id == 30000000) {
                break;
            }
            int valType = (int) res.get("type");
            int type = (int) res.get("type");
            if (valType == 0) {
                valType = 22;// 降雨量
            }
            // else if(res.type==3){
            // valType=4;
            // }
            Map<String, Object> item = new HashMap<>();
            item.put("id", id);
            item.put("type", type);
            item.put("valType", valType);
            List<ES_ZHANDIANDATAPojo> bjDataTemp = bjData.stream().filter(p -> {
                return p.getZHANID().equals(res.get("id").toString());
            }).collect(Collectors.toList());
            ArrayList<Double> vals = new ArrayList<>();
            if (bjDataTemp.size() == 0) {// 无数据的用0代替
                for (int _index = 0; _index < predictSteps; _index++) {
                    vals.add(0.0);
                }
                // console.error(res.name,bjDataTemp,vals);
            } else {
                for (int _index = 0; _index < bjDataTemp.size(); _index++) {
                    ES_ZHANDIANDATAPojo bj = bjDataTemp.get(_index);
                    String gcDATA = bj.getZHANDATA();
                    if (type == 3) {// 工程
                        if (!isNumeric(gcDATA)) {
                            List<Map<String, Object>> planList = (List<Map<String, Object>>) res.get("plan");
                            List<Map<String, Object>> planTemp = new ArrayList<>();
                            for (Map<String, Object> dataItem : planList) {
                                // 检查 bCur 字段是否等于 true
                                String name = dataItem.get("name").toString();
                                if (name.equals(gcDATA)) {
                                    planTemp.add(dataItem);
                                }
                            }
                            item.put("valType", 4);// 4代表是采用规则调度
                            int valIdex = planTemp.size() > 0 ? (int) planTemp.get(0).get("index") : 0;
                            if (_index == 0) {
                                vals.add(Double.valueOf(valIdex));
                                // break;//跳出循环
                            }
                        } else {
                            item.put("valType", 2);// 2代表是采用流量调度
                            vals.add(Double.valueOf(gcDATA));
                            // break;//跳出循环
                        }
                    } else if (type == 0) {// 雨量需要小时转5分钟
                        double drp = Double.valueOf(gcDATA) / 12;
                        for (int minu = 0; minu < 12; minu++) {
                            vals.add(drp);
                        }
                    } else {// 边界潮水位
                        if (_index > 0) {// 依据时间的不要
                            vals.add(Double.valueOf(bj.getZHANDATA()));
                        }
                    }
                }
            }
            System.out.println(id + "，" + res.get("name") + "，" + vals.size());
            item.put("vals", vals);
            scheduleObjs.add(item);
        }

        // 设置预报调度数据
        // scheduleObjs=[];
        Map<String, Object> taskData = new HashMap<>();
        taskData.put("version", 1);
        taskData.put("api", "SetTask");
        taskData.put("user", "管理员");
        taskData.put("hydroID", hydroID);
        taskData.put("doc", subjectListPojo.getDocid());
        taskData.put("sbjID", subjectListPojo.getSbjID());
        taskData.put("name", "管理员");

        List<Map<String, Object>> actions = new ArrayList<>();
        Map<String, Object> actionsItem = new HashMap<>();
        actionsItem.put("type", "预报调度");
        actionsItem.put("time", stime);
        actionsItem.put("predictSteps", predictSteps);
        actionsItem.put("scheduleObjs", scheduleObjs);
        actions.add(actionsItem);
        taskData.put("actions", actions);

        String parmasMap = "";// taskData.toString();
        // 使用Jackson库将taskData转换为JSON格式的String
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            parmasMap = objectMapper.writeValueAsString(taskData);
            System.out.println("传给模型的参数：" + parmasMap);
            new javalog().writelog("传给模型的参数，parmasMap（" + parmasMap + "）", filePathName);
        } catch (IOException e) {
            System.out.println("解析参数报错：" + e.getMessage());
            new javalog().writelog("解析参数报错：" + e.getMessage(), filePathName);
        }
        HashMap<String, Object> header = new HashMap<>();
        header.put("Content-Type", "application/json;charset=UTF-8");
        String result = apihelper.apipost(HuishuiApi, parmasMap, header);

        Map<String, Object> mapList = new HashMap<>();
        try {
            new javalog().writelog("设置任务接口返回结果：" + result, filePathName);
            mapList = objectMapper.readValue(result, new TypeReference<Map<String, Object>>() {
            });
            Map<String, Object> info = (Map<String, Object>) mapList.get("info");

            Boolean success = (Boolean) info.get("success");
            if (success) {// 调用接口成功
                String taskID = mapList.get("taskID").toString();
                System.out.println("任务编号：" + taskID);
                new javalog().writelog("任务编号：" + taskID, filePathName);
                // **************************************************************定时获取模型的状态
                int TaskStatus = 0;
                while (TaskStatus == 0) {// 等于1代表计算完成
                    TaskStatus = modelGetTaskStatus(taskID);
                    System.out.println("任务状态：" + TaskStatus);
                    new javalog().writelog("任务" + taskID + "的状态：" + TaskStatus, filePathName);
                }
                if (TaskStatus == 1) {
                    new javalog().writelog("任务" + taskID + "计算完成，状态：" + TaskStatus, filePathName);
                    resultRows = onResultOk(dd_id, stime, etime, taskID, DD_DISTRIBY);
                } else {
                    System.out.println("任务报错，模型计算不了");
                }
            } else {
                System.out.println("设置任务报错：" + result);
            }
        } catch (IOException e) {
            System.out.println("调用" + parmasMap + "接口报错,接口返回结果是：" + result);
        }
        return resultRows;
    }

    // 获取模型任务状态GetTaskStatus
    public int modelGetTaskStatus(String taskID) {
        int Status = 0;
        String parmasMap = "{\"version\": 1,\"taskID\":\"" + taskID + "\",\"api\": \"GetTaskStatus\"}";
        HashMap<String, Object> header = new HashMap<>();
        header.put("Content-Type", "application/json;charset=UTF-8");
        String result = apihelper.apipost(HuishuiApi, parmasMap, header);
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> mapList = new HashMap<>();
        List<Map<String, Object>> dataList = new ArrayList<>();
        try {
            mapList = objectMapper.readValue(result, new TypeReference<Map<String, Object>>() {
            });
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
                System.out.println("模型计算完成：" + result);
                // ****************************************************************保存结果
                Status = 1;
            }
        } catch (IOException e) {
            System.out.println("调用" + parmasMap + "接口报错,接口返回结果是：" + result);
        }
        return Status;
    }

    // 计算完成之后保存计算结果
    public int onResultOk(String _dd_id, String stime, String etime, String taskID, String DD_DISTRIBY) {
        int rows = 0;
        // 保存数据
        List<BDMS_PREDICTPojo> bdms_predictSql = getModelResult(_dd_id, stime, etime, taskID);
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
        String title = formattedEndDate + "水情预报(自动预报)";
        if (ms > 0) {
            int hour = (int) Math.floor(ms / 1000 / 60 / 60);
            DD_SOLUTIONPojo ddobj = new DD_SOLUTIONPojo();
            ddobj.setID(_dd_id);
            ddobj.setDD_ID(_dd_id);
            ddobj.setDD_NAME(title);
            ddobj.setDD_BY("自动预报员");
            ddobj.setDD_TM(stime);
            ddobj.setDD_CARRYTM(dateFormat.format(new Date()));
            ddobj.setDD_NOTE("自动预报");
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

    public List<BDMS_PREDICTPojo> getModelResult(String dd_id, String stime, String etime, String taskID) {
        GetSubjectListPojo getSubjectListPojo = modelGetSubjectList();
        // 获取模型的成果
        List<Map<String, Object>> zhuData = modelGetPresetResultInfoList(getSubjectListPojo.getDocid(),
                getSubjectListPojo.getSbjID());
        List<BDMS_PREDICTPojo> bdms_predictSql = new ArrayList<>();
        for (int num = 0; num < zhuData.size(); num++) {
            String id = zhuData.get(num).get("id").toString();
            int dataType = (int) zhuData.get(num).get("type");
            String stcd = zhuData.get(num).get("stcd").toString();
            stcd = stcd != "" ? stcd.replace(".", "") : id;
            String parmasMap = "{\"version\": 1,\"api\": \"GetResultPresetByTimePeriod\",\"id\": \"" + id
                    + "\",\"startTM\": \"" + stime + "\",\"endTM\": \"" + etime + "\",\"taskID\": \"" + taskID + "\" }";
            HashMap<String, Object> header = new HashMap<>();
            header.put("Content-Type", "application/json;charset=UTF-8");
            String result = apihelper.apipost(HuishuiApi, parmasMap, header);
            ObjectMapper objectMapper = new ObjectMapper();
            List<Map<String, Object>> dataList = new ArrayList<>();
            try {
                // 解析JSON字符串为JsonNode
                JsonNode rootNode = objectMapper.readTree(result);
                System.out.println("GetResultPresetByTimePeriod接口调用成功id：" + id + ",stcd:" + stcd);
                // 获取data节点
                JsonNode data = rootNode.path("data");
                if (data != null) {
                    if (data.get("times") != null) {
                        // 获取vals数组
                        JsonNode vals = data.path("vals");
                        // 获取times数组
                        JsonNode times = data.path("times");
                        // 定义时间格式
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        for (int _index = 0; _index < times.size(); _index++) {
                            String _tm = times.get(_index).asText();
                            // 将字符串解析为Instant对象
                            Instant instant = Instant.parse(_tm);
                            // 将Instant转换为ZonedDateTime（指定时区为UTC）
                            ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of("UTC"));
                            // 转换为北京时间（东八区，UTC+8）
                            ZonedDateTime beijingDateTime = zonedDateTime
                                    .withZoneSameInstant(ZoneId.of("Asia/Shanghai"));
                            // 将ZonedDateTime转换为LocalDateTime
                            LocalDateTime localDateTime = beijingDateTime.toLocalDateTime();
                            // 格式化为指定格式的字符串
                            String formattedDateTimeString = localDateTime.format(formatter);

                            double _data = vals.get(_index).asDouble();
                            BDMS_PREDICTPojo pREDICT = new BDMS_PREDICTPojo();
                            if (dataType == 1) {// 水位，取五分钟的数据
                                String uuid = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16);
                                pREDICT.setID(uuid);
                                pREDICT.setUSERID("自动预报");
                                pREDICT.setSTCD(stcd);
                                pREDICT.setYMDHM(formattedDateTimeString);
                                pREDICT.setPLAN_N(dd_id);
                                pREDICT.setDATA_TYPE(String.valueOf(dataType));
                                pREDICT.setDATA(Float.parseFloat(String.format("%.2f", _data)));
                                bdms_predictSql.add(pREDICT);
                            } else {// 其他取一小时一个数据
                                int minute = localDateTime.getMinute(); // 获取分钟部分
                                if (minute == 0) {
                                    if (dataType == 15 || dataType == 14) {// 水面积和蓄量需要转单位：万m³转百万m³，平方米转平方公里
                                        _data = _data / 1000000;
                                    }
                                    String uuid = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16);
                                    pREDICT.setID(uuid);
                                    pREDICT.setUSERID("自动预报");
                                    pREDICT.setSTCD(stcd);
                                    pREDICT.setYMDHM(formattedDateTimeString);
                                    pREDICT.setPLAN_N(dd_id);
                                    pREDICT.setDATA_TYPE(String.valueOf(dataType));
                                    pREDICT.setDATA(Float.parseFloat(String.format("%.2f", _data)));
                                    bdms_predictSql.add(pREDICT);
                                }
                            }
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("调用" + parmasMap + "接口报错,接口返回结果是：" + result);
            }

        }
        return bdms_predictSql;
    }

    // 提取模拟期结果主图形结果
    public List<BDMS_PREDICTPojo> getModelResultMoni(String dd_id, String stime, String etime, String taskID,
            Integer sjbID, Integer hydroID) {
        GetSubjectListPojo getSubjectListPojo = modelGetSubjectList();
        // 获取模型的成果
        List<Map<String, Object>> zhuData = modelGetPresetResultInfoList(getSubjectListPojo.getDocid(),
                getSubjectListPojo.getSbjID());
        List<BDMS_PREDICTPojo> bdms_predictSql = new ArrayList<>();
        for (int num = 0; num < zhuData.size(); num++) {
            String id = zhuData.get(num).get("id").toString();
            int dataType = (int) zhuData.get(num).get("type");
            String stcd = zhuData.get(num).get("stcd").toString();
            stcd = stcd != "" ? stcd.replace(".", "") : id;
            String parmasMap = "{\"version\": 1,\"api\": \"GetResultPresetByTimePeriod\",\"id\": \"" + id
                    + "\",\"startTM\": \"" + stime + "\",\"endTM\": \"" + etime + "\",\"taskID\": \"" + taskID
                    + "\",\"sbjID\":" + sjbID + ",\"hydroID\":" + hydroID + " }";
            HashMap<String, Object> header = new HashMap<>();
            header.put("Content-Type", "application/json;charset=UTF-8");
            String result = apihelper.apipost(HuishuiApi, parmasMap, header);
            ObjectMapper objectMapper = new ObjectMapper();
            List<Map<String, Object>> dataList = new ArrayList<>();
            try {
                // 解析JSON字符串为JsonNode
                JsonNode rootNode = objectMapper.readTree(result);
                System.out.println("GetResultPresetByTimePeriod接口调用成功id：" + id + ",参数:" + parmasMap);
                // 获取data节点
                JsonNode data = rootNode.path("data");
                if (data != null) {
                    if (data.get("times") != null) {
                        // 获取vals数组
                        JsonNode vals = data.path("vals");
                        // 获取times数组
                        JsonNode times = data.path("times");
                        // 定义时间格式
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        for (int _index = 0; _index < times.size(); _index++) {
                            String _tm = times.get(_index).asText();
                            // 将字符串解析为Instant对象
                            Instant instant = Instant.parse(_tm);
                            // 将Instant转换为ZonedDateTime（指定时区为UTC）
                            ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of("UTC"));
                            // 转换为北京时间（东八区，UTC+8）
                            ZonedDateTime beijingDateTime = zonedDateTime
                                    .withZoneSameInstant(ZoneId.of("Asia/Shanghai"));
                            // 将ZonedDateTime转换为LocalDateTime
                            LocalDateTime localDateTime = beijingDateTime.toLocalDateTime();
                            // 格式化为指定格式的字符串
                            String formattedDateTimeString = localDateTime.format(formatter);

                            double _data = vals.get(_index).asDouble();
                            BDMS_PREDICTPojo pREDICT = new BDMS_PREDICTPojo();
                            if (dataType == 1) {// 水位，取五分钟的数据
                                String uuid = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16);
                                pREDICT.setID(uuid);
                                pREDICT.setUSERID("自动预报");
                                pREDICT.setSTCD(stcd);
                                pREDICT.setYMDHM(formattedDateTimeString);
                                pREDICT.setPLAN_N(dd_id);
                                pREDICT.setDATA_TYPE(String.valueOf(dataType));
                                pREDICT.setDATA(Float.parseFloat(String.format("%.2f", _data)));
                                bdms_predictSql.add(pREDICT);
                            } else {// 其他取一小时一个数据
                                int minute = localDateTime.getMinute(); // 获取分钟部分
                                if (minute == 0) {
                                    if (dataType == 15 || dataType == 14) {// 水面积和蓄量需要转单位：万m³转百万m³，平方米转平方公里
                                        _data = _data / 1000000;
                                    }
                                    String uuid = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16);
                                    pREDICT.setID(uuid);
                                    pREDICT.setUSERID("自动预报");
                                    pREDICT.setSTCD(stcd);
                                    pREDICT.setYMDHM(formattedDateTimeString);
                                    pREDICT.setPLAN_N(dd_id);
                                    pREDICT.setDATA_TYPE(String.valueOf(dataType));
                                    pREDICT.setDATA(Float.parseFloat(String.format("%.2f", _data)));
                                    bdms_predictSql.add(pREDICT);
                                }
                            }
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("调用" + parmasMap + "接口报错,接口返回结果是：" + result);
            }

        }
        return bdms_predictSql;
    }

    // 获取某时刻全流域计算结果GetResultAllModelByTime
    public Map<String, Object> modeGetResultAllModelByTime(String taskID, String time) {
        String parmasMap = "{\"version\":\"1\",\"api\":\"GetResultAllModelByTime\",\"taskID\":\"" + taskID
                + "\",\"time\":\"" + time + "\"}";
        HashMap<String, Object> header = new HashMap<>();
        header.put("Content-Type", "application/json;charset=UTF-8");
        String result = apihelper.apipost(HuishuiApi, parmasMap, header);
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> mapList = new HashMap<>();
        Map<String, Object> resultsList = new HashMap<>();
        try {
            mapList = objectMapper.readValue(result, new TypeReference<Map<String, Object>>() {
            });
            Map<String, Object> info = (Map<String, Object>) mapList.get("info");

            Boolean success = (Boolean) info.get("success");
            if (success) {// 调用接口成功
                // 获取 data 部分的值
                resultsList = (Map<String, Object>) mapList.get("results");
                System.out.println("获取全流域的结果为：" + resultsList);
            }
        } catch (IOException e) {
            System.out.println("调用" + parmasMap + "接口报错,接口返回结果是：" + result);
        }
        return resultsList;
    }

    // 获取某时刻全流域计算结果并保存为JSON文件 GetResultAllModelByTime
    public boolean saveResultAllModelByTime(String time, int objID, int hydroID, String taskID) {
        boolean isSuccess = false;
        String parmasMap = "{"
                + "\"version\": 1,"
                + "\"api\": \"GetResultAllModelByTime\","
                + "\"time\": \"" + time + "\","
                + "\"objID\": " + objID + ","
                + "\"hydroID\": " + hydroID + ","
                + "\"taskID\": \"" + taskID + "\""
                + "}";
        HashMap<String, Object> header = new HashMap<>();
        header.put("Content-Type", "application/json;charset=UTF-8");
        String result = apihelper.apipost(HuishuiApi, parmasMap, header);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, Object> mapList = objectMapper.readValue(result, new TypeReference<Map<String, Object>>() {
            });
            Map<String, Object> info = (Map<String, Object>) mapList.get("info");
            Boolean success = (Boolean) info.get("success");
            if (success) {
                // 将time转为yyyyMMddHHmmss格式作为文件名
                String fileName = time.replaceAll("[-: ]", "") + ".json";
                String saveDir = filePathName + "GetResultAllModelByTime/";
                java.io.File dir = new java.io.File(saveDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                String filePath = saveDir + fileName;
                java.io.FileWriter fileWriter = new java.io.FileWriter(filePath);
                fileWriter.write(result);
                fileWriter.close();
                isSuccess = true;
                new javalog().writelog("GetResultAllModelByTime接口调用成功，结果已保存至：" + filePath, filePathName);
                System.out.println("GetResultAllModelByTime结果已保存至：" + filePath);
            } else {
                new javalog().writelog("GetResultAllModelByTime接口调用失败，info：" + info, filePathName);
                System.out.println("GetResultAllModelByTime接口调用失败：" + result);
            }
        } catch (IOException e) {
            System.out.println("调用" + parmasMap + "接口报错,接口返回结果是：" + result);
            new javalog().writelog("调用GetResultAllModelByTime接口报错：" + e.getMessage(), filePathName);
        }
        return isSuccess;
    }

    // 更新模型调度工程（调度控制要素）方案预案库
    public int modifyModePlan() {
        int rows = 0;
        GetSubjectListPojo subjectListPojo = modelGetSubjectList();
        List<Map<String, Object>> bjZhan = modelGetScheduleObjList(subjectListPojo.getDocid(),
                subjectListPojo.getSbjID());

        List<ES_MODELFANGANZHANPojo> listZhan = new ArrayList<>();
        List<ES_MODELFANGANPojo> listFang = new ArrayList<>();
        for (int num = 0; num < bjZhan.size(); num++) {
            Map<String, Object> res = bjZhan.get(num);
            int id = (int) res.get("id");
            System.out.println("站点编码: " + id + ", ID: " + id);
            if (id == 30000000) {
                Object regionsObj = res.get("regions");
                if (regionsObj != null) {
                    String regionsJsonStr = JSON.toJSONString(regionsObj);
                    // 3. 再将 JSON 字符串解析为 Fastjson 的 JSONArray
                    JSONArray regionsArray = JSON.parseArray(regionsJsonStr);
                    // 3. 遍历读取里面的内容
                    for (int i = 0; i < regionsArray.size(); i++) {
                        JSONObject regionObj = regionsArray.getJSONObject(i);

                        // 读取具体字段，例如读取 "name" 和 "id"
                        String name = regionObj.getString("name");
                        String description = regionObj.getString("description");
                        Integer regionID = regionObj.getInteger("id");
                        // scheduleObjs 在 JSON 中是数字数组，例如 [123, 456]
                        JSONArray scheduleObjsArray = regionObj.getJSONArray("scheduleObjs");
                        // --- 读取 plan (数组/列表) ---
                        // plan 在 JSON 中是一个对象数组，包含 id, name, type 等
                        JSONArray planArray = regionObj.getJSONArray("plan");
                        if (planArray != null) {
                            System.out.println("包含的预案信息:");
                            String newFa_name = "";
                            for (int k = 1; k < planArray.size(); k++) {// 索引0个不需要
                                JSONObject planObj = planArray.getJSONObject(k);
                                // 读取 plan 里面的具体字段
                                String planIndex = planObj.getString("index");
                                String planName = planObj.getString("name");
                                String planDescription = planObj.getString("description");
                                JSONArray planIndexes = planObj.getJSONArray("planIndexes");
                                for (int j = 0; j < planIndexes.size(); j++) {
                                    ES_MODELFANGANZHANPojo zhanPojo = new ES_MODELFANGANZHANPojo();
                                    zhanPojo.setZHANID(scheduleObjsArray.getString(j));// 站码
                                    zhanPojo.setZHANNAME(planDescription);// 站名
                                    zhanPojo.setFA_ID(planIndex);// 方案编号
                                    zhanPojo.setNORMAL(planName);
                                    zhanPojo.setSPECIAL(name);// 分区

                                    Object czObj = planIndexes.get(j);
                                    if (czObj != null) {
                                        zhanPojo.setCZ(((Number) czObj).doubleValue());
                                    } else {
                                        zhanPojo.setCZ(null); // 或者设置默认值 0.0
                                    }
                                    listZhan.add(zhanPojo);

                                    newFa_name += scheduleObjsArray.getString(j) + ",";
                                }

                                double maxDrp = 0;
                                if (planName.equals("日常调度")) {
                                    maxDrp = 50;
                                } else if (planName.equals("防汛防台蓝色预警")) {
                                    maxDrp = 100;
                                } else if (planName.equals("防汛防台黄色预警")) {
                                    maxDrp = 150;
                                } else if (planName.equals("防汛防台橙色预警")) {
                                    maxDrp = 200;
                                } else if (planName.equals("防汛防台红色预警")) {
                                    maxDrp = 1000;
                                }
                                ES_MODELFANGANPojo pojo = new ES_MODELFANGANPojo();
                                pojo.setID(name);
                                pojo.setFA_NAME(planName);
                                pojo.setNOTE(description);
                                pojo.setTYPE("防洪调度");
                                pojo.setNEWFA_NAME(newFa_name);
                                pojo.setTYPE(planIndex);
                                pojo.setYJZ(Double.valueOf(i));
                                if (maxDrp > 0) {
                                    pojo.setMAXDRP(maxDrp);
                                }
                                listFang.add(pojo);
                            }
                        }
                        System.out.println("区域名称: " + name + ", ID: " + regionID);
                    }
                }
            }
            // else {
            // break;
            // }
        }
        if (listZhan.size() > 0) {
            esModelfanganzhanData.deleteAll();
            rows = esModelfanganzhanData.insertALL(listZhan);

            // ****************************旧代码 */

            // List<ES_MODELFANGANPojo> listFang = new ArrayList<>();
            // // 假设 listZhan 已经被赋值
            // Map<String, List<ES_MODELFANGANZHANPojo>> mapByFaId = listZhan.stream()
            // .collect(Collectors.groupingBy(ES_MODELFANGANZHANPojo::getFA_ID));

            // for (Map.Entry<String, List<ES_MODELFANGANZHANPojo>> entry :
            // mapByFaId.entrySet()) {
            // String faId = entry.getKey(); // 获取 FA_ID
            // List<ES_MODELFANGANZHANPojo> pojos = entry.getValue(); // 获取该 FA_ID 下的所有对象

            // String zhanIdsStr = pojos.stream()
            // .map(ES_MODELFANGANZHANPojo::getZHANID)
            // .collect(Collectors.joining(","));

            // String slpStr = pojos.stream()
            // .map(ES_MODELFANGANZHANPojo::getSPECIAL)
            // .collect(Collectors.joining(","));

            // List<Map<String, Object>> filteredList = bjZhan.stream()
            // .filter(map -> {
            // // 1. 获取站名，防止 null
            // String name = (String) map.get("name");
            // // 2. 判断是否包含关键词
            // return name != null && slpStr.contains(name);
            // })
            // .collect(Collectors.toList());

            // // 假设 filteredList 是你已经筛选好的 List
            // String idStr = filteredList.stream()
            // .map(map -> map.get("id")) // 1. 提取 ID 字段
            // .filter(Objects::nonNull) // 2. 过滤掉 null 值（防止报错）
            // .map(Object::toString) // 3. 转为字符串
            // .collect(Collectors.joining(",")); // 4. 逗号拼接

            // double maxDrp = 0;
            // if (pojos.get(0).getNORMAL().equals("日常调度")) {
            // maxDrp = 50;
            // } else if (pojos.get(0).getNORMAL().equals("防汛防台蓝色预警")) {
            // maxDrp = 100;
            // } else if (pojos.get(0).getNORMAL().equals("防汛防台黄色预警")) {
            // maxDrp = 150;
            // } else if (pojos.get(0).getNORMAL().equals("防汛防台橙色预警")) {
            // maxDrp = 200;
            // } else if (pojos.get(0).getNORMAL().equals("防汛防台红色预警")) {
            // maxDrp = 1000;
            // }
            // String newFa_name = zhanIdsStr + "," + idStr;
            // if (newFa_name.contains("1795167015")) {// 特殊处理：苏州河河口闸，浏河闸
            // newFa_name += ",9999999999";// 归为其他工程
            // }
            // ES_MODELFANGANPojo pojo = new ES_MODELFANGANPojo();
            // pojo.setID(faId);
            // pojo.setFA_NAME(pojos.get(0).getNORMAL());
            // pojo.setNOTE(pojos.get(0).getZHANNAME());
            // pojo.setTYPE("防洪调度");
            // pojo.setNEWFA_NAME(newFa_name);
            // if (maxDrp > 0) {
            // pojo.setMAXDRP(maxDrp);
            // }
            // listFang.add(pojo);
            // }
            // ****************************旧代码 */
            if (listFang.size() > 0) {
                esModelfanganData.deleteAll();
                rows += esModelfanganData.insertALL(listFang);
            }
        }
        return rows;
    }

    public List<GetAreaXSLPojo> GetResultXSL(String stime, String etime, String idStr) {
        List<GetAreaXSLPojo> listGetAreaXSLPojo = new ArrayList<>();
        int hydroID = modeGetHydroSeriesList();
        GetSubjectListPojo subjectListPojo = modelGetSubjectList();
        List<ES_JISUANZHANPojo> listJisuan = esJisuanzhanData.selectList(null, null, null, null, null);
        List<ST_WATERSTORAGE_BPojo> listWaterstorageb = esWaterstoragebData.selectList(null, null);
        // 当前蓄量,当前水位,较昨日,距警戒水位还有多少
        String[] ids = idStr.split(",");

        // 警戒水位对应的可调蓄库容,保证水位对应的可调蓄库容
        List<ES_MODELGUANLIANPojo> listModel = esModelGuanlianData.selectListByID(Arrays.asList(ids), null, null, null);

        for (String id : ids) {
            List<ES_JISUANZHANPojo> listJisuanT = listJisuan.stream().filter(p -> p.getID().equals(id))
                    .collect(Collectors.toList());
            final String name = listJisuanT.size() > 0 ? listJisuanT.get(0).getNAME() : "";

            List<ES_MODELGUANLIANPojo> listModelT = listModel.stream().filter(p -> p.getMKEYID().equals(id))
                    .collect(Collectors.toList());

            HashMap<String, Object> header = new HashMap<>();
            header.put("Content-Type", "application/json;charset=UTF-8");
            String parmasMap = "{" +
                    "    \"sbjID\": " + subjectListPojo.getSbjID() + "," +
                    "    \"hydroID\": " + hydroID + "," +
                    "    \"version\": 1," +
                    "    \"api\": \"GetResultPresetByTimePeriod\"," +
                    "    \"id\": " + id + "," +
                    "    \"startTM\": \"" + stime + "\"," +
                    "    \"endTM\": \"" + etime + "\"" +
                    "}";
            String result = apihelper.apipost(HuishuiApi, parmasMap, header);
            ObjectMapper objectMapper = new ObjectMapper();
            List<Map<String, Object>> dataList = new ArrayList<>();
            try {
                // 解析JSON字符串为JsonNode
                JsonNode rootNode = objectMapper.readTree(result);
                JsonNode data = rootNode.path("data");
                String tm = "";
                if (data != null) {
                    if (data.get("times") != null) {
                        // 获取vals数组
                        JsonNode vals = data.path("vals");
                        // 获取times数组
                        JsonNode times = data.path("times");
                        // 定义时间格式
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        for (int _index = 0; _index < times.size(); _index++) {
                            double upz = 0.0, zuoxsl = 0.0, jxsl = 0.0, bxsl = 0.0, yl = 0.0;
                            double[] xsl = { 0.0 }; // 使用数组包装
                            String _tm = times.get(_index).asText();
                            // 将字符串解析为Instant对象
                            Instant instant = Instant.parse(_tm);
                            // 将Instant转换为ZonedDateTime（指定时区为UTC）
                            ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of("UTC"));
                            // 转换为北京时间（东八区，UTC+8）
                            ZonedDateTime beijingDateTime = zonedDateTime
                                    .withZoneSameInstant(ZoneId.of("Asia/Shanghai"));
                            // 将ZonedDateTime转换为LocalDateTime
                            LocalDateTime localDateTime = beijingDateTime.toLocalDateTime();
                            // 格式化为指定格式的字符串
                            String formattedDateTimeString = localDateTime.format(formatter);
                            tm = formattedDateTimeString;

                            xsl[0] = vals.get(_index).asDouble() / 1000000;
                            // zuoxsl=vals.get(0).asDouble();

                            // 蓄量和水位的对应关系
                            // name=name.replace("槽蓄容量", "");
                            // name=name.replace("槽蓄", "");

                            List<ST_WATERSTORAGE_BPojo> listWaterstoragebT = listWaterstorageb.stream()
                                    .filter(p -> p.getTYPE().equals(name.replace("槽蓄容量", "")) && p.getS() >= xsl[0])
                                    .collect(Collectors.toList());
                            upz = listWaterstoragebT.size() > 0 ? listWaterstoragebT.get(0).getUPZ() : 0;

                            BigDecimal dxsl = new BigDecimal(String.valueOf(xsl[0]));

                            double wrzXsl = 0.0, grzXsl = 0.0;
                            if (listModelT.size() > 0) {
                                List<ES_MODELGUANLIANPojo> listModelTWRZ = listModelT.stream()
                                        .filter(p -> p.getTYPE().equals("警戒水位对应的可调蓄库容")).collect(Collectors.toList());
                                if (listModelTWRZ.size() > 0) {
                                    wrzXsl = Double.parseDouble(listModelTWRZ.get(0).getFIELD());
                                    jxsl = wrzXsl - dxsl.doubleValue();
                                }

                                List<ES_MODELGUANLIANPojo> listModelTGRZ = listModelT.stream()
                                        .filter(p -> p.getTYPE().equals("保证水位对应的可调蓄库容")).collect(Collectors.toList());
                                if (listModelTGRZ.size() > 0) {
                                    grzXsl = Double.parseDouble(listModelTGRZ.get(0).getFIELD());
                                    bxsl = grzXsl - dxsl.doubleValue();
                                }
                            }

                            // 使用 BigDecimal 进行四舍五入，保留 2 位小数
                            BigDecimal bdWRZ = new BigDecimal(jxsl);
                            bdWRZ = bdWRZ.setScale(2, RoundingMode.HALF_UP); // HALF_UP 表示四舍五入

                            // 使用 BigDecimal 进行四舍五入，保留 2 位小数
                            BigDecimal bdGRZ = new BigDecimal(bxsl);
                            bdGRZ = bdGRZ.setScale(2, RoundingMode.HALF_UP); // HALF_UP 表示四舍五入

                            // 使用 BigDecimal 进行四舍五入，保留 2 位小数
                            dxsl = dxsl.setScale(2, RoundingMode.HALF_UP); // HALF_UP 表示四舍五入

                            GetAreaXSLPojo getAreaXSLPojo = new GetAreaXSLPojo();
                            getAreaXSLPojo.setId(id);
                            getAreaXSLPojo.setName(name);
                            getAreaXSLPojo.setUpz(upz);
                            getAreaXSLPojo.setXsl(dxsl);
                            getAreaXSLPojo.setTm(tm);
                            getAreaXSLPojo.setZuoxsl(BigDecimal.valueOf(zuoxsl));
                            getAreaXSLPojo.setJxsl(BigDecimal.valueOf(bdWRZ.doubleValue()));
                            getAreaXSLPojo.setBxsl(BigDecimal.valueOf(bdGRZ.doubleValue()));
                            getAreaXSLPojo.setYl(yl);
                            getAreaXSLPojo.setWrzxsl(BigDecimal.valueOf(wrzXsl));
                            getAreaXSLPojo.setGrzxsl(BigDecimal.valueOf(grzXsl));
                            listGetAreaXSLPojo.add(getAreaXSLPojo);
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("调用" + parmasMap + "接口报错,接口返回结果是：" + result);
            }
        }
        return listGetAreaXSLPojo;
    }

}
