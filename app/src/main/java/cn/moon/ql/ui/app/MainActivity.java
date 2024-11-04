package cn.moon.ql.ui.app;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.Map;

import cn.moon.ql.App;
import cn.moon.ql.R;
import cn.moon.ql.SiteConfig;
import cn.moon.ql.data.QLApiClient;
import cn.moon.ql.data.model.QLEnvData;
import cn.moon.ql.data.model.QLStoreData;
import cn.moon.ql.ui.ql.QLLoginActivity;
import cn.moon.ql.util.CookieUtil;

public class MainActivity extends AppCompatActivity {


    private WebView webView;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            String str = (String) msg.obj;
            if (str != null) {
                Toast.makeText(getBaseContext(), str, Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void info(String msg) {
        handler.sendMessage(handler.obtainMessage(1, msg));
    }

    private void err(String msg) {
        handler.sendMessage(handler.obtainMessage(-1, msg));
    }

    private QLApiClient qlApiClient = new QLApiClient();


    private String getEnv() {
        return SiteConfig.JD.getEnv();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 设置点击事件
        findViewById(R.id.uploadCookieButton).setOnClickListener(v -> uploadCookie());
        findViewById(R.id.setQingLongButton).setOnClickListener(v -> showQingLongLogin());
        findViewById(R.id.clear_webview).setOnClickListener(v -> clearWebview());

        // 设置 WebView 的基本属性
        webView = findViewById(R.id.webView);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(SiteConfig.JD.getUrl());
    }


    private void uploadCookie() {
        if (!App.getQLStoreData().isLoggedQL()) {
            err("未配置密钥");
            return;
        }
        Map<String, String> jdCookie = getJDCookie();
        if (jdCookie.isEmpty()) {
            err("未登录京东");
            return;
        }

        String envValue = CookieUtil.join(jdCookie);

        new Thread() {
            @Override
            public void run() {
                doUploadEnv(envValue);
            }
        }.start();

    }


    private void showQingLongLogin() {
        Intent intent = new Intent(MainActivity.this, QLLoginActivity.class);
        startActivity(intent);
    }

    private Map<String, String> getJDCookie() {
        String cookies = CookieManager.getInstance().getCookie(webView.getUrl());

        Map<String, String> map = CookieUtil.parse(cookies, "pt_pin", "pt_key");


        return map;
    }

    private void doUploadEnv(String envValue) {
        Map<String, String> map = CookieUtil.parse(envValue);
        String ptPin = map.get("ptPin");
        try {
            QLStoreData qlStoreData = App.getQLStoreData();
            String env = getEnv();
            List<QLEnvData> envDataList = qlApiClient.listEnv(env, qlStoreData.getSettingsData(), qlStoreData.getLoginData());
            Integer id = null;
            for (QLEnvData envData : envDataList) {
                String name = envData.getName();
                String value = envData.getValue();
                if (env.equals(name) && value.contains(ptPin)) {
                    id = envData.getId();
                }
            }

            QLEnvData updateEnv = new QLEnvData(env, envValue, null);
            if (id == null) {
                qlApiClient.addEnv(updateEnv, qlStoreData.getSettingsData(), qlStoreData.getLoginData());
                info(String.format("🎉添加JDCookie【%s】成功", ptPin));
            } else {
                updateEnv.setId(id);
                qlApiClient.updateEnv(updateEnv, qlStoreData.getSettingsData(), qlStoreData.getLoginData());
                //启用token
                qlApiClient.enableEnv(id, qlStoreData.getSettingsData(), qlStoreData.getLoginData());
                info(String.format("🎉更新JDCookie【%s】成功", ptPin));
            }
        } catch (Exception e) {
            MainActivity.this.err(String.format("更新JDCookie【%s】失败", ptPin));
        }
    }

    private void clearWebview() {
        CookieManager.getInstance().removeAllCookies(new ValueCallback<Boolean>() {
            @Override
            public void onReceiveValue(Boolean aBoolean) {
                info("清除cookie成功");
                // Cookie清除完成后的操作
            }
        });
        CookieManager.getInstance().flush();
        webView.loadUrl(SiteConfig.JD.getUrl());
    }

}
