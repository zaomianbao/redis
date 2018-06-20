package operation.zset;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.connection.RedisZSetCommands.Range;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
/**
 * Redis 有序集合和无序集合一样也是string类型元素的集合,且不允许重复的成员。
 * 不同的是每个元素都会关联一个double类型的分数。redis正是通过分数来为集合中的成员进行从小到大的排序。
 * 有序集合的成员是唯一的,但分数(score)却可以重复。
 * ZSetOperations提供了一系列方法对有序集合进行操作
 * @author LiuChengxiang
 * @time 2017年9月28日下午5:15:18
 *
 */
public class ZSetDemo {
	
	private RedisTemplate<String,Object> redisTemplate;
	private ZSetOperations<String, Object> opsForZSet;
	
	@SuppressWarnings("unchecked")
	@Before
	public void before(){
		@SuppressWarnings("resource")
		ApplicationContext context = new ClassPathXmlApplicationContext("/applicationContext.xml");
		redisTemplate = (RedisTemplate<String,Object>)context.getBean("redisTemplate");
		opsForZSet = redisTemplate.opsForZSet();
	}

	@Test
	public void testAdd(){
		redisTemplate.delete("fan1");
		//将值添加到键中的排序集合，如果已存在，则更新其分数。
		System.out.println(opsForZSet.add("fan1", "a", 1));//true （这里的1.0可以用1代替,因为用double收参） 
		ZSetOperations.TypedTuple<Object> objectTypedTuple1 = new DefaultTypedTuple<Object>("b",2.0);//这里必须是2.0，因为那边是用Double收参
		ZSetOperations.TypedTuple<Object> objectTypedTuple2 = new DefaultTypedTuple<Object>("c",3.0);
		Set<ZSetOperations.TypedTuple<Object>> tuples = new HashSet<ZSetOperations.TypedTuple<Object>>();
		tuples.add(objectTypedTuple1);
		tuples.add(objectTypedTuple2);
		System.out.println(opsForZSet.add("fan1",tuples));//2
		//通过索引区间返回有序集合指定区间内的成员，其中有序集成员按分数值递增(从小到大)顺序排列
		System.out.println(opsForZSet.range("fan1",0,-1));//[a, b, c]
	}
	
	@Test
	public void testRemove(){
		redisTemplate.delete("fan2");
		opsForZSet.add("fan2", "a", 1);
		System.out.println(opsForZSet.range("fan2", 0, -1));//[a]
		opsForZSet.remove("fan2", "a");
		System.out.println(opsForZSet.range("fan2", 0, -1));//[]
	}
	
	@Test
	public void testIncrementScore(){
		redisTemplate.delete("fan3");
		//通过增量增加排序集中的元素的分数
		System.out.println(redisTemplate.keys("fan3"));//[]
		System.out.println(opsForZSet.incrementScore("fan3", "a", -1));//-1.0(可见默认技术为0)
		System.out.println(redisTemplate.keys("fan3"));//[fan3]
	}
	
	@Test
	public void testRank(){
		redisTemplate.delete("fan4");
		opsForZSet.add("fan4", "a", 1);
		opsForZSet.add("fan4", "b", 3);
		opsForZSet.add("fan4", "c", 2);
		opsForZSet.add("fan4", "d", -1);
		System.out.println(opsForZSet.range("fan4", 0, -1));//[d, a, c, b]（从小到大）
		//在排序集中确定具有值的元素的索引,并返回其索引(从低到高)
		System.out.println(opsForZSet.rank("fan4", "b"));//3(从小到大且从零开始)
	}
	
	@Test
	public void testReverseRank(){
		redisTemplate.delete("fan5");
		opsForZSet.add("fan5", "a", 1);
		opsForZSet.add("fan5", "b", 3);
		opsForZSet.add("fan5", "c", 2);
		opsForZSet.add("fan5", "d", -1);
		//当从高到低时，确定排序集中的值的元素的索引。
		System.out.println(opsForZSet.reverseRank("fan5", "b"));//0(从大到小且从零开始)
	}
	
	@Test
	public void testRangeWithScores(){
		redisTemplate.delete("fan6");
		opsForZSet.add("fan6", "a", 1);
		opsForZSet.add("fan6", "b", 3);
		opsForZSet.add("fan6", "c", 2);
		opsForZSet.add("fan6", "d", -1);
		//从排序集中获取开始和结束之间的元组(Tuple)。
		Set<TypedTuple<Object>> rangeWithScores = opsForZSet.rangeWithScores("fan6", 0	, -1);
		Iterator<TypedTuple<Object>> iterator = rangeWithScores.iterator();
		while(iterator.hasNext()){
			TypedTuple<Object> next = iterator.next();
			System.out.println("value:"+next.getValue()+" score:"+next.getScore());
		/*
			value:d score:-1.0
			value:a score:1.0
			value:c score:2.0
			value:b score:3.0
		 */
		}
	}
	
