/**
 * 调用后台公共方法-说明
 * 作者：01115238 王波
 *
 * 参数说明
 * action-动作名 ：自定义动作时为动作名，数据模型时为数据模型的名称，必须引用到epg页面模型
 * params-参数列表：必须是JSON对象（不是JSON字符串）例子：{key1:value1, key2:value2 ...}
 *        如果不需要参数就直接传{},调用批量操作方法时，请传JSON数组
 * aSync-同步异步开关： true 异步， false 同步
 *
 * 调用分两种
 * 1.调用数据模型原生动作
 *   dataModelAdd 对应数据模型-ADD
 *   dataModelDelete 对应数据模型-DELETE
 *   dataModelModify 对应数据模型-MODIFY
 *   dataModelSave 对应数据模型-SAVE
 *   dataModelQuery 对应数据模型-QUERY
 *   dataModelBatchAdd 对应数据模型-ADD 批量
 *   dataModelBatchDelete 对应数据模型-DELETE 批量
 *   dataModelBatchSave 对应数据模型-SAVE 批量
 *   dataModelBatchModify 对应数据模型-MODIFY 批量
 *
 * 2.调用自定义动作
 *   mineExcute 支持新增、修改、删除类型的自定义动作
 *   mineBatchExcute 支持新增、修改、删除类型的自定义动作 批量
 *   mineQuery  自定义查询
 *
 * 返回值(非查询)
 * {
 *  "resultCode" : "00000",
 *  "msg" : "success"
 *  }
 *
 * 返回值(查询)
 * {
 *  "resultCode" : "00000",
 *  "msg" : "success"
 *  "data" :[{"CZZ":"admin"}]
 *  }
 *
 *  其中resultCode是返回码
 *  msg是返回信息 对于结果的描述
 *  data 当动作为查询时，返回的数据JSON
 *
 * 返回码定义
 *     成功     00000
 *   参数错误     10001
 *  数据库异常     19999
 *  未知异常      99999
 *
 *  【使用实例】
 *  1.使用数据模型ADD
 *  var resultAdd = dataModelAdd('T_XG_JBXX',{XSBH : '123'}, false);
 *  if ('00000' == resultAdd.resultCode)
 *  {
 *  	alert('新增成功');
 *  }
 *
 *  2.查询自定义动作
 *  var queryResult = mineQuery('cxxsjbxx',{XSBH : '123'}, false);
 *  if ('00000' == queryResult.resultCode)
 *  {
 *  	alert('查询结果为：' + queryResult.data);
 *  }
 *
 *  3.自定义动作更新
 *  var updateResult = mineExcute('xgxsjbxx',{XSBH : '123',XSMC : '王大锤'}, false);
 *  if ('00000' == updateResult.resultCode)
 *  {
 *  	alert('修改成功');
 *  }
 */

//参数错误
var AJAX_SUCCESS = "00000";

//参数错误
var AJAX_PARAM_ERROR = "10001";

//数据库异常
var AJAX_DATABASE_ERROR = "19999";

// 未知异常
var AJAX_UNKOWN_ERROR = "99999";

// 数据库异常
var resultDataBaseError = {"resultCode" : AJAX_DATABASE_ERROR , "msg" : "后台数据异常 请联系管理员"};

