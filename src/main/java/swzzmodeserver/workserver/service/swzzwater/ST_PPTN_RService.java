package swzzmodeserver.workserver.service.swzzwater;

import java.util.List;
import java.util.Map;

public interface ST_PPTN_RService {

    List<Map<String,Object>> WATER_ST_PPTN_RCUSTOMLIST(List<String> stcdList,String stime,String etime,String dayHour);
}
