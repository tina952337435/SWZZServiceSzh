package swzzmodeserver.workserver.controller.swzzmode;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swzzmodeserver.tools.*;
import swzzmodeserver.workserver.data.swzzmode.ES_MODELGUANLIANData;
import swzzmodeserver.workserver.data.swzzmode.ST_FORECAST_FData;
import swzzmodeserver.workserver.pojo.swzzmode.BDMS_PREDICTPojo;
import swzzmodeserver.workserver.pojo.swzzmode.ES_MODELGUANLIANPojo;
import swzzmodeserver.workserver.pojo.swzzmode.ST_FORECAST_FPojo;
import swzzmodeserver.workserver.service.swzzmode.swicApiService;
import  swzzmodeserver.tools.LinearInterpolationUtil;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/SWZZ_MODE_ST_FORECAST_F")
public class ST_FORECAST_FController {
    @Autowired
    private ST_FORECAST_FData data;

    @Autowired
    private swicApiService swicApiService;
    @Autowired
    private ES_MODELGUANLIANData es_modelguanlianData;

    @RequestMapping("/findResult")
    public ResultUtils findResult(@RequestBody ParamField bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String FYMDH = "";
        Integer pageindex = null,pagesize = null;
        List<String> STCDList = new ArrayList<>();
        List<String> UNITNAME = new ArrayList<>();
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo,ParamField.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if(null != bpPojo.getStcd()&&!bpPojo.getStcd().equals("")){
            STCDList = Arrays.asList(bpPojo.getStcd().split(","));
        }
        if(null != bpPojo.getName()){
            UNITNAME = Arrays.asList(bpPojo.getName().split(","));
        }
        if(null != bpPojo.getStartdate()){
            FYMDH = bpPojo.getStartdate();
        }
        List<ST_FORECAST_FPojo> list = data.selectList(STCDList,UNITNAME,FYMDH);
        watch.stop();
        if (list.size() > 0){
            return new ResultUtils<List>(list,"操作成功",true,list.size(),watch.getTime());
        }else {
            return new ResultUtils<List>(null,"操作成功",false,list.size(),watch.getTime());
        }
    }

    @RequestMapping("/findResultFYMDH")
    public  ResultUtils findResultFYMDH(@RequestBody ParamField bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        Integer pageindex = null,pagesize = null;
        List<String> STCDList = new ArrayList<>();
        List<String> UNITNAME = new ArrayList<>();
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo,ParamField.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if(null != bpPojo.getStcd()&&!bpPojo.getStcd().equals("")){
            STCDList = Arrays.asList(bpPojo.getStcd().split(","));
        }
        if(null != bpPojo.getName()){
            UNITNAME = Arrays.asList(bpPojo.getName().split(","));
        }
        String stime="",etime="";
        if(null != bpPojo.getStartdate()){
            stime = bpPojo.getStartdate();
        }
        if(null != bpPojo.getEnddate()){
            etime = bpPojo.getEnddate();
        }
        List<ST_FORECAST_FPojo> list = data.selectListFYMDH(STCDList,UNITNAME,stime,etime);
        watch.stop();
        if (list.size() > 0){
            return new ResultUtils<List>(list,"操作成功",true,list.size(),watch.getTime());
        }else {
            return new ResultUtils<List>(null,"操作成功",false,list.size(),watch.getTime());
        }
    }


