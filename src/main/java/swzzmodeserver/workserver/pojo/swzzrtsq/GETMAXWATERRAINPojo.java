package swzzmodeserver.workserver.pojo.swzzrtsq;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class GETMAXWATERRAINPojo {
    public String STCD;
    public String STNM;
    public String RVNM;
    public String HNNM;
    public String BSNM;
    public Double LGTD;
    public Double LTTD;
    public String STTP;
    public Double DRP;
    public String TM;
    public String DRPTM;
    public String SORT;
    public String Z;
    //最大日雨量
    public Double MAX_DDRP;
    //最大日雨量时间
    public String MAX_DTM;
    //最大小时雨量
    public Double MAX_HDRP;
    //最大小时雨量时间
    public String MAX_HTM;
    public int W_SORT;
}
