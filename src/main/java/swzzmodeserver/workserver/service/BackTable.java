package swzzmodeserver.workserver.service;

import java.util.ArrayList;
import java.util.List;

public class BackTable {
    
    public static <T> Integer backTable(T data,String type,List<Object> objList){
        int count = 80;
        int number = objList.size() / count;
        if(objList.size() % count != 0){
            number = number + 1;
        }
        List<Object> list = new ArrayList<>();
        Integer num = 0;
        for(int i = 0;i < number;i++){
            if(i == number - 1){
                list = objList.subList(count * i,objList.size());
            }else {
                list = objList.subList(count * i,count * ( i + 1));
            }
        }
        return num;
    }

}
