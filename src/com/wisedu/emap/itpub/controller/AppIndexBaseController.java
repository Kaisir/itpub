package com.wisedu.emap.itpub.controller;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wisedu.emap.base.core.EmapContext;
import com.wisedu.emap.base.util.StringUtil;
import com.wisedu.emap.itpub.bean.Constants;
import com.wisedu.emap.itpub.service.IConfigService;
import com.wisedu.emap.itpub.util.CertUtils;
import com.wisedu.emap.itpub.util.EnvUtils;
import com.wisedu.emap.itpub.util.HttpUtils;
import com.wisedu.emap.itpub.util.JsonUtils;
import com.wisedu.emap.itpub.util.XssUtils;
import com.wisedu.emap.pedestal.app.IEmapApp;
import com.wisedu.emap.pedestal.core.AppManager;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 公共组件：应用的路由控制器
 */
public class AppIndexBaseController extends BaseController {

	// 添加功能链接的js
	private static final String AUTH_LIST_ADD = "auth_list_add";

	@Autowired
	private IConfigService configSV;

	/**
	 * 应用首页登陆路由
	 * 
	 * @param model
	 * @param resp
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/index.do")
	public String index(ModelMap model, HttpServletResponse resp) throws Exception {
		model.put(Constants.CONFIG_TYPE, Constants.CONFIG_NORMAL);
		return getJspPath("/index.jsp");
	}

	/**
	 * 移动跳转地址 需要配置跳转路径 否则跳转当前路由下的index.html
	 * 
	 * @date 2016年12月29日 下午4:40:28
	 * @author wjfu 01116035
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/mobileIndex.do")
	public String mobileIndex(ModelMap model) throws Exception {
		String redirect = HttpUtils.getParameter("redirectTo");
		if (StringUtil.isEmpty(redirect)) {
			redirect = "index.html";
		}
		return "redirect:" + redirect;
	}

	/**
	 * 授权应用界面
	 * 
	 * @author mengbin
	 * @date 2016年4月19日 下午5:11:05
	 * @return
	 */
	@RequestMapping("/auth.do")
	public String auth(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return getAuthUrl(request, response, AUTH_LIST_ADD);
	}

	private String getAuthUrl(HttpServletRequest request, HttpServletResponse response, String... authPrivs)
			throws IOException {
		return getAuthUrl(request, response, true, true, authPrivs);
	}

	/**
	 * 重构增加是否展示权限列表及数据权限列表
	 * 
	 * @param request
	 * @param response
	 * @param needFlow
	 * @param needData
	 * @param authPrivs
	 * @return
	 * @throws IOException
	 */
	private String getAuthUrl(HttpServletRequest request, HttpServletResponse response, boolean needFlow,
			boolean needData, String... authPrivs) throws IOException {
		if (EnvUtils.isCloudEnv()) {
			response.sendError(404);
		}
		IEmapApp appInfo = AppManager.currentApp();
		if (authPrivs != null && authPrivs.length > 0) {
			List<String> scripts = Lists.newArrayList();
			for (String priv : authPrivs) {
				if (priv.endsWith(".js")) {
					scripts.add(priv);
				} else {
					scripts.add(request.getContextPath() + "/sys/itpub/widget/group_auth/" + priv + ".js");
					request.setAttribute(priv, "1");
				}
			}
			request.setAttribute("scripts", scripts);
		}
		// 默认隐藏这两块区域
		if (!needData) {
			request.setAttribute("dataPrivArea", "hide");
		}
		if (!needFlow) {
			request.setAttribute("flowPrivArea", "hide");

		}
		return "forward:/sys/funauthapp/qxgl.do?appId=" + appInfo.getId() + "&appName=" + appInfo.getName();
	}

