package swzzmodeserver.workserver.data.swzzmode;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.tools.ParamField;
import swzzmodeserver.workserver.pojo.swzzmode.ST_ASTRONOMICALTIDE_RPojo;

import java.util.List;

@Mapper
public interface ST_ASTRONOMICALTIDE_RData {

    List<ST_ASTRONOMICALTIDE_RPojo> selectList(@Param(value = "IDList") List<String> ID,@Param(value = "key") String key,
                                               @Param(value = "stime") String stime,@Param(value = "etime") String etime,
                                               @Param(value = "type")List<String> type,
                                               @Param(value = "startindex") Integer startindex,@Param(value = "pagesize") Integer pagesize);


    List<ST_ASTRONOMICALTIDE_RPojo> selectListNew(@Param(value = "IDList") List<String> ID,@Param(value = "stime") String stime,@Param(value = "etime") String etime);

    Integer updateOne(ST_ASTRONOMICALTIDE_RPojo bdmsPredictPojo);

    Integer insertOne(ST_ASTRONOMICALTIDE_RPojo bdmsPredictPojo);

    Integer deleteOne(@Param(value = "ID") String ID,@Param(value = "TM") String TM);

    Integer insertALL(@Param(value = "bpPojo")List<ST_ASTRONOMICALTIDE_RPojo> bpPojo);

    Integer deleteALL(@Param(value = "bpPojo")List<ParamField> bpPojo);

    Integer updateALL(@Param(value = "bpPojo")List<ST_ASTRONOMICALTIDE_RPojo> bpPojo);

    Integer selectCount(@Param(value = "IDList") List<String> ID,@Param(value = "key") String key,
                        @Param(value = "stime") String stime,@Param(value = "etime") String etime,
                        @Param(value = "type")List<String> type);
}
