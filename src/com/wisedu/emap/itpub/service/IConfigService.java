package com.wisedu.emap.itpub.service;

import java.util.List;
import java.util.Map;

import com.wisedu.emap.pedestal.app.IEmapApp;

/**
 * 访问页面时候 页面的配置项服务
 * 
 * @filename IConfigService.java
 * @date 2016年5月13日 下午5:15:28
 * @author wjfu 01116035
 */
public interface IConfigService {

    /**
     * 获取用户的权限
     * 
     * @author xianghao
     * @date 2016年8月2日 上午9:03:22
     * @param userId
     * @param appId 应用的id
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getUserLimit(String appName) throws Exception;
    
    /**
     * 获取共有云的授权路由和授权信息
     * @date 2016年12月2日 下午4:38:23
     * @author wjfu 01116035
     * @return
     * @throws Exception
     */
    public Map<String, Object> getCloudRoute(IEmapApp appInfo) throws Exception;

    /**
     * 切换用户角色触发的方法
     * @date 2016年12月8日 下午2:01:04
     * @author wjfu 01116035
     * @param appName
     * @param roleId
     * @return
     * @throws Exception
     */
    public Map<String, Object> changeAppRole(String appName, String roleId) throws Exception;
}
