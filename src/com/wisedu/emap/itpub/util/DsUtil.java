package com.wisedu.emap.itpub.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.wisedu.emap.base.util.GuidUtil;
import com.wisedu.emap.base.util.ListUtil;
import com.wisedu.emap.dao.DaoParam;
import com.wisedu.emap.dao.SubScript;
import com.wisedu.emap.model2.QueryResult;
import com.wisedu.emap.model2.action.ActionType;
import com.wisedu.emap.model2.action.IDataModelQueryAction;
import com.wisedu.emap.model2.action.IDataModelUpdateAction;
import com.wisedu.emap.model2.container.DataModelContainer;
import com.wisedu.emap.pedestal.core.AppManager;

/**
 * 数据集相关工具方法
 * 
 * @author mengbin
 * @date 2015年9月15日 上午8:45:15
 */
public class DsUtil {

	private DsUtil() {
	}

	private static String appName = "itpub";

	/**
	 * 保存单条记录
	 * 
	 * @author mengbin
	 * @date 2015年9月15日 上午8:55:23
	 * @param tableName
	 *            表名
	 * @param data
	 *            数据
	 * @return
	 * @throws Exception
	 */
	public static String saveOrUpdate(String tableName, Map<String, Object> data) throws Exception {
		String wid = (String) data.get("WID");
		if (StringUtils.isEmpty(wid)) {
			wid = getNewWid();
			data.put("WID", wid);
			IDataModelUpdateAction action = getContainer(tableName).getUpdateAction(ActionType.ADD);
			action.executeUpdate(data);
		} else {
			IDataModelUpdateAction action = getContainer(tableName).getUpdateAction(ActionType.MODIFY);
			action.executeUpdate(data, "WID = ?", Arrays.asList(wid));
		}
		return wid;
	}

	/**
	 * 批量保存,目前暂时循环单个保存
	 * 
	 * @author mengbin
	 * @date 2015年9月15日 上午8:54:53
	 * @param tableName
	 *            表名
	 * @param datas
	 *            数据集合
	 * @throws Exception
	 */
	public static void saveOrUpdate(String tableName, List<Map<String, Object>> datas) throws Exception {
		if (ListUtil.isNotEmpty(datas)) {
			for (int i = 0; i < datas.size(); i++) {
				saveOrUpdate(tableName, datas.get(i));
			}
		}
	}

	/**
	 * 获取新的WID
	 * 
	 * @author mengbin
	 * @date 2015年9月15日 上午8:53:46
	 * @return
	 * @throws Exception
	 */
	public static String getNewWid() throws Exception {
		return GuidUtil.getRandomGuid();
	}

	/**
	 * 获取模型
	 * 
	 * @author mengbin
	 * @date 2015年9月15日 上午8:52:22
	 * @param tableName
	 * @return
	 */
	public static DataModelContainer getContainer(String tableName) {
		DataModelContainer res = AppManager.currentApp().getAppContext().getDataModel(tableName);
		if (res == null) {
			res = AppManager.getInstance().getApp(appName).getAppContext().getDataModel(tableName);
		}
		return res;
	}

	/**
	 * 根据WID删除记录
	 * 
	 * @author mengbin
	 * @date 2015年9月15日 上午8:55:47
	 * @param tableName
	 * @param wid
	 * @throws Exception
	 */
	public static void deleteByWid(String tableName, String wid) throws Exception {
		IDataModelUpdateAction action = getContainer(tableName).getUpdateAction(ActionType.DELETE);
		action.executeUpdate(null, "WID = ?", Arrays.asList(wid));
	}

	/**
	 * 通过传入的wids删除指定的列数据
	 * 
	 * @date 2016年4月1日 下午3:48:21
	 * @author wjfu 01116035
	 * @param tableName
	 * @param wids
	 */
	public static void delByWids(String tableName, List<String> wids) throws Exception {
		if (wids == null || wids.isEmpty()) {
			return;
		}
		IDataModelUpdateAction action = getContainer(tableName).getUpdateAction(ActionType.DELETE);
		// 删除的的字句是单独一个一个匹配的
		int size = wids.size();
		StringBuilder inSub = new StringBuilder("WID IN ( ");
		for (int i = 0; i < size; i++) {
			if (i == size - 1) {
				inSub.append("? )");
			} else {
				inSub.append("?, ");
			}
		}
		action.executeUpdate(null, inSub.toString(), wids);
	}