	/**
	 * 当从任务中心来的链接时候 就认为是流程审批的路由 否则 即为正常路由
	 * 
	 * @date 2016年4月7日 下午4:27:55
	 * @author wjfu 01116035
	 * @param configType
	 * @return
	 */
	@RequestMapping("/configSet/{configType}/getRouteConfig.do")
	public String getRouteConfig(@PathVariable String configType) throws Exception {
		String appName = AppManager.currentApp().getName();
		if (Constants.CONFIG_NORMAL.equals(configType) || "undefined".equals(configType) || "null".equals(configType)
				|| "".equals(configType)) {
			if (EnvUtils.isCloudEnv()) {
				return "forward:/sys/" + appName + "/getPublicCloudRouteConf.do";
			} else {
				String appId = AppManager.currentApp().getId();
				return "forward:/sys/funauthapp/api/getAppConfig/" + appName + "-" + appId + ".do";
			}
		} else {
			return "forward:/sys/" + appName + "/getAppOtherRouteConfig.do?type=" + configType;
		}
	}

	/**
	 * 获取其他路由 非授权类型路由
	 * 
	 * @date 2016年9月30日 下午12:30:29
	 * @author wjfu 01116035
	 * @param type
	 * @return
	 */
	@RequestMapping("/getAppOtherRouteConfig.do")
	public @ResponseBody Map<String, Object> getAppOtherRouteConfig(String type) {
		Map<String, Object> config = Maps.newHashMap();
		List<Map<String, String>> modules = Lists.newArrayList();
		modules.add(ImmutableMap.of("route", type));
		config.put("MODULES", modules);
		return config;
	}

	/**
	 * 获取公有云的授权信息
	 * 
	 * @date 2016年12月2日 下午4:37:45
	 * @author wjfu 01116035
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/getPublicCloudRouteConf.do")
	public @ResponseBody Map<String, Object> getPublicCloudRouteConf() throws Exception {
		return configSV.getCloudRoute(AppManager.currentApp());
	}

	/**
	 * 初始化消息发送的设置
	 * 
	 * @date 2016年11月5日 下午6:58:44
	 * @author wjfu 01116035
	 * @return
	 */
	@RequestMapping("mrSetting/initMrSetting.do")
	public String inintMrSetting() {
		return "forward:/sys/itpub/mrSetting/initMrSetting.do";
	}

	/**
	 * 设置消息发送的开关状态
	 * 
	 * @date 2016年11月5日 下午6:58:59
	 * @author wjfu 01116035
	 * @return
	 */
	@RequestMapping("mrSetting/setMessageRemainChange.do")
	public String setMessageRemainChange() {
		return "forward:/sys/itpub/mrSetting/setMessageRemainChange.do";
	}

	/**
	 * 保存消息提醒的类型
	 * 
	 * @date 2016年11月5日 下午6:59:44
	 * @author wjfu 01116035
	 * @return
	 */
	@RequestMapping("mrSetting/saveMrRemaindTypes.do")
	public String saveMrRemaindTypes() {
		return "forward:/sys/itpub/mrSetting/saveMrRemaindTypes.do";
	}

	/**
	 * 判断是否是手机访问
	 * 
	 * @date 2016年10月20日 下午1:41:57
	 * @author xianghao
	 * @param userAgent
	 * @return
	 */
	public boolean isMobile(String userAgent) {
		String[] mobileTypes = { "Android", "iPhone", "iPod", "iPad", "Windows Phone", "MQQBrowser" };
		for (String mobile : mobileTypes) {
			if (userAgent.contains(mobile)) {
				// 表示是手机访问
				return true;
			}
		}
		return false;
	}

	/**
	 * 展示当前应用版本(从证书获取)
	 * 
	 * @return
	 */
	@RequestMapping("/version.do")
	protected @ResponseBody Object getVersion() {
		if (EmapContext.isManagerUser()) {
			return CertUtils.readCerts(AppManager.currentApp());
		}
		return "403";
	}

	/**
	 * callback 包装类
	 * 
	 * @param request
	 * @param object
	 * @return
	 */
	protected Object callbackWrapper(HttpServletRequest request, Object object) {
		String callback = XssUtils.xssEncode(request.getParameter("callback"));
		if (callback != null && !"".equals(callback)) {
			StringBuffer sb = new StringBuffer();
			sb.append(callback).append("(").append(JsonUtils.toJsonElement(object)).append(")");
			return sb.toString();
		} else {
			return JsonUtils.toJsonElement(object);
		}
	}

	/**
	 * emapol 在线部署工具
	 * 
	 * @return
	 */
	@RequestMapping("emapol.do")
	public String emapol() {
		return "forward:/sys/emapol/*default/index.do";
	}

}
