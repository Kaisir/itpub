package com.wisedu.emap.itpub.util.exception;

/**
 * 缓存异常
 *
 */
public class CacheException extends AppProductException {

	private static final long serialVersionUID = 262980528435959012L;

	public static final String CODE = "03";

	public CacheException(String productCode, String msg) {
		super(getAppExceptionCode(productCode, CODE), msg);
	}

	public CacheException(String productCode, Throwable e) {
		super(getAppExceptionCode(productCode, CODE), e);
	}

	public CacheException(String productCode, String msg, Throwable e) {
		super(getAppExceptionCode(productCode, CODE), msg, e);
	}
}
