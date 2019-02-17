package com.wisedu.emap.itpub.bean.auth;

/**
 * @filename WeUserGroup.java
 * @date 2016年12月2日 上午10:56:37
 * @author wjfu 01116035
 */
/*
{
    "groupId": "用户组id",
    "groupName": "用户组名称",
    "domainCode": "所属业务域编号",
    "inType": "@用户组所属类型"
}
 */
public class WeUserGroup {
    
    /** 用户组id */
    private String groupId;
    
    /** 用户组名称 */
    private String groupName;
    
    /** 所属业务域编号 */
    private String domainCode;
    
    /**
     * 用户组所属类型
     * 10:普通
     * 20:校级
     * 21:班级
     * 22:专业级
     * 23:院系级
     * 30:etl
     */
    private String inType;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getDomainCode() {
        return domainCode;
    }

    public void setDomainCode(String domainCode) {
        this.domainCode = domainCode;
    }

    public String getInType() {
        return inType;
    }

    public void setInType(String inType) {
        this.inType = inType;
    }
    
}
