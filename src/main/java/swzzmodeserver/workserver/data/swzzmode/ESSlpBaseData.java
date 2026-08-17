package swzzmodeserver.workserver.data.swzzmode;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.workserver.pojo.swzzmode.ESSlpBasePojo;

import java.util.List;

@Mapper
public interface ESSlpBaseData {

    List<ESSlpBasePojo> selectList();

    ESSlpBasePojo selectOne(@Param("ID") String ID);

    Integer insertOne(ESSlpBasePojo pojo);

    Integer updateOne(ESSlpBasePojo pojo);

    Integer deleteOne(@Param("ID") String ID);
}
