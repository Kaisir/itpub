package com.wisedu.emap.itpub.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.wisedu.emap.base.util.ListUtil;
import com.wisedu.emap.base.util.StringUtil;
import com.wisedu.emap.itpub.bean.Constants;
import com.wisedu.emap.itpub.bean.HttpClientParam;
import com.wisedu.emap.itpub.bean.RestfulBean;
import com.wisedu.emap.itpub.util.wec.OpenApiUtils;
import com.wisedu.emap.mvc.CurrentThread;
import com.wisedu.emap.pedestal.core.AppManager;

import java.io.Closeable;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * Http请求工具类
 */
@Slf4j
public class HttpUtils {

	private HttpUtils() {
	}

	private static final int SOCKET_TIMEOUT = 30000;
	private static final int CONN_TIMEOUT = 30000;
	private static final int CONN_REQ_TIMEOUT = 30000;
	private static final int SOCKET_TIMEOUT_LONG = 30000;
	private static final int CONN_TIMEOUT_LONG = 30000;
	private static final int CONN_REQ_TIMEOUT_LONG = 30000;

	private static final String SPRING_TOKEN_NAME = "spring.token.name";

	/**
	 * 取得HttpRequest中Parameter的简化方法.
	 */
	public static String getParameter(final String name) {
		return getRequest().getParameter(name);
	}

	/**
	 * OPEN API的RESTFUL请求
	 * 
	 * @date 2017年1月9日 下午3:35:44
	 * @author wjfu 01116035
	 * @param apiId
	 * @param accessToken
	 * @param httpParam
	 * @return
	 * @throws Exception
	 */
	public static String apiPost(String apiId, HttpClientParam httpParam) throws Exception {
		// 如果入参的为空 则构造一个入参结构
		if (null == httpParam) {
			httpParam = new HttpClientParam();
		}
		String url = OpenApiUtils.loadUrl(apiId);
		// 将accessToken拼接到url上
		// String accessToken = getRequest().getHeader("access_token");
		// url += "?access_token=" + accessToken;
		httpParam.setUrl(url);
		List<Header> headers = httpParam.getHeaders();
		// 如果
		if (null == headers) {
			headers = Lists.newArrayList();
			headers.add(Constants.JSON_HEADER);
			httpParam.setHeaders(headers);
		}
		// 添加accessToken和appId
		httpParam.getHeaders().add(new BasicHeader("accessToken", ITPubPropsUtil.getProp("RESTFUL.ACCESS_TOKEN")));
		httpParam.getHeaders().add(new BasicHeader("appId", AppManager.currentApp().getId()));
		// 暂时去掉此种方式传递到后台的accessToken 目前直接拼接到url上
		// JsonElement params = httpParam.getParams();
		// if (null != params) {
		// params.getAsJsonObject().addProperty("access_token", accessToken);
		// }
		// List<NameValuePair> nvParams = httpParam.getNvParams();
		// if (null != nvParams) {
		// nvParams.add(new BasicNameValuePair("access_token", accessToken));
		// }
		// if (null == params && null == nvParams) {
		// nvParams = Lists.newArrayList();
		// nvParams.add(new BasicNameValuePair("access_token", accessToken));
		// httpParam.setNvParams(nvParams);
		// }
		return post(httpParam);
	}

	/**
	 * 获取post请求数据
	 * 
	 * @author wjfu 01116035
	 * @date 2016年3月21日 上午11:33:59
	 * @param httpParam
	 * @see com.wisedu.emap.it.bean.HttpClientParam
	 * @return
	 * @throws Exception
	 *             String
	 */
	public static String post(HttpClientParam httpParam) throws Exception {
		// 请求参数设置
		RequestConfig config = RequestConfig.custom().setSocketTimeout(httpParam.getSocketTimeout())
				.setConnectionRequestTimeout(httpParam.getConnTimeout())
				.setConnectTimeout(httpParam.getConnReqTimeout()).build();
		// 创建httpClient对象
		CloseableHttpClient httpClient = HttpClients.custom()
				// .setProxy(new HttpHost("172.16.4.111", 808, "http"))
				// httpClient链接池
				.setConnectionManager(new PoolingHttpClientConnectionManager()).setDefaultRequestConfig(config).build();
		return httpPost(httpClient, httpParam);
	}

