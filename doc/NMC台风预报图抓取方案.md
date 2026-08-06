# 中央气象台台风预报图抓取方案

## 1. 数据来源

**页面地址**: `http://www.nmc.cn/publish/typhoon/probability-img{N}.html`

中央气象台为每个活跃台风分配一个 `probability-img{N}.html` 页面，`{N}` 从 1 开始顺序递增。每个页面展示该台风最近若干天的路径预报图（3小时间隔，每天8张）。

### 当前活跃台风（2026-07-31）

| 页面 | 台风名 | 产品代码 | 台风编号 |
|------|--------|----------|----------|
| `probability-img1.html` | 红霞 | `0W26120004` | 202612 |
| `probability-img2.html` | 白海豚 | `0W26130000` | 202613 |
| `probability-img3.html` | 麦德姆 | `0W25210015` | 202521 |
| `probability-img4.html` | 海神 | `0W26110000` | 202611 |
| `probability-img5.html` | 美莎克 | `0W26100003` | 202610 |
| `probability-img6.html` | 米克拉 | `0W26070000` | 202607 |

---

## 2. 图片URL规律

所有图片托管在 CDN `https://image.nmc.cn/product/`，无需登录，直接 GET 即可下载。

### URL 格式

```
https://image.nmc.cn/product/{YYYY}/{MM}/{DD}/TCBU/medium/
  SEVP_NMC_TCBU_SFER_EME_ACWP_L89_{产品代码}_{时间戳}00000{序号}.JPG
```

### 实际示例

```
https://image.nmc.cn/product/2026/07/31/TCBU/medium/
  SEVP_NMC_TCBU_SFER_EME_ACWP_L89_0W26130000_20260731110000069.JPG
```

- 每张图片约 360KB
- 图片 CORS 头允许跨域：`Access-Control-Allow-Origin: *`
- URL 末尾 `?v=时间戳` 是防缓存参数，下载时可忽略

---

## 3. 页面访问

### 问题

直接访问 `www.nmc.cn` 的页面会被重定向到 Flash + 内网IP验证（192.168.0.246），无法获取真实内容。

### 绕过方法

在 URL 后加任意查询参数即可绕过网关：

```
http://www.nmc.cn/publish/typhoon/probability-img2.html?t=当前时间戳
```

### 发现所有活跃台风

顺序扫描 `img1` → `imgN`，通过页面大小判断：

```
活跃页面：约 60KB ~ 70KB（含图片列表的完整HTML）
空页面  ：约 16KB（404错误页）
```

伪代码：

```
N = 1
while true:
    html = fetch("probability-img{N}.html?t=" + timestamp)
    if len(html) < 20000:   // 16KB = 空页面
        break
    解析该页面
    N++
```

---

## 4. HTML 解析

### 提取台风名称

从 `<title>` 标签提取，格式为 `台风海洋_台风路径预报_{台风名}`：

```html
<title>台风海洋_台风路径预报_白海豚</title>
```

### 提取产品代码

从图片 URL 中正则提取 `0W\d{8}`：

```
https://image.nmc.cn/product/2026/07/31/TCBU/medium/
  SEVP_NMC_TCBU_SFER_EME_ACWP_L89_0W26130000_20260731110000069.JPG

正则: 0W\d{8}  →  匹配: 0W26130000
```

### 提取图片列表

页面的 `#timeWrap` 容器内，每个 `<div class="time">` 包含一张图片：

```html
<div class="col-xs-12 time actived"
     data-index="0"
     data-img="https://image.nmc.cn/product/2026/07/31/TCBU/medium/SEVP_...JPG?v=1785468688169"
     data-time="07/31 11:00">
    <div> 07/31 11:00 </div>
</div>
```

解析所有 `div.time` 节点，提取：
- `data-img` → 图片URL（去掉 `?v=` 参数）
- `data-time` → 图片时间，格式 `MM/DD HH:MM`
- 年份从 URL 路径 `/product/{YYYY}/` 中提取

---

## 5. 产品代码 → 台风编号 转换

### 规则

```
0W{YY}{NN}XXXX  →  20{YY}{NN}
```

- 把 `0W` 替换为 `20`
- 取 `0W` 后面 4 位数字

### 对照表

| 产品代码 | 转换过程 | 台风编号 | 说明 |
|----------|----------|----------|------|
| `0W26130000` | 20 + 2613 | **202613** | 2026年第13号台风 |
| `0W26120004` | 20 + 2612 | **202612** | 2026年第12号 |
| `0W25210015` | 20 + 2521 | **202521** | 2025年第21号 |
| `0W26070000` | 20 + 2607 | **202607** | 2026年第7号 |

### 代码实现

```java
String tfCode = "20" + productCode.substring(2, 6);
```

```python
tf_code = "20" + product_code[2:6]
```

---

## 6. 数据库设计

### 建表 SQL（达梦DM / MySQL通用）

```sql
CREATE TABLE TYPHOON_PROB_IMG (
    TF_CODE   VARCHAR(20)  NOT NULL,   -- 台风编号，如 '202613'
    TF_NAME   VARCHAR(100),            -- 台风名称，如 '白海豚'
    IMG_TIME  VARCHAR(20)  NOT NULL,   -- 图片时间，如 '2026-07-31 11:00:00'
    IMG_PATH  VARCHAR(500),            -- 本地存储路径
    IMG_URL   VARCHAR(500),            -- 原始CDN URL
    PRIMARY KEY (TF_CODE, IMG_TIME)
);
```

