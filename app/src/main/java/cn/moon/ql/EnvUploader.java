package cn.moon.ql;

import java.util.List;
import java.util.Map;

import cn.moon.ql.data.QLSdk;
import cn.moon.ql.data.model.QLEnvData;
import cn.moon.ql.data.model.QLStoreData;
import cn.moon.ql.util.CookieUtil;

public class EnvUploader {

    private QLSdk sdk = new QLSdk();

    public String upload(SiteType type, String envName, String cookies) throws Exception {
        switch (type) {
            case JD:
                return uploadJd(envName, cookies);
        }
        return envName;
    }

    public String uploadJd(String envName, String cookies) throws Exception {
        Map<String, String> jdCookie = CookieUtil.parse(cookies,  "pt_key", "pt_pin");
        if (jdCookie.isEmpty()) {
            throw new Exception("未登录京东");
        }

        String envValue = CookieUtil.join(jdCookie);

        Map<String, String> map = CookieUtil.parse(envValue);
        String ptPin = map.get("pt_pin");
        QLStoreData qlStoreData = App.getQLStoreData();

        List<QLEnvData> envDataList = sdk.listEnv(envName, qlStoreData.getSettingsData(), qlStoreData.getLoginData());
        Integer id = null;
        for (QLEnvData envData : envDataList) {
            String name = envData.getName();
            String value = envData.getValue();
            if (envName.equals(name) && value.contains(ptPin)) {
                id = envData.getId();
            }
        }

        QLEnvData updateEnv = new QLEnvData(envName, envValue, null);
        if (id == null) {
            sdk.addEnv(updateEnv, qlStoreData.getSettingsData(), qlStoreData.getLoginData());
            return "添加成功";
        }

        updateEnv.setId(id);
        sdk.updateEnv(updateEnv, qlStoreData.getSettingsData(), qlStoreData.getLoginData());
        //启用token
        sdk.enableEnv(id, qlStoreData.getSettingsData(), qlStoreData.getLoginData());
        return "更新成功";
    }
}
