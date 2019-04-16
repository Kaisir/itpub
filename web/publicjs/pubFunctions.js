/**
 * 封装了常用的前端组件的使用方法
 * 使用本文件中的方法，需先引用***文件
 * 其他说明...
 * 创建时间：
 * 创建人：
 * 最后修改时间：
 * 最后修改人：
 */

;
var PUB_FUNC = {};

var default_db_error = {
	"resultCode": "19999",
	"msg": "unkown error"
};

/*
 *高级搜索中的文本字段集合，用于在高级搜索时将这些字段的条件构造器改为include，实现模糊搜索
 */
var ADVANCE_SERACHER_TEXT_FIELDS = {
	XH: true, //学号
	XM: true, //姓名
	ZGH:true
};

(function(PUB_FUNC) {

	PUB_FUNC.ADVANCE_SERACHER_TEXT_FIELDS = ADVANCE_SERACHER_TEXT_FIELDS;

	PUB_FUNC.REQUEST_PARAM_NAME = "requestParamStr=";

	/**
	 * 封装生成常规form表单的方法
	 * 说明：必传的参数有containerName,pageUrl,actionName
	 * @param containerName html/jsp文件中用于存放form的DOM元素的id属性值 e.g.  <div id="formCon"></div> 参数值为"formCon"
	 * @param pageUrl 页面路径（不含".do"） e.g. "/modules*teacher/violate_query"
	 * @param actionName 动作名称 e.g. "cxxscfjlxq"
	 * @param params 动作参数 e.g. {XSBH:'123',WID:'abc'}
	 * @param columns 列数 正整数
	 * @param readonly 是否只读 true/false
	 * @param isEmpty 是否创建一个空表单 为true表示生成的是一个新增表单 默认为false
	 * @param model 表单类型 分为水平和垂直两种，支持参数"h"（水平）/"v"（垂直），默认"h"
	 * @param inputWidth 水平表单 表单控件所占列数  默认6  最高12
	 */
	PUB_FUNC.buildFormByList = function(containerName, pageUrl, actionName, params, columns, readonly, isEmpty, model, fileOptions, rootpath, dataModel, inputWidth, defaultOptions) {
		var ROOT_PATH = rootpath ? rootpath : WIS_CONFIG.ROOT_PATH;
		var datamodel = WIS_EMAP_SERV.getModel(ROOT_PATH + pageUrl + ".do", actionName, "form");
		datamodel = dataModel ? dataModel : datamodel;
		var defaultOption = defaultOptions ? defaultOptions : {};
		defaultOption["tree"] = {
			unblind: '/'
		};
		//增加新版上传组件option限制信息
		if (fileOptions && fileOptions['cache-upload']) {
			defaultOption['cache-upload'] = fileOptions['cache-upload'];
		}else{
			defaultOption['cache-upload'] = {
					limit: 3,
					size: 5120,
					type: ['doc', 'jpg', 'png', 'jpeg', 'bmp', 'docx', 'zip', 'rar', 'pdf', 'xls', 'xlsx', 'txt']
			};
		}
		if (fileOptions && fileOptions.uploadfile) {
			defaultOption['uploadfile'] = fileOptions.uploadfile;
		} else {
			defaultOption['uploadfile'] = {
				limit: 3,
				size: 5120,
				type: ['doc', 'jpg', 'png', 'jpeg', 'bmp', 'docx', 'zip', 'rar', 'pdf', 'xls', 'xlsx', 'txt']
			};
		}
		if (fileOptions && fileOptions['uploadmuiltimage']) {
			defaultOption['uploadmuiltimage'] = fileOptions['uploadmuiltimage'];
		}else{
			defaultOption['uploadmuiltimage'] = {
					limit: 10,
					size: 5120,
					type: ['jpg', 'png', 'jpeg']
			};
		}
		var formObj = $("#" + containerName).emapForm({
			root: WIS_EMAP_SERV.getContextPath(),
			data: datamodel,
			textareaEasyCheck: true,
			readonly: (readonly == true) ? true : false,
			cols: (columns && !(isNaN(columns))) ? parseInt(columns) : 3,
			model: model ? model : "h",
			inputWidth: inputWidth ? inputWidth : '6',
			defaultOptions: defaultOption
		});
		isEmpty = (isEmpty == true) ? true : false;
		if (!isEmpty) {
			params = (params == undefined) ? null : params;
			var data = BH_UTILS.doSyncAjax(ROOT_PATH + pageUrl + "/" + actionName + ".do", params, "POST");
			var value = data.datas[actionName].rows;
			if (value && value.length > 0) {
				$("#" + containerName).emapForm('setValue', value[0]);
			}
		}
		return formObj;
	};

	/**
	 * 封装生成常规form表单的方法，与buildFormByList的区别在于传的参数不同，该方法接受的参数为拼装好相应属性的对象
	 * 拼装成对象的好处是，不用在意参数的顺序，可以更好的使用默认值
	 * 说明：对象中必需的参数有containerName,pageUrl,actionName
	 * @param paramObj 参数对象
	 */
	PUB_FUNC.buildFormByObj = function(paramObj) {
		var containerName = paramObj.containerName;
		var pageUrl = paramObj.pageUrl;
		var actionName = paramObj.actionName;
		var params = paramObj.params;
		var columns = paramObj.columns;
		var readonly = paramObj.readonly;
		var model = paramObj.model;
		var isEmpty = paramObj.isEmpty;
		isEmpty = (isEmpty == true) ? true : false;
		if (isEmpty) {
			readonly = false;
		}
		var fileOptions = paramObj.fileOptions;
		var rootath = paramObj.rootpath;
		var dataModel = paramObj.dataModel;
		var inputWidth = paramObj.inputWidth;
		var defaultOptions = paramObj.defaultOptions;
		return PUB_FUNC.buildFormByList(containerName, pageUrl, actionName, params, columns, readonly, isEmpty, model, fileOptions, rootath, dataModel, inputWidth, defaultOptions);
	};



	/**
	 * 封装生成常规table的方法
	 * 说明：必传的参数有containerName,pageUrl,actionName
	 * @param containerName html/jsp文件中用于存放生成table的DOM元素的id属性值 e.g. <div id="tableCon"></div> 参数为"tableCon"
	 * @param pageUrl 页面路径（不含.do） e.g. "modules*teacher/violate_query"
	 * @param actionName 动作名 e.g. "wjcfcx"
	 * @param params 动作的参数
	 * @param customColumns 操作列 ,自己拼装的操作列对象数组
	 * @param pageable 是否有分页
	 * @param searchContainerName 高级查询的DOM容器节点的id属性值，如果不传的话，表示没有
	 */
	PUB_FUNC.createTableByList = function(containerName, pageUrl, actionName, params, customColumns, sortable, pageable, searchContainerName, selectionMode, renderedFunction, onceParams, pagerMode, rootPath, lineNum, pageSizeOptions, pageSize,searcherInitComplete) {
		var ROOT_PATH = rootPath ? rootPath : "";
		var options = {
			pagePath: ROOT_PATH + pageUrl + ".do",
			height: null,
			minLineNum: (lineNum == undefined) ? 10 : lineNum,
			params: (params == undefined) ? {} : params,
			action: actionName,
			pageable: (pageable == false) ? false : true,
			sortable: (sortable == true) ? true : false,
			customColumns: (customColumns == undefined) ? [] : customColumns,
			selectionMode: selectionMode ? selectionMode : 'custom',
			onceParams: (onceParams == undefined) ? {} : onceParams,
			enableBrowserSelection: true,
			fastRender: true,
			rendered: renderedFunction,
			pageSizeOptions: (pageSizeOptions == undefined) ? [10, 20, 50, 100] : pageSizeOptions,
			pageSize: (pageSize == undefined) ? 10 : pageSize
		};
		if (pagerMode) {
			options.pagerMode = pagerMode;
		}
		var tableObj = $("#" + containerName).emapdatatable(options);
		//如果参数传了高级查询的DOM元素id,则为table创建一个相关的高级查询组件
		if (searchContainerName) {
			PUB_FUNC.createSearcher(searchContainerName, ROOT_PATH + pageUrl, actionName, function(condition) {
				//生源地单独处理，解决生源地查询时，需要能查询到子节点的相关数据 问题 ----eidt by shifeng  2017-8-9 13:54:27
				if ($('#' + searchContainerName).emapAdvancedQuery(true).options.searchModel == 'advanced') {
					//解析json
					condition = JSON.parse(condition);
					//遍历结果集合，如果是SYDDM, 则判断是否有具体值
					for (var i = 0; i < condition.length; i++) {
						//基本信息 SYDDM CSDDM
						if ((condition[i].name == 'SYDDM' || condition[i].name == 'CSDDM' ||
								condition[i].name == 'JTDZQH' || condition[i].name == 'JG') && condition[i].value.length == 6) {
							var ssyValue = condition[i].value;
							var secondValue = ssyValue.substr(2, 2); //第二级，市
							var thirdValue = ssyValue.substr(4, 2); //第三级 县镇
							condition[i].builder = 'beginWith';
							//市不存在，只留省份
							if (secondValue == '00') {
								condition[i].value = condition[i].value.substr(0, 2);
								continue;
							}
							//县镇不存在，只留省份市 00 表示没有
							if (thirdValue == '00') {
								condition[i].value = condition[i].value.substr(0, 4);
								continue;
							}
						}

						// 政工队伍 ，ZYJSZWDM
						if (condition[i].name == 'ZYJSZWDM' && condition[i].value.length == 3) {
							//参数值
							var ssyValue = condition[i].value;
							var secondValue = ssyValue.substr(2, 1); //第二级
							//市不存在，只留省份
							if (secondValue == '0') {
								condition[i].value = condition[i].value.substr(0, 2);
								continue;
							}
						}
					}
					//格式转换
					condition = JSON.stringify(condition);
				}
				$("#" + containerName).emapdatatable('reloadFirstPage', {
					"querySetting": condition
				});
			},searcherInitComplete);
			
		}
		return tableObj;
	};

	/**
	 * 封装生成常规table的方法，与createTableByList的区别在于传的参数不同，该方法接受的参数为拼装好相应属性的对象
	 * 拼装对象的好处是，不用在意参数的顺序，可以更好的利用默认值
	 * 说明：对象中必需的参数有containerName,pageUrl,actionName
	 * @param paramObj
	 */
	PUB_FUNC.createTableByObj = function(paramObj) {
		var containerName = paramObj.containerName;
		var pageUrl = paramObj.pageUrl;
		var actionName = paramObj.actionName;
		var params = paramObj.params;
		var customColumns = paramObj.customColumns;
		var sortable = paramObj.sortable;
		var pageable = paramObj.pageable;
		var searchContainerName = paramObj.searchContainerName;
		var selectionMode = paramObj.selectionMode;
		var onceParams = paramObj.onceParams;
		var renderedFunction = paramObj.rendered;
		var pagerMode = paramObj.pagerMode;
		var rootpath = paramObj.rootpath;
		var lineNum = paramObj.lineNum;
		var pageSizeOptions = paramObj.pageSizeOptions;
		var pageSize = paramObj.pageSize;
		var searcherInitComplete = paramObj.searcherInitComplete;
		return PUB_FUNC.createTableByList(containerName, pageUrl, actionName, params, customColumns, sortable, pageable, searchContainerName, selectionMode, renderedFunction, onceParams, pagerMode, rootpath, lineNum, pageSizeOptions, pageSize,searcherInitComplete);
	};



	/**
	 * 封装生成高级查询组件的方法
	 * @param containerName html/jsp文件中用于存放高级搜索的DOM元素的id属性值 e.g. <div id="searchCon"></div> 参数为"searchCon"
	 * @param pageUrl 页面路径（不含.do） e.g. "modules*teacher/violate_query"
	 * @param actionName 动作名
	 * @param callback 高级搜索组件查询时的回调函数，该函数可以获得查询条件参数
	 */
	PUB_FUNC.createSearcher = function(containerName, pageUrl, actionName, callback,searcherInitComplete) {
		var dataModel = WIS_EMAP_SERV.getModel(pageUrl + ".do", actionName, "search");
		$("#" + containerName).emapAdvancedQuery({
			data: dataModel,
            initComplete: function() {
                if (searcherInitComplete && (typeof searcherInitComplete) === "function") {
                    searcherInitComplete();
                }
            }
		});
		$("#" + containerName).on('search', function(e, condition) {
			var queryOptions = JSON.parse(condition);
			//将所有注册为文本字段的条件构造器修改为include,用以实现模糊搜索
			$(queryOptions).each(function(index) {
				var queryOption = this;
				//如果为组合条件,则继续遍历
				if (queryOption instanceof Array) {
					$(queryOption).each(function() {
						//如果该字段注册为文本字段，则修改条件构造器
						if (ADVANCE_SERACHER_TEXT_FIELDS[this.name] && this.builder == 'equal') {
							this.builder = 'include';
						}
					});
				} else {
					//如果该字段注册为文本字段，则修改条件构造器
					if (ADVANCE_SERACHER_TEXT_FIELDS[this.name] && this.builder == 'equal') {
						this.builder = 'include';
					}
				}
			});
			condition = JSON.stringify(queryOptions);
			if (typeof(callback) == "function") {
				callback(condition);
			}
		});
	};


	/**
	 * 封装导出功能
	 * @param el 封装好的参数对象，其中需拼装的属性有 "app","contextPath","module","page","action","querySetting","*order","containerId"
	 */
	PUB_FUNC.exportfn = function(el) {

		var $table = $("#" + el.containerId);
		if (el.isGrid) {
			$table = $("#" + el.containerId).emapGrid('getTable');
		}
		var url = el.contextPath + "/sys/emapcomponent/imexport/export.do";
		var params = el;
		params["module"] = el.module ? el.module : "*default";
		if (!el.colnames) {
			var visibleColumns = $table.emapdatatable("getVisibleColumns");
			var colnames = "";
			for (var i = 0; i < visibleColumns.length; i++) {
				if (visibleColumns[i].datafield && visibleColumns[i].datafield != "field_checkbox") {
					colnames += visibleColumns[i].datafield.replace("_DISPLAY", "") + ",";
				}
			}
			params["colnames"] = colnames.substr(0, colnames.length - 1);
		}
		if (!el.order) {
			var pxzd = $table.emapdatatable("getSort");
			if (pxzd && pxzd.length > 0) {
				params["*order"] = pxzd.exp.replace("_DISPLAY", "");
			}
		} else {
			params["*order"] = el.order;
		}

		jQuery.ajax({
			url: url,
			data: params,
			type: 'post',
			dataType: 'json',
			cache: false,
			success: function(ret) {
				var attachment = ret.attachment;
				var url = el.contextPath + "/sys/emapcomponent/file/getAttachmentFile/" + attachment + ".do";
				window.location.href = url;
				return false;
			},
			error: function(resp) {
				if (resp.status == 401) {
					window.location.reload();
				} else if (resp.status == 403) {
					BH_UTILS.bhDialogWarning({
						title: '提示',
						content: '当前角色权限不足，请切换角色后重新操作',
						buttons: [{
							text: '确认',
							className: 'bh-btn-warning',
							callback: function() {}
						}]
					});
					return false;
				}
				// 长时间未操作提示错误
				if (resp.statusText.indexOf("NetworkError") > -1) {
					BH_UTILS.bhDialogDanger({
						title: '网络错误',
						content: '您可以尝试刷新页面解决该问题',
						buttons: [{
							text: '关闭',
							className: 'bh-btn-default'
						}]
					});
					return false;
				}
			}
		});
	};


	/**
	 * 导出功能方法，支持
	 * @param 	el  参数对象
	 * 		   	app 应用名
	 * 		   	contextPath 上下文路径
	 * 		   	module      模块名称
	 * 			page        页面
	 * 			action      动作
	 * 			querySetting 查询参数！
	 * 			*order       排序
	 * 			containerId  表格ID
	 */
	PUB_FUNC.exportExtraFieldfn = function(el) {

		var $table = $("#" + el.containerId);
		if (el.isGrid) {
			$table = $("#" + el.containerId).emapGrid('getTable');
		}
		//参数处理
		if (el.querySetting) {
			var querySetting = JSON.parse(el.querySetting);
			PUB_FUNC.calculateExportSearch(querySetting);
			el.querySetting = JSON.stringify(querySetting);
		}
		var url = el.contextPath + "/sys/emapcomponent/imexport/export.do";
		var params = el;
		params["module"] = el.module ? el.module : "*default";
		if (!el.colnames) {
			var visibleColumns = $table.emapdatatable("getVisibleColumns");
			var colnames = "";
			for (var i = 0; i < visibleColumns.length; i++) {
				if (visibleColumns[i].datafield && visibleColumns[i].datafield != "field_checkbox") {
					colnames += visibleColumns[i].datafield.replace("_DISPLAY", "") + ",";
				}
			}
			params["colnames"] = colnames.substr(0, colnames.length - 1);
		}
		if (!el.order) {
			var pxzd = $table.emapdatatable("getSort");
			if (pxzd && pxzd.length > 0) {
				params["*order"] = pxzd.exp.replace("_DISPLAY", "");
			}
		} else {
			params["*order"] = el.order;
		}
		jQuery.ajax({
			url: url,
			data: params,
			type: 'post',
			dataType: 'json',
			cache: false,
			success: function(ret) {
				var attachment = ret.attachment;
				var url = el.contextPath + "/sys/emapcomponent/file/getAttachmentFile/" + attachment + ".do";
				window.location.href = url;
				return false;
			},
			error: function(resp) {
				if (resp.status == 401) {
					window.location.reload();
				} else if (resp.status == 403) {
					BH_UTILS.bhDialogWarning({
						title: '提示',
						content: '当前角色权限不足，请切换角色后重新操作',
						buttons: [{
							text: '确认',
							className: 'bh-btn-warning',
							callback: function() {}
						}]
					});
					return false;
				}
				// 长时间未操作提示错误
				if (resp.statusText.indexOf("NetworkError") > -1) {
					BH_UTILS.bhDialogDanger({
						title: '网络错误',
						content: '您可以尝试刷新页面解决该问题',
						buttons: [{
							text: '关闭',
							className: 'bh-btn-default'
						}]
					});
					return false;
				}
			}
		});
	};


	/**
	 * 支持生源地，出生地等树形高级检索字段数据处理
	 * 生源地 ，出生地等字段，为三级联动，选择上一级，则把下面的所有数据全部查出来
	 * @param querySetting  对象
	 */
	PUB_FUNC.calculateExportSearch = function(querySetting) {
		//遍历数组
		for (var i = 0; i < querySetting.length; i++) {
			//对象，为高级检索模式
			if (querySetting[i] instanceof Object) {
				//匹配字段， SYDDM ， CSDDM 等字段，且存在查询值
				if ((querySetting[i].name == 'SYDDM' || querySetting[i].name == 'CSDDM' ||
						querySetting[i].name == 'JTDZQH' || querySetting[i].name == 'JG') &&
					querySetting[i].value.length == 6) {
					//参数值
					var ssyValue = querySetting[i].value;
					var secondValue = ssyValue.substr(2, 2); //第二级，市
					var thirdValue = ssyValue.substr(4, 2); //第三级 县镇
					//市不存在，只留省份
					if (secondValue == '00') {
						querySetting[i].value = querySetting[i].value.substr(0, 2);
						continue;
					}
					//县镇不存在，只留省份市 00 表示没有
					if (thirdValue == '00') {
						querySetting[i].value = querySetting[i].value.substr(0, 4);
						continue;
					}
				}

				//  ，ZYJSZWDM
				if (querySetting[i].name == 'ZYJSZWDM' && querySetting[i].value.length == 3) {
					//参数值
					var ssyValue = querySetting[i].value;
					var secondValue = ssyValue.substr(2, 1); //第二级，市
					//市不存在，只留省份
					if (secondValue == '0') {
						querySetting[i].value = querySetting[i].value.substr(0, 2);
						continue;
					}
				}
			}

			//数组复合结构 [ [{},{}] , []]
			if (querySetting[i] instanceof Array) {
				for (var j = 0; j < querySetting[i].length; j++) {
					if (querySetting[i][j] instanceof Object) {
						//匹配字段， SYDDM ， CSDDM 等字段，且存在查询值
						if ((querySetting[i][j].name == 'SYDDM' || querySetting[i][j].name == 'CSDDM' ||
								querySetting[i][j].name == 'JTDZQH' || querySetting[i][j].name == 'JG') && querySetting[i][j].value.length == 6) {
							var ssyValue = querySetting[i][j].value;
							var secondValue = ssyValue.substr(2, 2); //第二级，市
							var thirdValue = ssyValue.substr(4, 2); //第三级 县镇
							//市不存在，只留省份
							if (secondValue == '00') {
								querySetting[i][j].value = querySetting[i][j].value.substr(0, 2);
								continue;
							}
							//县镇不存在，只留省份市 00 表示没有
							if (thirdValue == '00') {
								querySetting[i][j].value = querySetting[i][j].value.substr(0, 4);
								continue;
							}
						}
					}

					//，ZYJSZWDM
					if (querySetting[i].name == 'ZYJSZWDM' && querySetting[i].value.length == 3) {
						//参数值
						var ssyValue = querySetting[i].value;
						var secondValue = ssyValue.substr(2, 1); //第二级，市
						//市不存在，只留省份
						if (secondValue == '0') {
							querySetting[i].value = querySetting[i].value.substr(0, 2);
							continue;
						}
					}
				}
			}
		}
	};

	/**
	 * 封装报表上传导出功能
	 * @param data
	 * 			var data = new FormData();
	 * 			data.append("inputfile", $('#inputfile').prop('files')[0]); //报表文件
				data.append("id",$('#schemeNameID').val());   				//id 为空新增，不为空更新
				data.append("schemeName",lbmc+"离校单");						//方案名称
				data.append("tag",lbdm);									//报表标识
				data.append("appname",WIS_CONFIG.APPNAME);					//报表所在应用名
	 */
	PUB_FUNC.saveReport = function(data, callback) {
		if (!window.location.origin) {
			window.location.origin = window.location.protocol + "//" + window.location.hostname + (window.location.port ? ':' + window.location.port : '');
		}
		/*$.ajax({
		    type: 'POST',
		    processData: false,
		    contentType: false,
		    data: data,
		    url: (window.location.origin + WIS_CONFIG.PATH+"/sys/frReport/webapi/schemes/custom.do"),
		    dataType : 'json',
		    success: function(returnData){
		    	if (returnData != "true") {

				}else{
					callback();
				}
		    }
		});*/

		//modify by 01314118 2016-08-08
		//data 为FormData类型 IE9 不兼容，修改为版本上传组件方式
		//fileupload 使用 send 提交时 files 不能为空，创建默认空值,具体内容在 formData中 提交
		var files = [];
		files.push({
			'lastModified': '',
			'lastModifiedDate': '',
			'name': "",
			'size': 1,
			'type': "",
			'webkitRelativePath': ""
		});
		var $dom = $('<input type="file" id = "reportdomid">');
		$dom.fileupload({
			autoUpload: false,
			dataType: 'json',
			done: function(e, data) {
				if (data.result != "true") {

				} else {
					callback();
				}
			}
		});
		$dom.fileupload('send', {
			url: (window.location.origin + WIS_CONFIG.PATH + "/sys/frReport/webapi/schemes/custom.do"),
			files: files,
			formData: data
		});
	};

	/**
	 * 封装报表删除功能
	 * @param id        保存报表的 id（必填）
	 * 		  callback  回调成功回调
	 */
	PUB_FUNC.deleteReport = function(id, callback) {
		if (!window.location.origin) {
			window.location.origin = window.location.protocol + "//" + window.location.hostname + (window.location.port ? ':' + window.location.port : '');
		}
		$.ajax({
			type: "DELETE",
			url: (window.location.origin + WIS_CONFIG.PATH + '/sys/frReport/webapi/schemes/' + id + '.do'),
			data: "",
			datatype: "json",
			async: false,
			success: function(data) {
				callback();
			},
			error: function(data) {
				$.bhDialog({
					title: '后台数据异常，请联系管理员',
					iconType: 'warning'
				});
				return false;
			}
		});
	};

	/**
	 * 封装报表发布功能
	 * @param id 保存报表的 id（必填）
	 * 		  started  （true发布：false 取消发布）
	 * 		  callback  回调
	 */
	PUB_FUNC.switchSchemeStatus = function(id, started, callback) {
		if (!window.location.origin) {
			window.location.origin = window.location.protocol + "//" + window.location.hostname + (window.location.port ? ':' + window.location.port : '');
		}
		$.ajax({
			url: (window.location.origin + WIS_CONFIG.PATH + "/sys/frReport/webapi/schemes/switchSchemeStatus.do"),
			data: {
				"started": started,
				"id": id
			},
			type: "POST",
			async: false,
			success: function(data) {
				if (!data) {
					//swal("", data, "error");
				} else {
					callback();
				}
			}
		});
	};
	/**
	 * 封装获取报表信息功能
	 * @param id 保存报表的 id（必填）
	 */
	PUB_FUNC.getReportById = function(id) {
		if (!window.location.origin) {
			window.location.origin = window.location.protocol + "//" + window.location.hostname + (window.location.port ? ':' + window.location.port : '');
		}
		var temp = "";
		$.ajax({
			url: (window.location.origin + WIS_CONFIG.PATH + '/sys/frReport/webapi/schemes/' + id + '.do'),
			data: "",
			type: "GET",
			dataType: "json",
			contentType: "application/json",
			async: false,
			success: function(data) {
				temp = data;
			}
		});
		return JSON.parse(temp);
	};


	/**
	 * 刷新字典
	 * 用于实时刷新字典内容
	 * @param appname 调用动作的app名称
	 * @param dicid	刷新的字典的id
	 */
	PUB_FUNC.refreshDic = function(appname, dicid) {
		$.ajax({
			url: WIS_EMAP_SERV.getContextPath() + "/sys/emapcomponent/clearDicCache.do?app=" + appname + "&dic=" + dicid + "",
			type: 'post',
			dataType: 'json',
			async: false,
			error: function(resp) {
				if (resp.status == 0 || resp.status == 401) {
					window.location.reload();
				} else if (resp.status == 403) {
					BH_UTILS.bhDialogWarning({
						title: '提示',
						content: '当前角色权限不足，请切换角色后重新操作',
						buttons: [{
							text: '确认',
							className: 'bh-btn-warning',
							callback: function() {}
						}]
					});
					return false;
				}


				// 长时间未操作提示错误
				if (resp.statusText.indexOf("NetworkError") > -1) {
					BH_UTILS.bhDialogDanger({
						title: '网络错误',
						content: '您可以尝试刷新页面解决该问题',
						buttons: [{
							text: '关闭',
							className: 'bh-btn-default'
						}]
					});
					return false;
				}
				$.bhDialog({
					title: '刷新异常，请联系管理员',
					iconType: 'warning'
				});
				//swal('刷新异常，请联系管理员！');
				return;
			},
			success: function(data) {
				if (data.code == 0) {} else {
					$.bhDialog({
						title: '刷新异常，请联系管理员',
						iconType: 'warning'
					});
					//swal('刷新异常，请联系管理员');
					return;
				}
			}
		});
	};



	/**
	 * 封装弹出框功能
	 * @param paramObj 拼装号的参数对象,其中可配的属性如下
	 ** iconType 图标类型 暂时只支持success/warning/danger
	 ** title 标题 对话框显示的标题
	 ** content 正文 对话框显示的正文
	 ** className 样式名 可以给对话框加上自定义的类
	 ** width 宽度 对话框默认的宽度 默认值464
	 ** height 高度 对话框默认的高度 默认值330
	 ** buttons 按钮集 对话框中展示的按钮集 默认值 [{text:'确定',className:'bh-btn-primary',callback:null}]
	 */
	PUB_FUNC.cPopup = function(paramObj) {
		var classJudge = false;
		var utils = require('utils');
		var bhVersion = utils.getConfig('BH_VERSION');
		if (bhVersion == '' || bhVersion == null || bhVersion == undefined || bhVersion == '1.1') {
			//need className
			classJudge = true;
		}

		console.log(utils);
		if (paramObj && paramObj.iconType && paramObj.iconType == "warning") {
			paramObj.buttons = [{
				text: '确定',
				className: (classJudge ? 'bh-btn-warning' : ''),
				callback: null
			}];
		}
		$.bhDialog(paramObj);
	};

	/**
	 * 封装的成功弹出框，可以定时消失
	 * @param title 标题
	 * @param content 内容
	 * @param time 消失的时间 毫秒为单位
	 */
	PUB_FUNC.tPopup = function(title, content, time, callback) {
		var classJudge = false;
		var utils = require('utils');
		var bhVersion = utils.getConfig('BH_VERSION');
		if (bhVersion == '' || bhVersion == null || bhVersion == undefined || bhVersion == '1.1') {
			//need className
			classJudge = true;
		}
		var paramObj = {};
		var uniqueClassMark = PUB_FUNC.randomString();
		paramObj["title"] = title;
		paramObj["content"] = content;
		paramObj["iconType"] = "success";
		if (!paramObj["content"] || paramObj["content"] === '') {
			paramObj["content"] = paramObj["title"];
			paramObj["title"] = "提示";
		}
		var buttons = [{
			text: '确定',
			className: (classJudge ? 'bh-btn-primary ' : '') + uniqueClassMark,
			callback: (callback ? callback : null)
		}];
		paramObj["buttons"] = buttons;
		time = (time && !isNaN(time)) ? parseInt(time) : 3000;
		var $dom = $.bhDialog(paramObj);
		setTimeout(function() {
			$dom.remove();
			if(callback){
				callback();
			}
		}, time);
	};


	/**
	 * 封装的警告弹出框，弹出框必须点击确定后消失
	 * @param title 标题
	 * @param content 内容
	 */
	PUB_FUNC.tPopup_Warning = function(title, content, param, callback) {
		var classJudge = false;
		var utils = require('utils');
		var bhVersion = utils.getConfig('BH_VERSION');
		if (bhVersion == '' || bhVersion == null || bhVersion == undefined || bhVersion == '1.1') {
			//need className
			classJudge = true;
		}
		var paramObj = {};
		var uniqueClassMark = PUB_FUNC.randomString();
		paramObj = param;
		paramObj["title"] = title;
		paramObj["content"] = content;
		paramObj["iconType"] = "";
		var buttons = [{
			text: '确定',
			className: (classJudge ? 'bh-btn-warning' : '') + uniqueClassMark,
			callback: (callback ? callback : null)
		}];
		paramObj["buttons"] = buttons;
		if (!paramObj["content"] || paramObj["content"] === '') {
			paramObj["content"] = paramObj["title"];
			paramObj["title"] = "提示";
		}
		BH_UTILS.bhDialogWarning(paramObj);
	};


	/**
	 * 封装的危险弹出框，弹出框必须点击确定后消失
	 * @param title 标题
	 * @param content 内容
	 */
	PUB_FUNC.tPopup_Danger = function(title, content, param, callback) {
		var classJudge = false;
		var utils = require('utils');
		var bhVersion = utils.getConfig('BH_VERSION');
		if (bhVersion == '' || bhVersion == null || bhVersion == undefined || bhVersion == '1.1') {
			//need className
			classJudge = true;
		}
		var paramObj = {};
		var uniqueClassMark = PUB_FUNC.randomString();
		paramObj = param;
		paramObj["title"] = title;
		paramObj["content"] = content;
		paramObj["iconType"] = "";
		var buttons = [{
			text: '确定',
			className: (classJudge ? 'bh-btn-primary' : '') + uniqueClassMark,
			callback: (callback ? callback : null)
		}];
		paramObj["buttons"] = buttons;
		if (!paramObj["content"] || paramObj["content"] === '') {
			paramObj["content"] = paramObj["title"];
			paramObj["title"] = "提示";
		}
		BH_UTILS.bhDialogDanger(paramObj);
	};


	/**
	 * 获取一个指定长度的随机字符串
	 * @param len 生成的字符串的长度 默认32位
	 */
	PUB_FUNC.randomString = function(len) {
		len = len || 32;
		var usingChars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
		var maxPos = usingChars.length;
		var resultStr = "";
		for (var i = 0; i < len; i++) {
			resultStr += usingChars.charAt(Math.floor(Math.random() * maxPos));
		}
		return resultStr;
	};



	/**
	 * 获取请求URL后请求参数
	 * @param name 参数名
	 */
	PUB_FUNC.GetQueryString = function(name) {
		var reg = new RegExp('.*[?&]' + name + '\=([^\&]*)\&?.*');
		var value = window.location.hash.replace(reg, "$1");
		if (window.location.hash == value) {
			return '';
		}
		return value;
	};

	/**
	 * ajax请求
	 *
	 * @param url
	 * @param params
	 * @returns {}
	 */
	PUB_FUNC.ajaxQuery = function(url, params, name) {
		var rData = "";
		$.ajax({
			url: url,
			type: 'post',
			dataType: 'json',
			data: params,
			async: false, // 默认为true 异步
			error: function(resp) {
				if (resp.status == 0 || resp.status == 401) {
					window.location.reload();
				} else if (resp.status == 403) {
					BH_UTILS.bhDialogWarning({
						title: '提示',
						content: '当前角色权限不足，请切换角色后重新操作',
						buttons: [{
							text: '确认',
							className: 'bh-btn-warning',
							callback: function() {}
						}]
					});
					return false;
				}
				// 长时间未操作提示错误
				if (resp.statusText.indexOf("NetworkError") > -1) {
					BH_UTILS.bhDialogDanger({
						title: '网络错误',
						content: '您可以尝试刷新页面解决该问题',
						buttons: [{
							text: '关闭',
							className: 'bh-btn-default'
						}]
					});
					return false;
				}
				$.bhDialog({
					title: '后台数据异常，请联系管理员',
					iconType: 'warning'
				});
				return;
			},
			success: function(data) {
				if (data.code == 0) {
					rData = data.datas[name].rows;
				} else {
					$.bhDialog({
						title: '数据获取异常，请联系管理员',
						iconType: 'warning'
					});
					return;
				}
			}
		});
		return rData;
	};
	/**
	 * ajax请求
	 *
	 * @param url
	 * @param params
	 * @returns callback
	 */
	PUB_FUNC.ajaxaction = function(url, params, callback, async) {
		$.ajax({
			url: url,
			type: 'post',
			dataType: 'json',
			data: params,
			async: async ? true : false,
			error: function(resp) {
				if (resp.status == 0 || resp.status == 401) {
					window.location.reload();
				} else if (resp.status == 403) {
					BH_UTILS.bhDialogWarning({
						title: '提示',
						content: '当前角色权限不足，请切换角色后重新操作',
						buttons: [{
							text: '确认',
							className: 'bh-btn-warning',
							callback: function() {}
						}]
					});
					return false;
				}
				// 长时间未操作提示错误
				if (resp.statusText.indexOf("NetworkError") > -1) {
					BH_UTILS.bhDialogDanger({
						title: '网络错误',
						content: '您可以尝试刷新页面解决该问题',
						buttons: [{
							text: '关闭',
							className: 'bh-btn-default'
						}]
					});
					return false;
				}
				$.bhDialog({
					title: '后台数据异常，请联系管理员',
					iconType: 'warning'
				});
				return;
			},
			success: function(data) {
				if (data.code == 0) {
					callback(data);
				} else {
					//参数是否正常，有误提示！！！！
					if (data.code == AJAX_PARAM_ERROR) {
						BH_UTILS.bhDialogWarning({
							title: '警告',
							content: '请求参数错误 请重试',
							buttons: [{
								text: '确认',
								className: 'bh-btn-warning',
								callback: function() {}
							}]
						});
					} else {
						$.bhDialog({
							title: '数据获取异常，请联系管理员',
							iconType: 'warning'
						});
						return;
					}
				}
			}
		});
	};

	/**
	 * 新版ajax请求，支持动作和自定义(同步)
	 *
	 * @param url
	 * @param params
	 * @returns callback
	 */
	PUB_FUNC.ajaxPost = function(url, params, successCallback, errorCallback) {
		$.ajax({
			url: url,
			type: 'post',
			dataType: 'json',
			data: params,
			async: false,
			error: function(resp) {
				if (resp.status == 0 || resp.status == 401) {
					window.location.reload();
				} else if (resp.status == 403) {
					BH_UTILS.bhDialogWarning({
						title: '提示',
						content: '当前角色权限不足，请切换角色后重新操作',
						buttons: [{
							text: '确认',
							className: 'bh-btn-warning',
							callback: function() {}
						}]
					});
					return false;
				}
				// 长时间未操作提示错误
				if (resp.statusText.indexOf("NetworkError") > -1) {
					BH_UTILS.bhDialogDanger({
						title: '网络错误',
						content: '您可以尝试刷新页面解决该问题',
						buttons: [{
							text: '关闭',
							className: 'bh-btn-default'
						}]
					});
					return false;
				}
				$.bhDialog({
					title: '后台数据异常，请联系管理员',
					iconType: 'warning'
				});
				errorCallback(default_db_error);
			},
			success: function(data) {
				if (null == data.code || undefined == data.code || data.code == 0) {
					successCallback(data);
				} else {
					$.bhDialog({
						title: '后台数据异常，请联系管理员',
						iconType: 'warning'
					});
					successCallback(default_db_error);
				}
			}
		});
	};

	/**
	 * 新版ajax请求，支持动作和自定义(异步)
	 *
	 * @param url
	 * @param params
	 * @returns callback
	 */
	PUB_FUNC.ajaxPostAsync = function(url, params, successCallback, errorCallback) {
		$.ajax({
			url: url,
			type: 'post',
			dataType: 'json',
			data: params,
			error: function(resp) {
				if (resp.status == 0 || resp.status == 401) {
					window.location.reload();
				} else if (resp.status == 403) {
					BH_UTILS.bhDialogWarning({
						title: '提示',
						content: '当前角色权限不足，请切换角色后重新操作',
						buttons: [{
							text: '确认',
							className: 'bh-btn-warning',
							callback: function() {}
						}]
					});
					return false;
				}
				// 长时间未操作提示错误
				if (resp.statusText.indexOf("NetworkError") > -1) {
					BH_UTILS.bhDialogDanger({
						title: '网络错误',
						content: '您可以尝试刷新页面解决该问题',
						buttons: [{
							text: '关闭',
							className: 'bh-btn-default'
						}]
					});
					return false;
				}
				$.bhDialog({
					title: '后台数据异常，请联系管理员',
					iconType: 'warning'
				});
				errorCallback(default_db_error);
			},
			success: function(data) {
				if (null == data.code || undefined == data.code || data.code == 0) {
					successCallback(data);
				} else {
					$.bhDialog({
						title: '后台数据异常，请联系管理员',
						iconType: 'warning'
					});
					successCallback(default_db_error);
				}
			}
		});
	};


	/**
	 * 确认弹出框
	 *
	 * @param url
	 * @param params
	 * @returns callback
	 */
	PUB_FUNC.confirmDialog = function(title, callback, text) {
		var classJudge = false;
		var utils = require('utils');
		var bhVersion = utils.getConfig('BH_VERSION');
		if (bhVersion == '' || bhVersion == null || bhVersion == undefined || bhVersion == '1.1') {
			//need className
			classJudge = true;
		}
		var buttons = [{
			text: text ? text : "确认",
			className: classJudge ? "bh-btn-warning" : '',
			callback: function() {
				callback();
			}
		}, {
			text: "取消",
			className: classJudge ? "bh-btn-default" : ''
		}];
		$.bhDialog({
			'title': title,
			"iconType": "warning",
			buttons: buttons
		});
	};

	/**
	 * 根据token获取文件路径
	 *
	 * @param token
	 * @returns fileurl
	 */
	PUB_FUNC.getImageSrc = function(token) {
		var fileUrl = '';
		$.ajax({
			type: "post",
			url: contextPath + "/sys/emapcomponent/file/getUploadedAttachment/" + token + ".do",
			dataType: "json",
			async: false, // 默认为true 异步
			success: function(res) {
				if (res.success) {
					$(res.items).each(function() {
						fileUrl = this.fileUrl;
					});
				}
			}
		});
		return fileUrl;
	};


})(window.PUB_FUNC = PUB_FUNC);