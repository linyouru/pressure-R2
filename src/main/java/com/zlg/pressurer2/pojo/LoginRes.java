package com.zlg.pressurer2.pojo;

public class LoginRes {

    private String jwt;
    private Integer tenant_id;
    private String token;
    private String ujwt;

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public Integer getTenant_id() {
        return tenant_id;
    }

    public void setTenant_id(Integer tenant_id) {
        this.tenant_id = tenant_id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUjwt() {
        return ujwt;
    }

    public void setUjwt(String ujwt) {
        this.ujwt = ujwt;
    }

    @Override
    public String toString() {
        return "LoginRes{" +
                "jwt='" + jwt + '\'' +
                ", tenant_id=" + tenant_id +
                ", token='" + token + '\'' +
                ", ujwt='" + ujwt + '\'' +
                '}';
    }
}
