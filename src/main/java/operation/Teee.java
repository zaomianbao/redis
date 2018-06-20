package operation;

import java.security.KeyPair;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

public class Teee {
	public static void main(String[] args) {
		@SuppressWarnings("resource")
		ApplicationContext context = new ClassPathXmlApplicationContext("/applicationContext.xml");
		@SuppressWarnings("unchecked")
		RedisTemplate<String,Object> stringTemplate = (RedisTemplate<String,Object>)context.getBean("redisTemplate");
		ValueOperations<String, Object> opsForValue = stringTemplate.opsForValue();
		KeyPair genKeyPair = RSAUtil.genKeyPair();
		opsForValue.set("yncjsso_server_keypair", genKeyPair);
	}
}
