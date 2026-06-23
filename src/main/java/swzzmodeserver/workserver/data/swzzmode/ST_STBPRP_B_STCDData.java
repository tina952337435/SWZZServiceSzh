package swzzmodeserver.workserver.data.swzzmode;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.workserver.pojo.swzzmode.ST_STBPRP_B_STCDPojo;

import java.util.List;

@Mapper
public interface ST_STBPRP_B_STCDData {

    List<ST_STBPRP_B_STCDPojo> selectList(@Param(value = "ID") String ID,@Param(value = "key") String key,
                                          @Param(value = "TYPE")List<String> TYPE,
                                          @Param(value = "SOURCE")String SOURCE,
                                          @Param(value = "startindex") Integer startindex,@Param(value = "pagesize") Integer pagesize);

    Integer updateOne(ST_STBPRP_B_STCDPojo stStbprpBStcdPojo);

    Integer insertOne(ST_STBPRP_B_STCDPojo stStbprpBStcdPojo);

    Integer deleteOne(@Param(value = "ID") String ID,@Param(value = "TYPE")String TYPE,@Param(value = "SOURCE")String SOURCE);

    Integer selectCount(@Param(value = "ID") String ID,@Param(value = "key") String key,
                        @Param(value = "TYPE")List<String> TYPE,
                        @Param(value = "SOURCE")String SOURCE);

    //Integer insertALL(@Param(value = "objList") List<ST_STBPRP_B_STCDPojo> objList);
}
