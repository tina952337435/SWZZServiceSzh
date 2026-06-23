package swzzmodeserver.workserver.server.swzzrtsq;

import swzzmodeserver.workserver.data.swzzrtsq.RTSQST_PPTN_RData;
import swzzmodeserver.workserver.pojo.swzzrtsq.ST_PPTN_RPojo;
import swzzmodeserver.tools.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
}
