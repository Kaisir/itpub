package com.wisedu.emap.itpub.controller.tool;

import com.google.common.collect.ImmutableMap;
import com.wisedu.emap.itpub.bean.Constants;
import com.wisedu.emap.itpub.util.ITPubPropsUtil;
import com.wisedu.emap.itpub.util.RestfulConf;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 重新加载配置的控制器
 * 
 * @filename ResourceReloadController.java
 * @date 2016年7月29日 下午3:05:42
 * @author wjfu 01116035
 */
@Controller
@RequestMapping("/reload")
public class ResourceReloadController {

	/**
	 * 重载所有的配置文件
	 * 
	 * @date 2016年7月29日 下午3:13:07
	 * @author wjfu 01116035
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/reloadAll.do")
	public @ResponseBody Map<String, Object> reloadAll() throws Exception {
		// 先重载数据库配置
		ITPubPropsUtil.reload();
		// 再重载其他配置
		RestfulConf.reload();
		return ImmutableMap.<String, Object> of(Constants.SUCCESS, true);
	}

	/**
	 * 重载数据库配置
	 * 
	 * @date 2016年7月29日 下午3:13:07
	 * @author wjfu 01116035
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/reloadProps.do")
	public @ResponseBody Map<String, Object> reloadProps() throws Exception {
		// 先重载数据库配置
		ITPubPropsUtil.reload();
		return ImmutableMap.<String, Object> of(Constants.SUCCESS, true);
	}

	/**
	 * 重载restful的配置
	 * 
	 * @date 2016年7月29日 下午3:13:07
	 * @author wjfu 01116035
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/reloadRestful.do")
	public @ResponseBody Map<String, Object> reloadRestful() throws Exception {
		// 先重载数据库配置
		RestfulConf.reload();
		return ImmutableMap.<String, Object> of(Constants.SUCCESS, true);
	}

}
