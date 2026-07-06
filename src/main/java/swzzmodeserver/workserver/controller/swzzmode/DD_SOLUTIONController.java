package swzzmodeserver.workserver.controller.swzzmode;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swzzmodeserver.tools.*;
import swzzmodeserver.workserver.data.swzzmode.*;
import swzzmodeserver.workserver.pojo.Huishui.GetAreaXSLPojo;
import swzzmodeserver.workserver.pojo.Huishui.GetPlansRiverHPJPojo;
import swzzmodeserver.workserver.pojo.swzzdata.EmployeeGetTokenPojo;
import swzzmodeserver.workserver.pojo.swzzmode.*;
import swzzmodeserver.workserver.pojo.swzzrtsq.ST_STBPRP_B_QUPojo;
import swzzmodeserver.workserver.service.swzzmode.HuishuiApiService;

import swzzmodeserver.workserver.data.swzzrtsq.RTSQST_STBPRP_BData;
import swzzmodeserver.workserver.data.swzzrtsq.ST_STBPRP_B_QUData;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/SWZZ_MODE_DD_SOLUTION")
public class DD_SOLUTIONController {
    @Autowired
    private DD_SOLUTIONData data;

    @Autowired
    private BDMS_PREDICTData bdmsPredictData;
    @Autowired
    private ST_STBPRP_BData stbprpBData;
    @Autowired
    private DD_SOLUTIONData ddSolutionData;

    @Autowired
    private ES_ZHANDIANData es_zhandianData;

    @Autowired
    private ES_ZHANDIANDATAData es_zhandiandataData;

    @Autowired
    private HuishuiApiService huishuiApiService;

    @Autowired
    private RTSQST_STBPRP_BData rStbprp_BData;

    @Autowired
    private ES_MODELGUANLIANData esModelGuanlianData;

    @Autowired
    private ST_STBPRP_B_QUData stbprpB_QUData;

    @Autowired
    private ES_JISUANZHANData esJisuanzhanData;

    @Value("${file.path.templatefilepath}")
    private String filePathName;

