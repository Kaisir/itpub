package com.wisedu.emap.itpub.util;

import java.net.URLDecoder;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.wisedu.emap.base.util.MapUtil;
import com.wisedu.emap.itpub.util.exception.PubExceptionUtil;

/**
 * 参数解析
 *
 *
 */
public class ComJsonUtil {

	public static String readJSONString(HttpServletRequest request) {
		StringBuilder json = new StringBuilder();
		String jsonReturn = null;
		try {
			Map<String, String[]> hm = request.getParameterMap();
			if (MapUtil.isNotEmpty(hm)) {
				json.append(readjson(hm).toString());
			}
			jsonReturn = json.toString();
			jsonReturn = jsonReturn.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
			jsonReturn = jsonReturn.replaceAll("\\+", "%2B");
			jsonReturn = URLDecoder.decode(jsonReturn, "utf-8");
		} catch (Exception e) {
			PubExceptionUtil.throwBusinessException("参数解析异常！", e);
		}

		return jsonReturn;
	}

	private static String readjson(Map<String, String[]> hm) {
		String[] data = hm.get("data");
		if (data == null) {
			return "";
		}
		return data[0].toString();
	}

}
