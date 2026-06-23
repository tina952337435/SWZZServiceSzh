package swzzmodeserver.workserver.data.swzzrtsq;

import swzzmodeserver.workserver.pojo.swzzrtsq.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RTSQST_STBPRP_BData {

    List<ST_STBPRP_BPojo> selectList(@Param(value = "STCDList") List<String> STCDList,@Param(value = "STNM") String STNM);

    Integer insertOne(ST_STBPRP_BPojo bPojo);

    Integer upDateOne(ST_STBPRP_BPojo bPojo);

    Integer deleteOne(@Param(value = "STCD") String STCD);

    List<ST_STBPRP_BPojo> selectStbprpBList(@Param(value = "STCDList") List<String> STCDList,@Param(value = "KEY") String KEY,@Param(value = "MTYPE") String MTYPE);
    List<ST_STBPRP_BPojo> selectStbprpBXBList(@Param(value = "STCDList") List<String> STCDList,@Param(value = "KEY") String KEY);
    List<ST_STBPRP_BPojo> findResult(@Param(value = "STCDList") List<String> STCDList,@Param(value = "STNM") String STNM,@Param(value = "pathname") String pathname);


    List<V_ST_STBPRP_BTZDto> GetSyncSTCD(@Param(value = "STCDList") List<String> STCDList,
                                         @Param(value = "typeList")  List<String>  typeList,
                                         @Param(value = "isstatus") String isstatus,
                                         @Param(value = "sourceList")  List<String> source);


    List<ST_STBPRP_FCCHPojo> selectGQList(@Param(value = "STCDList") List<String> STCDList);

}
