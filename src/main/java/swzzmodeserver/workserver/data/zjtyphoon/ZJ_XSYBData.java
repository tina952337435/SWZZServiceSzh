package swzzmodeserver.workserver.data.zjtyphoon;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.workserver.pojo.zjtyphoon.ZJ_XSPojo;
import swzzmodeserver.workserver.pojo.zjtyphoon.ZJ_XSYBPojo;

import java.util.List;

@Mapper
public interface ZJ_XSYBData {

    List<ZJ_XSYBPojo> selectList(@Param(value = "ID") String ID, @Param(value = "key") String key,
                                 @Param(value = "stime") String stime, @Param(value = "etime") String etime,
                                 @Param(value = "type")List<String> type,
                                 @Param(value = "startindex") Integer startindex, @Param(value = "pagesize") Integer pagesize);

    Integer updateOne(ZJ_XSYBPojo St_stbprp_b_qu_treePojo);

    Integer insertOne(ZJ_XSYBPojo St_stbprp_b_qu_treePojo);

    Integer deleteOne(@Param(value = "ID1") String ID1,@Param(value = "ID2") String ID2,@Param(value = "ID3") String ID3);

    Integer selectCount(@Param(value = "ID") String ID, @Param(value = "key") String key,
                        @Param(value = "stime") String stime, @Param(value = "etime") String etime,
                        @Param(value = "type")List<String> type);

    Integer insertALL(@Param(value = "objList") List<ZJ_XSYBPojo> objList);

    Integer updateALL(@Param(value = "objList") List<ZJ_XSYBPojo> objList);

    Integer deleteALL(@Param(value = "objList") List<ZJ_XSYBPojo> objList);
}
