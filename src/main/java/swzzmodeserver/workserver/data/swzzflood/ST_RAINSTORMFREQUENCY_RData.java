package swzzmodeserver.workserver.data.swzzflood;

import swzzmodeserver.workserver.pojo.swzzflood.ST_RAINSTORMFREQUENCY_RPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ST_RAINSTORMFREQUENCY_RData {

    List<ST_RAINSTORMFREQUENCY_RPojo> selectList(@Param(value = "stcd") String stcd,
                                                  @Param(value = "stime") String stime,
                                                  @Param(value = "etime") String etime);

    ST_RAINSTORMFREQUENCY_RPojo selectOne(@Param(value = "STCD") String STCD,
                                           @Param(value = "BGTM") String BGTM,
                                           @Param(value = "ENDTM") String ENDTM);

    Integer insertOne(ST_RAINSTORMFREQUENCY_RPojo pojo);

    Integer upDateOne(ST_RAINSTORMFREQUENCY_RPojo pojo);

    Integer deleteOne(@Param(value = "STCD") String STCD,
                      @Param(value = "BGTM") String BGTM,
                      @Param(value = "ENDTM") String ENDTM);
}