package swzzmodeserver.workserver.controller.swzzrtsq;

import swzzmodeserver.tools.ResultUtils;
import swzzmodeserver.workserver.data.swzzrtsq.*;
import swzzmodeserver.workserver.pojo.swzzrtsq.*;
import swzzmodeserver.workserver.server.swzzrtsq.GetWaterViewNewServer;
import swzzmodeserver.tools.ColumnName;
import swzzmodeserver.tools.CommonUtills;
import swzzmodeserver.tools.DateUtil;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/SWZZ_RTSQ_ST_GATE_RNEW")
public class ST_GATE_RNEWController {
    @Autowired
    private RTSQST_GATE_RNEWData data;
    @Autowired
    private RTSQST_STBPRP_BData stbprpBData;
    @Autowired
    private GetWaterViewNewData waterViewNewData;
    @Autowired
    private ST_STBPRP_B_QUData quData;
    @Autowired
    private ST_STBPRP_B_TREEData treeData;
    @Autowired
    private RTSQST_STBPRP_B_STCDData stcdData;
    @Autowired
    private GetWaterViewNewServer server;

    @Autowired
    private ST_WDPSTAT_RData stWdpstatRData;

    @Autowired
    private RTSQST_GATE_RData rtsqstGateRData;

    @RequestMapping("/selectGQList")
    public List<ST_STBPRP_FCCHPojo> selectGQList(@RequestBody ColumnName param){
        List<String> stcdList = new ArrayList<>();
        String key = "",stime= DateUtil.dateFormat(new Date(),"yyyy-MM-dd HH:mm:ss"),etime="",PID="",stcd = "";
        if(null != param.getKey()){
            key = param.getKey();
        }
        if(null != param.getStime()){
            stime = param.getStime();
        }
        if(null != param.getEtime()){
            etime = param.getEtime();
        }
        if(null != param.getPid()){
            PID = param.getPid();
        }
        if(null != param.getStcd()){
            stcdList.add(param.getStcd());
        }else
        {
            List<ST_STBPRP_B_QUPojo> quList = quData.selectList(null, null, null, PID,null);
            for(ST_STBPRP_B_QUPojo quPojo :quList){
                if(null != quPojo.getSTCD()){
                    stcdList.add(quPojo.getSTCD());
                }
            }
        }
        if(!(stcdList.size() > 0)){
            return new ArrayList<>();
        }
        String mtype="";
        if(CommonUtills.isEmpty(param.getDatasource())){
            mtype=param.getDatasource();
        }
        List<ST_STBPRP_FCCHPojo> sfList = stbprpBData.selectGQList(stcdList);
        List<ST_GATE_RNEWPojo> gateList = data.selectGateListNew(stcdList);
        List<GetWaterViewNewPojo> waterViewNewList = server.selectListByNew(stcdList, stime, etime,mtype);

        for(ST_STBPRP_FCCHPojo stStbprpFcchPojo : sfList){
            String finalKey = key;
            List<ST_GATE_RNEWPojo> collect = gateList.stream().filter(i ->
                    i.getSTCD().equals(stStbprpFcchPojo.getSTCD()) //&& ("".equals(finalKey) || finalKey.equals(i.getEXKEY()))
            ).collect(Collectors.toList());
            stStbprpFcchPojo.setGateList(collect);
            List<GetWaterViewNewPojo> collects = waterViewNewList.stream().filter(i -> i.getSTCD().equals(stStbprpFcchPojo.getSTCD())).collect(Collectors.toList());
            if(collect.size() > 0 &&collects.size() > 0){
                GetWaterViewNewPojo waterViewNewPojo = collects.get(0);
                if(null != waterViewNewPojo.getUPZ()){
                    stStbprpFcchPojo.setUPZ(waterViewNewPojo.getUPZ());
                }
                stStbprpFcchPojo.setWaterHisList(collects);
            }
        }
        return sfList;
    }
    @RequestMapping("/selectGQListHis")
    public List<ST_STBPRP_FCCHPojo> selectGQListHis(@RequestBody ColumnName param){
        Date date = new Date(new Date().getTime() - 24 * 60 * 60 * 1000);
        String stime= DateUtil.dateFormat(date,"yyyy-MM-dd HH:mm:ss"),etime="";
        List<String> stcdList = new ArrayList<>();
        String key = "";
        if(null != param.getStcd()){
            stcdList = Arrays.asList(param.getStcd().split(","));
        }
        if(null != param.getKey()){
            key = param.getKey();
        }
        if(null != param.getStime()){
            stime = param.getStime();
        }
        if(null != param.getEtime()){
            etime = param.getEtime();
        }
        String mtype="";
        if(!CommonUtills.isEmpty(param.getDatasource())){
            mtype = param.getDatasource();
        }
        List<ST_STBPRP_FCCHPojo> sfList = stbprpBData.selectGQList(stcdList);
        List<ST_GATE_RPojo> gateList = rtsqstGateRData.selectByHis(null,stime,etime);
        List<GetWaterViewNewPojo> waterViewNewList = server.selectListByHisIsTime(stcdList, stime, etime,mtype);
        for(ST_STBPRP_FCCHPojo stStbprpFcchPojo : sfList){
            String finalKey = key;
            List<ST_GATE_RPojo> collect = gateList.stream().filter(i -> {
                return i.getSTCD().equals(stStbprpFcchPojo.getSTCD()) && ("".equals(finalKey) || finalKey.equals(i.getEXKEY()));
            } ).collect(Collectors.toList());
            stStbprpFcchPojo.setGateListHis(collect);
            List<GetWaterViewNewPojo> collects = waterViewNewList.stream().filter(i -> {
                return i.getSTCD().equals(stStbprpFcchPojo.getSTCD());
            }).collect(Collectors.toList());
            stStbprpFcchPojo.setWaterHisList(collects);

        }
        return sfList;
    }
    @RequestMapping("/queryList")
    public ResultUtils queryList(@RequestBody ColumnName param){
        StopWatch watch = new StopWatch();
        watch.start();
        List<String> stcdList = new ArrayList<>();
        String key = "",stime= DateUtil.dateFormat(new Date(),"yyyy-MM-dd HH:mm:ss"),etime="",PID="",stcd = "",pathname="";
        if(null != param.getKey()){
            key = param.getKey();
        }
        if(null != param.getStime()){
            stime = param.getStime();
        }
        if(null != param.getEtime()){
            etime = param.getEtime();
        }
        if(null != param.getPid()){
            PID = param.getPid();
        }
        if(null != param.getPathname()){
            pathname = param.getPathname();
        }
        List<ST_STBPRP_B_QUPojo> quList =new ArrayList<>();
        if(null != param.getStcd()){
            stcdList.add(param.getStcd());
        }
        else
        {
            quList = quData.selectList(null, null, null, PID,null);
            for(ST_STBPRP_B_QUPojo quPojo :quList){
                if(null != quPojo.getSTCD()){
                    stcdList.add(quPojo.getSTCD());
                }
            }
        }
        if(!(stcdList.size() > 0)){
            return new ResultUtils<>(null, "操作成功",true ,0,watch.getTime());
        }
        List<ST_STBPRP_FCCHPojo> sfList = stbprpBData.selectGQList(stcdList);

        List<ST_STBPRP_FCCHPojo>  userList=new ArrayList<>();
        List<ST_GATE_RNEWPojo> gateList = data.selectGateListNew(stcdList);

        for(ST_STBPRP_FCCHPojo stStbprpFcchPojo : sfList){
            List<ST_GATE_RNEWPojo> collect = gateList.stream().filter(i ->
                    i.getSTCD().equals(stStbprpFcchPojo.getSTCD())
            ).collect(Collectors.toList());

            List<ST_GATE_RNEWPojo> collectKai = collect.stream()
            .filter(i -> i.getGTOPHGT() != null) // 先排除 getGTOPHGT 为 null 的情况
            .filter(i -> i.getGTOPHGT() >= 0.1)  // 再进行正常的业务数值比较
            .collect(Collectors.toList());//开启的闸和泵

            stStbprpFcchPojo.setSTATUS(collectKai.size()>0?"open":"close");
            stStbprpFcchPojo.setGateList(collect);
            userList.add(stStbprpFcchPojo);
        }
        watch.stop();
        if(userList.size() > 0){
            return new ResultUtils<>(userList, "操作成功",true ,userList.size(),watch.getTime());
        }else {
            return new ResultUtils<>(userList, "操作成功",false,userList.size(),watch.getTime());
        }
    }

