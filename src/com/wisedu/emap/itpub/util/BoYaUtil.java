package com.wisedu.emap.itpub.util;

import com.wisedu.emap.itpub.bean.wec.WecAmpRequest;
import com.wisedu.emap.itpub.bean.wec.WecAmpResponse;
import com.wisedu.emap.itpub.controller.BaseController;

import org.apache.commons.codec.binary.Base64;

/**
 * 
 * 处理伯牙接口的工具类
 * 
 * @author 01118203
 *
 */
public class BoYaUtil extends BaseController {

	public WecAmpResponse response;

	private WecAmpRequest request;

	private String wecToken;

	private String wecEncodingAesKey;

	public BoYaUtil(WecAmpRequest wecAmpRequest, String wecToken, String wecEncodingAesKey) {
		response = new WecAmpResponse();
		this.request = wecAmpRequest;
		this.wecToken = wecToken;
		this.wecEncodingAesKey = wecEncodingAesKey;
	}

	public String decodeStr() {
		String echoStr = "";
		if (checkSign()) {
			try {
				echoStr = AESUtil.decrypt(request.getEchoStr(),
						// new String(Base64Util.decode(wecEncodingAesKey +
						// "=")));
						new String(Base64.decodeBase64(wecEncodingAesKey + "=")));
			} catch (Exception e) {
				e.printStackTrace();
				response.setStatus("failed");
				response.setMessage("AES解密失败！");
			}
		} else {
			response.setStatus("failed");
			response.setMessage("sign校验不通过！");
		}
		return echoStr;
	}

	public void encodeStr(String dataStr) {
		String echoStrResponse = "";
		try {
			// echoStrResponse = AESUtil.encrypt(dataStr, new
			// String(Base64Util.decode(wecEncodingAesKey + "=")));
			echoStrResponse = AESUtil.encrypt(dataStr, new String(Base64.decodeBase64(wecEncodingAesKey + "=")));
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus("failed");
			response.setMessage("AES加密失败！");
		}

		response.setStatus("success");
		response.setEchoStr(echoStrResponse);

	}

	private boolean checkSign() {
		String signNew = MD5Utils.md5(wecToken, request.getTimestamp(), request.getNonce(), request.getEchoStr());
		return request.getSign().equalsIgnoreCase(signNew);
	}

}
