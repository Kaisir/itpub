package com.wisedu.emap.itpub.cache;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Pipeline;
import self.micromagic.util.annotation.Config;

import com.wisedu.emap.base.core.EmapTool;
import com.wisedu.emap.base.core.ExceptionWrapper;

/**
 * Redis缓存工具类
 *
 */
public class RedisUtil
{
    private static final transient Log LOGGER = LogFactory.getLog(RedisUtil.class);

    @Config(name = "redis.enable", description = "redis是否生效")
    private static boolean redisEnable;

    @Config(name = "redis.ip", description = "redis的ip地址")
    private static String redisIP;

    @Config(name = "redis.port", defaultValue = "6379", description = "redis的端口")
    private static int redisPort;

    @Config(name = "redis.timeout", defaultValue = "2000", description = "redis的超时时间")
    private static int redisTimeout;

    @Config(name = "redis.password", description = "redis服务器的密码")
    private static String redisPassword;

    @Config(name = "redis.database", defaultValue = "0", description = "redis的数据库编号")
    private static int redisDatabase;

    @Config(name = "redis.charset", defaultValue = "UTF-8", description = "redis使用的字符集")
    private static String redisCharset;

    private static JedisPool pool;

    static
    {
        EmapTool.bindConfig();
        try{
            init();
        }
        catch (Exception e){
        	LOGGER.error(RedisUtil.class, e);
        }
    }

    private RedisUtil() {
    }

    private static void init()
    {
        if (pool != null)
        {
            return;
        }
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(1000);
        config.setMaxIdle(300);
        config.setMaxWaitMillis(2000L);
        config.setTestOnBorrow(false);
        pool = new JedisPool(config, redisIP, redisPort, redisTimeout, redisPassword, redisDatabase);
    }

    public static void destroy()
    {
        JedisPool tmp = pool;
        if (tmp != null)
        {
            pool = null;
            tmp.destroy();
        }
    }
    /**
     * 释放redis
     * @param redis
     */
    private static void returnJedis(Jedis redis)
    {
    	if(redis != null){
    		redis.close();
    	}
    }

    /**
     * 清空当前DB
     */
    public static void clear()
    {
//    	boolean flag = false;
    	Jedis redis=null;
    	try{
    		redis = getRedisClient();
    		if(redis.isConnected()){
    			redis.flushDB();
//    			flag = true;
    		}
    	}catch(Exception e){
    		LOGGER.error("redis flushDB error.", e);
    	}finally{
    		returnJedis(redis);
    	}
//    	return flag;
    }

    /**
     * 添加缓存
     *
     * @author CJJ
     * @date 2015年11月13日 上午11:07:46
     * @param key
     * @param value
     */
    public static void putCache(String key, Object value)
    {
    	putCacheInternal(key, value);
    }
    /**
     * 带有超时时间的缓存
     * @author CJJ
     * @param key
     * @param seconds 超时时间,单位秒
     * @param value
     */
    public static void setEx(String key,int seconds,String value)
    {
//    	boolean flag = false;
    	Jedis redis = null;
    	try{
    		redis = getRedisClient();
    		if(redis.isConnected()){
    			redis.setex(getBytes(key), seconds, SerializeUtil.serialize(value));
//    			flag = true;
    		}
    	}catch(Exception e){
    		LOGGER.error(RedisUtil.class, e);
    	}finally{
    		 returnJedis(redis);
    	}
//    	return flag;
    }

    /**
     * 获取缓存
     *
     * @author CJJ
     * @param key
     * @return
     */
    public static Object getCache(String key)
    {
    	return getCacheInternal(key);
    }

    /**
     * 移除缓存
     *
     * @author CJJ
     * @param key
     */
    public static void removeCache(String key)
    {
    	 remove(key);
    }

