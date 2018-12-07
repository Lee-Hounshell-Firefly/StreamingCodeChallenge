package com.harlie.codechallenge;

import android.app.Application;
import android.content.Context;
import android.util.Log;

public class StreamingCodeChallengeApplication extends Application {
    private final static String TAG = "LEE: <" + StreamingCodeChallengeApplication.class.getSimpleName() + ">";

    private static StreamingCodeChallengeApplication sInstance;


    public void onCreate() {
        Log.v(TAG, "===> onCreate <===");
        StreamingCodeChallengeApplication.sInstance = this;
        super.onCreate();
    }

    public static StreamingCodeChallengeApplication getInstance() {
        return sInstance;
    }

    public static Context getAppContext() {
        return getInstance().getApplicationContext();
    }

}

