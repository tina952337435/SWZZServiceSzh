package swzzmodeserver.tools;

import java.lang.reflect.Field;
import java.util.List;
import java.util.regex.Pattern;

public class FieldIsValid {

    public static <T> T getColumnName(T object,Class<T> objClass) {
        Field[] fields = objClass.getDeclaredFields();
        for(Field field : fields){
            try {
                field.setAccessible(true);
                Object fieldValue = field.get(object);
                if(!CommonUtills.isEmpty(fieldValue)){
                    boolean value = isValid(fieldValue.toString());
                    if (!value) {
                        return null;
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return object;
    }

    public static <T> List<T> getListColumnName(List<T> objectList, Class<T> objClass){
        for(T t : objectList){
            T tobj = getColumnName(t,objClass);
            if(CommonUtills.isEmpty(tobj)){
                return null;
            }
        }
        return objectList;
    }

    /**正则表达式**/
    private static String reg = "(?:')|(?:--)|(/\\*(?:.|[\\n\\r])*?\\*/)|"+ "(\\b(select|update|union|and|or|delete|insert|trancate|char|into|substr|ascii|declare|exec|count|master|drop|execute|ResultSet|where|on|create|connection|statement|jdbcTemplate|all|queryForInt|queryForObject|queryForMap|getConnection|outfile|load_file|join|left|right|between|rownum|dual)\\b)";
    private static Pattern sqlPattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
    public static boolean isValid(String str) {
        if (sqlPattern.matcher(str).find()){
//            System.out.println("未能通过过滤器：str=" + str);
            return false;
        }
        return true;
    }

    public static Object getColumnName(ColumnName param, Class<ParamField> class1) {
        if (param == null) {
            return null;
        }

        // 2. 使用反射获取 param 类的所有声明字段
        Field[] fields = param.getClass().getDeclaredFields();
        
        for (Field field : fields) {
            // 设置私有字段也可以访问
            field.setAccessible(true);
            try {
                // 获取当前字段的值
                Object value = field.get(param);
                
                // 3. 验证值：只校验 String 类型的字段，其他类型（如 Integer, Date）通常不需要校验 SQL 注入
                if (value != null && value instanceof String) {
                    String strValue = ((String) value).toLowerCase();
                    if(!isValid(strValue)){
                        return null; 
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        
        // 如果代码能走到这里，说明所有字段值都是安全的
        // 这里可以根据你的业务需求返回相应的结果
        System.out.println("参数校验通过，无 SQL 注入风险。");

        Object testObj = new java.util.HashMap<String, Object>() {{
            put("name", "参数校验通过");
        }};
        return testObj; 
    }
}
