package swzzmodeserver.workserver.data.swzzmode;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.workserver.pojo.swzzmode.HD_FLOWLINEPojo;
import swzzmodeserver.workserver.pojo.swzzmode.MigrationHistoryPojo;

import java.util.List;

@Mapper
public interface MigrationHistoryData {

    List<MigrationHistoryPojo> selectList(@Param(value = "ID") String ID,@Param(value = "key")String ContextKey,
                                          @Param(value = "startindex") Integer startindex,@Param(value = "pagesize") Integer pagesize);

    Integer updateOne(MigrationHistoryPojo migrationHistoryPojo);

    Integer insertOne(MigrationHistoryPojo migrationHistoryPojo);

    Integer deleteOne(@Param(value = "ID") String ID,@Param(value = "key")String ContextKey);

    Integer insertALL(@Param(value = "objList")List<MigrationHistoryPojo> objList);

    Integer selectCount(@Param(value = "ID") String ID,@Param(value = "key")String ContextKey);
}