	public static String httpsPost(HttpClientParam httpParam) throws Exception {
		CloseableHttpClient httpClient = createSSLClientDefault();
		return httpPost(httpClient, httpParam);
	}

	/**
	 * https的post请求
	 * 
	 * @date 2017年5月3日 下午3:09:32
	 * @author xianghao
	 * @param url
	 * @param headers
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static String httpsPost(String url, List<Header> headers, JsonElement params) throws Exception {
		HttpClientParam httpParam = new HttpClientParam();
		httpParam.setUrl(url);
		httpParam.setHeaders(headers);
		httpParam.setParams(params);
		httpParam.setSocketTimeout(SOCKET_TIMEOUT);
		httpParam.setConnTimeout(CONN_TIMEOUT);
		httpParam.setConnReqTimeout(CONN_REQ_TIMEOUT);
		return httpsPost(httpParam);
	}

	public static String post(CloseableHttpClient httpClient, HttpClientParam httpParam) throws Exception {
		return httpPost(httpClient, httpParam);
	}

	public static String httpsPost(HttpClient httpClient, String url, List<Header> headers,
			List<NameValuePair> nvParams) throws Exception {
		HttpClientParam httpParam = new HttpClientParam();
		httpParam.setUrl(url);
		httpParam.setHeaders(headers);
		httpParam.setNvParams(nvParams);
		httpParam.setSocketTimeout(SOCKET_TIMEOUT);
		httpParam.setConnTimeout(CONN_TIMEOUT);
		httpParam.setConnReqTimeout(CONN_REQ_TIMEOUT);
		return httpsPost(httpParam);
	}

	private static String httpPost(CloseableHttpClient httpClient, HttpClientParam httpParam) throws Exception {
		String result = null;
		// 根据url建立post对象
		String url = httpParam.getUrl();
		HttpPost post = new HttpPost(url);
		// 如果header不为空 填入header
		List<Header> headers = httpParam.getHeaders();
		if (null != headers) {
			for (Header header : headers) {
				post.addHeader(header);
			}
		}
		// 将当前的参数传入
		JsonElement params = httpParam.getParams();
		if (params != null) {
			post.setEntity(new StringEntity(params.toString(), Consts.UTF_8));
		}
		List<NameValuePair> nameValueParams = httpParam.getNvParams();
		if (nameValueParams != null) {
			post.setEntity(new UrlEncodedFormEntity(nameValueParams, Consts.UTF_8));
		}
		String cookies = httpParam.getCookies();
		if (StringUtil.isNotEmpty(cookies)) {
			post.addHeader("Cookie", cookies);
		}
		// httpClient
		// 创建返回对象
		CloseableHttpResponse resp = null;
		// 捕捉异常
		try {
			long t1 = System.currentTimeMillis();
			log.info("request header >>>" + JsonUtils.toJsonStr(post.getAllHeaders()) + "<<<");
			log.info("request params >>>" + params + "<<<");
			log.info("request nvParams >>>" + nameValueParams + "<<<");
			log.info("request url >>>" + url + "<<<");
			resp = httpClient.execute(post);
			long t2 = System.currentTimeMillis();
			log.info("request cost >>>" + (t2 - t1) + " mills<<<");
			int statusCode = resp.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				// 如果是301或者302 给出提示跳转到哪里了
				if (statusCode == HttpStatus.SC_MOVED_TEMPORARILY || statusCode == HttpStatus.SC_MOVED_PERMANENTLY) {
					log.info("request redirected to: " + resp.getHeaders("Location"));
				}
				log.error("request fail with: " + EntityUtils.toString(resp.getEntity()));
				return result;
			}
			result = EntityUtils.toString(resp.getEntity(), Consts.UTF_8);
			return result;
		} catch (Exception e) {
			throw e;
		} finally {
			close(resp);
			close(httpClient);
		}
	}

	private static void close(Closeable source) {
		try {
			if (null != source) {
				source.close();
			}
		} catch (Exception e) {
			log.error("close resource[" + source + "]error with" + e.getMessage(), e);
		} finally {
			source = null;
		}
	}

	/**
	 * 获取以配置id为key的post结果
	 * 
	 * @author wjfu01116035
	 * @date 2016年3月31日 下午3:49:04
	 * @param restId
	 *            配置文件restful-config.xml中的uri的id
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static String confPost(String restId, JsonElement params) throws Exception {
		RestfulBean rest = RestfulConf.getRestBeanById(restId);
		// 不改变原始请求对象
		List<Header> headers = Lists.newArrayList(rest.getHeaders());
		headers.add(Constants.JSON_HEADER);
		// 添加数据库配置的appId和accessToken
		headers.add(new BasicHeader("appId", ITPubPropsUtil.getProp("RESTFUL.APP_ID")));
		headers.add(new BasicHeader("accessToken", ITPubPropsUtil.getProp("RESTFUL.ACCESS_TOKEN")));
		return post(rest.getUrl(), headers, params);
	}

	/**
	 * 有默认超时的post请求 - json参数请求
	 * 
	 * @author wjfu 01116035 @date 2016年3月21日 上午11:50:25 @param url @param
	 *         headers @param params @return @throws Exception String @throws
	 */
	public static String post(String url, List<Header> headers, JsonElement params) throws Exception {
		HttpClientParam httpParam = new HttpClientParam();
		httpParam.setUrl(url);
		httpParam.setHeaders(headers);
		httpParam.setParams(params);
		httpParam.setSocketTimeout(SOCKET_TIMEOUT);
		httpParam.setConnTimeout(CONN_TIMEOUT);
		httpParam.setConnReqTimeout(CONN_REQ_TIMEOUT);
		return post(httpParam);
	}

