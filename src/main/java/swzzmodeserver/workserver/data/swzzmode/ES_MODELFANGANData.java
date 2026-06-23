package swzzmodeserver.workserver.data.swzzmode;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.workserver.pojo.swzzmode.ES_MODELFANGANPojo;

import java.util.List;

@Mapper
public interface ES_MODELFANGANData {

    List<ES_MODELFANGANPojo> selectList(@Param(value = "ID") String ID,
                                        @Param(value = "startindex") Integer startindex,@Param(value = "pagesize") Integer pagesize);

    Integer updateOne(ES_MODELFANGANPojo esModelfanganPojo);

    Integer insertOne(ES_MODELFANGANPojo esModelfanganPojo);

    Integer deleteOne(@Param(value = "ID") String ID);

    Integer insertALL(@Param(value = "objList") List<ES_MODELFANGANPojo> objList);

    Integer selectCount(@Param(value = "ID") String ID);

    Integer deleteAll();
}
