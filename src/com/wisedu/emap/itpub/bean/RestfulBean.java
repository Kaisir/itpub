/*
 * @Title: RestfulBean.java 
 * @Package com.wisedu.emap.wdyktzd.bean 
 * @author wjfu 01116035
 */
package com.wisedu.emap.itpub.bean;

import java.util.List;

import org.apache.http.Header;

import com.google.common.collect.Lists;

/** 
 * restful的数据类
 * @ClassName: RestfulBean 
 * @author wjfu 01116035
 * @date 2016年3月21日 下午3:18:54
 */
public class RestfulBean {
    
    /**
     * 请求的地址
     */
    private String url;
    
    /**
     * 请求的所带请求头
     */
    private List<Header> headers = Lists.newArrayList();

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<Header> getHeaders() {
        return headers;
    }

    public void setHeaders(List<Header> headers) {
        this.headers = headers;
    }
    
}
