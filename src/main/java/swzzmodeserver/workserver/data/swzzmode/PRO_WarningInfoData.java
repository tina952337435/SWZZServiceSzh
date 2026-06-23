package swzzmodeserver.workserver.data.swzzmode;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.workserver.pojo.swzzmode.ES_ZHANDIANDATA_MODEPojo;
import swzzmodeserver.workserver.pojo.swzzmode.PRO_WarningInfoPojo;

import java.util.List;

@Mapper
public interface PRO_WarningInfoData {

    List<PRO_WarningInfoPojo> selectList(@Param(value = "ID") String ID,@Param(value = "key") String key,
                                         @Param(value = "stime") String stime,@Param(value = "etime") String etime,
                                         @Param(value = "type")List<String> type,
                                         @Param(value = "startindex") Integer startindex,@Param(value = "pagesize") Integer pagesize);

    Integer updateOne(PRO_WarningInfoPojo bdmsPredictPojo);

    Integer insertOne(PRO_WarningInfoPojo bdmsPredictPojo);

    Integer deleteOne(@Param(value = "ID") String ID);

    Integer selectCount(@Param(value = "ID") String ID,@Param(value = "key") String key,
                        @Param(value = "stime") String stime,@Param(value = "etime") String etime,
                        @Param(value = "type")List<String> type);

    //Integer insertALL(@Param(value = "objList")List<PRO_WarningInfoPojo> objList);
}
