package swzzmodeserver.workserver.data.swzzflood;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.workserver.pojo.swzzflood.ST_WAS_RPojo;

import java.util.List;
import java.util.Map;

@Mapper
public interface RTSQData {

    List<ST_WAS_RPojo> getSCSW(@Param("stime") String stime, @Param("etime") String etime,
                               @Param("idList") List<String> idList,
                               @Param("hour")String hour);

    List<ST_WAS_RPojo> getSCSWLS(@Param("stime") String stime, @Param("etime") String etime,
                                 @Param("idList") List<String> idList,
                                 @Param("hour")String hour);

    List<ST_WAS_RPojo> GetWaterData(@Param("stime") String stime, @Param("etime") String etime,
                               @Param("idList") List<String> idList,
                               @Param("hour")String hour);


    List<ST_WAS_RPojo> GetWaterDataLL(@Param("stime") String stime, @Param("etime") String etime,
                                    @Param("idList") List<String> idList,
                                    @Param("hour")String hour);

    List<Map<String,Object>> getSCHOUR(@Param("stime") String stime, @Param("etime") String etime,
                                        @Param("idList") List<String> idList);
    List<Map<String,Object>> getSCJY(@Param("stime") String stime, @Param("etime") String etime,
                                     @Param("idList") List<String> idList);

    List<Map<String,Object>> getSCJYDAY(@Param("stime") String stime, @Param("etime") String etime,
                                        @Param("idList") List<String> idList);

    List<Map<String,Object>> getSCJY_5MIN(@Param("stime") String stime, @Param("etime") String etime,
                                          @Param("idList") List<String> idList);
}
