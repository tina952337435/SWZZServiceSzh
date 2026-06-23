package swzzmodeserver.workserver.data.swzzrtsq;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.workserver.pojo.swzzrtsq.ST_WDWV_RPojo;
import swzzmodeserver.workserver.pojo.swzzrtsq.tb_fengsufengxiangPojo;
import swzzmodeserver.workserver.pojo.swzzrtsq.tb_wind_informationPojo;

import java.util.List;

@Mapper
public interface ST_WDWV_RData {

    List<ST_WDWV_RPojo> selectNew(@Param(value = "stcdList") List<String> stcdList,@Param(value = "stime") String stime,@Param(value = "etime") String etime);

    List<ST_WDWV_RPojo> selectHis(@Param(value = "stcdList") List<String> stcdList,@Param(value = "stime") String stime,@Param(value = "etime") String etime);

    Integer insertAll(@Param(value = "quPojo") List<ST_WDWV_RPojo> quPojo);


    List<tb_fengsufengxiangPojo> selecttb_fengsufengxiangList(@Param(value = "stcdList") List<String> stcdList,@Param(value = "stime") String stime,@Param(value = "etime") String etime);

    List<tb_fengsufengxiangPojo> selecttb_fengsufengxiangListtTop(@Param(value = "stcdList") List<String> stcdList,@Param(value = "stime") String stime,@Param(value = "etime") String etime);


    List<tb_wind_informationPojo> selecttb_wind_informationList(@Param(value = "stcdList") List<String> stcdList, @Param(value = "stime") String stime, @Param(value = "etime") String etime);

    List<tb_wind_informationPojo> selecttb_wind_informationListtTop(@Param(value = "stcdList") List<String> stcdList,@Param(value = "stime") String stime,@Param(value = "etime") String etime);

    List<ST_WDWV_RPojo> selectHisAll(
        @Param(value = "stcdList") List<String> stcdList,
        @Param(value = "stime") String stime,
        @Param(value = "etime") String etime,
        @Param(value = "startindex") Integer startindex,
        @Param(value = "pagesize") Integer pagesize
    );

    Integer selectHisAllCount(@Param(value = "stcdList") List<String> stcdList,@Param(value = "stime") String stime,@Param(value = "etime") String etime);
}
