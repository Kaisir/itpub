package com.wisedu.emap.itpub.util.exception;

/**
 * 网络异常
 *
 */
public class NetworkException extends AppProductException {

	private static final long serialVersionUID = 8057150804850830968L;

	public static final String CODE = "05";

	public NetworkException(String productCode, String msg) {
		super(getAppExceptionCode(productCode, CODE), msg);
	}

	public NetworkException(String productCode, Throwable e) {
		super(getAppExceptionCode(productCode, CODE), e);
	}

	public NetworkException(String productCode, String msg, Throwable e) {
		super(getAppExceptionCode(productCode, CODE), msg, e);
	}
}
