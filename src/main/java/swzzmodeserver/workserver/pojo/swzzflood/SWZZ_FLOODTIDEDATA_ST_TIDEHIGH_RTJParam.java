package swzzmodeserver.workserver.pojo.swzzflood;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class SWZZ_FLOODTIDEDATA_ST_TIDEHIGH_RTJParam {
    private String STCD;      // 站码
    private String STNM;      // 站名
    private Double MAXZ;      // 最高潮位
    private String MAXZTM;    // 最高潮位时间
    private Double MAXZS;     // 最大增水
    private String MAXZSTM;   // 最大增水时间
    private Double MAXZZS;    // 高潮增水
    private String MAXZZSTM;  // 高潮增水时间
    private Double WRZ;       // 警戒水位
    private Double WRZFD;     // 超警幅度
    private Double WRZCOUT;   // 超警次数
}