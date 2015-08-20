package isports.workplan.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.activity.BaseActivity;
import com.avos.avoscloud.AVException;
import com.fragment.BaseFragment;
import com.squareup.otto.Subscribe;

import java.util.LinkedList;

import isports.workplan.R;
import isports.workplan.activity.ProjectActivity;
import isports.workplan.adapter.TaskListAdapter;
import isports.workplan.bean.Project;
import isports.workplan.bean.ProjectUser;
import isports.workplan.bean.ProjectUserList;
import isports.workplan.bean.Task;
import isports.workplan.bean.TaskList;
import isports.workplan.info.TaskInfo;
import isports.workplan.info.UserInfo;

/**
 * Created by Duan on 7月16日.
 */
public class TaskListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    private TaskListAdapter adapter;
    private LinkedList<Task> tasks = new LinkedList<>();
    private BaseActivity activity;
    private SwipeRefreshLayout swipeRefreshLayout;
    public static final int USERTASK = 1, PROJECTTASK = 2;
    public int type;
    private String account;

    public static TaskListFragment newInstance(int type, String account) {
        TaskListFragment fragment = new TaskListFragment();
        Bundle args = new Bundle();
        args.putInt("type", type);
        args.putString("account", account);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getInt("type");
            account = getArguments().getString("account");
        }
    }

    @Override
    protected int getID() {
        return R.layout.fragment_recyclelist;
    }

    @Override
    protected void editView() {
        activity = (BaseActivity) getActivity();
        RecyclerView recyclerView = (RecyclerView) viewGroup.findViewById(R.id.recyclerView);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        final LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(layoutManager);
        swipeRefreshLayout = (SwipeRefreshLayout) viewGroup.findViewById(R.id.swipe_refresh_widget);
        swipeRefreshLayout.setOnRefreshListener(this);
        adapter = new TaskListAdapter(tasks, this);
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(true);
        getData();
        if (type == PROJECTTASK) {
            swipeRefreshLayout.setEnabled(false);
        }
    }

    private void getData() {
        switch (type) {
            case USERTASK:
                TaskInfo.getAllTaskByExecutorAccount(account);
                break;
            case PROJECTTASK:
                TaskInfo.getAllTaskByProjectId(account);
                break;
        }
    }

    @Override
    public void onRefresh() {
        getData();
    }

    @Subscribe
    public void newTask(Task task) {
        tasks.add(0, task);
        adapter.notifyDataSetChanged();
    }

    @Subscribe
    public void taskLists(TaskList taskList) {
        swipeRefreshLayout.setRefreshing(false);
        tasks.clear();
        tasks.addAll(taskList.getTasks());
        adapter.notifyDataSetChanged();
    }

    @Subscribe
    public void project(Project project) {
        Intent intent = new Intent(activity, ProjectActivity.class);
        intent.putExtra("project", project);
        startActivity(intent);
    }

    @Subscribe
    public void projectUserList(final ProjectUserList list) {
        int position = adapter.getSelectPosition();
        if (position >= 0) {
            final Task task = tasks.get(position);
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            final View view = activity.getLayoutInflater().inflate(R.layout.dialog_setexcutor, null, true);
            final TextInputLayout layout = (TextInputLayout) view.findViewById(R.id.til_describe);
            layout.setHint("任务说明");
            //执行者列表
            final Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
            ArrayAdapter<String> strAdapter = new ArrayAdapter<>(activity, R.layout.item_username, getUserList(list.getProjectUsers()));
            spinner.setAdapter(strAdapter);
            builder.setView(view);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //描述
                    EditText et_describe = (EditText) view.findViewById(R.id.et_describe);
                    String et_text = TextUtils.isEmpty(et_describe.getText().toString()) ? "什么都没说" : et_describe.getText().toString();
                    String newDescribed = task.getDescribed() + "\n" + task.getExecutorname() + ":" + et_text;
                    //执行者
                    int index = spinner.getSelectedItemPosition() - 1;
                    if (index >= 0) {
                        ProjectUser excutor = list.getProjectUsers().get(index);
                        //用户身份判断
                        if (!excutor.getUseraccount().equals(UserInfo.getCurrentUser().getUsername())) {
                            updateExcutor(task, newDescribed, excutor);
                            String msg = String.format("你变更为%s项目中%s任务的执行者", task.getProjectName(), task.getName());
                            UserInfo.sendMsg(excutor.getUseraccount(), msg);
                        }
                        adapter.resetPosition();
                    } else {
                        Toast.makeText(activity, "请选择执行者", Toast.LENGTH_LONG).show();
                    }
                }
            });
            builder.setNegativeButton("取消", null);
            builder.show();
        }
    }

    /***
     * 更新任务执行者
     *
     * @param task
     * @param newDescribed
     * @param excutor
     */
    private void updateExcutor(Task task, String newDescribed, ProjectUser excutor) {
        TaskInfo.changeExecutor(task.getId(), excutor.getUseraccount(), excutor.getUsername(), newDescribed);
        task.setExecutoraccount(excutor.getUseraccount());
        task.setExecutorname(excutor.getUsername());
        task.setDescribed(newDescribed);
        adapter.notifyDataSetChanged();
    }

    private String[] getUserList(LinkedList<ProjectUser> projectUsers) {
        int size = projectUsers == null ? 1 : projectUsers.size() + 1;
        String[] items = new String[size];
        items[0] = "请选择执行者";
        for (int a = 0; a < size - 1; a++) {
            items[a + 1] = projectUsers.get(a).getUsername();
        }
        return items;
    }

    @Subscribe
    public void onError(AVException e) {
        String message = e.getMessage();
        Snackbar.make(swipeRefreshLayout, message, Snackbar.LENGTH_LONG)
                .show();
    }
}