	/**
	 * 有默认超时的post请求 - 键值对参数请求
	 * 
	 * @author wjfu 01116035 @date 2016年3月21日 上午11:50:25 @param url @param
	 *         headers @param nvParams @return @throws Exception String @throws
	 */
	public static String post(String url, List<Header> headers, List<NameValuePair> nvParams) throws Exception {
		HttpClientParam httpParam = new HttpClientParam();
		httpParam.setUrl(url);
		httpParam.setHeaders(headers);
		httpParam.setNvParams(nvParams);
		httpParam.setSocketTimeout(SOCKET_TIMEOUT);
		httpParam.setConnTimeout(CONN_TIMEOUT);
		httpParam.setConnReqTimeout(CONN_REQ_TIMEOUT);
		return post(httpParam);
	}

	public static String post(HttpClient httpClient, String url, List<Header> headers, List<NameValuePair> nvParams)
			throws Exception {
		HttpClientParam httpParam = new HttpClientParam();
		httpParam.setUrl(url);
		httpParam.setHeaders(headers);
		httpParam.setNvParams(nvParams);
		httpParam.setSocketTimeout(SOCKET_TIMEOUT);
		httpParam.setConnTimeout(CONN_TIMEOUT);
		httpParam.setConnReqTimeout(CONN_REQ_TIMEOUT);
		return post(httpParam);
	}

	/**
	 * 无请求入参的post方法
	 * 
	 * @date 2016年7月23日 下午3:18:11
	 * @author wjfu 01116035
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public static String post(String url) throws Exception {
		HttpClientParam httpParam = new HttpClientParam();
		httpParam.setUrl(url);
		httpParam.setSocketTimeout(SOCKET_TIMEOUT);
		httpParam.setConnTimeout(CONN_TIMEOUT);
		httpParam.setConnReqTimeout(CONN_REQ_TIMEOUT);
		return post(httpParam);
	}

	/**
	 * 获取请求对象
	 * 
	 * @author mengbin
	 * @date 2015年4月16日 上午11:41:04
	 * @return
	 */
	public static HttpServletRequest getRequest() {
		return CurrentThread.getCurrentRequest();
	}

	/**
	 * 获取响应对象
	 * 
	 * @author mengbin
	 * @date 2015年4月16日 上午11:41:04
	 * @return
	 */
	public static HttpServletResponse getResponse() {
		return CurrentThread.getCurrentResponse();
	}

