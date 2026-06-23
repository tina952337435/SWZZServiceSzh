package swzzmodeserver.workserver.data.swzzmode;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.workserver.pojo.swzzmode.ES_PUMP_BPojo;

import java.util.List;

@Mapper
public interface ES_PUMP_BData {

    List<ES_PUMP_BPojo> selectList(@Param(value = "ID") String ID,
                                  @Param(value = "startindex") Integer startindex,@Param(value = "pagesize") Integer pagesize);

    Integer updateOne(ES_PUMP_BPojo esPumpBPojo);

    Integer insertOne(ES_PUMP_BPojo esPumpBPojo);

    Integer deleteOne(@Param(value = "ID") String ID);

    Integer insertALL(@Param(value = "objList") List<ES_PUMP_BPojo> objList);

    Integer selectCount(@Param(value = "ID") String ID);
}