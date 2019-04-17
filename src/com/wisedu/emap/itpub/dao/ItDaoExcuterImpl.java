package com.wisedu.emap.itpub.dao;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.wisedu.emap.dao.DaoParam;
import com.wisedu.emap.model2.IEmapAction;
import com.wisedu.emap.model2.QueryResult;
import com.wisedu.emap.model2.action.ActionType;
import com.wisedu.emap.model2.action.IDataModelQueryAction;
import com.wisedu.emap.model2.action.IDataModelUpdateAction;
import com.wisedu.emap.model2.container.ActionContainer;
import com.wisedu.emap.model2.container.DataModelContainer;
import com.wisedu.emap.pedestal.app.IEmapAppContext;

/**
 * 
 * 项目名称：itpub      
 * @version 1.0
 */
@Service("itDaoExcuter")
public class ItDaoExcuterImpl implements ItDaoExcuter
{
    public QueryResult<Map<String, Object>> customQuery(IEmapAppContext appContext, String actionName, DaoParam daoParam)
    {
        ActionContainer actionContainer = appContext.getAction(actionName);
        IEmapAction<QueryResult<Map<String, Object>>> action = actionContainer.createAction(null);
        QueryResult<Map<String, Object>> result = action.execute(daoParam);
        return result;
    }

    public <T> QueryResult<T> customQuery(IEmapAppContext appContext, String actionName, DaoParam daoParam, Class<T> rowType)
    {
        ActionContainer actionContainer = appContext.getAction(actionName);
        IEmapAction<QueryResult<T>> action = actionContainer.createAction(null);
        QueryResult<T> result = action.execute(daoParam);
        return result;
    }

    public QueryResult<Map<String, Object>> dataModelQuery(IEmapAppContext appContext, String modelName,
            DaoParam daoParam)
    {
        DataModelContainer dataModelContainer = appContext.getDataModel(modelName);
        IDataModelQueryAction<Map<String, Object>> queryAction = dataModelContainer.getQueryAction();
        return queryAction.executeQuery(daoParam);
    }

    public <T> QueryResult<T> dataModelQuery(IEmapAppContext appContext, String modelName, DaoParam daoParam, Class<T> rowType)
    {
        DataModelContainer dataModelContainer = appContext.getDataModel(modelName);
        IDataModelQueryAction<T> queryAction = dataModelContainer.getQueryAction(rowType);
        return queryAction.executeQuery(daoParam);
    }

    
    /**
     * [简要描述]：动作DML数据操作公共方法实现   </br>
     * [详细描述]：返回int 处理条数
     * 
     * @param IEmapAppContext appContext 上下文
     *        action   执行的动作名称
     *        DaoParam daoParam  参数对象
     * @return int处理条数
     */
    public Integer customExecute(IEmapAppContext appContext, String action, DaoParam daoParam)
    {
        //更新数据库
        IEmapAction<Integer> dmlAction = appContext.getAction(action).createAction(null);
        int number = dmlAction.execute(daoParam);
        
        return number;
    }
    
    /**
     * [简要描述]：公共后台调用动作流方法   </br>
     * [详细描述]：
     * 
     * @param IEmapAppContext appContext 上下文
     *        action   执行的动作名称
     *        DaoParam daoParam  参数对象
     * @return int处理条数
     */
    public void customActionFlowExecute(IEmapAppContext appContext, String action, DaoParam daoParam)
    {
        //更新数据库
        IEmapAction<QueryResult<Map<String, Object>>> dmlAction = appContext.getAction(action).createAction(null);
         dmlAction.execute(daoParam);
    }


    /**
     * [简要描述]：通过数据模型表，执行DML 新增功能</br>
     * [详细描述]：返回int 处理条数
     * 
     * @param IEmapAppContext appContext 上下文
     *        String model       模型名称
     *        DaoParam daoParam  参数对象
     * @return int处理条数
     */
    public int dataModelExcuteAdd(IEmapAppContext appContext, String model,DaoParam daoParam)
    {
        
        DataModelContainer container = appContext.getDataModel(model);
        IDataModelUpdateAction queryAction = container.getUpdateAction(ActionType.ADD);
        int number = queryAction.executeUpdate(daoParam);
        
        return number;
    }
    
    /**
     * [简要描述]：通过数据模型表，执行DML 保存功能</br>
     * [详细描述]：返回int 处理条数
     * 
     * @param IEmapAppContext appContext 上下文
     *        String model       模型名称
     *        DaoParam daoParam  参数对象
     * @return int处理条数
     */
    public int dataModelExcuteSave(IEmapAppContext appContext, String model,DaoParam daoParam)
    {
        
        DataModelContainer container = appContext.getDataModel(model);
        IDataModelUpdateAction queryAction = container.getUpdateAction(ActionType.SAVE);
        int number = queryAction.executeUpdate(daoParam);
        
        return number;
    }


    
    /**
     * [简要描述]：通过数据模型表，执行DML 修改数据</br>
     * [详细描述]：返回int 处理条数
     * 
     * @param IEmapAppContext appContext 上下文
     *        String model       模型名称
     *        ActionType type    操作类型 新增 ，修改 ，删除
     *        DaoParam daoParam  参数对象
     * @return int处理条数
     */
    public int dataModelExcuteModify(IEmapAppContext appContext, String model, DaoParam daoParam)
    {
        
        DataModelContainer container = appContext.getDataModel(model);
        IDataModelUpdateAction queryAction = container.getUpdateAction(ActionType.MODIFY);
        int number = queryAction.executeUpdate(daoParam);
        
        return number;
    }

    
    /**
     * [简要描述]：通过数据模型表，执行DML 删除数据</br>
     * [详细描述]：返回int 处理条数
     * 
     * @param IEmapAppContext appContext 上下文
     *        String model       模型名称
     *        DaoParam daoParam  参数对象
     * @return int处理条数
     */
    public int dataModelExcuteDelete(IEmapAppContext appContext, String model, DaoParam daoParam)
    {
        
        DataModelContainer container = appContext.getDataModel(model);
        IDataModelUpdateAction queryAction = container.getUpdateAction(ActionType.DELETE);
        int number = queryAction.executeUpdate(daoParam);
        
        return number;
    }
    
    
    
    
}