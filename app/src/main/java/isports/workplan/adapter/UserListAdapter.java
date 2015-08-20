package isports.workplan.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.avos.avoscloud.AVUser;
import com.fragment.BaseFragment;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;

import isports.workplan.R;
import isports.workplan.activity.UserListActivity;

/**
 * Created by Duan on 7月14日.
 */
public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> implements View.OnClickListener {
    private LinkedList<AVUser> users = new LinkedList<>();
    private BaseFragment fragment;

    public UserListAdapter(LinkedList<AVUser> users, BaseFragment fragment) {
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
        AVUser user = users.get(position);
        View view = itemViewHolder.itemView;
        view.setOnClickListener(this);
        view.setTag(user);
        AQuery aq = new AQuery(view);
        aq.id(R.id.textView1).text(String.format("姓名:%s", user.getString("name")));
        aq.id(R.id.textView2).text(String.format("邮箱:%s", user.getEmail()));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    @Override
    public void onClick(@NotNull View v) {
        AVUser user = (AVUser) v.getTag();
        //来自选择项目成员界面
        if (fragment.getActivity() instanceof UserListActivity) {
            Intent intent = new Intent();
            intent.putExtra("id", user.getObjectId());
            intent.putExtra("account", user.getUsername());
            intent.putExtra("email", user.getEmail());
            intent.putExtra("name", user.getString("name"));
            fragment.getActivity().setResult(Activity.RESULT_OK, intent);
            fragment.getActivity().finish();
        } else {
            Toast.makeText(fragment.getActivity(), user.getString("name"), Toast.LENGTH_LONG).show();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
