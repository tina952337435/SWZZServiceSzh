package swzzmodeserver.workserver.controller.swzzmode;

import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swzzmodeserver.tools.CommonUtills;
import swzzmodeserver.tools.FieldIsValid;
import swzzmodeserver.tools.ParamField;
import swzzmodeserver.tools.ResultUtils;
import swzzmodeserver.workserver.data.swzzmode.DD_SOLUTIONData;
import swzzmodeserver.workserver.data.swzzmode.SCHEME_TYPEData;
import swzzmodeserver.workserver.pojo.swzzmode.DD_SOLUTIONPojo;
import swzzmodeserver.workserver.pojo.swzzmode.SCHEME_TYPEDto;
import swzzmodeserver.workserver.pojo.swzzmode.SCHEME_TYPEPojo;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/SWZZ_MODE_SCHEME_TYPE")
public class SCHEME_TYPEController {
    @Autowired
    private SCHEME_TYPEData data;
    @Autowired
    private DD_SOLUTIONData ddSolutionData;

    @RequestMapping("/findResult")
    public ResultUtils findResult(@RequestBody ParamField bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String ID = "",key = "",pageindex = "",pagesize = "10";
        List<String> type = new ArrayList<>();
        String stime = ""//new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date().getTime() - 24 * 60 * 60 * 1000)
                ,etime = "";
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo,ParamField.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if(null != bpPojo.getStcd()){
            ID = bpPojo.getStcd();
        }
        if(null != bpPojo.getKwtxt()){
            key = bpPojo.getKwtxt();
        }
        if(null != bpPojo.getStartdate()){
            stime = bpPojo.getStartdate();
        }
        if(null != bpPojo.getEnddate()){
            etime = bpPojo.getEnddate();
        }
        if(null != bpPojo.getPattem()){
            type = Arrays.asList(bpPojo.getPattem().split(","));
        }
        if(null != bpPojo.getPageindex()){
            pageindex = bpPojo.getPageindex();
        }
        if(null != bpPojo.getPagesize()){
            pagesize = bpPojo.getPagesize();
        }
        Integer startindex = null;
        if(!"".equals(pageindex) && !"".equals(pagesize)){
            startindex = (Integer.parseInt(pageindex) - 1) * Integer.parseInt(pagesize);
        }
        List<SCHEME_TYPEPojo> fxList = data.selectList(ID,key,stime,etime,"","",startindex, Integer.valueOf(pagesize));
        Integer integer = data.selectCount(ID,key,stime,etime);
        Integer count = 1;
        if(null != pagesize && !"".equals(pagesize)){
            count = integer / Integer.parseInt(pagesize);
            if(integer % Integer.parseInt(pagesize) != 0){
                count += 1;
            }
        }
        watch.stop();
        if(!"".equals(pagesize) && !"".equals(pageindex)){
            if(fxList.size() > 0){
                return new ResultUtils<>(fxList, "操作成功",true,Integer.parseInt(pagesize) ,Integer.parseInt(pageindex),count,integer,fxList.size(),watch.getTime());
            }else {
                return new ResultUtils<>(fxList, "操作成功",false,Integer.parseInt(pagesize) ,Integer.parseInt(pageindex),count,integer,fxList.size(),watch.getTime());
            }
        }else {
            if(fxList.size() > 0){
                return new ResultUtils<>(fxList, "操作成功",true ,fxList.size(),watch.getTime());
            }else {
                return new ResultUtils<>(fxList, "操作成功",false,fxList.size(),watch.getTime());
            }
        }
    }
    @RequestMapping("/add")
    public ResultUtils add(@RequestBody SCHEME_TYPEPojo bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo,SCHEME_TYPEPojo.class))){
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
    public ResultUtils modify(@RequestBody SCHEME_TYPEPojo bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo,SCHEME_TYPEPojo.class))){
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
    public ResultUtils remove(@RequestBody ParamField bpPojo){
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
    @RequestMapping("/MODE_SCHEME_TYPEContSel")
    public ResultUtils MODE_SCHEME_TYPEContSel(@RequestBody ParamField bpPojo) throws InvocationTargetException, IllegalAccessException {
        StopWatch watch = new StopWatch();
        watch.start();
        String sDate = "",eDate = "",Kwtxt = "",pagesize = "",pageindex = "";
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo,ParamField.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if(null != bpPojo.getStartdate()){
            sDate = bpPojo.getStartdate();
        }
        if(null != bpPojo.getEnddate()){
            eDate = bpPojo.getEnddate();
        }
        if(null != bpPojo.getKwtxt()){
            Kwtxt = bpPojo.getKwtxt();
        }
        if(null != bpPojo.getPagesize()){
            pagesize = bpPojo.getPagesize();
        }
        if(null != bpPojo.getPageindex()){
            pageindex = bpPojo.getPageindex();
        }
        Integer startindex = null;
        if(!"".equals(pageindex) && !"".equals(pagesize)){
            startindex = (Integer.parseInt(pageindex) - 1) * Integer.parseInt(pagesize);
        }
        List<SCHEME_TYPEPojo> userList = data.selectList("", Kwtxt, sDate, eDate,"","", startindex, Integer.valueOf(pagesize));
        List<String> idlist = userList.stream().map(SCHEME_TYPEPojo::getID).collect(Collectors.toList());
        List<DD_SOLUTIONPojo> userListDD = ddSolutionData.selectList("", "",idlist,null, sDate, eDate, null, null);
        List<SCHEME_TYPEDto> typeDtoList = new ArrayList<>();
        for(SCHEME_TYPEPojo obj : userList){
            SCHEME_TYPEDto dto = new SCHEME_TYPEDto();
            BeanUtils.copyProperties(obj,dto);
            List<DD_SOLUTIONPojo> collect = userListDD.stream().filter(m -> m.getDD_MIND().equals(obj.getID())).collect(Collectors.toList());
            dto.setNum(collect.size());
            typeDtoList.add(dto);
        }
        watch.stop();
        if(typeDtoList.size() > 0){
            return new ResultUtils<>(typeDtoList, "操作成功",true, typeDtoList.size(),watch.getTime());
        }else {
            return new ResultUtils<>(typeDtoList, "操作成功",false, typeDtoList.size(),watch.getTime());
        }
    }
}
