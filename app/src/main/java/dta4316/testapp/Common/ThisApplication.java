package dta4316.testapp.Common;

import android.app.Application;

public class ThisApplication extends Application {

    private static ThisApplication m_Context;

    @Override
    public void onCreate() {
        super.onCreate();
        m_Context = this;

        Authentication.Init();
    }

    public static ThisApplication GetContext() {
        return m_Context;
    }
}