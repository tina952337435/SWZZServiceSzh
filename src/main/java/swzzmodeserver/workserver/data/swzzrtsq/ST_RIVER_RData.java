package swzzmodeserver.workserver.data.swzzrtsq;

import swzzmodeserver.workserver.pojo.swzzrtsq.ST_RIVER_RPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ST_RIVER_RData {
    Integer insertAll(@Param(value = "quPojo") List<ST_RIVER_RPojo> quPojo);
    Integer upsertAll(@Param(value = "quPojo") List<ST_RIVER_RPojo> quPojo);
    Integer upDateMaxRiver(ST_RIVER_RPojo mode);

    /**
     * 按站点+时间范围查询河道水位时序数据
     * @param stcdList 站点编码列表
     * @param stime 开始时间
     * @param etime 结束时间
     * @return 水位时序数据列表
     */
    List<ST_RIVER_RPojo> selectListByTime(
            @Param(value = "stcdList") List<String> stcdList,
            @Param(value = "stime") String stime,
            @Param(value = "etime") String etime);

    /**
     * 按时间范围查询有水位数据的所有站点（去重）
     * @param stime 开始时间
     * @param etime 结束时间
     * @return 站点编码列表
     */
    List<String> selectDistinctStcd(
            @Param(value = "stime") String stime,
            @Param(value = "etime") String etime);
}
