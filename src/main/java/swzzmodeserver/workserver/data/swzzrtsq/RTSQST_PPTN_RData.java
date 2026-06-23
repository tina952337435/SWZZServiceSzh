package swzzmodeserver.workserver.data.swzzrtsq;

import swzzmodeserver.workserver.pojo.swzzrtsq.GetWaterViewNewPojo;
import swzzmodeserver.workserver.pojo.swzzrtsq.ST_PPTN_RDto;
import swzzmodeserver.workserver.pojo.swzzrtsq.ST_PPTN_RPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.workserver.pojo.swzzrtsq.TSDBPojo;

import java.util.List;

@Mapper
public interface RTSQST_PPTN_RData {
    List<ST_PPTN_RPojo> selectListIsDay(@Param(value = "stcdList") List<String> stcdList,@Param(value = "stime") String stime,@Param(value = "etime") String etime);

    List<ST_PPTN_RPojo> selectList(@Param(value = "stcdList") List<String> stcdList,@Param(value = "stime") String stime,@Param(value = "etime") String etime);
    List<ST_PPTN_RPojo> queryList(@Param(value = "stcdList") List<String> stcdList,@Param(value = "stime") String stime,@Param(value = "etime") String etime);

    List<ST_PPTN_RPojo> selectListByHouse(@Param(value = "stcdList") List<String> stcdList,@Param(value = "stime") String stime,@Param(value = "etime") String etime,@Param(value = "mtype") String mtype);

    List<ST_PPTN_RPojo> selectListByTime(@Param(value = "stcdList") List<String> stcdList,@Param(value = "stime") String stime,@Param(value = "etime") String etime,@Param(value = "mtype") String mtype);

    /*
    *  过滤掉降雨量为0的记录
    * */
    List<ST_PPTN_RPojo> selectListByTimeNull(@Param(value = "stcdList") List<String> stcdList,@Param(value = "stime") String stime,@Param(value = "etime") String etime,@Param(value = "mtype") String mtype);

    Integer insertAll(@Param(value = "quPojo") List<ST_PPTN_RPojo> quPojo);

    List<ST_PPTN_RDto> selectListBySen(@Param(value = "stcdList") List<String> stcdList,@Param(value = "etime") String etime,@Param(value = "stcd") String stcd);

    List<ST_PPTN_RPojo> selectListDanZhanByHouse(@Param(value = "stcdList") List<String> stcdList,@Param(value = "stime") String stime,@Param(value = "etime") String etime);

    List<ST_PPTN_RPojo> selectListSumByHour(@Param(value = "stcdList") List<String> stcdList,@Param(value = "stime") String stime,@Param(value = "etime") String etime,@Param(value = "flag") String flag);

    List<ST_PPTN_RPojo> selectListSumByDay(@Param(value = "stcdList") List<String> stcdList,@Param(value = "stime") String stime,@Param(value = "etime") String etime,@Param(value = "flag") String flag);

    List<ST_PPTN_RPojo> queryDRPList(@Param(value = "stime") String stime,@Param(value = "etime") String etime,@Param(value = "MTYPE") String MTYPE,@Param(value = "listSTCD") List<String> listSTCD);
    List<ST_PPTN_RPojo> queryHOURDRPList(@Param(value = "stime") String stime,@Param(value = "etime") String etime,@Param(value = "MTYPE") String MTYPE,@Param(value = "listSTCD") List<String> listSTCD);
    List<ST_PPTN_RPojo> queryDAYDRPList(@Param(value = "stime") String stime,@Param(value = "etime") String etime,@Param(value = "MTYPE") String MTYPE,@Param(value = "listSTCD") List<String> listSTCD);
    List<ST_PPTN_RPojo> querySUMDRPList(@Param(value = "stime") String stime,@Param(value = "etime") String etime,@Param(value = "MTYPE") String MTYPE,@Param(value = "listSTCD") List<String> listSTCD);
    List<ST_PPTN_RPojo> queryMAXHOURDRPList(@Param(value = "stime") String stime,@Param(value = "etime") String etime,@Param(value = "MTYPE") String MTYPE,@Param(value = "listSTCD") List<String> listSTCD);


    List<TSDBPojo> selectTSDBList (@Param(value = "stcdList") List<String> stcdList, @Param(value = "stime") String stime, @Param(value = "etime") String etime);

    List<TSDBPojo> selectTSDBListtTop (@Param(value = "stcdList") List<String> stcdList, @Param(value = "stime") String stime, @Param(value = "etime") String etime);


    List<ST_PPTN_RPojo> selectListTotal(@Param(value = "stcdList") List<String> stcdList,@Param(value = "stime") String stime,@Param(value = "etime") String etime);

    List<ST_PPTN_RPojo> selectListHourly(@Param(value = "stcdList") List<String> stcdList,@Param(value = "stime") String stime,@Param(value = "etime") String etime);


    List<ST_PPTN_RPojo> selectListHourlyArea(@Param(value = "stcdList") List<String> stcdList,@Param(value = "stime") String stime,@Param(value = "etime") String etime);

    
    List<ST_PPTN_RPojo> selectListMinuArea(
        @Param(value = "stcdList") List<String> stcdList,
        @Param(value = "stime") String stime,
        @Param(value = "etime") String etime
    );
    

    List<ST_PPTN_RPojo> queryDRPListAll(
        @Param(value = "stime") String stime,
        @Param(value = "etime") String etime,
        @Param(value = "MTYPE") String MTYPE,
        @Param(value = "listSTCD") List<String> listSTCD,
        @Param(value = "startindex") Integer startindex,
        @Param(value = "pagesize") Integer pagesize
    );

    public Integer queryDRPListAllCount(@Param(value = "stime") String stime,
        @Param(value = "etime") String etime,
        @Param(value = "MTYPE") String MTYPE,
        @Param(value = "listSTCD") List<String> listSTCD
    );

    List<ST_PPTN_RPojo> selectListSL323(
        @Param(value = "stcdList") List<String> stcdList,
        @Param(value = "stime") String stime,
        @Param(value = "etime") String etime
    );
    
}
