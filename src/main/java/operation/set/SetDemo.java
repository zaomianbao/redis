package operation.set;

import java.util.Arrays;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.SetOperations;

import entity.User;
/**
 * Redis的Set是string类型的无序集合。集合成员是唯一的，这就意味着集合中不能出现重复的数据。
 * Redis 中 集合是通过哈希表实现的，所以添加，删除，查找的复杂度都是O(1)。
 * SetOperations提供了对无序集合的一系列操作
 * @author LiuChengxiang
 * @time 2017年9月28日下午5:00:23
 *
 */
public class SetDemo {
	
	private RedisTemplate<String,Object> redisTemplate;
	private SetOperations<String, Object> opsForSet;
	
	@SuppressWarnings("unchecked")
	@Before
	public void before(){
		@SuppressWarnings("resource")
		ApplicationContext context = new ClassPathXmlApplicationContext("/applicationContext.xml");
		redisTemplate = (RedisTemplate<String,Object>)context.getBean("redisTemplate");
		opsForSet = redisTemplate.opsForSet();
	}
	
	@Test
	public void testAddSizeAndMembers(){
		redisTemplate.delete("zhou1");
		//无序集合中添加元素，返回添加个数 也可以直接在add里面添加多个值
		System.out.println(opsForSet.add("zhou1", "a"));//1
		//无序集合的大小长度
		System.out.println(opsForSet.size("zhou1"));//1
		System.out.println(opsForSet.add("zhou1", "a","b","c","d","e"));//4
		System.out.println(opsForSet.size("zhou1"));//5
		System.out.println(opsForSet.add("zhou1", new Object[]{"f","g"}));//4
		System.out.println(opsForSet.size("zhou1"));//7
		//返回集合中的所有成员
		System.out.println(opsForSet.members("zhou1"));//[f, g, d, a, e, b, c]
	}
	
	@Test
	public void testRemove(){
		redisTemplate.delete("zhou2");
		User user = new User();
		user.setId(1l);
		opsForSet.add("zhou2", "a","b","c","d","e",user);
		User user1 = new User();
		//移除集合中一个或多个成员
		System.out.println(opsForSet.remove("zhou2", "a","d","f",user1));//2
		System.out.println(opsForSet.members("zhou2"));//[e, b, c]
	}
	
	@Test
	public void testPop(){
		redisTemplate.delete("zhou3");
		opsForSet.add("zhou3", "a","b","c","d","e");
		//移除并返回集合中的一个随机元素
		System.out.println(opsForSet.pop("zhou3"));//e
		System.out.println(opsForSet.members("zhou3"));//[d, a, b, c]
	}
	
	@Test
	public void testMove(){
		redisTemplate.delete("zhou4");
		redisTemplate.delete("zhou5");
		opsForSet.add("zhou4", "a","b");
		//将 member 元素从 source 集合移动到 destination 集合
		System.out.println(opsForSet.move("zhou4", "a", "zhou4"));//true
		System.out.println(opsForSet.move("zhou4", "c", "zhou5"));//false
		System.out.println(opsForSet.move("zhou4", "a", "zhou5"));//true
		opsForSet.add("zhou5", "b");
		System.out.println(opsForSet.move("zhou4", "b", "zhou5"));//true
	}
	
	@Test
	public void testIsMember(){
		//判断 member 元素是否是集合 key 的成员
		System.out.println(opsForSet.isMember("zhou6", "a"));//false
	}
	
	@Test
	public void testIntersect(){
		redisTemplate.delete("zhou7");
		redisTemplate.delete("zhou8");
		redisTemplate.delete("zhou9");
		redisTemplate.delete("zhou10");
		redisTemplate.delete("zhou11");
		opsForSet.add("zhou7", "a","b","c","d","e");
		opsForSet.add("zhou8", "c","d","e","f","g");
		//key对应的无序集合与otherKey对应的无序集合求交集
		Set<Object> intersect = opsForSet.intersect("zhou7", "zhou8");
		System.out.println(intersect);//[d, c, e]
		opsForSet.add("zhou9", "c","h");
		//key对应的无序集合与多个otherKey对应的无序集合求交集
		System.out.println(opsForSet.intersect("zhou7", Arrays.asList("zhou8","zhou9")));//[c]
		//key无序集合与otherkey无序集合的交集存储到destKey无序集合中
		System.out.println(opsForSet.intersectAndStore("zhou7", "zhou8","zhou10"));//3
		System.out.println(opsForSet.members("zhou10"));//[e, c, d]
		//key对应的无序集合与多个otherKey对应的无序集合求交集存储到destKey无序集合中
		System.out.println(opsForSet.intersectAndStore("zhou7", Arrays.asList("zhou8","zhou9"),"zhou11"));//1
		System.out.println(opsForSet.members("zhou11"));//[c]
	}
	
