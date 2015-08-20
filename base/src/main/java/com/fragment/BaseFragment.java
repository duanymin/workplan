package com.fragment;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.BaseApplaction;
import com.androidquery.AQuery;
import com.orhanobut.logger.Logger;

import org.jetbrains.annotations.NotNull;

/**
 * Fragment基类
 *
 * @author Duan
 */
public abstract class BaseFragment extends Fragment {
    protected AQuery aq;
    protected String TAG;
    public View viewGroup;
    public LayoutInflater inflater;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @org.jetbrains.annotations.Nullable ViewGroup container, Bundle savedInstanceState) {
        //可配置ActivityMenu菜单
        setHasOptionsMenu(true);
        if (container == null) {
            return null;
        }
        this.inflater = inflater;
        if (viewGroup == null) {
            viewGroup = inflater.inflate(getID(), null);
            aq = new AQuery(getActivity(), viewGroup);
        } else {
            ViewGroup group = (ViewGroup) viewGroup.getParent();
            if (group != null) {
                group.removeView(viewGroup);
            }
        }
        BaseApplaction.getBusInstance(BaseApplaction.FRAGMENT).register(this);
        return viewGroup;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BaseApplaction.getBusInstance(BaseApplaction.FRAGMENT).unregister(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        editView();
        TAG = this.getClass().getName();
        StringBuilder builder = new StringBuilder();
        builder
                .append(" (")
                .append(getClassName())
                .append(".java")
                .append(":40")
                .append(")");
        Logger.d(builder.toString());
    }

    @NotNull
    private String getClassName() {
        int lastIndex = TAG.lastIndexOf(".");
        return TAG.substring(lastIndex + 1);
    }

    /**
     * 得到布局ID
     *
     * @return
     */
    protected abstract
    @LayoutRes
    int getID();

    /**
     * 编辑UI
     */
    protected abstract void editView();

}
