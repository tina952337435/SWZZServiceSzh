package swzzmodeserver.tools;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;

public class CacheHelperUp<K, V> {
    // 创建一个高性能缓存：最多存1000条，写入后5分钟自动过期
    private final Cache<K, V> cache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();

    public V get(K key) {
        return cache.getIfPresent(key);
    }

    public void put(K key, V value) {
        cache.put(key, value);
    }

    public void clear() {
        cache.invalidateAll();
    }

    // 👇 新增一个判断 Key 是否失效的方法
    public boolean isExpired(K key) {
        return cache.getIfPresent(key) == null;
    }
    
    // 或者判断是否有效
    public boolean isValid(K key) {
        return cache.getIfPresent(key) != null;
    }
}
