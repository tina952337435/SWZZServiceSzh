package swzzmodeserver.workserver.data.swzzmode;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.tools.ParamField;
import swzzmodeserver.workserver.pojo.swzzmode.BDMS_PREDICTPojo;
import swzzmodeserver.workserver.pojo.swzzmode.GL_STCDPojo;
import swzzmodeserver.workserver.pojo.swzzmode.GL_STCDStnmPojo;
import swzzmodeserver.workserver.pojo.swzzmode.ST_STRVFCCH_SKBPojo;
import swzzmodeserver.workserver.pojo.swzzmode.GL_STCDStnmPojo;

import java.util.List;

@Mapper
public interface GL_STCDData {

    List<GL_STCDPojo> selectList(@Param(value = "IDList") List<String> IDList,
                                 @Param(value = "STCDList") List<String> STCDList,
                                 @Param(value = "NAME") String NAME,
                                 @Param(value = "TYPE") List<String> TYPE,
                                 @Param(value = "STTP") String STTP);

    List<GL_STCDStnmPojo> selectListNew(@Param(value = "IDList") List<String> IDList,
                                        @Param(value = "STCDList") List<String> STCDList,
                                             @Param(value = "NAME") String NAME,
                                             @Param(value = "TYPE") List<String> TYPE,
                                             @Param(value = "STTP") String STTP);

    Integer updateOne(GL_STCDPojo stStrvfcchSkbPojo);

    Integer insertOne(GL_STCDPojo stStrvfcchSkbPojo);

    Integer deleteOne(@Param(value = "ID") String ID);


    Integer insertALL(@Param(value = "objList") List<GL_STCDPojo> objList);

    Integer updateALL(@Param(value = "objList")List<GL_STCDPojo> objList);

    Integer deleteMore(@Param(value = "IDList") List<String> IDList);
}
