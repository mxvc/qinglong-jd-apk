package cn.moon.ql.data.model;


public class QLLoginData {

    private String tokenType;
    private String token;

    public QLLoginData(String userId, String token) {
        this.tokenType = userId;
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public String getToken() {
        return token;
    }

    public String toAuthValue(){
        return this.tokenType + " " + token;
    }
}