function dataModelAdd(action, params, aSync)
{
	// 返回值
	var result = {"resultCode" : "99999" , "msg" : "unkown error"};

	// 数据模型的原生动作
	var dataModelAction = "ADD";

	// 请求类型为数据模型
	var actionType = "DATAMODEL";

	var callJsRequest = {};
	callJsRequest["requestParams"] = JSON.stringify(params);
	callJsRequest["actionType"] = actionType;
	callJsRequest["actionName"] = action;
	callJsRequest["dataModelAction"] = dataModelAction;

    $.ajax(
    {
        url: WIS_CONFIG.ROOT_PATH + "/commoncall/call/"+action+"-"+actionType+"-"+dataModelAction+".do",
        type: 'post',
        data: callJsRequest,
        async: aSync,
        error: function(resp)
        {
            //2016-04-19 qiyu 未登录则刷新页
            if(resp.status == 401)
            {
                window.location.reload();
            }
            else if (resp.status == 403)
            {
				BH_UTILS.bhDialogWarning({
					title: '提示',
					content:'当前角色权限不足，请切换角色后重新操作',
					buttons: [{
						text: '确认',
						className: 'bh-btn-warning',
						callback: function() {
						}
					}]
				});
            }
            
            // 长时间未操作提示错误
            if(resp.statusText.indexOf("NetworkError") > -1)
            {
                BH_UTILS.bhDialogDanger({
                    title: '网络错误',
                    content:'您可以尝试刷新页面解决该问题',
                    buttons:[{
                        text: '关闭',
                        className: 'bh-btn-default'
                    }]
                });
            }
        },
        success: function(data)
        {
			if(null == data.code || undefined == data.code || data.code == 0)
			{
				result = data;
			}else if(data.code == '500'){
				$.bhDialog({title:'权限不足，无法访问！',iconType:'warning'});
				return false;
			}
			else
			{
				$.bhDialog({title:'后台数据异常，请联系管理员',iconType:'warning'});
				result = resultDataBaseError;
			}
        }
    });

    return result;
}

function dataModelBatchAdd(action, params, aSync)
{
	// 返回值
	var result = {"resultCode" : "99999" , "msg" : "unkown error"};

	// 数据模型的原生动作
	var dataModelAction = "ADD";

	// 请求类型为数据模型
	var actionType = "DATAMODEL";

	var callJsRequest = {};
	callJsRequest["requestParams"] = JSON.stringify(params);
	callJsRequest["actionType"] = actionType;
	callJsRequest["actionName"] = action;
	callJsRequest["dataModelAction"] = dataModelAction;

    $.ajax(
    {
        url: WIS_CONFIG.ROOT_PATH + "/commoncall/callBatch/"+action+"-"+actionType+'-'+dataModelAction+".do",
        type: 'post',
        data: callJsRequest,
        async: aSync,
        error: function(resp)
        {
            //2016-04-19 qiyu 未登录则刷新页
            if(resp.status == 401)
            {
                window.location.reload();
            }
            else if (resp.status == 403)
            {
				BH_UTILS.bhDialogWarning({
					title: '提示',
					content:'当前角色权限不足，请切换角色后重新操作',
					buttons: [{
						text: '确认',
						className: 'bh-btn-warning',
						callback: function() {
			
						}
					}]
				});
            }
            // 长时间未操作提示错误
            if(resp.statusText.indexOf("NetworkError") > -1)
            {
                BH_UTILS.bhDialogDanger({
                    title: '网络错误',
                    content:'您可以尝试刷新页面解决该问题',
                    buttons:[{
                        text: '关闭',
                        className: 'bh-btn-default'
                    }]
                });
            }
        },
        success: function(data)
        {
			if(null == data.code || undefined == data.code || data.code == 0)
			{
				result = data;
			}else if(data.code == '500'){
				$.bhDialog({title:'权限不足，无法访问！',iconType:'warning'});
				return false;
			}
			else
			{
				$.bhDialog({title:'后台数据异常，请联系管理员',iconType:'warning'});
				result = resultDataBaseError;
			}
        }
    });

    return result;
}

function dataModelBatchModify(action, params, aSync)
{
	// 返回值
	var result = {"resultCode" : "99999" , "msg" : "unkown error"};

	// 数据模型的原生动作
	var dataModelAction = "MODIFY";

	// 请求类型为数据模型
	var actionType = "DATAMODEL";

	var callJsRequest = {};
	callJsRequest["requestParams"] = JSON.stringify(params);
	callJsRequest["actionType"] = actionType;
	callJsRequest["actionName"] = action;
	callJsRequest["dataModelAction"] = dataModelAction;

    $.ajax(
    {
        url: WIS_CONFIG.ROOT_PATH + "/commoncall/callBatch/"+action+"-"+actionType+"-"+dataModelAction+".do",
        type: 'post',
        data: callJsRequest,
        async: aSync,
        error: function(resp)
        {
            //2016-04-19 qiyu 未登录则刷新页
            if(resp.status == 401)
            {
                window.location.reload();
            }
            else if (resp.status == 403)
            {
				BH_UTILS.bhDialogWarning({
					title: '提示',
					content:'当前角色权限不足，请切换角色后重新操作',
					buttons: [{
						text: '确认',
						className: 'bh-btn-warning',
						callback: function() {
						}
					}]
				});
            }
            // 长时间未操作提示错误
            if(resp.statusText.indexOf("NetworkError") > -1)
            {
                BH_UTILS.bhDialogDanger({
                    title: '网络错误',
                    content:'您可以尝试刷新页面解决该问题',
                    buttons:[{
                        text: '关闭',
                        className: 'bh-btn-default'
                    }]
                });
            }
        },
        success: function(data)
        {
			if(null == data.code || undefined == data.code || data.code == 0)
			{
				result = data;
			}else if(data.code == '500'){
				$.bhDialog({title:'权限不足，无法访问！',iconType:'warning'});
				return false;
			}
			else
			{
				$.bhDialog({title:'后台数据异常，请联系管理员',iconType:'warning'});
				result = resultDataBaseError;
			}
        }
    });

    return result;
}

