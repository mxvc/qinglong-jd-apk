package cn.jiangtao.qinglongclient;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private Button copyCookieButton;
    private Button uploadCookieButton;

    private String token;

    private static final int PERMISSION_REQUEST_READ_PHONE_STATE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webView);
        copyCookieButton = findViewById(R.id.copyCookieButton);
        uploadCookieButton = findViewById(R.id.uploadCookieButton);

        // 设置 WebView 的基本属性
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("https://m.jd.com");



        // 复制 Cookie
        copyCookieButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cookies = CookieManager.getInstance().getCookie(webView.getUrl());
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                if (clipboard != null) {
                    ClipData clip = ClipData.newPlainText("Cookie", cookies);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(MainActivity.this, "Cookie 已复制", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 上传 Cookie
        uploadCookieButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取手机号的权限
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        && ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, PERMISSION_REQUEST_READ_PHONE_STATE);
                } else {
                    // 已经拥有了获取手机号的权限，执行上传逻辑
                    uploadCookie();
                }
            }
        });

        login();
    }

    private void login() {


        Request request = new Request.Builder()
                .url("http://soulsoup.cn:5700/open/auth/token?client_id=Bx-SAI5gHE4D&client_secret=_Pw8yO6tT7QrK6Rf4CgUrUmS")
                .get()
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "登录失败" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String rsBody = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {


                            if (response.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();


                                JSONObject rs = new JSONObject(rsBody);

                                if (rs.getInt("code") == 200) {
                                    String tokenType = rs.getJSONObject("data").getString("token_type");
                                    String tokenValue = rs.getJSONObject("data").getString("token");
                                    token = tokenType + " " + tokenValue;
                                } else {
                                    Toast.makeText(MainActivity.this, "登录失败" + rs.getString("message"), Toast.LENGTH_SHORT).show();
                                }


                            } else {
                                Toast.makeText(MainActivity.this, "登录失败" + response.message(), Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, "登录失败" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }


                });


            }
        });
    }

    private void uploadCookie() {
        String cookies = CookieManager.getInstance().getCookie(webView.getUrl());

        // 构建 JSON 请求体
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", getDevicePhoneNumber());
            jsonObject.put("remarks", getDevicePhoneNumber());
            jsonObject.put("name", "JD_COOKIE");
            jsonObject.put("value", Arrays.asList(cookies) );
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 发起 POST 请求
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(mediaType, jsonObject.toString());
        Request request = new Request.Builder()
                .url("http://soulsoup.cn:5700/open/envs")
                .addHeader("Authorization", token)
                .post(body)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "上传 Cookie 失败", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String rsBody = response.body().string();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (response.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "上传 Cookie 成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, rsBody, Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });
    }

    private String getDevicePhoneNumber() {
        String phoneModel = Build.MODEL;
        return phoneModel;
    }


}