	/**
	 * 查询记录第一行
	 * 
	 * @author mengbin
	 * @date 2015年9月23日 下午3:17:10
	 * @param tableName
	 * @param condition
	 * @param params
	 * @param fieldNames
	 * @param orderBy
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> queryRow(String tableName, String condition, Object[] params, String[] fieldNames,
			String orderBy) throws Exception {
		List<Map<String, Object>> datas = query(tableName, condition, params, fieldNames, orderBy);
		if (ListUtil.isNotEmpty(datas)) {
			return datas.get(0);
		}
		return null;
	}

	/**
	 * 根据条件查询
	 * 
	 * @author mengbin
	 * @date 2015年9月15日 上午8:47:07
	 * @param tableName
	 *            表名
	 * @param condition
	 *            查询条件
	 * @param params
	 *            条件参数
	 * @param fieldNames
	 *            返回字段列表
	 * @param orderBy
	 *            排序条件
	 * @return
	 * @throws Exception
	 */
	public static List<Map<String, Object>> query(String tableName, String condition, Object[] params,
			String[] fieldNames, String orderBy) throws Exception {
		return query(tableName, condition, params, fieldNames, orderBy, -1, -1);
	}

	/**
	 * 按条件查询
	 * 
	 * @author mengbin
	 * @date 2015年9月15日 上午8:47:52
	 * @param tableName
	 *            表名
	 * @param condition
	 *            查询条件
	 * @param params
	 *            条件参数
	 * @param fieldNames
	 *            返回字段列表
	 * @param orderBy
	 *            排序条件
	 * @param pageNum
	 *            页索引
	 * @param pageSize
	 *            页大小
	 * @return
	 * @throws Exception
	 */
	public static List<Map<String, Object>> query(String tableName, String condition, Object[] params,
			String[] fieldNames, String orderBy, int pageNum, int pageSize) throws Exception {
		IDataModelQueryAction<Map<String, Object>> action = getContainer(tableName).getQueryAction();
		DaoParam daoParam = new DaoParam();
		if (!StringUtils.isEmpty(condition)) {
			daoParam.setSubScript(1, new SubScript(condition, params == null ? null : Arrays.asList(params)));
		}
		if (fieldNames != null && fieldNames.length > 0) {
			daoParam.setDisplayColumns(StringUtils.join(fieldNames, ","));
		}
		if (!StringUtils.isEmpty(orderBy)) {
			daoParam.setOrderColumns(orderBy);
		}
		if (pageNum < 0 || pageSize < 0) {
			daoParam.setAllRow(true);
		} else {
			daoParam.setPageNum(pageNum);
			daoParam.setPageSize(pageSize);
		}
		QueryResult<Map<String, Object>> result = action.executeQuery(daoParam);
		List<Map<String, Object>> retList = new ArrayList<Map<String, Object>>();
		Iterator<Map<String, Object>> iter = result.iterator();
		while (iter.hasNext()) {
			retList.add(new HashMap<String, Object>(iter.next()));
		}
		return ListUtil.isEmpty(retList) ? null : retList;
	}

	/**
	 * 查询第一行第一列
	 * 
	 * @author mengbin
	 * @date 2015年9月23日 下午3:20:07
	 * @param tableName
	 * @param condition
	 * @param params
	 * @param fieldName
	 * @param orderBy
	 * @return
	 * @throws Exception
	 */
	public static Object queryOne(String tableName, String condition, Object[] params, String fieldName, String orderBy)
			throws Exception {
		Map<String, Object> row = queryRow(tableName, condition, params, new String[] { fieldName }, orderBy);
		if (row != null) {
			return row.get(fieldName);
		}
		return null;
	}

}
