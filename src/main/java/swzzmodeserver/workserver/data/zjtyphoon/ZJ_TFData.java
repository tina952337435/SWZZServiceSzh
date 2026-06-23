package swzzmodeserver.workserver.data.zjtyphoon;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.tools.ParamField;
import swzzmodeserver.workserver.pojo.zjtyphoon.ZJ_LANDPojo;
import swzzmodeserver.workserver.pojo.zjtyphoon.ZJ_TFPojo;
import swzzmodeserver.workserver.pojo.zjtyphoon.ZJ_TFZTPojo;

import java.util.List;

@Mapper
public interface ZJ_TFData {

    List<ZJ_TFPojo> selectList(@Param(value = "ID") String ID, @Param(value = "key") String key,
                                 @Param(value = "stime") String stime, @Param(value = "etime") String etime,
                                 @Param(value = "type")List<String> type,
                                 @Param(value = "tfbhList")List<String> tfbhList,
                                 @Param(value = "startindex") Integer startindex, @Param(value = "pagesize") Integer pagesize);

    List<ZJ_TFZTPojo> selectListandzhuanti(@Param(value = "ID") String ID, @Param(value = "key") String key,
                               @Param(value = "stime") String stime, @Param(value = "etime") String etime,
                               @Param(value = "type")List<String> type,
                               @Param(value = "tfbhList")List<String> tfbhList,
                               @Param(value = "startindex") Integer startindex, @Param(value = "pagesize") Integer pagesize);

    Integer updateOne(ZJ_TFPojo St_stbprp_b_qu_treePojo);

    Integer insertOne(ZJ_TFPojo St_stbprp_b_qu_treePojo);

    Integer deleteOne(@Param(value = "ID") String ID);

    Integer selectCount(@Param(value = "ID") String ID, @Param(value = "key") String key,
                        @Param(value = "stime") String stime, @Param(value = "etime") String etime,
                        @Param(value = "type")List<String> type,
                        @Param(value = "tfbhList")List<String> tfbhList);

    Integer insertALL(@Param(value = "objList") List<ZJ_TFPojo> objList);

    Integer updateALL(@Param(value = "objList") List<ZJ_TFPojo> objList);

    Integer deleteALL(@Param(value = "objList") List<ParamField> objList);
}
