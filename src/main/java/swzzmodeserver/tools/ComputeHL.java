package swzzmodeserver.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComputeHL {

    public static List<Map<String,Object>> computeHL(String[][] tideHList){
        List<Map<String,Object>> gdcList = new ArrayList<>();
        boolean vvalid = false;
        double v0 = Double.parseDouble(tideHList[0][1]),v1 = Double.parseDouble(tideHList[1][1]),v2 = Double.parseDouble(tideHList[2][1]);
        // 3小时15分 - 3小时30分
        for (int index = 39; index < tideHList.length - 3 - 39; index++) {
            double v3 = Double.parseDouble(tideHList[index][1]),
                    v4 = Double.parseDouble(tideHList[index + 1][1]),
                    v5 = Double.parseDouble(tideHList[index + 2][1]),
                    v6 = Double.parseDouble(tideHList[index + 3][1]);
            String hlstr = "";
            if (v3 != v5) {
                vvalid = false;
                if (v3 >= v0 && v3 >= v1 && v3 >= v2 && v3 >= v4 && v3 >= v5 && v3 >= v6) {
                    if (v3 >= Double.parseDouble(tideHList[index - 6][1]) && v3 >= Double.parseDouble(tideHList[index - 12][1]) &&
                            v3 >= Double.parseDouble(tideHList[index - 18][1]) && v3 >= Double.parseDouble(tideHList[index - 24][1]) &&
                            v3 >= Double.parseDouble(tideHList[index - 30][1]) && v3 >= Double.parseDouble(tideHList[index - 36][1]) &&
                            v3 >= Double.parseDouble(tideHList[index + 6][1]) && v3 >= Double.parseDouble(tideHList[index + 12][1]) &&
                            v3 >= Double.parseDouble(tideHList[index + 18][1]) && v3 >= Double.parseDouble(tideHList[index + 24][1]) &&
                            v3 >= Double.parseDouble(tideHList[index + 30][1]) && v3 >= Double.parseDouble(tideHList[index + 36][1])) {
                        vvalid = true;
                        hlstr = "H";
                    }
                } else if (v3 <= v0 && v3 <= v1 && v3 <= v2 && v3 <= v4 && v3 <= v5 && v3 <= v6) {
                    if (v3 <= Double.parseDouble(tideHList[index - 6][1]) && v3 <= Double.parseDouble(tideHList[index - 12][1]) &&
                            v3 <= Double.parseDouble(tideHList[index - 18][1]) && v3 <= Double.parseDouble(tideHList[index - 24][1]) &&
                            v3 <= Double.parseDouble(tideHList[index - 30][1]) && v3 <= Double.parseDouble(tideHList[index - 36][1]) &&
                            v3 <= Double.parseDouble(tideHList[index + 6][1]) && v3 <= Double.parseDouble(tideHList[index + 12][1]) &&
                            v3 <= Double.parseDouble(tideHList[index + 18][1]) && v3 <= Double.parseDouble(tideHList[index + 24][1]) &&
                            v3 <= Double.parseDouble(tideHList[index + 30][1]) && v3 <= Double.parseDouble(tideHList[index + 36][1])) {
                        vvalid = true;
                        hlstr = "L";
                    }
                }
                if (vvalid) {
                    Map<String,Object> map = new HashMap<>();
                    map.put("tm",tideHList[index][0]);
                    map.put("tdz",tideHList[index][1]);
                    map.put("isHL",hlstr);
                    gdcList.add(map);
                    index = (index - 1) + 180 / 5;
                }
                v0 = v1;
                v1 = v2;
                v2 = v3;
            }
        }
        return gdcList;
    }
}
