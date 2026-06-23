package swzzmodeserver.workserver.data.swzzflood;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import swzzmodeserver.workserver.pojo.swzzflood.ST_AREASTATION_BPojo;

import java.util.List;

@Mapper
@Component
public interface ST_AREASTATION_BData {

    List<ST_AREASTATION_BPojo> selectList(@Param(value = "BID") String BID,
                                            @Param(value = "stcdList") List<String> stcdList,
                                            @Param(value = "PAID") String PAID,
                                            @Param(value = "BTYPE") String BTYPE);

    Integer insertOne(ST_AREASTATION_BPojo pojo);

    Integer updateOne(ST_AREASTATION_BPojo pojo);

    Integer deleteOne(@Param(value = "BID") String BID);

    Integer selectCount(@Param(value = "BID") String BID,
                        @Param(value = "stcdList") List<String> stcdList,
                        @Param(value = "PAID") String PAID,
                        @Param(value = "BTYPE") String BTYPE);
}