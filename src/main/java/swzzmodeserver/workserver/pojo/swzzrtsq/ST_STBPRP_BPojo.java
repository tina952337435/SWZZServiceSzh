package swzzmodeserver.workserver.pojo.swzzrtsq;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ST_STBPRP_BPojo {
    private String STCD;
    private String STNM;
    private String RVNM;
    private String HNNM;
    private String BSNM;
    private Double LGTD;
    private Double LTTD;
    private String STLC;
    private String ADDVCD;
    private String ADDVNM;
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
    private Double WRZ;
    private Double GRZ;
    private String MTYPE;
    private String INTM;
    private String NT;
    private List<ST_WDPSTAT_RPojo> wdpstatRList;

    private String TYPENAME;
    private boolean ONLINE;


    private String SFLAG;
    private String YFLAG;
    private String TDLAG;
    private String WDLAG;
    private String FLOWLAG;
    private String GATELAG;
    private String HNNMK;
    private Double LGTD84;
    private Double LTTD84;
    private String SLP;//水利片名称
}
