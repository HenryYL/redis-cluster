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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

import com.alibaba.druid.util.StringUtils;
import com.redistest.commons.RedisUtil;

/**
 * 
 * @Description: AOP实现Redis缓存处理CacheEvict
 *
 * @author yl
 * @date 2018年5月9日
 */
@Component
@Aspect
public class RedisCacheEvict {

	private static final Logger LOGGER = LoggerFactory.getLogger(RedisCacheEvict.class);
	@Autowired
	@Qualifier("redisCache")
	private RedisCache redisCache;

	/**
	 * 拦截所有元注解CacheEvict注解的方法
	 */
	@Pointcut("@annotation(org.springframework.cache.annotation.CacheEvict)")
	public void pointcutCacheEvict() {

	}

	/**
	 * CacheEvict是用来标注在需要清除缓存元素的方法或类上的
	 * 
	 * @param joinPoint
	 * @return
	 * @throws Throwable 
	 */
	@Around("pointcutCacheEvict()")
	public Object aroundCacheEvict(ProceedingJoinPoint joinPoint) throws Throwable {
		Method method = RedisUtil.getMethod(joinPoint);
		CacheEvict cacheEvict = method.getAnnotation(CacheEvict.class);
		String key = cacheEvict.key();
		String condition = cacheEvict.condition();
		boolean beforeInvocation = cacheEvict.beforeInvocation();
		if(!StringUtils.isEmpty(condition)){
			condition = RedisUtil.parseKey(condition,method,joinPoint.getArgs());
		}
		LOGGER.info("CacheEvict.condition()="+condition);
		if(StringUtils.isEmpty(key)){
			//如果key为空，重新生成缓存key
			key = RedisUtil.keyGenerator(joinPoint);
		}else{
			key = RedisUtil.parseKey(key,method,joinPoint.getArgs());
		}
		LOGGER.info("cacheEvict.key()="+key);
		if(beforeInvocation && !condition.equals("false") && key !=null ){
			Long code = redisCache.delDataToRedisByKey(key);
			if (code>0) {
				LOGGER.info("**********beforeInvocation为true数据删除缓存成功!!!**********");
				LOGGER.info("Redis的KEY值:" + key);
			}
		}
		Object obj = joinPoint.proceed();
		if(!condition.equals("false") && key !=null){
			Long code = redisCache.delDataToRedisByKey(key);
			if (code>0) {
				LOGGER.info("**********数据删除缓存成功!!!**********");
				LOGGER.info("Redis的KEY值:" + key);
			}
		}
		return obj;
	}
}
