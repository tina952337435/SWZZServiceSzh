package swzzmodeserver.tools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class JsonToMapUtils {
    public static Map<String,Object> convertJsonToMap(String json) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(json);
        Map<String,Object> map = new HashMap<>();
        Iterator<String> fieldNames = rootNode.fieldNames();
        while(fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            JsonNode node = rootNode.get(fieldName);
            if(node.isValueNode()) {
                map.put(fieldName, node.asText());
            } else if (node.isObject()) {
                map.put(fieldName, convertJsonToMap(node.toString()));
            } else if (node.isArray()) {
                // TODO: handle array elements
            }
        }
        return map;
    }

    /// <summary>
    /// 通过Dictionary key值获取对应value
    /// </summary>
    public static String GetValueByKey(Map<String, Object> map, String key)
    {
        String result = "";
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getKey().equals(key))
            {
                result = entry.getValue().toString();
                break;
            }
        }
        return result;
    }
}
