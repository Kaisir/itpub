/*
 * @Project powerauth
 * @Package com.wisedu.emap.itauth.service.impl
 * @date 2016年5月14日 下午2:59:21
 * @author wjfu 01116035
 */
package com.wisedu.emap.itpub.service.impl;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import com.wisedu.emap.auth.IUser;
import com.wisedu.emap.base.util.ListUtil;
import com.wisedu.emap.base.util.StringUtil;
import com.wisedu.emap.itpub.bean.Constants;
import com.wisedu.emap.itpub.bean.HttpClientParam;
import com.wisedu.emap.itpub.bean.auth.AuthModule;
import com.wisedu.emap.itpub.bean.auth.WeAuthItem;
import com.wisedu.emap.itpub.bean.auth.WeUser;
import com.wisedu.emap.itpub.bean.auth.WeUserGroup;
import com.wisedu.emap.itpub.bean.cache.CacheManager;
import com.wisedu.emap.itpub.bean.soap.UserAppRequest;
import com.wisedu.emap.itpub.service.IConfigService;
import com.wisedu.emap.itpub.util.DbUtil;
import com.wisedu.emap.itpub.util.EnvUtils;
import com.wisedu.emap.itpub.util.HttpUtils;
import com.wisedu.emap.itpub.util.JsonUtils;
import com.wisedu.emap.itpub.util.UserUtils;
import com.wisedu.emap.itpub.util.wec.OpenApiUtils;
import com.wisedu.emap.model2.IEmapAction;
import com.wisedu.emap.mvc.CurrentThread;
import com.wisedu.emap.pedestal.app.IEmapApp;
import com.wisedu.emap.pedestal.app.IEmapAppContext;
import com.wisedu.emap.pedestal.core.AppManager;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import self.micromagic.util.annotation.Config;

/**
 * @filename ConfigServiceImpl.java
 * @date 2016年5月14日 下午2:59:21
 * @author wjfu 01116035
 */
@Service
@Slf4j
public class ConfigServiceImpl implements IConfigService {

	@Autowired
	private IEmapAppContext content;

	@Config(name = "funauthapp.func_permission_check", description = "功能权限是否启用", defaultValue = "false")
	private static boolean funcPermissionCheck;

	public Map<String, List<String>> packButtonAuth(List<Map<String, Object>> menuList) {
		Map<String, List<String>> result = new HashMap<String, List<String>>();

		List<String> list = null;
		String ls = null;
		String gnId = null;
		String route = null;
		for (Map<String, Object> map : menuList) {
			ls = (String) map.get("LS");
			gnId = (String) map.get("GNID");
			route = (String) map.get("GNBS");

			// 菜单级
			if (StringUtils.isEmpty(ls)) {
				list = result.get(gnId);
				if (null == list) {
					list = new ArrayList<String>();
					result.put(gnId, list);
				}
			}
			// 按钮级
			else {
				list = result.get(ls);

				if (null == list) {
					list = new ArrayList<String>();
				}

				list.add(route);
				result.put(ls, list);
			}
		}

		return result;
	}

	@Override
	public List<Map<String, Object>> getUserLimit(String appName) throws Exception {
		List<String> groupIds = queryUserGroupIds(appName);
		if (ListUtil.isNotEmpty(groupIds)) {
			if (funcPermissionCheck) {
				return getNewPermissionLimits(groupIds);
			} else {
				return getLimitsList(groupIds);
			}
		}
		return Lists.newArrayList();
	}

	/**
	 * 获取新的授权列表的方法 此为开启安全权限后的获取移动权限方式
	 * 
	 * @date 2016年11月7日 下午2:38:19
	 * @author wjfu 01116035
	 * @param params
	 * @return
	 * @throws Exception
	 */
	private List<Map<String, Object>> getNewPermissionLimits(List<String> params) throws Exception {
		String whereInStr = DbUtil.genWhereInSub(params.size());
		String sql = "  SELECT GNBS, GNMC           " + "  FROM T_FUNC_LIST            "
				+ "  WHERE GNFW = 'MOBILE'       " + "      AND GNID IN (           "
				+ "          SELECT SUBID         " + "          FROM EMAP_SAPP_SUB_ROLE    "
				+ "          WHERE INUSE = 1 AND ROLEID IN " + whereInStr + ")";
		return DbUtil.defaultEmptyQuery(sql, params.toArray());
	}

