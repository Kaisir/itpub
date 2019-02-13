/**
 * @author xianghao
 * @date 2016年9月28日 下午3:19:32
 */
package com.wisedu.emap.itpub.controller.tool;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.wisedu.emap.base.util.ListUtil;
import com.wisedu.emap.base.util.StringUtil;
import com.wisedu.emap.itpub.bean.Constants;
import com.wisedu.emap.itpub.util.DbUtil;
import com.wisedu.emap.itpub.util.ITPubPropsUtil;
import com.wisedu.emap.pedestal.core.AppManager;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

/**
 * 由properties文件生成permission的xml文件
 * 
 * @author xianghao
 * @date 2016年9月28日 下午3:19:32
 */
@Controller
public class GenPermissionController {

	@RequestMapping("/permission.do")
	public String genPermission(ModelMap model) throws Exception {
		model.put("resServer", ITPubPropsUtil.getProp(Constants.COMMON_RES_SERVER, "http://res.wisedu.com"));
		return AppManager.currentApp().locateJSP("modules/permissionXml.jsp");
	}

	@RequestMapping("/genPermission.do")
	public void upload(HttpServletRequest request, HttpServletResponse response) throws Exception {
		OutputStream out = response.getOutputStream();
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/xml");
		response.setHeader("Content-Disposition", "attachment;filename=permission.xml");

		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		List<MultipartFile> listFile = multipartRequest.getFiles("file");
		if (ListUtil.isNotEmpty(listFile)) {
			for (MultipartFile mf : listFile) {
				if (!mf.isEmpty()) {
					InputStream in = mf.getInputStream();
					Properties prop = new Properties();
					prop.load(in);
					genXml(prop, out);
				}
			}
		} else {
			// throw new BizException("没有读取到文件信息!");
			throw new Exception("没有读取到文件信息!");
		}
	}

	/**
	 * 生成xml的方法
	 * 
	 * @author xianghao
	 * @date 2016年9月29日 上午10:20:45
	 * @param prop
	 *            prop文件
	 * @param out
	 *            输出流
	 * @return
	 * @throws Exception
	 */
	private String genXml(Properties prop, OutputStream out) throws Exception {
		// 用于存放安全的url的地址
		List<String> safeList = Lists.newArrayList();

		Element rootElement = DocumentHelper.createElement("permissions");
		Document document = DocumentHelper.createDocument(rootElement);
		// 给根节点添加属性
		rootElement.addAttribute("prefix", "");
		// 获取appname
		String appName = prop.getProperty("appname");
		Enumeration<Object> keys = prop.keys();
		Splitter splitter = Splitter.on(";").omitEmptyStrings().trimResults();

		// 用于xml中exclude节点
		Element excludeElement = rootElement.addElement("exclude");
		// 有几个每个app都有的属性
		excludeElement.addElement("path").addAttribute("value", "/sys/itpub/common/**/*.do")
				.addAttribute("type", "other").addAttribute("needLogin", "true").addAttribute("expr", "true");
		excludeElement.addElement("path").addAttribute("value", "/sys/emapcomponent/**/*.do")
				.addAttribute("type", "global").addAttribute("needLogin", "true").addAttribute("expr", "true");
		excludeElement.addElement("path").addAttribute("value", "/code/*.do").addAttribute("type", "global")
				.addAttribute("needLogin", "true").addAttribute("expr", "true");
		// /sys/funauthapp/api/getAppConfig/*.do;
		excludeElement.addElement("path").addAttribute("value", "/sys/funauthapp/api/getAppConfig/*.do")
				.addAttribute("type", "normal").addAttribute("needLogin", "true").addAttribute("expr", "false");
		// /sys/xyhd/index.do
		excludeElement.addElement("path").addAttribute("value", "/sys/" + appName + "/index.do")
				.addAttribute("type", "normal").addAttribute("needLogin", "true").addAttribute("expr", "false");
		// /sys/xyhd/auth.do
		excludeElement.addElement("path").addAttribute("value", "/sys/" + appName + "/auth.do")
				.addAttribute("type", "normal").addAttribute("needLogin", "true").addAttribute("expr", "false");
		// /sys/xyhd/api/*
		excludeElement.addElement("path").addAttribute("value", "/sys/" + appName + "/api/*")
				.addAttribute("type", "normal").addAttribute("needLogin", "true").addAttribute("expr", "false");
		// /sys/xyhd/configSet/*/getRouteConfig.do
		excludeElement.addElement("path").addAttribute("value", "/sys/" + appName + "/configSet/*/getRouteConfig.do")
				.addAttribute("type", "global").addAttribute("needLogin", "true").addAttribute("expr", "true");
		while (keys.hasMoreElements()) {
			// key后缀为0表示安全的 1表示不安全的
			String key = StringUtil.getString(keys.nextElement());
			if ("appname".equals(key)) {
				continue;
			}
			if ("".equals(prop.getProperty(key))) {
				continue;
			}
			if (key.endsWith("0")) {
				// path节点
				List<String> pathList = splitter.splitToList(prop.getProperty(key));
				safeList.addAll(pathList);
			} else if (key.endsWith("1")) {
				String gnbs = key.substring(0, key.length() - 1);
				// 查询数据库 用于填充permission的name cn GNBS PX LX的值
				String sql = "SELECT GNID, GNMC, PX, LX FROM T_FUNC_LIST WHERE GNBS = ? AND APPNAME = ?";
				Map<String, Object> result = DbUtil.queryRow(sql, new Object[] { gnbs, appName });
				String name = StringUtil.getString(result.get("GNID"));
				String gnmc = StringUtil.getString(result.get("GNMC"));
				String px = StringUtil.getString(result.get("PX"));
				String lx = StringUtil.getString(result.get("LX"));

				// 添加permission节点
				Element permissionElement = rootElement.addElement("permission");
				permissionElement.addAttribute("name", name).addAttribute("cn", gnmc).addAttribute("menu", "true");
				// attribute节点
				permissionElement.addElement("attribute").addAttribute("name", "GNBS").addAttribute("value", gnbs);
				permissionElement.addElement("attribute").addAttribute("name", "PX").addAttribute("value", px);
				permissionElement.addElement("attribute").addAttribute("name", "LX").addAttribute("value", lx);
				// 如果没有这个属性 直接continue
				// if("".equals(prop.getProperty(key))) {
				// permissionElement.addElement("path").addAttribute("value",
				// "/sys/itpub/**/*.do")
				// .addAttribute("expr", "true");
				// continue;
				// }
				// path节点
				List<String> pathList = splitter.splitToList(prop.getProperty(key));
				for (String path : pathList) {
					permissionElement.addElement("path").addAttribute("value", path).addAttribute("expr", "true");
				}
			}
		}
		// 添加exclude标签
		if (ListUtil.isNotEmpty(safeList)) {
			for (String path : safeList) {
				excludeElement.addElement("path").addAttribute("value", path).addAttribute("expr", "true")
						.addAttribute("type", "normal").addAttribute("needLogin", "true").addAttribute("expr", "false");
			}
		}
		// 把生成的xml文档存放在硬盘上 true代表是否换行
		OutputFormat format = new OutputFormat("    ", true);
		format.setEncoding("UTF-8");// 设置编码格式
		XMLWriter xmlWriter = new XMLWriter(out, format);
		xmlWriter.write(document);
		xmlWriter.close();
		return document.asXML();
	}
}
