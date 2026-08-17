package swzzmodeserver.workserver.controller.swzzmode;

import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swzzmodeserver.tools.ColumnName;
import swzzmodeserver.tools.ResultUtils;
import swzzmodeserver.workserver.data.swzzmode.ESSlpBaseData;
import swzzmodeserver.workserver.pojo.swzzmode.ESSlpBasePojo;
import swzzmodeserver.workserver.service.swzzmode.SlpForecastService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 水利片水位预测接口
 */
@RestController("SlpForecastController")
public class SlpForecastController {

    @Autowired
    private ESSlpBaseData esslpBaseData;

    @Autowired
    private SlpForecastService slpForecastService;

    // ===================== 基础资料 CRUD =====================

    @RequestMapping("/SWZZ_SLP_BASE/findResult")
    public ResultUtils<List<ESSlpBasePojo>> findResult() {
        StopWatch watch = new StopWatch();
        watch.start();
        List<ESSlpBasePojo> list = esslpBaseData.selectList();
        watch.stop();
        return new ResultUtils<>(list, "操作成功", true, list.size(), watch.getTime());
    }

    @RequestMapping("/SWZZ_SLP_BASE/add")
    public ResultUtils<Integer> add(@RequestBody ESSlpBasePojo pojo) {
        StopWatch watch = new StopWatch();
        watch.start();
        if (pojo.getID() == null || pojo.getID().isEmpty()) {
            pojo.setID(UUID.randomUUID().toString());
        }
        Integer r = esslpBaseData.insertOne(pojo);
        watch.stop();
        return new ResultUtils<>(r, "操作成功", r > 0, r, watch.getTime());
    }

    @RequestMapping("/SWZZ_SLP_BASE/modify")
    public ResultUtils<Integer> modify(@RequestBody ESSlpBasePojo pojo) {
        StopWatch watch = new StopWatch();
        watch.start();
        Integer r = esslpBaseData.updateOne(pojo);
        watch.stop();
        return new ResultUtils<>(r, "操作成功", r > 0, r, watch.getTime());
    }

    @RequestMapping("/SWZZ_SLP_BASE/remove")
    public ResultUtils<Integer> remove(@RequestBody ColumnName param) {
        StopWatch watch = new StopWatch();
        watch.start();
        Integer r = esslpBaseData.deleteOne(param.getPid());
        watch.stop();
        return new ResultUtils<>(r, "操作成功", r > 0, r, watch.getTime());
    }

    // ===================== 预测查询 =====================

    @RequestMapping("/SWZZ_SLP_FORECAST/query")
    public ResultUtils<List<Map<String, Object>>> query(@RequestBody Map<String, String> param) {
        StopWatch watch = new StopWatch();
        watch.start();
        String ddId = param.get("ddId");
        String stime = param.get("stime");
        List<Map<String, Object>> list = slpForecastService.query(ddId, stime);
        watch.stop();
        return new ResultUtils<>(list, "操作成功", true, list.size(), watch.getTime());
    }
}
