package com.wisedu.emap.itpub.mod.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import self.micromagic.eterna.digester2.ConfigResource;
import self.micromagic.util.annotation.Config;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wisedu.emap.auth.AuthTool;
import com.wisedu.emap.auth.IAuthManager;
import com.wisedu.emap.auth.IRole;
import com.wisedu.emap.auth.IUser;
import com.wisedu.emap.auth.RoleType;
import com.wisedu.emap.base.util.JSONUtil;
import com.wisedu.emap.base.util.ListUtil;
import com.wisedu.emap.base.util.LogUtil;
import com.wisedu.emap.base.util.StringUtil;
import com.wisedu.emap.dao.DaoParam;
import com.wisedu.emap.emapAuth.service.obj.EmapUser;
import com.wisedu.emap.funauthapp.bean.GetAppConfigBean;
import com.wisedu.emap.funauthapp.service.AppGroupRelService;
import com.wisedu.emap.itpub.dao.ItDaoExcuter;
import com.wisedu.emap.itpub.mod.common.MobCommonConstant;
import com.wisedu.emap.itpub.mod.service.IMobileCommonService;
import com.wisedu.emap.itpub.service.ICommonService;
import com.wisedu.emap.itpub.service.wechart.IWechatService;
import com.wisedu.emap.itpub.util.ComJsonUtil;
import com.wisedu.emap.itpub.util.CommonUtils;
import com.wisedu.emap.itpub.util.exception.PubExceptionUtil;
import com.wisedu.emap.model2.QueryResult;
import com.wisedu.emap.model2.container.RoleContainer;
import com.wisedu.emap.mvc.CurrentThread;
import com.wisedu.emap.pedestal.app.IEmapApp;
import com.wisedu.emap.pedestal.app.IEmapAppContext;
import com.wisedu.emap.pedestal.core.AppManager;

@Service(IMobileCommonService.BEANID)
public class MobileCommonServiceImpl implements IMobileCommonService
{
    @Autowired
    IEmapAppContext appContext;

    @Autowired
    ItDaoExcuter daoExcuter;

    @Autowired
    private IWechatService wechatService;

    @Autowired
    private ICommonService appGroupRelService;
   
    @Autowired
    private AppGroupRelService appGroupRelService2;
    protected static org.slf4j.Logger logger = LogUtil.getOnlineLog();

    /** 系统参数:是否需要角色选择页 */
    @Config(name = "mobile.selectrole.manage", defaultValue = "", description = "是否需要角色选择页 ")
    private static String isNeedSelectRole;


    /**
     * 获取登陆角色有权限的菜单信息
     * @param collection
     * @return
     */
    public JsonObject getMenuInfo(HttpServletRequest request) throws Exception
    {
        try
        {
            //1.接收参数及校验
            IUser user = CurrentThread.getUser();
            String userId = user.getId();
            Map<String, Object> paramsMap = CommonUtils.buildMapParams(ComJsonUtil.readJSONString(request), true);
            String appId = StringUtil.getString(paramsMap.get("APPID"));
            String appName = StringUtil.getString(paramsMap.get("APPNAME"));
            validateParam(request);

            //2.读取注册页面文件pageRegister.xml信息
            JsonObject resultJsonObject = new JsonObject();
            Set<String> gnbsSet = new LinkedHashSet<String>();
            List<Map<String, Object>> pageList = readRegisterInfo(appName);
            if (StringUtil.isEmpty(userId))
            {//游客返回
                gnbsSet.add("EXCLUDE");
                resultJsonObject.add("PAGES", buildRoutesArray(gnbsSet, pageList));
                return resultJsonObject;
            }

            //3.设置默认角色
            Map<String, Object> resultMap = setDefaultRoleFunc(userId, appId, appName);
            String roleId = StringUtil.getString(resultMap.get("IDENTITY_TYPE"));

            //4.根据默认角色查询菜单
            List<String> menuList = getMenuInfoByRoleId(appId, appName, userId, roleId);
            gnbsSet.addAll(menuList);

            //5.获取按钮权限
            List<String> buttonList = getAuthButtonInfo(appName, roleId);

            //6.组装最后返回的数据
            resultJsonObject.addProperty("ROLETYPE", roleId);
            resultJsonObject.add("MENU", buildJsonArrayFromList(gnbsSet, "GNBS"));
            resultJsonObject.add("BUTTON", JSONUtil.parseString2JSONArray(buttonList.toString()));
            gnbsSet.addAll(buttonList);
            resultJsonObject.add("PAGES", buildRoutesArray(gnbsSet, pageList));

            return resultJsonObject;
        }
        catch (Exception e)
        {
            throw e;
        }
    }

