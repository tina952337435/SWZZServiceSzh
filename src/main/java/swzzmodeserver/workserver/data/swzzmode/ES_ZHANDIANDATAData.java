package swzzmodeserver.workserver.data.swzzmode;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.tools.ParamField;
import swzzmodeserver.workserver.pojo.swzzmode.*;

import java.util.List;

@Mapper
public interface ES_ZHANDIANDATAData {

    List<ES_ZHANDIANDATAPojo> selectList(@Param(value = "ID") String ID,
            @Param(value = "startindex") Integer startindex, @Param(value = "pagesize") Integer pagesize,
            @Param(value = "SOLUTIONID") String SOLUTIONID,
            @Param(value = "zhanid") List<String> zhanid,
            @Param(value = "startdate") String startdate,
            @Param(value = "enddate") String enddate);

    List<ES_ZHANDIANDATAPojo> selectListGCID(@Param(value = "SOLUTIONID") String SOLUTIONID,
            @Param(value = "PTYPE") String PTYPE,@Param(value = "IDList") List<String> IDList);

    List<ES_ZHANDIANDATAPojo> selectListGC(@Param(value = "SOLUTIONID") String SOLUTIONID,
            @Param(value = "PTYPE") String PTYPE);

    Integer updateOne(ES_ZHANDIANDATAPojo bdmsPredictPojo);

    Integer insertOne(ES_ZHANDIANDATAPojo bdmsPredictPojo);

    Integer deleteOne(@Param(value = "ID") String ID);

    Integer deleteOneBySOLUTIONID(@Param(value = "ID") String ID);

    Integer insertALL(@Param(value = "objList") List<ES_ZHANDIANDATAPojo> objList);

    Integer deleteALL(@Param(value = "bpPojo") List<ParamField> bpPojo);

    Integer updateALL(@Param(value = "list") List<ES_ZHANDIANDATAPojo> list);

    Integer insertALLDto(@Param(value = "objList") List<ES_ZHANDIANDATADto> objList);

    Integer selectCount(@Param(value = "ID") String ID,
            @Param(value = "SOLUTIONID") String SOLUTIONID,
            @Param(value = "zhanid") List<String> zhanid);

    Integer updateALLBatch(@Param(value = "list") List<ES_ZHANDIANDATAPojo> list);

    Integer updateALLME(@Param(value = "list") List<ES_ZHANDIANDATAPojo> list);

               // ES_ZHANDIANDATAData.java 新增方法
    List<ES_ZHANDIANDATAPojo> selectListBySolutionIds(
    @Param("solutionIds") List<String> solutionIds,
    @Param("zhanid") List<String> zhanid);

}
