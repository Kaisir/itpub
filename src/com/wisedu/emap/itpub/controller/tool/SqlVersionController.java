/*
 * @Project itservicecommon
 * @Package com.wisedu.emap.it.controller.tool
 * @date 2016年6月2日 下午2:25:40
 * @author wjfu 01116035
 */
package com.wisedu.emap.itpub.controller.tool;

import com.google.common.base.Splitter;
import com.wisedu.emap.itpub.bean.Constants;
import com.wisedu.emap.itpub.controller.BaseController;
import com.wisedu.emap.itpub.util.GenVersionUtil;
import com.wisedu.emap.itpub.util.HttpUtils;
import com.wisedu.emap.itpub.util.ITPubPropsUtil;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @filename SqlVersionController.java
 * @date 2016年6月2日 下午2:25:40
 * @author wjfu 01116035
 */
@RequestMapping("/sql")
@Controller
public class SqlVersionController extends BaseController {

	@RequestMapping("/excel.do")
	public String toExcelExport(ModelMap model, HttpServletResponse resp) throws Exception {
		if ("false".equals(ITPubPropsUtil.getProp("SYS.DEV_MODE", "false"))) {
			resp.sendError(404);
			return "404";
		}
		model.put("resServer", ITPubPropsUtil.getProp(Constants.COMMON_RES_SERVER, "http://res.wisedu.com"));
		model.put("tables", GenVersionUtil.getItTableNames());
		return getJspPath("modules/excelExport.jsp");
	}

	@RequestMapping("/index.do")
	public String toIndex(ModelMap model) throws Exception {
		if ("false".equals(ITPubPropsUtil.getProp("SYS.DEV_MODE", "false"))) {
			return "404";
		}
		model.put("resServer", ITPubPropsUtil.getProp(Constants.COMMON_RES_SERVER, "http://res.wisedu.com"));
		model.put("tables", GenVersionUtil.getItTableNames());
		return getJspPath("modules/versionSql.jsp");
	}

	@RequestMapping("/insert.do")
	public String toInsertSql(ModelMap model) throws Exception {
		if ("false".equals(ITPubPropsUtil.getProp("SYS.DEV_MODE", "false"))) {
			return "404";
		}
		model.put("resServer", ITPubPropsUtil.getProp(Constants.COMMON_RES_SERVER, "http://res.wisedu.com"));
		return getJspPath("modules/insertSql.jsp");
	}

	@RequestMapping("genInsertVersion.do")
	public void genInsertVersion(String insertSql, String tableName, HttpServletResponse response) throws Exception {
		if ("false".equals(ITPubPropsUtil.getProp("SYS.DEV_MODE", "false"))) {
			return;
		}
		Map<String, Object> param = HttpUtils.getParameterMap();
		OutputStream out = response.getOutputStream();
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/xml");
		response.setHeader("Content-Disposition", "attachment;filename=version.xml");
		GenVersionUtil.genInsertVersionXml(param, out);
	}

	@RequestMapping("genVersion.do")
	public void genSqlVersion(String tableName, HttpServletResponse resp) throws Exception {
		if ("false".equals(ITPubPropsUtil.getProp("SYS.DEV_MODE", "false"))) {
			return;
		}
		List<String> tableNames = Splitter.on(";").splitToList(tableName);
		if (tableNames.isEmpty()) {
			return;
		}
		resp.reset();
		OutputStream out = resp.getOutputStream();
		resp.setCharacterEncoding("utf-8");
		resp.setContentType("text/xml");
		resp.setHeader("Content-Disposition", "attachment;filename=version.xml");
		GenVersionUtil.genDbVersionXml(tableNames, out);
	}

	@RequestMapping("genExcel.do")
	public void genExcel(String tableName, HttpServletResponse resp) throws Exception {
		if ("false".equals(ITPubPropsUtil.getProp("SYS.DEV_MODE", "false"))) {
			return;
		}
		List<String> tableNames = Splitter.on(";").splitToList(tableName);
		if (tableNames.isEmpty()) {
			return;
		}
		resp.reset();
		OutputStream out = resp.getOutputStream();
		resp.setCharacterEncoding("utf-8");
		resp.setContentType("multipart/form-data");
		resp.setHeader("Content-Disposition", "attachment;filename=tableInfo.xls");
		GenVersionUtil.genDbExcelInfo(tableNames, out);
	}

}
