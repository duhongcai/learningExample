package com.yile.learning.oauth.model;
import com.rabbitframework.dbase.annontations.Column;
import com.rabbitframework.dbase.annontations.ID;
import com.rabbitframework.dbase.annontations.Table;

/**
* This class corresponds to the database table oauth_client
*/
@Table
public class OauthClient {
    /**
    * This field corresponds to the database column oauth_client.oauth_client_id
    */
    @ID
    private long oauthClientId;

    /**
    * This field corresponds to the database column oauth_client.client_secret
    */
    @Column
    private String clientSecret;

    /**
    * This field corresponds to the database column oauth_client.client_id
    */
    @Column
    private String clientId;

    /**
    * This field corresponds to the database column oauth_client.client_name
    */
    @Column
    private String clientName;

    public void setOauthClientId(long oauthClientId) {
        this.oauthClientId = oauthClientId;
    }

    public long getOauthClientId() {
        return oauthClientId;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientName() {
        return clientName;
    }

}
