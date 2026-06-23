package swzzmodeserver.workserver.data.swzzwater;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import swzzmodeserver.workserver.pojo.swzzwater.BP_DATAPojo;

import java.util.List;

public interface BP_DATAData {
    List<BP_DATAPojo> selectList(@Param("stime") String stime,
                                   @Param("etime") String etime,
                                   @Param("stcdList") List<String> stcdList);
}
