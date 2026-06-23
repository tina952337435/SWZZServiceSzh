package swzzmodeserver.workserver.data.swzzrtsq;

import swzzmodeserver.workserver.pojo.swzzrtsq.ST_RIVER_RPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ST_RIVER_RData {
    Integer insertAll(@Param(value = "quPojo") List<ST_RIVER_RPojo> quPojo);
    Integer upDateMaxRiver(ST_RIVER_RPojo mode);
}
