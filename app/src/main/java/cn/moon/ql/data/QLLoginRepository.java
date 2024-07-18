package cn.moon.ql.data;

import cn.moon.ql.data.model.QLLoginData;
import cn.moon.ql.data.model.QLSettingsData;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class QLLoginRepository {

    private static volatile QLLoginRepository instance;

    private QLLoginDataSource dataSource;

    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    private QLLoginData loginData = null;

    // private constructor : singleton access
    private QLLoginRepository(QLLoginDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static QLLoginRepository getInstance(QLLoginDataSource dataSource) {
        if (instance == null) {
            instance = new QLLoginRepository(dataSource);
        }
        return instance;
    }

    public boolean isLoggedIn() {
        return loginData != null;
    }

    public void logout() {
        loginData = null;
        dataSource.logout();
    }

    private void setLoggedInData(QLLoginData loginData) {
        this.loginData = loginData;
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }

    public Result<QLLoginData> login(QLSettingsData settings) {
        // handle login
        Result<QLLoginData> result = dataSource.login(settings);

        if (result instanceof Result.Success) {
            setLoggedInData(((Result.Success<QLLoginData>) result).getData());
        }
        return result;
    }
}