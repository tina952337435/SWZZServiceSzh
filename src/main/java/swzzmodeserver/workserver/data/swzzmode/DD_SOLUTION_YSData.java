package swzzmodeserver.workserver.data.swzzmode;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.workserver.pojo.swzzmode.BDMS_PREDICTPojo;
import swzzmodeserver.workserver.pojo.swzzmode.DD_SOLUTION_AUTOPojo;
import swzzmodeserver.workserver.pojo.swzzmode.DD_SOLUTION_YSPojo;

import java.util.List;

@Mapper
public interface DD_SOLUTION_YSData {

    List<DD_SOLUTION_YSPojo> selectList(@Param(value = "ID") String ID,
                                        @Param(value = "startindex") Integer startindex,@Param(value = "pagesize") Integer pagesize);

    Integer updateOne(DD_SOLUTION_YSPojo ddSolutionYsPojo);

    Integer insertOne(DD_SOLUTION_YSPojo ddSolutionYsPojo);

    Integer deleteOne(@Param(value = "ID") String ID);

    Integer insertALL(@Param(value = "objList") List<DD_SOLUTION_YSPojo> objList);

    Integer selectCount(String id);
}
