package swzzmodeserver.workserver.controller.swzzrtsq;

import swzzmodeserver.tools.ResultUtils;
import swzzmodeserver.workserver.data.swzzrtsq.RTSQST_RVFCCH_BData;
import swzzmodeserver.workserver.pojo.swzzrtsq.ST_RVFCCH_BPojo;
import swzzmodeserver.tools.ColumnName;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// 【修改点】在 @RestController 中指定唯一名称
@RestController("RtsqST_RVFCCH_BController")
@RequestMapping("/SWZZ_RTSQ_ST_RVFCCH_B")
public class ST_RVFCCH_BController {

    @Autowired
    private RTSQST_RVFCCH_BData data;

    @RequestMapping("/findResult")
    public ResultUtils selectList(@RequestBody ST_RVFCCH_BPojo param){
        StopWatch watch = new StopWatch();
        watch.start();
        String STCD = "";
        if(null != param.getSTCD()){
            STCD = param.getSTCD();
        }
        List<ST_RVFCCH_BPojo> treeList = data.selectList(STCD);
        watch.stop();
        if(treeList.size() > 0){
            return new ResultUtils<>(treeList, "操作成功",true ,treeList.size(),watch.getTime());
        }else {
            return new ResultUtils<>(treeList, "操作成功",false,treeList.size(),watch.getTime());
        }
    }
    @RequestMapping("/add")
    public Integer insertOne(@RequestBody ST_RVFCCH_BPojo quPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        Integer integer = data.insertOne(quPojo);
        watch.stop();
        return integer;
    }
    @RequestMapping("/modify")
    public ResultUtils upDateOne(@RequestBody ST_RVFCCH_BPojo quPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        Integer num = data.upDateOne(quPojo);
        watch.stop();
        if(num > 0){
            return new ResultUtils<>(num, "操作成功",true, num,watch.getTime());
        }else {
            return new ResultUtils<>(num, "操作成功",false, num,watch.getTime());
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
        Integer num = data.deleteOne(id);
        watch.stop();
        if(num > 0){
            return new ResultUtils<>(num, "操作成功",true, num,watch.getTime());
        }else {
            return new ResultUtils<>(num, "操作成功",false, num,watch.getTime());
        }
    }
}
