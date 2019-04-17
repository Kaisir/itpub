package com.wisedu.emap.itpub.util.wec;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.google.common.collect.Maps;
import com.wisedu.emap.base.util.StringUtil;
import com.wisedu.emap.itpub.util.JsonUtils;

/**
 * @filename OpenApiUtils.java
 * @date 2017年1月9日 下午1:58:12
 * @author wjfu 01116035
 */
@Slf4j
public class OpenApiUtils {

	private OpenApiUtils() {
	}

	private static Map<String, String> openApiMap = Maps.newHashMap();

	static {
		loadOpenApiConf();
	}

	@SuppressWarnings("unchecked")
	private static void loadOpenApiConf() {
		try {
			InputStream input = OpenApiUtils.class.getClassLoader().getResourceAsStream("open-api-config.xml");
			if (input != null) {
				Document document = new SAXReader().read(input);
				Element e = document.getRootElement();
				// 获取根节点下的open api的host子节点
				String host = e.elementTextTrim("host");
				// 如果配置的host为空
				if (StringUtil.isEmpty(host)) {
					log.error("OPEN API未配置host节点，无法加载无host的api配置信息。");
				}
				Iterator<Element> apisIter = e.elementIterator();
				while (apisIter.hasNext()) {
					Element apiEle = apisIter.next();
					if (!"api".equals(apiEle.getName())) {
						continue;
					}
					String apiId = apiEle.elementTextTrim("apiId");
					String url = apiEle.elementTextTrim("url");
					if (StringUtil.isNotEmpty(apiId) && StringUtil.isNotEmpty(url)) {
						if (url.startsWith("http")) {
							openApiMap.put(apiId, url);
						} else {
							openApiMap.put(apiId, host + url);
						}
					}
				}
				log.info("^_^ OPEN API REST CONFIG LOAD SUCCESS!!!");
			} else {
				log.info(" OPEN API未配置open-api-config.xml文件。");
			}
		} catch (Exception e) {
			log.error(">_< OPEN API REST CONFIG LOAD FAILED!!!", e);
		}
	}

	/**
	 * 获取已加载的公开API的内容
	 * 
	 * @date 2017年1月9日 下午2:36:37
	 * @author wjfu 01116035
	 * @param key
	 * @return
	 */
	public static String loadUrl(String key) {
		return openApiMap.get(key);
	}

	/**
	 * 返回通用的公开API结果 如果有错误信息则打印此信息 并且返回空
	 * 
	 * @date 2017年2月5日 下午4:02:58
	 * @author wjfu 01116035
	 * @param resultStr
	 * @return
	 */
	public static String handleResult(String resultStr) {
		Map<String, Object> wrapAuthBean = JsonUtils.json2Map(resultStr);
		if (!"0".equals(wrapAuthBean.get("error_code"))) {
			log.error(" POST RESULT FAIL WITH " + wrapAuthBean.get("error_msg"));
			return null;
		}
		return StringUtil.getString(wrapAuthBean.get("datas"));
	}

}
