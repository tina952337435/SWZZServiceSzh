package swzzmodeserver.workserver.data.zjtyphoon;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.tools.ParamField;
import swzzmodeserver.workserver.pojo.zjtyphoon.ZJ_LANDPojo;
import swzzmodeserver.workserver.pojo.zjtyphoon.ZJ_TFLSLJPojo;

import java.util.List;

@Mapper
public interface ZJ_TFLSLJData {

    List<ZJ_TFLSLJPojo> selectList(@Param(value = "ID") String ID, @Param(value = "key") String key,
                                 @Param(value = "stime") String stime, @Param(value = "etime") String etime,
                                 @Param(value = "tfidList")List<String> tfidList,
                                 @Param(value = "startindex") Integer startindex, @Param(value = "pagesize") Integer pagesize);

    Integer updateOne(ZJ_TFLSLJPojo St_stbprp_b_qu_treePojo);

    Integer insertOne(ZJ_TFLSLJPojo St_stbprp_b_qu_treePojo);

    Integer deleteOne(@Param(value = "ID") String ID);

    Integer selectCount(@Param(value = "ID") String ID, @Param(value = "key") String key,
                        @Param(value = "stime") String stime, @Param(value = "etime") String etime,
                        @Param(value = "tfidList")List<String> tfidList);

    Integer insertALL(@Param(value = "objList") List<ZJ_TFLSLJPojo> objList);

    Integer updateALL(@Param(value = "objList") List<ZJ_TFLSLJPojo> objList);

    Integer deleteALL(@Param(value = "objList") List<ParamField> objList);
}
