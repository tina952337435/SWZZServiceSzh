package swzzmodeserver.workserver.controller.swzzflood;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swzzmodeserver.tools.CommonUtills;
import swzzmodeserver.tools.FieldIsValid;
import swzzmodeserver.tools.ParamField;
import swzzmodeserver.tools.ResultUtils;
import swzzmodeserver.workserver.data.swzzflood.HY_DP_CData;
import swzzmodeserver.workserver.data.swzzflood.ZhuantiData;
import swzzmodeserver.workserver.pojo.swzzflood.HY_DP_CPojo;
import swzzmodeserver.workserver.pojo.swzzflood.ZhuantiPojo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/SWZZ_FLOOD_HY_DP_C")
public class HY_DP_CController {
    @Autowired
    private HY_DP_CData data;

    @RequestMapping("/selectListByPage")
    public ResultUtils selectListByPage(@RequestBody ParamField paramField){
        StopWatch watch = new StopWatch();
        watch.start();
        String key = "",stime = "",etime = "";
        Integer pageIndex = null,pageSize = null;
        List<String> idList = new ArrayList<>();
        List<String> prcdList = new ArrayList<>();
        ParamField params = FieldIsValid.getColumnName(paramField,ParamField.class);
        if(CommonUtills.isEmpty(params)){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if (null != paramField.getStcd()){
            idList = Arrays.asList(paramField.getStcd().split(","));
        }
        if (null != paramField.getKwtxt()){
            key = paramField.getKwtxt();
        }
        if (null != paramField.getStartdate()){
            stime = paramField.getStartdate();
        }
        if (null != paramField.getEnddate()){
            etime = paramField.getEnddate();
        }
        if (null != paramField.getStrExp()){
            prcdList = Arrays.asList(paramField.getStrExp().split(","));
        }
        if (null != paramField.getPageindex() && null != paramField.getPagesize()){
            pageIndex = Integer.parseInt(paramField.getPageindex());
            pageSize = Integer.parseInt(paramField.getPagesize());
            PageHelper.startPage(pageIndex,pageSize);
            List<HY_DP_CPojo> hyList = data.selectListByPage(key, stime, etime, prcdList, idList);
            watch.stop();
            PageInfo<HY_DP_CPojo> pageInfo = new PageInfo<>(hyList);
            if (pageInfo.getList().size() > 0){
                return new ResultUtils<List>(pageInfo.getList(),"操作成功",true,pageInfo.getSize(),pageInfo.getPageNum(),pageInfo.getPages(),pageInfo.getTotal(),hyList.size(),watch.getTime());
            }else {
                return new ResultUtils<List>(null,"操作成功",false,pageInfo.getSize(),pageInfo.getPageNum(),pageInfo.getPages(),pageInfo.getTotal(),hyList.size(),watch.getTime());
            }
        }
        List<HY_DP_CPojo> hyList = data.selectListByPage(key, stime, etime, prcdList, idList);
        watch.stop();
        if (hyList.size() > 0){
            return new ResultUtils<List>(hyList,"操作成功",true,hyList.size(),watch.getTime());
        }else {
            return new ResultUtils<List>(null,"操作成功",false,hyList.size(),watch.getTime());
        }
    }
}
