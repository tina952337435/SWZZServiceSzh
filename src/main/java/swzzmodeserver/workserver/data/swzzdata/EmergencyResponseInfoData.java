package swzzmodeserver.workserver.data.swzzdata;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import swzzmodeserver.workserver.pojo.swzzdata.EmergencyResponseInfoPojo;

import java.util.List;

/**
 * 应急响应信息数据访问层接口
 */
@Mapper
public interface EmergencyResponseInfoData {

    /**
     * 分页查询列表
     *
     * @param ID         唯一标识 (用于精确匹配)
     * @param key        关键字 (用于模糊搜索标题或内容)
     * @param stime      开始时间
     * @param etime      结束时间
     * @param type       响应类型集合 (预留扩展，如不需要可移除)
     * @param startindex 起始索引
     * @param pagesize   每页条数
     * @return 应急响应信息列表
     */
    List<EmergencyResponseInfoPojo> selectList(
            @Param(value = "ID") String ID,
            @Param(value = "key") String key,
            @Param(value = "stime") String stime,
            @Param(value = "etime") String etime,
            @Param(value = "type") List<String> type,
            @Param(value = "startindex") Integer startindex,
            @Param(value = "pagesize") Integer pagesize
    );
  List<EmergencyResponseInfoPojo> selectByTMNew(@Param(value = "stime") String stime,
            @Param(value = "etime") String etime);
    /**
     * 统计总数
     *
     * @param ID    唯一标识
     * @param key   关键字
     * @param stime 开始时间
     * @param etime 结束时间
     * @param type  响应类型集合
     * @return 总记录数
     */
    Integer selectCount(
            @Param(value = "ID") String ID,
            @Param(value = "key") String key,
            @Param(value = "stime") String stime,
            @Param(value = "etime") String etime,
            @Param(value = "type") List<String> type
    );

    /**
     * 新增一条记录
     *
     * @param pojo 应急响应信息对象
     * @return 影响行数
     */
    Integer insertOne(EmergencyResponseInfoPojo pojo);

    /**
     * 更新一条记录
     *
     * @param pojo 应急响应信息对象 (需包含主键ID)
     * @return 影响行数
     */
    Integer updateOne(EmergencyResponseInfoPojo pojo);

    /**
     * 删除一条记录
     *
     * @param ID 唯一标识
     * @return 影响行数
     */
    Integer deleteOne(@Param(value = "ID") String ID);

    Integer insertAll(@Param(value = "quPojo") List<EmergencyResponseInfoPojo> pojo);
    
}