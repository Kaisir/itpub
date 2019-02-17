/*
 * @Project itservicecommon
 * @Package com.wisedu.emap.it.controller.base
 * @date 2016年6月14日 下午3:32:07
 * @author wjfu 01116035
 */
package com.wisedu.emap.itpub.controller.api;

import com.wisedu.emap.itpub.util.DbUtil;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 通用的对外的api基类接口 此基类提供通用的一些对外的路由 子类应当注解在@RequestMapping("/api")路由下
 * 
 * @filename ApiBaseController.java
 * @date 2016年6月14日 下午3:32:07
 * @author wjfu 01116035
 */
public class ApiBaseController {

	/**
	 * 查询字典
	 * 
	 * @date 2016年6月13日 下午3:58:57
	 * @author wjfu 01116035
	 * @param dicId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/dic/{dicId}.do")
	public @ResponseBody Object getDic(@PathVariable String dicId) throws Exception {
		return DbUtil.getDicById(dicId, null);
	}

	/**
	 * 查询类型不同的字典字典
	 * 
	 * @date 2016年6月17日 上午10:28:43
	 * @author wjfu 01116035
	 * @param dicId
	 * @param type
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/dic/{dicId}/{type}.do")
	public @ResponseBody Object getDic(@PathVariable String dicId, @PathVariable String type) throws Exception {
		return DbUtil.getDicById(dicId, type);
	}
}
