package com.wisedu.emap.itpub.bean.auth;

import java.util.List;

/**
 * @filename WeUser.java
 * @date 2016年12月2日 上午10:48:42
 * @author wjfu 01116035
 */
/*
{
    "userId": "用户唯一标识",
    "userName": "姓名",   
    "userAccount": "职工号或者学号或者临时人员号",
    "userGenderCode": "性别代码",
    "userGender": "性别",
    "userDepartmentCode": "部门代码",
    "userDepartment": "所属部门",
    "userMajorCode": "专业代码",
    "userMajor": "专业",
    "userGrade": "年级",
    "userClassCode": "班级代码",
    "userClass": "班级",
    "userAvatar": "用户头像",
    "userType": "@用户类型",
    "groups": [
        {
            "groupId": "用户组id",           
            "groupName": "用户组名称",
            "domainCode": "所属业务域编号",
            "inType": "@用户组所属类型"
        }
    ],
    "managedUnits": [
        {
            "positionLevel": "@岗位级别",
            "unitId": "管辖范围Id"
        }
    ]
}*/
public class WeUser {
    
    public WeUser() {
    }
    
    public WeUser(String userAccount) {
        this.userAccount = userAccount;
    }
    
    /** 用户唯一标识 */
    private String userId;
    
    /** 姓名 */
    private String userName;
    
    /** 职工号或者学号或者临时人员号 */
    private String userAccount;
    
    /** 性别代码 */
    private String userGenderCode;
    
    /** 性别 */
    private String userGender;
    
    /** 用户部门代码 */
    private String userDepartmentCode;
    
    /** 所属部门 */
    private String userDepartment;
    
    /** 专业代码 */
    private String userMajorCode;
    
    /** 专业 */
    private String userMajor;
    
    /** 年级 */
    private String userGrade;
    
    /** 班级代码 */
    private String userClassCode;
    
    /** 班级 */
    private String userClass;
    
    /** 用户头像 */
    private String userAvatar;
    
    /**
     * 用户类型
     * 1:新生
     * 2:在校生
     * 3:校友
     * 4:外聘教职工
     * 5:正式教职工
     */
    private String userType;
    
    /** 用户组信息 */
    private List<WeUserGroup> groups;
    
    /** 用户管理范围信息 */
    private List<WeUserManagedUnit> managedUnits;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public String getUserGenderCode() {
        return userGenderCode;
    }

    public void setUserGenderCode(String userGenderCode) {
        this.userGenderCode = userGenderCode;
    }

    public String getUserGender() {
        return userGender;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }

    public String getUserDepartmentCode() {
        return userDepartmentCode;
    }

    public void setUserDepartmentCode(String userDepartmentCode) {
        this.userDepartmentCode = userDepartmentCode;
    }

    public String getUserDepartment() {
        return userDepartment;
    }

    public void setUserDepartment(String userDepartment) {
        this.userDepartment = userDepartment;
    }

    public String getUserMajorCode() {
        return userMajorCode;
    }

    public void setUserMajorCode(String userMajorCode) {
        this.userMajorCode = userMajorCode;
    }

    public String getUserMajor() {
        return userMajor;
    }

    public void setUserMajor(String userMajor) {
        this.userMajor = userMajor;
    }

    public String getUserGrade() {
        return userGrade;
    }

    public void setUserGrade(String userGrade) {
        this.userGrade = userGrade;
    }

    public String getUserClassCode() {
        return userClassCode;
    }

    public void setUserClassCode(String userClassCode) {
        this.userClassCode = userClassCode;
    }

    public String getUserClass() {
        return userClass;
    }

    public void setUserClass(String userClass) {
        this.userClass = userClass;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getUserType() {
        if("1".equals(userType) || "2".equals(userType)) {
            return "student";
        } else if("4".equals(userType) || "5".equals(userType)) {
            return "teacher";
        } else {
            return "other";
        }
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public List<WeUserGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<WeUserGroup> groups) {
        this.groups = groups;
    }

    public List<WeUserManagedUnit> getManagedUnits() {
        return managedUnits;
    }

    public void setManagedUnits(List<WeUserManagedUnit> managedUnits) {
        this.managedUnits = managedUnits;
    }
}
