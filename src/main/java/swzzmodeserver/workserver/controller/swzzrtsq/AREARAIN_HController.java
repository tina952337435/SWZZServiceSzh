package swzzmodeserver.workserver.controller.swzzrtsq;

import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swzzmodeserver.tools.ColumnName;
import swzzmodeserver.tools.ResultUtils;
import swzzmodeserver.workserver.data.swzzrtsq.AREARAIN_HData;
import swzzmodeserver.workserver.pojo.swzzrtsq.AREARAIN_HPojo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

// 【修改点】在 @RestController 中指定唯一名称
@RestController
@RequestMapping("/SWZZ_RTSQ_AREARAIN_H")
public class AREARAIN_HController {
    @Autowired
    private AREARAIN_HData data;

    // 定义场次雨量的判定标准：连续多少小时无雨（或雨量<0.1mm）视为一场雨结束
    private static final int RAIN_STOP_THRESHOLD_HOURS = 6;

    @RequestMapping("/findResult")
    public ResultUtils selectList(@RequestBody ColumnName param){
        StopWatch watch = new StopWatch();
        watch.start();
        List<String> STCDList = new ArrayList<>();
        if(null != param.getStcd()){
            STCDList = Arrays.asList(param.getStcd().split(","));
        }
        String stime="",etime="";
        if(null!=param.getStime()){
            stime=param.getStime();
        }
        if(null!=param.getStime()){
            stime=param.getStime();
        }
        else{
            return new ResultUtils<>(null, "时间参数必传",true ,0,watch.getTime());
        }
        if(null!=param.getEtime()){
            etime=param.getEtime();
        }
        List<AREARAIN_HPojo> treeList = data.selectList(STCDList,stime,etime);
        watch.stop();
        if(treeList.size() > 0){
            return new ResultUtils<>(treeList, "操作成功",true ,treeList.size(),watch.getTime());
        }else {
            return new ResultUtils<>(treeList, "操作成功",false,treeList.size(),watch.getTime());
        }
    }
    @RequestMapping("/add")
    public ResultUtils insertOne(@RequestBody AREARAIN_HPojo quPojo){
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
    public ResultUtils upDateOne(@RequestBody AREARAIN_HPojo quPojo){
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

    /**
     * 获取面雨量统计看板数据
     */
    @RequestMapping("/getRainDashboard")
    public ResultUtils getRainDashboard(@RequestBody ColumnName param) {
        String stcd="";
        if(param.getStcd()!=null){
            stcd=param.getStcd();
        }

        StopWatch watch = new StopWatch();
        watch.start();
        Map<String, Object> result = new HashMap<>();

        // 1. 计算滑动时段雨量 (1h, 3h, 6h, 12h, 24h)
        result.put("rain_1h", format(data.sumRainByHours(stcd, 1,param.getEtime())));
        result.put("rain_3h", format(data.sumRainByHours(stcd, 3,param.getEtime())));
        result.put("rain_6h", format(data.sumRainByHours(stcd, 6,param.getEtime())));
        result.put("rain_12h", format(data.sumRainByHours(stcd, 12,param.getEtime())));
        result.put("rain_24h", format(data.sumRainByHours(stcd, 24,param.getEtime())));

        // 2. 计算场次雨量 (通过 Java 逻辑分析最近的数据)
        BigDecimal eventRain = calculateEventRain(stcd,param.getEtime());
        result.put("rain_event", format(eventRain));

//        return result;
        watch.stop();
        if(result.size() > 0){
            return new ResultUtils<>(result, "操作成功",true ,result.size(),watch.getTime());
        }else {
            return new ResultUtils<>(result, "操作成功",false,result.size(),watch.getTime());
        }
    }

    /**
     * 计算场次雨量逻辑：
     * 从当前时间往前推，直到遇到连续 N 小时雨量接近 0，则切断，之前的累加和为场次雨量。
     */
    private BigDecimal calculateEventRain(String stcd, String etime) {
        // 获取过去 72 小时的明细
        List<AREARAIN_HPojo> details = data.selectRecentDetails(stcd, 72, etime);

        if (details == null || details.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal total = BigDecimal.ZERO;
        int zeroCount = 0;
        // 假设阈值为 6 小时无雨视为场次结束 (根据实际需求调整 RAIN_STOP_THRESHOLD_HOURS)
        int threshold = RAIN_STOP_THRESHOLD_HOURS;

        // 遍历列表 (假设列表是倒序：最新 -> 最旧)
        for (AREARAIN_HPojo item : details) {
            double rainVal = item.getDRP() == null ? 0.0 : item.getDRP().doubleValue();

            // 判断是否为有效降雨 (< 0.1mm 视为无雨)
            if (rainVal < 0.1) {
                zeroCount++;

                // 【关键修改】：如果连续无雨超过阈值，说明这场雨已经彻底结束了。
                // 之前的累加值就是我们要的结果，直接跳出循环，不要清零！
                if (zeroCount >= threshold) {
                    break;
                }
                // 如果只是短时间无雨（比如雨中间停了1小时），继续累加后面的（其实是前面的时间）
                // 注意：这里不需要做任何操作，继续下一次循环
            } else {
                // 有雨
                total = total.add(BigDecimal.valueOf(item.getDRP()));
                // 遇到有雨，重置无雨计数器（说明雨还在下，或者只是短暂间歇）
                zeroCount = 0;
            }
        }

        return total;
    }
    
    // 格式化保留一位小数
    private BigDecimal format(BigDecimal val) {
        if (val == null) return BigDecimal.ZERO;
        return val.setScale(1, RoundingMode.HALF_UP);
    }
}
