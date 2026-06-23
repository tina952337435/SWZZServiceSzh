package swzzmodeserver.workserver.data.swzzrtsq;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.workserver.pojo.swzzrtsq.AREARAIN_HPojo;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface AREARAIN_HData {

    List<AREARAIN_HPojo> selectList(@Param(value = "STCDList") List<String> STCDList,@Param(value = "stime") String stime,@Param(value = "etime") String etime);

    /**
     * 计算过去 N 小时的累积雨量
     * 逻辑：SUM(RR) WHERE TM BETWEEN (Now - N hours) AND Now
     */
    BigDecimal sumRainByHours(@Param("stcd") String stcd, @Param("hours") int hours,@Param(value = "etime") String etime);

    /**
     * 获取最近的降雨明细（用于计算场次雨量）
     * 按时间倒序排列，方便 Java 代码判断雨停间隔
     */
    List<AREARAIN_HPojo> selectRecentDetails(@Param("stcd") String stcd, @Param("limitHours") int limitHours,@Param(value = "etime") String etime);

    Integer insertOne(AREARAIN_HPojo bPojo);

    Integer upDateOne(AREARAIN_HPojo bPojo);

    Integer deleteOne(@Param(value = "ID") String ID);
}
