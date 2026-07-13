package swzzmodeserver.workserver.controller.swzzmode;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swzzmodeserver.tools.*;
import swzzmodeserver.workserver.data.swzzmode.ES_ZHANDIANDATA_YUANData;
import swzzmodeserver.workserver.pojo.Huishui.GetSubjectListPojo;
import swzzmodeserver.workserver.pojo.swzzmode.DD_SOLUTIONPojo;
import swzzmodeserver.workserver.pojo.swzzmode.ES_ZHANDIANDATA_YUANPojo;
import swzzmodeserver.workserver.pojo.swzzzjk.ST_TIDEHIGHParam;
import swzzmodeserver.workserver.service.swzzmode.HuishuiApiService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/SWZZ_MODE_ES_ZHANDIANDATA_YUAN")
public class ES_ZHANDIANDATA_YUANController {
    @Autowired
    private ES_ZHANDIANDATA_YUANData data;

    @Autowired
    private HuishuiApiService huishuiApiService;

    @RequestMapping("/findResult")
    public ResultUtils findResult(@RequestBody ParamField bpPojo) {
        StopWatch watch = new StopWatch();
        watch.start();
        String soid = "";
        List<String> type = new ArrayList<>();
        String stime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date().getTime() - 24 * 60 * 60 * 1000),
                etime = "";
        if (CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo, ParamField.class))) {
            watch.stop();
            return new ResultUtils<>(null, "存在非法字符", false, -1, watch.getTime());
        }
        if (null != bpPojo.getPattem()) {
            type = Arrays.asList(bpPojo.getPattem().split(","));
        }
        if (null != bpPojo.getStrExp()) {
            soid = bpPojo.getStrExp();
        }
        List<ES_ZHANDIANDATA_YUANPojo> fxList = data.selectList(soid, type);
        watch.stop();
        if (fxList.size() > 0) {
            return new ResultUtils<>(fxList, "操作成功", true, fxList.size(), watch.getTime());
        } else {
            return new ResultUtils<>(fxList, "操作成功", false, fxList.size(), watch.getTime());
        }
    }

    @RequestMapping("/add")
    public ResultUtils add(@RequestBody ES_ZHANDIANDATA_YUANPojo bpPojo) {
        StopWatch watch = new StopWatch();
        watch.start();
        if (CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo, ES_ZHANDIANDATA_YUANPojo.class))) {
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
    public ResultUtils modify(@RequestBody ES_ZHANDIANDATA_YUANPojo bpPojo) {
        StopWatch watch = new StopWatch();
        watch.start();
        if (CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo, ES_ZHANDIANDATA_YUANPojo.class))) {
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
        String ID = "", strExp = "", SOLUTIONID = "";
        Integer num = 0;
        if (CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo, ParamField.class))) {
            watch.stop();
            return new ResultUtils<>(null, "存在非法字符", false, -1, watch.getTime());
        }
        if (null != bpPojo.getStcd()) {
            ID = bpPojo.getStcd();
        }
        if (null != bpPojo.getStrExp()) {
            strExp = bpPojo.getStrExp();
        }
        if (null != bpPojo.getStcd()) {
            SOLUTIONID = bpPojo.getStcd();
        }
        if (strExp.equals("SOLUTIONID")) {
            num = data.deleteOneBySOLUTIONID(SOLUTIONID);
        } else {
            num = data.deleteOne(ID);
        }

        watch.stop();
        if (num > 0) {
            return new ResultUtils<>(num, "操作成功", true, num, watch.getTime());
        } else {
            return new ResultUtils<>(num, "操作成功", false, num, watch.getTime());
        }
    }

    @RequestMapping("/batchResult/{pattem}")
    public ResultUtils batchResult(@PathVariable("pattem") String pattem,
            @RequestBody List<ES_ZHANDIANDATA_YUANPojo> bpPojo) {
        StopWatch watch = new StopWatch();
        watch.start();
        String type = "";
        if (CommonUtills.isEmpty(FieldIsValid.getListColumnName(bpPojo, ES_ZHANDIANDATA_YUANPojo.class))
                || !FieldIsValid.isValid(pattem)) {
            watch.stop();
            return new ResultUtils<>(null, "存在非法字符", false, -1, watch.getTime());
        }
        if (null != pattem) {
            type = pattem;
        }
        Integer num = 0;
        int count = 80;
        int number = bpPojo.size() / count;
        if (bpPojo.size() % count != 0) {
            number = number + 1;
        }
        List<ES_ZHANDIANDATA_YUANPojo> list = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            if (i == number - 1) {
                list = bpPojo.subList(count * i, bpPojo.size());
            } else {
                list = bpPojo.subList(count * i, count * (i + 1));
            }
            if (type.equals("true")) {
                num += data.updateALL(list);
            } else {
                num += data.insertALL(list);
            }
        }
        watch.stop();
        if (num > 0) {
            return new ResultUtils<>(num, "操作成功", true, num, watch.getTime());
        } else {
            return new ResultUtils<>(num, "操作成功", false, num, watch.getTime());
        }
    }

    @RequestMapping("/batchRemove")
    public ResultUtils batchRemove(String pattem, @RequestBody List<ParamField> bpPojo) {
        StopWatch watch = new StopWatch();
        watch.start();
        if (CommonUtills.isEmpty(FieldIsValid.getListColumnName(bpPojo, ParamField.class))) {
            watch.stop();
            return new ResultUtils<>(null, "存在非法字符", false, -1, watch.getTime());
        }
        Integer num = 0;
        int count = 80;
        int number = bpPojo.size() / count;
        if (bpPojo.size() % count != 0) {
            number = number + 1;
        }
        List<ParamField> list = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            if (i == number - 1) {
                list = bpPojo.subList(count * i, bpPojo.size());
            } else {
                list = bpPojo.subList(count * i, count * (i + 1));
            }
            num += data.deleteALL(list);
        }
        watch.stop();
        if (num > 0) {
            return new ResultUtils<>(num, "操作成功", true, num, watch.getTime());
        } else {
            return new ResultUtils<>(num, "操作成功", false, num, watch.getTime());
        }
    }
}
