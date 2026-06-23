package swzzmodeserver.workserver.data.swzzrtsq;

import swzzmodeserver.workserver.pojo.swzzrtsq.ST_GATE_BPojo;
import swzzmodeserver.workserver.pojo.swzzrtsq.ST_GATE_RNEWPojo;
import swzzmodeserver.workserver.pojo.swzzrtsq.ST_GATE_RNEWPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RTSQST_GATE_RNEWData {

    List<ST_GATE_RNEWPojo> selectByNew(@Param(value = "STCD") String STCD);

    List<ST_GATE_RNEWPojo> selectGateListNew(@Param(value = "stcdList") List<String> stcdList);

    List<ST_GATE_RNEWPojo> selectByHis(@Param(value = "STCD") String STCD,@Param(value = "stime") String stime,@Param(value = "etime") String etime);

    Integer insertAll(@Param(value = "quPojo") List<ST_GATE_RNEWPojo> quPojo);

    List<ST_GATE_RNEWPojo> queryList(@Param(value = "listSTCD") List<String> listSTCD,@Param(value = "stime") String stime,@Param(value = "etime") String etime,@Param(value = "exkey") String exkey,@Param(value = "eqptp") String eqptp);
    List<ST_GATE_RNEWPojo> queryListQ(@Param(value = "listSTCD") List<String> listSTCD,@Param(value = "stime") String stime,@Param(value = "etime") String etime,@Param(value = "exkey") String exkey,@Param(value = "eqptp") String eqptp);
    List<ST_GATE_BPojo> queryGateBList(@Param(value = "listSTCD") List<String> listSTCD);
    List<ST_GATE_BPojo> queryGateBListByType(@Param(value = "listSTCD") List<String> listSTCD,@Param(value = "mtype") String mtype);

    Integer deleteAll();
    Integer updateAll(@Param(value = "list") List<ST_GATE_RNEWPojo> quPojo);
    Integer upsertOne(ST_GATE_RNEWPojo pojo);
    Integer upsertByStcd(@Param("stcd") String stcd);

    Integer upsertAll();

}
