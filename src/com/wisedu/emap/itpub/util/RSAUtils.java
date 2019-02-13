package com.wisedu.emap.itpub.util;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * RSA算法加密/解密工具类。
 * 
 * @author fuchun
 * @version 1.0.0, 2010-05-05
 */
public abstract class RSAUtils {

	/** 算法名称 */
	private static final String ALGORITHOM = "RSA";
	/** 保存生成的密钥对的文件名称。 */
	private static final String RSA_PAIR_FILENAME = "__RSA_PAIR.txt";
	/** 默认的安全服务提供者 */
	private static final Provider DEFAULT_PROVIDER = new BouncyCastleProvider();

	private static KeyFactory keyFactory = null;
	/** 缓存的密钥对。 */
	private static KeyPair oneKeyPair = null;

	static {
		try {
			keyFactory = KeyFactory.getInstance(ALGORITHOM, DEFAULT_PROVIDER);
		} catch (NoSuchAlgorithmException ex) {
			throw new RuntimeException(ex);
		}
	}

	private RSAUtils() {
	}

	/**
	 * 返回RSA密钥对。
	 */
	public static KeyPair getKeyPair() throws Exception {
		return readKeyPair();
	}

	// 同步读出保存的密钥对
	private static KeyPair readKeyPair() throws Exception {
		InputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis = RSAUtils.class.getClassLoader().getResourceAsStream(RSA_PAIR_FILENAME);
			ois = new ObjectInputStream(fis);
			oneKeyPair = (KeyPair) ois.readObject();
			return oneKeyPair;
		} finally {
			IOUtils.closeQuietly(ois);
			IOUtils.closeQuietly(fis);
		}
	}

	/**
	 * 根据给定的系数和专用指数构造一个RSA专用的公钥对象。
	 * 
	 * @param modulus
	 *            系数。
	 * @param publicExponent
	 *            专用指数。
	 * @return RSA专用公钥对象。
	 */
	public static RSAPublicKey generateRSAPublicKey(byte[] modulus, byte[] publicExponent) throws Exception {
		RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(new BigInteger(modulus), new BigInteger(publicExponent));
		return (RSAPublicKey) keyFactory.generatePublic(publicKeySpec);
	}

	/**
	 * 根据给定的系数和专用指数构造一个RSA专用的私钥对象。
	 * 
	 * @param modulus
	 *            系数。
	 * @param privateExponent
	 *            专用指数。
	 * @return RSA专用私钥对象。
	 */
	public static RSAPrivateKey generateRSAPrivateKey(byte[] modulus, byte[] privateExponent) throws Exception {
		RSAPrivateKeySpec privateKeySpec = new RSAPrivateKeySpec(new BigInteger(modulus),
				new BigInteger(privateExponent));
		return (RSAPrivateKey) keyFactory.generatePrivate(privateKeySpec);
	}

	/**
	 * 根据给定的16进制系数和专用指数字符串构造一个RSA专用的私钥对象。
	 * 
	 * @param modulus
	 *            系数。
	 * @param privateExponent
	 *            专用指数。
	 * @return RSA专用私钥对象。
	 */
	public static RSAPrivateKey getRSAPrivateKey(String hexModulus, String hexPrivateExponent) throws Exception {
		if (StringUtils.isBlank(hexModulus) || StringUtils.isBlank(hexPrivateExponent)) {
			throw new Exception(
					"hexModulus and hexPrivateExponent cannot be empty. RSAPrivateKey value is null to return.");
		}
		byte[] modulus = null;
		byte[] privateExponent = null;
		modulus = Hex.decodeHex(hexModulus.toCharArray());
		privateExponent = Hex.decodeHex(hexPrivateExponent.toCharArray());
		if (modulus != null && privateExponent != null) {
			return generateRSAPrivateKey(modulus, privateExponent);
		}
		return null;
	}

	/**
	 * 根据给定的16进制系数和专用指数字符串构造一个RSA专用的公钥对象。
	 * 
	 * @param modulus
	 *            系数。
	 * @param publicExponent
	 *            专用指数。
	 * @return RSA专用公钥对象。
	 */
	public static RSAPublicKey getRSAPublidKey(String hexModulus, String hexPublicExponent) throws Exception {
		if (StringUtils.isBlank(hexModulus) || StringUtils.isBlank(hexPublicExponent)) {
			throw new Exception("hexModulus and hexPublicExponent cannot be empty. return null(RSAPublicKey).");
		}
		byte[] modulus = null;
		byte[] publicExponent = null;
		modulus = Hex.decodeHex(hexModulus.toCharArray());
		publicExponent = Hex.decodeHex(hexPublicExponent.toCharArray());
		if (modulus != null && publicExponent != null) {
			return generateRSAPublicKey(modulus, publicExponent);
		}
		return null;
	}

	/**
	 * 使用指定的公钥加密数据。
	 * 
	 * @param publicKey
	 *            给定的公钥。
	 * @param data
	 *            要加密的数据。
	 * @return 加密后的数据。
	 */
	public static byte[] encrypt(PublicKey publicKey, byte[] data) throws Exception {
		Cipher ci = Cipher.getInstance(ALGORITHOM, DEFAULT_PROVIDER);
		ci.init(Cipher.ENCRYPT_MODE, publicKey);
		return ci.doFinal(data);
	}

	/**
	 * 使用指定的私钥解密数据。
	 * 
	 * @param privateKey
	 *            给定的私钥。
	 * @param data
	 *            要解密的数据。
	 * @return 原数据。
	 */
	public static byte[] decrypt(PrivateKey privateKey, byte[] data) throws Exception {
		Cipher ci = Cipher.getInstance(ALGORITHOM, DEFAULT_PROVIDER);
		ci.init(Cipher.DECRYPT_MODE, privateKey);
		return ci.doFinal(data);
	}

	/**
	 * 使用给定的公钥加密给定的字符串。
	 * <p />
	 * 若 {@code publicKey} 为 {@code null}，或者 {@code plaintext} 为 {@code null}
	 * 则返回 {@code null}。
	 * 
	 * @param publicKey
	 *            给定的公钥。
	 * @param plaintext
	 *            字符串。
	 * @return 给定字符串的密文。
	 */
	public static String encryptString(PublicKey publicKey, String plaintext) throws Exception {
		if (publicKey == null || plaintext == null) {
			return null;
		}
		byte[] data = plaintext.getBytes();
		byte[] en_data = encrypt(publicKey, data);
		return new String(Hex.encodeHex(en_data));
	}

	/**
	 * 使用默认的公钥加密给定的字符串。
	 * <p />
	 * 若{@code plaintext} 为 {@code null} 则返回 {@code null}。
	 * 
	 * @param plaintext
	 *            字符串。
	 * @return 给定字符串的密文。
	 */
	public static String encryptString(String plaintext) throws Exception {
		if (plaintext == null) {
			return null;
		}
		byte[] data = plaintext.getBytes();
		KeyPair keyPair = getKeyPair();
		byte[] en_data = encrypt((RSAPublicKey) keyPair.getPublic(), data);
		return new String(Hex.encodeHex(en_data));
	}

	/**
	 * 使用给定的私钥解密给定的字符串。
	 * <p />
	 * 若私钥为 {@code null}，或者 {@code encrypttext} 为 {@code null}或空字符串则返回
	 * {@code null}。 私钥不匹配时，返回 {@code null}。
	 * 
	 * @param privateKey
	 *            给定的私钥。
	 * @param encrypttext
	 *            密文。
	 * @return 原文字符串。
	 */
	public static String decryptString(PrivateKey privateKey, String encrypttext) throws Exception {
		if (privateKey == null || StringUtils.isBlank(encrypttext)) {
			return null;
		}
		byte[] en_data = Hex.decodeHex(encrypttext.toCharArray());
		byte[] data = decrypt(privateKey, en_data);
		return new String(data);
	}

	/**
	 * 使用默认的私钥解密给定的字符串。
	 * <p />
	 * 若{@code encrypttext} 为 {@code null}或空字符串则返回 {@code null}。 私钥不匹配时，返回
	 * {@code null}。
	 * 
	 * @param encrypttext
	 *            密文。
	 * @return 原文字符串。
	 */
	public static String decryptString(String encrypttext) throws Exception {
		if (StringUtils.isBlank(encrypttext)) {
			return null;
		}
		return new String(decryptBytes(encrypttext));
	}

	/**
	 * 解密为字节数组
	 * 
	 * @author mengbin
	 * @date 2014-2-19 下午05:12:58
	 */
	public static byte[] decryptBytes(String encrypttext) throws Exception {
		if (StringUtils.isBlank(encrypttext)) {
			return null;
		}
		KeyPair keyPair = getKeyPair();
		byte[] en_data = Hex.decodeHex(encrypttext.toCharArray());
		byte[] data = decrypt((RSAPrivateKey) keyPair.getPrivate(), en_data);
		return data;
	}

	/**
	 * 使用默认的私钥解密由JS加密（使用此类提供的公钥加密）的字符串。
	 * 
	 * @param encrypttext
	 *            密文。
	 * @return {@code encrypttext} 的原文字符串。
	 */
	public static String decryptStringByJs(String encrypttext) throws Exception {
		if (encrypttext == null) {
			return null;
		}
		String[] tmps = encrypttext.split(" ");
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < tmps.length; i++) {
			String text = decryptString(tmps[i]);
			if (text != null) {
				buf.append(StringUtils.reverse(text));
			}
		}
		return buf.toString();
	}

	/** 返回已初始化的默认的公钥。 */
	public static RSAPublicKey getDefaultPublicKey() throws Exception {
		KeyPair keyPair = getKeyPair();
		if (keyPair != null) {
			return (RSAPublicKey) keyPair.getPublic();
		}
		return null;
	}

	/** 返回已初始化的默认的私钥。 */
	public static RSAPrivateKey getDefaultPrivateKey() throws Exception {
		KeyPair keyPair = getKeyPair();
		if (keyPair != null) {
			return (RSAPrivateKey) keyPair.getPrivate();
		}
		return null;
	}
}