package com.wisedu.emap.itpub.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wisedu.emap.base.util.LogUtil;
import com.wisedu.emap.base.util.StringUtil;
import com.wisedu.emap.dao.DaoParam;
import com.wisedu.emap.itpub.bean.CallJsRequest;
import com.wisedu.emap.model2.IEmapAction;
import com.wisedu.emap.model2.QueryResult;
import com.wisedu.emap.model2.action.ActionType;
import com.wisedu.emap.model2.action.IDataModelQueryAction;
import com.wisedu.emap.model2.action.IDataModelUpdateAction;
import com.wisedu.emap.model2.container.DataModelContainer;
import com.wisedu.emap.pedestal.app.IEmapAppContext;

/**
 *
 * 类名称：CommonController
 * 类描述：公共控制器
 * @version 1.0
 */
@Controller("com.wisedu.emap.itpub.common.CommonActionController")
@RequestMapping("/commoncall")
public class CommonActionController
{
    /** 日志 **/
    protected static org.slf4j.Logger logger = LogUtil.getOnlineLog();

    @Autowired
    private IEmapAppContext appContext;

    @Autowired
    private CommonService commonService;

    private final Gson gson = new Gson();

    private final JsonParser jsonParser = new JsonParser();

    /**
     *
     * [简要描述]：新增、修改、删除
     * [详细描述]： 自定义动作和数据模型原生动作
     *
     * @param request
     * @return
     */
//    @RequestMapping(value = "/call")
    @RequestMapping("/call/{action}-{actionType}-{dataModelAction}.do")
    @ResponseBody
    public JsonObject call(@PathVariable String action, @PathVariable String actionType,@PathVariable String dataModelAction,CallJsRequest callJsRequest)
    {
        if (checkMustParam(callJsRequest))
        {
            return buildRsp(CommonConstant.PARAM_ERROR);
        }

        String resultCode = CommonConstant.SUCCESS;

        // 获取动作类型
        String actionTypeBean = callJsRequest.getActionType();

        if(checkNotEql(actionTypeBean,actionTypeBean)){
        	return buildRsp(CommonConstant.PARAM_ERROR);
        }
        // 获取数据模型原生动作 ADD,MODIFY,DELETE,SAVE,QUERY
        // 当actionType为数据模型时起作用
        String dataModelActionBean = callJsRequest.getDataModelAction();
        if(checkNotEql(dataModelAction,dataModelActionBean)){
        	return buildRsp(CommonConstant.PARAM_ERROR);
        }
        // 动作名称
        String actionName = callJsRequest.getActionName();

        if(checkNotEql(action,actionName)){
        	return buildRsp(CommonConstant.PARAM_ERROR);
        }

        // 获取请求参数
        String requestParams = callJsRequest.getRequestParams();

        // 封装参数列表
        DaoParam daoParam = buildParams(requestParams);

        if (CommonConstant.ACTIONTYPE_MINE.equals(actionType))
        {
            resultCode = mineExcute(daoParam, actionName);
        }
        else if (CommonConstant.ACTIONTYPE_DATAMODEL.equals(actionType))
        {
            resultCode = dataModelExcute(daoParam, actionName, dataModelAction);
        }

        return buildRsp(resultCode);
    }

    /**
     *
     * [简要描述]：新增、修改、删除     批量
     * [详细描述]： 自定义动作和数据模型原生动作
     *
     * @param request
     * @return
     */
    @RequestMapping("/callBatch/{action}-{actionType}-{dataModelAction}.do")
    @ResponseBody
    public JsonObject callBatch(@PathVariable String action, @PathVariable String actionType,@PathVariable String dataModelAction,CallJsRequest callJsRequest)
    {
        if (checkMustParam(callJsRequest))
        {
            return buildRsp(CommonConstant.PARAM_ERROR);
        }

        String resultCode = CommonConstant.SUCCESS;

        // 获取动作类型
        String actionTypeBean = callJsRequest.getActionType();

        if(checkNotEql(actionType,actionTypeBean)){
        	return buildRsp(CommonConstant.PARAM_ERROR);
        }
        // 获取数据模型原生动作 ADD,MODIFY,DELETE,SAVE,QUERY
        // 当actionType为数据模型时起作用
        String dataModelActionBean = callJsRequest.getDataModelAction();

        if(checkNotEql(dataModelActionBean,dataModelAction)){
        	return buildRsp(CommonConstant.PARAM_ERROR);
        }
        // 动作名称
        String actionName = callJsRequest.getActionName();

        if(checkNotEql(actionName,action)){
        	return buildRsp(CommonConstant.PARAM_ERROR);
        }
        // 获取请求参数
        String requestParams = callJsRequest.getRequestParams();

        // 封装参数列表
        List<DaoParam> daoParamList = buildBatchParams(requestParams);

        if (CommonConstant.ACTIONTYPE_MINE.equals(actionType))
        {
            resultCode = commonService.mineBatchExcute(daoParamList, actionName);
        }
        else if (CommonConstant.ACTIONTYPE_DATAMODEL.equals(actionType))
        {
            resultCode = commonService.dataModelBatchExcute(daoParamList, actionName, dataModelAction);
        }

        return buildRsp(resultCode);
    }

