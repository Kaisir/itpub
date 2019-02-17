package com.wisedu.emap.itpub.util;

import com.google.common.collect.Maps;
import com.wisedu.emap.base.util.StringUtil;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 加载T_ITPUB_PROPS表配置信息
 * 
 */
@Slf4j
public class ITPubPropsUtil {

	private ITPubPropsUtil() {
	}

	/**
	 * 系统配置
	 */
	private static Map<String, String> sysProps = Maps.newHashMap();

	/**
	 * 消息配置
	 */
	private static Map<String, String> msgProps = Maps.newHashMap();

	static {
		load();
	}

	static {
		loadMsg();
	}

	/**
	 * 加载数据库中的常量配置
	 * 
	 * @date 2016年7月29日 上午11:24:27
	 * @author wjfu 01116035
	 */
	private static void load() {
		try {
			List<Map<String, Object>> propList = DbUtil
					.defaultEmptyQuery("SELECT KEY, VALUE FROM T_ITPUB_PROPS WHERE XT = ?", new Object[] { "itpub" });
			for (Map<String, Object> prop : propList) {
				sysProps.put(StringUtil.getString(prop.get("KEY")), StringUtil.getString(prop.get("VALUE")));
			}
			log.info("^.^ IT PROPS LOAD SUCCESSFUL!!");
		} catch (Exception e) {
			log.error(">.< LOAD IT PROPERTIES ERROR!!", e);
		}
	}

	/**
	 * 加载数据库中消息通知的配置
	 * 
	 * @date 2016年8月9日 上午10:25:58
	 * @author wjfu 01116035
	 */
	private static void loadMsg() {
		try {
			List<Map<String, Object>> propList = DbUtil
					.defaultEmptyQuery("SELECT KEY, VALUE, BELONG_TO FROM T_ITPUB_PROPS_MSG");
			for (Map<String, Object> prop : propList) {
				msgProps.put(StringUtil.getString(prop.get("KEY")) + StringUtil.getString(prop.get("BELONG_TO")),
						StringUtil.getString(prop.get("VALUE")));
			}
			log.info("^.^ IT MESSAGE PROPS LOAD SUCCESSFUL!!");
		} catch (Exception e) {
			log.error(">.< LOAD IT MESSAGE PROPERTIES ERROR!!", e);
		}
	}

	/**
	 * 重载当前的配置
	 * 
	 * @date 2016年7月29日 下午3:09:49
	 * @author wjfu 01116035
	 */
	public static void reload() {
		sysProps.clear();
		msgProps.clear();
		load();
		loadMsg();
	}

	/**
	 * 获取系统中的常量
	 * 
	 * @date 2016年4月12日 下午1:26:18
	 * @author wjfu 01116035
	 * @param name
	 * @return
	 */
	public static String getProp(String name) {
		return sysProps.get(name);
	}

	/**
	 * 获取消息的配置值 如果找不到值会返回空
	 * 
	 * @date 2016年8月9日 上午10:29:21
	 * @author wjfu 01116035
	 * @param name
	 * @return
	 */
	public static String getMsg(String key, String belong_to, Object... format) {
		String oriVal = msgProps.get(key + belong_to);
		String value = null == oriVal ? "" : oriVal;
		return MessageFormat.format(value, format);
	}

	/**
	 * 获取系统常量 如果没有 返回默认值
	 * 
	 * @date 2016年4月22日 下午2:22:50
	 * @author wjfu 01116035
	 * @param name
	 * @param defaultVal
	 * @return
	 */
	public static String getProp(String name, String defaultVal) {
		String val = sysProps.get(name);
		return StringUtils.isEmpty(val) ? defaultVal : val;
	}
}
