package com.bytes.fightr;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.bytes.fightr.client.controller.login.FightrLoginActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class ApplicationTest {

    private static final String mStringToBeTyped = "Kaelyn";

    @Rule
    public ActivityTestRule<FightrLoginActivity> mActivityRule = new ActivityTestRule<>(FightrLoginActivity.class);

    @Test
    public void changeText_sameActivity() {
        // Type text and then press the button.
        onView(withId(R.id.fightr_login_text)).perform(typeText(mStringToBeTyped), closeSoftKeyboard());

        // Check that the text was changed.
        onView(withId(R.id.fightr_login_text)).check(matches(withText(mStringToBeTyped)));
    }
}