	/**
	 * 获取用户拥有的权限的用户组id集合
	 * 
	 * @date 2016年8月12日 下午2:51:09
	 * @author wjfu 01116035
	 * @param appName
	 * @return
	 * @throws Exception
	 */
	private List<String> queryUserGroupIds(String appName) throws Exception {
		String userId = UserUtils.getCurUserId();
		String appId = AppManager.getInstance().getApp(appName).getId();
		Map<String, Object> resultObject = getGroups(appId, userId);
		List<String> ids = Lists.newArrayList();
		if ("200".equals(resultObject.get("status").toString())) {
			// 请求成功返回
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> groups = (List<Map<String, Object>>) resultObject.get("groups");
			if (ListUtil.isNotEmpty(groups)) {
				for (int i = 0; i < groups.size(); i++) {
					Map<String, Object> group = groups.get(i);
					String id = (String) group.get("groupId");
					if (StringUtils.isEmpty(id)) {
						continue;
					}
					ids.add(id);
				}
			}
		}
		return ids;
	}

	/**
	 * 获取用户的权限信息
	 * 
	 * @date 2016年8月12日 下午2:52:28
	 * @author wjfu 01116035
	 * @param params
	 * @return
	 * @throws Exception
	 */
	private List<Map<String, Object>> getLimitsList(List<String> params) throws Exception {
		String whereInStr = DbUtil.genWhereInSub(params.size());
		String sql = "SELECT GNBS, GNMC" + " FROM T_FUNC_LIST" + " WHERE GNFW = 'MOBILE'" + " AND GNID IN ("
				+ " SELECT GNID" + "  FROM T_FUNC_AUTH" + " WHERE ROLEID IN " + whereInStr + ")";
		return DbUtil.defaultEmptyQuery(sql, params.toArray());
	}

