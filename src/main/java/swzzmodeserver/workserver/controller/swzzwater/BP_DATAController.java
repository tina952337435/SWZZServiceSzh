package swzzmodeserver.workserver.controller.swzzwater;

import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swzzmodeserver.tools.CommonUtills;
import swzzmodeserver.tools.FieldIsValid;
import swzzmodeserver.tools.ParamField;
import swzzmodeserver.tools.ResultUtils;
import swzzmodeserver.workserver.data.swzzwater.BP_DATAData;
import swzzmodeserver.workserver.pojo.swzzwater.BP_DATAPojo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/SWZZ_WATER_BP_DATA")
public class BP_DATAController {
    @Autowired
    private BP_DATAData data;

    @RequestMapping("/findResult")
    public ResultUtils findResult(@RequestBody ParamField bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        List<String> stcd = new ArrayList<>();
        String stime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date().getTime() - 24 * 60 * 60 * 1000),etime = "";
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo, ParamField.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if(null != bpPojo.getStartdate()){
            stime = bpPojo.getStartdate();
        }
        if(null != bpPojo.getEnddate()){
            etime = bpPojo.getEnddate();
        }
        if(null != bpPojo.getPattem()){
            stcd = Arrays.asList(bpPojo.getStcd().split(","));
        }
        List<BP_DATAPojo> fxList = data.selectList(stime,etime,stcd);
        watch.stop();
        if(fxList.size() > 0){
            return new ResultUtils<>(fxList, "操作成功",true ,fxList.size(),watch.getTime());
        }else {
            return new ResultUtils<>(fxList, "操作成功",false,fxList.size(),watch.getTime());
        }
    }
}
