package swzzmodeserver.workserver.data.swzzmode;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.workserver.pojo.swzzmode.BDMS_PREDICTPojo;
import swzzmodeserver.workserver.pojo.swzzmode.ES_RAINFALLPojo;
import swzzmodeserver.workserver.pojo.swzzmode.ES_SLTONGJIPojo;

import java.util.List;

@Mapper
public interface ES_SLTONGJIData {

    List<ES_SLTONGJIPojo> selectList(@Param(value = "ID") String ID,
            @Param(value = "type") String type,
            @Param(value = "PID") String pid,
            @Param(value = "startindex") Integer startindex, @Param(value = "pagesize") Integer pagesize);

    List<ES_SLTONGJIPojo> selectListByID(List<String> IDList,
            @Param(value = "type") String type,
            @Param(value = "PID") String pid,
            @Param(value = "startindex") Integer startindex, @Param(value = "pagesize") Integer pagesize);

    Integer updateOne(ES_SLTONGJIPojo esSltongjiPojo);

    Integer insertOne(ES_SLTONGJIPojo esSltongjiPojo);

    Integer deleteOne(@Param(value = "ID") String ID);

    Integer insertALL(@Param(value = "objList") List<ES_SLTONGJIPojo> objList);

    Integer selectCount(@Param(value = "ID") String ID, @Param(value = "type") String type,
            @Param(value = "PID") String pid);
}
