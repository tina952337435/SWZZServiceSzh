package swzzmodeserver.workserver.data.swzzflood;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import swzzmodeserver.workserver.pojo.swzzflood.ST_AREACWSTATION_BPojo;

import java.util.List;

@Mapper
@Component
public interface ST_AREACWSTATION_BData {

    List<ST_AREACWSTATION_BPojo> selectList(@Param(value = "CWID") String CWID,
                                            @Param(value = "stcdList") List<String> stcdList,
                                            @Param(value = "PAID") String PAID);

    Integer insertOne(ST_AREACWSTATION_BPojo pojo);

    Integer updateOne(ST_AREACWSTATION_BPojo pojo);

    Integer deleteOne(@Param(value = "CWID") String CWID);

    Integer selectCount(@Param(value = "CWID") String CWID,
                        @Param(value = "stcdList") List<String> stcdList,
                        @Param(value = "PAID") String PAID);
}