package com.bytes.fightr.client.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.view.View;


import static android.Manifest.permission.READ_CONTACTS;

/**
 * Created by Kent on 10/16/2015.
 */
public class ContactUtils {


    /**
     * Indicate if this activity may request data from the Contacts Manager.
     * @param activity the activity requesting the Contacts
     * @param requestCode the request code used for callback
     * @param rationale the rationale to display
     * @param view the view to display the rationale
     * @return
     */
    public static boolean mayRequestContacts(
            final Activity activity,
            final int requestCode,
            String rationale,
            View view) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        if (activity.checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        if (activity.shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(view, rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            activity.requestPermissions(new String[]{READ_CONTACTS}, requestCode);
                        }
                    });
        } else {
            activity.requestPermissions(new String[]{READ_CONTACTS}, requestCode);
        }
        return false;
    }

}
