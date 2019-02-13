package com.wisedu.emap.itpub.util;

import self.micromagic.util.annotation.Config;

/**
 * @filename EnvUtils.java
 * @date 2016年12月2日 上午11:55:48
 * @author wjfu 01116035
 */
public class EnvUtils {
    
    private EnvUtils() {
        
    }
    
    @Config(name="context.wecloud", description="是否为公有云环境,默认非共有云环境")
    private static String isCloudEnv;
    
    @Config(name = "auth.header.logoutPath", description = "注销后的跳转地址，请根据现场统一身份认证的环境进行配置")
    private static String logoutPath;
    
    /**
     * 判断是否为公有云环境
     * @date 2016年12月2日 下午12:17:23
     * @author wjfu 01116035
     * @return
     * @throws Exception
     */
    public static boolean isCloudEnv() {
        return "true".equals(isCloudEnv) || "1".equals(isCloudEnv);
    }
    
    /**
     * 获取登出地址
     * @date 2016年12月3日 下午3:16:36
     * @author wjfu 01116035
     * @return
     */
    public static String getLogoutUrl() {
        return logoutPath;
    }
    
}
