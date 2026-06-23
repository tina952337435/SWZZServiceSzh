package swzzmodeserver.workserver.controller.swzzrtsq;

import swzzmodeserver.tools.ResultUtils;
import swzzmodeserver.workserver.data.swzzrtsq.ST_STBPRP_B_TREEData;
import swzzmodeserver.workserver.pojo.swzzrtsq.ST_STBPRP_B_TREEPojo;
import swzzmodeserver.workserver.server.swzzrtsq.ST_STBPRP_B_TREEServer;
import swzzmodeserver.tools.ColumnName;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * 菜单测试接口
 */

@RestController
@RequestMapping("/SWZZ_RTSQ_ST_STBPRP_B_TREE")
public class ST_STBPRP_B_TREEController {
    @Autowired
    private ST_STBPRP_B_TREEServer stStbprpBTreeServer;
    @Autowired
    private ST_STBPRP_B_TREEData data;

    @RequestMapping("/selectMenu")
    public ResultUtils selectMenu(@RequestBody ColumnName param){
        StopWatch watch = new StopWatch();
        watch.start();
        String pid = "-1",pathname = "";
        if(null != param.getPid()){
            pid = param.getPid();
        }
        if(null != param.getPathname()){
            pathname = param.getPathname();
        }
//        List<ST_STBPRP_B_TREEPojo> treeList = stStbprpBTreeServer.selectMenu(pathname);
        List<ST_STBPRP_B_TREEPojo> treeList = stStbprpBTreeServer.selectMenu(pathname,pid);
        watch.stop();

        if(treeList.size() > 0){
            return new ResultUtils<>(treeList, "操作成功",true ,treeList.size(),watch.getTime());
        }else {
            return new ResultUtils<>(treeList, "操作成功",false,treeList.size(),watch.getTime());
        }
    }
    @RequestMapping("/findResult")
    public ResultUtils selectList(@RequestBody ST_STBPRP_B_TREEPojo param){
        StopWatch watch = new StopWatch();
        watch.start();
        List<ST_STBPRP_B_TREEPojo> treeList = data.selectList(param);
        watch.stop();
        if(treeList.size() > 0){
            return new ResultUtils<>(treeList, "操作成功",true ,treeList.size(),watch.getTime());
        }else {
            return new ResultUtils<>(treeList, "操作成功",false,treeList.size(),watch.getTime());
        }
    }
    @RequestMapping("/add")
    public ResultUtils insertOne(@RequestBody ST_STBPRP_B_TREEPojo stStbprpBTreePojo){
        StopWatch watch = new StopWatch();
        watch.start();
        Integer integer = data.insertOne(stStbprpBTreePojo);
        watch.stop();
        if(integer > 0){
            return new ResultUtils<>(integer, "操作成功",true, integer,watch.getTime());
        }else {
            return new ResultUtils<>(integer, "操作成功",false, integer,watch.getTime());
        }
    }
    @RequestMapping("/modify")
    public ResultUtils upDateOne(@RequestBody ST_STBPRP_B_TREEPojo stStbprpBTreePojo){
        StopWatch watch = new StopWatch();
        watch.start();
        Integer integer = data.upDateOne(stStbprpBTreePojo);
        watch.stop();
        if(integer > 0){
            return new ResultUtils<>(integer, "操作成功",true, integer,watch.getTime());
        }else {
            return new ResultUtils<>(integer, "操作成功",false, integer,watch.getTime());
        }
    }
    @RequestMapping("/remove")
    public ResultUtils deleteOne(@RequestBody ColumnName param){
        StopWatch watch = new StopWatch();
        watch.start();
        String id = "";
        Integer integer = 0;
        if(null != param.getStcd() && "" != param.getId()){
            id = param.getStcd();
            integer = data.deleteOne(id);
        }

        watch.stop();
        if(integer > 0){
            return new ResultUtils<>(integer, "操作成功",true, integer,watch.getTime());
        }else {
            return new ResultUtils<>(integer, "操作成功",false, integer,watch.getTime());
        }
    }
}
