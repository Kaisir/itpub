/*
 * @Project itservicecommon
 * @Package com.wisedu.emap.it.controller.widget
 * @date 2016年5月12日 上午11:57:08
 * @author wjfu 01116035
 */
package com.wisedu.emap.itpub.controller.widget;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wisedu.emap.base.util.StringUtil;
import com.wisedu.emap.itpub.bean.Constants;
import com.wisedu.emap.itpub.bean.search.UserSearchCond;
import com.wisedu.emap.itpub.util.HttpUtils;
import com.wisedu.emap.itpub.util.JsonUtils;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 选人组件
 */
@Controller
@RequestMapping("/widget")
public class ChoosePersonController {

	/**
	 * 搜索人员接口 传入searchType查询特定的群组teacher或者student 不传查询类型则查询全部类型
	 * 
	 * @date 2016年10月8日 上午11:06:14
	 * @author wjfu 01116035
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/choose_person.do")
	public @ResponseBody Map<String, Object> dialog() throws Exception {
		Map<String, Object> retVal = Maps.newHashMap();
		Map<String, Object> actionMap = Maps.newHashMap();
		Map<String, Object> dataMap = Maps.newHashMap();
		actionMap.put("data", dataMap);
		retVal.put("datas", actionMap);
		retVal.put("code", "0");
		String pageSizeStr = HttpUtils.getParameter("pageSize");
		String pageNumberStr = HttpUtils.getParameter("pageNumber");
		String pageSize = StringUtil.isEmpty(pageSizeStr) ? Constants.PAGE_SIZE_DEFAULT : pageSizeStr;
		String pageNum = StringUtil.isEmpty(pageNumberStr) ? Constants.PAGE_NUM_START : pageNumberStr;
		// String paramsJson = HttpUtils.getRsaParameter("params");
		// 检查是否存在搜索 如果存在的话 搜索结果
		String searchKey = StringUtil.getString(HttpUtils.getParameter("SEARCHKEY"));
		Map<String, Object> reqParam = Maps.newHashMap();
		// 构造请求的分页数据
		Map<String, String> page = Maps.newHashMap();
		page.put(Constants.PAGE_NUM, pageNum);
		page.put(Constants.PAGE_SIZE, pageSize);
		reqParam.put(Constants.PAGE, page);
		List<UserSearchCond> conditions = Lists.newArrayList();
		// 类型：教职工、学生、所有
		String searchType = "";// HttpUtils.getParameter("searchType");
		// 部门编码
		String deptCode = "";
		String paramsJson = HttpUtils.getParameter("params");
		if (StringUtil.isNotEmpty(paramsJson)) {
			Map<String, Object> params = JsonUtils.json2Map(paramsJson);
			searchType = (String) params.get("searchType");
			deptCode = (String) params.get("deptCode");
		}
		if ("teacher".equalsIgnoreCase(searchType)) {
			UserSearchCond cond = new UserSearchCond("type", "equal", "2", "");
			cond.setSearchJoin("and");
			conditions.add(cond);
		} else if ("student".equalsIgnoreCase(searchType)) {
			UserSearchCond cond = new UserSearchCond("type", "equal", "1", "");
			cond.setSearchJoin("and");
			conditions.add(cond);
		}
		if (StringUtil.isNotEmpty(deptCode)) {
			conditions.add(new UserSearchCond("deptCode", "equal", deptCode, "and"));
		}
		// 根据条件设置搜索
		conditions.add(new UserSearchCond("id", "like", "%" + searchKey + "%", "or"));
		// 咨询了魏计涛，手动加的参数要写两遍，不懂为什么
		if ("teacher".equalsIgnoreCase(searchType)) {
			UserSearchCond cond = new UserSearchCond("type", "equal", "2", "");
			cond.setSearchJoin("and");
			conditions.add(cond);
		} else if ("student".equalsIgnoreCase(searchType)) {
			UserSearchCond cond = new UserSearchCond("type", "equal", "1", "");
			cond.setSearchJoin("and");
			conditions.add(cond);
		}
		if (StringUtil.isNotEmpty(deptCode)) {
			conditions.add(new UserSearchCond("deptCode", "equal", deptCode, "and"));
		}
		conditions.add(new UserSearchCond("name", "like", "%" + searchKey + "%", "or"));
		reqParam.put("conditions", conditions);
		String resStr = HttpUtils.confPost("getUsersInfoByCond", JsonUtils.toJsonElement(reqParam));
		Map<String, Object> resMap = JsonUtils.json2Map(resStr);
		// 返回结果转换为页面的数据模型
		Object users = resMap.get("users");
		if (StringUtil.isEmptyObj(users)) {
			return null;
		}
		dataMap.put(Constants.PAGE_SIZE, pageSize);
		dataMap.put(Constants.PAGE_NUMBER, pageNum);
		dataMap.put(Constants.TOTAL_SIZE, resMap.get(Constants.TOTAL));
		dataMap.put("rows", users);
		return retVal;
	}

}
