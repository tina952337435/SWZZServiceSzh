package swzzmodeserver.workserver.data.swzzmode;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.workserver.pojo.swzzmode.PRO_WarningInfoPojo;
import swzzmodeserver.workserver.pojo.swzzmode.St_AstronomicalTide_BPojo;

import java.util.List;

@Mapper
public interface St_AstronomicalTide_BData {

    List<St_AstronomicalTide_BPojo> selectList(@Param(value = "ID") String ID,@Param(value = "key") String key,
                                               @Param(value = "stime") String stime,@Param(value = "etime") String etime,
                                               @Param(value = "type")List<String> type,
                                               @Param(value = "startindex") Integer startindex,@Param(value = "pagesize") Integer pagesize);

    Integer updateOne(St_AstronomicalTide_BPojo bdmsPredictPojo);

    Integer insertOne(St_AstronomicalTide_BPojo bdmsPredictPojo);

    Integer deleteOne(@Param(value = "ID") String ID);

    Integer selectCount(@Param(value = "ID") String ID,@Param(value = "key") String key,
                        @Param(value = "stime") String stime,@Param(value = "etime") String etime,
                        @Param(value = "type")List<String> type);

    //Integer insertALL(@Param(value = "objList")List<PRO_WarningInfoPojo> objList);
}