function dataModelBatchDelete(action, params, aSync)
{
	// 返回值
	var result = {"resultCode" : "99999" , "msg" : "unkown error"};

	// 数据模型的原生动作
	var dataModelAction = "DELETE";

	// 请求类型为数据模型
	var actionType = "DATAMODEL";

	var callJsRequest = {};
	callJsRequest["requestParams"] = JSON.stringify(params);
	callJsRequest["actionType"] = actionType;
	callJsRequest["actionName"] = action;
	callJsRequest["dataModelAction"] = dataModelAction;

    $.ajax(
    {
        url: WIS_CONFIG.ROOT_PATH + "/commoncall/callBatch/"+action+"-"+actionType+"-"+dataModelAction+".do",
        type: 'post',
        data: callJsRequest,
        async: aSync,
        error: function(resp)
        {
            //2016-04-19 qiyu 未登录则刷新页
            if(resp.status == 401)
            {
                window.location.reload();
            }
            else if (resp.status == 403)
            {
				BH_UTILS.bhDialogWarning({
					title: '提示',
					content:'当前角色权限不足，请切换角色后重新操作',
					buttons: [{
						text: '确认',
						className: 'bh-btn-warning',
						callback: function() {
						}
					}]
				});
            }
            // 长时间未操作提示错误
            if(resp.statusText.indexOf("NetworkError") > -1)
            {
                BH_UTILS.bhDialogDanger({
                    title: '网络错误',
                    content:'您可以尝试刷新页面解决该问题',
                    buttons:[{
                        text: '关闭',
                        className: 'bh-btn-default'
                    }]
                });
            }
        },
        success: function(data)
        {
			if(null == data.code || undefined == data.code || data.code == 0)
			{
				result = data;
			}else if(data.code == '500'){
				$.bhDialog({title:'权限不足，无法访问！',iconType:'warning'});
				return false;
			}
			else
			{
				$.bhDialog({title:'后台数据异常，请联系管理员',iconType:'warning'});
				result = resultDataBaseError;
			}
        }
    });

    return result;
}

function mineBatchExcute(action, params, aSync)
{
	// 返回值
	var result = {"resultCode" : "99999" , "msg" : "unkown error"};

	// 请求类型为数据模型
	var actionType = "MINE";

	var callJsRequest = {};
	callJsRequest["requestParams"] = JSON.stringify(params);
	callJsRequest["actionType"] = actionType;
	callJsRequest["actionName"] = action;
	callJsRequest["dataModelAction"] = "NULL";
    $.ajax(
    {
        url: WIS_CONFIG.ROOT_PATH + "/commoncall/callBatch/"+action+"-"+actionType+"-NULL"+".do",
        type: 'post',
        data: callJsRequest,
        async: aSync,
        error: function(resp)
        {
            //2016-04-19 qiyu 未登录则刷新页
            if(resp.status == 401)
            {
                window.location.reload();
            }
            else if (resp.status == 403)
            {
				BH_UTILS.bhDialogWarning({
					title: '提示',
					content:'当前角色权限不足，请切换角色后重新操作',
					buttons: [{
						text: '确认',
						className: 'bh-btn-warning',
						callback: function() {
						}
					}]
				});
            }
            // 长时间未操作提示错误
            if(resp.statusText.indexOf("NetworkError") > -1)
            {
                BH_UTILS.bhDialogDanger({
                    title: '网络错误',
                    content:'您可以尝试刷新页面解决该问题',
                    buttons:[{
                        text: '关闭',
                        className: 'bh-btn-default'
                    }]
                });
            }
        },
        success: function(data)
        {
			if(null == data.code || undefined == data.code || data.code == 0)
			{
				result = data;
			}else if(data.code == '500'){
				$.bhDialog({title:'权限不足，无法访问！',iconType:'warning'});
				return false;
			}
			else
			{
				$.bhDialog({title:'后台数据异常，请联系管理员',iconType:'warning'});
				result = resultDataBaseError;
			}
        }
    });

    return result;
}

