package swzzmodeserver.workserver.data.swzzrtsq;

import swzzmodeserver.workserver.pojo.swzzrtsq.ST_STBPRP_B_TREEPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ST_STBPRP_B_TREEData {

    List<ST_STBPRP_B_TREEPojo> selectList(ST_STBPRP_B_TREEPojo stStbprpBTreePojo);

    Integer insertOne(ST_STBPRP_B_TREEPojo stStbprpBTreePojo);

    Integer upDateOne(ST_STBPRP_B_TREEPojo stStbprpBTreePojo);

    Integer deleteOne(@Param(value = "ID") String ID);

    List<ST_STBPRP_B_TREEPojo> selectListByID(@Param(value = "idList")List<String> type,@Param(value = "pidList") List<String> PID);
}
