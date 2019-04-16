package com.wisedu.emap.itpub.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.ibm.icu.math.BigDecimal;
import com.wisedu.emap.auth.IUser;
import com.wisedu.emap.base.util.ListUtil;
import com.wisedu.emap.base.util.StringUtil;
import com.wisedu.emap.itpub.bean.BizException;
import com.wisedu.emap.itpub.bean.Constants;
import com.wisedu.emap.itpub.bean.HttpClientParam;
import com.wisedu.emap.itpub.bean.ItUser;
import com.wisedu.emap.itpub.bean.Receivers;
import com.wisedu.emap.itpub.bean.auth.WeUser;
import com.wisedu.emap.itpub.bean.auth.WecUserResponse;
import com.wisedu.emap.itpub.bean.rest.UndergraduateInfo;
import com.wisedu.emap.mvc.CurrentInfo;
import com.wisedu.emap.mvc.CurrentThread;
import com.wisedu.emap.pedestal.core.AppManager;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 用户工具类
 * 
 */

@Slf4j
public class UserUtils {

	private UserUtils() {
	}

	/**
	 * 获取当前登陆用户的id
	 * 
	 * @author wjfu 01116035 @date 2016年3月21日 下午1:48:41 @return String @throws
	 */
	public static String getCurUserId() {
		// if (EnvUtils.isCloudEnv()) {
		// return getCloudUser().getUserAccount();
		// }
		// 先从线程中获取当前登陆用户 如果不是 则尝试从request中获取
		IUser user = getCurrentUser();
		String userId = "";
		// log.info("!!!!用户信息:" + JsonUtils.toJsonStr(user));
		if (null != user) {
			userId = user.getId();
		} else {
			userId = HttpUtils.getRequest().getHeader("cas_user");
		}
		return userId;
	}

	/**
	 * 获取公有云用户信息
	 * 
	 * @date 2016年12月2日 下午1:47:16
	 * @author wjfu 01116035
	 * @return
	 */
	public static WeUser getCloudUser() {
		// 代理方式 TODO 需要重新处理 此处更适合redis缓存当前个人信息 因为个人信息通常来说是不怎么变动的
		String userId = getCurUserId();
		JsonObject params = new JsonObject();
		params.addProperty("schoolId", getSchoolId());
		params.addProperty("userId", userId);
		HttpClientParam httpParam = new HttpClientParam();
		httpParam.setParams(params);
		try {
			String result = HttpUtils.apiPost("getUserInfo", httpParam);
			WecUserResponse wecUserRes = JsonUtils.json2Bean(result, WecUserResponse.class);
			WeUser weUser = wecUserRes.getUserInfo();
			if (null == weUser) {
				weUser = new WeUser(userId);
			}
			return weUser;
		} catch (Exception e1) {
			log.error("请求用户信息异常: " + e1.getMessage(), e1);
		}
		return new WeUser(userId);
		/**
		 * String encodedUserInfo =
		 * HttpUtils.getRequest().getHeader("wecUserInfo"); // oauth方式获取用户信息 //
		 * String encodedUserInfo = null; try { // encodedUserInfo =
		 * WeCloudOAuthHelper.getLoginUserInfo();
		 * if(StringUtil.isNotEmpty(encodedUserInfo)) { String decodedUserInfo =
		 * URLDecoder.decode(encodedUserInfo, "UTF-8"); return
		 * JsonUtils.json2Bean(decodedUserInfo, WeUser.class); } } catch
		 * (UnsupportedEncodingException e) { log.error("获取用户信息无法解码,源用户信息串为:" +
		 * encodedUserInfo, e); } catch (Exception e) { log.error("获取用户信息失败，原因："
		 * + e.getMessage(), e); } return new WeUser();
		 */
	}

	/**
	 * 获取当前登陆用户对象
	 * 
	 * @author wjfu 01116035 @date 2016年3月21日 下午1:48:54 @return IUser @throws
	 */
	public static IUser getCurrentUser() {
		return CurrentThread.getUser();
	}

	/**
	 * 获取当前用户的姓名
	 * 
	 * @author zhuangyuhao
	 * @date 2016年5月13日 下午2:48:03
	 * @return
	 */
	public static String getCurrentUserName() throws Exception {
		if (EnvUtils.isCloudEnv()) {
			return getCloudUser().getUserName();
		}
		String name = null;
		List<Map<String, Object>> userList = UserUtils.getUsersInfo(Lists.newArrayList(UserUtils.getCurUserId()));
		if (ListUtil.isNotEmpty(userList)) {
			Map<String, Object> userMap = userList.get(0);
			name = StringUtil.getString(userMap.get("name"));
		} else {
			throw new BizException("获取不到当前用户的信息");
		}
		return name;
	}

