package swzzmodeserver.workserver.data.swzzdata;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.workserver.pojo.swzzdata.EmployeePojo;
import java.util.List;

@Mapper
public interface EmployeeData {

    List<EmployeePojo> selectList(@Param(value = "ID") String ID,
                                  @Param(value = "QX_LOGIN") String QX_LOGIN,
                                  @Param(value = "PWD") String PWD,
                                  @Param(value = "key") String key,
                                  @Param(value = "stime") String stime,@Param(value = "etime") String etime,
                                  @Param(value = "type")List<String> type,
                                  @Param(value = "startindex") Integer startindex,@Param(value = "pagesize") Integer pagesize);

    List<EmployeePojo> selectListPWD(@Param(value = "ID") String ID,
                                  @Param(value = "QX_LOGIN") String QX_LOGIN,
                                  @Param(value = "PWD") String PWD,
                                  @Param(value = "key") String key,
                                  @Param(value = "stime") String stime,@Param(value = "etime") String etime,
                                  @Param(value = "type")List<String> type,
                                  @Param(value = "startindex") Integer startindex,@Param(value = "pagesize") Integer pagesize);


    Integer updateOne(EmployeePojo bdmsPredictPojo);

    Integer insertOne(EmployeePojo bdmsPredictPojo);

    Integer deleteOne(@Param(value = "ID") String ID);

    Integer selectCount(@Param(value = "ID") String ID,
                        @Param(value = "QX_LOGIN") String QX_LOGIN,
                        @Param(value = "key") String key,
                        @Param(value = "stime") String stime,@Param(value = "etime") String etime,
                        @Param(value = "type")List<String> type);
}
