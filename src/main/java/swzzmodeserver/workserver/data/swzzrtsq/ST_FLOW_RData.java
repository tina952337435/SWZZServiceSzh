package swzzmodeserver.workserver.data.swzzrtsq;

import org.apache.ibatis.annotations.Mapper;
import swzzmodeserver.workserver.pojo.swzzrtsq.ST_FLOW_RPojo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ST_FLOW_RData {

    List<ST_FLOW_RPojo> selectNew(@Param(value = "stcdList") List<String> stcdList,@Param(value = "stime") String stime,@Param(value = "etime") String etime);

    List<ST_FLOW_RPojo> selectHis(@Param(value = "stcdList") List<String> stcdList,@Param(value = "stime") String stime,@Param(value = "etime") String etime);

    Integer insertAll(@Param(value = "quPojo") List<ST_FLOW_RPojo> quPojo);


    List<ST_FLOW_RPojo> selectHisAll(
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



    List<ST_FLOW_RPojo> selectHisQV(
        @Param(value = "stcdList") List<String> stcdList,
        @Param(value = "stime") String stime,
        @Param(value = "etime") String etime
    );


}
