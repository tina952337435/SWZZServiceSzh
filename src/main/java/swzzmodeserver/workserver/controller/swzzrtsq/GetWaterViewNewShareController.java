package swzzmodeserver.workserver.controller.swzzrtsq;

//import Equivalent.MyWaterDZXUtils;
//import Equivalent.ObjUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import swzzmodeserver.workserver.data.swzzmode.BDMS_PREDICTData;
import swzzmodeserver.workserver.data.swzzmode.DD_SOLUTIONData;
import swzzmodeserver.workserver.data.swzzrtsq.*;
//import swzzmodeserver.workserver.pojo.DENGZHIXIANYQPojo;
import swzzmodeserver.workserver.pojo.swzzmode.BDMS_PREDICTPojo;
import swzzmodeserver.workserver.pojo.swzzmode.DD_SOLUTIONPojo;
import swzzmodeserver.workserver.pojo.swzzrtsq.*;
import swzzmodeserver.workserver.server.swzzrtsq.GetWaterByGateServer;
import swzzmodeserver.workserver.server.swzzrtsq.GetWaterViewNewServer;
import swzzmodeserver.tools.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import swzzmodeserver.workserver.server.swzzrtsq.TongbuServer;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/GetWaterViewNewShare")
public class GetWaterViewNewShareController {
    @Autowired
    private GetWaterViewNewData data;
    @Autowired
    private GetWaterViewNewBXData databx;
    @Autowired
    private ST_STBPRP_B_QUData quData;
    @Autowired
    private GetWaterViewNewServer server;
    @Autowired
    private RTSQST_STBPRP_BData stbprpBData;
    @Autowired
    private RTSQST_RVFCCH_BData rvfcchBData;
    @Autowired
    private RTSQST_PPTN_RData dataRain;
    @Autowired
    private ST_WDPSTAT_RData dataLL;
    @Autowired
    private GetWaterByGateServer dataWaterGateServer;

    @Autowired
    private TongbuServer tongbuServer;

    @Autowired
    private DD_SOLUTIONData ddSolutionData;

    @Autowired
    private BDMS_PREDICTData bdmsPredictData;

    @Autowired
    private RTSQST_STBPRP_B_STCDData stbprpBStcdData;

    @Autowired
    private ST_FLOW_RData sFlow_RData;

    @Autowired
    private ST_WDWV_RData stdwvRData;

    //文件存储路径
    @Value("${file.path.templatefilepath}")
    private String templatefilepath;

