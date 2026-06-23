package swzzmodeserver.workserver.controller.swzzrtsq;

import swzzmodeserver.tools.ResultUtils;
import swzzmodeserver.workserver.data.swzzrtsq.ST_STBPRP_B_QUData;
import swzzmodeserver.workserver.data.swzzrtsq.RTSQST_STBPRP_B_STCDData;
import swzzmodeserver.workserver.pojo.swzzrtsq.ST_STBPRP_B_QUPojo;
import swzzmodeserver.workserver.pojo.swzzrtsq.ST_STBPRP_B_STCDPojo;
import swzzmodeserver.tools.ColumnName;
import swzzmodeserver.tools.CommonUtills;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

// 【修改点】在 @RestController 中指定唯一名称
@RestController("RtsqST_STBPRP_B_STCDController")
@RequestMapping("/SWZZ_RTSQ_ST_STBPRP_B_STCD")
public class ST_STBPRP_B_STCDController {
    @Autowired
    private RTSQST_STBPRP_B_STCDData data;

    @Autowired
    private ST_STBPRP_B_QUData quData;

    @RequestMapping("/selectList")
    public ResultUtils selectList(@RequestBody ST_STBPRP_B_STCDPojo param){
        StopWatch watch = new StopWatch();
        watch.start();
        String STCD = "",STNM = "",PID = "";
        List<String> stcdList = new ArrayList<>();
        List<String> typeList = new ArrayList<>();
        List<String> sourceList = new ArrayList<>();
        if(null != param.getSTCD()){
            stcdList = Arrays.asList(param.getSTCD().split(","));
        }
        if(null != param.getTYPE()){
            typeList = Arrays.asList(param.getTYPE().split(","));
        }
        if(null != param.getSOURCE()){
            sourceList = Arrays.asList(param.getSOURCE().split(","));
        }
        List<ST_STBPRP_B_STCDPojo> treeList = data.selectList(stcdList,typeList,sourceList);
        watch.stop();
        if(treeList.size() > 0){
            return new ResultUtils<>(treeList, "操作成功",true ,treeList.size(),watch.getTime());
        }else {
            return new ResultUtils<>(treeList, "操作成功",false,treeList.size(),watch.getTime());
        }
    }

    @RequestMapping("/findResult")
    public ResultUtils findResult(@RequestBody ColumnName param){
        StopWatch watch = new StopWatch();
        watch.start();
        String STCD = "",STNM = "",PID = "";
        List<String> stcdList = new ArrayList<>();
        List<String> typeList = new ArrayList<>();
        List<String> sourceList = new ArrayList<>();
        if(null != param.getStcd()){
            stcdList = Arrays.asList(param.getStcd().split(","));
        }
        if(null != param.getPathname()){
            typeList = Arrays.asList(param.getPathname().split(","));
        }
        List<ST_STBPRP_B_QUPojo> quList =new ArrayList<>();
        if(null != param.getPid()){
//            sourceList = Arrays.asList(param.getPid().split(","));
            quList = quData.selectList(null, null, null, param.getPid(),null);
            if(quList.size()>0){
                stcdList = new ArrayList<>();
                for(ST_STBPRP_B_QUPojo quPojo :quList){
                    if(null != quPojo.getSTCD()){
                        stcdList.add(quPojo.getSTCD());
                    }
                }
            }
            else{
                stcdList = new ArrayList<>();
                stcdList.add("temp");
            }

        }

        List<ST_STBPRP_B_STCDPojo> treeList = data.selectList(stcdList,typeList,null);
        if(quList.size()>0){
            List<ST_STBPRP_B_QUPojo> finalQuList = quList;
            treeList.forEach(item->{
                List<ST_STBPRP_B_QUPojo> onequList = finalQuList.stream().filter(u->u.getSTCD().equals(item.getSTCD())).collect(Collectors.toList());
                if(onequList.size()>0){
                    item.setSTNM(onequList.get(0).getSTNM());
                    item.setORDERBYID(onequList.get(0).getORDERBYID());
                    item.setMAPSIZE(onequList.get(0).getMAPSIZE());
                    item.setDIR(onequList.get(0).getDIR());
                    item.setROTATE(onequList.get(0).getROTATE());
                }
            });
        }
        watch.stop();
        if(treeList.size() > 0){
            return new ResultUtils<>(treeList, "操作成功",true ,treeList.size(),watch.getTime());
        }else {
            return new ResultUtils<>(treeList, "操作成功",false,treeList.size(),watch.getTime());
        }
    }

    @RequestMapping("/remove")
    public ResultUtils deleteOne(@RequestBody ColumnName param){
        StopWatch watch = new StopWatch();
        watch.start();
        String stcd = "";
        if(null != param.getStcd()){
            stcd = param.getStcd();
        }
        String type = "";
        if(null != param.getPathname()){
            type = param.getPathname();
        }

        Integer num = 0;
        if(StringUtils.isNotEmpty(stcd) && StringUtils.isNotEmpty(type)){
            num = data.deleteOne(stcd,type);
        }
        watch.stop();

        if(num > 0){
            return new ResultUtils<>(num, "操作成功",true, num,watch.getTime());
        }else {
            return new ResultUtils<>(num, "操作成功",false, num,watch.getTime());
        }
    }

    @RequestMapping("/modify")
    public ResultUtils upDateOne(@RequestBody ColumnName param){
        StopWatch watch = new StopWatch();
        watch.start();
        String stime = "";
        if(null != param.getStime()){
            stime = param.getStime();
        }
        Integer  num = data.upDateOne(stime);
        watch.stop();
        if(num > 0){
            return new ResultUtils<>(num, "操作成功",true, num,watch.getTime());
        }else {
            return new ResultUtils<>(num, "操作成功",false, num,watch.getTime());
        }
    }
}
