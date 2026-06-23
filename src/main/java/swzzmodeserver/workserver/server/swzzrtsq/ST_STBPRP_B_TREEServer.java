package swzzmodeserver.workserver.server.swzzrtsq;

import swzzmodeserver.workserver.data.swzzrtsq.ST_STBPRP_B_TREEData;
import swzzmodeserver.workserver.pojo.swzzrtsq.ST_STBPRP_B_TREEPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ST_STBPRP_B_TREEServer {
    @Autowired
    private ST_STBPRP_B_TREEData data;

    public List<ST_STBPRP_B_TREEPojo> selectMenu(String pathname){
        ST_STBPRP_B_TREEPojo param = new ST_STBPRP_B_TREEPojo();
        param.setPID("-1");
        param.setPATHNAME(pathname);
        List<ST_STBPRP_B_TREEPojo> treeList = data.selectList(param);

        param.setPID("");
        List<ST_STBPRP_B_TREEPojo> treeAllList = data.selectList(param);
        for(ST_STBPRP_B_TREEPojo treePojo : treeList){
            List<ST_STBPRP_B_TREEPojo> collect = treeAllList.stream().filter(i -> i.getPID().equals(treePojo.getID())).collect(Collectors.toList());
            treePojo.setTreePojoList(collect);
        }
        return treeList;

    }

    //递归返回树形菜单
    public List<ST_STBPRP_B_TREEPojo> selectMenu(String pathname,String pid){
        List<ST_STBPRP_B_TREEPojo> childList = new ArrayList<>();

        ST_STBPRP_B_TREEPojo param = new ST_STBPRP_B_TREEPojo();
        param.setPID(pid);
        param.setPATHNAME(pathname);
        List<ST_STBPRP_B_TREEPojo> treeList = data.selectList(param);

        param.setPID("");
        List<ST_STBPRP_B_TREEPojo> treeAllList = data.selectList(param);
        for(ST_STBPRP_B_TREEPojo treePojo : treeList){
            childList.add(findChildMenu(treePojo,treeAllList));
        }
        return childList;
    }


    public ST_STBPRP_B_TREEPojo findChildMenu(ST_STBPRP_B_TREEPojo model,List<ST_STBPRP_B_TREEPojo> mTreeList){
        List<ST_STBPRP_B_TREEPojo> childList = new ArrayList<>();

        ST_STBPRP_B_TREEPojo treeVo = model;
        for(ST_STBPRP_B_TREEPojo node : mTreeList) {
            if (model.getID().equals(node.getPID())) {
                childList.add(findChildMenu(node,mTreeList));
            }
        }
        treeVo.setTreePojoList(childList);
//        List<ST_STBPRP_B_TREEPojo> childrenMenu = mTreeList.stream().filter(i -> i.getPID().equals(pid)).sorted(Comparator.comparing(ST_STBPRP_B_TREEPojo::getORDERBYID,Comparator.naturalOrder())).collect(Collectors.toList());

        return treeVo;
    }

}