    /**
     * 获取用户角色配置的菜单信息
     * @param appId
     * @param appName
     * @param userId
     * @param roleId
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public List<String> getMenuInfoByRoleId(String appId, String appName, String userId, String roleId)
            throws Exception
    {

        GetAppConfigBean infoBean = new GetAppConfigBean();
        infoBean.setAppId(appId);
        infoBean.setAppName(appName);
        infoBean.setGnfw("MOBILE");
        infoBean.setUserId(userId);
        infoBean.setYwy("IT");
        infoBean.setRoleId(roleId);

        // 获取功能菜单
        Map<String, Object> configMap = appGroupRelService2.getMenuInfo(infoBean);
        List<String> gnbsListTemp = getGnbsList((List<Map<String, Object>>) configMap.get("MODULES"));
        return gnbsListTemp;
    }
    /**
     * 设置登陆用户的当前角色
     * @param request
     * @return
     * @throws Exception
     */
    public void setAppRole(HttpServletRequest request) throws Exception
    {
        IUser user = CurrentThread.getUser();
        Map<String, Object> paramsMap = CommonUtils.buildMapParams(ComJsonUtil.readJSONString(request), true);
        String appName = StringUtil.getString(paramsMap.get("APPNAME"));
        String roleId = StringUtil.getString(paramsMap.get("ROLEID"));
        EmapUser.setAppRole(user, appName, roleId);
        EmapUser.setAppRole(user, "emapcomponent", roleId);
        EmapUser.setAppRole(user, "itpub", roleId);

    }

    /**
     * 获取系统参数:是否需要角色选择页
     * 如果用户有且只有一个角色，直接跳转角色配置的页面
     * @param request
     * @return
     * @throws Exception
     */
    public JsonObject getSelRoleConfig(HttpServletRequest request) throws Exception
    {
        JsonObject resultJsonObject = new JsonObject();
        IUser user = CurrentThread.getUser();
        String userId = user.getUserId();
        Map<String, Object> paramsMap = CommonUtils.buildMapParams(ComJsonUtil.readJSONString(request), true);
        String appId = StringUtil.getString(paramsMap.get("APPID"));
        //如果用户有且只有一个角色，直接跳转角色配置的页面
        List<Map<String, Object>> groupList = appGroupRelService.getUserGroupsWithName(userId,appId, null);
        if (CollectionUtils.isEmpty(groupList))
        {
            PubExceptionUtil.throwBusinessException("未获取到角色，无权操作");
        }

        if (groupList.size() == 1)
        {
            resultJsonObject.addProperty("IS_NEED_SELECTROLE", "0");
            resultJsonObject.addProperty("DEFAULT_ROLEID", StringUtil.getString(groupList.get(0).get("id")));
        }
        else
        {
            resultJsonObject.addProperty("IS_NEED_SELECTROLE", isNeedSelectRole);
        }

        return resultJsonObject;

    }

    /**
     * 获取登陆用户所有的角色
     * @param request
     * @return
     * @throws Exception
     */
    public JsonObject getUserRoles(HttpServletRequest request) throws Exception
    {
        JsonArray jsonArr = new JsonArray();
        JsonObject resultJsonObject = new JsonObject();
        IUser user = CurrentThread.getUser();
        String userId = user.getUserId();
        Map<String, Object> paramsMap = CommonUtils.buildMapParams(ComJsonUtil.readJSONString(request), true);
        String appId = StringUtil.getString(paramsMap.get("APPID"));
        // 调用funauthapp的查询组件，查询用户在当前应用中的所有用户组
        List<Map<String, Object>> groupList = appGroupRelService.getUserGroupsWithName(userId,appId, null);
        if (!CollectionUtils.isEmpty(groupList))
        {
            for (Map<String, Object> tmpMap : groupList)
            {
                jsonArr.add(JSONUtil.toJSON(tmpMap));
            }
        }
        resultJsonObject.add("ROLELIST", jsonArr);
        return resultJsonObject;

    }

