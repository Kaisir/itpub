package com.wisedu.emap.itpub.bean.cache;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.wisedu.emap.base.util.StringUtil;
import com.wisedu.emap.itpub.bean.BizException;
import com.wisedu.emap.itpub.bean.auth.AuthModule;
import com.wisedu.emap.itpub.util.DbUtil;
import com.wisedu.emap.itpub.util.HttpUtils;
import com.wisedu.emap.itpub.util.JsonUtils;

/**
 * 单例缓存对象 服务器单一持有
 * 
 * @filename CacheManager.java
 * @date 2016年7月12日 下午3:51:03
 * @author wjfu 01116035
 */
@Slf4j
public class CacheManager {

	private CacheManager() {
	}

	/**
	 * 获取单例的缓存对象
	 * 
	 * @date 2016年7月12日 下午3:54:02
	 * @author wjfu 01116035
	 * @return
	 */
	public static CacheManager getInstance() {
		return CacheManagerHolder.INSTANCE;
	}

	private static class CacheManagerHolder {
		private CacheManagerHolder() {
		}

		private static CacheManager INSTANCE = new CacheManager();
	}

	private Cache<String, String> cache = CacheBuilder.newBuilder().expireAfterWrite(24 * 60 * 60, TimeUnit.SECONDS)
			.build();

	private LoadingCache<String, String> propsCache = CacheBuilder.newBuilder()
			.expireAfterWrite(24 * 60 * 60, TimeUnit.SECONDS).build(new CacheLoader<String, String>() {
				@Override
				public String load(String key) throws Exception {
					Object[] args = key.split(";");
					Object mrSettingConf = DbUtil.queryOne("SELECT VALUE FROM T_ITPUB_PROPS WHERE KEY = ? AND XT = ?",
							args);
					String conf = StringUtil.getString(mrSettingConf);
					return conf;
				}

			});

	private Cache<String, Long> ipCache = CacheBuilder.newBuilder().expireAfterWrite(20 * 60, TimeUnit.SECONDS).build();

	private LoadingCache<String, Map<String, AuthModule>> appModuleCache = CacheBuilder.newBuilder()
			.build(new CacheLoader<String, Map<String, AuthModule>>() {
				@Override
				@SuppressWarnings("unchecked")
				public Map<String, AuthModule> load(String key) throws Exception {
					Map<String, AuthModule> authItemMap = Maps.newHashMap();
					Document appDoc = null;
					String appInfoPath = HttpUtils.getRequestBaseUrl() + key + "/app_info.xml";
					log.info("[app_info.xml] LOAD PATH : " + appInfoPath);
					try {
						appDoc = new SAXReader().read(appInfoPath);
					} catch (DocumentException e) {
						log.error(" LOAD [app_info.xml] EXCEPTION WITH PATH [" + appInfoPath + "] EXCEPTION : "
								+ e.getMessage(), e);
						return authItemMap;
					}
					Element rootEle = appDoc.getRootElement();
					List<Node> nodes = rootEle.selectNodes("//authItems/authItem");
					for (Node node : nodes) {
						Node itemIdNode = node.selectSingleNode("./itemId");
						if (itemIdNode == null) {
							continue;
						}
						Node itemNameNode = node.selectSingleNode("./itemName");
						Node itemPathNode = node.selectSingleNode("./itemPath");
						AuthModule authModule = new AuthModule();
						authModule.setModuleName(itemNameNode == null ? "" : itemNameNode.getText());
						authModule.setModulePath(itemPathNode == null ? "" : itemPathNode.getText());
						authItemMap.put(itemIdNode.getText(), authModule);
					}
					log.info(" POST APP INFO RESULT " + JsonUtils.toJsonStr(authItemMap));
					return authItemMap;
				}
			});

	public Cache<String, Long> getIpCache() {
		return ipCache;
	}

	/**
	 * 获取缓存的应用模块授权信息
	 * 
	 * @date 2016年12月8日 下午5:18:50
	 * @author wjfu 01116035
	 * @param key
	 * @return
	 */
	public Map<String, AuthModule> getAppModuleMap(String key) {
		try {
			return appModuleCache.get(key);
		} catch (Exception e) {
			log.error("GET APP AUTH INFO ERROR WITH " + e.getMessage(), e);
		}
		return null;
	}

	/**
	 * 创建EMAP缓存对象
	 * 
	 * @date 2016年7月12日 下午3:55:10
	 * @author wjfu 01116035
	 * @param cacheMap
	 *            缓存的存放
	 * @param liveTime
	 *            缓存存在时间
	 */
	public void createCache(final Map<String, String> cacheMap, int liveTime) {
		// AppBeanContainer<ICacheFactory> factory = new
		// AppBeanContainer<ICacheFactory>("emapcomponent",
		// ICacheFactory.BEANID, true);
		// this.cache = factory.get().getMultipleCache("itservice_cache",
		// CacheType.LOCAL, new IMultipleValueHandler<String>() {
		// @Override
		// public String get(String key) {
		// return cacheMap.get(key);
		// }
		// }, liveTime, liveTime, 0);
		this.cache = CacheBuilder.newBuilder().expireAfterWrite(liveTime, TimeUnit.SECONDS).build();
		cache.putAll(cacheMap);
	}

	/**
	 * 获取EMAP的缓存值
	 * 
	 * @date 2016年7月12日 下午3:55:21
	 * @author wjfu 01116035
	 * @param key
	 * @return
	 */
	public String getCache(String key) {
		return cache.getIfPresent(key);
	}

	/**
	 * 清理指定的EMAP缓存
	 * 
	 * @date 2016年7月12日 下午3:55:34
	 * @author wjfu 01116035
	 * @param key
	 */
	public void cacheClear(String key) {
		cache.invalidate(key);
	}

	/**
	 * 获取PROPS的缓存
	 * 
	 * @date 2016年9月7日 上午10:35:10
	 * @author wjfu 01116035
	 * @param key
	 * @return
	 */
	public String getProp(String key) {
		try {
			return propsCache.get(key);
		} catch (ExecutionException e) {
			throw new BizException("无法从数据库获取到当前key的Props信息，key为：" + key, e);
		}
	}

}
