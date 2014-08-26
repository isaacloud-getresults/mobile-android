package com.sointeractive.getresults.app.pebble;

import android.content.Context;
import android.util.Log;

import com.sointeractive.android.kit.PebbleKit;
import com.sointeractive.android.kit.util.PebbleDictionary;
import com.sointeractive.getresults.app.config.Settings;
import com.sointeractive.getresults.app.pebble.cache.AchievementsCache;
import com.sointeractive.getresults.app.pebble.cache.BeaconsCache;
import com.sointeractive.getresults.app.pebble.cache.LoginCache;
import com.sointeractive.getresults.app.pebble.cache.PeopleCache;
import com.sointeractive.getresults.app.pebble.communication.NotificationSender;
import com.sointeractive.getresults.app.pebble.responses.AchievementDescriptionResponse;
import com.sointeractive.getresults.app.pebble.responses.AchievementInResponse;
import com.sointeractive.getresults.app.pebble.responses.AchievementOutResponse;
import com.sointeractive.getresults.app.pebble.responses.PersonInResponse;
import com.sointeractive.getresults.app.pebble.responses.PersonOutResponse;
import com.sointeractive.getresults.app.pebble.responses.ResponseItem;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PebbleConnector extends Observable {
    private static final String TAG = PebbleConnector.class.getSimpleName();

    private final Queue<ResponseItem> sendingQueue = new ConcurrentLinkedQueue<ResponseItem>();
    private final Context context;
    private final NotificationSender sender;

    private ResponseItem lastData = null;
    private int resendCount = 0;

    private boolean connectionState;

    public PebbleConnector(final Context context) {
        Log.i(TAG, "Action: Initialize Pebble connector");

        this.context = context;
        sender = new NotificationSender(context);
    }

    public void clearSendingQueue() {
        Log.i(TAG, "Action: Clear sending queue");
        sendingQueue.clear();
        resendCount = 0;
    }

    public void sendNotification(final String title, final String body) {
        sender.send(title, body);
    }

    public void sendDataToPebble(final Collection<ResponseItem> data) {
        synchronized (sendingQueue) {
            if (isPebbleConnected()) {
                final boolean wasEmpty = sendingQueue.isEmpty();
                sendingQueue.addAll(data);
                if (wasEmpty) {
                    Log.d(TAG, "Check: Queue was empty");
                    sendNext();
                }
            }
        }
    }

    public void sendNext() {
        synchronized (sendingQueue) {
            if (sendingQueue.isEmpty()) {
                Log.i(TAG, "Event: Nothing to send, sendingQueue is empty");
            } else {
                final ResponseItem data = sendingQueue.peek();
                updateResendCounter(data);
                sendOrSkip(data);
            }
        }
    }

    private void updateResendCounter(final ResponseItem data) {
        if (lastData == data) {
            resendCount += 1;
        } else {
            resendCount = 0;
            lastData = data;
        }
    }

    private void sendOrSkip(final ResponseItem response) {
        if (resendCount < Settings.RESEND_TIMES_LIMIT) {
            send(response);
        } else {
            Log.e(TAG, "Error: Resend limit reached, sending next");
            onAckReceived();
        }
    }

    private void send(final ResponseItem response) {
        final String responseType = response.getClass().getSimpleName();
        final PebbleDictionary data = response.getData();
        Log.d(TAG, "Action: Sending " + responseType + ": " + data.toJsonString());
        PebbleKit.sendDataToPebble(context, Settings.PEBBLE_APP_UUID, data);
    }

    public void onAckReceived() {
        Log.d(TAG, "Action: Poll queue");
        sendingQueue.poll();
        sendNext();
    }

    public boolean isPebbleConnected() {
        final boolean currentState = PebbleKit.isWatchConnected(context);
        if (currentState) {
            Log.d(TAG, "Check: Pebble is connected");
        } else {
            Log.w(TAG, "Check: Pebble is not connected");
        }

        if (connectionState != currentState) {
            connectionState = currentState;
            if (currentState) {
                Log.d(TAG, "Action: Resume sending");
                sendNext();
            }
            setChanged();
            notifyObservers();
        }

        return currentState;
    }

    public boolean areAppMessagesSupported() {
        final boolean appMessagesSupported = PebbleKit.areAppMessagesSupported(context);
        if (appMessagesSupported) {
            Log.d(TAG, "Check: AppMessages are supported");
        } else {
            Log.e(TAG, "Check: AppMessages are not supported");
        }

        return appMessagesSupported;
    }

    public void closePebbleApp() {
        AchievementsCache.INSTANCE.clear();
        BeaconsCache.INSTANCE.clear();
        PeopleCache.INSTANCE.clear();
        LoginCache.INSTANCE.clear();
        clearSendingQueue();
        PebbleKit.closeAppOnPebble(context, Settings.PEBBLE_APP_UUID);
    }

    public void deleteAchievementResponses() {
        deleteResponses(AchievementInResponse.class, AchievementOutResponse.class, AchievementDescriptionResponse.class);
    }

    public void deletePeopleResponses() {
        deleteResponses(PersonInResponse.class, PersonOutResponse.class);
    }

    private void deleteResponses(final Class<?>... classes) {
        synchronized (sendingQueue) {
            final Collection<ResponseItem> responses = new LinkedList<ResponseItem>();
            for (final ResponseItem response : sendingQueue) {
                if (classToDelete(classes, response)) {
                    responses.add(response);
                }
            }
            //sendingQueue.removeAll(responses);
        }
    }

    private boolean classToDelete(final Class<?>[] classes, final ResponseItem response) {
        for (final Class<?> cls : classes) {
            if (cls.isInstance(response)) {
                return true;
            }
        }
        return false;
    }
}
