package swzzmodeserver.workserver.controller.swzzflood;

import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swzzmodeserver.tools.*;
import swzzmodeserver.workserver.data.swzzflood.TB_TIDE_HIGHLOWData;
import swzzmodeserver.workserver.pojo.swzzflood.TB_TIDE_HIGHLOWPojo;

import java.util.*;

@RestController
@RequestMapping("/swzzflood_tb_tide_highlow")
public class TB_TIDE_HIGHLOWController {
    @Autowired
    private TB_TIDE_HIGHLOWData data;

    @Autowired
    private CommonUtills commonUtills;

    @RequestMapping("/findResult")
    public ResultUtils findResult(@RequestBody ParamField bpPojo) {
        StopWatch watch = new StopWatch();
        watch.start();
        List<String> idList = new ArrayList<>();
        String stime = "" , etime = "";
        ParamField params = FieldIsValid.getColumnName(bpPojo, ParamField.class);
        if (CommonUtills.isEmpty(params)) {
            watch.stop();
            return new ResultUtils<>(null, "存在非法字符", false, -1, watch.getTime());
        }
        if (null != bpPojo.getStcd()) {
            idList = Arrays.asList(bpPojo.getStcd().split(","));
        }
        if (null != bpPojo.getStartdate()) {
            stime = bpPojo.getStartdate();
        }
        if (null != bpPojo.getEnddate()) {
            etime = bpPojo.getEnddate();
        }
        List<TB_TIDE_HIGHLOWPojo> fxList = data.selectList(idList, stime, etime);
        watch.stop();
        if (fxList.size() > 0) {
            return new ResultUtils<>(fxList, "操作成功", true, fxList.size(), watch.getTime());
        } else {
            return new ResultUtils<>(fxList, "操作成功", false, fxList.size(), watch.getTime());
        }
    }
}
