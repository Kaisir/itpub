package com.wisedu.emap.itpub.service.wechart.impl;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import self.micromagic.util.annotation.Config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wisedu.emap.base.util.GuidUtil;
import com.wisedu.emap.base.util.LogUtil;
import com.wisedu.emap.base.util.StringUtil;
import com.wisedu.emap.dao.DaoService;
import com.wisedu.emap.file.util.IFileDownload;
import com.wisedu.emap.file.util.UploadParam;
import com.wisedu.emap.itpub.cache.RedisUtil;
import com.wisedu.emap.itpub.service.wechart.IWechatService;
import com.wisedu.emap.itpub.util.CommonUtils;
import com.wisedu.emap.itpub.util.exception.PubExceptionUtil;
import com.wisedu.emap.pedestal.app.AppBeanContainer;

@Service("wechatService")
public class WechatServiceImpl implements IWechatService
{
	@Config(name = "itpub.wechart.corpid", defaultValue = "", description = "微信企业号ID")
	private static String wechart_corpid;

	@Config(name = "itpub.wechart.corpsecret", defaultValue = "", description = "微信企业号证书")
	private static String wechart_corpsecret;

	@Autowired
	private DaoService daoService;

	private final AppBeanContainer<IFileDownload> fileDl = new AppBeanContainer<IFileDownload>("emapcomponent",IFileDownload.BEANID, false);

	/** 日志 **/
    protected static org.slf4j.Logger logger = LogUtil.getOnlineLog();

    /**
	 * 从微信服务器获取媒体文件保存本地
	 * @param accessToken 接口访问凭证
	 * @param media_id 媒体文件id
	 * @param savePath 文件在服务器上的存储路径
     * @throws Exception
	 * */
	public String downloadFile(String mediaIds,String storeId,String token,String... otherParam) throws Exception {
		//拼接请求地址
		try {
			if(StringUtil.isEmpty(mediaIds) || StringUtil.isEmpty(storeId)){
				return null;
			}
			if(StringUtil.isEmpty(token)){
				token = StringUtil.getString(daoService.getDataBaseTime() + Math.random()*100);
			}
			String[] mediaIdArr = mediaIds.split(",");
			String accessToken = this.getAccessToken();
			for(String mediaId : mediaIdArr){
				String requestUrl = "https://qyapi.weixin.qq.com/cgi-bin/media/get?access_token="+accessToken+"&media_id="+mediaId;
				URL url = new URL(null, requestUrl, new sun.net.www.protocol.https.Handler());//requestUrl);
				HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
				conn.setDoInput(true);
				conn.setRequestMethod("GET");
				//TODO 获取配置的文件保存路径  并插入附件表
				// 根据内容类型获取扩展名
				String fileExt = this.getFileEndWitsh(conn.getHeaderField("Content-Type"));

				// 从配置参数中获取文件名，若为空则使用mediaId
				String fileName = mediaId + fileExt;
				//取otherParam中第一个参数作为fileName
				if(otherParam != null && otherParam.length>0 && StringUtil.isNotEmpty(otherParam[0])){
				    fileName =  otherParam[0] + fileExt;
				}

				BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
				IFileDownload fileDownload = fileDl.get();
	            if (fileDownload != null)
	            {
	                UploadParam paramUploadParam = new UploadParam();
	                paramUploadParam.setFileName(fileName);
	                paramUploadParam.setScope(GuidUtil.getRandomGuid());
	                paramUploadParam.setStoreId(storeId);
	                paramUploadParam.setToken(token);
	                paramUploadParam.setWid(GuidUtil.getRandomGuid());
	                paramUploadParam.setSortNum(1);
					fileDownload.uploadAttachment(bis, paramUploadParam);
	            }
				bis.close();
				conn.disconnect();
			}
		} catch (Exception e) {
			logger.error("下载媒体文件失败!", e);
			throw e;
		}
		return token;
	}

