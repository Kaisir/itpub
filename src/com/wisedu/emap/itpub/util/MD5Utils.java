package com.wisedu.emap.itpub.util;

import java.io.InputStream;
import java.security.MessageDigest;

/**
 *
 * @author nick
 * 2013-3-12
 */
public class MD5Utils {
	private static char hexDigits[] = {
		'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'
	};
	
	public static String md5(Object ... params) {
		if(params == null || params.length <= 0) {
			throw new RuntimeException("...");
		}
		StringBuffer sb = new StringBuffer();
		for(Object o : params) {
			sb.append(o.toString());
		}
		return md5(sb.toString());
	}
	
	public static String md5(InputStream is) {
		String ret = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.reset();
			byte[] dataBytes = new byte[1024];
			int nread = 0;
			while((nread = is.read(dataBytes)) != -1) {
				md.update(dataBytes, 0, nread);
			}
			byte[] mdbytes = md.digest();
			ret = md5byte2Str(mdbytes);
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return ret;
	}
	
	public static String md5(String str) {
		String ret = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] bs = str.getBytes("UTF-8");
//			System.out.println(bs);
//			for(byte b : bs) {
//				System.out.print(b);
//			}
//			System.out.println();
//			System.out.println(bs.length);
			md.reset();
			md.update(bs);
			byte[] md5bytes = md.digest();
			return md5byte2Str(md5bytes);
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return ret;
	}
	
	public static String md5byte2Str(byte[] md5bytes) {
		char md5chars[] = new char[32];
		int k = 0;
		for(byte b : md5bytes) {
			md5chars[k++] = hexDigits[b >>> 4 & 0xf];
			md5chars[k++] = hexDigits[b & 0xf];
		}
		return new String(md5chars);
	}
}
