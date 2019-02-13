/*
 * @Title: RestfulConf.java 
 * @Package com.wisedu.emap.wdyktzd.util 
 * @author wjfu 01116035
 */
package com.wisedu.emap.itpub.util;

import com.google.common.collect.Maps;
import com.wisedu.emap.base.util.StringUtil;
import com.wisedu.emap.itpub.bean.RestfulBean;

import java.util.Iterator;
import java.util.Map;

import org.apache.http.message.BasicHeader;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import lombok.extern.slf4j.Slf4j;

/**
 * 获取restful的配置
 * 
 * @ClassName: RestfulConf
 * @author wjfu 01116035
 * @date 2016年3月21日 下午3:03:39
 */
@Slf4j
public class RestfulConf {

	private RestfulConf() {
	}

	private static Map<String, RestfulBean> beanMap = Maps.newHashMap();

	/**
	 * 静态加载配置文件
	 */
	static {
		load();
	}

	/**
	 * 获取已配置的rest数据 @author wjfu 01116035 @date 2016年3月21日 下午4:07:45 @param
	 * id @return RestfulBean @throws
	 */
	public static RestfulBean getRestBeanById(String id) {
		return beanMap.get(id);
	}

	/**
	 * 重载配置
	 * 
	 * @date 2016年7月29日 下午3:11:48
	 * @author wjfu 01116035
	 */
	public static void reload() {
		beanMap.clear();
		load();
	}

	/**
	 * 载入应用的restful配置信息
	 * 
	 * @date 2016年7月29日 上午10:39:02
	 * @author wjfu 01116035
	 */
	@SuppressWarnings("unchecked")
	private static void load() {
		try {
			// 获取综合服务库的请求API地址
			String host = ITPubPropsUtil.getProp("RESTFUL.HOST");
			if (StringUtil.isEmpty(host)) {
				throw new Exception("未获取到服务器配置的restful host请检查T_ITPUB_PROPS是否存在KEY[RESTFUL.HOST]");
			}
			// 如果尾部有/替换掉
			host = host.replaceAll("\\/$", "");
			Document document = new SAXReader()
					.read(RestfulConf.class.getClassLoader().getResourceAsStream("restful-config.xml"));
			Element e = document.getRootElement();
			Iterator<Element> apisIter = e.elementIterator();
			// 遍历apis节点下的api子节点
			while (apisIter.hasNext()) {
				Element apiEle = apisIter.next();
				// 遍历api下的具体内容的节点 填充节点的值
				Iterator<Element> apiIter = apiEle.elementIterator();
				RestfulBean bean = new RestfulBean();
				while (apiIter.hasNext()) {
					Element ele = apiIter.next();
					String name = ele.getName();
					String value = StringUtil.getString(ele.getData());
					if ("apiId".equals(name)) {
						beanMap.put(value, bean);
					} else if ("accessUrl".equals(name)) {
						if (value.startsWith("http:") || value.startsWith("https:")) {
							bean.setUrl(value);
						} else {
							bean.setUrl(host + value);
						}
					} else {
						bean.getHeaders().add(new BasicHeader(name, value));
					}
				}
			}
			log.info("^_^ REST CONFIG LOAD SUCCESS!!!");
		} catch (Exception e) {
			log.error(">_< REST CONFIG LOAD FAILED!!!", e);
		}
	}
}