    // 获取最新数据接口
    @RequestMapping("/getShiShiShuiWei")
    public ResultUtils getShiShiShuiWei(@RequestBody ColumnName param){
        StopWatch watch = new StopWatch();
        watch.start();
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(param, ParamField.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        Date date = new Date(new Date().getTime() - 3 * 60 * 60 * 1000);
        String dayhour="Minute",stime= DateUtil.dateFormat(date,"yyyy-MM-dd HH:mm:ss"),etime="";
        List<String> listSTCD=new ArrayList<>();
        if(null != param.getPathname()){
            dayhour = param.getPathname();
        }
        if(null != param.getStime()){
            stime = param.getStime();
        }
        if(null != param.getEtime()){
            etime = param.getEtime();
        }
        String mtype="";
        if(null != param.getDatasource()){
            mtype = param.getDatasource();
        }
        String pid="";
        List<ST_STBPRP_B_QUPojo> quList=new ArrayList<>();
        if(null != param.getPid()){
            pid = param.getPid();
            if(pid.equals("shquanshi")){//上海全市的数据
               pid="2026031114184492913-2";
            }
            else if(pid.equals("huashida")){//华师大和伏羲模型
               pid="2026051300001-1-1";
            }
            
            List<String> idList=new ArrayList<>();
            idList.add(pid);
            quList = quData.queryList("", "", null, idList);
            listSTCD = quList.stream().map(s -> s.getSTCD()).distinct().collect(Collectors.toList());
        }
        else {
            watch.stop();
            return new ResultUtils<>(null,"必传参数不能为空",false,-1,watch.getTime());
        }

        List<GetWaterViewNewPojo> userListRiver=data.queryByRiver(listSTCD, stime, etime,dayhour,mtype);
        List<GetWaterViewNewPojo> userListWas=data.queryByWas(listSTCD, stime, etime,dayhour,mtype);
        List<GetWaterViewNewPojo> userListTide=data.queryByTide(listSTCD, stime, etime,dayhour,mtype);
        List<GetWaterViewNewPojo> userListPump=data.queryByPump(listSTCD, stime, etime,dayhour,mtype);
        List<GetWaterViewNewPojo> dataList=new ArrayList<>();
        List<ST_STBPRP_BPojo> stStbprpBList = stbprpBData.selectStbprpBList(listSTCD, null,null);

        if(quList.size()>0){
           for (int num=0;num<quList.size();num++){
               ST_STBPRP_B_QUPojo onestbprpBQuList=quList.get(num);
               String stcd= onestbprpBQuList.getSTCD().toString();
               List<GetWaterViewNewPojo> oneRiverList=userListRiver.stream().filter(u->u.getSTCD().equals(stcd)&&onestbprpBQuList.getSTTP().equals(u.getMTYPE())).collect(Collectors.toList());
               List<GetWaterViewNewPojo> oneWasList=userListWas.stream().filter(u->u.getSTCD().equals(stcd)&&onestbprpBQuList.getSTTP().equals(u.getMTYPE())).collect(Collectors.toList());
               List<GetWaterViewNewPojo> oneTideList=userListTide.stream().filter(u->u.getSTCD().equals(stcd)&&onestbprpBQuList.getSTTP().equals(u.getMTYPE())).collect(Collectors.toList());
               List<GetWaterViewNewPojo> onePumpList=userListPump.stream().filter(u->u.getSTCD().equals(stcd)&&onestbprpBQuList.getSTTP().equals(u.getMTYPE())).collect(Collectors.toList());
               List<ST_STBPRP_BPojo> onestStbprpB=stStbprpBList.stream().filter(u->u.getSTCD().equals(stcd)&&onestbprpBQuList.getSTTP().equals(u.getMTYPE())).collect(Collectors.toList());
               List<ST_STBPRP_B_QUPojo> tempquList=quList.stream().filter(u->u.getSTCD().equals(stcd)).collect(Collectors.toList());
               List<GetWaterViewNewPojo> temponeRiverList=new ArrayList<>();
               List<GetWaterViewNewPojo> temponewasList=new ArrayList<>();
               temponeRiverList=oneRiverList.stream().filter(u->u.getUPZ()!=null&&u.getDWZ()!=null).sorted(Comparator.comparing(GetWaterViewNewPojo::getTM).reversed()).collect(Collectors.toList());
               if(temponeRiverList.size()==0){
                   temponeRiverList=oneRiverList.stream().filter(u->u.getUPZ()!=null||u.getDWZ()!=null).sorted(Comparator.comparing(GetWaterViewNewPojo::getTM).reversed()).collect(Collectors.toList());
               }
               temponewasList=oneWasList.stream().filter(u->u.getUPZ()!=null&&u.getDWZ()!=null).sorted(Comparator.comparing(GetWaterViewNewPojo::getTM).reversed()).collect(Collectors.toList());
               if(temponewasList.size()==0){
                   temponewasList=oneWasList.stream().filter(u->u.getUPZ()!=null||u.getDWZ()!=null).sorted(Comparator.comparing(GetWaterViewNewPojo::getTM).reversed()).collect(Collectors.toList());;
               }


               if(temponeRiverList.size()>0){
                   GetWaterViewNewPojo pojoOld=temponeRiverList.get(0);
                   GetWaterViewNewPojo pojo=new GetWaterViewNewPojo();
                   pojo.setSTCD(pojoOld.getSTCD());
                   pojo.setSTNM(pojoOld.getSTNM());
                   pojo.setLGTD(pojoOld.getLGTD());
                   pojo.setLTTD(pojoOld.getLTTD());
                   pojo.setLGTD84(pojoOld.getLGTD84());
                   pojo.setLTTD84(pojoOld.getLTTD84());
                   pojo.setUPZ(pojoOld.getUPZ());
                //    pojo.setDWZ(pojoOld.getDWZ());
                   pojo.setTM(pojoOld.getTM());
                   if(tempquList.size()>0){
                       pojo.setSTNM(tempquList.get(0).getSTNM());
                   }
                   dataList.add(pojo);
               }
               else if(temponewasList.size()>0){

                   GetWaterViewNewPojo pojoOld=temponewasList.get(0);
                   GetWaterViewNewPojo pojo=new GetWaterViewNewPojo();
                   pojo.setSTCD(pojoOld.getSTCD());
                   pojo.setSTNM(pojoOld.getSTNM());
                   pojo.setLGTD(pojoOld.getLGTD());
                   pojo.setLTTD(pojoOld.getLTTD());
                   pojo.setLGTD84(pojoOld.getLGTD84());
                   pojo.setLTTD84(pojoOld.getLTTD84());
                   pojo.setUPZ(pojoOld.getUPZ());
                   if(pojoOld.getDWZ()!=null){
                    if(pojoOld.getDWZ().equals("0")){
                        pojo.setDWZ(pojoOld.getDWZ());
                    }
                   }
                   pojo.setTM(pojoOld.getTM());
                   if(tempquList.size()>0){
                       pojo.setSTNM(tempquList.get(0).getSTNM());
                   }
                   dataList.add(pojo);
               }
               else if(oneTideList.size()>0){
                   GetWaterViewNewPojo pojoOld=oneTideList.get(0);
                   GetWaterViewNewPojo pojo=new GetWaterViewNewPojo();
                   pojo.setSTCD(pojoOld.getSTCD());
                   pojo.setSTNM(pojoOld.getSTNM());
                   pojo.setLGTD(pojoOld.getLGTD());
                   pojo.setLTTD(pojoOld.getLTTD());
                   pojo.setLGTD84(pojoOld.getLGTD84());
                   pojo.setLTTD84(pojoOld.getLTTD84());
                   pojo.setUPZ(pojoOld.getUPZ());
                //    pojo.setDWZ(pojoOld.getDWZ());
                   pojo.setTM(pojoOld.getTM());
                   if(tempquList.size()>0){
                       pojo.setSTNM(tempquList.get(0).getSTNM());
                   }
                   dataList.add(pojo);
               }
               else if(onePumpList.size()>0){
                   GetWaterViewNewPojo pojoOld=onePumpList.get(0);
                   GetWaterViewNewPojo pojo=new GetWaterViewNewPojo();
                   pojo.setSTCD(pojoOld.getSTCD());
                   pojo.setSTNM(pojoOld.getSTNM());
                   pojo.setLGTD(pojoOld.getLGTD());
                   pojo.setLTTD(pojoOld.getLTTD());
                   pojo.setLGTD84(pojoOld.getLGTD84());
                   pojo.setLTTD84(pojoOld.getLTTD84());
                   pojo.setUPZ(pojoOld.getUPZ());
                //    pojo.setDWZ(pojoOld.getDWZ());
                   pojo.setTM(pojoOld.getTM());
                   if(tempquList.size()>0){
                       pojo.setSTNM(tempquList.get(0).getSTNM());
                   }
                   dataList.add(pojo);
               }
               else{
                   if(onestStbprpB.size()>0){
                       GetWaterViewNewPojo pojo=new GetWaterViewNewPojo();
                       pojo.setSTCD(onestStbprpB.get(0).getSTCD());
                       pojo.setSTNM(onestStbprpB.get(0).getSTNM());
                       pojo.setLGTD(String.valueOf(onestStbprpB.get(0).getLGTD()));
                       pojo.setLTTD(String.valueOf(onestStbprpB.get(0).getLTTD()));
                       pojo.setLGTD84(String.valueOf(onestStbprpB.get(0).getLGTD84()));
                       pojo.setLTTD84(String.valueOf(onestStbprpB.get(0).getLTTD84()));                      

                       dataList.add(pojo);
                   }
               }
           }
       }
        watch.stop();
        if(dataList.size() > 0){
            return new ResultUtils<>(dataList, "操作成功",true ,dataList.size(),watch.getTime());
        }else {
            return new ResultUtils<>(dataList, "操作成功",false,dataList.size(),watch.getTime());
        }
    }

    //单站过程水位
    @RequestMapping("/getShiShiShuiWei_xx")
    public ResultUtils getShiShiShuiWei_xx(@RequestBody ColumnName param){
        StopWatch watch = new StopWatch();
        watch.start();
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(param, ParamField.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if(param.getStcd()==null){
            return new ResultUtils<>(null, "stcd参数必传",false,-1,watch.getTime());
        }
        if(param.getStime()==null){
            return new ResultUtils<>(null, "stime参数必传",false,-1,watch.getTime());
        }
        if(param.getEtime()==null){
            return new ResultUtils<>(null, "etime参数必传",false,-1,watch.getTime());
        }
        // if(param.getPathname()==null){
        //     return new ResultUtils<>(null, "pathname参数必传",false,-1,watch.getTime());
        // }
        if(param.getPagesize()==null||param.getPageindex()==null){
            return new ResultUtils<>(null, "分页参数必传",false,-1,watch.getTime());
        }

        Date date = new Date(new Date().getTime() - 24 * 60 * 60 * 1000);
        String pageindex = "",pagesize = "10";
        String stime=DateUtil.dateFormat(date,"yyyy-MM-dd HH:mm:ss"),etime=DateUtil.dateFormat(new Date(),"yyyy-MM-dd HH:mm:ss"),id="Minute";
        List<String> stcdList = new ArrayList<>();
        String mtype="";
        if(null != param.getPathname()){
            id = param.getPathname();
        }
        if(null != param.getStcd()){
            stcdList = Arrays.asList(param.getStcd().split(","));
        }
        if(null != param.getStime()){
            stime = param.getStime();
        }
        if(null != param.getEtime()){
            etime = param.getEtime();
        }
        if(null != param.getDatasource()){
            mtype = param.getDatasource();
        }
        if(null != param.getPageindex()){
            pageindex =String.valueOf( param.getPageindex());
        }
        if(null != param.getPagesize()){
            pagesize = String.valueOf(param.getPagesize());
        }


        // 定义时间格式化器
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // 将字符串解析为 LocalDateTime 对象
        // 2. 兼容解析方法：如果字符串只有日期（长度为10），自动补全为 00:00:00
        LocalDateTime startTime = stime.trim().length() == 10 
            ? LocalDate.parse(stime).atStartOfDay() 
            : LocalDateTime.parse(stime.replace(" ", "T"));

        LocalDateTime endTime = etime.trim().length() == 10 
            ? LocalDate.parse(etime).atStartOfDay() 
            : LocalDateTime.parse(etime.replace(" ", "T"));

        // 计算两个时间相差的天数（绝对值）
        long daysBetween = Math.abs(ChronoUnit.DAYS.between(startTime, endTime));
        // 判断跨度是否超过 10 天
        if (daysBetween > 10) {
            return new ResultUtils<>(null, "时间范围不能超过10天",false,-1,watch.getTime());
        } 

        List<GetWaterViewNewPojo> list = new ArrayList<>();
        

        Integer startindex = null;
        if(!"".equals(pageindex) && !"".equals(pagesize)){
            startindex = (Integer.parseInt(pageindex) - 1) * Integer.parseInt(pagesize);
        }
        if(Integer.parseInt(pagesize)>1000){
            pagesize="1000";
        }
        System.out.print("startindex:::::::"+startindex+",pagesize:::::::"+pagesize);
        list = server.selectListWaterAll(stcdList, stime, etime,mtype,startindex,Integer.valueOf(pagesize));

        Integer integer = server.selectListWaterAllCount(stcdList, stime, etime,mtype);
        Integer count = 1;
        if(!"".equals(pagesize)){
            count = integer / Integer.parseInt(pagesize);
            if(integer % Integer.parseInt(pagesize) != 0){
                count += 1;
            }
        }

        

        watch.stop();
        // if(list.size() > 0){
        //     return new ResultUtils<>(list, "操作成功",true ,list.size(),watch.getTime());
        // }else {
        //     return new ResultUtils<>(list, "操作成功",false,list.size(),watch.getTime());
        // }

        if(list.size() > 0){
            return new ResultUtils<>(
                list, 
                "操作成功",
                true,
                Integer.parseInt(pagesize) ,
                Integer.parseInt(pageindex),
                count,
                integer,
                list.size(),
                watch.getTime()
            );
        }else {
            return new ResultUtils<>(list, "操作成功",false,Integer.parseInt(pagesize) ,Integer.parseInt(pageindex),count,integer,list.size(),watch.getTime());
        }
    }

    //雨量
    @RequestMapping("/getYuLiang")
    public ResultUtils getShiShiYuLiang(@RequestBody ColumnName param){
        StopWatch watch = new StopWatch();
        watch.start();
         if(CommonUtills.isEmpty(FieldIsValid.getColumnName(param, ParamField.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        Date date = new Date(new Date().getTime() - 1 * 60 * 60 * 1000);
        String stcd="",stime= DateUtil.dateFormat(date,"yyyy-MM-dd HH:mm:ss"),etime="",PID="",ADMAUTH="",STNM="",pathname="";
        if(null != param.getPid()){
            PID = param.getPid();
        }
        else{
            return new ResultUtils<>(null,"必传参数不能为空",false,-1,watch.getTime());
        }
        List<String> stcdList = new ArrayList<>();
        if(!CommonUtills.isEmpty(PID)){
            if(PID.equals("shquanshi")){//上海全市的数据
               PID="201901101419326076-1-1";
            }
            else if(PID.equals("huashida")){//华师大和伏羲模型
               PID="2026051300001-1-2";
            }
            //查询配置的站码
            List<ST_STBPRP_B_QUPojo> quList = quData.selectList("", STNM, null, PID,ADMAUTH);
            if(null != quList && quList.size() > 0){
                for(ST_STBPRP_B_QUPojo quPojo : quList){
                    if(null != quPojo.getSTCD()){
                        stcdList.add(quPojo.getSTCD());
                    }
                }
            }
        }
        List<ST_STBPRP_BPojo> stStbprpBList = stbprpBData.selectStbprpBList(stcdList, null,null);     

        List<ST_PPTN_RPojo> mListList =dataRain.querySUMDRPList(stime, etime,null,stcdList);
        List<ST_PPTN_RPojo> mList=new ArrayList<>();
        stStbprpBList.forEach(item->{
            List<ST_PPTN_RPojo> mListListT=mListList.stream().filter(u->u.getSTCD().equals(item.getSTCD())).collect(Collectors.toList());
            ST_PPTN_RPojo pojo=new ST_PPTN_RPojo();
            if(mListListT.size()>0){
                pojo=mListListT.get(0);
            }
            else{
                pojo.setSTCD(item.getSTCD());
                pojo.setSTNM(item.getSTNM());
                pojo.setLGTD(item.getLGTD());
                pojo.setLTTD(item.getLTTD());
                pojo.setLGTD84(item.getLGTD84());
                pojo.setLTTD84(item.getLTTD84());
            }
            mList.add(pojo);
        });

        watch.stop();
        if(mList.size() > 0){
            return new ResultUtils<>(mList, "操作成功",true ,mList.size(),watch.getTime());
        }else {
            return new ResultUtils<>(mList, "操作成功",false,mList.size(),watch.getTime());
        }
    }

    //雨量过程
    @RequestMapping("/getYuLiang_xx")
    public ResultUtils getYuLiang_xx(@RequestBody ColumnName param){
        StopWatch watch = new StopWatch();
        watch.start();
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(param, ParamField.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if(param.getStcd()==null){
            return new ResultUtils<>(null, "stcd参数必传",false,-1,watch.getTime());
        }
        if(param.getStime()==null){
            return new ResultUtils<>(null, "stime参数必传",false,-1,watch.getTime());
        }
        if(param.getEtime()==null){
            return new ResultUtils<>(null, "etime参数必传",false,-1,watch.getTime());
        }
        if(param.getPagesize()==null||param.getPageindex()==null){
            return new ResultUtils<>(null, "分页参数必传",false,-1,watch.getTime());
        }

        Date date = new Date(new Date().getTime() - 24 * 60 * 60 * 1000);
        String pageindex = "",pagesize = "10";
        String stime=DateUtil.dateFormat(date,"yyyy-MM-dd HH:mm:ss"),etime=DateUtil.dateFormat(new Date(),"yyyy-MM-dd HH:mm:ss"),id="Minute";
        List<String> stcdList = new ArrayList<>();
        String mtype="";
        if(null != param.getPathname()){
            id = param.getPathname();
        }
        if(null != param.getStcd()){
            stcdList = Arrays.asList(param.getStcd().split(","));
        }
        if(null != param.getStime()){
            stime = param.getStime();
        }
        if(null != param.getEtime()){
            etime = param.getEtime();
        }
        if(null != param.getDatasource()){
            mtype = param.getDatasource();
        }
        if(null != param.getPageindex()){
            pageindex =String.valueOf( param.getPageindex());
        }
        if(null != param.getPagesize()){
            pagesize = String.valueOf(param.getPagesize());
        }


        // 定义时间格式化器
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // 将字符串解析为 LocalDateTime 对象
        // 2. 兼容解析方法：如果字符串只有日期（长度为10），自动补全为 00:00:00
        LocalDateTime startTime = stime.trim().length() == 10 
            ? LocalDate.parse(stime).atStartOfDay() 
            : LocalDateTime.parse(stime.replace(" ", "T"));

        LocalDateTime endTime = etime.trim().length() == 10 
            ? LocalDate.parse(etime).atStartOfDay() 
            : LocalDateTime.parse(etime.replace(" ", "T"));

        // 计算两个时间相差的天数（绝对值）
        long daysBetween = Math.abs(ChronoUnit.DAYS.between(startTime, endTime));
        // 判断跨度是否超过 10 天
        if (daysBetween > 10) {
            return new ResultUtils<>(null, "时间范围不能超过10天",false,-1,watch.getTime());
        } 
        List<ST_PPTN_RPojo> mList = new ArrayList<>();
        Integer startindex = null;
        if(!"".equals(pageindex) && !"".equals(pagesize)){
            startindex = (Integer.parseInt(pageindex) - 1) * Integer.parseInt(pagesize);
        }
        if(Integer.parseInt(pagesize)>1000){
            pagesize="1000";
        }
        System.out.print("startindex:::::::"+startindex+",pagesize:::::::"+pagesize);
        mList = dataRain.queryDRPListAll(stime, etime,null,stcdList,Integer.parseInt(pageindex),Integer.parseInt(pagesize));


        Integer integer = dataRain.queryDRPListAllCount(stime, etime,null,stcdList);
        Integer count = 1;
        if(!"".equals(pagesize)){
            count = integer / Integer.parseInt(pagesize);
            if(integer % Integer.parseInt(pagesize) != 0){
                count += 1;
            }
        }
        watch.stop();

        if(mList.size() > 0){
            return new ResultUtils<>(
                mList, 
                "操作成功",
                true,
                Integer.parseInt(pagesize) ,
                Integer.parseInt(pageindex),
                count,
                integer,
                mList.size(),
                watch.getTime()
            );
        }else {
            return new ResultUtils<>(mList, "操作成功",false,Integer.parseInt(pagesize) ,Integer.parseInt(pageindex),count,integer,mList.size(),watch.getTime());
        }
        
    }



   @RequestMapping("/getShiShiLiuLiang")
   public ResultUtils getShiShiLiuLiang(@RequestBody ColumnName param) {
        StopWatch watch = new StopWatch();
        watch.start();        
         if(CommonUtills.isEmpty(FieldIsValid.getColumnName(param, ParamField.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        Date date = new Date(new Date().getTime() - 3 * 60 * 60 * 1000);
        String stime = DateUtil.dateFormat(date, "yyyy-MM-dd HH:mm:ss"), etime = "", PID = "";
        String dayhour = "Minute";
        String mtype = "";
        List<String> stcdList = new ArrayList<>();
        if (null != param.getPid()) {
            PID = param.getPid();
            if(PID.equals("shquanshi")){//上海全市的数据
               PID="2026031114184492913-4";
            }
            else if(PID.equals("huashida")){//华师大和伏羲模型
               PID="2026051300001-1-3";
            }
        }
        else{
            return new ResultUtils<>(null,"必传参数不能为空",false,-1,watch.getTime());
        }
        if (null != param.getStime()) {
            stime = param.getStime();
        }
        if (null != param.getEtime()) {
            etime = param.getEtime();
        }
        if (null != param.getPathname()) {
            dayhour = param.getPathname();
        }
        if (null != param.getDatasource()) {
            mtype = param.getDatasource();
        }
        List<ST_STBPRP_B_QUPojo> quList = quData.selectList("", "", null, PID, null);
        if (null != quList && quList.size() > 0) {
            for (ST_STBPRP_B_QUPojo quPojo : quList) {
                if (null != quPojo.getSTCD()) {
                    stcdList.add(quPojo.getSTCD());
                }
            }
        }
        if (!(stcdList.size() > 0)) {
            return new ResultUtils<>(null, "操作成功", false, 0, watch.getTime());
        }
        List<ST_STBPRP_BPojo> stStbprpBList = stbprpBData.selectStbprpBList(stcdList, null, null);
        List<GetWaterViewNewPojo> flowRList = data.queryByFlow(stcdList, stime, etime, dayhour, mtype);
        List<GetWaterViewNewPojo> dataList = new ArrayList<>();
        if (quList.size() > 0) {
            for (int num = 0; num < quList.size(); num++) {
                ST_STBPRP_B_QUPojo onestbprpBQuList = quList.get(num);
                String stcd = onestbprpBQuList.getSTCD().toString();
                List<GetWaterViewNewPojo> oneFlowList = flowRList.stream().filter(u -> u.getSTCD().equals(stcd) && onestbprpBQuList.getSTTP().equals(u.getMTYPE())).collect(Collectors.toList());
                List<ST_STBPRP_BPojo> onestStbprpB = stStbprpBList.stream().filter(u -> u.getSTCD().equals(stcd) && onestbprpBQuList.getSTTP().equals(u.getMTYPE())).collect(Collectors.toList());
                List<ST_STBPRP_B_QUPojo> tempquList = quList.stream().filter(u -> u.getSTCD().equals(stcd)).collect(Collectors.toList());
                if (oneFlowList.size() > 0) {
                    GetWaterViewNewPojo pojoOld = oneFlowList.get(0);
                    GetWaterViewNewPojo pojo=new GetWaterViewNewPojo();
                    pojo.setSTCD(pojoOld.getSTCD());
                    pojo.setSTNM(pojoOld.getSTNM());
                    pojo.setLGTD(pojoOld.getLGTD());
                    pojo.setLTTD(pojoOld.getLTTD());
                    pojo.setLGTD84(pojoOld.getLGTD84());
                    pojo.setLTTD84(pojoOld.getLTTD84());
                    pojo.setQ(pojoOld.getQ());
                    pojo.setTM(pojoOld.getTM());
                    dataList.add(pojo);
                } else {
                    if (onestStbprpB.size() > 0) {
                        GetWaterViewNewPojo pojo = new GetWaterViewNewPojo();
                        pojo.setSTCD(onestStbprpB.get(0).getSTCD());
                        pojo.setSTNM(onestStbprpB.get(0).getSTNM());
                        pojo.setLGTD(String.valueOf(onestStbprpB.get(0).getLGTD()));
                        pojo.setLTTD(String.valueOf(onestStbprpB.get(0).getLTTD()));   
                        pojo.setLGTD84(String.valueOf(onestStbprpB.get(0).getLGTD84()));
                        pojo.setLTTD84(String.valueOf(onestStbprpB.get(0).getLTTD84()));
                        dataList.add(pojo);
                    }
                }
            }
        }
        watch.stop();
        if (dataList.size() > 0) {
            return new ResultUtils<>(dataList, "操作成功", true, dataList.size(), watch.getTime());
        } else {
            return new ResultUtils<>(dataList, "操作成功", false, dataList.size(), watch.getTime());
        }
    }

   @RequestMapping("/getShiShiLiuLiang_xx")
   public ResultUtils getShiShiLiuLiang_xx(@RequestBody ColumnName param){
        StopWatch watch = new StopWatch();
        watch.start();
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(param, ParamField.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if(param.getStcd()==null){
            return new ResultUtils<>(null, "stcd参数必传",false,-1,watch.getTime());
        }
        if(param.getStime()==null){
            return new ResultUtils<>(null, "stime参数必传",false,-1,watch.getTime());
        }
        if(param.getEtime()==null){
            return new ResultUtils<>(null, "etime参数必传",false,-1,watch.getTime());
        }
        if(param.getPagesize()==null||param.getPageindex()==null){
            return new ResultUtils<>(null, "分页参数必传",false,-1,watch.getTime());
        }

        Date date = new Date(new Date().getTime() - 24 * 60 * 60 * 1000);
        String pageindex = "",pagesize = "10";
        String stime=DateUtil.dateFormat(date,"yyyy-MM-dd HH:mm:ss"),etime=DateUtil.dateFormat(new Date(),"yyyy-MM-dd HH:mm:ss"),id="Minute";
        List<String> stcdList = new ArrayList<>();
        String mtype="";
        if(null != param.getPathname()){
            id = param.getPathname();
        }
        if(null != param.getStcd()){
            stcdList = Arrays.asList(param.getStcd().split(","));
        }
        if(null != param.getStime()){
            stime = param.getStime();
        }
        if(null != param.getEtime()){
            etime = param.getEtime();
        }
        if(null != param.getDatasource()){
            mtype = param.getDatasource();
        }
        if(null != param.getPageindex()){
            pageindex =String.valueOf( param.getPageindex());
        }
        if(null != param.getPagesize()){
            pagesize = String.valueOf(param.getPagesize());
        }


        // 定义时间格式化器
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // 将字符串解析为 LocalDateTime 对象
        // 2. 兼容解析方法：如果字符串只有日期（长度为10），自动补全为 00:00:00
        LocalDateTime startTime = stime.trim().length() == 10 
            ? LocalDate.parse(stime).atStartOfDay() 
            : LocalDateTime.parse(stime.replace(" ", "T"));

        LocalDateTime endTime = etime.trim().length() == 10 
            ? LocalDate.parse(etime).atStartOfDay() 
            : LocalDateTime.parse(etime.replace(" ", "T"));

        // 计算两个时间相差的天数（绝对值）
        long daysBetween = Math.abs(ChronoUnit.DAYS.between(startTime, endTime));
        // 判断跨度是否超过 10 天
        if (daysBetween > 10) {
            return new ResultUtils<>(null, "时间范围不能超过10天",false,-1,watch.getTime());
        } 
        List<ST_FLOW_RPojo> mList = new ArrayList<>();
        Integer startindex = null;
        if(!"".equals(pageindex) && !"".equals(pagesize)){
            startindex = (Integer.parseInt(pageindex) - 1) * Integer.parseInt(pagesize);
        }
        if(Integer.parseInt(pagesize)>1000){
            pagesize="1000";
        }
        System.out.print("单站流量startindex:::::::"+startindex+",pagesize:::::::"+pagesize);
        mList = sFlow_RData.selectHisAll(stcdList, stime, etime,startindex,Integer.parseInt(pagesize));     


        Integer integer = sFlow_RData.selectHisAllCount(stcdList, stime, etime);
        Integer count = 1;
        if(!"".equals(pagesize)){
            count = integer / Integer.parseInt(pagesize);
            if(integer % Integer.parseInt(pagesize) != 0){
                count += 1;
            }
        }
        watch.stop();

        if(mList.size() > 0){
            return new ResultUtils<>(
                mList, 
                "操作成功",
                true,
                Integer.parseInt(pagesize) ,
                Integer.parseInt(pageindex),
                count,
                integer,
                mList.size(),
                watch.getTime()
            );
        }else {
            return new ResultUtils<>(mList, "操作成功",false,Integer.parseInt(pagesize) ,Integer.parseInt(pageindex),count,integer,mList.size(),watch.getTime());
        }
        
    }
    
   @RequestMapping("/getShiShiFeng")
   public ResultUtils getShiShiFeng(@RequestBody ColumnName param) {
        StopWatch watch = new StopWatch();
        watch.start();        
         if(CommonUtills.isEmpty(FieldIsValid.getColumnName(param, ParamField.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        Date date = new Date(new Date().getTime() - 3 * 60 * 60 * 1000);
        String stime = DateUtil.dateFormat(date, "yyyy-MM-dd HH:mm:ss"), etime = "", PID = "";
        String dayhour = "Minute";
        String mtype = "";
        List<String> stcdList = new ArrayList<>();
        if (null != param.getPid()) {
            PID = param.getPid();
            if(PID.equals("shquanshi")){//上海全市的数据
               PID="2026031114184492913-4";
            }
            else if(PID.equals("huashida")){//华师大和伏羲模型
               PID="2026051300001-1-4";
            }
        }
        else{
            return new ResultUtils<>(null,"必传参数不能为空",false,-1,watch.getTime());
        }
        if (null != param.getStime()) {
            stime = param.getStime();
        }
        if (null != param.getEtime()) {
            etime = param.getEtime();
        }
        if (null != param.getPathname()) {
            dayhour = param.getPathname();
        }
        if (null != param.getDatasource()) {
            mtype = param.getDatasource();
        }
        List<ST_STBPRP_B_QUPojo> quList = quData.selectList("", "", null, PID, null);
        if (null != quList && quList.size() > 0) {
            for (ST_STBPRP_B_QUPojo quPojo : quList) {
                if (null != quPojo.getSTCD()) {
                    stcdList.add(quPojo.getSTCD());
                }
            }
        }
        if (!(stcdList.size() > 0)) {
            return new ResultUtils<>(null, "操作成功", false, 0, watch.getTime());
        }
        List<ST_STBPRP_BPojo> stStbprpBList = stbprpBData.selectStbprpBList(stcdList, null, null);
        List<ST_WDWV_RPojo> flowRList = stdwvRData.selectNew(stcdList, stime, etime);
        List<GetWaterViewNewPojo> dataList = new ArrayList<>();
        if (quList.size() > 0) {
            for (int num = 0; num < quList.size(); num++) {
                ST_STBPRP_B_QUPojo onestbprpBQuList = quList.get(num);
                String stcd = onestbprpBQuList.getSTCD().toString();
                List<ST_WDWV_RPojo> oneFengList = flowRList.stream().filter(u -> u.getSTCD().equals(stcd)).collect(Collectors.toList());
                List<ST_STBPRP_BPojo> onestStbprpB = stStbprpBList.stream().filter(u -> u.getSTCD().equals(stcd)).collect(Collectors.toList());
                List<ST_STBPRP_B_QUPojo> tempquList = quList.stream().filter(u -> u.getSTCD().equals(stcd)).collect(Collectors.toList());
                if (oneFengList.size() > 0) {
                    ST_WDWV_RPojo pojoOld = oneFengList.get(0);
                    GetWaterViewNewPojo pojo=new GetWaterViewNewPojo();
                    pojo.setSTCD(pojoOld.getSTCD());                    
                    pojo.setTM(pojoOld.getTM());
                    pojo.setWNDV(pojoOld.getWNDV());
                    pojo.setWNDPWR(pojoOld.getWNDPWR());
                    pojo.setWNDDIR(pojoOld.getWNDDIR());
                    pojo.setWVHGT(pojoOld.getWVHGT());
                    pojo.setWNANGLE(pojoOld.getWNANGLE());
                    pojo.setPRESSURE(pojoOld.getPRESSURE());

                    if (onestStbprpB.size() > 0) {
                        pojo.setSTNM(onestStbprpB.get(0).getSTNM());
                        pojo.setLGTD(String.valueOf(onestStbprpB.get(0).getLGTD()));
                        pojo.setLTTD(String.valueOf(onestStbprpB.get(0).getLTTD()));    
                        pojo.setLGTD84(String.valueOf(onestStbprpB.get(0).getLGTD84()));
                        pojo.setLTTD84(String.valueOf(onestStbprpB.get(0).getLTTD84()));
                    }
                    dataList.add(pojo);
                } else {
                    if (onestStbprpB.size() > 0) {
                        GetWaterViewNewPojo pojo = new GetWaterViewNewPojo();
                        pojo.setSTCD(onestStbprpB.get(0).getSTCD());
                        pojo.setSTNM(onestStbprpB.get(0).getSTNM());
                        pojo.setLGTD(String.valueOf(onestStbprpB.get(0).getLGTD()));
                        pojo.setLTTD(String.valueOf(onestStbprpB.get(0).getLTTD()));   
                        pojo.setLGTD84(String.valueOf(onestStbprpB.get(0).getLGTD84()));
                        pojo.setLTTD84(String.valueOf(onestStbprpB.get(0).getLTTD84()));
                        dataList.add(pojo);
                    }
                }
            }
        }
        watch.stop();
        if (dataList.size() > 0) {
            return new ResultUtils<>(dataList, "操作成功", true, dataList.size(), watch.getTime());
        } else {
            return new ResultUtils<>(dataList, "操作成功", false, dataList.size(), watch.getTime());
        }
    }
   
    @RequestMapping("/getShiShiFeng_xx")
   public ResultUtils getShiShiFeng_xx(@RequestBody ColumnName param){
        StopWatch watch = new StopWatch();
        watch.start();
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(param, ParamField.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if(param.getStcd()==null){
            return new ResultUtils<>(null, "stcd参数必传",false,-1,watch.getTime());
        }
        if(param.getStime()==null){
            return new ResultUtils<>(null, "stime参数必传",false,-1,watch.getTime());
        }
        if(param.getEtime()==null){
            return new ResultUtils<>(null, "etime参数必传",false,-1,watch.getTime());
        }
        if(param.getPagesize()==null||param.getPageindex()==null){
            return new ResultUtils<>(null, "分页参数必传",false,-1,watch.getTime());
        }

        Date date = new Date(new Date().getTime() - 24 * 60 * 60 * 1000);
        String pageindex = "",pagesize = "10";
        String stime=DateUtil.dateFormat(date,"yyyy-MM-dd HH:mm:ss"),etime=DateUtil.dateFormat(new Date(),"yyyy-MM-dd HH:mm:ss"),id="Minute";
        List<String> stcdList = new ArrayList<>();
        String mtype="";
        if(null != param.getPathname()){
            id = param.getPathname();
        }
        if(null != param.getStcd()){
            stcdList = Arrays.asList(param.getStcd().split(","));
        }
        if(null != param.getStime()){
            stime = param.getStime();
        }
        if(null != param.getEtime()){
            etime = param.getEtime();
        }
        if(null != param.getDatasource()){
            mtype = param.getDatasource();
        }
        if(null != param.getPageindex()){
            pageindex =String.valueOf( param.getPageindex());
        }
        if(null != param.getPagesize()){
            pagesize = String.valueOf(param.getPagesize());
        }


        // 定义时间格式化器
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // 将字符串解析为 LocalDateTime 对象
        // 2. 兼容解析方法：如果字符串只有日期（长度为10），自动补全为 00:00:00
        LocalDateTime startTime = stime.trim().length() == 10 
            ? LocalDate.parse(stime).atStartOfDay() 
            : LocalDateTime.parse(stime.replace(" ", "T"));

        LocalDateTime endTime = etime.trim().length() == 10 
            ? LocalDate.parse(etime).atStartOfDay() 
            : LocalDateTime.parse(etime.replace(" ", "T"));

        // 计算两个时间相差的天数（绝对值）
        long daysBetween = Math.abs(ChronoUnit.DAYS.between(startTime, endTime));
        // 判断跨度是否超过 10 天
        if (daysBetween > 10) {
            return new ResultUtils<>(null, "时间范围不能超过10天",false,-1,watch.getTime());
        } 
        List<ST_WDWV_RPojo> mList = new ArrayList<>();
        Integer startindex = null;
        if(!"".equals(pageindex) && !"".equals(pagesize)){
            startindex = (Integer.parseInt(pageindex) - 1) * Integer.parseInt(pagesize);
        }
        if(Integer.parseInt(pagesize)>1000){
            pagesize="1000";
        }
        System.out.print("单站风情startindex:::::::"+startindex+",pagesize:::::::"+pagesize);
        mList = stdwvRData.selectHisAll(stcdList, stime, etime,startindex,Integer.parseInt(pagesize));     


        Integer integer = stdwvRData.selectHisAllCount(stcdList, stime, etime);
        Integer count = 1;
        if(!"".equals(pagesize)){
            count = integer / Integer.parseInt(pagesize);
            if(integer % Integer.parseInt(pagesize) != 0){
                count += 1;
            }
        }
        watch.stop();

        if(mList.size() > 0){
            return new ResultUtils<>(
                mList, 
                "操作成功",
                true,
                Integer.parseInt(pagesize) ,
                Integer.parseInt(pageindex),
                count,
                integer,
                mList.size(),
                watch.getTime()
            );
        }else {
            return new ResultUtils<>(mList, "操作成功",false,Integer.parseInt(pagesize) ,Integer.parseInt(pageindex),count,integer,mList.size(),watch.getTime());
        }
        
    }
   
}
