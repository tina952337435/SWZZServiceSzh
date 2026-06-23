package swzzmodeserver.workserver.data.swzzdata;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.workserver.pojo.swzzdata.St_formula_bPojo;
import swzzmodeserver.workserver.pojo.swzzdata.St_formula_rPojo;

import java.util.List;

@Mapper
public interface St_formula_rData {

    List<St_formula_rPojo> selectList(@Param(value = "ID") String ID, @Param(value = "key") String key,
                                      @Param(value = "stime") String stime, @Param(value = "etime") String etime,
                                      @Param(value = "type")List<String> type,
                                      @Param(value = "PID") String PID,
                                      @Param(value = "startindex") Integer startindex,@Param(value = "pagesize") Integer pagesize);

    Integer updateOne(St_formula_rPojo bdmsPredictPojo);

    Integer insertOne(St_formula_rPojo bdmsPredictPojo);

    Integer deleteOne(@Param(value = "ID") String ID);

    Integer selectCount(@Param(value = "ID") String ID, @Param(value = "key") String key,
                        @Param(value = "stime") String stime, @Param(value = "etime") String etime,
                        @Param(value = "type")List<String> type,
                        @Param(value = "PID") String PID);
}
