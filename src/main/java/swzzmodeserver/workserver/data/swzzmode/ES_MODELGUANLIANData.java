package swzzmodeserver.workserver.data.swzzmode;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.workserver.pojo.swzzmode.ES_MODELFANGANZHANPojo;
import swzzmodeserver.workserver.pojo.swzzmode.ES_MODELGUANLIANPojo;

import java.util.List;

@Mapper
public interface ES_MODELGUANLIANData {

    List<ES_MODELGUANLIANPojo> selectList(@Param(value = "ID") String ID,
            @Param(value = "type") String type,
            @Param(value = "startindex") Integer startindex,
            @Param(value = "pagesize") Integer pagesize);

    List<ES_MODELGUANLIANPojo> selectListByID(@Param(value = "IDList") List<String> IDList,
            @Param(value = "type") String type,
            @Param(value = "startindex") Integer startindex,
            @Param(value = "pagesize") Integer pagesize);

    Integer updateOne(ES_MODELGUANLIANPojo bdmsPredictPojo);

    Integer insertOne(ES_MODELGUANLIANPojo bdmsPredictPojo);

    Integer deleteOne(@Param(value = "ID") String ID, @Param(value = "type") String type);

    Integer insertALL(@Param(value = "objList") List<ES_MODELGUANLIANPojo> objList);

    Integer selectCount(@Param(value = "ID") String ID, @Param(value = "type") String type);
}
