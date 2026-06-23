package swzzmodeserver.workserver.data.swzzflood;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.workserver.pojo.swzzflood.Flood_ST_STBPRP_BDto;
import swzzmodeserver.workserver.pojo.swzzwater.Water_ST_STBPRP_BDto;
import swzzmodeserver.workserver.pojo.swzzwater.Water_ST_STBPRP_BPojo;

import java.util.List;

@Mapper
public interface Flood_ST_STBPRP_BData {
    List<Flood_ST_STBPRP_BDto> getBASE_BTZ(@Param("stcdList") List<String> stcdList);
}
