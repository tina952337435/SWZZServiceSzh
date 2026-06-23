package swzzmodeserver.workserver.data.swzzrtsq;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.workserver.pojo.swzzrtsq.ST_GATE_BPojo;
import swzzmodeserver.workserver.pojo.swzzrtsq.ST_GATE_RPojo;

import java.util.List;

@Mapper
public interface RTSQST_GATE_RData {

    List<ST_GATE_RPojo> selectByNew(@Param(value = "STCD") String STCD);

    List<ST_GATE_RPojo> selectGateListNew(@Param(value = "stcdList") List<String> stcdList);

    List<ST_GATE_RPojo> selectByHis(@Param(value = "STCD") String STCD,@Param(value = "stime") String stime,@Param(value = "etime") String etime);

    Integer insertAll(@Param(value = "quPojo") List<ST_GATE_RPojo> quPojo);

    List<ST_GATE_RPojo> queryList(@Param(value = "listSTCD") List<String> listSTCD,@Param(value = "stime") String stime,@Param(value = "etime") String etime,@Param(value = "exkey") String exkey,@Param(value = "eqptp") String eqptp);
    List<ST_GATE_RPojo> queryListQ(@Param(value = "listSTCD") List<String> listSTCD,@Param(value = "stime") String stime,@Param(value = "etime") String etime,@Param(value = "exkey") String exkey,@Param(value = "eqptp") String eqptp);
    List<ST_GATE_BPojo> queryGateBList(@Param(value = "listSTCD") List<String> listSTCD);
    List<ST_GATE_BPojo> queryGateBListByType(@Param(value = "listSTCD") List<String> listSTCD,@Param(value = "mtype") String mtype);

    List<ST_GATE_RPojo> selectNewList();
    List<ST_GATE_RPojo> selectNewListByStcd(@Param("stcd") String stcd);

}
