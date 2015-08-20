package isports.workplan.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.fragment.BaseFragment;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;

import isports.workplan.R;
import isports.workplan.bean.ProjectUser;

/**
 * Created by Duan on 7月16日.
 */
public class ProjectUserListAdapter extends RecyclerView.Adapter<ProjectUserListAdapter.ViewHolder> implements View.OnClickListener {
    private LinkedList<ProjectUser> users = new LinkedList<>();
    private BaseFragment fragment;

    public ProjectUserListAdapter(LinkedList<ProjectUser> users, BaseFragment fragment) {
        this.users = users;
        this.fragment = fragment;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_user, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NotNull ViewHolder itemViewHolder, int position) {
        ProjectUser user = users.get(position);
        View view = itemViewHolder.itemView;
        view.setOnClickListener(this);
        view.setTag(user);
        AQuery aq = new AQuery(view);
        aq.id(R.id.textView1).text(String.format("姓名:%s", user.getUsername()));
        aq.id(R.id.textView2).text(String.format("邮箱:%s", user.getUseremail()));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    @Override
    public void onClick(@NotNull View v) {
        ProjectUser user = (ProjectUser) v.getTag();
        Toast.makeText(fragment.getActivity(), user.getUsername(), Toast.LENGTH_LONG).show();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}

