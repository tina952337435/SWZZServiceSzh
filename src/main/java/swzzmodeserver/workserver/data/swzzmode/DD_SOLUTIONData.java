package swzzmodeserver.workserver.data.swzzmode;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.workserver.pojo.swzzmode.BDMS_PREDICTPojo;
import swzzmodeserver.workserver.pojo.swzzmode.DD_SOLUTIONPojo;
import swzzmodeserver.workserver.pojo.swzzmode.DD_SOLUTION_YSPojo;

import java.util.List;

@Mapper
public interface DD_SOLUTIONData {

    List<DD_SOLUTIONPojo> selectList(@Param(value = "ID") String ID,
                                     @Param(value = "key") String key,
                                     @Param("mindList")List<String> mindList,
                                     @Param(value = "DD_DISTRIBY") String DD_DISTRIBY,
                                     @Param(value = "stime") String stime,
                                     @Param(value = "etime") String etime,
                                     @Param(value = "startindex") Integer startindex,@Param(value = "pagesize") Integer pagesize);

    Integer updateOne(DD_SOLUTIONPojo ddSolutionPojo);

    Integer insertOne(DD_SOLUTIONPojo ddSolutionPojo);

    Integer deleteOne(@Param(value = "ID") String ID);

    Integer insertALL(@Param(value = "objList") List<DD_SOLUTIONPojo> objList);

    Integer selectCount(@Param(value = "ID") String ID,
                        @Param(value = "key") String key,
                        @Param("mindList")List<String> mindList,
                        @Param(value = "stime") String stime,
                        @Param(value = "etime") String etime);

    List<DD_SOLUTIONPojo> selectListByDD_IDandDD_status(@Param(value = "DD_ID") String ID,
                                     @Param(value = "DD_status") String key,@Param(value = "DD_DISTRIBY") String DD_DISTRIBY,@Param(value = "DD_TM") String DD_TM
                                     );
    List<DD_SOLUTIONPojo> selectListNew(@Param("pathnameList")List<String> pathnameList,
                                     @Param(value = "stime") String stime,
                                     @Param(value = "etime") String etime);

    List<DD_SOLUTIONPojo> selectListDuo(@Param(value = "ID") String ID,
                                     @Param(value = "key") String key,
                                     @Param("mindList")List<String> mindList,
                                     @Param(value = "DD_DISTRIBY") String DD_DISTRIBY,
                                     @Param(value = "stime") String stime,
                                     @Param(value = "etime") String etime,
                                     @Param(value = "startindex") Integer startindex,@Param(value = "pagesize") Integer pagesize);
}
