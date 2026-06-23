package swzzmodeserver.workserver.data.swzzmode;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.workserver.pojo.swzzmode.BDMS_PREDICTPojo;
import swzzmodeserver.workserver.pojo.swzzmode.DD_AUTOMATICPojo;
import swzzmodeserver.workserver.pojo.swzzmode.DD_SOLUTION_AUTOPojo;

import java.util.List;

@Mapper
public interface DD_SOLUTION_AUTOData {

    List<DD_SOLUTION_AUTOPojo> selectList(@Param(value = "ID") String ID,
                                          @Param(value = "stime") String stime,
                                          @Param(value = "etime") String etime,
                                          @Param(value = "startindex") Integer startindex,@Param(value = "pagesize") Integer pagesize);

    Integer updateOne(DD_SOLUTION_AUTOPojo ddSolutionAutoPojo);

    Integer insertOne(DD_SOLUTION_AUTOPojo ddSolutionAutoPojo);

    Integer deleteOne(@Param(value = "ID") String ID);

    Integer insertALL(@Param(value = "objList") List<DD_SOLUTION_AUTOPojo> objList);

    Integer selectCount(@Param(value = "ID") String ID,@Param(value = "stime") String stime,
                        @Param(value = "etime") String etime);
}
