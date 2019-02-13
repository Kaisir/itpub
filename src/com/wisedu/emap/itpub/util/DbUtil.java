package com.wisedu.emap.itpub.util;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wisedu.emap.base.util.ListUtil;
import com.wisedu.emap.base.util.StringUtil;
import com.wisedu.emap.biz.dictionary.CodeEntity;
import com.wisedu.emap.dao.DaoParam;
import com.wisedu.emap.dao.DaoService;
import com.wisedu.emap.dicOperation.IDicOperation;
import com.wisedu.emap.itpub.bean.Constants;
import com.wisedu.emap.pedestal.app.AppBeanContainer;
import com.wisedu.emap.pedestal.app.DictionaryContainer;
import com.wisedu.emap.pedestal.core.AppManager;

import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 数据库基本操作工具类
 */
@Slf4j
public class DbUtil {

	private DbUtil() {
	}

	public static final String[] TYPES = { "LONG", "DATE", "TIMESTAMP", "BLOB", "CLOB" };

	/**
	 * 关闭数据库对象
	 * 
	 * @author mengbin
	 * @date 2015-7-8 上午8:49:03
	 * @param rss
	 * @param stmts
	 * @param conns
	 */
	public static void closeJdbc(ResultSet[] rss, Statement[] stmts, Connection[] conns) {
		if (rss != null && rss.length > 0) {
			for (ResultSet rs : rss) {
				if (rs != null) {
					try {
						rs.close();
					} catch (Exception e) {
						log.error(e.getMessage(), e);
					}
				}
			}
		}
		if (stmts != null && stmts.length > 0) {
			for (Statement stmt : stmts) {
				if (stmt != null) {
					try {
						stmt.close();
					} catch (Exception e) {
						log.error(e.getMessage(), e);
					}
				}
			}
		}
		if (conns != null && conns.length > 0) {
			for (Connection conn : conns) {
				if (conn != null) {
					try {
						conn.close();
					} catch (Exception e) {
						log.error(e.getMessage(), e);
					}
				}
			}
		}
	}

	/**
	 * 获取数据库连接
	 * 
	 * @author mengbin
	 * @date 2015年10月8日 下午4:02:13
	 * @return
	 * @throws Exception
	 */
	public static Connection getConnection(boolean needTrans) throws Exception {
		return new DaoService().getConnection(needTrans);
	}

