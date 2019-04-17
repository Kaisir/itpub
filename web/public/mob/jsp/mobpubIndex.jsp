<%@ page language="java" contentType="text/html; charset=UTF-8" session="false" %>
<div id="selectrole">
	<!-- 路由出口 -->
	<!-- 路由匹配到的组件将渲染在这里 -->
	<router-view></router-view>
</div>
<div id="app">
	<!-- 路由出口 -->
	<!-- 路由匹配到的组件将渲染在这里 -->
	<keep-alive>
		<router-view v-if="$route.meta.keepAlive"></router-view>
	</keep-alive>
	<router-view v-if="!$route.meta.keepAlive"></router-view>
</div>