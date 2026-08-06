package swzzmodeserver.workserver.data.swzzrtsq;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.workserver.pojo.swzzrtsq.ST_RVAV_RPojo;

import java.util.List;

/**
 * SL323-2011 表79 河道水情多日均值表 (ST_RVAV_R) 数据访问层
 */
@Mapper
public interface ST_RVAV_RData {

    /**
     * 按站点+统计时段标志查询均值列表
     */
    List<ST_RVAV_RPojo> selectListByStcdAndPeriod(
            @Param(value = "stcdList") List<String> stcdList,
            @Param(value = "sttdrcd") String sttdrcd);

    /**
     * 按站点+时段+时间范围查询均值列表
     */
    List<ST_RVAV_RPojo> selectListByStcdAndPeriodAndTime(
            @Param(value = "stcdList") List<String> stcdList,
            @Param(value = "sttdrcd") String sttdrcd,
            @Param(value = "stime") String stime,
            @Param(value = "etime") String etime);

    /**
     * 批量 MERGE（原子 upsert）
     */
    Integer upsertAll(@Param(value = "quPojo") List<ST_RVAV_RPojo> quPojo);
}
