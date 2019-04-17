package com.wisedu.emap.itpub.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.wisedu.emap.base.util.StringUtil;

/**
 *
 */
public class ResponseUtil {

	private static final String SUCCESS_CODE = "0";
	private static final String SUCCESS_MSG = "成功";

	/**
	 * 成功返回的JSON对象
	 *
	 * @param data
	 * @return
	 */
	public static JsonObject buildSuccessResponse(JsonElement data) {
		JsonObject resultObject = new JsonObject();
		resultObject.addProperty("code", SUCCESS_CODE);
		resultObject.addProperty("msg", SUCCESS_MSG);
		if (null != data) {
			resultObject.add("data", data);
		}
		return resultObject;
	}
	
	/**
	 * 成功返回的字典数据结构
	 *
	 * @param data
	 * @return
	 */
	public static JsonObject buildSuccessDicResponse(JsonElement data) {
		JsonObject resultObject = new JsonObject();
		JsonObject codeObject = new JsonObject();
		JsonObject dataObject = new JsonObject();
		resultObject.addProperty("code", SUCCESS_CODE);
		resultObject.addProperty("msg", SUCCESS_MSG);
		if (null != data) {
			codeObject.add("rows", data);
			dataObject.add("code", codeObject);
			resultObject.add("datas", dataObject);
		}
		return resultObject;
	}
	/**
	 * 错误返回
	 * @param data
	 * @return
	 */
    public static JsonObject buildErrorResponse(String errorMsg)
    {
        JsonObject resultObject = new JsonObject();
        String errorCode = "-1";
        if (StringUtil.isNotEmpty(errorMsg) && errorMsg.indexOf(" ") > 0)
        {
            errorCode = errorMsg.substring(0, errorMsg.indexOf(" "));
            errorMsg = errorMsg.substring(errorMsg.indexOf(" "));
        }
        resultObject.addProperty("code", errorCode);
        resultObject.addProperty("msg", errorMsg);
        return resultObject;
    }
}
