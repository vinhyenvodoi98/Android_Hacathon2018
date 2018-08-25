package com.example.hanh.ava_android.androidbase;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import static java.security.AccessController.getContext;

public class BaseActivity {

    public static void hideSoftKeyboard(Activity activity) {
        if(activity == null) {
            return;
        }

        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View focusView = activity.getCurrentFocus();
        if(focusView != null) {
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
            }
            focusView.clearFocus();

        }
    }

}
