package cn.moon.ql.ui.ql;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.util.Patterns;

import cn.moon.ql.QLApplication;
import cn.moon.ql.data.QLLoginRepository;
import cn.moon.ql.data.Result;
import cn.moon.ql.data.model.QLLoginData;
import cn.moon.ql.R;
import cn.moon.ql.data.model.QLSettingsData;

public class QLLoginViewModel extends ViewModel {

    private MutableLiveData<QLLoginFormState> settingsFormState = new MutableLiveData<>();
    private MutableLiveData<QLLoginResult> loginResult = new MutableLiveData<>();
    private QLLoginRepository loginRepository;

    QLLoginViewModel(QLLoginRepository QLLoginRepository) {
        this.loginRepository = QLLoginRepository;
    }

    LiveData<QLLoginFormState> getSettingsFormState() {
        return settingsFormState;
    }

    LiveData<QLLoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(String url, String cid, String csk) {
        // can be launched in a separate asynchronous job
        QLSettingsData settingsData = new QLSettingsData(url, cid, csk);
        Result<QLLoginData> result = loginRepository.login(settingsData);

        if (result instanceof Result.Success) {
            QLLoginData loginData = ((Result.Success<QLLoginData>) result).getData();
            loginResult.setValue(new QLLoginResult(new QLLoginView(loginData.getToken())));
            QLApplication.storeQLDate(settingsData,loginData);
        } else {
            loginResult.setValue(new QLLoginResult(R.string.login_failed));
        }
    }

    public void settingsFormChanged(String url, String cid, String sck) {
        if (!isUrlValid(url)) {
            settingsFormState.setValue(new QLLoginFormState(R.string.invalid_url, null, null));
        } else if (!isCidValid(cid)) {
            settingsFormState.setValue(new QLLoginFormState(null, R.string.invalid_cid, null));
        } else if (!isCskValid(sck)) {
            settingsFormState.setValue(new QLLoginFormState(null, null, R.string.invalid_csk));
        } else {
            settingsFormState.setValue(new QLLoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUrlValid(String url) {
        if (url == null) {
            return false;
        }
        if (url.contains("http")) {
            return Patterns.WEB_URL.matcher(url).matches();
        } else {
            return !url.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isCidValid(String cid) {
        return cid != null && cid.trim().length() == 12;
    }

    private boolean isCskValid(String csk) {
        return csk != null && csk.trim().length() == 24;
    }
}