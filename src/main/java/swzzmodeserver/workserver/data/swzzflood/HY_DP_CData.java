package swzzmodeserver.workserver.data.swzzflood;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import swzzmodeserver.workserver.pojo.swzzflood.HY_DP_CPojo;

import java.util.List;

@Mapper
@Component
public interface HY_DP_CData {
    List<HY_DP_CPojo> selectList(@Param("stime") String stime, @Param("etime") String etime,
                                 @Param("idList") List<String> idList);

    List<HY_DP_CPojo> selectListByPage(@Param("key") String key,
                                       @Param("stime") String stime,
                                       @Param("etime") String etime,
                                       @Param("prcdList") List<String> prcdList,
                                       @Param("idList") List<String> idList);
}
