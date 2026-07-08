package swzzmodeserver.workserver.data.swzzmode;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.tools.ParamField;
import swzzmodeserver.workserver.pojo.swzzmode.*;

import java.util.List;

@Mapper
public interface ES_ZHANDIANDATA_YUANData {

        List<ES_ZHANDIANDATA_YUANPojo> selectList(@Param(value = "ID") String ID,
                        @Param(value = "startindex") Integer startindex, @Param(value = "pagesize") Integer pagesize,
                        @Param(value = "SOLUTIONID") String SOLUTIONID,
                        @Param(value = "zhanid") List<String> zhanid,
                        @Param(value = "startdate") String startdate,
                        @Param(value = "enddate") String enddate);

        Integer updateOne(ES_ZHANDIANDATA_YUANPojo bdmsPredictPojo);

        Integer insertOne(ES_ZHANDIANDATA_YUANPojo bdmsPredictPojo);

        Integer deleteOne(@Param(value = "ID") String ID);

        Integer deleteOneBySOLUTIONID(@Param(value = "ID") String ID);

        Integer insertALL(@Param(value = "objList") List<ES_ZHANDIANDATA_YUANPojo> objList);

        Integer deleteALL(@Param(value = "bpPojo") List<ParamField> bpPojo);

        Integer updateALL(@Param(value = "list") List<ES_ZHANDIANDATA_YUANPojo> list);

}
