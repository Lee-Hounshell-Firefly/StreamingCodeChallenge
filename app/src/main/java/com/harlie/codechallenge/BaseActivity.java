package com.harlie.codechallenge;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class BaseActivity extends AppCompatActivity {
    private static final String TAG = "LEE: " + BaseActivity.class.getSimpleName();

    static volatile boolean sIsRunning = true;

    public static boolean isRunning() {
        return sIsRunning;
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        sIsRunning = false;
        super.onDestroy();
    }
}
