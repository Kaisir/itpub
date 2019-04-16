package com.wisedu.emap.itpub.service.wechart;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public interface IWechatService 
{
	/** 
	 * 获取微信企业号的access_token
	 * access_token有效时间7200秒
	 * @param url
	 * @return
	 */
	public String getAccessToken();

	/**
	 * 获取微信企业号的jsapi_ticket
	 * jsapi_ticket有效时间7200秒
	 * @param url
	 * @return
	 */
	public String getJsapiTicket();

	/**
	 * 根据url获取微信企业号的授权签名
	 * @throws NoSuchAlgorithmException 
	 * @throws UnsupportedEncodingException 
	 * 
	 */
	public Map<String, Object> sign(String signUrl) throws NoSuchAlgorithmException, UnsupportedEncodingException;
	
	/**
	 * 从微信服务器获取媒体文件保存本地
	 * @param accessToken 接口访问凭证
	 * @param media_id 媒体文件id
	 * @param savePath 文件在服务器上的存储路径
	 * @param configObject 前端组件所传的配置参数
     * @throws Exception 
	 * */
	public String downloadFile(String mediaId,String storeId,String token,String... fileName) throws Exception;
}
