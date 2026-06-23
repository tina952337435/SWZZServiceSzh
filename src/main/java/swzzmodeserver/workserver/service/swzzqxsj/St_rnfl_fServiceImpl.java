package swzzmodeserver.workserver.service.swzzqxsj;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swzzmodeserver.workserver.data.swzzqxsj.St_rnfl_fData;
import swzzmodeserver.workserver.pojo.swzzqxsj.St_rnfl_fPojo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class St_rnfl_fServiceImpl implements St_rnfl_fService{
    @Autowired
    private St_rnfl_fData data;

    @Override
    public List<Map<String,Object>> DATA_ST_RNFL_FDay(String ybtm) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat format = new SimpleDateFormat("MM月dd日HH时");
        List<St_rnfl_fPojo> listData = data.selectList("", "", "", "", null, ybtm, null, null);
        listData.sort((a,b)->{
            return a.getTM().compareTo(b.getTM());
        });
        Map<String, List<St_rnfl_fPojo>> map = listData.stream().collect(Collectors.groupingBy(St_rnfl_fPojo::getTYPE));
        Set<String> types = map.keySet();
        List<Map<String,Object>> mapList = new ArrayList<>();
        if (listData.size() > 0){
            Date TMtime = null;
            try {
                TMtime = dateFormat.parse(ybtm);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            int dataCount = 7;
            for (int num = 0;num < dataCount;num++){
                Date p1 = new Date(TMtime.getTime() + num * 24 * 60 * 60 * 1000);
                Date p2 = new Date(TMtime.getTime() + (num + 1) * 24 * 60 * 60 * 1000);
                for(String type : types){
                    List<St_rnfl_fPojo> tempst_pptn_r = listData.stream().filter(m -> {
                        if (null != m.getTYPE() && null != m.getTM()) {
                            try {
                                return type.equals(m.getTYPE()) &&
                                        dateFormat.parse(m.getTM()).getTime() > p1.getTime() &&
                                        dateFormat.parse(m.getTM()).getTime() <= p2.getTime();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        return false;
                    }).collect(Collectors.toList());
                    Map<String,Object> dto = new HashMap<>();
                    dto.put("NAME",type);
                    dto.put("TM",dateFormat.format(p1));
                    dto.put("YBTM",ybtm);
                    dto.put("DRP",tempst_pptn_r.stream().mapToDouble(m->{
                        if (m.getDRP() != null){
                            return m.getDRP();
                        }
                        return 0.0;
                    }).sum());
                    String strTM = format.format(p1.getTime()) + "至" + format.format(p2);
                    dto.put("DATESPAN",strTM);
                    mapList.add(dto);
                }
            }
        }
        return mapList;
    }
}
