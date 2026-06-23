package swzzmodeserver.workserver.controller.swzzrtsq;

import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swzzmodeserver.tools.ColumnName;
import swzzmodeserver.tools.DateUtil;
import swzzmodeserver.tools.ResultUtils;
import swzzmodeserver.workserver.data.swzzrtsq.*;
import swzzmodeserver.workserver.pojo.swzzrtsq.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/SWZZ_RTSQ_ST_WDWV_R")
public class ST_WDWV_RController {
    @Autowired
    private ST_WDWV_RData data;
    @Autowired
    private ST_STBPRP_B_QUData quData;
    @Autowired
    private ST_STBPRP_B_TREEData treeData;

    @Autowired
    private GetWaterViewNewData getWaterViewNewData;

    @Autowired
    private RTSQST_STBPRP_BData stbprpBData;

    @RequestMapping("/selectNew")
    public ResultUtils selectNew(@RequestBody ColumnName param) {
        StopWatch watch = new StopWatch();
        watch.start();
        Date date = new Date(new Date().getTime() - 24 * 60 * 60 * 1000);
        String stime = DateUtil.dateFormat(date, "yyyy-MM-dd HH:mm:ss"), etime = "", PID = "";
        List<String> stcdList = new ArrayList<>();
        if (null != param.getPid()) {
            PID = param.getPid();
        }
        if (null != param.getStime()) {
            stime = param.getStime();
        }
        if (null != param.getEtime()) {
            etime = param.getEtime();
        }
        List<ST_STBPRP_B_QUPojo> quList = quData.selectList("", "", null, PID, null);
        if (null != quList && quList.size() > 0) {
            for (ST_STBPRP_B_QUPojo quPojo : quList) {
                if (null != quPojo.getSTCD()) {
                    stcdList.add(quPojo.getSTCD());
                }
            }
        }
        if (!(stcdList.size() > 0)) {
            return new ResultUtils<>(null, "操作成功", false, 0, watch.getTime());
        }
        List<ST_WDWV_RPojo> flowRList = data.selectNew(stcdList, stime, etime);
        List<ST_STBPRP_B_QUDto> quDtoList = new ArrayList<>();
        for (ST_STBPRP_B_QUPojo quPojo : quList) {
            ST_STBPRP_B_QUDto quDto = new ST_STBPRP_B_QUDto();
            BeanUtils.copyProperties(quPojo, quDto);
            List<ST_WDWV_RPojo> collect = flowRList.stream().filter(i -> i.getSTCD().equals(quDto.getSTCD())).collect(Collectors.toList());
            if (collect.size() > 0) {
                ST_WDWV_RPojo flowRPojo = collect.get(0);
                if (null != flowRPojo.getWNDV()) {
                    quDto.setWNDV(flowRPojo.getWNDV());
                }
                if (null != flowRPojo.getWNDPWR()) {
                    quDto.setWNDPWR(flowRPojo.getWNDPWR());
                }
                if (null != flowRPojo.getWNDDIR()) {
                    quDto.setWNDDIR(flowRPojo.getWNDDIR());
                }
                if (null != flowRPojo.getWVHGT()) {
                    quDto.setWVHGT(flowRPojo.getWVHGT());
                }
                if (null != flowRPojo.getWNANGLE()) {
                    quDto.setWNANGLE(flowRPojo.getWNANGLE());
                }
                if (null != flowRPojo.getTM()) {
                    quDto.setTM(flowRPojo.getTM());
                }
            }
            quDtoList.add(quDto);
        }
        watch.stop();
        if (quDtoList.size() > 0) {
            return new ResultUtils<>(quDtoList, "操作成功", true, quDtoList.size(), watch.getTime());
        } else {
            return new ResultUtils<>(quDtoList, "操作成功", false, quDtoList.size(), watch.getTime());
        }
    }

    @RequestMapping("/selectHis")
    public ResultUtils selectHis(@RequestBody ColumnName param) {
        StopWatch watch = new StopWatch();
        watch.start();
        Date date = new Date(new Date().getTime() - 24 * 60 * 60 * 1000);
        String stime = DateUtil.dateFormat(date, "yyyy-MM-dd HH:mm:ss"), etime = "";
        List<String> stcdList = new ArrayList<>();
        if (null != param.getStcd()) {
            stcdList = Arrays.asList(param.getStcd().split(","));
        }
        if (null != param.getStime()) {
            stime = param.getStime();
        }
        if (null != param.getEtime()) {
            etime = param.getEtime();
        }
        List<ST_WDWV_RPojo> quDtoList = data.selectHis(stcdList, stime, etime);
        watch.stop();
        if (quDtoList.size() > 0) {
            return new ResultUtils<>(quDtoList, "操作成功", true, quDtoList.size(), watch.getTime());
        } else {
            return new ResultUtils<>(quDtoList, "操作成功", false, quDtoList.size(), watch.getTime());
        }
    }
}