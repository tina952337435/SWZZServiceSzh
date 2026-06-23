package swzzmodeserver.tools;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.jsonwebtoken.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JwtUtil {
    //设置过期时间 3小时
    private static final long EXPIRE_TIME = 3 * 60 * 60 * 1000;
    //token秘钥
    private static final String TOKEN_SECRET = "768ADC6A9E72BFEE4891F1F98650FEEE";

    public static String createToken(String userName, String userID){
        JwtBuilder jwtBuilder = Jwts.builder();//构建JWT对象
        return jwtBuilder
                // Header
                .setHeaderParam("typ","JWT")
                .setHeaderParam("alg","HS256")
                // payload
                .claim("userName",userName)
                .claim("userId", userID)
                // 设置有效期（毫秒单位）
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_TIME))
                .setId(UUID.randomUUID().toString())
                // signature
                .signWith(SignatureAlgorithm.HS256, TOKEN_SECRET)
                // compact拼接三部分header、payload、signature
                .compact();
    }

    public static String getTokenInfo(String token, String key){
        if(token == null || key == null)   return null;
        JwtParser parser = Jwts.parser();
        Jws<Claims> claimsJws = parser.setSigningKey(TOKEN_SECRET).parseClaimsJws(token);
        Claims payload = claimsJws.getBody();

        // 获取key对于的内容
        return payload.get(key).toString();
    }


    public static Boolean checkToken(String token){
        if(token == null){
            return false;
        }
        try {
            JwtParser jwtParser = Jwts.parser();
            jwtParser.setSigningKey(TOKEN_SECRET).parseClaimsJws(token);
            return true;
        }catch (Exception e){
            // log.error("token失效");
            return false;
        }
    }

    public static String sign(String username, String permission) {
        String token = "";

        try {
            //过期时间
            Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
            //秘钥及加密算法
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
            //设置头部信息
            Map<String, Object> header = new HashMap<>(2);
            header.put("typ", "JWT");
            header.put("alg", "HS256");
            //携带username,password信息，生成签名
            return JWT.create()
                    .withHeader(header)
                    .withClaim("loginName", username)
                    .withClaim("permission", permission)
                    .withExpiresAt(date)
                    .sign(algorithm);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
    public static boolean verify(String token){
        /**
         * @desc   验证token，通过返回true
         * @params [token]需要校验的串
         **/
        try {
//            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
//            JWTVerifier verifier = JWT.require(algorithm).build();
//            DecodedJWT jwt = verifier.verify(token);
            if(token.equals(TOKEN_SECRET)==true){
                return true;
            }else {
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    public static String parseJWT(String token){
        /**
         * @desc   解密token，返回一个map
         * @params [token]需要校验的串
         **/
        DecodedJWT decodeToken = JWT.decode(token);
        return decodeToken.getClaim("loginName").asString();
    }
    public static boolean isJwtExpired(String token){
        /**
         * @desc 判断token是否过期
         * @author lj
         */
        try {
            DecodedJWT decodeToken = JWT.decode(token);
            return decodeToken.getExpiresAt().before(new Date());
        } catch(Exception e){
            return true;
        }
    }
}