	public String getFileEndWitsh(String contentType)
	{
        String fileEndWitsh = "";
        if ("image/jpeg".equals(contentType))
            fileEndWitsh = ".jpg";
        else if ("audio/mpeg".equals(contentType))
            fileEndWitsh = ".mp3";
        else if ("audio/amr".equals(contentType))
            fileEndWitsh = ".amr";
        else if ("video/mp4".equals(contentType))
            fileEndWitsh = ".mp4";
        else if ("video/mpeg4".equals(contentType))
            fileEndWitsh = ".mp4";
        return fileEndWitsh;
    }

	/**
	 * 获取微信企业号的access_token
	 * access_token有效时间7200秒
	 * @param url
	 * @return
	 */
	public String getAccessToken()
	{
		String accessToken = StringUtil.getString(RedisUtil.getCache("accessToken"));
		if(StringUtil.isNotEmpty(accessToken)){
			return accessToken;
		}
		String url = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid="+ wechart_corpid +"&corpsecret=" + wechart_corpsecret;
//		Map<String, Object> resultmap = this.getWechatRequest(url);
		Map<String, Object> resultmap = getWechatByUrl(url);
		if(null == resultmap){
			return null;
		}
		accessToken = StringUtil.getString(resultmap.get("access_token"));
		RedisUtil.setEx("accessToken", 7000, accessToken);
		return accessToken;
	}
	/**
	 * 获取token
	 * @param url
	 * @return
	 */
	public Map<String, Object> getWechatByUrl(String url){
		HttpsURLConnection httpUrlConnection = null;
		BufferedReader bfr = null;
		Map<String,Object> tokenMap = new HashMap<String, Object>();
		try{
			URL urlObj  = new URL(null,url, new sun.net.www.protocol.https.Handler());
			logger.info("访问微信接口---->url---->"+url);
			httpUrlConnection = (HttpsURLConnection)urlObj.openConnection();
			httpUrlConnection.setRequestMethod("GET");
			httpUrlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			httpUrlConnection.setDoOutput(true);
			httpUrlConnection.setDoInput(true);
			httpUrlConnection.setReadTimeout(5000);
			httpUrlConnection.setConnectTimeout(5000);
			httpUrlConnection.connect();
			int responseCode = httpUrlConnection.getResponseCode();
			if(responseCode==HttpURLConnection.HTTP_OK){
				String result = "";
				bfr = new BufferedReader(new InputStreamReader(httpUrlConnection.getInputStream()));
				String outStr;
				while((outStr=bfr.readLine())!=null){
					result+=outStr;
				}
				tokenMap = CommonUtils.buildMapParams(result, false);
			}
		}catch(Exception e){
			PubExceptionUtil.throwBusinessException("获取微信token异常", e);
		}finally{
			if(bfr != null){
				try {
					bfr.close();
				} catch (IOException e) {
					logger.error("流关闭异常!", e);
				}
			}
			if(httpUrlConnection !=null){
				httpUrlConnection.disconnect();
			}
		}
		return tokenMap;
	}
	/**
	 * 获取微信企业号的jsapi_ticket
	 * jsapi_ticket有效时间7200秒
	 * @param url
	 * @return
	 */
	public String getJsapiTicket()
	{
		String jsapiTicket = StringUtil.getString(RedisUtil.getCache("jsapiTicket"));
		if(StringUtil.isNotEmpty(jsapiTicket)){
			return jsapiTicket;
		}
		String accessToken = this.getAccessToken();
		String url = "https://qyapi.weixin.qq.com/cgi-bin/get_jsapi_ticket?access_token="+ accessToken;
		Map<String, Object> resultmap = this.getWechatByUrl(url);
		if(null == resultmap){
			return null;
		}
		jsapiTicket =  StringUtil.getString(resultmap.get("ticket"));
		RedisUtil.setEx("jsapiTicket", 7000, jsapiTicket);
		return jsapiTicket;
	}

