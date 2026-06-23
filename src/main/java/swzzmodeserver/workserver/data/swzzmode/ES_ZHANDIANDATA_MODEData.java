package swzzmodeserver.workserver.data.swzzmode;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.tools.ParamField;
import swzzmodeserver.workserver.pojo.swzzmode.ES_ZHANDIANDATAPojo;
import swzzmodeserver.workserver.pojo.swzzmode.ES_ZHANDIANDATA_MODEPojo;

import java.util.List;

@Mapper
public interface ES_ZHANDIANDATA_MODEData {

    List<ES_ZHANDIANDATA_MODEPojo> selectList(@Param(value = "IDList") List<String> ID,
                                              @Param(value = "startindex") Integer startindex,@Param(value = "pagesize") Integer pagesize);

    Integer updateOne(ES_ZHANDIANDATA_MODEPojo bdmsPredictPojo);

    Integer insertOne(ES_ZHANDIANDATA_MODEPojo bdmsPredictPojo);

    Integer deleteOne(@Param(value = "ID") String ID);

    Integer insertALL(@Param(value = "objList")List<ES_ZHANDIANDATA_MODEPojo> objList);

    Integer deleteALL(@Param(value = "bpPojo")List<ParamField> bpPojo);

    Integer updateALL(@Param(value = "objList")List<ES_ZHANDIANDATA_MODEPojo> objList);

    Integer selectCount(@Param(value = "IDList") List<String> ID);
}
