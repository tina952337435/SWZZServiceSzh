package swzzmodeserver.workserver.pojo.swzzflood;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ST_RAINSTORMFREQUENCY_RPojo {
    private String STCD;           // 站码
    private String BGTM;            // 场次开始时间
    private String ENDTM;           // 场次结束时间
    private Double DRP;       // 场次累计降雨量
    private String ISIN;          // 是否是插补值，1表示插补
    private String RTYPE;         // 雨型
    private Integer HOUR;         // 暴雨历时
    private String BYCENTER;      // 暴雨中心
    private Double AVGDRP;    // 平均雨量
    private Double AREA;      // 笼罩范围
    private Double MAXHOURDRP; // 场次内最大点暴雨最大小时雨量
    private Double DRP24;     // 最大24小时降雨
    private String BGTM24;          // 最大24小时雨量开始时间
    private String ENDTM24;        // 最大24小时雨量结束时间
    private Double SHIDRP;    // 全市平均雨量
    private Double DRP3;      // 最大3h雨量
    private Double DRP6;      // 最大6h雨量
    private Double DRP12;     // 最大12h雨量
    private Double COUNTYDRP; // 所在行政分区平均雨量
    private Double AREADRP;   // 所在水利片区平均雨量
    private Double UPZCHA;    // 水位涨幅
    private Double AREADRPBEFORE24;  // 场次前24小时的水利片区累计降雨量
    private Double COUNTYDRPBEFORE24; // 场次前24小时的行政分区累计降雨量
}