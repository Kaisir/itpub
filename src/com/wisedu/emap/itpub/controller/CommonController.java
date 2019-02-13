package com.wisedu.emap.itpub.controller;

import com.wisedu.emap.base.util.StringUtil;
import com.wisedu.emap.itpub.service.ICommonService;
import com.wisedu.emap.itpub.service.IConfigService;
import com.wisedu.emap.itpub.util.EnvUtils;
import com.wisedu.emap.itpub.util.HttpUtils;
import com.wisedu.emap.itpub.util.JsonUtils;
import com.wisedu.emap.itpub.util.UserUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.slf4j.Slf4j;

/**
 * 通用的url请求
 */
@Slf4j
@RequestMapping("/common")
@Controller
public class CommonController extends BaseController {

	@Autowired
	private ICommonService commonSV;

	@Autowired
	private IConfigService configSV;

	/**
	 * 部门选择的下拉数据
	 * 
	 * @author 01116035
	 * @date 2016-3-31 17:11:16
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/getDeptSelect.do")
	@ResponseBody
	public Map<String, Object> getDeptSelect() throws Exception {
		return commonSV.getDeptsWithRestService();
	}

	/**
	 * 获取审核组下用户接口
	 *
	 * @date 2016年5月18日 下午1:36:50
	 * @author wjfu 01116035
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/getAuditUsers.do")
	@ResponseBody
	public Object getUsersWithDeptAndRoleGroup() throws Exception {
		Map<String, Object> params = HttpUtils.getParameterMap();
		String param = StringUtil.getString(params.get("param"));
		if (StringUtil.isNotEmpty(param)) {
			Map<String, Object> paramMap = JsonUtils.json2Map(param);
			String deptCode = StringUtil.getString(paramMap.get("deptCode"));
			String groupId = StringUtil.getString(paramMap.get("groupId"));
			log.info(deptCode);
			log.info(groupId);
		}
		return "T2009027";
	}

	/**
	 * 获取指定appId的用户组信息
	 *
	 * @date 2016年5月19日 下午8:01:42
	 * @author wjfu 01116035
	 * @param appId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/getAppUserGroups.do")
	@ResponseBody
	public Map<String, Object> getAppUserGroups(String appId) throws Exception {
		return commonSV.getAppUserGroups(appId);
	}

	/**
	 * 获取人员的头像
	 *
	 * @date 2016年8月4日 下午7:54:37
	 * @author wjfu 01116035
	 * @param id
	 * @param resp
	 * @throws IOException
	 */
	@RequestMapping("/headPic.do")
	public @ResponseBody void headPic(String id, HttpServletResponse resp) throws IOException {
		String userAvatar = "";
		// 公有云环境直接跳转到头像地址
		if (EnvUtils.isCloudEnv()) {
			userAvatar = UserUtils.getCloudUser().getUserAvatar();
			if (StringUtil.isNotEmpty(userAvatar)) {
				resp.sendRedirect(userAvatar);
				return;
			}
		} else {
			// 第二个参数传入1为照片地址 2为流
			userAvatar = UserUtils.getUserPicById(id, "2");
		}
		InputStream is = null;
		if (StringUtil.isEmpty(userAvatar)) {
			// 如果找不到上传的文件流 就加载默认的图片
			is = getClass().getClassLoader().getResourceAsStream("default_img.png");
		} else {
			is = new ByteArrayInputStream(Base64.decodeBase64(userAvatar.getBytes()));
		}
		if (is != null) {
			try {
				resp.setContentType("image/jpg");
				IOUtils.copy(is, resp.getOutputStream());
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			} finally {
				IOUtils.closeQuietly(is);
			}
		}
	}

	/**
	 * 通过token展示图片 没有图片流的时候展示一个头像
	 *
	 * @date 2016年11月14日 下午9:53:44
	 * @author wjfu 01116035
	 * @param fileToken
	 * @param resp
	 * @throws Exception
	 */
	@RequestMapping("/showImgByToken/{fileToken}.do")
	public void showImgByToken(@PathVariable String fileToken, HttpServletResponse resp) throws Exception {
		String imgType = HttpUtils.getParameter("imgType");
		imgType = StringUtil.isEmpty(imgType) ? "1" : imgType;
		InputStream is = commonSV.getFistPicStreamByToken(fileToken, Integer.parseInt(imgType));
		if (is == null) {
			// 如果找不到上传的文件流 就加载默认的图片
			is = getClass().getClassLoader().getResourceAsStream("default_img.png");
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

	/**
	 * 改变应用的角色的触发的方法
	 *
	 * @date 2016年12月8日 下午7:02:21
	 * @author wjfu 01116035
	 * @param appName
	 * @param roleId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/changeAppRole/{appName}/{roleId}.do")
	public @ResponseBody Map<String, Object> changeAppRole(@PathVariable String appName, @PathVariable String roleId)
			throws Exception {
		return configSV.changeAppRole(appName, roleId);
	}

}
