package swzzmodeserver.workserver.data.swzzmode;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.workserver.pojo.swzzmode.BDMS_PREDICTPojo;
import swzzmodeserver.workserver.pojo.swzzmode.ES_ZHANDIANDATAPojo;
import swzzmodeserver.workserver.pojo.swzzmode.ES_ZHANDIANPXPojo;

import java.util.List;

@Mapper
public interface ES_ZHANDIANPXData {

    List<ES_ZHANDIANPXPojo> selectList(@Param(value = "ID") String ID,
                                       @Param(value = "startindex") Integer startindex,@Param(value = "pagesize") Integer pagesize);

    Integer updateOne(ES_ZHANDIANPXPojo esZhandianpxPojo);

    Integer insertOne(ES_ZHANDIANPXPojo esZhandianpxPojo);

    Integer deleteOne(@Param(value = "ID") String ID);

    Integer insertALL(@Param(value = "objList")List<ES_ZHANDIANPXPojo> objList);

    Integer selectCount(@Param(value = "ID") String ID);
}
