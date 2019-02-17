package com.wisedu.emap.itpub.util;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import com.wisedu.emap.itpub.bean.Constants;

/**
 * @filename EmapDataBuildUtil.java
 * @date 2016年5月18日 上午11:34:50
 * @author wjfu 01116035
 */
public class EmapDataBuildUtil {

	private EmapDataBuildUtil() {
	}

	/**
	 * 此方法用于组装emap的下拉对象 如果未传入rowHandle 则返回无rows的外层包装
	 * 
	 * @date 2016年5月18日 上午11:49:38
	 * @author wjfu 01116035
	 * @param rowHandle
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> wrapDropList(List<?> rows) throws Exception {
		Map<String, Object> rowsMap = Maps.newHashMap();
		// 通过接口处理rows 让外部的方法归一
		rowsMap.put(Constants.ROWS, rows);
		Map<String, Object> codeMap = Maps.newHashMap();
		codeMap.put(Constants.CODE, rowsMap);
		Map<String, Object> resultMap = Maps.newHashMap();
		resultMap.put(Constants.DATAS, codeMap);
		return resultMap;
	}

	/**
	 * 
	 * @date 2016年5月27日 下午12:33:45
	 * @author wjfu 01116035
	 * @param action
	 * @param rows
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> wrapTableList(String action, List<?> rows) throws Exception {
		Map<String, Object> rowsMap = Maps.newHashMap();
		// 通过接口处理rows 让外部的方法归一
		rowsMap.put(Constants.ROWS, rows);
		Map<String, Object> codeMap = Maps.newHashMap();
		codeMap.put(action, rowsMap);
		Map<String, Object> resultMap = Maps.newHashMap();
		resultMap.put(Constants.DATAS, codeMap);
		resultMap.put(Constants.CODE, Constants.CODE_OK);
		return resultMap;
	}

	/**
	 * 将动作查询的queryResult包装为结果返回
	 * 
	 * @date 2016年6月12日 下午4:40:36
	 * @author wjfu 01116035
	 * @param action
	 * @param queryResult
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> wrapQueryResult(String action, Object queryResult) throws Exception {
		Map<String, Object> codeMap = Maps.newHashMap();
		codeMap.put(action, queryResult);
		Map<String, Object> resultMap = Maps.newHashMap();
		resultMap.put(Constants.DATAS, codeMap);
		resultMap.put(Constants.CODE, Constants.CODE_OK);
		return resultMap;
	}

}
