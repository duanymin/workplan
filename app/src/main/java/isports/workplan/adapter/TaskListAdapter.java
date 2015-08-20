package isports.workplan.adapter;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.util.DateUtils;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;

import isports.workplan.R;
import isports.workplan.bean.Task;
import isports.workplan.fragment.TaskListFragment;
import isports.workplan.info.ProjectInfo;
import isports.workplan.info.ProjectUserInfo;
import isports.workplan.info.TaskInfo;
import isports.workplan.info.UserInfo;

/**
 * Created by Duan on 7月15日.
 */
public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.ViewHolder> implements View.OnClickListener {
    private LinkedList<Task> tasks = new LinkedList<>();
    private TaskListFragment fragment;
    private int position = -1;
    private String[] states = new String[]{"进行中", "待确认", "已完成", "搁置"};

    public TaskListAdapter(LinkedList<Task> tasks, TaskListFragment fragment) {
        this.tasks = tasks;
        this.fragment = fragment;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_task, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NotNull ViewHolder itemViewHolder, int position) {
        Task task = tasks.get(position);
        View view = itemViewHolder.itemView;
        view.setOnClickListener(this);
        view.setTag(position + "");
        AQuery aq = new AQuery(view);
        aq.id(R.id.textView1).text(String.format("任务名称:%s", task.getName()));
        if (fragment.type == TaskListFragment.USERTASK) {
            aq.id(R.id.textView1).tag(position + "").textColorId(android.R.color.holo_blue_light).clicked(this);
        }
        aq.id(R.id.textView2).text(String.format("创建者:%s", task.getCreatename()));
        String text = TextUtils.isEmpty(task.getExecutorname()) ? "执行者:暂无" : String.format("执行者:%s", task.getExecutorname());
        int color = TextUtils.isEmpty(task.getExecutorname()) ? android.R.color.holo_red_light : android.R.color.holo_blue_light;
        aq.id(R.id.textView3).tag(position + "").text(text).textColorId(color).clicked(this);
        String createDate = DateUtils.parseDate2(task.getCreatedate());
        aq.id(R.id.textView4).text(String.format("创建时间:%s", createDate));
        String state = "";
        switch (task.getState()) {
            case 1:
                state = "进行中";
                break;
            case 2:
                state = "待确认";
                break;
            case 3:
                state = "已完成";
                break;
            case 4:
                state = "搁置";
                break;
        }
        aq.id(R.id.textView5).tag(position + "").clicked(this).text(String.format("当前状态:%s", state));
        if (TextUtils.isEmpty(task.getDescribed())) {
            aq.id(R.id.textView6).text("任务说明:暂无");
        } else {
            aq.id(R.id.textView6).text(String.format("任务说明:%s", task.getDescribed()));
        }
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    @Override
    public void onClick(@NotNull View v) {
        int id = v.getId();
        int position = Integer.parseInt(v.getTag().toString());
        Task task = tasks.get(position);
        switch (id) {
            case R.id.textView1:
                showProjectInfo(task);
                break;
            case R.id.textView3:
                this.position = position;
                setExecutor(task);
                break;
            case R.id.textView5:
                setState(task, v);
                break;
        }
    }

    private void showProjectInfo(Task task) {
        ProjectInfo.getProjectById(task.getProjectId());
    }

    private void setExecutor(final Task task) {
        if (UserInfo.hasRight(task.getCreateaccount()) || UserInfo.hasRight(task.getExecutoraccount())) {
            ProjectUserInfo.getAllUserByProjectId(task.getProjectId());
            Snackbar.make(fragment.getView(), "加载成员列表，请稍后", Snackbar.LENGTH_LONG)
                    .show();
        } else {
            Snackbar.make(fragment.getView(), "您没有该权限", Snackbar.LENGTH_LONG)
                    .show();
        }
    }

    private void setState(final Task task, final View view) {
        if (UserInfo.hasRight(task.getCreateaccount()) || UserInfo.hasRight(task.getExecutoraccount())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getActivity());
            builder.setItems(states, new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String state = states[which];
                    TextView text = (TextView) view;
                    text.setText(String.format("当前状态:%s", state));
                    TaskInfo.changeState(task.getId(), which + 1);
                    task.setState(which + 1);
                }
            });
            builder.show();
        } else {
            Snackbar.make(fragment.getView(), "您没有该权限", Snackbar.LENGTH_LONG)
                    .show();
        }
    }

    public void resetPosition() {
        position = -1;
    }

    public int getSelectPosition() {
        return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}