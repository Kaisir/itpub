package com.wisedu.emap.itpub.bean;

/**
 * 业务异常
 * 
 * @author zhuangyuhao
 * @date 2016年4月20日 上午11:51:49
 */
public class BizException extends RuntimeException {

	private static final long serialVersionUID = 6352279438342373712L;

	private String code;

	public BizException(String msg) {
		super(msg);
	}

	public BizException(String code, String msg) {
		super(msg);
		this.code = code;
	}

	public BizException(Throwable e) {
		super(e);
	}

	public BizException(String msg, Throwable e) {
		super(msg, e);
	}

	public String getCode() {
		return code;
	}
}
