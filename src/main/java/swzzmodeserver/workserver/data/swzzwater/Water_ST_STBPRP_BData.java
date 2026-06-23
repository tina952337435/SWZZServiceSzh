package swzzmodeserver.workserver.data.swzzwater;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.workserver.pojo.swzzwater.Water_ST_STBPRP_BDto;
import swzzmodeserver.workserver.pojo.swzzwater.Water_ST_STBPRP_BPojo;


import java.util.List;

@Mapper
public interface Water_ST_STBPRP_BData {

    List<Water_ST_STBPRP_BDto> GetSyncSTCD(@Param("stcdList") List<String> stcdList);

    List<Water_ST_STBPRP_BPojo> selectList(@Param("sttp") String sttp);
}
