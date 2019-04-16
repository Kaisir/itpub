package com.wisedu.emap.itpub.util.exception;

/**
 * 接口调用异常
 *
 */
public class InterfaceException extends AppProductException {

	private static final long serialVersionUID = -3461652667184124810L;

	public static final String CODE = "07";

	public InterfaceException(String productCode, String msg) {
		super(getAppExceptionCode(productCode, CODE), msg);
	}

	public InterfaceException(String productCode, Throwable e) {
		super(getAppExceptionCode(productCode, CODE), e);
	}

	public InterfaceException(String productCode, String msg, Throwable e) {
		super(getAppExceptionCode(productCode, CODE), msg, e);
	}
}
