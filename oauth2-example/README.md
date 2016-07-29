#oauth2示例
##OAuth2是什么

OAuth2是在2006年底创建的下一代OAuth协议,是一个关于授权（authorization）的开放网络标准，在全世界得到广泛应用。

##OAuth2角色

* 资源拥有者(resource owner):能够授权访问被保护资源的一个实体,可以是一个人(最终用户)，如:新浪微博用户。

* 资源服务器(resource server):存储管理受保护资源的服务器，客户端通过access token请求资源时，它决定是否接受该令牌并输出受保护的资源。

* 授权服务器(authorization server):客户端成功验证资源所有者并获取授权后，授权服务器发放访问令牌(access token)给客户端。

* 客户端(client):如微博等第三方应用，其本身不存储任何受保护的资源，而是资源所有者授权通过后，使用它的授权(授权领牌)访问受保护的资源，然后客户端把响应的数据展示/提交给服务器。 使受保护的资源请求资源所有者的代表和授权。

##OAuth授权流程
###OAuth2协议流程：

![](oauth2.png)

### OAuth2说明：

　　A、客户端(client)向资源拥有者(Resource Owner)发起授权请求(Authorization Request)，这种授权请求可以直接向资源拥有者发起，也可以间接通过授权服务器作为中介发起。

　　B、客户端(client)接收授权许可，代表资源所有者的授权凭证。授权类型可以OAuth 2.0规范中四种的任意一种，也可以是扩展授权类型。授权类型取决于方法所使用的客户端请求授权和授权服务器所支持的类型。

　　C、客户端通过私有证书和授权许可请求授权服务器(Authorization server)授权验证。

　　Ｄ、授权服务器(Authorization server)对客户端进行验证。验证通过后，返回访问令牌。

　　Ｅ、客户端使用访问令牌向资源服务器请求受保护资源。

　　Ｆ、资源服务器验证令牌的有效性，如果验证成功下发受保护的资源。

本示例所用技术框架：rabbitframework、redis、oltu

###示例请求：

初始化登陆用户名和密码:admin/123

* 授权模式请求:

	1、获取授权码:http://localhost:8080/oauth\_authz/authorize?response \_type=code&client_id=0f02598f-5414-11e6-8b1e-3e7189dda781&state=test&redirect\_uri=/oauth\_authz/clientcode
	
	2、客户端获取accessToken：http://localhost:8080/oauth\_authz/clientcode?state=test&code=8922193b64cab198560a171a054e552e
	
	3、测试:http://localhost:8080/user/getUserInfo?access_token=82387a8136ab2c7cbb9d30fce4528777

* 简化模式(implicit grant type):
	
	1、获取授权码:http://localhost:8080/implicit/authorize?response\_type=token&client_id=0f02598f-5414-11e6-8b1e-3e7189dda781&state=test&redirect\_uri=/oauth\_authz/clientcode

  