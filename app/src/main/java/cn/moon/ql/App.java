package cn.moon.ql;

import android.app.Application;
import android.content.Context;


public class App  extends Application {

    private static App app;

    private static Context context;


    @Override
    public void onCreate() {
        super.onCreate();
        app = this;

        context = getApplicationContext();

    }

    public static App getInstance() {
        return app;
    }

    public static Context getStaticContext() {
        return context;
    }

}
