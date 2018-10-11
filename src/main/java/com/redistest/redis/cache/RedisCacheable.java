package com.redistest.redis.cache;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.alibaba.druid.util.StringUtils;
import com.redistest.commons.RedisUtil;

/**
 * 
 * @Description: AOP实现Redis缓存处理Cacheable
 *
 * @author yl
 * @date 2018年5月9日
 */
@Component
@Aspect
public class RedisCacheable {

	private static final Logger LOGGER = LoggerFactory.getLogger(RedisCacheable.class);
	@Autowired
	@Qualifier("redisCache")
	private RedisCache redisCache;

	/**
	 * 拦截所有元注解CacheEvict注解的方法
	 */
	@Pointcut("@annotation(org.springframework.cache.annotation.Cacheable)")
	public void pointcutCacheable() {

	}

	/**
	 * 环绕处理，在其被调用后将其返回值缓存起来，以保证下次利用同样的参数来执行该方法时可以直接从缓存中获取结果，而不需要再次执行该方法
	 * 
	 * @param joinPoint
	 * @return
	 * @throws Throwable 
	 */
	@Around("pointcutCacheable()")
	public Object aroundCacheable(ProceedingJoinPoint joinPoint) throws Throwable {
		LOGGER.info("**********pointcutCacheable执行**********");
		Method method = RedisUtil.getMethod(joinPoint);
		Cacheable cacheable = method.getAnnotation(Cacheable.class);
		String key = cacheable.key();
		String condition = cacheable.condition();
		if(!StringUtils.isEmpty(condition)){
			condition = RedisUtil.parseKey(condition,method,joinPoint.getArgs());
		}
		LOGGER.info("cacheable.condition()="+condition);
		if(StringUtils.isEmpty(key)){
			//如果key为空，重新生成缓存key
			key = RedisUtil.keyGenerator(joinPoint);
		}else{
			key = RedisUtil.parseKey(key,method,joinPoint.getArgs());
		}
		LOGGER.info("cacheable.key()="+key);
		Object obj = redisCache.getDataFromRedis(key);
		if (obj != null) {
			LOGGER.info("**********从Redis中查到了数据**********");
			LOGGER.info("Redis的KEY值:" + key);
			LOGGER.info("REDIS的VALUE值:" + obj.toString());
			return obj;
		}
		LOGGER.info("**********从Redis中不存在数据**********");
		obj = joinPoint.proceed();
		LOGGER.info("**********开始将数据保存到Redis缓存**********");
		if(!condition.equals("false") && key != null){
			String code = redisCache.saveDataToRedis(key, obj);
			if (code.equals("OK")) {
				LOGGER.info("**********数据成功保存到Redis缓存!!!**********");
				LOGGER.info("Redis的KEY值:" + key);
				LOGGER.info("REDIS的VALUE值:" + obj.toString());
			}
		}
		return obj;
	}
}
