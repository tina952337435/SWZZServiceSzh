package swzzmodeserver.workserver.data.swzzrtsq;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.workserver.pojo.swzzrtsq.ST_TIDE_RPojo;

import java.util.List;

@Mapper
public interface RTSQST_TIDE_RData {
    Integer insertAll(@Param(value = "quPojo") List<ST_TIDE_RPojo> quPojo);
}