    /**
     * 参数校验
     * @param request
     */
    public void validateParam(HttpServletRequest request) throws Exception
    {

        IUser user = CurrentThread.getUser();
        String userId = user.getId();
        Map<String, Object> paramsMap = CommonUtils.buildMapParams(ComJsonUtil.readJSONString(request), true);
        String appId = StringUtil.getString(paramsMap.get("APPID"));
        String appName = StringUtil.getString(paramsMap.get("APPNAME"));

        if (StringUtils.isEmpty(userId))
        {
            PubExceptionUtil.throwParamsException("用户失效，请刷新或重新登陆");
        }
        if (StringUtils.isEmpty(appId) || StringUtils.isEmpty(appName))
        {
            PubExceptionUtil.throwParamsException("参数错误，获取APPID或APPNAME为空");
        }
    }

    /**
     * 读取注册页面文件pageRegister.xml信息
     * @param appName
     * @return
     */
    public List<Map<String, Object>> readRegisterInfo(String appName) throws Exception
    {

        List<Map<String, Object>> pageList = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> extendPageList = new ArrayList<Map<String, Object>>();

        //读取原应用文件
        String path = AppManager.getAppRootDir().getAbsolutePath() + File.separator + appName + File.separator
                + "config" + File.separator + MobCommonConstant.PAGE_REGISTER;
        File file = new File(path);
        if (!file.exists())
        {
            PubExceptionUtil.throwBusinessException("注册页面文件pageRegister.xml在config目录未找到");
        }

        InputStream fileInputStream = new FileInputStream(file);

        pageList = getPageList(fileInputStream);

        //尝试读取二开扩展文件
        String extendPath = "config" + File.separator + MobCommonConstant.PAGE_REGISTER_EXTEND;
        IEmapApp emapApp = AppManager.getInstance().getApp(appName);
        ConfigResource resource = emapApp.getResource(extendPath);
        if (resource != null)
        {
            fileInputStream = resource.getAsStream();
            if (fileInputStream != null)
            {
                extendPageList = getPageList(fileInputStream);
            }
        }

        return pageListMerge(pageList, extendPageList);
    }

    /**
     * 
     * [简要描述]：解析页面注册文件
     *
     * @param inputStream
     * @return
     * @throws Exception
     */
    private List<Map<String, Object>> getPageList(InputStream inputStream) throws Exception
    {
        List<Map<String, Object>> pageList = new ArrayList<Map<String, Object>>();
        Map<String, Object> nodeMap = new HashMap<String, Object>();
        String gnbs = "";
        try
        {
            //创建SAXReader对象
            SAXReader reader = new SAXReader();
            //读取文件 转换成Document
            Document document = reader.read(inputStream);
            //获取根节点元素对象
            Element root = document.getRootElement();
            //遍历当前节点下的所有节点
            listNodes(root, pageList, nodeMap, gnbs);

        }
        catch (Exception e)
        {
            PubExceptionUtil.throwBusinessException("解析pageRegister异常", e);
        }
        finally
        {
            if (inputStream != null)
            {
                inputStream.close();
            }
        }
        return pageList;
    }

