package com.wisedu.emap.itpub.service;

import org.springframework.stereotype.Service;

import com.wisedu.emap.pedestal.app.IEmapApp;
import com.wisedu.emap.pedestal.app.IEmapAppListener;
import com.wisedu.emap.pedestal.app.ISysAppLoader;
import com.wisedu.emap.pedestal.core.SysAppConfig;
@Service("itpub.service.randomService")
public class RandomService implements ISysAppLoader, IEmapAppListener {


	public long sysTime=System.currentTimeMillis();

	public void action(IEmapApp paramIEmapApp, Type paramType) throws Exception {
		this.sysTime=System.currentTimeMillis();
	}

	public void init(SysAppConfig paramSysAppConfig) {
		paramSysAppConfig.setAppListener(this);

	}

	public void destroy(SysAppConfig paramSysAppConfig) {
		// TODO 自动生成的方法存根

	}

	public long getSysTime(){
		return sysTime;
	}

}
