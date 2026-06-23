package swzzmodeserver.workserver.data.swzzflood;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.workserver.pojo.swzzflood.ST_TIDEHHL_RPojo;
import swzzmodeserver.workserver.pojo.swzzflood.ST_TIDEHL_RPojo;
import swzzmodeserver.workserver.pojo.swzzflood.St_stbprp_b_qu_treePojo;

import java.util.List;

@Mapper
public interface ST_TIDEHL_RData {

    List<ST_TIDEHL_RPojo> selectList(@Param(value = "ID") String ID, @Param(value = "key") String key,
                                     @Param(value = "stime") String stime, @Param(value = "etime") String etime,
                                     @Param(value = "type")List<String> type,
                                     @Param(value = "startindex") Integer startindex, @Param(value = "pagesize") Integer pagesize);

    Integer upDateOne(ST_TIDEHL_RPojo St_stbprp_b_qu_treePojo);

    Integer insertOne(ST_TIDEHL_RPojo St_stbprp_b_qu_treePojo);

    Integer deleteOne(@Param(value = "ID") String ID);

    Integer selectCount(@Param(value = "ID") String ID, @Param(value = "key") String key,
                        @Param(value = "stime") String stime, @Param(value = "etime") String etime,
                        @Param(value = "type")List<String> type);

    Integer insertAll(@Param(value = "objList") List<ST_TIDEHL_RPojo> objList);

    Integer upDateAll(@Param(value = "objList") List<ST_TIDEHL_RPojo> objList);

    Integer deleteAll(@Param(value = "stcdList") List<String> stcdList);
}
