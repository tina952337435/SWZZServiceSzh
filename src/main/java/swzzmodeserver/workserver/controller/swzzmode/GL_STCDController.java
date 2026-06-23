package swzzmodeserver.workserver.controller.swzzmode;

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
import swzzmodeserver.workserver.data.swzzmode.GL_STCDData;
import swzzmodeserver.workserver.pojo.swzzflood.ZhuantiPojo;
import swzzmodeserver.workserver.pojo.swzzmode.BDMS_PREDICTPojo;
import swzzmodeserver.workserver.pojo.swzzmode.GL_STCDPojo;
import swzzmodeserver.workserver.pojo.swzzmode.GL_STCDStnmPojo;
import swzzmodeserver.workserver.pojo.swzzmode.ST_STRVFCCH_SKBPojo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/SWZZ_MODE_GL_STCD")
public class GL_STCDController {
    @Autowired
    private GL_STCDData data;

    @RequestMapping("/findResult")
    public ResultUtils findResult(@RequestBody ParamField bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String NAME = "",STTP = "";
        Integer pageindex = null,pagesize = null;
        List<String> IDList = new ArrayList<>();
        List<String> STCDList = new ArrayList<>();
        List<String> type = new ArrayList<>();
        String stime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date().getTime() - 24 * 60 * 60 * 1000),etime = "";
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo,ParamField.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if(null != bpPojo.getTreeID()&&!bpPojo.getTreeID().equals("")){
            IDList = Arrays.asList(bpPojo.getTreeID().split(","));
        }
        if(null != bpPojo.getStcd()&&!bpPojo.getStcd().equals("")){
            STCDList = Arrays.asList(bpPojo.getStcd().split(","));
        }
        if(null != bpPojo.getName()){
            NAME = bpPojo.getName();
        }
        if(null != bpPojo.getSttp()){
            STTP = bpPojo.getSttp();
        }
        if(null != bpPojo.getPattem()){
            type = Arrays.asList(bpPojo.getPattem().split(","));
        }
        if (null != bpPojo.getPageindex() && null != bpPojo.getPagesize()){
            pageindex = Integer.parseInt(bpPojo.getPageindex());
            pagesize = Integer.parseInt(bpPojo.getPagesize());
            PageHelper.startPage(pageindex,pagesize);
            List<GL_STCDPojo> list = data.selectList(IDList,STCDList,NAME,type,STTP);
            watch.stop();
            PageInfo<GL_STCDPojo> pageInfo = new PageInfo<>(list);
            if (pageInfo.getList().size() > 0){
                return new ResultUtils<List>(pageInfo.getList(),"操作成功",true,pageInfo.getSize(),pageInfo.getPageNum(),pageInfo.getPages(),pageInfo.getTotal(),list.size(),watch.getTime());
            }else {
                return new ResultUtils<List>(null,"操作成功",false,pageInfo.getSize(),pageInfo.getPageNum(),pageInfo.getPages(),pageInfo.getTotal(),list.size(),watch.getTime());
            }
        }
        List<GL_STCDPojo> zhuantiList = data.selectList(IDList,STCDList,NAME,type,STTP);
        watch.stop();
        if (zhuantiList.size() > 0){
            return new ResultUtils<List>(zhuantiList,"操作成功",true,zhuantiList.size(),watch.getTime());
        }else {
            return new ResultUtils<List>(null,"操作成功",false,zhuantiList.size(),watch.getTime());
        }
    }
    @RequestMapping("/add")
    public ResultUtils<Integer> add(@RequestBody GL_STCDPojo bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo,GL_STCDPojo.class))){
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
    public ResultUtils<Integer> modify(@RequestBody GL_STCDPojo bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo,GL_STCDPojo.class))){
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
        Integer num = data.deleteOne(ID);
        watch.stop();
        if(num > 0){
            return new ResultUtils<>(num, "操作成功",true, num,watch.getTime());
        }else {
            return new ResultUtils<>(num, "操作成功",false, num,watch.getTime());
        }
    }

    @RequestMapping("/batchResult/{pattem}")
    public ResultUtils batchResult(@PathVariable("pattem") String pattem, @RequestBody List<GL_STCDPojo> bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String type = "";
        if(CommonUtills.isEmpty(FieldIsValid.getListColumnName(bpPojo, GL_STCDPojo.class)) || !FieldIsValid.isValid(pattem)){
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
        List<GL_STCDPojo> list = new ArrayList<>();
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


    @RequestMapping("/findResultStnm")
    public ResultUtils findResultStnm(@RequestBody ParamField bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String NAME = "",STTP = "";
        Integer pageindex = null,pagesize = null;
        List<String> IDList = new ArrayList<>();
        List<String> STCDList = new ArrayList<>();
        List<String> type = new ArrayList<>();
        String stime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date().getTime() - 24 * 60 * 60 * 1000),etime = "";
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo,ParamField.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if(null != bpPojo.getTreeID()&&!bpPojo.getTreeID().equals("")){
            IDList = Arrays.asList(bpPojo.getTreeID().split(","));
        }
        if(null != bpPojo.getStcd()&&!bpPojo.getStcd().equals("")){
            STCDList = Arrays.asList(bpPojo.getStcd().split(","));
        }
        if(null != bpPojo.getName()){
            NAME = bpPojo.getName();
        }
        if(null != bpPojo.getSttp()){
            STTP = bpPojo.getSttp();
        }
        if(null != bpPojo.getPattem()){
            type = Arrays.asList(bpPojo.getPattem().split(","));
        }
        if (null != bpPojo.getPageindex() && null != bpPojo.getPagesize()){
            pageindex = Integer.parseInt(bpPojo.getPageindex());
            pagesize = Integer.parseInt(bpPojo.getPagesize());
            PageHelper.startPage(pageindex,pagesize);
            List<GL_STCDStnmPojo> list = data.selectListNew(IDList,STCDList,NAME,type,STTP);
            watch.stop();
            PageInfo<GL_STCDStnmPojo> pageInfo = new PageInfo<>(list);
            if (pageInfo.getList().size() > 0){
                return new ResultUtils<List>(pageInfo.getList(),"操作成功",true,pageInfo.getSize(),pageInfo.getPageNum(),pageInfo.getPages(),pageInfo.getTotal(),list.size(),watch.getTime());
            }else {
                return new ResultUtils<List>(null,"操作成功",false,pageInfo.getSize(),pageInfo.getPageNum(),pageInfo.getPages(),pageInfo.getTotal(),list.size(),watch.getTime());
            }
        }
        List<GL_STCDStnmPojo> zhuantiList = data.selectListNew(IDList,STCDList,NAME,type,STTP);
        watch.stop();
        if (zhuantiList.size() > 0){
            return new ResultUtils<List>(zhuantiList,"操作成功",true,zhuantiList.size(),watch.getTime());
        }else {
            return new ResultUtils<List>(null,"操作成功",false,zhuantiList.size(),watch.getTime());
        }
    }
}
