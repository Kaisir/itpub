package com.wisedu.emap.itpub.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.JsonObject;
import com.wisedu.emap.base.util.LogUtil;
import com.wisedu.emap.itpub.service.PublicApplySettingService;
import com.wisedu.emap.itpub.util.ComJsonUtil;
import com.wisedu.emap.itpub.util.ResponseUtil;

/**
 * 公共申请表设置
 * 用于提交大文本内容
 *
 */
@Controller
@RequestMapping("/sys/itpub/publicApplySetting")
public class PublicApplySettingController {
	protected static org.slf4j.Logger logger = LogUtil.getOnlineLog();
	@Autowired
	private PublicApplySettingService publicApplySettingService;
	
	
	@ResponseBody
	@RequestMapping("dataModelExecute")
	public JsonObject dataModelExecute(HttpServletRequest request ){
		
		try{
			String paramString = ComJsonUtil.readJSONString(request);
			publicApplySettingService.dataModelExecute(paramString);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			return ResponseUtil.buildErrorResponse(e.getMessage());
		}
    	return ResponseUtil.buildSuccessResponse(null);
		
	}
}