    @RequestMapping("/queryTreeList")
    public ResultUtils queryTreeList(@RequestBody ColumnName param){
        StopWatch watch = new StopWatch();
        watch.start();
        String key = "",stime= DateUtil.dateFormat(new Date(),"yyyy-MM-dd HH:mm:ss"),etime="",PID="",stcd = "",pathname="";
        if(null != param.getKey()){
            key = param.getKey();
        }
        if(null != param.getStime()){
            stime = param.getStime();
        }
        if(null != param.getEtime()){
            etime = param.getEtime();
        }
        if(null != param.getPid()){
            PID = param.getPid();
        }
        if(null != param.getPathname()){
            pathname = param.getPathname();
        }
        List<ST_GATE_BPojo>  userList=new ArrayList<>();

        if(!CommonUtills.isEmpty(PID)){
            ST_STBPRP_B_TREEPojo treePojo=new ST_STBPRP_B_TREEPojo();
            treePojo.setPID(PID);
            List<ST_STBPRP_B_TREEPojo> treePojoList=treeData.selectList(treePojo);
            if(treePojoList.size()==0){
                treePojo=new ST_STBPRP_B_TREEPojo();
                treePojo.setID(PID);
                treePojoList=treeData.selectList(treePojo);
            }
            if(treePojoList.size()>0){
                treePojoList.forEach(u->{
                    //查询配置的站码
                    List<ST_STBPRP_B_QUPojo> quList = quData.selectList("", null, null, u.getID(),null);
                    if(null != quList && quList.size() > 0){
                        List<String> stcdList = quList.stream().map(item -> item.getSTCD()).distinct().collect(Collectors.toList());
                        List<ST_GATE_RNEWPojo> gateList = data.selectGateListNew(stcdList);
                        AtomicReference<Integer> kaiCount= new AtomicReference<>(0);
                        quList.forEach(stStbprpFcchPojo->{
                            List<ST_GATE_RNEWPojo> collect = gateList.stream().filter(i ->
                                    i.getSTCD().equals(stStbprpFcchPojo.getSTCD())
                            ).collect(Collectors.toList());
                            List<ST_GATE_RNEWPojo> collectKai= collect.stream().filter(i->i.getGTOPHGT()>=0.1).collect(toList());//开启的闸和泵
                            if(collectKai.size()>0){
                                kaiCount.getAndSet(kaiCount.get() + 1);
                            }
                        });
                        ST_GATE_BPojo pojo=new ST_GATE_BPojo();
                        double total=Double.parseDouble(String.valueOf(quList.size())) ;
                        double bili=(kaiCount.get()/total)*100;
                        pojo.setSTCD(u.getID());
                        pojo.setSTNM(u.getTITLE());
                        pojo.setNUM(kaiCount.get()+"/"+quList.size());//开起的工程数/总工程数
                        pojo.setROTATE(String.valueOf(bili));
                        pojo.setStGateR(gateList);
                        userList.add(pojo);
                    }
                });
            }
        }

        watch.stop();
        if(userList.size() > 0){
            return new ResultUtils<>(userList, "操作成功",true ,userList.size(),watch.getTime());
        }else {
            return new ResultUtils<>(userList, "操作成功",false,userList.size(),watch.getTime());
        }
    }

