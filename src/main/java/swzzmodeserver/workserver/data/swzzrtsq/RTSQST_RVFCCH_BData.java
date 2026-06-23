package swzzmodeserver.workserver.data.swzzrtsq;

import swzzmodeserver.workserver.pojo.swzzrtsq.ST_RVFCCH_BPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RTSQST_RVFCCH_BData {

    List<ST_RVFCCH_BPojo> selectList(@Param(value = "STCD") String STCD);

    Integer insertOne(ST_RVFCCH_BPojo bPojo);

    Integer upDateOne(ST_RVFCCH_BPojo bPojo);

    Integer deleteOne(@Param(value = "STCD") String STCD);
}
