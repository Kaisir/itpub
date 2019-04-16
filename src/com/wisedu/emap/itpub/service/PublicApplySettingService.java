package com.wisedu.emap.itpub.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wisedu.emap.base.util.StringUtil;
import com.wisedu.emap.dao.DaoParam;
import com.wisedu.emap.itpub.common.CommonService;
import com.wisedu.emap.itpub.util.CommonUtils;
import com.wisedu.emap.itpub.util.exception.PubExceptionUtil;
import com.wisedu.emap.model2.action.ActionType;
import com.wisedu.emap.model2.action.IDataModelUpdateAction;
import com.wisedu.emap.model2.container.DataModelContainer;
import com.wisedu.emap.pedestal.app.IEmapAppContext;

@Service
public class PublicApplySettingService {

	
	@Autowired
    private IEmapAppContext appContext;
	
	
	public void dataModelExecute(String paramString) throws Exception{
		
		Map<String,Object> paramMap = CommonUtils.buildMapParams(paramString, true);
		
		try{
			String dataModel = StringUtil.getString(paramMap.get("DATA_MODEL"));
			String dataAction = StringUtil.getString(paramMap.get("ACTION_TYPE"));
			
			DaoParam daoParam = new DaoParam();
			
			String dataMapStr = StringUtil.getString(paramMap.get("DATA"));
			
			daoParam = CommonUtils.buildParams(dataMapStr, true);
			
			DataModelContainer container = appContext.getDataModel(dataModel);

	        ActionType actionType = CommonService.getActionType(dataAction);

	        IDataModelUpdateAction action = container.getUpdateAction(actionType);
	        
	        action.execute(daoParam);
			
		}catch(Exception e){
			PubExceptionUtil.throwDatabaseException("执行异常", e);
		}
	}
}
