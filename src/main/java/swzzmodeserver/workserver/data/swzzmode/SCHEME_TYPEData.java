package swzzmodeserver.workserver.data.swzzmode;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.workserver.pojo.swzzmode.BDMS_PREDICTPojo;
import swzzmodeserver.workserver.pojo.swzzmode.MigrationHistoryPojo;
import swzzmodeserver.workserver.pojo.swzzmode.SCHEME_TYPEPojo;

import java.util.List;

@Mapper
public interface SCHEME_TYPEData {

    List<SCHEME_TYPEPojo> selectList(@Param(value = "ID") String ID,
                                     @Param(value = "key") String key,
                                     @Param(value = "stime") String stime,
                                     @Param(value = "etime") String etime,
                                     @Param(value = "DDstime") String DDstime,
                                     @Param(value = "DDetime") String DDetime,
                                     @Param(value = "startindex") Integer startindex,@Param(value = "pagesize") Integer pagesize);

    Integer updateOne(SCHEME_TYPEPojo bdmsPredictPojo);

    Integer insertOne(SCHEME_TYPEPojo bdmsPredictPojo);

    Integer deleteOne(@Param(value = "ID") String ID);

    Integer insertALL(@Param(value = "objList")List<SCHEME_TYPEPojo> objList);

    Integer selectCount(@Param(value = "ID") String ID,
                        @Param(value = "key") String key,
                        @Param(value = "stime") String stime,
                        @Param(value = "etime") String etime);
}
