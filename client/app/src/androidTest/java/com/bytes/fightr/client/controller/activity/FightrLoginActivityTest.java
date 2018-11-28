package com.bytes.fightr.client.controller.activity;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.bytes.fightr.R;
import com.bytes.fightr.client.controller.login.FightrLoginActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by Kent on 5/25/2017.
 */
@RunWith(AndroidJUnit4.class)
public class FightrLoginActivityTest {

    @Rule
    public ActivityTestRule<FightrLoginActivity> mActivityRule = new ActivityTestRule<>(FightrLoginActivity.class);

    @Test
    public void loginTest() {
        onView(withId(R.id.fightr_login_text)).perform(typeText("Kent"));
    }

}