function dataModelBatchSave(action, params, aSync)
{
	// 返回值
	var result = {"resultCode" : "99999" , "msg" : "unkown error"};

	// 数据模型的原生动作
	var dataModelAction = "SAVE";

	// 请求类型为数据模型
	var actionType = "DATAMODEL";

	var callJsRequest = {};
	callJsRequest["requestParams"] = JSON.stringify(params);
	callJsRequest["actionType"] = actionType;
	callJsRequest["actionName"] = action;
	callJsRequest["dataModelAction"] = dataModelAction;

    $.ajax(
    {
        url: WIS_CONFIG.ROOT_PATH + "/commoncall/callBatch/"+action+"-"+actionType+"-"+dataModelAction+".do",
        type: 'post',
        data: callJsRequest,
        async: aSync,
        error: function(resp)
        {
            //2016-04-19 qiyu 未登录则刷新页
            if(resp.status == 401)
            {
                window.location.reload();
            }
            else if (resp.status == 403)
            {
				BH_UTILS.bhDialogWarning({
					title: '提示',
					content:'当前角色权限不足，请切换角色后重新操作',
					buttons: [{
						text: '确认',
						className: 'bh-btn-warning',
						callback: function() {
						}
					}]
				});
            }
            // 长时间未操作提示错误
            if(resp.statusText.indexOf("NetworkError") > -1)
            {
                BH_UTILS.bhDialogDanger({
                    title: '网络错误',
                    content:'您可以尝试刷新页面解决该问题',
                    buttons:[{
                        text: '关闭',
                        className: 'bh-btn-default'
                    }]
                });
            }
        },
        success: function(data)
        {
			if(null == data.code || undefined == data.code || data.code == 0)
			{
				result = data;
			}else if(data.code == '500'){
				$.bhDialog({title:'权限不足，无法访问！',iconType:'warning'});
				return false;
			}
			else
			{
				$.bhDialog({title:'后台数据异常，请联系管理员',iconType:'warning'});
				result = resultDataBaseError;
			}
        }
    });

    return result;
}

function dataModelModify(action, params, aSync)
{
	// 返回值
	var result = {"resultCode" : "99999" , "msg" : "unkown error"};

	// 数据模型的原生动作
	var dataModelAction = "MODIFY";

	// 请求类型为数据模型
	var actionType = "DATAMODEL";

	var callJsRequest = {};
	callJsRequest["requestParams"] = JSON.stringify(params);
	callJsRequest["actionType"] = actionType;
	callJsRequest["actionName"] = action;
	callJsRequest["dataModelAction"] = dataModelAction;

    $.ajax(
    {
        url: WIS_CONFIG.ROOT_PATH + "/commoncall/call/"+action+"-"+actionType+"-"+dataModelAction+".do",
        type: 'post',
        data: callJsRequest,
        async: aSync,
        error: function(resp)
        {
            //2016-04-19 qiyu 未登录则刷新页
            if(resp.status == 401)
            {
                window.location.reload();
            }
            else if (resp.status == 403)
            {
				BH_UTILS.bhDialogWarning({
					title: '提示',
					content:'当前角色权限不足，请切换角色后重新操作',
					buttons: [{
						text: '确认',
						className: 'bh-btn-warning',
						callback: function() {
						}
					}]
				});
            }
            // 长时间未操作提示错误
            if(resp.statusText.indexOf("NetworkError") > -1)
            {
                BH_UTILS.bhDialogDanger({
                    title: '网络错误',
                    content:'您可以尝试刷新页面解决该问题',
                    buttons:[{
                        text: '关闭',
                        className: 'bh-btn-default'
                    }]
                });
            }
        },
        success: function(data)
        {
			if(null == data.code || undefined == data.code || data.code == 0)
			{
				result = data;
			}else if(data.code == '500'){
				$.bhDialog({title:'权限不足，无法访问！',iconType:'warning'});
				return false;
			}
			else
			{
				$.bhDialog({title:'后台数据异常，请联系管理员',iconType:'warning'});
				result = resultDataBaseError;
			}
        }
    });

    return result;
}

