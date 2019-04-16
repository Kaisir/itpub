package com.wisedu.emap.itpub.util.exception;


/**
 * 参数校验异常
 *
 */
public class ParamsException extends AppProductException {

    private static final long serialVersionUID = -377309892457135562L;

    public static final String CODE = "01";

    public ParamsException(String productCode,String msg) {
		super(getAppExceptionCode(productCode, CODE), msg);
    }

    public ParamsException(String productCode,Throwable e) {
        super(getAppExceptionCode(productCode,CODE), e);
    }

    public ParamsException(String productCode,String msg, Throwable e) {
        super(getAppExceptionCode(productCode,CODE), msg, e);
    }
}
