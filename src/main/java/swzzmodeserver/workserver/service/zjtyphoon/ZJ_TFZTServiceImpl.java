package swzzmodeserver.workserver.service.zjtyphoon;

import org.apache.xmlbeans.GDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swzzmodeserver.tools.CacheHelper;
import swzzmodeserver.workserver.data.swzzflood.ZhuantiData;
import swzzmodeserver.workserver.data.zjtyphoon.*;
import swzzmodeserver.workserver.pojo.swzzflood.ZhuantiPojo;
import swzzmodeserver.workserver.pojo.zjtyphoon.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ZJ_TFZTServiceImpl implements ZJ_TFZTService{
    @Autowired
    private ZJ_TFData zjTfData;
    @Autowired
    private ZJ_XSData zjXsData;
    @Autowired
    private ZJ_XSYBData zjXsybData;
    @Autowired
    private ZJ_TFYBLJData zjTfybljData;
    @Autowired
    private ZJ_TFLSLJData zjTflsljData;
    @Autowired
    private ZJ_TFFLData zjTfflData;
    @Autowired
    private ZhuantiData zhuantiData;

    @Override
    public List<Map<String,Object>> TYPHOON_ZJ_TFZTSel(String key, String stime, String etime, String ddwj, String year, String ZJ_ISCOMPLETED, Integer startIndex, Integer pageSize) {
        List<Map<String,Object>> mapList = new ArrayList<>();
        List<ZJ_TFPojo> userList = new ArrayList<>();
        String strKey = key.replace(" ","");
        String sTIME = stime;
        String eTime = etime;
        if (year != null){
            sTIME = year + "-01-01 00:00:00";
            eTime = year + "-12-31 23:59:59";
        }
        List<String> list = new ArrayList<>();
        if (!"".equals(ZJ_ISCOMPLETED)){
            list = Collections.singletonList(ZJ_ISCOMPLETED);
        }
        userList = zjTfData.selectList(null, strKey, sTIME, eTime,list,null, startIndex, pageSize);
        List<String> aggTFBH = new ArrayList<>();
        for(ZJ_TFPojo obj : userList){
            aggTFBH.add(obj.getZJ_TFBH());
        }
        //查专题
        List<ZhuantiPojo> listZT = zhuantiData.selectList(null);//待完善
        userList.forEach(m->{
            Map<String,Object> map = new HashMap<>();
            map.put("ZJ_ID",m.getZJ_ID());
            map.put("ZJ_TFBH",m.getZJ_TFBH());
            map.put("ZJ_TFM",m.getZJ_TFM());
            map.put("ZJ_TFME",m.getZJ_TFME());
            map.put("ZJ_TFLAND",m.getZJ_TFLAND());
            map.put("ZJ_TFDATE",m.getZJ_TFDATE());
            map.put("ZJ_BEDIT",m.getZJ_BEDIT());
            map.put("ZJ_ISCOMPLETED",m.getZJ_ISCOMPLETED());
            map.put("ZJ_REMARK",m.getZJ_REMARK());
            map.put("ZT_ZJ_YDSD",m.getZT_ZJ_YDSD());
            map.put("ZJ_SEVENRADIUS",m.getZJ_SevenRadius());
            map.put("ZJ_TENRADIUS",m.getZJ_TenRadius());
            map.put("ZJ_GRADE",m.getZJ_Grade());
            List<ZhuantiPojo> listZTTemp = listZT.stream().filter(n-> {
                if (n.getSTATUS() != null && !"".equals(n.getSTATUS())){
                    return Double.parseDouble(n.getSTATUS()) == Double.parseDouble(m.getZJ_TFBH());
                }
                return false;
            }).collect(Collectors.toList());//筛选
            if(listZTTemp.size() > 0){
                map.put("ZT_ID",listZTTemp.get(0).getID());
                map.put("STM",listZTTemp.get(0).getSTM());
                map.put("ETM",listZTTemp.get(0).getETM());
            }
            mapList.add(map);
        });
        return mapList;
    }

    @Override
    public List<Map<String, Object>> GetTFBH_XSSel(String tfbh, String type, String isWX) {
        List<Map<String,Object>> mapList = new ArrayList<>();
        List<ZJ_XSPojo> listData = zjXsData.selectList(tfbh, null, null, null, Collections.singletonList(type), null, null);
        //查专题
        List<ZhuantiPojo> listZT = zhuantiData.selectList(null);//待完善
        if(!"all".equals(isWX)){
            List<Double> aggZTTFBH = new ArrayList<>();
            for (ZhuantiPojo obj : listZT){
                if (null != obj.getSTATUS() && !"".equals(obj.getSTATUS())){
                    aggZTTFBH.add(Double.valueOf(obj.getSTATUS()));
                }
            }
            if ("true".equals(isWX)){
                listData = listData.stream().filter(m->aggZTTFBH.contains(m.getTFBH())).collect(Collectors.toList());
            }else {
                listData = listData.stream().filter(m->!aggZTTFBH.contains(m.getTFBH())).collect(Collectors.toList());
            }
        }
        listData.sort((a,b)-> b.getTFXIANGSIDU().compareTo(a.getTFXIANGSIDU()));
        listData.sort((a,b)-> b.getTFBH().compareTo(a.getTFBH()));
        List<String> aggTFBH = new ArrayList<>();
        for(ZJ_XSPojo obj : listData){
            String tfbhStr=String.format("%.0f", obj.getTFBH());
            aggTFBH.add(tfbhStr);
        }
        List<ZJ_TFPojo> listTF = zjTfData.selectList(null, null, null, null, null, null, null,null);
        List<ZJ_TFPojo> finalListTF=listTF.stream().filter(m -> aggTFBH.contains(m.getZJ_TFBH())).collect(Collectors.toList());

        listData.forEach(m->{
            Map<String,Object> map = new HashMap<>();
            map.put("PTFBH",m.getPTFBH());
            map.put("TFBH",m.getTFBH());
            map.put("TFSTIME",m.getTFSTIME());
            map.put("TFETIME",m.getTFETIME());
            map.put("TFXIANGSIDU", (double) m.getTFXIANGSIDU().intValue());
            map.put("TYPE",m.getTYPE());

            String tfbhStr=String.format("%.0f", m.getTFBH());

            List<ZJ_TFPojo> listTFTemp = finalListTF.stream().filter(n -> n.getZJ_TFBH().equals(tfbhStr)).collect(Collectors.toList());
            if (listTFTemp.size() > 0){
                map.put("TFNAME",listTFTemp.get(0).getZJ_TFM());
            }
            List<ZhuantiPojo> listZTTemp = listZT.stream().filter(n-> n.getSTATUS() == m.getTFBH().toString()).collect(Collectors.toList());//筛选待完善
            if(listZTTemp.size() > 0){
                map.put("ZT_ID",listZTTemp.get(0).getID());
            }
            mapList.add(map);
        });
        mapList.sort((a,b)-> Double.compare((double)b.get("TFXIANGSIDU"),(double)a.get("TFXIANGSIDU")));
        mapList.sort((a,b)-> Double.compare ((double)b.get("TFBH"),(double)a.get("TFBH")));
        return mapList;
    }

    @Override
    public List<Map<String, Object>> GetTFBH_XSYBSel(String tfbh, String ddwj, String isWX,String ZJ_YBSJ,String ZJ_TM) {
        List<Map<String,Object>> mapList = new ArrayList<>();
        List<ZJ_XSYBPojo> listData = zjXsybData.selectList(tfbh, null, null, null, Collections.singletonList(ddwj), null, null);
        //查专题
        List<ZhuantiPojo> listZT = zhuantiData.selectList(null);//待完善
        if(!"all".equals(isWX)){
            List<Double> aggZTTFBH = new ArrayList<>();
            for (ZhuantiPojo obj : listZT){
                if (null != obj.getSTATUS() && !"".equals(obj.getSTATUS())){
                    aggZTTFBH.add(Double.valueOf(obj.getSTATUS()));
                }
            }
            if ("true".equals(isWX)){
                listData = listData.stream().filter(m->aggZTTFBH.contains(m.getTFBH())).collect(Collectors.toList());
            }else {
                listData = listData.stream().filter(m->!aggZTTFBH.contains(m.getTFBH())).collect(Collectors.toList());
            }
        }
        if (ZJ_YBSJ != null){
            listData =  listData.stream().filter(m->m.getZJ_YBSJ().equals(ZJ_YBSJ)).collect(Collectors.toList());
        }else {
            String zj_tfbh = tfbh;
            List<String> tfbhList=Arrays.asList(zj_tfbh.split(","));
            List<ZJ_TFYBLJPojo> yblist = zjTfybljData.selectList(null,tfbhList, ZJ_TM, null, null, null, null, null)
                    .stream().filter(m -> m.getZJ_TFBH().equals(zj_tfbh)).collect(Collectors.toList());
            String zj_ybsj = yblist.stream().map(ZJ_TFYBLJPojo::getZJ_YBSJ).max(String::compareTo).get();
            listData = listData.stream().filter(m->m.getZJ_YBSJ().equals(zj_ybsj)).collect(Collectors.toList());
        }
        if (ZJ_TM != null){
            listData = listData.stream().filter(m->m.getZJ_TM().equals(ZJ_TM)).collect(Collectors.toList());
        }
        listData.sort((a,b)-> b.getTFXIANGSIDU().compareTo(a.getTFXIANGSIDU()));
        listData.sort((a,b)-> b.getTFBH().compareTo(a.getTFBH()));
        List<String> aggTFBH = new ArrayList<>();
        for(ZJ_XSYBPojo obj : listData){
            aggTFBH.add(obj.getTFBH().toString());
        }
        List<ZJ_TFPojo> listTF = zjTfData.selectList(null, null, null, null, null, null, null,null)
                .stream().filter(m -> aggTFBH.contains(m.getZJ_TFBH())).collect(Collectors.toList());
        listData.forEach(m->{
            Map<String,Object> map = new HashMap<>();
            map.put("PTFBH",m.getPTFBH());
            map.put("TFBH",m.getTFBH());
            map.put("TFSTIME",m.getTFSTIME());
            map.put("TFETIME",m.getTFETIME());
            map.put("TFXIANGSIDU", (double) m.getTFXIANGSIDU().intValue());
            List<ZJ_TFPojo> listTFTemp = listTF.stream().filter(n -> n.getZJ_TFBH().equals(m.getTFBH().toString())).collect(Collectors.toList());
            if (listTFTemp.size() > 0){
                map.put("TFNAME",listTFTemp.get(0).getZJ_TFM());
            }
            List<ZhuantiPojo> listZTTemp = listZT.stream().filter(n-> Double.parseDouble(n.getSTATUS()) == m.getTFBH()).collect(Collectors.toList());//筛选待完善
            if(listZTTemp.size() > 0){
                map.put("ZT_ID",listZTTemp.get(0).getID());
            }
            mapList.add(map);
        });
        mapList.sort((a,b)-> Double.compare((double)b.get("TFXIANGSIDU"),(double)a.get("TFXIANGSIDU")));
        mapList.sort((a,b)-> Double.compare ((double)b.get("TFBH"),(double)a.get("TFBH")));
        return mapList;
    }

    @Override
    public List<Map<String,Object>> GetTyphoonList(List<String> tfbh, String isYB) {
        List<Map<String,Object>> mapList = new ArrayList<>();
        List<ZJ_TFPojo> listUser = new ArrayList<>();
        if (tfbh.size() > 0){
            listUser = zjTfData.selectList(null, null, null, null, null, tfbh, null, null);
        }
        List<Map<String, Object>> listPoints = GetTfLSPath(tfbh);
        listUser.forEach(m->{
            Map<String,Object> map = new HashMap<>();
            map.put("tfbh",m.getZJ_TFBH());
            map.put("name",m.getZJ_TFM());
            List<Map<String, Object>> listPointsTemp = listPoints.stream().filter(s -> m.getZJ_TFBH().equals(s.get("ID"))).collect(Collectors.toList());
            map.put("points",listPointsTemp);
            String sj = "";
            if (listPointsTemp.size() > 0){
               sj = listPointsTemp.get(0).get("RQSJ2").toString();
            }

            if (null != isYB && Boolean.getBoolean(isYB)){
                List<Map<String, Object>> forecast = GetTFLSLJForYBLJToJson(m.getZJ_TFBH(), sj);
                map.put("forecast",forecast);
            }else {
                if (m.getZJ_ISCOMPLETED() == 1){
                    List<Map<String, Object>> forecast = GetTFLSLJForYBLJToJson(m.getZJ_TFBH(),sj);
                    map.put("forecast",forecast);
                }
            }
            mapList.add(map);
        });
        return mapList;
    }

    @Override
    public List<Map<String, Object>> GetTyphoonListYB(String tfbh, String rqsj2, String tfybdw) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        String sj= "";
        if (null != tfbh && !"".equals(tfbh)){
            if (null != rqsj2 && !"".equals(rqsj2)){
                sj = rqsj2;
                List<ZJ_TFLSLJPojo> listSJ = zjTflsljData.selectList(null, null, rqsj2, rqsj2, Collections.singletonList(tfbh), null, null);
                List<String> ZJTM = Arrays.asList("中国,美国,日本,中国香港,中国台湾,韩国".split(","));
                if (!"".equals(tfybdw)){
                    ZJTM = Arrays.asList(tfybdw.split(","));
                }
                List<String> tfbhList=Arrays.asList(tfbh.split(","));
                List<ZJ_TFYBLJPojo> list = zjTfybljData.selectList(null,tfbhList, null, null, null, ZJTM, null, null)
                        .stream().filter(m -> m.getZJ_TFBH().equals(tfbh) && m.getZJ_RQSJ().compareTo(rqsj2) > 0).sorted(Comparator.comparing(ZJ_TFYBLJPojo::getZJ_RQSJ)).collect(Collectors.toList());
                List<ZJ_TFYBLJPojo> arrList = new ArrayList<>();
                Map<String, List<ZJ_TFYBLJPojo>> listMap = list.stream().collect(Collectors.groupingBy(ZJ_TFYBLJPojo::getZJ_TM));
                for (String key : listMap.keySet()){
                    Optional<ZJ_TFYBLJPojo> max = listMap.get(key).stream().max(Comparator.comparing(ZJ_TFYBLJPojo::getZJ_YBSJ));
                    max.ifPresent(arrList::add);
                }

                List<ZJ_TFYBLJPojo> listYBJL = zjTfybljData.selectList(null, tfbhList,null, rqsj2, rqsj2, null, null, null)
                        .stream().filter(m -> m.getZJ_TFBH().equals(tfbh) && m.getZJ_RQSJ().compareTo(rqsj2) > 0)
                        .sorted(Comparator.comparing(ZJ_TFYBLJPojo::getZJ_RQSJ)).collect(Collectors.toList());
                arrList.forEach(m->{
                    String ftm = m.getZJ_TM();
                    if (!"".equals(ftm)){
                        List<ZJ_TFYBLJPojo> listYBJLTemp = listYBJL.stream().filter(n -> n.getZJ_TM().equals(ftm)).collect(Collectors.toList());
                        List<Map<String, Object>> dt_Value = GetTLYBLJ(listYBJLTemp, ftm, listSJ);
                        if (dt_Value.size() > 0){
                            Map<String,Object> map = new HashMap<>();
                            map.put("FTM",ftm);
                            map.put("COLOR",GetColorToTFM(ftm));
                            map.put("point",dt_Value);
                            mapList.add(map);
                        }
                    }
                });
            }
        }
        
        return mapList;
    }

    private List<Map<String,Object>> GetTFLSLJForYBLJToJson(String tfbh,String ybsj){
        List<Map<String,Object>> mapList = new ArrayList<>();
        if (!"".equals(tfbh)){
            List<String> ZJTM  = Arrays.asList("中国,美国,日本,中国香港,中国台湾,韩国".split(","));
            List<String> tfbhList=Arrays.asList(tfbh.split(","));
            List<ZJ_TFYBLJPojo> yblist = zjTfybljData.selectList(null, tfbhList,null, null, null, null, null, null);
            List<ZJ_TFYBLJPojo> list = yblist.stream().filter(m -> m.getZJ_TFBH().equals(tfbh) && ZJTM.contains(m.getZJ_TM()))
                    .collect(Collectors.toList());
            List<ZJ_TFYBLJPojo> arrList = new ArrayList<>();
            Map<String, List<ZJ_TFYBLJPojo>> listMap = list.stream().collect(Collectors.groupingBy(ZJ_TFYBLJPojo::getZJ_TM));
            for (String key : listMap.keySet()){
                Optional<ZJ_TFYBLJPojo> max = listMap.get(key).stream().max(Comparator.comparing(ZJ_TFYBLJPojo::getZJ_YBSJ));
                max.ifPresent(arrList::add);
            }
            String sj = "";
            List<ZJ_TFLSLJPojo> listSJ = zjTflsljData.selectList(null,null,null,null, Collections.singletonList(tfbh),null,null);
            listSJ.sort((a,b)-> b.getZJ_RQSJ().compareTo(a.getZJ_RQSJ()));
            List<ZJ_TFYBLJPojo> listYBJL = yblist.stream().filter(m -> m.getZJ_TFBH().equals(tfbh)).collect(Collectors.toList());
            list.sort(Comparator.comparing(ZJ_TFYBLJPojo::getZJ_RQSJ));
            arrList.forEach(m->{
                String ftm = m.getZJ_TM();
                if (!"".equals(ftm)){
                    List<ZJ_TFLSLJPojo> listSJTemp = listSJ.stream().filter(n -> n.getZJ_RQSJ().equals(m.getZJ_YBSJ())).collect(Collectors.toList());
                    List<ZJ_TFYBLJPojo> listYBJLTemp = listYBJL.stream().filter(n -> n.getZJ_TM().equals(ftm) && n.getZJ_YBSJ().equals(m.getZJ_YBSJ())).collect(Collectors.toList());
                    List<Map<String,Object>> dt_Value = GetTLYBLJ(listYBJLTemp,ftm,listSJTemp);
                    if (dt_Value.size() > 0){
                        Map<String,Object> map = new HashMap<>();
                        map.put("FTM",ftm);
                        map.put("COLOR",GetColorToTFM(ftm));
                        map.put("point",dt_Value);
                        mapList.add(map);
                    }
                }
            });
        }
        return mapList;
    }

    private String GetColorToTFM(String ftm) {
        String str = "";
        switch (ftm){
            case "香港":
                str = "FEBD00";
                break;
            case "中国香港":
                str = "FEBD00";
                break;
            case "美国":
                str = "04FAF7";
                break;
            case "台湾":
                str = "FF00FE";
                break;
            case "中国台湾":
                str = "FF00FE";
                break;
            case "日本":
                str = "24BC00";
                break;
            case "韩国":
                str = "72A3AD";
                break;
            default:
                str = "FF3C4E";
                break;
        }
        return str;
    }

    private List<Map<String, Object>> GetTLYBLJ(List<ZJ_TFYBLJPojo> listYBJLTemp, String ftm, List<ZJ_TFLSLJPojo> listSJTemp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat dateFormatT = new SimpleDateFormat("MM月dd日HH时");
        List<Map<String,Object>> mapList = new ArrayList<>();
        List<ZJ_TFPojo> list = zjTfData.selectList(null, null, null, null, null, null, null, null);
        if (null != listYBJLTemp && listYBJLTemp.size() > 0){
            for (ZJ_TFLSLJPojo obj : listSJTemp){
                String name = list.stream().filter(m->m.getZJ_TFBH().equals(obj.getZJ_TFBH())).collect(Collectors.toList()).get(0).getZJ_TFM();
                Map<String,Object> map = new HashMap<>();
                map.put("ID",obj.getZJ_TFBH());
                map.put("TFBH",obj.getZJ_TFBH());
                map.put("TFNM",name);
                map.put("JD",obj.getZJ_JD());
                map.put("WD",obj.getZJ_WD());
                map.put("COLOR",GetColor(obj.getZJ_ZXFS().toString()));
                try {
                    map.put("RQSJ",dateFormatT.format(dateFormat.parse(obj.getZJ_RQSJ())));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                map.put("FTM",ftm);
                map.put("QY",obj.getZJ_ZXQY());
                map.put("FS",obj.getZJ_ZXFS());
                map.put("FL",GetTFFLByValue(obj.getZJ_ZXFS().toString()));
                map.put("RADIUS7",obj.getZJ_SevenRadius());
                map.put("RADIUS10",obj.getZJ_TenRadius());
                map.put("YDSD",obj.getZJ_YDSD());
                map.put("YDFX",obj.getZJ_YDFX());
                mapList.add(map);
            }
            for (ZJ_TFYBLJPojo ybObj : listYBJLTemp){
                String name = list.stream().filter(m->m.getZJ_TFBH().equals(ybObj.getZJ_TFBH())).collect(Collectors.toList()).get(0).getZJ_TFM();
                Map<String,Object> map = new HashMap<>();
                map.put("ID",ybObj.getZJ_ID());
                map.put("TFBH",ybObj.getZJ_TFBH());
                map.put("TFNM",name);
                map.put("JD",ybObj.getZJ_JD());
                map.put("WD",ybObj.getZJ_WD());
                map.put("COLOR",GetColor(ybObj.getZJ_ZXFS().toString()));
                try {
                    map.put("RQSJ",dateFormatT.format(dateFormat.parse(ybObj.getZJ_RQSJ())));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                map.put("FTM",ftm);
                map.put("QY",ybObj.getZJ_ZXQY());
                map.put("FS",ybObj.getZJ_ZXFS());
                map.put("FL",GetTFFLByValue(ybObj.getZJ_ZXFS().toString()));
                map.put("RADIUS7",ybObj.getZJ_RADIUS7());
                map.put("RADIUS10",ybObj.getZJ_RADIUS10());
                map.put("YDSD",ybObj.getZJ_YDSD());
                map.put("YDFX",ybObj.getZJ_YDFX());
                mapList.add(map);
            }
        }
        return mapList;
    }

    private String GetTFFLByValue(String toString) {
        String result = "0";
        if (!"".equals(toString)){
            double Fmin = Double.parseDouble(toString);
            List<ZJ_TFFLPojo> DT_TFFL=new ArrayList<>();
            // 获取缓存
            Object ZJ_TFFLData = cache.get("ZJ_TFFLData");
            if(ZJ_TFFLData!=null){
                DT_TFFL=(List<ZJ_TFFLPojo>) ZJ_TFFLData;
            }
            else{
                DT_TFFL = zjTfflData.selectList(null, null, null, null, null, null, null);
                // 添加缓存
                cache.put("ZJ_TFFLData", DT_TFFL);
            }
            DT_TFFL=DT_TFFL.stream().filter(m -> m.getZJ_Fmin() <= Fmin && m.getZJ_Fmax() >= Fmin).collect(Collectors.toList());
            if (DT_TFFL.size() > 0){
                result = !"".equals(DT_TFFL.get(0).getZJ_GRADE().toString()) ? DT_TFFL.get(0).getZJ_GRADE().toString() : "0";
            }
        }
        return result;
    }

    private List<Map<String,Object>> GetTfLSPath(List<String> tfids){
        List<Map<String,Object>> mapList = new ArrayList<>();
        String sysTM = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date().getTime());
        System.out.println("###########查询台风路径之前："+sysTM);
        List<ZJ_TFLSLJPojo> list = zjTflsljData.selectList(null, null, null, null, tfids, null, null);
        sysTM = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date().getTime());
        System.out.println("###########查询台风路径之后："+sysTM);
        list.sort(Comparator.comparing(ZJ_TFLSLJPojo::getZJ_RQSJ));
        list.forEach(m->{
            Map<String,Object> map = new HashMap<>();
            map.put("ID",m.getZJ_TFBH());
            map.put("RQSJ2",m.getZJ_RQSJ());
            map.put("JD",m.getZJ_JD());
            map.put("WD",m.getZJ_WD());
            map.put("QY",m.getZJ_ZXQY());
            map.put("FS",m.getZJ_ZXFS());
            map.put("FL",m.getZJ_ZXFS());
            map.put("TYPE",GetIcon(String.valueOf(m.getZJ_ZXFS())));
            map.put("RADIUS7",m.getZJ_SevenRadius());
            map.put("RADIUS10",m.getZJ_TenRadius());
            map.put("ZJ_RADIUS7",m.getZJ_Radius7());
            map.put("ZJ_RADIUS10",m.getZJ_Radius10());
            map.put("ZJ_RADIUS12",m.getZJ_Radius12());
            map.put("MOVESD",m.getZJ_YDSD());
            map.put("MOVEFX",m.getZJ_YDFX());
            map.put("COLOR",GetColor(String.valueOf(m.getZJ_ZXFS())));
            mapList.add(map);
        });


        sysTM = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date().getTime());
        System.out.println("###########查询台风路径循环之后："+sysTM);

        return mapList;

    }
    CacheHelper<String, Object> cache = new CacheHelper<>();
    private String GetColor(String zj_zxfs) {
        String result = "";
        if (null != zj_zxfs && !"".equals(zj_zxfs)){
            double dou = Double.parseDouble(zj_zxfs);
            List<ZJ_TFFLPojo> DT_TFFL=new ArrayList<>();
            // 获取缓存
            Object ZJ_TFFLData = cache.get("ZJ_TFFLData");
            if(ZJ_TFFLData!=null){
                DT_TFFL=(List<ZJ_TFFLPojo>) ZJ_TFFLData;
            }
            else{
                DT_TFFL = zjTfflData.selectList(null, null, null, null, null, null, null);
                // 添加缓存
                cache.put("ZJ_TFFLData", DT_TFFL);
            }
            DT_TFFL=DT_TFFL.stream().filter(m -> m.getZJ_Fmin() <= dou && m.getZJ_Fmax() >= dou).collect(Collectors.toList());
            if (DT_TFFL.size() > 0){
                result = DT_TFFL.get(0).getZJ_COLOR();
            }
        }
        return result;
    }

    private String GetIcon(String zj_zxfs) {
        String result = "";
        switch (zj_zxfs){
            case "0062FE":
                result = "热带风暴";
                break;
            case "FDFA00":
                result = "强热带风暴";
                break;
            case "FDAC03":
                result = "台风";
                break;
            case "F072F6":
                result = "强台风";
                break;
            case "FD0002":
                result = "超强台风";
                break;
            default:
                result = "热带低压";
                break;
        }
        return result;
    }
}