	@Test
	public void testRangeByScore(){
		redisTemplate.delete("fan7");
		opsForZSet.add("fan7", "a", 1);
		opsForZSet.add("fan7", "b", 3);
		opsForZSet.add("fan7", "c", 2);
		opsForZSet.add("fan7", "d", -1);
		//得到分数在最小和最大值之间的元素。(从小到大)
		Set<Object> rangeByScore = opsForZSet.rangeByScore("fan7", 1, 2);
		System.out.println(rangeByScore);//[a, c]
		//从开始到结束的范围内获取元素，其中分数在分类集合的最小值和最大值之间。
		Set<Object> rangeByScore2 = opsForZSet.rangeByScore("fan7", 0, 10, 0, -1);
		System.out.println(rangeByScore2);//[a, c, b]
		Set<Object> rangeByScore3 = opsForZSet.rangeByScore("fan7", -1, 3, 0, 1);
		System.out.println(rangeByScore3);//[d]
	}
	
	@Test
	public void testRangeByScoreWithScores(){
		redisTemplate.delete("fan8");
		opsForZSet.add("fan8", "a", 1);
		opsForZSet.add("fan8", "b", 3);
		opsForZSet.add("fan8", "c", 2);
		opsForZSet.add("fan8", "d", -1);
		//得到一组元组，其中分数在分类集合的最小值和最大值之间
		Set<TypedTuple<Object>> rangeByScoreWithScores = opsForZSet.rangeByScoreWithScores("fan8", 1, 2);//注意("fan8",2,1)是获取不到数据的
		Iterator<TypedTuple<Object>> iterator = rangeByScoreWithScores.iterator();
		while(iterator.hasNext()){
			TypedTuple<Object> next = iterator.next();
			System.out.println("value:"+next.getValue()+" score:"+next.getScore());
		/*
			value:a score:1.0
			value:c score:2.0
		 */
		}
		//从开始到结束的范围内获取一组元组，其中分数在分类集中的最小值和最大值之间。
		Set<TypedTuple<Object>> rangeByScoreWithScores2 = opsForZSet.rangeByScoreWithScores("fan8", 1, 2, 1, 2);
		Iterator<TypedTuple<Object>> iterator2 = rangeByScoreWithScores2.iterator();
		while(iterator2.hasNext()){
			TypedTuple<Object> next = iterator2.next();
			System.out.println("value:"+next.getValue()+" score:"+next.getScore());
		/*
			value:c score:2.0
		 */
		}
	}
	
	@Test
	public void testReverseRange(){
		redisTemplate.delete("fan9");
		opsForZSet.add("fan9", "a", 1);
		opsForZSet.add("fan9", "b", 3);
		opsForZSet.add("fan9", "c", 2);
		opsForZSet.add("fan9", "d", -1);
		//从从高到低的排序集中获取从头(start)到尾(end)内的元素。
		Set<Object> reverseRange = opsForZSet.reverseRange("fan9", 0, -1);
		System.out.println(reverseRange);//[b, c, a, d]
		//从开始(start)到结束(end)，从排序从高到低的排序集中获取元组的集合
		Set<TypedTuple<Object>> reverseRangeWithScores = opsForZSet.reverseRangeWithScores("fan9", 0, -1);
		Iterator<TypedTuple<Object>> iterator = reverseRangeWithScores.iterator();
		while(iterator.hasNext()){
			TypedTuple<Object> next = iterator.next();
			System.out.println("value:"+next.getValue()+" score:"+next.getScore());
			/*
				value:b score:3.0
				value:c score:2.0
				value:a score:1.0
				value:d score:-1.0 
			 */
		}
		//从高到低的排序集中获取分数在最小和最大值之间的元素。
		Set<Object> reverseRangeByScore = opsForZSet.reverseRangeByScore("fan9", -1, 2);
		System.out.println(reverseRangeByScore);//[c, a, d]
		//从开始到结束的范围内获取元素，其中分数在最小和最大之间，从排序集排序高 - >低。
		Set<Object> reverseRangeByScore2 = opsForZSet.reverseRangeByScore("fan9", -1, 2, 2, 3);
		System.out.println(reverseRangeByScore2);//[d]
		//得到一组元组，其中分数在最小和最大之间，从排序从高到低
		Set<TypedTuple<Object>> reverseRangeByScoreWithScores = opsForZSet.reverseRangeByScoreWithScores("fan9", -1, 2);
		Iterator<TypedTuple<Object>> iterator2 = reverseRangeByScoreWithScores.iterator();
		while(iterator2.hasNext()){
			TypedTuple<Object> next = iterator2.next();
			System.out.println("value:"+next.getValue()+" score:"+next.getScore());
			/*
				value:c score:2.0
				value:a score:1.0
				value:d score:-1.0
			 */
		}
		//从开始到结束的范围内获取一组元组，其中分数在最小和最大之间，从排序集排序高 - >低。
		Set<TypedTuple<Object>> reverseRangeByScoreWithScores2 = opsForZSet.reverseRangeByScoreWithScores("fan9", -1, 2, 1, 3);
		Iterator<TypedTuple<Object>> iterator3 = reverseRangeByScoreWithScores2.iterator();
		while(iterator3.hasNext()){
			TypedTuple<Object> next = iterator3.next();
			System.out.println("value:"+next.getValue()+" score:"+next.getScore());
			/*
				value:a score:1.0
				value:d score:-1.0
			 */
		}
		
	}
	
