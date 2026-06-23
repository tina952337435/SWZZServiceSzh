package swzzmodeserver.workserver.service.swzzmode;

import org.springframework.stereotype.Service;
import swzzmodeserver.workserver.pojo.swzzmode.DD_SOLUTIONPojo;
import swzzmodeserver.workserver.pojo.swzzmode.ES_ZHANDIANDATAPojo;
import swzzmodeserver.workserver.pojo.swzzzjk.ST_TIDEHIGHParam;

import java.util.List;
import java.util.Map;

public interface ES_ZHANDIANDATAService {

    Integer FH_modify_batchJY(String zhanid,String solutionid,String dayhour,String zhandata);

    Integer FH_modify_batchJY134(String zhanid,String solutionid,String dayhour,String zhandata);

    Integer FH_ModifyMethod(String zhandata,String zhantime,String zhanid,String dayhour,String solutionid);

    Integer FH_ModifyMethodJY(String zhandata,String zhantime,String zhanid,String dayhour,String solutionid);

    Integer FH_modify_batch(String zhandata,String zhanid,String dayhour,String solutionid);

    Integer chooseTideMethod(String sDate,String eDate,String solutionid,String type);

    Integer TideLineardifference(String solutionid, List<ES_ZHANDIANDATAPojo> list, String stcd, String type,String startDate,String endDate);

    Integer ModifyGCSSLLAREAGCGZ(String solutionid,List<String> zhanid,List<String> yanid);

    Integer FH_inset_ModifyApi(String bdms_predictSqlStr,DD_SOLUTIONPojo ddobj, Boolean isGetCookieDD_ID, String solutionid);

    Integer MODIFY_MODEZHANDData(String startdate,String enddate,String solutionid,String jydatatype,
                                 String gcdatatype,String scwdatatype,String username);

    List<Map<String,Object>> YBSHUIWEI(String stime, String etime, String stcd, String plan_n, String type, String mkeyid, String solutionid);

    Integer FH_modify_batchJYQuan(String dd_id,List<String> ZhanID,String DayHour,String ZhanData);

    Integer modify_byTM(String TM,String dd_id,List<String> ZhanID,String DayHour,String ZhanData);

    List<ST_TIDEHIGHParam> SWZZ_FLOODTIDEDATA_ST_TIDEHIGH_RSel(String stcd, String stime, String etime, String tdptn);

    Integer StatisticalCorrelationModelSW(String solutionid,List<String> strList);

    Integer mx_recalculate(String solutionid,String dd_id);

    Integer ModifyGCSSLLAREAGCGZ_SZH(String solutionid,List<String> zhanid,List<String> yanid);

    Integer ModifyGCSSLLAREAGCGZ_SZHFJL(String solutionid,String stime);

    Integer MODIFY_MODEZHANDDataNew(String startdate,String enddate,String solutionid,String jydatatype,
                                    String gcdatatype,String scwdatatype,String username);

    Integer FH_modify_batchJYQuanJY(String dd_id, List<String> ZhanID, String DayHour, String ZhanData);
}
