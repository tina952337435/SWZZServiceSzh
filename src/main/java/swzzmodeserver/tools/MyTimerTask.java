package swzzmodeserver.tools;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import swzzmodeserver.workserver.data.swzzqxsj.Tba_weacontentData;
import swzzmodeserver.workserver.pojo.swzzqxsj.Tba_weacontentPojo;


import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

//定时器任务类
@Component
public class MyTimerTask {
    @Autowired
    private Tba_weacontentData data;

    public static String getHtmlResourceByUrl(String url,String encoding){
        StringBuffer buffer = new StringBuffer();
        URL urlObj = null;
        URLConnection urlConnection = null;
        InputStreamReader in = null;
        BufferedReader reader = null;

        try {
            urlObj = new URL(url);
            urlConnection = urlObj.openConnection();
            in = new InputStreamReader(urlConnection.getInputStream(),encoding);
            reader = new BufferedReader(in);
            String line = null;
            while ((line = reader.readLine()) != null){
                buffer.append(line).append("\r\n");
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }finally {
            if (in != null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return buffer.toString();
    }

    public static void downImage(String filePath,String imageUrl){
        String imageName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
        if (imageName.contains("?")){
            imageName = imageName.substring(0,imageName.indexOf("?"));
        }
        if (imageName.equals("nodata.jpg")){
            return;
        }

        File file = new File(filePath);
        if(!file.exists()){
            file.mkdirs();
        }
        try {
            URL urlObj = new URL(imageUrl);
            URLConnection urlConnection = urlObj.openConnection();
            InputStream in = urlConnection.getInputStream();
            FileOutputStream out = new FileOutputStream(new File(filePath + "//" + imageName));
            int i = 0;
            while ((i = in.read()) != -1){
                out.write(i);
            }
            in.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getImageName(String imageUrl){
        String imageName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
        if (imageName.contains("?")){
            imageName = imageName.substring(0,imageName.indexOf("?"));
        }
        if (imageName.equals("nodata.jpg")){
            return null;
        }
        return imageName;
    }

    public void YBJYTask(List<String> urlList,String imgType){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String stime = dateFormat.format(new Date()) + " 00:00:00";
        String etime = dateFormat.format(new Date()) + " 23:59:59";
        List<Tba_weacontentPojo> tbaList = new ArrayList<>();
        System.out.println("=========开始下载" + imgType + "图片===========");
        for(String url : urlList){
            Tba_weacontentPojo tba = new Tba_weacontentPojo();
            String html = MyTimerTask.getHtmlResourceByUrl(url, "UTF-8");
            Document document = Jsoup.parse(html);
            Element element = document.getElementById("imgpath");
            String imgUrl = element.attr("src");
            String hourStr = element.attr("data-fffmm");
            String date = imgUrl.substring("http://image.nmc.cn/product/".length(),imgUrl.indexOf("/STFC/")).replace("/","-");
            String time = date + " 08:00:00";
            System.out.println(imgType + " : " + time + " : "+ imgUrl);
            String imageName = getImageName(imgUrl);
            List<Tba_weacontentPojo> pojos = data.selectList(null, imageName, stime, etime, null, null, null);
            if (null == imageName || pojos.size() > 0) continue;
            MyTimerTask.downImage("D://work//UploadDoc//qx", imgUrl);
            tba.setTBA_WEAID(UUID.randomUUID().toString().replaceAll(" ","-"));
            tba.setTBA_FILENAME(imageName);
            tba.setTBA_INFOTYPE(imgType);
            tba.setTBA_FILETYPE(imageName.substring(imageName.indexOf(".")));
            tba.setTBA_DESDATE(time);
            tba.setTBA_GETDATE(time);
            tba.setTBA_NOTE(String.valueOf(Integer.parseInt(hourStr)));
            tbaList.add(tba);
        }
        System.out.println("=========下载" + imgType + "图片结束===========");
        if (tbaList.size() > 0){
            data.insertALL(tbaList);
        }
    }

    public void WrapTask(String url,String imgType,String suffix){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String stime = dateFormat.format(new Date()) + " 00:00:00";
        String etime = dateFormat.format(new Date()) + " 23:59:59";
        List<Tba_weacontentPojo> tbaList = new ArrayList<>();
        String html = MyTimerTask.getHtmlResourceByUrl(url, "UTF-8");
        Document document = Jsoup.parse(html);
        Element element = document.getElementById("timeWrap");
        List<Node> nodeList = element.childNodes();
        System.out.println("=========开始下载" + imgType + "图片===========");
        for(Node node : nodeList){
            Tba_weacontentPojo tba = new Tba_weacontentPojo();
            String imgUrl = node.attr("data-img");
            String dataTime = node.attr("data-time");
            String date = imgUrl.substring("http://image.nmc.cn/product/".length(),imgUrl.indexOf(suffix)).replace("/","-");
            String time = dataTime.split(" ")[1];
            String tm = date + " " + time + ":00";
            System.out.println(imgType + " : " + tm + " : "+ imgUrl);
            String imageName = getImageName(imgUrl);
            List<Tba_weacontentPojo> pojos = data.selectList(null, imageName, stime, etime, null, null, null);
            if (null == imageName || pojos.size() > 0) continue;
            MyTimerTask.downImage("D://work//UploadDoc//qx",imgUrl);
            tba.setTBA_WEAID(UUID.randomUUID().toString().replaceAll(" ","-"));
            tba.setTBA_FILENAME(imageName);
            tba.setTBA_INFOTYPE(imgType);
            tba.setTBA_FILETYPE(imageName.substring(imageName.indexOf(".")));
            tba.setTBA_DESDATE(tm);
            tba.setTBA_GETDATE(tm);
            //tba.setTBA_NOTE(String.valueOf(Integer.parseInt(hourStr)));
            tbaList.add(tba);
        }
        System.out.println("=========下载" + imgType + "图片结束===========");
        if (tbaList.size() >0){
            data.insertALL(tbaList);
        }
    }
//    @Override
//    public void run() {
//        String url = "http://www.nmc.cn/publish/radar/huadong.html";
//        String html = getHtmlResourceByUrl(url, "UTF-8");
//        Document document = Jsoup.parse(html);
//        Element element = document.getElementById("timeWrap");
//        List<Node> nodeList = element.childNodes();
//        System.out.println("=========开始下载图片===========");
//        for(Node node : nodeList){
//            String imgUrl = node.attr("data-img");
//            System.out.println(imgUrl);
//            downImage("D://work//UploadDoc//qx",imgUrl);
//        }
//        System.out.println("=========下载图片结束===========");
//    }

    //public static void main(String[] args) {
        //Timer timer = new Timer();
        //Date startTime = new Date();
        //timer.scheduleAtFixedRate(new MyTimerTask(),startTime,5 * 60 * 1000L);
        //ScheduledExecutorService service = Executors.newScheduledThreadPool(2);
        //service.scheduleAtFixedRate(new MyTimerTask(),5000,5 * 60 * 1000L, TimeUnit.MILLISECONDS);
        //service.shutdown();
    //}
}
