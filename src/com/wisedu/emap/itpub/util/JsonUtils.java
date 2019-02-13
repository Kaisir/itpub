package com.wisedu.emap.itpub.util;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

/**
 * json工具类
 * 
 * @filename JsonUtils.java
 * @date 2016年4月13日 下午5:33:26
 * @author wjfu 01116035
 */
@Slf4j
public class JsonUtils {

	private JsonUtils() {
	}

	/**
	 * 创建gson对象
	 */
	private static Gson gson = new Gson();

	/**
	 * 创建一个解析对象 将字符串或者读入流转换成json
	 */
	private static JsonParser parser = new JsonParser();

	/**
	 * 获取json的map解析
	 * 
	 * @date 2016年4月13日 下午5:34:49
	 * @author wjfu 01116035
	 * @param str
	 * @return
	 */
	public static Map<String, Object> json2Map(String str) {
		Map<String, Object> map = gson.fromJson(str, new TypeToken<Map<String, Object>>() {
		}.getType());
		return null == map ? Maps.<String, Object> newHashMap() : map;
	}

	/**
	 * 获取指定类型的json数据 调用方式 JsonUtils.<List<String>>json2Type("['1', '2']");
	 * 类型不匹配时候会抛出异常
	 * 
	 * @date 2016年5月23日 上午11:35:04
	 * @author wjfu 01116035
	 * @param str
	 * @return
	 */
	public static <T> T json2Type(String str) {
		return gson.fromJson(str, new TypeToken<T>() {
		}.getType());
	}

	/**
	 * 将对象转换为json字符串
	 * 
	 * @date 2016年4月13日 下午6:43:54
	 * @author wjfu 01116035
	 * @param obj
	 * @return
	 */
	public static String toJsonStr(Object obj) {
		return gson.toJson(obj);
	}

	/**
	 * 将对象转换成json对象元素
	 * 
	 * @date 2016年4月19日 上午10:36:26
	 * @author wjfu 01116035
	 * @param obj
	 * @return
	 */
	public static JsonElement toJsonElement(Object obj) {
		return gson.toJsonTree(obj);
	}

	/**
	 * 将传入到json字符串转换成List<Map<String, Object>>类型
	 * 
	 * @date 2016年5月6日 下午1:49:47
	 * @author wjfu 01116035
	 * @param str
	 * @return
	 */
	public static List<Map<String, Object>> toListMap(String str) {
		try {
			return gson.fromJson(str, new TypeToken<List<Map<String, Object>>>() {
			}.getType());
		} catch (Exception e) {
			log.error("JSON 转换异常: " + e.getMessage() + " SOURCE: " + str, e);
		}
		return Lists.newArrayList();
	}

	/**
	 * 返回指定的javaBean类型
	 * 
	 * @date 2016年6月23日 上午11:15:18
	 * @author wjfu 01116035
	 * @param str
	 * @param clazz
	 * @return
	 */
	public static <T> T json2Bean(String str, Class<T> clazz) {
		return gson.fromJson(str, clazz);
	}

	/**
	 * 此方法返回一个List<T>为实体集合 如不能转换则返回空集合
	 * 
	 * @date 2016年10月9日 上午11:07:31
	 * @author wjfu 01116035
	 * @param gsonStr
	 * @param typeOfT
	 * @return
	 */
	public static <T> List<T> json2BeanList(String gsonStr, Type typeOfT) {
		try {
			return gson.fromJson(gsonStr, typeOfT);
		} catch (JsonSyntaxException e) {
			log.error("GSON PARSE ERROR WITH : 【" + e.getMessage() + "】 SOURCE STRING : 【" + gsonStr + "】", e);
		}
		return Lists.newArrayList();
	}

	/**
	 * 将传入的src(String) 转换为JsonArray类型 传入的src必须是[]包含的数组类型的Array字符串
	 * 
	 * @date 2016年7月27日 上午9:59:01
	 * @author wjfu 01116035
	 * @param src
	 * @return
	 * @throws Exception
	 */
	public static JsonArray parse2JsonArray(String src) throws Exception {
		return parser.parse(src).getAsJsonArray();
	}
}
