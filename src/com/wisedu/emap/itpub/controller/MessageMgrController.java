package com.wisedu.emap.itpub.controller;

import com.wisedu.emap.itpub.service.ICommonService;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 信息设置的公共接口路由
 * 
 * @filename MessageMgrController.java
 * @date 2016年5月31日 下午4:55:24
 * @author wjfu 01116035
 */
@Controller
@RequestMapping("/mrSetting")
public class MessageMgrController {

	@Autowired
	private ICommonService commonSV;

	/**
	 * 消息通知状态开关切换时候触发的方法 成功返回success失败抛出异常
	 * 
	 * @date 2016年4月11日 上午10:03:14
	 * @author wjfu 01116035
	 * @param remaindStatus
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/setMessageRemainChange.do")
	@ResponseBody
	public String setMessageRemainChange(boolean remaindStatus, String xtName, String remaindName) throws Exception {
		return commonSV.messageRemaindStatusChange(remaindStatus, xtName, remaindName);
	}

	/**
	 * 获取初始化时候的消息设置状态 status 状态 / types 提醒的方式
	 * 
	 * @date 2016年4月11日 上午10:15:37
	 * @author wjfu 01116035
	 * @return
	 */
	@RequestMapping("/initMrSetting.do")
	@ResponseBody
	public Map<String, Object> initMrSetting(String xtName) throws Exception {
		return commonSV.getMrSetting(xtName);
	}

	/**
	 * 保存域名提醒的类型
	 * 
	 * @date 2016年4月11日 下午1:55:09
	 * @author wjfu 01116035
	 * @param types
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/saveMrRemaindTypes.do")
	@ResponseBody
	public String saveMrRemaindTypes(String xtName, String types) throws Exception {
		return commonSV.saveMrTypes(types, xtName);
	}

}
