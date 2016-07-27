package com.yile.learning.oauth.rest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.error.OAuthError;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.oltu.oauth2.common.message.types.ParameterStyle;
import org.apache.oltu.oauth2.common.utils.OAuthUtils;
import org.apache.oltu.oauth2.rs.request.OAuthAccessResourceRequest;
import org.apache.oltu.oauth2.rs.response.OAuthRSResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.rabbitframework.commons.utils.StringUtils;
import com.yile.learning.oauth.biz.OauthBiz;
import com.yile.learning.oauth.utils.Constants;

@Component("userInfoResource")
@Path("/user")
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class UserInfoResource {
	private static final Logger logger = LogManager.getLogger(UserInfoResource.class);
	@Autowired
	private OauthBiz oauthBiz;

	@GET
	@Path("/getUserInfo")
	public Response getUserInfo(@Context HttpServletRequest request) throws OAuthSystemException {
		ResponseBuilder builder = null;
		try {
			OAuthAccessResourceRequest oauthRequest = new OAuthAccessResourceRequest(request,ParameterStyle.QUERY);
			String accessToken = oauthRequest.getAccessToken();
			String token = oauthBiz.getAccessTokenByKey(Constants.ACCESS_TOKEN_KEY_PREFIX + accessToken);
			// 不存在或过期处理
			if (StringUtils.isEmpty(token)) {
				OAuthResponse oauthResponse = OAuthRSResponse.errorResponse(HttpServletResponse.SC_UNAUTHORIZED)
						.setRealm("getUserInfo") // 服务名称
						.setError(OAuthError.ResourceResponse.INVALID_TOKEN).buildHeaderMessage();
				builder = Response.status(HttpServletResponse.SC_UNAUTHORIZED).header(OAuth.HeaderType.WWW_AUTHENTICATE,
						oauthResponse.getHeader(OAuth.HeaderType.WWW_AUTHENTICATE));
				return builder.build();
			}
		} catch (OAuthProblemException e) {
			logger.error(e.getMessage(), e);
			String errorCode = e.getError();
			OAuthResponse oAuthResponse = null;
			if (OAuthUtils.isEmpty(errorCode)) {
				oAuthResponse = OAuthRSResponse.errorResponse(HttpServletResponse.SC_UNAUTHORIZED)
						.setRealm("getUserInfo").buildHeaderMessage();
				builder = Response.status(HttpServletResponse.SC_UNAUTHORIZED).header(OAuth.HeaderType.WWW_AUTHENTICATE,
						oAuthResponse.getHeader(OAuth.HeaderType.WWW_AUTHENTICATE));
				return builder.build();
			}
			oAuthResponse = OAuthRSResponse.errorResponse(HttpServletResponse.SC_UNAUTHORIZED).setRealm("getUserInfo")
					.setError(errorCode).setErrorDescription(e.getDescription()).setError(e.getUri())
					.buildHeaderMessage();
			builder = Response.status(HttpServletResponse.SC_BAD_REQUEST).header(OAuth.HeaderType.WWW_AUTHENTICATE,
					oAuthResponse.getHeader(OAuth.HeaderType.WWW_AUTHENTICATE));
			return builder.build();
		}
		// 模拟返回信息
		String userName = "admin";
		builder = Response.ok(userName);
		return builder.build();
	}
}
