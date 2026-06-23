package swzzmodeserver.workserver.data.wds;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.workserver.pojo.swzzrtsq.TSDBPojo;

import java.util.List;

@Mapper
public interface TSDBData {
    List<TSDBPojo> selectList(@Param("stime") String stime, @Param("etime") String etime, @Param("stcdList") List<String> stcdList);

    List<TSDBPojo> selectTSDBListtTop (@Param(value = "stcdList") List<String> stcdList, @Param(value = "stime") String stime, @Param(value = "etime") String etime);

}