package com.wisedu.emap.itpub.cache;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

/**
 * EMAP环境下，其他APP进行反序列化时，会出现ClassNotFoundException异常，修改一下classloader
 * 
 * 
 */
public class BundleObjectInputStream extends ObjectInputStream {

	private ClassLoader loader;

	protected BundleObjectInputStream(InputStream in, ClassLoader loader) throws IOException {
		super(in);
		this.loader = loader;
	}

	protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {

		String name = desc.getName();

		try {
			return Class.forName(name, false, loader);
		} catch (ClassNotFoundException ex) {
			throw ex;
		}

	}
}
