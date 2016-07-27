DELIMITER ;
delete from oauth_user;
delete from oauth_client;

insert into oauth_user values(1,'admin','202cb962ac59075b964b07152d234b70','202cb962ac59075b964b07152d234b70');
insert into oauth_client values(1,'client','0f02598f-5414-11e6-8b1e-3e7189dda781','0f02598f-5414-11e6-8b1e-3e7189dda781');