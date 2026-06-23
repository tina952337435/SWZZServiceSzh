package swzzmodeserver.workserver.data.swzzflood;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.workserver.pojo.swzzdata.Menu_logPojo;
import swzzmodeserver.workserver.pojo.swzzflood.St_stbprp_b_qu_treePojo;

import java.util.List;

@Mapper
public interface St_stbprp_b_qu_treeData {

    List<St_stbprp_b_qu_treePojo> selectList(@Param(value = "ID") String ID, @Param(value = "key") String key,
                                             @Param(value = "stime") String stime, @Param(value = "etime") String etime,
                                             @Param(value = "type")List<String> type,
                                             @Param(value = "startindex") Integer startindex, @Param(value = "pagesize") Integer pagesize);

    Integer updateOne(St_stbprp_b_qu_treePojo St_stbprp_b_qu_treePojo);

    Integer insertOne(St_stbprp_b_qu_treePojo St_stbprp_b_qu_treePojo);

    Integer deleteOne(@Param(value = "ID") String ID);

    Integer selectCount(@Param(value = "ID") String ID, @Param(value = "key") String key,
                        @Param(value = "stime") String stime, @Param(value = "etime") String etime,
                        @Param(value = "type")List<String> type);
}
