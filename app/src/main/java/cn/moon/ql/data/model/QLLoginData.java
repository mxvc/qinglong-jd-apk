package cn.moon.ql.data.model;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
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