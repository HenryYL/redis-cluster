package com.redistest.data.controller;

import org.apache.commons.lang.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 
 * @Description: 缓存测试类
 *
 * @author yl
 * @date 2018年4月23日
 */
@RestController
public class testController {

	private static final Logger LOGGER = LoggerFactory.getLogger(testController.class);

	/**
	 * Cacheable 缓存对象
	 * @param id
	 * @return
	 */
	@RequestMapping("/redis/Cacheable")
	@Cacheable(value="testCacheable",key="#id")
	public Testbean redisTest(Integer id) {
		Testbean b = new Testbean();
		b.setUserage(id.toString());
		b.setUsername("张三");
		return b;
	}
	
	/**
	 * Cacheable 缓存字符串
	 * @param id
	 * @return
	 */
	@RequestMapping("/redis/CacheableString")
	@Cacheable(value="testCacheable",key="#userName")
	public String CacheableString(String userName) {
		LOGGER.info("======================>缓存成功");
		return userName+RandomUtils.nextInt();
	}
	
	/**
	 * 根据指定保存缓存
	 * @param id
	 */
	@RequestMapping("/redis/CachePut")
	@CachePut(value="testCachePut",key="#id")
	public String CachePut(String id) {
		LOGGER.info("======================>保存成功");
		return id+RandomUtils.nextInt();
	}
	
	/**
	 * 根据指定key删除缓存
	 * @param id
	 */
	@RequestMapping("/redis/CacheEvict")
	@CacheEvict(value="testCacheEvict",key="#id")
	public String CacheEvict(Integer id) {
		LOGGER.info("======================>删除成功");
		return ""+RandomUtils.nextInt();
	}
	
	/**
	 * 根据指定key删除缓存
	 * @param id
	 */
	@RequestMapping("/redis/CacheEvict1")
	@CacheEvict(value="testCacheEvict",key="#bb.userage")
	public void getinfo(Testbean bb) {
		LOGGER.info("======================>getinfo");
	}
}