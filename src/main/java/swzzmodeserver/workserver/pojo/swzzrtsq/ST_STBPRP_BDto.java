package swzzmodeserver.workserver.pojo.swzzrtsq;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ST_STBPRP_BDto {
    private String STCD;
    private String STNM;
    private String RVNM;
    private String HNNM;
    private String BSNM;
    private Double LGTD;
    private Double LTTD;
    private String STLC;
    private String ADDVCD;
    private String DTMNM;
    private Double DTMEL;
    private Double DTPR;
    private String STTP;
    private String FRGRD;
    private String ESSTYM;
    private String BGFRYM;
    private String ATCUNIT;
    private String ADMAUTH;
    private String LOCALITY;
    private String STBK;
    private Double STAZT;
    private Double DSTRVM;
    private Double DRNA;
    private String PHCD;
    private String USFL;
    private String COMMENTS;
    private String MODITIME;
    private List<ST_WDPSTAT_RPojo> wdpstatRList;
    private String year;
    private String tm;
    private String z;
    private String orderIndex;
}
