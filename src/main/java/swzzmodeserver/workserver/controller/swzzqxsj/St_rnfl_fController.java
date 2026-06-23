package swzzmodeserver.workserver.controller.swzzqxsj;

import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import swzzmodeserver.tools.*;
import swzzmodeserver.workserver.data.swzzmode.ES_SLTONGJIData;
import swzzmodeserver.workserver.data.swzzmode.ES_ZHANDIANDATAData;
import swzzmodeserver.workserver.data.swzzmode.ES_ZHANDIANData;
import swzzmodeserver.workserver.data.swzzmode.ES_ZHANGUANLIANData;
import swzzmodeserver.workserver.data.swzzqxsj.St_rnfl_fData;
import swzzmodeserver.workserver.pojo.swzzmode.ES_SLTONGJIPojo;
import swzzmodeserver.workserver.pojo.swzzmode.ES_ZHANDIANDATAPojo;
import swzzmodeserver.workserver.pojo.swzzmode.ES_ZHANDIANPojo;
import swzzmodeserver.workserver.pojo.swzzmode.ES_ZHANGUANLIANPojo;
import swzzmodeserver.workserver.pojo.swzzqxsj.St_rnfl_fPojo;
import swzzmodeserver.workserver.service.swzzqxsj.St_rnfl_fService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/SWZZ_QXSJ_st_rnfl_f")
public class St_rnfl_fController {
    @Autowired
    private St_rnfl_fData data;
    @Autowired
    private ES_ZHANGUANLIANData esZhanguanlianData;
    @Autowired
    private ES_ZHANDIANData esZhandianData;
    @Autowired
    private ES_ZHANDIANDATAData esZhandiandataData;
    @Autowired
    private St_rnfl_fService service;
    @Autowired
    private ES_SLTONGJIData esSltongjiData;

