package swzzmodeserver.workserver.controller.swzzdata;

import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import swzzmodeserver.tools.ColumnName;
import swzzmodeserver.tools.CommonUtills;
import swzzmodeserver.tools.FieldIsValid;
import swzzmodeserver.tools.ParamField;
import swzzmodeserver.tools.ResultUtils;
import swzzmodeserver.tools.javalog;
import swzzmodeserver.workserver.data.swzzdata.EmergencyResponseInfoData;
import swzzmodeserver.workserver.pojo.swzzdata.EmergencyResponseInfoPojo;
import swzzmodeserver.workserver.server.swzzrtsq.TongbuServer;
import swzzmodeserver.workserver.server.swzzrtsq.shuiwupingServer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 应急响应信息控制器
 */
@RestController
@RequestMapping("/SWZZ_DATA_emergency_response")
public class EmergencyResponseInfoController {

    @Autowired
    private EmergencyResponseInfoData data;

    @Autowired
    private shuiwupingServer shuiwupingServer;

    @Autowired
    private TongbuServer tongbuServer;

    @Value("${file.path.templatefilepath}")
    private String filePathName;

    /**
     * 分页查询应急响应信息
     */
    @RequestMapping("/findResult")
    public ResultUtils findResult(@RequestBody ParamField bpPojo) {
        StopWatch watch = new StopWatch();
        watch.start();

        String ID = "", key = "", pageindex = "", pagesize = "10";
        List<String> type = new ArrayList<>();
        String stime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date().getTime() - 24 * 60 * 60 * 1000);
        String etime = "";

        // 1. 校验非法字符
        if (CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo, ParamField.class))) {
            watch.stop();
            return new ResultUtils<>(null, "存在非法字符", false, -1, watch.getTime());
        }

        // 2. 解析查询参数
        if (null != bpPojo.getStcd()) {
            ID = bpPojo.getStcd();
        }
        if (null != bpPojo.getKwtxt()) {
            key = bpPojo.getKwtxt();
        }
        if (null != bpPojo.getStartdate()) {
            stime = bpPojo.getStartdate();
        }
        if (null != bpPojo.getEnddate()) {
            etime = bpPojo.getEnddate();
        }
        if (null != bpPojo.getPattem()) {
            type = Arrays.asList(bpPojo.getPattem().split(","));
        }
        if (null != bpPojo.getPageindex()) {
            pageindex = bpPojo.getPageindex();
        }
        if (null != bpPojo.getPagesize()) {
            pagesize = bpPojo.getPagesize();
        }

        // 3. 计算分页起始索引
        Integer startindex = null;
        if (!"".equals(pageindex) && !"".equals(pagesize)) {
            startindex = (Integer.parseInt(pageindex) - 1) * Integer.parseInt(pagesize);
        }

        // 4. 执行查询
        List<EmergencyResponseInfoPojo> fxList = data.selectList(ID, key, stime, etime, type, startindex,
                Integer.valueOf(pagesize));
        Integer integer = data.selectCount(ID, key, stime, etime, type);

        // 5. 计算总页数
        Integer count = 1;
        if (null != pagesize && !"".equals(pagesize)) {
            count = integer / Integer.parseInt(pagesize);
            if (integer % Integer.parseInt(pagesize) != 0) {
                count += 1;
            }
        }

        watch.stop();

        // 6. 封装返回结果
        if (!"".equals(pagesize) && !"".equals(pageindex)) {
            if (fxList.size() > 0) {
                return new ResultUtils<>(fxList, "操作成功", true, Integer.parseInt(pagesize), Integer.parseInt(pageindex),
                        count, integer, fxList.size(), watch.getTime());
            } else {
                return new ResultUtils<>(fxList, "操作成功", false, Integer.parseInt(pagesize), Integer.parseInt(pageindex),
                        count, integer, fxList.size(), watch.getTime());
            }
        } else {
            if (fxList.size() > 0) {
                return new ResultUtils<>(fxList, "操作成功", true, fxList.size(), watch.getTime());
            } else {
                return new ResultUtils<>(fxList, "操作成功", false, fxList.size(), watch.getTime());
            }
        }
    }

    /**
     * 新增应急响应信息
     */
    @RequestMapping("/add")
    public ResultUtils add(@RequestBody EmergencyResponseInfoPojo bpPojo) {
        StopWatch watch = new StopWatch();
        watch.start();

        if (CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo, EmergencyResponseInfoPojo.class))) {
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

    /**
     * 修改应急响应信息
     */
    @RequestMapping("/modify")
    public ResultUtils modify(@RequestBody EmergencyResponseInfoPojo bpPojo) {
        StopWatch watch = new StopWatch();
        watch.start();

        if (CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo, EmergencyResponseInfoPojo.class))) {
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

    /**
     * 删除应急响应信息
     */
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

    // 当前市级响应
    @RequestMapping("/getSJYJXY_CURRENT")
    public ResultUtils getSJYJXY_CURRENT(@RequestBody ParamField bpPojo) {
        StopWatch watch = new StopWatch();
        watch.start();
        List<Map<String, Object>> list = shuiwupingServer.getSJYJXY_CURRENT();
        watch.stop();
        if (list.size() > 0) {
            return new ResultUtils<>(list, "操作成功", true, list.size(), watch.getTime());
        } else {
            return new ResultUtils<>(list, "操作成功", false, list.size(), watch.getTime());
        }
    }

    @RequestMapping("/SyncDataYJXY")
    public ResultUtils SyncDataYJXY(@RequestBody ColumnName param) {
        StopWatch watch = new StopWatch();
        watch.start();
        new   javalog().writelog("进入主服务SyncDataYJXY接口：",filePathName);
        int num= tongbuServer.SyncDataYJXY();
        watch.stop();
        if(num > 0){
            return new ResultUtils<>(num, "操作成功",true, num,watch.getTime());
        }else {
            return new ResultUtils<>(num, "操作成功",false, num,watch.getTime());
        }
    }
}