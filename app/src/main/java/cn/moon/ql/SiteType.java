package cn.moon.ql;

public enum SiteType {
    JD("https://m.jd.com","JD_COOKIE");

    private String url;
    private String env; // 青龙环境变量


    SiteType(String url, String env){
        this.url = url;
        this.env = env;
    }


    public String getUrl() {
        return url;
    }

    public String getEnv() {
        return env;
    }

}
