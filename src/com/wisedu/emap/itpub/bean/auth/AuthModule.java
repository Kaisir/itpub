package com.wisedu.emap.itpub.bean.auth;

/**
 * @filename AuthModule.java
 * @date 2016年12月8日 下午4:16:54
 * @author wjfu 01116035
 */
public class AuthModule {
    
    /** 模块的名称 */
    private String moduleName;
    
    /** 模块的路由 */
    private String modulePath;

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getModulePath() {
        return modulePath;
    }

    public void setModulePath(String modulePath) {
        this.modulePath = modulePath;
    }
    
    
}
