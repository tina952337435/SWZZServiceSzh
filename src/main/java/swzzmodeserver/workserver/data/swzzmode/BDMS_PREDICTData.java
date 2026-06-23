package swzzmodeserver.workserver.data.swzzmode;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.tools.ParamField;
import swzzmodeserver.workserver.pojo.swzzmode.BDMS_PREDICTPojo;
import java.util.List;

@Mapper
public interface BDMS_PREDICTData {

    List<BDMS_PREDICTPojo> selectList(@Param(value = "ID") String ID,@Param(value = "stime") String stime,
                                      @Param(value = "etime") String etime,
                                      @Param("PLAN_N")List<String> PLAN_N,
                                      @Param(value = "STCD") String STCD,
                                      @Param("dataType") String type,
                                      @Param(value = "startindex") Integer startindex,@Param(value = "pagesize") Integer pagesize,
                                      @Param("order")String order,@Param(value = "stcdlist") List<String> stcdlist);

    Integer updateOne(BDMS_PREDICTPojo bdmsPredictPojo);

    Integer insertOne(BDMS_PREDICTPojo bdmsPredictPojo);

    Integer deleteOne(@Param(value = "ID") String ID);

    Integer insertALL(@Param(value = "objList") List<BDMS_PREDICTPojo> objList);

    Integer deleteALL(@Param(value = "bpPojo")List<ParamField> bpPojo);

    Integer deleteFangAn(@Param(value = "bpPojo")List<ParamField> bpPojo);

    Integer updateALL(@Param(value = "objList")List<BDMS_PREDICTPojo> objList);

    Integer selectCount(@Param(value = "ID") String ID,@Param(value = "stime") String stime,
                        @Param(value = "etime") String etime,
                        @Param("PLAN_N")List<String> PLAN_N,
                        @Param(value = "STCD") String STCD,
                        @Param("dataType") String type);
}
