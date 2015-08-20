package com.util;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Duan on 7月16日.
 */
public class InputUtils {
    //强制显示系统键盘
    public static void showKeyBoard(final EditText txtSearchKey) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                InputMethodManager m = (InputMethodManager)
                        txtSearchKey.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                m.showSoftInput(txtSearchKey, InputMethodManager.SHOW_FORCED);
            }
        }, 200);
    }

    //强制关闭系统键盘
    public static void hideKeyBoard(final EditText txtSearchKey) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                InputMethodManager m = (InputMethodManager)
                        txtSearchKey.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                m.hideSoftInputFromWindow(txtSearchKey.getWindowToken(), 0);
            }
        }, 200);
    }
}
