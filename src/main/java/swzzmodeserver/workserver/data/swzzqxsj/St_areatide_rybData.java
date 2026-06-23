package swzzmodeserver.workserver.data.swzzqxsj;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.workserver.pojo.swzzdata.EmployeePojo;
import swzzmodeserver.workserver.pojo.swzzqxsj.St_areatide_rybPojo;
import swzzmodeserver.workserver.pojo.swzzrtsq.tb_wind_informationPojo;

import java.util.List;

@Mapper
public interface St_areatide_rybData {

    List<St_areatide_rybPojo> selectList(@Param(value = "ID") String ID, @Param(value = "key") String key,
                                         @Param(value = "stime") String stime, @Param(value = "etime") String etime,
                                         @Param(value = "type")List<String> type,
                                         @Param(value = "startindex") Integer startindex,@Param(value = "pagesize") Integer pagesize);

    Integer updateOne(St_areatide_rybPojo bdmsPredictPojo);

    Integer insertOne(St_areatide_rybPojo bdmsPredictPojo);

    Integer deleteOne(@Param(value = "ID") String ID);

    Integer selectCount(@Param(value = "ID") String ID, @Param(value = "key") String key,
                        @Param(value = "stime") String stime, @Param(value = "etime") String etime,
                        @Param(value = "type")List<String> type);

    List<tb_wind_informationPojo> selecttb_wind_informationListtTop(@Param(value = "stcdList") List<String> stcdList,@Param(value = "stime") String stime,@Param(value = "etime") String etime);

}
