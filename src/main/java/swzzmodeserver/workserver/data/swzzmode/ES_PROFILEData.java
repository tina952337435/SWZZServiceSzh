package swzzmodeserver.workserver.data.swzzmode;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.workserver.pojo.swzzmode.BDMS_PREDICTPojo;
import swzzmodeserver.workserver.pojo.swzzmode.ES_MODELUSERPojo;
import swzzmodeserver.workserver.pojo.swzzmode.ES_PROFILEPojo;

import java.util.List;

@Mapper
public interface ES_PROFILEData {

    List<ES_PROFILEPojo> selectList(@Param(value = "ID") String ID,
                                    @Param(value = "startindex") Integer startindex,@Param(value = "pagesize") Integer pagesize);

    Integer updateOne(ES_PROFILEPojo esProfilePojo);

    Integer insertOne(ES_PROFILEPojo esProfilePojo);

    Integer deleteOne(@Param(value = "ID") String ID);

    Integer insertALL(@Param(value = "objList") List<ES_PROFILEPojo> objList);

    Integer selectCount(@Param(value = "ID") String ID);
}
