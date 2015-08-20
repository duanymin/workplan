package isports.workplan.fragment;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.activity.BaseActivity;
import com.avos.avoscloud.AVException;
import com.fragment.BaseFragment;
import com.squareup.otto.Subscribe;
import com.util.InputUtils;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;

import isports.workplan.R;
import isports.workplan.adapter.ProjectListAdapter;
import isports.workplan.bean.Project;
import isports.workplan.bean.ProjectList;
import isports.workplan.info.ProjectInfo;

public class ProjectListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    private BaseActivity activity;
    private LinkedList<Project> projects = new LinkedList<>();
    private ProjectListAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

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
        adapter = new ProjectListAdapter(projects, this);
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(true);
        ProjectInfo.getAllProject();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, @NotNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_projectlist, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_create) {
            newProject();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            swipeRefreshLayout.setRefreshing(true);
            onRefresh();
        }
    }

    private void newProject() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final View view = inflater.inflate(R.layout.dialog_newproject, null, true);
        builder.setView(view);
        final TextInputLayout layout = (TextInputLayout) view.findViewById(R.id.til_name);
        layout.setHint("项目名称");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = aq.id(view.findViewById(R.id.et_name)).getText().toString();
                if (checkName(name)) {
                    ProjectInfo.createProject(name);
                }
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
        InputUtils.showKeyBoard((EditText) view.findViewById(R.id.et_name));
    }

    private boolean checkName(String name) {
        if (TextUtils.isEmpty(name)) {
            String message = "请输入项目名称";
            Snackbar.make(swipeRefreshLayout, message, Snackbar.LENGTH_LONG)
                    .show();
            return false;
        }
        return true;
    }


    @Override
    public void onRefresh() {
        ProjectInfo.getAllProject();
    }

    @Subscribe
    public void newProject(Project project) {
        projects.add(0, project);
        adapter.notifyDataSetChanged();
    }

    @Subscribe
    public void getProjectLists(ProjectList projectList) {
        swipeRefreshLayout.setRefreshing(false);
        projects.clear();
        projects.addAll(projectList.getProjects());
        adapter.notifyDataSetChanged();
    }

    @Subscribe
    public void onError(AVException e) {
        String message = e.getMessage();
        Snackbar.make(swipeRefreshLayout, message, Snackbar.LENGTH_LONG)
                .show();
    }
}
