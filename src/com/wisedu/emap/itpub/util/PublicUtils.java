/*
 * @Project itservicecommon
 * @Package com.wisedu.emap.it.util
 * @date 2016年12月24日 下午3:56:12
 * @author wjfu 01116035
 */
package com.wisedu.emap.itpub.util;

/**
 * @filename PublicUtils.java
 * @date 2016年12月24日 下午3:56:12
 * @author wjfu 01116035
 */
public class PublicUtils {

	private PublicUtils() {
	}

	private final static String[] NUM_ZH_CN = new String[] { "零", "一", "二", "三", "四", "五" };

	/**
	 * 将YYYY-YYYY-NUM类型的学年学期翻译为中文 EG.2015-2016-2 --> 2015-2016 第二学期
	 * 
	 * @date 2016年12月24日 下午3:58:39
	 * @author wjfu 01116035
	 * @param code
	 * @return
	 */
	public static String transSchoolYearTermCode(String code) {
		if (!code.matches("^\\d{4}-\\d{4}-\\d{1,2}$")) {
			return null;
		}
		String years = code.substring(0, code.lastIndexOf('-'));
		String term = code.substring(code.lastIndexOf('-') + 1);
		return years + " 第" + NUM_ZH_CN[Integer.parseInt(term)] + "学期";
	}

	/**
	 * 调整传入的URL 如果URL后缀没有/则添加一个/ 如果前缀没有http://或者https://则添加http://
	 * 
	 * @date 2016年12月27日 上午10:10:17
	 * @author wjfu 01116035
	 * @param baseUrl
	 * @return
	 */
	public static String urlFixed(String baseUrl) {
		// 默认都为http请求 如果没有http头 则为其添加http头
		if (!baseUrl.startsWith("http")) {
			baseUrl += "http://";
		}
		if (!baseUrl.endsWith("/")) {
			baseUrl += "/";
		}
		return baseUrl;
	}

}
