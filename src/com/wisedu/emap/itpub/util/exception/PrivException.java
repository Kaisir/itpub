package com.wisedu.emap.itpub.util.exception;

/**
 * 权限异常
 *
 */
public class PrivException extends AppProductException {

	private static final long serialVersionUID = -2620634638902246082L;

	public static final String CODE = "04";

	public PrivException(String productCode, String msg) {
		super(getAppExceptionCode(productCode, CODE), msg);
	}

	public PrivException(String productCode, Throwable e) {
		super(getAppExceptionCode(productCode, CODE), e);
	}

	public PrivException(String productCode, String msg, Throwable e) {
		super(getAppExceptionCode(productCode, CODE), msg, e);
	}
}
