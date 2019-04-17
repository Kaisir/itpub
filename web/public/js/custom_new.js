/**
 * 新前端常用方法/组件封装
 */

$.extend({
	// -----常用提示组件-----

	/**
	 * success弹出窗
	 * 
	 * @param {[type]}
	 *            msg title 居中提示内容
	 * @param {Function}
	 *            fn 回调方法
	 * @param {[type]}
	 *            opts 配置的参数,如果使用此配置，则认为调用utils.dialog
	 */
	alert : function(msg, fn, opts) {
		$.msg(msg, fn, opts);
	},

	/**
	 * 弹出警告tip
	 */
	warnTip : function(msg) {
		$.bhTip({
					content : msg || '',
					state : 'warning'
				});
	},
	/**
	 * 消息弹出框，弹出后一段时间后自动关闭
	 * 
	 * @param {[type]}
	 *            msg title 提示内容
	 * @param {Function}
	 *            fn 回调方法
	 * @param {[type]}
	 *            opts 配置的参数,如果使用此配置，则认为调用utils.dialog
	 */
	msg : function(msg, fn, opts) {
		$.bhTip($.extend({
					content : msg || '',
					state : 'success'
				}, opts));
		if (fn) {
			fn.call(this);
		}
	},

	/**
	 * 警告提示框
	 * 
	 * @param {[type]}
	 *            msg title 提示内容
	 * @param {Function}
	 *            fn 回调方法
	 * @param {[type]}
	 *            opts 配置的参数,如果使用此配置，则认为调用utils.dialog
	 */
	warn : function(msg, fn, opts) {
		opts = $.extend({
					title : '提示',
					content : msg || '',
					type : 'warning',
					okCallback : function() {
						if (fn) {
							fn.call(this);
						}
					}
				}, opts);
		require('utils').dialog(opts);
	},

	/**
	 * 成功提示框
	 * 
	 * @param {[type]}
	 *            msg title 提示内容
	 * @param {Function}
	 *            fn 回调方法
	 * @param {[type]}
	 *            opts 配置的参数
	 */
	success : function(msg, fn, opts) {
		opts = $.extend({
					title : '提示',
					content : msg || '',
					type : 'success',
					okCallback : function() {
						if (fn) {
							fn.call(this);
						}
					}
				}, opts);
		require('utils').dialog(opts);
	},

	/**
	 * 错误弹出框
	 * 
	 * @param {[type]}
	 *            msg title 提示内容
	 * @param {Function}
	 *            fn 回调方法
	 * @param {[type]}
	 *            opts 配置的参数,如果使用此配置，则认为调用utils.dialog
	 */
	err : function(msg, fn, opts) {
		opts = $.extend({
					title : '提示',
					content : msg || '',
					type : 'error',
					okCallback : function() {
						if (fn) {
							fn.call(this);
						}
					}
				}, opts);
		require('utils').dialog(opts);
	},
	/**
	 * 信息确认弹出框
	 * 
	 * @param {[type]}
	 *            msg title 提示内容
	 * @param {[type]}
	 *            yes 确定按扭回调
	 * @param {[type]}
	 *            no 取消按钮回调
	 * @param {[type]}
	 *            opts 配置参数
	 */
	confirm : function(msg, yes, no, opts) {
		opts = $.extend({
					title : '提示',
					content : msg || '',
					type : 'confirm',
					okCallback : function() {
						if (yes) {
							yes.call(this);
						}
					},
					cancelCallback : function() {
						if (no) {
							no.call(this);
						}
					}
				}, opts);
		require('utils').dialog(opts);
	},
	/**
	 * 信息确认弹出框,默认显示是和否
	 * 
	 * @param {[type]}
	 *            msg title 提示内容
	 * @param {[type]}
	 *            yes 确定按扭回调
	 * @param {[type]}
	 *            no 取消按钮回调
	 * @param {[type]}
	 *            opts 配置参数
	 */
	yesNo : function(msg, yes, no, opts) {
		opts = $.extend({
					title : '提示',
					content : msg || '',
					type : 'confirm',
					okCallback : function() {
						if (yes) {
							yes.call(this);
						}
					},
					cancelCallback : function() {
						if (no) {
							no.call(this);
						}
					},
					okText : '是',
					noText : '否'
				}, opts);
		require('utils').dialog(opts);
	},

	closeWin : function() {
		window.opener = null;
		window.open('', '_self');
		window.close();
	},

	/**
	 * 前端部门封装的window组件
	 * 
	 * @param {[type]}
	 *            title 标题
	 * @param {[type]}
	 *            content 内容
	 * @param {Function}
	 *            callback 确定事件回调
	 * @param {[type]}
	 *            btns 按钮组
	 * @param {[type]}
	 *            options 配置项
	 */
	openWindow : function(content, title, callback, btns, options) {
		options = $.extend({
					width : 800
				}, options);
		BH_UTILS.bhWindow(content, title, btns, options, callback);
	},
	/**
	 * 纸质弹窗显示事件
	 * 
	 * @param {[type]}
	 *            title 弹窗标题
	 * @param {[type]}
	 *            content 弹窗内容
	 * @param {Function}
	 *            fn DOM元素渲染结束执行的回调，相对于ready，不会产生卡顿感
	 * @param {[type]}
	 *            opts close,closeBefore,open,openBefore,ready回调及其他配置
	 */
	showPaperDialog : function(title, content, fn, opts) {
		$.bhPaperPileDialog.show($.extend({
					'title' : title || '',
					'content' : content,
					'render' : function(a1, a2) {
						if (fn) {
							fn.call(a2);
						}
					}
				}, opts));
	},
	/**
	 * 隐藏纸张对话框，会依次触发closeBefore()和close() 设置100ms的延迟触发函数 避免因为过快被执行此函数导致列表缩小
	 */
	hidePaperDialog : function(cb) {
		var opt = typeof(cb) === 'function' ? {
			close : function() {
				setTimeout(function() {
							cb();
						}, 100);
			}
		} : {};
		$.bhPaperPileDialog.hide(opt);
	},
	/**
	 * 重新计算纸质弹窗和footer位置的方法 millisecond 毫秒数，若传了此参数，则每50毫秒刷新一次页脚和FOOTER，到时终止
	 */
	resetPaperDialog : function(millisecond) {
		$.bhPaperPileDialog.resetPageFooter();
		$.bhPaperPileDialog.resetDialogFooter();
		if (millisecond) {
			for (var i = 0; i < millisecond;) {
				setTimeout(function() {
							$.bhPaperPileDialog.resetPageFooter();
							$.bhPaperPileDialog.resetDialogFooter();
						}, i);
				i += 50;
			}
		}
	},
	/**
	 * 浮动弹窗层
	 */
	popDialog : function($target, html, opts) {
		var $pop = $("#popDialog");
		if ($pop.size() < 1) {
			$pop = $('<div></div>').attr("id", "popDialog")
					.prependTo($("body"));
		} else {
			$pop.jqxPopover("destroy");
		}
		$pop.html(html);
		$pop.jqxPopover($.extend({

					width : 500,
					selector : $target,
					position : 'left'
				}, opts)).jqxPopover("open");
	},
	/**
	 * 等待进度条
	 */
	showLoading : function() {
		var $loading = $("#loading");
		if ($loading.size() < 1) {
			$loading = $("<div id='loading'></div>").prependTo($("body"));
			$loading.jqxLoader({
						width : 200,
						height : 60,
						imagePosition : 'top'
					});
		}
		$loading.jqxLoader("open");
	},
	/**
	 * 隐藏进度条
	 */
	hideLoading : function() {
		$("#loading").jqxLoader("close");
	},
	// -----加密编码类-----
	toJsonStr : function(obj) {
		return JSON.stringify(obj);
	},
	toJson : function(str) {
		return JSON.parse(str);
	},
	base64_enc : function(str) {
		return $.base64.encode(str, true);
	},
	base64_dec : function(str) {
		return $.base64.decode(str, true);
	},
	// RSA公钥
	rsaKey : RSAUtils.getKeyPair('010001', '',
			'00a8526d6c9afe64fd481a49a05fadaca3'),
	// RSA加密
	rsa_enc : function(str) {
		return RSAUtils.encryptedString($.rsaKey, str);
	},
	// HTML转义
	escapeHtml : function(txt) {
		if (!txt) {
			return txt;
		}
		return $("<div/>").text(txt).html();
	},
	// Html解码获取Html实体
	decodeHtml : function(html) {
		return $("<div/>").html(html).text();
	},
	// -----常用工具类-----
	// 国际化资源
	i18n : function() {
		var code = arguments[0];
		if (!code) {
			return "";
		}
		if (window._i18n) {
			code = window._i18n[code];
		}
		var args = Array.prototype.slice.call(arguments, 1);
		if (args && args.length > 0) {
			code = code.replace(/\{\s*(\d+)\s*\}/g, function() {
						var index = arguments[1]; // 参数下标
						if (args.length > index) {
							return args[index];
						}
						return arguments[0];
					});
		}
		return code;
	},

	syncPost : function(url, param, type) {
		var result = null;
		$.ajax({
					url : url,
					type : 'POST',
					async : false,
					data : param,
					success : function(resp) {
						result = resp;
					},
					dataType : type
				});
		return result;
	},

	/**
	 * @param $tableDiv
	 *            表格的div
	 * @param opts
	 *            表格配置 默认为可排序 分页 不可拖动列宽 设置opts属性checkbox为表格添加check列
	 * @param noSearch
	 *            不传或者传入true则无搜索 默认搜索div的id为'#advancedQueryPlaceholder'
	 */
	initEmapTable : function($tableDiv, opts, noSearch) {
		opts = opts || {};
		var tableOptions = {
			sortable : true,
			pagerMode : 'advanced',
			columnsResize : false,
			customColumns : []
		};
		if (opts.checkbox) {
			tableOptions.customColumns.push({
						colIndex : 0,
						align : 'center',
						cellsalign : 'center',
						type : 'checkbox'
					});
			delete opts.checkbox;
		}
		if (opts.customColumns) {
			$.each(opts.customColumns, function(idx, data) {
						if (data.colIndex == 'last') {
							tableOptions.customColumns.push(data);
							return true;
						}
						data.colIndex = tableOptions.customColumns.length;
						tableOptions.customColumns.push(data);
					});
			delete opts.customColumns;
		}
		// var tableHeight = BH_UTILS.getTableHeight(10); // 固定10行的高度
		// tableOptions.height = tableHeight;
		// 初始化表格相关的搜索
		if (!noSearch) {
			var $searchDiv = opts.searchDiv
					? $(opts.searchDiv)
					: $("#advancedQueryPlaceholder");
			var searchData = WIS_EMAP_SERV.getModel(opts.pagePath, opts.action,
					"search");
			// 如果需要对搜索的元数据进行处理 此处提供回调
			if (opts.searchMetaHandle) {
				opts.searchMetaHandle(searchData);
			}
			var allowAllOption = opts.allowAllOption === undefined
					? true
					: opts.allowAllOption;
			var showBlankOption = opts.showBlankOption === undefined
					? false
					: opts.allowAllOption;
			$searchDiv.emapAdvancedQuery({
						data : searchData || [],
						showBlankOption : showBlankOption,
						allowAllOption : allowAllOption
					});
			var queryDataHandle = opts.queryDataHandle;
			// 切换页面时候需要解绑定之前的事件否则会多次监听
			$searchDiv.off("search").on("search", function(e, data, opts) {
						if (!data) {
							data = $(this).emapAdvancedQuery(true).getValue();
						}
						// 转换为json
						data = JSON.parse(data);
						// 合并处理的请求入参
						var param = {};
						if (queryDataHandle) {
							param = queryDataHandle(data);
						}
						$.extend(param, {
									querySetting : JSON.stringify(data)
								});
						$tableDiv.emapdatatable('reload', param, true);
					});
			delete opts.queryDataHandle;
			delete opts.allowAllOption;
			delete opts.searchDiv;
			delete opts.searchMetaHandle;
			delete opts.showBlankOption;
		}
		// 合并
		$.extend(tableOptions, opts);
		$tableDiv.emapdatatable(tableOptions);
	},

	/**
	 * 初始化IT系统的表单
	 * 
	 * @param $formDiv
	 *            表单的div
	 * @param opts
	 *            pagePath和action 以及表单是否可读 自定义校验存放
	 * @param modelCallback
	 *            对model进行个性化处理的方法
	 */
	initItEmapForm : function($formDiv, opts, modelCallback) {
		var datamodel = WIS_EMAP_SERV.getModel(opts.pagePath, opts.action,
				"form");
		// 对模型处理
		if (modelCallback && typeof modelCallback === "function") {
			modelCallback(datamodel);
		}
		var needCustomVali = opts.customRules && opts.customRules.length > 0;
		// 不开启校验 需要自定义校验
		$formDiv.emapForm({
					data : datamodel,
					size : "L",
					model : opts.model || "h",
					root : window.contextPath,
					readonly : opts.readonly || false,
					validate : !needCustomVali,
					textareaEasyCheck : opts.textareaEasyCheck || false,
					inputWidth : opts.inputWidth || '6'
				});
		// 自定义校验
		if (needCustomVali) {
			$formDiv.emapValidate({
						callback : function(rules) {
							$.each(opts.customRules, function(idx, rule) {
										rules.push(rule);
									});
						}
					});
		}
	},

	/**
	 * 删除emap的表格选择
	 * 
	 * @param $tableDiv
	 *            table的选择器或者jquery对象
	 * @param opts
	 *            type: string 当前表格存放数据的名称 url: string 删除的请求url rowsHandleCb:
	 *            function 处理选择列的回调 此处会返回需要传到后台的数据
	 */
	delEmapTableRecord : function($tableDiv, opts) {
		$tableDiv = typeof $tableDiv === 'string' ? $($tableDiv) : $tableDiv;
		var selectedRows = $tableDiv.emapdatatable("checkedRecords");
		if (!selectedRows || selectedRows.length === 0) {
			$.warn('请选择想要删除的' + opts.type + '。');
			return false;
		}
		// 弹窗确认是否删除
		$.confirm('确定删除当前选择的' + opts.type + '吗？', function() {
					var params = opts.rowsHandleCb(selectedRows);
					$.post(opts.url, params).done(function(resp) {
								if (resp) {
									if (resp.success) {
										$.alert('成功删除选择数据。');
										$tableDiv.emapdatatable('reload', {},
												true);
									} else {
										$.warn(resp.msg);
									}
								} else {
									$.warn('未知原因导致删除失败，请重试或联系系统管理员。');
								}
							});
				});
	},

	/**
	 * 初始化EMAP表单
	 * 
	 * @param form
	 *            表单容器.可以是任意的DOM元素
	 * @param modelOrUrl
	 *            模型对象或者模型请求URL
	 * @param formAction
	 *            EMAP模型动作ID,不传此参数,表示使用自定义模型.
	 * @param opts
	 *            可配置编辑属性readonly/显示样式mode等EMAP表单自有属性
	 *            opts.showFields为自定义显示列，数组格式,如["XSMC","LBDM"] opts.hideFields
	 *            自定义隐藏列.数组格式
	 */
	initEmapForm : function(form, modelOrUrl, formAction, opts) {
		opts = opts || {};
		var $form = $(form);
		var model = null;
		$form.emapForm('destroy');
		if ($.type(modelOrUrl) == "array") {
			model = modelOrUrl;
		} else {
			if (!formAction) { // 取自定义模型
				model = this.syncPost(modelOrUrl);
			} else { // 取EMAP标准模型
				model = WIS_EMAP_SERV.getModel(modelOrUrl, formAction, "form");
			}
		}
		if (opts.showFields && opts.showFields.length > 0) {
			for (var i = model.length - 1; i >= 0; i--) {
				var m = model[i];
				m.hidden = true;
				var len = opts.showFields.length;
				for (var j = 0; j < len; j++) {
					if (opts.showFields[j] == m.name) {
						m.hidden = false;
						break;
					}
				}
			}
			delete opts.showFields;
		}
		if (opts.hideFields && opts.hideFields.length > 0) {
			for (var i = model.length - 1; i >= 0; i--) {
				var m = model[i];
				var len = opts.hideFields.length;
				for (var j = 0; j < len; j++) {
					if (opts.hideFields[j] == m.name) {
						m.hidden = true;
						break;
					}
				}
			}
			delete opts.hideFields;
		}
		var options = $.extend({
					root : contextPath,
					data : model,
					readonly : false,
					mode : "L",
					defaultOptions : {
						tree : {
							unblind : "/"
						}
					}
				}, opts);
		$form.emapForm(options);
	},

	/**
	 * 选人组件,调用组件
	 * 
	 * @param {Function}
	 *            callback 点击确认后回调方法,传递选中结果参数
	 * @param {[type]}
	 *            param 调用组件参数,比如fields,filter,orderBy,privId等信息
	 * @param {[type]}
	 *            opts 组件配置参数
	 */
	// 上面的注释实现的是人事的，公共并未实现
	// searchType:teacher,student,all
	choosePerson : function(callback, params, opts) {
		var url = contextPath + "/sys/itpub/widget/choose_person.do";

		params = $.toJsonStr(params);
		// params = $.rsa_enc(params);
		url += '?params=' + params;

		var options = $.extend({
					leftSourceUrl : url,
					leftSourceAction : 'data',
					placeholder : '搜索职工号/姓名',
					id : 'id',
					title : '添加成员',
					rightcellsRenderer : function(row, column, value, rowData) {
						var deptName = !rowData.deptName
								? rowData.deptCode
								: rowData.deptName;
						deptName = !deptName ? '' : deptName;
						var html = '<p class="gm-member-row bh-clearfix" >'
								+ '<span class="gm-member-user bh-col-md-6"  row="'
								+ row + '">' + rowData.name + '<span>('
								+ rowData.id + ')</span>' + '</span>'
								+ '<span class="bh-col-md-3"  title='
								+ deptName + '>' + deptName + '</span>'
								+ '</p>';
						return html;
					},
					leftcellsRenderer : function(row, column, value, rowData) {
						var deptName = !rowData.deptName
								? rowData.deptCode
								: rowData.deptName;
						deptName = !deptName ? '' : deptName;
						var html = '<p class="choose-person-row bh-clearfix" >'
								+ '<span class="gm-member-user bh-col-md-6"  row="'
								+ row + '">' + rowData.name + '<span>('
								+ rowData.id + ')</span>' + '</span>'
								+ '<span class="bh-col-md-6"  title='
								+ deptName + '>' + deptName + '</span>'
								+ '</p>';
						return html;
					},
					callback : function(result) {
						if (callback) {
							return callback(result);
						}
					}
				}, opts);
		var chooseWidget = $.bh_choose(options);
		chooseWidget.show();
		return chooseWidget;
	},

	/**
	 * 选人组件,调用组件
	 * 
	 * @param {Function}
	 *            callback 点击确认后回调方法,传递选中结果参数
	 * @param {[type]}
	 *            param 调用组件参数,比如fields,filter,orderBy,privId等信息
	 * @param {[type]}
	 *            opts 组件配置参数
	 */
	getDep : function(callback, params, opts) {

		var url = contextPath + "/sys/itpub/common/getDeptSelect.do";

		params = $.toJsonStr(params);
		params = $.rsa_enc(params);

		url += "?params=" + params;
		var options = $.extend({
					leftSourceUrl : url,
					leftSourceAction : 'data',
					placeholder : '搜索职工号/姓名',
					id : 'id',
					title : '添加成员',
					rightcellsRenderer : function(row, column, value, rowData) {
						var deptName = !rowData.deptName
								? rowData.deptCode
								: rowData.deptName;
						deptName = !deptName ? '' : deptName;
						var html = '<p class="gm-member-row bh-clearfix" >'
								+ '<span class="gm-member-user bh-col-md-6"  row="'
								+ row + '">' + rowData.name + '<span>('
								+ rowData.id + ')</span>' + '</span>'
								+ '<span class="bh-col-md-3"  title='
								+ deptName + '>' + deptName + '</span>'
								+ '</p>';
						return html;
					},
					leftcellsRenderer : function(row, column, value, rowData) {
						var deptName = !rowData.deptName
								? rowData.deptCode
								: rowData.deptName;
						deptName = !deptName ? '' : deptName;
						var html = '<p class="choose-person-row bh-clearfix" >'
								+ '<span class="gm-member-user bh-col-md-6"  row="'
								+ row + '">' + rowData.name + '<span>('
								+ rowData.id + ')</span>' + '</span>'
								+ '<span class="bh-col-md-6"  title='
								+ deptName + '>' + deptName + '</span>'
								+ '</p>';
						return html;
					},
					callback : function(result) {
						if (callback) {
							return callback(result);
						}
					}
				}, opts);
		var chooseWidget = $.bh_choose(options);
		chooseWidget.show();
		return chooseWidget;
	},

	/**
	 * 销毁表单元素
	 */
	destroyForm : function($form, opts) {
		$form.emapForm("destroy");
	},

	scrollTo : function($target, offset, $obj) {
		offset = offset || 0;
		$obj = $obj || $("html,body");
		$target = $target || $("html,body");
		$obj.animate({
					scrollTop : $target.offset().top + offset
				}, "fast", "linear", function() {
					$target.addClass("animated bounce");
					setTimeout(function() {
								$target.removeClass("animated bounce");
							}, 1000);
				});
	},

	/**
	 * 切换应用角色信息
	 */
	changeAppRole : function(appName, roleId, cb) {
		$.post(	contextPath + "/sys/itpub/common/changeAppRole/" + appName
						+ "/" + roleId + ".do", function(resp) {
					if (cb) {
						cb(resp.success);
					} else {
						location.href = location.href.replace(/&?gid_=[^&]*/,
								"").replace(/#.*/, "");
					}
				});
	},

	/**
	 * 选中下拉框的第一个选项
	 * 
	 * @param $dropSelect
	 *            下拉div的选择器
	 */
	selectDropListFirst : function($dropSelect) {
		$.selectDropList($dropSelect);
	},

	/**
	 * 选中指定的下拉选项 默认选择第一项 如果val传入为空或不存在 则默认选中下拉的第一项
	 * 
	 * @param $dropSelect
	 *            下拉的选择器
	 * @param val
	 *            需要选中项的value
	 */
	selectDropList : function($dropSelect, val) {
		var $dropDiv = $($dropSelect);
		WIS_EMAP_SERV._getInputOptions($dropDiv.data("url"), function(res) {
					if (!val) {
						$dropDiv.jqxDropDownList('addItem', res[0]);
						$dropDiv.val(res[0].id);
					} else {
						$.each(res, function(idx, data) {
									if (data.id === val) {
										$dropDiv.jqxDropDownList('addItem',
												data);
										$dropDiv.val(data.id);
										return false;
									}
								});
					}
					$dropDiv.trigger("itCustom.dropSelected");
				});
	},

	/**
	 * 根据key获取url中的值 http://localhost:8080/emap/sys/xyhy/index.do?key=1 key->1
	 * 
	 * @param name
	 */
	getParam : function getQueryString(name) {
		var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
		var r = window.location.search.substr(1).match(reg);
		if (r != null) {
			return unescape(r[2]);
		}
		return null;
	},

	/**
	 * 获取uuid的方法
	 */
	UUID : function() {
		var s = [];
		var hexDigits = "0123456789abcdef";
		for (var i = 0; i < 36; i++) {
			s[i] = hexDigits.substr(Math.floor(Math.random() * 0x10), 1);
		}
		s[14] = "4";
		s[19] = hexDigits.substr((s[19] & 0x3) | 0x8, 1);
		s[8] = s[13] = s[18] = s[23] = "-";
		var uuid = s.join("");
		return uuid;
	},

	/**
	 * 检查是否是手机格式
	 * 
	 * @param number
	 */
	isMobile : function(number) {
		var testMobile = /(^0{0,1}1[3|4|5|6|7|8|9][0-9]{9}$)/;
		return testMobile.test(number);
	},

	/**
	 * 检查是否是固话格式
	 * 
	 * @param number
	 */
	isPhone : function(number) {
		var testPhone = /^0\d{2,3}-?\d{7,8}$/;
		return testPhone.test(number);
	},
	/**
	 * 判断字符串是否为数字
	 * 
	 * @param number
	 */
	isNumber : function(number) {
		var re = /^[0-9]+.?[0-9]*$/; // 判断正整数 /^[1-9]+[0-9]*]*$/
		return re.test(number);
	},
	/**
	 * 获取当前日期，格式为YYYY-MM-DD
	 * 
	 */
	newDate : function() {
		var date = new Date();
		var seperator1 = "-";
		var year = date.getFullYear();
		var month = date.getMonth() + 1;
		var strDate = date.getDate();
		if (month >= 1 && month <= 9) {
			month = "0" + month;
		}
		if (strDate >= 0 && strDate <= 9) {
			strDate = "0" + strDate;
		}
		var currentdate = year + seperator1 + month + seperator1 + strDate;
		return currentdate;
	},
	/**
	 * 将日期转为格式为YYYY-MM-DD
	 * 
	 */
	parseDate2Str : function(date) {
		var seperator1 = "-";
		var year = date.getFullYear();
		var month = date.getMonth() + 1;
		var strDate = date.getDate();
		if (month >= 1 && month <= 9) {
			month = "0" + month;
		}
		if (strDate >= 0 && strDate <= 9) {
			strDate = "0" + strDate;
		}
		var currentdate = year + seperator1 + month + seperator1 + strDate;
		return currentdate;
	},
	/**
	 * 比较两个YYYY-MM-DD格式的日期
	 * 
	 * @return 日期相差的天数
	 */
	minusDate : function(dateStr1, dateStr2) {
		var date1 = new Date(dateStr1.replace('/-/g', '/'));
		var date2 = new Date(dateStr2.replace('/-/g', '/'));
		return (date1 - date2) / 24 / 60 / 60 / 1000;
	},
	/**
	 * 比较两个HH:mm格式的时间，假设是同一天
	 * 
	 * @return 时间相差的分钟数
	 */
	minusTime : function(timeStr1, timeStr2) {
		var time1 = new Date("January 1,2000 " + timeStr1);
		var time2 = new Date("January 1,2000 " + timeStr2);
		return (time1 - time2) / 1000 / 60;
	},
	/**
	 * 生成emapflow审批意见表格，简略版
	 * 
	 * @param {}
	 *            taskId
	 */
	getFlowCommentTable : function(taskId) {
		// 流程流转记录接口，不显示第一个节点
		var url = WIS_EMAP_SERV.getContextPath()
				+ "/sys/emapflow/tasks/queryFlowState.do?responseType=JSON&taskId="
				+ taskId;
		var html = "";
		$.post(url).done(function(data) {
					if (data.length > 0) {
						// console.log(data)
						for (var i = 1; i < data.length; i++) {
							if (data[i].flowComment === undefined)
								data[i].flowComment = '';
							html += "<tr>";
							html += "<td>" + data[i].endTime + "</td>"// 审批时间
							html += "<td>" + data[i].assignee + "</td>"// 审批人
							html += "<td>" + data[i].name + "</td>"// 环节
							html += "<td>" + data[i].type + "</td>"// 审批状态
							html += "<td>" + data[i].flowComment + "</td>"// 意见
							html += "</tr>";
						}
						$("#flowComment").html(html)
					}
				});
	},
	/**
	 * 生成流转图和审批意见表
	 * 
	 * @param {}
	 *            taskid
	 */
	getFlowView : function(taskid, defKey) {
		if (taskid != null && taskid != "") {
			// 请求流转数据
			$.post(
					'' + WIS_EMAP_SERV.getContextPath()
							+ '/sys/emapflow/tasks/queryFlowState.do?taskId='
							+ taskid + '&responseType=JSON', '', 'get').done(
					function(data) {
						// 绘制流转图
						var $flowprocess = $("#flowprocessview");
						var flowStateData = [];
						for (var i = 0; i < data.length; i++) {
							var flowState = {
								content : data[i].assignee,
								status : data[i].typeCode == null
										? 'not started'
										: 'success',
								statusDescription : data[i].name
							}
							flowStateData.push(flowState);
						}
						$flowprocess.flowState({
									width : '100%',
									flowStateData : flowStateData
								});
						// 绘制流转表格
						data[0].flowComment = "";
						var source = {
							localData : data,
							dataType : "json"
						};
						var dataAdapter = new $.jqx.dataAdapter(source);
						var flowprocesstable = $("#flowprocesstable");
						// table列宽设定，第一列固定34px，其他列按咱比分配
						var specifyColumnWidth = [1, 1, 1, 1, 1, 0, 1];
						// 将占比换算成百分比
						var tableColWidthList = BH_UTILS.getTableColWidth(
								flowprocesstable, specifyColumnWidth);
						// 根据table行数获取table高度
						var tableHeight = BH_UTILS.getTableHeight(data.length);
						// table初始化
						flowprocesstable.jqxDataTable({
									// filterable: true,
									// filterHeight:35,
									pageable : true,
									pagerMode : 'advanced',
									width : '100%',
									source : dataAdapter,
									height : tableHeight,
									pageSizeOptions : ['10', '20', '50', '100'],
									columns : [{
												text : '环节',
												dataField : 'name',
												width : tableColWidthList[0]
											}, {
												text : '处理人',
												dataField : 'assignee',
												width : tableColWidthList[1]
											}, {
												text : '处理结果',
												dataField : 'type',
												width : tableColWidthList[2]
											}, {
												text : '开始时间',
												dataField : 'startTime',
												width : tableColWidthList[3]
											}, {
												text : '结束时间',
												dataField : 'endTime',
												width : tableColWidthList[4]
											}, {
												text : '耗时',
												dataField : 'duration',
												width : tableColWidthList[5],
												hidden : true
											}, {
												text : '意见',
												dataField : 'flowComment',
												width : tableColWidthList[6]
											}],
									localization : {
										pagergotopagestring : "跳往:",
										pagershowrowsstring : "每页显示:",
										pagerrangestring : " 共 "
									},
									ready : function() {
										var totalRows = $("#flowprocesstable")
												.jqxDataTable('getRows');
										var num = totalRows.length;
										$("#flowprocesstable").jqxDataTable(
												'selectRow', num - 1);
									}
								});
						$("#flowprocesstable").jqxDataTable('refresh');
					});
			$("#flowimg")
					.html("<iframe src='"
							+ WIS_EMAP_SERV.getContextPath()
							+ "/sys/emapflow/modules/tasks/flowDiagram.html?taskId="
							+ taskid
							+ "'  width='100%' height='200px' frameborder='0' name='_blank' id='_blank'  />");
		} else {
			$("#flowprocessview").prev().remove();
			$("#flowprocessview").remove();
			$("#flowprocesstable").remove();
			$("#flowimg")
					.html("<iframe src='"
							+ WIS_EMAP_SERV.getContextPath()
							+ "/sys/emapflow/modules/tasks/flowDiagram.html?defKey="
							+ defKey
							+ "'  width='100%' height='200px' frameborder='0' name='_blank' id='_blank'  />");
		}
		$('#_blank').load(function() {
			var varsion = document.getElementById('_blank').contentWindow.document
					.getElementsByClassName('version');
			var flowing = document.getElementById('_blank').contentWindow.document
					.getElementsByClassName('canvas');
			varsion[0].style.display = 'none';
			flowing[0].style.margin = 'auto';
			flowing[0].style.marginLeft = '25%';
			flowing[0].style.marginTop = '-10px';
			flowing[0].style.border = '0px solid #ddd';
			flowing[0].style.position = 'fixed';
		});
	}

});
// 增强jquery函数
$.fn.extend({
			/**
			 * @param params
			 *            表格reload请求的参数
			 * @param go2First
			 *            false则在当前的页码刷新 true刷新到第一页
			 */
			reloadDataTable : function(params, go2First) {
				// 如果未传入参数 则默认刷新到第一页
				if (params == undefined && go2First == undefined) {
					params = true;
				}
				if (!go2First && typeof params === 'boolean') {
					go2First = params;
					$(this).emapdatatable("reload", {}, go2First);
				} else if (typeof params === 'object' && go2First) {
					$(this).emapdatatable("reload", params, go2First);
				} else if (go2First) {
					$(this).emapdatatable("reload", {}, go2First);
				} else {
					$(this).emapdatatable("reload", params);
				}
			},

			/**
			 * @param totalSize
			 *            分页的总页数
			 * @param callback
			 *            回调函数
			 * @param param
			 *            封装的额外条件信息
			 */
			customPagination : function(totalSize, callback, param) {
				var pageSizeOptions;
				if (param && param.pagesize) {
					pagesize = param.pagesize;
					pageSizeOptions = [pagesize, pagesize * 2, pagesize * 3,
							pagesize * 4];
				} else {
					pageSizeOptions = [5, 10, 15, 20];
					pagesize = 10;
				}
				$(this).pagination({
							mode : 'advanced',
							pagenum : 0,
							pagesize : pagesize,
							totalSize : totalSize,
							pageSizeOptions : pageSizeOptions,
							pagerButtonsCount : pagesize || 5
						});

				$(this).off('pagersearch').on('pagersearch',
						function(e, pagenum, pagesize, total) {
							var params = $.extend({}, param);
							params.pageSize = pagesize;
							params.pageNumber = pagenum + 1;
							callback(params);
						});
			}
		});

/**
 * 页面初始化操作
 */
$(function() {
	var currentRequest = {};

	function genToken(setting) {
		var key = setting.url + "|";
		if (setting.data) {
			if ($.type(setting.data) == 'string') {
				key += setting.data;
			} else {
				key += $.toJsonStr(setting.data);
			}
		}
		return $.md5(key);
	}

	$.ajaxSetup({
				beforeSend : function(xhr) {
					var url = this.url;
					if (url.indexOf("/code/") >= 0
							|| url.indexOf("/emapcomponent/file/") >= 0) {
						return true;
					}
					var token = genToken(this);
					if (!token) {
						$.msg("无法生成Token，请检查请求参数。。。");
						return false;
					}
					if (currentRequest[token] === 1) {
						$.msg("正在处理,请稍后...");
						return false;
					}
					currentRequest[token] = 1;
					return true;
				},
				complete : function(xhr) {
					var token = genToken(this);
					delete currentRequest[token];
				}
			});

	// 为array增加增强方法
	if (!Array.prototype.includes) {
		Array.prototype.includes = function(searchElement /* , fromIndex */) {
			'use strict';
			var O = Object(this);
			var len = parseInt(O.length) || 0;
			if (len === 0) {
				return false;
			}
			var n = parseInt(arguments[1]) || 0;
			var k;
			if (n >= 0) {
				k = n;
			} else {
				k = len + n;
				if (k < 0) {
					k = 0;
				}
			}
			var currentElement;
			while (k < len) {
				currentElement = O[k];
				if (searchElement === currentElement
						|| (searchElement !== searchElement && currentElement !== currentElement)) { // NaN
					// !==
					// NaN
					return true;
				}
				k++;
			}
			return false;
		};
	}
});