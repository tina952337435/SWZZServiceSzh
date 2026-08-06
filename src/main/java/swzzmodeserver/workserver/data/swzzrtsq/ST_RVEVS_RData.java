package swzzmodeserver.workserver.data.swzzrtsq;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.workserver.pojo.swzzrtsq.ST_RVEVS_RPojo;

import java.util.List;

/**
 * SL323-2011 表90 河道水情极值表 (ST_RVEVS_R) 数据访问层
 */
@Mapper
public interface ST_RVEVS_RData {

    /**
     * 按站点+统计时段标志查询极值列表
     * @param stcdList 站点编码列表
     * @param sttdrcd 统计时段标志
     * @return 极值数据列表
     */
    List<ST_RVEVS_RPojo> selectListByStcdAndPeriod(
            @Param(value = "stcdList") List<String> stcdList,
            @Param(value = "sttdrcd") String sttdrcd);

    /**
     * 按站点+时段+时间范围查询极值列表
     * @param stcdList 站点编码列表
     * @param sttdrcd 统计时段标志
     * @param stime 开始时间
     * @param etime 结束时间
     * @return 极值数据列表
     */
    List<ST_RVEVS_RPojo> selectListByStcdAndPeriodAndTime(
            @Param(value = "stcdList") List<String> stcdList,
            @Param(value = "sttdrcd") String sttdrcd,
            @Param(value = "stime") String stime,
            @Param(value = "etime") String etime);

    /**
     * 按时间范围查询极值列表
     * @param stime 开始时间
     * @param etime 结束时间
     * @return 极值数据列表
     */
    List<ST_RVEVS_RPojo> selectListByTimeRange(
            @Param(value = "stime") String stime,
            @Param(value = "etime") String etime);

    /**
     * 批量插入
     * @param quPojo 极值数据列表
     * @return 受影响行数
     */
    Integer insertAll(@Param(value = "quPojo") List<ST_RVEVS_RPojo> quPojo);

    /**
     * 按站点+标志时间+统计时段标志查询单条记录
     * @param stcd 测站编码
     * @param idtm 标志时间
     * @param sttdrcd 统计时段标志
     * @return 极值数据
     */
    ST_RVEVS_RPojo selectOne(
            @Param(value = "stcd") String stcd,
            @Param(value = "idtm") String idtm,
            @Param(value = "sttdrcd") String sttdrcd);

    /**
     * 批量 MERGE（原子 upsert）：存在则更新，不存在则插入，无空档期
     * 基于联合主键 (STCD, IDTM, STTDRCD) 匹配
     * @param quPojo 极值数据列表
     * @return 受影响行数
     */
    Integer upsertAll(@Param(value = "quPojo") List<ST_RVEVS_RPojo> quPojo);
}
