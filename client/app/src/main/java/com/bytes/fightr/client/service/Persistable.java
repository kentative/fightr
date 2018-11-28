package com.bytes.fightr.client.service;

import android.os.Bundle;

/**
 * Created by Kent on 10/21/2015.
 * This interface provides a consistent pattern to use with an activity.
 *
 */
public interface Persistable {

    void start();

    void stop();

    void resume();

    void pause();

    void onRestoreInstanceState(Bundle savedInstanceState);

    void onSaveInstanceState(Bundle savedInstanceState);
}
