<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" session="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<script type="text/javascript">
    window.zhuge = window.zhuge || [];
    window.zhuge.methods = "_init debug identify track trackLink trackForm page".split(" ");
    window.zhuge.factory = function(b) {
        return function() {
            var a = Array.prototype.slice.call(arguments);
            a.unshift(b);
            window.zhuge.push(a);
            return window.zhuge;
        }
    };
    for (var i = 0; i < window.zhuge.methods.length; i++) {
        var key = window.zhuge.methods[i];
        window.zhuge[key] = window.zhuge.factory(key);
    }
    window.zhuge.load = function(b, x) {
        if (!document.getElementById("zhuge-js")) {
            var a = document.createElement("script");
            var verDate = new Date();
            var verStr = verDate.getFullYear().toString()
                + verDate.getMonth().toString() + verDate.getDate().toString();
    
            a.type = "text/javascript";
            a.id = "zhuge-js";
            a.async = !0;
            a.src = (location.protocol == 'http:' ? "http://sdk.zhugeio.com/zhuge.min.js?v=" : 'https://zgsdk.zhugeio.com/zhuge.min.js?v=') + verStr;
             a.onerror = function(){  
             window.zhuge.identify = window.zhuge.track = function(ename, props, callback){                   
               if(callback && Object.prototype.toString.call(callback) === '[object Function]')callback();               
                };     
            };
            var c = document.getElementsByTagName("script")[0];
            c.parentNode.insertBefore(a, c);
            window.zhuge._init(b, x)
        }
    };
    window.zhuge.load('${statisticId}', {debug: ${zhugeDebug}});
    // 记录登陆人信息
    zhuge.identify(USERID, { 
        gender: USER_INFO.sexName,
        schoolId: USER_INFO.schoolCode,
        identityType: USER_INFO.userType,
        birthday: USER_INFO.birthday
    });
</script>