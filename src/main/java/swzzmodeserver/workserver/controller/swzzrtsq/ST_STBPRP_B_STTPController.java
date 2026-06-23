package swzzmodeserver.workserver.controller.swzzrtsq;

import swzzmodeserver.tools.ResultUtils;
import swzzmodeserver.workserver.data.swzzrtsq.ST_STBPRP_B_STTPData;
import swzzmodeserver.workserver.pojo.swzzrtsq.ST_STBPRP_B_STTPPojo;
import swzzmodeserver.tools.ColumnName;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/SWZZ_RTSQ_ST_STBPRP_B_STTP")
public class ST_STBPRP_B_STTPController {
    @Autowired
    private ST_STBPRP_B_STTPData data;

    @RequestMapping("/findResult")
    public ResultUtils selectList(@RequestBody ST_STBPRP_B_STTPPojo param){
        StopWatch watch = new StopWatch();
        watch.start();
        String STZZ = "",STPP = "",STDP = "",STDD = "",STMM = "";
        if(null != param.getSTZZ()){
            STZZ = param.getSTZZ();
        }
        if(null != param.getSTPP()){
            STPP = param.getSTPP();
        }
        if(null != param.getSTDP()){
            STDP = param.getSTDP();
        }
        if(null != param.getSTDD()){
            STDD = param.getSTDD();
        }
        if(null != param.getSTMM()){
            STMM = param.getSTMM();
        }
        List<ST_STBPRP_B_STTPPojo> treeList = data.selectList(STZZ,STPP,STDP,STDD,STMM);
        watch.stop();
        if(treeList.size() > 0){
            return new ResultUtils<>(treeList, "操作成功",true ,treeList.size(),watch.getTime());
        }else {
            return new ResultUtils<>(treeList, "操作成功",false,treeList.size(),watch.getTime());
        }
    }
    @RequestMapping("/add")
    public ResultUtils insertOne(@RequestBody ST_STBPRP_B_STTPPojo sttpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        Integer integer = data.insertOne(sttpPojo);
        watch.stop();
        if(integer > 0){
            return new ResultUtils<>(integer, "操作成功",true, integer,watch.getTime());
        }else {
            return new ResultUtils<>(integer, "操作成功",false, integer,watch.getTime());
        }
    }
    @RequestMapping("/modify")
    public ResultUtils upDateOne(@RequestBody ST_STBPRP_B_STTPPojo sttpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        Integer integer = data.upDateOne(sttpPojo);
        watch.stop();
        if(integer > 0){
            return new ResultUtils<>(integer, "操作成功",true, integer,watch.getTime());
        }else {
            return new ResultUtils<>(integer, "操作成功",false, integer,watch.getTime());
        }
    }
    @RequestMapping("/remove")
    public ResultUtils deleteOne(@RequestBody ColumnName param){
        StopWatch watch = new StopWatch();
        watch.start();
        String id = "";
        if(null != param.getStcd()){
            id = param.getStcd();
        }
        Integer integer = data.deleteOne(id);
        watch.stop();
        if(integer > 0){
            return new ResultUtils<>(integer, "操作成功",true, integer,watch.getTime());
        }else {
            return new ResultUtils<>(integer, "操作成功",false, integer,watch.getTime());
        }
    }
}
