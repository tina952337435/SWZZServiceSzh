package swzzmodeserver.workserver.data.swzzmode;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.workserver.pojo.swzzmode.ES_PUMP_RPojo;

import java.util.List;

@Mapper
public interface ES_PUMP_RData {

    List<ES_PUMP_RPojo> selectList(@Param(value = "id") String ID,
                                  @Param(value = "stime") String stime,
                                  @Param(value = "etime") String etime,
                                  @Param(value = "startindex") Integer startindex,
                                  @Param(value = "pagesize") Integer pagesize);

    Integer updateOne(ES_PUMP_RPojo esPumpRPojo);

    Integer insertOne(ES_PUMP_RPojo esPumpRPojo);

    Integer deleteOne(@Param(value = "id") String ID);

    Integer insertALL(@Param(value = "objList") List<ES_PUMP_RPojo> objList);

    Integer selectCount(@Param(value = "id") String ID);

    List<ES_PUMP_RPojo> selectListNew(@Param(value = "stcd") String stcd,
                                  @Param(value = "stime") String stime,
                                  @Param(value = "etime") String etime);
    
}