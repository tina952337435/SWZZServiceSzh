package swzzmodeserver.workserver.report.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * NMC（国家气象中心）台风数据爬取服务
 *
 * 数据来源: http://typhoon.nmc.cn/weatherservice/
 * - 台风列表: /typhoon/jsons/list_{year}
 * - 台风详情: /typhoon/jsons/view_{id}
 *
 * 注意: 风速单位是 m/s（不是 knots），API 字段名虽有歧义但实为 m/s
 */
@Service
public class NmcTyphoonService {

    @Value("${http.urlPath.nmcUrl:http://typhoon.nmc.cn}")
    private String nmcUrl;

    @Value("${http.urlPath.nmcWebUrl:http://www.nmc.cn}")
    private String nmcWebUrl;

    @Value("${http.urlPath.imageWebUrl:#{null}}")
    private String imageWebUrl;

    private static final ObjectMapper mapper = new ObjectMapper();

    /** 去掉末尾斜杠，保证拼接不出现双斜杠 */
    private String baseUrl() {
        String url = nmcUrl != null ? nmcUrl : "http://typhoon.nmc.cn";
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    // ===================== API 调用 =====================

    public List<TyphoonSummary> getTyphoonList(int year) throws Exception {
        String url = baseUrl() + "/weatherservice/typhoon/jsons/list_" + year;
        String jsonp = httpGet(url);
        JsonNode root = parseJsonp(jsonp);

        List<TyphoonSummary> list = new ArrayList<>();
        JsonNode typhoonList = root.get("typhoonList");
        if (typhoonList != null && typhoonList.isArray()) {
            for (JsonNode node : typhoonList) {
                TyphoonSummary ts = new TyphoonSummary();
                ts.setId(node.get(0).asInt());
                ts.setEnName(node.get(1).asText());
                ts.setCnName(node.get(2).asText());
                ts.setCode1(node.get(3).asText());
                ts.setCode2(node.get(4).asText());
                ts.setCmaNum(node.get(5).isNull() ? null : node.get(5).asText());
                ts.setDescription(node.get(6).isNull() ? null : node.get(6).asText());
                ts.setStatus(node.get(7).asText());
                list.add(ts);
            }
        }
        return list;
    }

    /** 按ID从列表中查找 */
    public TyphoonSummary findTyphoonById(List<TyphoonSummary> list, String id) {
        if (id == null || list == null)
            return null;
        for (TyphoonSummary ts : list) {
            if (id.equals(String.valueOf(ts.getId())))
                return ts;
        }
        return null;
    }

    public TyphoonSummary findTyphoon(int year, String code, String name) throws Exception {
        List<TyphoonSummary> list = getTyphoonList(year);
        for (TyphoonSummary ts : list) {
            if (name != null && !name.isEmpty() && name.equals(ts.getCnName()))
                return ts;
            if (code != null && !code.isEmpty()
                    && (code.equals(ts.getCode1()) || code.equals(ts.getCode2())))
                return ts;
        }
        return null;
    }

    public TyphoonDetail getTyphoonDetail(int typhoonId) throws Exception {
        String url = baseUrl() + "/weatherservice/typhoon/jsons/view_" + typhoonId;
        String jsonp = httpGet(url);
        JsonNode root = parseJsonp(jsonp);
        JsonNode typhoon = root.get("typhoon");
        if (typhoon == null || !typhoon.isArray())
            return null;

        TyphoonDetail detail = new TyphoonDetail();
        detail.setId(typhoon.get(0).asInt());
        detail.setEnName(typhoon.get(1).asText());
        detail.setCnName(typhoon.get(2).asText());
        detail.setCode1(typhoon.get(3).asText());
        detail.setCode2(typhoon.get(4).asText());
        detail.setDescription(typhoon.get(6).isNull() ? null : typhoon.get(6).asText());
        detail.setStatus(typhoon.get(7).asText());

        List<TrackPoint> tracks = new ArrayList<>();
        if (typhoon.size() > 8) {
            JsonNode trackArray = typhoon.get(8);
            if (trackArray != null && trackArray.isArray()) {
                for (JsonNode tpNode : trackArray) {
                    TrackPoint tp = parseTrackPoint(tpNode);
                    if (tp != null)
                        tracks.add(tp);
                }
            }
        }
        detail.setTracks(tracks);
        return detail;
    }

    public TrackPoint getLatestObservation(TyphoonDetail detail) {
        if (detail == null || detail.getTracks() == null || detail.getTracks().isEmpty())
            return null;
        return detail.getTracks().get(detail.getTracks().size() - 1);
    }

    public List<ForecastPoint> getCmaForecast(TyphoonDetail detail) {
        if (detail == null || detail.getTracks() == null)
            return new ArrayList<>();
        TrackPoint latest = getLatestObservation(detail);
        if (latest != null && latest.getForecasts() != null) {
            Map<String, List<ForecastPoint>> forecasts = latest.getForecasts();
            for (String agency : new String[] { "BABJ", "BCC" }) {
                if (forecasts.containsKey(agency))
                    return forecasts.get(agency);
            }
            if (!forecasts.isEmpty())
                return forecasts.values().iterator().next();
        }
        return new ArrayList<>();
    }

    /**
     * 从 probability-img1.html 提取当前网页展示的台风编号
     * 与 NMC 台风网主页保持一致
     */
    public String fetchCurrentTyphoonCode() {
        try {
            String webBase = nmcWebUrl != null ? nmcWebUrl : "http://www.nmc.cn";
            String html = httpGet(webBase + "/publish/typhoon/probability-img1.html");
            java.util.regex.Matcher m = java.util.regex.Pattern.compile(
                    "0W(\\d{4})0000").matcher(html);
            if (m.find()) {
                return m.group(1); // e.g., "2613"
            }
        } catch (Exception e) {
            System.err.println("提取当前台风编号失败: " + e.getMessage());
        }
        return null;
    }

    /**
     * 从 probability-img{N}.html 页面爬取台风路径预报图 URL
     * 遍历 img1～img5，匹配对应台风编号的页面
     */
    public String fetchTrackImageUrl(String typhoonCode) {
        if (typhoonCode == null)
            return null;
        try {
            java.util.regex.Pattern imgPattern = java.util.regex.Pattern.compile(
                    "https?://[^\"']*?SEVP[^\"']*?0W" + typhoonCode + "[^\"']*?\\.(?:JPG|jpg|PNG|png)");
            // 遍历可能的分页
            for (int i = 1; i <= 5; i++) {
                try {
                    String webBase = nmcWebUrl != null ? nmcWebUrl : "http://www.nmc.cn";
                    String html = httpGet(webBase + "/publish/typhoon/probability-img" + i + ".html");
                    java.util.regex.Matcher m = imgPattern.matcher(html);
                    if (m.find()) {
                        return m.group();
                    }
                } catch (Exception e) {
                    // 某页不存在，继续下一页
                }
            }
        } catch (Exception e) {
            System.err.println("爬取台风图片URL失败: " + e.getMessage());
        }
        return null;
    }

    /**
     * 下载台风路径预报图到本地
     *
     * @param imageUrl    图片 URL
     * @param saveDir     保存目录
     * @param typhoonCode 台风编号
     * @return 本地文件路径，失败返回 null
     */
    public String downloadTrackImage(String imageUrl, String saveDir, String typhoonCode) {
        if (imageUrl == null || imageUrl.isEmpty())
            return null;
        // 图片代理：用配置的域名替换 image.nmc.cn
        if (imageWebUrl != null && !imageWebUrl.isEmpty()) {
            imageUrl = imageUrl.replaceFirst("https?://image\\.nmc\\.cn", imageWebUrl);
        }
        try {
            File dir = new File(saveDir);
            if (!dir.exists())
                dir.mkdirs();

            String ext = imageUrl.toLowerCase().contains(".png") ? ".png" : ".jpg";
            String fileName = "typhoon_" + typhoonCode + "_" + System.currentTimeMillis() + ext;
            String filePath = saveDir + File.separator + fileName;

            // HTTPS 证书绕过（跟项目 apihelper.apigethttps 一致）
            javax.net.ssl.SSLContext sslCtx = javax.net.ssl.SSLContext.getInstance("TLS");
            sslCtx.init(null, new javax.net.ssl.TrustManager[] { new javax.net.ssl.X509TrustManager() {
                public void checkClientTrusted(java.security.cert.X509Certificate[] c, String a) {
                }

                public void checkServerTrusted(java.security.cert.X509Certificate[] c, String a) {
                }

                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            } }, null);

            org.apache.http.impl.client.CloseableHttpClient client = org.apache.http.impl.client.HttpClients.custom()
                    .setSSLContext(sslCtx)
                    .setSSLHostnameVerifier(org.apache.http.conn.ssl.NoopHostnameVerifier.INSTANCE)
                    .build();
            org.apache.http.client.methods.HttpGet get = new org.apache.http.client.methods.HttpGet(imageUrl);

            org.apache.http.client.methods.CloseableHttpResponse resp = client.execute(get);
            java.io.InputStream is = resp.getEntity().getContent();
            java.io.FileOutputStream fos = new java.io.FileOutputStream(filePath);
            byte[] buf = new byte[8192];
            int n;
            while ((n = is.read(buf)) != -1)
                fos.write(buf, 0, n);
            is.close();
            fos.close();
            resp.close();
            client.close();

            return filePath;
        } catch (Exception e) {
            System.err.println("下载台风图片失败: " + e.getMessage());
        }
        return null;
    }

    // ===================== 文本生成（匹配模板格式） =====================

    /**
     * 生成台风动态描述:
     * "7月8日8时，2609号超强台风"巴威"，距离台湾基隆市东偏南方向约1580公里，
     * 中心位置为北纬16.9度、东经134.1度，中心最大风力17级以上（62米/秒），
     * 中心气压915hPa，七级风圈半径350～500公里。"
     */
    public String buildTyphoonStatusText(TyphoonDetail detail, TrackPoint obs,
            List<ForecastPoint> forecast) {
        if (obs == null)
            return "";
        System.out.println("buildTyphoonStatus: localTimeChinese=" + obs.getLocalTimeChinese() + " utcTime=" + obs.getUtcTime());

        StringBuilder sb = new StringBuilder();

        // 观测时间
        sb.append(formatObsTime(getObsTimeString(obs)));

        // 编号+等级+名称: "，2609号超强台风"巴威""
        sb.append("，").append(detail.getCode1()).append("号");
        sb.append(gradeToFullChinese(obs.getGrade())).append("台风");
        if (detail.getCnName() != null && !detail.getCnName().equals("nameless")) {
            sb.append("“").append(detail.getCnName()).append("”");
        }

        // 参考位置: "，距离台湾基隆市东偏南方向约1580公里"
        String desc = coalesce(detail.getDescription(), obs.getDescription());
        if (desc != null && !desc.isEmpty()) {
            sb.append("，").append(desc);
        }

        // 经纬度: "，中心位置为北纬16.9度、东经134.1度"
        sb.append("，中心位置为北纬").append(fmt(obs.getLat()))
                .append("度、东经").append(fmt(obs.getLon())).append("度");

        // 风力: "，中心最大风力17级以上（62米/秒）"
        sb.append("，中心最大风力").append(windToGrade(obs.getWindSpeed()))
                .append("（").append(obs.getWindSpeed()).append("米/秒）");

        // 气压: "，中心气压915hPa"
        sb.append("，中心气压").append(obs.getPressure()).append("hPa");

        // 七级风圈: "，七级风圈半径350～500公里"
        int[] r7 = getWindRadiiByKts(obs, 30);
        if (r7 != null) {
            int minR = min(r7[1], r7[2], r7[3], r7[4]);
            int maxR = max(r7[1], r7[2], r7[3], r7[4]);
            sb.append("，七级风圈半径").append(minR).append("～").append(maxR).append("公里");
        }
        sb.append("。");
        return sb.toString();
    }

    /**
     * 生成移动预测描述（单独使用，放在台风动态之后）:
     * "据气象部门预测，"巴威"将以每小时15-20公里的速度向偏西方向移动，强度变化不大。"
     */
    public String buildTyphoonMovementText(TyphoonDetail detail, TrackPoint obs,
            List<ForecastPoint> forecast) {
        if (obs == null)
            return "";

        StringBuilder sb = new StringBuilder();
        if (obs.getDirection() != null && obs.getSpeed() > 0) {
            sb.append("据气象部门预测，“").append(detail.getCnName()).append("”");
            sb.append("将以每小时").append(obs.getSpeed()).append("公里的速度");
            sb.append("向").append(dirToChinese(obs.getDirection())).append("方向移动");

            String trend = computeIntensityTrend(obs, forecast);
            if (!trend.isEmpty())
                sb.append("，").append(trend);
            sb.append("。");
        }
        return sb.toString();
    }

    /**
     * 生成预报分析段落（需预报员完善）:
     * ""巴威"强度强，尺度大。如"巴威"登陆浙江中南部到福建北部...如"巴威"登陆浙江北部..."
     * 注: 这部分内容来自气象专家分析，NMC 数据中不包含，需预报员在 OnlyOffice 中补充
     */
    private String buildForecastText(TyphoonDetail detail, TrackPoint obs,
            List<ForecastPoint> forecast) {
        StringBuilder sb = new StringBuilder();
        String name = detail.getCnName();

        // 根据预报路径数据生成基本描述
        if (!forecast.isEmpty()) {
            ForecastPoint last = forecast.get(forecast.size() - 1);
            sb.append("“").append(name).append("”").append("强度强，尺度大。");

            // 强度变化描述（从预报数据推断）
            int obsWind = obs.getWindSpeed();
            int lastWind = last.getWindSpeed();

            if (lastWind > obsWind + 3) {
                sb.append("预计未来将有所加强，");
            } else if (lastWind < obsWind - 3) {
                sb.append("预计未来强度将逐渐减弱，");
            } else {
                sb.append("预计未来强度变化不大，");
            }

            // 位置趋势
            double latDiff = last.getLat() - obs.getLat();
            double lonDiff = last.getLon() - obs.getLon();
            if (Math.abs(latDiff) > 2 || Math.abs(lonDiff) > 2) {
                String dirDesc = "";
                if (latDiff > 1 && lonDiff < -2)
                    dirDesc = "向西北方向移动";
                else if (latDiff > 1 && Math.abs(lonDiff) < 2)
                    dirDesc = "向北移动";
                else if (Math.abs(latDiff) < 1 && lonDiff < -2)
                    dirDesc = "向西移动";
                else if (latDiff > 1 && lonDiff < -1)
                    dirDesc = "向偏西方向移动";
                sb.append(dirDesc).append("，");
            }

            sb.append("可能对上海产生风雨影响。");
        }
        return sb.toString();
    }

    // ===================== 文本格式化 =====================

    /** 获取观测时间的北京时间字符串（localTimeChinese优先，UTC回退） */
    public String getObsTimeString(TrackPoint obs) {
        if (obs == null) return null;
        String t = obs.getLocalTimeChinese();
        if (t != null) return t;
        return utcToBeijing(obs.getUtcTime());
    }

    /** "202608071200" UTC → "2026年08月07日20时00分" 北京时间 */
    private String utcToBeijing(String utcTime) {
        try {
            SimpleDateFormat utcFmt = new SimpleDateFormat("yyyyMMddHHmm", Locale.CHINA);
            utcFmt.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
            Date d = utcFmt.parse(utcTime);
            SimpleDateFormat cnFmt = new SimpleDateFormat("yyyy年MM月dd日HH时mm分", Locale.CHINA);
            cnFmt.setTimeZone(java.util.TimeZone.getTimeZone("GMT+8"));
            return cnFmt.format(d);
        } catch (Exception e) { return null; }
    }

    /** "2026年07月08日08时00分" → "7月8日8时" */
    private String formatObsTime(String chineseTime) {
        if (chineseTime == null)
            return "";
        try {
            SimpleDateFormat in = new SimpleDateFormat("yyyy年MM月dd日HH时mm分", Locale.CHINA);
            Date date = in.parse(chineseTime);
            SimpleDateFormat out = new SimpleDateFormat("M月d日H时", Locale.CHINA);
            String result = out.format(date);
            return result.replace("月0", "月"); // M月0d日 → M月d日
        } catch (Exception e) {
            return chineseTime;
        }
    }

    /** 台风等级英文 → 中文完整名: STY→强, SuperTY→超强, TY→"", TS→"" */
    private String gradeToFullChinese(String grade) {
        if (grade == null)
            return "";
        switch (grade) {
            case "TD":
                return "热带低压";
            case "TS":
                return "";
            case "STS":
                return "强热带风暴级";
            case "TY":
                return "";
            case "STY":
                return "强";
            case "SuperTY":
                return "超强";
            default:
                return "";
        }
    }

    /** 风速 m/s → 风力等级文字: 62→"17级以上", 48→"15级" */
    private String windToGrade(int windMs) {
        if (windMs >= 61)
            return "17级以上";
        if (windMs >= 51)
            return "16级";
        if (windMs >= 46)
            return "15级";
        if (windMs >= 41)
            return "14级";
        if (windMs >= 37)
            return "13级";
        if (windMs >= 33)
            return "12级";
        if (windMs >= 29)
            return "11级";
        if (windMs >= 25)
            return "10级";
        if (windMs >= 21)
            return "9级";
        if (windMs >= 17)
            return "8级";
        if (windMs >= 14)
            return "7级";
        return windMs + "米/秒";
    }

    /** 风向缩写 → 中文: W→偏西, WNW→西北偏西, NW→西北, NNW→偏北 */
    private String dirToChinese(String dir) {
        if (dir == null)
            return "";
        switch (dir.toUpperCase()) {
            case "N":
                return "北";
            case "NNE":
                return "东北偏北";
            case "NE":
                return "东北";
            case "ENE":
                return "东北偏东";
            case "E":
                return "东";
            case "ESE":
                return "东南偏东";
            case "SE":
                return "东南";
            case "SSE":
                return "东南偏南";
            case "S":
                return "南";
            case "SSW":
                return "西南偏南";
            case "SW":
                return "西南";
            case "WSW":
                return "西南偏西";
            case "W":
                return "偏西";
            case "WNW":
                return "西北偏西";
            case "NW":
                return "西北";
            case "NNW":
                return "西北偏北";
            default:
                return dir;
        }
    }

    /** 根据最新观测和预报，推断强度变化趋势 */
    private String computeIntensityTrend(TrackPoint obs, List<ForecastPoint> forecast) {
        if (forecast == null || forecast.isEmpty())
            return "";
        ForecastPoint last = forecast.get(forecast.size() - 1);
        int windDiff = last.getWindSpeed() - obs.getWindSpeed();
        int pressDiff = last.getPressure() - obs.getPressure();

        if (windDiff >= 5)
            return "强度明显加强";
        if (windDiff >= 2)
            return "强度有所加强";
        if (windDiff <= -5 || pressDiff >= 20)
            return "强度逐渐减弱";
        if (windDiff <= -2)
            return "强度略有下降";
        return "强度变化不大";
    }

    /** 从风圈列表中提取指定级别的风圈 [kts, NE, SE, SW, NW] */
    private int[] getWindRadiiByKts(TrackPoint obs, int kts) {
        if (obs.getWindRadii() == null)
            return null;
        for (int[] r : obs.getWindRadii()) {
            if (r[0] == kts)
                return r;
        }
        return null;
    }

    // ===================== HTTP & 解析 =====================

    private String httpGet(String urlStr) throws Exception {
        // 使用跟项目一致的 Apache HttpClient（自动走系统代理）
        org.apache.http.impl.client.CloseableHttpClient client = org.apache.http.impl.client.HttpClients
                .createDefault();
        org.apache.http.client.methods.HttpGet get = new org.apache.http.client.methods.HttpGet(urlStr);
        get.setHeader("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
        get.setHeader("Referer", baseUrl() + "/web.html");

        try {
            org.apache.http.client.methods.CloseableHttpResponse resp = client.execute(get);
            String result = org.apache.http.util.EntityUtils.toString(resp.getEntity(), "UTF-8");
            resp.close();
            return result;
        } finally {
            client.close();
        }
    }

    private JsonNode parseJsonp(String jsonp) throws Exception {
        int start = jsonp.indexOf('(');
        int end = jsonp.lastIndexOf(')');
        if (start >= 0 && end > start)
            return mapper.readTree(jsonp.substring(start + 1, end));
        return mapper.readTree(jsonp);
    }

    private TrackPoint parseTrackPoint(JsonNode node) {
        if (node == null || !node.isArray() || node.size() < 10)
            return null;

        TrackPoint tp = new TrackPoint();
        tp.setId(node.get(0).asInt());
        tp.setUtcTime(node.get(1).asText());
        tp.setTimestamp(node.get(2).asLong());
        tp.setGrade(node.get(3).asText());
        tp.setLon(node.get(4).asDouble());
        tp.setLat(node.get(5).asDouble());
        tp.setPressure(node.get(6).asInt());
        tp.setWindSpeed(node.get(7).asInt()); // 单位: m/s
        tp.setDirection(node.get(8).asText());
        tp.setSpeed(node.get(9).asInt()); // 移动速度 km/h

        // 风圈: [["30KTS",NE,SE,SW,NW,id], ["50KTS",...], ["64KTS",...]]
        if (node.size() > 10 && node.get(10).isArray()) {
            List<int[]> radiiList = new ArrayList<>();
            for (JsonNode rNode : node.get(10)) {
                if (rNode.isArray() && rNode.size() >= 5) {
                    String ktsLabel = rNode.get(0).asText();
                    int kts = ktsLabel.contains("30") ? 30
                            : ktsLabel.contains("50") ? 50
                                    : ktsLabel.contains("64") ? 64 : 0;
                    radiiList.add(new int[] { kts, rNode.get(1).asInt(), rNode.get(2).asInt(),
                            rNode.get(3).asInt(), rNode.get(4).asInt() });
                }
            }
            tp.setWindRadii(radiiList);
        }

        // 各家机构预报: {"BABJ":[[+12h,time,lon,lat,press,wind,agency,grade],...],...}
        if (node.size() > 11 && node.get(11).isObject()) {
            Map<String, List<ForecastPoint>> fm = new LinkedHashMap<>();
            JsonNode fObj = node.get(11);
            Iterator<String> fieldNames = fObj.fieldNames();
            while (fieldNames.hasNext()) {
                String agency = fieldNames.next();
                JsonNode af = fObj.get(agency);
                if (af.isArray()) {
                    List<ForecastPoint> fpList = new ArrayList<>();
                    for (JsonNode fpNode : af) {
                        if (fpNode.isArray() && fpNode.size() >= 7) {
                            ForecastPoint fp = new ForecastPoint();
                            fp.setHours(fpNode.get(0).asInt());
                            fp.setTime(fpNode.get(1).asText());
                            fp.setLon(fpNode.get(2).asDouble());
                            fp.setLat(fpNode.get(3).asDouble());
                            fp.setPressure(fpNode.get(4).asInt());
                            fp.setWindSpeed(fpNode.get(5).asInt()); // m/s
                            fp.setAgency(fpNode.get(6).asText());
                            fp.setGrade(fpNode.get(7).asText());
                            fpList.add(fp);
                        }
                    }
                    fm.put(agency, fpList);
                }
            }
            tp.setForecasts(fm);
        }

        // 本地时间: ["202608030900","2026年08月03日17时00分",null,null]
        if (node.size() > 12 && node.get(12).isArray() && node.get(12).size() >= 2) {
            tp.setLocalTime(node.get(12).get(0).asText());
            tp.setLocalTimeChinese(node.get(12).get(1).asText());
        }

        return tp;
    }

    // ===================== 工具 =====================

    private static String fmt(double v) {
        return String.format("%.1f", v);
    }

    private static int min(int a, int b, int c, int d) {
        return Math.min(Math.min(a, b), Math.min(c, d));
    }

    private static int max(int a, int b, int c, int d) {
        return Math.max(Math.max(a, b), Math.max(c, d));
    }

    private static String coalesce(String a, String b) {
        return (a != null && !a.isEmpty()) ? a : b;
    }

    // ===================== 数据模型 =====================

    public static class TyphoonSummary {
        private int id;
        private String enName, cnName, code1, code2, cmaNum, description, status;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getEnName() {
            return enName;
        }

        public void setEnName(String v) {
            enName = v;
        }

        public String getCnName() {
            return cnName;
        }

        public void setCnName(String v) {
            cnName = v;
        }

        public String getCode1() {
            return code1;
        }

        public void setCode1(String v) {
            code1 = v;
        }

        public String getCode2() {
            return code2;
        }

        public void setCode2(String v) {
            code2 = v;
        }

        public String getCmaNum() {
            return cmaNum;
        }

        public void setCmaNum(String v) {
            cmaNum = v;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String v) {
            description = v;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String v) {
            status = v;
        }
    }

    public static class TyphoonDetail {
        private int id;
        private String enName, cnName, code1, code2, description, status;
        private List<TrackPoint> tracks;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getEnName() {
            return enName;
        }

        public void setEnName(String v) {
            enName = v;
        }

        public String getCnName() {
            return cnName;
        }

        public void setCnName(String v) {
            cnName = v;
        }

        public String getCode1() {
            return code1;
        }

        public void setCode1(String v) {
            code1 = v;
        }

        public String getCode2() {
            return code2;
        }

        public void setCode2(String v) {
            code2 = v;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String v) {
            description = v;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String v) {
            status = v;
        }

        public List<TrackPoint> getTracks() {
            return tracks;
        }

        public void setTracks(List<TrackPoint> v) {
            tracks = v;
        }
    }

    public static class TrackPoint {
        private int id, pressure, windSpeed, speed;
        private long timestamp;
        private double lon, lat;
        private String utcTime, grade, direction, localTime, localTimeChinese, description;
        private List<int[]> windRadii;
        private Map<String, List<ForecastPoint>> forecasts;

        public int getId() {
            return id;
        }

        public void setId(int v) {
            id = v;
        }

        public String getUtcTime() {
            return utcTime;
        }

        public void setUtcTime(String v) {
            utcTime = v;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long v) {
            timestamp = v;
        }

        public String getGrade() {
            return grade;
        }

        public void setGrade(String v) {
            grade = v;
        }

        public double getLon() {
            return lon;
        }

        public void setLon(double v) {
            lon = v;
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double v) {
            lat = v;
        }

        public int getPressure() {
            return pressure;
        }

        public void setPressure(int v) {
            pressure = v;
        }

        public int getWindSpeed() {
            return windSpeed;
        }

        public void setWindSpeed(int v) {
            windSpeed = v;
        }

        public String getDirection() {
            return direction;
        }

        public void setDirection(String v) {
            direction = v;
        }

        public int getSpeed() {
            return speed;
        }

        public void setSpeed(int v) {
            speed = v;
        }

        public List<int[]> getWindRadii() {
            return windRadii;
        }

        public void setWindRadii(List<int[]> v) {
            windRadii = v;
        }

        public Map<String, List<ForecastPoint>> getForecasts() {
            return forecasts;
        }

        public void setForecasts(Map<String, List<ForecastPoint>> v) {
            forecasts = v;
        }

        public String getLocalTime() {
            return localTime;
        }

        public void setLocalTime(String v) {
            localTime = v;
        }

        public String getLocalTimeChinese() {
            return localTimeChinese;
        }

        public void setLocalTimeChinese(String v) {
            localTimeChinese = v;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String v) {
            description = v;
        }
    }

    public static class ForecastPoint {
        private int hours, pressure, windSpeed;
        private double lon, lat;
        private String time, agency, grade;

        public int getHours() {
            return hours;
        }

        public void setHours(int v) {
            hours = v;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String v) {
            time = v;
        }

        public double getLon() {
            return lon;
        }

        public void setLon(double v) {
            lon = v;
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double v) {
            lat = v;
        }

        public int getPressure() {
            return pressure;
        }

        public void setPressure(int v) {
            pressure = v;
        }

        public int getWindSpeed() {
            return windSpeed;
        }

        public void setWindSpeed(int v) {
            windSpeed = v;
        }

        public String getAgency() {
            return agency;
        }

        public void setAgency(String v) {
            agency = v;
        }

        public String getGrade() {
            return grade;
        }

        public void setGrade(String v) {
            grade = v;
        }
    }
}
