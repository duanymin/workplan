package isports.workplan.fragment;

import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.activity.BaseActivity;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.fragment.BaseFragment;
import com.squareup.otto.Subscribe;

import java.util.LinkedList;

import isports.workplan.R;
import isports.workplan.adapter.UserListAdapter;
import isports.workplan.info.UserInfo;

/**
 * Created by Duan on 7月14日.
 */
public class UserListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    private LinkedList<AVUser> users = new LinkedList<>();
    private BaseActivity activity;
    private SwipeRefreshLayout swipeRefreshLayout;
    private UserListAdapter adapter;

    @Override
    protected int getID() {
        return R.layout.fragment_recyclelist;
    }

    @Override
    protected void editView() {
        activity = (BaseActivity) getActivity();
        swipeRefreshLayout = (SwipeRefreshLayout) viewGroup.findViewById(R.id.swipe_refresh_widget);
        swipeRefreshLayout.setOnRefreshListener(this);
        RecyclerView recyclerView = (RecyclerView) viewGroup.findViewById(R.id.recyclerView);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        final LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new UserListAdapter(users, this);
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(true);
        UserInfo.getAllUser();
    }


    @Override
    public void onRefresh() {
        UserInfo.getAllUser();
    }


    @Subscribe
    public void userLists(LinkedList<AVUser> users) {
        swipeRefreshLayout.setRefreshing(false);
        this.users.clear();
        this.users.addAll(users);
        adapter.notifyDataSetChanged();
    }

    @Subscribe
    public void onError(AVException e) {
        String message = e.getMessage();
        Snackbar.make(swipeRefreshLayout, message, Snackbar.LENGTH_LONG)
                .show();
    }
}
