package com.harlie.codechallenge;

import android.os.Bundle;

import com.harlie.codechallenge.ui.main.PartyLogFragment;

public class MainActivity extends BaseActivity {
    private static final String TAG = "LEE: " + MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, PartyLogFragment.newInstance())
                    .commitNow();
        }
    }
}
