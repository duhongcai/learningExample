<%@page contentType="text/html" pageEncoding="UTF-8"%>
<html>
<head>
<title>测试jersey上传文件</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

</head>
<body>
	<h1>测试jersey上传文件</h1>
	<form action="${pageContext.request.contextPath}/uploadImage"
		method="post" enctype="multipart/form-data">
		<p>
			文件 :<input type="file" id="file" name="file" />
			<br /> 用户名: 
			<input type="text" id="username" name="username" /><br />
		</p>
		<input type="submit" value="上传" />
	</form>
</body>
</html>