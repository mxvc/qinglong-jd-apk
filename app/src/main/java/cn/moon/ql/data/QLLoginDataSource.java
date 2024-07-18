package cn.moon.ql.data;

import cn.moon.ql.data.model.QLLoginData;
import cn.moon.ql.data.model.QLSettingsData;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class QLLoginDataSource {
    private QLApiClient apiClient;
    ExecutorService executor = Executors.newSingleThreadExecutor();

    public QLLoginDataSource(QLApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public Result<QLLoginData> login(QLSettingsData settingsData) {

        try {
            Future<QLLoginData> future = executor.submit(() -> apiClient.login(settingsData));
            return new Result.Success<>(future.get());
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}