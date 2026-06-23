package swzzmodeserver.workserver.data.swzzflood;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import swzzmodeserver.workserver.pojo.swzzflood.ST_TIDEH_RParam;
import swzzmodeserver.workserver.pojo.swzzflood.ST_TIDEH_RPojo;
import swzzmodeserver.workserver.pojo.swzzflood.ST_TIDE_RPojo;

import java.util.List;

@Mapper
public interface ST_TIDEH_RData {

    List<ST_TIDEH_RPojo> selectTideHList(@Param(value = "stcdList") List<String> stcdList,
                                           @Param(value = "stime")String stime,
                                           @Param(value = "etime")String etime);

    Integer insertOne(ST_TIDEH_RPojo pojo);

    Integer upDateOne(ST_TIDEH_RPojo pojo);

    Integer deleteOne(@Param(value = "id") String id);

    Integer insertAll(@Param(value = "objList") List<ST_TIDEH_RPojo> objList);

    Integer upDateAll(@Param(value = "objList") List<ST_TIDEH_RPojo> objList);

    Integer deleteAll(@Param(value = "stcdList") List<String> stcdList);   
    
}
