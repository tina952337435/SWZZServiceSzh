package swzzmodeserver.workserver.data.swzzmode;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.tools.ParamField;
import swzzmodeserver.workserver.pojo.swzzmode.BDMS_PREDICTPojo;
import swzzmodeserver.workserver.pojo.swzzmode.ES_SLTONGJIPojo;
import swzzmodeserver.workserver.pojo.swzzmode.ES_ZHANDIANDto;
import swzzmodeserver.workserver.pojo.swzzmode.ES_ZHANDIANPojo;

import java.util.List;

@Mapper
public interface ES_ZHANDIANData {

    List<ES_ZHANDIANPojo> selectList(@Param(value = "ID") String ID,
                                     @Param(value = "startindex") Integer startindex,@Param(value = "pagesize") Integer pagesize,
                                     @Param(value = "ptype") List<String> ptype,
                                     @Param(value = "key") String key
                                     );

    Integer updateOne(ES_ZHANDIANPojo esZhandianPojo);

    Integer insertOne(ES_ZHANDIANPojo esZhandianPojo);

    Integer deleteOne(@Param(value = "ID") String ID);

    Integer insertALL(@Param(value = "objList")List<ES_ZHANDIANPojo> objList);

    Integer deleteALL(@Param(value = "bpPojo")List<ParamField> bpPojo);

    Integer deleteFangAn(@Param(value = "bpPojo")List<ParamField> bpPojo);

    List<ES_ZHANDIANDto> selectES_ZHANDIANAndDATAByDay(@Param(value = "solutionid") String solutionid,
                                                       @Param(value = "datatype")List<String> datatype,
                                                       @Param(value = "startdate")String startdate,
                                                       @Param(value = "enddate")String enddate);

    List<ES_ZHANDIANDto> selectES_ZHANDIANAndDATAByHour(@Param(value = "solutionid")String solutionid,
                                                        @Param(value = "datatype")List<String> datatype,
                                                        @Param(value = "startdate")String startdate,
                                                        @Param(value = "enddate")String enddate);

    Integer updateALL(@Param(value = "list")List<ES_ZHANDIANPojo> bpPojo);

    Integer selectCount(@Param(value = "ID") String ID,
                        @Param(value = "ptype") List<String> ptype,
                        @Param(value = "key") String key);
}
