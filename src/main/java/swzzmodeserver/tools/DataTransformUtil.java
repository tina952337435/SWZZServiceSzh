package swzzmodeserver.tools;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import swzzmodeserver.workserver.pojo.swzzrtsq.ChuLaoBengZhaResponse;
import swzzmodeserver.workserver.pojo.swzzrtsq.ChuLaoBengZhaResponse.GateWasData;
import swzzmodeserver.workserver.pojo.swzzrtsq.ST_GATE_RPojo;

import swzzmodeserver.workserver.pojo.swzzrtsq.ST_WAS_RPojo;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataTransformUtil {

    @Value("${file.path.templatefilepath}")
    private String filePathName;

    /**
     * 将接口返回的 Result 列表转换为数据库实体列表
     */
    public GateWasData transformToGateR(List<ChuLaoBengZhaResponse.ResultItem> resultList, double Sfq) {
        new javalog().writelog("=== transformToGateR called, input size=" + resultList.size(), filePathName,
                "SWZZServiceGate");
        List<ST_GATE_RPojo> saveList = new ArrayList<>();
        List<ST_WAS_RPojo> saveWasList = new ArrayList<>();
        for (ChuLaoBengZhaResponse.ResultItem item : resultList) {
            List<ST_GATE_RPojo> currentPumpList = new ArrayList<>();
            // 1. 处理闸门数据 (DOOR1 - DOOR9)
            try {
                if (item.getDOOR1() != null) {
                    saveList.add(createGateRecord(item, "1", item.getDOOR1()));
                }
                if (item.getDOOR2() != null) {
                    saveList.add(createGateRecord(item, "2", item.getDOOR2()));
                }
                if (item.getDOOR3() != null) {
                    saveList.add(createGateRecord(item, "3", item.getDOOR3()));
                }
                if (item.getDOOR4() != null) {
                    saveList.add(createGateRecord(item, "4", item.getDOOR4()));
                }
                if (item.getDOOR5() != null) {
                    saveList.add(createGateRecord(item, "5", item.getDOOR5()));
                }
                if (item.getDOOR6() != null) {
                    saveList.add(createGateRecord(item, "6", item.getDOOR6()));
                }
                if (item.getDOOR7() != null) {
                    saveList.add(createGateRecord(item, "7", item.getDOOR7()));
                }
                if (item.getDOOR8() != null) {
                    saveList.add(createGateRecord(item, "8", item.getDOOR8()));
                }
                if (item.getDOOR9() != null) {
                    saveList.add(createGateRecord(item, "9", item.getDOOR9()));
                }
            } catch (Exception e) {
                // TODO: handle exception
                new javalog().writelog("=== " + item.getSTATIONID() + "站闸门数据异常:"+e.getMessage(), filePathName, "SWZZServiceGateError");
            }

            // 2. 处理泵站数据 (PUMP1 - PUMP6)
            try {
                // new javalog().writelog("1泵站数据：：：：：：："+item, filePathName,"SWZZServiceGateError");
                if (item.getPUMP1() != null) {
                    currentPumpList.add(
                            createPumpRecord(item, "1", item.getPUMP1(), Sfq, item.getOUTWATER(), item.getINWATER()));
                }
                if (item.getPUMP2() != null) {
                    currentPumpList.add(
                            createPumpRecord(item, "2", item.getPUMP2(), Sfq, item.getOUTWATER(), item.getINWATER()));
                }
                if (item.getPUMP3() != null) {
                    currentPumpList.add(
                            createPumpRecord(item, "3", item.getPUMP3(), Sfq, item.getOUTWATER(), item.getINWATER()));
                }
                if (item.getPUMP4() != null) {
                    currentPumpList.add(
                            createPumpRecord(item, "4", item.getPUMP4(), Sfq, item.getOUTWATER(), item.getINWATER()));
                }
                if (item.getPUMP5() != null) {
                    currentPumpList.add(
                            createPumpRecord(item, "5", item.getPUMP5(), Sfq, item.getOUTWATER(), item.getINWATER()));
                }
                if (item.getPUMP6() != null) {
                    currentPumpList.add(
                            createPumpRecord(item, "6", item.getPUMP6(), Sfq, item.getOUTWATER(), item.getINWATER()));
                }
                // new javalog().writelog("2泵站数据：：：：：：："+item, filePathName,"SWZZServiceGateError");
                // 统计当前这条记录的开启泵数量
                int GTOPNUM = (int) currentPumpList.stream().filter(p -> p.getGTOPHGT() > 0).count();
                currentPumpList.forEach(p -> p.setGTOPNUM((double) GTOPNUM));
                saveList.addAll(currentPumpList);
            } catch (Exception e) {
                // TODO: handle exception
                new javalog().writelog("=== " + item.getSTATIONID() + "站泵站数据异常:"+e.getMessage(), filePathName, "SWZZServiceGateError");
            }

            // 3. 处理水位数据 (OUTWATER, INWATER)
            try {
                ST_WAS_RPojo was_RPojo = new ST_WAS_RPojo();
                was_RPojo.setStcd(item.getSTATIONID());
                was_RPojo.setTm(item.getDATETIME());
                if (item.getOUTWATER() != null) {
                    was_RPojo.setUpz(String.valueOf(item.getOUTWATER()));
                }
                if (item.getINWATER() != null) {
                    was_RPojo.setDwz(String.valueOf(item.getINWATER()));
                }
                saveWasList.add(was_RPojo);
            } catch (Exception e) {
                // TODO: handle exception
                new javalog().writelog("=== " + item.getSTATIONID() + "站水位数据异常:"+e.getMessage(), filePathName, "SWZZServiceGateError");            
            }
            
        }
        GateWasData item = new GateWasData();
        item.setGateDate(saveList);
        item.setWasDate(saveWasList);
        return item;
    }

    // 构建闸门记录
    private ST_GATE_RPojo createGateRecord(ChuLaoBengZhaResponse.ResultItem item, String pumpNo, Double doorOpen) {
        ST_GATE_RPojo pojo = new ST_GATE_RPojo();
        pojo.setSTCD(item.getSTATIONID()); // 站码
        pojo.setTM(item.getDATETIME()); // 时间
        pojo.setEXKEY(pumpNo); // 唯一键
        pojo.setEQPTP("闸坝开度"); // 类型
        pojo.setEQPNO("1"); // 编号
        pojo.setGTOPHGT(doorOpen); // 开启高度
        pojo.setGTQ(doorOpen);
        // 其他字段如 GTQ, MSQMT 等根据业务逻辑填充
        return pojo;
    }

    // 构建泵站记录
    private ST_GATE_RPojo createPumpRecord(ChuLaoBengZhaResponse.ResultItem item, String pumpNo, Integer pumpStatus,
            double Sfq, Double upz, Double dwz) {
        ST_GATE_RPojo pojo = new ST_GATE_RPojo();
        pojo.setSTCD(item.getSTATIONID());
        pojo.setTM(item.getDATETIME());
        pojo.setEXKEY(pumpNo);
        pojo.setEQPTP("泵站状态");
        pojo.setEQPNO("2");

        // 如果 PUMP 字段仅表示开关，这里可能需要映射。
        // 例如：如果 GTOPHGT 代表状态，或者你需要根据状态计算 GTQ
        // 这里暂时将状态值存入 GTOPHGT 或根据业务修改
        pojo.setGTOPHGT(pumpStatus.doubleValue());
        if (pumpStatus == 0) {// 0代表关闭，1代表打开
            Sfq = 0;
        }
        // 根据状态计算 GTQ
        pojo.setGTQ(Sfq);// 默认只要开了就是开全

        if(upz!=null&&dwz!=null){   
            // upz 闸外水位,dwz 闸内水位
            // 闸内水位低于闸外水位，泵机就是在排水；
            // 闸内水位高于闸外水位，泵机就是在引水；
            if (Sfq > 0) {
                if (dwz <= upz) {// 闸内水位低于闸外水位，泵机就是在排水；
                    pojo.setTYPE("1");
                } else {// 闸内水位高于闸外水位，泵机就是在引水；
                    pojo.setTYPE("-1");
                }
            }
        }
        return pojo;
    }
}