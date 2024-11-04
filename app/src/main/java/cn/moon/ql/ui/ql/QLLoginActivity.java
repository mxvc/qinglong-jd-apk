package cn.moon.ql.ui.ql;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import cn.moon.ql.App;
import cn.moon.ql.data.QLSdk;
import cn.moon.ql.data.model.QLLoginData;
import cn.moon.ql.data.model.QLSettingsData;
import cn.moon.ql.data.model.QLStoreData;
import cn.moon.ql.databinding.ActivityQingLongLoginBinding;

public class QLLoginActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityQingLongLoginBinding binding;

    private QLSdk sdk = new QLSdk();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQingLongLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        QLStoreData data = App.getQLStoreData();
        QLSettingsData settingsData = data.getSettingsData();
        binding.qlUrl.setText(settingsData.getUrl());
        binding.qlCid.setText(settingsData.getCid());
        binding.qlCsk.setText(settingsData.getCsk());


        binding.qlLogin.setOnClickListener(this);
    }




    @Override
    public void onClick(View v) {
        String url = binding.qlUrl.getText().toString();
        String cid = binding.qlCid.getText().toString();
        String csk = binding.qlCsk.getText().toString();

        new Thread() {
            @Override
            public void run() {
                try {
                    QLLoginData login = sdk.login(url, cid, csk);


                    runOnUiThread(() -> {
                        Toast.makeText(getApplication(), "登录成功", Toast.LENGTH_SHORT).show();

                        QLSettingsData settingsData = new QLSettingsData(url, cid, csk);

                        App.storeQLData(settingsData, login);

                        setResult(Activity.RESULT_OK);
                        finish();
                    });

                } catch (Exception e) {
                    runOnUiThread(() -> Toast.makeText(getApplication(), e.getMessage(), Toast.LENGTH_SHORT).show());
                    e.printStackTrace();
                }
            }
        }.start();
    }
}