package swzzmodeserver.workserver.data.swzzrtsq;

import swzzmodeserver.workserver.pojo.swzzrtsq.ST_STBPRP_B_STTPPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ST_STBPRP_B_STTPData {

    List<ST_STBPRP_B_STTPPojo> selectList(@Param(value = "STZZ") String STZZ,@Param(value = "STPP") String STPP,
                                          @Param(value = "STDP") String STDP,@Param(value = "STDD") String STDD,
                                          @Param(value = "STMM") String STMM);

    Integer insertOne(ST_STBPRP_B_STTPPojo sttpPojo);

    Integer upDateOne(ST_STBPRP_B_STTPPojo sttpPojo);

    Integer deleteOne(@Param(value = "STCD") String STCD);

}
