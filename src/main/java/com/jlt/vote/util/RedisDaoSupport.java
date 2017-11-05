package com.jlt.vote.util;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 通用缓存工具类
 * @Author gaoyan
 * @Date: 2017/7/16
 */
@Component
public class RedisDaoSupport {

	@Autowired
	private RedisTemplate<String,Object> redisTemplate;

	/**
	 * 删除缓存<br>
	 * 根据key精确匹配删除
	 * @param key
	 */
	public void del(String... key){
		if(key!=null && key.length > 0){
			if(key.length == 1){
				redisTemplate.delete(key[0]);
			}else{
				redisTemplate.delete(CollectionUtils.arrayToList(key));
			}
		}
	}
	
	/**
	 * 批量删除<br>
	 * （该操作会执行模糊查询，请尽量不要使用，以免影响性能或误删）
	 * @param pattern
	 */	
	public void batchDel(String... pattern){
		for (String kp : pattern) {
			redisTemplate.delete(redisTemplate.keys(kp + "*"));
		}
	}
	
	/**
	 * 取得缓存（int型）
	 * @param key
	 * @return
	 */
	public Integer getInt(String key){
		Object valueObject = get(key);
		if(Objects.nonNull(valueObject)){
			String value = String.valueOf(valueObject);
			if(StringUtils.isNotBlank(value)){
				return Integer.valueOf(value);
			}
		}
		return null;
	}
	
	/**
	 * 取得缓存（字符串类型）
	 * @param key
	 * @return
	 */
	public Object get(String key){
		return redisTemplate.boundValueOps(key).get();
	}

	/**
	 * 取得缓存（字符串类型）
	 * @param key
	 * @return
	 */
	public <T> T get(String key,Class<T> tClass){
		return (T) redisTemplate.boundValueOps(key).get();
	}

	/**
	 * 取得缓存（List类型）
	 * @param key
	 * @return
	 */
	public <T> List<T> getList(String key,Class<T> tClass){
		return (List<T>) get(key);
	}
	
	/**
	 * 将value对象写入缓存
	 * @param key
	 * @param value
	 * @param expireTime 失效时间(秒)
	 */
	public void set(String key,Object value,long  expireTime){
		redisTemplate.opsForValue().set(key, value);
		if(expireTime > 0L){
			redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
		}
	}

	/**
	 * 将value对象写入缓存
	 * @param key
	 * @param value
	 */
	public void set(String key,Object value){
		set(key,value,0L);
		redisTemplate.opsForValue().set(key, value,0);
	}

	/**
	 * 递减操作
	 * @param key
	 * @param by
	 * @return
	 */
	public double decr(String key, long by){
		return redisTemplate.opsForValue().increment(key, -by);
	}
	
	/**
	 * 递增操作
	 * @param key
	 * @param by
	 * @return
	 */
	public double incr(String key, long by){
		return redisTemplate.opsForValue().increment(key, by);
	}
	

	/**
	 * 设置Int类型值
	 * @param key
	 * @param value
	 * @param expireTime 失效时间(秒)
	 */
	public void setInt(String key, int value,long  expireTime) {
		set(key,value,expireTime);
	}

	/**
	 * 设置Int类型值
	 * @param key
	 * @param value
	 */
	public void setInt(String key, int value) {
		set(key,value,0);
	}
	
	/**
	 * 将map写入缓存
	 * @param key
	 * @param map
	 */
	public <T> void hmset(String key, Map<String, T> map){
		redisTemplate.opsForHash().putAll(key, map);
	}

	/**
	 * 向key对应的map中添加缓存对象
	 * @param key	cache对象key
	 * @param field map对应的key
	 * @param value 	值
	 */
	public void hset(String key, Object field, String value){
		redisTemplate.opsForHash().put(key, field, value);
	}

	/**
	 * 获取map缓存
	 * @param key
	 * @return
	 */
	public <T> Map<String, T> hgetAll(String key){
		BoundHashOperations<String, String, T> boundHashOperations = redisTemplate.boundHashOps(key);
		return boundHashOperations.entries();
	}

	/**
	 * 获取map缓存中的某个对象
	 * @param key
	 * @param field
	 * @return
	 */
	public <T> T hget(String key, String field){
		BoundHashOperations<String, String, T> boundHashOperations = redisTemplate.boundHashOps(key);
		return boundHashOperations.get(field);
	}

	/**
	 * map缓存对象累加数据
	 * @param key
	 * @param field
	 * @param value
	 * @return
	 */
	public Long hinc(String key, String field, long value){
		return redisTemplate.opsForHash().increment(key,field,value);
	}

	/**
	 * 删除map中的某个对象
	 * @author lh
	 * @date 2016年8月10日
	 * @param key	map对应的key
	 * @param field	map中该对象的key
	 */
	public void hdelField(String key, String... field){
		BoundHashOperations<String, String, ?> boundHashOperations = redisTemplate.boundHashOps(key);
		boundHashOperations.delete(field);
	}

