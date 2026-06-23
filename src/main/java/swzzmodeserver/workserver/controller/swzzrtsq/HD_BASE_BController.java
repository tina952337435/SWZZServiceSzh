package swzzmodeserver.workserver.controller.swzzrtsq;


import swzzmodeserver.tools.ResultUtils;
import swzzmodeserver.workserver.data.swzzrtsq.HD_BASE_BData;
import swzzmodeserver.workserver.pojo.swzzrtsq.HD_BASE_BPojo;
import swzzmodeserver.workserver.pojo.swzzrtsq.HD_BASE_B_PATHPojo;
import swzzmodeserver.tools.ColumnName;
import swzzmodeserver.tools.CommonUtills;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/SWZZ_RTSQ_HD_BASE_B")
@CrossOrigin
/**
 * 测试控制器
 */
//@Api(tags = "图标集合")
public class HD_BASE_BController {
    @Autowired
    private HD_BASE_BData db;


    @Autowired
    private CommonUtills Utills;
    /**
     * 查询
     * @return
     */
//    @ApiOperation("图标集合")
    @RequestMapping(value = "/findResult",method = RequestMethod.POST)
    @ResponseBody
    public ResultUtils selectList(@RequestBody ColumnName param)  {
        StopWatch watch = new StopWatch();
        watch.start();
        Map<String, Object> params = new HashMap<String, Object>();
        List<String> stcdList=new ArrayList<>();
        List<String> rvnmList=new ArrayList<>();
        if(!CommonUtills.isEmpty(param.getStcd())){
            stcdList= Arrays.asList(param.getStcd().split(","));
        }
        if(!CommonUtills.isEmpty(param.getDatasource())){
            rvnmList= Arrays.asList(param.getDatasource().split(","));
        }
        String key="",pathname="";
        if(!CommonUtills.isEmpty(param.getPathname())){
            pathname= param.getPathname();
        }
        if(!CommonUtills.isEmpty(param.getKey())){
            key= param.getKey();
        }
        List<HD_BASE_BPojo> allList = db.selectList(stcdList,rvnmList,key,pathname);
        watch.stop();
        if(allList.size() > 0){
            return new ResultUtils<>(allList, "操作成功",true ,allList.size(),watch.getTime());
        }else {
            return new ResultUtils<>(allList, "操作成功",false,allList.size(),watch.getTime());
        }
    }

    @RequestMapping(value = "/findResultPathList",method = RequestMethod.POST)
    @ResponseBody
    public ResultUtils selectPathList(@RequestBody ColumnName param)  {
        StopWatch watch = new StopWatch();
        watch.start();
        Map<String, Object> params = new HashMap<String, Object>();
        List<String> stcdList=new ArrayList<>();
        List<String> rvnmList=new ArrayList<>();
        if(!CommonUtills.isEmpty(param.getStcd())){
            stcdList= Arrays.asList(param.getStcd().split(","));
        }
        List<HD_BASE_BPojo> allList = db.selectPathList(stcdList);
        watch.stop();
        if(allList.size() > 0){
            return new ResultUtils<>(allList, "操作成功",true ,allList.size(),watch.getTime());
        }else {
            return new ResultUtils<>(allList, "操作成功",false,allList.size(),watch.getTime());
        }
    }
}