### 字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| `TF_CODE` | VARCHAR(20) | 台风编号，主键之一 |
| `TF_NAME` | VARCHAR(100) | 台风中文名 |
| `IMG_TIME` | VARCHAR(20) | 图片时间，主键之二，格式 `yyyy-MM-dd HH:mm:ss` |
| `IMG_PATH` | VARCHAR(500) | 图片下载到本地的文件路径 |
| `IMG_URL` | VARCHAR(500) | NMC CDN 原始URL |

---

## 7. 下载流程

### 整体流程

```
开始
  │
  ├─ 从 img1 开始循环 N=1,2,3...
  │    │
  │    ├─ GET probability-img{N}.html?t=时间戳
  │    │
  │    ├─ 页面大小 < 20KB？──是──→ 结束循环
  │    │
  │    ├─ 解析HTML:
  │    │   ├─ <title> → 台风名称
  │    │   ├─ 第一张图片URL → 提取产品代码(0W\d{8})
  │    │   └─ #timeWrap 下所有 div.time
  │    │       ├─ data-img → 图片URL
  │    │       └─ data-time → 图片时间(MM/DD HH:MM)
  │    │
  │    ├─ 产品代码 → 台风编号 (20 + productCode[2:6])
  │    │
  │    └─ 遍历每张图片:
  │         │
  │         ├─ 拼接完整时间: 年份(从URL提取) + MM/DD HH:MM → yyyy-MM-dd HH:mm:ss
  │         │
  │         ├─ SELECT * WHERE TF_CODE=? AND IMG_TIME=?
  │         │    │
  │         │    └─ 记录存在？──是──→ 跳过
  │         │
  │         ├─ 下载图片到本地目录
  │         │
  │         └─ INSERT INTO TYPHOON_PROB_IMG
  │
  └─ 结束
```

### 伪代码

```
function downloadTyphoonImages():
    imgIndex = 1
    while true:
        url = "http://www.nmc.cn/publish/typhoon/probability-img" + imgIndex + ".html?t=" + timestamp()
        html = httpGet(url)

        if len(html) < 20000:     // 空页面，无更多台风
            break

        typhoonName = extractFromTitle(html)          // 台风名
        productCode = extractProductCode(html)        // 正则: 0W\d{8}
        tfCode = "20" + productCode[2:6]              // 台风编号

        images = parseImageNodes(html)                // [{url, time}]
        year = extractYear(images[0].url)             // 从URL路径取年份 /product/2026/

        for img in images:
            imgTime = year + "-" + img.time + ":00"   // "2026-07-31 11:00:00"
            cleanUrl = img.url.split("?")[0]           // 去?v参数

            if existsInDB(tfCode, imgTime):
                continue

            localPath = downloadImage(cleanUrl, saveDir)
            insertDB(tfCode, typhoonName, imgTime, localPath, cleanUrl)

        imgIndex++
```

---

## 8. 关键注意点

### 8.1 页面网关绕过

- **症状**：直接访问返回 996 字节 Flash 验证页，内容含 `192.168.0.246` 内网IP
- **解决**：URL 加 `?t=任意值` 即可
- **原理**：网关匹配规则未覆盖带参数的URL

### 8.2 图片CDN无需认证

- `image.nmc.cn` 完全公开访问
- 返回 `Access-Control-Allow-Origin: *`
- 但需要带 `User-Agent` 头（否则可能被部分CDN节点拒绝）

### 8.3 台风编号会变

- 页面编号 `img{N}` 是动态分配的
- 新台风出现时分配下一个可用编号
- 旧台风消散后页面可能保留（不再更新图片）
- **必须用顺序扫描方式发现**，不要硬编码编号范围

### 8.4 图片时间特殊处理

- HTML 中的 `data-time` 不包含年份（只有 `MM/DD HH:MM`）
- 年份需要从图片URL路径 `/product/2026/` 中提取
- 跨年场景（12月31日→1月1日）自动正确，因为URL路径包含完整日期

### 8.5 去重策略

- 联合主键 `(TF_CODE, IMG_TIME)` 保证数据库层面不重复
- 下载前先查询，存在则跳过（避免无效HTTP请求）
- 图片文件本身可覆盖写入（同名文件直接替换）

### 8.6 错误处理

- 图片下载失败（网络超时、404）：记录日志，跳过该张，继续下一张
- 页面解析失败：记录日志，跳过该页面，继续下一个台风
- CDN 偶发不可用：整个任务失败，等待下次定时触发重试

---

## 9. 定时调度建议

| 参数 | 建议值 | 说明 |
|------|--------|------|
| 触发间隔 | 30 分钟 | 图片每3小时更新，30分钟足够及时 |
| 首次执行 | 启动后延迟 10 秒 | 等 Spring 上下文初始化完成 |
| 线程池 | 独立线程 | 避免阻塞主业务 |
| 重试 | 不额外重试 | 等下一个周期自动重试 |

Spring Boot 示例：

```java
@Async
@Scheduled(fixedDelay = 1000 * 60 * 30)  // 30分钟
public void typhoonProbImgJob() {
    typhoonTask.downloadTyphoonImages();
}
```

---

## 10. 图片更新频率

中央气象台台风预报图每 **3 小时**更新一次，时间点为：

```
02:00, 05:00, 08:00, 11:00, 14:00, 17:00, 20:00, 23:00（北京时间）
```

每天 8 张图，每个台风通常保留最近 3 天（约 24 张）的历史图片。

---

## 11. 完整 API / URL 参考

| 用途 | URL |
|------|-----|
| 台风图片CDN基址 | `https://image.nmc.cn/product/` |
| 台风预报图页面 | `http://www.nmc.cn/publish/typhoon/probability-img{N}.html` |
| 台风快讯JSON | `https://www.nmc.cn/dataservice/typhoon/news.json`（仅最新一个台风） |
| 台风交互地图 | `https://typhoon.nmc.cn/web.html` |