	@Test
	public void testUnion(){
		redisTemplate.delete("zhou12");
		redisTemplate.delete("zhou13");
		redisTemplate.delete("zhou14");
		redisTemplate.delete("zhou15");
		redisTemplate.delete("zhou16");
		opsForSet.add("zhou12", "a","b","c","d","e");
		opsForSet.add("zhou13", "c","d","e","f","g");
		//key无序集合与otherKey无序集合的并集
		Set<Object> union = opsForSet.union("zhou12", "zhou13");
		System.out.println(union);//[f, g, d, a, e, c, b]
		opsForSet.add("zhou14", "c","h");
		//key无序集合与多个otherKey无序集合的并集
		System.out.println(opsForSet.union("zhou12", Arrays.asList("zhou13","zhou14")));//[h, f, g, d, a, e, c, b]
		//key无序集合与otherkey无序集合的并集存储到destKey无序集合中
		System.out.println(opsForSet.unionAndStore("zhou12", "zhou13","zhou15"));//7
		System.out.println(opsForSet.members("zhou15"));//[f, g, d, a, e, c, b]
		//key无序集合与多个otherkey无序集合的并集存储到destKey无序集合中
		System.out.println(opsForSet.unionAndStore("zhou12", Arrays.asList("zhou13","zhou14"),"zhou16"));//8
		System.out.println(opsForSet.members("zhou16"));//[h, f, g, d, a, e, c, b]
	}
	
	@Test
	public void testDifference(){
		redisTemplate.delete("zhou17");
		redisTemplate.delete("zhou18");
		redisTemplate.delete("zhou19");
		redisTemplate.delete("zhou20");
		redisTemplate.delete("zhou21");
		opsForSet.add("zhou17", "a","b","c","d","e");
		opsForSet.add("zhou18", "c","d","e","f","g");
		//key无序集合与otherKey无序集合的差集
		Set<Object> difference = opsForSet.difference("zhou17", "zhou18");
		System.out.println(difference);//[a, b]
		opsForSet.add("zhou19", "c","h");
		//key无序集合与多个otherKey无序集合的差集
		System.out.println(opsForSet.difference("zhou17", Arrays.asList("zhou18","zhou19")));//[a, b]
		//key无序集合与otherkey无序集合的差集存储到destKey无序集合中
		System.out.println(opsForSet.differenceAndStore("zhou17", "zhou18","zhou20"));//2
		System.out.println(opsForSet.members("zhou20"));//[a, b]
		//key无序集合与多个otherkey无序集合的差集存储到destKey无序集合中
		System.out.println(opsForSet.differenceAndStore("zhou17", Arrays.asList("zhou18","zhou19"),"zhou21"));//2
		System.out.println(opsForSet.members("zhou21"));//[a, b]
	}
	
	@Test
	public void testRandomMember(){
		redisTemplate.delete("zhou22");
		opsForSet.add("zhou22", "a","b","c","d","e");
		//随机获取key无序集合中的一个元素
		System.out.println(opsForSet.randomMember("zhou22"));//e
		System.out.println(opsForSet.randomMember("zhou22"));//d
		System.out.println(opsForSet.randomMember("zhou22"));//c
		System.out.println(opsForSet.randomMember("zhou22"));//b
		System.out.println(opsForSet.randomMember("zhou22"));//e
		//获取多个key无序集合中的元素，count表示个数
		System.out.println(opsForSet.randomMembers("zhou22",8));//[e, a, e, e, d, e, b, e]
		System.out.println(opsForSet.randomMembers("zhou22",4));//[d, c, d, d]
		//获取多个key无序集合中的元素（去重），count表示个数
		System.out.println(opsForSet.distinctRandomMembers("zhou22",6));//[c, e, d, a, b]
		System.out.println(opsForSet.distinctRandomMembers("zhou22",4));//c, b, e, d]
	}
	
	@Test
	public void testScan(){
		redisTemplate.delete("zhou23");
		opsForSet.add("zhou23", "a","b","c","d","e");
		//遍历set,类似于Interator
		Cursor<Object> curosr = opsForSet.scan("zhou23", ScanOptions.NONE);
		while(curosr.hasNext()){
			System.out.println(curosr.next());//e a d c b
		}
	}
	
}
