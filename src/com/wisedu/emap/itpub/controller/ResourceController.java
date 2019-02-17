package com.wisedu.emap.itpub.controller;

import com.wisedu.emap.itpub.bean.Constants;
import com.wisedu.emap.itpub.util.ITPubPropsUtil;
import com.wisedu.emap.mvc.CurrentInfo;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import self.micromagic.util.annotation.Config;

/**
 * @filename ResController.java
 * @date 2016年4月22日 下午2:12:33
 * @author wjfu 01116035
 */
@Controller
public class ResourceController extends BaseController {

	@Config(name = "ZHUGE_DEBUG", defaultValue = "false", description = "诸葛IO的调试开关发布时候此项应该为fasle")
	private static boolean zhugeDebug;

	/**
	 * 加载公共的footer
	 * 
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/footer.do")
	public String getFooterResource(ModelMap model, HttpServletRequest request) throws Exception {
		model.put("resServer", ITPubPropsUtil.getProp(Constants.COMMON_RES_SERVER, "http://res.wisedu.com"));
		model.put("basePath", request.getContextPath() + "/sys/" + CurrentInfo.getInfo().getAppName());
		return getJspPath("common/foot.jsp");
	}

	/**
	 * 加载百度统计的代码
	 * 
	 * @param mode
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/header.do")
	public String getStatisticCommon(ModelMap model, HttpServletRequest request) throws Exception {
		// 认为主页请求的/**/*.jsp的**为appName
		String url = request.getServletPath();
		if (url.indexOf('/') >= 0) {
			url = url.substring(0, url.lastIndexOf("/"));
		}
		if (url.indexOf('/') >= 0) {
			url = url.substring(url.lastIndexOf("/") + 1);
		}
		// String statisticSwith = PropUtils.getProp("STATISTIC_SWITCH", "OFF");
		// // 先查询应用的统计key
		// String statisticId = PropUtils.getProp("STATISTIC_KEY." +
		// url.toUpperCase(Locale.ENGLISH));
		// // 如果不存在应用的统计key 则使用公共的统计key
		// if(StringUtil.isEmpty(statisticId)) {
		// statisticId = PropUtils.getProp("STATISTIC_KEY");
		// }
		// // 如果依旧找不到key或者统计开关为关闭状态 则不开启统计功能
		// if("OFF".equals(statisticSwith) || StringUtils.isEmpty(statisticId))
		// {
		// model.put("isStatisticOpen", "N");
		// } else {
		// }
		String statisticId = ITPubPropsUtil.getProp("STATISTIC_KEY." + url.toUpperCase(Locale.ENGLISH));
		model.put("statisticId", statisticId);
		model.put("zhugeDebug", zhugeDebug);
		return getJspPath("common/header.jsp");
	}

}