	/**
	 * 获取请求参数
	 * 
	 * @author mengbin
	 * @date 2014年8月21日 上午10:57:26
	 */
	public static Map<String, Object> getParameterMap() {
		Enumeration<String> e = getRequest().getParameterNames();
		Map<String, Object> retMap = new HashMap<String, Object>();
		while (e.hasMoreElements()) {
			String name = e.nextElement();
			if (SPRING_TOKEN_NAME.equals(name)) {
				continue;
			}
			String value = getRequest().getParameter(name);
			retMap.put(name, value);
		}
		return retMap;
	}

	/**
	 * 获取Map<String, String>的请求参数
	 * 
	 * @date 2016年6月16日 下午8:01:12
	 * @author wjfu 01116035
	 * @return
	 */
	public static Map<String, String> getParamStrMap() {
		Enumeration<String> e = getRequest().getParameterNames();
		Map<String, String> retMap = Maps.newHashMap();
		while (e.hasMoreElements()) {
			String name = e.nextElement();
			if (SPRING_TOKEN_NAME.equals(name)) {
				continue;
			}
			String value = getRequest().getParameter(name);
			retMap.put(name, value);
		}
		return retMap;
	}

	/**
	 * 获取键值对传递的post请求结果
	 * 
	 * @date 2016年4月7日 下午4:50:03
	 * @author wjfu 01116035
	 * @param url
	 * @param nameValueParams
	 * @return
	 * @throws Exception
	 */
	public static String nameValPost(String url, List<NameValuePair> nameValueParams) throws Exception {
		HttpClientParam httpParam = new HttpClientParam();
		httpParam.setUrl(url);
		httpParam.setNvParams(nameValueParams);
		httpParam.setSocketTimeout(SOCKET_TIMEOUT_LONG);
		httpParam.setConnTimeout(CONN_TIMEOUT_LONG);
		httpParam.setConnReqTimeout(CONN_REQ_TIMEOUT_LONG);
		return post(httpParam);
	}

