package swzzmodeserver.workserver.controller.swzzrtsq;

import org.apache.commons.lang3.time.StopWatch;
import swzzmodeserver.tools.ResultUtils;
import swzzmodeserver.workserver.data.swzzrtsq.ST_RNFL_FData;
import swzzmodeserver.workserver.pojo.swzzrtsq.ST_RNFL_FPojo;
import swzzmodeserver.tools.ColumnName;
import swzzmodeserver.tools.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/SWZZ_RTSQ_ST_RNFL_F")
public class ST_RNFL_FController {
    @Autowired
    private ST_RNFL_FData data;

    @RequestMapping("/findResult")
    public ResultUtils selectList(@RequestBody ColumnName param){
        StopWatch watch = new StopWatch();
        watch.start();
        Date date = new Date(new Date().getTime() - 24 * 60 * 60 * 1000);
        String stime= DateUtil.dateFormat(date,"yyyy-MM-dd HH:mm:ss"),etime="",
                type="昆山",intv="",path="";
        List<String> stcdList = new ArrayList<>();
        if(null != param.getStcd()){
            stcdList = Arrays.asList(param.getStcd().split(","));
        }
        if(null != param.getStime()){
            stime = param.getStime();
        }
        if(null != param.getEtime()){
            etime = param.getEtime();
        }
        if(null != param.getDatasource()){
            path = param.getDatasource();
        }
        if(null != param.getPathname()){
            type = param.getPathname();
        }
        if(null != param.getId()){
            intv = param.getId();
        }
        List<ST_RNFL_FPojo> stRnflFList = new ArrayList<>();
        if("TmAsc".equals(path)){
            stRnflFList = data.selectListByTmAsc(stcdList,stime,etime,intv,type);
        }else if("YbtmDesc".equals(path)){
            stRnflFList = data.selectListByYbtmDesc(stcdList,stime,etime,intv,type);
        }
        watch.stop();

        if(stRnflFList.size() > 0){
            return new ResultUtils<>(stRnflFList, "操作成功",true ,stRnflFList.size(),watch.getTime());
        }else {
            return new ResultUtils<>(stRnflFList, "操作成功",false,stRnflFList.size(),watch.getTime());
        }
    }

    @RequestMapping("/batchResultI")
    public ResultUtils insertAll(@RequestBody List<ST_RNFL_FPojo> rnflFList){
        StopWatch watch = new StopWatch();
        watch.start();
        int num= data.insertAll(rnflFList);
        if(num > 0){
            return new ResultUtils<>(num, "操作成功",true, num,watch.getTime());
        }else {
            return new ResultUtils<>(num, "操作成功",false, num,watch.getTime());
        }
    }

}
