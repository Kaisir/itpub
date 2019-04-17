<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<!-- meta标签 -->
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<!-- 公共样式 -->
<!-- <link type="text/css" rel="stylesheet" href="../../公共应用名称/public/css/demo.css"> -->
<link rel="stylesheet" type="text/css" href="<%= com.wisedu.emap.base.core.EmapContext.getStaticResourceRoot() %>/fe_components/iview2/styles/iview.css">
<link rel="stylesheet" href="<%= com.wisedu.emap.base.core.EmapContext.getStaticResourceRoot() %>/bower_components/element-ui/lib/theme-default/index.css">

<!-- 项目样式 -->
<link type="text/css" rel="stylesheet" href="<%=request.getContextPath() %>/sys/emapflow/public/css/style.css">
<!-- 公共js依赖 -->
<script src="<%= com.wisedu.emap.base.core.EmapContext.getStaticResourceRoot() %>/bower_components/vue2/vue.min.js"></script>
<script src="<%= com.wisedu.emap.base.core.EmapContext.getStaticResourceRoot() %>/fe_components/iview2/iview.min.js"></script>
<script src="<%= request.getContextPath() %>/sys/emapflow/public/js/mixins/common.js"></script>
<script src="<%= request.getContextPath() %>/sys/emapflow/public/js/emap-h5tag.min.js"></script>
<script src="<%= request.getContextPath() %>/sys/emapflow/public/js/emapflow.js"></script>
