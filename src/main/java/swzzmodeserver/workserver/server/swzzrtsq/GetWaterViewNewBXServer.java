package swzzmodeserver.workserver.server.swzzrtsq;

import swzzmodeserver.workserver.data.swzzrtsq.GetWaterViewNewBXData;
import swzzmodeserver.workserver.data.swzzrtsq.RTSQST_STBPRP_BData;
import swzzmodeserver.workserver.data.swzzrtsq.ST_STBPRP_B_QUData;
import swzzmodeserver.workserver.pojo.swzzrtsq.GetWaterViewNewPojo;
import swzzmodeserver.workserver.pojo.swzzrtsq.ST_STBPRP_BPojo;
import swzzmodeserver.workserver.pojo.swzzrtsq.ST_STBPRP_B_QUPojo;
import swzzmodeserver.tools.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swzzmodeserver.workserver.pojo.swzzrtsq.GetWaterViewNewPojo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GetWaterViewNewBXServer {
    @Autowired
    private GetWaterViewNewBXData data;
    @Autowired
    private ST_STBPRP_B_QUData quData;
    @Autowired
    private GetWaterViewNewBXServer server;
    @Autowired
    private RTSQST_STBPRP_BData stbprpBData;

    public List<GetWaterViewNewPojo> selectListNewAndLength(String PID,String stime,String etime){
        List<ST_STBPRP_B_QUPojo> quList = quData.selectList("", "", null, PID,null);
        List<String> stcdList = new ArrayList<>();
        if(null != quList && quList.size() > 0){
            for(ST_STBPRP_B_QUPojo quPojo : quList){
                if(null != quPojo.getSTCD()){
                    stcdList.add(quPojo.getSTCD());
                }
            }
        }
        if(!(stcdList.size() > 0)){
            return null;
        }
        List<ST_STBPRP_BPojo> stStbprpBList = stbprpBData.selectList(stcdList, null);
        List<GetWaterViewNewPojo> waterViewNewListNew = server.selectListByNew(stcdList, stime, etime);
        List<GetWaterViewNewPojo> waterViewNewList = new ArrayList<>();
        if(waterViewNewListNew.size() > 0){
            for(int i = 0;i < quList.size()-1;i++) {
                GetWaterViewNewPojo water1 =  new GetWaterViewNewPojo() ;
                GetWaterViewNewPojo water2 = new GetWaterViewNewPojo() ;

                ST_STBPRP_B_QUPojo quDto = quList.get(i);
                List<GetWaterViewNewPojo> collects = waterViewNewListNew.stream().filter(p ->
                        p.getSTCD().equals(quDto.getSTCD())&&(Double.valueOf(p.getUPZ())==0&&Double.valueOf(p.getDWZ())==0)==false).collect(Collectors.toList());
                if(collects.size() > 0){
                    water1 = collects.get(0);
                }else{
                    List<ST_STBPRP_BPojo> collectsModel = stStbprpBList.stream().filter(p ->
                            p.getSTCD().equals(quDto.getSTCD())).collect(Collectors.toList());
                    water1.setSTCD(collectsModel.get(0).getSTCD());
                    water1.setSTNM(collectsModel.get(0).getSTNM());
                    water1.setLGTD(collectsModel.get(0).getLGTD().toString());
                    water1.setLTTD(collectsModel.get(0).getLTTD().toString());
                }

                ST_STBPRP_B_QUPojo quDto2 = quList.get(i + 1);
                List<GetWaterViewNewPojo> collects2 = waterViewNewListNew.stream().filter(p ->
                        p.getSTCD().equals(quDto2.getSTCD())&&(Double.valueOf(p.getUPZ())==0&&Double.valueOf(p.getDWZ())==0)==false).collect(Collectors.toList());
                if(collects2.size() > 0) {
                    water2 = collects2.get(0);
                    double g1 = Double.parseDouble(water1.getLGTD());
                    double t1 = Double.parseDouble(water1.getLTTD());
                    double g2 = Double.parseDouble(water2.getLGTD());
                    double t2 = Double.parseDouble(water2.getLTTD());
                    double distance = GetWaterViewNewBXServer.GetDistance(g1, t1, g2, t2);
                    water2.setDistance(distance);
                }else{
                    List<ST_STBPRP_BPojo> collectsModel = stStbprpBList.stream().filter(p ->
                            p.getSTCD().equals(quDto2.getSTCD())).collect(Collectors.toList());
                    double g1 = Double.parseDouble(water1.getLGTD());
                    double t1 = Double.parseDouble(water1.getLTTD());
                    double g2 = Double.parseDouble(collectsModel.get(0).getLGTD().toString());
                    double t2 = Double.parseDouble(collectsModel.get(0).getLTTD().toString());
                    double distance = GetWaterViewNewBXServer.GetDistance(g1, t1, g2, t2);

                    water2.setSTCD(collectsModel.get(0).getSTCD());
                    water2.setSTNM(collectsModel.get(0).getSTNM());
                    water2.setLGTD(collectsModel.get(0).getLGTD().toString());
                    water2.setLTTD(collectsModel.get(0).getLTTD().toString());
                    water2.setDistance(distance);
                }

                if(i < 1 ){
                    waterViewNewList.add(water1);
                    waterViewNewList.add(water2);
                }else{
                    waterViewNewList.add(water2);
                }

            }
        }else{
            for(int i = 0;i < quList.size()-1;i++){
                ST_STBPRP_B_QUPojo quDto = quList.get(i);
                List<ST_STBPRP_BPojo> collects = stStbprpBList.stream().filter(p ->
                        p.getSTCD().equals(quDto.getSTCD())).collect(Collectors.toList());
                GetWaterViewNewPojo water1 = new GetWaterViewNewPojo();
                GetWaterViewNewPojo water2 = new GetWaterViewNewPojo() ;
                water1.setSTCD(collects.get(0).getSTCD());
                water1.setSTNM(collects.get(0).getSTNM());
                water1.setLGTD(collects.get(0).getLGTD().toString());
                water1.setLTTD(collects.get(0).getLTTD().toString());

                double g1 = Double.parseDouble(water1.getLGTD());
                double t1 = Double.parseDouble(water1.getLTTD());

                ST_STBPRP_B_QUPojo quDto2 = quList.get(i + 1);
                List<ST_STBPRP_BPojo> collects2 = stStbprpBList.stream().filter(p ->
                        p.getSTCD().equals(quDto2.getSTCD())).collect(Collectors.toList());
                double g2 = Double.parseDouble(collects2.get(0).getLGTD().toString());
                double t2 = Double.parseDouble(collects2.get(0).getLTTD().toString());
                double distance = GetWaterViewNewBXServer.GetDistance(g1, t1, g2, t2);

                water2.setSTCD(collects2.get(0).getSTCD());
                water2.setSTNM(collects2.get(0).getSTNM());
                water2.setLGTD(collects2.get(0).getLGTD().toString());
                water2.setLTTD(collects2.get(0).getLTTD().toString());
                water2.setDistance(distance);

                if(i < 1 ){
                    waterViewNewList.add(water1);
                    waterViewNewList.add(water2);
                }else{
                    waterViewNewList.add(water2);
                }
            }
        }
        return waterViewNewList;
    }

    //计算经纬度距离
    private static double EARTH_RADIUS = 6371000;//赤道半径(单位m)

    /**
     * 转化为弧度(rad)
     * */
    private static double rad(double d)
    {
        return d * Math.PI / 180.0;
    }
    /**
     * @param lon1 第一点的精度
     * @param lat1 第一点的纬度
     * @param lon2 第二点的精度
     * @param lat2 第二点的纬度
     * @return 返回的距离，单位m
     * */
    public static double GetDistance(double lon1,double lat1,double lon2, double lat2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lon1) - rad(lon2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }
    public List<GetWaterViewNewPojo> queryByRiver(List<String> stcdList,String stime,String etime){
        List<GetWaterViewNewPojo> waterViewListRiver = data.queryByRiver(stcdList, stime, etime,"Minute");

        /**
         *  联合主键的情况下
         *  分组 后获取创建时间 最新的一条
         */
        List<GetWaterViewNewPojo> collect = new ArrayList<>(waterViewListRiver.stream().collect(Collectors.toMap(
                GetWaterViewNewPojo::getSTCD,
                v -> v,
                (v1, v2) -> {
                    Date dataTime = DateUtil.strToDateTM(v1.getTM(),DateUtil.YMDHMS);
                    Date startDate1 =  DateUtil.strToDateTM(v2.getTM(),DateUtil.YMDHMS);
                    if (dataTime == null || startDate1 == null) {
                        return dataTime != null ? v1 : v2; //拿到不为空的 ，都为空取后一条
                    }
                    return dataTime.after(startDate1) || dataTime.equals(startDate1) ? v1 : v2;
                }
        )).values());

        return collect;

    }
    public List<GetWaterViewNewPojo> queryByWas(List<String> stcdList,String stime,String etime){
        List<GetWaterViewNewPojo> waterViewListRiver = data.queryByWas(stcdList, stime, etime,"Minute");

        /**
         *  联合主键的情况下
         *  分组 后获取创建时间 最新的一条
         */
        List<GetWaterViewNewPojo> collect = new ArrayList<>(waterViewListRiver.stream().collect(Collectors.toMap(
                GetWaterViewNewPojo::getSTCD,
                v -> v,
                (v1, v2) -> {
                    Date dataTime = DateUtil.strToDateTM(v1.getTM(),DateUtil.YMDHMS);
                    Date startDate1 =  DateUtil.strToDateTM(v2.getTM(),DateUtil.YMDHMS);
                    if (dataTime == null || startDate1 == null) {
                        return dataTime != null ? v1 : v2; //拿到不为空的 ，都为空取后一条
                    }
                    return dataTime.after(startDate1) || dataTime.equals(startDate1) ? v1 : v2;
                }
        )).values());

        return collect;

    }
    public List<GetWaterViewNewPojo> queryByTide(List<String> stcdList, String stime, String etime){
        List<GetWaterViewNewPojo> waterViewListRiver = data.queryByTide(stcdList, stime, etime,"Minute");

        /**
         *  联合主键的情况下
         *  分组 后获取创建时间 最新的一条
         */
        List<GetWaterViewNewPojo> collect = new ArrayList<>(waterViewListRiver.stream().collect(Collectors.toMap(
                GetWaterViewNewPojo::getSTCD,
                v -> v,
                (v1, v2) -> {
                    Date dataTime = DateUtil.strToDateTM(v1.getTM(),DateUtil.YMDHMS);
                    Date startDate1 =  DateUtil.strToDateTM(v2.getTM(),DateUtil.YMDHMS);
                    if (dataTime == null || startDate1 == null) {
                        return dataTime != null ? v1 : v2; //拿到不为空的 ，都为空取后一条
                    }
                    return dataTime.after(startDate1) || dataTime.equals(startDate1) ? v1 : v2;
                }
        )).values());

        return collect;

    }
    public List<GetWaterViewNewPojo> queryByPump(List<String> stcdList,String stime,String etime){
        List<GetWaterViewNewPojo> waterViewListRiver = data.queryByPump(stcdList, stime, etime,"Minute");

        /**
         *  联合主键的情况下
         *  分组 后获取创建时间 最新的一条
         */
        List<GetWaterViewNewPojo> collect = new ArrayList<>(waterViewListRiver.stream().collect(Collectors.toMap(
                GetWaterViewNewPojo::getSTCD,
                v -> v,
                (v1, v2) -> {
                    Date dataTime = DateUtil.strToDateTM(v1.getTM(),DateUtil.YMDHMS);
                    Date startDate1 =  DateUtil.strToDateTM(v2.getTM(),DateUtil.YMDHMS);
                    if (dataTime == null || startDate1 == null) {
                        return dataTime != null ? v1 : v2; //拿到不为空的 ，都为空取后一条
                    }
                    return dataTime.after(startDate1) || dataTime.equals(startDate1) ? v1 : v2;
                }
        )).values());

        return collect;

    }

    //水位实时数据
    public List<GetWaterViewNewPojo> selectListByNew(List<String> stcdList,String stime,String etime){
        List<GetWaterViewNewPojo> waterViewList = new ArrayList<>();

        List<GetWaterViewNewPojo> waterViewListRiver = queryByRiver(stcdList, stime, etime);
        if(null != waterViewListRiver && waterViewListRiver.size() > 0) {
            for (int i = 0; i < waterViewListRiver.size(); i++) {
                waterViewList.add(waterViewListRiver.get(i));
            }
        }

        List<GetWaterViewNewPojo> waterViewListWas = queryByWas(stcdList, stime, etime);
        if(null != waterViewListWas && waterViewListWas.size() > 0) {
            for (int i = 0; i < waterViewListWas.size(); i++) {
                waterViewList.add(waterViewListWas.get(i));
            }
        }

        List<GetWaterViewNewPojo> waterViewListTide = queryByTide(stcdList, stime, etime);
        if(null != waterViewListTide && waterViewListTide.size() > 0) {
            for (int i = 0; i < waterViewListTide.size(); i++) {
                waterViewList.add(waterViewListTide.get(i));
            }
        }

        List<GetWaterViewNewPojo> waterViewListPump = queryByPump(stcdList, stime, etime);
        if(null != waterViewListPump && waterViewListPump.size() > 0) {
            for (int i = 0; i < waterViewListPump.size(); i++) {
                waterViewList.add(waterViewListPump.get(i));
            }
        }
        return  waterViewList;
    }

    //单站分钟过程
    public List<GetWaterViewNewPojo> selectListByHisIsTime(List<String> stcdList,String stime,String etime) {
        List<GetWaterViewNewPojo> waterViewList = new ArrayList<>();

        List<GetWaterViewNewPojo> waterViewListRiver = data.selectListMinuteByRiver(stcdList, stime, etime);
        if(null != waterViewListRiver && waterViewListRiver.size() > 0) {
            waterViewList = waterViewListRiver;
        }


            List<GetWaterViewNewPojo> waterViewListWas = data.selectListMinuteByWas(stcdList, stime, etime);
            if (null != waterViewListWas && waterViewListWas.size() > 0) {
                for (int i = 0; i < waterViewListWas.size(); i++) {
                    waterViewList.add(waterViewListWas.get(i));
                }
            }


            List<GetWaterViewNewPojo> waterViewListTide = data.selectListMinuteByTide(stcdList, stime, etime);
            if (null != waterViewListTide && waterViewListTide.size() > 0) {
                for (int i = 0; i < waterViewListTide.size(); i++) {
                    waterViewList.add(waterViewListTide.get(i));
                }
            }


            List<GetWaterViewNewPojo> waterViewListPump = data.selectListMinuteByPump(stcdList, stime, etime);
            if (null != waterViewListPump && waterViewListPump.size() > 0) {
                for (int i = 0; i < waterViewListPump.size(); i++) {
                    waterViewList.add(waterViewListPump.get(i));
                }
            }

        return  waterViewList;
    }

    //单站小时过程
    public List<GetWaterViewNewPojo> selectListByHisIsHouse(List<String> stcdList,String stime,String etime) {
        List<GetWaterViewNewPojo> waterViewList = new ArrayList<>();

        List<GetWaterViewNewPojo> waterViewListRiver = data.selectListHourByRiver(stcdList, stime, etime);
        if(null != waterViewListRiver && waterViewListRiver.size() > 0) {
            waterViewList = waterViewListRiver;
        }

            List<GetWaterViewNewPojo> waterViewListWas = data.selectListHourByWas(stcdList, stime, etime);
            if (null != waterViewListWas && waterViewListWas.size() > 0) {
                for (int i = 0; i < waterViewListWas.size(); i++) {
                    waterViewList.add(waterViewListWas.get(i));
                }
            }


            List<GetWaterViewNewPojo> waterViewListTide = data.selectListHourByTide(stcdList, stime, etime);
            if (null != waterViewListTide && waterViewListTide.size() > 0) {
                for (int i = 0; i < waterViewListTide.size(); i++) {
                    waterViewList.add(waterViewListTide.get(i));
                }
            }


            List<GetWaterViewNewPojo> waterViewListPump = data.selectListHourByPump(stcdList, stime, etime);
            if (null != waterViewListPump && waterViewListPump.size() > 0) {
                for (int i = 0; i < waterViewListPump.size(); i++) {
                    waterViewList.add(waterViewListPump.get(i));
                }
            }

        return  waterViewList;
    }

    //单站日过程
    public List<GetWaterViewNewPojo> selectListByHisIsDay(List<String> stcdList,String stime,String etime) {
        List<GetWaterViewNewPojo> waterViewList = new ArrayList<>();

        List<GetWaterViewNewPojo> waterViewListRiver = data.selectListDayByRiver(stcdList, stime, etime);
        if(null != waterViewListRiver && waterViewListRiver.size() > 0) {
            waterViewList = waterViewListRiver;
        }

            List<GetWaterViewNewPojo> waterViewListWas = data.selectListDayByWas(stcdList, stime, etime);
            if (null != waterViewListWas && waterViewListWas.size() > 0) {
                for (int i = 0; i < waterViewListWas.size(); i++) {
                    waterViewList.add(waterViewListWas.get(i));
                }
            }


            List<GetWaterViewNewPojo> waterViewListTide = data.selectListDayByTide(stcdList, stime, etime);
            if (null != waterViewListTide && waterViewListTide.size() > 0) {
                for (int i = 0; i < waterViewListTide.size(); i++) {
                    waterViewList.add(waterViewListTide.get(i));
                }
            }


            List<GetWaterViewNewPojo> waterViewListPump = data.selectListDayByPump(stcdList, stime, etime);
            if (null != waterViewListPump && waterViewListPump.size() > 0) {
                for (int i = 0; i < waterViewListPump.size(); i++) {
                    waterViewList.add(waterViewListPump.get(i));
                }
            }

        return  waterViewList;
    }

}
