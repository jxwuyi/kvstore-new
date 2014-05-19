package kvstore;

import static org.junit.Assert.*;

import org.junit.*;

public class KVCacheTest {

    /**
     * Verify the cache can put and get a KV pair successfully.
     */
    @Test
    public void singlePutAndGet() {
        KVCache cache = new KVCache(1, 4);
        cache.put("hello", "world");
        assertEquals("world", cache.get("hello"));
    }
    
    @Test
    public void singleGet() {
        KVCache cache = new KVCache(1, 4);
        assertNull(cache.get("hello"));
    }
    
    @Test
    public void multiplePutsAndGets() {
        KVCache cache = new KVCache(1, 4);
        cache.put("hello", "world");
        assertEquals("world", cache.get("hello"));
        cache.put("hello", "world!");
        assertEquals("world!", cache.get("hello"));
    }
    
    @Test
    public void testEviction() {
        KVCache cache = new KVCache(1, 3);
        cache.put("A", "1");
        cache.put("B", "2");
        cache.put("C", "3");
        cache.put("D", "4");
        assertNull(cache.get("A"));
        cache.put("B", "5");
        assertEquals("5", cache.get("B"));
        cache.put("E", "6");
        assertNull(cache.get("C"));
    }

}
