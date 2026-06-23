package swzzmodeserver.workserver.data.swzzrtsq;

import swzzmodeserver.workserver.pojo.swzzrtsq.INOUTWDPSTATPojo;
import swzzmodeserver.workserver.pojo.swzzrtsq.ST_WDPSTAT_RPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ST_WDPSTAT_RData {

    List<ST_WDPSTAT_RPojo> selectList(@Param(value = "idList") List<String> idList,@Param(value = "stime") String stime,@Param(value = "etime") String etime);

    Integer insertOne(ST_WDPSTAT_RPojo hrPojo);

    Integer upDateOne(ST_WDPSTAT_RPojo hrPojo);

    Integer deleteOne(@Param(value = "id") String id);

    List<ST_WDPSTAT_RPojo> selectWdpstatRBXList(@Param(value = "idList") List<String> idList,@Param(value = "stime") String stime,@Param(value = "etime") String etime);
    List<ST_WDPSTAT_RPojo> selectWdpstatRList(@Param(value = "idList") List<String> idList,@Param(value = "stime") String stime,@Param(value = "etime") String etime);


    List<ST_WDPSTAT_RPojo> selectGQDAYList(@Param("STIME") String STIME, @Param("ETIME") String ETIME,  @Param("listSTCD") List<String> listSTCD);
    List<ST_WDPSTAT_RPojo> selectGQHOURList(@Param("STIME") String STIME, @Param("ETIME") String ETIME,  @Param("listSTCD") List<String> listSTCD);
    List<INOUTWDPSTATPojo> INOUTWDPSTATDAYSel(@Param("STIME") String STIME, @Param("ETIME") String ETIME, @Param("listSTCD") List<String> listSTCD, @Param("PATHNAME") String PATHNAME, @Param("RVNM") String RVNM);

}