	/**
	 * 获取get请求的返回数据
	 * 
	 * @date 2016年4月20日 下午7:25:29
	 * @author wjfu 01116035
	 * @param url
	 * @param headers
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static String doGet(String url, List<Header> headers, List<NameValuePair> params) throws Exception {
		HttpClientParam httpParam = new HttpClientParam();
		httpParam.setUrl(url);
		httpParam.setHeaders(headers);
		httpParam.setNvParams(params);
		httpParam.setSocketTimeout(SOCKET_TIMEOUT_LONG);
		httpParam.setConnTimeout(CONN_TIMEOUT_LONG);
		httpParam.setConnReqTimeout(CONN_REQ_TIMEOUT_LONG);
		return doGet(httpParam);
	}

	public static String doGet(HttpClientParam httpParam) throws Exception {
		String result = "";
		// 请求参数设置
		RequestConfig config = RequestConfig.custom().setSocketTimeout(httpParam.getSocketTimeout())
				.setConnectionRequestTimeout(httpParam.getConnTimeout())
				.setConnectTimeout(httpParam.getConnReqTimeout()).build();
		// 创建httpClient对象
		CloseableHttpClient httpClient = HttpClients.custom()
				// 云内要开代理
				// .setProxy(new HttpHost("172.16.4.111", 808, "http"))
				.setDefaultRequestConfig(config).build();
		// 根据url建立get对象
		String url = httpParam.getUrl();
		HttpGet httpGet = new HttpGet(url);
		List<Header> headers = httpParam.getHeaders();
		if (null != headers) {
			for (Header header : headers) {
				httpGet.addHeader(header);
			}
		}
		// 将请求参数转换成URL参数
		List<NameValuePair> params = httpParam.getNvParams();
		if (ListUtil.isNotEmpty(params)) {
			String paramsStr = EntityUtils.toString(new UrlEncodedFormEntity(params, Consts.UTF_8));
			httpGet.setURI(new URI(httpGet.getURI() + "?" + paramsStr));
		}
		CloseableHttpResponse resp = null;
		try {
			resp = httpClient.execute(httpGet);
			if (HttpStatus.SC_OK == resp.getStatusLine().getStatusCode()) {
				result = EntityUtils.toString(resp.getEntity());
			}
			return result;
		} catch (Exception e) {
			log.error("HttpGet Error with: " + httpGet.getURI());
			throw e;
		} finally {
			close(resp);
			close(httpClient);
		}
	}

	/**
	 * Https的get请求
	 * 
	 * @date 2016年11月28日 下午2:42:17
	 * @author xianghao
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public static String httpsGet(String url) throws Exception {
		CloseableHttpClient client = null;
		HttpGet get = null;
		CloseableHttpResponse response = null;
		try {
			client = createSSLClientDefault();
			get = new HttpGet(url);
			response = client.execute(get);
			return EntityUtils.toString(response.getEntity(), "UTF-8");
		} catch (Exception e) {
			log.error("HttpGet Error with: " + get.getURI());
			throw e;
		} finally {
			close(client);
			close(response);
		}
	}

	/**
	 * Https请求
	 * 
	 * @date 2016年11月28日 下午2:41:12
	 * @author xianghao
	 * @return
	 */
	private static CloseableHttpClient createSSLClientDefault() {
		try {
			// 创建请求配置
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000)
					.setConnectionRequestTimeout(30000).setConnectTimeout(30000).build();
			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
				@Override
				public boolean isTrusted(java.security.cert.X509Certificate[] x509Certificates, String s)
						throws java.security.cert.CertificateException {
					return true;
				}
			}).build();
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext,
					SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			return HttpClients.custom().setDefaultRequestConfig(requestConfig).setSSLSocketFactory(sslsf)
					// 云内需要加一个代理
					// .setProxy(new HttpHost("172.16.4.111", 808, "http"))
					.build();
		} catch (KeyManagementException e) {
			log.error("秘钥管理异常：" + e.getMessage(), e);
		} catch (NoSuchAlgorithmException e) {
			log.error("算法异常：" + e.getMessage(), e);
		} catch (KeyStoreException e) {
			log.error("秘钥存储异常：" + e.getMessage(), e);
		}
		return HttpClients.createDefault();
	}

	/**
	 * 获取RSA加密的请求参数
	 * 
	 * @date 2016年6月28日 下午5:17:57
	 * @author wjfu 01116035
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public static String getRsaParameter(String name) throws Exception {
		String value = getParameter(name);
		if (!StringUtil.isEmpty(value)) {
			value = RSAUtils.decryptStringByJs(value);
		}
		return value;
	}

	public static void setCrossDomainHeader(HttpServletResponse resp) {
		HttpServletRequest request = getRequest();
		resp.addHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
		resp.addHeader("Access-Control-Allow-Methods", "POST,GET,OPTIONS,DELETE,PUT");
		resp.addHeader("Access-Control-Max-Age", "100");
		resp.addHeader("Access-Control-Allow-Headers", "Content-Type");
		resp.addHeader("Access-Control-Allow-Credentials", "true");
		resp.setCharacterEncoding("UTF-8");// 设置编码
		resp.setHeader("Content-Type", "application/json;charset=UTF-8");
	}

	/**
	 * 获取容器地址 /sys之前 结尾没有/
	 * 
	 * @date 2016年8月4日 下午3:59:05
	 * @author wjfu 01116035
	 * @return
	 */
	public static String getRequestContextUrl() {
		HttpServletRequest request = getRequest();
		String url = request.getScheme() + "://"; // 请求协议 http 或 https
		url += request.getHeader("host"); // 请求服务器
		url += request.getContextPath();
		return url;
	}

	/**
	 * 获取请求的基本地址 到/sys/结尾有/
	 * 
	 * @date 2016年6月28日 下午5:19:10
	 * @author wjfu 01116035
	 * @return
	 */
	public static String getRequestBaseUrl() {
		return getRequestContextUrl() + "/sys/";
	}

	/**
	 * 获取指定cookie信息
	 * 
	 * @date 2017年1月6日 下午6:00:17
	 * @author wjfu 01116035
	 * @param key
	 * @return
	 */
	public static String getCookie(String key) {
		Cookie[] cookies = getRequest().getCookies();
		if (null != cookies) {
			for (Cookie cookie : cookies) {
				if (key.equals(cookie.getName())) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}
}