    /**
     *
     * [简要描述]：查询
     * [详细描述]： 自定义动作和数据模型原生动作
     *
     * @param request
     * @return
     */
    @RequestMapping("/callQuery/{action}-{actionType}-{dataModelAction}.do")
    @ResponseBody
    public JsonObject callQuery(@PathVariable String action, @PathVariable String actionType,@PathVariable String dataModelAction,CallJsRequest callJsRequest)
    {
        if (checkMustQueryParam(callJsRequest))
        {
            return buildRsp(CommonConstant.PARAM_ERROR);
        }

        String resultCode = CommonConstant.SUCCESS;

        // 获取动作类型
        String actionTypeBean = callJsRequest.getActionType();

        if(checkNotEql(actionType,actionTypeBean)){
        	return buildRsp(CommonConstant.PARAM_ERROR);
        }
        // 动作名称
        String actionName = callJsRequest.getActionName();

        if(checkNotEql(actionName,action)){
        	return buildRsp(CommonConstant.PARAM_ERROR);
        }
        // 获取请求参数
        String requestParams = callJsRequest.getRequestParams();

        JsonArray queryResult = null;

        // 封装参数列表
        DaoParam daoParam = buildParams(requestParams);

        if (CommonConstant.ACTIONTYPE_MINE.equals(actionType))
        {
            queryResult = mineQuery(daoParam, actionName);
        }
        else if (CommonConstant.ACTIONTYPE_DATAMODEL.equals(actionType))
        {
            queryResult = dataModelQuery(daoParam, actionName);
        }

        return buildRsp(resultCode, queryResult);
    }

    /**
     *
     * [简要描述]：
     * [详细描述]：
     *
     * @param request
     * @return true：非法， false合法
     */
    private static boolean checkMustQueryParam(CallJsRequest callJsRequest)
    {
        // 获取动作类型
        String actionType = callJsRequest.getActionType();
        // 动作名称
        String actionName = callJsRequest.getActionName();

        return StringUtils.isEmpty(actionType) || StringUtils.isEmpty(actionName);
    }

    /**
     *
     * [简要描述]：
     * [详细描述]：
     *
     * @param request
     * @return true：非法， false合法
     */
    private static boolean checkMustParam(CallJsRequest callJsRequest)
    {
        // 获取动作类型
        String actionType = callJsRequest.getActionType();
        // 动作名称
        String actionName = callJsRequest.getActionName();

        // 获取数据模型原生动作 ADD,MODIFY,DELETE,SAVE,QUERY
        // 当actionType为数据模型时起作用
        String dataModelAction = callJsRequest.getDataModelAction();

        if (CommonConstant.ACTIONTYPE_DATAMODEL.equals(actionType))
        {
            if (StringUtils.isEmpty(dataModelAction))
            {
                return true;
            }
        }

        return StringUtils.isEmpty(actionType) || StringUtils.isEmpty(actionName);
    }

    private static JsonObject buildRsp(String resultCode)
    {
        JsonObject rsp = new JsonObject();
        rsp.addProperty("resultCode", resultCode);

        if (CommonConstant.SUCCESS.equals(resultCode))
        {
            rsp.addProperty("msg", "success");
        }
        else if (CommonConstant.PARAM_ERROR.equals(resultCode))
        {
            rsp.addProperty("msg", "invalid params");
        }
        else if (CommonConstant.DATABASE_ERROR.equals(resultCode))
        {
            rsp.addProperty("msg", "后台数据异常");
        }

        return rsp;
    }

