package swzzmodeserver.workserver.service.swzzwater;

import com.github.pagehelper.PageInfo;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.workserver.pojo.swzzwater.ST_GATE_RPojo;

import java.util.List;
import java.util.Map;

public interface ST_GATE_RService {

    List<Map<String,Object>> WATER_ST_GATE_RNEWLIST(List<String> treeId, String stime, String etime, List<String> stcds);

    Map<String,Object> selectList(String stime, String etime, String EXKEY, String EQPTP, List<String> stcdList,Integer pageIndex,Integer pagesize);
}
