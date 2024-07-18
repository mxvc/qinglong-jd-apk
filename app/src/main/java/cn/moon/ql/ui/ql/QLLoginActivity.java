package cn.moon.ql.ui.ql;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import cn.moon.ql.QLApplication;
import cn.moon.ql.R;
import cn.moon.ql.data.model.QLSettingsData;
import cn.moon.ql.data.model.QLStoreData;
import cn.moon.ql.databinding.ActivityQingLongLoginBinding;

public class QLLoginActivity extends AppCompatActivity {

    private QLLoginViewModel loginViewModel;
    private ActivityQingLongLoginBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityQingLongLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this, new QLLoginViewModelFactory())
                .get(QLLoginViewModel.class);

        final EditText urlEditText = binding.qlUrl;
        final EditText cliendIDEditText = binding.qlCid;
        final EditText cliendSecretEditText = binding.qlCsk;
        final Button loginButton = binding.qlLogin;
        final ProgressBar loadingProgressBar = binding.loading;

        displayLocalData(urlEditText, cliendIDEditText, cliendSecretEditText);

        loginViewModel.getSettingsFormState().observe(this, qlLoginFormState -> {
            if (qlLoginFormState == null) {
                return;
            }
            loginButton.setEnabled(qlLoginFormState.isDataValid());
            if (qlLoginFormState.getUrlError() != null) {
                urlEditText.setError(getString(qlLoginFormState.getUrlError()));
            }
            if (qlLoginFormState.getCidError() != null) {
                cliendIDEditText.setError(getString(qlLoginFormState.getCidError()));
            }
            if (qlLoginFormState.getCskError() != null) {
                cliendIDEditText.setError(getString(qlLoginFormState.getCskError()));
            }
        });

        loginViewModel.getLoginResult().observe(this, loginResult -> {
            if (loginResult == null) {
                return;
            }
            loadingProgressBar.setVisibility(View.GONE);
            if (loginResult.getError() != null) {
                Log.e("Login", "Error:" + loginResult.getError().toString());
                showLoginFailed(loginResult.getError());
            }
            if (loginResult.getSuccess() != null) {
                updateUiWithQL(loginResult.getSuccess());
            }
            setResult(Activity.RESULT_OK);

            //Complete and destroy login activity once successful
//            finish();
        });

//        TextWatcher afterTextChangedListener = new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                // ignore
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                // ignore
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        };
//        urlEditText.addTextChangedListener(afterTextChangedListener);
//        cliendIDEditText.addTextChangedListener(afterTextChangedListener);
//        cliendSecretEditText.addTextChangedListener(afterTextChangedListener);
        cliendSecretEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginViewModel.settingsFormChanged(urlEditText.getText().toString(),
                        cliendIDEditText.getText().toString(),
                        cliendSecretEditText.getText().toString());
            }
            return false;
        });

        loginButton.setOnClickListener(v -> {
            loadingProgressBar.setVisibility(View.VISIBLE);
            loginViewModel.login(urlEditText.getText().toString(),
                    cliendIDEditText.getText().toString(),
                    cliendSecretEditText.getText().toString());
            loadingProgressBar.setVisibility(View.GONE);
        });
    }

    private void displayLocalData(EditText urlEditText, EditText cliendIDEditText, EditText cliendSecretEditText) {
        QLStoreData qlStoreData = QLApplication.getQLStoreData();
        QLSettingsData settingsData = qlStoreData.getSettingsData();
        if (settingsData != null) {
            urlEditText.setText(settingsData.getUrl());
            cliendIDEditText.setText(settingsData.getCid());
            cliendSecretEditText.setText(settingsData.getCsk());
        }
    }

    private void updateUiWithQL(QLLoginView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}