    /**
     * 批量获取缓存
     * @author CJJ
     * @param <T>
     * @param <T>
     * @param key
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static <T> RedisObjList<T> getCaches(Class<T> clazz, String preKey, String... keys)
    {
    	if(keys == null){
    		return null;
    	}
    	String prekeytemp = null == preKey ? StringUtils.EMPTY : preKey;
    	RedisObjList<T> result = new RedisObjList<T>();
    	List<T> list = new ArrayList<T>();
    	List<String> noneList = new ArrayList<String>();
    	Jedis redis = null;
    	try{
    		redis = getRedisClient();
    		if(redis.isConnected()){
    			T ret = null;
    			byte[] obytes = null;
    			for (String key : keys){
    				obytes = redis.get(getBytes(prekeytemp + key));
    				if (obytes == null){
    					noneList.add(key);
    				}else{
    					ret = (T) SerializeUtil.unserialize(obytes);
    					list.add(ret);
    				}
    			}
    		}else{
    			for (String key : keys){
    				noneList.add(key);
    			}
    		}
    	}catch(Exception e){
    		LOGGER.error(RedisUtil.class, e);
    	}finally{
    		returnJedis(redis);
    	}
      result.setObjectList(list);
      result.setNoneList(noneList);
      return result;
    }

    /**
     * 批量删除缓存
     *
     * @author CJJ
     * @param head  key的前置标识
     * @param key
     * @return
     * @throws Exception
     */
    public static void removeCaches(String head,String... keys)
    {
//    	boolean flag=false;
    	if (keys == null){
            return ;
        }
    	Jedis redis = null;
    	try{
    		redis = getRedisClient();
    		if(redis.isConnected()){
    			Pipeline pl = redis.pipelined();
    			for (String key : keys){
    				pl.del(getBytes(head + key));
    			}
    			pl.sync();
//    			flag = true;
    		}
    	}catch(Exception e){
    		LOGGER.error("redis removeCaches error.", e);
    	}finally{
    		returnJedis(redis);
    	}
//    	return flag;
    }

    /**
     *
     * [简要描述]：批量添加缓存
     * [详细描述]：
     *
     * @param updateMap
     */
    public static void upadteBatch(Map<String, Object> updateMap)
    {
//    	boolean flag = false;
        if (null == updateMap){
            return ;
        }
        Jedis redis = null;
        try{
        	redis = getRedisClient();
        	if(redis.isConnected()){
        		Pipeline pl = redis.pipelined();
        		Set<Entry<String, Object>> entrySet = updateMap.entrySet();
        		String key = null;
        		Object object = null;
        		for (Entry<String, Object> entry : entrySet){
        			key = entry.getKey();
        			object = entry.getValue();
        			if (null == object){
        				 continue;
        			}
        			pl.set(getBytes(key), SerializeUtil.serialize(object));
        		}
        		pl.sync();
//        		flag =true;
        	}
        }catch(Exception e){
        	LOGGER.error("redis upadteBatch error.", e);
        }finally{
        	returnJedis(redis);
        }
//        return flag;
    }


    /**
     * 缓存加带有失效时间锁
     * @param key
     * @param seconds(秒)
     * @param value
     * @return
     */
    public static boolean lockWithExpire(String key,int seconds,String value){
    	boolean flag = false;
    	Jedis redis = null;
    	try{
    		redis = getRedisClient();
    		if(redis.isConnected()){
    			String result = redis.set(key, value, "NX", "EX", seconds);
    			if(!StringUtils.isEmpty(result)){
    				flag = true;
        		}
    		}
    	}catch(Exception e){
			LOGGER.error(RedisUtil.class, e);
			return flag;
    	}finally{
    		returnJedis(redis);
    	}
    	return flag;
    }

    /**
     * 添加缓存失效时间，已有不会重新指定
     * @param key
     * @param seconds(秒)
     */
    public static long expire(String key, int seconds)
    {
    	return expireInternal(key,seconds);
    }

    private static long expireInternal(String key, int seconds)
    {
    	Jedis redis = null;
    	Long losetime = -99l;
    	try{
    		redis = getRedisClient();
    		if(redis.isConnected()){
    			losetime = redis.ttl(key);
    			if(losetime==-1){
    				losetime = redis.expire(getBytes(key), seconds);
    			}
    			if(losetime==-2){
    				redis.set(key, "lock", "NX", "EX", seconds);
    				losetime = (long)seconds;
    			}
    		}
    	}catch(Exception e){
    		LOGGER.error("redis expireInternal error.", e);
    	}finally{
    		returnJedis(redis);
    	}
    	return losetime;
    }

    /**
     * 添加缓存计数器
     * @param key
     * @param value
     */
    public static long incrCache(String key, Long value)
    {
    	return incrCacheInternal(key,value);
    }

    /**
     * 批量添加或重置指定值缓存计数器
     * @param key
     * @param value
     */
    public static void batchResetIncr(Map<String,Long> updateMap)
    {
//    	boolean flag = false;
    	if (null == updateMap){
            return ;
        }
    	Jedis redis = null;
    	try{
    		redis = getRedisClient();
    		if(redis.isConnected()){
    			Pipeline pl = redis.pipelined();
    			Set<Entry<String, Long>> entrySet = updateMap.entrySet();
    			String key = null;
    			Long value = null;
    			for (Entry<String, Long> entry : entrySet){
    				key = entry.getKey();
    				value = entry.getValue();
    				if (null == value){
    					value = 0L;
    				}
    				pl.del(key);
    				pl.incrBy(key, value);
    			}
    			pl.sync();
//    			flag = true;
    		}
    	}catch(Exception e){
    		LOGGER.error("redis batchResetIncr error.", e);
    	}finally{
    		returnJedis(redis);
    	}
//    	return flag;
    }

