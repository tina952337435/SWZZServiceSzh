package swzzmodeserver.workserver.service.swzzmode;


import swzzmodeserver.workserver.pojo.swzzmode.AREASLParam;
import swzzmodeserver.workserver.pojo.swzzmode.WJ_MODELSINGRESULTHLParam;
import swzzmodeserver.workserver.pojo.swzzmode.WJ_MODELSINGRESULTParam;
import swzzmodeserver.workserver.pojo.swzzmode.YBList;

import java.util.List;
import java.util.Map;

public interface BDMS_PREDICTService {

    String GetFangAnData(String DD_MIND,String STCD,String DATA_TYPE,String DD_EVALUE,String MKEYID);

    String GetFangAnDataDuo(String DD_MIND,String STCD,String DATA_TYPE,String DD_EVALUE,String MKEYID);
    List<AREASLParam> AREASL(String DD_ID, String STCD);

    List<YBList> YBList(String solutionid,String typeID,String TM);

    List<WJ_MODELSINGRESULTParam> WJ_MODELSINGRESULT(List<String> SOLUTIONID,List<String> STCD,String DATA_TYPE);

    List<Map<String,String>> MODE_BDMS_PREDICTSelMORE(String stime, String etime, List<String> singletonList, String stcd, String data_type);

    List<WJ_MODELSINGRESULTHLParam> WJ_MODELSINGRESULTHL(List<String> SOLUTIONID, List<String> STCD, String DATA_TYPE);

}
