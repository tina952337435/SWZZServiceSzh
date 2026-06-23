package swzzmodeserver.workserver.data.swzzmode;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.workserver.pojo.swzzmode.ST_STRVFCCH_SKBPojo;

import java.util.List;

@Mapper
public interface ST_STRVFCCH_SKBData {

    List<ST_STRVFCCH_SKBPojo> selectList(@Param(value = "ID") String ID,
                                         @Param(value = "startindex") Integer startindex,@Param(value = "pagesize") Integer pagesize);

    Integer updateOne(ST_STRVFCCH_SKBPojo stStrvfcchSkbPojo);

    Integer insertOne(ST_STRVFCCH_SKBPojo stStrvfcchSkbPojo);

    Integer deleteOne(@Param(value = "ID") String ID);

    Integer insertALL(@Param(value = "objList")List<ST_STRVFCCH_SKBPojo> objList);

    Integer selectCount(@Param(value = "ID") String ID);
}
