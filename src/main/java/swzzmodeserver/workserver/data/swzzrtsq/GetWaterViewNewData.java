package swzzmodeserver.workserver.data.swzzrtsq;

import swzzmodeserver.workserver.pojo.swzzrtsq.GetWaterViewNewPojo;
import swzzmodeserver.workserver.pojo.swzzrtsq.ST_STBPRP_BPojo;
import swzzmodeserver.workserver.pojo.swzzrtsq.ST_WAS_RPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface GetWaterViewNewData {

    List<GetWaterViewNewPojo> selectListByNew(@Param(value = "stcdList") List<String> stcdList,@Param(value = "stime") String stime,@Param(value = "etime") String etime);

    List<GetWaterViewNewPojo> selectListByHisIsTime(@Param(value = "stcdList") List<String> stcdList,@Param(value = "stime") String stime,@Param(value = "etime") String etime,@Param(value = "dayhour") String dayhour,@Param(value = "mtype") String mtype);

    List<GetWaterViewNewPojo> selectListByHisIsHouse(@Param(value = "stcdList") List<String> stcdList,@Param(value = "stime") String stime,@Param(value = "etime") String etime);

    List<GetWaterViewNewPojo> selectListByHisIsDay(@Param(value = "stcdList") List<String> stcdList,@Param(value = "stime") String stime,@Param(value = "etime") String etime);


    List<GetWaterViewNewPojo> selectListMinuteByRiver(@Param(value = "stcdList") List<String> stcdList,@Param(value = "stime") String stime,@Param(value = "etime") String etime,@Param(value = "mtype") String mtype);
    List<GetWaterViewNewPojo> selectListMinuteByWas(@Param(value = "stcdList") List<String> stcdList,@Param(value = "stime") String stime,@Param(value = "etime") String etime,@Param(value = "mtype") String mtype);
    List<GetWaterViewNewPojo> selectListMinuteByTide(@Param(value = "stcdList") List<String> stcdList,@Param(value = "stime") String stime,@Param(value = "etime") String etime,@Param(value = "mtype") String mtype);
    List<GetWaterViewNewPojo> selectListMinuteByPump(@Param(value = "stcdList") List<String> stcdList,@Param(value = "stime") String stime,@Param(value = "etime") String etime,@Param(value = "mtype") String mtype);


    List<GetWaterViewNewPojo> selectListHourByRiver(@Param(value = "stcdList") List<String> stcdList,@Param(value = "stime") String stime,@Param(value = "etime") String etime,@Param(value = "mtype") String mtype);
    List<GetWaterViewNewPojo> selectListHourByWas(@Param(value = "stcdList") List<String> stcdList,@Param(value = "stime") String stime,@Param(value = "etime") String etime,@Param(value = "mtype") String mtype);
    List<GetWaterViewNewPojo> selectListHourByTide(@Param(value = "stcdList") List<String> stcdList,@Param(value = "stime") String stime,@Param(value = "etime") String etime,@Param(value = "mtype") String mtype);
    List<GetWaterViewNewPojo> selectListHourByPump(@Param(value = "stcdList") List<String> stcdList,@Param(value = "stime") String stime,@Param(value = "etime") String etime,@Param(value = "mtype") String mtype);

    List<GetWaterViewNewPojo> selectListDayByRiver(@Param(value = "stcdList") List<String> stcdList,@Param(value = "stime") String stime,@Param(value = "etime") String etime,@Param(value = "mtype") String mtype);
    List<GetWaterViewNewPojo> selectListDayByWas(@Param(value = "stcdList") List<String> stcdList,@Param(value = "stime") String stime,@Param(value = "etime") String etime,@Param(value = "mtype") String mtype);
    List<GetWaterViewNewPojo> selectListDayByTide(@Param(value = "stcdList") List<String> stcdList,@Param(value = "stime") String stime,@Param(value = "etime") String etime,@Param(value = "mtype") String mtype);
    List<GetWaterViewNewPojo> selectListDayByPump(@Param(value = "stcdList") List<String> stcdList,@Param(value = "stime") String stime,@Param(value = "etime") String etime,@Param(value = "mtype") String mtype);


    List<GetWaterViewNewPojo> selectListByRiver(@Param(value = "stcdList") List<String> stcdList,@Param(value = "stime") String stime,@Param(value = "etime") String etime);
    List<GetWaterViewNewPojo> selectListByWas(@Param(value = "stcdList") List<String> stcdList,@Param(value = "stime") String stime,@Param(value = "etime") String etime);
    List<GetWaterViewNewPojo> selectListByTide(@Param(value = "stcdList") List<String> stcdList,@Param(value = "stime") String stime,@Param(value = "etime") String etime);
    List<GetWaterViewNewPojo> selectListByPump(@Param(value = "stcdList") List<String> stcdList,@Param(value = "stime") String stime,@Param(value = "etime") String etime);

    List<Map<String, Object>> selectListByRiverAvgQ(@Param(value = "stcdList") List<String> stcdList,@Param(value = "stime") String stime,@Param(value = "etime") String etime);
    List<GetWaterViewNewPojo> queryByRiver(@Param(value = "stcdList") List<String> stcdList,@Param(value = "stime") String stime,@Param(value = "etime") String etime,@Param(value = "DAYHOUR") String DAYHOUR,@Param(value = "mtype") String mtype);
    List<GetWaterViewNewPojo> queryByWas(@Param(value = "stcdList") List<String> stcdList,@Param(value = "stime") String stime,@Param(value = "etime") String etime,@Param(value = "DAYHOUR") String DAYHOUR,@Param(value = "mtype") String mtype);
    List<GetWaterViewNewPojo> queryByTide(@Param(value = "stcdList") List<String> stcdList,@Param(value = "stime") String stime,@Param(value = "etime") String etime,@Param(value = "DAYHOUR") String DAYHOUR,@Param(value = "mtype") String mtype);
    List<GetWaterViewNewPojo> queryByPump(@Param(value = "stcdList") List<String> stcdList,@Param(value = "stime") String stime,@Param(value = "etime") String etime,@Param(value = "DAYHOUR") String DAYHOUR,@Param(value = "mtype") String mtype);

    List<GetWaterViewNewPojo> queryByFlow(@Param(value = "stcdList") List<String> stcdList,@Param(value = "stime") String stime,@Param(value = "etime") String etime,@Param(value = "DAYHOUR") String DAYHOUR,@Param(value = "mtype") String mtype);


    Integer insertAll(@Param(value = "quPojo") List<ST_WAS_RPojo> quPojo);
    Integer updatePLMAX(@Param(value = "NT") String NT);


    List<GetWaterViewNewPojo> selectListWaterAll(
        @Param(value = "stcdList") List<String> stcdList,
        @Param(value = "stime") String stime,
        @Param(value = "etime") String etime,
        @Param(value = "mtype") String mtype,
        @Param(value = "startindex") Integer startindex,
        @Param(value = "pagesize") Integer pagesize
    );

    
    Integer selectListWaterAllCount(
        @Param(value = "stcdList") List<String> stcdList,
        @Param(value = "stime") String stime,
        @Param(value = "etime") String etime,
        @Param(value = "mtype") String mtype
    );

    List<swzzmodeserver.workserver.pojo.swzzflood.ST_WAS_RPojo> selectHis(
        @Param(value = "stcdList") List<String> stcdList,
        @Param(value = "stime") String stime,
        @Param(value = "etime") String etime        
    );
}