    @RequestMapping("/add")
    public ResultUtils<Integer> add(@RequestBody ST_FORECAST_FPojo bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo,ST_FORECAST_FPojo.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        Integer num = data.insertOne(bpPojo);
        watch.stop();
        if(num > 0){
            return new ResultUtils<>(num, "操作成功",true, num,watch.getTime());
        }else {
            return new ResultUtils<>(num, "操作成功",false, num,watch.getTime());
        }
    }
    @RequestMapping("/modify")
    public ResultUtils<Integer> modify(@RequestBody ST_FORECAST_FPojo bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo,ST_FORECAST_FPojo.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        Integer num = data.updateOne(bpPojo);
        watch.stop();
        if(num > 0){
            return new ResultUtils<>(num, "操作成功",true, num,watch.getTime());
        }else {
            return new ResultUtils<>(num, "操作成功",false, num,watch.getTime());
        }
    }
    @RequestMapping("/remove")
    public ResultUtils<Integer> remove(@RequestBody ParamField bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String ID = "";
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo,ParamField.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if(null != bpPojo.getStcd()){
            ID = bpPojo.getStcd();
        }
        Integer num = data.deleteOne(ID,null,null,null,null);
        watch.stop();
        if(num > 0){
            return new ResultUtils<>(num, "操作成功",true, num,watch.getTime());
        }else {
            return new ResultUtils<>(num, "操作成功",false, num,watch.getTime());
        }
    }

