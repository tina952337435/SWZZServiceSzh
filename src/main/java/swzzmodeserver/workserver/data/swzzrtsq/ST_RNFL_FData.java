package swzzmodeserver.workserver.data.swzzrtsq;

import swzzmodeserver.workserver.pojo.swzzrtsq.ST_RNFL_FPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ST_RNFL_FData {

    List<ST_RNFL_FPojo> selectListByTmAsc(@Param(value = "stcdList") List<String> stcdList,
                                   @Param(value = "stime") String stime,
                                   @Param(value = "etime") String etime,
                                   @Param(value = "intv") String intv,
                                   @Param(value = "type") String type);

    List<ST_RNFL_FPojo> selectListByYbtmDesc(@Param(value = "stcdList") List<String> stcdList,
                                   @Param(value = "stime") String stime,
                                   @Param(value = "etime") String etime,
                                   @Param(value = "intv") String intv,
                                   @Param(value = "type") String type);

    Integer insertAll(@Param(value = "rnflList") List<ST_RNFL_FPojo> rnflList);
    /*
     * 根据预报时间范围时间，查询最新预报数据
     * */
    List<ST_RNFL_FPojo> selectListNew(@Param(value = "stcdList") List<String> stcdList,
                                      @Param(value = "stime") String stime,
                                      @Param(value = "etime") String etime,
                                      @Param(value = "intv") String intv,
                                      @Param(value = "type") String type);
}