    /**
     * 
     * [简要描述]：合并二开扩展的页面注册文件
     *
     * @param pageList
     * @param extendPagelList
     * @return
     */
    private List<Map<String, Object>> pageListMerge(List<Map<String, Object>> pageList,
            List<Map<String, Object>> extendPagelList)
    {
        List<Map<String, Object>> mergeList = new ArrayList<Map<String, Object>>();
        if (ListUtil.isEmpty(extendPagelList))
        {
            mergeList = pageList;
        }
        else
        {
            //获取扩展文件中的GNBS集合
            Set<String> gnbsSet = new HashSet<String>();
            for (Map<String, Object> page : extendPagelList)
            {
                gnbsSet.add(StringUtil.getString(page.get("GNBS")));
            }

            for (Map<String, Object> page : pageList)
            {
                if (!gnbsSet.contains(page.get("GNBS")))
                {
                    extendPagelList.add(page);
                }
            }
            mergeList = extendPagelList;
        }

        return mergeList;
    }

    /**
     * 遍历当前节点下的所有节点
     * @param node
     */
    @SuppressWarnings("unchecked")
    public void listNodes(Element node, List<Map<String, Object>> resultList, Map<String, Object> resultMap, String gnbs)
    {

        //遍历节点里所有属性
        if ("permission".equals(node.getName()))
        {
            List<Attribute> list = node.attributes();
            for (Attribute attribute : list)
            {
                if ("GNBS".equals(attribute.getName()))
                {
                    gnbs = attribute.getValue();
                }
            }
        }
        if ("exclude".equals(node.getName()))
        {
            //游客自定义功能标识
            gnbs = "EXCLUDE";
        }
        if ("mobilepage".equals(node.getName()))
        {
            List<Attribute> list = node.attributes();
            for (Attribute attribute : list)
            {
                resultMap.put(attribute.getName(), attribute.getValue());
            }
            resultMap.put("GNBS", gnbs);
            resultList.add(resultMap);
        }
        if ("component".equals(node.getName()))
        {
            if (!resultMap.containsKey("components"))
            {
                List<Map<String, Object>> comList = new ArrayList<Map<String, Object>>();
                resultMap.put("components", comList);
            }
            Map<String, Object> comMap = new HashMap<String, Object>();
            List<Attribute> list = node.attributes();
            for (Attribute attribute : list)
            {
                comMap.put(attribute.getName(), attribute.getValue());
            }
            List<Map<String, Object>> comList = (List<Map<String, Object>>) resultMap.get("components");
            comList.add(comMap);
        }

        //迭代当前节点下面的所有子节点,使用递归
        Iterator<Element> iterator = node.elementIterator();
        while (iterator.hasNext())
        {
            Element e = iterator.next();
            if ("mobilepage".equals(e.getName()))
            {
                Map<String, Object> nodeMap = new HashMap<String, Object>();
                listNodes(e, resultList, nodeMap, gnbs);
            }
            else
            {
                listNodes(e, resultList, resultMap, gnbs);
            }
        }
    }

    /**
     * 设置默认角色
     * @param domain
     * @param defaultRoleId
     * @return
     */
    public Map<String, Object> setDefaultRoleFunc(String userId, String appId, String appName)
            throws Exception
    {

        Map<String, Object> resultMap = new HashMap<String, Object>();

        //存在角色直接返回
        IUser iuser = AuthTool.currentUser();
        String roleId = EmapUser.getAppRole(iuser, appName);
        if (!StringUtil.isEmpty(roleId))
        {
            resultMap.put("IDENTITY_TYPE", roleId);
            return resultMap;
        }

        resultMap = setDefaultRole(userId, appId, appName);
        
        return resultMap;
    }

   

    /**
     * 默认规则设置角色，取第一个
     * @param identityId
     * @return
     * @throws Exception
     */
    public Map<String, Object> setDefaultRole(String identityId, String appId, String appName) throws Exception
    {

        Map<String, Object> resultMap = new HashMap<String, Object>();
        IUser user = CurrentThread.getUser();
        String userId = user.getUserId();

        try
        {
            // 调用funauthapp的查询组件，查询用户在当前应用中的所有用户组
            List<Map<String, Object>> groupList = appGroupRelService.getUserGroupsWithName(userId,appId, null);
            if (CollectionUtils.isEmpty(groupList))
            {
                PubExceptionUtil.throwBusinessException("未获取到角色，无权操作");
            }

            //默认设置第一个组角色
            String groupId = StringUtil.getString(groupList.get(0).get("id"));
            EmapUser.setAppRole(user, appName, groupId);
            resultMap.put("IDENTITY_TYPE", groupId);

            return resultMap;
        }
        catch (Exception e)
        {
            throw e;
        }
    }

