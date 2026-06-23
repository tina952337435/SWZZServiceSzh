package swzzmodeserver.workserver.service.zjtyphoon;

import java.util.List;
import java.util.Map;

public interface ZJ_TFZTService {

    List<Map<String,Object>> TYPHOON_ZJ_TFZTSel(String key, String stime, String etime, String ddwj, String year, String ZJ_ISCOMPLETED, Integer pageIndex, Integer pageSize);

    List<Map<String,Object>> GetTFBH_XSSel(String tfbh,String type,String isWX);

    List<Map<String,Object>> GetTFBH_XSYBSel(String tfbh, String ddwj, String isWX,String ZJ_YBSJ,String ZJ_TM);

    List<Map<String,Object>> GetTyphoonList(List<String> tfbh, String isYB);

    List<Map<String,Object>> GetTyphoonListYB(String tfbh,String rqsj2,String tfybdw);
}
