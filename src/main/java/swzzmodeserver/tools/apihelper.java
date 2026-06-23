package swzzmodeserver.tools;


import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import java.util.HashMap;


/**
 * 这里键入类的描述
 *
 * @author 高起
 * @版权： 版权所有 (c) 2018
 * @see：
 * @创建日期： 2018/12/10 14:49
 * @功能说明：
 */
public class apihelper {
    /**
     * 发送post 请求访问本地应用并根据传递参数的不同返回不同的结果
     * @param url 访问地址
     * @param parmasMap 传递的参数
     */
    public static String apipost(String url, String parmasMap, HashMap<String, Object> header){
        String data = null;
        //创建默认的httpClient实例
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        if (header != null){
            header.forEach((key,value)->{
                httpPost.addHeader(key,value.toString());
            });
        }
        try{
            ColumnName param = new ColumnName();
            // 设置请求体（参数）
            StringEntity entity = new StringEntity(parmasMap, "UTF-8");
            httpPost.setEntity(entity);
            CloseableHttpResponse response = httpClient.execute(httpPost);
            try{
                HttpEntity entityResult = response.getEntity();
                if (entityResult != null){
                    data = EntityUtils.toString(entityResult);
                    return data;
                }
                return data;
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                response.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                httpClient.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return data;
    }
    public static String apiget(String url,HashMap<String, Object> header){
        String data = null;
        //创建默认的httpClient实例
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        if (header != null){
            header.forEach((key,value)->{
                httpGet.addHeader(key,value.toString());
            });
        }
        try{
            CloseableHttpResponse response = httpClient.execute(httpGet);
            try{
                HttpEntity entityResult = response.getEntity();
                if (entityResult != null){
                    data = EntityUtils.toString(entityResult);
                    return data;
                }
                return data;
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                response.close();
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("请求"+url+"接口报错："+e.getMessage());
        }finally {
            try{
                httpClient.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return data;
    }


    public static String apigethttps(String url, HashMap<String, Object> header) throws Exception {
        String data = null;
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[]{new X509TrustManager(){
            public void checkClientTrusted(X509Certificate[] chain, String authType){}
            public void checkServerTrusted(X509Certificate[] chain, String authType){}
            public X509Certificate[] getAcceptedIssuers(){ return null; }
        }}, null);

        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLContext(sslContext)
                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                .build();
        // 剩余逻辑与原方法一致...
        HttpGet httpGet = new HttpGet(url);
        if (header != null){
            header.forEach((key,value)->{
                httpGet.addHeader(key,value.toString());
            });
        }
        try{
            CloseableHttpResponse response = httpClient.execute(httpGet);
            try{
                HttpEntity entityResult = response.getEntity();
                if (entityResult != null){
                    data = EntityUtils.toString(entityResult);
                    return data;
                }
                return data;
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                response.close();
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("请求"+url+"接口报错："+e.getMessage());
        }finally {
            try{
                httpClient.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return data;
    }
}
