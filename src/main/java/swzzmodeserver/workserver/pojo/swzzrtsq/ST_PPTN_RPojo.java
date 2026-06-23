package swzzmodeserver.workserver.pojo.swzzrtsq;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ST_PPTN_RPojo {
    /**
     *站码
     */
    private String STCD;
    /**
     *时间
     */
    private String TM;
    /**
     *降雨量
     */
    private Double DRP;
    private Double INTV;
    private Double PDR;
    private Double DYP;
    private String WTH;

    private String STNM;
    /**
     *区镇
     */
    private String ADMAUTH;
    private String ADDVNM;
    /**
     *河流名称
     */
    private String RVNM;
    /**
     *流域名称
     */
    private String BSNM;
    private String HNNM;
    private String STLC;
    private String MTYPE;

    private Double LGTD;
    private Double LTTD;
    private String PATHNAME;
    private Double LGTD84;
    private Double LTTD84;
    /**
     * 场次排名
     */
    private Integer RANK;
    /**
     * 最大24小时雨量
     */
    private Double DRP24;
    private Integer STORMTOTAL;//暴雨场次
    /**
     * 数据来源
     */
    private String ATCUNIT;
}