function dataModelSave(action, params, aSync)
{
	// 返回值
	var result = {"resultCode" : "99999" , "msg" : "unkown error"};

	// 数据模型的原生动作
	var dataModelAction = "SAVE";

	// 请求类型为数据模型
	var actionType = "DATAMODEL";

	var callJsRequest = {};
	callJsRequest["requestParams"] = JSON.stringify(params);
	callJsRequest["actionType"] = actionType;
	callJsRequest["actionName"] = action;
	callJsRequest["dataModelAction"] = dataModelAction;

    $.ajax(
    {
        url: WIS_CONFIG.ROOT_PATH + "/commoncall/call/"+action+"-"+actionType+"-"+dataModelAction+".do",
        type: 'post',
        data: callJsRequest,
        async: aSync,
        error: function(resp)
        {
            //2016-04-19 qiyu 未登录则刷新页
            if(resp.status == 401)
            {
                window.location.reload();
            }
            else if (resp.status == 403)
            {
				BH_UTILS.bhDialogWarning({
					title: '提示',
					content:'当前角色权限不足，请切换角色后重新操作',
					buttons: [{
						text: '确认',
						className: 'bh-btn-warning',
						callback: function() {
						}
					}]
				});
            }
            // 长时间未操作提示错误
            if(resp.statusText.indexOf("NetworkError") > -1)
            {
                BH_UTILS.bhDialogDanger({
                    title: '网络错误',
                    content:'您可以尝试刷新页面解决该问题',
                    buttons:[{
                        text: '关闭',
                        className: 'bh-btn-default'
                    }]
                });
            }
        },
        success: function(data)
        {
			if(null == data.code || undefined == data.code || data.code == 0)
			{
				result = data;
			}else if(data.code == '500'){
				$.bhDialog({title:'权限不足，无法访问！',iconType:'warning'});
				return false;
			}
			else
			{
				$.bhDialog({title:'后台数据异常，请联系管理员',iconType:'warning'});
				result = resultDataBaseError;
			}
        }
    });

    return result;
}

function dataModelDelete(action, params, aSync)
{
	// 返回值
	var result = {"resultCode" : "99999" , "msg" : "unkown error"};

	// 数据模型的原生动作
	var dataModelAction = "DELETE";

	// 请求类型为自定义动作
	var actionType = "DATAMODEL";

	var callJsRequest = {};
	callJsRequest["requestParams"] = JSON.stringify(params);
	callJsRequest["actionType"] = actionType;
	callJsRequest["actionName"] = action;
	callJsRequest["dataModelAction"] = dataModelAction;

    $.ajax(
    {
        url: WIS_CONFIG.ROOT_PATH + "/commoncall/call/"+action+"-"+actionType+"-"+dataModelAction+".do",
        type: 'post',
        data: callJsRequest,
        async: aSync,
        error: function(resp)
        {
            //2016-04-19 qiyu 未登录则刷新页
            if(resp.status == 401)
            {
                window.location.reload();
            }
            else if (resp.status == 403)
            {
				BH_UTILS.bhDialogWarning({
					title: '提示',
					content:'当前角色权限不足，请切换角色后重新操作',
					buttons: [{
						text: '确认',
						className: 'bh-btn-warning',
						callback: function() {
						}
					}]
				});
            }
            // 长时间未操作提示错误
            if(resp.statusText.indexOf("NetworkError") > -1)
            {
                BH_UTILS.bhDialogDanger({
                    title: '网络错误',
                    content:'您可以尝试刷新页面解决该问题',
                    buttons:[{
                        text: '关闭',
                        className: 'bh-btn-default'
                    }]
                });
            }
        },
        success: function(data)
        {
			if(null == data.code || undefined == data.code || data.code == 0)
			{
				result = data;
			}else if(data.code == '500'){
				$.bhDialog({title:'权限不足，无法访问！',iconType:'warning'});
				return false;
			}
			else
			{
				$.bhDialog({title:'后台数据异常，请联系管理员',iconType:'warning'});
				result = resultDataBaseError;
			}
        }
    });

    return result;
}


