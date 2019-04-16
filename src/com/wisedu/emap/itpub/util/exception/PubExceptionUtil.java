package com.wisedu.emap.itpub.util.exception;



public class PubExceptionUtil {
	/**业务公共应用*/
	private static final String IT_CODE="11";
	/**
	 * 抛出业务异常，没有堆栈可以不传此参数
	 * @param msg
	 */
	public static void throwBusinessException(String msg,Throwable ...e){
		if(e!=null&&e.length>0){
			throw new BusinessException(IT_CODE,msg,e[0]);
		}
		throw new BusinessException(IT_CODE,msg);
	}
	/**
	 * 抛出网络异常
	 * @param msg
	 * @param e
	 */
	public static void throwNetworkException(String msg,Throwable ...e){
		if(e!=null&&e.length>0){
			throw new NetworkException(IT_CODE,msg,e[0]);
		}
		throw new NetworkException(IT_CODE,msg);
	}
	/**
	 * 抛出参数校验异常
	 * @param msg
	 * @param e
	 */
	public static void throwParamsException(String msg,Throwable ...e){
		if(e!=null&&e.length>0){
			throw new ParamsException(IT_CODE,msg,e[0]);
		}
		throw new ParamsException(IT_CODE,msg);
	}
	/**
	 * 抛出权限异常
	 * @param msg
	 * @param e
	 */
	public static void throwPrivException(String msg,Throwable ...e){
		if(e!=null&&e.length>0){
			throw new PrivException(IT_CODE,msg,e[0]);
		}
		throw new PrivException(IT_CODE,msg);
	}
	/**
	 * 数据库操作异常
	 * @param msg
	 * @param e
	 */
	public static void throwDatabaseException(String msg,Throwable ...e){
		if(e!=null&&e.length>0){
			throw new DatabaseException(IT_CODE,msg,e[0]);
		}
		throw new DatabaseException(IT_CODE,msg);
	}
	/**
	 * 接口异常
	 * @param msg
	 * @param e
	 */
	public static void throwInterfaceException(String msg,Throwable ...e){
		if(e!=null&&e.length>0){
			throw new InterfaceException(IT_CODE,msg,e[0]);
		}
		throw new InterfaceException(IT_CODE,msg);
	}
	/**
	 * 其他异常
	 * @param msg
	 * @param e
	 */
	public static void throwOtherException(String msg,Throwable ...e){
		if(e!=null&&e.length>0){
			throw new OtherException(IT_CODE,msg,e[0]);
		}
		throw new OtherException(IT_CODE,msg);
	}
}
