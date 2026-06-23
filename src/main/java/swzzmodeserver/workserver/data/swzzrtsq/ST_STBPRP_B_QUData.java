package swzzmodeserver.workserver.data.swzzrtsq;

import swzzmodeserver.workserver.pojo.swzzrtsq.ST_PPTN_RPojo;
import swzzmodeserver.workserver.pojo.swzzrtsq.ST_STBPRP_B_QUDto;
import swzzmodeserver.workserver.pojo.swzzrtsq.ST_STBPRP_B_QUPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ST_STBPRP_B_QUData {

    List<ST_STBPRP_B_QUPojo> selectList(@Param(value = "STCD") String STCD,
                                        @Param(value = "STNM") String STNM,
                                        @Param(value = "STTPList") List<String> STTPList,
                                        @Param(value = "PID") String PID,
                                        @Param(value = "ADMAUTH") String ADMAUTH);
    List<ST_STBPRP_B_QUPojo> queryList(@Param(value = "STCD") String STCD,
                                        @Param(value = "STNM") String STNM,
                                        @Param(value = "STTPList") List<String> STTPList,
                                       @Param(value = "PIDList") List<String> PIDList);

    Integer insertOne(ST_STBPRP_B_QUPojo quPojo);

    Integer upDateOne(ST_STBPRP_B_QUPojo quPojo);

    Integer deleteOne(@Param(value = "ID") String ID);

    Integer insertALL(@Param(value = "quPojos") List<ST_STBPRP_B_QUPojo> quPojos);

    List<ST_STBPRP_B_QUDto> selectQUAndRPBList(@Param(value = "PID") String PID);
}
