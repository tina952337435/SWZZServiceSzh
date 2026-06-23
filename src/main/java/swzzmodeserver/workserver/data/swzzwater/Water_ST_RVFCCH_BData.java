package swzzmodeserver.workserver.data.swzzwater;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.workserver.pojo.swzzwater.Water_ST_RVFCCH_BPojo;

import java.util.List;

@Mapper
public interface Water_ST_RVFCCH_BData {

    List<Water_ST_RVFCCH_BPojo> selectList(@Param(value = "ID") String ID,@Param(value = "stime") String stime,
                                     @Param(value = "etime") String etime,
                                     @Param(value = "startindex") Integer startindex,@Param(value = "pagesize") Integer pagesize);

    Integer updateOne(Water_ST_RVFCCH_BPojo stRvfcchBPojo);

    Integer insertOne(Water_ST_RVFCCH_BPojo stRvfcchBPojo);

    Integer deleteOne(@Param(value = "ID") String ID);

    Integer selectCount(@Param(value = "ID") String ID,@Param(value = "stime") String stime,
                        @Param(value = "etime") String etime);

    //Integer insertALL(@Param(value = "objList")List<ST_RVFCCH_BPojo> objList);
}
