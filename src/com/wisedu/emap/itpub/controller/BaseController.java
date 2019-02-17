package com.wisedu.emap.itpub.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wisedu.emap.pedestal.core.AppManager;

/**
 * 基础控制器,方法规则如下
 * 1. Json格式请求
 *         public @ResponseBody Map<String,Object> methodName(...)throws Exception;
 *         返回值一般为Map,其中需要包含success: true/false 表示操作是否成功,与业务是否成功无关
 *     也可以返回其它类型,主要用于控件的集成.(不建议使用)
 * 2. Html格式请求
 *         public String methodName(ModelMap model,...)throws Exception;
 *     返回路径需要通过getPath()进行转换.
 * 注解规则
 *     1. 尽量使用类REST风格注释,例如:/doc/view/{jzgbh}.do
 * 2. 注解路径都以/开头.do结尾,每个Controller类上都需要加上@RequestMapping限定命名空间路径
 * 3. 请求路径名称尽量和方法名一致
 * @author mengbin
 * @date 2015年5月13日 下午1:22:46
 */
public abstract class BaseController {
    
    protected static final Log LOG = LogFactory.getLog(BaseController.class);
    
    /**
     * 获取工程JSP文件位置
     * @author mengbin
     * @date 2015年9月14日 下午8:16:08
     * @param path
     * @return
     */
    protected String getJspPath(String path){
        return AppManager.currentApp().locateJSP(path);
    }
}
