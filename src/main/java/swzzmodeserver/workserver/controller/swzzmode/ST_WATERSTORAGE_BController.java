package swzzmodeserver.workserver.controller.swzzmode;

import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swzzmodeserver.tools.CommonUtills;
import swzzmodeserver.tools.FieldIsValid;
import swzzmodeserver.tools.ParamField;
import swzzmodeserver.tools.ResultUtils;
import swzzmodeserver.workserver.data.swzzmode.ST_WATERSTORAGE_BData;
import swzzmodeserver.workserver.pojo.swzzmode.ST_WATERSTORAGE_BPojo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/SWZZ_MODE_ST_WATERSTORAGE_B")
public class ST_WATERSTORAGE_BController {
    @Autowired
    private ST_WATERSTORAGE_BData data;

    @RequestMapping("/findResult")
    public ResultUtils findResult(@RequestBody ParamField bpPojo) {
        StopWatch watch = new StopWatch();
        watch.start();
        String ID = "", TYPE = "";
        if (CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo, ParamField.class))) {
            watch.stop();
            return new ResultUtils<>(null, "存在非法字符", false, -1, watch.getTime());
        }
        if (null != bpPojo.getStcd()) {
            ID = bpPojo.getStcd();
        }
        if (null != bpPojo.getKwtxt()) {
            TYPE = bpPojo.getKwtxt();
        }
        List<ST_WATERSTORAGE_BPojo> fxList = data.selectList(ID, TYPE);
        if (fxList.size() > 0) {
            return new ResultUtils<>(fxList, "操作成功", true, fxList.size(), watch.getTime());
        } else {
            return new ResultUtils<>(fxList, "操作成功", false, fxList.size(), watch.getTime());
        }
    }

    @RequestMapping("/add")
    public ResultUtils add(@RequestBody ST_WATERSTORAGE_BPojo bpPojo) {
        StopWatch watch = new StopWatch();
        watch.start();
        if (CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo, ST_WATERSTORAGE_BPojo.class))) {
            watch.stop();
            return new ResultUtils<>(null, "存在非法字符", false, -1, watch.getTime());
        }
        Integer num = data.insertOne(bpPojo);
        watch.stop();
        if (num > 0) {
            return new ResultUtils<>(num, "操作成功", true, num, watch.getTime());
        } else {
            return new ResultUtils<>(num, "操作成功", false, num, watch.getTime());
        }
    }

    @RequestMapping("/modify")
    public ResultUtils modify(@RequestBody ST_WATERSTORAGE_BPojo bpPojo) {
        StopWatch watch = new StopWatch();
        watch.start();
        if (CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo, ST_WATERSTORAGE_BPojo.class))) {
            watch.stop();
            return new ResultUtils<>(null, "存在非法字符", false, -1, watch.getTime());
        }
        Integer num = data.updateOne(bpPojo);
        watch.stop();
        if (num > 0) {
            return new ResultUtils<>(num, "操作成功", true, num, watch.getTime());
        } else {
            return new ResultUtils<>(num, "操作成功", false, num, watch.getTime());
        }
    }

    @RequestMapping("/remove")
    public ResultUtils remove(@RequestBody ParamField bpPojo) {
        StopWatch watch = new StopWatch();
        watch.start();
        String ID = "";
        if (CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo, ParamField.class))) {
            watch.stop();
            return new ResultUtils<>(null, "存在非法字符", false, -1, watch.getTime());
        }
        if (null != bpPojo.getStcd()) {
            ID = bpPojo.getStcd();
        }
        Integer num = data.deleteOne(ID);
        watch.stop();
        if (num > 0) {
            return new ResultUtils<>(num, "操作成功", true, num, watch.getTime());
        } else {
            return new ResultUtils<>(num, "操作成功", false, num, watch.getTime());
        }
    }
}