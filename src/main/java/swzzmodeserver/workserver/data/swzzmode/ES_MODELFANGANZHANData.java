package swzzmodeserver.workserver.data.swzzmode;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.workserver.pojo.swzzmode.BDMS_PREDICTPojo;
import swzzmodeserver.workserver.pojo.swzzmode.ES_MBMANAGEPojo;
import swzzmodeserver.workserver.pojo.swzzmode.ES_MODELFANGANZHANPojo;

import java.util.List;

@Mapper
public interface ES_MODELFANGANZHANData {

    List<ES_MODELFANGANZHANPojo> selectList(@Param(value = "ID") String ID,
                                            @Param(value = "FAID") List<String> FAID,
                                            @Param(value = "startindex") Integer startindex,@Param(value = "pagesize") Integer pagesize);

    Integer updateOne(ES_MODELFANGANZHANPojo bdmsPredictPojo);

    Integer insertOne(ES_MODELFANGANZHANPojo bdmsPredictPojo);

    Integer deleteOne(@Param(value = "ID") String ID);

    Integer insertALL(@Param(value = "objList") List<ES_MODELFANGANZHANPojo> objList);

    Integer selectCount(@Param(value = "ID") String ID,
                        @Param(value = "FAID") List<String> FAID);

    Integer deleteAll();
}
