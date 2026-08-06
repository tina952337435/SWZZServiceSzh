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
import swzzmodeserver.workserver.data.swzzmode.ST_RNFL_RMODEData;
import swzzmodeserver.workserver.pojo.swzzmode.ST_RNFL_RMODEPojo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/SWZZ_MODE_ST_RNFL_RMODE")
public class ST_RNFL_RMODEController {
    @Autowired
    private ST_RNFL_RMODEData data;

    @RequestMapping("/findResult")
    public ResultUtils findResult(@RequestBody ParamField bpPojo) {
        StopWatch watch = new StopWatch();
        watch.start();
        String STCD = "", pageindex = "", pagesize = "10";
        String stime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date().getTime() - 24 * 60 * 60 * 1000), etime = "";
        if (CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo, ParamField.class))) {
            watch.stop();
            return new ResultUtils<>(null, "存在非法字符", false, -1, watch.getTime());
        }
        if (null != bpPojo.getStcd()) {
            STCD = bpPojo.getStcd();
        }
        if (null != bpPojo.getStartdate()) {
            stime = bpPojo.getStartdate();
        }
        if (null != bpPojo.getEnddate()) {
            etime = bpPojo.getEnddate();
        }
        if (null != bpPojo.getPageindex()) {
            pageindex = bpPojo.getPageindex();
        }
        if (null != bpPojo.getPagesize()) {
            pagesize = bpPojo.getPagesize();
        }
        Integer startindex = null;
        if (!"".equals(pageindex) && !"".equals(pagesize)) {
            startindex = (Integer.parseInt(pageindex) - 1) * Integer.parseInt(pagesize);
        }
        List<ST_RNFL_RMODEPojo> list = data.selectList(STCD, stime, etime, startindex, Integer.valueOf(pagesize));
        Integer count = data.selectCount(STCD, stime, etime);
        watch.stop();
        if (!"".equals(pagesize) && !"".equals(pageindex)) {
            int totalPages = count / Integer.parseInt(pagesize);
            if (count % Integer.parseInt(pagesize) != 0) totalPages += 1;
            if (list.size() > 0) {
                return new ResultUtils<>(list, "操作成功", true, Integer.parseInt(pagesize), Integer.parseInt(pageindex), totalPages, count, list.size(), watch.getTime());
            } else {
                return new ResultUtils<>(list, "操作成功", false, Integer.parseInt(pagesize), Integer.parseInt(pageindex), totalPages, count, list.size(), watch.getTime());
            }
        } else {
            if (list.size() > 0) {
                return new ResultUtils<>(list, "操作成功", true, list.size(), watch.getTime());
            } else {
                return new ResultUtils<>(list, "操作成功", false, list.size(), watch.getTime());
            }
        }
    }

    @RequestMapping("/add")
    public ResultUtils add(@RequestBody ST_RNFL_RMODEPojo bpPojo) {
        StopWatch watch = new StopWatch();
        watch.start();
        if (CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo, ST_RNFL_RMODEPojo.class))) {
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
    public ResultUtils modify(@RequestBody ST_RNFL_RMODEPojo bpPojo) {
        StopWatch watch = new StopWatch();
        watch.start();
        if (CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo, ST_RNFL_RMODEPojo.class))) {
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
        String STCD = "", YBTM = "", TM = "";
        Double FPDR = 0.0;
        if (CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo, ParamField.class))) {
            watch.stop();
            return new ResultUtils<>(null, "存在非法字符", false, -1, watch.getTime());
        }
        if (null != bpPojo.getStcd()) {
            STCD = bpPojo.getStcd();
        }
        if (null != bpPojo.getStartdate()) {
            YBTM = bpPojo.getStartdate();
        }
        if (null != bpPojo.getEnddate()) {
            TM = bpPojo.getEnddate();
        }
        if (null != bpPojo.getStrExp()) {
            FPDR = Double.valueOf(bpPojo.getStrExp());
        }
        Integer num = data.deleteOne(STCD, YBTM, TM, FPDR);
        watch.stop();
        if (num > 0) {
            return new ResultUtils<>(num, "操作成功", true, num, watch.getTime());
        } else {
            return new ResultUtils<>(num, "操作成功", false, num, watch.getTime());
        }
    }

    @RequestMapping("/insertALL")
    public ResultUtils insertALL(@RequestBody List<ST_RNFL_RMODEPojo> list) {
        StopWatch watch = new StopWatch();
        watch.start();
        Integer num = data.insertALL(list);
        watch.stop();
        if (num > 0) {
            return new ResultUtils<>(num, "操作成功", true, num, watch.getTime());
        } else {
            return new ResultUtils<>(num, "操作成功", false, num, watch.getTime());
        }
    }
}
