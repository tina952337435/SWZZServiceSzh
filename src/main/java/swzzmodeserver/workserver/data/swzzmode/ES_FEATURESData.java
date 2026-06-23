package swzzmodeserver.workserver.data.swzzmode;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.workserver.pojo.swzzmode.BDMS_PREDICTPojo;
import swzzmodeserver.workserver.pojo.swzzmode.DD_SOLUTIONPojo;
import swzzmodeserver.workserver.pojo.swzzmode.ES_FEATURESPojo;

import java.util.List;

@Mapper
public interface ES_FEATURESData {

    List<ES_FEATURESPojo> selectList(@Param(value = "ID") String ID,
                                     @Param(value = "startindex") Integer startindex,@Param(value = "pagesize") Integer pagesize);

    Integer updateOne(ES_FEATURESPojo esFeaturesPojo);

    Integer insertOne(ES_FEATURESPojo esFeaturesPojo);

    Integer deleteOne(@Param(value = "ID") String ID);

    Integer insertALL(@Param(value = "objList") List<ES_FEATURESPojo> objList);

    Integer selectCount(@Param(value = "ID") String ID);
}
