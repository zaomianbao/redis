package operation.string;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
/**
 * 
 * 	Redis 可以存储键与5种不同数据结构类型之间的映射，这5种数据结构类型分别为String（字符串）、List（列表）、Set（集合）、Hash（散列）和 Zset（有序集合）。
 * 
 *	结构类型			结构存储的值												结构的读写能力
 *	String	可以是字符串、整数或者浮点数								对整个字符串或者字符串的其中一部分执行操作；对象和浮点数执行自增(increment)或者自减(decrement)
 *	List	一个链表，链表上的每个节点都包含了一个字符串					从链表的两端推入或者弹出元素；根据偏移量对链表进行修剪(trim)；读取单个或者多个元素；根据值来查找或者移除元素
 *	Set		包含字符串的无序收集器(unorderedcollection)				添加、获取、移除单个元素；检查一个元素是否存在于某个集合中；计算交集、并集、差集；从集合里卖弄随机获取元素
 *			并且被包含的每个字符串都是独一无二的、各不相同					
 *	Hash	包含键值对的无序散列表									添加、获取、移除单个键值对；获取所有键值对
 *	Zset	字符串成员(member)与浮点数分值(score)之间的有序映射			添加、获取、删除单个元素；根据分值范围(range)或者成员来获取元素
 *			元素的排列顺序由分值的大小决定								
 *
 * @author LiuChengxiang
 * @time 2017年9月28日下午5:00:28
 *
 */
public class StringDemo {
	
	private RedisTemplate<String,String> stringTemplate;
	private ValueOperations<String, String> opsForValue;

	@Before
	public void before(){
		//初始化
		@SuppressWarnings("resource")
		ApplicationContext context = new ClassPathXmlApplicationContext("/applicationContext.xml");
		stringTemplate = (RedisTemplate<String,String>)context.getBean("stringRedisTemplate");
		opsForValue = stringTemplate.opsForValue();
	}
	
	@Test
	public void  testSet(){
		stringTemplate.delete("liu1");
		opsForValue.set("liu1", "liu1");
		System.out.println(opsForValue.get("liu1"));//liu1
	}
	
	@Test
	public void  testSetTimeOut() throws InterruptedException{
		stringTemplate.delete("liu2");
		//加了失效机制
		opsForValue.set("liu2", "liu2", 10, TimeUnit.SECONDS);
		Thread.sleep(5000);
		System.out.println(opsForValue.get("liu2"));//liu2
		Thread.sleep(5000);
		System.out.println(opsForValue.get("liu2"));//null
	}
	
	@Test
	public void  testSetOverwrite(){
		stringTemplate.delete("liu3");
		opsForValue.set("liu3", "liu3");
		System.out.println(opsForValue.get("liu3"));//liu3
		//该方法是用 value 参数覆写(overwrite)给定 key 所储存的字符串值，从偏移量 offset 开始
		opsForValue.set("liu3", "666666", 1);
		System.out.println(opsForValue.get("liu3"));//l666666
	}
	
	@Test
	public void  testSetIfAbsent(){
		stringTemplate.delete("liu4");
		stringTemplate.delete("liu5");
		opsForValue.set("liu4", "liu4");
		System.out.println(opsForValue.setIfAbsent("liu4", "liu4"));//false
		System.out.println(opsForValue.setIfAbsent("liu5", "liu5"));//true
	}
	
	@Test
	public void  testMultiSetAndGet (){
		stringTemplate.delete("liu6");
		stringTemplate.delete("liu7");
		stringTemplate.delete("liu8");
		stringTemplate.delete("liu9");
		Map<String,String> param = new HashMap<String,String>();
		param.put("liu6", "liu6");
		param.put("liu7", "liu7");
		param.put("liu8", "liu8");
		//为多个键分别设置它们的值
		opsForValue.multiSet(param);
		List<String> keys = new ArrayList<String>();
		keys.add("liu6");
		keys.add("liu7");
		keys.add("liu8");
		//为多个键分别取出它们的值
		List<String> results = opsForValue.multiGet(keys);
		for (String result : results) {
			System.out.println(result);
			/*
				liu6
				liu7
				liu8
			 */
		}
		param.clear();
		param.put("liu8", "hahaha");
		param.put("liu9", "liu9");
		//为多个键分别设置它们的值，如果存在则返回false，不存在返回true
		System.out.println(opsForValue.multiSetIfAbsent(param));//false
		System.out.println(opsForValue.get("liu8"));//liu8
	}
	
	@Test
	public void  testGetAndSet(){
		stringTemplate.delete("liu9");
		opsForValue.set("liu9", "liu9");
		//设置键的字符串值并返回其旧值
		System.out.println(opsForValue.getAndSet("liu9", "haha"));//liu9
		System.out.println(opsForValue.get("liu9"));//haha
	}
	
	@Test
	public void  testIncrement(){
		stringTemplate.delete("liu10");
		opsForValue.set("liu10", "6");
		//值增长，支持整形和浮点型
		System.out.println(opsForValue.increment("liu10", 1));//7
		System.out.println(opsForValue.increment("liu10", 1.1));//8.1
		opsForValue.set("liu10", "liu10");
		opsForValue.increment("liu10", 1);//redis.clients.jedis.exceptions.JedisDataException: ERR value is not an integer or out of range
	}
	
	@Test
	public void  testAppend(){
		stringTemplate.delete("liu11");
		stringTemplate.delete("liu12");
		//如果key已经存在并且是一个字符串，则该命令将该值追加到字符串的末尾。如果键不存在，则它被创建并设置为空字符串，因此APPEND在这种特殊情况下将类似于SET。
		opsForValue.append("liu11", "liu11");
		System.out.println(opsForValue.get("liu11"));//liu11
		opsForValue.set("liu12", "liu12");
		opsForValue.append("liu12", "haha");
		System.out.println(opsForValue.get("liu12"));//liu12haha
	}
	
	@Test
	public void  testGetPart(){
		stringTemplate.delete("liu13");
		opsForValue.set("liu13", "liu13");
		//截取key所对应的value字符串
		System.out.println(opsForValue.get("liu13", 0, 2));//liu
	}
	
	@Test
	public void  testSize(){
		stringTemplate.delete("liu14");
		opsForValue.set("liu14", "liu14");
		//返回key所对应的value值得长度
		System.out.println(opsForValue.size("liu14"));//5
	}
	
	@Test
	public void  testSetBit(){
		stringTemplate.delete("liu15");
		//true为1，false为0
		opsForValue.set("liu15", "liu15");
		//对 key 所储存的字符串值，设置或清除指定偏移量上的位(bit)
		//key键对应的值value对应的ASCII码,在offset的位置(从左向右数)变为value
		System.out.println(opsForValue.setBit("liu15", 13, true));//false
		System.out.println(opsForValue.get("liu15"));//lmu15
		for(int i = 0 ; i<"liu15".length()*8;i++){
			if(opsForValue.getBit("liu15", i)){
				System.out.print(1);
			}else{
				System.out.print(0);
			}
			//0110110001101101011101010011000100110101
		}
	}
	
}
