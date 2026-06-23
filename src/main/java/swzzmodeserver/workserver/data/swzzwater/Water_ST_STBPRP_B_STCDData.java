package swzzmodeserver.workserver.data.swzzwater;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import swzzmodeserver.workserver.pojo.swzzmode.ST_STBPRP_B_STCDPojo;
import swzzmodeserver.workserver.pojo.swzzwater.ST_GATE_RNEWPojo;
import swzzmodeserver.workserver.pojo.swzzwater.Water_ST_STBPRP_B_STCDPojo;

import java.util.List;

@Mapper
@Component
public interface Water_ST_STBPRP_B_STCDData {
    List<Water_ST_STBPRP_B_STCDPojo> selectList(@Param("type") List<String> TYPE);
}
