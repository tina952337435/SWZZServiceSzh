package swzzmodeserver.workserver.data.swzzmode;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.workserver.pojo.swzzmode.BDMS_PREDICTPojo;
import swzzmodeserver.workserver.pojo.swzzmode.ES_ZHANDIANDATAPojo;
import swzzmodeserver.workserver.pojo.swzzmode.ES_ZHANDIANPXPojo;
import swzzmodeserver.workserver.pojo.swzzmode.ES_ZHANDIANXSPojo;

import java.util.List;

@Mapper
public interface ES_ZHANDIANXSData {

    List<ES_ZHANDIANXSPojo> selectList(@Param(value = "ID") String ID,
                                       @Param(value = "startindex") Integer startindex,@Param(value = "pagesize") Integer pagesize);

    Integer updateOne(ES_ZHANDIANXSPojo bdmsPredictPojo);

    Integer insertOne(ES_ZHANDIANXSPojo bdmsPredictPojo);

    Integer deleteOne(@Param(value = "ID") String ID);

    Integer insertALL(@Param(value = "objList")List<ES_ZHANDIANXSPojo> objList);

    Integer selectCount(@Param(value = "ID") String ID);

    Integer updateALL(@Param(value = "list")List<ES_ZHANDIANXSPojo> list);
}
