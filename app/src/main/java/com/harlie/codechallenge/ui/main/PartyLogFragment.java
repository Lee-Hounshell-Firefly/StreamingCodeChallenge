package com.harlie.codechallenge.ui.main;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.harlie.codechallenge.BaseActivity;
import com.harlie.codechallenge.R;
import com.harlie.codechallenge.model.PartyLogItem;
import com.harlie.codechallenge.model.PartyLogRepository;
import com.harlie.codechallenge.service.PartyLogReaderService;
import com.harlie.codechallenge.viewmodel.PartyLogViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class PartyLogFragment extends BaseFragment {
    private static final String TAG = "LEE: " + PartyLogFragment.class.getSimpleName();

    private PartyLogViewModel mPartyLogViewModel;
    private RecyclerView mRecyclerView;
    private PartyLogAdapter mPartyLogAdapter;

    public static PartyLogFragment newInstance() {
        return new PartyLogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.main_fragment, container, false);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        mPartyLogViewModel = ViewModelProviders.of(this).get(PartyLogViewModel.class);
        mPartyLogViewModel.setPartyLogRepository(new PartyLogRepository((BaseActivity) getActivity()));
        mPartyLogAdapter = new PartyLogAdapter((BaseActivity) getActivity(), mPartyLogViewModel);
        mRecyclerView.setAdapter(mPartyLogAdapter);

        mPartyLogViewModel.getPartyLogLiveData().observe(this, new Observer<List<PartyLogItem>>() {
            @Override
            public void onChanged(@Nullable final List<PartyLogItem> partyLogItems) {
                if (partyLogItems != null) {
                    Log.d(TAG, "onChanged: party list size=" + partyLogItems.size());
                    try {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "--> run setPartyLogItems <--");
                                mPartyLogAdapter.setPartyLogItems(partyLogItems);
                                mRecyclerView.scrollToPosition(partyLogItems.size());
                            }
                        });
                    } catch (Exception e) {
                        Log.e(TAG, "problem with runOnUiThread: e=" + e);
                    }
                }
                else {
                    Log.d(TAG, "...waiting for data...");
                }
            }
        });
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        EventBus.getDefault().register(this); // register for EventBus events
        super.onResume();
    }

    @Subscribe
    public void onEvent(final PartyLogReaderService.PartyLogReceiptEvent logReceiptEvent) {
        Log.d(TAG, "---------> onEvent <--------- logReceiptEvent=" + logReceiptEvent);
        try {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //Log.d(TAG, "run mPartyLogViewModel.showNewPartyLogItems");
                    mPartyLogViewModel.showNewPartyLogItems(logReceiptEvent);
                }
            });
        }
        catch (Exception e) {
            Log.e(TAG, "problem with runOnUiThread: e=" + e);
        }
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onPause");
        super.onDestroy();
    }
}
