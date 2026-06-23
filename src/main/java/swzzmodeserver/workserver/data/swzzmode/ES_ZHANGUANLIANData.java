package swzzmodeserver.workserver.data.swzzmode;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.workserver.pojo.swzzmode.BDMS_PREDICTPojo;
import swzzmodeserver.workserver.pojo.swzzmode.ES_ZHANDIANXSPojo;
import swzzmodeserver.workserver.pojo.swzzmode.ES_ZHANGUANLIANPojo;

import java.util.List;

@Mapper
public interface ES_ZHANGUANLIANData {

    List<ES_ZHANGUANLIANPojo> selectList(@Param(value = "ID") String ID,
                                         @Param(value = "startindex") Integer startindex,@Param(value = "pagesize") Integer pagesize,
                                         @Param(value = "type") List<String> type);

    Integer updateOne(ES_ZHANGUANLIANPojo bdmsPredictPojo);

    Integer insertOne(ES_ZHANGUANLIANPojo bdmsPredictPojo);

    Integer deleteOne(@Param(value = "ID") String ID);

    Integer insertALL(@Param(value = "objList")List<ES_ZHANGUANLIANPojo> objList);

    Integer selectCount(@Param(value = "ID") String ID,
                        @Param(value = "type") List<String> type);
}
