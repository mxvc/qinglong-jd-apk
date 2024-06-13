package cn.moon.ql;

import static cn.moon.ql.Config.JD_COOKIE;
import static cn.moon.ql.Config.JD_URL;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {


    private WebView webView;
    private Button uploadCookieButton;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            String str = (String) msg.obj;
            if (str != null) {
                Toast.makeText(getBaseContext(), str, Toast.LENGTH_SHORT).show();
            }
        }
    };
    private QLApi api = new QLApi();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webView);
        uploadCookieButton = findViewById(R.id.uploadCookieButton);

        // 设置 WebView 的基本属性
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(JD_URL);


        // 上传 Cookie
        uploadCookieButton.setOnClickListener(v -> uploadCookie());

        login();
    }


    private void login() {
        new Thread() {
            @Override
            public void run() {
                try {
                    api.login();
                    MainActivity.this.info("青龙脚本服务连接成功");
                } catch (Exception e) {
                    MainActivity.this.err(e);
                }
            }
        }.start();

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
        String cookies = CookieManager.getInstance().getCookie(webView.getUrl());

        String[] cs = cookies.split(";");

        String pin = null;
        StringBuilder sb = new StringBuilder();
        for (String c : cs) {
            if (c.contains("pt_key") || c.contains("pt_pin")) {
                sb.append(c.trim()).append(";");
            }
            if (c.contains("pt_pin")) {
                pin = c.replace("pt_pin=", "").trim();
            }
        }
        if (sb.length() == 0) {
            err("cookie未登录");
            return;
        }


        String finalPin = pin;
        new Thread() {
            @Override
            public void run() {
                try {
                    JSONArray list = api.list();
                    int id = 0;
                    String remarks = null;
                    for (int i = 0; i < list.length(); i++) {
                        JSONObject o = (JSONObject) list.get(i);

                        if (o.getString("value").contains(finalPin)) {
//                            api.delete(o.getInt("id"));
                            id = o.getInt("id");
                            remarks = o.getString("remarks");
//                            info("删除" + finalPin + "成功");
                        }

                    }

                    JSONObject param = new JSONObject();
                    param.put("name", JD_COOKIE);
                    param.put("value", sb.toString());
                    String finalremarks = Build.BRAND + " " + finalPin;
                    if (remarks != null) {
                        finalremarks = remarks;
                    }
                    param.put("remarks", finalremarks);

                    if (id == 0) {
                        JSONArray arr = new JSONArray();
                        arr.put(param);
                        api.add(arr);
                        info("添加成功");
                    } else {
                        param.put("id", id);
                        api.update(param);
                        info("Id：" + id + " " + finalPin + "更新成功");
                    }
                } catch (Exception e) {
                    MainActivity.this.err(e);
                }
            }
        }.start();

    }


}
