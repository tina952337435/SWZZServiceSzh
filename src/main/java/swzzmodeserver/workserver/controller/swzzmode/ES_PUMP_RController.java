package swzzmodeserver.workserver.controller.swzzmode;

import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swzzmodeserver.tools.CommonUtills;
import swzzmodeserver.tools.FieldIsValid;
import swzzmodeserver.tools.ParamField;
import swzzmodeserver.tools.ResultUtils;
import swzzmodeserver.workserver.data.swzzmode.ES_PUMP_RData;
import swzzmodeserver.workserver.pojo.swzzmode.ES_PUMP_RPojo;
import swzzmodeserver.workserver.pojo.swzzmode.ES_PUMP_STAVPojo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/SWZZ_MODE_ES_PUMP_R")
public class ES_PUMP_RController {
    @Autowired
    private ES_PUMP_RData data;

    @RequestMapping("/findResult")
    public ResultUtils findResult(@RequestBody ParamField bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String ID = null;
        String key = "",pageindex = "",pagesize = "10";
        List<String> type = new ArrayList<>();
        String stime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date().getTime() - 24 * 60 * 60 * 1000),etime = "";
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo,ParamField.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if(null != bpPojo.getStcd()){
            ID = bpPojo.getStcd();
        }
        if(null != bpPojo.getKwtxt()){
            key = bpPojo.getKwtxt();
        }
        if(null != bpPojo.getStartdate()){
            stime = bpPojo.getStartdate();
        }
        if(null != bpPojo.getEnddate()){
            etime = bpPojo.getEnddate();
        }
        if(null != bpPojo.getPattem()){
            type = Arrays.asList(bpPojo.getPattem().split(","));
        }
        if(null != bpPojo.getPageindex()){
            pageindex = bpPojo.getPageindex();
        }
        if(null != bpPojo.getPagesize()){
            pagesize = bpPojo.getPagesize();
        }
        Integer startindex = null;
        if(!"".equals(pageindex) && !"".equals(pagesize)){
            startindex = (Integer.parseInt(pageindex) - 1) * Integer.parseInt(pagesize);
        }
        List<ES_PUMP_RPojo> fxList = data.selectList(ID,stime,etime,startindex, Integer.valueOf(pagesize));
        Integer integer = data.selectCount(ID);
        Integer count = 1;
        if(null != pagesize && !"".equals(pagesize)){
            count = integer / Integer.parseInt(pagesize);
            if(integer % Integer.parseInt(pagesize) != 0){
                count += 1;
            }
        }
        watch.stop();
        if(!"".equals(pagesize) && !"".equals(pageindex)){
            if(fxList.size() > 0){
                return new ResultUtils<>(fxList, "操作成功",true,Integer.parseInt(pagesize) ,Integer.parseInt(pageindex),count,integer,fxList.size(),watch.getTime());
            }else {
                return new ResultUtils<>(fxList, "操作成功",false,Integer.parseInt(pagesize) ,Integer.parseInt(pageindex),count,integer,fxList.size(),watch.getTime());
            }
        }else {
            if(fxList.size() > 0){
                return new ResultUtils<>(fxList, "操作成功",true ,fxList.size(),watch.getTime());
            }else {
                return new ResultUtils<>(fxList, "操作成功",false,fxList.size(),watch.getTime());
            }
        }
    }
    @RequestMapping("/add")
    public ResultUtils add(@RequestBody ES_PUMP_RPojo bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo,ES_PUMP_RPojo.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        Integer num = data.insertOne(bpPojo);
        watch.stop();
        if(num > 0){
            return new ResultUtils<>(num, "操作成功",true, num,watch.getTime());
        }else {
            return new ResultUtils<>(num, "操作成功",false, num,watch.getTime());
        }
    }
    @RequestMapping("/modify")
    public ResultUtils modify(@RequestBody ES_PUMP_RPojo bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo,ES_PUMP_RPojo.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        Integer num = data.updateOne(bpPojo);
        watch.stop();
        if(num > 0){
            return new ResultUtils<>(num, "操作成功",true, num,watch.getTime());
        }else {
            return new ResultUtils<>(num, "操作成功",false, num,watch.getTime());
        }
    }
    @RequestMapping("/remove")
    public ResultUtils remove(@RequestBody ParamField bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String ID = null;
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo,ParamField.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if(null != bpPojo.getStcd()){
            ID = bpPojo.getStcd();
        }
        Integer num = data.deleteOne(ID);
        watch.stop();
        if(num > 0){
            return new ResultUtils<>(num, "操作成功",true, num,watch.getTime());
        }else {
            return new ResultUtils<>(num, "操作成功",false, num,watch.getTime());
        }
    }

    /**
     * 按模型预报时间(RLSTM)联查ES_PUMP_B，求每个泵站的平均流量和最大流量
     * @param bpPojo startdate: 模型预报时间(RLSTM)
     * @return 站点编码、站名、经纬度、平均流量、最大流量
     */
    @RequestMapping("/findStAvByRlstm")
    public ResultUtils findStAvByRlstm(@RequestBody ParamField bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo,ParamField.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        String rlstm = "";
        if(null != bpPojo.getStartdate()){
            rlstm = bpPojo.getStartdate();
        }
        if("".equals(rlstm)){
            watch.stop();
            return new ResultUtils<>(null,"模型预报时间不能为空",false,0,watch.getTime());
        }
        List<ES_PUMP_STAVPojo> result = data.selectStAvByRlstm(rlstm);
        watch.stop();
        if(result != null && result.size() > 0){
            return new ResultUtils<>(result, "操作成功",true, result.size(), watch.getTime());
        }else {
            return new ResultUtils<>(result, "未查到数据",false, 0, watch.getTime());
        }
    }

    /**
     * 按模型计算时间(RLSTM)和站点(STCD)查询ES_PUMP_R数据
     * @param bpPojo startdate: 模型计算时间(RLSTM), stcd: 站点编码(可选)
     */
    @RequestMapping("/findByRlstmAndStcd")
    public ResultUtils findByRlstmAndStcd(@RequestBody ParamField bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo,ParamField.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        String rlstm = "";
        String stcd = "";
        if(null != bpPojo.getStartdate()){
            rlstm = bpPojo.getStartdate();
        }
        if(null != bpPojo.getStcd()){
            stcd = bpPojo.getStcd();
        }
        if("".equals(rlstm)){
            watch.stop();
            return new ResultUtils<>(null,"模型计算时间不能为空",false,0,watch.getTime());
        }
        List<ES_PUMP_RPojo> result = data.selectByRlstmAndStcd(rlstm, stcd);
        watch.stop();
        if(result != null && result.size() > 0){
            return new ResultUtils<>(result, "操作成功",true, result.size(), watch.getTime());
        }else {
            return new ResultUtils<>(result, "未查到数据",false, 0, watch.getTime());
        }
    }
}