	/**
	 * 指定缓存的失效时间
	 *
	 * @author FangJun
	 * @date 2016年8月14日
	 * @param key 缓存KEY
	 * @param expireTime 失效时间(秒)
	 */
	public void expire(String key, long  expireTime) {
		if(expireTime > 0){
			redisTemplate.expire(key, expireTime, TimeUnit.MILLISECONDS);
		}
	}
	
	/**
	 * 添加set
	 * @param key
	 * @param value
	 */
	public void sadd(String key, Object... value) {
		redisTemplate.boundSetOps(key).add(value);
	}

	/**
	 * 删除set集合中的对象
	 * @param key
	 * @param value
	 */
	public void srem(String key, Object... value) {
		redisTemplate.boundSetOps(key).remove(value);
	}
	
	/**
	 * set重命名
	 * @param oldkey
	 * @param newkey
	 */
	public void rename(String oldkey, String newkey){
		redisTemplate.boundSetOps(oldkey).rename(newkey);
	}

	/**
	 * zadd处理
	 * @param key
	 * @param member
	 * @param score
	 */
	public void zadd(String key, String member, double score) {
		redisTemplate.opsForZSet().add(key, member, score);
	}

	/**
	 * 元素分数增加，increment是增量
	 * @param key
	 * @param member
	 * @param increment
	 */
	public Double zincrby(String key, String member, double increment) {
		return redisTemplate.opsForZSet().incrementScore(key,member,increment);
	}

	/**
	 * 键为key的集合，offerset和count是限制条件，从索引1开始找到count个元素=子集，从子集中找到min<=score<=max的元素集合，返回泛型接口（包括score和value），倒序
	 * @param key
	 * @param min
	 * @param max
	 * @param offset
	 * @param count
	 * @return
	 */
	public Set<ZSetOperations.TypedTuple<Object>> zrevrangeByScoreWithScores(String key, final double min, final double max, final int offset, final int count) {
		return redisTemplate.opsForZSet().reverseRangeByScoreWithScores(key, min, max, offset,count);
	}

	/**
	 * 键为key的集合，offerset和count是限制条件，从索引1开始找到count个元素=子集，从子集中找到min<=score<=max的元素集合，返回Set<V>，倒序
	 * @param key
	 * @param min
	 * @param max
	 * @param offset
	 * @param count
	 * @return
	 */
	public Set<Object> zrevrangeByScore(String key, final double min, final double max, final int offset, final int count) {
		return redisTemplate.opsForZSet().reverseRangeByScore(key, min, max, offset,count);
	}

	/**
	 * 键为key的集合，索引offset<=index<=(offset + count)的元素子集，正序
	 * @param key
	 * @param offset
	 * @param count
	 * @return
	 */
	public Set<Object> zrangeByOffset(String key, int offset, int count) {
		return redisTemplate.opsForZSet().range(key, offset, offset + count);
	}

	/**
	 * 键为key的集合，从子集中找到min<=score<=max的元素集合，返回Set<V>，正序
	 * @param key
	 * @param min
	 * @param max
	 * @return
	 */
	public Set<Object> zrangeByScore(String key, double min, double max)  {
		return redisTemplate.opsForZSet().rangeByScore(key, min, max);
	}

	/**
	 * 键为key的集合，offerset和count是限制条件，从索引1开始找到count个元素=子集，从子集中找到min<=score<=max的元素集合，返回Set<V>，正序
	 * @param key
	 * @param min
	 * @param max
	 * @param offset
	 * @param count
	 * @return
	 */
	public Set<Object> zrangeByScore(String key, double min, double max, int offset, int count) {
		return redisTemplate.opsForZSet().rangeByScore(key, min, max,offset, count);

	}

	/**
	 * 删除，键为key的集合，min<=score<=max的元素，返回删除个数
	 * @param key
	 * @param min
	 * @param max
	 * @return
	 */
	public Long zremrangeByScore(String key, double min, double max) {
		return redisTemplate.opsForZSet().removeRangeByScore(key, min, max);
	}

	/**
	 * 键为key的集合，value为member的元素分数
	 * @param key
	 * @param member
	 * @return
	 */
	public Double zscore(String key, String member) {
		return redisTemplate.opsForZSet().score(key,member);
	}

	/**
	 * 键为key的集合，min<=score<=max
	 * @param key
	 * @param min
	 * @param max
	 * @return
	 */
	public Long zcount(String key, double min, double max) {
		return redisTemplate.opsForZSet().count(key,min,max);
	}

	/**
	 * 键为key的集合元素个数
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public Long zcard(String key) throws Exception {
		return redisTemplate.opsForZSet().size(key);
	}


	/**
	 * 模糊查询keys
	 * @param pattern
	 * @return
	 */
	public Set<String> keys(String pattern){
		return redisTemplate.keys(pattern);	
	}
	
}
