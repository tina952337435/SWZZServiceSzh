package swzzmodeserver.workserver.controller.swzzflood;


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
import swzzmodeserver.workserver.data.swzzflood.ZhuantiData;
import swzzmodeserver.workserver.pojo.swzzflood.ZhuantiPojo;
import swzzmodeserver.workserver.service.swzzwater.ST_PPTN_RService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/SWZZ_FLOOD_ST_PPTN_R")
public class ST_PPTN_RController {
    @Autowired
    private ST_PPTN_RService service;

    @RequestMapping("/WATER_ST_PPTN_RCUSTOMLIST")
    public ResultUtils WATER_ST_PPTN_RCUSTOMLIST(@RequestBody ParamField paramField){
        StopWatch watch = new StopWatch();
        watch.start();
        String stime = "",etime = "",dayHour = "";
        List<String> idList = new ArrayList<>();
        ParamField params = FieldIsValid.getColumnName(paramField,ParamField.class);
        if(CommonUtills.isEmpty(params)){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if (null != paramField.getStcd()){
            idList = Arrays.asList(paramField.getStcd().split(","));
        }
        if (null != paramField.getStartdate()){
            stime = paramField.getStartdate();
        }
        if (null != paramField.getEnddate()){
            etime = paramField.getEnddate();
        }
        if (null != paramField.getDayHour()){
            dayHour = paramField.getDayHour();
        }
        List<Map<String,Object>> zhuantiList = service.WATER_ST_PPTN_RCUSTOMLIST(idList,stime,etime,dayHour);
        watch.stop();
        if (zhuantiList.size() > 0){
            return new ResultUtils<List>(zhuantiList,"操作成功",true,zhuantiList.size(),watch.getTime());
        }else {
            return new ResultUtils<List>(null,"操作成功",false,zhuantiList.size(),watch.getTime());
        }
    }
}
