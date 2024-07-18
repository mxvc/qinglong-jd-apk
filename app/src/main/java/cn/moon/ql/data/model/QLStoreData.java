package cn.moon.ql.data.model;

public class QLStoreData {
    private QLLoginData loginData;
    private QLSettingsData settingsData;

    public QLStoreData(QLLoginData loginData, QLSettingsData settingsData) {
        this.loginData = loginData;
        this.settingsData = settingsData;
    }

    public QLLoginData getLoginData() {
        return loginData;
    }

    public QLSettingsData getSettingsData() {
        return settingsData;
    }

    public boolean isLoggedQL() {
        return this.loginData != null && loginData.getToken() != null;
    }
}
