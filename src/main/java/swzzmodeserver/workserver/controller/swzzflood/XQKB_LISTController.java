package swzzmodeserver.workserver.controller.swzzflood;

import swzzmodeserver.tools.ColumnName;
import swzzmodeserver.tools.ResultUtils;
import swzzmodeserver.workserver.data.swzzflood.XQKB_LISTData;
import swzzmodeserver.workserver.pojo.swzzflood.XQKB_LISTPojo;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController("FloodXQKB_LISTController")
@RequestMapping("/SWZZ_FLOOD_XQKB_LIST")
public class XQKB_LISTController {

    @Autowired
    private XQKB_LISTData data;

    @RequestMapping("/findResult")
    public ResultUtils selectList(@RequestBody ColumnName param) {
        StopWatch watch = new StopWatch();
        watch.start();
        String stime="",etime="";
        List<String> pathnameList = null;

        if (param.getStime()!=null) {
            stime = param.getStime();            
        }
        else {
            return new ResultUtils<>(null, "必传参数需传", false, 0, watch.getTime());
        }
        if (param.getEtime()!=null) {
            etime = param.getEtime();
        }
        if (param.getPathname()!=null) {
            pathnameList =Arrays.asList(param.getPathname().split(",")); 
        }
        List<XQKB_LISTPojo> list = data.selectList(pathnameList,stime,etime );
        watch.stop();
        if (list.size() > 0) {
            return new ResultUtils<>(list, "操作成功", true, list.size(), watch.getTime());
        } else {
            return new ResultUtils<>(list, "操作成功", false, list.size(), watch.getTime());
        }
    }

    @RequestMapping("/add")
    public ResultUtils insertOne(@RequestBody XQKB_LISTPojo pojo) {
        StopWatch watch = new StopWatch();
        watch.start();
        Integer result = data.insertOne(pojo);
        watch.stop();
        if (result > 0) {
            return new ResultUtils<>(result, "操作成功", true, result, watch.getTime());
        } else {
            return new ResultUtils<>(result, "操作成功", false, result, watch.getTime());
        }
    }

    @RequestMapping("/modify")
    public ResultUtils upDateOne(@RequestBody XQKB_LISTPojo pojo) {
        StopWatch watch = new StopWatch();
        watch.start();
        Integer result = data.upDateOne(pojo);
        watch.stop();
        if (result > 0) {
            return new ResultUtils<>(result, "操作成功", true, result, watch.getTime());
        } else {
            return new ResultUtils<>(result, "操作成功", false, result, watch.getTime());
        }
    }

    @RequestMapping("/remove")
    public ResultUtils deleteOne(@RequestBody ColumnName param) {
        StopWatch watch = new StopWatch();
        watch.start();
        String id = "";
        if (null != param.getStcd()) {
            id = param.getStcd();
        }
        Integer result = data.deleteOne(id);
        watch.stop();
        if (result > 0) {
            return new ResultUtils<>(result, "操作成功", true, result, watch.getTime());
        } else {
            return new ResultUtils<>(result, "操作成功", false, result, watch.getTime());
        }
    }
}