package swzzmodeserver.workserver.pojo.swzzrtsq;

import lombok.Data;
import java.util.List;

@Data
public class SZBZGKResponse {
    private String msg;
    private int code;
    private ResponseData data;

    @Data
    public static class ResponseData {
        private List<ResultItem> result;
        private int totalPage;
        private int totalRows;
    }

    @Data
    public static class ResultItem {
        private String STATIONID;
        private String STATIONNAME;
        private String DATETIME;
        private Double PUMPLEVEL;
        private Double FLOW;
        private Double RAINFALL;
        private String RPUMPTYPE;
        private Integer RPUMP_R;
        private Integer RPUMP_F;
        private Integer RPUMP_C;
        private String SPUMPTYPE;
        private Integer SPUMP_R;
        private Integer SPUMP_F;
        private Integer SPUMP_C;
        private Integer FX_OPEN;
        private Integer FX_CLOSE;
        private Integer JY_OPEN;
        private Integer JY_CLOSE;
        private String DATASOURCE;
        private String QUYU;
        private String TYPE;
        private String SQTYPE;
        private String SLP;
        private String HELIU;
        private Double XX;
        private Double YY;
        private Double XX2000;
        private Double YY2000;
        private Integer BZJB;
        private String BJYXZT;
    }
}