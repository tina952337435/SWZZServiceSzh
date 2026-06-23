package swzzmodeserver.tools;



import java.util.HashMap;
import java.util.Map;

public class CacheHelper<K, V> {
    private final Map<K, V> cache = new HashMap<>();

    public V get(K key) {
        return cache.get(key);
    }

    public void put(K key, V value) {
        cache.put(key, value);
    }

    public void clear() {
        cache.clear();
    }
}
