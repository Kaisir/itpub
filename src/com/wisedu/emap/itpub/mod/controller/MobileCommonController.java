package com.wisedu.emap.itpub.mod.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.wisedu.emap.base.util.LogUtil;
import com.wisedu.emap.itpub.mod.service.IMobileCommonService;
import com.wisedu.emap.itpub.util.ResponseUtil;

/**
 * 移动公共控制类
 *
 */
@Controller
@RequestMapping("/sys/itpub/MobileCommon")
public class MobileCommonController
{
    @Autowired
    private IMobileCommonService mobComServer;

    protected static org.slf4j.Logger logger = LogUtil.getOnlineLog();

    /**
     * 设置登陆用户的当前角色
     * @param request
     * @return
     */
    @RequestMapping("setAppRole")
    @ResponseBody
    public JsonObject setAppRole(HttpServletRequest request)
    {
        JsonObject jsonObj = new JsonObject();
        try
        {
            mobComServer.setAppRole(request);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            return ResponseUtil.buildErrorResponse(e.getMessage());
        }
        return ResponseUtil.buildSuccessResponse(jsonObj);
    }

    /**
     * 获取登陆用户所有的角色
     * @param request
     * @return
     */
    @RequestMapping("getUserRoles")
    @ResponseBody
    public JsonObject getUserRoles(HttpServletRequest request)
    {
        JsonObject jsonObj = new JsonObject();
        try
        {
            jsonObj = mobComServer.getUserRoles(request);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            return ResponseUtil.buildErrorResponse(e.getMessage());
        }
        return ResponseUtil.buildSuccessResponse(jsonObj);
    }

    /**
     *  获取系统参数:是否需要角色选择页
     *  如果用户已经登录，即ROLEID不为空，直接跳转角色配置的页面
     *  如果用户有且只有一个角色，直接跳转角色配置的页面
     * @param request
     * @return
     */
    @RequestMapping("getSelRoleConfig")
    @ResponseBody
    public JsonObject getSelRoleConfig(HttpServletRequest request)
    {
        JsonObject jsonObj = new JsonObject();
        try
        {
            jsonObj = mobComServer.getSelRoleConfig(request);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            return ResponseUtil.buildErrorResponse(e.getMessage());
        }
        return ResponseUtil.buildSuccessResponse(jsonObj);
    }

    /**
     * 获取登陆用户有权限的角色菜单
     * @param request
     * @return
     */
    @RequestMapping("getMenuInfo")
    @ResponseBody
    public JsonObject getMenuInfo(HttpServletRequest request)
    {
        JsonObject jsonObj = new JsonObject();
        try
        {
            jsonObj = mobComServer.getMenuInfo(request);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            return ResponseUtil.buildErrorResponse(e.getMessage());
        }
        return ResponseUtil.buildSuccessResponse(jsonObj);
    }
    
    /**
     * 获取微信jsapi_ticket票据
     * @param request
     * @return
     */
    @RequestMapping("getWechatSign")
    @ResponseBody
    public JsonObject getWechatSign(HttpServletRequest request)
    {
        JsonObject jsonObj = new JsonObject();
        try
        {
            jsonObj = mobComServer.getWechatSign(request);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            return ResponseUtil.buildErrorResponse(e.getMessage());
        }
        return ResponseUtil.buildSuccessResponse(jsonObj);
    }
    
    /**
     * 从微信服务器获取媒体文件保存本地
     * @param request
     * @return
     */
    @RequestMapping("saveFileFromWechat")
    @ResponseBody
    public JsonObject saveFileFromWechat(HttpServletRequest request)
    {
        JsonObject jsonObj = new JsonObject();
        try
        {
            jsonObj = mobComServer.saveFileFromWechat(request);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            return ResponseUtil.buildErrorResponse(e.getMessage());
        }
        return ResponseUtil.buildSuccessResponse(jsonObj);
    }
    
    /**
     * 
     * [简要描述]：获取发布学年字典
     * [详细描述]：    
     *
     * @param request
     * @return
     */
    @RequestMapping("getDictionaryOfFbxn")
    @ResponseBody
    public JsonElement getDictionaryOfFbxn(HttpServletRequest request)
    {
        JsonElement je = new JsonObject();
        try
        {
            je = mobComServer.getDictionaryOfFbxn(request);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            return ResponseUtil.buildErrorResponse(e.getMessage());
        }
        return ResponseUtil.buildSuccessDicResponse(je);
    }
}
