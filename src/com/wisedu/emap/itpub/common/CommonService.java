package com.wisedu.emap.itpub.common;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wisedu.emap.base.util.LogUtil;
import com.wisedu.emap.base.util.StringUtil;
import com.wisedu.emap.dao.DaoParam;
import com.wisedu.emap.model2.IEmapAction;
import com.wisedu.emap.model2.QueryResult;
import com.wisedu.emap.model2.action.ActionType;
import com.wisedu.emap.model2.action.IDataModelUpdateAction;
import com.wisedu.emap.model2.container.DataModelContainer;
import com.wisedu.emap.pedestal.app.IEmapAppContext;

/**
 * 
 * 类名称：CommonService    
 * 类描述： 
 * @version 1.0
 */
@Service("commonService")
public class CommonService
{
    /** 日志 **/
    protected static org.slf4j.Logger logger = LogUtil.getOnlineLog();
    
    @Autowired
    private IEmapAppContext appContext;
    
    /**
     * 
     * [简要描述]：批量
     * [详细描述]：有事务
     *
     * @return
     */
    public String mineBatchExcute(List<DaoParam> daoParamList, String actionName)
    {
    	String result = CommonConstant.SUCCESS;
        try
        {
            IEmapAction<Object> action = appContext.getAction(actionName).createAction(null);
            for (DaoParam dp : daoParamList)
            {
                action.execute(dp);
            }
        }
        catch (Exception e)
        {
            logger.error("dataModelBatchExcute|" + actionName + "|" , e);
            result = CommonConstant.DATABASE_ERROR;
            
        }
        return result;
    }
    
    /**
     * 
     * [简要描述]：批量
     * [详细描述]：有事务
     *
     * @return
     */
    public String dataModelBatchExcute(List<DaoParam> daoParamList, String actionName, String dataModelAction)
    {
    	String result = CommonConstant.SUCCESS;
        try
        {
            DataModelContainer container = appContext.getDataModel(actionName);
            
            ActionType actionType = getActionType(dataModelAction);
            
            IDataModelUpdateAction action = container.getUpdateAction(actionType);
            
            for (DaoParam dp : daoParamList)
            {
                action.execute(dp);
            }
        }
        catch (Exception e)
        {
            logger.error("dataModelBatchExcute|" + actionName + "|ActionType=" + dataModelAction + "|" , e);
            result = CommonConstant.DATABASE_ERROR;
        }        
        
        return result;
    }
    
    /**
     * 
     * [简要描述]：
     * [详细描述]：    
     *
     * @param dataModelAction
     * @return
     */
    public static ActionType getActionType(String dataModelAction)
    {
        if (CommonConstant.ACTION_TYPE_ADD.equals(dataModelAction))
        {
            return ActionType.ADD;
        }
       
        if (CommonConstant.ACTION_TYPE_SAVE.equals(dataModelAction))
        {
            return ActionType.SAVE;
        }
       
        if (CommonConstant.ACTION_TYPE_MODIFY.equals(dataModelAction))
        {
            return ActionType.MODIFY;
        }
        
        if (CommonConstant.ACTION_TYPE_DELTE.equals(dataModelAction))
        {
            return ActionType.DELETE;
        }
        
        return null;
    }
    
    /**
     * 
     * [简要描述]：根据用户组获取当前用户组的数据权限
     * [详细描述]：    
     *
     * @param roleId
     * @return
     */
    @SuppressWarnings("unchecked")
    public String getDataAuth(String roleId)
    {
        IEmapAction<Object> action = appContext.getAction("hqsjjs").createAction(null);

        DaoParam daoParam = new DaoParam();
        daoParam.addParam("ROLEID", roleId);
        Object result = action.execute(daoParam);

        if (null != result)
        {
            QueryResult<Map<String, Object>> queryResult = (QueryResult<Map<String, Object>>) result;
            Map<String, Object> map = queryResult.getFirst();
            if (null != map && !map.isEmpty())
            {
                String containsRoles = StringUtil.getString(map.get("CONTAINSROLES"));
                return containsRoles;
            }
        }

        return null;
    }
}
