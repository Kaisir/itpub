package com.wisedu.emap.itpub.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;

/**
 * 文件工具类。
 * 
 * @author 01115210
 *
 */
public class FileUtil {

	/**
	 * 将图片文件转化为字节数组字符串，并对其进行Base64编码处理.
	 * 
	 * @param in
	 * @return
	 */
	public static String getBase64FromInputStream(InputStream in) throws IOException {
		byte[] data = null;
		try {
			ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
			byte[] buff = new byte[100];
			int rc = 0;
			while ((rc = in.read(buff, 0, 100)) > 0) {
				swapStream.write(buff, 0, rc);
			}
			data = swapStream.toByteArray();
		} catch (IOException e) {
			throw e;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return new String(Base64.encodeBase64String(data));
	}

	/**
	 * 将字节流转换为字符串。
	 * 
	 * @param in
	 * @return
	 */
	public static String getStringFromInputStream(InputStream in) {
		byte[] data = null;
		try {
			ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
			byte[] buff = new byte[100];
			int rc = 0;
			while ((rc = in.read(buff, 0, 100)) > 0) {
				swapStream.write(buff, 0, rc);
			}
			data = swapStream.toByteArray();
			return new String(data, "UTF-8");
		} catch (IOException e) {
			return "读取文件出错!";
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public static void main(String[] args) {
		System.out.println(UUID.randomUUID().toString());
	}

}
