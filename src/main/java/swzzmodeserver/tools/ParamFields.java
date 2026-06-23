package swzzmodeserver.tools;

import lombok.Data;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

import swzzmodeserver.workserver.pojo.swzzmode.DD_SOLUTIONPojo;
import swzzmodeserver.workserver.pojo.swzzmode.ES_ZHANDIANDATAPojo;

import java.util.List;
import java.util.Map;

@Component
@Data
public class ParamFields {
    private String startdate;
    private String enddate;
    private String solutionid;
    private String datatype;
    private String dayhour;
    private String fpdr;
    private String ZhanData;
    private String ZhanID;
    private String dd_id;
    private String ZhanTime;
    private String type;
    private List<ES_ZHANDIANDATAPojo> ListMode;
    private String stcd;
    private String gcddfa;
    private String bdms_predictSqlStr;
    private Boolean isGetCookieDD_ID;
    private String DD_MIND;
    private String DATA_TYPE;
    private String DD_EVALUE;
    private String MKEYID;
    private String TM;
    private String typeID;
    private DD_SOLUTIONPojo ddobj;
    private String QX_LOGIN;
    private String QX_PASSWORD;
    private String jydatatype;
    private String gcdatatype;
    private String scwdatatype;
    // private String username;
    private String ojbList;
    private String filename;
    private String plan_n;
    private String tfbh;
    private String isWX;
    private String ddwj;
    private String ZJ_YBSJ;
    private String ZJ_TM;
    private String isYB;
    private String rqsj2;
    private String tfybdw;
    private String tdptn;
    private String Tdptn;
    private String ddobjson;

     @JsonProperty("USERNAME")
    private String username;
    @JsonProperty("PWD")
    private String pwd;
}
