package swzzmodeserver.workserver.data.wds;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.workserver.pojo.wds.RTEVPojo;
import swzzmodeserver.workserver.pojo.swzzflood.ST_WAS_RPojo;

import java.util.List;

@Mapper
public interface RTEVData {
    List<RTEVPojo> selectList(@Param("stime") String stime, @Param("etime") String etime, @Param("stcdList") List<String> stcdList);

    List<RTEVPojo> ga(@Param("stime") String stime, @Param("etime") String etime, @Param("stcdList") List<String> stcdList);

    List<ST_WAS_RPojo> GetWaterData(@Param("idList") List<String> idList, @Param("stime") String stime);

    List<ST_WAS_RPojo> GetWaterDataLL(@Param("idList") List<String> idList, @Param("stime") String stime);

    List<ST_WAS_RPojo> GetWaterDataFX(@Param("idList") List<String> idList, @Param("stime") String stime, @Param("etime") String etime);

    List<ST_WAS_RPojo> selectHis(@Param("idList") List<String> idList, @Param("stime") String stime,@Param("etime") String etime);
    
}