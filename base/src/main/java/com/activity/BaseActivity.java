package com.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;

import com.BaseApplaction;
import com.androidquery.AQuery;
import com.anno.ActionBarSet;
import com.anno.ActionBarSet.WAY;
import com.base.R;
import com.orhanobut.logger.Logger;

import org.jetbrains.annotations.NotNull;

/**
 * 包括了AQ对象和基本的网络请求的基类封装
 *
 * @author Duan
 */
public abstract class BaseActivity extends AppCompatActivity {
    protected String TAG;
    protected Toolbar toolbar;
    public AQuery aq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = this.getClass().getName();
        aq = new AQuery(this);
        // 硬件加速开启
        StringBuilder builder = new StringBuilder();
        builder
                .append(" (")
                .append(getClassName())
                .append(".java")
                .append(":40")
                .append(")");
        Logger.d(builder.toString());
        BaseApplaction.getBusInstance(BaseApplaction.ACTIVITY).register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BaseApplaction.getBusInstance(BaseApplaction.ACTIVITY).unregister(this);
    }

    @NotNull
    private String getClassName() {
        int lastIndex = TAG.lastIndexOf(".");
        return TAG.substring(lastIndex + 1);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        checkActionBar();
        initView();
    }

    protected abstract void initView();

    /**
     * 检测actionBar的设置
     */
    private void checkActionBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar == null) {
            return;
        }
        setSupportActionBar(toolbar);
        if (getClass().isAnnotationPresent(ActionBarSet.class)) {
            ActionBarSet set = getClass().getAnnotation(ActionBarSet.class);
            if (set == null) {
                return;
            }
            //标题
            String title = set.title();
            Logger.d("TITLE:%s", title);
            if (set.way() == WAY.TITLE) {
                if (!TextUtils.isEmpty(title)) {
                    toolbar.setTitle(title);
                } else {
                    toolbar.setTitle("");
                }
            }
            if (set.way() == WAY.METHOD) {
                toolbar.setTitle(getABTitle());
            }
            //按钮
            ActionBar actionBar = getSupportActionBar();
            if (actionBar == null) {
                return;
            }
            actionBar.setHomeButtonEnabled(set.homeAsUpEnabled()); //设置返回键可用
            actionBar.setDisplayHomeAsUpEnabled(set.homeAsUpEnabled());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @NotNull
    public String getABTitle() {
        return "";
    }
}
