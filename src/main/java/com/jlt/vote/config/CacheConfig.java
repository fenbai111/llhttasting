package com.jlt.vote.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import java.lang.reflect.Method;

/**
 * 缓存管理（注解用）
 *
 * @author Administrator
 */
@Configuration
@EnableCaching
public class CacheConfig extends CachingConfigurerSupport {

    /**
     * 生成key的策略
     *
     * @return
     */
    @Bean
    public KeyGenerator keyGenerator() {
        return new KeyGenerator() {
            @Override
            public Object generate(Object target, Method method, Object... params) {
                StringBuilder sb = new StringBuilder();
                sb.append(target.getClass().getName());
                sb.append(method.getName());
                for (Object obj : params) {
                    sb.append(obj.toString());
                }
                return sb.toString();
            }
        };
    }

    /**
     * 管理缓存
     *
     * @param redisTemplate
     * @return
     */
    @Bean
    public CacheManager cacheManager(RedisTemplate redisTemplate) {
        return new RedisCacheManager(redisTemplate);
        //设置缓存过期时间
        //rcm.setDefaultExpiration(60);//秒
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        if (connectionFactory instanceof JedisConnectionFactory) {
            JedisConnectionFactory jedisConnectionFactory = (JedisConnectionFactory) connectionFactory;
            jedisConnectionFactory.getPoolConfig().setTestOnBorrow(true);
            jedisConnectionFactory.getPoolConfig().setTestWhileIdle(true);
            jedisConnectionFactory.getPoolConfig().setMinEvictableIdleTimeMillis(60000L);
            jedisConnectionFactory.getPoolConfig().setTimeBetweenEvictionRunsMillis(30000L);
            jedisConnectionFactory.getPoolConfig().setNumTestsPerEvictionRun(-1);
        }
        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(connectionFactory);
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(
                Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        template.setKeySerializer(template.getStringSerializer());
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();
        return template;
    }

//    /**
//     * RedisTemplate配置
//     * @param factory
//     * @return
//     */
//    @Bean
//    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {
//        if(factory instanceof JedisConnectionFactory){
//            JedisConnectionFactory jedisConnectionFactory = (JedisConnectionFactory) factory;
//            jedisConnectionFactory.getPoolConfig().setTestOnBorrow(true);
//            jedisConnectionFactory.getPoolConfig().setTestWhileIdle(true);
//            jedisConnectionFactory.getPoolConfig().setMinEvictableIdleTimeMillis(60000L);
//            jedisConnectionFactory.getPoolConfig().setTimeBetweenEvictionRunsMillis(30000L);
//            jedisConnectionFactory.getPoolConfig().setNumTestsPerEvictionRun(-1);
//        }
//        StringRedisTemplate template = new StringRedisTemplate(factory);
//        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
//        ObjectMapper om = new ObjectMapper();
//        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
//        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
//        jackson2JsonRedisSerializer.setObjectMapper(om);
//        template.setValueSerializer(jackson2JsonRedisSerializer);//如果key是String 需要配置一下StringSerializer,不然key会乱码 /XX/XX
//        template.setHashValueSerializer(jackson2JsonRedisSerializer);
//        template.afterPropertiesSet();
//        return template;
//    }


}