	/**
	 * 根据url获取微信企业号的授权签名
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 *
	 */
	public Map<String, Object> sign(String signUrl) throws NoSuchAlgorithmException, UnsupportedEncodingException
	{
		String jsapiTicket = this.getJsapiTicket();
		if(StringUtil.isEmpty(jsapiTicket)){
			return null;
		}
		String noncestr = GuidUtil.getRandomGuid().substring(0, 15);
		long timestamp = daoService.getDataBaseTime() / 1000;
		StringBuilder signBuffer = new StringBuilder();
		//对所有待签名参数按照字段名的ASCII 码从小到大排序（字典序）后，使用URL键值对的格式，得到string1
		signBuffer.append("jsapi_ticket=").append(jsapiTicket)
				  .append("&noncestr=").append(noncestr)
			      .append("&timestamp=").append(timestamp)
			      .append("&url=").append(signUrl);
		//对string1进行sha1签名，得到signature
		String signature = "";
		try
        {
			MessageDigest crypt = MessageDigest.getInstance("SHA-1");
	        crypt.reset();
	        crypt.update(signBuffer.toString().getBytes("UTF-8"));
	        signature = byteToHex(crypt.digest());
        }
        catch (NoSuchAlgorithmException e)
        {
            throw e;
        }
        catch (UnsupportedEncodingException e)
        {
        	throw e;
        }
		//封装返回结果
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("jsapi_ticket", jsapiTicket);
		map.put("noncestr", noncestr);
		map.put("timestamp", timestamp);
		map.put("signature", signature);
		map.put("corpid", wechart_corpid);
		return map;
	}

	/**
	 * 加密字符格式化
	 * @param hash
	 * @return
	 */
	private static String byteToHex(final byte[] hash)
	{
        Formatter formatter = new Formatter();
        for (byte b : hash)
        {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

	/**
	 * 发送微信请求
	 * @param url
	 * @return
	 */
	private Map<String, Object> getWechatRequest(String url)
	{
		if(varifyParam()){
			logger.info("未配置微信企业号");
			return null;
		}
		Map<String,Object> resultmap = null;
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        CloseableHttpClient client = httpClientBuilder.build();
        try
        {
        	logger.info("访问微信接口 begin!");
            logger.info("访问微信接口---->url---->" + url);
            HttpGet httpgets = new HttpGet(url);
            //设置请求和传输超时时间
           /* RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(50000).setConnectTimeout(50000).build();
            httpgets.setConfig(requestConfig);*/
            HttpResponse response = client.execute(httpgets);
            if (response.getStatusLine().getStatusCode() == 200)
            {
                String responseInfo = EntityUtils.toString(response.getEntity());
                logger.info(StringUtil.getString("访问微信接口---->response.getEntity---->"+ responseInfo));
                // responseInfo 解析
                resultmap = this.buildParams(responseInfo);
            }
            else
            {
                logger.info("访问微信接口---->StatusCode---->" + response.getStatusLine().getStatusCode());
            }
        }
        catch (ClientProtocolException e)
        {
            logger.error("访问微信接口 error!", e);
        }
        catch (IOException e)
        {
            logger.error("访问微信接口 error!", e);
        }
        catch (Exception e)
        {
            logger.error("访问微信接口 error!", e);
        }
        finally
        {
            try
            {
            	logger.info("访问微信接口 end!");
                client.close();

            }
            catch (IOException e)
            {
                logger.warn("关闭GET请求 client.close error", e);
            }
        }
        return resultmap;
	}

	/**
	 * 微信配置参数校验
	 * @return
	 */
	private boolean varifyParam()
	{
		if(StringUtil.isEmpty(wechart_corpid) || StringUtil.isEmpty(wechart_corpsecret)){
			return true;
		}
		return false;
	}

	/**
	 * 接口返回数据解析
	 * @param paramString
	 * @return
	 * @throws SwException
	 */
	private Map<String, Object> buildParams(String paramString)
    {
        Map<String, Object> param = new HashMap<String, Object>();
        if (StringUtil.isEmpty(paramString))
        {
            return param;
        }

        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(paramString);
        JsonObject jo = je.getAsJsonObject();
        Set<Entry<String, JsonElement>> set = jo.entrySet();
        for (Entry<String, JsonElement> ssEntry : set)
        {
            param.put(ssEntry.getKey(), ssEntry.getValue().getAsString());
        }
        return param;
    }
}
