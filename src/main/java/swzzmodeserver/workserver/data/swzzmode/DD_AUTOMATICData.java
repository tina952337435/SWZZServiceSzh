package swzzmodeserver.workserver.data.swzzmode;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.tools.ParamField;
import swzzmodeserver.workserver.pojo.swzzmode.COMPUTELISTPojo;
import swzzmodeserver.workserver.pojo.swzzmode.DD_AUTOMATICPojo;

import java.util.List;

@Mapper
public interface DD_AUTOMATICData {

    List<DD_AUTOMATICPojo> selectList(@Param(value = "ID") String ID,
                                      @Param(value = "startindex") Integer startindex,@Param(value = "pagesize") Integer pagesize);

    Integer updateOne(DD_AUTOMATICPojo ddAutomaticPojo);

    Integer insertOne(DD_AUTOMATICPojo ddAutomaticPojo);

    Integer deleteOne(@Param(value = "ID") String ID);

    Integer insertALL(@Param(value = "objList") List<DD_AUTOMATICPojo> objList);

    Integer deleteALL(@Param(value = "bpPojo")List<ParamField> bpPojo);

    Integer updateALL(@Param(value = "objList") List<DD_AUTOMATICPojo> objList);

    Integer selectCount(@Param(value = "ID") String ID);
}
