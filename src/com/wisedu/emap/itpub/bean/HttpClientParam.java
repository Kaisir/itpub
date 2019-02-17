package com.wisedu.emap.itpub.bean;

import com.google.gson.JsonElement;

import java.util.List;

import org.apache.http.Header;
import org.apache.http.NameValuePair;

import lombok.Data;

/**
 * @filename HttpClientParam.java
 * @date 2016年11月7日 下午3:13:49
 * @author wjfu 01116035
 */
public @Data class HttpClientParam {

	/** 请求的url */
	private String url;

	/** 请求头 */
	private List<Header> headers;

	/** 请求json参数 */
	private JsonElement params;

	/** 请求nameValue类型参数 */
	private List<NameValuePair> nvParams;

	/** 从界面传递的cookie */
	private String cookies;

	/** 端口超时时间 默认10s */
	private int socketTimeout = 10000;

	/** 连接超时时间 默认10s */
	private int connTimeout = 10000;

	/** 连接响应超时时间 默认10s */
	private int connReqTimeout = 10000;

	// public String getUrl() {
	// return url;
	// }
	//
	// public void setUrl(String url) {
	// this.url = url;
	// }
	//
	// public List<Header> getHeaders() {
	// return headers;
	// }
	//
	// public void setHeaders(List<Header> headers) {
	// this.headers = headers;
	// }
	//
	// public JsonElement getParams() {
	// return params;
	// }
	//
	// public void setParams(JsonElement params) {
	// this.params = params;
	// }
	//
	// public List<NameValuePair> getNvParams() {
	// return nvParams;
	// }
	//
	// public void setNvParams(List<NameValuePair> nvParams) {
	// this.nvParams = nvParams;
	// }
	//
	// public int getSocketTimeout() {
	// return socketTimeout;
	// }
	//
	// public void setSocketTimeout(int socketTimeout) {
	// this.socketTimeout = socketTimeout;
	// }
	//
	// public int getConnTimeout() {
	// return connTimeout;
	// }
	//
	// public void setConnTimeout(int connTimeout) {
	// this.connTimeout = connTimeout;
	// }
	//
	// public int getConnReqTimeout() {
	// return connReqTimeout;
	// }
	//
	// public void setConnReqTimeout(int connReqTimeout) {
	// this.connReqTimeout = connReqTimeout;
	// }
	//
	// public String getCookies() {
	// return cookies;
	// }
	//
	// public void setCookies(String cookies) {
	// this.cookies = cookies;
	// }

}
