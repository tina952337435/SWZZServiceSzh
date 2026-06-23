package swzzmodeserver.workserver.data.swzzflood;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.workserver.pojo.swzzflood.TB_TIDE_HIGHLOWPojo;

import java.util.List;

@Mapper
public interface TB_TIDE_HIGHLOWData {
    List<TB_TIDE_HIGHLOWPojo> selectList(@Param(value = "idList") List<String> idList,@Param(value = "stime") String stime,@Param(value = "etime") String etime);
}
