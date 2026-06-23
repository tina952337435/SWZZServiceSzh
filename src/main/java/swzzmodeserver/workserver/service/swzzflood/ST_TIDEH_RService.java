package swzzmodeserver.workserver.service.swzzflood;

import swzzmodeserver.workserver.pojo.swzzwater.BP_DATAPojo;
import swzzmodeserver.workserver.pojo.swzzzjk.ST_TIDEHIGHParam;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ST_TIDEH_RService {

    List<ST_TIDEHIGHParam> WATER_ST_TIDE_RHTIDE(List<String> stcdList, String stime, String etime, String type);

    List<ST_TIDEHIGHParam> WATER_ST_TIDE_RHTIDEHS(List<String> stcdList, String stime, String etime, String type);

    List<Map<String,Object>> WaterRainfallFlowBINYB(String stime,String etime,String stcd,String type);

    List<Map<String,Object>> WaterRainfallFlowBIN(String stime,String etime,String stcd,String type);

    List<BP_DATAPojo> WaterRainfallFlowBINJX(String stime, String etime, String stcd, String type,String strExp);

    List<BP_DATAPojo> shftpconvertGrb2ToNc(String inputGrb2File, String outputNcFile) throws IOException, InterruptedException;

    List<Map<String,Object>> computeHL(String stime, String etime, String stcd);
}
