package swzzmodeserver.workserver.data.swzzdata;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.workserver.pojo.swzzdata.EmployeePojo;
import swzzmodeserver.workserver.pojo.swzzdata.MyiconPojo;

import java.util.List;

@Mapper
public interface MyiconData {

    List<MyiconPojo> selectList(@Param(value = "ID") String ID, @Param(value = "key") String key,
                                @Param(value = "stime") String stime, @Param(value = "etime") String etime,
                                @Param(value = "type")List<String> type,
                                @Param(value = "startindex") Integer startindex,@Param(value = "pagesize") Integer pagesize);

    Integer updateOne(MyiconPojo bdmsPredictPojo);

    Integer insertOne(MyiconPojo bdmsPredictPojo);

    Integer deleteOne(@Param(value = "ID") String ID);

    Integer selectCount(@Param(value = "ID") String ID, @Param(value = "key") String key,
                        @Param(value = "stime") String stime, @Param(value = "etime") String etime,
                        @Param(value = "type")List<String> type);
}
