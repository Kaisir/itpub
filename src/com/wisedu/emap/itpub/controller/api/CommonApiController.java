/*
 * @Project itservicecommon
 * @Package com.wisedu.emap.it.controller.api
 * @date 2016年5月24日 下午3:23:29
 * @author wjfu 01116035
 */
package com.wisedu.emap.itpub.controller.api;

import com.google.common.collect.ImmutableMap;
import com.wisedu.emap.base.util.StringUtil;
import com.wisedu.emap.itpub.bean.Constants;
import com.wisedu.emap.itpub.bean.analysis.AnalysisData;
import com.wisedu.emap.itpub.service.ICommonService;
import com.wisedu.emap.itpub.service.IConfigService;
import com.wisedu.emap.itpub.util.DbUtil;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @filename RestfulRequestController.java
 * @date 2016年5月24日 下午3:23:29
 * @author wjfu 01116035
 */
@RequestMapping("/api")
@Controller
public class CommonApiController {

	@Autowired
	private ICommonService commonSV;

	@Autowired
	private IConfigService configSV;

	/**
	 * 获取流程中心的处理人接口
	 * 
	 * @date 2016年5月24日 下午3:23:54
	 * @author wjfu 01116035
	 * @param groupId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/getDeptActors.do")
	@ResponseBody
	public String getDeptActors(String groupId, String deptCode) throws Exception {
		return commonSV.getWorkflowActors(groupId, deptCode);
	}

	/**
	 * 获取用户的权限信息
	 * 
	 * @author xianghao
	 * @date 2016年8月2日 上午9:37:36
	 * @param appId
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/getUserLimit.do")
	@ResponseBody
	public List<Map<String, Object>> getUserLimit(String appName) throws Exception {
		return configSV.getUserLimit(appName);
	}

	/**
	 * 获取当前登陆用户的具体信息 此用户从emap中获取
	 * 
	 * @date 2016年9月9日 上午11:57:12
	 * @author wjfu 01116035
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/getUserDetails.do")
	@ResponseBody
	public Map<String, Object> getUserDetails() throws Exception {
		return commonSV.getUserDetailInfo();
	}

	/**
	 * 获取当前服务器的时间
	 * 
	 * @date 2016年10月11日 下午3:04:03
	 * @author wjfu 01116035
	 * @return yyyy-MM-dd HH:mm:ss类型的当前数据库服务器时间
	 * @throws Exception
	 */
	@RequestMapping("/getServerCurrentTime.do")
	public @ResponseBody Object getServerCurrentTime() throws Exception {
		return DbUtil.queryOne(" SELECT TO_CHAR(SYSDATE, 'YYYY-MM-DD HH24:MI:SS') FROM DUAL ");
	}

	/**
	 * 获取当前用户的数据分析项
	 * 
	 * @date 2016年12月19日 上午11:12:01
	 * @author wjfu 01116035
	 * @param appName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/queryAppAndUserDatas.do")
	public @ResponseBody Map<String, Object> queryAppAndUserDatas(String appName) throws Exception {
		AnalysisData analysisData = commonSV.queryAppAndUserDatas(appName);
		return ImmutableMap.<String, Object> of(Constants.CODE, HttpStatus.SC_OK, "analysisData", analysisData);
	}

	/**
	 * 通过token查询显示封面图片
	 * 
	 * @date 2017年3月13日 上午9:58:55
	 * @author wjfu 01116035
	 * @param token
	 * @param resp
	 * @throws Exception
	 */
	@RequestMapping("/frontImg/{token}.do")
	public void setPicByTokenApply(@PathVariable String token, HttpServletResponse resp) throws Exception {
		InputStream is = null;
		if (StringUtil.isEmpty(token) || "null".equals(token) || "undefined".equals(token)) {
			is = getClass().getClassLoader().getResourceAsStream("default.png");
		} else {
			// 第二个参数传入1为低质量图片 做列表展示
			is = commonSV.getFistPicStreamByToken(token, 1);
			if (is == null) {
				// 如果没有低质量的图片就查找原图
				is = commonSV.getFistPicStreamByToken(token, 0);
				// 如果原图也不存在 则加载默认图片
				if (is == null) {
					// 如果找不到上传的文件流 就加载默认的图片
					is = getClass().getClassLoader().getResourceAsStream("default.png");
				}
			}
		}
		if (is != null) {
			try {
				IOUtils.copy(is, resp.getOutputStream());
			} catch (Exception e) {
				throw e;
			} finally {
				IOUtils.closeQuietly(is);
			}
		}
	}
}
