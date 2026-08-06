package swzzmodeserver.workserver.data.swzzmode;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.workserver.pojo.swzzmode.ST_RNFL_RMODEPojo;

import java.util.List;

/**
 * 降雨预报模式数据表 数据访问层
 */
@Mapper
public interface ST_RNFL_RMODEData {

        List<ST_RNFL_RMODEPojo> selectList(
                        @Param(value = "STCD") String STCD,
                        @Param(value = "stime") String stime,
                        @Param(value = "etime") String etime,
                        @Param(value = "startindex") Integer startindex,
                        @Param(value = "pagesize") Integer pagesize);

        Integer selectCount(
                        @Param(value = "STCD") String STCD,
                        @Param(value = "stime") String stime,
                        @Param(value = "etime") String etime);

        Integer insertOne(ST_RNFL_RMODEPojo pojo);

        Integer insertALL(@Param(value = "objList") List<ST_RNFL_RMODEPojo> objList);

        Integer updateOne(ST_RNFL_RMODEPojo pojo);

        Integer deleteOne(
                        @Param(value = "STCD") String STCD,
                        @Param(value = "YBTM") String YBTM,
                        @Param(value = "TM") String TM,
                        @Param(value = "FPDR") Double FPDR);

        /**
         * 按站点+时间范围查询最新预报数据
         * 每个 (STCD, TM) 取最新 YBTM 的记录
         */
        List<ST_RNFL_RMODEPojo> selectListLastBySTCD(
                        @Param(value = "stcdList") List<String> stcdList,
                        @Param(value = "stime") String stime,
                        @Param(value = "etime") String etime,
                        @Param(value = "fpdr") List<String> fpdr,
                        @Param(value = "type") String type);
}
