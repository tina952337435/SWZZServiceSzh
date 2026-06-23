package swzzmodeserver.workserver.service.swzzflood;

import java.util.List;
import java.util.Map;

public interface RTSQService {

    List<Map<String,Object>> WATER_ST_WAS_RNEW(List<String> stcdList,String stime,String etime);

    List<Map<String,Object>> WATER_ST_WAS_RDZNEW(List<String> stcdList,String stime,String etime,String hour);

    List<Map<String,Object>> WATER_ST_PPTN_RNEW(List<String> stcdList,String stime,String etime);

    List<Map<String,Object>> WATER_ST_PPTN_RDZNEW(List<String> stcdList,String stime,String etime,String dayHour);

    List<Map<String,Object>> WATER_ST_PPTNWAS_RDZNEW(List<String> stcdList,String stime,String etime,String dayHour);
}
