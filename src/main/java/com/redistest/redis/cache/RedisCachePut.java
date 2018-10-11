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
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Component;

import com.alibaba.druid.util.StringUtils;
import com.redistest.commons.RedisUtil;

/**
 * 
 * @Description: AOP实现Redis缓存处理CachePut
 *
 * @author yl
 * @date 2018年5月9日
 */
@Component
@Aspect
public class RedisCachePut {

	private static final Logger LOGGER = LoggerFactory.getLogger(RedisCachePut.class);
	@Autowired
	@Qualifier("redisCache")
	private RedisCache redisCache;

	/**
	 * 拦截所有元注解CachePut注解的方法
	 */
	@Pointcut("@annotation(org.springframework.cache.annotation.CachePut)")
	public void pointcutCachePut() {

	}

	/**
	 * 与@Cacheable不同的是使用@CachePut标注的方法在执行前不会去检查缓存中是否存在之前执行过的结果
	 * 而是每次都会执行该方法，并将执行结果以键值对的形式存入指定的缓存中
	 * 
	 * @param joinPoint
	 * @return
	 * @throws Throwable 
	 */
	@Around("pointcutCachePut()")
	public Object aroundCachePut(ProceedingJoinPoint joinPoint) throws Throwable {
		Method method = RedisUtil.getMethod(joinPoint);
		CachePut cachePut = method.getAnnotation(CachePut.class);
		String key = cachePut.key();
		String condition = cachePut.condition();
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
		Object obj = joinPoint.proceed();
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
