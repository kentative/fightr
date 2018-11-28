package com.bytes.fightr.client.util;

import android.app.Activity;
import android.text.Selection;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Kent on 10/11/2016.
 *
 */
public class DisplayUtil {

    private static final int MAX_BUFFER_SIZE = 5000;

    /**
     * Replace the specified message to the view on the UI thread.
     * This can be invoked from a background thread.
     *
     * @param message the message to be displayed
     */
    public static void setText(Activity activity, final TextView textView, final String message) {

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String data = message;
                if (message.length() > MAX_BUFFER_SIZE) {
                    data = message.substring((message.length() - MAX_BUFFER_SIZE));
                }

                textView.setText(data);
            }
        });
    }

    /**
     * Appends the specified message to the specified view on the UI thread.
     * This can be invoked from a background thread.
     *
     * @param message the message to be appended
     */
    public static void appendText(Activity activity, final TextView textView, final String message) {

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String data = message;
                if (message.length() > MAX_BUFFER_SIZE) {
                    data = message.substring((message.length() - MAX_BUFFER_SIZE));
                }

                if (textView.getText().length() + textView.length() >= MAX_BUFFER_SIZE) {
                    textView.setText(data + System.lineSeparator());
                } else {
                    textView.append(message + System.lineSeparator());
                }
                Selection.setSelection(textView.getEditableText(), textView.getText().length());
            }
        });
    }

    public static void toast(final Activity activity, final String message) {

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity.getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Request to display a toast
     * @param activity - the activity hosting the toast
     * @param message - the message
     * @param toastDurationConstant  Toast.LENGTH_LONG or Toast.LENGTH_SHORT
     */
    public static void toast(final Activity activity, final String message, final int toastDurationConstant) {

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity.getApplicationContext(), message, toastDurationConstant).show();
            }
        });
    }
}
