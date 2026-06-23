package swzzmodeserver.workserver.controller.swzzrtsq;

import swzzmodeserver.tools.ResultUtils;
import swzzmodeserver.workserver.data.swzzrtsq.ST_STBPRP_B_QUData;
import swzzmodeserver.workserver.pojo.swzzrtsq.ST_STBPRP_B_QUPojo;
import swzzmodeserver.tools.ColumnName;
import swzzmodeserver.tools.CommonUtills;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/SWZZ_RTSQ_ST_STBPRP_B_QU")
public class ST_STBPRP_B_QUController {
    @Autowired
    private ST_STBPRP_B_QUData data;

    @RequestMapping("/findResult")
    public ResultUtils selectList(@RequestBody ST_STBPRP_B_QUPojo param){
        StopWatch watch = new StopWatch();
        watch.start();
        String STCD = "",STNM = "",PID = "";
        List<String> STTPList = new ArrayList<>();
        if(null != param.getSTCD()){
            STCD = param.getSTCD();
        }
        if(null != param.getSTNM()){
            STNM = param.getSTNM();
        }
        if(null != param.getPID()){
            PID = param.getPID();
        }
        if(null != param.getSTTP()){
            STTPList = Arrays.asList(param.getSTTP().split(","));
        }
        List<ST_STBPRP_B_QUPojo> treeList = data.selectList(STCD,STNM,STTPList,PID,null);
        watch.stop();
        if(treeList.size() > 0){
            return new ResultUtils<>(treeList, "操作成功",true ,treeList.size(),watch.getTime());
        }else {
            return new ResultUtils<>(treeList, "操作成功",false,treeList.size(),watch.getTime());
        }
    }
    @RequestMapping("/findResultdDuo")
    public ResultUtils findResultdDuo(@RequestBody ST_STBPRP_B_QUPojo param){
        StopWatch watch = new StopWatch();
        watch.start();
        String STCD = "",STNM = "",PID = "";
        List<String> STTPList = new ArrayList<>();
        List<String> pidList = new ArrayList<>();
        if(null != param.getSTCD()){
            STCD = param.getSTCD();
        }
        if(null != param.getSTNM()){
            STNM = param.getSTNM();
        }
        if(null != param.getPID()){
            PID = param.getPID();
            pidList=Arrays.asList(param.getPID().split(","));
        }
        if(null != param.getSTTP()){
            STTPList = Arrays.asList(param.getSTTP().split(","));
        }
        List<ST_STBPRP_B_QUPojo> treeList = data.queryList(STCD,STNM,STTPList,pidList);
        watch.stop();
        if(treeList.size() > 0){
            return new ResultUtils<>(treeList, "操作成功",true ,treeList.size(),watch.getTime());
        }else {
            return new ResultUtils<>(treeList, "操作成功",false,treeList.size(),watch.getTime());
        }
    }
    @RequestMapping("/add")
    public Integer insertOne(@RequestBody ST_STBPRP_B_QUPojo quPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        Integer integer = data.insertOne(quPojo);
        watch.stop();
        return integer;
    }
    @RequestMapping("/batchResultI")
    public Integer insertALL(@RequestBody ColumnName param) throws IOException {
        StopWatch watch = new StopWatch();
        watch.start();
        List<ST_STBPRP_B_QUPojo> quPojos=new ArrayList<>();
        if(!CommonUtills.isEmpty(param.getDatasource())){
            String jsonStr=param.getDatasource();
            ObjectMapper mapper=new ObjectMapper();
            quPojos=mapper.readValue(jsonStr, new TypeReference<List<ST_STBPRP_B_QUPojo>>() {});
        }
        Integer integer = data.insertALL(quPojos);
        watch.stop();
        return integer;
    }
    @RequestMapping("/modify")
    public ResultUtils upDateOne(@RequestBody ST_STBPRP_B_QUPojo quPojo){
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
