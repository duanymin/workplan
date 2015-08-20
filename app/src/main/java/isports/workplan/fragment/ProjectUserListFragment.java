package isports.workplan.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.activity.BaseActivity;
import com.avos.avoscloud.AVException;
import com.fragment.BaseFragment;
import com.squareup.otto.Subscribe;

import java.util.LinkedList;

import isports.workplan.R;
import isports.workplan.adapter.ProjectUserListAdapter;
import isports.workplan.bean.Project;
import isports.workplan.bean.ProjectUser;
import isports.workplan.bean.ProjectUserList;
import isports.workplan.info.ProjectUserInfo;

/**
 * Created by Duan on 7月16日.
 */
public class ProjectUserListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    private LinkedList<ProjectUser> users = new LinkedList<>();
    private BaseActivity activity;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProjectUserListAdapter adapter;
    private Project project;

    public static ProjectUserListFragment newInstance(Project project) {
        ProjectUserListFragment fragment = new ProjectUserListFragment();
        Bundle args = new Bundle();
        args.putSerializable("project", project);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            project = (Project) getArguments().getSerializable("project");
        }
    }

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
        adapter = new ProjectUserListAdapter(users, this);
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(true);
        ProjectUserInfo.getAllUserByProjectId(project.getId());
        swipeRefreshLayout.setEnabled(false);
    }

    @Override
    public void onRefresh() {
        ProjectUserInfo.getAllUserByProjectId(project.getId());
    }

    @Subscribe
    public void newuser(ProjectUser user) {
        this.users.add(0, user);
        adapter.notifyDataSetChanged();
    }

    @Subscribe
    public void userLists(ProjectUserList projectUserList) {
        swipeRefreshLayout.setRefreshing(false);
        this.users.clear();
        this.users.addAll(projectUserList.getProjectUsers());
        adapter.notifyDataSetChanged();
    }

    @Subscribe
    public void onError(AVException e) {
        String message = e.getMessage();
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
    }
}
