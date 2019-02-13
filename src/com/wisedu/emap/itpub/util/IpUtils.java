/*
 * @Project ymsqxt
 * @Package com.wisedu.emap.ymsqxt.util
 * @date 2016年4月1日 上午10:24:26
 * @author wjfu 01116035
 */
package com.wisedu.emap.itpub.util;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

/**
 * 关于ip的一些工具
 * 
 * @filename IpUtils.java
 * @date 2016年4月1日 上午10:24:26
 * @author wjfu 01116035
 */
public class IpUtils {

	private IpUtils() {
	}

	/**
	 * 将ip转换为long型数字
	 * 
	 * @date 2016年4月4日 下午2:41:18
	 * @author wjfu 01116035
	 * @param ipAddress
	 * @return
	 */
	public static long ipToLong(String ipAddress) {
		long result = 0;
		String[] ipAddressInArray = ipAddress.split("\\.");
		for (int i = 3; i >= 0; i--) {
			long ip = Long.parseLong(ipAddressInArray[3 - i]);
			result |= ip << (i * 8);
		}
		return result;
	}

	/**
	 * 将long型数字转换成ip地址
	 * 
	 * @date 2016年4月4日 下午2:41:42
	 * @author wjfu 01116035
	 * @param i
	 * @return
	 */
	public static String longToIp(long i) {
		return ((i >> 24) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + (i & 0xFF);
	}

	public static String getClientIp(HttpServletRequest request) {

		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		if (StringUtils.isNotBlank(ip)) {
			ip = ip.split(",")[0];
		}
		return ip;

	}
}
