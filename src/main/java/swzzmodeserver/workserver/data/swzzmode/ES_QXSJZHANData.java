package swzzmodeserver.workserver.data.swzzmode;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.workserver.pojo.swzzmode.BDMS_PREDICTPojo;
import swzzmodeserver.workserver.pojo.swzzmode.ES_PROFILEPojo;
import swzzmodeserver.workserver.pojo.swzzmode.ES_QXSJZHANPojo;

import java.util.List;

@Mapper
public interface ES_QXSJZHANData {

    List<ES_QXSJZHANPojo> selectList(@Param(value = "ID") String ID,
                                     @Param(value = "startindex") Integer startindex,@Param(value = "pagesize") Integer pagesize);

    Integer updateOne(ES_QXSJZHANPojo bdmsPredictPojo);

    Integer insertOne(ES_QXSJZHANPojo bdmsPredictPojo);

    Integer deleteOne(@Param(value = "ID") String ID);

    Integer insertALL(@Param(value = "objList") List<ES_QXSJZHANPojo> objList);

    Integer selectCount(@Param(value = "ID") String ID);
}
