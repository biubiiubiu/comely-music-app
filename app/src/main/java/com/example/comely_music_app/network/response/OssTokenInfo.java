package com.example.comely_music_app.network.response;

import lombok.Data;

@Data
public class OssTokenInfo {
    private String requestId;
    private String endpoint;
    private String bucketName;
    private String securityToken;
    private String accessKeySecret;
    private String accessKeyId;
    private String expiration;

    public OssTokenInfo setRequestId(String requestId) {
        this.requestId = requestId;
        return this;
    }

    public OssTokenInfo setEndpoint(String endpoint) {
        this.endpoint = endpoint;
        return this;
    }

    public OssTokenInfo setBucketName(String bucketName) {
        this.bucketName = bucketName;
        return this;
    }

    public OssTokenInfo setSecurityToken(String securityToken) {
        this.securityToken = securityToken;
        return this;
    }

    public OssTokenInfo setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
        return this;
    }

    public OssTokenInfo setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
        return this;
    }

    public OssTokenInfo setExpiration(String expiration) {
        this.expiration = expiration;
        return this;
    }
}
