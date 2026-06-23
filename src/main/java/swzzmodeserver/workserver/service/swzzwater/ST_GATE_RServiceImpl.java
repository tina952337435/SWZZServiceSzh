package swzzmodeserver.workserver.service.swzzwater;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swzzmodeserver.workserver.data.swzzflood.RTSQData;
import swzzmodeserver.workserver.data.swzzflood.St_stbprp_b_quData;
import swzzmodeserver.workserver.data.swzzflood.St_stbprp_b_qu_treeData;
import swzzmodeserver.workserver.data.swzzmode.ST_RVFCCH_BData;
import swzzmodeserver.workserver.data.swzzwater.ST_GATE_RData;
import swzzmodeserver.workserver.data.swzzwater.ST_GATE_RNEWData;
import swzzmodeserver.workserver.data.swzzwater.Water_ST_STBPRP_BData;
import swzzmodeserver.workserver.pojo.swzzflood.ST_WAS_RPojo;
import swzzmodeserver.workserver.pojo.swzzflood.St_stbprp_b_quPojo;
import swzzmodeserver.workserver.pojo.swzzflood.St_stbprp_b_qu_treePojo;
import swzzmodeserver.workserver.pojo.swzzmode.ST_RVFCCH_BPojo;
import swzzmodeserver.workserver.pojo.swzzwater.ST_GATE_RNEWPojo;
import swzzmodeserver.workserver.pojo.swzzwater.ST_GATE_RPojo;
import swzzmodeserver.workserver.pojo.swzzwater.Water_ST_STBPRP_BPojo;
import swzzmodeserver.workserver.data.swzzwater.Water_ST_RVFCCH_BData;
import swzzmodeserver.workserver.pojo.swzzwater.Water_ST_RVFCCH_BPojo;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ST_GATE_RServiceImpl implements ST_GATE_RService{
    @Autowired
    private St_stbprp_b_qu_treeData treeData;
    @Autowired
    private St_stbprp_b_quData quData;
    @Autowired
    private Water_ST_STBPRP_BData stbprpBData;
    @Autowired
    private ST_RVFCCH_BData stRvfcchBData;
    @Autowired
    private Water_ST_RVFCCH_BData waterstRvfcchBData;
    @Autowired
    private RTSQData rtsqData;
    @Autowired
    private ST_GATE_RData gateRData;
    @Autowired
    private ST_GATE_RNEWData gateRnewData;

    private final List<St_stbprp_b_qu_treePojo> children_Menues = new ArrayList<>();

    @Override
    public List<Map<String,Object>> WATER_ST_GATE_RNEWLIST(List<String> treeId, String stime, String etime,List<String> stcds) {
        List<Map<String,Object>> mapList = new ArrayList<>();
        List<St_stbprp_b_qu_treePojo> TreeList = treeData.selectList(null, null, null, null, null, null, null);
        List<St_stbprp_b_qu_treePojo> QU_TREE = GetChildren_Menue(treeId, TreeList);
        List<String> aggstcd = QU_TREE.stream().map(St_stbprp_b_qu_treePojo::getID).collect(Collectors.toList());
        aggstcd = insert(aggstcd, treeId);
        List<String> finalAggstcd = aggstcd;
        List<St_stbprp_b_quPojo> quSTCD = quData.selectList(null, null, null, null, null, null, null)
                .stream().filter(m-> finalAggstcd.contains(m.getPID())).sorted(Comparator.comparing(St_stbprp_b_quPojo::getORDERBYID))
                .collect(Collectors.toList());
        List<String> stcdList = quSTCD.stream().map(St_stbprp_b_quPojo::getSTCD).collect(Collectors.toList());
        //List<String> idList = quSTCD.stream().map(St_stbprp_b_quPojo::getID).collect(Collectors.toList());
        if (stcds.size() > 0){
            stcdList = stcds;
        }
        List<Water_ST_STBPRP_BPojo> st_stbprp_b = stbprpBData.selectList("DD");
        List<Water_ST_RVFCCH_BPojo> st_rvfcch_b = waterstRvfcchBData.selectList(null, null, null, null, null);
        if (stcdList.size() > 0){
            List<String> finalStcdList = stcdList;
            st_stbprp_b = st_stbprp_b.stream().filter(m -> finalStcdList.contains(m.getSTCD())).collect(Collectors.toList());
            st_rvfcch_b = st_rvfcch_b.stream().filter(m -> finalStcdList.contains(m.getSTCD())).collect(Collectors.toList());
        }else {
            stcdList = st_stbprp_b.stream().map(Water_ST_STBPRP_BPojo::getSTCD).collect(Collectors.toList());
        }
        List<ST_WAS_RPojo> st_was_r = rtsqData.getSCSW(stime, etime, stcdList, null).stream().sorted((a, b) -> {
            return b.getTM().compareTo(a.getTM());
        }).collect(Collectors.toList());
        List<ST_GATE_RPojo> tempST_GATE_R = new ArrayList<>();
        List<ST_GATE_RNEWPojo> newList = new ArrayList<>();
        //List<Water_ST_STBPRP_B_STCDPojo> st_stbprp_b_stcd = new ArrayList<>();
        if (!"".equals(stime) || !"".equals(etime)){
            tempST_GATE_R = gateRData.selectList(stime, etime, stcdList);
        }else {
            newList = gateRnewData.selectList(null, null, null);
            //st_stbprp_b_stcd = stbprpBStcdData.selectList(Arrays.asList("3,1".split(",")));
        }
        if (stcdList.size() > 0){
            for (String stcd : stcdList){
                Map<String,Object> map = new HashMap<>();
                map.put("STCD",stcd);
                map.put("STNM",null);
                map.put("LTTD",null);
                map.put("LGTD",null);
                map.put("WRZ",null);
                map.put("GRZ",null);
                map.put("OMCNUM",null);
                map.put("GTOPNUM",null);
                map.put("SFQ",null);
                map.put("ST_GATE_R",null);
                map.put("UPZ",null);
                List<Water_ST_STBPRP_BPojo> temp = st_stbprp_b.stream().filter(m -> m.getSTCD().equals(stcd)).collect(Collectors.toList());
                if (temp.size() > 0){
                     map.put("STNM",temp.get(0).getSTNM());
                     map.put("LTTD",temp.get(0).getLTTD());
                     map.put("LGTD",temp.get(0).getLGTD());
                }
                List<Water_ST_RVFCCH_BPojo> rvfcch = st_rvfcch_b.stream().filter(m -> m.getSTCD().equals(stcd)).collect(Collectors.toList());
                if (rvfcch.size() > 0){
                    map.put("WRZ",rvfcch.get(0).getWRZ());
                    map.put("GRZ",rvfcch.get(0).getGRZ());
                    map.put("OMCNUM",rvfcch.get(0).getFLPQ() != null ? rvfcch.get(0).getFLPQ() : 0.0);
                    map.put("GTOPNUM",rvfcch.get(0).getGRQ() != null ? rvfcch.get(0).getGRQ() : 0.0);
                    map.put("SFQ",rvfcch.get(0).getSFQ() != null ? rvfcch.get(0).getSFQ() : 0.0);
                }
                if (newList.size() > 0){
                    List<ST_GATE_RNEWPojo> ST_GATE_R = newList.stream().filter(m -> m.getSTCD().equals(stcd)).collect(Collectors.toList());
                    map.put("ST_GATE_R",ST_GATE_R);
                }else {
                    List<ST_GATE_RNEWPojo> tempST_GATE_RNew = new ArrayList<>();
                    if (tempST_GATE_R.size() > 0){
                        List<ST_GATE_RPojo> tt = tempST_GATE_R.stream().filter(m -> m.getSTCD().equals(stcd)).sorted((a, b) -> {
                            return b.getTM().compareTo(a.getTM());
                        }).collect(Collectors.toList());
                        if (tt.size() > 0){
                            map.put("TM",tt.get(0).getTM());
                        }
                        tempST_GATE_RNew = tt.stream().map(m -> {
                            ST_GATE_RNEWPojo rnewPojo = new ST_GATE_RNEWPojo();
                            BeanUtils.copyProperties(m,rnewPojo);
                            return rnewPojo;
                        }).collect(Collectors.toList());
                    }
                    map.put("ST_GATE_R",tempST_GATE_RNew);
                }
                List<ST_WAS_RPojo> tempSTCD = st_was_r.stream().filter(m -> m.getSTCD().equals(stcd)).sorted((a, b) -> {
                    return b.getTM().compareTo(a.getTM());
                }).collect(Collectors.toList());
                if (tempSTCD.size() > 0){
                    map.put("UPZ",tempSTCD.get(0).getUPZ());
                }
                mapList.add(map);
            }
        }
        return mapList;
    }

    @Override
    public Map<String,Object> selectList(String stime, String etime,String EXKEY,String EQPTP, List<String> stcdList,Integer pageIndex,Integer pagesize) {
        Map<String,Object> map = new HashMap<>();
        if (null != pageIndex && null != pagesize){
            PageHelper.startPage(pageIndex,pagesize);
            List<ST_GATE_RPojo> pojoList = gateRData.selectListTwo(stime, etime, stcdList,EXKEY,EQPTP);
            PageInfo<ST_GATE_RPojo> pageInfo = new PageInfo<>(pojoList);
            map.put("list",pageInfo.getList());
            map.put("pageInfo",pageInfo);
            return map;
        }
        List<ST_GATE_RPojo> gateRPojos = gateRData.selectListTwo(stime, etime, stcdList,EXKEY,EQPTP);
        map.put("list",gateRPojos);
        map.put("pageInfo",null);
        return map;
    }

    public List<St_stbprp_b_qu_treePojo> GetChildren_Menue(List<String> treeId,List<St_stbprp_b_qu_treePojo> treeList){
        List<St_stbprp_b_qu_treePojo> children_Menue = treeList.stream().filter(m -> treeId.contains(m.getPID()))
                .sorted(Comparator.comparing(St_stbprp_b_qu_treePojo::getORDERBYID)).collect(Collectors.toList());
        if (children_Menue.size() > 0){
            children_Menues.addAll(children_Menue);
            List<String> ids = children_Menue.stream().map(St_stbprp_b_qu_treePojo::getID).collect(Collectors.toList());
            GetChildren_Menue(ids,treeList);
        }
        return children_Menues;
    }

    public List<String> insert(List<String> arr,List<String> str){
        List<String> stcdList = new ArrayList<>(arr);
        if (str.size() > 0){
            stcdList.addAll(str);
        }
        return stcdList;
    }


}
