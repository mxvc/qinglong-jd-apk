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
import cn.moon.ql.EnvUploader;
import cn.moon.ql.R;
import cn.moon.ql.SiteType;
import cn.moon.ql.data.model.QLEnvData;
import cn.moon.ql.data.model.QLStoreData;
import cn.moon.ql.databinding.ActivityMainBinding;
import cn.moon.ql.ui.ql.QLLoginActivity;
import cn.moon.ql.util.CookieUtil;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding bd;

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


    private SiteType siteType = SiteType.JD;

    private EnvUploader uploader = new EnvUploader();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bd = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(bd.getRoot());

        // 设置点击事件
        bd.uploadCookieButton.setOnClickListener(v -> uploadCookie());
        bd.setQingLongButton.setOnClickListener(v -> showQingLongLogin());
        bd.clearWebview.setOnClickListener(v -> clearWebview());
        bd.qlEnvName.setText(siteType.getEnv());


        // 设置 WebView 的基本属性
        webView = findViewById(R.id.webView);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(siteType.getUrl());
    }


    private void uploadCookie() {
        if (!App.getQLStoreData().isLoggedQL()) {
            err("未配置密钥");
            return;
        }
        String cookies = CookieManager.getInstance().getCookie(webView.getUrl());


        new Thread() {
            @Override
            public void run() {
                try {
                  String msg=  uploader.upload(siteType, bd.qlEnvName.getText().toString(), cookies);
                  info(msg);
                } catch (Exception e) {
                    err(e.getMessage());
                }
            }
        }.start();

    }


    private void showQingLongLogin() {
        Intent intent = new Intent(MainActivity.this, QLLoginActivity.class);
        startActivity(intent);
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
        webView.loadUrl(siteType.getUrl());
    }

}
