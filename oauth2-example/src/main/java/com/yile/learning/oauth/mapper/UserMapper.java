package com.yile.learning.oauth.mapper;

import com.rabbitframework.dbase.annontations.Mapper;
import com.rabbitframework.dbase.annontations.Param;
import com.rabbitframework.dbase.annontations.Select;
import com.yile.learning.oauth.model.OauthUser;

@Mapper
public interface UserMapper {
	@Select("select * from oauth_user where user_name=#{userName} and password=#{password}")
	public OauthUser findByUserName(@Param("userName") String userName, @Param("password") String password);
}
