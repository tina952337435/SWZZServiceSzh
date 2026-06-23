package swzzmodeserver.workserver.data.swzzmode;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.workserver.pojo.swzzmode.BDMS_PREDICTPojo;
import swzzmodeserver.workserver.pojo.swzzmode.ES_TIDALFORECASTPojo;
import swzzmodeserver.workserver.pojo.swzzqxsj.St_rnfl_fPojo;

import java.util.Date;
import java.util.List;

@Mapper
public interface ES_TIDALFORECASTData {

    List<ES_TIDALFORECASTPojo> selectList(@Param(value = "ID") String ID, @Param(value = "key") String key,
                                         @Param(value = "stime") String stime, @Param(value = "etime") String etime,
                                          @Param(value = "ybstime") String ybstime, @Param(value = "ybetime") String ybetime,
                                         @Param(value = "type")List<String> type,
                                         @Param(value = "startindex") Integer startindex,@Param(value = "pagesize") Integer pagesize);

    List<ES_TIDALFORECASTPojo> selectMaxFast(@Param(value = "stcd") String stcd,
                                             @Param(value = "stime") String stime, @Param(value = "etime") String etime);

    Integer insertALL(@Param(value = "objList") List<ES_TIDALFORECASTPojo> objList);

    Integer insertOne(ES_TIDALFORECASTPojo bdmsPredictPojo);

    Integer updateALLSTATUS(@Param(value = "list")List<ES_TIDALFORECASTPojo> list);

    Date selectMaxYBTM(@Param(value = "ybstime") String ybstime,
                       @Param(value = "ybetime") String ybetime,
                       @Param("stcdList") List<String> stcdList);
}
