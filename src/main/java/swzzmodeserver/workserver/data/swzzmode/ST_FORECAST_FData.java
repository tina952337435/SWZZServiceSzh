package swzzmodeserver.workserver.data.swzzmode;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.workserver.pojo.swzzmode.ST_FORECAST_FPojo;

import java.util.List;

@Mapper
public interface ST_FORECAST_FData {

    List<ST_FORECAST_FPojo> selectList(
                                 @Param(value = "STCDList") List<String> STCDList,
                                 @Param(value = "UNITNAMEList") List<String> UNITNAMEList,
                                 @Param(value = "FYMDH") String FYMDH);

    List<ST_FORECAST_FPojo>  selectListFast(
            @Param(value = "UNITNAME") String UNITNAME,
            @Param(value = "SFYMDH") String SFYMDH,@Param(value = "EFYMDH") String EFYMDH,
            @Param(value = "stime") String stime,@Param(value = "etime") String etime);

    List<ST_FORECAST_FPojo>  selectListFYMDH(
            @Param(value = "STCDList") List<String> STCDList,
            @Param(value = "UNITNAMEList") List<String> UNITNAMEList,
            @Param(value = "stime") String stime,@Param(value = "etime") String etime);

    Integer updateOne(ST_FORECAST_FPojo stStrvfcchSkbPojo);

    Integer insertOne(ST_FORECAST_FPojo stStrvfcchSkbPojo);

    Integer deleteOne(@Param(value = "ID") String ID, @Param(value = "UNITNAM") String UNITNAM, @Param(value = "FYMDH") String FYMDH, @Param(value = "IYMDH") String IYMDH, @Param(value = "YMDH") String YMDH);

    Integer insertALL(@Param(value = "objList") List<ST_FORECAST_FPojo> objList);

    Integer updateALL(@Param(value = "objList")List<ST_FORECAST_FPojo> objList);

    Integer deleteMore(@Param(value = "IDList") List<String> IDList);
}
