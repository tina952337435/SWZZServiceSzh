package swzzmodeserver.workserver.data.swzzmode;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.workserver.pojo.swzzmode.ES_PUMP_RNEWPojo;

import java.util.List;

@Mapper
public interface ES_PUMP_RNEWData {

    List<ES_PUMP_RNEWPojo> selectList(@Param(value = "id") String ID,
                                      @Param(value = "stime") String stime,
                                      @Param(value = "etime") String etime,
                                      @Param(value = "startindex") Integer startindex,
                                      @Param(value = "pagesize") Integer pagesize
                                    );

    Integer updateOne(ES_PUMP_RNEWPojo esPumpRnewPojo);

    Integer insertOne(ES_PUMP_RNEWPojo esPumpRnewPojo);

    Integer deleteOne(@Param(value = "id") String ID);

    Integer insertALL(@Param(value = "objList") List<ES_PUMP_RNEWPojo> objList);

    Integer selectCount(@Param(value = "id") String ID);
}