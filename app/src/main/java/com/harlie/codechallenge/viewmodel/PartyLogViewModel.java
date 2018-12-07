package com.harlie.codechallenge.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;
import android.view.View;

import com.harlie.codechallenge.model.PartyLogItem;
import com.harlie.codechallenge.model.PartyLogRepository;
import com.harlie.codechallenge.service.PartyLogReaderService;

import java.util.ArrayList;
import java.util.List;

public class PartyLogViewModel extends ViewModel {
    private static final String TAG = "LEE: " + PartyLogViewModel.class.getSimpleName();

    public static final int MAX_LOG_SIZE = 2000;
    private PartyLogRepository mPartyLogRepository;
    private List<PartyLogItem> mAllPartyLogItems;
    private MutableLiveData<List<PartyLogItem>>  mMutableLiveData = null;

    public PartyLogViewModel() {
    }

    public void setPartyLogRepository(PartyLogRepository partyLogRepository) {
        Log.d(TAG, "setPartyLogRepository");
        this.mPartyLogRepository = partyLogRepository;
        if (mMutableLiveData == null) {
            mMutableLiveData = mPartyLogRepository.getPartyLogLiveData();
        }
        this.mAllPartyLogItems = mMutableLiveData.getValue();
    }

    public MutableLiveData<List<PartyLogItem>> getPartyLogLiveData() {
        Log.d(TAG, "getPartyLogLiveData");
        return mMutableLiveData;
    }

    public void showNewPartyLogItems(PartyLogReaderService.PartyLogReceiptEvent logReceiptEvent) {
        Log.d(TAG, "showNewPartyLogItems");
        List<PartyLogItem> listOfNewPartyLogItems = logReceiptEvent.getTheListOfPartyLogItem();
        int receivedItemCount = listOfNewPartyLogItems.size();
        if (receivedItemCount >= MAX_LOG_SIZE) {
            // here we received more items than can be displayed, so truncate the list
            Log.d(TAG, "showNewPartyLogItems: do truncate");
            int toIndex = listOfNewPartyLogItems.size() - 1;
            int fromIndex = listOfNewPartyLogItems.size() - MAX_LOG_SIZE;
            if (fromIndex < 0) {
                fromIndex = 0;
            }
            mAllPartyLogItems = listOfNewPartyLogItems.subList(fromIndex, toIndex);
            mMutableLiveData.setValue(mAllPartyLogItems);
        }
        else {
            // here we need to append new data to the end of the list, keeping max length at MAX_LOG_SIZE
            Log.d(TAG, "showNewPartyLogItems: do append");
            if (mAllPartyLogItems == null) {
                Log.d(TAG, "showNewPartyLogItems: the initial list is empty");
                mAllPartyLogItems = new ArrayList<PartyLogItem>(MAX_LOG_SIZE * 2);
            }
            Log.d(TAG, "showNewPartyLogItems: new size=" + listOfNewPartyLogItems.size() + ", existing size=" + mAllPartyLogItems.size());
            int carryOverSize = MAX_LOG_SIZE - listOfNewPartyLogItems.size();
            if (mAllPartyLogItems.size() > carryOverSize) {
                Log.d(TAG, "showNewPartyLogItems: need to truncate old list");
                int fromIndex = (mAllPartyLogItems.size() - carryOverSize) - 1;
                int toIndex = mAllPartyLogItems.size() - 1;
                mAllPartyLogItems = mAllPartyLogItems.subList(fromIndex, toIndex);
            }
            try {
                mAllPartyLogItems.addAll(listOfNewPartyLogItems);
                mMutableLiveData.setValue(mAllPartyLogItems);
            }
            catch (IndexOutOfBoundsException e) {
                Log.e(TAG, "List addAll failed! - mAllPartyLogItems size=" +
                    mAllPartyLogItems.size() + ", listOfNewPartyLogItems size=" +
                    listOfNewPartyLogItems.size());
                mMutableLiveData = mPartyLogRepository.getPartyLogLiveData();
                mAllPartyLogItems = mMutableLiveData.getValue();
            }
        }
    }

    public void doClick(final View view, final PartyLogItem item) {
        Log.d(TAG, "doClick: -click- item=" + item); // TODO
    }
}
