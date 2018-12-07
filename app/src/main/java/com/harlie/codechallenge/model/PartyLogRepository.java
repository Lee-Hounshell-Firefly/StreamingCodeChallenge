package com.harlie.codechallenge.model;

import android.arch.lifecycle.MutableLiveData;
import android.content.Intent;
import android.util.Log;

import com.harlie.codechallenge.BaseActivity;
import com.harlie.codechallenge.service.PartyLogReaderService;

import java.util.List;

// this is an abstraction layer on top of data repositories.
// this class is responsible for the PartyLog data repository.
// the data might pass through here from web or a database or service.

public class PartyLogRepository {
    private static final String TAG = "LEE: " + PartyLogRepository.class.getSimpleName();

    private MutableLiveData<List<PartyLogItem>> mAllPartyLogItems;

    public PartyLogRepository(BaseActivity baseActivity) {
        Log.d(TAG, "create PartyLogRepository");
        this.mAllPartyLogItems = new MutableLiveData<>();
        baseActivity.startService(new Intent(baseActivity, PartyLogReaderService.class));
    }

    public MutableLiveData<List<PartyLogItem>> getPartyLogLiveData() {
        Log.d(TAG, "getPartyLogLiveData");
        return mAllPartyLogItems;
    }
}
