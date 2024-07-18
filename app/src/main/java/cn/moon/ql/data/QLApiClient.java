package cn.moon.ql.data;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.moon.ql.data.model.QLEnvData;
import cn.moon.ql.data.model.QLLoginData;
import cn.moon.ql.data.model.QLSettingsData;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class QLApiClient {
    private OkHttpClient client = new OkHttpClient();

    public QLLoginData login(QLSettingsData settings) throws Exception {
        JSONObject data = (JSONObject) this.doRequest(settings.getUrl(), "/open/auth/token?client_id=" + settings.getCid() + "&client_secret=" + settings.getCsk(), "GET", null, null);

        Log.d(QLApiClient.class.getName(), "login: " + data.toString());
        String tokenType = data.getString("token_type");
        String tokenValue = data.getString("token");

        return new QLLoginData(tokenType, tokenValue);
    }

    public List<QLEnvData> listEnv(String name, QLSettingsData settingsData, QLLoginData loginData) throws Exception {
        Object response = this.doRequest(settingsData.getUrl(), "/open/envs", "GET", null, loginData.toAuthValue());
        if (response == null) {
            return Collections.emptyList();
        }

        JSONArray jsonArray = (JSONArray) response;
        List<QLEnvData> envList = new ArrayList<>();
        if (jsonArray.length() < 1) {
            return envList;
        }
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jo = jsonArray.getJSONObject(i);
            QLEnvData envData = new QLEnvData(jo.getInt("id"),
                    jo.getString("name"),
                    jo.getString("value"),
                    jo.getString("remarks"));
            envList.add(envData);
        }
        return envList;
    }

    public void addEnv(QLEnvData envData, QLSettingsData settingsData, QLLoginData loginData) throws Exception {
        JSONArray arr = new JSONArray();
        arr.put(envData.toJson());
        this.doRequest(settingsData.getUrl(), "/open/envs", "POST", arr.toString(), loginData.toAuthValue());
    }

    public void updateEnv(QLEnvData envData, QLSettingsData settingsData, QLLoginData loginData) throws Exception {
        this.doRequest(settingsData.getUrl(), "/open/envs", "PUT", envData.toJsonString(), loginData.toAuthValue());
    }

    private Object doRequest(String url, String uri, String method, String content, String authStr) throws Exception {
        RequestBody body = null;
        if (content != null) {
            MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
            body = RequestBody.create(mediaType, content);
        }

        Request.Builder builder = new Request.Builder();
        if (authStr != null) {
            builder.addHeader("Authorization", authStr);
        }
        Request request = builder
                .url(url + uri)
                .method(method, body)
                .build();

        try {
            Response response = client.newCall(request).execute();

            String rsBody = response.body().string();
            JSONObject rs = new JSONObject(rsBody);

            if (!response.isSuccessful()) {

                throw new IllegalStateException(uri + " " + rs.getString("message"));
            }


            System.out.println(rs);
            if (rs.getInt("code") != 200) {
                String message = rs.getString("message");
                throw new IllegalStateException(message);
            }
            if (rs.has("data")) {
                return rs.get("data");
            }
            return null;
        } catch (Exception e) {
            Log.e(this.getClass().getName(), e.getMessage());
            return null;
        }

    }
}
