package com.wisedu.emap.itpub.util;

import java.nio.charset.Charset;
import java.security.MessageDigest;

import org.apache.commons.codec.binary.Base64;

/**
 * 加密解码工具
 */
public class EncryptUtils {

	private EncryptUtils() {
	}

	/**
	 * MD5加密
	 * 
	 * @param msg
	 * @return
	 * @throws Exception
	 */
	public static String md5(String msg) throws Exception {
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(msg.getBytes(Charset.forName("UTF-8")));
		byte b[] = md.digest();
		return toHex(b);
	}

	private static String toHex(byte[] bytes) {
		StringBuilder buf = new StringBuilder();
		for (int offset = 0; offset < bytes.length; offset++) {
			int i = bytes[offset];
			if (i < 0)
				i += 256;
			if (i < 16)
				buf.append("0");
			buf.append(Integer.toHexString(i));
		}
		return buf.toString();
	}

	/**
	 * BASE64解码
	 * 
	 * @date 2016年4月27日 上午11:37:29
	 * @author wjfu 01116035
	 * @param obj
	 * @return
	 */
	public static String base64Decode(Object obj) {
		if (null == obj) {
			return "";
		}
		byte[] decodeBytes = Base64.decodeBase64(obj.toString());
		return org.apache.commons.codec.binary.StringUtils.newStringUtf8(decodeBytes);
	}

}
