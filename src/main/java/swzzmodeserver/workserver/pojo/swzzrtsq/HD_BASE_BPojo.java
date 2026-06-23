package swzzmodeserver.workserver.pojo.swzzrtsq;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class HD_BASE_BPojo {
    /// <summary>
    ///编号
    /// </summary>
    @TableId(value = "ID")
    private String id;
    /// <summary>
    ///河道名称
    /// </summary>
    @TableField(value = "RVNM")
    private String rvnm;
    /// <summary>
    ///所在水利分区
    /// </summary>
    @TableField(value = "BSNM")
    private String bsnm;
    /// <summary>
    ///起点
    /// </summary>
    @TableField(value = "STARTPOINT")
    private String startpoint;
    /// <summary>
    ///讫点
    /// </summary>
    @TableField(value = "ENDPOINT")
    private String endpoint;
    /// <summary>
    ///长度(km)
    /// </summary>
    @TableField(value = "SHAPELENG")
    private String shapeleng;
    /// <summary>
    ///功能
    /// </summary>
    @TableField(value = "GN")
    private String gn;
    /// <summary>
    ///类别
    /// </summary>
    @TableField(value = "TYPEID")
    private String typeid;
    /// <summary>
    ///等级
    /// </summary>
    @TableField(value = "DENGJI")
    private String dengji;
    /// <summary>
    ///涉及行政区
    /// </summary>
    @TableField(value = "CITYNAME")
    private String cityname;
    /// <summary>
    ///备注
    /// </summary>
    @TableField(value = "NT")
    private String nt;
    /// <summary>
    ///路径
    /// </summary>
    @TableField(value = "SHAPEPATH")
    private String shapepath;
    /// <summary>
    ///排序
    /// </summary>
    @TableField(value = "ORDERBYID")
    private Double orderbyid;

    /// <summary>
    ///经度
    /// </summary>
    @TableField(value = "LGTD")
    private Double lgtd;

    /// <summary>
    ///纬度
    /// </summary>
    @TableField(value = "LTTD")
    private Double lttd;
    /// <summary>
    ///水位关联PID
    /// </summary>
    @TableField(value = "SWPID")
    private String swpid;
    /// <summary>
    ///流量关联PID
    /// </summary>
    @TableField(value = "TGTQPID")
    private String tgtqpid;
    /// <summary>
    ///工情关联PID
    /// </summary>
    @TableField(value = "GQPID")
    private String gqpid;
    /// <summary>
    ///备用字段
    /// </summary>
    @TableField(value = "EXPID")
    private String expid;
    /// <summary>
    ///污水井关联PID
    /// </summary>
    @TableField(value = "WSJPID")
    private String wsjpid;

}
