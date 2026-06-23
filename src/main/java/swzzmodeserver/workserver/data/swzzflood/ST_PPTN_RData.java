package swzzmodeserver.workserver.data.swzzflood;

import lombok.Data;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import swzzmodeserver.workserver.pojo.swzzflood.ST_PPTN_RPojo;

import java.util.List;

@Mapper
@Component
public interface ST_PPTN_RData {
    List<ST_PPTN_RPojo> selectList(@Param("stime") String stime,
                                   @Param("etime") String etime,
                                   @Param("stcdList") List<String> stcdList);

}
