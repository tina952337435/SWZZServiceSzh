package swzzmodeserver.workserver.controller.wds;

import swzzmodeserver.tools.ColumnName;
import swzzmodeserver.tools.CommonUtills;
import swzzmodeserver.tools.DateUtil;
import swzzmodeserver.workserver.data.wds.TSDBData;
import swzzmodeserver.workserver.pojo.swzzrtsq.TSDBPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/WDS_TSDB")
public class TSDBController {
    @Autowired
    private TSDBData data;

    @Value("${file.path.templatefilepath}")
    private String templatefilepath;

    @Autowired
    private CommonUtills commonUtills;

    @RequestMapping("/findResult")
    public Map<String, Object> findResult(@RequestBody ColumnName param){
        Date date = new Date(new Date().getTime() - 24 * 60 * 60 * 1000);
        String stime = DateUtil.dateFormat(date,"yyyy-MM-dd 08:00:00"), etime = "";
        List<String> stcdList = new ArrayList<>();
        if(null != param.getStcd()){
            stcdList = Arrays.asList(param.getStcd().split(","));
        }
        if(null != param.getStime()){
            stime = param.getStime();
        }
        if(null != param.getEtime()){
            etime = param.getEtime();
        }
        List<TSDBPojo> tsdbList = data.selectList(stime, etime, stcdList);
        if(null != tsdbList){
            return commonUtills.returnJson("select",tsdbList.size(),tsdbList);
        }else {
            return commonUtills.returnJson("select", -1, tsdbList);
        }
    }

    @RequestMapping("/GetDRPData")
    public Map<String, Object> GetDRPData(@RequestBody ColumnName param){
        List<String> idList = new ArrayList<>();
        String stime = "";
        if(null != param.getStcd()){
            idList = Arrays.asList(param.getStcd().split(","));
        }
        if(null != param.getStime()){
            stime = param.getStime();
        }
        List<TSDBPojo> waterList = data.selectTSDBListtTop(idList, stime,"");
        if(null != waterList){
            return commonUtills.returnJson("select",waterList.size(),waterList);
        }else {
            return commonUtills.returnJson("select", -1, waterList);
        }
    }
}