    @RequestMapping("/batchResult/{pattem}")
    public ResultUtils batchResult(@PathVariable("pattem") String pattem, @RequestBody List<ST_FORECAST_FPojo> bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String type = "";
        if(CommonUtills.isEmpty(FieldIsValid.getListColumnName(bpPojo, ST_FORECAST_FPojo.class)) || !FieldIsValid.isValid(pattem)){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if(null != pattem){
            type = pattem;
        }
        Integer num = 0;
        int count = 80;
        int number = bpPojo.size() / count;
        if (bpPojo.size() % count != 0){
            number += 1;
        }
        List<ST_FORECAST_FPojo> list = new ArrayList<>();
        for(int i=0;i<number;i++){
            if(i == number - 1){
                list = bpPojo.subList(count * i,bpPojo.size());
            }else {
                list = bpPojo.subList(count * i,count * (i + 1));
            }
            if(type.equals("true") ){
                num += data.updateALL(list);
            }else {
                num += data.insertALL(list);
            }
        }
        watch.stop();
        if(num > 0){
            return new ResultUtils<>(num, "操作成功",true, num,watch.getTime());
        }else {
            return new ResultUtils<>(num, "操作成功",false, num,watch.getTime());
        }
    }

    @RequestMapping("/removemore")
    public ResultUtils<Integer> removemore(@RequestBody ParamField bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        List<String> IDList = new ArrayList<>();
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo,ParamField.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if(null != bpPojo.getStcd()){
            IDList = Arrays.asList(bpPojo.getStcd().split(","));
        }
        Integer num = data.deleteMore(IDList);
        watch.stop();
        if(num > 0){
            return new ResultUtils<>(num, "操作成功",true, num,watch.getTime());
        }else {
            return new ResultUtils<>(num, "操作成功",false, num,watch.getTime());
        }
    }

    //数据入库
    @RequestMapping("/SwicSQYBCG_XY")
    public ResultUtils SwicSQYBCG_XY(@RequestBody ParamField bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        List<ST_FORECAST_FPojo> list = new ArrayList<>();
        int pageSize=10000, pageNumber=1;
        List<Map<String, Object>> dataList=swicApiService.getSwicSQYBCG_XY(pageSize,pageNumber);
        List<String> STCDList = Arrays.asList("TH89999925,TH89999926,TH89999927,TH89999948".split(","));//平望、陈墓、昆山、嘉兴
        // 遍历 data 列表
        for (Map<String, Object> dataItem : dataList) {
            //{"XX":-32188.5441,"YY":-49501.7422,"YY2000":30.7885,"YIJUTIME":"2025-07-22 08:00:00","DATETIME":"2025-07-22 08:00:00","STATIONID":"TH89999942","FABUTIME":"2025-07-22 08:36:12","XX2000":121.1307,"STATIONNAME":"马家宅","DATASOURCE":"太湖局","VALUE":"3.11","TYPE":"水情预报成果"}
            String STCD=dataItem.get("STATIONID").toString();//站码
            String FYMDH =dataItem.get("YIJUTIME").toString();//发布时间
            String YMDH =dataItem.get("DATETIME").toString();//发生时间
            String IYMDH=dataItem.get("FABUTIME").toString();//预报时间
            String UNITNAME=dataItem.get("DATASOURCE").toString();//发布单位
            String PLCD=dataItem.get("STATIONID").toString();//原来的站码
            double Z=(double)dataItem.get("VALUE");
            if(STCDList.contains(STCD)){
                ST_FORECAST_FPojo pojo=new ST_FORECAST_FPojo();
                pojo.setSTCD(STCD);
                pojo.setPLCD(PLCD);
                pojo.setFYMDH(FYMDH);
                pojo.setYMDH(YMDH);
                pojo.setIYMDH(IYMDH);
                pojo.setUNITNAME(UNITNAME);
                pojo.setZ(Z);
                list.add(pojo);
            }
        }
        int rows=0;
        if(list.size()>0){
            rows=data.insertALL(list);
        }
        watch.stop();
        if (list.size() > 0){
            return new ResultUtils<List>(list,"操作成功",true,rows,watch.getTime());
        }else {
            return new ResultUtils<List>(null,"操作成功",false,rows,watch.getTime());
        }
    }

    @RequestMapping("/SwicSQYBCG")
    public ResultUtils SwicSQYBCG(@RequestBody ParamField bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        List<ST_FORECAST_FPojo> list = new ArrayList<>();
        List<ST_FORECAST_FPojo> distinctList=new ArrayList<>();
        int rows=0;
        try{
            List<String> STCDList = Arrays.asList("TH89999925,TH89999926,TH89999927,TH89999948".split(","));//平望、陈墓、昆山、嘉兴
            List<String> STNMList = Arrays.asList("平望,陈墓,昆山,嘉兴".split(","));
            List<ES_MODELGUANLIANPojo>  listGuan=es_modelguanlianData.selectList("","边界站",null,null);
            int pageSize=1, pageNumber=1;
            List<Map<String, Object>> dataList=swicApiService.getSwicSQYBCG_XY(pageSize,pageNumber);
            if(dataList.size()>0){
                String FYMDH =dataList.get(0).get("YIJUTIME").toString();//发布时间
                String UNITNAME=dataList.get(0).get("DATASOURCE").toString();//发布单位
                String IYMDH=dataList.get(0).get("FABUTIME").toString();//预报时间

                List<String> UNITNAMEList=Arrays.asList( UNITNAME.split(","));
                List<ST_FORECAST_FPojo> listFP = data.selectList(null,UNITNAMEList,FYMDH);

                if(listFP.size()==0){
                    String STARTTIME=FYMDH;
                    // 定义时间格式器
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    // 字符串转LocalDateTime
                    LocalDateTime dateTime = LocalDateTime.parse(STARTTIME, formatter);
                    // 增加240小时（10天）
                    LocalDateTime newDateTime = dateTime.plusHours(240);
                    // 转回字符串
                    String ENDTIME=newDateTime.format(formatter);
                    listGuan.forEach(es_modelguanlianPojo->{
                        String STCD=es_modelguanlianPojo.getMKEYID();//站码
                        String PLCD=es_modelguanlianPojo.getSTCD();//原来的站码
                        //dataListGC的格式：{"XX":-32188.5441,"YY":-49501.7422,"YY2000":30.7885,"YIJUTIME":"2025-07-22 08:00:00","DATETIME":"2025-07-22 08:00:00","STATIONID":"TH89999942","FABUTIME":"2025-07-22 08:36:12","XX2000":121.1307,"STATIONNAME":"马家宅","DATASOURCE":"太湖局","VALUE":"3.11","TYPE":"水情预报成果"}
                        List<Map<String, Object>> dataListGC=swicApiService.getSwicSQYBCG(es_modelguanlianPojo.getSTCD(),es_modelguanlianPojo.getFIELD(),STARTTIME,ENDTIME,FYMDH);
                        //1小时一个数据，需要插值为5分钟的
                        List<Map<String, Object>> dataListGC5=LinearInterpolationUtil.interpolateTideData(dataListGC,"DT_TIME","CHAOWEI_VALUE");
                        // 遍历 data 列表
                        for (Map<String, Object> dataItem : dataListGC5) {
                            String YMDH =dataItem.get("DT_TIME").toString();//发生时间
                            double Z=(double)dataItem.get("CHAOWEI_VALUE");
                            ST_FORECAST_FPojo pojo=new ST_FORECAST_FPojo();
                            pojo.setSTCD(STCD);
                            pojo.setPLCD(PLCD);
                            pojo.setFYMDH(FYMDH);
                            pojo.setYMDH(YMDH);
                            pojo.setIYMDH(IYMDH);
                            pojo.setUNITNAME(UNITNAME);
                            pojo.setZ(Z);
                            list.add(pojo);
                        }
                    });
                }else {
                    System.out.println(FYMDH+"发布的预报数据已经存在");
                }
            }

            if(list.size()>0){
                Set<ST_FORECAST_FPojo> uniqueSet = new TreeSet<>(Comparator
                        .comparing(ST_FORECAST_FPojo::getSTCD)
                        .thenComparing(ST_FORECAST_FPojo::getUNITNAME)
                        .thenComparing(ST_FORECAST_FPojo::getPLCD)
                        .thenComparing(ST_FORECAST_FPojo::getFYMDH)
                        .thenComparing(ST_FORECAST_FPojo::getIYMDH)
                        .thenComparing(ST_FORECAST_FPojo::getYMDH));
                uniqueSet.addAll(list);
                distinctList = new ArrayList<>(uniqueSet);


                int count = 1000;
                int number = distinctList.size() / count;
                if (distinctList.size() % count != 0){
                    number += 1;
                }
                List<ST_FORECAST_FPojo> listNew = new ArrayList<>();
                for(int i=0;i<number;i++){
                    if(i == number - 1){
                        listNew = distinctList.subList(count * i,distinctList.size());
                    }else {
                        listNew = distinctList.subList(count * i,count * (i + 1));
                    }
                    rows+=data.insertALL(listNew);
                }
            }
        }catch (Exception e){
            System.out.println("接口报错，错误信息："+e.getMessage());
        }
        watch.stop();
        if (list.size() > 0){
            return new ResultUtils<List>(distinctList,"操作成功",true,rows,watch.getTime());
        }else {
            return new ResultUtils<List>(null,"操作成功",false,rows,watch.getTime());
        }
    }

    //雨型测试
    @RequestMapping("/RainType")
    public ResultUtils RainType(@RequestBody ParamField bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        List<ST_FORECAST_FPojo> list = new ArrayList<>();
        try{
            int[] hoursArray = {12, 24, 36, 48};
            DecimalFormat df = new DecimalFormat("0.00");

            for (int hours : hoursArray) {
                System.out.println("\n历时" + hours + "小时分配表：");
                System.out.println("小时\t单峰1\t单峰2\t单峰3\t双峰5");

                double[][] results = {
                        RainfallDistribution.singlePeakType1(hours, 100),
                        RainfallDistribution.singlePeakType2(hours, 100),
                        RainfallDistribution.singlePeakType3(hours, 100),
                        RainfallDistribution.doublePeakType5(hours, 100)
                };

                for (int h = 0; h < hours; h++) {
                    System.out.print((h+1) + "\t");
                    for (int type = 0; type < 4; type++) {
                        System.out.print(df.format(results[type][h]) + "\t");
                    }
                    System.out.println();
                }
            }
        }catch (Exception e){}
        watch.stop();
        if (list.size() > 0){
            return new ResultUtils<List>(list,"操作成功",true,list.size(),watch.getTime());
        }else {
            return new ResultUtils<List>(null,"操作成功",false,list.size(),watch.getTime());
        }
    }
}
