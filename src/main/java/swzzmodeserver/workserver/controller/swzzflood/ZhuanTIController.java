package swzzmodeserver.workserver.controller.swzzflood;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swzzmodeserver.tools.CommonUtills;
import swzzmodeserver.tools.FieldIsValid;
import swzzmodeserver.tools.ParamField;
import swzzmodeserver.tools.ResultUtils;
import swzzmodeserver.workserver.data.swzzflood.ZhuantiData;
import swzzmodeserver.workserver.pojo.swzzflood.ST_TIDEHL_RPojo;
import swzzmodeserver.workserver.pojo.swzzflood.ZhuantiPojo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/SWZZ_FLOOD_ZhuanTi")
public class ZhuanTIController {
    @Autowired
    private ZhuantiData data;

    @RequestMapping("/selectZhuanTiList")
    public ResultUtils selectZhuanTiList(@RequestBody ParamField paramField){
        StopWatch watch = new StopWatch();
        watch.start();
        String ddwj = "";
        Integer pageIndex = null,pageSize = null;
        List<String> idList = new ArrayList<>();
        ParamField params = FieldIsValid.getColumnName(paramField,ParamField.class);
        if(CommonUtills.isEmpty(params)){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if (null != paramField.getStcd()){
            idList = Arrays.asList(paramField.getStcd().split(","));
        }
        if (null != paramField.getDdwj()){
            ddwj = paramField.getDdwj();
        }
        if (null != paramField.getPageindex() && null != paramField.getPagesize()){
            pageIndex = Integer.parseInt(paramField.getPageindex());
            pageSize = Integer.parseInt(paramField.getPagesize());
            PageHelper.startPage(pageIndex,pageSize);
            List<ZhuantiPojo> zhuantiList = data.selectZhuanTiList(idList, ddwj);
            watch.stop();
            PageInfo<ZhuantiPojo> pageInfo = new PageInfo<>(zhuantiList);
            if (pageInfo.getList().size() > 0){
                return new ResultUtils<List>(pageInfo.getList(),"操作成功",true,pageInfo.getSize(),pageInfo.getPageNum(),pageInfo.getPages(),pageInfo.getTotal(),zhuantiList.size(),watch.getTime());
            }else {
                return new ResultUtils<List>(null,"操作成功",false,pageInfo.getSize(),pageInfo.getPageNum(),pageInfo.getPages(),pageInfo.getTotal(),zhuantiList.size(),watch.getTime());
            }
        }
        List<ZhuantiPojo> zhuantiList = data.selectZhuanTiList(idList, ddwj);
        watch.stop();
        if (zhuantiList.size() > 0){
            return new ResultUtils<List>(zhuantiList,"操作成功",true,zhuantiList.size(),watch.getTime());
        }else {
            return new ResultUtils<List>(null,"操作成功",false,zhuantiList.size(),watch.getTime());
        }
    }

    @RequestMapping("/add")
    public ResultUtils add(@RequestBody ZhuantiPojo bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo, ZhuantiPojo.class))){
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
    public ResultUtils modify(@RequestBody ZhuantiPojo bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo, ZhuantiPojo.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        Integer num = data.upDateOne(bpPojo);
        watch.stop();
        if(num > 0){
            return new ResultUtils<>(num, "操作成功",true, num,watch.getTime());
        }else {
            return new ResultUtils<>(num, "操作成功",false, num,watch.getTime());
        }
    }
    @RequestMapping("/remove")
    public ResultUtils remove(@RequestBody ParamField bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String ID = "";
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo, ParamField.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if(null != bpPojo.getStcd()){
            ID = bpPojo.getStcd();
        }
        Integer num = data.deleteOne(ID);
        watch.stop();
        if(num > 0){
            return new ResultUtils<>(num, "操作成功",true, num,watch.getTime());
        }else {
            return new ResultUtils<>(num, "操作成功",false, num,watch.getTime());
        }
    }

    @RequestMapping("/batchResult/{pattem}")
    public ResultUtils batchResult(@PathVariable("pattem") String pattem, @RequestBody List<ZhuantiPojo> bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String type = "";
        if(CommonUtills.isEmpty(FieldIsValid.getListColumnName(bpPojo, ZhuantiPojo.class)) || !FieldIsValid.isValid(pattem)){
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
        List<ZhuantiPojo> list = new ArrayList<>();
        for(int i=0;i<number;i++){
            if(i == number - 1){
                list = bpPojo.subList(count * i,bpPojo.size());
            }else {
                list = bpPojo.subList(count * i,count * (i + 1));
            }
            if(type.equals("true") ){
                num += data.upDateAll(list);
            }else {
                num += data.insertAll(list);
            }
        }
        watch.stop();
        if(num > 0){
            return new ResultUtils<>(num, "操作成功",true, num,watch.getTime());
        }else {
            return new ResultUtils<>(num, "操作成功",false, num,watch.getTime());
        }
    }

    @RequestMapping("/batchRemove")
    public ResultUtils batchRemove(@RequestBody ParamField bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo, ParamField.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        List<String> stringList = new ArrayList<>();
        if (bpPojo.getStcd() != null){
            stringList = Arrays.asList(bpPojo.getStcd().split(","));
        }
        Integer num = 0;
        int count = 80;
        int number = stringList.size() / count;
        if (stringList.size() % count != 0){
            number += 1;
        }
        List<String> list = new ArrayList<>();
        for(int i=0;i<number;i++){
            if(i == number - 1){
                list = stringList.subList(count * i,stringList.size());
            }else {
                list = stringList.subList(count * i,count * (i + 1));
            }
            num += data.deleteAll(list);
        }
        watch.stop();
        if(num > 0){
            return new ResultUtils<>(num, "操作成功",true, num,watch.getTime());
        }else {
            return new ResultUtils<>(num, "操作成功",false, num,watch.getTime());
        }
    }
}