	/**
	 * 获取用户组
	 * 
	 * @author xianghao
	 * @date 2016年8月2日 上午9:28:56
	 * @param appId
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getGroups(String appId, String userId) {
		IEmapAction<Object> action = content.getAction("getGroupsByAppIdAndUserId").createAction(null);
		Map<String, Object> params = new HashMap<String, Object>();
		UserAppRequest requestObj = new UserAppRequest();
		requestObj.setAppId(appId);
		requestObj.setUserId(userId);
		params.put("userAppRequest", requestObj);
		return (Map<String, Object>) action.execute(params);
	}

	@Override
	public Map<String, Object> getCloudRoute(IEmapApp curApp) throws Exception {
		Map<String, Object> configMap = Maps.newHashMap();
		IUser user = CurrentThread.getUser();
		if (user == null || user.isGuest()) {
			throw new Exception("未获取到用户信息,请先登录");
		}
		WeUser weUser = UserUtils.getCloudUser();
		List<WeUserGroup> groups = weUser.getGroups();
		// 获取用户当前角色的ID
		String roleId = user.getItem(IUser.APP_ROLE_PREFIX.concat(curApp.getName()));
		// 如果当前角色无法直接获得 则在其属性中获取
		if (StringUtils.isEmpty(roleId)) {
			roleId = user.getRoleId();
			// 如果roleId仍然为空 则设置默认roleId
			if (StringUtil.isEmpty(roleId)) {
				if (ListUtil.isNotEmpty(groups)) {
					roleId = groups.get(0).getGroupId();
					user.changeRole(roleId);
					// 如果选择用户 组信息为空 则直接返回空
				} else {
					return null;
				}
			}
		}
		// 下拉角色用户组菜单填充
		// List<Object> dropMenus = Lists.newArrayList();
		// if(ListUtil.isNotEmpty(groups)) {
		// for(WeUserGroup group : groups) {
		// Map<String, Object> dropMenu = Maps.newHashMap();
		// if(group.getGroupId().equals(roleId)) {
		// dropMenu.put("active", true);
		// }
		// dropMenu.put("id", group.getGroupId());
		// dropMenu.put("text", group.getGroupName());
		// dropMenus.add(dropMenu);
		// }
		// }
		// headerMap.put("dropMenu", dropMenus);
		// 组装头信息
		Map<String, Object> headerMap = Maps.newHashMap();
		Map<String, String> logoutHref = Maps.newHashMap();
		logoutHref.put("logoutHref", EnvUtils.getLogoutUrl());
		headerMap.put("userInfo", logoutHref);
		configMap.put("HEADER", headerMap);
		configMap.put("APP_ID", curApp.getId());
		// 组装功能列表信息
		configMap.put("MODULES", packAuthModules(curApp, roleId));
		return configMap;
	}

	private List<Map<String, Object>> packAuthModules(IEmapApp curApp, String roleId) throws Exception {
		List<Map<String, Object>> modules = Lists.newArrayList();
		// 获取公开API的授权信息 此授权信息需要
		HttpClientParam httpParam = new HttpClientParam();
		httpParam.setUrl(OpenApiUtils.loadUrl("getAuthIds"));
		JsonObject params = new JsonObject();
		JsonArray groupIds = new JsonArray();
		for (WeUserGroup group : UserUtils.getCloudUser().getGroups()) {
			groupIds.add(new JsonPrimitive(group.getGroupId()));
		}
		params.addProperty("appId", curApp.getId());
		params.add("groupIds", groupIds);
		httpParam.setParams(params);
		List<Header> headers = Lists.newArrayList();
		headers.add(Constants.JSON_HEADER);
		headers.add(new BasicHeader(Constants.CLOUD_SCHOOL_HEADER, UserUtils.getSchoolId()));
		httpParam.setHeaders(headers);
		String resultStr = HttpUtils.apiPost("getAuthIds", httpParam);
		log.info("应用授权API返回结果为：" + resultStr);
		if (StringUtil.isEmpty(resultStr)) {
			return null;
		}
		String authBeanStr = handleResult(resultStr);
		if (StringUtil.isEmpty(authBeanStr)) {
			return null;
		}
		Type type = new TypeToken<List<WeAuthItem>>() {
		}.getType();
		List<WeAuthItem> authItems = JsonUtils.json2BeanList(authBeanStr, type);
		// 得到缓存的模块信息
		Map<String, AuthModule> authItemMap = CacheManager.getInstance().getAppModuleMap(curApp.getName());
		log.info("应用app_info授权信息为：" + JsonUtils.toJsonStr(authItemMap));
		if (ListUtil.isEmpty(authItemMap)) {
			return null;
		}
		if (ListUtil.isEmpty(authItems)) {
			return null;
		}
		List<String> authIds = Lists.newArrayList();
		for (WeAuthItem module : authItems) {
			if (ListUtil.isNotEmpty(module.getItemIds())) {
				authIds.addAll(module.getItemIds());
			}
		}
		if (ListUtil.isEmpty(authIds)) {
			return null;
		}
		// 通过ID组装模块的功能信息
		for (String authId : authIds) {
			AuthModule authModule = authItemMap.get(authId);
			if (null == authModule) {
				continue;
			}
			Map<String, Object> moduleMap = Maps.newHashMap();
			moduleMap.put("title", authModule.getModuleName());
			moduleMap.put("route", authModule.getModulePath());
			modules.add(moduleMap);
		}
		log.info("当前用户的功能列表为：" + JsonUtils.toJsonStr(modules));
		return modules;
	}

	@Override
	public Map<String, Object> changeAppRole(String appName, String roleId) throws Exception {
		IUser user = CurrentThread.getUser();
		if (user == null || user.isGuest()) {
			throw new Exception("未获取到用户信息,请先登录");
		}
		user.setItem(IUser.APP_ROLE_PREFIX.concat(appName), roleId);
		log.info("APP ROLE CHANGED " + JsonUtils.toJsonStr(user));
		return ImmutableMap.<String, Object> of("success", true);
	}

	private String handleResult(String resultStr) {
		Map<String, Object> wrapAuthBean = JsonUtils.json2Map(resultStr);
		if (!"0".equals(wrapAuthBean.get("status"))) {
			log.error(" POST RESULT FAIL WITH " + wrapAuthBean.get("msg"));
			return null;
		}
		return StringUtil.getString(wrapAuthBean.get("authIds"));
	}

}
