package swzzmodeserver.workserver.controller.swzzrtsq;

import swzzmodeserver.tools.ColumnName;
import swzzmodeserver.tools.ResultUtils;
import swzzmodeserver.workserver.data.swzzrtsq.RTSQGL_STCDData;
import swzzmodeserver.workserver.pojo.swzzrtsq.GL_STCDPojo;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
// 【修改点】在 @RestController 中指定唯一名称
@RestController("RtsqGL_STCDController")
@RequestMapping("/SWZZ_RTSQ_GL_STCD")
public class GL_STCDController {
    @Autowired
    private RTSQGL_STCDData data;

    @RequestMapping("/findResult")
    public ResultUtils selectList(@RequestBody GL_STCDPojo param){
        StopWatch watch = new StopWatch();
        watch.start();
        List<String> STCDList = new ArrayList<>();
        if(null != param.getSTCD()){
            STCDList = Arrays.asList(param.getSTCD().split(","));
        }
        List<GL_STCDPojo> treeList = data.selectList(STCDList);
        watch.stop();
        if(treeList.size() > 0){
            return new ResultUtils<>(treeList, "操作成功",true ,treeList.size(),watch.getTime());
        }else {
            return new ResultUtils<>(treeList, "操作成功",false,treeList.size(),watch.getTime());
        }
    }
    @RequestMapping("/add")
    public ResultUtils insertOne(@RequestBody GL_STCDPojo quPojo){
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
    public ResultUtils upDateOne(@RequestBody GL_STCDPojo quPojo){
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
}
