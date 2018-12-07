package com.harlie.codechallenge.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.harlie.codechallenge.BaseActivity;
import com.harlie.codechallenge.StreamingCodeChallengeApplication;
import com.harlie.codechallenge.model.PartyLogItem;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class PartyLogReaderService extends Service {
    private static final String TAG = "LEE: " + PartyLogReaderService.class.getSimpleName();

    static final int BATCH_SIZE = 5;
    static final long BATCH_REST_THRESHOLD = 500; // adjust the rest timing based on the BATCH_SIZE, a larger BATCH_SIZE will load more quickly

    private static final String CODECHALLENGE_PRIMARY_URL = "https://codechallenge.secrethouse.party/?since=";
    private static final String CODECHALLENGE_BACKUP_URL = "https://hp-codechallenge.herokuapp.com/?since=";
    private static int sCallbackKey = 0;

    public PartyLogReaderService() {
        Log.d(TAG, "create PartyLogReaderService");
        setMostRecentTimeStamp(0L); // FIXME: comment to begin where we last left off.
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run loadThePartyLog in Thread");
                if (! loadThePartyLog(CODECHALLENGE_PRIMARY_URL)) {
                    if (BaseActivity.isRunning()) {
                        Log.d(TAG, "retry/w backup server..");
                        loadThePartyLog(CODECHALLENGE_BACKUP_URL);
                    }
                }
            }
        }).start();
        return Service.START_STICKY;
    }

    private boolean loadThePartyLog(String theLogUrl) {
        List<PartyLogItem> theListOfPartyLogItem = new ArrayList<PartyLogItem>(BATCH_SIZE);
        URL streamUrl = null;
        URLConnection partyConnection = null;
        BufferedReader in = null;
        String inputLine;
        PartyLogItem partyLogItem = null;
        try {
            streamUrl = new URL(theLogUrl + String.valueOf(getMostRecentTimeStamp()));
            partyConnection = streamUrl.openConnection();
            in = new BufferedReader(new InputStreamReader(partyConnection.getInputStream()));
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            while ((inputLine = in.readLine()) != null && BaseActivity.isRunning()) {  // stop Thread if the app exits
                try {
                    int lastCharIndex = inputLine.length() - 1;
                    if (inputLine.charAt(lastCharIndex) == ',') {
                        inputLine = inputLine.substring(0, lastCharIndex); // FIXME: extra buffer copy
                    }
                    partyLogItem = gson.fromJson(inputLine, PartyLogItem.class);
                    Log.d(TAG, "thread=" + Thread.currentThread().getId() + ", partyLogItem=" + partyLogItem);
                    theListOfPartyLogItem.add(partyLogItem);
                    if (theListOfPartyLogItem.size() >= BATCH_SIZE) {
                        saveMostRecentBatchOfPartyLogItems(theListOfPartyLogItem, partyLogItem.getTimestampAsLong());
                        SystemClock.sleep(BATCH_REST_THRESHOLD); // give Android some time to empty the Q
                    }
                }
                catch (JsonSyntaxException e) {
                    Log.e(TAG, "Bad JSON syntax: inputLine=" + inputLine + ", e=" + e);
                    //TODO: save invalid JSON to an exception log for fix and resubmission
                }
            }
            if (theListOfPartyLogItem.size() > 0 && partyLogItem != null) {
                saveMostRecentBatchOfPartyLogItems(theListOfPartyLogItem, partyLogItem.getTimestampAsLong());
            }
            Log.d(TAG, "=========> Completed. <=========");
            in.close();
        } catch (FileNotFoundException e) {
            Log.e(TAG, "FileNotFound: e=" + e);
            return false;
        } catch (IOException e) {
            Log.e(TAG, "I/O problem: e=" + e);
            if (theListOfPartyLogItem.size() > 0 && partyLogItem != null) {
                saveMostRecentBatchOfPartyLogItems(theListOfPartyLogItem, 0L); // NOTE: reset timestamp!
            }
            return false;
        }
        finally {
            Log.d(TAG, "stopSelf");
            stopSelf();
        }
        return true;
    }

    private void saveMostRecentBatchOfPartyLogItems(List<PartyLogItem> theListOfPartyLogItem, Long timestamp) {
        Log.d(TAG, "saveMostRecentBatchOfPartyLogItems");
        if (BaseActivity.isRunning()) {
            PartyLogReceiptEvent partyLogReceiptEvent = new PartyLogReceiptEvent(++sCallbackKey, theListOfPartyLogItem);
            partyLogReceiptEvent.post();
            if (timestamp != null) {
                setMostRecentTimeStamp(timestamp);
            }
            theListOfPartyLogItem.clear();
        }
    }

    // continue reading from where we last left off
    private Long getMostRecentTimeStamp() {
        SharedPreferences prefs = StreamingCodeChallengeApplication.getAppContext().getSharedPreferences("RECENT", MODE_PRIVATE);
        Long mostRecentTimeStamp = prefs.getLong("timestamp", 0L);
        Log.d(TAG, "getMostRecentTimeStamp: " + mostRecentTimeStamp);
        return mostRecentTimeStamp;
    }

    // persistent save how far we have read into the PartyLog
    private void setMostRecentTimeStamp(Long mostRecentTimeStamp) {
        Log.d(TAG, "setMostRecentTimeStamp: " + mostRecentTimeStamp);
        SharedPreferences.Editor editor = StreamingCodeChallengeApplication.getAppContext().getSharedPreferences("RECENT", MODE_PRIVATE).edit();
        editor.putLong("timestamp", mostRecentTimeStamp);
        editor.apply();
    }

    public static int getBATCH_SIZE() {
        return BATCH_SIZE;
    }

    public static class PartyLogReceiptEvent {
        private static final String TAG = "LEE: " + PartyLogReceiptEvent.class.getSimpleName();

        private int mCallbackId;
        private final List<PartyLogItem> mTheListOfPartyLogItem;

        public PartyLogReceiptEvent(int callbackId, List<PartyLogItem> theListOfPartyLogItem) {
            this.mTheListOfPartyLogItem = new ArrayList<PartyLogItem>(theListOfPartyLogItem);
            this.mCallbackId = callbackId;
        }

        public void post() {
            Log.v(TAG, "post: mCallbackId=" + mCallbackId);
            EventBus.getDefault().post(new PartyLogReceiptEvent(mCallbackId, mTheListOfPartyLogItem));
        }

        public int getCallbackId() {
            return mCallbackId;
        }

        public List<PartyLogItem> getTheListOfPartyLogItem() {
            return mTheListOfPartyLogItem;
        }

        @Override
        public String toString() {
            return "PartyLogReceiptEvent{" +
                    "mTheListOfPartyLogItem=" + mTheListOfPartyLogItem +
                    ", mCallbackId=" + mCallbackId +
                    '}';
        }
    }
}
