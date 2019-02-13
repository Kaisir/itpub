package com.wisedu.emap.itpub.bean.auth;

import java.util.List;

/**
 * @filename WeAuthItem.java
 * @date 2016年12月8日 下午2:21:04
 * @author wjfu 01116035
 */
public class WeAuthItem {
    
    /** 授权模块的ID */
    private String moduleId;
    
    /** 授权的项目的ID */
    private List<String> itemIds;

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public List<String> getItemIds() {
        return itemIds;
    }

    public void setItemIds(List<String> itemIds) {
        this.itemIds = itemIds;
    }
    
    
}
