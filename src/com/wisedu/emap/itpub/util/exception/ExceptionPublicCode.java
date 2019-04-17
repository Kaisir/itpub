package com.wisedu.emap.itpub.util.exception;

/**
 * 异常类型代码
 *
 */
public interface ExceptionPublicCode {

	/**
	 * 参数校验异常
	 */
	public static final String PARAM_EXCEPTION = "01";
	/**
	 * 数据库异常
	 * 
	 */
	public static final String DATABASE_EXCEPTION = "02";
	/**
	 * 缓存异常含redis
	 */
	public static final String CACHE_EXCEPTION = "03";
	/**
	 * 权限异常
	  */
	public static final String PERMISSION_EXCEPTION = "04";
	/**
	 * 网络异常
	 */
	public static final String INTERNET_EXCEPTION="05";
	/**
	 * 运行异常
	 */
	public static final String RUNTIME_EXCEPTION="06";
	/**
	 * 接口异常
	 */
	public static final String INTERFACE_EXCEPTION="07";
	/**
	 * 业务数据异常
	 */
	public static final String BUSINESSDATA_EXCEPTION="08";
	/**
	 * 其他异常
	 */
	public static final String OTHER_EXCEPTION="09";
}
