package swzzmodeserver.workserver.data.swzzmode;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.workserver.pojo.swzzmode.BDMS_PREDICTPojo;
import swzzmodeserver.workserver.pojo.swzzmode.SDE_AREAPojo;
import swzzmodeserver.workserver.pojo.swzzmode.SDE_HDDMPojo;

import java.util.List;

@Mapper
public interface SDE_HDDMData {

    List<SDE_HDDMPojo> selectList(@Param(value = "ID") Integer ID,
                                  @Param(value = "startindex") Integer startindex,@Param(value = "pagesize") Integer pagesize);

    Integer updateOne(SDE_HDDMPojo sdeHddmPojo);

    Integer insertOne(SDE_HDDMPojo sdeHddmPojo);

    Integer deleteOne(@Param(value = "ID") Integer ID);

    Integer insertALL(@Param(value = "objList") List<SDE_HDDMPojo> objList);

    Integer selectCount(@Param(value = "ID") Integer ID);
}
