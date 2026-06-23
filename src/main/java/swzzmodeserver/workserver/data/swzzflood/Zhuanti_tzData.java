package swzzmodeserver.workserver.data.swzzflood;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import swzzmodeserver.workserver.pojo.swzzflood.Zhuanti_tzPojo;

import java.util.List;

@Mapper
@Component
public interface Zhuanti_tzData {

    List<Zhuanti_tzPojo> selectList(@Param("stcdList")List<String> stcdList,
                                    @Param("ztidList")List<String> ztidList,
                                    @Param("typeList")List<String> typeList);

    Integer insertOne(Zhuanti_tzPojo pojo);

    Integer upDateOne(Zhuanti_tzPojo pojo);

    Integer deleteOne(@Param(value = "id") String id);

    Integer insertAll(@Param(value = "objList") List<Zhuanti_tzPojo> objList);

    Integer upDateAll(@Param(value = "objList") List<Zhuanti_tzPojo> objList);

    Integer deleteAll(@Param(value = "stcdList") List<String> stcdList);
}
