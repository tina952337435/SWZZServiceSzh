package swzzmodeserver.workserver.data.swzzrtsq;

import org.apache.ibatis.annotations.Mapper;
import swzzmodeserver.workserver.pojo.swzzrtsq.ST_WAS_RPojo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RTSQST_WAS_RData {

    List<ST_WAS_RPojo> selectNew(@Param(value = "stcdList") List<String> stcdList,@Param(value = "stime") String stime,@Param(value = "etime") String etime);

    List<ST_WAS_RPojo> selectHis(@Param(value = "stcdList") List<String> stcdList,@Param(value = "stime") String stime,@Param(value = "etime") String etime);

    Integer insertAll(@Param(value = "quPojo") List<ST_WAS_RPojo> quPojo);

}