function mineQuery(action, params, aSync)
{
	// 返回值
	var result = {"resultCode" : "99999" , "msg" : "unkown error"};

	// 请求类型为自定义动作
	var actionType = "MINE";


	var callJsRequest = {};
	callJsRequest["requestParams"] = JSON.stringify(params);
	callJsRequest["actionType"] = actionType;
	callJsRequest["actionName"] = action;
	callJsRequest["dataModelAction"] = "QUERY";

    $.ajax(
    {
        url: WIS_CONFIG.ROOT_PATH + "/commoncall/callQuery/"+action+"-"+actionType+"-QUERY"+".do",
        type: 'post',
        data: callJsRequest,
        async: aSync,
        error: function(resp)
        {
            //2016-04-19 qiyu 未登录则刷新页
            if(resp.status == 401)
            {
                window.location.reload();
            }
            else if (resp.status == 403)
            {
				BH_UTILS.bhDialogWarning({
					title: '提示',
					content:'当前角色权限不足，请切换角色后重新操作',
					buttons: [{
						text: '确认',
						className: 'bh-btn-warning',
						callback: function() {
						}
					}]
				});
            }
            // 长时间未操作提示错误
            if(resp.statusText.indexOf("NetworkError") > -1)
            {
                BH_UTILS.bhDialogDanger({
                    title: '网络错误',
                    content:'您可以尝试刷新页面解决该问题',
                    buttons:[{
                        text: '关闭',
                        className: 'bh-btn-default'
                    }]
                });
            }
        },
        success: function(data)
        {
			if(null == data.code || undefined == data.code || data.code == 0)
			{
				result = data;
			}else if(data.code == '500'){
				$.bhDialog({title:'权限不足，无法访问！',iconType:'warning'});
				return false;
			}
			else
			{
				$.bhDialog({title:'后台数据异常，请联系管理员',iconType:'warning'});
				result = resultDataBaseError;
			}
        }
    });

    return result;
}

function mineQueryCallBack(action, params, aSync, callBack)
{
	// 返回值
	var result = {"resultCode" : "99999" , "msg" : "unkown error"};

	// 请求类型为自定义动作
	var actionType = "MINE";

	var callJsRequest = {};
	callJsRequest["requestParams"] = JSON.stringify(params);
	callJsRequest["actionType"] = actionType;
	callJsRequest["actionName"] = action;
	callJsRequest["dataModelAction"] = "QUERY";
    $.ajax(
    {
        url: WIS_CONFIG.ROOT_PATH + "/commoncall/callQuery/"+action+"-"+actionType+"-QUERY"+".do",
        type: 'post',
        data: callJsRequest,
        async: aSync,
        error: function(resp)
        {
            //2016-04-19 qiyu 未登录则刷新页
            if(resp.status == 401)
            {
                window.location.reload();
            }
            else if (resp.status == 403)
            {
				BH_UTILS.bhDialogWarning({
					title: '提示',
					content:'当前角色权限不足，请切换角色后重新操作',
					buttons: [{
						text: '确认',
						className: 'bh-btn-warning',
						callback: function() {
						}
					}]
				});
            }
            // 长时间未操作提示错误
            if(resp.statusText.indexOf("NetworkError") > -1)
            {
                BH_UTILS.bhDialogDanger({
                    title: '网络错误',
                    content:'您可以尝试刷新页面解决该问题',
                    buttons:[{
                        text: '关闭',
                        className: 'bh-btn-default'
                    }]
                });
            }
        	callBack(null);
        },
        success: function(data)
        {
			if(null == data.code || undefined == data.code || data.code == 0)
			{
				callBack(data);
			}else if(data.code == '500'){
				$.bhDialog({title:'权限不足，无法访问！',iconType:'warning'});
				return false;
			}
			else
			{
				$.bhDialog({title:'后台数据异常，请联系管理员',iconType:'warning'});
				callBack(data);
			}
        }
    });

    return result;
}

