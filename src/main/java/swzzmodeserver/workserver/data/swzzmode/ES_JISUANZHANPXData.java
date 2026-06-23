package swzzmodeserver.workserver.data.swzzmode;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.workserver.pojo.swzzmode.BDMS_PREDICTPojo;
import swzzmodeserver.workserver.pojo.swzzmode.ES_JISUANZHANPXPojo;
import swzzmodeserver.workserver.pojo.swzzmode.ES_JISUANZHANPojo;

import java.util.List;

@Mapper
public interface ES_JISUANZHANPXData {

    List<ES_JISUANZHANPXPojo> selectList(@Param(value = "ID") String ID,
                                         @Param(value = "startindex") Integer startindex,@Param(value = "pagesize") Integer pagesize);

    Integer updateOne(ES_JISUANZHANPXPojo esJisuanzhanpxPojo);

    Integer insertOne(ES_JISUANZHANPXPojo esJisuanzhanpxPojo);

    Integer deleteOne(@Param(value = "ID") String ID);

    Integer insertALL(@Param(value = "objList") List<ES_JISUANZHANPXPojo> objList);

    Integer selectCount(@Param(value = "ID") String ID);
}