    @RequestMapping("/findResult")
    public ResultUtils findResult(@RequestBody ParamField bpPojo) {
        StopWatch watch = new StopWatch();
        watch.start();
        String ID = "", key = "", pageindex = "", pagesize = "10";
        List<String> type = new ArrayList<>();
        String stime = ""// new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date().getTime() - 24
                         // * 60 * 60 * 1000)
                , etime = "";
        if (CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo, ParamField.class))) {
            watch.stop();
            return new ResultUtils<>(null, "存在非法字符", false, -1, watch.getTime());
        }
        if (null != bpPojo.getStcd()) {
            ID = bpPojo.getStcd();
        }
        if (null != bpPojo.getKwtxt()) {
            key = bpPojo.getKwtxt();
        }
        if (null != bpPojo.getStartdate()) {
            stime = bpPojo.getStartdate();
        }
        if (null != bpPojo.getEnddate()) {
            etime = bpPojo.getEnddate();
        }
        if (null != bpPojo.getPattem()) {
            type = Arrays.asList(bpPojo.getPattem().split(","));
        }
        if (null != bpPojo.getPageindex()) {
            pageindex = bpPojo.getPageindex();
        }
        if (null != bpPojo.getPagesize()) {
            pagesize = bpPojo.getPagesize();
        }
        Integer startindex = null;
        if (!"".equals(pageindex) && !"".equals(pagesize)) {
            startindex = (Integer.parseInt(pageindex) - 1) * Integer.parseInt(pagesize);
        }
        List<DD_SOLUTIONPojo> fxList = data.selectList(ID, key, type, null, stime, etime, startindex,
                Integer.valueOf(pagesize));
        Integer integer = data.selectCount(ID, key, type, stime, etime);
        Integer count = 1;
        if (!"".equals(pagesize)) {
            count = integer / Integer.parseInt(pagesize);
            if (integer % Integer.parseInt(pagesize) != 0) {
                count += 1;
            }
        }
        watch.stop();
        if (!"".equals(pagesize) && !"".equals(pageindex)) {
            if (fxList.size() > 0) {
                return new ResultUtils<>(fxList, "操作成功", true, Integer.parseInt(pagesize), Integer.parseInt(pageindex),
                        count, integer, fxList.size(), watch.getTime());
            } else {
                return new ResultUtils<>(fxList, "操作成功", false, Integer.parseInt(pagesize), Integer.parseInt(pageindex),
                        count, integer, fxList.size(), watch.getTime());
            }
        } else {
            if (fxList.size() > 0) {
                return new ResultUtils<>(fxList, "操作成功", true, fxList.size(), watch.getTime());
            } else {
                return new ResultUtils<>(fxList, "操作成功", false, fxList.size(), watch.getTime());
            }
        }
    }

    @RequestMapping("/add")
    public ResultUtils add(@RequestBody DD_SOLUTIONPojo dsPojo) {
        StopWatch watch = new StopWatch();
        watch.start();
        if (CommonUtills.isEmpty(FieldIsValid.getColumnName(dsPojo, DD_SOLUTIONPojo.class))) {
            watch.stop();
            return new ResultUtils<>(null, "存在非法字符", false, -1, watch.getTime());
        }
        Integer num = data.insertOne(dsPojo);
        watch.stop();
        if (num > 0) {
            return new ResultUtils<>(num, "操作成功", true, num, watch.getTime());
        } else {
            return new ResultUtils<>(num, "操作成功", false, num, watch.getTime());
        }
    }

    @RequestMapping("/modify")
    public ResultUtils modify(@RequestBody DD_SOLUTIONPojo dsPojo) {
        StopWatch watch = new StopWatch();
        watch.start();
        if (CommonUtills.isEmpty(FieldIsValid.getColumnName(dsPojo, DD_SOLUTIONPojo.class))) {
            watch.stop();
            return new ResultUtils<>(null, "存在非法字符", false, -1, watch.getTime());
        }
        Integer num = data.updateOne(dsPojo);
        watch.stop();
        if (num > 0) {
            return new ResultUtils<>(num, "操作成功", true, num, watch.getTime());
        } else {
            return new ResultUtils<>(num, "操作成功", false, num, watch.getTime());
        }
    }

    @RequestMapping("/remove")
    public ResultUtils remove(@RequestBody ParamField dsPojo) {
        StopWatch watch = new StopWatch();
        watch.start();
        String ID = "";
        if (CommonUtills.isEmpty(FieldIsValid.getColumnName(dsPojo, ParamField.class))) {
            watch.stop();
            return new ResultUtils<>(null, "存在非法字符", false, -1, watch.getTime());
        }
        if (null != dsPojo.getStcd()) {
            ID = dsPojo.getStcd();
        }
        Integer num = data.deleteOne(ID);
        watch.stop();
        if (num > 0) {
            return new ResultUtils<>(num, "操作成功", true, num, watch.getTime());
        } else {
            return new ResultUtils<>(num, "操作成功", false, num, watch.getTime());
        }
    }

    // @RequestMapping("/insertALL")
    // public ResultUtils insertALL(@RequestBody List<DD_SOLUTIONPojo> dsPojo){
    // StopWatch watch = new StopWatch();
    // watch.start();
    // Integer num = data.insertALL(dsPojo);
    // watch.stop();
    // if(num > 0){
    // return new ResultUtils<>(num, "操作成功",true, num,watch.getTime());
    // }else {
    // return new ResultUtils<>(num, "操作成功",false, num,watch.getTime());
    // }
    // }
    @RequestMapping("/findByDD_IDandDD_status")
    public ResultUtils findByDD_IDandDD_status(@RequestBody DD_SOLUTIONPojo dsPojo) {
        StopWatch watch = new StopWatch();
        watch.start();
        String dd_id = "", dd_status = "";
        if (CommonUtills.isEmpty(FieldIsValid.getColumnName(dsPojo, DD_SOLUTIONPojo.class))) {
            watch.stop();
            return new ResultUtils<>(null, "存在非法字符", false, -1, watch.getTime());
        }
        if (null != dsPojo.getDD_ID()) {
            dd_id = dsPojo.getDD_ID();
        }
        if (null != dsPojo.getDD_STATUS()) {
            dd_status = dsPojo.getDD_STATUS();
        }
        List<DD_SOLUTIONPojo> ddStatus = data.selectListByDD_IDandDD_status(dd_id, dd_status, null, null);
        watch.stop();
        if (ddStatus.size() > 0) {
            return new ResultUtils<>(ddStatus, "操作成功", true, ddStatus.size(), watch.getTime());
        } else {
            return new ResultUtils<>(ddStatus, "操作成功", false, ddStatus.size(), watch.getTime());
        }
    }

    @RequestMapping("/GetListPlans")
    public ResultUtils GetListPlans(@RequestBody ParamField bpPojo) {
        StopWatch watch = new StopWatch();
        watch.start();
        String stime = "", etime = "";

        LocalDateTime sTime = LocalDateTime.now().minusDays(3);
        LocalDateTime eTime = LocalDateTime.now();
        // 定义日期时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // 转换为String
        stime = sTime.format(formatter);
        etime = eTime.format(formatter);
        String dd_dis = "15";// 后面需要导入接口的DD_DISTRIBY参数
        List<DD_SOLUTIONPojo> fxList = data.selectList(null, "自动预报员", null, dd_dis, null, null, null, null);// 15"
        List<DD_SOLUTIONDtoPojo> fxListNew = new ArrayList<>();
        fxList.forEach(item -> {
            DD_SOLUTIONDtoPojo pojo = new DD_SOLUTIONDtoPojo();
            pojo.setID(item.getID());
            pojo.setDD_ID(item.getDD_ID());
            pojo.setDD_NAME(item.getDD_NAME());
            pojo.setDD_TM(item.getDD_TM());
            pojo.setDD_FOR(item.getDD_FOR());
            pojo.setDD_BY(item.getDD_BY());
            pojo.setDD_STANA(item.getDD_STANA());
            pojo.setDD_CHECKBY(item.getDD_CHECKBY());
            pojo.setDD_DISTRIBY(item.getDD_DISTRIBY());
            pojo.setDD_NOTE(item.getDD_NOTE());
            pojo.setDD_MIND(item.getDD_MIND());
            pojo.setDD_CARRYTM(item.getDD_CARRYTM());
            pojo.setDD_EVALUE(item.getDD_EVALUE());
            pojo.setDD_CARRYBY(item.getDD_CARRYBY());
            pojo.setDD_STATUS(item.getDD_STATUS());
            fxListNew.add(pojo);
        });
        watch.stop();
        if (fxList.size() > 0) {
            return new ResultUtils<>(fxListNew, "操作成功", true, fxList.size(), watch.getTime());
        } else {
            return new ResultUtils<>(fxListNew, "操作成功", false, fxList.size(), watch.getTime());
        }
    }

    @RequestMapping("/SWZZ_MODELSINGRESULT")
    public ResultUtils SWZZ_MODELSINGRESULT(@RequestBody EmployeeGetTokenPojo bpPojo) {
        StopWatch watch = new StopWatch();
        watch.start();
        String message = "";
        List<SWZZ_MODELSINGRESULTParam> list = new ArrayList<>();
        if (bpPojo.getSOLUTIONID() != null && bpPojo.getDATA_TYPE() != null) {
            List<String> fxftStcd = Arrays.asList(
                    "1850281,1170181,2060881,1770181,1460281,1970381,1360481,1330183,1230483,1831983,1530383,2060181"
                            .split(","));
            List<ST_STBPRP_BDto> ListBasic = stbprpBData.selectListBandStcd("").stream()
                    .filter(m -> "1".equals(m.getTYPE().trim())).collect(Collectors.toList());
            List<BDMS_PREDICTPojo> ListBDMS = bdmsPredictData.selectList("", "", "",
                    Collections.singletonList(bpPojo.getSOLUTIONID()), null, bpPojo.getDATA_TYPE(), null, null, "",
                    fxftStcd);

            ListBDMS = ListBDMS.stream().filter(u -> fxftStcd.contains(u.getSTCD())).collect(Collectors.toList());
            ListBasic = ListBasic.stream().filter(u -> fxftStcd.contains(u.getZSTCD())).collect(Collectors.toList());
            List<DD_SOLUTIONPojo> ListDD = ddSolutionData.selectList(null, "", null, null, null, null, null, null);

            List<BDMS_PREDICTPojo> finalListBDMS = ListBDMS;
            ListBasic.forEach(n -> {
                SWZZ_MODELSINGRESULTParam dto = new SWZZ_MODELSINGRESULTParam();
                if (ListDD.size() > 0) {
                    dto.setSOLUTIONID(ListDD.get(0).getDD_ID());
                    dto.setDD_NAME(ListDD.get(0).getDD_NAME());
                }
                dto.setSTCD(n.getZSTCD());
                dto.setSTNM(n.getSTNM());
                dto.setLGTD(n.getLGTD());
                dto.setLTTD(n.getLTTD());
                List<BDMS_PREDICTPojo> ListBDMSTemp = finalListBDMS.stream()
                        .filter(s -> s.getSTCD().trim().equals(n.getZSTCD().trim())).collect(Collectors.toList());
                if (ListBDMSTemp.size() > 0) {
                    float max = ListBDMSTemp.stream().map(BDMS_PREDICTPojo::getDATA).max(Float::compareTo).get();
                    dto.setMAXZ(String.valueOf(max));
                    String maxtm = ListBDMSTemp.stream().filter(s -> max == s.getDATA()).collect(Collectors.toList())
                            .get(0).getYMDHM();
                    dto.setMAXTM(maxtm);
                    float min = ListBDMSTemp.stream().map(BDMS_PREDICTPojo::getDATA).min(Float::compareTo).get();
                    dto.setMINZ(String.valueOf(min));
                    String mintm = ListBDMSTemp.stream().filter(s -> min == s.getDATA()).collect(Collectors.toList())
                            .get(0).getYMDHM();
                    dto.setMINTM(mintm);
                }
                list.add(dto);
            });
            message = "操作成功";
        } else {
            message = "参数不能为空";
        }

        watch.stop();
        if (list.size() > 0) {
            return new ResultUtils<>(list, message, true, list.size(), watch.getTime());
        } else {
            return new ResultUtils<>(list, message, false, list.size(), watch.getTime());
        }
    }

    @RequestMapping("/SWZZ_MODELSINGRESULTSZH")
    public ResultUtils SWZZ_MODELSINGRESULTSZH(@RequestBody EmployeeGetTokenPojo bpPojo) {
        StopWatch watch = new StopWatch();
        watch.start();
        String message = "";
        List<SWZZ_MODELSINGRESULTParam> list = new ArrayList<>();
        if (bpPojo.getSOLUTIONID() != null && bpPojo.getDATA_TYPE() != null) {
            List<String> fxftStcd = Arrays.asList(
                    "63405150,63404710,63405250,63405000,63404000,63401500,63405100,63405480,63404700".split(","));
            String pid = "2026060312362939391";
            if (bpPojo.getPID() != null) {
                pid = bpPojo.getPID();
                List<ES_JISUANZHANPojo> fxList = esJisuanzhanData.selectList(null, null, null, null, null);
                List<ST_STBPRP_B_QUPojo> listQU = stbprpB_QUData.selectList(null, null, null, pid, null);
                List<String> listID = listQU.stream().map(item -> item.getSTCD()).collect(Collectors.toList());
                fxList = fxList.stream().filter(item -> listID.contains(item.getID())).collect(Collectors.toList());
                fxftStcd = fxList.stream().map(item -> item.getSTCD()).collect(Collectors.toList());
            }

            List<BDMS_PREDICTPojo> ListBDMS = bdmsPredictData.selectList("", "", "",
                    Collections.singletonList(bpPojo.getSOLUTIONID()), null, bpPojo.getDATA_TYPE(), null, null, "",
                    fxftStcd);

            // ListBDMS = ListBDMS.stream().filter(u ->
            // fxftStcd.contains(u.getSTCD())).collect(Collectors.toList());
            // ListBasic=ListBasic.stream().filter(u->fxftStcd.contains(
            // u.getSTCD())).collect(Collectors.toList());
            List<DD_SOLUTIONPojo> ListDD = ddSolutionData.selectList(bpPojo.getSOLUTIONID(), "", null, null, null, null,
                    null, null);

            List<swzzmodeserver.workserver.pojo.swzzrtsq.ST_STBPRP_BPojo> ListBasic = rStbprp_BData.selectList(fxftStcd,
                    null);
            List<BDMS_PREDICTPojo> finalListBDMS = ListBDMS;
            ListBasic.forEach(n -> {
                SWZZ_MODELSINGRESULTParam dto = new SWZZ_MODELSINGRESULTParam();
                if (ListDD.size() > 0) {
                    dto.setSOLUTIONID(ListDD.get(0).getDD_ID());
                    dto.setDD_NAME(ListDD.get(0).getDD_NAME());
                }
                dto.setSTNM(n.getSTNM());
                dto.setLGTD(n.getLGTD());
                dto.setLTTD(n.getLTTD());
                List<BDMS_PREDICTPojo> ListBDMSTemp = finalListBDMS.stream()
                        .filter(s -> s.getSTCD().trim().equals(n.getSTCD().trim())).collect(Collectors.toList());
                if (ListBDMSTemp.size() > 0) {
                    dto.setSTCD(ListBDMSTemp.get(0).getSTCD());
                    float max = ListBDMSTemp.stream().map(BDMS_PREDICTPojo::getDATA).max(Float::compareTo).get();
                    dto.setMAXZ(String.valueOf(max));
                    String maxtm = ListBDMSTemp.stream().filter(s -> max == s.getDATA()).collect(Collectors.toList())
                            .get(0).getYMDHM();
                    dto.setMAXTM(maxtm);
                    float min = ListBDMSTemp.stream().map(BDMS_PREDICTPojo::getDATA).min(Float::compareTo).get();
                    dto.setMINZ(String.valueOf(min));
                    String mintm = ListBDMSTemp.stream().filter(s -> min == s.getDATA()).collect(Collectors.toList())
                            .get(0).getYMDHM();
                    dto.setMINTM(mintm);
                }
                list.add(dto);
            });
            message = "操作成功";
        } else {
            message = "参数不能为空";
        }

        watch.stop();
        if (list.size() > 0) {
            return new ResultUtils<>(list, message, true, list.size(), watch.getTime());
        } else {
            return new ResultUtils<>(list, message, false, list.size(), watch.getTime());
        }
    }

    @RequestMapping("/MODE_BDMS_PREDICTSel")
    public ResultUtils MODE_BDMS_PREDICTSel(@RequestBody EmployeeGetTokenPojo bpPojo) {
        StopWatch watch = new StopWatch();
        watch.start();
        String message = "";
        List<BDMS_PREDICTDtoPojo> list = new ArrayList<>();
        if (bpPojo.getSOLUTIONID() != null && bpPojo.getSTCD() != null) {
            List<String> fxftStcd = Arrays.asList(bpPojo.getSTCD().split(","));
            List<BDMS_PREDICTPojo> ListBDMS = bdmsPredictData.selectList("", "", "",
                    Collections.singletonList(bpPojo.getSOLUTIONID()), null, bpPojo.getDATA_TYPE(), null, null, "",
                    fxftStcd);
            ListBDMS.forEach(n -> {
                BDMS_PREDICTDtoPojo dto = new BDMS_PREDICTDtoPojo();
                dto.setID(n.getID());
                dto.setUSERID(n.getUSERID());
                dto.setSTCD(n.getSTCD());
                dto.setYMDHM(n.getYMDHM());
                dto.setPLAN_N(n.getPLAN_N());
                dto.setDATA_TYPE(n.getDATA_TYPE());
                dto.setDATA(n.getDATA());
                list.add(dto);
            });
            message = "操作成功";
        } else {
            message = "参数不能为空";
        }

        watch.stop();
        if (list.size() > 0) {
            return new ResultUtils<>(list, message, true, list.size(), watch.getTime());
        } else {
            return new ResultUtils<>(list, message, false, list.size(), watch.getTime());
        }
    }

    /**
     * 处理获取河流计划数据的请求接口
     * 
     * @param bpPojo 包含SOLUTIONID和TM参数的请求体对象
     * @return 返回ResultUtils对象，包含处理结果、消息、状态、数据量和处理时间
     */
    @RequestMapping("/GetPlansRiverHPJ")
    public ResultUtils GetPlansRiverHPJ(@RequestBody EmployeeGetTokenPojo bpPojo) {
        // 创建并启动计时器，用于测量方法执行时间
        StopWatch watch = new StopWatch();
        watch.start();
        // 刔回消息变量
        String message = "";
        // 初始化结果列表
        List<GetPlansRiverHPJPojo> list = new ArrayList<>();
        // 检查请求参数是否为空
        if (bpPojo.getSOLUTIONID() != null && bpPojo.getTM() != null) {
            // 根据SOLUTIONID查询解决方案数据
            List<DD_SOLUTIONPojo> ListDD = ddSolutionData.selectList(bpPojo.getSOLUTIONID(), "", null, null, null, null,
                    null, null);
            // 如果查询结果不为空
            if (ListDD.size() > 0) {
                // 获取第一个解决方案的任务ID
                String taskID = ListDD.get(0).getDD_FOR();
                // 调用回水API获取指定时间点的所有模型结果
                Map<String, Object> result = huishuiApiService.modeGetResultAllModelByTime(taskID, bpPojo.getTM());
                try {
                    // 从结果中获取特定ID的数据对象
                    Map<String, Object> obj = (Map<String, Object>) result.get("100667523");
                    // 获取 data 部分的值
                    List<Map<String, Object>> dataList = (List<Map<String, Object>>) obj.get("sections");
                    ;
                    // 遍历 data 列表
                    for (Map<String, Object> dataItem : dataList) {
                        GetPlansRiverHPJPojo pojo = new GetPlansRiverHPJPojo();
                        // 检查 bCur 字段是否等于 true
                        int ID = 100667523;
                        int DMNUM = (int) dataItem.get("index");
                        double UPZ = (double) dataItem.get("Z");
                        double V = (double) dataItem.get("V");
                        pojo.setID(ID);
                        pojo.setDMNUM(DMNUM);
                        pojo.setUPZ(UPZ);
                        pojo.setSPEED(V);
                        list.add(pojo);
                    }
                } catch (Exception e) {
                    System.out.println("调用接口报错,接口返回结果是：" + result);
                }
            }
            message = "操作成功";
        } else {
            message = "参数不能为空";
        }

        watch.stop();
        if (list.size() > 0) {
            return new ResultUtils<>(list, message, true, list.size(), watch.getTime());
        } else {
            return new ResultUtils<>(list, message, false, list.size(), watch.getTime());
        }
    }

    @RequestMapping("/GetPlansRiverMain")
    public ResultUtils GetPlansRiver33(@RequestBody EmployeeGetTokenPojo bpPojo) {
        StopWatch watch = new StopWatch();
        watch.start();
        String message = "";
        List<GetPlansRiverHPJPojo> list = new ArrayList<>();
        if (bpPojo.getSOLUTIONID() != null && bpPojo.getTM() != null) {
            List<DD_SOLUTIONPojo> ListDD = ddSolutionData.selectList(bpPojo.getSOLUTIONID(), "", null, null, null, null,
                    null, null);
            if (ListDD.size() > 0) {
                String taskID = ListDD.get(0).getDD_FOR();
                final Map<String, Object> result = "2025072800001".equals(bpPojo.getSOLUTIONID())
                        ? readResultFromLocalFile(bpPojo.getSOLUTIONID(), bpPojo.getTM())
                        : huishuiApiService.modeGetResultAllModelByTime(taskID, bpPojo.getTM());
                try {
                    List<ES_MODELGUANLIANPojo> listRiver = esModelGuanlianData.selectList(null, "A级河道", null, null);
                    listRiver.forEach(u -> {
                        String riverID = u.getMKEYID();
                        try {
                            Map<String, Object> obj = (Map<String, Object>) result.get(riverID);
                            // 获取 data 部分的值
                            List<Map<String, Object>> dataList = (List<Map<String, Object>>) obj.get("sections");
                            ;
                            // 遍历 data 列表
                            for (Map<String, Object> dataItem : dataList) {
                                GetPlansRiverHPJPojo pojo = new GetPlansRiverHPJPojo();
                                // 检查 bCur 字段是否等于 true
                                int ID = Integer.valueOf(riverID);
                                int DMNUM = (int) dataItem.get("index");
                                double UPZ = (double) dataItem.get("Z");
                                double V = (double) dataItem.get("V");
                                pojo.setID(ID);
                                pojo.setDMNUM(DMNUM);
                                pojo.setUPZ(UPZ);
                                pojo.setSPEED(V);
                                pojo.setRVNM(u.getSTCD());
                                list.add(pojo);
                            }
                        } catch (Exception e) {
                            // System.out.println("调用接口报错,接口返回结果是：" + result);
                        }
                    });

                } catch (Exception e) {
                    // System.out.println("调用接口报错,接口返回结果是：" + result);
                }
            }
            message = "操作成功";
        } else {
            message = "参数不能为空";
        }

        watch.stop();
        if (list.size() > 0) {
            return new ResultUtils<>(list, message, true, list.size(), watch.getTime());
        } else {
            return new ResultUtils<>(list, message, false, list.size(), watch.getTime());
        }
    }

    // 定期删除自动计算的方案：7天以前的
    @RequestMapping("/removeAutoModeFang")
    public ResultUtils removeAutoModeFang(@RequestBody ParamField bpPojo) {
        StopWatch watch = new StopWatch();
        watch.start();
        int day = 15;
        if (CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo, ParamField.class))) {
            watch.stop();
            return new ResultUtils<>(null, "存在非法字符", false, -1, watch.getTime());
        }
        if (null != bpPojo.getPid()) {
            day = Integer.parseInt(bpPojo.getPid());
        }
        String stime = "", etime = "";
        // 格式化输出
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        // 计算n天前时间
        stime = now.minusDays(day).format(formatter);
        etime = now.format(formatter);
        List<DD_SOLUTIONPojo> fxList = data.selectList(null, null, null, null, null, stime, null,
                Integer.valueOf("10"));
        fxList = fxList.stream().filter(u -> u.getDD_CARRYBY() == null).collect(Collectors.toList());// 删除n天前不收藏的方案
        int num = fxList.size();
        if (fxList.size() > 0) {
            List<ParamField> list = new ArrayList<>();
            fxList.forEach(u -> {
                String ID = u.getDD_ID();
                data.deleteOne(ID);// 删除方案表

                ParamField pojo = new ParamField();
                pojo.setStcd(ID);
                list.add(pojo);
            });
            // 删除方案边界条件
            es_zhandianData.deleteFangAn(list);
            // 删除方案预报成果
            bdmsPredictData.deleteFangAn(list);
        }
        watch.stop();
        if (num > 0) {
            return new ResultUtils<>(num, "操作成功", true, num, watch.getTime());
        } else {
            return new ResultUtils<>(num, "操作成功", false, num, watch.getTime());
        }
    }

    // 最新方案的特征信息
    @RequestMapping("/findResultTZ")
    public ResultUtils findResultTZ(@RequestBody ParamField bpPojo) {
        StopWatch watch = new StopWatch();
        watch.start();
        List<String> type = new ArrayList<>();
        String stime = ""// new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date().getTime() - 24
                         // * 60 * 60 * 1000)
                , etime = "";
        if (CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo, ParamField.class))) {
            watch.stop();
            return new ResultUtils<>(null, "存在非法字符", false, -1, watch.getTime());
        }
        if (null != bpPojo.getStartdate()) {
            stime = bpPojo.getStartdate();
        }
        if (null != bpPojo.getEnddate()) {
            etime = bpPojo.getEnddate();
        }
        if (null != bpPojo.getPattem()) {
            type = Arrays.asList(bpPojo.getPattem().split(","));
        }

        List<DD_SOLUTIONTZParam> fxList = new ArrayList<>();
        List<DD_SOLUTIONPojo> ListDDTop = ddSolutionData.selectListNew(type, stime, etime);
        if (ListDDTop.size() > 0) {
            String tm = ListDDTop.get(0).getDD_CARRYTM();
            String stm = DateUtil.pastTM(tm, "yyyy-MM-dd") + " 00:00:00";
            String etm = DateUtil.pastTM(tm, "yyyy-MM-dd") + " 23:59:59";
            List<DD_SOLUTIONPojo> ListDD = ddSolutionData.selectList(null, null, null, bpPojo.getPattem(), stm, etm,
                    null, null);
            List<String> dd_ids = ListDD.stream()
                    .map(p -> p.getDD_ID())
                    .collect(Collectors.toList());

            // 边界条件------------------------------------------雨量
            String zhanType = "0";
            List<String> zhanTypeList = Arrays.asList(zhanType.split(","));
            List<ES_ZHANDIANPojo> listZhan = es_zhandianData.selectList(null, null, null, zhanTypeList, null);
            List<String> zhanids = listZhan.stream()
                    .map(p -> p.getZHANID())
                    .collect(Collectors.toList());
            
            List<ES_ZHANDIANDATAPojo> allZhanData = es_zhandiandataData.selectListBySolutionIds(dd_ids,zhanids);
            // 按 SOLUTIONID + ZHANID 建立二级索引 Map
            Map<String, Map<String, List<ES_ZHANDIANDATAPojo>>> dataIndex = 
            allZhanData.stream()
                .collect(Collectors.groupingBy(
                    ES_ZHANDIANDATAPojo::getSOLUTIONID,
                    Collectors.groupingBy(ES_ZHANDIANDATAPojo::getZHANID)
                ));
            ListDD.forEach(u -> {               
                DD_SOLUTIONTZParam dto = new DD_SOLUTIONTZParam();
                dto.setID(u.getID());
                dto.setDD_ID(u.getDD_ID());
                dto.setDD_NAME(u.getDD_NAME());
                dto.setDD_TM(u.getDD_TM());
                dto.setDD_FOR(u.getDD_FOR());
                dto.setDD_BY(u.getDD_BY());
                dto.setDD_STANA(u.getDD_STANA());
                dto.setDD_CHECKBY(u.getDD_CHECKBY());
                dto.setDD_DISTRIBY(u.getDD_DISTRIBY());
                dto.setDD_NOTE(u.getDD_NOTE());
                dto.setDD_MIND(u.getDD_MIND());
                dto.setDD_CARRYTM(u.getDD_CARRYTM());
                dto.setDD_EVALUE(u.getDD_EVALUE());
                dto.setDD_CARRYBY(u.getDD_CARRYBY());
                dto.setDD_STATUS(u.getDD_STATUS());
                dto.setCJCOUNT(0);

                Map<String, List<ES_ZHANDIANDATAPojo>> solutionData = 
                dataIndex.getOrDefault(u.getDD_ID(), Collections.emptyMap());
                List<DD_SOLUTIONTZParamChi> listTZ = new ArrayList<>();
                listZhan.forEach(z -> {
                   List<ES_ZHANDIANDATAPojo> zhanDataList =solutionData.get(z.getZHANID());
                   if (zhanDataList != null && !zhanDataList.isEmpty()) {
                        String DATA = "";
                        if (Integer.parseInt(z.getPTYPE()) == 0) {                            
                            BigDecimal sum = zhanDataList.stream()
                                    .map(p -> p.getZHANDATA() != null ? new BigDecimal(p.getZHANDATA())
                                            : BigDecimal.ZERO)
                                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                            DATA = sum.toString();
                        } else if (Integer.parseInt(z.getPTYPE()) >= 3) {
                            DATA = zhanDataList.get(0).getZHANDATA();
                        }
                        DD_SOLUTIONTZParamChi dtoChi = new DD_SOLUTIONTZParamChi();
                        dtoChi.setZHANID(z.getZHANID());
                        dtoChi.setZHANNAME(z.getZHANNAME());
                        dtoChi.setDATA(DATA);
                        dtoChi.setPTYPE(z.getPTYPE());
                        listTZ.add(dtoChi);
                    }
                });
                dto.setListTZ(listTZ);
                fxList.add(dto);
            });

            // 边界条件------------------------------------------雨量

        }
        watch.stop();
        if (fxList.size() > 0) {
            return new ResultUtils<>(fxList, "操作成功", true, fxList.size(), watch.getTime());
        } else {
            return new ResultUtils<>(fxList, "操作成功", false, fxList.size(), watch.getTime());
        }
    }

    @RequestMapping("/saveResultAllModelByTimeRange")
    public ResultUtils saveResultAllModelByTimeRange(@RequestBody ParamField bpPojo) {
        StopWatch watch = new StopWatch();
        watch.start();
        String message = "";
        int successCount = 0;
        int failCount = 0;
        if (bpPojo.getStartdate() != null && bpPojo.getEnddate() != null) {
            String stime = bpPojo.getStartdate();
            String etime = bpPojo.getEnddate();
            // objID和hydroID默认值，可通过pid和kID1覆盖
            int objID = (bpPojo.getPid() != null && !bpPojo.getPid().equals(""))
                    ? Integer.parseInt(bpPojo.getPid())
                    : 608667460;
            int hydroID = (bpPojo.getKID1() != null && !bpPojo.getKID1().equals(""))
                    ? Integer.parseInt(bpPojo.getKID1())
                    : 1865991977;
            String taskID = (bpPojo.getKID2() != null) ? bpPojo.getKID2() : "";

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            try {
                LocalDateTime startTime = LocalDateTime.parse(stime, formatter);
                LocalDateTime endTime = LocalDateTime.parse(etime, formatter);
                long hours = java.time.Duration.between(startTime, endTime).toHours();
                if (hours < 0) {
                    watch.stop();
                    return new ResultUtils<>(null, "开始时间不能大于结束时间", false, -1, watch.getTime());
                }
                for (int i = 0; i <= hours; i++) {
                    LocalDateTime currentTime = startTime.plusHours(i);
                    String timeStr = currentTime.format(formatter);
                    boolean ok = huishuiApiService.saveResultAllModelByTime(timeStr, objID, hydroID, taskID);
                    if (ok) {
                        successCount++;
                    } else {
                        failCount++;
                    }
                }
                message = "执行完成，成功：" + successCount + "，失败：" + failCount;
            } catch (Exception e) {
                message = "时间解析异常：" + e.getMessage();
                watch.stop();
                return new ResultUtils<>(null, message, false, -1, watch.getTime());
            }
        } else {
            message = "开始时间和结束时间不能为空";
            watch.stop();
            return new ResultUtils<>(null, message, false, -1, watch.getTime());
        }
        watch.stop();
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("successCount", successCount);
        resultMap.put("failCount", failCount);
        return new ResultUtils<>(resultMap, message, successCount > 0, successCount + failCount, watch.getTime());
    }

    @RequestMapping("/getModelResultMoni")
    public ResultUtils getModelResultMoni(@RequestBody ParamField bpPojo) {
        StopWatch watch = new StopWatch();
        watch.start();
        String message = "";
        int successCount = 0;
        int failCount = 0;
        if (bpPojo.getStartdate() != null && bpPojo.getEnddate() != null) {
            String stime = bpPojo.getStartdate();
            String etime = bpPojo.getEnddate();
            // objID和hydroID默认值，可通过pid和kID1覆盖
            int objID = (bpPojo.getPid() != null && !bpPojo.getPid().equals(""))
                    ? Integer.parseInt(bpPojo.getPid())
                    : 608667460;
            int hydroID = (bpPojo.getKID1() != null && !bpPojo.getKID1().equals(""))
                    ? Integer.parseInt(bpPojo.getKID1())
                    : 1865991977;
            String taskID = (bpPojo.getKID2() != null) ? bpPojo.getKID2() : "";

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            try {
                List<BDMS_PREDICTPojo> listAll = huishuiApiService.getModelResultMoni(bpPojo.getStcd(), stime, etime,
                        taskID, objID, hydroID);
                if (listAll.size() > 0) {
                    Integer num = 0;
                    int count = 500;
                    int number = listAll.size() / count;
                    if (listAll.size() % count != 0) {
                        number += 1;
                    }
                    List<BDMS_PREDICTPojo> list = new ArrayList<>();
                    for (int i = 0; i < number; i++) {
                        if (i == number - 1) {
                            list = listAll.subList(count * i, listAll.size());
                        } else {
                            list = listAll.subList(count * i, count * (i + 1));
                        }
                        successCount += bdmsPredictData.insertALL(list);
                    }
                }
                message = "执行完成，成功：" + successCount + "，失败：" + failCount;
            } catch (Exception e) {
                message = "时间解析异常：" + e.getMessage();
                watch.stop();
                return new ResultUtils<>(null, message, false, -1, watch.getTime());
            }
        } else {
            message = "开始时间和结束时间不能为空";
            watch.stop();
            return new ResultUtils<>(null, message, false, -1, watch.getTime());
        }
        watch.stop();
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("successCount", successCount);
        resultMap.put("failCount", failCount);
        return new ResultUtils<>(resultMap, message, successCount > 0, successCount + failCount, watch.getTime());
    }

    @RequestMapping("/findResultModeXSL")
    public ResultUtils findResultModeXSL(@RequestBody ParamField bpPojo) {
        StopWatch watch = new StopWatch();
        watch.start();
        String stime = "", etime = "";
        if (bpPojo.getStartdate() != null) {
            stime = bpPojo.getStartdate();
        }
        if (bpPojo.getEnddate() != null) {
            etime = bpPojo.getEnddate();
        }
        List<GetAreaXSLPojo> fxList = huishuiApiService.GetResultXSL(stime, etime, bpPojo.getPid());
        watch.stop();
        if (fxList.size() > 0) {
            return new ResultUtils<>(fxList, "操作成功", true, fxList.size(), watch.getTime());
        } else {
            return new ResultUtils<>(fxList, "操作成功", false, fxList.size(), watch.getTime());
        }
    }

    @RequestMapping("/MODE_BDMS_PREDICTSelKTXL")
    public ResultUtils MODE_BDMS_PREDICTSelKTXL(@RequestBody EmployeeGetTokenPojo bpPojo) {
        StopWatch watch = new StopWatch();
        watch.start();
        String message = "";
        List<BDMS_PREDICTDtoPojo> list = new ArrayList<>();
        String dataType = bpPojo.getDATA_TYPE() != null ? bpPojo.getDATA_TYPE() : "15";
        String xltype = bpPojo.getXLTYPE() != null ? bpPojo.getXLTYPE() : "保证水位对应的可调蓄库容";

        if (bpPojo.getSOLUTIONID() != null && bpPojo.getSTCD() != null) {
            List<String> fxftStcd = Arrays.asList(bpPojo.getSTCD().split(","));
            List<BDMS_PREDICTPojo> ListBDMS = bdmsPredictData.selectList("", "", "",
                    Collections.singletonList(bpPojo.getSOLUTIONID()), null, dataType, null, null, "", fxftStcd);
            List<ES_MODELGUANLIANPojo> listModel = esModelGuanlianData.selectList("", xltype, null, null);
            ListBDMS.forEach(n -> {
                List<ES_MODELGUANLIANPojo> listModelT = listModel.stream()
                        .filter(p -> p.getMKEYID().equals(n.getSTCD())).collect(Collectors.toList());
                double grzXsl = 0, data = 0;
                if (listModelT.size() > 0) {
                    grzXsl = Double.parseDouble(listModelT.get(0).getFIELD());
                    data = grzXsl - Double.parseDouble(n.getDATA().toString());
                }
                // 使用 BigDecimal 进行四舍五入，保留 2 位小数
                BigDecimal bd = new BigDecimal(data);
                bd = bd.setScale(2, RoundingMode.HALF_UP); // HALF_UP 表示四舍五入

                BDMS_PREDICTDtoPojo dto = new BDMS_PREDICTDtoPojo();
                dto.setID(n.getID());
                dto.setUSERID(n.getUSERID());
                dto.setSTCD(n.getSTCD());
                dto.setYMDHM(n.getYMDHM());
                dto.setPLAN_N(n.getPLAN_N());
                dto.setDATA_TYPE(n.getDATA_TYPE());
                dto.setDATA(bd.floatValue());
                list.add(dto);
            });
            message = "操作成功";
        } else {
            message = "参数不能为空";
        }

        watch.stop();
        if (list.size() > 0) {
            return new ResultUtils<>(list, message, true, list.size(), watch.getTime());
        } else {
            return new ResultUtils<>(list, message, false, list.size(), watch.getTime());
        }
    }

    // 从本地文件读取GetResultAllModelByTime的results数据
    private Map<String, Object> readResultFromLocalFile(String solutionID, String tm) {
        String timeFileName = tm.replaceAll("[-: ]", "");
        String filePath = filePathName + "/Result/" + solutionID + "/" + timeFileName + ".json";
        try {
            String fileContent = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(filePath)));
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> fullResp = mapper.readValue(fileContent,
                    new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {
                    });
            return (Map<String, Object>) fullResp.get("results");
        } catch (IOException e) {
            System.out.println("读取本地文件失败：" + filePath + "，错误：" + e.getMessage());
            return new HashMap<>();
        }
    }

}