function dataModelQuery(action, params, aSync)
{
	// 返回值
	var result = {"resultCode" : "99999" , "msg" : "unkown error"};

	// 请求类型为数据模型
	var actionType = "DATAMODEL";

	var callJsRequest = {};
	callJsRequest["requestParams"] = JSON.stringify(params);
	callJsRequest["actionType"] = actionType;
	callJsRequest["actionName"] = action;
	callJsRequest["dataModelAction"] = "QUERY";
    $.ajax(
    {
        url: WIS_CONFIG.ROOT_PATH + "/commoncall/callQuery/"+action+"-"+actionType+"-QUERY"+".do",
        type: 'post',
        data: callJsRequest,
        async: aSync,
        error: function(resp)
        {
            //2016-04-19 qiyu 未登录则刷新页
            if(resp.status == 401)
            {
                window.location.reload();
            }
            else if (resp.status == 403)
            {
				BH_UTILS.bhDialogWarning({
					title: '提示',
					content:'当前角色权限不足，请切换角色后重新操作',
					buttons: [{
						text: '确认',
						className: 'bh-btn-warning',
						callback: function() {
						}
					}]
				});
            }
            // 长时间未操作提示错误
            if(resp.statusText.indexOf("NetworkError") > -1)
            {
                BH_UTILS.bhDialogDanger({
                    title: '网络错误',
                    content:'您可以尝试刷新页面解决该问题',
                    buttons:[{
                        text: '关闭',
                        className: 'bh-btn-default'
                    }]
                });
            }
        },
        success: function(data)
        {
			if(null == data.code || undefined == data.code || data.code == 0)
			{
				result = data;
			}else if(data.code == '500'){
				$.bhDialog({title:'权限不足，无法访问！',iconType:'warning'});
				return false;
			}
			else
			{
				$.bhDialog({title:'后台数据异常，请联系管理员',iconType:'warning'});
				result = resultDataBaseError;
			}
        }
    });

    return result;
}

function mineExcute(action, params, aSync)
{
	// 返回值
	var result = {"resultCode" : "99999" , "msg" : "unkown error"};

	// 请求类型为数据模型
	var actionType = "MINE";

	var callJsRequest = {};
	callJsRequest["requestParams"] = JSON.stringify(params);
	callJsRequest["actionType"] = actionType;
	callJsRequest["actionName"] = action;
	callJsRequest["dataModelAction"] = "NULL";
    $.ajax(
    {
        url: WIS_CONFIG.ROOT_PATH + "/commoncall/call/"+action+"-"+actionType+"-"+"NULL"+".do",
        type: 'post',
        data: callJsRequest,
        async: aSync,
        error: function(resp)
        {
            //2016-04-19 qiyu 未登录则刷新页
            if(resp.status == 401)
            {
                window.location.reload();
            }
            else if (resp.status == 403)
            {
				BH_UTILS.bhDialogWarning({
					title: '提示',
					content:'当前角色权限不足，请切换角色后重新操作',
					buttons: [{
						text: '确认',
						className: 'bh-btn-warning',
						callback: function() {
						}
					}]
				});
            }
            // 长时间未操作提示错误
            if(resp.statusText.indexOf("NetworkError") > -1)
            {
                BH_UTILS.bhDialogDanger({
                    title: '网络错误',
                    content:'您可以尝试刷新页面解决该问题',
                    buttons:[{
                        text: '关闭',
                        className: 'bh-btn-default'
                    }]
                });
            }
        },
        success: function(data)
        {
			if(null == data.code || undefined == data.code || data.code == 0)
			{
				result = data;
			}else if(data.code == '500'){
				$.bhDialog({title:'权限不足，无法访问！',iconType:'warning'});
				return false;
			}
			else
			{
				$.bhDialog({title:'后台数据异常，请联系管理员',iconType:'warning'});
				result = resultDataBaseError;
			}
        }
    });

    return result;
}