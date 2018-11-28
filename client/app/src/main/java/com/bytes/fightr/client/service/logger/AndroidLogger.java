package com.bytes.fightr.client.service.logger;

import android.util.Log;


/**
 * Created by Kent on 10/14/2016.
 * AndroidLogger wrapper for Android and Java JVM
 */

public class AndroidLogger implements Logger {

    private static final String TAG = "fightr";

    @Override
    public void debug(String tag, String message) {
        Log.d(tag, message);
    }

    @Override
    public void info(String tag, String message) {
        Log.i(tag, message);
    }

    @Override
    public void error(String tag, String message, Throwable t) {
        Log.e(tag, message, t);
    }

    @Override
    public void debug(String message) {
        Log.d(TAG, message);
    }

    @Override
    public void info(String message) {
        Log.i(TAG, message);
    }

    @Override
    public void warn(String message) {
        Log.w(TAG, message);
    }

    @Override
    public void error(String message) {
        Log.e(TAG, message);
    }

    @Override
    public void error(String message, Throwable t) {
        Log.e(TAG, message, t);
    }
}
