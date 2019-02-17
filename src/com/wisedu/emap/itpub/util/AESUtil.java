package com.wisedu.emap.itpub.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
 * 简单的AES加解密工具
 *
 * @author mdmo
 */
public class AESUtil {
	/**
	 * 加密
	 *
	 * @param content
	 *            需要加密的内容
	 * @param password
	 *            加密密码
	 * @return
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws UnsupportedEncodingException
	 * @throws NoSuchPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 */
	public static String encrypt(String content, String password) throws BadPaddingException, IllegalBlockSizeException,
			UnsupportedEncodingException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
		try {
			KeyGenerator kgen = KeyGenerator.getInstance("AES");

			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			random.setSeed(password.getBytes());
			kgen.init(128, random);
			SecretKey secretKey = kgen.generateKey();
			byte[] enCodeFormat = secretKey.getEncoded();
			SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
			Cipher cipher = Cipher.getInstance("AES");// 创建密码器
			byte[] byteContent = content.getBytes("utf-8");
			cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
			byte[] result = cipher.doFinal(byteContent);
			return parseByte2HexStr(result); // 加密
		} catch (NoSuchAlgorithmException e) {
			throw e;
		} catch (NoSuchPaddingException e) {
			throw e;
		} catch (InvalidKeyException e) {
			throw e;
		} catch (UnsupportedEncodingException e) {
			throw e;
		} catch (IllegalBlockSizeException e) {
			throw e;
		} catch (BadPaddingException e) {
			throw e;
		}
	}

	/**
	 * 将二进制转换成16进制
	 *
	 * @param buf
	 * @return
	 */
	public static String parseByte2HexStr(byte buf[]) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < buf.length; i++) {
			String hex = Integer.toHexString(buf[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			sb.append(hex.toUpperCase());
		}
		return sb.toString();
	}

	/**
	 * 解密
	 *
	 * @param content
	 *            待解密内容
	 * @param password
	 *            解密密钥
	 * @return
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws NoSuchPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 */
	public static String decrypt(String content, String password) throws IllegalBlockSizeException, BadPaddingException,
			NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
		try {
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			random.setSeed(password.getBytes());
			kgen.init(128, random);
			SecretKey secretKey = kgen.generateKey();
			byte[] enCodeFormat = secretKey.getEncoded();
			SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
			Cipher cipher = Cipher.getInstance("AES");// 创建密码器
			cipher.init(Cipher.DECRYPT_MODE, key);// 初始化
			byte[] result = cipher.doFinal(parseHexStr2Byte(content));
			return new String(result); // 加密
		} catch (NoSuchAlgorithmException e) {
			throw e;
		} catch (NoSuchPaddingException e) {
			throw e;
		} catch (InvalidKeyException e) {
			throw e;
		} catch (IllegalBlockSizeException e) {
			throw e;
		} catch (BadPaddingException e) {
			throw e;
		}
	}

	/**
	 * 将16进制转换为二进制
	 *
	 * @param hexStr
	 * @return
	 */
	public static byte[] parseHexStr2Byte(String hexStr) {
		if (hexStr.length() < 1)
			return null;
		byte[] result = new byte[hexStr.length() / 2];
		for (int i = 0; i < hexStr.length() / 2; i++) {
			int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
			int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
			result[i] = (byte) (high * 16 + low);
		}
		return result;
	}

	public static String getEncodingAESKey() {
		String aesKey = getRandomString(32);
		// String encodingAESKey = Base64Util.encode(aesKey.getBytes());
		String encodingAESKey = Base64.encodeBase64String(aesKey.getBytes());
		encodingAESKey = encodingAESKey.substring(0, encodingAESKey.length() - 1);
		return encodingAESKey;
	}

	public static String getRandomString(int length) {
		String str = "zxcvbnmlkjhgfdsaqwertyuiopQWERTYUIOPASDFGHJKLZXCVBNM1234567890";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; ++i) {
			int number = random.nextInt(62);
			sb.append(str.charAt(number));
		}
		return sb.toString();
	}

	public static void main(String[] args) {
		String content = "{\"timestamp\":\"" + System.currentTimeMillis()
				+ "\",\"userMailAddr\":\"xinxihua@nbu.edu.cn\",\"userId\":\"\"}";
		String password = "whatsyourpassword";
		try {
			String encodedStr = encrypt(content, password);
			System.out.println(encodedStr);
			String decodedStr = decrypt(encodedStr, password);
			System.out.println(decodedStr);

			// String strrr =
			// Base64Util.encode("1aashdgfkjashgdfkjhgasjkdfuywhha".getBytes());
			String strrr = Base64.encodeBase64String("1aashdgfkjashgdfkjhgasjkdfuywhha".getBytes());
			System.out.println(strrr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
