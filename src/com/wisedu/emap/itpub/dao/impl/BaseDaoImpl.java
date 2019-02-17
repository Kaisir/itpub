package com.wisedu.emap.itpub.dao.impl;

import com.wisedu.emap.itpub.dao.IBaseDao;
import com.wisedu.emap.itpub.util.DsUtil;

import java.util.List;
import java.util.Map;

/**
 * 基础服务接口
 */
public class BaseDaoImpl implements IBaseDao {

	protected String tableName;

	public BaseDaoImpl(String tableName) {
		this.tableName = tableName;
	}

	public String getTableName() {
		return this.tableName;
	}

	@Override
	public String saveOrUpdate(Map<String, Object> data) throws Exception {
		return DsUtil.saveOrUpdate(tableName, data);
	}

	@Override
	public void saveOrUpdate(List<Map<String, Object>> datas) throws Exception {
		DsUtil.saveOrUpdate(tableName, datas);
	}

	@Override
	public void delByWids(List<String> wids) throws Exception {
		DsUtil.delByWids(tableName, wids);
	}

	@Override
	public Map<String, Object> queryRow(String condition, Object[] params, String[] fieldNames, String orderBy)
			throws Exception {
		return DsUtil.queryRow(tableName, condition, params, fieldNames, orderBy);
	}

	@Override
	public Object queryOne(String condition, Object[] params, String fieldName, String orderBy) throws Exception {
		return DsUtil.queryOne(tableName, condition, params, fieldName, orderBy);
	}

	public void deleteByWid(String wid) throws Exception {
		DsUtil.deleteByWid(tableName, wid);
	}

	@Override
	public List<Map<String, Object>> query(String condition, Object... params) throws Exception {
		return query(condition, params, null, null);
	}

	@Override
	public List<Map<String, Object>> query(String condition, Object[] params, String[] fieldNames, String orderBy)
			throws Exception {
		return query(condition, params, fieldNames, orderBy, -1, -1);
	}

	@Override
	public List<Map<String, Object>> query(String condition, Object[] params, String[] fieldNames, String orderBy,
			int pageNum, int pageSize) throws Exception {
		return DsUtil.query(tableName, condition, params, fieldNames, orderBy, pageNum, pageSize);
	}

}
