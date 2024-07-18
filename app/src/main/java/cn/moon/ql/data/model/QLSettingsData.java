package cn.moon.ql.data.model;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class QLSettingsData {

    private String url;
    private String cid;
    private String csk;

    public QLSettingsData(String url, String cid,String csk) {
        this.url = url;
        this.cid = cid;
        this.csk = csk;
    }

    public String getUrl() {
        return url;
    }

    public String getCid() {
        return cid;
    }

    public String getCsk() {
        return csk;
    }
}