package swzzmodeserver.workserver.controller.swzzrtsq;

import swzzmodeserver.tools.ResultUtils;
import swzzmodeserver.workserver.data.swzzrtsq.RTSQST_STBPRP_BData;
import swzzmodeserver.workserver.pojo.swzzrtsq.ST_PPTN_RPojo;
import swzzmodeserver.workserver.pojo.swzzrtsq.ST_STBPRP_BPojo;
import swzzmodeserver.tools.ColumnName;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// 【修改点】在 @RestController 中指定唯一名称
@RestController("RtsqST_STBPRP_BController")
@RequestMapping("/SWZZ_RTSQ_ST_STBPRP_B")
public class ST_STBPRP_BController {
    @Autowired
    private RTSQST_STBPRP_BData data;

    @RequestMapping("/findResult")
    public ResultUtils selectList(@RequestBody ST_STBPRP_BPojo param){
        StopWatch watch = new StopWatch();
        watch.start();
        String STNM = "";
        List<String> STCDList = new ArrayList<>();
        if(null != param.getSTCD()){
            STCDList = Arrays.asList(param.getSTCD().split(","));
        }
        if(null != param.getSTNM()){
            STNM = param.getSTNM();
        }
        List<ST_STBPRP_BPojo> treeList = data.selectList(STCDList,STNM);
        watch.stop();
        if(treeList.size() > 0){
            return new ResultUtils<>(treeList, "操作成功",true ,treeList.size(),watch.getTime());
        }else {
            return new ResultUtils<>(treeList, "操作成功",false,treeList.size(),watch.getTime());
        }
    }
    @RequestMapping("/add")
    public ResultUtils insertOne(@RequestBody ST_STBPRP_BPojo quPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        Integer integer = data.insertOne(quPojo);
        watch.stop();
        if(integer > 0){
            return new ResultUtils<>(integer, "操作成功",true, integer,watch.getTime());
        }else {
            return new ResultUtils<>(integer, "操作成功",false, integer,watch.getTime());
        }
    }
    @RequestMapping("/modify")
    public ResultUtils upDateOne(@RequestBody ST_STBPRP_BPojo quPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        Integer integer = data.upDateOne(quPojo);
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
    @RequestMapping("/selectStbprpBList")
    public ResultUtils selectStbprpBList(@RequestBody ColumnName param){
        StopWatch watch = new StopWatch();
        watch.start();
        String STNM = "",pathname="";
        List<String> STCDList = new ArrayList<>();
        if(null != param.getStcd()){
            STCDList = Arrays.asList(param.getStcd().split(","));
        }
        if(null != param.getKey()){
            STNM = param.getKey();
        }
        if(null != param.getPathname()){
            pathname = param.getPathname();
        }
        String mtype="";
        if(null != param.getDatasource()){
            mtype = param.getDatasource();
        }
        List<ST_STBPRP_BPojo> treeList = data.selectStbprpBList(STCDList,STNM,mtype);
        watch.stop();
        if(treeList.size() > 0){
            return new ResultUtils<>(treeList, "操作成功",true ,treeList.size(),watch.getTime());
        }else {
            return new ResultUtils<>(treeList, "操作成功",false,treeList.size(),watch.getTime());
        }
    }
}