    @RequestMapping("/queryDANZHANList")
    public ResultUtils queryDANZHANList(@RequestBody ColumnName param){
        StopWatch watch = new StopWatch();
        watch.start();
        List<String> stcdList = new ArrayList<>();
        String key = "",stime= DateUtil.dateFormat(new Date(),"yyyy-MM-dd HH:mm:ss"),etime="",PID="",stcd = "",pathname="";
        if(null != param.getKey()){
            key = param.getKey();
        }
        if(null != param.getStime()){
            stime = param.getStime();
        }
        if(null != param.getEtime()){
            etime = param.getEtime();
        }
        if(null != param.getPathname()){
            pathname = param.getPathname();
        }

        if(!CommonUtills.isEmpty(param.getStcd())){
//            stcdList.add(param.getStcd());
            stcdList= Arrays.asList(param.getStcd().split(","));
        }
        if(!(stcdList.size() > 0)){
            return new ResultUtils<>(null, "操作成功",false,0,watch.getTime());
        }

        List<ST_GATE_RNEWPojo> userList = data.queryList(stcdList,stime,etime,pathname,null);
        if(!CommonUtills.isEmpty(param.getStrExp())){

        }
        watch.stop();
        if(userList.size() > 0){
            return new ResultUtils<>(userList, "操作成功",true ,userList.size(),watch.getTime());
        }else {
            return new ResultUtils<>(userList, "操作成功",false,userList.size(),watch.getTime());
        }
    }
}
