package com.wisedu.emap.itpub.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

//import com.wisedu.business.exception.BusinessException;
import com.wisedu.emap.pedestal.core.AppManager;

/**
 * EMAP环境下，提供其他APP进行序列化,反序列化工具
 * 
 * 
 */
public class SerializeUtil 
{

	private static final transient Log LOGGER = LogFactory.getLog(SerializeUtil.class);
	
	private SerializeUtil() {
	}

	/**
	 * 序列化
	 * 
	 * @param object
	 * @return
	 */
	public static byte[] serialize(Object object) {
		if (object == null) {
			return null;
		}
		ObjectOutputStream oos = null;
		ByteArrayOutputStream baos = null;
		try {
			// 序列化
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			return baos.toByteArray();
		} catch (Exception e) {
			LOGGER.error(SerializeUtil.class,e);
		}
		return null;
		
	}

	/**
	 * 反序列化
	 * 
	 * @param bytes
	 * @return
	 */
	public static Object unserialize(byte[] bytes) {
		if (bytes == null) {
			return null;
		}
		ByteArrayInputStream bais = null;
		ObjectInputStream ois = null;
		try {
			// 反序列化
			bais = new ByteArrayInputStream(bytes);
			ois = new BundleObjectInputStream(bais, AppManager.currentApp().getClassLoader());

			return ois.readObject();
		} catch (Exception e) {
			LOGGER.error(SerializeUtil.class,e);
		} finally {
			if (null != ois) {
				try {
					ois.close();
				} catch (IOException e) {
					LOGGER.error(SerializeUtil.class,e);
				}
			}
		}
		return null;
	}

}
