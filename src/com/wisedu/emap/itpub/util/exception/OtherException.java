package com.wisedu.emap.itpub.util.exception;

/**
 * 其他类型异常
 *
 */
public class OtherException extends AppProductException {

	private static final long serialVersionUID = 6286245778934477964L;

	public static final String CODE = "09";

	public OtherException(String productCode, String msg) {
		super(getAppExceptionCode(productCode, CODE), msg);
	}

	public OtherException(String productCode, Throwable e) {
		super(getAppExceptionCode(productCode, CODE), e);
	}

	public OtherException(String productCode, String msg, Throwable e) {
		super(getAppExceptionCode(productCode, CODE), msg, e);
	}
}
