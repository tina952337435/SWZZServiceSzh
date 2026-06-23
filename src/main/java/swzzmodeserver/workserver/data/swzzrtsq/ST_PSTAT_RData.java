package swzzmodeserver.workserver.data.swzzrtsq;

import swzzmodeserver.workserver.pojo.swzzrtsq.ST_PSTAT_RPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ST_PSTAT_RData {
    List<ST_PSTAT_RPojo> selectList(@Param(value = "stcdList") List<String> stcdList,@Param(value = "stime") String stime,@Param(value = "etime") String etime,@Param(value = "pathname") List<String> pathname);

    List<ST_PSTAT_RPojo> selectListHour(@Param(value = "stcdList") List<String> stcdList,@Param(value = "stime") String stime,@Param(value = "etime") String etime,@Param(value = "pathname") List<String> pathname);

    List<ST_PSTAT_RPojo> selectListDay(@Param(value = "stcdList") List<String> stcdList,@Param(value = "stime") String stime,@Param(value = "etime") String etime,@Param(value = "pathname") List<String> pathname);

    List<ST_PSTAT_RPojo> selectListMonth(@Param(value = "stcdList") List<String> stcdList,@Param(value = "stime") String stime,@Param(value = "etime") String etime,@Param(value = "pathname") List<String> pathname);

    List<ST_PSTAT_RPojo> selectListYear(@Param(value = "stcdList") List<String> stcdList,@Param(value = "stime") String stime,@Param(value = "etime") String etime,@Param(value = "pathname") List<String> pathname);

    List<ST_PSTAT_RPojo> selectListTotal(@Param(value = "stcdList") List<String> stcdList,@Param(value = "stime") String stime,@Param(value = "etime") String etime,@Param(value = "pathname") List<String> pathname);

    List<ST_PSTAT_RPojo> selectListTotalDay(
        @Param(value = "stcdList") List<String> stcdList,
        @Param(value = "stime") String stime,
        @Param(value = "etime") String etime,
        @Param(value = "pathname") List<String> pathname);

    
    //selectListIsLast
    List<ST_PSTAT_RPojo> selectListIsLast(@Param(value = "stcdList") List<String> stcdList,@Param(value = "stime") String stime,@Param(value = "etime") String etime,@Param(value = "pathname") List<String> pathname);

    List<ST_PSTAT_RPojo> selectListDayly(@Param(value = "stcdList") List<String> stcdList,@Param(value = "stime") String stime,@Param(value = "etime") String etime,@Param(value = "pathname") List<String> pathname);

    Integer insertAll(@Param(value = "quPojo") List<ST_PSTAT_RPojo> quPojo);

    Integer insertOne(ST_PSTAT_RPojo bPojo);

    Integer upDateOne(ST_PSTAT_RPojo bPojo);

    Integer updateALL(@Param(value = "objList") List<ST_PSTAT_RPojo> objList);
}
