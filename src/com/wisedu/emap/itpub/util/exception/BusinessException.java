package com.wisedu.emap.itpub.util.exception;

/**
 * 业务异常
 *
 */
public class BusinessException extends AppProductException {

	private static final long serialVersionUID = -4344489748640793982L;

	public static final String CODE = "08";

	public BusinessException(String productCode, String msg) {
		super(getAppExceptionCode(productCode, CODE), msg);
	}

	public BusinessException(String productCode, Throwable e) {
		super(getAppExceptionCode(productCode, CODE), e);
	}

	public BusinessException(String productCode, String msg, Throwable e) {
		super(getAppExceptionCode(productCode, CODE), msg, e);
	}
}
