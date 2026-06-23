package swzzmodeserver.workserver.data.swzzmode;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.workserver.pojo.swzzmode.BDMS_PREDICTPojo;
import swzzmodeserver.workserver.pojo.swzzmode.ES_JISUANZHANPXPojo;
import swzzmodeserver.workserver.pojo.swzzmode.ES_MBMANAGEPojo;

import java.util.List;

@Mapper
public interface ES_MBMANAGEData {

    List<ES_MBMANAGEPojo> selectList(@Param(value = "ID") String ID,
                                     @Param(value = "startindex") Integer startindex,@Param(value = "pagesize") Integer pagesize);

    Integer updateOne(ES_MBMANAGEPojo es_mbmanagePojo);

    Integer insertOne(ES_MBMANAGEPojo es_mbmanagePojo);

    Integer deleteOne(@Param(value = "ID") String ID);

    Integer insertALL(@Param(value = "objList") List<ES_MBMANAGEPojo> objList);

    Integer selectCount(@Param(value = "ID") String ID);
}
