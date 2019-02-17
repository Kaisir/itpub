package com.wisedu.emap.itpub.dao;

import java.util.List;
import java.util.Map;

/**
 * 基础DAO接口
 * 
 * @author mengbin
 * @date 2015年4月24日 下午3:44:17
 */
public interface IBaseDao {
	/**
	 * 获取表名
	 * 
	 * @author mengbin
	 * @date 2015年4月24日 下午3:46:12
	 * @return
	 */
	String getTableName();

	/**
	 * 保存或更新
	 * 
	 * @author mengbin
	 * @date 2015年4月24日 下午4:17:43
	 * @param data
	 *            数据
	 * @throws Exception
	 */
	String saveOrUpdate(Map<String, Object> data) throws Exception;

	/**
	 * 批量新增或更新
	 * 
	 * @author mengbin
	 * @date 2015年4月24日 下午4:18:09
	 * @param datas
	 * @throws Exception
	 */
	void saveOrUpdate(List<Map<String, Object>> datas) throws Exception;

	/**
	 * 通过idList删除数据
	 * 
	 * @date 2016年4月1日 下午3:46:18
	 * @author wjfu 01116035
	 * @param wids
	 */
	void delByWids(List<String> wids) throws Exception;

	/**
	 * 查询单条记录
	 * 
	 * @author mengbin
	 * @date 2015年9月23日 下午3:15:13
	 * @param condition
	 * @param params
	 * @param fieldNames
	 * @param orderBy
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> queryRow(String condition, Object[] params, String[] fieldNames, String orderBy)
			throws Exception;

	/**
	 * 查询记录第一行第一列
	 * 
	 * @author mengbin
	 * @date 2015年9月23日 下午3:22:06
	 * @param condition
	 * @param params
	 * @param fieldName
	 * @param orderBy
	 * @return
	 * @throws Exception
	 */
	public Object queryOne(String condition, Object[] params, String fieldName, String orderBy) throws Exception;

	/**
	 * 根据WID删除记录
	 * 
	 * @author mengbin
	 * @date 2015年4月24日 下午4:17:19
	 * @param wid
	 *            记录WID
	 * @throws Exception
	 */
	public void deleteByWid(String wid) throws Exception;

	/**
	 * 查询重载
	 * 
	 * @author mengbin
	 * @date 2015年4月29日 下午5:23:26
	 * @param condition
	 *            查询条件,参数使用?代替
	 * @param params
	 *            查询参数
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> query(String condition, Object... params) throws Exception;

	/**
	 * 查询重载
	 * 
	 * @author mengbin
	 * @date 2015年4月29日 下午5:23:28
	 * @param condition
	 *            查询条件,参数使用?代替
	 * @param params
	 *            查询参数
	 * @param fieldNames
	 *            返回的字段列表
	 * @param orderBy
	 *            排序SQL,例如: +PX,-AGE ==> PX ASC,AGE DESC
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> query(String condition, Object[] params, String[] fieldNames, String orderBy)
			throws Exception;

	/**
	 * 根据条件查询记录
	 * 
	 * @author mengbin
	 * @date 2015年4月24日 下午4:16:45
	 * @param condition
	 *            查询条件,参数使用?代替
	 * @param params
	 *            查询参数
	 * @param fieldNames
	 *            返回字段列表
	 * @param orderBy
	 *            排序信息
	 * @param startIndex
	 *            开始索引
	 * @param endIndex
	 *            结束索引
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> query(String condition, Object[] params, String[] fieldNames, String orderBy,
			int pageNum, int pageSize) throws Exception;
}
