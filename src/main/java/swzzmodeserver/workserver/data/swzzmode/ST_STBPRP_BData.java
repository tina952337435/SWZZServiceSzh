package swzzmodeserver.workserver.data.swzzmode;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.workserver.pojo.swzzmode.ST_STBPRP_BDto;
import swzzmodeserver.workserver.pojo.swzzmode.ST_STBPRP_BPojo;

import java.util.List;

@Mapper
public interface ST_STBPRP_BData {

    List<ST_STBPRP_BPojo> selectList(@Param(value = "ID") String ID,@Param(value = "key") String key,
                                     @Param(value = "stime") String stime,@Param(value = "etime") String etime,
                                     @Param(value = "startindex") Integer startindex,@Param(value = "pagesize") Integer pagesize);

    Integer updateOne(ST_STBPRP_BPojo stStbprpBPojo);

    Integer insertOne(ST_STBPRP_BPojo stStbprpBPojo);

    Integer deleteOne(@Param(value = "ID") String ID);

    List<ST_STBPRP_BDto> selectListBandStcd(@Param("stcd") String stcd);

    List<ST_STBPRP_BDto> selectListBandStcdByStcdList(@Param("stcdList") List<String> stcdList);

    Integer selectCount(@Param(value = "ID") String ID,@Param(value = "key") String key,
                        @Param(value = "stime") String stime,@Param(value = "etime") String etime);

    //Integer insertALL(@Param(value = "objList")List<ST_STBPRP_BPojo> objList);
}
