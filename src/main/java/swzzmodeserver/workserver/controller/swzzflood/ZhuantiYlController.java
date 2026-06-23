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
import swzzmodeserver.workserver.data.swzzflood.ZhuantiYlData;
import swzzmodeserver.workserver.pojo.swzzflood.ZhuantiYlPojo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/SWZZ_FLOOD_ZhuantiYl")
public class ZhuantiYlController {
    @Autowired
    private ZhuantiYlData data;

    @RequestMapping("/findResult")
    public ResultUtils select(@RequestBody ParamField paramField){
        StopWatch watch = new StopWatch();
        watch.start();
        List<String> stcdList = new ArrayList<>();
        String stime = null, etime = null, pathname = null;
        ParamField params = FieldIsValid.getColumnName(paramField, ParamField.class);
        if(CommonUtills.isEmpty(params)){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if (null != paramField.getStcd()){
            stcdList = Arrays.asList(paramField.getStcd().split(","));
        }
        if (null != paramField.getStartdate()){
            stime = paramField.getStartdate();
        }
        if (null != paramField.getEnddate()){
            etime = paramField.getEnddate();
        }
        if (null != paramField.getKwtxt()){
            pathname = paramField.getKwtxt();
        }
        Integer pageIndex = null, pageSize = null;
        if (null != paramField.getPageindex() && null != paramField.getPagesize()){
            pageIndex = Integer.parseInt(paramField.getPageindex());
            pageSize = Integer.parseInt(paramField.getPagesize());
            PageHelper.startPage(pageIndex, pageSize);
            List<ZhuantiYlPojo> list = data.selectList(stcdList,stime,etime,pathname);
            watch.stop();
            PageInfo<ZhuantiYlPojo> pageInfo = new PageInfo<>(list);
            if (pageInfo.getList().size() > 0){
                return new ResultUtils<List>(pageInfo.getList(),"操作成功",true,pageInfo.getSize(),pageInfo.getPageNum(),pageInfo.getPages(),pageInfo.getTotal(),list.size(),watch.getTime());
            }else {
                return new ResultUtils<List>(null,"操作成功",false,pageInfo.getSize(),pageInfo.getPageNum(),pageInfo.getPages(),pageInfo.getTotal(),list.size(),watch.getTime());
            }
        }
        List<ZhuantiYlPojo> list = data.selectList(stcdList,stime,etime,pathname);
        watch.stop();
        if (list.size() > 0){
            return new ResultUtils<List>(list,"操作成功",true,list.size(),watch.getTime());
        }else {
            return new ResultUtils<List>(list,"操作成功",false,list.size(),watch.getTime());
        }
    }

    @RequestMapping("/add")
    public ResultUtils add(@RequestBody ZhuantiYlPojo pojo){
        StopWatch watch = new StopWatch();
        watch.start();
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(pojo, ZhuantiYlPojo.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        Integer num = data.insertOne(pojo);
        watch.stop();
        if(num > 0){
            return new ResultUtils<>(num, "操作成功",true, num,watch.getTime());
        }else {
            return new ResultUtils<>(num, "操作成功",false, num,watch.getTime());
        }
    }

    @RequestMapping("/modify")
    public ResultUtils modify(@RequestBody ZhuantiYlPojo pojo){
        StopWatch watch = new StopWatch();
        watch.start();
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(pojo, ZhuantiYlPojo.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        Integer num = data.upDateOne(pojo);
        watch.stop();
        if(num > 0){
            return new ResultUtils<>(num, "操作成功",true, num,watch.getTime());
        }else {
            return new ResultUtils<>(num, "操作成功",false, num,watch.getTime());
        }
    }

    @RequestMapping("/remove")
    public ResultUtils remove(@RequestBody ParamField paramField){
        StopWatch watch = new StopWatch();
        watch.start();
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(paramField, ParamField.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        String stcd = paramField.getStcd() != null ? paramField.getStcd() : "";
        String tm = paramField.getStrExp() != null ? paramField.getStrExp() : "";
        Integer num = data.deleteOne(stcd, tm);
        watch.stop();
        if(num > 0){
            return new ResultUtils<>(num, "操作成功",true, num,watch.getTime());
        }else {
            return new ResultUtils<>(num, "操作成功",false, num,watch.getTime());
        }
    }

    @RequestMapping("/batchResult/{pattem}")
    public ResultUtils batchResult(@PathVariable("pattem") String pattem, @RequestBody List<ZhuantiYlPojo> pojoList){
        StopWatch watch = new StopWatch();
        watch.start();
        if(CommonUtills.isEmpty(FieldIsValid.getListColumnName(pojoList, ZhuantiYlPojo.class)) || !FieldIsValid.isValid(pattem)){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        Integer num = 0;
        int count = 80;
        int number = pojoList.size() / count;
        if (pojoList.size() % count != 0){
            number += 1;
        }
        List<ZhuantiYlPojo> list = new ArrayList<>();
        for(int i=0;i<number;i++){
            if(i == number - 1){
                list = pojoList.subList(count * i, pojoList.size());
            }else {
                list = pojoList.subList(count * i, count * (i + 1));
            }
            if("true".equals(pattem)){
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
    public ResultUtils batchRemove(@RequestBody ParamField paramField){
        StopWatch watch = new StopWatch();
        watch.start();
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(paramField, ParamField.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        List<String> stcdList = new ArrayList<>();
        if (paramField.getStcd() != null){
            stcdList = Arrays.asList(paramField.getStcd().split(","));
        }
        Integer num = 0;
        int count = 80;
        int number = stcdList.size() / count;
        if (stcdList.size() % count != 0){
            number += 1;
        }
        List<String> list = new ArrayList<>();
        for(int i=0;i<number;i++){
            if(i == number - 1){
                list = stcdList.subList(count * i, stcdList.size());
            }else {
                list = stcdList.subList(count * i, count * (i + 1));
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