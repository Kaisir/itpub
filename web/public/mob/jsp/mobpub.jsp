<%@ page language="java" contentType="text/html; charset=UTF-8" session="false" %>
<%@ page import="java.util.*" %>
<%@ taglib uri="/WEB-INF/etags/emap.tld" prefix="e" %>
<%@page import="com.wisedu.emap.base.core.EmapContext"%>
<%@page import="java.lang.*"%>
<e:call id="itpub.service.randomService" method="getSysTime" var ="randomValue"/>
<% 
    String path = request.getContextPath();
    String appId = com.wisedu.emap.pedestal.core.AppManager.currentApp().getId();
    String appname= com.wisedu.emap.pedestal.core.AppManager.currentApp().getName();
	String SERVER_PATH = com.wisedu.emap.base.core.EmapContext.getStaticResourceRoot();//路径名
	String hostPath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path;
%>
<meta charset="UTF-8">
<meta name="renderer" content="webkit">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
<!-- 用来禁止浏览器缓存，使得每次请求test.html都从服务器下载最新版本 -->
<meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate" />
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="Expires" content="0" />
<meta name="format-detection" content="telephone=yes">

<script>
        var SERVER_PATH = "<%=SERVER_PATH%>";
        var pageMeta = <e:page/> ;
        var contextPath = "<%= request.getContextPath() %>";
        var rootPath = "<%=path%>";
        rootPath = rootPath.replace("http:", window.location.protocol);
        rootPath = rootPath.replace(":80/", "/");
        var hostPath = "<%=hostPath%>";
        hostPath = hostPath.replace("http:", window.location.protocol);
        hostPath = hostPath.replace(":80/", "/");
        window.WIS_CONFIG = {
            ROOT_PATH: rootPath,
            PATH: "<%=path%>",
            APPID: "<%=appId%>",
            APPNAME: "<%=appname%>",
            RES_SERVER: "<%=SERVER_PATH%>",
            HOST_PATH:hostPath
        }
        window.WIS_CONFIG.STATIC_TIMESTAMP = "v";
        var userId = pageMeta.params.userId;
        var roleId = pageMeta.params.roleId;
        window.APP_CONFIG = {};
</script>

<link rel="stylesheet" href="<%=SERVER_PATH%>/bh_components/mobile/1.0.0/bh-lib.min.css">
<link rel="stylesheet" href="<%=SERVER_PATH%>/fe_components/iconfont_mobile/iconfont.css">
<link rel="stylesheet" href="public/css/style.css">
<link rel="stylesheet" href="../../itpub/public/mob/css/mob.css">
<link rel="stylesheet" href="<%=SERVER_PATH%>/fe_components/skins/mint2.0/skin.css">
<link rel="stylesheet" href="<%=SERVER_PATH%>/fe_components/mobile/MINT/style.min.css">
<link rel="stylesheet" href="<%=SERVER_PATH%>/bower_components/cropper/cropper.min.css">
<script src="https://res.wx.qq.com/open/js/jweixin-1.2.0.js?v=${randomValue}"></script>
<script src="<%=SERVER_PATH%>/bower_components/requirejs/require.js?v=${randomValue}"></script>
<script src="../../itpub/public/mob/js/mobutil.js?v=${randomValue}"></script>
<script src="../../itpub/public/mob/js/spriteFrame.js?v=${randomValue}"></script>
<script src="<%=SERVER_PATH%>/bower_components/qrcode.js/qrcode.js?v=${randomValue}"></script>
<script src="<%=SERVER_PATH%>/fe_components/mobile/bh_utils_mobile.js?v=${randomValue}"></script>