	/**
	 * 执行sql语句
	 * 
	 * @author mengbin
	 * @date 2015-7-14 上午10:40:40
	 * @param sql
	 * @param params
	 */
	public static void execute(String sql, Object... params) throws Exception {
		log.info("【执行的sql：】" + sql);
		Connection conn = getConnection(true);
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);
			if (params != null && params.length > 0) {
				for (int i = 0; i < params.length; i++) {
					pstmt.setObject(i + 1, params[i]);
				}
			}
			pstmt.execute();
		} finally {
			closeJdbc(null, new Statement[] { pstmt }, null);
		}
	}

	/**
	 * 附带传入connection的执行sql方法用于强制控制事务
	 * 
	 * @date 2016年11月23日 上午10:06:37
	 * @author wjfu 01116035
	 * @param conn
	 * @param sql
	 * @param params
	 * @throws Exception
	 */
	public static void execute(Connection conn, String sql, Object... params) throws Exception {
		log.info("【执行的sql：】" + sql);
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);
			if (params != null && params.length > 0) {
				for (int i = 0; i < params.length; i++) {
					pstmt.setObject(i + 1, params[i]);
				}
			}
			pstmt.execute();
		} finally {
			closeJdbc(null, new Statement[] { pstmt }, null);
		}
	}

	/**
	 * 批量更新或保存
	 * 
	 * @author mengbin
	 * @date 2015-10-19 下午7:25:47
	 * @param tableName
	 * @param datas
	 * @return
	 * @throws Exception
	 */
	public static void saveOrUpdate(String tableName, List<Map<String, Object>> datas) throws Exception {
		List<Map<String, Object>> saveList = Lists.newArrayList();
		List<Map<String, Object>> updateList = Lists.newArrayList();
		for (Map<String, Object> data : datas) {
			String wid = (String) data.get("WID");
			if (StringUtils.isEmpty(wid)) {
				data.put("WID", DsUtil.getNewWid());
				saveList.add(data);
			} else {
				updateList.add(data);
			}
		}
		if (ListUtil.isNotEmpty(saveList)) {
			Map<String, Object> data0 = saveList.get(0);
			StringBuilder buf = new StringBuilder("INSERT INTO ").append(tableName).append("(");
			StringBuilder buf1 = new StringBuilder();
			for (Iterator<String> iter = data0.keySet().iterator(); iter.hasNext();) {
				String zdid = iter.next();
				buf.append(zdid).append(",");
				buf1.append("?,");
			}
			buf.replace(buf.length() - 1, buf.length(), ") VALUES (");
			buf1.replace(buf1.length() - 1, buf1.length(), ")");
			buf.append(buf1.toString());
			Connection conn = getConnection(true);
			log.info("【执行的sql：】" + buf.toString());
			PreparedStatement pstmt = conn.prepareStatement(buf.toString());
			for (Map<String, Object> data : saveList) {
				int pos = 0;
				for (Iterator<String> iter = data.keySet().iterator(); iter.hasNext();) {
					String zdid = iter.next();
					pstmt.setObject(++pos, data.get(zdid));
				}
				pstmt.addBatch();
			}
			pstmt.executeBatch();
			try {
				pstmt.close();
			} finally {
			}
		}
		if (ListUtil.isNotEmpty(updateList)) {
			Map<String, Object> data0 = updateList.get(0);
			StringBuilder buf = new StringBuilder("UPDATE ").append(tableName).append(" SET ");
			for (Iterator<String> iter = data0.keySet().iterator(); iter.hasNext();) {
				String zdid = iter.next();
				buf.append(zdid).append(" = ?,");
			}
			buf.replace(buf.length() - 1, buf.length(), " WHERE WID = ? ");
			Connection conn = getConnection(true);
			log.info("【执行的sql：】" + buf.toString());
			PreparedStatement pstmt = conn.prepareStatement(buf.toString());
			for (int i = 0; i < updateList.size(); i++) {
				Map<String, Object> data = updateList.get(i);
				int pos = 0;
				for (Iterator<String> iter = data.keySet().iterator(); iter.hasNext();) {
					String zdid = iter.next();
					Object value = data.get(zdid);
					pstmt.setObject(++pos, value);
				}
				pstmt.setObject(++pos, data.get("WID"));
				pstmt.addBatch();
			}
			pstmt.executeBatch();
			try {
				pstmt.close();
			} finally {
			}
		}
	}

	/**
	 * 更新或保存
	 * 
	 * @author mengbin
	 * @date 2015-10-19 下午7:26:19
	 * @param tableName
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static String saveOrUpdate(String tableName, Map<String, Object> data) throws Exception {
		List<Map<String, Object>> datas = Lists.newArrayList();
		datas.add(data);
		saveOrUpdate(tableName, datas);
		return (String) data.get("WID");
	}

	/**
	 * 返回SQL语句的第一条记录
	 * 
	 * @author mengbin
	 * @date 2015-7-8 上午8:51:10
	 * @param sql
	 * @param params
	 * @return
	 */
	public static Map<String, Object> queryRow(String sql, Object... params) throws Exception {
		List<Map<String, Object>> datas = query(sql, params);
		if (ListUtil.isNotEmpty(datas)) {
			return datas.get(0);
		}
		return null;
	}

	/**
	 * 查询所有记录
	 * 
	 * @author mengbin
	 * @date 2015-7-8 上午8:52:04
	 * @param sql
	 * @param params
	 * @return
	 */
	public static List<Map<String, Object>> query(String sql, Object... params) throws Exception {
		Connection conn = getConnection(false);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<Map<String, Object>> retList = null;
		try {
			log.info("【执行的sql：】" + sql);
			pstmt = conn.prepareStatement(sql);
			if (params != null && params.length > 0) {
				for (int i = 0; i < params.length; i++) {
					pstmt.setObject(i + 1, params[i]);
				}
			}
			rs = pstmt.executeQuery();
			retList = extractResults(rs);
		} finally {
			closeJdbc(new ResultSet[] { rs }, new Statement[] { pstmt }, null);
		}
		return retList;
	}

	/**
	 * 如果查询结果为null 返回一个空的集合
	 * 
	 * @date 2016年5月27日 下午2:17:44
	 * @author wjfu 01116035
	 * @param sql
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static List<Map<String, Object>> defaultEmptyQuery(String sql, Object... params) throws Exception {
		List<Map<String, Object>> list = query(sql, params);
		return null == list ? Lists.<Map<String, Object>> newArrayList() : list;
	}

	/**
	 * 释放数据集
	 * 
	 * @author mengbin
	 * @date 2015年10月6日 上午10:07:03
	 * @param rs
	 * @return
	 * @throws Exception
	 */
	private static List<Map<String, Object>> extractResults(ResultSet rs) throws Exception {
		if (rs == null) {
			return null;
		}
		List<String> colNames = getColumnNames(rs);
		List<Map<String, Object>> retList = Lists.newArrayList();
		while (rs.next()) {
			Map<String, Object> data = Maps.newHashMap();
			for (String colName : colNames) {
				Object value = rs.getObject(colName);
				if (value != null && (value instanceof Clob)) {
					Clob clob = (Clob) value;
					value = getClobString(clob);
				}
				data.put(colName, value);
			}
			retList.add(data);
		}
		return ListUtil.isEmpty(retList) ? null : retList;
	}

	/**
	 * 获取表结构中数据列英文名
	 * 
	 * @author yingwu01
	 * @date 2015年12月15日下午5:31:23
	 * @param rs
	 * @return
	 * @throws Exception
	 */
	private static List<String> getColumnNames(ResultSet rs) throws Exception {
		ResultSetMetaData rsmd = rs.getMetaData();
		int count = rsmd.getColumnCount();
		List<String> colNames = Lists.newArrayList();
		for (int i = 1; i <= count; i++) {
			String colName = rsmd.getColumnLabel(i); // 或者列名,包括别名
			colNames.add(colName.toUpperCase());
		}
		return colNames;
	}

	/**
	 * 获取Clob内容
	 * 
	 * @author mengbin
	 * @date 2015-10-21 下午2:11:04
	 * @param clob
	 * @return
	 * @throws Exception
	 */
	public static String getClobString(Clob clob) throws Exception {
		return clob.getSubString(1L, (int) clob.length());
	}

	/**
	 * 按表名查询
	 * 
	 * @author mengbin
	 * @date 2015年12月2日 上午9:49:41
	 * @param tableName
	 * @param condition
	 * @param fields
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> queryTableRow(String tableName, String condition, Object[] params,
			String[] fields) throws Exception {
		List<Map<String, Object>> datas = queryTable(tableName, condition, params, fields);
		if (ListUtil.isNotEmpty(datas)) {
			return datas.get(0);
		}
		return null;
	}

	/**
	 * 根据表名查询
	 * 
	 * @author mengbin
	 * @date 2015年12月2日 上午9:45:50
	 * @param tableName
	 * @param condition
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static List<Map<String, Object>> queryTable(String tableName, String condition, Object[] params,
			String[] fields) throws Exception {
		return queryTable(tableName, condition, params, fields, null, -1, -1);
	}

	/**
	 * 分页查询
	 * 
	 * @author mengbin
	 * @date 2016年1月15日 上午11:28:18
	 * @param tableName
	 *            表名
	 * @param condition
	 *            查询条件,?作为占位符
	 * @param params
	 *            查询参数
	 * @param fields
	 *            返回的字段名称数组
	 * @param orderBy
	 *            排序条件. 正常写法
	 * @param startIndex
	 *            开始索引 从0开始, 如果传入-1则表示查询所有记录
	 * @param endIndex
	 *            结束索引
	 * @return
	 * @throws Exception
	 */
	public static List<Map<String, Object>> queryTable(String tableName, String condition, Object[] params,
			String[] fields, String orderBy, int startIndex, int endIndex) throws Exception {
		StringBuilder sb = new StringBuilder();
		String fieldStr = (fields == null || fields.length < 1) ? "*" : Joiner.on(",").join(fields);
		if (startIndex < 0 || endIndex < 0) {
			sb.append("SELECT ").append(fieldStr).append(" FROM ").append(tableName);
			if (!StringUtils.isEmpty(condition)) {
				sb.append(" WHERE ").append(condition);
			}
			if (!StringUtils.isEmpty(orderBy)) {
				sb.append(" ORDER BY ").append(orderBy);
			}
			return query(sb.toString(), params);
		}
		// 分页查询
		sb.append("SELECT ").append(fieldStr).append(" FROM (").append("SELECT ").append(fieldStr)
				.append(",ROWNUM RN FROM (").append("SELECT ").append(fieldStr).append(" FROM ").append(tableName);
		if (!StringUtils.isEmpty(condition)) {
			sb.append(" WHERE ").append(condition);
		}
		if (!StringUtils.isEmpty(orderBy)) {
			sb.append(" ORDER BY ").append(orderBy);
		}
		sb.append(")) TMP_A WHERE TMP_A.RN > ").append(startIndex).append(" AND TMP_A.RN <= ").append(endIndex);
		return query(sb.toString(), params);
	}

	/**
	 * 查询整数结果
	 * 
	 * @author mengbin
	 * @date 2015年12月1日 下午2:10:15
	 * @param sql
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static int queryInt(String sql, Object... params) throws Exception {
		Object obj = queryOne(sql, params);
		return obj == null ? 0 : Integer.parseInt(obj.toString());
	}

	/**
	 * 查询第一行第一列
	 * 
	 * @author mengbin
	 * @date 2015-7-8 上午8:52:49
	 * @param sql
	 * @param params
	 * @return
	 */
	public static Object queryOne(String sql, Object... params) throws Exception {
		log.info("【执行的sql：】" + sql);
		Connection conn = getConnection(false);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(sql);
			if (params != null && params.length > 0) {
				for (int i = 0; i < params.length; i++) {
					pstmt.setObject(i + 1, params[i]);
				}
			}
			rs = pstmt.executeQuery();
			if (rs.next()) {
				Object value = rs.getObject(1);
				if (value != null && value instanceof Clob) {
					value = getClobString((Clob) value);
				}
				return value;
			}
		} finally {
			closeJdbc(new ResultSet[] { rs }, new Statement[] { pstmt }, null);
		}
		return null;
	}

	/**
	 * 获取数据库当前时间
	 * 
	 * @author zhuangyuhao
	 * @date 2016年4月26日 下午3:41:05
	 * @return
	 * @throws Exception
	 */
	public static Date getCurrentDate() throws Exception {
		return (Date) queryOne("SELECT SYSDATE FROM DUAL");
	}

	/**
	 * 刷新字典
	 * 
	 * @date 2016年4月21日 下午2:22:57
	 * @author wjfu 01116035
	 * @param appName
	 * @param dicId
	 * @throws Exception
	 */
	public static void refreshDic(String appName, String dicId) throws Exception {
		// HttpClient需要登录 暂无不登陆解决方法
		AppBeanContainer<IDicOperation> dicOpt = new AppBeanContainer<IDicOperation>("emapcomponent",
				IDicOperation.BEANID, false);
		IDicOperation dicOperation = dicOpt.get();
		if (dicOperation != null) {
			Map<String, Object> result = dicOperation.clearDicCacheByID(appName, dicId);
			log.info("应用[" + appName + "]字典表刷新结果:" + result);
		} else {
			log.error("应用字典表容器获取失败。");
		}

	}

	/**
	 * 生成where in的子句 传入参数的size生成(问号数量) eg: 传入:2 传出: (?, ?)
	 * 
	 * @date 2016年5月12日 下午4:46:11
	 * @author wjfu 01116035
	 * @param size
	 * @return
	 */
	public static String genWhereInSub(int size) {
		if (size == 0) {
			return " NULL ";
		}
		StringBuilder sub = new StringBuilder(" (");
		for (int i = 0; i < size; i++) {
			if (i == (size - 1)) {
				sub.append("? ) ");

			} else {
				sub.append("?, ");
			}
		}
		return sub.toString();
	}

	/**
	 * 通过传入的list批量删除信息 使用where in删除
	 * 
	 * @date 2016年5月26日 上午9:36:56
	 * @author wjfu 01116035
	 * @param tableName
	 * @param params
	 */
	public static void batchDelete(String tableName, String col, List<String> params) throws Exception {
		StringBuilder sql = new StringBuilder();
		sql.append("DELETE FROM ");
		sql.append(tableName);
		sql.append(" WHERE ");
		sql.append(col);
		sql.append(" IN ");
		sql.append(genWhereInSub(params.size()));
		execute(sql.toString(), params.toArray());
	}

	/**
	 * 判断数据库内是否存在以 name 为名的表或视图
	 * 
	 * @author yingwu01
	 * @date 2015年12月15日下午6:12:41
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public static boolean exsitTableOrView(String name) throws Exception {
		Connection conn = getConnection(false);
		DatabaseMetaData meta = conn.getMetaData();
		ResultSet rsTables = meta.getTables(null, null, name.toUpperCase(), null);
		return rsTables.next();
	}

	/**
	 * 通过列名获取表名
	 * 
	 * @author yingwu01
	 * @date 2015年12月15日下午5:32:55
	 * @param tableName
	 * @return
	 * @throws Exception
	 */
	public static List<String> getFieldNameByTableName(String tableName) throws Exception {
		if (!exsitTableOrView(tableName)) {
			return null;
		}
		String sql = "SELECT * FROM " + tableName + " WHERE 1=2";
		Connection conn = getConnection(false);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<String> retList = null;
		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			if (rs != null) {
				retList = getColumnNames(rs);
			}
		} finally {
			closeJdbc(new ResultSet[] { rs }, new Statement[] { pstmt }, null);
		}
		return retList;
	}

	/**
	 * 通过字典的ID获取字典内容 免登陆版本
	 * 
	 * @date 2016年6月13日 下午2:34:25
	 * @author wjfu 01116035
	 * @param dicId
	 * @return
	 */
	public static Map<String, Object> getDicById(String dicId, String type) {
		Map<String, Object> rData = Maps.newHashMap();
		DictionaryContainer c = AppManager.currentApp().getAppContext().getDictionary(dicId);
		rData.put("code", "0");
		Map<String, Object> rows = Maps.newHashMap();
		Map<String, CodeEntity> r = c.getDictionaryData(type, null, true, null);
		rows.put("rows", r.values());
		rData.put("datas", Collections.singletonMap("code", rows));
		return rData;
	}

	/**
	 * 包装请求的分页信息 以便后台能够做出正确的分页请求
	 * 
	 * @date 2016年6月13日 下午2:33:58
	 * @author wjfu 01116035
	 * @param param
	 */
	public static void wrapPageInfo(DaoParam param) {
		String pageSize = HttpUtils.getParameter("pageSize");
		String pageNum = HttpUtils.getParameter("pageNumber");
		pageSize = StringUtil.isEmpty(pageSize) ? Constants.PAGE_SIZE_DEFAULT : pageSize;
		pageNum = StringUtil.isEmpty(pageNum) ? Constants.PAGE_NUM_START : pageNum;
		param.setPageNum(Integer.valueOf(pageNum));
		param.setPageSize(Integer.valueOf(pageSize));
	}

	/**
	 * 传入表名和字段名称传出自增1的序号 序号代码为000-999
	 * 
	 * @date 2016年6月25日 下午3:57:25
	 * @author wjfu 01116035
	 * @param tbName
	 *            表名
	 * @param fields
	 *            字段名 不传则默认为DM 传入多个字段则只取第一个字段
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> genCodeByTbNameAndFiled(String tbName, String... fields) throws Exception {
		Map<String, Object> res = Maps.newHashMap();
		String field = "DM";
		if (fields.length != 0) {
			field = fields[0];
		}
		StringBuilder dmSql = new StringBuilder();
		// 需要获取到最大的数字
		dmSql.append("SELECT MAX(TO_NUMBER(");
		dmSql.append(field);
		dmSql.append(")) FROM ");
		dmSql.append(tbName);
		dmSql.append(" WHERE REGEXP_LIKE(");
		dmSql.append(field);
		dmSql.append(", '^[[:digit:]]+$')");
		Object dm = queryOne(dmSql.toString());
		int code = 0;
		try {
			if (StringUtil.isNotEmptyObj(dm)) {
				code = Integer.parseInt(StringUtil.getString(dm));
			}
		} catch (Exception e) {
			log.error("CODE PARSE ERR WITH " + dm, e);
		}
		String dmStr = new DecimalFormat("000").format(++code);
		res.put(Constants.SUCCESS, true);
		res.put(field, dmStr);
		return res;
	}

	/**
	 * 获取页面数和页面大小
	 * 
	 * @date 2016年7月28日 下午6:37:01
	 * @author xianghao
	 * @param params
	 *            前端传入的信息
	 * @param pageSize
	 *            默认的页面大小
	 * @return Map，value为页面数,页面大小,开始页和结束页
	 */
	public static Map<String, Integer> getPageInfo(Map<String, Object> params, int pageSize) {
		Object pageNumObj = params.get(Constants.PAGE_NUMBER);
		Object pageSizeObj = params.get(Constants.PAGE_SIZE);
		Integer pagenum = StringUtil.isEmptyObj(pageNumObj) ? Integer.valueOf(1)
				: Integer.parseInt(StringUtil.getString(pageNumObj));
		Integer pagesize = StringUtil.isEmptyObj(pageSizeObj) ? Integer.valueOf(pageSize)
				: Integer.parseInt(StringUtil.getString(pageSizeObj));
		Map<String, Integer> resultMap = new HashMap<String, Integer>();
		resultMap.put(Constants.PAGE_NUM, pagenum);
		resultMap.put(Constants.PAGE_SIZE, pagesize);
		resultMap.put(Constants.PAGE_START, ((pagenum - 1) * pagesize) + 1);
		resultMap.put(Constants.PAGE_END, pagenum * pagesize);
		return resultMap;
	}

	/**
	 * 获取分页sql的模板
	 * 
	 * @date 2016年10月29日 上午11:03:03
	 * @author xianghao
	 * @param sql
	 * @return
	 */
	public static String genPaginationSql(String sql) {
		StringBuilder sqlSB = new StringBuilder();
		sqlSB.append("SELECT *  FROM (SELECT A.*, ROWNUM RN FROM( ");
		sqlSB.append(sql);
		sqlSB.append("         ) A WHERE ROWNUM <= ?)  WHERE RN >= ? ");
		return sqlSB.toString();
	}
}