	@Test
	public void testCount(){
		redisTemplate.delete("fan10");
		opsForZSet.add("fan10", "a", 1);
		opsForZSet.add("fan10", "b", 3);
		opsForZSet.add("fan10", "c", 2);
		opsForZSet.add("fan10", "d", -1);
		//计算排序集中在最小和最大分数之间的元素数。
		Long count = opsForZSet.count("fan10", -1, 2);
		System.out.println(count);//3
	}
	
	@Test
	public void testSizeAndZCard(){
		redisTemplate.delete("fan11");
		opsForZSet.add("fan11", "a", 1);
		opsForZSet.add("fan11", "b", 3);
		opsForZSet.add("fan11", "c", 2);
		opsForZSet.add("fan11", "d", -1);
		//返回使用给定键存储的排序集的元素数(其实size()底层就是调用的zCard())
		Long size = opsForZSet.size("fan11");
		System.out.println(size);//4
		//使用键获取排序集的大小。
		Long zCard = opsForZSet.zCard("fan11");
		System.out.println(zCard);//4
	}
	
	@Test
	public void testScore(){
		redisTemplate.delete("fan12");
		opsForZSet.add("fan12", "a", 1);
		opsForZSet.add("fan12", "b", 3);
		opsForZSet.add("fan12", "c", 2);
		opsForZSet.add("fan12", "d", -1);
		//使用键值从排序集中获取具有值的元素的分数
		Double score = opsForZSet.score("fan12", "b");
		System.out.println(score);//3.0
	}
	
	@Test
	public void testRemoveRange(){
		redisTemplate.delete("fan13");
		opsForZSet.add("fan13", "a", 1);
		opsForZSet.add("fan13", "b", 3);
		opsForZSet.add("fan13", "c", 2);
		opsForZSet.add("fan13", "d", -1);
		//使用键从排序集中删除开始和结束之间范围内的元素
		Long removeRange = opsForZSet.removeRange("fan13", 1, 3);
		System.out.println(removeRange);//3
		System.out.println(opsForZSet.zCard("fan13"));//1
	}
	
	@Test
	public void testRemoveRangeByScore(){
		redisTemplate.delete("fan13");
		opsForZSet.add("fan13", "a", 1);
		opsForZSet.add("fan13", "b", 3);
		opsForZSet.add("fan13", "c", 2);
		opsForZSet.add("fan13", "d", -1);
		//使用键从排序集中移除最小和最大值之间的元素
		Long removeRangeByScore = opsForZSet.removeRangeByScore("fan13", 2, 100);
		System.out.println(removeRangeByScore);//2
	}
	
	@Test
	public void testUnionAndStore(){
		redisTemplate.delete("fan14");
		redisTemplate.delete("fan15");
		redisTemplate.delete("fan16");
		redisTemplate.delete("fan17");
		redisTemplate.delete("fan18");
		opsForZSet.add("fan14", "a", 1);
		opsForZSet.add("fan14", "b", 3);
		opsForZSet.add("fan14", "c", 2);
		opsForZSet.add("fan14", "d", -1);
		
		opsForZSet.add("fan15", "c", 1);
		opsForZSet.add("fan15", "d", 3);
		opsForZSet.add("fan15", "e", 2);
		opsForZSet.add("fan15", "f", -1);
		//在键和其他键上的联合排序集合，并将结果存储在目标destIny中(注意相交的元素分数相加)
		Long unionAndStore = opsForZSet.unionAndStore("fan14", "fan15", "fan16");
		System.out.println(unionAndStore);//6
		Set<TypedTuple<Object>> rangeWithScores = opsForZSet.rangeWithScores("fan16", 0, -1);
		Iterator<TypedTuple<Object>> iterator = rangeWithScores.iterator();
		while(iterator.hasNext()){
			TypedTuple<Object> next = iterator.next();
			System.out.println("value:"+next.getValue()+" score:"+next.getScore());
			/*
				value:f score:-1.0
				value:a score:1.0
				value:d score:2.0
				value:e score:2.0
				value:b score:3.0
				value:c score:3.0	可以看出，score相加了
			 */
		}
		opsForZSet.add("fan17", "e", 5);
		opsForZSet.add("fan17", "f", -7);
		opsForZSet.add("fan17", "g", 31);
		opsForZSet.add("fan17", "h", -11);
		opsForZSet.add("fan17", "c", -11);
		//计算给定的多个有序集的并集，并存储在新的 destKey中
		Long unionAndStore2 = opsForZSet.unionAndStore("fan14", Arrays.asList("fan15","fan17"), "fan18");
		System.out.println(unionAndStore2);//8
		Set<TypedTuple<Object>> rangeWithScores2 = opsForZSet.rangeWithScores("fan18", 0, -1);
		Iterator<TypedTuple<Object>> iterator2 = rangeWithScores2.iterator();
		while(iterator2.hasNext()){
			TypedTuple<Object> next = iterator2.next();
			System.out.println("value:"+next.getValue()+" score:"+next.getScore());
			/*
				value:h score:-11.0
				value:c score:-8.0
				value:f score:-8.0
				value:a score:1.0
				value:d score:2.0
				value:b score:3.0
				value:e score:7.0
				value:g score:31.0
			 */
		}
	}
	
