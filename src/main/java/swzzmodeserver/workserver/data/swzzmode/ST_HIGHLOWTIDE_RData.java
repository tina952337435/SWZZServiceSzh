package swzzmodeserver.workserver.data.swzzmode;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.tools.ParamField;
import swzzmodeserver.workserver.pojo.swzzmode.BDMS_PREDICTPojo;
import swzzmodeserver.workserver.pojo.swzzmode.SDE_HDDMPojo;
import swzzmodeserver.workserver.pojo.swzzmode.ST_HIGHLOWTIDE_RPojo;

import java.util.List;

@Mapper
public interface ST_HIGHLOWTIDE_RData {

    List<ST_HIGHLOWTIDE_RPojo> selectList(@Param(value = "ID") String ID,@Param(value = "stime") String stime,
                                          @Param(value = "etime") String etime,
                                          @Param(value = "type")List<String> type,
                                          @Param(value = "startindex") Integer startindex,@Param(value = "pagesize") Integer pagesize);

    Integer updateOne(ST_HIGHLOWTIDE_RPojo bdmsPredictPojo);

    Integer insertOne(ST_HIGHLOWTIDE_RPojo bdmsPredictPojo);

    Integer deleteOne(@Param(value = "ID") String ID,@Param(value = "TM") String TM);

    Integer insertALL(@Param(value = "objList") List<ST_HIGHLOWTIDE_RPojo> objList);

    Integer deleteALL(@Param(value = "bpPojo")List<ParamField> bpPojo);

    Integer updateALL(@Param(value = "objList") List<ST_HIGHLOWTIDE_RPojo> objList);

    Integer selectCount(@Param(value = "ID") String ID,@Param(value = "stime") String stime,
                        @Param(value = "etime") String etime,
                        @Param(value = "type")List<String> type);
}
