package com.redistest.redis.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.redistest.commons.SerializeUtil;

import redis.clients.jedis.JedisCluster;

@Component("redisCache")
public class RedisCache {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RedisCache.class);

	@Autowired
	private JedisCluster jedisCluster;

	/**
	 * 从Redis缓存获取数据
	 * 
	 * @param redisKey
	 * @return
	 */
	public Object getDataFromRedis(String redisKey) {
		String value = jedisCluster.get(redisKey);
		if (value != null) {
			return SerializeUtil.objectDeserialization(value);
		}
		return null;
	}

	/**
	 * 保存数据到Redis
	 * 
	 * @param redisKey
	 */
	public String saveDataToRedis(String redisKey, Object obj) {
		return jedisCluster.set(redisKey, SerializeUtil.objectSerialiable(obj));
	}
	
	/**
	 * 删除指定key值的缓存
	 * 
	 * @param redisKey
	 * @return 
	 */
	public Long delDataToRedisByKey(String redisKey) {
		return jedisCluster.del(redisKey);
	}
	
	/**
	 * 删除指定key值的缓存
	 * 
	 * @param redisKey
	 * @return 
	 */
	public Long delDataToRedisByKey(String[] redisKey) {
		return jedisCluster.del(redisKey);
	}
}