    /**
     * 读取缓存计数器
     * @param key
     * @return
     */
    public static String getIncr(String key)
    {
    	return getIncrInternal(key);
    }

    private static long incrCacheInternal(String key, Long value)
    {
    	long result = -99l;
    	Jedis redis = null;
    	try{
    		redis = getRedisClient();
    		if(redis.isConnected()){
    			if (value == null){
    				result = redis.incr(key);
    			}else if(value<0){
    				result = redis.decrBy(key, value*-1);
    			}else if(value>0){
    				result = redis.incrBy(key, value);
    			}
    		}
    	}catch(Exception e){
    		LOGGER.error("redis incrCacheInternal error.", e);
    	}finally{
    		returnJedis(redis);
    	}
    	return result;
    }

    private static String getIncrInternal(String key)
    {
    	String ret = null;
    	Jedis redis = null;
    	try{
    		redis = getRedisClient();
    		if(redis.isConnected()){
    			ret = redis.get(key);
    		}
    	}catch(Exception e){
    		LOGGER.error("redis getIncrInternal error.", e);
    	}finally{
    		returnJedis(redis);
    	}
    	return ret;
    }
    /**
     * 判断redis是否可用
     *
     * @author CJJ
     * @return
     */
    public static boolean isEnabled()
    {
        return getRedisEnable();
    }

    private static boolean getRedisEnable()
    {
        boolean result = false;
        if (redisEnable)
        {
            try
            {
                Jedis redis = getRedisClient();
                try{
                    result = redis.isConnected();
                }
                finally
                {
                    returnJedis(redis);
                }
            }
            catch (Exception ex)
            {
            	LOGGER.error("Check redis error.", ex);
                redisEnable = false;
                return false;
            }
        }
        return result;
    }

    private static Jedis getRedisClient()
    {
        Jedis jedis = null;
        try {
            jedis = pool.getResource(); 
        }
        catch (Exception e) {
            LOGGER.error(RedisUtil.class, e);
        }
        return jedis;
    }

    private static void putCacheInternal(String key, Object value)
    {
//    	boolean flag = false;
    	if (value == null){
            return ;
        }
        Jedis redis = null;
        try{
        	redis = getRedisClient();
        	if(redis != null && redis.isConnected()){
        		redis.set(getBytes(key), SerializeUtil.serialize(value));
//        		flag = true;
        	}
        }
        catch (Exception e)
        {
        	LOGGER.error("redis set value error.", e);
        }
        finally
        {
            returnJedis(redis);
        }
//        return flag;
    }

    private static Object getCacheInternal(String key)
    {
        Jedis redis = getRedisClient();
        Object ret = null;
        try
        {
            byte[] obytes = redis.get(getBytes(key));
            if (null == obytes)
            {
                return obytes;
            }
            ret = SerializeUtil.unserialize(obytes);
        }
        catch (Exception e)
        {
        	LOGGER.error("redis get value error.", e);
        }
        finally
        {
            returnJedis(redis);
        }
        return ret;
    }

    private static void remove(String key)
    {
//    	boolean flag = false;
    	Jedis redis = null;
    	try{
    		redis = getRedisClient();
    		if(redis.isConnected()){
    			redis.del(getBytes(key));
//    			flag = true;
    		}
    	}catch(Exception e){
    		LOGGER.error("redis delete value error.", e);
    	}finally{
    		returnJedis(redis);
    	}
//    	return flag;
    }

    public static Object searchKeys(String key)
    {
    	return getKeysByHead(key);
    }

    private static Object getKeysByHead(String key)
    {
    	Set<String> set = new HashSet<String>();
    	Jedis redis = null;
    	try{
    		redis = getRedisClient();
    		if(redis.isConnected()){
    			set = redis.keys(key);
    		}
    	}catch(Exception e){
    		LOGGER.error("redis get keys error.", e);
    	}finally{
    		returnJedis(redis);
    	}
    	 return set;
    }


    private static byte[] getBytes(String value)
    {
        try{
            return value.getBytes(redisCharset);
        }
        catch (UnsupportedEncodingException e)
        {
            throw new ExceptionWrapper(e);
        }
    }
}
