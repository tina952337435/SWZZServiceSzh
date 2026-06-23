package swzzmodeserver.workserver.data.swzzrtsq;

import swzzmodeserver.workserver.pojo.swzzrtsq.GL_STCDPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RTSQGL_STCDData {

    List<GL_STCDPojo> selectList(@Param(value = "STCDList") List<String> STCDList);

    Integer insertOne(GL_STCDPojo bPojo);

    Integer upDateOne(GL_STCDPojo bPojo);

    Integer deleteOne(@Param(value = "ID") String ID);
}
