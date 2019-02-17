package com.wisedu.emap.itpub.bean.auth;

/**
 * @filename WeUserManagedUnit.java
 * @date 2016年12月2日 上午10:58:24
 * @author wjfu 01116035
 */
/*
{
    "positionLevel": "@岗位级别",
    "unitId": "管辖范围Id"
}
 */
public class WeUserManagedUnit {
    
    /** 
     * 岗位级别
     * 1:班级
     * 2:专业
     * 3:院系
     * -1:校级
     */
    private String positionLevel;
    
    /** 管辖范围Id */
    private String unitId;

    public String getPositionLevel() {
        return positionLevel;
    }

    public void setPositionLevel(String positionLevel) {
        this.positionLevel = positionLevel;
    }

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }
}
