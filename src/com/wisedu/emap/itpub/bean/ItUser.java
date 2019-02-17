package com.wisedu.emap.itpub.bean;

/**
 * @filename ITUser.java
 * @date 2016年4月21日 上午11:07:42
 * @author wjfu 01116035
 */
/*
{
      "name": "林争辉",
      "id": "03048",
      "userType": "Teacher",
      "deptName": "大学生心理健康教育中心",
      "deptCode": "000059",
      "phone": "13843838438",
      "birthday": "1933-11-04",
      "nationCode": "04",
      "nationName": "藏族",
      "nativeCode": "542200",
      "nativeName": "山南地区",
      "politicalCode": "01",
      "politicalName": "中国共产党党员",
      "positionLevelCode": null,
      "positionLevelName": null,
      "email": "zh@nothing.com",
      "position": "老大",
      "sexCode": "1",
      "sexName": "男",
      "academyCode": "000007",
      "academyName": "学生工作处、学生工作部",
      "grade": null,
      "className": null,
      "classCode": null,
      "majorName": null,
      "majorCode": null,
      "attachToCode": "000007",
      "clevel": 2
    }
 */
public class ItUser {
    
    /**
     * 用户姓名
     */
    private String name;
    
    /**
     * 用户id
     */
    private String id;
    
    /**
     * 用户类型 Teacher/Student
     */
    private String userType;

    /**
     * 班级
     */
    private String className;

    /**
     * 班级代码
     */
    private String classCode;

    /**
     * 部门
     */
    private String deptName;

    /**
     * 部门代码
     */
    private String deptCode;
    
    /**
     * 性别代码
     */
    private String sexCode;
    
    /**
     * 性别
     */
    private String sexName;
    
    /**
     * 手机号码
     */
    private String phone;
    
    /**
     * email
     */
    private String email;
    
    /**
     * 专业
     */
    private String majorName;
    
    /**
     * 专业code
     */
    private String majorCode;
    
    /**
     * 年级
     */
    private String grade;
    
    /**
     * 职位
     */
    private String position;
    
    /**
     * 隶属部门代码 (一级部门)
     */
    private String academyCode;
    
    /**
     * 隶属部门名称 (一级部门)
     */
    private String academyName;
    
    /**
     * 生日
     */
    private String birthday;
    
    /**
     * 民族编码
     */
    private String nationCode;
    
    /**
     * 民族
     */
    private String nationName;
    
    /**
     * 籍贯编码
     */
    private String nativeCode;
    
    /**
     * 籍贯
     */
    private String nativeName;
    
    /**
     * 政治面貌编码
     */
    private String politicalCode;
    
    /**
     * 政治面貌
     */
    private String politicalName;
    
    /**
     * 党政职务级别编码
     */
    private String positionLevelCode;
    
    /**
     * 党政职务级别编码
     */
    private String positionLevelName;
    
    /**
     * 上级部门代码
     */
    private String attachToCode;
    
    /**
     * 所在部门层次
     */
    private String clevel;
    
    /**
     * 数据类型
     */
    private String dataType;
    
    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getAcademyCode() {
        return academyCode;
    }

    public void setAcademyCode(String academyCode) {
        this.academyCode = academyCode;
    }

    public String getAcademyName() {
        return academyName;
    }

    public void setAcademyName(String academyName) {
        this.academyName = academyName;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getNationCode() {
        return nationCode;
    }

    public void setNationCode(String nationCode) {
        this.nationCode = nationCode;
    }

    public String getNationName() {
        return nationName;
    }

    public void setNationName(String nationName) {
        this.nationName = nationName;
    }

    public String getNativeCode() {
        return nativeCode;
    }

    public void setNativeCode(String nativeCode) {
        this.nativeCode = nativeCode;
    }

    public String getNativeName() {
        return nativeName;
    }

    public void setNativeName(String nativeName) {
        this.nativeName = nativeName;
    }

    public String getPoliticalCode() {
        return politicalCode;
    }

    public void setPoliticalCode(String politicalCode) {
        this.politicalCode = politicalCode;
    }

    public String getPoliticalName() {
        return politicalName;
    }

    public void setPoliticalName(String politicalName) {
        this.politicalName = politicalName;
    }

    public String getPositionLevelCode() {
        return positionLevelCode;
    }

    public void setPositionLevelCode(String positionLevelCode) {
        this.positionLevelCode = positionLevelCode;
    }

    public String getPositionLevelName() {
        return positionLevelName;
    }

    public void setPositionLevelName(String positionLevelName) {
        this.positionLevelName = positionLevelName;
    }

    public String getAttachToCode() {
        return attachToCode;
    }

    public void setAttachToCode(String attachToCode) {
        this.attachToCode = attachToCode;
    }

    public String getClevel() {
        return clevel;
    }

    public void setClevel(String clevel) {
        this.clevel = clevel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getDeptCode() {
        return deptCode;
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    public String getSexCode() {
        return sexCode;
    }

    public void setSexCode(String sexCode) {
        this.sexCode = sexCode;
    }

    public String getSexName() {
        return sexName;
    }

    public void setSexName(String sexName) {
        this.sexName = sexName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMajorName() {
        return majorName;
    }

    public void setMajorName(String majorName) {
        this.majorName = majorName;
    }

    public String getMajorCode() {
        return majorCode;
    }

    public void setMajorCode(String majorCode) {
        this.majorCode = majorCode;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    @Override
    public String toString() {
        return "ItUser [name=" + name + ", id=" + id + ", userType=" + userType
                + ", className=" + className + ", classCode=" + classCode
                + ", deptName=" + deptName + ", deptCode=" + deptCode
                + ", sexCode=" + sexCode + ", sexName=" + sexName + ", phone="
                + phone + ", email=" + email + ", majorName=" + majorName
                + ", majorCode=" + majorCode + ", grade=" + grade
                + ", dataType=" + dataType + "]";
    }
    
    

}
