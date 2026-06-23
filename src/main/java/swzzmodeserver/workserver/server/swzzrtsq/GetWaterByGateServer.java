package swzzmodeserver.workserver.server.swzzrtsq;

import swzzmodeserver.workserver.data.swzzrtsq.ST_STBPRP_B_QUData;
import swzzmodeserver.workserver.data.swzzrtsq.ST_STBPRP_B_TREEData;
import swzzmodeserver.workserver.pojo.swzzrtsq.GetWaterViewNewPojo;
import swzzmodeserver.workserver.pojo.swzzrtsq.ST_STBPRP_B_QUPojo;
import swzzmodeserver.workserver.pojo.swzzrtsq.ST_STBPRP_B_TREEPojo;
import swzzmodeserver.tools.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GetWaterByGateServer {

    @Autowired
    private ST_STBPRP_B_TREEData treeData;
    @Autowired
    private ST_STBPRP_B_QUData quData;
    @Autowired
    private GetWaterViewNewServer server;

    //计算一段时间内，开闸时长
    public List<Map<String,Object>> selectListByGate(String type, String pid, String stime, String etime, Double z){
        List<Map<String,Object>> mlist = new ArrayList<>();

        ST_STBPRP_B_TREEPojo model = new ST_STBPRP_B_TREEPojo();
        model.setPID(pid);

        Double zMax = z,zMin = z * -1;
        Long sumHour = Long.valueOf("0");
        List<ST_STBPRP_B_TREEPojo> quList = treeData.selectList(model);
        for(ST_STBPRP_B_TREEPojo xzpojo : quList) {
            Map<String,Object> map = new HashMap<>();

            //查询站点
            List<ST_STBPRP_B_QUPojo> quPojos = quData.selectList(null, null, null, xzpojo.getID(), null);
            List<String> stcds = new ArrayList<>();
            if (quPojos.size() > 0) {
                for (ST_STBPRP_B_QUPojo quPojo : quPojos) {
                    stcds.add(quPojo.getSTCD());
                }
            }
            map.put("title",xzpojo.getTITLE());
            if(stcds.size() > 0) {
                List<GetWaterViewNewPojo> list = new ArrayList<>();
                List<GetWaterViewNewPojo> listN = new ArrayList<>();
                String stm = DateUtil.pastTM(etime,"yyyy-MM-dd")+" 00:00:00";
                String etm = DateUtil.pastTM(etime,"yyyy-MM-dd")+" 23:59:59";
                if("Minute".equals(type)){
                    list = server.selectListByHisIsTime(stcds, stime, etime,null);
                    listN = server.selectListByHisIsTime(stcds, stm, etm,null);
                }else if("HOUR".equals(type)){
                    list = server.selectListByHisIsHouse(stcds, stime, etime,null);
                    listN = server.selectListByHisIsHouse(stcds, stm, etm,null);
                }else if("DAY".equals(type)){
                    list = server.selectListByHisIsDay(stcds, stime, etime,null);
                    listN = server.selectListByHisIsDay(stcds, stm, etm,null);
                }
                List<Map<String,Object>> mlistWater = new ArrayList<>();

                List<GetWaterViewNewPojo> waterList = new ArrayList<>();
                if (null != list && list.size() > 0) {
                    List<List<GetWaterViewNewPojo>> waterGroupList = new ArrayList<>();

                    list.sort(Comparator.comparing(GetWaterViewNewPojo::getTM));
                    list.stream().collect(Collectors.groupingBy(GetWaterViewNewPojo::getTM, LinkedHashMap::new,Collectors.toList())).forEach((tm, fooListByTm) ->{
                        waterGroupList.add(fooListByTm);
                    });
//                    System.out.println(" waterGroupList = " + JSON.toJSONString(waterGroupList));
                    if(null != waterGroupList && waterGroupList.size() > 0){
                        final boolean[] flag = {false};
                        Map<String,Object> mapNew = new HashMap<>();
                        waterGroupList.forEach((mWaterList) ->{
                            GetWaterViewNewPojo waterModel = new GetWaterViewNewPojo();
                            if(mWaterList.size() > 0){
                                String tm = mWaterList.get(0).getTM();
                                String upz = mWaterList.get(0).getUPZ();
                                String dwz = "";
//                                mapNew.put("tm",tm);
                                if(mWaterList.size() > 1) {
                                    dwz = mWaterList.get(1).getUPZ();
                                }
                                if("" != upz && "" != dwz){
                                    waterModel.setSTCD(mWaterList.get(0).getSTCD());
                                    if(mWaterList.get(0).getSTNM().indexOf("外闸") > -1){
                                        waterModel.setUPZ(upz);
                                        waterModel.setDWZ(dwz);
                                    }else{
                                        waterModel.setUPZ(dwz);
                                        waterModel.setDWZ(upz);
                                    }

                                    waterModel.setTM(tm);
                                    waterList.add(waterModel);
                                    Double cz = Double.parseDouble(String.format("%.2f",(Double.valueOf(upz) - Double.valueOf(dwz))));
//                                    System.out.println(mWaterList.get(0).getSTNM()+" = "+tm +" cz = " + cz);
                                    if((cz <= zMax && cz >= zMin) && !flag[0]){
                                        //开闸
                                        mapNew.put("stm",tm);
                                        mapNew.put("supz", upz);
                                        mapNew.put("sdwz", dwz);
                                        mapNew.put("scz", Double.parseDouble(String.format("%.2f",cz)));
                                        flag[0] = true;
                                    }else if((cz >= zMax || cz <= zMin) && flag[0]){
                                        //关闸
                                        mapNew.put("etm",tm);
                                        mapNew.put("eupz", upz);
                                        mapNew.put("edwz", dwz);
                                        mapNew.put("ecz", Double.parseDouble(String.format("%.2f",cz)));
                                        flag[0] = false;
                                        mapNew.put("tmcz", DateUtil.dateDiff(mapNew.get("stm").toString(),mapNew.get("etm").toString(),"yyyy-MM-dd HH:mm:ss","h"));

                                        Map<String,Object> mapNewGate = new HashMap<>();
                                        mapNewGate.put("stm",mapNew.get("stm"));
                                        mapNewGate.put("supz", mapNew.get("supz"));
                                        mapNewGate.put("sdwz", mapNew.get("sdwz"));
                                        mapNewGate.put("scz", mapNew.get("scz"));
                                        mapNewGate.put("etm",mapNew.get("etm"));
                                        mapNewGate.put("eupz", mapNew.get("eupz"));
                                        mapNewGate.put("edwz", mapNew.get("edwz"));
                                        mapNewGate.put("ecz", mapNew.get("ecz"));
                                        mapNewGate.put("tmcz", mapNew.get("tmcz"));
                                        mlistWater.add(mapNewGate);
                                    }
                                }
                            }

                        });

                        //计算：至结束时间未关闸，以最后时间计算开闸时长
                        if(flag[0]){
                            List<GetWaterViewNewPojo> mWaterList = waterGroupList.get(waterGroupList.size() -1);
                            if(mWaterList.size() > 0) {
                                String tm = mWaterList.get(0).getTM();
                                String upz = mWaterList.get(0).getUPZ();
                                String dwz = "";
                                if(mWaterList.size() > 1) {
                                    dwz = mWaterList.get(1).getUPZ();
                                }
                                Double cz = 0.0;
                                if("" != upz && "" != dwz) {
                                    cz = Double.parseDouble(String.format("%.2f", (Double.valueOf(upz) - Double.valueOf(dwz))));
                                }
                                //关闸
                                mapNew.put("etm", tm);
                                mapNew.put("eupz", upz);
                                mapNew.put("edwz", dwz);
                                mapNew.put("ecz", Double.parseDouble(String.format("%.2f", cz)));
                                flag[0] = false;
                                mapNew.put("tmcz", DateUtil.dateDiff(mapNew.get("stm").toString(), mapNew.get("etm").toString(), "yyyy-MM-dd HH:mm:ss", "h"));

                                Map<String, Object> mapNewGate = new HashMap<>();
                                mapNewGate.put("stm", mapNew.get("stm"));
                                mapNewGate.put("supz", mapNew.get("supz"));
                                mapNewGate.put("sdwz", mapNew.get("sdwz"));
                                mapNewGate.put("scz", mapNew.get("scz"));
                                mapNewGate.put("etm", mapNew.get("etm"));
                                mapNewGate.put("eupz", mapNew.get("eupz"));
                                mapNewGate.put("edwz", mapNew.get("edwz"));
                                mapNewGate.put("ecz", mapNew.get("ecz"));
                                mapNewGate.put("tmcz", mapNew.get("tmcz"));
                                mlistWater.add(mapNewGate);
                            }
                        }
                    }
                }

                //开闸时长（小时）
                sumHour = mlistWater.stream().mapToLong(s -> Long.valueOf(s.get("tmcz").toString())).sum();
                map.put("sumHour",sumHour);
                map.put("list",mlistWater);
                map.put("waterList", waterList);

                List<GetWaterViewNewPojo> waterListN = new ArrayList<>();
                if (null != listN && listN.size() > 0) {
                    List<List<GetWaterViewNewPojo>> waterGroupList = new ArrayList<>();
                    listN.sort(Comparator.comparing(GetWaterViewNewPojo::getTM));
                    listN.stream().collect(Collectors.groupingBy(GetWaterViewNewPojo::getTM, LinkedHashMap::new,Collectors.toList())).forEach((tm, fooListByTm) ->{
                        waterGroupList.add(fooListByTm);
                    });

                    if(null != waterGroupList && waterGroupList.size() > 0){
                        final boolean[] flag = {false};
                        Map<String,Object> mapNew = new HashMap<>();
                        waterGroupList.forEach((mWaterList) ->{
                            GetWaterViewNewPojo waterModel = new GetWaterViewNewPojo();
                            if(mWaterList.size() > 0){
                                String tm = mWaterList.get(0).getTM();
                                String upz = mWaterList.get(0).getUPZ();
                                String dwz = "";
//                                mapNew.put("tm",tm);
                                if(mWaterList.size() > 1) {
                                    dwz = mWaterList.get(1).getUPZ();
                                }
                                if("" != upz && "" != dwz){
                                    waterModel.setSTCD(mWaterList.get(0).getSTCD());
                                    if(mWaterList.get(0).getSTNM().indexOf("外闸") > -1){
                                        waterModel.setUPZ(upz);
                                        waterModel.setDWZ(dwz);
                                    }else{
                                        waterModel.setUPZ(dwz);
                                        waterModel.setDWZ(upz);
                                    }

                                    waterModel.setTM(tm);
                                    waterListN.add(waterModel);
                                }
                            }

                        });
                    }
                }
                map.put("waterListN", waterListN);
            }

            mlist.add(map);
        }

        return mlist;
    }


    //计算当前开关闸情况（工程最新开关闸状态）
    public List<Map<String,Object>> selectListByGateNew(String type, String pid, String stime, String etime, Double z){
        List<Map<String,Object>> mlist = new ArrayList<>();

        ST_STBPRP_B_TREEPojo model = new ST_STBPRP_B_TREEPojo();
        model.setPID(pid);

        Double zMax = z,zMin = z * -1;
        Long sumHour = Long.valueOf("0");
        List<ST_STBPRP_B_TREEPojo> quList = treeData.selectList(model);
        for(ST_STBPRP_B_TREEPojo xzpojo : quList) {
            Map<String,Object> map = new HashMap<>();

            //查询站点
            List<ST_STBPRP_B_QUPojo> quPojos = quData.selectList(null, null, null, xzpojo.getID(), null);
            List<String> stcds = new ArrayList<>();
            if (quPojos.size() > 0) {
                for (ST_STBPRP_B_QUPojo quPojo : quPojos) {
                    stcds.add(quPojo.getSTCD());
                }
            }
            map.put("title",xzpojo.getTITLE());
            if(stcds.size() > 0) {
                List<GetWaterViewNewPojo> list = new ArrayList<>();
                List<GetWaterViewNewPojo> listN = new ArrayList<>();
                String stm = DateUtil.pastTM(etime,"yyyy-MM-dd")+" 00:00:00";
                String etm = DateUtil.pastTM(etime,"yyyy-MM-dd")+" 23:59:59";
                if("Minute".equals(type)){
                    list = server.selectListByHisIsTime(stcds, stime, etime,null);
                    listN = server.selectListByHisIsTime(stcds, stm, etm,null);
                }else if("HOUR".equals(type)){
                    list = server.selectListByHisIsHouse(stcds, stime, etime,null);
                    listN = server.selectListByHisIsHouse(stcds, stm, etm,null);
                }else if("DAY".equals(type)){
                    list = server.selectListByHisIsDay(stcds, stime, etime,null);
                    listN = server.selectListByHisIsDay(stcds, stm, etm,null);
                }
                List<Map<String,Object>> mlistWater = new ArrayList<>();

                List<GetWaterViewNewPojo> waterList = new ArrayList<>();
                if (null != list && list.size() > 0) {
                    List<List<GetWaterViewNewPojo>> waterGroupList = new ArrayList<>();

                    list.sort(Comparator.comparing(GetWaterViewNewPojo::getTM));
                    list.stream().collect(Collectors.groupingBy(GetWaterViewNewPojo::getTM, LinkedHashMap::new,Collectors.toList())).forEach((tm, fooListByTm) ->{
                        waterGroupList.add(fooListByTm);
                    });
//                    System.out.println(" waterGroupList = " + JSON.toJSONString(waterGroupList));
                    if(null != waterGroupList && waterGroupList.size() > 0){
                        final boolean[] flag = {false};
                        Map<String,Object> mapNew = new HashMap<>();
                        waterGroupList.forEach((mWaterList) ->{
                            GetWaterViewNewPojo waterModel = new GetWaterViewNewPojo();
                            if(mWaterList.size() > 0){
                                String tm = mWaterList.get(0).getTM();
                                String upz = mWaterList.get(0).getUPZ();
                                String dwz = "";
//                                mapNew.put("tm",tm);
                                if(mWaterList.size() > 1) {
                                    dwz = mWaterList.get(1).getUPZ();
                                }
                                if("" != upz && "" != dwz){
                                    waterModel.setSTCD(mWaterList.get(0).getSTCD());
                                    if(mWaterList.get(0).getSTNM().indexOf("外闸") > -1){
                                        waterModel.setUPZ(upz);
                                        waterModel.setDWZ(dwz);
                                    }else{
                                        waterModel.setUPZ(dwz);
                                        waterModel.setDWZ(upz);
                                    }

                                    waterModel.setTM(tm);
                                    waterList.add(waterModel);
                                    Double cz = Double.parseDouble(String.format("%.2f",(Double.valueOf(upz) - Double.valueOf(dwz))));
//                                    System.out.println(mWaterList.get(0).getSTNM()+" = "+tm +" cz = " + cz);
                                    if((cz <= zMax && cz >= zMin) && !flag[0]){
                                        //开闸
                                        mapNew.put("stm",tm);
                                        mapNew.put("supz", upz);
                                        mapNew.put("sdwz", dwz);
                                        mapNew.put("scz", Double.parseDouble(String.format("%.2f",cz)));
                                        flag[0] = true;
                                    }else if((cz >= zMax || cz <= zMin) && flag[0]){
                                        //关闸
                                        mapNew.put("etm",tm);
                                        mapNew.put("eupz", upz);
                                        mapNew.put("edwz", dwz);
                                        mapNew.put("ecz", Double.parseDouble(String.format("%.2f",cz)));
                                        flag[0] = false;
                                        mapNew.put("tmcz", DateUtil.dateDiff(mapNew.get("stm").toString(),mapNew.get("etm").toString(),"yyyy-MM-dd HH:mm:ss","h"));

                                        Map<String,Object> mapNewGate = new HashMap<>();
                                        mapNewGate.put("stm",mapNew.get("stm"));
                                        mapNewGate.put("supz", mapNew.get("supz"));
                                        mapNewGate.put("sdwz", mapNew.get("sdwz"));
                                        mapNewGate.put("scz", mapNew.get("scz"));
                                        mapNewGate.put("etm",mapNew.get("etm"));
                                        mapNewGate.put("eupz", mapNew.get("eupz"));
                                        mapNewGate.put("edwz", mapNew.get("edwz"));
                                        mapNewGate.put("ecz", mapNew.get("ecz"));
                                        mapNewGate.put("tmcz", mapNew.get("tmcz"));
                                        mlistWater.add(mapNewGate);
                                    }
                                }
                            }

                        });

                        //计算：至结束时间未关闸，以最后时间计算开闸时长
                        if(flag[0]){
                            List<GetWaterViewNewPojo> mWaterList = waterGroupList.get(waterGroupList.size() -1);
                            if(mWaterList.size() > 0) {
                                String tm = mWaterList.get(0).getTM();
                                String upz = mWaterList.get(0).getUPZ();
                                String dwz = "";
                                if(mWaterList.size() > 1) {
                                    dwz = mWaterList.get(1).getUPZ();
                                }
                                Double cz = 0.0;
                                if("" != upz && "" != dwz) {
                                    cz = Double.parseDouble(String.format("%.2f", (Double.valueOf(upz) - Double.valueOf(dwz))));
                                }
                                //关闸
                                mapNew.put("etm", tm);
                                mapNew.put("eupz", upz);
                                mapNew.put("edwz", dwz);
                                mapNew.put("ecz", Double.parseDouble(String.format("%.2f", cz)));
                                flag[0] = false;
                                mapNew.put("tmcz", DateUtil.dateDiff(mapNew.get("stm").toString(), mapNew.get("etm").toString(), "yyyy-MM-dd HH:mm:ss", "h"));

                                Map<String, Object> mapNewGate = new HashMap<>();
                                mapNewGate.put("stm", mapNew.get("stm"));
                                mapNewGate.put("supz", mapNew.get("supz"));
                                mapNewGate.put("sdwz", mapNew.get("sdwz"));
                                mapNewGate.put("scz", mapNew.get("scz"));
                                mapNewGate.put("etm", mapNew.get("etm"));
                                mapNewGate.put("eupz", mapNew.get("eupz"));
                                mapNewGate.put("edwz", mapNew.get("edwz"));
                                mapNewGate.put("ecz", mapNew.get("ecz"));
                                mapNewGate.put("tmcz", mapNew.get("tmcz"));
                                mlistWater.add(mapNewGate);
                            }
                        }
                    }
                }

                //开闸时长（小时）
                sumHour = mlistWater.stream().mapToLong(s -> Long.valueOf(s.get("tmcz").toString())).sum();
                map.put("sumHour",sumHour);
                map.put("list",mlistWater);
                map.put("waterList", waterList);

                List<GetWaterViewNewPojo> waterListN = new ArrayList<>();
                if (null != listN && listN.size() > 0) {
                    List<List<GetWaterViewNewPojo>> waterGroupList = new ArrayList<>();
                    listN.sort(Comparator.comparing(GetWaterViewNewPojo::getTM));
                    listN.stream().collect(Collectors.groupingBy(GetWaterViewNewPojo::getTM, LinkedHashMap::new,Collectors.toList())).forEach((tm, fooListByTm) ->{
                        waterGroupList.add(fooListByTm);
                    });

                    if(null != waterGroupList && waterGroupList.size() > 0){
                        final boolean[] flag = {false};
                        Map<String,Object> mapNew = new HashMap<>();
                        waterGroupList.forEach((mWaterList) ->{
                            GetWaterViewNewPojo waterModel = new GetWaterViewNewPojo();
                            if(mWaterList.size() > 0){
                                String tm = mWaterList.get(0).getTM();
                                String upz = mWaterList.get(0).getUPZ();
                                String dwz = "";
//                                mapNew.put("tm",tm);
                                if(mWaterList.size() > 1) {
                                    dwz = mWaterList.get(1).getUPZ();
                                }
                                if("" != upz && "" != dwz){
                                    waterModel.setSTCD(mWaterList.get(0).getSTCD());
                                    if(mWaterList.get(0).getSTNM().indexOf("外闸") > -1){
                                        waterModel.setUPZ(upz);
                                        waterModel.setDWZ(dwz);
                                    }else{
                                        waterModel.setUPZ(dwz);
                                        waterModel.setDWZ(upz);
                                    }

                                    waterModel.setTM(tm);
                                    waterListN.add(waterModel);
                                }
                            }

                        });
                    }
                }
                map.put("waterListN", waterListN);
            }

            mlist.add(map);
        }

        return mlist;
    }


}
