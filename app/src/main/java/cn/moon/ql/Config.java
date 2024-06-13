package cn.moon.ql;

public class Config {

    // 青龙后台地址，请修改
    public static final String QL_URL =  System.getenv("QL_URL"); // "http://13.212.144.45:5701";

    // 接口信息，请修改
    public static final String CLIENT_ID = System.getenv("QL_CLIENT_ID");  // "0TPSFc-0r8Yj";
    public static final String CLIENT_SECRET = System.getenv("QL_CLIENT_SECRET"); // "9b4kmwbZQZkd99cmSKW-_QIL";



    // 固定值 环境变量
    public static final String JD_COOKIE = "JD_COOKIE";

    // 固定值 京东网址
    public static final String JD_URL = "https://m.jd.com";

}
