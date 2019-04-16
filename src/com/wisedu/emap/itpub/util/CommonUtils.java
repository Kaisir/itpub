package com.wisedu.emap.itpub.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wisedu.emap.base.util.ListUtil;
import com.wisedu.emap.base.util.LogUtil;
import com.wisedu.emap.base.util.MapUtil;
import com.wisedu.emap.base.util.StringUtil;
import com.wisedu.emap.dao.DaoParam;
import com.wisedu.emap.itpub.util.exception.PubExceptionUtil;
import com.wisedu.emap.model2.QueryResult;

/**
 * 
 *  类名称：CommonUtils 
 * 
 * @version 1.0
 */
public abstract class CommonUtils {
	private static String param_err_msg="参数字符串解析失败";
    /** 日志 **/
    protected static final org.slf4j.Logger LOGGER = LogUtil.getOnlineLog();

    /**
     * 获取用户真实IP地址，不使用request.getRemoteAddr();的原因是有可能用户使用了代理软件方式避免真实IP地址,
     * 
     * 可是，如果通过了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP值，究竟哪个才是真正的用户端的真实IP呢？
     * 答案是取X-Forwarded-For中第一个非unknown的有效IP字符串。
     * 
     * 如：X-Forwarded-For：192.168.1.110, 192.168.1.120, 192.168.1.130,
     * 192.168.1.100
     * 
     * 用户真实IP为： 192.168.1.110
     * 
     * @param request
     * @return
     */
    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 
     * [简要描述]：从字符串参数中解析出DaoParam [详细描述]：该方法适用于不确定参数列表时使用 [重要提示]：paramString
     * 必须为标准的JSON字符串
     * 
     * @param paramString
     *            JSON字符串
     * @param mustNotEmpty
     *            不能为空 true不能为空，false可以为空
     * @return DaoParam 数据库对象
     * @throws Exception
     *             内部异常
     */
    public static DaoParam buildParams(String paramString, boolean mustNotEmpty) throws Exception {
        DaoParam daoParam = new DaoParam();

        try {
            if (StringUtils.isEmpty(paramString) && mustNotEmpty) {
                PubExceptionUtil.throwParamsException(param_err_msg, null);
            }
            else if (StringUtils.isEmpty(paramString) && !mustNotEmpty) {
                return daoParam;
            }

            JsonParser jp = new JsonParser();
            JsonElement je = jp.parse(paramString);
            JsonObject jo = je.getAsJsonObject();
            return json2DaoParam(jo);
        }
        catch (Exception e) {
        	PubExceptionUtil.throwParamsException(param_err_msg, e);
        }
		return daoParam;
    }

    /**
     * 
     * [简要描述]：从字符串参数中解析出DaoParam的集合 [详细描述]：该方法适用于不确定参数列表时使用 [重要提示]：paramString
     * 必须为标准的JSON数组字符串
     * 
     * @param paramString
     *            JSON字符串
     * @param mustNotEmpty
     *            不能为空 true不能为空，false可以为空
     * @return List<DaoParam> 数据库对象
     * @throws Exception
     *             内部异常
     */
    public static List<DaoParam> buildParamList(String paramString, boolean mustNotEmpty) throws Exception {
        List<DaoParam> daoParamList = new ArrayList<DaoParam>();

        try {
            if (StringUtils.isEmpty(paramString) && mustNotEmpty) {
            	PubExceptionUtil.throwParamsException(param_err_msg, null);
            }
            else if (StringUtils.isEmpty(paramString) && !mustNotEmpty) {
                return daoParamList;
            }

            JsonParser jp = new JsonParser();
            JsonElement je = jp.parse(paramString);

            if (!je.isJsonArray()) {
                LOGGER.info("[" + paramString + "] is not a json array.");
            }
            else {
                JsonArray array = je.getAsJsonArray();

                for (JsonElement element : array) {
                    DaoParam daoParam = json2DaoParam(element.getAsJsonObject());
                    daoParamList.add(daoParam);
                }
            }
        }
        catch (Exception e) {
        	PubExceptionUtil.throwParamsException(param_err_msg, e);
        }

        return daoParamList;
    }