    /**
     * 处理用户组的数据权限
     * @param groupId
     * @return
     */
    public List<String> handleDataRole(String groupId)
    {

        DaoParam queryroleParam = new DaoParam();
        queryroleParam.addParam("ROLEID", groupId);
        QueryResult<Map<String, Object>> roleResult = daoExcuter.customQuery(appContext, "hqsjjs", queryroleParam);

        if (null != roleResult)
        {
            Map<String, Object> roleMap = roleResult.getFirst();

            if (null != roleMap)
            {
                // 获取出来的数据权限是用逗号分割的
                String dataRoles = StringUtil.getString(roleMap.get("CONTAINSROLES"));
                String[] roleArray = dataRoles.split(",");
                return Arrays.asList(roleArray);
            }
        }

        return new ArrayList<String>();
    }


    /**
     *
     * [简要描述]：获取移动端已授权的按钮
     * [详细描述]：
     *
     * @param appName
     * @param roleId
     * @return
     * @throws Exception
     */
    public List<String> getAuthButtonInfo(String appName, String roleId) throws Exception
    {
        List<String> list = new ArrayList<String>();
        IEmapApp emapApp = AppManager.getInstance().getApp("emapAuth");
        Object bean = emapApp.getSpring().getBean(IAuthManager.BEANID);
        IAuthManager authManager = (IAuthManager) bean;
        IRole roleObj = authManager.getRole(roleId);
        if (roleObj == null)
        {
            return list;
        }
        //获取该角色所有已授权的菜单及按钮id
        Set<String> subRoleSet = roleObj.getSubRoles();
        //获取当前应用所有的菜单及按钮id
        IEmapApp curApp = AppManager.getInstance().getApp(appName);
        RoleContainer[] roleContainer = curApp.getAppContext().getRealRoles();
        //取出移动端所有已授权的按钮id
        for (int i = 0; i < roleContainer.length; i++)
        {
            /*RealRole的类型为功能 并且 在已授权的组里*/
            if (roleContainer[i].getRoleType() == RoleType.FUNC && !roleContainer[i].isMenu()
                    && subRoleSet.contains(roleContainer[i].getRoleId()))
            {
                list.add(StringUtil.getString(roleContainer[i].getAttribute("GNBS")));
            }
        }
        return list;
    }

    /**
     * 从menus中获取出GNBS集合
     * @param list
     * @return
     */
    public List<String> getGnbsList(List<Map<String, Object>> list)
    {

        List<String> resultList = new ArrayList<String>();
        if (CollectionUtils.isEmpty(list))
        {
            return resultList;
        }
        for (Map<String, Object> map : list)
        {
            resultList.add(StringUtil.getString(map.get("route")));
        }
        return resultList;
    }

    /**
     * 获取permission中有权限的菜单GNBS
     * @param collection
     * @param key
     * @return
     */
    public static JsonArray buildJsonArrayFromList(Collection<String> collection, String key)
    {

        JsonArray result = new JsonArray();
        if (CollectionUtils.isEmpty(collection))
        {
            return result;
        }

        JsonObject jo = null;
        for (String value : collection)
        {
            jo = new JsonObject();
            jo.addProperty(key, value);
            result.add(jo);
        }
        return result;
    }

    /**
     * 获取有权限的移动page
     * @param collection
     * @param pages
     * @return
     */
    public static JsonArray buildRoutesArray(Collection<String> collection, List<Map<String, Object>> pageList)
    {

        JsonArray result = new JsonArray();
        if (CollectionUtils.isEmpty(collection) || ListUtil.isEmpty(pageList))
        {
            return result;
        }

        //过滤有权限的功能标识移动页面
        for (String value : collection)
        {
            for (Map<String, Object> tmpMap : pageList)
            {
                if (value.equals(StringUtil.getString(tmpMap.get("GNBS"))))
                {
                    result.add(map2Json(tmpMap));
                }
            }
        }

        //处理childrenIds属性，转换成json数组
        for (JsonElement element : result)
        {
            JsonObject jo = element.getAsJsonObject();
            Set<Entry<String, JsonElement>> set = jo.entrySet();
            for (Entry<String, JsonElement> ssEntry : set)
            {
                if ("childrenIds".equals(ssEntry.getKey()) && ssEntry.getValue() != null)
                {
                    JsonElement je = JSONUtil.toJSON(ssEntry.getValue().getAsString().split(","));
                    ssEntry.setValue(je);
                }
            }
        }

        return result;
    }

