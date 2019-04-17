package com.wisedu.emap.itpub.mod.service;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public interface IMobileCommonService {
	
	public static final String BEANID = "MobileCommonService";
	
	/**
     * 获取登陆用户所有的角色
     * @param request
     * @return
     * @throws Exception
     */
    public JsonObject getUserRoles(HttpServletRequest request) throws Exception;
	
	/**
	 * 获取登陆角色有权限的菜单
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public JsonObject getMenuInfo(HttpServletRequest request) throws Exception;

	/**
     * 获取系统参数:是否需要角色选择页
     * @param request
     * @return
     * @throws Exception
     */
    public JsonObject getSelRoleConfig(HttpServletRequest request) throws Exception;
    
    /**
     * 设置登陆用户的当前角色
     * @param request
     * @return
     * @throws Exception
     */
    public void setAppRole(HttpServletRequest request) throws Exception;
    
    /**
     * 
     * [简要描述]：获取微信jsapi_ticket票据
     * [详细描述]：票据获取方式见 http://qydev.weixin.qq.com/wiki/index.php?title=微信JS-SDK接口   
     *
     * @param request
     * @return
     * @throws Exception
     */
    public JsonObject getWechatSign(HttpServletRequest request) throws Exception;
    
    /**
     * 
     * [简要描述]：从微信服务器获取媒体文件保存本地
     * [详细描述]：由于微信中的上传图片接口会将图片上传至微信的图片服务器，因此需要发起请求将图片下载到本地保存
     *          从微信服务器下载图片的方式见   http://qydev.weixin.qq.com/wiki/index.php?title=获取临时素材文件
     *
     * @param request
     * @return
     * @throws Exception
     */
    public JsonObject saveFileFromWechat(HttpServletRequest request) throws Exception;

    /**
     * 
     * [简要描述]：获取发布学年字典
     * [详细描述]：    
     *
     * @param request
     * @return
     */
    public JsonElement getDictionaryOfFbxn(HttpServletRequest request) throws Exception;
}
