package swzzmodeserver.workserver.data.swzzwater;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import swzzmodeserver.workserver.pojo.swzzwater.ST_GATE_RNEWPojo;
import swzzmodeserver.workserver.pojo.swzzwater.ST_GATE_RPojo;

import java.util.List;

@Component
@Mapper
public interface ST_GATE_RNEWData {
    List<ST_GATE_RNEWPojo> selectList(@Param("stime") String stime,
                                      @Param("etime") String etime,
                                      @Param("stcdList") List<String> stcdList);
}
