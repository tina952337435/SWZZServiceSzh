package swzzmodeserver.workserver.data.swzzflood;

import swzzmodeserver.workserver.pojo.swzzflood.XQKB_LISTPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface XQKB_LISTData {

    List<XQKB_LISTPojo> selectList(@Param(value = "pathnameList") List<String> pathnameList,
                                   @Param(value = "stime")String stime,
                                   @Param(value = "etime")String etime);

    XQKB_LISTPojo selectOne(@Param(value = "XQKB_ID") String XQKB_ID);

    Integer insertOne(XQKB_LISTPojo pojo);

    Integer upDateOne(XQKB_LISTPojo pojo);

    Integer deleteOne(@Param(value = "XQKB_ID") String XQKB_ID);
}