    /**
     * 
     * [简要描述]：从字符串参数中解析出Map的集合 [详细描述]： [重要提示]：paramString 必须为标准的JSON数组字符串
     * 
     * @param paramString
     *            JSON字符串
     * @param mustNotEmpty
     *            不能为空 true不能为空，false可以为空
     * @return List<Map<String,Object>> 数据库对象
     * @throws Exception
     *             内部异常
     */
    public static List<Map<String, Object>> buildMapList(String paramString, boolean mustNotEmpty) throws Exception {
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();

        try {
            if (StringUtils.isEmpty(paramString) && mustNotEmpty) {
            	PubExceptionUtil.throwParamsException(param_err_msg, null);
            }
            else if (StringUtils.isEmpty(paramString) && !mustNotEmpty) {
                return resultList;
            }

            JsonParser jp = new JsonParser();
            JsonElement je = jp.parse(paramString);

            if (!je.isJsonArray()) {
                LOGGER.info("[" + paramString + "] is not a json array.");
            }
            else {
                JsonArray array = je.getAsJsonArray();

                for (JsonElement element : array) {
                    resultList.add(build(element.getAsJsonObject()));
                }
            }
        }
        catch (Exception e) {
        	PubExceptionUtil.throwParamsException(param_err_msg, e);
        }

        return resultList;
    }

    /**
     * 
     * [简要描述]：把一个JsonObject转成DaoParam</br> [详细描述]：
     * 
     * @param jo
     *            json对象
     * @return dao对象
     */
    public static DaoParam json2DaoParam(JsonObject jo) {
        DaoParam daoParam = new DaoParam();
        Set<Entry<String, JsonElement>> set = jo.entrySet();
        for (Entry<String, JsonElement> ssEntry : set) {
            if (StringUtils.contains(ssEntry.getKey(), "*order")) {
                daoParam.setOrderColumns(ssEntry.getValue().getAsString());
                continue;
            }
            if (!ssEntry.getValue().isJsonNull()) {
                daoParam.addParam(ssEntry.getKey(), ssEntry.getValue().getAsString());
            }
        }
        return daoParam;
    }

    /**
     * 
     * [简要描述]：从字符串参数中解析出Map [详细描述]：支持JSON复合结构 [重要提示]：paramString 必须为标准的JSON字符串
     * 
     * @param paramString
     *            JSON字符串
     * @param mustNotEmpty
     *            不能为空 true不能为空，false可以为空
     * @return Map
     * @throws Exception
     *             内部异常
     */
    public static Map<String, Object> buildMapParams(String paramString, boolean mustNotEmpty) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();

