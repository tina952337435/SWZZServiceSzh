package swzzmodeserver.workserver.controller.swzzdata;

import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swzzmodeserver.tools.*;
import swzzmodeserver.workserver.data.swzzdata.EmployeeData;
import swzzmodeserver.workserver.data.swzzdata.Sys_logData;
import swzzmodeserver.workserver.data.swzzmode.BDMS_PREDICTData;
import swzzmodeserver.workserver.pojo.swzzdata.EmployeeGetTokenPojo;
import swzzmodeserver.workserver.pojo.swzzdata.EmployeePojo;
import swzzmodeserver.workserver.pojo.swzzdata.Sys_logPojo;
import swzzmodeserver.workserver.pojo.swzzmode.BDMS_PREDICTPojo;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/SWZZ_DATA_employee")
public class EmployeeController {
    @Autowired
    private EmployeeData data;

    @Autowired
    private Sys_logData logData;
    @Autowired
    private CommonUtills commonUtills;

    @Value("${file.path.templatefilepath}")
    private String filePathName;

    @RequestMapping("/findResult")
    public ResultUtils findResult(@RequestBody ParamField bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String ID = "";
        String key = "";
        String pageindex = "";
        String pagesize = "10";
        String qx_login = "";
        List<String> type = new ArrayList<>();
        String stime = ""//new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date().getTime() - 24 * 60 * 60 * 1000)
                ,etime = "";
        ParamField params = FieldIsValid.getColumnName(bpPojo,ParamField.class);
        if(CommonUtills.isEmpty(params)){
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
        if(null != bpPojo.getStrExp()){
            qx_login =bpPojo.getStrExp();
        }
        Integer startindex = null;
        if(!"".equals(pageindex) && !"".equals(pagesize)){
            startindex = (Integer.parseInt(pageindex) - 1) * Integer.parseInt(pagesize);
        }
        List<EmployeePojo> fxList = data.selectList(ID,qx_login,null,key,stime,etime,type,startindex, Integer.valueOf(pagesize));
        Integer integer = data.selectCount(ID,qx_login,key,stime,etime,type);
        Integer count = 1;
        if(!"".equals(pagesize)){
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
    public ResultUtils add(@RequestBody EmployeePojo bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        EmployeePojo params = FieldIsValid.getColumnName(bpPojo,EmployeePojo.class);
        if(CommonUtills.isEmpty(params)){
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
    public ResultUtils modify(@RequestBody EmployeePojo bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        EmployeePojo params = FieldIsValid.getColumnName(bpPojo,EmployeePojo.class);
        if(CommonUtills.isEmpty(params)){
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
        ParamField params = FieldIsValid.getColumnName(bpPojo,ParamField.class);
        if(CommonUtills.isEmpty(params)){
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

    @RequestMapping("/LOGINSel")
    public ResultUtils LOGINSel(@RequestBody ParamFields bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String name = "",password = "";
        ParamFields params = FieldIsValid.getColumnName(bpPojo,ParamFields.class);
        if(CommonUtills.isEmpty(params)){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if(null != bpPojo.getQX_LOGIN()){
            name = bpPojo.getQX_LOGIN();
        }
        if(null != bpPojo.getQX_PASSWORD()){
            password = bpPojo.getQX_PASSWORD();
        }
        List<EmployeePojo> pojoList = new ArrayList<>();
        
        // System.out.print("登录参数bpPojo::::::"+bpPojo+"\n");
        if(!name.equals("")){
            pojoList= data.selectListPWD("", name,password,"", "", "", null, null, null);
        }
        watch.stop();
        if(pojoList.size() > 0){
            // System.out.print("name"+name+":password"+password+"\n");
            // System.out.print("pojoList"+pojoList);
            String ps = pojoList.get(0).getQX_PASSWORD();//加密
            String psy = pojoList.get(0).getQX_YPASSWORD();//没加密
            
            if(password.equals(ps)){
                // System.out.print("加密密码ps"+ps);
                pojoList.get(0).setQX_PASSWORD(null);
                pojoList.get(0).setQX_YPASSWORD(null);
                String token=JwtUtil.createToken(pojoList.get(0).getQX_LOGIN(),psy);
                // System.out.print("没加密密码psy"+psy);
                pojoList.get(0).setTOKEN(token);                
                return new ResultUtils<>(pojoList.get(0), "操作成功",true, pojoList.size(),watch.getTime());
            }
        }
        return new ResultUtils<>(null, "操作成功",false, pojoList.size(),watch.getTime());
    }

    @RequestMapping(value = "/GetToken")
    public Map<String, Object> GetToken(@RequestBody ParamFields param){
        new   javalog().writelog("进入GetToken***********************",filePathName);
        List<EmployeePojo> pojoList=new ArrayList<>();
        Map<String,Object> mp = new HashMap<>();
        String resMsg = "失败";
        String token="";
        try{
            if(param.getUsername()!=null&&param.getPwd()!=null){
                new   javalog().writelog("进入GetToken,参数是：用户名"+param.getUsername()+"，密码"+param.getPwd(),filePathName);
                pojoList = data.selectList("", param.getUsername(),param.getPwd(), "", "", "", null, null, null);
                if(pojoList.size() > 0){
                    new   javalog().writelog("进入GetToken,pojoList的长度是"+pojoList.size(),filePathName);

                    resMsg = "成功";
                    token=JwtUtil.createToken(param.getUsername(),param.getPwd());
                    new   javalog().writelog("进入GetToken,token的值是"+token,filePathName);    
                    Date date = new Date(System.currentTimeMillis());
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String str = sdf.format(date);

                    Sys_logPojo pojo = new Sys_logPojo();
                    pojo.setEMP_ID(CommonUtills.getUUid());
                    pojo.setLOGO_DATE(str);
                    pojo.setEMP_NAME(pojoList.get(0).getNAME());
                    pojo.setSTATUS("登录成功");
                    pojo.setNAME(pojoList.get(0).getQX_LOGIN());
                    pojo.setFLAG("平台");
                    logData.insertOne(pojo);
                }
            }
        }
        catch (Exception e){
            new   javalog().writelog("进入GetToken,错误信息:"+e.getMessage(),filePathName);    
        }
        
        return  commonUtills.returnJsonToken("select",pojoList.size(),token,resMsg);
    }
}
