package com.yile.learning.oauth.rest.oauth;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.oltu.oauth2.as.issuer.MD5Generator;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuer;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuerImpl;
import org.apache.oltu.oauth2.as.request.OAuthAuthzRequest;
import org.apache.oltu.oauth2.as.response.OAuthASResponse;
import org.apache.oltu.oauth2.common.error.OAuthError;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.oltu.oauth2.common.message.types.ResponseType;
import org.apache.oltu.oauth2.common.utils.OAuthUtils;
import org.glassfish.jersey.server.mvc.Viewable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import com.rabbitframework.security.RabbitSecurityUtils;
import com.yile.learning.oauth.biz.OauthBiz;
import com.yile.learning.oauth.model.OauthClient;
import com.yile.learning.oauth.utils.Constants;

/**
 * 简化模式(implicit grant type) 不通过第三方应用程序的服务器，直接在浏览器中向认证服务器申请令牌，
 * 跳过了"授权码"这个步骤，因此得名。所有步骤在浏览器中完成， 令牌对访问者是可见的，且客户端不需要认证
 * 
 * @author justin.liang
 */
@Path("/implicit")
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ImplicitGrantTypeResource {
	private static final Logger logger = LogManager.getLogger(ImplicitGrantTypeResource.class);
	@Autowired
	private OauthBiz oauthBiz;

	/**
	 * 客户端申请认证的URI,如：
	 * <p/>
	 * GET /authorize?response_type=token&client_id=s6BhdRkqt3&state=xyz
	 * &redirect_uri=/oauth_authz/clientcode
	 * <p/>
	 * response_type：表示授权类型，必选项，此处的值固定为"token"
	 * <p/>
	 * client_id：表示客户端的ID，必选项
	 * <p/>
	 * redirect_uri：表示重定向URI，可选项
	 * <p/>
	 * scope：表示申请的权限范围，可选项
	 * <p/>
	 * state：表示客户端的当前状态，可以指定任意值，认证服务器会原封不动地返回这个值
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws URISyntaxException
	 */
	@GET
	@Path("/authorize")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_HTML })
	public Object authorize(@Context HttpServletRequest request, @Context HttpServletResponse response)
			throws OAuthSystemException, URISyntaxException {
		OAuthAuthzRequest oauthRequest;
		try {
			oauthRequest = new OAuthAuthzRequest(request);
			String clientId = oauthRequest.getClientId();
			OauthClient oauthClient = oauthBiz.findClientByClientId(clientId);
			if (oauthClient == null) {
				OAuthResponse authResponse = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
						.setError(OAuthError.TokenResponse.INVALID_CLIENT).setErrorDescription("客户端验证失败,clientId不匹配")
						.buildBodyMessage();
				String body = authResponse.getBody();
				logger.error(body);
				ResponseBuilder builder = Response.status(authResponse.getResponseStatus()).entity(body);
				return builder.build();
			}
			if (!RabbitSecurityUtils.isAuthenticated()) {
				return new Viewable("/login/login.jsp");
			}
			String responseType = oauthRequest.getResponseType();
			String accessToken = "";
			if (ResponseType.TOKEN.toString().equals(responseType)) {
				// 生成Access Token
				OAuthIssuer oauthIssuer = new OAuthIssuerImpl(new MD5Generator());
				accessToken = oauthIssuer.accessToken();
			}
			// 生成Oauth响应，过期时间3600秒
			OAuthResponse oauthResponse = OAuthASResponse.tokenResponse(HttpServletResponse.SC_OK)
					.setAccessToken(accessToken).setExpiresIn(Constants.ACCESS_TOKEN_EXPIRE + "").buildJSONMessage();
			oauthBiz.addAccessToken(Constants.ACCESS_TOKEN_KEY_PREFIX + accessToken, accessToken);
			ResponseBuilder builder = Response.status(oauthResponse.getResponseStatus())
					.entity(oauthResponse.getBody());
			return builder.build();
		} catch (OAuthProblemException e) {
			logger.error(e.getMessage(), e);
			ResponseBuilder responseBuilder = Response.status(HttpServletResponse.SC_NOT_FOUND);
			responseBuilder.entity("申请失败");
			return responseBuilder.build();
		}

	}
}
