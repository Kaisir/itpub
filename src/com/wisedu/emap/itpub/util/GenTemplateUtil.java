package com.wisedu.emap.itpub.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import self.micromagic.util.annotation.Config;

/**
 * 生成模板文件的工具
 * 
 * @author 01118203
 *
 */
public class GenTemplateUtil {

	private static Configuration configuration = null;

	private Map<String, Object> params = new HashMap<String, Object>();

	// 项目路径，例：C:\EMAP\studio\workspace
	private static String PROJECT_PATH = System.getProperty("user.dir").replace("server", "studio/workspace/");

	@Config(name = "template.file.path", description = "模板文件目录")
	private static String TEMPLATE_FILE_PATH;

	@Config(name = "author", description = "作者")
	private static String AUTHOR;

	public GenTemplateUtil(Map<String, Object> data) {

		params.put("author", AUTHOR);
		params.put("date", DateUtils.getCurFullTime());

		params.putAll(data);
	}

	/**
	 * Freemarker 模板环境配置
	 * 
	 * @return
	 * @throws IOException
	 */
	private Configuration initFreemarkerConfiguration() {
		try {
			if (configuration == null) {
				configuration = new Configuration(Configuration.VERSION_2_3_23);
				configuration.setDirectoryForTemplateLoading(new File(PROJECT_PATH + TEMPLATE_FILE_PATH));
				configuration.setDefaultEncoding("UTF-8");
				configuration.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
			}
		} catch (IOException e) {
			throw new RuntimeException("Freemarker 模板环境初始化异常!", e);
		}
		return configuration;
	}

	/**
	 * 生成继承itpub的文件
	 * 
	 * @return
	 * @throws Exception
	 */
	public String genTemplateFiles() throws Exception {

		Configuration cfg = initFreemarkerConfiguration();

		String appName = (String) params.get("appName");

		// 生成classpath文件,继承itpu、引用emapflow
		File classpath = new File(PROJECT_PATH + appName + "/" + ".classpath");
		if (!classpath.getParentFile().exists()) {
			return "无法找到应用：" + appName;
		}

		// 生成继承itpub路由的controller
		File controllerFile = new File(PROJECT_PATH + appName + "/src/com/wisedu/emap/" + appName + "/controller/"
				+ "AppIndexController.java");

		// 删除原来的index目录
		// 重写config.js和index.jsp
		File configjs = new File(PROJECT_PATH + appName + "/web/" + "config.js");
		File indexjsp = new File(PROJECT_PATH + appName + "/web/" + "index.jsp");
		if (!configjs.getParentFile().exists()) {
			return "无法找到web目录！";
		}

		// 生成web目录的文件common.jsp、source.jsp
		File commonjsp = new File(PROJECT_PATH + appName + "/web/common/" + "common.jsp");
		File sourcejsp = new File(PROJECT_PATH + appName + "/web/common/" + "source.jsp");
		if (!commonjsp.getParentFile().exists()) {
			commonjsp.getParentFile().mkdirs();
		}

		cfg.getTemplate("classpath.ftl").process(params, new FileWriter(classpath));
		cfg.getTemplate("controller.ftl").process(params, new FileWriter(controllerFile));
		cfg.getTemplate("configjs.ftl").process(params, new FileWriter(configjs));
		cfg.getTemplate("indexjsp.ftl").process(params, new FileWriter(indexjsp));
		cfg.getTemplate("commonjsp.ftl").process(params, new FileWriter(commonjsp));
		cfg.getTemplate("sourcejsp.ftl").process(params, new FileWriter(sourcejsp));

		return "生成itpub模板文件成功！";

	}

	/**
	 * 生成dao、impl
	 * 
	 * @return
	 * @throws Exception
	 */
	public String genDaoImplFiles() throws Exception {

		Configuration cfg = initFreemarkerConfiguration();

		String appName = (String) params.get("appName");
		String daoName = (String) params.get("daoName");

		File daofile = new File(
				PROJECT_PATH + appName + "/src/com/wisedu/emap/" + appName + "/dao/" + daoName + ".java");
		if (!daofile.getParentFile().exists()) {
			daofile.getParentFile().mkdirs();
		}
		cfg.getTemplate("dao.ftl").process(params, new FileWriter(daofile));

		File implfile = new File(
				PROJECT_PATH + appName + "/src/com/wisedu/emap/" + appName + "/dao/impl/" + daoName + "_IMPL.java");
		if (!implfile.getParentFile().exists()) {
			implfile.getParentFile().mkdirs();
		}
		cfg.getTemplate("daoimpl.ftl").process(params, new FileWriter(implfile));

		return "生成dao模板文件成功！";
	}
}
