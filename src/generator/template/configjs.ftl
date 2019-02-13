define(function(require, exports, module) {
	var config = {
	    "BH_VERSION":"1.2",
		"FE_DEBUG_MODE": false,
		"RESOURCE_SERVER": window.resServer,
		'API_BASE_PATH': window.basePath,
		 "SERVER_CONFIG_API": contextPath + "/sys/funauthapp/api/getAppConfig/" + APPNAME + "-" + APPID + ".do",
		'APP_ENTRY': "",
		"APP_TITLE": window.APP_TITLE || "${appCnName}",
		"FOOTER_TEXT": window.FOOTER_TEXT,
		"HEADER": {
			"dropMenuCallback": function(item) {
				$.changeAppRole(APPNAME, item.id);
			},
			"logo": window.resServer + "/images/logo.png",
			"icons": ["icon-apps"],
			"userImage": window.USER_INFO.image,
			"userInfo": window.USER_INFO
		}
	};
	return config;
});