    @RequestMapping("/findResult")
    public ResultUtils findResult(@RequestBody ParamField bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String ID = "",key = "",pageindex = "",pagesize = "10",ybtm = "";
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
        if(null != bpPojo.getStrExp()){
            ybtm = bpPojo.getStrExp();
        }
        Integer startindex = null;
        if(!"".equals(pageindex) && !"".equals(pagesize)){
            startindex = (Integer.parseInt(pageindex) - 1) * Integer.parseInt(pagesize);
        }
        List<St_rnfl_fPojo> fxList = data.selectList(ID,key,stime,etime,type,ybtm,startindex, Integer.valueOf(pagesize));
        Integer integer = data.selectCount(ID,key,stime,etime,type,ybtm);
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
    public ResultUtils add(@RequestBody St_rnfl_fPojo bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo,St_rnfl_fPojo.class))){
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
    public ResultUtils modify(@RequestBody St_rnfl_fPojo bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo,St_rnfl_fPojo.class))){
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
        String ID = "",ID2 = "",ID3 = "";
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo,ParamField.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if(null != bpPojo.getStcd()){
            ID = bpPojo.getStcd();
        }
        if(null != bpPojo.getKID1()){
            ID2 = bpPojo.getKID1();
        }
        if(null != bpPojo.getKID2()){
            ID3 = bpPojo.getKID2();
        }
        Integer num = data.deleteOne(ID,ID2,ID3);
        watch.stop();
        if(num > 0){
            return new ResultUtils<>(num, "操作成功",true, num,watch.getTime());
        }else {
            return new ResultUtils<>(num, "操作成功",false, num,watch.getTime());
        }
    }

    @RequestMapping("/batchResult/{pattem}")
    public ResultUtils batchResult(@PathVariable("pattem") String pattem, @RequestBody List<St_rnfl_fPojo> bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String type = "";
        if(CommonUtills.isEmpty(FieldIsValid.getListColumnName(bpPojo,St_rnfl_fPojo.class)) || !FieldIsValid.isValid(pattem)){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if(null != pattem){
            type = pattem;
        }
        Integer num = 0;
        int count = 80;
        int number = bpPojo.size() / count;
        if(bpPojo.size() % count != 0){
            number = number + 1;
        }
        List<St_rnfl_fPojo> list = new ArrayList<>();
        for(int i = 0;i < number;i++){
            if(i == number - 1){
                list = bpPojo.subList(count * i,bpPojo.size());
            }else {
                list = bpPojo.subList(count * i,count * ( i + 1));
            }
            if("true".equals(type)){
                num += -data.updateALL(list);
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

    @RequestMapping("/batchRemove")
    public ResultUtils batchRemove(String pattem,@RequestBody List<ParamField> bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        if(CommonUtills.isEmpty(FieldIsValid.getListColumnName(bpPojo,ParamField.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        Integer num = 0;
        int count = 80;
        int number = bpPojo.size() / count;
        if(bpPojo.size() % count != 0){
            number = number + 1;
        }
        List<ParamField> list = new ArrayList<>();
        for(int i = 0;i < number;i++){
            if(i == number - 1){
                list = bpPojo.subList(count * i,bpPojo.size());
            }else {
                list = bpPojo.subList(count * i,count * ( i + 1));
            }
            num += data.deleteALL(list);
        }
        watch.stop();
        if(num > 0){
            return new ResultUtils<>(num, "操作成功",true, num,watch.getTime());
        }else {
            return new ResultUtils<>(num, "操作成功",false, num,watch.getTime());
        }
    }


    @RequestMapping("/MODIFYJYYBBYXH")
    public ResultUtils MODIFYJYYBBYXH (@RequestBody ParamFields bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String startdate = format.format(new Date(new Date().getTime() - 3 * 24 * 60 * 60 * 1000)),
                enddate = format.format(new Date()),solutionid = "";
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo,ParamFields.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if(null != bpPojo.getStartdate()){
            startdate = bpPojo.getStartdate();
        }
        if(null != bpPojo.getEnddate()){
            enddate = bpPojo.getEnddate();
        }
        if(null != bpPojo.getSolutionid()){
            solutionid = bpPojo.getSolutionid();
        }
        Date rldate = null;
        try {
            rldate = format.parse(startdate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        List<St_rnfl_fPojo> rnflFList = data.selectByHourHX(startdate, enddate,
                format.format(new Date(rldate.getTime() - 3 * 24 * 60 * 60 * 1000)),
                startdate, Collections.singletonList(""));
        List<St_rnfl_fPojo> newRnflFList = new ArrayList<>();//生成站每小时一条的容器
        rnflFList.forEach(m->{
            int iniv = m.getINTV().intValue();
            for(int i = iniv;i >= 0;i--){
                St_rnfl_fPojo stRnflFPojo = new St_rnfl_fPojo();
                Double zfp = null;
                if(null != m.getDRP()){
                    zfp = Double.valueOf(String.format("%.3f",m.getDRP() / iniv));
                }
                stRnflFPojo.setSTCD(m.getSTCD());
                stRnflFPojo.setYBTM(m.getYBTM());
                try {
                    stRnflFPojo.setTM(format.format(new Date(format.parse(m.getTM()).getTime() - i * 60 * 60 * 1000)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                stRnflFPojo.setAIRPRESSURE(m.getAIRPRESSURE());
                stRnflFPojo.setDRP(zfp);
                stRnflFPojo.setINTV(m.getINTV());
                stRnflFPojo.setTEMP(m.getTEMP());
                stRnflFPojo.setHUMIDITY(m.getHUMIDITY());
                stRnflFPojo.setWINDDIR(m.getWINDDIR());
                stRnflFPojo.setWINDSPEED(m.getWINDSPEED());
                stRnflFPojo.setWEATHERCODE(m.getWEATHERCODE());
                stRnflFPojo.setAIRPRESSURE(m.getAIRPRESSURE());
                stRnflFPojo.setTYPE(m.getTYPE());
                newRnflFList.add(stRnflFPojo);
            }
        });
        Integer num = 0;
        List<ES_ZHANGUANLIANPojo> listDataGuan = esZhanguanlianData.selectList("", null, null, Collections.singletonList("0"));
        if(rnflFList.size() >0){
            List<ES_ZHANDIANDATAPojo> zhandiandataList = new ArrayList<>();
            List<ES_ZHANDIANPojo> listZhan = esZhandianData.selectList("", null, null, Collections.singletonList("0"),"");
            List<String> zhanidList = listZhan.stream().map(ES_ZHANDIANPojo::getZHANID).collect(Collectors.toList());
            List<ES_ZHANDIANDATAPojo> listZhanData = esZhandiandataData.selectList("", null, null, solutionid,zhanidList,null,null);
//            listZhanData = listZhanData.stream().filter(m->{
//                if(m.getZHANID() != null){
//                    return zhanidList.contains(m.getZHANID());
//                }
//                return false;
//            }).collect(Collectors.toList());
            //List<String> zhanTimeList = listZhanData.stream().map(ES_ZHANDIANDATAPojo::getZHANTIME).collect(Collectors.toList());
            List<ES_ZHANDIANDATAPojo> listZhanDataCopy = listZhanData;
            String finalSolutionid = solutionid;
            listZhanData.forEach(m->{
                List<ES_ZHANGUANLIANPojo> listDataGuanTemp = listDataGuan.stream().filter(n->{
                    if(n.getZHANID() != null){
                        return n.getZHANID().equals(m.getZHANID());
                    }
                    return false;
                }).collect(Collectors.toList());
                List<St_rnfl_fPojo> stRnflFList = new ArrayList<>();
                if(listDataGuanTemp.size() > 0){
                    stRnflFList = newRnflFList.stream().filter(b->{
                        if(b.getTM() != null && b.getSTCD() != null){
                            return b.getTM().equals(m.getZHANTIME()) && b.getSTCD().equals(listDataGuanTemp.get(0).getSTCD());
                        }
                        return false;
                     }).collect(Collectors.toList());
                }
                if(stRnflFList.size() > 0){
                    double ZFP = stRnflFList.get(0).getDRP();
                    List<ES_ZHANDIANDATAPojo> temp = listZhanDataCopy.stream().filter(i->{
                        if(i.getZHANTIME() != null && i.getZHANID() != null){
                            return i.getZHANTIME().equals(m.getZHANTIME()) && i.getZHANID().equals(listDataGuanTemp.get(0).getZHANID());
                        }
                        return false;
                    }).collect(Collectors.toList());
                    if(temp.size() > 0){
                        ES_ZHANDIANDATAPojo pojo = new ES_ZHANDIANDATAPojo();
                        pojo.setID(temp.get(0).getID());
                        pojo.setZHANID(temp.get(0).getZHANID());
                        pojo.setZHANTIME(temp.get(0).getZHANTIME());
                        pojo.setZHANDATA(String.valueOf(ZFP));
                        pojo.setSOLUTIONID(finalSolutionid);
                        zhandiandataList.add(pojo);
                    }
                }else {
                    String ZFP = "0";
                    List<ES_ZHANDIANDATAPojo> temp = listZhanDataCopy.stream().filter(s->{
                        if (s.getZHANTIME() != null && s.getZHANID() != null){
                            return s.getZHANTIME().equals(m.getZHANTIME()) && s.getZHANID().equals(m.getZHANID());
                        }
                        return false;
                    }).collect(Collectors.toList());
                    List<ES_ZHANDIANDATAPojo> pojoList = zhandiandataList.stream().filter(i->{
                        if (null != i.getZHANTIME() && null != i.getZHANID()){
                            return i.getZHANTIME().equals(m.getZHANTIME()) && i.getZHANID().equals(listDataGuanTemp.get(0).getZHANID());
                        }
                        return false;
                    }).collect(Collectors.toList());
                    if(pojoList.size() == 0){
                        if(temp.size() > 0){
                            ES_ZHANDIANDATAPojo pojo = new ES_ZHANDIANDATAPojo();
                            pojo.setID(temp.get(0).getID());
                            pojo.setZHANID(temp.get(0).getZHANID());
                            pojo.setZHANTIME(temp.get(0).getZHANTIME());
                            pojo.setZHANDATA(ZFP);
                            pojo.setSOLUTIONID(finalSolutionid);
                            zhandiandataList.add(pojo);
                        }
                    }
                }
            });
            if(zhandiandataList.size() > 0){
                int count = 4000;
                int number = zhandiandataList.size() / count;
                if(zhandiandataList.size() % count != 0){
                    number = number + 1;
                }
                List<ES_ZHANDIANDATAPojo> list = new ArrayList<>();
                for(int i = 0;i < number;i++){
                    if(i == number - 1){
                        list = zhandiandataList.subList(count * i,zhandiandataList.size());
                    }else {
                        list = zhandiandataList.subList(count * i,count * ( i + 1));
                    }
                    num += esZhandiandataData.updateALL(list);
                }
            }
        }
        watch.stop();
        if(num > 0){
            return new ResultUtils<>(num, "操作成功",true, num,watch.getTime());
        }else {
            return new ResultUtils<>(num, "操作成功",false, num,watch.getTime());
        }
    }


    @RequestMapping("/MODIFYJYYBBYXH134")
    public ResultUtils MODIFYJYYBBYXH134 (@RequestBody ParamFields bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String startdate = format.format(new Date(new Date().getTime() - 3 * 24 * 60 * 60 * 1000)),
                enddate = format.format(new Date()),solutionid = "";
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo,ParamFields.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if(null != bpPojo.getStartdate()){
            startdate = bpPojo.getStartdate();
        }
        if(null != bpPojo.getEnddate()){
            enddate = bpPojo.getEnddate();
        }
        if(null != bpPojo.getSolutionid()){
            solutionid = bpPojo.getSolutionid();
        }
        Date rldate = null;
        try {
            rldate = format.parse(startdate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        List<St_rnfl_fPojo> rnflFList = data.selectByHourHX(startdate, enddate,
                format.format(new Date(rldate.getTime() - 3 * 24 * 60 * 60 * 1000)),
                startdate, Collections.singletonList(""));
        List<St_rnfl_fPojo> newRnflFList = new ArrayList<>();//生成站每小时一条的容器
        rnflFList.forEach(m->{
            int iniv = m.getINTV().intValue();
            for(int i = iniv;i >= 0;i--){
                St_rnfl_fPojo stRnflFPojo = new St_rnfl_fPojo();
                Double zfp = null;
                if(null != m.getDRP()){
                    zfp = Double.valueOf(String.format("%.3f",m.getDRP() / iniv));
                }
                stRnflFPojo.setSTCD(m.getSTCD());
                stRnflFPojo.setYBTM(m.getYBTM());
                try {
                    stRnflFPojo.setTM(format.format(new Date(format.parse(m.getTM()).getTime() - i * 60 * 60 * 1000)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                stRnflFPojo.setAIRPRESSURE(m.getAIRPRESSURE());
                stRnflFPojo.setDRP(zfp);
                stRnflFPojo.setINTV(m.getINTV());
                stRnflFPojo.setTEMP(m.getTEMP());
                stRnflFPojo.setHUMIDITY(m.getHUMIDITY());
                stRnflFPojo.setWINDDIR(m.getWINDDIR());
                stRnflFPojo.setWINDSPEED(m.getWINDSPEED());
                stRnflFPojo.setWEATHERCODE(m.getWEATHERCODE());
                stRnflFPojo.setAIRPRESSURE(m.getAIRPRESSURE());
                stRnflFPojo.setTYPE(m.getTYPE());
                newRnflFList.add(stRnflFPojo);
            }
        });
        List<ES_SLTONGJIPojo> esSltongjiList = esSltongjiData.selectList(null, "134", null, null,null);
        Integer num = 0;
        List<ES_ZHANGUANLIANPojo> listDataGuan = esZhanguanlianData.selectList("", null, null, Collections.singletonList("0"));
        if(rnflFList.size() >0){
            List<ES_ZHANDIANDATAPojo> zhandiandataList = new ArrayList<>();
            List<ES_ZHANDIANPojo> listZhan = esZhandianData.selectList("", null, null, Collections.singletonList("0"),"");
            List<String> zhanidList = listZhan.stream().map(ES_ZHANDIANPojo::getZHANID).collect(Collectors.toList());
            List<ES_ZHANDIANDATAPojo> listZhanData = esZhandiandataData.selectList("", null, null, solutionid,zhanidList,null,null);

            List<ES_ZHANDIANDATAPojo> listZhanDataCopy = listZhanData;
            String finalSolutionid = solutionid;
            listZhanData.forEach(m->{                
                List<St_rnfl_fPojo> stRnflFList = new ArrayList<>();
                List<ES_SLTONGJIPojo> esSltongjiListT=esSltongjiList.stream().filter(p->p.getSTCD().contains(m.getZHANID())).collect(Collectors.toList());

                if(esSltongjiListT.size() > 0){                    
                    List<ES_ZHANGUANLIANPojo> listDataGuanTemp = listDataGuan.stream().filter(n->{
                        if(n.getZHANID() != null){
                            return n.getZHANID().equals(esSltongjiListT.get(0).getID());
                        }
                        return false;
                    }).collect(Collectors.toList());
                    
                    System.out.println("listDataGuanTemp长度："+listDataGuanTemp.size()+"，*****esSltongjiListT长度："+esSltongjiListT.size()+"*************ZHANID："+m.getZHANID());
                    
                    if(listDataGuanTemp.size() > 0){
                        stRnflFList = newRnflFList.stream().filter(b->{
                            if(b.getTM() != null && b.getSTCD() != null){
                                return b.getTM().equals(m.getZHANTIME()) && b.getSTCD().equals(listDataGuanTemp.get(0).getSTCD());
                            }
                            return false;
                        }).collect(Collectors.toList());
                    }                    
                }
                
                if(stRnflFList.size() > 0){
                    // if(listDataGuanTemp.size()>0){                    
                        double ZFP = stRnflFList.get(0).getDRP();
                        // List<ES_ZHANDIANDATAPojo> temp = listZhanDataCopy.stream().filter(i->{
                        //     if(i.getZHANTIME() != null && i.getZHANID() != null){
                        //         return i.getZHANTIME().equals(m.getZHANTIME()) && i.getZHANID().equals(listDataGuanTemp.get(0).getZHANID());
                        //     }
                        //     return false;
                        // }).collect(Collectors.toList());
                        // if(temp.size() > 0){
                        //     ES_ZHANDIANDATAPojo pojo = new ES_ZHANDIANDATAPojo();
                        //     pojo.setID(temp.get(0).getID());
                        //     pojo.setZHANID(temp.get(0).getZHANID());
                        //     pojo.setZHANTIME(temp.get(0).getZHANTIME());
                        //     pojo.setZHANDATA(String.valueOf(ZFP));
                        //     pojo.setSOLUTIONID(finalSolutionid);
                        //     zhandiandataList.add(pojo);
                        // }

                        ES_ZHANDIANDATAPojo pojo = new ES_ZHANDIANDATAPojo();
                        pojo.setID(m.getID());
                        pojo.setZHANID(m.getZHANID());
                        pojo.setZHANTIME(m.getZHANTIME());
                        pojo.setZHANDATA(String.valueOf(ZFP));
                        pojo.setSOLUTIONID(finalSolutionid);
                        zhandiandataList.add(pojo);
                    // }
                }else {
                    String ZFP = "0";
                    ES_ZHANDIANDATAPojo pojo = new ES_ZHANDIANDATAPojo();
                    pojo.setID(m.getID());
                    pojo.setZHANID(m.getZHANID());
                    pojo.setZHANTIME(m.getZHANTIME());
                    pojo.setZHANDATA(ZFP);
                    pojo.setSOLUTIONID(finalSolutionid);
                    zhandiandataList.add(pojo);
                    // List<ES_ZHANDIANDATAPojo> temp = listZhanDataCopy.stream().filter(s->{
                    //     if (s.getZHANTIME() != null && s.getZHANID() != null){
                    //         return s.getZHANTIME().equals(m.getZHANTIME()) && s.getZHANID().equals(m.getZHANID());
                    //     }
                    //     return false;
                    // }).collect(Collectors.toList());
                    // List<ES_ZHANDIANDATAPojo> pojoList = zhandiandataList.stream().filter(i->{
                    //     if (null != i.getZHANTIME() && null != i.getZHANID()){
                    //         return i.getZHANTIME().equals(m.getZHANTIME()) && i.getZHANID().equals(listDataGuanTemp.get(0).getZHANID());
                    //     }
                    //     return false;
                    // }).collect(Collectors.toList());
                    // if(pojoList.size() == 0){
                    //     if(temp.size() > 0){
                    //         ES_ZHANDIANDATAPojo pojo = new ES_ZHANDIANDATAPojo();
                    //         pojo.setID(temp.get(0).getID());
                    //         pojo.setZHANID(temp.get(0).getZHANID());
                    //         pojo.setZHANTIME(temp.get(0).getZHANTIME());
                    //         pojo.setZHANDATA(ZFP);
                    //         pojo.setSOLUTIONID(finalSolutionid);
                    //         zhandiandataList.add(pojo);
                    //     }
                    // }
                }
            });
            if(zhandiandataList.size() > 0){
                int count = 500;
                int number = zhandiandataList.size() / count;
                if(zhandiandataList.size() % count != 0){
                    number = number + 1;
                }
                List<ES_ZHANDIANDATAPojo> list = new ArrayList<>();
                for(int i = 0;i < number;i++){
                    if(i == number - 1){
                        list = zhandiandataList.subList(count * i,zhandiandataList.size());
                    }else {
                        list = zhandiandataList.subList(count * i,count * ( i + 1));
                    }
                    num += esZhandiandataData.updateALLME(list);
                }
            }
        }
        watch.stop();
        if(num > 0){
            return new ResultUtils<>(num, "操作成功",true, num,watch.getTime());
        }else {
            return new ResultUtils<>(num, "操作成功",false, num,watch.getTime());
        }
    }



    @RequestMapping("/DATA_ST_RNFL_FDay")
    public ResultUtils DATA_ST_RNFL_FDay(@RequestBody ParamField bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        String YBTM = "";
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo,ParamField.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        if (null != bpPojo.getStartdate()){
            YBTM = bpPojo.getStartdate();
        }
        List<Map<String,Object>> mapList = service.DATA_ST_RNFL_FDay(YBTM);
        watch.stop();
        if(mapList.size() > 0){
            return new ResultUtils<>(mapList, "操作成功",true, mapList.size(),watch.getTime());
        }else {
            return new ResultUtils<>(mapList, "操作成功",false, mapList.size(),watch.getTime());
        }
    }

    @RequestMapping("/selectByYBTMNew")
    public ResultUtils selectByYBTMNew(@RequestBody St_rnfl_fPojo bpPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        if(CommonUtills.isEmpty(FieldIsValid.getColumnName(bpPojo,St_rnfl_fPojo.class))){
            watch.stop();
            return new ResultUtils<>(null,"存在非法字符",false,-1,watch.getTime());
        }
        St_rnfl_fPojo fPojo = data.selectByYBTMNew();
        watch.stop();
        if(fPojo != null){
            return new ResultUtils<>(fPojo, "操作成功",true, 1,watch.getTime());
        }else {
            return new ResultUtils<>(fPojo, "操作成功",false, -1,watch.getTime());
        }
    }
}
