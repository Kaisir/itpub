package com.wisedu.emap.itpub.util.exception;

/**
 * 数据库异常
 *
 */
public class DatabaseException extends AppProductException {

	private static final long serialVersionUID = 3829256652371191412L;

	public static final String CODE = "02";

	public DatabaseException(String productCode, String msg) {
		super(getAppExceptionCode(productCode, CODE), msg);
	}

	public DatabaseException(String productCode, Throwable e) {
		super(getAppExceptionCode(productCode, CODE), e);
	}

	public DatabaseException(String productCode, String msg, Throwable e) {
		super(getAppExceptionCode(productCode, CODE), msg, e);
	}
}
