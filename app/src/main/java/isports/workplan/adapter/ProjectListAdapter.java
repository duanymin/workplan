package isports.workplan.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidquery.AQuery;
import com.fragment.BaseFragment;
import com.util.DateUtils;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;

import isports.workplan.R;
import isports.workplan.activity.ProjectActivity;
import isports.workplan.bean.Project;

/**
 * Created by Duan on 7月14日.
 */
public class ProjectListAdapter extends RecyclerView.Adapter<ProjectListAdapter.ViewHolder> implements View.OnClickListener {
    private LinkedList<Project> projects = new LinkedList<>();
    private BaseFragment fragment;
    private final int SHOWPROJECT = 100;

    public ProjectListAdapter(LinkedList<Project> projects, BaseFragment fragment) {
        this.projects = projects;
        this.fragment = fragment;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_project, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NotNull ViewHolder itemViewHolder, int position) {
        Project project = projects.get(position);
        View view = itemViewHolder.itemView;
        view.setOnClickListener(this);
        view.setTag(project);
        AQuery aq = new AQuery(view);
        aq.id(R.id.textView1).text(String.format("项目名称:%s", project.getName()));
        aq.id(R.id.textView2).text(String.format("创建者:%s", project.getCreatename()));
        aq.id(R.id.textView3).text(String.format("项目人数:%d", project.getMembernum()));
        String createDate = DateUtils.parseDate2(project.getCreatedate());
        aq.id(R.id.textView4).text(String.format("创建时间:%s", createDate));
        aq.id(R.id.textView6).text(String.format("任务数:%d", project.getTasknum()));
        String state = "";
        switch (project.getState()) {
            case 1:
                state = "进行中";
                break;
            case 2:
                state = "关闭";
                break;
            case 3:
                state = "搁置";
                break;
        }
        aq.id(R.id.textView5).text(String.format("当前状态:%s", state));

    }

    @Override
    public int getItemCount() {
        return projects.size();
    }

    @Override
    public void onClick(@NotNull View v) {
        Project project = (Project) v.getTag();
        Intent intent = new Intent(fragment.getActivity(), ProjectActivity.class);
        intent.putExtra("project", project);
        fragment.startActivityForResult(intent, SHOWPROJECT);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}