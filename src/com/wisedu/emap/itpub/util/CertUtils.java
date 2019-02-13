package com.wisedu.emap.itpub.util;

import java.util.HashMap;
import java.util.Map;
import java.io.*;

import lombok.extern.slf4j.Slf4j;
import self.micromagic.util.StringTool;

import com.wisedu.amp.base.sdk.crypt.CerDecrypt;
import com.wisedu.emap.base.util.JSONUtil;
import com.wisedu.emap.pedestal.app.IEmapApp;

@Slf4j
public class CertUtils {
	@SuppressWarnings("unchecked")
	public static Map<String, Object> readCerts(IEmapApp app) {
		log.info("itservicecommon: read cert of:"+app.getName());
		Map<String,Object> certs = new HashMap<String,Object>();
		String realCerName = app.getName().concat("_dis.cer");
		File realCerFile = new File(new File(app.getPath(), "config"),realCerName);
		String runtimeCer = "";
		if (realCerFile.isFile()) {
			FileInputStream in=null;
			try {
				in = new FileInputStream(realCerFile);
				String realCerStr = StringTool.toString(in, "UTF-8");
				if (!StringTool.isEmpty(realCerStr)) {
					runtimeCer = realCerStr;
				}
				certs = (Map<String, Object>) JSONUtil.parse2Obj(CerDecrypt.decrypt(runtimeCer, CerDecrypt.getPublicKey()));

			} catch (Exception e) {
				log.info("itservicecommon: read cert failed!");
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			} finally {
				try {
					in.close();
				} catch (IOException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
			}
		}
		
		return certs;
	}
	
	public static boolean validateCert(IEmapApp app,boolean disable){
		boolean isValidate = false;
		if (!disable){
		//if (!EmapContext.isDebug() || !disable){
			Map<String,Object> cert = readCerts(app);
			try {
				isValidate = "dev_wisedu".equals(cert.get("userRole"))?true:false;
			}catch (Exception e){
				log.info("itservicecommon: validateCert failed of "+app.getName());
			}
		}else{
			isValidate = true ;
		}
		if(!isValidate){
			log.error(app.getId()+"|应用未授权!请确认应用来源!");
		}
		return isValidate;
	}
}
