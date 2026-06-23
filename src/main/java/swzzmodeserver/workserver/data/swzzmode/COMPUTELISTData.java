package swzzmodeserver.workserver.data.swzzmode;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.workserver.pojo.swzzmode.COMPUTELISTPojo;

import java.util.List;

@Mapper
public interface COMPUTELISTData {

    List<COMPUTELISTPojo> selectList(@Param(value = "ID") String ID,@Param(value = "key") String key,
                                     @Param(value = "startindex") Integer startindex,@Param(value = "pagesize") Integer pagesize);

    Integer updateOne(COMPUTELISTPojo computelistPojo);

    Integer insertOne(COMPUTELISTPojo computelistPojo);

    Integer deleteOne(@Param(value = "ID") String ID);

    Integer insertALL(@Param(value = "objList") List<COMPUTELISTPojo> objList);

    Integer selectCount(@Param(value = "ID") String ID,@Param(value = "key") String key);
}
