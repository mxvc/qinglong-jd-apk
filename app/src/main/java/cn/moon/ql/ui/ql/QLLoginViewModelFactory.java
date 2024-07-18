package cn.moon.ql.ui.ql;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

import cn.moon.ql.data.QLApiClient;
import cn.moon.ql.data.QLLoginDataSource;
import cn.moon.ql.data.QLLoginRepository;

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
public class QLLoginViewModelFactory implements ViewModelProvider.Factory {

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(QLLoginViewModel.class)) {
            QLApiClient apiClient = new QLApiClient();
            return (T) new QLLoginViewModel(QLLoginRepository.getInstance(new QLLoginDataSource(apiClient)));
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}