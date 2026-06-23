package swzzmodeserver.workserver.data.swzzflood;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.workserver.pojo.swzzflood.ST_TIDEHHL_RPojo;
import swzzmodeserver.workserver.pojo.swzzflood.ST_TIDEHL_RPojo;
import swzzmodeserver.workserver.pojo.swzzflood.ST_TIDE_RPojo;

import java.util.List;

@Mapper
public interface ST_TIDEHHL_RData {

    List<ST_TIDEHHL_RPojo> selectList(@Param(value = "ID") List<String> ID, @Param(value = "key") String key,
                                      @Param(value = "stime") String stime, @Param(value = "etime") String etime,
                                      @Param(value = "type")List<String> type,
                                      @Param(value = "startindex") Integer startindex, @Param(value = "pagesize") Integer pagesize);

    Integer selectCount(@Param(value = "ID") List<String> ID, @Param(value = "key") String key,
                        @Param(value = "stime") String stime, @Param(value = "etime") String etime,
                        @Param(value = "type")List<String> type);

    Integer insertOne(ST_TIDEHHL_RPojo pojo);

    Integer upDateOne(ST_TIDEHHL_RPojo pojo);

    Integer deleteOne(@Param(value = "id") String id);

    Integer insertAll(@Param(value = "objList") List<ST_TIDEHHL_RPojo> objList);

    Integer upDateAll(@Param(value = "objList") List<ST_TIDEHHL_RPojo> objList);

    Integer deleteAll(@Param(value = "stcdList") List<String> stcdList);
}
