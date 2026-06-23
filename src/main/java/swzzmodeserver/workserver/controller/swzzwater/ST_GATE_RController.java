package swzzmodeserver.workserver.controller.swzzwater;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swzzmodeserver.tools.CommonUtills;
import swzzmodeserver.tools.FieldIsValid;
import swzzmodeserver.tools.ParamField;
import swzzmodeserver.tools.ResultUtils;
import swzzmodeserver.workserver.pojo.swzzwater.ST_GATE_RPojo;
import swzzmodeserver.workserver.service.swzzflood.RTSQService;
import swzzmodeserver.workserver.service.swzzwater.ST_GATE_RService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/SWZZ_WATER_ST_GATE_R")
public class ST_GATE_RController {
    @Autowired
    private ST_GATE_RService service;

    @RequestMapping("/WATER_ST_GATE_RNEWLIST")
    public ResultUtils WATER_ST_GATE_RNEWLIST(@RequestBody ParamField bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String starttime = "",endtime = "";
        List<String> stcd = new ArrayList<>();
        List<String> treeID = new ArrayList<>();
        ParamField params = FieldIsValid.getColumnName(bpPojo,ParamField.class);
        if(CommonUtills.isEmpty(params)){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if(null != bpPojo.getStartdate()){
            starttime = bpPojo.getStartdate();
        }
        if(null != bpPojo.getEnddate()){
            endtime = bpPojo.getEnddate();
        }
        if (null != bpPojo.getStcd()){
            stcd = Arrays.asList(bpPojo.getStcd().split(","));
        }
        if (null != bpPojo.getTreeID()){
            treeID = Arrays.asList(bpPojo.getTreeID().split(","));
        }
        List<Map<String,Object>> pojoList = service.WATER_ST_GATE_RNEWLIST(treeID,starttime, endtime,stcd);
        watch.stop();
        if(pojoList.size() > 0){
            return new ResultUtils<>(pojoList, "操作成功",true ,pojoList.size(),watch.getTime());
        }else {
            return new ResultUtils<>(pojoList, "操作成功",false,pojoList.size(),watch.getTime());
        }
    }

    @RequestMapping("/findResult")
    public ResultUtils findResult(@RequestBody ParamField bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String starttime = "",endtime = "",EXKEY = null,EQPTP = null;
        List<String> stcd = new ArrayList<>();
        List<String> treeID = new ArrayList<>();
        ParamField params = FieldIsValid.getColumnName(bpPojo,ParamField.class);
        if(CommonUtills.isEmpty(params)){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if(null != bpPojo.getStartdate()){
            starttime = bpPojo.getStartdate();
        }
        if(null != bpPojo.getEnddate()){
            endtime = bpPojo.getEnddate();
        }
        if (null != bpPojo.getStcd()){
            stcd = Arrays.asList(bpPojo.getStcd().split(","));
        }
        if (null != bpPojo.getEXKEY()){
            EXKEY = bpPojo.getEXKEY();
        }
        if (null != bpPojo.getEQPTP()){
            EQPTP = bpPojo.getEQPTP();
        }
        if (null != bpPojo.getPageindex() && null != bpPojo.getPagesize()){
            Map<String, Object> map = service.selectList(starttime, endtime, EXKEY, EQPTP, stcd, Integer.parseInt(bpPojo.getPageindex()), Integer.parseInt(bpPojo.getPagesize()));
            List<ST_GATE_RPojo> list = (List<ST_GATE_RPojo>)map.get("list");
            PageInfo<ST_GATE_RPojo> pageInfo = (PageInfo<ST_GATE_RPojo>)map.get("pageInfo");
            watch.stop();
            if (list.size() > 0){
                return new ResultUtils<>(list, "操作成功",true ,pageInfo.getSize(),pageInfo.getPageNum(),pageInfo.getPages(),pageInfo.getTotal(),list.size(),watch.getTime());
            }else {
                return new ResultUtils<>(list, "操作成功",false ,pageInfo.getSize(),pageInfo.getPageNum(),pageInfo.getPages(),pageInfo.getTotal(),list.size(),watch.getTime());
            }
        }
        Map<String, Object> map = service.selectList(starttime, endtime, EXKEY, EQPTP, stcd, null, null);
        List<ST_GATE_RPojo> pojoList = (List<ST_GATE_RPojo>)map.get("list");
        watch.stop();
        if(pojoList.size() > 0){
            return new ResultUtils<>(pojoList, "操作成功",true ,pojoList.size(),watch.getTime());
        }else {
            return new ResultUtils<>(pojoList, "操作成功",false,pojoList.size(),watch.getTime());
        }
    }
}
