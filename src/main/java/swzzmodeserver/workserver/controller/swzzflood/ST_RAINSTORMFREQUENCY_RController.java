package swzzmodeserver.workserver.controller.swzzflood;

import swzzmodeserver.tools.ColumnName;
import swzzmodeserver.tools.ResultUtils;
import swzzmodeserver.workserver.data.swzzflood.ST_RAINSTORMFREQUENCY_RData;
import swzzmodeserver.workserver.pojo.swzzflood.ST_RAINSTORMFREQUENCY_RPojo;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("FloodST_RAINSTORMFREQUENCY_RController")
@RequestMapping("/SWZZ_FLOOD_ST_RAINSTORMFREQUENCY_R")
public class ST_RAINSTORMFREQUENCY_RController {

    @Autowired
    private ST_RAINSTORMFREQUENCY_RData data;

    @RequestMapping("/findResult")
    public ResultUtils selectList(@RequestBody ColumnName param) {
        StopWatch watch = new StopWatch();
        watch.start();
        String stcd = "", stime = "", etime = "";

        if (param.getStcd() != null) {
            stcd = param.getStcd();
        }
        if (param.getStime() != null) {
            stime = param.getStime();
        }
        if (param.getEtime() != null) {
            etime = param.getEtime();
        }

        List<ST_RAINSTORMFREQUENCY_RPojo> list = data.selectList(stcd, stime, etime);
        watch.stop();
        if (list.size() > 0) {
            return new ResultUtils<>(list, "操作成功", true, list.size(), watch.getTime());
        } else {
            return new ResultUtils<>(list, "操作成功", false, list.size(), watch.getTime());
        }
    }

    @RequestMapping("/add")
    public ResultUtils insertOne(@RequestBody ST_RAINSTORMFREQUENCY_RPojo pojo) {
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
    public ResultUtils upDateOne(@RequestBody ST_RAINSTORMFREQUENCY_RPojo pojo) {
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
        String stcd = "", bgTm = "", endTm = "";

        if (param.getStcd() != null) {
            stcd = param.getStcd();
        }
        if (param.getStime() != null) {
            bgTm = param.getStime();
        }
        if (param.getEtime() != null) {
            endTm = param.getEtime();
        }

        Integer result = data.deleteOne(stcd, bgTm, endTm);
        watch.stop();
        if (result > 0) {
            return new ResultUtils<>(result, "操作成功", true, result, watch.getTime());
        } else {
            return new ResultUtils<>(result, "操作成功", false, result, watch.getTime());
        }
    }
}