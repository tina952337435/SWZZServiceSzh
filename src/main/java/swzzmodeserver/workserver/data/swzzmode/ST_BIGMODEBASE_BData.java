package swzzmodeserver.workserver.data.swzzmode;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.workserver.pojo.swzzmode.ST_BIGMODEBASE_BPojo;

import java.util.List;

@Mapper
public interface ST_BIGMODEBASE_BData {

    List<ST_BIGMODEBASE_BPojo> selectList(@Param(value = "ID") String ID, @Param(value = "key") String key,
                                          @Param(value = "startindex") Integer startindex, @Param(value = "pagesize") Integer pagesize);

    Integer updateOne(ST_BIGMODEBASE_BPojo stBigmodebaseBPojo);

    Integer insertOne(ST_BIGMODEBASE_BPojo stBigmodebaseBPojo);

    Integer deleteOne(@Param(value = "ID") String ID);

    Integer selectCount(@Param(value = "ID") String ID, @Param(value = "key") String key);
}
