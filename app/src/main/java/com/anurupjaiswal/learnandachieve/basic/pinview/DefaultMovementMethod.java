/*
 * *
 *  * Created by Labhansh Gupta on 16/01/24, 4:47 pm
 *  * Copyright (c) 2024 . All rights reserved.
 *  * Last modified 16/01/24, 3:15 pm
 *  * All rights reserved.
 *
 */

package com.anurupjaiswal.learnandachieve.basic.pinview;

import android.text.Selection;
import android.text.Spannable;
import android.text.method.MovementMethod;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * For disable arrow key
 *
 * @author Chaos
 *         31/03/2018
 */
class DefaultMovementMethod implements MovementMethod {

    private static DefaultMovementMethod sInstance;

    public static MovementMethod getInstance() {
        if (sInstance == null) {
            sInstance = new DefaultMovementMethod();
        }

        return sInstance;
    }

    private DefaultMovementMethod() {
    }

    @Override
    public void initialize(TextView widget, Spannable text) {
        // It will mark the IMM as openable
        Selection.setSelection(text, 0);
    }

    @Override
    public boolean onKeyDown(TextView widget, Spannable text, int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean onKeyUp(TextView widget, Spannable text, int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean onKeyOther(TextView view, Spannable text, KeyEvent event) {
        return false;
    }

    @Override
    public void onTakeFocus(TextView widget, Spannable text, int direction) {

    }

    @Override
    public boolean onTrackballEvent(TextView widget, Spannable text, MotionEvent event) {
        return false;
    }

    @Override
    public boolean onTouchEvent(TextView widget, Spannable text, MotionEvent event) {
        return false;
    }

    @Override
    public boolean onGenericMotionEvent(TextView widget, Spannable text, MotionEvent event) {
        return false;
    }

    @Override
    public boolean canSelectArbitrarily() {
        return false;
    }
}
