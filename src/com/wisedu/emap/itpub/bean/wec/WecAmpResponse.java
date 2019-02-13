package com.wisedu.emap.itpub.bean.wec;

/**
 * 
 * 伯牙接口返回消息体
 * @author 01118203
 *
 */
public class WecAmpResponse {
	private String status;
	private String message;
	private String echoStr;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getEchoStr() {
		return echoStr;
	}

	public void setEchoStr(String echoStr) {
		this.echoStr = echoStr;
	}
}