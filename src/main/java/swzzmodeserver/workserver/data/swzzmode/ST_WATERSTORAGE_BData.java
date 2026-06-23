package swzzmodeserver.workserver.data.swzzmode;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.workserver.pojo.swzzmode.ST_WATERSTORAGE_BPojo;

import java.util.List;

@Mapper
public interface ST_WATERSTORAGE_BData {

    List<ST_WATERSTORAGE_BPojo> selectList(@Param(value = "ID") String ID,
                                            @Param(value = "TYPE") String TYPE);

    Integer insertOne(ST_WATERSTORAGE_BPojo pojo);

    Integer updateOne(ST_WATERSTORAGE_BPojo pojo);

    Integer deleteOne(@Param(value = "ID") String ID);

    Integer selectCount(@Param(value = "ID") String ID,
                        @Param(value = "TYPE") String TYPE);
}