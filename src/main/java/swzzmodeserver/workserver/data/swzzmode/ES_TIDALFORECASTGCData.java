package swzzmodeserver.workserver.data.swzzmode;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.workserver.pojo.swzzflood.TB_TIDE_HIGHLOWPojo;
import swzzmodeserver.workserver.pojo.swzzmode.ES_TIDALFORECASTGCPojo;

import java.util.Date;
import java.util.List;

@Mapper
public interface ES_TIDALFORECASTGCData {

    List<ES_TIDALFORECASTGCPojo> selectList(@Param(value = "ID") String ID, @Param(value = "key") String key,
                                         @Param(value = "stime") String stime, @Param(value = "etime") String etime,
                                          @Param(value = "ybstime") String ybstime, @Param(value = "ybetime") String ybetime,
                                         @Param(value = "type")List<String> type,
                                         @Param(value = "startindex") Integer startindex,@Param(value = "pagesize") Integer pagesize);

    Integer insertALL(@Param(value = "objList") List<ES_TIDALFORECASTGCPojo> objList);

    Integer insertOne(ES_TIDALFORECASTGCPojo bdmsPredictPojo);

    Date selectMaxYBTM(@Param(value = "ybstime") String ybstime, @Param(value = "ybetime") String ybetime);

    List<ES_TIDALFORECASTGCPojo>  selectMaxFast(@Param(value = "stcd") String stcd);

    List<TB_TIDE_HIGHLOWPojo>  selectMaxTBTIDE(@Param(value = "stcd") String stcd,@Param(value = "stime") String stime, @Param(value = "etime") String etime);
}
