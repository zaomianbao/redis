package operation.hash;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
/**
 * Redis的散列可以让用户将多个键值对存储到一个Redis键里面。
 * HashOperations提供一系列方法操作hash
 * @author LiuChengxiang
 * @time 2017年9月28日下午4:59:58
 *
 */
public class HashDemo {
	
	private RedisTemplate<String,Object> redisTemplate;
	private HashOperations<String,String,Object> opsForHash;
	
	@SuppressWarnings("unchecked")
	@Before
	public void before(){
		@SuppressWarnings("resource")
		ApplicationContext context = new ClassPathXmlApplicationContext("/applicationContext.xml");
		redisTemplate = (RedisTemplate<String,Object>)context.getBean("redisTemplate");
		opsForHash = redisTemplate.opsForHash();
	}
	
	@Test
	public void testPut(){
		opsForHash.put("he1", "key1", "a");
		opsForHash.put("he1", "key2", "b");
		opsForHash.put("he1", "key3", "c");
		Map<String, Object> entries = opsForHash.entries("he1");
		System.out.println(entries);//{key3=c, key1=a, key2=b}(无序)
	}
	
	@Test
	public void testPutAll(){
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("key1", "a");
		param.put("key2", "b");
		param.put("key3", "c");
		opsForHash.putAll("he2", param);
		System.out.println(opsForHash.entries("he2"));//{key2=b, key1=a, key3=c}
	}
	
	@Test
	public void testDelete(){
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("key1", "a");
		param.put("key2", "b");
		param.put("key3", "c");
		opsForHash.putAll("he3", param);
		System.out.println(opsForHash.entries("he3"));//{key3=c, key2=b, key1=a}
		opsForHash.delete("he3", "key1");
		System.out.println(opsForHash.entries("he3"));//{key2=b, key3=c}
	}
	
	@Test
	public void testHashKey(){
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("key1", "a");
		param.put("key2", "b");
		param.put("key3", "c");
		opsForHash.putAll("he4", param);
		System.out.println(opsForHash.hasKey("he", "key2"));//false
		System.out.println(opsForHash.hasKey("he4", "key4"));//false
		System.out.println(opsForHash.hasKey("he4", "key2"));//true
	}
	
	@Test
	public void testGet(){
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("key1", "a");
		param.put("key2", "b");
		param.put("key3", "c");
		opsForHash.putAll("he5", param);
		System.out.println(opsForHash.get("he5", "key1"));//a
		System.out.println(opsForHash.get("he5", "key"));//null
	}
	
	@Test
	public void testMultiGet(){
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("key1", "a");
		param.put("key2", "b");
		param.put("key3", "c");
		opsForHash.putAll("he6", param);
		List<String> keys = new ArrayList<String>();
		keys.add("key1");
		keys.add("key");
		keys.add("key2");
		System.out.println(opsForHash.multiGet("he6", keys));//[a, null, b]
	}
	
	@Test
	public void testIncrement(){
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("key1", "a");
		param.put("key2", "b");
		param.put("key3", "c");
		param.put("key4", 4);
		opsForHash.putAll("he7", param);
		System.out.println(opsForHash.increment("he7", "key4", 1));//5
		System.out.println(opsForHash.increment("he7", "key4", 1.1));//6.1
		try {
			opsForHash.increment("he7", "key1", 1);//ERR hash value is not an integer
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			opsForHash.increment("he7", "key1", 1.1);//ERR hash value is not a float
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testKeys(){
		redisTemplate.delete("he8");
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("key4", "d");
		param.put("key1", "a");
		param.put("key3", "c");
		param.put("key5", "e");
		param.put("key2", "b");
		opsForHash.putAll("he8", param);
		Set<String> keys = opsForHash.keys("he8");
		System.out.println(keys);//[key4, key3, key5, key2, key1]
	}
	
	@Test
	public void testSize(){
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("key4", "d");
		param.put("key1", "a");
		param.put("key3", "c");
		param.put("key5", "e");
		param.put("key2", "b");
		opsForHash.putAll("he9", param);
		System.out.println(opsForHash.size("he9"));//5
	}
	
	@Test
	public void testPutIfAbsent(){
		//仅当hashKey不存在时才设置散列hashKey的值。
		System.out.println(opsForHash.putIfAbsent("he10", "key1", "a"));//true
		System.out.println(opsForHash.putIfAbsent("he10", "key1", "a"));//false
	}
	
	@Test
	public void testValues(){
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("key4", "d");
		param.put("key1", "a");
		param.put("key3", "c");
		param.put("key5", "e");
		param.put("key2", "b");
		opsForHash.putAll("he11", param);
		List<Object> values = opsForHash.values("he11");
		System.out.println(values);//[d, c, e, b, a]
	}
	
	@Test
	public void testEntries(){
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("key4", "d");
		param.put("key1", "a");
		param.put("key3", "c");
		param.put("key5", "e");
		param.put("key2", "b");
		opsForHash.putAll("he12", param);
		Map<String, Object> entries = opsForHash.entries("he12");
		System.out.println(entries);//{key3=c, key2=b, key5=e, key1=a, key4=d}
	}
	
	@Test
	public void testScan(){
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("key4", "d");
		param.put("key1", "a");
		param.put("key3", "c");
		param.put("key5", "e");
		param.put("key2", "b");
		opsForHash.putAll("he13", param);
		Cursor<Map.Entry<String, Object>> curosr = opsForHash.scan("he13", ScanOptions.NONE);
		while(curosr.hasNext()){
			Map.Entry<String, Object> entry = curosr.next();
			System.out.println(entry.getKey()+":"+entry.getValue());
		}
		/**
			key4:d
			key3:c
			key5:e
			key2:b
			key1:a
		 */
	}

}
