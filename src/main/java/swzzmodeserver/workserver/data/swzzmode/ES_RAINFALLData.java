package swzzmodeserver.workserver.data.swzzmode;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.tools.ParamField;
import swzzmodeserver.workserver.pojo.swzzmode.BDMS_PREDICTPojo;
import swzzmodeserver.workserver.pojo.swzzmode.ES_RAINFALLPojo;
import swzzmodeserver.workserver.pojo.swzzmode.ES_RAINFALL_TREEPojo;

import java.util.List;

@Mapper
public interface ES_RAINFALLData {

    List<ES_RAINFALLPojo> selectList(@Param(value = "ID") String ID,
                                     @Param(value = "type") List<String> type,
                                     @Param(value = "key") String key,
                                     @Param(value = "zhanidList") List<String> zhanidList,
                                     @Param(value = "startindex") Integer startindex,@Param(value = "pagesize") Integer pagesize);

    Integer updateOne(ES_RAINFALLPojo esRainfallPojo);

    Integer insertOne(ES_RAINFALLPojo esRainfallPojo);

    Integer deleteOne(@Param(value = "ID") String ID);

    Integer insertALL(@Param(value = "objList") List<ES_RAINFALLPojo> objList);

    Integer deleteALL(@Param(value = "bpPojo")List<ParamField> bpPojo);

    Integer updateALL(@Param(value = "objList") List<ES_RAINFALLPojo> objList);

    Integer selectCount(@Param(value = "ID") String ID,@Param(value = "type") List<String> type,
                        @Param(value = "key") String key,
                        @Param(value = "zhanidList") List<String> zhanidList
                        );
}
