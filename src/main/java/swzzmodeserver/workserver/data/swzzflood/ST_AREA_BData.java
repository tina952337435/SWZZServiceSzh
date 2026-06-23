package swzzmodeserver.workserver.data.swzzflood;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import swzzmodeserver.workserver.pojo.swzzflood.ST_AREA_BPojo;

import java.util.List;

@Mapper
@Component
public interface ST_AREA_BData {

    List<ST_AREA_BPojo> selectList(@Param(value = "AID") String AID,
                                    @Param(value = "key") String key,
                                    @Param(value = "pathname") String pathname);

    Integer insertOne(ST_AREA_BPojo pojo);

    Integer updateOne(ST_AREA_BPojo pojo);

    Integer deleteOne(@Param(value = "AID") String AID);

    Integer selectCount(@Param(value = "AID") String AID,
                        @Param(value = "key") String key,
                        @Param(value = "pathname") String pathname);
}