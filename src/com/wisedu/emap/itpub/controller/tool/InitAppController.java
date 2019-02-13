package com.wisedu.emap.itpub.controller.tool;

import com.wisedu.emap.base.util.StringUtil;
import com.wisedu.emap.itpub.util.GenTemplateUtil;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 新建emap应用后，调用此初始化方法，自动生成匹配itpub用法的模板文件
 * 
 * @author 01118203
 *
 */
@Controller
public class InitAppController {

	@ResponseBody
	@RequestMapping("/initApp.do")
	public String initAppTemplate(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// 应用名称
		String appName = request.getParameter("appName");
		// 应用中文名
		String appCnName = request.getParameter("appCnName");
		if (StringUtil.isEmpty(appName) || StringUtil.isEmpty(appCnName))
			return "参数错误！";
		appCnName = new String(appCnName.getBytes("ISO-8859-1"), "UTF-8");

		// 设置参数
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("appName", appName);
		params.put("appCnName", appCnName);

		GenTemplateUtil gtu = new GenTemplateUtil(params);

		return gtu.genTemplateFiles();
	}

	@ResponseBody
	@RequestMapping("/genDao.do")
	public String genDaoImpl(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// 应用名称
		String appName = request.getParameter("appName");
		// dao类名
		String daoName = request.getParameter("daoName");
		if (StringUtil.isEmpty(appName) || StringUtil.isEmpty(daoName))
			return "参数错误！";

		// 设置参数
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("appName", appName);
		params.put("daoName", daoName);

		GenTemplateUtil gtu = new GenTemplateUtil(params);

		return gtu.genDaoImplFiles();
	}

}
