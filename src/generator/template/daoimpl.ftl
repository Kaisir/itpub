package com.wisedu.emap.${appName}.dao.impl;

import com.wisedu.emap.${appName}.dao.${daoName};
import com.wisedu.emap.itpub.dao.impl.BaseDaoImpl;

import org.springframework.stereotype.Component;

@Component
public class ${daoName}_IMPL extends BaseDaoImpl implements ${daoName} {

	public ${daoName}_IMPL() {
		super("${daoName}");
	}

}
