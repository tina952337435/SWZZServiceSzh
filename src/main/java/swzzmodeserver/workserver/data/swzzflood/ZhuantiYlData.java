package swzzmodeserver.workserver.data.swzzflood;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import swzzmodeserver.workserver.pojo.swzzflood.ZhuantiYlPojo;

import java.util.List;

@Mapper
@Component
public interface ZhuantiYlData {

    List<ZhuantiYlPojo> selectList(
        @Param("stcdList")List<String> stcdList,
        @Param(value = "stime") String stime,
        @Param(value = "etime") String etime,
        @Param(value = "pathname") String pathname);

    Integer insertOne(ZhuantiYlPojo pojo);

    Integer upDateOne(ZhuantiYlPojo pojo);

    Integer deleteOne(@Param(value = "stcd") String stcd, @Param(value = "tm") String tm);

    Integer insertAll(@Param(value = "objList") List<ZhuantiYlPojo> objList);

    Integer upDateAll(@Param(value = "objList") List<ZhuantiYlPojo> objList);

    Integer deleteAll(@Param(value = "stcdList") List<String> stcdList);
}