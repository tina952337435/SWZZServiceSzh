package swzzmodeserver.workserver.data.swzzmode;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.workserver.pojo.swzzmode.ES_MODELGUANLIANPojo;
import swzzmodeserver.workserver.pojo.swzzmode.ES_MODELUSERPojo;

import java.util.List;

@Mapper
public interface ES_MODELUSERData {

    List<ES_MODELUSERPojo> selectList(@Param(value = "ID") String ID,
                                      @Param(value = "startindex") Integer startindex,@Param(value = "pagesize") Integer pagesize);

    Integer updateOne(ES_MODELUSERPojo esModeluserPojo);

    Integer insertOne(ES_MODELUSERPojo esModeluserPojo);

    Integer deleteOne(@Param(value = "ID") String ID);

    Integer insertALL(@Param(value = "objList") List<ES_MODELUSERPojo> objList);

    Integer selectCount(@Param(value = "ID") String ID);
}
