package swzzmodeserver.workserver.data.swzzrtsq;

import swzzmodeserver.workserver.pojo.swzzrtsq.ST_STBPRP_B_STCDPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RTSQST_STBPRP_B_STCDData {
    List<ST_STBPRP_B_STCDPojo> selectList(@Param(value = "stcdList") List<String> stcdList,
                                          @Param(value = "typeList") List<String> typeList,
                                          @Param(value = "sourceList") List<String> sourceList);

    Integer deleteOne(@Param(value = "STCD") String STCD,@Param(value = "TYPE") String TYPE);
    Integer upDateOne(@Param(value = "stime") String stime);

    Integer UpdateWaterDataRiver(@Param(value = "type") String type);

    Integer UpdateWaterDataWas(@Param(value = "type") String type);

    Integer UpdateWaterDataTide(@Param(value = "type") String type);


    Integer UpdateWaterDataPump(@Param(value = "type") String type);


    Integer UpdateWaterDataRsvr(@Param(value = "type") String type);

    Integer UpdateWaterDataPptn(@Param(value = "type") String type,@Param(value = "stcd") String stcd);

    Integer UpdateWaterDataFlow(@Param(value = "type") String type);

    Integer UpdateWaterDataFeng(@Param(value = "type") String type);

    Integer UpdateWaterDataGate(@Param(value = "type") String type,@Param(value = "stcd") String stcd,@Param(value = "tm") String tm);    

    Integer UpdateWaterDataGateAll(@Param(value = "type") String type,@Param(value = "stcd") String stcd);
}
