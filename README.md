# redis-cluster
自定义缓存注解实现

# 项目架构
 - Springboot + redis + influxdb
 - 因为之前学习influxdb所以将influxdb配置加入了项目中，但并未实际去使用，后续会完善

# AOP实现Redis缓存处理介绍
其中RedisAspect类拦截所有元注解RedisCache注解的方法，代码中会有详细注释
 - RedisCacheable拦截所有元注解Cacheable注解的方法
 - RedisCacheEvict拦截所有元注解CacheEvict注解的方法
 - RedisCachePut拦截所有元注解CachePut注解的方法
 各个注解方法就不一一详细介绍了，后续大家使用如有修改，可以去对应的类文件中查找

# 配置文件
项目配置均在application.yml 配置文件中，启动后请自行更改端口号和应用上下文，如下：
server:
    tomcat:
        uri-encoding: UTF-8
        max-threads: 1000
        min-spare-threads: 30
    port: 11122
    context-path: /redis
 数据库配置在application-dev.yml中，dev为测试文件，pro为正式环境文件，启动时可自行选择。
