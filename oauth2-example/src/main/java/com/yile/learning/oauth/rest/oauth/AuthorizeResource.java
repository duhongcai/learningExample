package com.yile.learning.oauth.rest.oauth;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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
import org.apache.oltu.oauth2.as.request.OAuthTokenRequest;
import org.apache.oltu.oauth2.as.response.OAuthASResponse;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthAccessTokenResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.error.OAuthError;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.apache.oltu.oauth2.common.message.types.ResponseType;
import org.apache.oltu.oauth2.common.utils.OAuthUtils;
import org.glassfish.jersey.server.mvc.Viewable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.rabbitframework.commons.utils.JsonUtils;
import com.rabbitframework.commons.utils.StringUtils;
import com.rabbitframework.security.RabbitSecurityUtils;
import com.yile.learning.oauth.biz.OauthBiz;
import com.yile.learning.oauth.model.OauthClient;
import com.yile.learning.oauth.utils.Constants;
import com.yile.learning.oauth.vo.ClientAccessTokenInfo;

/**
 * oauth授权模式(authorization code)
 * 
 * @author justin.liang
 */
@Component("authorizeResource")
@Path("/oauth_authz")
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class AuthorizeResource {
	private static final Logger logger = LogManager.getLogger(AuthorizeResource.class);
	@Autowired
	private OauthBiz oauthBiz;

	/**
	 * 客户端申请认证的URI,如：
	 * <p/>
	 * GET /authorize?response_type=code&client_id=s6BhdRkqt3&state=xyz
	 * &redirect_uri=/oauth_authz/clientcode
	 * <p/>
	 * response_type：表示授权类型，必选项，此处的值固定为"code"
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
			throws URISyntaxException, OAuthSystemException {
		try {
			OAuthAuthzRequest oauthRequest = new OAuthAuthzRequest(request);
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
				// return new Viewable("/login");
			}

			// 生成授权码
			String authoriztionCode = "";
			String responseType = oauthRequest.getResponseType();
			if (ResponseType.CODE.toString().equals(responseType)) {
				OAuthIssuerImpl oAuthIssuerImpl = new OAuthIssuerImpl(new MD5Generator());
				authoriztionCode = oAuthIssuerImpl.authorizationCode();
				String authoriztionCodeKey = Constants.AUTH_CODE_KEY_PREFIX + authoriztionCode;
				oauthBiz.addAuthCode(authoriztionCodeKey, authoriztionCode);
			}
			OAuthASResponse.OAuthAuthorizationResponseBuilder builder = OAuthASResponse.authorizationResponse(request,
					HttpServletResponse.SC_FOUND);
			builder.setCode(authoriztionCode);
			String redirectURL = oauthRequest.getRedirectURI();
			// 生成locationUrl,buildQuery如:/oauth_authz/clientcode?state=test&code=6cbf239b33f4bb80879576403915c9be
			OAuthResponse authResponse = builder.location(redirectURL).buildQueryMessage();
			// bodymessage方式:将参数放到了body属性当中。
			// OAuthResponse authResponse =
			// builder.location(redirectURL).buildBodyMessage();
			// logger.debug("body:"+authResponse.getBody());

			String locationUrl = authResponse.getLocationUri();
			logger.debug("locationUrl:" + locationUrl);

			// ResponseBuilder responseBuilder =
			// Response.status(authResponse.getResponseStatus())
			// .location(new URI(locationUrl));
			ResponseBuilder responseBuilder = Response.ok("clientcode:" + locationUrl);
			return responseBuilder.build();

		} catch (OAuthProblemException e) {
			logger.error(e.getMessage(), e);
			String reditectUri = e.getRedirectUri();
			if (OAuthUtils.isEmpty(reditectUri)) {
				ResponseBuilder responseBuilder = Response.status(HttpServletResponse.SC_NOT_FOUND);
				responseBuilder.entity("没有提供客户端回调http地址");
				return responseBuilder.build();
			}
			final OAuthResponse oAuthResponse = OAuthASResponse.errorResponse(HttpServletResponse.SC_FOUND).error(e)
					.location(reditectUri).buildQueryMessage();
			String locationUrl = oAuthResponse.getLocationUri();
			logger.debug("locationUrl:" + locationUrl);
			ResponseBuilder responseBuilder = Response.status(oAuthResponse.getResponseStatus())
					.location(new URI(locationUrl));
			return responseBuilder.build();
		}
	}

	/**
	 * 请求accessToken获取访问key,
	 * <p/>
	 * 这个请求一般是在客户端发起,这里只是个示例，统一在服务器端完成
	 * 
	 * @return
	 * @throws OAuthSystemException
	 */
	@GET
	@Path("/clientcode")
	public Object clientCode(@QueryParam("code") @DefaultValue("") String code,
			@QueryParam("state") @DefaultValue("") String state, @QueryParam("error") @DefaultValue("") String error,
			@QueryParam("error_description") @DefaultValue("") String errorDescription) throws Exception {
		String accessTokenurl = "http://localhost:8080/oauth_authz/accessToken";
		String userInfoUrl = "http://localhost:8080/user/getUserInfo";
		String redirectUrl = "http://localhost:8080/oauth_authz/clientcode";
		String clientId = "0f02598f-5414-11e6-8b1e-3e7189dda781";
		String clientSecret = "0f02598f-5414-11e6-8b1e-3e7189dda781";
		logger.debug("code:" + code);
		if (StringUtils.isNotEmpty(error)) {
			String body = "error:" + error + ",errorDescription:" + errorDescription;
			ResponseBuilder responseBuilder = Response.status(HttpServletResponse.SC_UNAUTHORIZED);
			responseBuilder.entity(body);
			return responseBuilder.build();
		}
		if (StringUtils.isEmpty(code)) {
			return new Viewable("/login/login.jsp");
		}
		try {
			OAuthClient oauthClient = new OAuthClient(new URLConnectionClient());
			OAuthClientRequest accessTokenRequest = OAuthClientRequest.tokenLocation(accessTokenurl)
					.setGrantType(GrantType.AUTHORIZATION_CODE).setClientId(clientId).setClientSecret(clientSecret)
					.setCode(code).setRedirectURI(redirectUrl).buildQueryMessage();
			OAuthAccessTokenResponse oAuthAccessTokenResponse = oauthClient.accessToken(accessTokenRequest,
					OAuth.HttpMethod.POST);

			String accessToken = oAuthAccessTokenResponse.getAccessToken();
			Long expiresIn = oAuthAccessTokenResponse.getExpiresIn();
			String tokenType = oAuthAccessTokenResponse.getTokenType();
			String refreshToken = oAuthAccessTokenResponse.getRefreshToken();
			ClientAccessTokenInfo accessTokenInfo = new ClientAccessTokenInfo();
			accessTokenInfo.setAccessToken(accessToken);
			accessTokenInfo.setExpiresIn(expiresIn);
			accessTokenInfo.setRefreshToken(refreshToken);// 空
			accessTokenInfo.setTokenType(tokenType); // 空
			return Response.ok(JsonUtils.toJsonString(accessTokenInfo, new ValueFilter() {
				@Override
				public Object process(Object object, String name, Object value) {
					if (value == null)
						return "";
					return value;
				}
			})).build();
			// OAuthClientRequest userInfoRequest = new
			// OAuthBearerClientRequest(userInfoUrl).setAccessToken(accessToken)
			// .buildQueryMessage();
			// OAuthResourceResponse resource =
			// oauthClient.resource(userInfoRequest, OAuth.HttpMethod.GET,
			// OAuthResourceResponse.class);
			// String userName = resource.getBody();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	/**
	 * POST /accessToken
	 * <p/>
	 * grant_type:表示使用的授权模式,固定值:'authorization_code'
	 * <p/>
	 * code:表示上一步获得的授权码
	 * <p/>
	 * redirect_uri:表示重定向URI 如：/clientcode
	 * <p/>
	 * client_id:表示客户端ID
	 * <p/>
	 * client_secret:客户端密码
	 * 
	 * @return
	 * @throws OAuthSystemException
	 */
	@POST
	@Path("/accessToken")
	public Response accessToken(@Context HttpServletRequest request) throws OAuthSystemException {
		try {
			OAuthTokenRequest oAuthTokenRequest = new OAuthTokenRequest(request);
			OauthClient oauthClient = oauthBiz.findClientByClientId(oAuthTokenRequest.getClientId());
			// clientId不匹配返回错误消息
			if (oauthClient == null) {
				OAuthResponse authResponse = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
						.setError(OAuthError.TokenResponse.INVALID_CLIENT).setErrorDescription("客户端验证失败,clientId不匹配")
						.buildJSONMessage();
				String body = authResponse.getBody();
				logger.error(body);
				ResponseBuilder builder = Response.status(authResponse.getResponseStatus()).entity(body);
				return builder.build();
			}
			oauthClient = oauthBiz.findClientByClientSecret(oAuthTokenRequest.getClientSecret());
			// clientSecret不匹配返回错误消息
			if (oauthClient == null) {
				OAuthResponse authResponse = OAuthASResponse.errorResponse(HttpServletResponse.SC_UNAUTHORIZED)
						.setError(OAuthError.TokenResponse.UNAUTHORIZED_CLIENT)
						.setErrorDescription("客户端验证失败,clientId不匹配").buildJSONMessage();
				String body = authResponse.getBody();
				logger.error("body:" + body);
				ResponseBuilder builder = Response.status(authResponse.getResponseStatus()).entity(body);
				return builder.build();
			}
			String authCode = oAuthTokenRequest.getParam(OAuth.OAUTH_CODE);
			String authorizationCode = oAuthTokenRequest.getParam(OAuth.OAUTH_GRANT_TYPE);
			if (GrantType.AUTHORIZATION_CODE.toString().equals(authorizationCode)) {
				String oauthCodeKey = Constants.AUTH_CODE_KEY_PREFIX + authCode;
				String sysAuthCode = oauthBiz.getAuthCodeByKey(oauthCodeKey);
				// 授权码出错
				if (StringUtils.isEmpty(sysAuthCode)) {
					OAuthResponse oauthResponse = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
							.setError(OAuthError.TokenResponse.INVALID_GRANT).setErrorDescription("错误授权码")
							.buildJSONMessage();
					ResponseBuilder builder = Response.status(oauthResponse.getResponseStatus())
							.entity(oauthResponse.getBody());
					return builder.build();
				}
			} else {
				OAuthResponse oauthResponse = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
						.setError(OAuthError.TokenResponse.INVALID_GRANT).setErrorDescription("授权模式不匹配")
						.buildJSONMessage();
				ResponseBuilder builder = Response.status(oauthResponse.getResponseStatus())
						.entity(oauthResponse.getBody());
				return builder.build();
			}
			// 生成Access Token
			OAuthIssuer oauthIssuer = new OAuthIssuerImpl(new MD5Generator());
			final String accessToken = oauthIssuer.accessToken();
			// 生成Oauth响应，过期时间3600秒
			OAuthResponse oauthResponse = OAuthASResponse.tokenResponse(HttpServletResponse.SC_OK)
					.setAccessToken(accessToken).setExpiresIn(Constants.ACCESS_TOKEN_EXPIRE + "").buildJSONMessage();
			oauthBiz.addAccessToken(Constants.ACCESS_TOKEN_KEY_PREFIX + accessToken, accessToken);
			oauthBiz.delAuthCode(Constants.AUTH_CODE_KEY_PREFIX + authCode);
			ResponseBuilder builder = Response.status(oauthResponse.getResponseStatus())
					.entity(oauthResponse.getBody());
			return builder.build();
		} catch (OAuthProblemException e) {
			logger.error(e.getMessage(), e);
			OAuthResponse oauthResponse = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST).error(e)
					.buildJSONMessage();
			ResponseBuilder builder = Response.status(oauthResponse.getResponseStatus())
					.entity(oauthResponse.getBody());
			return builder.build();
		}
	}
}
