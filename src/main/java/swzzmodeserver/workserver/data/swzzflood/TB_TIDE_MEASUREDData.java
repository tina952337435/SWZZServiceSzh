package swzzmodeserver.workserver.data.swzzflood;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.workserver.pojo.swzzflood.TB_TIDE_MEASUREDPojo;

import java.util.List;

@Mapper
public interface TB_TIDE_MEASUREDData {

    List<TB_TIDE_MEASUREDPojo> selectList(@Param(value = "stcdList") List<String> stcdList,
                                          @Param(value = "stime")String stime,
                                          @Param(value = "etime")String etime);

}
