package swzzmodeserver.workserver.data.swzzrtsq;

import org.apache.ibatis.annotations.Mapper;
import swzzmodeserver.workserver.pojo.swzzrtsq.ST_VEL_RPojo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ST_VEL_RData {

    List<ST_VEL_RPojo> selectNew(@Param(value = "stcdList") List<String> stcdList, @Param(value = "stime") String stime, @Param(value = "etime") String etime);

    List<ST_VEL_RPojo> selectHis(@Param(value = "stcdList") List<String> stcdList, @Param(value = "stime") String stime, @Param(value = "etime") String etime);

    Integer insertAll(@Param(value = "quPojo") List<ST_VEL_RPojo> quPojo);

    List<ST_VEL_RPojo> selectHisAll(
        @Param(value = "stcdList") List<String> stcdList,
        @Param(value = "stime") String stime,
        @Param(value = "etime") String etime,
        @Param(value = "startindex") Integer startindex,
        @Param(value = "pagesize") Integer pagesize);

    public Integer selectHisAllCount(
        @Param(value = "stcdList") List<String> stcdList,
        @Param(value = "stime") String stime,
        @Param(value = "etime") String etime
    );

}
