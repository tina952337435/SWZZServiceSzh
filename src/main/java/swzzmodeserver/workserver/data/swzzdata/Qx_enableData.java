package swzzmodeserver.workserver.data.swzzdata;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.workserver.pojo.swzzdata.MyiconPojo;
import swzzmodeserver.workserver.pojo.swzzdata.Qx_enablePojo;

import java.util.List;

@Mapper
public interface Qx_enableData {

    List<Qx_enablePojo> selectList(@Param(value = "ID") String ID, @Param(value = "key") String key,
                                   @Param(value = "stime") String stime, @Param(value = "etime") String etime,
                                   @Param(value = "type")List<String> type,
                                   @Param(value = "startindex") Integer startindex,@Param(value = "pagesize") Integer pagesize);

    Integer updateOne(Qx_enablePojo bdmsPredictPojo);

    Integer insertOne(Qx_enablePojo bdmsPredictPojo);

    Integer deleteOne(@Param(value = "ID") String ID);

    Integer instertAll(@Param(value = "QxobjList")List<Qx_enablePojo> QxobjList);

    Integer updateAll(@Param(value = "QxobjList")List<Qx_enablePojo> QxobjList);

    Integer selectCount(@Param(value = "ID") String ID, @Param(value = "key") String key,
                        @Param(value = "stime") String stime, @Param(value = "etime") String etime,
                        @Param(value = "type")List<String> type);
}
