package com.wisedu.emap.itpub.bean;

public class CallJsRequest
{
    private String actionType;
    
    private String actionName;
    
    private String dataModelAction;
    
    private String requestParams;

    public String getActionType()
    {
        return actionType;
    }

    public void setActionType(String actionType)
    {
        this.actionType = actionType;
    }

    public String getActionName()
    {
        return actionName;
    }

    public void setActionName(String actionName)
    {
        this.actionName = actionName;
    }

    public String getDataModelAction()
    {
        return dataModelAction;
    }

    public void setDataModelAction(String dataModelAction)
    {
        this.dataModelAction = dataModelAction;
    }

    public String getRequestParams()
    {
        return requestParams;
    }

    public void setRequestParams(String requestParams)
    {
        this.requestParams = requestParams;
    }
}
