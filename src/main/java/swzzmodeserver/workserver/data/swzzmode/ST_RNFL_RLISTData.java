package swzzmodeserver.workserver.data.swzzmode;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.workserver.pojo.swzzmode.ST_RNFL_RLISTPojo;

import java.util.List;

/**
 * 降雨预报文件列表表 数据访问层
 */
@Mapper
public interface ST_RNFL_RLISTData {

    List<ST_RNFL_RLISTPojo> selectList(
            @Param(value = "ID") String ID,
            @Param(value = "stime") String stime,
            @Param(value = "etime") String etime,
            @Param(value = "startindex") Integer startindex,
            @Param(value = "pagesize") Integer pagesize);

    Integer selectCount(
            @Param(value = "ID") String ID,
            @Param(value = "stime") String stime,
            @Param(value = "etime") String etime);

    Integer insertOne(ST_RNFL_RLISTPojo pojo);

    Integer insertALL(@Param(value = "objList") List<ST_RNFL_RLISTPojo> objList);

    Integer updateOne(ST_RNFL_RLISTPojo pojo);

    Integer deleteOne(
            @Param(value = "ID") String ID,
            @Param(value = "TM") String TM,
            @Param(value = "FPDR") Double FPDR);
}
