package com.wisedu.emap.itpub.bean.wec;

/**
 * 
 * 伯牙接口接收消息体
 * 
 * @author 01118203
 *
 */
public class WecAmpRequest {
	private String sign;
	private String timestamp;
	private String nonce;
	private String echoStr;

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getNonce() {
		return nonce;
	}

	public void setNonce(String nonce) {
		this.nonce = nonce;
	}

	public String getEchoStr() {
		return echoStr;
	}

	public void setEchoStr(String echoStr) {
		this.echoStr = echoStr;
	}
}