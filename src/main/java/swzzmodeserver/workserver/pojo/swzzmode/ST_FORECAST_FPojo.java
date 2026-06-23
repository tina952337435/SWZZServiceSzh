package swzzmodeserver.workserver.pojo.swzzmode;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ST_FORECAST_FPojo {
    private String STCD;//站 码
    private String UNITNAME;//预报单位
    private String PLCD;//匹配码
    private String FYMDH;//发布时间
    private String IYMDH;//预报时间
    private String YMDH;//发生时间
    private double Z;//水位
    private double Q;//流量
}