    /**
     * map转化成json元素
     * @param map
     * @return
     */
    public static JsonElement map2Json(Map<?, ?> map)
    {

        Gson gson = new Gson();
        String str = gson.toJson(map);
        JsonParser jsonParser = new JsonParser();
        return jsonParser.parse(str);
    }

    /**
     * 获取微信jsapi_ticket票据
     * @param request
     * @return JsonObject
     */
    public JsonObject getWechatSign(HttpServletRequest request) throws Exception
    {
        JsonObject wechatSign = new JsonObject();
        String paramStr = ComJsonUtil.readJSONString(request);
        Map<String, Object> paramMap = CommonUtils.buildMapParams(paramStr, true);
        String url = StringUtil.getString(paramMap.get("url"));
        Map<String, Object> signature = new HashMap<String, Object>();
        try
        {
            signature = wechatService.sign(url);
        }
        catch (Exception e)
        {
            PubExceptionUtil.throwBusinessException("获取微信jsapi_ticket票据失败", e);
        }
        if (null != signature)
        {
            wechatSign.addProperty("jsapi_ticket", StringUtil.getString(signature.get("jsapi_ticket")));
            wechatSign.addProperty("nonceStr", StringUtil.getString(signature.get("noncestr")));
            wechatSign.addProperty("timestamp", StringUtil.getString(signature.get("timestamp")));
            wechatSign.addProperty("signature", StringUtil.getString(signature.get("signature")));
            wechatSign.addProperty("corpId", StringUtil.getString(signature.get("corpid")));
        }
        return wechatSign;
    }

    /**
     * 从微信服务器获取媒体文件保存本地
     * @param request
     * @return JsonObject
     */
    public JsonObject saveFileFromWechat(HttpServletRequest request) throws Exception
    {
        JsonObject fileToken = new JsonObject();
        String mediaId = request.getParameter("serverIds");
        String storeid = request.getParameter("storeid");
        String token = request.getParameter("token");
        String fileName = request.getParameter("fileName");
        
        if (StringUtil.isEmpty(storeid) || StringUtil.isEmpty(mediaId))
        {
            PubExceptionUtil.throwBusinessException("serverIds与storeid不可为空");
        }
        try
        {
            token = wechatService.downloadFile(mediaId, storeid, token, fileName);
        }
        catch (Exception e)
        {
            PubExceptionUtil.throwBusinessException("从微信服务器获取媒体文件保存本地失败", e);
        }
        fileToken.addProperty("token", token);
        return fileToken;
    }

    /**
     * 
     * [简要描述]：获取发布学年字典
     * [详细描述]：    
     *
     * @param request
     * @return
     */
    public JsonElement getDictionaryOfFbxn(HttpServletRequest request) throws Exception
    {
        String paramStr = ComJsonUtil.readJSONString(request);
        Map<String, Object> paramMap = CommonUtils.buildMapParams(paramStr, true);
        String isPre = StringUtil.getString(paramMap.get("IS_PRE"));
        DaoParam daoParam = new DaoParam();
        String actionName = "";
        if (StringUtil.isNotEmpty(isPre))
        {
            daoParam.addParam("FW", 1);
            actionName = "cxsxnzqydfwndxn";
        }
        else
        {
            daoParam.addParam("FW", 6);
            actionName = "cxzdfwndxnxx";
        }
        QueryResult<Map<String, Object>> pdxnQs = daoExcuter.customQuery(appContext, actionName, daoParam);
        return CommonUtils.buildResult(pdxnQs);
    }
}
