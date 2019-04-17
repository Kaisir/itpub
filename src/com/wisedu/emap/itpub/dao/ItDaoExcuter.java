package com.wisedu.emap.itpub.dao;

import java.util.Map;

import com.wisedu.emap.dao.DaoParam;
import com.wisedu.emap.model2.QueryResult;
import com.wisedu.emap.pedestal.app.IEmapAppContext;

/**
 * 
 * 项目名称：itpub</br>   
 * @version 1.0
 */
public interface ItDaoExcuter
{
    /**
     * 
     * [简要描述]：自定义的动作查询</br>
     * [详细描述]：返回QueryResult<Map<String, Object>>
     *
     * @param appContext 应用上下文
     * @param actionName 动作名
     * @param daoParam 参数对象
     * @return 查询结果
     */
    public QueryResult<Map<String, Object>> customQuery(IEmapAppContext appContext, String actionName, DaoParam daoParam);
    
    /**
     * 
     * [简要描述]：自定义的动作查询</br>
     * [详细描述]：返回QueryResult<Map<String, Object>>
     *
     * @param appContext 应用上下文
     * @param actionName 动作名
     * @param daoParam 参数对象
     * @param rowType 需要返回的类型
     * @return 查询结果
     */
    public <T> QueryResult<T> customQuery(IEmapAppContext appContext, String actionName, DaoParam daoParam, Class<T> rowType);
    
    /**
     * 
     * [简要描述]：模型动作查询</br>
     * [详细描述]：返回QueryResult<Map<String, Object>>
     *
     * @param appContext 应用上下文
     * @param modelName 模型名
     * @param daoParam 参数对象
     * @return 查询结果
     */
    public QueryResult<Map<String, Object>> dataModelQuery(IEmapAppContext appContext, String modelName, DaoParam daoParam);
    
    /**
     * 
     * [简要描述]：模型动作查询</br>
     * [详细描述]：返回QueryResult<Map<String, Object>>
     *
     * @param appContext 应用上下文
     * @param modelName 模型名
     * @param daoParam 参数对象
     * @param rowType 需要返回的类型 
     * @return 查询结果
     */
    public <T> QueryResult<T> dataModelQuery(IEmapAppContext appContext, String modelName, DaoParam daoParam, Class<T> rowType);

    
    /**
     * [简要描述]：动作DML数据操作公共方法   </br>
     * [详细描述]：返回int 处理条数
     * 
     * @param  IEmapAppContext appContext 上下文
     *         action   执行的动作名称
     *         DaoParam daoParam  参数对象
     * @return int处理条数
     * 时间：2016-11-5 09:56:12
     */
    public Integer customExecute(IEmapAppContext appContext,String action,DaoParam daoParam);
    
    /**
     * [简要描述]：公共后台调用动作流方法   </br>
     * [详细描述]：
     * 
     * @param IEmapAppContext appContext 上下文
     *        action   执行的动作名称
     *        DaoParam daoParam  参数对象
     * @return int处理条数
     * 时间：2017-01-13 14:18:12
     */
    public void customActionFlowExecute(IEmapAppContext appContext, String action, DaoParam daoParam);
    
    /**
     * [简要描述]：通过数据模型表，执行DML 保存数据</br>
     * [详细描述]：返回int 处理条数
     * 
     * @param IEmapAppContext appContext 上下文
     *        String model       模型名称
     *        DaoParam daoParam  参数对象
     * @return int处理条数
     * 时间：2016-11-5 09:56:12
     */
    public int dataModelExcuteSave(IEmapAppContext appContext,String model,DaoParam daoParam);
    
    
    /**
     * [简要描述]：通过数据模型表，执行DML 新增数据</br>
     * [详细描述]：返回int 处理条数
     * 
     * @param IEmapAppContext appContext 上下文
     *        String model       模型名称
     *        DaoParam daoParam  参数对象
     * @return int处理条数
     * 时间：2016-11-5 09:56:12
     */
    public int dataModelExcuteAdd(IEmapAppContext appContext,String model,DaoParam daoParam);
    

    /**
     * [简要描述]：通过数据模型表，执行DML 修改数据</br>
     * [详细描述]：返回int 处理条数
     * 
     * @param IEmapAppContext appContext 上下文
     *        String model       模型名称
     *        ActionType type    操作类型 新增 ，修改 ，删除
     *        DaoParam daoParam  参数对象
     * @return int处理条数
     * 时间：2016-11-5 09:56:12
     */
    public int dataModelExcuteModify(IEmapAppContext appContext,String model,DaoParam daoParam);
    
    
    /**
     * [简要描述]：通过数据模型表，执行DML 删除数据</br>
     * [详细描述]：返回int 处理条数
     * 
     * @param IEmapAppContext appContext 上下文
     *        String model       模型名称
     *        DaoParam daoParam  参数对象
     * @return int处理条数
     * 时间：2016-11-5 09:56:12
     */
    public int dataModelExcuteDelete(IEmapAppContext appContext,String model,DaoParam daoParam);
    
}