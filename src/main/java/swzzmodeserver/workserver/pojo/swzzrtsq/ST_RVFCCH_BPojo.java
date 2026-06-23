package swzzmodeserver.workserver.pojo.swzzrtsq;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.stereotype.Component;


@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ST_RVFCCH_BPojo {
    private String STCD;
    private Double LDKEL;
    private Double RDKEL;
    private Double WRZ;
    private Double WRQ;
    private Double GRZ;
    private Double GRQ;
    private Double FLPQ;
    private Double OBHTZ;
    private String OBHTZTM;
    private Double IVHZ;
    private String IVHZTM;
    private Double OBMXQ;
    private String OBMXQTM;
    private Double IVMXQ;
    private String IVMXQTM;
    private Double HMXS;
    private String HMXSTM;
    private Double HMXAVV;
    private String HMXAVVTM;
    private Double HLZ;
    private String HLZTM;
    private Double HMNQ;
    private String HMNQTM;
    private Double TAZ;
    private Double TAQ;
    private Double LAZ;
    private Double LAQ;
    private Double SFZ;
    private Double SFQ;
    private String MODITIME;
}