	/**
	 * 获取当前登陆用户的信息
	 * 
	 * @date 2016年4月13日 下午6:57:39
	 * @author wjfu 01116035
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> getCurrentUserInfo() {
		Map<String, Object> userInfo = Maps.newHashMap();
		if (EnvUtils.isCloudEnv()) {
			fillCloudUser(userInfo);
		} else {
			fillPrivateUser(userInfo);
		}
		return userInfo;
	}

	private static void fillPrivateUser(Map<String, Object> userInfo) {
		String isDept = ITPubPropsUtil.getProp("api.fromDept", "false");
		// 私有云走API的集成方式
		List<Map<String, Object>> users = Lists.newArrayList();
		String userId = getCurUserId();
		try {
			users = getUsersInfo(Lists.newArrayList(userId));
		} catch (Exception e) {
			log.error("CURRENT USER INFO GET ERROR: " + e.getMessage(), e);
		}
		if (null != users && !users.isEmpty()) {
			Map<String, Object> userMap = users.get(0);
			// null值转空字符串
			Set<Entry<String, Object>> entries = userMap.entrySet();
			Iterator<Entry<String, Object>> it = entries.iterator();
			while (it.hasNext()) {
				Entry<String, Object> next = it.next();
				next.setValue(next.getValue() != null ? next.getValue() : "");
			}

			List<String> infos = Lists.newArrayList();
			infos.add(userId);
			infos.add(StringUtil.getString(userMap.get("name")));
			infos.add(StringUtil.getString(userMap.get("sexName")));
			String deptName = isDept == "false" ? StringUtil.getString(userMap.get("academyName"))
					: StringUtil.getString(userMap.get("deptName"));
			if (!StringUtils.isEmpty(deptName)) {
				infos.add(deptName);
			}
			userInfo.put("info", infos);
			userInfo.put("deptCode", isDept == "false" ? StringUtil.getString(userMap.get("academyCode"))
					: StringUtil.getString(userMap.get("academyCode")));
			userInfo.put("deptName", isDept == "false" ? StringUtil.getString(userMap.get("academyName"))
					: StringUtil.getString(userMap.get("deptName")));
			userInfo.put("phone", userMap.get("phone"));
			userInfo.put("email", userMap.get("email"));
			userInfo.put("identityCode", userMap.get("identityCode"));
			String userType = StringUtil.getString(userMap.get("userType"));
			if ("STUDENT".equalsIgnoreCase(userType)) {
				// String deptName =
				// StringUtil.getString(userMap.get("deptName"));
				if (!StringUtils.isEmpty(deptName)) {
					infos.add(deptName);
				}
				// 如果是学生的话选择的是学院
				userInfo.put("deptCode", userMap.get("deptCode"));
				userInfo.put("deptName", userMap.get("deptName"));
				String grade = StringUtil.getString(userMap.get("grade"));
				String className = StringUtil.getString(userMap.get("className"));
				StringBuilder userPos = new StringBuilder();
				if (StringUtil.isNotEmpty(grade)) {
					// CONFIRM GRADES LEN IS CERTENLY LONGER THAN 2
					int gradeLen = grade.length();
					if (gradeLen > 2) {
						userPos.append(grade.substring(gradeLen - 2)).append("级");
					} else {
						userPos.append(grade).append("级");
					}
				}
				if (StringUtil.isNotEmpty(className)) {
					userPos.append(className);
					if (!className.endsWith("班")) {
						userPos.append("班");
					}
				}
				userInfo.put("userPos", userPos);
			} else if ("TEACHER".equalsIgnoreCase(userType)) {

				// String deptName =
				// StringUtil.getString(userMap.get("deptName"));
				if (!StringUtils.isEmpty(deptName)) {
					infos.add(deptName);
				}
				// 如果是老师的话选择的是部门
				// userInfo.put("deptCode", userMap.get("deptCode"));
				// userInfo.put("deptName", userMap.get("deptName"));
				userInfo.put("userPos", userMap.get("postion"));
			}
			userInfo.put("userName", userMap.get("name"));
			userInfo.put("userType", userType);
			userInfo.put("sexName", userMap.get("sexName"));
			String birthStr = StringUtil.getString(userMap.get("birthday"));
			if (!"".equals(birthStr)) {
				try {
					userInfo.put("birthday", birthStr);
					userInfo.put("age", DateUtils.getAgeByBirthday(birthStr));
				} catch (Exception e) {
					// 生日日期获取失败
					log.error("生日计算失败,传入生日日期为:" + birthStr, e);
				}
			}
			userInfo.put("image", HttpUtils.getRequestBaseUrl() + "itpub/common/headPic.do?id=" + userId);
		}
	}

	private static void fillCloudUser(Map<String, Object> userInfo) {
		WeUser weUser = getCloudUser();
		List<String> infos = Lists.newArrayList();
		infos.add(weUser.getUserAccount());
		infos.add(weUser.getUserName());
		infos.add(weUser.getUserGender());
		String deptName = weUser.getUserDepartment();
		if (!StringUtils.isEmpty(deptName)) {
			infos.add(deptName);
		}
		userInfo.put("info", infos);
		userInfo.put("deptCode", weUser.getUserDepartmentCode());
		userInfo.put("deptName", weUser.getUserDepartment());
		// 公有云暂时无法获取到手机和邮箱 此处设置为空
		userInfo.put("phone", null);
		userInfo.put("email", null);
		userInfo.put("userName", weUser.getUserName());
		String userType = weUser.getUserType();
		if ("STUDENT".equalsIgnoreCase(userType)) {
			String grade = weUser.getUserGrade();
			String className = weUser.getUserClass();
			StringBuilder userPos = new StringBuilder();
			if (StringUtil.isNotEmpty(grade)) {
				userPos.append(grade.substring(grade.length() - 2)).append("级");
			}
			if (StringUtil.isNotEmpty(className)) {
				userPos.append(className);
				if (!className.endsWith("班")) {
					userPos.append("班");
				}
			}
			userInfo.put("userPos", userPos);
		}
		userInfo.put("userType", userType);
		userInfo.put("sexName", weUser.getUserGender());
		userInfo.put("image", weUser.getUserAvatar());
	}

	/**
	 * 通过用户ids获取消息通知的接受人列表
	 * 
	 * @date 2016年4月19日 上午10:45:36
	 * @author wjfu 01116035
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public static List<Receivers> getReceiversByIds(List<String> userId) throws Exception {
		List<Receivers> recList = Lists.newArrayList();
		// 公有云环境下 直接返回有ID的接收人即可
		if (EnvUtils.isCloudEnv()) {
			for (String oneId : userId) {
				// 公有云只支持ID推送 无email和手机
				recList.add(new Receivers(oneId, null, null, null));
			}
			return recList;
		}
		List<Map<String, Object>> users = getUsersInfo(userId);
		if (null == users || users.isEmpty()) {
			return recList;
		}
		for (Map<String, Object> userMap : users) {
			Receivers rec = new Receivers();
			rec.setUserId(StringUtil.getString(userMap.get("id")));
			rec.setEmail(StringUtil.getString(userMap.get("email")));
			rec.setMobile(StringUtil.getString(userMap.get("phone")));
			// flag 0-主送 1-抄送 2-密送
			rec.setFlag((short) 0);
			recList.add(rec);
		}
		return recList;
	}

	/**
	 * 查询当前id的用户信息 如果不存在当前用户 至少返回当前用户的id
	 * 
	 * @date 2016年4月21日 上午11:37:10
	 * @author wjfu 01116035
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public static ItUser getUserByUserId(String userId) throws Exception {
		if (EnvUtils.isCloudEnv()) {
			return transWeUser2ItUser();
		}

		ItUser user = new ItUser();
		List<Map<String, Object>> usersMap = getUsersInfo(Lists.newArrayList(userId));
		if (null == usersMap) {
			user.setId(userId);
		} else {
			Map<String, Object> userMap = usersMap.get(0);
			BeanUtils.populate(user, userMap);
		}
		return user;
	}

	private static ItUser transWeUser2ItUser() {
		ItUser user = new ItUser();
		WeUser weUser = getCloudUser();
		user.setId(weUser.getUserAccount());
		user.setName(weUser.getUserName());
		user.setUserType(weUser.getUserType());
		user.setAcademyCode(weUser.getUserDepartmentCode());
		user.setAcademyName(weUser.getUserDepartment());
		user.setClassCode(weUser.getUserClassCode());
		user.setClassName(weUser.getUserClass());
		user.setDeptCode(weUser.getUserDepartmentCode());
		user.setAcademyName(weUser.getUserDepartment());
		user.setGrade(weUser.getUserGrade());
		user.setMajorCode(weUser.getUserMajorCode());
		user.setMajorName(weUser.getUserMajor());
		user.setSexCode(weUser.getUserGenderCode());
		user.setSexName(weUser.getUserGender());
		return user;
	}

	/**
	 * 通过传入的id通过接口获取用户的信息
	 * 
	 * @date 2016年4月21日 上午11:02:46
	 * @author wjfu 01116035
	 * @param userIds
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> getUsersInfo(List<String> userIds) throws Exception {
		JsonObject params = new JsonObject();
		JsonElement ids = JsonUtils.toJsonElement(userIds);
		params.add(Constants.IDS_STR, ids);
		String userRes = HttpUtils.confPost("getUserDetailInfoByIds", params);
		Map<String, Object> user = JsonUtils.json2Map(userRes);
		if (null == user) {
			return null;
		}
		List<Map<String, Object>> users = (List<Map<String, Object>>) user.get("userInfoWithDepts");
		if (ListUtil.isEmpty(users)) {
			return null;
		}
		return users;
	}

	/**
	 * 获取本科生基本信息
	 * 
	 * @date 2016年4月21日 上午11:02:46
	 * @author wjfu 01116035
	 * @param userIds
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> getUndergraduateInfo(String userId) throws Exception {
		JsonObject params = new JsonObject();
		UndergraduateInfo undergraduateInfo = new UndergraduateInfo();
		undergraduateInfo.setXH(userId);
		JsonElement undergraduateInfo_json = JsonUtils.toJsonElement(undergraduateInfo);
		params.add(Constants.PARAM, undergraduateInfo_json);
		String userRes = HttpUtils.confPost("getUndergraduateInfo", params);
		Map<String, Object> user = JsonUtils.json2Map(userRes);
		if (null == user) {
			return null;
		}
		List<Map<String, Object>> users = (List<Map<String, Object>>) user.get("result");
		if (ListUtil.isEmpty(users)) {
			return null;
		}
		return users;
	}

	/**
	 * 获取人员信息
	 * 
	 * @date 2016年4月21日 上午11:02:46
	 * @author wjfu 01116035
	 * @param userIds
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> getUserInfosByIds(String userId, String userType) throws Exception {
		JsonObject params = new JsonObject();
		JsonElement ids = JsonUtils.toJsonElement(Lists.newArrayList(userId));
		JsonElement types = JsonUtils.toJsonElement(userType);
		params.add(Constants.USER_TYPE, types);
		params.add(Constants.IDS_STR, ids);
		String userRes = HttpUtils.confPost("getUserInfosByIds", params);
		Map<String, Object> user = JsonUtils.json2Map(userRes);
		if (null == user) {
			return null;
		}
		List<Map<String, Object>> users = (List<Map<String, Object>>) user.get("users");
		if (ListUtil.isEmpty(users)) {
			return null;
		}
		return users;
	}

	/**
	 * 设置应用变量
	 * 
	 * @author mengbin
	 * @date 2016年4月14日 上午11:23:15
	 */
	public static String setAppVar(HttpServletRequest request) {
		if (request.getAttribute("contextPath") == null) {
			// 应用相关变量
			String appName = CurrentInfo.getInfo().getAppName();
			String parentAppname = AppManager.currentApp().getParent().getName();
			String contextPath = request.getContextPath();
			request.setAttribute("contextPath", contextPath);
			String basePath = contextPath + "/sys/" + appName;
			request.setAttribute("basePath", basePath);
			String modulePath = "/sys/" + appName;
			request.setAttribute("modulePath", modulePath);
			request.setAttribute("APPNAME", appName);
			String appId = AppManager.getInstance().getApp(appName).getId();
			request.setAttribute("APPID", appId);
			// 学校的ID
			String schoolId = ITPubPropsUtil.getProp(Constants.SCHOOL_CODE_KEY, "wisedu");
			// String systemPath = getSystemBasePath();
			// request.setAttribute("systemPath", systemPath);
			String parentBasePath = contextPath + "/sys/" + parentAppname;
			request.setAttribute("parentBasePath", parentBasePath);
			// 用户相关变量
			request.setAttribute("USERID", getCurUserId());
			// String groupId = EmapUser.getAppRole(user, appName);
			String groupId = getCurGroupId();
			request.setAttribute("GROUPID", groupId);
			// 前端资源相关变量
			String resServer = ITPubPropsUtil.getProp(Constants.COMMON_RES_SERVER, "http://res.wisedu.com");
			request.setAttribute("resServer", resServer);
			request.setAttribute("bower_components", resServer + "/bower_components");
			request.setAttribute("fe_components", resServer + "/fe_components");
			request.setAttribute("product", resServer + "/products/" + appName);

			// 页脚版权信息
			String footerText = ITPubPropsUtil.getProp("FOOTER_VERSION_INFO",
					"版权信息：© 2019 江苏金智教育信息股份有限公司 苏ICP备10204514号");
			footerText = footerText.replaceAll("'", "\"");
			request.setAttribute("footerText", footerText);
			/*
			 * Map<String, Object> userInfo = Maps.newHashMap();
			 * userInfo.put("image",
			 * "http://res.wisedu.com/scenes/public/images/demo/user1.png");
			 * userInfo.put("info", getCurrentUserInfo().get("info"));
			 */
			String userInfoJson = JsonUtils.toJsonStr(getCurrentUserInfo());
			String appTitle = ITPubPropsUtil.getProp("APP_TITLE." + appName.toUpperCase(), "");
			request.setAttribute("appTitle", appTitle);

			StringBuilder buf = new StringBuilder();
			buf.append("<script>contextPath='").append(contextPath).append("';basePath='").append(basePath)
					.append("';modulePath='").append(modulePath).append("';APPNAME='").append(appName)
					.append("';APPID='").append(appId).append("';SCHOOLID='").append(schoolId).append("';USERID='")
					.append(getCurUserId()).append("';GROUPID='").append(StringUtil.getString(groupId))
					.append("';resServer='").append(resServer).append("';FOOTER_TEXT='").append(footerText)
					.append("';USER_INFO=").append(userInfoJson).append(";APP_TITLE='").append(appTitle)
					.append("';parentBasePath='").append(parentBasePath).append("';</script>");
			return buf.toString();
		}
		return "";
	}

