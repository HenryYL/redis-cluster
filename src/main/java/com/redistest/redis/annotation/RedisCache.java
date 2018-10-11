package com.redistest.redis.annotation;

import java.lang.annotation.*;
/**
 * 
 * @Description: 自定义缓存
 *
 * @author yl
 * @date 2018年5月9日
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisCache {

}