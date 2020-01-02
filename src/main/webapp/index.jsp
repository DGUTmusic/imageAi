<%--
  Created by IntelliJ IDEA.
  User: asus
  Date: 2020/1/2
  Time: 15:36
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>图片识别</title>
</head>
<body>
<form action="/ImageAi/servlet/ImageServlet" method="post">
    图片选择：<input type="file" id="file"/>
    <br/>
    图片类型：<select name="type">
    <option value="0">通用文字识别</option>
    <option value="1">车牌识别</option>
    <option value="2">发票识别</option>
    <option value="3">身份证正面识别</option>
</select>
    <br/>
    <input type="submit" value="提交">
    <input name="path" type="hidden" id="path"/>
</form>
<script type="text/javascript" src="/ImageAi/jq/jquery-3.3.1.min.js"></script>
<script>
    $(document).on('change', '#file', function () {
        function getObjectURL(file) {
            var url = null;
            if (window.createObjcectURL != undefined) {
                url = window.createOjcectURL(file);
            } else if (window.URL != undefined) {
                url = window.URL.createObjectURL(file);
            } else if (window.webkitURL != undefined) {
                url = window.webkitURL.createObjectURL(file);
            }
            return url;
        }

        var objURL = getObjectURL(this.files[0]);//这里的objURL就是input file的真实路径

        //设置路径
        $('path').val(objURL);
    });
</script>
</body>
</html>
