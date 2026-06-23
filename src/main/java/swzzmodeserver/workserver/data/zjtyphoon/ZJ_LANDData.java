package swzzmodeserver.workserver.data.zjtyphoon;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.tools.ParamField;
import swzzmodeserver.workserver.pojo.swzzflood.St_stbprp_b_qu_treePojo;
import swzzmodeserver.workserver.pojo.zjtyphoon.ZJ_LANDPojo;

import java.util.List;

@Mapper
public interface ZJ_LANDData {

    List<ZJ_LANDPojo> selectList(@Param(value = "ID") String ID, @Param(value = "key") String key,
                                 @Param(value = "stime") String stime, @Param(value = "etime") String etime,
                                 @Param(value = "type")List<String> type,
                                 @Param(value = "startindex") Integer startindex, @Param(value = "pagesize") Integer pagesize);

    Integer updateOne(ZJ_LANDPojo St_stbprp_b_qu_treePojo);

    Integer insertOne(ZJ_LANDPojo St_stbprp_b_qu_treePojo);

    Integer deleteOne(@Param(value = "ID") String ID);

    Integer selectCount(@Param(value = "ID") String ID, @Param(value = "key") String key,
                        @Param(value = "stime") String stime, @Param(value = "etime") String etime,
                        @Param(value = "type")List<String> type);

    Integer insertALL(@Param(value = "objList") List<ZJ_LANDPojo> objList);

    Integer updateALL(@Param(value = "objList") List<ZJ_LANDPojo> objList);

    Integer deleteALL(@Param(value = "objList") List<ParamField> objList);
}
