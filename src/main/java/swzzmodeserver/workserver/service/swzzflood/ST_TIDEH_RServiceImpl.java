package swzzmodeserver.workserver.service.swzzflood;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import swzzmodeserver.tools.ComputeHL;
import swzzmodeserver.tools.DateUtil;
import swzzmodeserver.tools.convertGrb2ToNcUtil;
import swzzmodeserver.workserver.data.swzzflood.TB_TIDE_MEASUREDData;
import swzzmodeserver.workserver.data.swzzflood.ST_TIDEHHL_RData;
import swzzmodeserver.workserver.data.swzzflood.ST_TIDE_RData;
import swzzmodeserver.workserver.data.swzzmode.ST_HIGHLOWTIDE_RData;
import swzzmodeserver.workserver.data.swzzmode.ST_STBPRP_B_STCDData;
import swzzmodeserver.workserver.data.swzzqxsj.St_windyweater_rData;
import swzzmodeserver.workserver.data.swzzwater.BP_DATAData;
import swzzmodeserver.workserver.pojo.swzzflood.TB_TIDE_MEASUREDPojo;
import swzzmodeserver.workserver.pojo.swzzflood.ST_TIDEHHL_RPojo;
import swzzmodeserver.workserver.pojo.swzzflood.ST_TIDEH_RPojo;
import swzzmodeserver.workserver.pojo.swzzflood.ST_TIDE_RPojo;
import swzzmodeserver.workserver.pojo.swzzmode.ST_HIGHLOWTIDE_RPojo;
import swzzmodeserver.workserver.pojo.swzzmode.ST_STBPRP_B_STCDPojo;
import swzzmodeserver.workserver.pojo.swzzqxsj.St_windyweater_rPojo;
import swzzmodeserver.workserver.pojo.swzzzjk.ST_TIDEHIGHParam;
import swzzmodeserver.workserver.pojo.swzzflood.ST_WAS_RPojo;
import swzzmodeserver.workserver.service.swzzmode.ES_ZHANDIANDATAServiceImpl;
import swzzmodeserver.workserver.pojo.swzzwater.BP_DATAPojo;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ST_TIDEH_RServiceImpl implements ST_TIDEH_RService{
    @Autowired
    private ST_TIDE_RData stTideRData;
    @Autowired
    private ST_TIDEHHL_RData stTidehhlRData;
    @Autowired
    private St_windyweater_rData stWindyweaterRData;
    @Autowired
    private ST_STBPRP_B_STCDData stStbprpBStcdData;
    @Autowired
    private ST_HIGHLOWTIDE_RData stHighlowtideRData;
    @Autowired
    private TB_TIDE_MEASUREDData tbTideMeasuredData;
    @Autowired
    private ES_ZHANDIANDATAServiceImpl esZhandiandataService;
    @Value(value = "${file.path.templatefilepath}")
    private String templatefilepath;
    @Autowired
    private BP_DATAData bp_dataData;
    @Override
    public List<ST_TIDEHIGHParam> WATER_ST_TIDE_RHTIDE(List<String> stcdList, String stime, String etime, String type) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<ST_TIDE_RPojo> list = stTideRData.selectTideList(stcdList, stime, etime);
        list.sort(Comparator.comparing(ST_TIDE_RPojo::getTM));
        List<ST_TIDEHIGHParam> listData = new ArrayList<>();
        List<ST_TIDEH_RPojo> listR = stTideRData.selectTideHList(stcdList, stime, etime);
        List<ST_TIDEHHL_RPojo> listHHL = stTidehhlRData.selectList(null, null, stime, etime, null, null, null).stream()
                .filter(m -> stcdList.contains(m.getSTCD())).collect(Collectors.toList());
        Set<String> tmSet = new TreeSet<>();
        for (ST_TIDE_RPojo pojo : list){
            tmSet.add(pojo.getTM());
        }
        for(String stcd : stcdList){
            List<ST_TIDE_RPojo> listTemp = list.stream().filter(m -> m.getSTCD().equals(stcd)).collect(Collectors.toList());
            tmSet.forEach(tm->{
                List<ST_TIDE_RPojo> listTempT = listTemp.stream().filter(m -> m.getTM().equals(tm)).collect(Collectors.toList());
                if(listTempT.size() > 0 ){
                    Date time = null;
                    try {
                        time = dateFormat.parse(tm);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Date tStime  = new Date(time.getTime() - 150 * 1000);
                    Date tEtime = new Date(time.getTime() + 150 * 1000);
                    List<ST_TIDEHHL_RPojo> listHHLTemp = listHHL.stream().filter(m -> {
                        try {
                            return dateFormat.parse(m.getTM()).getTime() >= tStime.getTime() && dateFormat.parse(m.getTM()).getTime() <= tEtime.getTime() &&
                                    m.getSTCD().equals(stcd);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        return false;
                    }).collect(Collectors.toList());
                    Double htdz = 0.0;
                    boolean isHTDZ = false;
                    if (listHHLTemp.size() > 0){
                        htdz = Double.valueOf(listHHLTemp.get(0).getTDZ());
                        isHTDZ = true;
                    }else {
                        List<ST_TIDEH_RPojo> listRTemp = new ArrayList<>();
                        if (type != null){
                            String stmh = tm.substring(0,tm.indexOf(":")) + ":00:00";
                            listRTemp = listR.stream().filter(m -> {
                                return m.getTM().equals(stmh) && m.getSTCD().equals(stcd);
                            }).collect(Collectors.toList());
                        }else {
                            listRTemp = listR.stream().filter(m->{
                                return m.getTM().equals(tm) && m.getSTCD().equals(stcd);
                            }).collect(Collectors.toList());
                        }
                        if (listRTemp.size() > 0){
                            htdz = listRTemp.get(0).getTDZ();
                            isHTDZ = true;
                        }
                    }
                    ST_TIDEHIGHParam dto = new ST_TIDEHIGHParam();
                    dto.setSTCD(stcd);
                    dto.setTDZ(String.valueOf(listTempT.get(0).getTDZ()));
                    dto.setTM(tm);
                    if (isHTDZ){
                        dto.setHTDZ(String.valueOf(htdz));
                        dto.setZTDZ(String.valueOf(listTempT.get(0).getTDZ() - htdz));
                    }
                    listData.add(dto);
                }
            });
        }
        return listData;
    }

    @Override
    public List<ST_TIDEHIGHParam> WATER_ST_TIDE_RHTIDEHS(List<String> stcdList, String stime, String etime, String type) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //List<ST_TIDE_RPojo> list = stTideRData.selectTideList(stcdList, stime, etime);
        List<TB_TIDE_MEASUREDPojo> list = tbTideMeasuredData.selectList(stcdList, stime, etime);
        list.sort(Comparator.comparing(TB_TIDE_MEASUREDPojo::getDt_time));
        List<ST_TIDEHIGHParam> listData = new ArrayList<>();
        List<ST_TIDEH_RPojo> listR = stTideRData.selectTideHList(stcdList, stime, etime);
        List<ST_TIDEHHL_RPojo> listHHL = stTidehhlRData.selectList(null, null, stime, etime, null, null, null).stream()
                .filter(m -> stcdList.contains(m.getSTCD())).collect(Collectors.toList());
        Set<String> tmSet = new TreeSet<>();
        for (TB_TIDE_MEASUREDPojo pojo : list){
            tmSet.add(pojo.getDt_time());
        }
        for(String stcd : stcdList){
            List<TB_TIDE_MEASUREDPojo> listTemp = list.stream().filter(m -> m.getSt_stationid().replaceAll(" ","").equals(stcd)).collect(Collectors.toList());
            tmSet.forEach(tm->{
                List<TB_TIDE_MEASUREDPojo> listTempT = listTemp.stream().filter(m -> m.getDt_time().equals(tm)).collect(Collectors.toList());
                if(listTempT.size() > 0 ){
                    Date time = null;
                    try {
                        time = dateFormat.parse(tm);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Date tStime  = new Date(time.getTime() - 150 * 1000);
                    Date tEtime = new Date(time.getTime() + 150 * 1000);
                    List<ST_TIDEHHL_RPojo> listHHLTemp = listHHL.stream().filter(m -> {
                        try {
                            return dateFormat.parse(m.getTM()).getTime() >= tStime.getTime() && dateFormat.parse(m.getTM()).getTime() <= tEtime.getTime() &&
                                    m.getSTCD().equals(stcd);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        return false;
                    }).collect(Collectors.toList());
                    Double htdz = 0.0;
                    boolean isHTDZ = false;
                    if (listHHLTemp.size() > 0){
                        htdz = Double.valueOf(listHHLTemp.get(0).getTDZ());
                        isHTDZ = true;
                    }else {
                        List<ST_TIDEH_RPojo> listRTemp = new ArrayList<>();
                        listRTemp = listR.stream().filter(m->{
                            return m.getTM().equals(tm) && m.getSTCD().equals(stcd);
                        }).collect(Collectors.toList());
                        if(listRTemp.size()==0){//没有数据的取整点的
                            String stmh = tm.substring(0,tm.indexOf(":")) + ":00:00";
                            listRTemp = listR.stream().filter(m -> {
                                return m.getTM().equals(stmh) && m.getSTCD().equals(stcd);
                            }).collect(Collectors.toList());
                        }
//                        if (type != null){
//
//                        }else {
//
//                        }
                        if (listRTemp.size() > 0){
                            htdz = listRTemp.get(0).getTDZ();
                            isHTDZ = true;
                        }
                    }
                    ST_TIDEHIGHParam dto = new ST_TIDEHIGHParam();
                    dto.setSTCD(stcd);
                    dto.setTDZ(String.valueOf(listTempT.get(0).getNm_watervalue()));
                    dto.setTM(tm);
                    if (isHTDZ){
                        dto.setHTDZ(String.valueOf(htdz));
                        dto.setZTDZ(String.valueOf(listTempT.get(0).getNm_watervalue() - htdz));
                    }
                    listData.add(dto);
                }
            });
        }
        return listData;
    }

    @Override
    public List<Map<String,Object>> WaterRainfallFlowBINYB(String stime, String etime, String stcd, String type) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<Map<String,Object>> mapList = new ArrayList<>();
        List<ST_TIDEHHL_RPojo> tidehhlRPojos = stTidehhlRData.selectList(Collections.singletonList(stcd), type, stime, etime, null, null, null);
        tidehhlRPojos.sort(Comparator.comparing(ST_TIDEHHL_RPojo::getTM));
        //查询最新的预报windy预报数据
        String ybStime=null;
        try {
            ybStime=dateFormat.format(new Date(dateFormat.parse(stime).getTime() - 72 * 60 * 60 * 1000));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        List<St_windyweater_rPojo> listWindDataMax = stWindyweaterRData.selectList(stcd, null, ybStime, stime, null, null, null);
        listWindDataMax.sort((a,b)-> b.getYBTM().compareTo(a.getYBTM()));
        List<St_windyweater_rPojo> listWindData = new ArrayList<>();
        if (listWindDataMax.size() > 0){
            String ybtm = listWindDataMax.get(0).getYBTM();
            listWindData = stWindyweaterRData.selectList(stcd, null, ybtm, ybtm, null, null, null);
        }
        List<St_windyweater_rPojo> listWindDataNew = new ArrayList<>();
        listWindData.forEach(m->{
            //System.out.println(m.getTYPE());
            int hours = Integer.parseInt(m.getTYPE());
            String tm = m.getTM();
            for (int i = 0;i < hours;i++){
                St_windyweater_rPojo dto = new St_windyweater_rPojo();
                BeanUtils.copyProperties(m,dto);
                try {
                    dto.setTM(dateFormat.format(new Date(dateFormat.parse(tm).getTime() + i * 60 * 60 * 1000)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                listWindDataNew.add(dto);
            }
        });
        List<St_windyweater_rPojo> listWindDataNewT = listWindDataNew.stream().filter(m -> {
            try {
                return dateFormat.parse(m.getTM()).getTime() >= dateFormat.parse(stime).getTime() &&
                        dateFormat.parse(m.getTM()).getTime() <= dateFormat.parse(etime).getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return false;
        }).collect(Collectors.toList());
        if (tidehhlRPojos.size() > 0){
            List<ST_STBPRP_B_STCDPojo> listB = stStbprpBStcdData.selectList(stcd, null, Collections.singletonList("1"), null, null, null);
            List<ST_WAS_RPojo> listSW = new ArrayList<>();
            if (listB.size() > 0){
                String zstcd = listB.get(0).getZSTCD();
                listSW = esZhandiandataService.getSCSW(stime,etime, Collections.singletonList(zstcd),null);
            }
            String _ybUpz = "0";
            for(int i=0;i<tidehhlRPojos.size();i++){
                String tm = tidehhlRPojos.get(i).getTM();
                Map<String,Object> param = new HashMap<>();
                List<ST_WAS_RPojo> listSWTemp = listSW.stream().filter(m -> m.getTM().equals(tm)).collect(Collectors.toList());
                if(listSWTemp.size() > 0){
                    param.put("SUPZ",listSWTemp.get(0).getUPZ());
                }
                param.put("AZ",tidehhlRPojos.get(i).getTDZ());
                param.put("TM",tm);
                Date date = null;
                try {
                    date = dateFormat.parse(tm);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:00:00");
                //DataFormatter dataFormatter = new DataFormatter();
                String tTMStr = date != null ? df.format(date) : "";
                List<St_windyweater_rPojo> listWindDataNewTemp = listWindDataNewT.stream().filter(m -> m.getTM().equals(tTMStr)).collect(Collectors.toList());
                if (listWindDataNewTemp.size() > 0){
                    param.put("WSPEED",listWindDataNewTemp.get(0).getWIND());
                    param.put("WDIRECTION",listWindDataNewTemp.get(0).getWINDDIR());
                    param.put("PRESSURE",listWindDataNewTemp.get(0).getPRESSURE());
                    double WSPEED = listWindDataNewTemp.get(0).getWIND();
                    double WDIRECTION = listWindDataNewTemp.get(0).getWINDDIR();
                    double PRESSURE = listWindDataNewTemp.get(0).getPRESSURE();
                    if ("低".equals(tidehhlRPojos.get(i).getTDZRCD())){
                        param.put("PATHNAME","低潮位");
                        _ybUpz = GetValue("Wlij.txt","Canshu.txt", WSPEED, WDIRECTION, PRESSURE);
                    }else if ("高".equals(tidehhlRPojos.get(i).getTDZRCD())){
                        param.put("PATHNAME","高潮位");
                        _ybUpz = GetValue("WlijGGC.txt","CanshuGGC.txt", WSPEED, WDIRECTION, PRESSURE);
                    }
                    String interValue = String.format("%.2f",Double.parseDouble(_ybUpz) / 10 - 1);
                    param.put("YBUPZ",tidehhlRPojos.get(i).getTDZ() + Double.parseDouble(interValue));
                    param.put("UPZ1",Double.parseDouble(interValue));
                    param.put("UPZ2",Double.parseDouble(_ybUpz));
                }
                mapList.add(param);
            }
        }
        return mapList;
    }

    @Override
    public List<Map<String, Object>> WaterRainfallFlowBIN(String stime, String etime, String stcd, String type) {
        //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<String> stringList = type.equals("") ? null : Collections.singletonList(type);
        List<Map<String,Object>> mapList = new ArrayList<>();
        List<ST_HIGHLOWTIDE_RPojo> list = stHighlowtideRData.selectList(stcd, stime, etime,stringList, null, null);
        list.sort(Comparator.comparing(ST_HIGHLOWTIDE_RPojo::getTM));
        if (list.size() > 0){
            double dou_drp = 0, dou_otfl = 0, dou_tgtq = 0, dou_upz1 = 0, dou_upz2 = 0, dou_supz = 0, dou_eupz = 0;
            String _ybUpz = "0";
            for (int i=0;i<list.size();i++){
                Map<String,Object> param = new HashMap<>();
                param.put("WSPEED",list.get(i).getWSPEED());
                param.put("WDIRECTION",list.get(i).getWDIRECTION());
                param.put("PRESSURE",list.get(i).getPRESSURE());
                param.put("SUPZ",list.get(i).getZ());
                param.put("AZ",list.get(i).getAZ());
                param.put("TM",list.get(i).getTM());
                double WSPEED = list.get(i).getWSPEED();
                double WDIRECTION = list.get(i).getWDIRECTION();
                double PRESSURE = list.get(i).getPRESSURE();
                if ("低潮位".equals(list.get(i).getTYPE())){
                    _ybUpz = GetValue("Wlij.txt","Canshu.txt",WSPEED,WDIRECTION,PRESSURE);
                }else if ("高潮位".equals(list.get(i).getTYPE())){
                    _ybUpz = GetValue("WlijGGC.txt","CanshuGGC.txt",WSPEED,WDIRECTION,PRESSURE);
                }
                String interValue = String.format("%.2f",Double.parseDouble(_ybUpz) / 10 - 1);
                param.put("YBUPZ",list.get(i).getAZ() + Double.parseDouble(interValue));
                param.put("UPZ1",Double.parseDouble(interValue));
                param.put("UPZ2",Double.parseDouble(_ybUpz));
                mapList.add(param);
            }
        }
        return mapList;
    }

    private String GetValue(String file1,String file2, double wspeed, double wdirection, double pressure) {
        String filePath = templatefilepath + "BP/";
        File Wlij = new File(filePath + file1);
        File Canshu = new File(filePath + file2);
        double[][][] readText = readText(Wlij);
        //System.out.println(readText);
        double[][] readTextMax_min = readTextMax_Min(Canshu, 3);
        double[] tt = {wspeed,wdirection,pressure};
        double jisuan = jisuan(tt, readText, readTextMax_min, 1, 6);
        String val = String.format("%.2f",jisuan);
        return val;
    }
    private String GetValue63301183(double drp, double np, double tpz, double cw, double cm, String stcd) {
        String filePath = templatefilepath + "BP/";
        String file1="Wlijjx.txt";
        String file2="Canshujx.txt";
        File Wlij = new File(filePath + file1);
        File Canshu = new File(filePath + file2);
        double[][][] readText = readText(Wlij);
        //System.out.println(readText);
        double[][] readTextMax_min = readTextMax_Min(Canshu, 4);
        double[] tt = {drp, np, tpz, cw};
        double jisuan = jisuan(tt, readText, readTextMax_min, 1, 8);
        String val = String.format("%.2f",jisuan);
        return val;
    }
    private double[][][] readText(File file){
        double[][][] doubles = new double[10][10][10];
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            int rownum = 0;
            for(int i=0;i<10;i++){
                //rownum++;
                reader.readLine();
                for(int j=0;j<10;j++){
                    String line = reader.readLine();
                    //System.out.println(line);
                    if(line != null && line.length() > 1){
                        String[] split = line.split("\t");
                        for(int k=0;k<10;k++){
                            doubles[i][j][k] = Double.parseDouble(split[k]);
                        }
                    }
                    //reader.readLine();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return doubles;
    }

    private double[][] readTextMax_Min(File file,int canshu){
        double[][] doubles = new double[4][canshu];
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            for(int i=0;i<4;i++){
                String line = reader.readLine();
                if(null != line && line.length() > 0){
                    String[] split = line.split("\t");
                    for(int j=0;j<split.length;j++){
                        if (!split[j].trim().equals("")){
                            doubles[i][j] = Double.parseDouble(split[j]);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return doubles;
    }

    private double jisuan(double[] inum,double[][][] wlij,double[][] canshu,int laynum,int hidenodemax){
        double[][] out = new double[laynum + 1][hidenodemax];
        double[][] net = new double[laynum + 1][hidenodemax];
        double renturnNum = 0;
        for (int i=0;i<inum.length;i++){
            inum[i] = (inum[i] - canshu[1][i]) / (canshu[0][i] - canshu[1][i]);
            for (int j=0;j<hidenodemax;j++){
                net[0][j] += inum[i] * wlij[0][i][j];
            }
        }
        for(int j = 0;j < hidenodemax;j++){
            out[0][j] = 1 / (1 + Math.exp(0 - net[0][j]));
        }
        for(int k=0;k < laynum + 1;k++){
            for (int j=0;j<hidenodemax;j++){
                net[k][j] = 0;
            }
        }
        for(int k=1;k < laynum + 1;k++){
            for(int j=0;j<laynum;j++){
                for (int i=0;i<hidenodemax;i++){
                    net[k][j] += wlij[k][i][j] * out[k - 1][i];
                }
                out[k][j] = 1 / (1 + Math.exp(0 - net[k][j]));
            }
        }
        for (int i=0;i<1;i++){
            renturnNum = out[1][i] * (canshu[2][i] - canshu[3][i]) + canshu[3][i];
        }
        return renturnNum;
    }


    @Override
    public List<BP_DATAPojo> WaterRainfallFlowBINJX(String stime, String etime, String stcd, String type,String strExp) {

        List<BP_DATAPojo> listNew = new ArrayList<>();
        List<BP_DATAPojo> list = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if(type.equals("yb")){//预报
            List<ST_WAS_RPojo> stWasRList = new ArrayList<>();
            //实测水位
            //String swSTCD="63301183,63310400,1460281";//嘉兴，青阳汇，米市渡
            List<String> swSTCD=Arrays.asList("63301183", "63310400", "1460281");

            stWasRList = esZhandiandataService.getSCSW(stime,etime,swSTCD,"");

            List<ST_WAS_RPojo> stFlowRList = new ArrayList<>();
            String llSTCD="11140093";//太浦闸流量
            stFlowRList = esZhandiandataService.getSCSW(stime,etime,Collections.singletonList(llSTCD),"");

            long dataCount= DateUtil.dateDiff(stime,etime,"yyyy-MM-dd HH:mm:ss","h");

            if (dataCount > 0)
            {
                for (int num = 0; num < dataCount; num++)
                {
                    BP_DATAPojo param = new BP_DATAPojo();
                    try {
                        String startTime = dateFormat.format(new Date(dateFormat.parse(stime).getTime() + num * 60 * 60 * 1000));
                        String endTime =dateFormat.format(new Date(dateFormat.parse(startTime).getTime() + 1 * 60 * 60 * 1000));
                        /// <summary>
                        ///时间
                        /// </summary>
                        param.setTM ( endTime);
                        List<ST_WAS_RPojo> _swList=stWasRList.stream().filter(m ->m.getSTCD().equals(stcd)&&m.getTM().equals(startTime)).collect(Collectors.toList());
                        if (_swList.size() > 0)
                        {
                            /// <summary>
                            ///期初水位
                            /// </summary>
                            if (_swList.get(0).getUPZ()!=null)
                            {
                                double upz= Double.valueOf(_swList.get(0).getUPZ())-1.84;
                                param.setSUPZ(String.format("%.2f",upz));
                            }
                        }
                        _swList=stWasRList.stream().filter(m ->m.getSTCD().equals(stcd)&&m.getTM().equals(endTime)).collect(Collectors.toList());
                        if (_swList.size() > 0)
                        {
                            /// <summary>
                            ///期末水位
                            /// </summary>
                            if (_swList.get(0).getUPZ()!=null)
                            {
                                double upz= Double.valueOf(_swList.get(0).getUPZ())-1.84;
                                param.setEUPZ(String.format("%.2f",upz));
                            }
                        }
                        /// <summary>
                        ///累计降雨
                        /// </summary>
                        param.setDRP("0");
                        /// <summary>
                        ///南排水量
                        /// </summary>
                        param.setOTFL("0");

                        /// <summary>
                        ///太浦闸水量
                        /// </summary>
                        List<ST_WAS_RPojo> _flowList = stFlowRList.stream().filter(m ->m.getTM().equals(endTime)).collect(Collectors.toList());
                        double psl=0;
                        if(_flowList.size()>0){
                            psl=Double.valueOf(_flowList.get(0).getUPZ())*300/1000;//小时流量转水量
                        }
                        param.setTGTQ(String.valueOf(psl));

                        //米市渡
                        _swList = stWasRList.stream().filter(m -> m.getTM().equals(startTime)&& m.getSTCD().equals("1460281")).collect(Collectors.toList());
                        if (_swList.size()>0){
                            if(_swList.get(0).getUPZ()!=null){
                                double upz= Double.valueOf(_swList.get(0).getUPZ())-1.84;
                                param.setUPZ1(String.format("%.2f",upz));
                            }
                        }else {
                            param.setUPZ1("0");
                        }

                        //青阳汇
                        _swList = stWasRList.stream().filter(m -> m.getTM().equals(startTime)&& m.getSTCD().equals("63310400")).collect(Collectors.toList());
                        if (_swList.size()>0){
                            if(_swList.get(0).getUPZ()!=null){
                                double upz= Double.valueOf(_swList.get(0).getUPZ())-1.84;
                                param.setUPZ2(String.format("%.2f",upz));
                            }
                        }else {
                            param.setUPZ2("0");
                        }
                        param.setPATHNAME (stcd);
                        list.add(param);
                    } catch (ParseException e) {
                    }
                }
                if (list.size() > 0)
                {
                    double dou_drp = 0, dou_otfl =0, dou_tgtq = 0, dou_upz1 = 0, dou_upz2 = 3.41, dou_supz = 3.56, dou_eupz = 3.56;
                    String _ybUpz = "0";
                    for (int num = 0; num < list.size(); num++)
                    {
                        BP_DATAPojo item=list.get(num);
                        Date time =null;
                        String curTM=item.getTM();
                        try {
                            time=dateFormat.parse(curTM);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        time  = new Date(time.getTime());
                        Date localDate = new Date();
                        if(time.compareTo(localDate)>0){//大于当前时间，表示是预报
                            item.setDRP (String.format("%.2f",dou_drp));
                            item.setOTFL ( String.format("%.2f",dou_otfl));
                            item.setTGTQ ( String.format("%.2f",dou_tgtq));
                            item.setUPZ1 ( String.format("%.2f",dou_upz1));//关联站预报用的实测的
                            item.setUPZ2 ( String.format("%.2f",dou_upz2));//关联站预报用的实测的
                            dou_supz = dou_eupz;
                            dou_eupz =dou_eupz+Double.valueOf(_ybUpz) / 1000 - 0.1;
                        }
                        else{
                            if(item.getDRP()!=null){
                                dou_drp =Double.valueOf(item.getDRP());
                            }
                            if(item.getOTFL()!=null){
                                dou_otfl = Double.valueOf(item.getOTFL());
                            }
                            if(item.getTGTQ()!=null){
                                dou_tgtq = Double.valueOf(item.getTGTQ());
                            }
                            if(item.getUPZ1()!=null){
                                dou_upz1 =Double.valueOf(item.getUPZ1());
                            }
                            if(item.getUPZ2()!=null){
                                dou_upz2 = Double.valueOf(item.getUPZ2());
                            }
                            if(item.getSUPZ()!=null){
                                dou_supz = Double.valueOf(item.getSUPZ());
                            }
                            if (item.getEUPZ()!=null)
                            {
                                dou_eupz = Double.valueOf(item.getEUPZ());
                            }
                            else
                            {
                                if (item.getSUPZ()!=null)
                                {
                                    dou_eupz = Double.valueOf(item.getSUPZ());
                                }
                            }
                        }
                        Double ybVal = 0.0;
                        Double _drp = dou_drp + 10;
                        Double _np = dou_otfl * 10 + 1;
                        Double _tpz = dou_tgtq * 10 + 1;

                        Double _cw = dou_upz1 - dou_upz2 + 2;
                        Double _cm = dou_eupz- dou_supz + 0.1;
                        if (stcd.equals("63301183"))
                        {
                            _ybUpz = GetValue63301183(_drp, _np, _tpz, _cw, _cm, stcd);
                        }
                        else if (stcd.equals("63301200"))
                        {
                            //_ybUpz = GetValue63301200(false, _drp, _np, stcd);
                        }
                        else
                        {
                            //_ybUpz = GetValue63310600(false, _drp, _np, _cw, stcd);
                        }
                        if(!strExp.equals("")){
                            if (num > 0)
                            {
                                dou_supz = Double.valueOf(listNew.get(num - 1).getYBUPZ());
                            }
                        }
                        _ybUpz=String.format("%.2f",Double.valueOf(_ybUpz)/1000-0.1+dou_supz+1.84);
                        item.setUPZ1(String.format("%.2f",dou_upz1+1.84));
                        item.setUPZ2(String.format("%.2f",dou_upz1+1.84));
                        item.setSUPZ(String.format("%.2f",dou_supz+1.84));
                        item.setEUPZ(String.format("%.2f",dou_eupz+1.84));
                        item.setYBUPZ(_ybUpz);
                        listNew.add(item);
                    }
                }
            }
        }
        else{//训练数据
            List<String> stcdList=Arrays.asList(stcd);
            list = bp_dataData.selectList(stime,etime,stcdList);
            if (list.size() > 0)
            {
                double dou_drp = 0, dou_otfl = 0, dou_tgtq = 0, dou_upz1 = 0, dou_upz2 = 0, dou_supz = 0, dou_eupz = 0;
                String _ybUpz = "0";
                for (int num = 0; num < list.size(); num++)
                {
                    BP_DATAPojo item=list.get(num);
                    Date time =null;
                    String curTM=item.getTM();
                    try {
                        time=dateFormat.parse(curTM);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    time  = new Date(time.getTime());
                    Date localDate = new Date();
                    if(time.compareTo(localDate)>0){//大于当前时间，表示是预报
                        item.setDRP(String.format("%.2f",dou_drp));
                        item.setOTFL(String.format("%.2f",dou_otfl));
                        item.setTGTQ(String.format("%.2f",dou_tgtq));
                        item.setUPZ1(String.format("%.2f",dou_upz1));
                        item.setUPZ2(String.format("%.2f",dou_upz2));
                        dou_supz = dou_eupz;
                        dou_eupz =dou_eupz +Double.valueOf( _ybUpz) / 1000 - 0.1;
                    }
                    else
                    {
                        if (item.getDRP() != null)
                        {
                            dou_drp =Double.valueOf( item.getDRP());
                        }
                        if (item.getOTFL() != null)
                        {
                            dou_otfl = Double.valueOf(item.getOTFL());
                        }
                        if (item.getTGTQ() != null)
                        {
                            dou_tgtq = Double.valueOf(item.getTGTQ());
                        }
                        if (item.getUPZ1() != null)
                        {
                            dou_upz1 =Double.valueOf(item.getUPZ1()) - 1.84;
                            //if (dou_upz1 <= 0) {
                            //    dou_upz1 = 0;
                            //}
                            if (dou_upz1 <= 0) {
                                dou_upz1 = Double.valueOf(item.getUPZ1());
                            }
                        }
                        if (item.getUPZ2() != null)
                        {
                            dou_upz2 = Double.valueOf(item.getUPZ2());
                        }
                        if (item.getSUPZ() != null)
                        {
                            dou_supz = Double.valueOf(item.getSUPZ());
                        }
                        if (item.getEUPZ() != null)
                        {
                            dou_eupz = Double.valueOf(item.getEUPZ());
                        }
                        else
                        {
                            if (item.getSUPZ() != null)
                            {
                                dou_eupz = Double.valueOf(item.getSUPZ());
                            }
                        }
                    }
                    double ybVal = 0;
                    double _drp = dou_drp + 10;
                    double _np =  dou_otfl * 10 + 1;
                    double _tpz =  dou_tgtq * 10 + 1;

                    double _cw = dou_upz1-  dou_upz2 + 2;
                    double _cm =  dou_eupz -  dou_supz + 0.1;
                    if (stcd.equals("63301183"))
                    {
                        _ybUpz = GetValue63301183(_drp, _np, _tpz, _cw, _cm, stcd);
                    }
                    else if (stcd.equals( "63301200"))
                    {
                        //_ybUpz = GetValue63301200(false, _drp, _np, stcd);
                    }
                    else
                    {
                        //_ybUpz = GetValue63310600(false, _drp, _np,_cw, stcd);
                    }
                    if(!strExp.equals("")){
                        if (num > 0)
                        {
                            dou_supz = Double.valueOf(listNew.get(num - 1).getYBUPZ());
                        }
                    }
                    //System.out.println("_drp:" + _drp + "，" + "_np:" + _np + "，" + "_tpz:" + _tpz + "，" + "_cw:" + _cw + "，" + "_cm:" + _cm + "，" + "_ybUpz:" + _ybUpz+ "，YBUPZ："+ String.valueOf( Double.valueOf(_ybUpz) / 1000 -0.1 + Double.valueOf(dou_supz)) + "，dou_supz："+ dou_supz);

                    _ybUpz  =String.format("%.2f",Double.valueOf(_ybUpz) / 1000 -0.1 + dou_supz+1.84);

                    item.setUPZ1(String.format("%.2f",dou_upz1+1.84));
                    item.setUPZ2(String.format("%.2f",dou_upz1+1.84));
                    item.setSUPZ(String.format("%.2f",dou_supz+1.84));
                    item.setEUPZ(String.format("%.2f",dou_eupz+1.84));
                    item.setYBUPZ(_ybUpz);
                    listNew.add(item);
                }
            }
        }
        return listNew;
    }

    @Override
    public List<BP_DATAPojo> shftpconvertGrb2ToNc(String inputGrb2File, String outputNcFile) throws IOException, InterruptedException {
        List<BP_DATAPojo> listNew = new ArrayList<>();
        String inputGrb2FileNew=templatefilepath+inputGrb2File;
        String outputNcFileNew=templatefilepath+outputNcFile;
        convertGrb2ToNcUtil.convertGrb2ToNc(inputGrb2FileNew,outputNcFileNew);
        return listNew;
    }

    @Override
    public List<Map<String, Object>> computeHL(String stime, String etime, String stcd) {
        List<ST_TIDEH_RPojo> tideHList = stTideRData.selectTideHList(Collections.singletonList(stcd), stime, etime);
        tideHList.sort(Comparator.comparing(ST_TIDEH_RPojo::getTM));
        String[][] tideHArr = new String[tideHList.size()][];
        for(int i = 0;i < tideHList.size();i++){
            String[] n = {tideHList.get(i).getTM(), String.valueOf(tideHList.get(i).getTDZ())};
            tideHArr[i] = n;
        }
        return ComputeHL.computeHL(tideHArr);
    }
}
