package swzzmodeserver.workserver.controller.swzzrtsq;

import swzzmodeserver.tools.ResultUtils;
import swzzmodeserver.workserver.data.swzzrtsq.ST_RIVER_RData;
import swzzmodeserver.workserver.pojo.swzzrtsq.ST_RIVER_RPojo;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/SWZZ_RTSQ_ST_RIVER_R")
public class ST_RIVER_RController {
    @Autowired
    private ST_RIVER_RData data;

    @RequestMapping("/batchResultI")
    public ResultUtils insertAll(@RequestBody List<ST_RIVER_RPojo> quPojo){
        StopWatch watch = new StopWatch();
        watch.start();
        Integer num = data.insertAll(quPojo);
        if(num > 0){
            num=data.upDateMaxRiver(quPojo.get(0));
        }
        watch.stop();

        watch.stop();
        if(num > 0){
            return new ResultUtils<>(num, "操作成功",true, num,watch.getTime());
        }else {
            return new ResultUtils<>(num, "操作成功",false, num,watch.getTime());
        }
    }

}
