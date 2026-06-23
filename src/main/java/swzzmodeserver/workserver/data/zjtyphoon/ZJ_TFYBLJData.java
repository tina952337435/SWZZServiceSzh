package swzzmodeserver.workserver.data.zjtyphoon;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.tools.ParamField;
import swzzmodeserver.workserver.pojo.zjtyphoon.ZJ_LANDPojo;
import swzzmodeserver.workserver.pojo.zjtyphoon.ZJ_TFYBLJPojo;

import java.util.List;

@Mapper
public interface ZJ_TFYBLJData {

    List<ZJ_TFYBLJPojo> selectList(@Param(value = "ID") String ID,
                                   @Param(value = "ZJ_TFBH") List<String> ZJ_TFBH,
                                   @Param(value = "key") String key,
                                   @Param(value = "stime") String stime,
                                   @Param(value = "etime") String etime,
                                   @Param(value = "ZJTM")List<String> ZJTM,
                                   @Param(value = "startindex") Integer startindex,
                                   @Param(value = "pagesize") Integer pagesize);

    Integer updateOne(ZJ_TFYBLJPojo St_stbprp_b_qu_treePojo);

    Integer insertOne(ZJ_TFYBLJPojo St_stbprp_b_qu_treePojo);

    Integer deleteOne(@Param(value = "ID") String ID);

    Integer selectCount(@Param(value = "ID") String ID, @Param(value = "key") String key,
                        @Param(value = "stime") String stime, @Param(value = "etime") String etime,
                        @Param(value = "type")List<String> type);

    Integer insertALL(@Param(value = "objList") List<ZJ_TFYBLJPojo> objList);

    Integer updateALL(@Param(value = "objList") List<ZJ_TFYBLJPojo> objList);

    Integer deleteALL(@Param(value = "objList") List<ParamField> objList);
}
