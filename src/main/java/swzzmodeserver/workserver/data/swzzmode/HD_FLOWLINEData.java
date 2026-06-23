package swzzmodeserver.workserver.data.swzzmode;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.workserver.pojo.swzzmode.BDMS_PREDICTPojo;
import swzzmodeserver.workserver.pojo.swzzmode.ES_ZHANGUANLIANPojo;
import swzzmodeserver.workserver.pojo.swzzmode.HD_FLOWLINEPojo;

import java.util.List;

@Mapper
public interface HD_FLOWLINEData {

    List<HD_FLOWLINEPojo> selectList(@Param(value = "ID") String ID,
                                     @Param(value = "startindex") Integer startindex,@Param(value = "pagesize") Integer pagesize);

    Integer updateOne(HD_FLOWLINEPojo bdmsPredictPojo);

    Integer insertOne(HD_FLOWLINEPojo bdmsPredictPojo);

    Integer deleteOne(@Param(value = "ID") String ID);

    Integer insertALL(@Param(value = "objList")List<HD_FLOWLINEPojo> objList);

    Integer selectCount(@Param(value = "ID") String ID);
}
