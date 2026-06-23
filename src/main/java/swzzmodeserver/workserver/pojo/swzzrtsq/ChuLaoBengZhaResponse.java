package swzzmodeserver.workserver.pojo.swzzrtsq;

import lombok.Data;
// import swzzmodeserver.workserver.pojo.swzzrtsq.ChuLaoBengZhaResponse.ResultItem;

import java.util.List;

@Data
public class ChuLaoBengZhaResponse {
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
        private Double INWATER;
        private Double OUTWATER;
        // 闸门开度 1-9
        private Double DOOR1;
        private Double DOOR2;
        private Double DOOR3;
        private Double DOOR4;
        private Double DOOR5;
        private Double DOOR6;
        private Double DOOR7;
        private Double DOOR8;
        private Double DOOR9;
        // 泵机状态 1-6
        private Integer PUMP1;
        private Integer PUMP2;
        private Integer PUMP3;
        private Integer PUMP4;
        private Integer PUMP5;
        private Integer PUMP6;

        // 其他字段
        private String DATASOURCE;
        private String QUYU;
        private String TYPE;
        private String SQTYPE;
        private String SLP;
        private String HELIU;
        private Double XX; private Double YY;
        private Double XX2000; private Double YY2000;
        private Integer BZJB;
        private String STATUSOUT;
        private String STATUSIN;
    }

    @Data
    public static class GateWasData {
        private List<ST_GATE_RPojo> gateDate;        
        private List<ST_WAS_RPojo> wasDate;
    }
}