    /**
     *
     * [简要描述]：查询结果
     * [详细描述]：
     *
     * @param resultCode
     * @param jsonElement
     * @return
     */
    private static JsonObject buildRsp(String resultCode, JsonElement jsonElement)
    {
        JsonObject rsp = new JsonObject();
        rsp.addProperty("resultCode", resultCode);

        if (CommonConstant.SUCCESS.equals(resultCode))
        {
            rsp.addProperty("msg", "success");
        }
        else if (CommonConstant.PARAM_ERROR.equals(resultCode))
        {
            rsp.addProperty("msg", "invalid params");
        }
        else if (CommonConstant.DATABASE_ERROR.equals(resultCode))
        {
            rsp.addProperty("msg", "后台数据异常");
        }

        rsp.add("data", jsonElement);

        return rsp;
    }

    private String mineExcute(DaoParam daoParam, String actionName)
    {
        IEmapAction<Object> action = appContext.getAction(actionName).createAction(null);
        action.execute(daoParam);

        return CommonConstant.SUCCESS;
    }

    private String dataModelExcute(DaoParam daoParam, String actionName, String dataModelAction)
    {
        DataModelContainer container = appContext.getDataModel(actionName);

        ActionType actionType = CommonService.getActionType(dataModelAction);

        IDataModelUpdateAction action = container.getUpdateAction(actionType);
        action.execute(daoParam);

        return CommonConstant.SUCCESS;
    }

    /**
     *
     * [简要描述]：
     * [详细描述]：
     *
     * @param paramString JSON字符串
     * @return
     */
    private static DaoParam buildParams(String paramString)
    {
        if (StringUtils.isEmpty(paramString))
        {
            return null;
        }

        DaoParam daoParam = new DaoParam();

        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(paramString);

        JsonObject jo = je.getAsJsonObject();

        Set<Entry<String, JsonElement>> set = jo.entrySet();

        for (Entry<String, JsonElement> ssEntry : set)
        {
            if (StringUtils.contains(ssEntry.getKey(), "*order"))
            {
                daoParam.setOrderColumns(ssEntry.getValue().getAsString());
                continue;
            }

            daoParam.addParam(ssEntry.getKey(), ssEntry.getValue().getAsString());
        }

        return daoParam;
    }

    /**
     *
     * [简要描述]：批量操作的参数封装
     * [详细描述]：
     *
     * @param paramString JSON字符串
     * @return
     */
    private static List<DaoParam> buildBatchParams(String paramString)
    {
        List<DaoParam> list = new ArrayList<DaoParam>();

        if (StringUtils.isEmpty(paramString))
        {
            return list;
        }

        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(paramString);

        JsonArray ja = je.getAsJsonArray();

        JsonObject jo = null;
        Set<Entry<String, JsonElement>> set = null;
        DaoParam daoParam = null;
        for (int i =0 ; i < ja.size() ; i ++)
        {
            daoParam = new DaoParam();

            jo = ja.get(i).getAsJsonObject();
            set = jo.entrySet();

            for (Entry<String, JsonElement> ssEntry : set)
            {
                daoParam.addParam(ssEntry.getKey(), ssEntry.getValue().getAsString());
            }
            list.add(daoParam);
        }

        return list;
    }

    private JsonArray mineQuery(DaoParam daoParam, String actionName)
    {
        // 返回结果
        JsonArray resultJsonArr = new JsonArray();

        IEmapAction <QueryResult<Map<?, ?>>> imapAction = appContext.getAction(actionName).createAction(null);
        QueryResult<Map<?, ?>> queryResult = imapAction.execute(daoParam);

        for (Map<?,?> mapObject : queryResult)
        {
            resultJsonArr.add(map2Json(mapObject));
        }

        return resultJsonArr;
    }

    private JsonArray dataModelQuery(DaoParam daoParam, String actionName)
    {
        // 返回结果
        JsonArray resultJsonArr = new JsonArray();

        DataModelContainer container = appContext.getDataModel(actionName);
        IDataModelQueryAction<Map<String,Object>> action = container.getQueryAction();
        QueryResult<Map<String, Object>> queryResult = action.execute(daoParam);

        for (Map<?,?> mapObject : queryResult)
        {
            resultJsonArr.add(map2Json(mapObject));
        }

        return resultJsonArr;
    }

    private JsonElement map2Json(Map<?, ?> map)
    {
        String str = gson.toJson(map);

        return jsonParser.parse(str);
    }
    /**
     * 校验两个字符串是否相等 或是空
     * @param str1
     * @param str2
     * @return
     */
    private boolean checkNotEql(String str1,String str2){
    	if(StringUtil.isEmpty(str1)||StringUtil.isEmpty(str2)){
    		return true;
    	}
    	if(!str1.equals(str2)){
    		return true;
    	}
    	return false;
    }
}
