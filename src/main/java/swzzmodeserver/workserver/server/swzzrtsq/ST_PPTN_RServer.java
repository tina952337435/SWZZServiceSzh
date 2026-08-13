package swzzmodeserver.workserver.server.swzzrtsq;

import swzzmodeserver.workserver.data.swzzrtsq.RTSQST_PPTN_RData;
import swzzmodeserver.workserver.pojo.swzzrtsq.ST_PPTN_RPojo;
import swzzmodeserver.tools.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import swzzmodeserver.workserver.pojo.swzzrtsq.MaxRainResultPojo;
import swzzmodeserver.workserver.pojo.swzzrtsq.ST_STBPRP_BPojo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ST_PPTN_RServer {
    @Autowired
    private RTSQST_PPTN_RData data;

    public List<ST_PPTN_RPojo> selectListByDay(List<String> stcdList,String stime,String etime,String mtype){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long timedif = 2,sTime = 0,eTime = 0;
        if(!(etime.equals("") && stime.equals(""))){
            try {
                sTime = dateFormat.parse(stime).getTime();
                eTime = dateFormat.parse(etime).getTime();
                timedif =  (eTime - sTime) / (24 * 60 * 60 * 1000);
                if(Integer.valueOf(new SimpleDateFormat("HH").format(eTime)) > 8){
                    timedif = timedif + 1;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        System.out.println("timedif = " + timedif);
        //查询降雨数据
        List<ST_PPTN_RPojo> msprlist = data.selectListByTimeNull(stcdList, stime, etime,mtype);
        List<ST_PPTN_RPojo> sprlist = null != msprlist ? (msprlist.stream().filter(i -> null != i.getDRP() && i.getDRP() > 0).collect(Collectors.toList())) : msprlist;
        List<ST_PPTN_RPojo> pptnList = new ArrayList<>();
        if(null != sprlist){
            for(String stcd : stcdList){
                List<ST_PPTN_RPojo> collect = sprlist.stream().filter(i -> i.getSTCD().equals(stcd) && null != i.getDRP() && i.getDRP() > 0).collect(Collectors.toList());
                for(int i = 0;i < timedif;i++){
                    Double DRPSum = 0.00;
                    int finalI = i;
                    long finalSTime = sTime;
                    List<ST_PPTN_RPojo> collectList = collect.stream().filter(j -> {
                        if(null != j.getTM()){
                            Date stm = DateUtil.addTimeToDate(new Date(finalSTime), "d" ,finalI);
                            Date etm = DateUtil.addTimeToDate(DateUtil.addTimeToDate(new Date(finalSTime), "d" ,finalI + 1),"n",5);
                            try {
                                return dateFormat.parse(j.getTM()).after(stm) && dateFormat.parse(j.getTM()).before(etm);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        return false;
                    }).collect(Collectors.toList());
                    for(ST_PPTN_RPojo pptnRPojo : collectList){
                        if(null != pptnRPojo.getDRP())
                            DRPSum += pptnRPojo.getDRP();
                    }

                    Date newDate =   DateUtil.addTimeToDate(new Date(sTime), "d" ,finalI);

                    ST_PPTN_RPojo pptnRPojo = new ST_PPTN_RPojo();
                    pptnRPojo.setSTCD(stcd);
                    pptnRPojo.setDRP(Double.parseDouble( String.format("%.1f",DRPSum)));
                    pptnRPojo.setTM(dateFormat.format(newDate));
//                    pptnRPojo.setTM(dateFormat.format(new Date(sTime + (24 * 60 * 60 * 1000) * finalI)));
                    pptnList.add(pptnRPojo);
                }
            }
        }
        return pptnList;
    }

    public List<ST_PPTN_RPojo> selectListByHour(List<String> stcdList,String stime,String etime,String mtype){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long timedif = 2,sTime = 0,eTime = 0;
        if(!(etime.equals("") && stime.equals(""))){
            try {
                sTime = dateFormat.parse(stime).getTime();
                eTime = dateFormat.parse(etime).getTime();
                timedif =  (eTime - sTime) / (60 * 60 * 1000);
                if(Integer.valueOf(new SimpleDateFormat("HH").format(eTime)) > 8){
                    timedif = timedif + 1;
                }
//                System.out.println("timedif = " + timedif);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        //查询降雨数据
        List<ST_PPTN_RPojo> sprlist = data.selectListByTime(stcdList, stime, etime,mtype);
        List<ST_PPTN_RPojo> pptnList = new ArrayList<>();
        if(null != sprlist){
            for(String stcd : stcdList){
                List<ST_PPTN_RPojo> collect = sprlist.stream().filter(i -> i.getSTCD().equals(stcd) && null != i.getDRP() && i.getDRP() > 0).collect(Collectors.toList());

                System.out.println("collect.size = " + collect.size());
                for(int i = 0;i < timedif;i++){
                    Double DRPSum = 0.00;
                    int finalI = i;
                    long finalSTime = sTime;
                    Date newDate =   DateUtil.addTimeToDate(new Date(sTime), "h" ,finalI);
//                    String tm = dateFormat.format(new Date(sTime + (1 * 60 * 60 * 1000) * (finalI + 1)));
//                    System.out.println("finalSTime " + tm +" S > " + dateForhmat.format(new Date(finalSTime + (1 * 60 * 60 * 1000) * finalI)) + " S <= " + dateFormat.format(new Date((finalSTime + (1 * 60 * 60 * 1000) * (finalI + 1)))));

                    List<ST_PPTN_RPojo> collectList = collect.stream().filter(j -> {
                        if(null != j.getTM()){
                              try {
                                Date stm = DateUtil.addTimeToDate(new Date(finalSTime), "h" ,finalI);
                                Date etm = DateUtil.addTimeToDate(DateUtil.addTimeToDate(new Date(finalSTime), "h" ,finalI + 1),"n",5);
                                return dateFormat.parse(j.getTM()).after(stm) && dateFormat.parse(j.getTM()).before(etm);
//                                    return dateFormat.parse(j.getTM()).getTime() > finalSTime + (1 * 60 * 60 * 1000) * finalI
//                                        && dateFormat.parse(j.getTM()).getTime() <= finalSTime + (1 * 60 * 60 * 1000) * (finalI + 1);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        return false;
                    }).collect(Collectors.toList());
                    for(ST_PPTN_RPojo pptnRPojo : collectList){
                        if(null != pptnRPojo.getDRP())
                            DRPSum += pptnRPojo.getDRP();
                    }

                    ST_PPTN_RPojo pptnRPojo = new ST_PPTN_RPojo();
                    pptnRPojo.setSTCD(stcd);
                    pptnRPojo.setDRP(Double.parseDouble( String.format("%.1f",DRPSum)));
                    pptnRPojo.setTM(dateFormat.format(newDate));
                    pptnList.add(pptnRPojo);
                }
            }
        }
        return pptnList;
    }

    /// isNull true 过滤掉降雨为0的数据
    public List<ST_PPTN_RPojo> selectListByHour(List<ST_PPTN_RPojo> sprlist,List<String> stcdList,String stime,String etime,boolean isNull){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long timedif = 2,sTime = 0,eTime = 0;
        if(!(etime.equals("") && stime.equals(""))){
            try {
                sTime = dateFormat.parse(stime).getTime();
                eTime = dateFormat.parse(etime).getTime();
                timedif =  (eTime - sTime) / (60 * 60 * 1000);
                if(Integer.valueOf(new SimpleDateFormat("HH").format(eTime)) > 8){
                    timedif = timedif + 1;
                }
//                System.out.println("timedif = " + timedif);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        //查询降雨数据
//        List<ST_PPTN_RPojo> sprlist = new ArrayList<>();
//        if(isNull){
//            sprlist = data.selectListByTimeNull(stcdList, stime, etime);
//        }else{
//            sprlist = data.selectListByTime(stcdList, stime, etime);
//        }
        List<ST_PPTN_RPojo> pptnList = new ArrayList<>();
        if(null != sprlist){
            for(String stcd : stcdList){
                List<ST_PPTN_RPojo> collect = sprlist.stream().filter(i -> i.getSTCD().equals(stcd) && null != i.getDRP() && i.getDRP() > 0).collect(Collectors.toList());

                System.out.println("collect.size = " + collect.size());
                for(int i = 0;i < timedif;i++){
                    Double DRPSum = 0.00;
                    int finalI = i;
                    long finalSTime = sTime;
                    Date newDate =   DateUtil.addTimeToDate(new Date(sTime), "h" ,finalI);
//                    String tm = dateFormat.format(new Date(sTime + (1 * 60 * 60 * 1000) * (finalI + 1)));
//                    System.out.println("finalSTime " + tm +" S > " + dateForhmat.format(new Date(finalSTime + (1 * 60 * 60 * 1000) * finalI)) + " S <= " + dateFormat.format(new Date((finalSTime + (1 * 60 * 60 * 1000) * (finalI + 1)))));

                    List<ST_PPTN_RPojo> collectList = collect.stream().filter(j -> {
                        if(null != j.getTM()){
                            try {
                                Date stm = DateUtil.addTimeToDate(new Date(finalSTime), "h" ,finalI);
                                Date etm = DateUtil.addTimeToDate(DateUtil.addTimeToDate(new Date(finalSTime), "h" ,finalI + 1),"n",5);
                                return dateFormat.parse(j.getTM()).after(stm) && dateFormat.parse(j.getTM()).before(etm);
//                                    return dateFormat.parse(j.getTM()).getTime() > finalSTime + (1 * 60 * 60 * 1000) * finalI
//                                        && dateFormat.parse(j.getTM()).getTime() <= finalSTime + (1 * 60 * 60 * 1000) * (finalI + 1);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        return false;
                    }).collect(Collectors.toList());
                    for(ST_PPTN_RPojo pptnRPojo : collectList){
                        if(null != pptnRPojo.getDRP())
                            DRPSum += pptnRPojo.getDRP();
                    }

                    ST_PPTN_RPojo pptnRPojo = new ST_PPTN_RPojo();
                    pptnRPojo.setSTCD(stcd);
                    pptnRPojo.setDRP(Double.parseDouble( String.format("%.1f",DRPSum)));
                    pptnRPojo.setTM(dateFormat.format(newDate));
                    pptnList.add(pptnRPojo);
                }
            }
        }
        return pptnList;
    }

    /**
     * 计算多时段最大滑动雨量
     * @param rainData 雨量数据（仅DRP>0的记录，含STCD, TM, DRP）
     * @param stationInfoList 站点基础信息（STCD, STNM, HNNM, LGTD, LTTD）
     * @param stime 查询开始时间
     * @param etime 查询结束时间
     * @return MaxRainResultPojo
     */
    public MaxRainResultPojo calculateMaxSlidingRain(List<ST_PPTN_RPojo> rainData,
                                                      List<ST_STBPRP_BPojo> stationInfoList,
                                                      String stime, String etime) {
        long t0 = System.currentTimeMillis();

        MaxRainResultPojo result = new MaxRainResultPojo();
        List<MaxRainResultPojo.StationItem> stationItems = new ArrayList<>();

        // 构建 STCD → 站点基础信息的 Map
        Map<String, ST_STBPRP_BPojo> infoMap = new HashMap<>();
        if (stationInfoList != null) {
            for (ST_STBPRP_BPojo b : stationInfoList) {
                if (b.getSTCD() != null) {
                    infoMap.put(b.getSTCD(), b);
                }
            }
        }

        // 收集所有 STCD
        Set<String> allStcds = new LinkedHashSet<>();
        allStcds.addAll(infoMap.keySet());

        // 按 STCD 分组，构建 (epoch → drp×10) 的 Map
        // 【优化】用快速解析替代 SimpleDateFormat.parse()，避免每条记录创建 Calendar 对象
        Map<String, Map<Long, Long>> rainMap = new HashMap<>();
        int rainRecordCount = 0;
        if (rainData != null) {
            for (ST_PPTN_RPojo r : rainData) {
                if (r.getSTCD() == null || r.getTM() == null || r.getDRP() == null) continue;
                try {
                    long epoch = parseEpochFast(r.getTM());
                    long val = Math.round(r.getDRP() * 10.0);
                    rainMap.computeIfAbsent(r.getSTCD(), k -> new HashMap<>()).put(epoch, val);
                    rainRecordCount++;
                } catch (Exception ignored) {}
            }
        }

        long t1 = System.currentTimeMillis();

        // 解析起止时间为 epoch
        long stEpoch = parseEpochFast(stime);
        long etEpoch = parseEpochFast(etime);
        long gridStep = 5 * 60 * 1000L; // 5 分钟
        int gridSize = (int) ((etEpoch - stEpoch) / gridStep) + 1;

        long[] windowMs = {60L * 60 * 1000, 3L * 60 * 60 * 1000, 6L * 60 * 60 * 1000,
                12L * 60 * 60 * 1000, 24L * 60 * 60 * 1000};

        // 仅用于输出格式化（调用次数少，无需优化）
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 【优化】times 数组对所有站点相同，只构建一次
        long[] times = new long[gridSize];
        for (int i = 0; i < gridSize; i++) {
            times[i] = stEpoch + i * gridStep;
        }

        int stationWithRain = 0;
        int stationNoRain = 0;
        for (String stcd : allStcds) {
            Map<Long, Long> stRain = rainMap.getOrDefault(stcd, Collections.emptyMap());

            // 构建该站点的前缀和数组
            long[] prefix = new long[gridSize + 1];
            for (int i = 0; i < gridSize; i++) {
                Long v = stRain.get(times[i]);
                long drpLong = (v != null) ? v : 0L;
                prefix[i + 1] = prefix[i] + drpLong;
            }

            // totalDrp = 整个时段累计雨量
            double totalDrp = prefix[gridSize] / 10.0;

            MaxRainResultPojo.StationItem item = new MaxRainResultPojo.StationItem();
            item.setStcd(stcd);
            item.setTotalDrp(totalDrp);

            ST_STBPRP_BPojo info = infoMap.get(stcd);
            if (info != null) {
                item.setStnm(info.getSTNM());
                item.setHnnm(info.getHNNM());
                item.setAddvnm(info.getADDVNM());
                item.setLgtd(info.getLGTD());
                item.setLttd(info.getLTTD());
            }

            // 如果该站没有雨量数据，直接填充空结果跳过滑动窗口计算
            if (stRain.isEmpty()) {
                stationNoRain++;
                for (int w = 0; w < 5; w++) {
                    MaxRainResultPojo.WindowRainInfo win = new MaxRainResultPojo.WindowRainInfo();
                    win.setStcd(stcd);
                    win.setDrp(0.0);
                    if (info != null) {
                        win.setStnm(info.getSTNM());
                    }
                    setWindowField(item, w, win);
                }
                stationItems.add(item);
                continue;
            }
            stationWithRain++;

            // 单次遍历计算全部 5 个窗口
            long[] maxSums = new long[5];
            int[] maxLefts = new int[5];
            int[] maxRights = new int[5];
            int[] lefts = new int[5];

            for (int right = 0; right < gridSize; right++) {
                long tRight = times[right];
                for (int w = 0; w < 5; w++) {
                    while (lefts[w] < right && tRight - times[lefts[w]] >= windowMs[w]) {
                        lefts[w]++;
                    }
                    long sum = prefix[right + 1] - prefix[lefts[w]];
                    if (sum > maxSums[w]) {
                        maxSums[w] = sum;
                        maxLefts[w] = lefts[w];
                        maxRights[w] = right;
                    }
                }
            }

            for (int w = 0; w < 5; w++) {
                MaxRainResultPojo.WindowRainInfo win = new MaxRainResultPojo.WindowRainInfo();
                win.setStcd(stcd);
                win.setDrp(maxSums[w] / 10.0);
                win.setStime(sdf.format(new Date(times[maxLefts[w]])));
                win.setEtime(sdf.format(new Date(times[maxRights[w]])));
                if (info != null) {
                    win.setStnm(info.getSTNM());
                }
                setWindowField(item, w, win);
            }

            stationItems.add(item);
        }

        long t2 = System.currentTimeMillis();

        result.setStations(stationItems);

        MaxRainResultPojo.Summary summary = new MaxRainResultPojo.Summary();
        summary.setMax60min(findGlobalMax(stationItems, 0));
        summary.setMax3h(findGlobalMax(stationItems, 1));
        summary.setMax6h(findGlobalMax(stationItems, 2));
        summary.setMax12h(findGlobalMax(stationItems, 3));
        summary.setMax24h(findGlobalMax(stationItems, 4));
        result.setSummary(summary);

        long t3 = System.currentTimeMillis();

        System.out.println("========== calculateMaxSlidingRain 耗时分析 ==========");
        System.out.println("总站点数: " + allStcds.size());
        System.out.println("有雨站点: " + stationWithRain + " | 无雨站点: " + stationNoRain);
        System.out.println("雨量记录数: " + rainRecordCount);
        System.out.println("时间网格点数(gridSize): " + gridSize + " (范围: " + stime + " ~ " + etime + ")");
        System.out.println("---");
        System.out.println("阶段1-数据分组(rainMap构建): " + (t1 - t0) + " ms");
        System.out.println("阶段2-逐站前缀和+滑动窗口:  " + (t2 - t1) + " ms");
        System.out.println("阶段3-汇总Summary:            " + (t3 - t2) + " ms");
        System.out.println("calculateMaxSlidingRain 总耗时: " + (t3 - t0) + " ms");
        System.out.println("=====================================================");

        return result;
    }

    private void setWindowField(MaxRainResultPojo.StationItem item, int w,
                                 MaxRainResultPojo.WindowRainInfo win) {
        switch (w) {
            case 0: item.setMax60min(win); break;
            case 1: item.setMax3h(win); break;
            case 2: item.setMax6h(win); break;
            case 3: item.setMax12h(win); break;
            case 4: item.setMax24h(win); break;
        }
    }

    private MaxRainResultPojo.WindowRainInfo findGlobalMax(List<MaxRainResultPojo.StationItem> items, int windowIndex) {
        MaxRainResultPojo.WindowRainInfo best = null;
        double maxDrp = -1;
        for (MaxRainResultPojo.StationItem item : items) {
            MaxRainResultPojo.WindowRainInfo win = null;
            switch (windowIndex) {
                case 0: win = item.getMax60min(); break;
                case 1: win = item.getMax3h(); break;
                case 2: win = item.getMax6h(); break;
                case 3: win = item.getMax12h(); break;
                case 4: win = item.getMax24h(); break;
            }
            if (win != null && win.getDrp() != null && win.getDrp() > maxDrp) {
                maxDrp = win.getDrp();
                best = win;
            }
        }
        return best;
    }

    /**
     * 快速解析固定格式 "yyyy-MM-dd HH:mm:ss" 为 epoch 毫秒。
     * 用手动字符运算 + LocalDateTime 替代 SimpleDateFormat，
     * 避免每条记录创建 Calendar 对象，性能提升 5-10 倍。
     */
    private static long parseEpochFast(String tm) {
        int year  = (tm.charAt(0)-'0')*1000 + (tm.charAt(1)-'0')*100 + (tm.charAt(2)-'0')*10 + (tm.charAt(3)-'0');
        int month = (tm.charAt(5)-'0')*10 + (tm.charAt(6)-'0');
        int day   = (tm.charAt(8)-'0')*10 + (tm.charAt(9)-'0');
        int hour  = (tm.charAt(11)-'0')*10 + (tm.charAt(12)-'0');
        int minute = (tm.charAt(14)-'0')*10 + (tm.charAt(15)-'0');
        int second = (tm.charAt(17)-'0')*10 + (tm.charAt(18)-'0');
        return LocalDateTime.of(year, month, day, hour, minute, second)
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}
