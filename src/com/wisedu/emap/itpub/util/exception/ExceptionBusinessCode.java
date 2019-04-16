package com.wisedu.emap.itpub.util.exception;

public interface ExceptionBusinessCode {
	/**
	 * 入参必填
	 */
	public static final String PARAM_REQURIED="001";
	/**
	 * 入参格式
	 */
	public static final String PARAM_FORMAT="002";
	/**
	 * 入参长度
	 */
	public static final String PARAM_LENGTH="003";
	/**
	 * 业务记录不存在
	 */
	public static final String BUSINESSDATA_RECORDNOTEXIST="004";
	/**
	 * 业务记录中某字段为空
	 */
	public static final String BUSINESSDATA_ROPERTYEMPTY="005";
	/**
	 * 调用接口业务性失败
	 */
	public static final String BUSINESSDATA_INTERFACEFAIL="006";
	/**
	 * 更新记录数数目异常
	 */
	public static final String BUSINESSDATA_UPDATENUMBER="007";
	
	/**
	 * 数据库添加异常
	 */
	public static final String DATABASE_ADD="013";
	/**
	 * 数据库查询异常
	 */
	public static final String DATABASE_QUERY="014";
	/**
	 * 数据库更新异常
	 */
	public static final String DATABASE_UPDATE="015";
	/**
	 * 数据库删除异常
	 */
	public static final String DATABASE_DELETE="016";
	
	/**
	 * 数据访问鉴权失败
	 */
	public static final String PERMISSON_NOPERMISSON="020";
	/**
	 * 教务其他异常（尽量不用）
	 */
	public static final String OTHER="999";
}