	public static String getCurGroupId() {
		IUser user = CurrentThread.getUser();
		String appName = CurrentInfo.getInfo().getAppName();
		String roleId = "";
		if (null != user) {
			roleId = user.getItem(IUser.APP_ROLE_PREFIX.concat(appName));
			if (StringUtils.isEmpty(roleId)) {
				roleId = user.getRoleId();
			}
		}
		return roleId;
	}

	public static String getUserPicById(String id, String type) {
		JsonObject param = new JsonObject();
		JsonArray ids = new JsonArray();
		ids.add(new JsonPrimitive(id));
		param.add(Constants.IDS_STR, ids);
		// 1为返回url 2为流
		param.addProperty("resultType", type);
		param.addProperty("sizeType", "1");
		String result = null;
		try {
			result = HttpUtils.confPost("getPhotoById", param);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		if (result != null) {
			Map<String, Object> picResult = JsonUtils.json2Map(result);
			if (ListUtil.isEmpty(picResult) || null == picResult.get(Constants.STATUS)) {
				return null;
			}
			String status = String.valueOf(picResult.get(Constants.STATUS));
			if (Constants.RESP_OK.compareTo(new BigDecimal(status)) == 0) {
				@SuppressWarnings("unchecked")
				List<Map<String, Object>> photosList = (List<Map<String, Object>>) picResult.get("photos");
				if (ListUtil.isNotEmpty(photosList)) {
					Map<String, Object> photoMap = photosList.get(0);
					if ("1".equals(type)) {
						return StringUtil.getString(photoMap.get("url"));
					} else {
						return StringUtil.getString(photoMap.get("zp"));
					}
				}
			}
		}
		// 如果结果为空或调用过程出错 则返回空 由调用者处理
		return null;
	}

	/**
	 * 获取学校的ID
	 * 
	 * @date 2017年1月4日 上午11:11:51
	 * @author wjfu 01116035
	 * @return
	 */
	public static String getSchoolId() {
		if (EnvUtils.isCloudEnv()) {
			return HttpUtils.getRequest().getHeader(Constants.CLOUD_SCHOOL_HEADER);
		} else {
			return ITPubPropsUtil.getProp("MR_PROPS.SCHOOL_CODE", "wisedu");
		}
	}

}
