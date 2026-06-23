package swzzmodeserver.workserver.data.swzzdata;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.workserver.pojo.swzzdata.SmsPojo;
import swzzmodeserver.workserver.pojo.swzzdata.St_formula_bPojo;

import java.util.List;

@Mapper
public interface St_formula_bData {

    List<St_formula_bPojo> selectList(@Param(value = "ID") String ID, @Param(value = "key") String key,
                                      @Param(value = "stime") String stime, @Param(value = "etime") String etime,
                                      @Param(value = "type")List<String> type,
                                      @Param(value = "startindex") Integer startindex,@Param(value = "pagesize") Integer pagesize);

    Integer updateOne(St_formula_bPojo bdmsPredictPojo);

    Integer insertOne(St_formula_bPojo bdmsPredictPojo);

    Integer deleteOne(@Param(value = "ID") String ID);

    Integer selectCount(@Param(value = "ID") String ID, @Param(value = "key") String key,
                        @Param(value = "stime") String stime, @Param(value = "etime") String etime,
                        @Param(value = "type")List<String> type);
}
