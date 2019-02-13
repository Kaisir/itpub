/*
 * @Project itservicecommon
 * @Package com.wisedu.emap.it.bean
 * @date 2016年5月13日 上午10:03:04
 * @author wjfu 01116035
 */
package com.wisedu.emap.itpub.bean.search;

/**
 * 用户查询的条件
 * @filename UserSearchCond.java
 * @date 2016年5月13日 上午10:03:04
 * @author wjfu 01116035
 */
public class UserSearchCond {
    
    public UserSearchCond() {
    }
    
    public UserSearchCond(String searchBy, String searchCondition,
            String searchContent, String searchJoin) {
        this.searchBy = searchBy;
        this.searchCondition = searchCondition;
        this.searchContent = searchContent;
        this.searchJoin = searchJoin;
    }
    
    /**
     * 查询字段
     * name(人员姓名) 
     * sexCode(性别代码) 
     * sexName(性别名称) 
     * grade(年级) 
     * className(班级名称)
     * classCode(班级代码) 
     * majorCode(专业代码) 
     * majorName(专业名称) 
     * schoolCode(学院代码) 
     * schoolName(学院名称)
     * id(Zgh 或者 xh) 
     * groupId(组id) 
     * domainId(域id) 
     * type(人员类别 1学生 2 老师 此项对应searchCondition 只能是equal)
     */
    private String searchBy;
    
    /**
     * 查询类型 
     * like(like) equal(=) notEqual(!=) greater(>)
     * greaterOrEqual(>=) lessOrEqual(<=) less(<) in(in)
     */
    private String searchCondition;
    
    /**
     * 对应查询字段的查询内容
     */
    private String searchContent;
    
    /**
     * 字段查询连接方式 (or And)
     */
    private String searchJoin;
    
    public String getSearchBy() {
        return searchBy;
    }

    public void setSearchBy(String searchBy) {
        this.searchBy = searchBy;
    }

    public String getSearchCondition() {
        return searchCondition;
    }

    public void setSearchCondition(String searchCondition) {
        this.searchCondition = searchCondition;
    }

    public String getSearchContent() {
        return searchContent;
    }

    public void setSearchContent(String searchContent) {
        this.searchContent = searchContent;
    }

    public String getSearchJoin() {
        return searchJoin;
    }

    public void setSearchJoin(String searchJoin) {
        this.searchJoin = searchJoin;
    }
    
}
