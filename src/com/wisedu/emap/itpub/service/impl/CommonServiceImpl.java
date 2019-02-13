package com.wisedu.emap.itpub.service.impl;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.wisedu.emap.base.util.ListUtil;
import com.wisedu.emap.base.util.StringUtil;
import com.wisedu.emap.file.util.IFileDownload;
import com.wisedu.emap.itpub.bean.BizException;
import com.wisedu.emap.itpub.bean.Constants;
import com.wisedu.emap.itpub.bean.ItUser;
import com.wisedu.emap.itpub.bean.Receivers;
import com.wisedu.emap.itpub.bean.analysis.AnalysisData;
import com.wisedu.emap.itpub.bean.search.UserSearchCond;
import com.wisedu.emap.itpub.bean.soap.UserAppRequest;
import com.wisedu.emap.itpub.dao.T_ITPUB_PROPS;
import com.wisedu.emap.itpub.service.ICommonService;
import com.wisedu.emap.itpub.util.DbUtil;
import com.wisedu.emap.itpub.util.EmapDataBuildUtil;
import com.wisedu.emap.itpub.util.HttpUtils;
import com.wisedu.emap.itpub.util.ITPubPropsUtil;
import com.wisedu.emap.itpub.util.JsonUtils;
import com.wisedu.emap.itpub.util.MRUtils;
import com.wisedu.emap.itpub.util.UserUtils;
import com.wisedu.emap.model2.IEmapAction;
import com.wisedu.emap.pedestal.app.AppBeanContainer;
import com.wisedu.emap.pedestal.app.IEmapApp;
import com.wisedu.emap.pedestal.app.IEmapAppContext;
import com.wisedu.emap.pedestal.core.AppManager;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CommonServiceImpl implements ICommonService {

	@Autowired
	private T_ITPUB_PROPS propsSV;

	@Autowired
	private IEmapAppContext content;

	@Override
	public Map<String, Object> getDeptsWithRestService() throws Exception {
		// 通过rest请求获取到原始的部门列表信息
		List<Map<String, String>> deptArr = getDeptArr();
		List<Map<String, String>> rows = Lists.newArrayList();
		if (ListUtil.isNotEmpty(deptArr)) {
			for (Map<String, String> dept : deptArr) {
				rows.add(ImmutableMap.of(Constants.NAME, dept.get("deptName"), Constants.ID_STR, dept.get("deptId")));
			}
			return EmapDataBuildUtil.wrapDropList(rows);
		}
		return Maps.newHashMap();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Map<String, String>> getDeptArr() throws Exception {
		String depts = HttpUtils.confPost("getDeptInfo", null);
		if (StringUtils.isEmpty(depts)) {
			log.info("【>>>>>>未获取到部门信息<<<<<<】");
			return null;
		}
		Map<String, Object> deptMap = JsonUtils.json2Map(depts);
		Object deptArr = deptMap.get("deptInfo");
		return deptArr == null ? Lists.<Map<String, String>> newArrayList() : (List<Map<String, String>>) deptArr;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Map<String, String>> getDeptTree(String deptCode) throws Exception {
		JsonObject params = new JsonObject();
		params.addProperty("tableName", "T_ZXBZ_DW");
		params.addProperty("parentCode", StringUtils.isEmpty(deptCode) ? "-1" : deptCode);
		String depts = HttpUtils.confPost(Constants.DEPT_TREE_REST_ID, params);
		if (StringUtils.isEmpty(depts)) {
			return null;
		}
		Map<String, Object> deptMap = JsonUtils.json2Map(depts);
		Object deptArr = deptMap.get("infoStands");
		return deptArr == null ? null : (List<Map<String, String>>) deptArr;

	}

	@Override
	public Map<String, Object> getMrSetting(String xtName) throws Exception {
		Object mrResult = propsSV.queryOne("KEY = ? AND XT = ?", new Object[] { Constants.DR_SWITCH_NAME, xtName },
				"VALUE", null);
		Map<String, Object> setting = Maps.newHashMap();
		String resultStr = StringUtil.getString(mrResult);
		String[] resArr = resultStr.split("\\|");
		/**
		 * switch(resArr.length) { case 2: setting.put(Constants.MR_TYPES,
		 * resArr[1]); case 1: setting.put(Constants.STATUS, resArr[0]);
		 * default: break; }
		 */
		if (resArr.length == 2) {
			setting.put(Constants.MR_TYPES, resArr[1]);
		}
		setting.put(Constants.STATUS, resArr[0]);
		return setting;
	}

	@Override
	public String messageRemaindStatusChange(boolean remaindStatus, String xtName, String remaindInfo)
			throws Exception {
		StringBuilder sql = new StringBuilder();
		// 检查是否已存在消息提醒的数据 如果存在 就更新 不存在就插入 点击开关触发
		sql.append("BEGIN ");
		sql.append("UPDATE T_ITPUB_PROPS ");
		sql.append("SET VALUE = ?||SUBSTR(VALUE, INSTR(VALUE, '|')) ");
		sql.append("WHERE KEY = ? AND XT = ?; ");
		sql.append("IF SQL%NOTFOUND THEN ");
		sql.append("INSERT INTO T_ITPUB_PROPS(WID, XT, NAME, KEY, VALUE) VALUES ");
		sql.append("(SYS_GUID(), ?, ?, ?, ? ); ");
		sql.append("END IF; ");
		sql.append("END; ");
		String rStatus = remaindStatus ? Constants.SWITCH_ON : Constants.SWITCH_OFF;
		try {
			DbUtil.execute(sql.toString(), new Object[] { rStatus, Constants.DR_SWITCH_NAME, xtName, xtName,
					remaindInfo, Constants.DR_SWITCH_NAME, rStatus + "|pc" });
			return Constants.SUCCESS;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public String saveMrTypes(String types, String xtName) throws Exception {
		StringBuilder sql = new StringBuilder();
		sql.append(" UPDATE T_ITPUB_PROPS ");
		sql.append(" SET VALUE = SUBSTR(VALUE, 0, INSTR(VALUE, '|'))||? ");
		sql.append(" WHERE KEY = ? AND XT = ? ");
		try {
			DbUtil.execute(sql.toString(), new Object[] { types, Constants.DR_SWITCH_NAME, xtName });
			return Constants.SUCCESS;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public InputStream getFistPicStreamByToken(String token, Integer imageType) throws Exception {
		AppBeanContainer<IFileDownload> fileDl = new AppBeanContainer<IFileDownload>("emapcomponent",
				IFileDownload.BEANID, false);
		IFileDownload tmp = fileDl.get();
		if (null != tmp) {
			try {
				Map<String, Object> params = Maps.newHashMap();
				if (imageType != null) {
					params.put("imageType", imageType);
				}
				return tmp.getStreamByToken(token, params);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<String, Object> getAppUserGroups(String appId) throws Exception {
		ImmutableMap<String, String> paramMap = ImmutableMap.of("appId", appId);
		IEmapAction<Object> action = content.getAction("cxyyyglyhz").createAction(null);
		Object resultObject = action.execute(paramMap);
		if (resultObject instanceof Map) {
			Map<String, Object> map = (Map<String, Object>) resultObject;
			if ("200".equals(StringUtil.getString(map.get(Constants.STATUS)))) {
				List<Map<String, String>> rows = Lists.newArrayList();
				List<Map<String, String>> groups = (List<Map<String, String>>) map.get("groups");
				for (Map<String, String> group : groups) {
					Map<String, String> newGroup = Maps.newHashMap();
					newGroup.put("id", group.get("groupId"));
					newGroup.put("name", group.get("name"));
					rows.add(newGroup);
				}
				return EmapDataBuildUtil.wrapDropList(rows);
			}
		}
		return null;
	}

	@Override
	public String getWorkflowActors(String groupId, String deptCode) throws Exception {
		List<Map<String, Object>> actorMapList = DbUtil.query(
				"SELECT BH FROM T_IT_BMYHZGXB WHERE ROLEID = ? AND YXDM = ?", new Object[] { groupId, deptCode });
		List<String> actors = Lists.newArrayList();
		for (Map<String, Object> actorMap : actorMapList) {
			actors.add(StringUtil.getString(actorMap.get("BH")));
		}
		return Joiner.on(",").join(actors);
	}

	@Override
	public void sendCommonMsg(String xtName, String title, String content, String url, List<Receivers> recs,
			String... isRecord) throws Exception {
		Map<String, Object> mrSetting = getMrSetting(xtName);
		if (mrSetting.isEmpty() || Constants.SWITCH_OFF.equals(mrSetting.get(Constants.STATUS))) {
			return;
		}
		String types = StringUtil.getString(mrSetting.get(Constants.MR_TYPES));

		// 消息提醒，一次只能最多发送300条数据，多于300，需要多次分批发送
		int count = recs.size() / 300;
		if (recs.size() % 300 != 0) {
			count = count + 1;
		}
		for (int i = 1; i <= count; i++) {
			List<Receivers> tempList = recs.subList((i - 1) * 300, i * 300 > recs.size() ? recs.size() : i * 300);
			MRUtils.sendMessage(xtName, types, title, content, url, tempList, isRecord);
		}

	}

	@Override
	@SuppressWarnings("unchecked")
	public List<String> getUserGroups(String userId, String appId) throws BizException {
		IEmapAction<Object> action = content.getAction("getGroupsByAppIdAndUserId").createAction(null);
		Map<String, Object> params = new HashMap<String, Object>();
		UserAppRequest requestObj = new UserAppRequest();
		requestObj.setAppId(appId);
		requestObj.setUserId(userId);
		params.put("userAppRequest", requestObj);
		Map<String, Object> resultObject = (Map<String, Object>) action.execute(params);
		if ("200".equals(resultObject.get(Constants.STATUS).toString())) {
			// 请求成功返回
			List<Map<String, Object>> groups = (List<Map<String, Object>>) resultObject.get("groups");
			if (ListUtil.isNotEmpty(groups)) {
				List<String> userGroups = Lists.newArrayList();
				for (Map<String, Object> group : groups) {
					String id = StringUtil.getString(group.get("groupId"));
					if (StringUtils.isEmpty(id)) {
						continue;
					}
					userGroups.add(id);
				}
				return userGroups;
			}
		}
		return Lists.newArrayList();
	}

	@Override
	public Map<String, Object> getUserDetailInfo() throws Exception {
		return UserUtils.getCurrentUserInfo();
	}

	@Override
	public Map<String, Object> getUndergraduateInfo() throws Exception {
		List<Map<String, Object>> users = Lists.newArrayList();
		String userId = UserUtils.getCurUserId();
		// String userId = "1927227";
		try {
			users = UserUtils.getUndergraduateInfo(userId);
		} catch (Exception e) {
			log.error("CURRENT USER INFO GET ERROR: " + e.getMessage(), e);
		}
		if (ListUtil.isEmpty(users)) {
			return null;
		}
		return users.get(0);
	}

	@Override
	public Map<String, Object> getUserInfosByIds(String userId, String userType) throws Exception {
		List<Map<String, Object>> users = Lists.newArrayList();
		try {
			users = UserUtils.getUserInfosByIds(userId, userType);
		} catch (Exception e) {
			log.error("CURRENT USER INFO GET ERROR: " + e.getMessage(), e);
		}
		if (ListUtil.isEmpty(users)) {
			return null;
		}
		return users.get(0);
	}

	@Override
	public AnalysisData queryAppAndUserDatas(String appName) throws Exception {
		AnalysisData ansData = new AnalysisData();
		String userId = UserUtils.getCurUserId();
		ItUser user = UserUtils.getUserByUserId(userId);
		String schoolCode = ITPubPropsUtil.getProp("MR_PROPS.SCHOOL_CODE", "wisedu");
		String schoolName = ITPubPropsUtil.getProp("SCHOOL_NAME", "金智大学");
		ansData.setSchoolId(schoolCode);
		ansData.setSchoolName(schoolName);
		ansData.setUserId(user.getId());
		ansData.setUserType(user.getUserType());
		ansData.setGrade(user.getGrade());
		ansData.setGender(user.getSexName());
		ansData.setBirthday(user.getBirthday());
		IEmapApp app = AppManager.getInstance().getApp(appName);
		ansData.setAppName(app.getName());
		ansData.setAppVer(app.getVersion());
		return ansData;
	}

	@Override
	public Map<String, Object> findSchoolGroupUserId(Integer pageSize, Integer pageNum, String type) throws Exception {
		Map<String, Object> reqParam = Maps.newHashMap();
		// 构造请求的分页数据
		Map<String, Object> page = Maps.newHashMap();
		page.put(Constants.PAGE_NUM, pageNum);
		page.put(Constants.PAGE_SIZE, pageSize);
		reqParam.put(Constants.PAGE, page);
		List<UserSearchCond> conditions = Lists.newArrayList();
		if ("teacher".equalsIgnoreCase(type)) {
			conditions.add(new UserSearchCond("type", "equal", "2", "and"));
		} else if ("student".equalsIgnoreCase(type)) {
			conditions.add(new UserSearchCond("type", "equal", "1", "and"));
		}
		conditions.add(new UserSearchCond("id", "like", "%", ""));
		reqParam.put("conditions", conditions);
		String resStr = HttpUtils.confPost("getUsersInfoByCond", JsonUtils.toJsonElement(reqParam));
		Map<String, Object> resMap = JsonUtils.json2Map(resStr);
		// 返回结果转换为页面的数据模型
		Object users = resMap.get("users");
		if (StringUtil.isEmptyObj(users)) {
			return null;
		}
		Map<String, Object> dataMap = Maps.newHashMap();
		dataMap.put(Constants.PAGE_SIZE, pageSize);
		dataMap.put(Constants.PAGE_NUMBER, pageNum);
		dataMap.put(Constants.TOTAL_SIZE, resMap.get(Constants.TOTAL));
		dataMap.put("rows", users);
		return dataMap;
	}

	@Override
	public void sendCommonMsg(String xtName, String title, String content, String url, List<Receivers> recs)
			throws Exception {
		sendCommonMsg(xtName, title, content, url, recs, new String[] {});
	}

}
