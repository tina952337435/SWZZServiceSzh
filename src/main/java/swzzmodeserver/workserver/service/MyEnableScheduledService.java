package swzzmodeserver.workserver.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import swzzmodeserver.tools.MyTimerTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class MyEnableScheduledService {
    @Autowired
    private MyTimerTask task;

//    @Async
//    @Scheduled(fixedDelay = 1000 * 60 * 5)
//    public void run1(){
//        String url = "http://www.nmc.cn/publish/radar/huadong.html";
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        System.out.println(dateFormat.format(new Date()));
//        task.WrapTask(url,"华东雷达拼图","/RDCP/");
//        System.out.println(dateFormat.format(new Date()));
//    }

//    @Async
//    @Scheduled(fixedDelay = 1000 * 60 * 5)
//    public void run2(){
//        List<String> urlList1 = new ArrayList<>();
//        urlList1.add("http://www.nmc.cn/publish/precipitation/1-day.html");
//        urlList1.add("http://www.nmc.cn/publish/precipitation/2-day.html");
//        urlList1.add("http://www.nmc.cn/publish/precipitation/3-day.html");
//        urlList1.add("http://www.nmc.cn/publish/precipitation/day4.html");
//        urlList1.add("http://www.nmc.cn/publish/precipitation/day5.html");
//        urlList1.add("http://www.nmc.cn/publish/precipitation/day6.html");
//        urlList1.add("http://www.nmc.cn/publish/precipitation/day7.html");
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        System.out.println(dateFormat.format(new Date()));
//        task.YBJYTask(urlList1,"全国雨量预报");
//        System.out.println(dateFormat.format(new Date()));
//    }

//    @Async
//    @Scheduled(fixedDelay = 1000 * 60 * 5)
//    public void run3(){
//        List<String> urlList2 = new ArrayList<>();
//        urlList2.add("http://www.nmc.cn/publish/precipitation/6hours-6.html");
//        urlList2.add("http://www.nmc.cn/publish/precipitation/6hours-12.html");
//        urlList2.add("http://www.nmc.cn/publish/precipitation/6hours-18.html");
//        urlList2.add("http://www.nmc.cn/publish/precipitation/6hours-24.html");
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        System.out.println(dateFormat.format(new Date()));
//        task.YBJYTask(urlList2,"6小时雨量预报");
//        System.out.println(dateFormat.format(new Date()));
//    }
}
