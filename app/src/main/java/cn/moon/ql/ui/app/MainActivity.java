package cn.moon.ql.ui.app;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import cn.moon.ql.QLApplication;
import cn.moon.ql.R;
import cn.moon.ql.data.QLApiClient;
import cn.moon.ql.data.model.JDCookie;
import cn.moon.ql.data.model.QLEnvData;
import cn.moon.ql.data.model.QLLoginData;
import cn.moon.ql.data.model.QLStoreData;
import cn.moon.ql.ui.ql.QLLoginActivity;

import static cn.moon.ql.Config.JD_URL;
import static cn.moon.ql.data.model.JDCookie.JD_COOKIE;

public class MainActivity extends AppCompatActivity {


    private WebView webView;
    private Button uploadCookieButton;
    private Button loginQingLongButton;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            String str = (String) msg.obj;
            if (str != null) {
                Toast.makeText(getBaseContext(), str, Toast.LENGTH_SHORT).show();
            }
        }
    };
    private QLApiClient qlApiClient = new QLApiClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        webView = findViewById(R.id.webView);
        uploadCookieButton = findViewById(R.id.uploadCookieButton);
        loginQingLongButton = findViewById(R.id.setQingLongButton);

        // 设置 WebView 的基本属性
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(JD_URL);


        // 上传 Cookie
        uploadCookieButton.setOnClickListener(v -> uploadCookie());
        loginQingLongButton.setOnClickListener(v -> showQingLongLogin());
    }


    private void info(String msg) {
        handler.sendMessage(handler.obtainMessage(1, msg));
    }

    private void err(String msg) {
        handler.sendMessage(handler.obtainMessage(-1, msg));
    }

    private void err(Exception e) {
        String msg = e.getMessage();
        if (msg == null) {
            msg = e.getClass().getSimpleName();
        }
        handler.sendMessage(handler.obtainMessage(-1, msg));
    }

    private void uploadCookie() {
        if (!QLApplication.getQLStoreData().isLoggedQL()) {
            err("☹️请先登录青龙服务器");
            return;
        }
        JDCookie jdCookie = getJDCookie();
        if (jdCookie == null) {
            err("☹️请先登录JD账号");
            return;
        }

        new Thread() {
            @Override
            public void run() {
                doUploadJDCookie(jdCookie);
            }
        }.start();

    }


    private void showQingLongLogin() {
        // 创建一个意图，指定要跳转到的活动
        Intent intent = new Intent(MainActivity.this, QLLoginActivity.class);

        // 启动活动
        startActivity(intent);
    }

    private JDCookie getJDCookie() {
        String cookies = CookieManager.getInstance().getCookie(webView.getUrl());
        String[] cookiesArr = cookies.split(";");

        String ptPin = null;
        String ptKey = null;
        for (String ck : cookiesArr) {
            if (ck.contains("pt_pin")) {
                ptPin = ck.replace("pt_pin=", "").trim();
            }

            if (ck.contains("pt_key")) {
                ptKey = ck.replace("pt_key=", "").trim();
            }
        }

        if (ptPin != null && ptKey != null) {
            return new JDCookie(ptPin, ptKey);
        }

        return null;
    }

    private void doUploadJDCookie(JDCookie jdCookie) {
        try {
            QLStoreData qlStoreData = QLApplication.getQLStoreData();
            List<QLEnvData> envDataList = qlApiClient.listEnv(JD_COOKIE, qlStoreData.getSettingsData(), qlStoreData.getLoginData());
            Integer id = null;
            String remarks = null;
            for (QLEnvData envData : envDataList) {
                String name = envData.getName();
                String value = envData.getValue();
                if (JD_COOKIE.equals(name) && value.contains(jdCookie.getPtPin())) {
                    id = envData.getId();
                    remarks = envData.getRemarks();
                }
            }

            String finalremarks = Build.BRAND + " " + jdCookie.getPtPin();
            if (remarks != null) {
                finalremarks = remarks;
            }


            QLEnvData updateEnv = new QLEnvData(JD_COOKIE, jdCookie.joinPinAndKey(), finalremarks);
            if (id == null) {
                qlApiClient.addEnv(updateEnv, qlStoreData.getSettingsData(), qlStoreData.getLoginData());
                info(String.format("🎉添加JDCookie【%s】成功", jdCookie.getPtPin()));
            } else {
                updateEnv.setId(id);
                qlApiClient.updateEnv(updateEnv, qlStoreData.getSettingsData(), qlStoreData.getLoginData());
                info(String.format("🎉更新JDCookie【%s】成功", jdCookie.getPtPin()));
            }
        } catch (Exception e) {
            MainActivity.this.err(String.format("☹️更新JDCookie【%s】失败", jdCookie.getPtPin()));
        }
    }

}
