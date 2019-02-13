package com.wisedu.emap.itpub.bean.auth;

import com.wisedu.emap.itpub.bean.wec.WecBaseResponse;

/**
 * @filename WecUserResponse.java
 * @date 2017年5月31日 下午8:33:51
 * @author wjfu 01116035
 */
public class WecUserResponse {
    
    private WeUser userInfo;
    
    private WecBaseResponse baseResponse;

    public WeUser getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(WeUser userInfo) {
        this.userInfo = userInfo;
    }

    public WecBaseResponse getBaseResponse() {
        return baseResponse;
    }

    public void setBaseResponse(WecBaseResponse baseResponse) {
        this.baseResponse = baseResponse;
    }
    
}
