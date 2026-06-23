package swzzmodeserver.workserver.data.swzzmode;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.workserver.pojo.swzzmode.BDMS_PREDICTPojo;
import swzzmodeserver.workserver.pojo.swzzmode.ES_FEATURESPojo;
import swzzmodeserver.workserver.pojo.swzzmode.ES_JISUANZHANPojo;

import java.util.List;

@Mapper
public interface ES_JISUANZHANData {

    List<ES_JISUANZHANPojo> selectList(@Param(value = "ID") String ID,
                                       @Param(value = "type") List<String> type,
                                       @Param(value = "key") String key,
                                       @Param(value = "startindex") Integer startindex,@Param(value = "pagesize") Integer pagesize);

    Integer updateOne(ES_JISUANZHANPojo esJisuanzhanPojo);

    Integer insertOne(ES_JISUANZHANPojo esJisuanzhanPojo);

    Integer deleteOne(@Param(value = "ID") String ID);

    Integer insertALL(@Param(value = "objList") List<ES_JISUANZHANPojo> objList);

    Integer selectCount(@Param(value = "ID") String ID,
                        @Param(value = "type") List<String> type,@Param(value = "key") String key);
}
