package cn.moon.ql.data.model;

public class JDCookie {
    public static final String JD_COOKIE = "JD_COOKIE";
    private String ptPin;
    private String ptKey;
    private String remarks;

    public JDCookie(String ptPin, String ptKey) {
        this.ptPin = ptPin;
        this.ptKey = ptKey;
    }

    public JDCookie(String ptPin, String ptKey, String remarks) {
        this.ptPin = ptPin;
        this.ptKey = ptKey;
        this.remarks = remarks;
    }

    public String getPtPin() {
        return ptPin;
    }

    public String getPtKey() {
        return ptKey;
    }

    public String getRemarks() {
        return remarks;
    }

    public String joinPinAndKey(){
        return "pt_key=".concat(this.ptKey).concat("; pt_pin=").concat(this.ptPin).concat(";");
    }
}
