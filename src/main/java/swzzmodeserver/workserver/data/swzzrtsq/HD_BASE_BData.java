package swzzmodeserver.workserver.data.swzzrtsq;

import swzzmodeserver.workserver.pojo.swzzrtsq.HD_BASE_BPojo;
import swzzmodeserver.workserver.pojo.swzzrtsq.HD_BASE_B_PATHPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface HD_BASE_BData {

    List<HD_BASE_BPojo> selectList(@Param(value = "stcdList") List<String> stcdList,
                                   @Param(value = "rvnmList") List<String> rvnmList,
                                   @Param(value = "KEY") String KEY,
                                   @Param(value = "PATHNAME") String PATHNAME
    );
    List<HD_BASE_BPojo> selectPathList(@Param(value = "stcdList") List<String> stcdList
    );

}
