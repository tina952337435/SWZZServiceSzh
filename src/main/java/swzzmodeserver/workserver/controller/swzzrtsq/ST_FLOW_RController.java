package swzzmodeserver.workserver.controller.swzzrtsq;

import org.apache.commons.lang3.time.StopWatch;
import swzzmodeserver.tools.ResultUtils;
import swzzmodeserver.workserver.data.swzzrtsq.*;
import swzzmodeserver.workserver.pojo.swzzrtsq.*;
import swzzmodeserver.tools.ColumnName;
import swzzmodeserver.tools.DateUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/SWZZ_RTSQ_ST_FLOW_R")
public class ST_FLOW_RController {
    @Autowired
    private ST_FLOW_RData data;
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
        List<ST_FLOW_RPojo> flowRList = data.selectNew(stcdList, stime, etime);
        List<ST_STBPRP_B_QUDto> quDtoList = new ArrayList<>();
        for (ST_STBPRP_B_QUPojo quPojo : quList) {
            ST_STBPRP_B_QUDto quDto = new ST_STBPRP_B_QUDto();
            BeanUtils.copyProperties(quPojo, quDto);
            List<ST_FLOW_RPojo> collect = flowRList.stream().filter(i -> i.getSTCD().equals(quDto.getSTCD())).collect(Collectors.toList());
            if (collect.size() > 0) {
                ST_FLOW_RPojo flowRPojo = collect.get(0);
                if (null != flowRPojo.getZ()) {
                    quDto.setZ(flowRPojo.getZ());
                }
                if (null != flowRPojo.getQ()) {
                    quDto.setQ(flowRPojo.getQ());
                }
                if (null != flowRPojo.getMSQMT()) {
                    quDto.setMSQMT(flowRPojo.getMSQMT());
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
        List<ST_FLOW_RPojo> quDtoList = data.selectHis(stcdList, stime, etime);
        watch.stop();
        if (quDtoList.size() > 0) {
            return new ResultUtils<>(quDtoList, "操作成功", true, quDtoList.size(), watch.getTime());
        } else {
            return new ResultUtils<>(quDtoList, "操作成功", false, quDtoList.size(), watch.getTime());
        }
    }

    @RequestMapping("/selectSlAndYsl")//进出水量
    public ResultUtils selectSlAndYsl(@RequestBody ColumnName param) {
        StopWatch watch = new StopWatch();
        watch.start();
        Date date = new Date(new Date().getTime() - 24 * 60 * 60 * 1000);
        String stime = DateUtil.dateFormat(date, "yyyy-MM-dd HH:mm:ss"), etime = "", PID = "";
        if (null != param.getStime()) {
            stime = param.getStime();
        }
        if (null != param.getEtime()) {
            etime = param.getEtime();
        }
        if (null != param.getPid()) {
            PID = param.getPid();
        }
        ST_STBPRP_B_TREEPojo treePojo = new ST_STBPRP_B_TREEPojo();
        treePojo.setPID(PID);
        List<ST_STBPRP_B_TREEPojo> treeList = treeData.selectList(treePojo);
        List<ST_STBPRP_B_QUPojo> quList = quData.selectList(null, null, null, null, null);
        List<ST_FLOW_RDto> flowList = new ArrayList<>();
        for (ST_STBPRP_B_TREEPojo tree : treeList) {
            List<ST_STBPRP_B_QUPojo> collect = quList.stream().filter(i -> {
                return i.getPID().equals(tree.getID());
            }).collect(Collectors.toList());
            List<String> stcdList = new ArrayList<>();
            for (ST_STBPRP_B_QUPojo quPojo : collect) {
                if (null != quPojo.getSTCD()) {
                    stcdList.add(quPojo.getSTCD());
                }
            }
            List<ST_FLOW_RPojo> flowRList = new ArrayList<>();
            if (stcdList.size() > 0) {
                flowRList = data.selectHis(stcdList, stime, etime);
            }
            Double SL = 0.00, YSL = 0.00;
            for (ST_FLOW_RPojo flow : flowRList) {
                if (null != flow.getQ()) {
                    if (flow.getQ() > 0) {
                        YSL += flow.getQ();
                    } else {
                        SL += flow.getQ();
                    }
                }
            }
            ST_FLOW_RDto flowRDto = new ST_FLOW_RDto();
            flowRDto.setSTNM(tree.getTITLE());
            flowRDto.setSL(Double.parseDouble(String.format("%.2f", (SL * 0.03))));
            flowRDto.setYSL(Double.parseDouble(String.format("%.2f", (YSL * 0.03))));
            flowList.add(flowRDto);
        }
        watch.stop();
        if (flowList.size() > 0) {
            return new ResultUtils<>(flowList, "操作成功", true, flowList.size(), watch.getTime());
        } else {
            return new ResultUtils<>(flowList, "操作成功", false, flowList.size(), watch.getTime());
        }
    }


    @RequestMapping("/queryByLLNew")
    public ResultUtils queryByLLNew(@RequestBody ColumnName param) {
        StopWatch watch = new StopWatch();
        watch.start();
        Date date = new Date(new Date().getTime() - 24 * 60 * 60 * 1000);
        String stime = DateUtil.dateFormat(date, "yyyy-MM-dd HH:mm:ss"), etime = "", PID = "";
        String dayhour = "Minute";
        String mtype = "";
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
        if (null != param.getPathname()) {
            dayhour = param.getPathname();
        }
        if (null != param.getDatasource()) {
            mtype = param.getDatasource();
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
        List<ST_STBPRP_BPojo> stStbprpBList = stbprpBData.selectStbprpBList(stcdList, null, null);
        List<GetWaterViewNewPojo> flowRList = getWaterViewNewData.queryByFlow(stcdList, stime, etime, dayhour, mtype);
        List<GetWaterViewNewPojo> dataList = new ArrayList<>();
        if (quList.size() > 0) {
            for (int num = 0; num < quList.size(); num++) {
                ST_STBPRP_B_QUPojo onestbprpBQuList = quList.get(num);
                String stcd = onestbprpBQuList.getSTCD().toString();
                List<GetWaterViewNewPojo> oneFlowList = flowRList.stream().filter(u -> u.getSTCD().equals(stcd) && onestbprpBQuList.getSTTP().equals(u.getMTYPE())).collect(Collectors.toList());
                List<ST_STBPRP_BPojo> onestStbprpB = stStbprpBList.stream().filter(u -> u.getSTCD().equals(stcd) && onestbprpBQuList.getSTTP().equals(u.getMTYPE())).collect(Collectors.toList());
                List<ST_STBPRP_B_QUPojo> tempquList = quList.stream().filter(u -> u.getSTCD().equals(stcd)).collect(Collectors.toList());
                if (oneFlowList.size() > 0) {
                    GetWaterViewNewPojo pojo = oneFlowList.get(0);
                    if(tempquList.size()>0){
                        pojo.setOrderbyid(tempquList.get(0).getORDERBYID());
                        pojo.setSTNM(tempquList.get(0).getSTNM());
                    }
                    if(onestStbprpB.size()>0){
                        pojo.setADMAUTH(onestStbprpB.get(0).getADMAUTH());
                        pojo.setDTPR(onestStbprpB.get(0).getDTPR());
                    }
                    List<ST_STBPRP_B_QUPojo> onequstcd=quList.stream().filter(o->o.getSTCD().equals(pojo.getSTCD())&&o.getSTTP().equals(pojo.getMTYPE())).collect(Collectors.toList());
                    if(onequstcd.size()>0){
                        pojo.setDIR(onequstcd.get(0).getDIR());
                        pojo.setMAPSIZE(String.valueOf(onequstcd.get(0).getMAPSIZE()));
                        pojo.setROTATE(onequstcd.get(0).getROTATE());
                    }
                    dataList.add(pojo);
                } else {
                    if (onestStbprpB.size() > 0) {
                        GetWaterViewNewPojo pojo = new GetWaterViewNewPojo();
                        pojo.setSTCD(onestStbprpB.get(0).getSTCD());
                        pojo.setSTNM(onestStbprpB.get(0).getSTNM());
                        pojo.setHNNM(onestStbprpB.get(0).getHNNM());
                        pojo.setRVNM(onestStbprpB.get(0).getRVNM());
                        pojo.setLGTD(String.valueOf(onestStbprpB.get(0).getLGTD()));
                        pojo.setLTTD(String.valueOf(onestStbprpB.get(0).getLTTD()));
                        pojo.setOrderbyid(quList.get(num).getORDERBYID());
                        pojo.setADMAUTH(onestStbprpB.get(0).getADMAUTH());
                        pojo.setMTYPE(onestStbprpB.get(0).getMTYPE());

                        if (onestStbprpB.get(0).getWRZ() != null) {
                            pojo.setWRZ(onestStbprpB.get(0).getWRZ().toString());
                        }
                        if (onestStbprpB.get(0).getGRZ() != null) {
                            pojo.setGRZ(onestStbprpB.get(0).getGRZ().toString());
                        }

                        List<ST_STBPRP_B_QUPojo> onequstcd = quList.stream().filter(o -> o.getSTCD().equals(pojo.getSTCD()) && o.getSTTP().equals(pojo.getMTYPE())).collect(Collectors.toList());
                        if (onequstcd.size() > 0) {
                            pojo.setDIR(onequstcd.get(0).getDIR());
                            pojo.setMAPSIZE(String.valueOf(onequstcd.get(0).getMAPSIZE()));
                            pojo.setROTATE(String.valueOf(onequstcd.get(0).getROTATE()));
                        }
                        if (tempquList.size() > 0) {
                            pojo.setOrderbyid(tempquList.get(0).getORDERBYID());
                            pojo.setSTNM(tempquList.get(0).getSTNM());
                        }
                        dataList.add(pojo);
                    }
                }
            }
        }
        watch.stop();
        if (dataList.size() > 0) {
            return new ResultUtils<>(dataList, "操作成功", true, dataList.size(), watch.getTime());
        } else {
            return new ResultUtils<>(dataList, "操作成功", false, dataList.size(), watch.getTime());
        }
    }
}