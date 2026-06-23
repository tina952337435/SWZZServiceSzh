package swzzmodeserver.workserver.data.swzzflood;

import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import swzzmodeserver.workserver.pojo.swzzflood.ST_TIDE_RPojo;
import swzzmodeserver.workserver.pojo.swzzflood.ZhuantiPojo;

import java.util.List;

@Mapper
@Component
public interface ZhuantiData {

    List<ZhuantiPojo> selectList(@Param("stcdList")List<String> stcdList);

    List<ZhuantiPojo> selectZhuanTiList(@Param("stcdList")List<String> stcdList,
                                        @Param("ddwj")String ddwj);

    Integer insertOne(ZhuantiPojo pojo);

    Integer upDateOne(ZhuantiPojo pojo);

    Integer deleteOne(@Param(value = "id") String id);

    Integer insertAll(@Param(value = "objList") List<ZhuantiPojo> objList);

    Integer upDateAll(@Param(value = "objList") List<ZhuantiPojo> objList);

    Integer deleteAll(@Param(value = "stcdList") List<String> stcdList);
}
