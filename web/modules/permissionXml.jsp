<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"  session="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <title>生成version.sql</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="renderer" content="webkit">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
        <link type="text/css" rel="stylesheet" href="${resServer}/fe_components/jqwidget/blue/bh.min.css">
        <script type="text/javascript" src="${resServer}/fe_components/commonlib.js"></script>
    </head>
    <body>
        <form method="post" action="genPermission.do" enctype="multipart/form-data">
	        <div class="bh-text-center bh-mv-24">
	        <div id="inputDiv"  style="margin-left: 91px;">
	            <input id="file" type="file" name="file" style="margin-bottom: 16px;">
	        </div>
	            <div style="padding-top: 16px;">
	               <button class='bh-btn bh-btn-primary' id="download" type="submit">生成permission.xml</button>
	            </div>
	        </div>
        </form>
       <!--  <div class="bh-text-center bh-mv-24">
            <button class='bh-btn bh-btn-primary' id="addFile">增加一个文件</button>
            <button class='bh-btn bh-btn-primary' id="deleteFile">删除一个文件</button>
        </div> -->
        <!-- <script>
            $(function(){
                $("#addFile").click(function(){
                    var html = '<input id="file" type="file" name="file" style="margin-bottom: 16px;">';
                    $("input:last").after(html);
                    if(0 === $("input:last").length) {
                        $("#inputDiv").append(html);
                    }
                });
                $("#deleteFile").click(function(){
                    $("input:last").remove();
                });
            });
        </script> -->
    </body>
</html>