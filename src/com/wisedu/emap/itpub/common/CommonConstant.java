package com.wisedu.emap.itpub.common;


/**
 *   
 * 类名称：CommonConstant    
 * 类描述：    
 * @version 1.0
 */
public interface CommonConstant
{
    /** 成功  **/
    public static final String SUCCESS = "00000";
    
    /** 参数错误 **/
    public static final String PARAM_ERROR = "10001";

    /** 数据库异常 **/
    public static final String DATABASE_ERROR = "19999";
    
    /** 无权限执行操作 **/
    public static final String NO_PERMISSION = "49999";
    
    /** 自定义动作类型**/
    public static final String ACTIONTYPE_MINE = "MINE";
    
    /** 数据模型动作类型**/
    public static final String ACTIONTYPE_DATAMODEL = "DATAMODEL";
    
    public static final String ACTION_TYPE_ADD = "ADD";
    
    public static final String ACTION_TYPE_SAVE = "SAVE";
    
    public static final String ACTION_TYPE_DELTE = "DELETE";
    
    public static final String ACTION_TYPE_MODIFY = "MODIFY";
    
    /** 1:有数据权限  0:无数据权限  **/
    public static final String HAS_PERMISSION_FLAG = "1";
    
    /** JS页面可变参数列表名  **/
    public static final String REQUEST_PARAM_NAME = "requestParamStr";
    
    /** 文件上传下载没有权限  **/
    public static final String FILE_AUTH_ERROR = "no permission!";
    
    /** 竖线字符串  **/
    public static final String VERTICAL_LINE = "|";
    
    /** 功能范围-移动  **/
    public static final String GNFW_NAME = "MOBILE";
    
    /** 公共应用业务域  **/
    public static final String YWY_NAME = "IT";
    
    /** 内容类型-图片  **/
    public static final String CONTENT_TYPE_IMAGE = "image/*";
    
    
   
  
}

