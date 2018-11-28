package com.bytes.fightr.client.controller.activity;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.bytes.fightr.R;
import com.bytes.fightr.client.controller.lobby.FightrLobbyActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by Kent on 5/25/2017.
 */
@RunWith(AndroidJUnit4.class)
public class FightrLobbyActivityTest {


    @Rule
    public ActivityTestRule<FightrLobbyActivity> mActivityRule = new ActivityTestRule<>(FightrLobbyActivity.class);


    @Test
    public void loginTest() throws InterruptedException {
        onView(withId(R.id.fightr_lobby_user_messages)).perform(typeText("Kent"), closeSoftKeyboard());
    }


}