package isports.workplan.fragment;


import android.app.Fragment;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.activity.BaseActivity;
import com.avos.avoscloud.AVUser;
import com.fragment.BaseFragment;

import org.jetbrains.annotations.NotNull;

import isports.workplan.R;
import isports.workplan.activity.LoginActivity;
import isports.workplan.info.UserInfo;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends BaseFragment {
    private BaseActivity activity;

    @Override
    protected int getID() {
        return R.layout.fragment_main;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, @NotNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            AVUser.logOut();
            Intent intent = new Intent(activity, LoginActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        AVUser user = UserInfo.getCurrentUser();
        if (user != null) {
            String message = String.format("%s,你好\n您的邮箱是:%s", user.getString("name"), user.getEmail());
            aq.id(R.id.tv_info).text(message);
        } else {
            aq.id(R.id.tv_info).text("请先登录");
        }
    }

    @Override
    protected void editView() {
        activity = (BaseActivity) getActivity();
    }
}