	@Test
	public void testIntersectAndStore(){
		redisTemplate.delete("fan19");
		redisTemplate.delete("fan20");
		redisTemplate.delete("fan21");
		redisTemplate.delete("fan22");
		redisTemplate.delete("fan23");
		opsForZSet.add("fan19", "a", 1);
		opsForZSet.add("fan19", "b", 3);
		opsForZSet.add("fan19", "c", 2);
		opsForZSet.add("fan19", "d", -1);
		
		opsForZSet.add("fan20", "c", 1);
		opsForZSet.add("fan20", "d", 3);
		opsForZSet.add("fan20", "e", 8);
		opsForZSet.add("fan20", "f", -5);
		
		opsForZSet.add("fan21", "e", 1);
		opsForZSet.add("fan21", "f", 3);
		opsForZSet.add("fan21", "g", 2);
		opsForZSet.add("fan21", "h", -1);
		opsForZSet.add("fan21", "c", 9);
		//计算给定的一个与另一个有序集的交集并将结果集存储在新的有序集合 key 中
		Long intersectAndStore = opsForZSet.intersectAndStore("fan19", "fan20", "fan22");
		System.out.println(intersectAndStore);//2
		Set<TypedTuple<Object>> rangeWithScores = opsForZSet.rangeWithScores("fan22", 0, -1);
		Iterator<TypedTuple<Object>> iterator = rangeWithScores.iterator();
		while(iterator.hasNext()){
			TypedTuple<Object> next = iterator.next();
			System.out.println("value:"+next.getValue()+" score:"+next.getScore());
			/*
				value:d score:2.0
				value:c score:3.0
			 */
		}
		//计算给定的一个或多个有序集的交集并将结果集存储在新的有序集合 key 中
		Long intersectAndStore2 = opsForZSet.intersectAndStore("fan19", Arrays.asList("fan20","fan21"), "fan23");
		System.out.println(intersectAndStore2);//1
		Set<TypedTuple<Object>> rangeWithScores2 = opsForZSet.rangeWithScores("fan23", 0, -1);
		Iterator<TypedTuple<Object>> iterator2 = rangeWithScores2.iterator();
		while(iterator2.hasNext()){
			TypedTuple<Object> next = iterator2.next();
			System.out.println("value:"+next.getValue()+" score:"+next.getScore());
			/*
				value:c score:12.0
			 */
		}
	}
	
	@Test
	public void testScan(){
		redisTemplate.delete("fan24");
		opsForZSet.add("fan24", "a", 1);
		opsForZSet.add("fan24", "b", 3);
		opsForZSet.add("fan24", "c", 2);
		opsForZSet.add("fan24", "d", -1);
		//跟iterator一毛一样，遍历集合
		Cursor<TypedTuple<Object>> scan = opsForZSet.scan("fan24", ScanOptions.NONE);
		while (scan.hasNext()){
			ZSetOperations.TypedTuple<Object> item = scan.next();
			System.out.println(item.getValue() + ":" + item.getScore());
			/*
				d:-1.0
				a:1.0
				c:2.0
				b:3.0
			 */
		}
	}
	
	@Test
	public void testRangeByLex(){
		redisTemplate.delete("fan25");
		opsForZSet.add("fan25", "a", 1);
		opsForZSet.add("fan25", "b", 1);
		opsForZSet.add("fan25", "c", 1);
		opsForZSet.add("fan25", "d", 1);
		opsForZSet.add("fan25", "e", 1);
		RedisZSetCommands.Range range = Range.unbounded();
		Set<Object> rangeByLex = opsForZSet.rangeByLex("fan25", range);
		System.out.println(rangeByLex);//[a, b, c, d, e]
	}
}
