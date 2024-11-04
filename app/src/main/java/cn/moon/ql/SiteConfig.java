package cn.moon.ql;

public class SiteConfig {

    private String url;
    private String env; // 青龙环境变量

    // 京东
    public static final SiteConfig JD = new SiteConfig();

    static {
        JD.url = "https://m.jd.com";
        JD.env = "JD_COOKIE";
    }


    public String getUrl() {
        return url;
    }

    public String getEnv() {
        return env;
    }

}