        try {
            if (StringUtils.isEmpty(paramString) && mustNotEmpty) {
            	PubExceptionUtil.throwParamsException(param_err_msg, null);
            }
            else if (StringUtils.isEmpty(paramString) && !mustNotEmpty) {
                return map;
            }

            JsonParser jp = new JsonParser();
            JsonElement je = jp.parse(paramString);
            JsonObject jo = je.getAsJsonObject();

            return build(jo);
        }
        catch (Exception e) {
        	PubExceptionUtil.throwParamsException(param_err_msg, e);
        }
		return map;
    }
    /**
     * 
     * [简要描述]：递归解析参数字符串为map</br> [详细描述]：支持复合结构包括JSON数组
     * 
     * @param jo
     *            JsonObject
     * @return map
     */
    public static Map<String, Object> build(JsonObject jo) {
        Map<String, Object> map = new HashMap<String, Object>();
        Set<Entry<String, JsonElement>> set = jo.entrySet();
        for (Entry<String, JsonElement> ssEntry : set) {
            String key = ssEntry.getKey();
            JsonElement element = ssEntry.getValue();

            // 判断是不是复合结构
            if (element.isJsonObject()) {
                map.put(key, build(element.getAsJsonObject()));
            }
            // 如果是数组
            else if (element.isJsonArray()) {
                JsonArray array = element.getAsJsonArray();
                List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
                for (JsonElement j : array) {
                    list.add(build(j.getAsJsonObject()));
                }

                map.put(key, list);
            }
            else {
                String str = "";
                // 简单类型
                if (!element.isJsonNull()) {// 增加null判断
                    str = element.getAsString();
                }
                map.put(key, str);
            }
        }

        return map;
    }

    /**
     * 
     * [简要描述]： 判断emap查询的结果集是否为空</br> [详细描述]：
     * 
     * @param queryResult
     * @return true为空 false不为空
     */
    public static boolean isQueryResultEmpty(QueryResult<?> queryResult) {
        return queryResult == null || queryResult.isEmpty();
    }

    /**
     * [简要描述]：将得到的数据集合转化成JSON格式 [详细描述]：
     * 
     * @param infosMaps
     * @return JsonArray数据集合
     */
    public static JsonArray buildResult(QueryResult<Map<String, Object>> infosMaps) throws Exception {
        JsonArray result = new JsonArray();
        try {
            if (ListUtil.isEmpty(infosMaps)) {
                return result;
            }
            for (Map<String, Object> infosMap : infosMaps) {
                result.add(map2Json(infosMap));
            }
        }
        catch (Exception e) {
        	PubExceptionUtil.throwParamsException(param_err_msg, e);
        }
        return result;
    }

    /**
     * [简要描述]：将map转化成json元素 [详细描述]：
     * 
     * @param map
     * @return json元素
     */
    public static JsonElement map2Json(Map<?, ?> map) {
        Gson gson = new Gson();
        String str = gson.toJson(map);
        JsonParser jsonParser = new JsonParser();
        return jsonParser.parse(str);
    }

    /**
     * [简要描述]：将map转换为daoparam [详细描述]：数据格式转换类
     * 
     * @author 01116077
     * @param Map
     *            <String,Object> paramMap
     * @param removeKeyList
     *            要清除掉的key
     * @return DaoParam 时间：2016-11-5 11:11:19
     */
    public static DaoParam mapBuildDaoParam(Map<String, Object> paramMap, String... removeKeys) {
        DaoParam daoParam = new DaoParam();
        // 非空map
        if (MapUtil.isNotEmpty(paramMap)) {
            for (String key : paramMap.keySet()) {
                if (!ArrayUtils.contains(removeKeys, key)) {
                    daoParam.addParam(key, StringUtil.getString(paramMap.get(key)));
                }
            }
        }
        return daoParam;
    }

    public static DaoParam mapBuildDaoParam(Map<String, Object> paramMap) {
        DaoParam daoParam = new DaoParam();
        // 非空map
        if (MapUtil.isNotEmpty(paramMap)) {
            for (String key : paramMap.keySet()) {
                daoParam.addParam(key, StringUtil.getString(paramMap.get(key)));
            }
        }
        return daoParam;
    }

    /**
     * 
     * [简要描述]：将JSON字符串转换成bean</br> [详细描述]：
     * 
     * @param paramString
     *            待转换的JSON字符串
     * @param mustNotEmpty
     *            必须不为空 true不允许为空，false允许为空
     * @param type
     *            javaBean的类
     * @return javaBean
     * @throws Exception
     */
    public static <T> T jsonStr2Bean(String paramString, boolean mustNotEmpty, Class<T> type) throws Exception {
        try {
            if (StringUtils.isEmpty(paramString) && mustNotEmpty) {
            	PubExceptionUtil.throwParamsException(param_err_msg, null);
            }
            else if (StringUtils.isEmpty(paramString) && !mustNotEmpty) {
                return type.newInstance();
            }

            JsonParser jp = new JsonParser();
            JsonElement je = jp.parse(paramString);
            JsonObject jo = je.getAsJsonObject();

            Gson gson = new Gson();
            return gson.fromJson(jo, type);
        }
        catch (Exception e) {
        	PubExceptionUtil.throwParamsException(param_err_msg, e);
        }
        return null;
    }

    /**
     * 
     * [简要描述]：将JSON字符串转换成JSON对象</br> [详细描述]：
     * 
     * @param jsonString
     *            json字符串
     * @return JSON对象
     * @throws Exception
     */
    public static JsonObject buildJsonObject(String jsonString) throws Exception {
        try {
            JsonParser jp = new JsonParser();
            JsonElement je = jp.parse(jsonString);
            return je.getAsJsonObject();
        }
        catch (Exception e) {
        	PubExceptionUtil.throwParamsException(param_err_msg, e);
        }
        return null;
    }

    /**
     * 
     * [简要描述]：转换object到JSON [详细描述]：
     * 
     * @param object
     *            简单java对象
     * @return JsonElement 为jsonObject
     * @throws Exception
     */
    public static JsonElement build(Object object) throws Exception {
        try {
            if (null == object) {
                return new JsonObject();
            }

            Gson gson = new Gson();
            String jsonStr = gson.toJson(object);
            JsonParser parser = new JsonParser();
            return parser.parse(jsonStr);
        }
        catch (Exception e) {
        	PubExceptionUtil.throwOtherException("转换JSON对象失败", e);
        }
        return new JsonObject();
    }

    /**
     * 
     * [简要描述]：转换java对象到JSON对象</br> [详细描述]：支持复杂结构
     * 
     * @param object
     *            简单/复杂java对象
     * @return JsonElement json对象
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
    public static JsonElement object2JsonObject(Object object) throws Exception {
        try {
            if (null == object) {
                return new JsonObject();
            }

            // 集合转换成JsonArray
            if (object instanceof List) {
                List list = (List) object;

                JsonArray array = new JsonArray();
                for (Object o : list) {
                    array.add(build(o));
                }
                return array;
            }
            // 集合转换成JsonArray
            else if (object instanceof Collection) {
                Collection coll = (Collection) object;

                JsonArray array = new JsonArray();
                for (Object o : coll) {
                    array.add(build(o));
                }
                return array;
            }
            return build(object);
        }
        catch (Exception e) {
        	PubExceptionUtil.throwOtherException("转换JSON对象失败", e);
        }
        return new JsonObject();
    }

    /**
     * 
     * [简要描述]：根据给定的key和集合构造Json数组</br>
     * [详细描述]：返回示例：[{key:val1},{key:val2},{key:val3}...}]
     * 
     * @param collection
     *            集合
     * @param key
     *            每一项的key
     * @return JsonArray
     * @throws Exception
     */
    public static JsonArray buildJsonArrayFromList(Collection<String> collection, String key) {
        try {
            JsonArray result = new JsonArray();
            if (CollectionUtils.isEmpty(collection)) {
                return result;
            }

            JsonObject jo = null;
            for (String value : collection) {
                jo = new JsonObject();
                jo.addProperty(key, value);
                result.add(jo);
            }

            return result;
        }
        catch (Exception e) {
        	PubExceptionUtil.throwOtherException("转换JSON对象失败", e);
        }
        return new JsonArray();
    }


    /**
     * 
     * [简要描述]：将QueryResult转换为List [详细描述]：
     * 由于QueryResult的遍历存在各种问题，所以建议将QueryResult转换为list后使用
     * 
     * @param result
     * @return
     */
    public static List<Map<String, Object>> queryResult2List(QueryResult<Map<String, Object>> result) throws Exception {
        // 由于QueryResult的遍历存在问题，所以使用list
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        if (ListUtil.isEmpty(result)) {
            return resultList;
        }
        for (Map<String, Object> map : result) {
            Map<String, Object> tmpMap = new HashMap<String, Object>();
            tmpMap.putAll(map);
            resultList.add(tmpMap);
        }
        return resultList;
    }

    /**
    * [简要描述]：将得到的数据集合转化成JSON格式 [详细描述]：
    * 
    * @param infosMaps
    * @return JsonArray数据集合
    */
    public static JsonArray buildResult(List<Map<String, Object>> infosMaps) throws Exception {
        JsonArray result = new JsonArray();
        if (ListUtil.isEmpty(infosMaps)) {
            return result;
        }
        for (Map<String, Object> infosMap : infosMaps) {
            result.add(map2Json(infosMap));
        }
        return result;
    }
}